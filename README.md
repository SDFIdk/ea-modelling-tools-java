# EA Modelling Tools

This repository contains a set of tools, written in Java, to support model driven development for models made with [Sparx Enterprise Architect (EA)](https://sparxsystems.com/). These tools are supposed to be used together with the scripts from the repository EA Modelling Tools JavaScript.

## Using the tools

### Prerequisites

1. **Windows**: The modelling tools only work on Windows systems.
2. **Enterprise Architect**: Verify that you have installed Enterprise Architect and that folder `<EA installation folder>\Java API` exists. It contains at least files `SSJavaCOM.dll` and `eaapi.jar` and it can also contain `SSJavaCOM64.dll`.
3. **Java**: Verify that you have installed a Java Runtime Environment (JRE) that is version 11 or later.
    1. You may choose between a 64-bit JRE or a 32-bit JRE if `<EA installation folder>\Java API\SSJavaCOM64.dll` exists.
    2. You must use a 32-bit JRE if `<EA installation folder>\Java API\SSJavaCOM64.dll` does not exist.

    💡 Type `java -XshowSettings:properties -version` or `C:\path\to\jre\or\jdk\bin\java.exe -XshowSettings:properties -version` on the command line to view all properties of your Java installation.

### Installation

1. **Download**:
    1. Go to the "Releases", where you can find a link to a zip file containing the packaged modelling tools. Find more information about viewing releases on GitLab instances in the [GitLab Docs](https://docs.gitlab.com/ee/user/project/releases/#view-releases) and about viewing releases on GitHub in the [GitHub Docs](https://docs.github.com/en/repositories/releasing-projects-on-github/viewing-your-repositorys-releases-and-tags#viewing-releases).
    2. Download the modelling tools and unzip the zip file.
    3. Place the contents somewhere in a suitable location, in a folder called `ea-modelling-tools-java` (so without the version number).

   Verify that you have the following structure in `C:\path\to\ea-modelling-tools-java`:

   ```
   +---C:\path\to\ea-modelling-tools-java
   |   +---bin
   |   |       *.bat
   |   |
   |   +---conf
   |   |   |   logback.xml
   |   |   |
   |   |   \---templates
   |   |           *.ftl
   |   |           *.ftlx
   |   |
   |   +---log
   |   \---repo
   |           *.jar
   ```

   ⚠ If you do not have the structure above, verify that you downloaded the _packaged_ modelling tools, as opposed to the _source code_ of the modelling tools.
   
   ℹ️ Leaving out the version number makes it easier to install a newer version of the tools, as `EAMT_HOME` (see next step) then does not have to be set again.

2. **User environment variables**: Set user environment variables `EAMT_HOME` (required), `EA_JAVA_API` (required), `JAVACMD` (conditional), for example by using the [`setx`](https://docs.microsoft.com/en-us/windows-server/administration/windows-commands/setx "setx | Microsoft Docs") command on the Windows command line.
   1. **(required)** `EAMT_HOME` must point to the ea-modelling-tools-java folder and is used when invoking the .bat-files from within a script in Enterprise Architect. This environment variable is defined by the EA Modelling Tools.
   2. **(required)** `EA_JAVA_API` must point to the folder Java API in your EA installation folder (see also the prerequisites) and is used in the .bat-files. This environment variable is defined by the EA Modelling Tools.
   3. **(conditional)** **Only if** another java.exe is to be used than the one invoked by calling `java` on the command line, `JAVACMD` must point to the java.exe that is to be used for running the tools. `JAVACMD` must not contain whitespace. `JAVACMD` is used in the .bat-files. This environment variable is defined by the [appassembler-maven-plugin](https://github.com/mojohaus/appassembler).
   
      💡 Get all the details of the default java by calling `java -XshowSettings:properties --version` on the command line.


   ```bat
   rem Set required user environment variable EAMT_HOME
   setx EAMT_HOME "C:\path\to\ea-modelling-tools-java"
   rem Set required user environment variable EA_JAVA_API
   setx EA_JAVA_API "C:\path\to\EA\installation\folder\Java API"
   rem The next command is only needed when another java installation than the default one should be used!
   rem Set conditional user environment variable JAVACMD, make sure that it does not contain whitespace!
   setx JAVACMD "C:\path\to\jre\or\jdk\bin\java.exe"
   ```

3. Close the command line window and open a **new** one. Verify using `echo`, that the environment variables are set correctly. The input should show the chosen paths:

   ```bat
   echo %EAMT_HOME%
   echo %EA_JAVA_API%
   echo %JAVACMD% 
   ```

4. Restart Enterprise Architect, if it is open.
5. Import the scripts from the repository EA Modelling Tools JavaScript.

### Usage

#### In Enterprise Architect

Run a script from the repository EA Modelling Tools JavaScript that calls a bat file (e.g. `export-eamt-scripts`), it will contain something like the following:

```js
runBatFileInDefaultWorkingDirectory("script-name.bat", "options");
```

#### On the command line

Find the process id of the Enterprise Architect process by using the [tasklist](https://docs.microsoft.com/en-us/windows-server/administration/windows-commands/tasklist) command: `TASKLIST /V /FO CSV /NH /FI "IMAGENAME eq EA.exe"`, or by using script `retrieve-process-id-of-running-ea-instance` in EA Modelling Tools JavaScript.

Provide that process id as option `-eapid` to the main class you want to run.

Use an application directly by navigating to the folder containing the tools and invoking a script:

```bat
cd %EAMT_HOME%
bin\script-name.bat
```

e.g.

```bat
bin\export-scripts.bat
```

The options will be shown in the output. For most scripts, one of the options is `-eapid`, use the process id identified in the previous step.

The output is produced with the logging configuration that comes with the tools (conf\logback.xml). It is shown in the command line and in the Script Window Enterprise Architect and it is saved in file log\output.log.

## For developers

### Using the tools in an Integrated Development Environment (IDE)

Add folder `src/main/config` to the class path of the project.

⚠️ Do **not** add folder `src/main/config` as a source folder, as the contents otherwise will be add to folder target by your IDE during the building of the project, and this does not reflect the Maven configuration.

ℹ️ The maven-assembly-plugin assembles the
code in such a way that the contents of the `conf` folder is added to the classpath, see also "Building the tools" and the [Maven configuration](./pom.xml).

ℹ️ The maven-surefire-plugin has `src/main/config` as additional class path entry, see the [Maven configuration](./pom.xml).

ℹ️ The reason for this set-up is that end users should have access to the templates in the config folder, so they can see the contents, and the templates can be modified locally when needed. Otherwise they would end up in the jar-file itself, and would not be editable.

Find the process id of the Enterprise Architect process by using the [tasklist](https://docs.microsoft.com/en-us/windows-server/administration/windows-commands/tasklist) command: `TASKLIST /V /FO CSV /NH /FI "IMAGENAME eq EA.exe"`, or by using script `retrieve-process-id-of-running-ea-instance` in EA Modelling Tools JavaScript.

Provide that process id as option `-eapid` to the main class you want to run.

### Building the tools

[Maven](http://maven.apache.org/download.cgi) version 3.6 or later is needed to build the project, see also pom.xml.

The project has a dependency on the Enterprise Architect Java API, which can be installed by executing the following command:

```bat
mvn install:install-file -Dfile="C:\Program Files\Sparx Systems\EA\Java API\eaapi.jar" -DgroupId=org.sparx -DartifactId=eaapi -Dversion=replace_with_ea_program_version -Dpackaging=jar
```
      
See also [Guide to installing 3rd party JARs](https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html).

The EA API does not change often, so a eaapi.jar from a different EA version can also work.

`mvn package` invokes the assemble goal of the appassembler-maven-plugin. It creates the following structure:

```
+---appassembler
|   +---bin
|   |       *.bat
|   |
|   +---conf
|   |   |   logback.xml
|   |   |
|   |   \---templates
|   |           *.ftl
|   |           *.ftlx
|   |
|   +---log
|   \---repo
|           *.jar
```

`mvn verify` verifies the code using the static code analyser tools [CheckStyle](https://checkstyle.org/), [PMD](https://pmd.github.io/) and [SpotBugs](https://spotbugs.github.io/).

`mvn site` creates an overview of the projects, include CheckStyle, PMD and SpotBugs reports.

Find more information about Maven's build lifecyle on [Introduction to the Build Lifecycle](http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html).

`mvn versions:display-dependency-updates` identifies the dependencies that should be updated because a newer release is available.

`mvn versions:use-latest-releases` updates the dependency versions. The plugin versions and the versions of any dependencies of the plugins have to be updated manually, see next goal.

`mvn versions:display-plugin-updates` identifies the plugin versions that should be updated because a newer release is available.

Read more about the [Versions Maven Plugin](https://www.mojohaus.org/versions/versions-maven-plugin/index.html).


### Adding applications

To add a new application:

1. Create a class that extends from `AbstractApplication` and add a [main method](https://docs.oracle.com/javase/tutorial/getStarted/application/#MAIN) to it.
2. Create a new `program` entry in pom.xml in `/project/build/plugins/plugin[artifactId='appassembler-maven-plugin']/executions/execution/configuration/programs`.
3. Add a corresponding script in EA Modelling Tools Javascript.

### Logging

The logging framework used is [logback](https://logback.qos.ch/manual/index.html).

Logging messages can be done by means of [parameterised logging](https://logback.qos.ch/manual/architecture.html#parametrized), e.g.

```java
LOGGER.debug("This is a message containing two values passed in the next arguments: {} and {}.", argument1, argument2);
```
