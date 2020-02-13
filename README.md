# Postal Service

This project wraps RabbitMQ into a higher level API that should make it easy to send and receive messages. In order
to use it you need to specify the following properties and make them available to Guice (presumably through a mvn
profile that populates a properties file). Note that these properties require a prefix that you specify and then
pass to the constructor of the Guice module. In this way you could conceivably start two PostalServiceModules with
each one pointing to different RabbitMQ daemons.

```
<your-prefix>.postalservice.username
<your-prefix>.postalservice.password
<your-prefix>.postalservice.virtualhost
<your-prefix>.postalservice.hostname
<your-prefix>.postalservice.port
```

Then install the module like: new PostalServiceModule(<your-prefix>, properties);

## To build this project
Execute the Maven command (a Maven profile is not required):

```
  mvn clean package install
```
