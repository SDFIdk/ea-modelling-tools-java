# EA Modelling Tools

This project contains a set of tools, written in Java, to support model driven development for models made with Sparx
Enterprise Architect (EA).

ℹ️ This project is work in progress, no releases have been made yet.

## Using the tools

### Prerequisites

1. The modelling tools only work on Windows systems.
2. Verify that you have installed a Java Runtime Environment (JRE) that is
    1. version 11 or later (as required according to the pom.xml file, see `/project/build/plugins/plugin[artifactId='maven-compiler-plugin']/configuration/release`)
    2. 32 bit (x86), as [EA is a 32-bit application](https://sparxsystems.com/products/ea/sysreq.html)
3. Copy the file SSJavaCom.dll located in <EA installation folder>/Java API to
    1. <Windows folder>/SysWOW64 (on a 64-bit machine)
    2. <Windows folder>/System32 (on a 32-bit machine)

    See also https://sparxsystems.com/enterprise_architect_user_guide/15.2/automation/automation_connect_setup.html.
    
    ⚠️ Administrator rights are needed for this operation.

### Installation

1. Download the modelling tools to a location on your computer.
2. Set user environment variables `JAVACMD` and `EAMT_HOME` using the [`setx`](https://docs.microsoft.com/en-us/windows-server/administration/windows-commands/setx "setx | Microsoft Docs") command on the Windows command line. `JAVACMD` must point to the java.exe in a **32-bit** Java installation and is used in the .bat-files. `EAMT_HOME` is used when invoking the .bat-files from within a script in Enterprise Architect.
    
    ```
    SETX JAVACMD "C:\path\to\jre\or\jdk\bin\java.exe"
    SETX EAMT_HOME "C:\path\to\folder\containing\bin\conf\log\and\repo"
    ```

    Note: The user environment variable name `JAVACMD` is defined by the [appassembler-maven-plugin](https://github.com/mojohaus/appassembler), the user environment variable name `EAMT_HOME` is defined by this project.

3. Close the command line window and open a new one. Check using `echo`, that the environment variables are set correctly. The input should show the chosen paths:
    
    ```
    echo %JAVACMD% 
    echo %EAMT_HOME%
    ```

4. Restart Enterprise Architect, if it is open.
5. Install the scripts from TODO.

### Usage

#### In Enterprise Architect

Run a script from within Enterprise Architect that calls a bat file, it will contain something like the following:

```
runBatFileInDefaultWorkingDirectory("export-scripts.bat", "options");
```

#### On the command line

Find the process id of the Enterprise Architect process using the [tasklist](https://docs.microsoft.com/en-us/windows-server/administration/windows-commands/tasklist) commmand: `TASKLIST /V /FO CSV /NH /FI "IMAGENAME eq EA.exe"`. Provide that process id as option `-eapid` to the main class you want to run.

Use an application directly by invoking

```
%EAMT_HOME%\bin\script-name.bat
```

on a command line, e.g.

```
%EAMT_HOME%\bin\export-scripts.bat
```

The options will be shown in the output. For most scripts, one of the options is `-eapid`, use the process id identified with the `tasklist` command as described above.

The output is produced with the logging configuration that comes with the tools (conf\logback.xml). It is shown in the command line and in the Script Window Enterprise Architect and it is saved in file log\output.log.

#### In an Integrated Development Environment (IDE)

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
