# Lock Server Sample

Run this project as a Spring Boot app (e.g. import into IDE and run
main method, or use "mvn spring-boot:run"). It will start up on port
8989 and serve the locks from "/".

## Resources

| Path             | Description  |
|------------------|--------------|
| /                | list locks   |
| /{name}          | POST to get a new lock |
| /{name}/{value}  | DELETE to release lock, PUT to extend expiry |


```
$ curl localhost:8989
[]
dsyer@dsyer:~/dev/platform/scripts$ curl localhost:8989/foo -d foo
{"name":"foo","value":"9ca1067e-af94-41cb-8aef-813d094d0d96","expires":1407181691424}
$ curl localhost:8989
[{"name":"foo","value":"9ca1067e-af94-41cb-8aef-813d094d0d96","expires":1407181691424}]
$ curl -X PUT localhost:8989/foo/9ca1067e-af94-41cb-8aef-813d094d0d96 -d foo
{"name":"foo","value":"9ca1067e-af94-41cb-8aef-813d094d0d96","expires":1407181706667}
$ curl localhost:8989
[{"name":"foo","value":"9ca1067e-af94-41cb-8aef-813d094d0d96","expires":1407181706667}]
$ curl -X DELETE localhost:8989/foo/9ca1067e-af94-41cb-8aef-813d094d0d96 -d foo
{"status":"OK"}
$ curl localhost:8989
[]
```
