```shell
$ git pull
$ mvn clean install
$ curl -v -b "JSESSIONID=xxxxxxx" -X POST http://localhost:8080/actuator/shutdown
$ nohup java -jar target/lisa-0.0.1-SNAPSHOT.jar &
$ tail -f nohup.out
```