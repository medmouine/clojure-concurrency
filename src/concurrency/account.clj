(ns concurrency.account)

(defrecord account [id balance initialBalance])

(defn fake-fetch []
    (Thread/sleep 1000))

(defn new [id balance]
  (atom (->account id balance balance)))

(defn transferMoneyTo [^account from ^account to ^Integer amount]
  (swap! from update-in [:balance] - amount)
  (fake-fetch)
  (swap! to update-in [:balance] + amount)
  )