# Precisely Data Experience Java SDK Sample App

The Precisely Data Experience Java SDK Sample App is a sample program that shows how to consume the PDX Java-SDK.

This application is not intended for production use.

## System Requirements
The following items are required to build and run the sample application.
1. Java JDK 1.8
2. Maven build tool

## Build and Execute

To build and run the sample app, perform the following steps:
- Install the precisely-pdx-sdk-<version>-full.jar in the local maven repository, using the below command

`mvn install:install-file -Dfile="/sdk/precisely-pdx-sdk-${BUILD_VERSION}-full.jar" \
-DgroupId="com.precisely.pdx" \
-DartifactId="precisely-pdx-sdk" \
-Dversion="${BUILD_VERSION}" \
-Dpackaging="jar"`

Example: 

`mvn install:install-file -Dfile="D:\Precisely_Data_Experience_SDK-Java-v3.0.0\precisely-pdx-sdk-3.0.0-full.jar" 
-DgroupId="com.precisely.pdx" -DartifactId="precisely-pdx-sdk" -Dversion="3.0.0" -Dpackaging="jar"`

- Build the sample application jar using the following command

    `mvn clean install`
- change directories (cd) to the folder `{project.basedir}/target`
- Execute commands like:

   `java -jar pdx-sdk-sample-commandline-<version>-full.jar <command and options>`

Example: 

`java -jar pdx-sdk-sample-commandline-3.0.0-full.jar -lp -a dummyAPIkey123 -s dummyAPISecret123`

## Commands and Options

list products

`-lp -a <apiKey> -s <secret>`

list products with proxy configuration information without username and password

`-lp -a <apiKey> -s <secret> -c<https://myproxyServer.com:8080>`

list products with proxy configuration information having username and password

`-lp -a <apiKey> -s <secret> -c<https://myproxyServer.com:8080> -u <username> -p <password>`

list deliveries

`-ld "productName#geography#rosterGranularity#minReleaseDate" -a <apiKey> -s <secret>`

list deliveries with proxy configuration information without username and password

`-ld "productName#geography#rosterGranularity#minReleaseDate" -a <apiKey> -s <secret> -c<https://myproxyServer.com:8080>`

list deliveries with proxy configuration information having username and password

`-ld "productName#geography#rosterGranularity#minReleaseDate" -a <apiKey> -s <secret> -c<https://myproxyServer.com:8080> -u <username> -p <password>`

list latest deliveries

`-lld "productName#geography#rosterGranularity#userPreference" -a <apiKey> -s <secret>`

list the latest deliveries with proxy configuration information without username and password

`-lld "productName#geography#rosterGranularity#userPreference" -a <apiKey> -s <secret> -c<https://myproxyServer.com:8080>`

list the latest deliveries with proxy configuration information having username and password

`-lld "productName#geography#rosterGranularity#userPreference" -a <apiKey> -s <secret> -c<https://myproxyServer.com:8080> -u <username> -p <password>`

download delivery

`-dd "productName#geography#rosterGranularity#dataFormat#version#vintage#minReleaseDate" -a <apiKey> -s <secret>`

download delivery with proxy configuration information without username and password

`-dd "productName#geography#rosterGranularity#dataFormat#version#vintage#minReleaseDate" -a <apiKey> -s <secret> -c<https://myproxyServer.com:8080>`

download delivery with proxy configuration information having username and password

`-dd "productName#geography#rosterGranularity#dataFormat#version#vintage#minReleaseDate" -a <apiKey> -s <secret> -c<https://myproxyServer.com:8080> -u <username> -p <password>`

download latest delivery  
`-ddl "productName#geography#rosterGranularity#dataFormat" -a <apiKey> -s <secret> --download-path <c:\downloads\> --s3-access <s3-access-key> --s3-secret <s3-secret key> --s3-bucket-name <s3-bucket-name> --s3-key-postfix <postfix>`