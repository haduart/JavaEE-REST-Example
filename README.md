# REST API Example with Wildfly
[1.1]: http://i.imgur.com/tXSoThF.png (twitter icon with padding)
[1]: https://twitter.com/flycouchdb
[2]: https://twitter.com/haduart

## Usage

Download [Wildfly](http://wildfly.org/downloads/).

Uncompress it.

Start WildFly:
```bash
./wildfly-8.2.0.Final/bin/standalone.sh
```

Compile, test and deploy this project:
```bash
 mvn clean package test wildfly:deploy
```

In the main path you will find your normal web project. In this case you have a REST
Console so that you can play with the REST API:
http://localhost:8080/RESTExample/

In the /api/ you find the root path where all the API is located for all the services.
For now there's only one service, that is the purchase simulation:
http://localhost:8080/RESTExample/api/

The service for the purchase simulation is in the /api/decision/
So far there's only two operations supported: GET and POST.

GET on the main path of /api/decisions/ will return all the users.
GET on a concrete user will return the current balance of that user: RESTExample/api/decision?email=ed@gmail.com

POST : You can add a new purchase for a new user that will be identified by its email. It expects a JSON object
with the following format:
{"email":"ed@gmail.com","first_name":"Eduard","last_name":"Cespedes","amount":300}


## Contributors

* **(Author)** [Eduard Cespedes Borras](https://github.com/haduart)[![alt text][1.1]][2]


## License

BSD.  See the LICENSE file at the root of this repository.