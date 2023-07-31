(ns Problem2
  (:require
    [clojure.spec.alpha :as s]
    [clojure.data.json :as json]
    )
  )

(def invoiceJson (json/read-str (slurp "../invoice.json")))

(defn not-blank? [value] (-> value clojure.string/blank? not))
(defn non-empty-string? [x] (and (string? x) (not-blank? x)))

(s/def :customer/name non-empty-string?)
(s/def :customer/email non-empty-string?)
(s/def :invoice/customer (s/keys :req [:customer/name
                                       :customer/email]))

(s/def :tax/rate double?)
(s/def :tax/category #{:iva})
(s/def ::tax (s/keys :req [:tax/category
                           :tax/rate]))
(s/def :invoice-item/taxes (s/coll-of ::tax :kind vector? :min-count 1))

(s/def :invoice-item/price double?)
(s/def :invoice-item/quantity double?)
(s/def :invoice-item/sku non-empty-string?)

(s/def ::invoice-item
  (s/keys :req [:invoice-item/price
                :invoice-item/quantity
                :invoice-item/sku
                :invoice-item/taxes]))

(s/def :invoice/issue-date inst?)
(s/def :invoice/items (s/coll-of ::invoice-item :kind vector? :min-count 1))

(s/def ::invoice
  (s/keys :req [:invoice/issue-date
                :invoice/customer
                :invoice/items]))

(defn dataStructureTaxes
  [tax]
  {
     :tax/category (if (= (get-in tax ["tax_category"]) "IVA")
                     :iva
                     (get-in tax ["tax_category"])
                     )
     :tax/rate (double (get-in tax ["tax_rate"]))
   }
)

(defn addTaxes
  [taxes]
  (loop [tax taxes
         taxes-final []]
    (if (empty? tax)
      taxes-final
      (let [[part & remaining] tax]
        (recur remaining
               (into taxes-final
                     (set [(dataStructureTaxes part)])
               )
        )
      )
    )
  )
)

(defn dataStructureItem
  [item]
  {
   :invoice-item/price (get-in item ["price"])
   :invoice-item/quantity (get-in item ["quantity"])
   :invoice-item/sku (get-in item ["sku"])
   :invoice-item/taxes (addTaxes (get-in item ["taxes"]))
  }
)

(defn addItems
  [items]
  ;(println (type items))
  (loop [item items
         items-final []]
    (if (empty? item)
      items-final
      (let [[part & remaining] item]
        (recur remaining
               (into items-final
                  (set [(dataStructureItem part)])
               )
        )
      )
    )
  )
)

(defn createSchema
  [file]
  {
   :invoice/issue-date (new java.util.Date (get-in file ["invoice" "issue_date"]))
   :invoice/customer {
                      :customer/name (get-in file ["invoice" "customer" "company_name"])
                      :customer/email (get-in file ["invoice" "customer" "email"])
                      }
   :invoice/items (addItems (get-in file ["invoice" "items"]))
  }
)

(defn validateInvoice
  [file]
  (let [invoice (createSchema file)]
    (s/valid? ::invoice invoice)
  )
)

(println (validateInvoice invoiceJson))
