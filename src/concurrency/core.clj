(ns concurrency.core
  (:require [concurrency.account :as account]))
(import '[java.util.concurrent Executors ExecutorService Callable])
(def service (Executors/newFixedThreadPool 100))

(def numberOfAccounts 10)
(def numberOfMoneyTransfers 20)
(def numberOfAccountPerMoneyTransfer 2)

(defn getRandomBalance []
  (* 1000 (+ 1 (rand-int 30))))

(defn createAccountWithRandomBalance [id]
  (let [balance (getRandomBalance)]
    (account/new id balance))
  )

(defn createAccounts [numberOfAccounts]
  (vec (for [i (range 1 (+ 1 numberOfAccounts))]
         (createAccountWithRandomBalance i))))

(def accounts (createAccounts numberOfAccounts))

(defn transfer []
  (dotimes [_ numberOfMoneyTransfers]
     (def from (nth accounts (+ 1 rand-int (+ 1 numberOfAccounts))))
     (for [_ (range numberOfAccountPerMoneyTransfer)]
       ((def to (nth accounts (+ 1 rand-int (+ 1 numberOfAccounts))))
        (def amount (rand-int 100))
        (account/transferMoneyTo from to amount)
        (account/transferMoneyTo to from amount))))))

(defn worker [f n]
  (doseq [task (repeat n f)]
    (f)))

(defn transferMoneyRandomly []
  (future (worker transfer numberOfMoneyTransfers))
  )

(defn -main
  []
  (println (str (vec (map (fn [acc] (:balance @acc)) accounts))))
  (transferMoneyRandomly)
  (println (str (vec (map (fn [acc] (:balance @acc)) accounts))))
  )