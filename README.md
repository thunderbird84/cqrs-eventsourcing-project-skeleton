# CQRS Project Skeleton
 CQPS project using akka actor-http micro-service, play2, mysql, redis, qpid, [docker](https://docs.docker.com/engine/installation/), [docker-compose](https://docs.docker.com/compose/install/), maven, [sbt](http://www.scala-sbt.org/0.13/docs/Installing-sbt-on-Linux.html)
 
## Getting Start
Step 1: get code
```
$> git clonet git@github.com:thunderbird84/cqrs-eventsourcing-project-skeleton.git cqrs-skeleton
$> cd cqrs-skeleton  
```
Step 2: Initialize database
```
$> docker-compose up -d mysql
$> docker-compose up -d redis
$> docker-compose up -d qpid
$> ./route.sh
$> mysql -h mysql.robin.examples -uroot # Ctrl +C to exit
$> mysql -h mysql.robin.examples -uroot < schema.sql
```

Step 3: Download dependencies
```
$> mvn clean install
$> sbt
#from sbt console type following command to start micro-services
$> dockerPack
$> usr-service-cmd/dockerComp
$> usr-service-query/dockerComp
$> usr-compounder/dockerComp
## to run service localy: usr-service-cmd/reStart

# In other linux terminal exec below command too add host file
$> ./route.sh
#out put
# 172.25.0.7 usr-compounder.robin.examples
# 172.25.0.6 usr-service-cmd.robin.examples
# 172.25.0.5 usr-service-query.robin.examples
# 172.25.0.4 mysql.robin.examples
# 172.25.0.3 redis.robin.examples
# 172.25.0.2 qpid.robin.examples
 
```
Step 4: Verify services

* [http://usr-service-query.robin.examples](http://usr-service-query.robin.examples)
* [http://usr-service-cmd.robin.examples](http://usr-service-cmd.robin.examples)
* [http://usr-compounder.robin.examples](http://usr-compounder.robin.examples)

Step 5: testing services

create user
```
curl -H "Content-Type: application/json" -X POST -d '{"id":"","firstName": "Robin","lastName": "Snow","age":18,"email": "robin@dev.examples", "secret": "123456"}' "http://localhost:8402/users" 
```

update user
```
curl -H "Content-Type: application/json" -X PUT -d '{"id":"<id>","firstName": "Robin","lastName": "Snow","age":19,"email": "robin@dev.examples", "secret": "123456"}' "http://localhost:8402/users/<id>" 
```
 
 

