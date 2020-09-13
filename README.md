# mutant-detector
Basic REST API that will detect if an ADN sequence belongs to a human being or a mutant.

# Running in local environment
- Clone this repository and navigate to it's root directory:
```
git clone https://github.com/facualderete/mutant-detector.git
cd mutant-detector
```
- Compile:
```
// Run all tests and generate artifacts
mvn clean install
```
- Build and run docker image: here you'll see how a Redis container is created along with the app's container.
```
docker-compose up --build
```
- Using the app:
```
// Once docker images are initialized, app will be available on localhost:8080
// but you can interact with it via Swagger in this URL:
http://localhost:8080/mutant-detector/swagger-ui/

// You can also use plain cURL calls like this:
curl -X GET "http://localhost:8080/mutant-detector/stats" -H "accept: */*"
curl -X POST "http://localhost:8080/mutant-detector/mutant" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"dna\": [ \"ATGCGA\", \"CAGTGC\", \"TTATGT\", \"AGAAGG\", \"CCCCTA\", \"TCACTG\" ]}"```
- Access local Redis container using redis-cli:
```
redis-cli -h localhost -p 6379
```
- Run commands on local Redis container:
```
// See all available keys
keys *
// See mutant-count
get mutant-count
// See human-count
get mutant-count
// See all evaluations
hgetall evaluation-result
// Drop all keys
FLUSHALL
```

# About deployment on Heroku
The app is deployed in a Heroku private environment that was provisioned with a Redis Cloud addon.
You can access it on 
https://mutant-detector-api.herokuapp.com/mutant-detector/swagger-ui/

This environment is private and you won't be able to deploy a new version without my credentials, but the steps I follow for deployment are:
```
// Login
heroku login
// Link the github repository to the Heroku app
heroku git:remote -a mutant-detector-api
// Push and deploy
git push heroku master
```
This app is also ready to be deployed on any environment. You just need a Redis server and set following environment variables to the app:
* REDISCLOUD_URL: URL to Redis server.
* REDISCLOUD_PASSWORD: password to Redis server.

# Understanding the data structures used in Redis
* Human and mutant counts used for statistics purposes are stored on two key/value pairs that are incremented with the evaluation results accordingly. Redisson's RAtomicLong object guarantees thread safety on increment operations:
    * mutant-count -> long (RAtomicLong)
    * human-count -> long (RAtomicLong)
* Evaluation results are stored using Redisson's RMap<String, Boolean>:
    * SHA256(dna) -> isMutant (true/false)
* This way we can avoid evaluations over DNA sequences that were already evaluated in the past.
* All database operations are executed in order O(1).

# Testing and coverage
* Testcontainers framework provides the possibility to instantiate docker containers for interaction with the app during integration tests. Used a GenericContainer instance running Redis image "redis:3.2.1".
* Code coverage is verified by JaCoCo plugin by adding the following rule:
```
<rule>
    <element>BUNDLE</element>
    <limits>
        <limit>
            <counter>INSTRUCTION</counter>
            <value>COVEREDRATIO</value>
            <minimum>0.80</minimum>
        </limit>
        <limit>
            <counter>LINE</counter>
            <value>COVEREDRATIO</value>
            <minimum>0.80</minimum>
        </limit>
        <limit>
            <counter>CLASS</counter>
            <value>COVEREDRATIO</value>
            <minimum>0.80</minimum>
        </limit>
    </limits>
</rule>
```
Which will guarantee INSTRUCTION, LINE and CLASS coverage with a minimum of 80%. See https://www.jacoco.org/jacoco/trunk/doc/counters.html for further reference.