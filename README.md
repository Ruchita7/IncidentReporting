# IncidentReporting
Spring boot application using Spring Data JPA and REST APIs for creating, editing and deleting Incident Reports. The application uses MySQL db and docker compose file to configure all of the application’s service dependencies 

### Pre-requisites 
* Download and installation of Java 8 or above
* Download and installation of maven
* Download and setup Docker desktop (https://www.docker.com/products/docker-desktop)
* Optional : Download and install MySQL Workbench (https://dev.mysql.com/downloads/workbench) 
* Download and install Postman

### Run the application
* On terminal execute the command `mvn clean install` for downloading all the dependencies defined in pom.xml
* Open docker-compose.yml and update the <b>MYSQL_ROOT_PASSWORD</b> with a password of your choice. The same password and the port 3306(as mentioned in docker-compose.yml file) will be used to configure MySQL Workbench to connect to incident DB(DB name is as per the yml file created).
* On terminal execute the command `` docker-compose up -d --build `` to run the services that are defined in `docker-compose.yml` . Here it is MYSQL.
* Right click on `IncidentReportApplication.java` and click on `Run IncidentReportApplication.java`. Spring boot application will be started.

<b>Swagger documentation</b> for all the APIs created can be viewed at http://localhost:8080/swagger-ui/index.html#
To test the various REST APIs created as part of this application, I have added JUnits for the Controllers and the Services. These Junits are added at `src/test/java/com/example/incidentreport/small`. For end-to-end/integration testing have added Test classes at `src/test/java/com/example/incidentreport/integration`.

CURLs for various APIs exposed(further details and API response code details can be found in Swagger documentation)
Postman Collection of the APIs can be found at https://www.getpostman.com/collections/c2adb7c30eb93d5b83a9


### Users

#### Create User

    curl --location --request POST 'http://localhost:8080/user' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "userName": "test",
    "firstName": "admin",
    "lastName": "user"
    }'

#### Update User

    curl --location --request PUT 'localhost:8080/user/test' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "userName": "test",
    "firstName": "amadmin",
    "lastName": "user"
    }'

#### Delete a user

    curl --location --request DELETE 'localhost:8080/user/test'

#### Get user by username

    curl --location --request GET 'localhost:8080/user/test'

#### Get all users

    curl --location --request GET 'localhost:8080/users'

### Incidents

#### Create an incident

    curl --location --request POST 'localhost:8080/incident' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "title": "Machine allocation",
    "assignee": "test",
    "creator": "test"
    }'

#### List existing incident reports

    curl --location --request GET 'localhost:8080/incidents'

#### Update incident

    curl --location --request PATCH 'localhost:8080/incident?incident_id=3&loggedin_user=test' \
    --header 'Content-Type: application/json' \
    --data-raw '{
    "title": "Machine allocation New"
    }'

#### Delete incident

    curl --location --request DELETE 'localhost:8080/incident?incident_id=5&loggedin_user=test1'



