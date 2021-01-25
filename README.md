![Team icon](TeamIcon.png)

# FDM Gamification
A standalone web application that gamifies [FDM] services to provide an interactive and enjoyable learning environment.

## Feature
- Admin Console
- Service stream selection
- Challenges explore page
- Challenge and question completion counter
- Questions variation:
    - *Drag and drop*
    - *Multiple choices*
    - *Text box*
- User's answers caching
- User statistic analysis
- Leaderboard

## Requirements
* [Java v11] or greater
* [Spring Boot v2.4.1] or greater
* [jQuery v3.5.1]
* [jQuery DataTable]
* [jQuery UI]
* [Font Awesome v4.7.0]
* [MySQL Database]

## Configuration

#### Database Connection
In [application.properties](src/main/resources/application.properties):
```properties
database.host=DATABASE_HOST
database.port=DATABASE_PORT
database.name=DATABASE_NAME

#Or self configured url
spring.datasource.jdbcUrl=jdbc:mysql://${database.host}:${database.port}/${database.name}
spring.datasource.username=DATABASE_USERNAME
spring.datasource.password=DATABASE_PASSWORD
```

#### SSH Tunneling
In [application.properties](src/main/resources/application.properties):
```properties
database.rhost=SSH_SERVER_HOST
database.rport=SSH_SERVER_DATABASE_PORT
```
1. Create the file sshConfig.properties in [src/main/resources](src/main/resources)
2. Fill in the values
```properties
ssh.tunnel.enabled=true/false
ssh.tunnel.url=SSH_SERVER_URL
ssh.tunnel.port=SSH_SERVER_PORT
ssh.tunnel.username=USERNAME
ssh.tunnel.password=PASSWORD
```

# Run the project
* Run: `$ gradlew bootRun`
* Build: `$ gradlew build`
* Build from scratch: `$ gradlew clean build`

\**Note: Environment variable JAVA_HOME must be set using version greater than or equal to 11.* 

[//]: #

[FDM]: <https://www.fdmgroup.com>
[Java v11]: <https://www.oracle.com/java/technologies/javase-jdk11-downloads.html>
[Spring Boot v2.4.1]: <https://spring.io/projects/spring-boot>
[jQuery v3.5.1]: <https://jquery.com/>
[jQuery DataTable]: <https://datatables.net/>
[jQuery UI]: <https://jqueryui.com/>
[Font Awesome v4.7.0]: <https://fontawesome.com/v4.7.0/>
[MySQL Database]: <https://www.mysql.com/>