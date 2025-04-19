[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/dev.magicmq/jython-compiler?nexusVersion=3&server=https%3A%2F%2Frepo.magicmq.dev&label=Latest%20Release)](https://repo.magicmq.dev/#browse/browse:maven-releases:dev%2Fmagicmq%2Fjython-compiler)
[![Latest Snapshot](https://img.shields.io/badge/dynamic/xml?color=orange&label=Latest%20Snapshot&query=%2F%2Fmetadata%2Fversioning%2Fversions%2Fversion%5Blast()%5D&url=https%3A%2F%2Frepo.magicmq.dev%2Frepository%2Fmaven-snapshots%2Fdev%2Fmagicmq%2Fjython-compiler%2Fmaven-metadata.xml)](https://repo.magicmq.dev/#browse/browse:maven-snapshots:dev%2Fmagicmq%2Fpyspigot)
![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/magicmq/JythonCompiler/maven.yml?branch=master)
![Apache 2.0 License](https://img.shields.io/github/license/magicmq/JythonCompiler)

# jython-compile-maven-plugin

jython-compile-maven-plugin is a Maven plugin that uses Jython to compile `.py` files in your project into `.class` files. This allows for bundling of pre-compiled Jython source files into your project for better runtime performance when using them. A standalone version of Jython is included in the plugin JAR file, so there is no need to install Jython separately.

See the [usage](#usage) section below for detailed usage.

This Maven plugin is utilized when building [PySpigot](https://github.com/magicmq/pyspigot), as PySpigot includes some Jython modules in the `/Lib` folder of the distributed JAR file.

## Usage

First, add the following repository to your POM:

```xml
<repositories>
    <repository>
        <id>repo</id>
        <url>https://repo.magicmq.dev/repository/maven-releases/</url>
    </repository>
</repositories>
```

Then, add the plugin to the `plugins` section of your POM:

```xml
<plugin>
    <groupId>dev.magicmq</groupId>
    <artifactId>jython-compile-maven-plugin</artifactId>
    <version>{VERSION}</version>
</plugin>
```
Replace `{VERSION}` with the latest release shown on the badge above.

The plugin is designed to work out of the box without additional configuration. The plugin includes one goal: `python-compile`. By default, the `jython-compile` goal is bound to the `process-resources` phase. It searches your project's resource folder (typically `src/main/resources`) for compilable `.py` files, compiles them, and outputs the compiled files to your project's build output directory (typically `/target/classes`).

### Configuration

#### Phase

The `jython-compile` goal is bound to the `process-resources` phase of the Maven lifecycle by default. To bind the goal to a different phase, use the following:

```xml
<plugin>
    <groupId>dev.magicmq</groupId>
    <artifactId>jython-compile-maven-plugin</artifactId>
    <version>1.1</version>
    <executions>
        <execution>
            <id>jython-compile</id>
            <phase>generate-resources</phase>
            <goals>
                <goal>jython-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### Source Directory

By default, the `jython-compile` goal searches in the project's resources folder for compilable `.py` files (`{project.basedir}/src/main/resources`). To change the source directory, configure the `sourceDirectory` parameter: 

```xml
<plugin>
    <groupId>dev.magicmq</groupId>
    <artifactId>jython-compile-maven-plugin</artifactId>
    <version>1.1</version>
    <configuration>
        <sourceDirectory>${project.basedir}/src/main/python</sourceDirectory>
    </configuration>
</plugin>
```

#### Output Directory

By default, the `jython-compile` goal outputs compiled files to the project's build output directory (`${project.build.outputDirectory}`) for later inclusion in a JAR file. To change the output directory, configure the `outputDirectory` parameter:

```xml
<plugin>
    <groupId>dev.magicmq</groupId>
    <artifactId>jython-compile-maven-plugin</artifactId>
    <version>1.1</version>
    <configuration>
        <outputDirectory>${project.build.outputDirectory}</outputDirectory>
    </configuration>
</plugin>
```

#### Jython Properties

The `python.cachedir.skip` system property is set to `true` by default, which should be left as-is. To override this property or add additional system properties for the Jython runtime, use the `jythonProperties` parameter:

```xml
<plugin>
    <groupId>dev.magicmq</groupId>
    <artifactId>jython-compile-maven-plugin</artifactId>
    <version>1.1</version>
    <configuration>
        <jythonProperties>
            <jythonProperty>python.cachedir.skip=false</jythonProperty>
        </jythonProperties>
    </configuration>
</plugin>
```

#### Skip

The `jython-compile` goal also includes a `skip` parameter that can be used to skip compilation:

```xml
<plugin>
    <groupId>dev.magicmq</groupId>
    <artifactId>jython-compile-maven-plugin</artifactId>
    <version>1.1</version>
    <configuration>
        <skip>true</skip>
    </configuration>
</plugin>
```

## Building

Building requires [Maven](https://maven.apache.org/) and [Git](https://git-scm.com/). Maven 3+ is recommended for building the project. Follow these steps:

1. Clone the repository: `git clone https://github.com/magicmq/JythonCompiler.git`
2. Enter the repository root: `cd PySpigot`
3. Build with Maven: `mvn clean package`
4. Built files will be located in the `target` directory.

## Issues/Suggestions

Do you have any issues or suggestions? [Submit an issue report.](https://github.com/magicmq/JythonCompiler/issues/new)