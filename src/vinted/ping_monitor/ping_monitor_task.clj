(ns vinted.ping-monitor.ping-monitor-task
  (:import
    ;[org.apache.samza.storage.kv.KeyValueStore]
    (org.apache.samza.task StreamTask WindowableTask MessageCollector TaskCoordinator)
    (org.apache.samza.system IncomingMessageEnvelope SystemStream OutgoingMessageEnvelope)
    (kafka.admin ReassignPartitionsCommand))
  (:require [clojure.tools.logging :as log]))

(gen-class
  :name vinted.ping-monitor.PingMonitorTask
  :implements [org.apache.samza.task.StreamTask
               org.apache.samza.task.WindowableTask]
  :state state
  :init init
  :prefix -)

(def output-stream (SystemStream. "kafka", "monitoring-ping-stats"))

(defn -init []
  ;; TODO: we have no concurency here!!!
  [[] (atom {})])

;; TODO: we need to get the key (e.g. test-id), like map-reduce to enfore that same test id gets same container
(defn -process
  [this ^IncomingMessageEnvelope envelope _ _]
  (log/debug "in process: new")
  (let [;msg (.getMessage envelope)
        ; TODO: decode avro/json msg & get the test id & msg id
         msg-num 6
         test-id 1]
    (reset! (.state this)
            (update-in @(.state this) [test-id :received msg-num] (fn [_] 1)))))

;; TODO: ignore the last few minutes... simplest would be to store last-window and current-window stats, and use last-window for reporting
(defn -window
  [this collector _]
  (doseq [[test-id v] @(.state this)]
    (let [msgs-received (keys (:received v))                ;; deduplicated msg ids
          received (count msgs-received)
          expected (apply max msgs-received)                ;; TODO: exclude a few latest ones..
          loss (- expected received)
          msg (str test-id ":" loss)]
      (log/info (str "Window:" msg))
      (->> (OutgoingMessageEnvelope. output-stream msg)
           (.send collector))
      (reset! (.state this) {}))))

;; on java interop: https://kotka.de/blog/2010/02/gen-class_how_it_works_and_how_to_use_it.html
; http://samza.incubator.apache.org/learn/documentation/0.7.0/api/overview.html
; http://clojure-doc.org/articles/language/interop.html