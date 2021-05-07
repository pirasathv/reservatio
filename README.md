# Reservatio

Campsite System Requirements

- The users will need to find out when the campsite is available. So the system should expose an API to provide information of the
availability of the campsite for a given date range with the default being 1 month.
Provide an end point for reserving the campsite.
- The user will provide his/her email & full name at the time of reserving the campsite
along with intended arrival date and departure date. Return a unique booking identifier back to the caller if the reservation is successful.
- The unique booking identifier can be used to modify or cancel the reservation later on. Provide appropriate end point(s) to allow
modification/cancellation of an existing reservation
- Due to the popularity of the island, there is a high likelihood of multiple users attempting to reserve the campsite for the same/overlapping
date(s). Demonstrate with appropriate test cases that the system can gracefully handle concurrent requests to reserve the campsite.
- Provide appropriate error messages to the caller to indicate the error cases.
- In general, the system should be able to handle large volume of requests for getting the campsite availability.
There are no restrictions on how reservations are stored as as long as system constraints are not violated.

#### Solution Approach

Library/Tools used:` K8, Docker-Desktop, AB, Akka/Akka Stream, Akka-Test, Mockito, ScalaLikeJDBC, Flyway, Json4s, Logback/Logstash.. `

Async design, while leveraging akka http/akka stream and root actor system to coordinate/schedule async threads. 
But, repository layer uses a blocking library `jdbclikescala` out of convenience. In order to achieve secure db write, I  controlled that at DB level.
Ideally, it would be best to utilize a library which can easily facilitate optimistic locking with versioning.

For alerting/logging I've logged the error that can be easily tagged in splunk or other metrics tools. Exceptions are properly handled for system requirements.

##### How to run 
  Dependencies: brew install sbt & docker-desktop 
````
sbt docker:publishLocal
kubectl create -f src/manifest/mysql.yaml
kubectl create -f src/manifest/reservatio.yaml
````
##### Concurrency test cases 

![concurrent test cases](https://user-images.githubusercontent.com/9923573/117382110-af61e080-aeab-11eb-8380-66dd73e8bd41.jpg)

````
sh 
script/concurrent_requests_scenario_1.sh
script/concurrent_requests_scenario_2.sh
script/concurrent_requests_scenario_3.sh
script/concurrent_requests_scenario_4.sh
script/concurrent_requests_scenario_5.sh
script/concurrent_requests_scenario_6.sh

````

##### Load testing metric (can be improved)
````
Server Software:        akka-http/10.1.11
Server Hostname:        localhost
Server Port:            9000

Document Path:          /api/reservatio/v1/bookings
Document Length:        372 bytes

Concurrency Level:      100
Time taken for tests:   40.004 seconds
Complete requests:      10000
Failed requests:        0
Keep-Alive requests:    10000
Total transferred:      5320000 bytes
HTML transferred:       3720000 bytes
Requests per second:    249.97 [#/sec] (mean)
Time per request:       400.043 [ms] (mean)
Time per request:       4.000 [ms] (mean, across all concurrent requests)
Transfer rate:          129.87 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.2      0       4
Processing:    14  393 443.5    297    8495
Waiting:       14  393 443.5    297    8495
Total:         14  393 443.6    297    8496

Percentage of the requests served within a certain time (ms)
  50%    297
  66%    424
  75%    522
  80%    603
  90%    786
  95%    906
  98%   1180
  99%   1413
 100%   8496 (longest request)
````
