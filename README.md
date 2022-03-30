# Precisely Data Experience Java SDK Sample App

The Precisely Data Experience Java SDK Sample App is a sample program that shows how to consume the PDX Java-SDK.

This application is not intended for production use.

## System Requirements
The following items are required to build and run the sample application.
1. Java JDK 11.0.14
2. Gradle Build
3. Hadoop (Installation Guide: https://gist.github.com/vorpal56/5e2b67b6be3a827b85ac82a63a5b3b2e)
    a. Add hadoop.dll file inside %HADOOP_HOME%/bin

## Build and Execute

To build and run the sample app, perform the following steps:
- Install the precisely-pdx-sdk-<version>-full.jar in `{project.basedir}/lib` from data.precisely.com

Example: 

- Build the sample application jar using the following command

    `gradlew clean build`
- change directories (cd) to the folder `{project.basedir}/build/libs`
- Execute commands like:

   `java -jar data-downloader-<version>-full.jar <command and options>`

Example: 

`java -jar data-downloader-<version>-full.jar -lp -a dummyAPIkey123 -s dummyAPISecret123`

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

download latest delivery</br>
`-dld "productName#geography#roster-granularity#format" -a <apiKey> -s <secret> c<https://myproxyServer.com:8080> --download-path <c:\downloads\> --s3-access <s3-access-key> --s3-secret <s3-secret key> --s3-bucket-name <s3-bucket-name> --s3-key-postfix <postfix>`

download latest delivery with optional arguments
saveToS3 (boolean) - Gives users an option to save the downloaded data to S3 and delete from local drive
convertToParquet (boolean) - Decompress all zip files from downloaded data and converts the files to Parquet
`-dld "productName#geography#roster-granularity#format#saveToS3#convertToParquet" -a <apiKey> -s <secret> -c<https://myproxyServer.com:8080> --download-path <c:\downloads\> --s3-access <s3-access-key> --s3-secret <s3-secret key> --s3-bucket-name <s3-bucket-name> --s3-key-postfix <postfix>`