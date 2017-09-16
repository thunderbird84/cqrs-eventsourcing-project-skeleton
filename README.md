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
```


 
 

