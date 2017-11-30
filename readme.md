# Devtool

This is the devtool project.

Devtool is a simple commandline tool to help install tools for primarily developers. Eg maven, git, different jdk's etc.

After installation run devtool to see usage:

    devtool

# How to build and run locally
Devtool is written in groovy and gradle is used for building.
Run this for building and running tests

    gradlew test

# About the code
Curently all the code is actually only in two files. Yeah we know - not very structured and OO'ish
And cleanup could definitely be done. But it works and there is not a lot of code. If much is going to be added
some refactoring is needed.
All the magic happens in:

    Devtool.groovy

It uses the groovy CliBuilder to parse the params from the command line and to do formatting etc

Devtool fetches all the tools from a "file repository". Currently there is only one provides which is 
nexus.

The local part is setup and handled using the windows command `setx` that enables the updating of the users
local path. That is how devtool can add stuff to the local user path.
It is individual for each tool whether anything is added to the user path. This is handled via
xml files for each individual tool that are also placed in the file repository.

Updating and changing of settings for the tools is currently a manual process as not many tools require this.

Devtool has a build in update notifier when a new version is available. It simply checks for a new version with every run of devtool.
And offcourse devtool is using itself to both install and update devtool.