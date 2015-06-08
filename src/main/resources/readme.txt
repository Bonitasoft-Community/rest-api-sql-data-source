This is a Rest API Extension example archive for Bonita BPM Portal.

You can customize this resource by modifying the existing resources or by adding/removing some others.

This example execute an SQL query via a data source.
The data source must be set in your web container/application server and you should add the driver in the lib folder as well in your web container/application server.

In order to hide the SQL query to the end user, a file which contains a mapping queryId/SQL Query can be configurable.
Warning: only string parameters can be added dynamically in that example.

The default URL to execute a query is: http://{ip}:{port}/bonita/API/extension/sql?queryId={queryId}

Content detail:

Index.groovy           Groovy source code of the extension
page.properties        File containing resource metadata (such as name, displayName, description, type)
datasource.properties  File containing properties to set the datasource (datasource.name is mandatory)
queries.properties     File containing the mapping between the queryId used by the Rest Client and the real query
readme.txt             this file



