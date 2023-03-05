```shell
$ curl -v -b "JSESSIONID=1FB5EDFAF64CEE5DBD80CDDECF4B4C15" -X POST http://localhost:8080/actuator/shutdown
$ tail -f nohup.out
$ ps -ef | grep java
$ git pull
$ mvn clean install
$ rm nohup.out
$ nohup java -jar target/lisa-0.0.1-SNAPSHOT.jar &
$ tail -f nohup.out
```