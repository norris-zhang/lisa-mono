```shell
$ git pull
$ curl -v -b "JSESSIONID=xxxxxxx" -X POST http://localhost:8080/actuator/shutdown
$ tail -f nohup.out
$ ps -ef | grep java
$ mvn clean install
$ nohup java -jar target/lisa-0.0.1-SNAPSHOT.jar &
$ tail -f nohup.out
```