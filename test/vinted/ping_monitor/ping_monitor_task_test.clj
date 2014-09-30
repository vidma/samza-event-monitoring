(ns vinted.ping-monitor.ping-monitor-task-test
  (:import (java.util.concurrent CountDownLatch TimeUnit)
           (vinted.ping-monitor PingMonitorTask)
           (org.apache.samza.system IncomingMessageEnvelope SystemStreamPartition)
           (org.apache.samza.task MessageCollector TaskCoordinator)
           (org.apache.samza Partition))
  (:require [clojure.test :refer :all]
            [clojure.tools.logging :as log]))

(deftest kafka-producer-future-is-realized
  (testing "kafka-producer-future is eventually realized"
    (let [part (SystemStreamPartition. "sys" "stream" (Partition. 1))
          collector (reify MessageCollector
                      (send [this envelope]))]
      (doto (PingMonitorTask.)
        (.process (IncomingMessageEnvelope. part "0", "k", "m") nil nil)
        (.window collector nil)))))
