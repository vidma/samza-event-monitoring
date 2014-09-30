# samza-ping-monitoring

Using Samza with Clojure...

Use monitoring events (batch-id, sequence-id) to calculate the event loss.


## Runing test environment (locally)


  ```
  bin/grid bootstrap
  ```
    
  check YARN is up at: http://localhost:8088
    
  ```
  lein clean && lein with-profile production jar && cp target/samza-ping-monitoring-0.1.0-SNAPSHOT.jar ./our-samza-app.jar && lein pom && mvn clean package
  rm -rf deploy/samza && mkdir -p deploy/samza && tar -xvf target/samza-ping-monitoring-*-dist.tar.gz -C deploy/samza
  ```

  now you shall be able to start a samza job:
    
  ```
  deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/resources/ping-monitor.properties
  ```

  more info: http://samza.incubator.apache.org/startup/hello-samza/0.7.0/


## Build and Deploy

  The clj app is built in standart way with Leiningen. 
  
  Then lein generates a .pom, and maven builds a fat assembly file (tar.gz). This `tar.gz` contains
   all the prerequisites including config, Samza jars and scripts...
  
  ```
  lein clean && lein with-profile production uberjar && lein pom && mvn clean package
  
  # optional: check the contents of generated tar.gz
  tar -tvzf target/samza-ping-monitoring-*-dist.tar.gz
  
  # copy this file over to hdfs
  ```

## License

Note: bin/grid is based on `hello-samza` example project.

Copyright © 2014 Vinted

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
