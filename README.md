# ApiJMeterTest
JMeter Test Purely in Java
------------------------------

Here's how to build a JMeter Test Plan from scratch using Java code only.

* JMeter Test configurations are described in file ([config.properties](config.properties))
    * `test.script.location`    is destination of generated jmx scrip file
    * `test.report.file.jtl`    is destination of generated jtl file  
    * `test.report.file.csv`    is destination of generated csv file  
    * `test.report.file.html`   is destination of generated dashboard report folder (__html report__)
    
    > __note:__ above mentioned files and folders must not exist before running test: 
    
    > e.x. report folder must not exist or must be empty
    
    * `test.rampUp`             is period (sec) tells JMeter how long to take to "ramp-up" to the full number of threads
    * `test.numThreads`         it represents the total number of virtual users performing the test script execution.
    * `test.loops`              is the number of executions for the script.

* Endpoints are described as xml file ([endpoints.xml](endpoints.xml)) and parsed to java beans using [JAXB](https://javaee.github.io/jaxb-v2/) lib
* Deploying an Executable Test JAR File -> `$ mvn package`