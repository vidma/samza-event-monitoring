(def slf4j-version "1.7.6")
(def metrics-clojure-version "2.3.0-beta2")


(defproject samza-ping-monitoring "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"

  ;; Abort when version ranges or version conflicts are detected in
  ;; dependencies. Also supports :warn to simply emit warnings.
  ;; requires lein 2.2.0+.
  :pedantic? :abort

  ;; TODO: log4j deps
  :dependencies [[org.clojure/clojure "1.6.0"]
                 ; TODO: logback/log4f, do we need it here with Samza?
                 [org.clojure/tools.logging "0.2.6"]
                 [ch.qos.logback/logback-classic "1.1.2"]
                 [org.slf4j/log4j-over-slf4j ~slf4j-version]
                 [me.moocar/logback-gelf "0.11"]

                 [clj-statsd "0.3.10"]                      ; TODO: samza also provides stats/metrics...
                 [clojurewerkz/buffy "1.0.0-beta4"]

                 ;; samza check ; from https://www.versioneye.com/search?q=samza
                 [org.apache.samza/samza-serializers_2.10 "0.7.0"]
                 [org.apache.samza/samza-core_2.10 "0.7.0"]
                 [org.apache.samza/samza-api "0.7.0"]
                 [org.apache.samza/samza-kafka_2.10 "0.7.0"]
                 [org.apache.samza/samza-serializers_2.10 "0.7.0"]
                 [org.apache.samza/samza-yarn_2.10 "0.7.0" :exclusions [org.codehaus.plexus/plexus-utils org.slf4j/slf4j-simple log4j jline junit org.slf4j/slf4j-log4j12 org.apache.zookeeper/zookeeper org.codehaus.jackson/jackson-mapper-asl org.apache.commons/commons-compress org.codehaus.jackson/jackson-core-asl]]
                 [org.apache.samza/samza-kv_2.10 "0.7.0"]


                 ; [org.slf4j/slf4j-log4j12]
                 [com.damballa/abracad "0.4.11" :exclusions [org.codehaus.plexus/plexus-utils org.slf4j/slf4j-simple log4j jline org.slf4j/slf4j-log4j12 org.codehaus.jackson/jackson-mapper-asl org.apache.commons/commons-compress org.codehaus.jackson/jackson-core-asl]]

                 ;; TODO: indirect dependencies, used in POM.xml only
                 [org.apache.hadoop/hadoop-hdfs "2.2.0"]
                 [org.apache.samza/samza-shell "0.7.0" :classifier "dist" :extension "tgz"]
                 [org.apache.kafka/kafka_2.10 "0.8.1.1"]    ;; TODO: ver; needed?

                 ;; TODO: mandatory deps here: https://samza.incubator.apache.org/startup/download/
                 ;; TODO: not needed? [org.codehaus.jackson/jackson-jaxrs]
                 ]

  ;; We are using logback, so exclude other loggers
  :exclusions [[org.slf4j/slf4j-log4j12]
               [org.slf4j/slf4j-simple]
               [jline]
               [log4j]
               [org.codehaus.plexus/plexus-utils]]

  ;; aggregate all artifacts into .tar.gz for samza: lein->pom->maven-assembly->tar.gz

  ;; plugins here will be propagated to the pom but not used by Leiningen.
  ;; see https://github.com/technomancy/leiningen/blob/master/sample.project.clj
  :pom-plugins [[org.apache.maven.plugins/maven-assembly-plugin "2.3"
                 ;; this section is optional, values have the same syntax as pom-addition
                 {:configuration [:descriptors [:descriptor "assembly/samza-tar.xml"]]
                  :executions ([:execution [:id "make-assembly"]
                                [:goals ([:goal "single"])]
                                [:phase "package"]])}]]


  :profiles {:dev {
                    :source-paths ["src" "test"]
                    :bootclasspath true
                    :classifiers ^:replace []
                    :javac-options ["-target" "1.7" "-source" "1.7"]
                    :aot :all
                    }
              :production {:source-paths ["src" "test"]
                         :bootclasspath true
                         :aot :all
                         ; :jvm-opts ["-server"]
                         :classifiers ^:replace []
                         :javac-options ["-target" "1.7" "-source" "1.7"]}})
