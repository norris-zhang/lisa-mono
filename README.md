# lisa-mono

## Flyway
```shell
$ mvn flyway:clean flyway:migrate -Dflyway.user=lisa -Dflyway.password=password -Dflyway.url=jdbc:postgresql://localhost:5432/lisadb -Dflyway.cleanDisabled=false
```

## JDK
Java 17+

## Start Application
```shell
$ mvn spring-boot:run
# or
$ java com.guoba.lisa.LisaApplication
# or
$ java -jar target/lisa-0.0.1-SNAPSHOT.jar
```

## 
