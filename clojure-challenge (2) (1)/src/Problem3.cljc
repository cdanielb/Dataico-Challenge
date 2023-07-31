(ns Problem3
  (:require [invoice-item])

)
;(import [invoice-item invoice-item])
(use 'clojure.test)

;(println (invoice-item/subtotal {:invoice-item/precise-quantity 10 :invoice-item/precise-price 50 :invoice-item/discount-rate 0}))
(def data-test-without-discount {:invoice-item/precise-quantity 10 :invoice-item/precise-price 50})
(def data-test {:invoice-item/precise-quantity 10 :invoice-item/precise-price 50 :invoice-item/discount-rate 4})
(deftest test1
  (is (float? (invoice-item/subtotal data-test)))
)

(deftest test2
  (is (= 480.0 (invoice-item/subtotal data-test)))
  (is (= 500.0 (invoice-item/subtotal data-test-without-discount)))
)

(deftest test3
  (println )

  (is (not= nil (first (->> (tree-seq #(or (map? %) (vector? %)) identity data-test-without-discount)
                         (filter #(if (and (map? %) (:invoice-item/precise-quantity %)) true  false))
                         (map :invoice-item/precise-quantity)
                         set
                         vec
                         ))))

  (is (not= nil (first (->> (tree-seq #(or (map? %) (vector? %)) identity data-test-without-discount)
                         (filter #(if (and (map? %) (:invoice-item/precise-price %)) true  false))
                         (map :invoice-item/precise-price)
                         set
                         vec
                         ))))
)

(deftest test4
  (is (int? (first (->> (tree-seq #(or (map? %) (vector? %)) identity data-test-without-discount)
                            (filter #(if (and (map? %) (:invoice-item/precise-quantity %)) true  false))
                            (map :invoice-item/precise-quantity)
                            set
                            vec
                            ))))

  (is (int? (first (->> (tree-seq #(or (map? %) (vector? %)) identity data-test-without-discount)
                            (filter #(if (and (map? %) (:invoice-item/precise-price %)) true  false))
                            (map :invoice-item/precise-price)
                            set
                            vec
                            ))))
)

(deftest test5
  (is (map? data-test-without-discount))
  (is (map? data-test))
)

(deftest subtotalTest
  (test1)
  (test2)
  (test3)
  (test4)
  (test5)
)

(run-tests 'Problem3)