# REST API extension with SQL data source

This Bonita BPM REST API extension allows you to run SQL queries relying on data source provided by Java application server.

Using this extension you can display data coming from 3rd party database in Bonita BPM forms and pages.

1 [Build instructions](build-instructions)<br>
1.1 Setup your IDE<br>
1.2 Retrieving the project from GitHub<br>
1.3 Building the project<br>

2 [Installation instructions](installation-instructions)<br>
2.1 Retrieve the binaries<br>
2.2 Configure REST API authorization<br>
2.3 Setting up the database<br>
2.4 Setting up the data source<br>
2.5 Deploying the resources in Bonita BPM Portal<br>
2.6 Running the sample application<br>


## Build instructions
If you want to modify and build the project, you may follow these instructions.
Otherwise, you can work directly with the binaries available in the [releases](../../releases) section.


### Setup your IDE
These instructions apply to a Eclipse Neon (4.6). You should be able to adapt them easily to other versions of Eclipse or to any other Java/Groovy IDE.

- Go in **Help** > **Install New Software...**
- Click on **Add...** button to add Groovy-Eclipse repository.
- Define a name for the repository, e.g.: Groovy Eclipse.
- Define the repository URL. For Eclipse Neon (4.6): http://dist.springsource.org/snapshot/GRECLIPSE/e4.6/
- In the **Work with** drop down list select `Groovy Eclipse - http://dist.springsource.org/snapshot/GRECLIPSE/e4.6`.
- Select following features to install:
	- Extra Groovy compilers -> Extra compiler 2.4.
	- Groovy-Eclipse -> Groovy-Eclipse Feature.
	- m2e Configurator for Groovy-Eclipse.
- Install (click **Next** button twice, accept license and click **Finish** button) and restart Eclipse.
- Go to **Help** > **Eclipse Marketplace...**
- Search for Spock Plugin and install it.


### Retrieving the project from GitHub

Clone the project:

```shell
git clone git@github.com:Bonitasoft-Community/rest-api-sql-data-source.git
```


### Import the project in Eclipse

- In Eclipse go to **File** -> **Import...** menu.
- In **Maven** select **Existing Maven Projects** and click **Next**.
- Click on **Browse** button and go to the project checkout folder.
- Click **Finish**.

You can now edit the project.


### Building the project

- Make sure you are in the project folder:
```shell
cd rest-api-sql-data-source
```

- Build the project with Maven:
```shell
mvn clean install
```
- Retrieve the generated REST API extension zip: `target/rest-api-sql-datasource.zip`.


## Installation instructions

### Retrieve the binaries

You can either retrieve the binaries from the [releases](../../releases) and unzip them or start from the built project (see build instructions).


### Configure REST API authorization

- Only if you are deploying the REST API extension in the Tomcat embedded in Bonita BPM Studio:
  - Go in `workspace/tomcat/setup`
  - Edit `database.properties` file
  - Set the `h2.database` property with the full path to the `h2_database` folder located in `workspace/default` folder (replace the existing `../h2_database` default value).
  - If you are using Mac or Linux, make sure that setup.sh is executable: `chmod u+x setup.sh`.
- Run the setup tool to retrieve the configuration: `setup.bat pull` (Windows), `setup.sh pull` (Mac, Linux).
- Edit `custom-permissions-mapping.properties` file located in `tenants/1/tenant_portal` directory. When you pull the configuration with the "setup" tool, `tenants` folder is located in `setup/platform_conf_current`.
- Add a the following permission on a new line at the end of the file: `profile|User=[demoPermission]`
- Run the setup tool to apply the configuration changes: `setup.bat push` (Windows), `setup.sh push` (Mac, Linux).

### Setting up the database

- Create a PostgreSQL database named `demo` owned by user `bonita` with password `bpm`.
- Run the `createExample.sql` script to create the table and populate it with sample data.


### Setting up the data source

These instructions apply to Tomcat.

- Edit the `conf/Catalina/localhost/bonita.xml` (in Bonita BPM Studio `conf` folder is located in `workspace/tomcat/server`) file and add a new data source:

```xml
<Resource name="demoDS"
              auth="Container"
              type="javax.sql.DataSource"
              maxActive="10"
              minIdle="1"
              maxWait="10000"
              initialSize="3"
              removeAbandoned="true"
              logAbandoned="true"
              username="bonita"
              password="bpm"
              driverClassName="org.postgresql.Driver"
              url="jdbc:postgresql://localhost:5432/demo"/>
```

- Add the [PostgreSQL jdbc driver](https://jdbc.postgresql.org/download.html) jar in the `lib/bonita` folder (in Bonita BPM Studio `lib` folder is located in `workspace/tomcat/server`).
- Restart Tomcat. For Bonita BPM Studio, go in "Server" menu and select "Restart web server".


### Deploying the resources in Bonita BPM Portal

- Log in with a user with Administrator profile in Bonita BPM Portal.
- Switch to administrator view.
- Go to "Resources" and click on the "Add" button to import the REST API Extension file: `rest-api-sql-datasource.zip`.
- Import the sample viewer page `page-apiExtensionDatasourceViewer.zip`
- Go to "Applications" and import the living application `application.xml`


### Running the sample application

- Log in with a user with Administrator profile in Bonita BPM Portal
- Navigate to this URL:
[http://localhost:8080/bonita/apps/sqlDemo/example/](http://localhost:8080/bonita/apps/sqlDemo/example/). You might need to adapt the port number (8080) especially if you are running Tomcat embedded in Bonita BPM Studio.
