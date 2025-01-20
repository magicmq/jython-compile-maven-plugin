[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/dev.magicmq/jython-compiler?nexusVersion=3&server=https%3A%2F%2Frepo.magicmq.dev&label=Latest%20Release)](https://repo.magicmq.dev/#browse/browse:maven-releases:dev%2Fmagicmq%2Fjython-compiler)
[![Latest Snapshot](https://img.shields.io/badge/dynamic/xml?color=orange&label=Latest%20Snapshot&query=%2F%2Fmetadata%2Fversioning%2Fversions%2Fversion%5Blast()%5D&url=https%3A%2F%2Frepo.magicmq.dev%2Frepository%2Fmaven-snapshots%2Fdev%2Fmagicmq%2Fjython-compiler%2Fmaven-metadata.xml)](https://repo.magicmq.dev/#browse/browse:maven-snapshots:dev%2Fmagicmq%2Fpyspigot)
![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/magicmq/JythonCompiler/maven.yml?branch=master)
![Apache 2.0 License](https://img.shields.io/github/license/magicmq/JythonCompiler)

# JythonCompiler

JythonCompiler is a simple Java application that compiles Jython `.py` modules/packages into Java/Jython bytecode. This is meant to speed up usage of said modules at runtime when `.py` modules are distributed.

This project is utilized when building [PySpigot](https://github.com/magicmq/pyspigot), as PySpigot includes some Jython modules in the `/Lib` folder of the distributed JAR file.

A standalone version of Jython is included in the JAR, so there is no need to install Jython separately.

## Usage

1. Download the latest release. Alternatively, you may build the project yourself. See the [building](#building) section below for details.
2. Run the JAR file with `java -jar jython-compiler-<version>.jar <directory>`, where `<directory`> is the root directory containing the Jython modules/packages that you want to compile.
3. The application should do the rest.


## Building

Building requires [Maven](https://maven.apache.org/) and [Git](https://git-scm.com/). Maven 3+ is recommended for building the project. Follow these steps:

1. Clone the repository: `git clone https://github.com/magicmq/JythonCompiler.git`
2. Enter the repository root: `cd PySpigot`
3. Build with Maven: `mvn clean package`
4. Built files will be located in the `target` directory.

## Issues/Suggestions

Do you have any issues or suggestions? [Submit an issue report.](https://github.com/magicmq/JythonCompiler/issues/new)