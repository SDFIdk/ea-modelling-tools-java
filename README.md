# EA Modelling Tools

This project contains a set of tools, written in Java, to support model driven development for models made with Sparx
Enterprise Architect.

ℹ️ This project is work in progress, no releases have been made yet.

## Using the tools

### In Enterprise Architect

Copy the modelling tools to a location on your computer.

Set user environment variables `JAVACMD` and `EAMT_HOME` using the [`setx`](https://docs.microsoft.com/en-us/windows-server/administration/windows-commands/setx "setx | Microsoft Docs") command on the Windows command line. `JAVACMD` must point to the java.exe in a **32-bit** Java installation and is used in the .bat-files (the name `JAVACMD` is defined by the [appassembler-maven-plugin](https://github.com/mojohaus/appassembler)). `EAMT_HOME` is used when invoking the .bat-files from within a script in Enterprise Architect (the name `EAMT_HOME` is defined by this project).

```
SETX JAVACMD "C:\path\to\OpenJDK\x86-32_11.0.2_9\jdk-11.0.2+9\bin\java.exe"
SETX EAMT_HOME "C:\path\to\folder\containing\bin\conf\log\and\repo"
```

Close the command line window and open a new one. Check using `echo`, that the environment variables are set correctly the input should show the chosen paths:

```
echo %JAVACMD% 
echo %EAMT_HOME%
```

Restart Enterprise Architect, if it is open.

Run a script from within Enterprise Architect that calls a bat file, it will contain something like the following:

```
runBatFileInDefaultWorkingDirectory("export-scripts.bat", "options");
```

Alternatively, use a script directly by invoking

```
%EAMT_HOME%\bin\script-name.bat
```

on a command line, e.g.

```
%EAMT_HOME%\bin\export-scripts.bat
```

The options will be shown in the output. The output is produced with the logging configuration that comes with the tools (config\logback.xml). It is shown in the command line and in the Script Window Enterprise Architect and it is saved in file log\output.log.

### In an Integrated Development Environment (IDE)

Use `src/main/config` as source folder as well, so add it to the build path of the project. Note: the maven-assembly-plugin assembles the
code in such a way that the contents of the `conf` folder is added to the classpath, see also "Building the tools" and the Maven configuration.

Find the process id of the Enterprise Architect process using the [tasklist](https://docs.microsoft.com/en-us/windows-server/administration/windows-commands/tasklist) commmand: `TASKLIST /V /FO CSV /NH /FI "IMAGENAME eq EA.exe"`. Provide that process id as option `-eapid` to the main class you want to run.

## Building the tools

[Maven](http://maven.apache.org/download.cgi) version 3.6 or later is needed to build the project, see also pom.xml.

The project has a dependency on the Enterprise Architect Java API, which can be installed by executing the following command:

```
mvn install:install-file -Dfile="C:\Program Files (x86)\Sparx Systems\EA\Java API\eaapi.jar" -DgroupId=org.sparx -DartifactId=eaapi -Dversion=15.1.1525 -Dpackaging=jar
```
      
See also [Guide to installing 3rd party JARs](https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html).

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

`mvn versions:display-plugin-updates` finds the plugin versions that should be updated.

See more information about Maven's build lifecyle on http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html.

## Adding applications

To add a new application:

1. Create a class that extends from `AbstractApplication` and add a [main method](https://docs.oracle.com/javase/tutorial/getStarted/application/#MAIN) to it.
2. Create a new `program` entry in pom.xml in `/project/build/plugins/plugin[artifactId='appassembler-maven-plugin']/executions/execution/configuration/programs`.