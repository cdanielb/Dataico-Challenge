(ns Problem1)

(def invoice  (clojure.edn/read-string (slurp "../invoice.edn")))

(defn getTaxCategory
  [part]
  (->> (tree-seq #(or (map? %) (vector? %)) identity part)
       (filter #(if (and (map? %) (:tax/category %)) true  false))
       (map :tax/category)
       set
       vec
  )
)

(defn getTaxRate
  [part]
  (->> (tree-seq #(or (map? %) (vector? %)) identity part)
       (filter #(if (and (map? %) (:tax/rate %)) true  false))
       (map :tax/rate)
       set
       vec
       )
  )

(defn getRetentionCategory
  [part]
  (->> (tree-seq #(or (map? %) (vector? %)) identity part)
       (filter #(if (and (map? %) (:retention/category %)) true  false))
       (map :retention/category)
       set
       vec
       )
  )

(defn getRetentionRate
  [part]
  (->> (tree-seq #(or (map? %) (vector? %)) identity part)
       (filter #(if (and (map? %) (:retention/rate %)) true  false))
       (map :retention/rate)
       set
       vec
       )
  )

(defn testNewItems
  [new-items]
  (loop [test new-items
         result-final []]
    (if (empty? test)
      result-final
      (let [[part & remaining] test]
        (recur remaining
                (into result-final
                      (if (and (contains? part :taxable/taxes) (contains? part :retentionable/retentions))
                        (let [tax-category (getTaxCategory part)
                              tax-rate (getTaxRate part)
                              retention-category (getRetentionCategory part)
                              retention-rate (getRetentionRate part)
                              ]
                          (if (or (and (and (= (first tax-rate) 19) (= (first tax-category) :iva)) (and (not= (first retention-rate) 1) (= (first retention-category) :ret_fuente))) (and (and (not= (first tax-rate) 19) (= (first tax-category) :iva)) (and (= (first retention-rate) 1) (= (first retention-category) :ret_fuente))))
                            (set [part])
                          )
                        )
                        (if (contains? part :taxable/taxes)
                          (let [tax-category (getTaxCategory part)
                                tax-rate (getTaxRate part)
                                ]
                            (if (and (and (= (first tax-rate) 19) (= (first tax-category) :iva)))
                              (set [part])
                              )
                            )
                          (let [retention-category (getRetentionCategory part)
                                retention-rate (getRetentionRate part)
                                ]
                            (if (and (and (= (first retention-rate) 1) (= (first retention-category) :ret_fuente)))
                              (set [part])
                              )
                          )
                        )
                      )

                )
        )
      )
    )
  )
)

;(println (get invoice :invoice/items))

(println (testNewItems (get invoice :invoice/items)))
