# Data Modelling Tools

Set of tools, written in Java, to support model driven development.

## Using the tools

Copy the data modelling tools to a location on your computer.

Set user environment variables `JAVACMD` and `DMT_HOME` using the [`setx`](https://docs.microsoft.com/en-us/windows-server/administration/windows-commands/setx) command on the Windows command line. `JAVACMD` must point to the java.exe in a **32-bit** Java installation and is used in the .bat-files (it is defined by the appassembler-maven-plugin). `DMT_HOME` is used when invoking the .bat-files from within a script in Enterprise Architect (it is defined by this project).

```
SETX JAVACMD "C:\path\to\OpenJDK\x86-32_11.0.2_9\jdk-11.0.2+9\bin\java.exe"
SETX DMT_HOME "C:\path\to\folder\containing\bin\conf\log\and\repo"
```

Close the command line window and open a new one. Check using `echo`, that the environment variables are set correctly the input should show the chosen paths:

```
echo %JAVACMD% 
echo %DMT_HOME%
```

Restart Enterprise Architect, if it is open.

Run a script from within Enterprise Architect that calls a bat file, it will contain something like the following:

```
runBatFileInDefaultWorkingDirectory("export-scripts.bat", "options");
```

Alternatively, use a script by invoking

```
%DMT_HOME%\bin\script-name.bat
```

on a command line, e.g. 

```
%DMT_HOME%\bin\export-scripts.bat
```

The options will be shown in the output. Output is with the logging configuration that comes with the tools (config\logback.xml) shown in the command line, in the Script Window Enterprise Architect and it is saved in file log\output.log.

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
|   |       logback.xml
|   |       
|   +---log
|   \---repo
|           *.jar
```

`mvn verify` verifies the code using the static code analyser tools [CheckStyle](https://checkstyle.org/), [PMD](https://pmd.github.io/) and [SpotBugs](https://spotbugs.github.io/).

`mvn site` creates an overview of the projects, include CheckStyle, PMD and SpotBugs reports.

`mvn versions:display-plugin-updates` finds the plugin versions that should be updated.

## Adding applications

Add a new application by creating a class that extends from `AbstractApplication`. It must have a main method. Create a new `program` entry in pom.xml in `/project/build/plugins/plugin[artifactId='appassembler-maven-plugin']/executions/execution/configuration/programs`.