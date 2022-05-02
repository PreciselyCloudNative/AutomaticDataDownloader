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

download latest delivery  
`-ddl "productName#geography#rosterGranularity#dataFormat" -a <apiKey> -s <secret> --download-path <c:\downloads\> --s3-access <s3-access-key> --s3-secret <s3-secret key> --s3-bucket-name <s3-bucket-name> --s3-key-postfix <postfix>`

## Architecture

### 1. Download Latest Delivery (DLD)

Download latest deliver command will download the latest vintage precisely has to offer for the requested product. </br>
The arguments are as follows:

1. jar file location  
    Eg. `java -jar "C:\DataDownloader\build\libs\data-downloader-3.0.3-all.jar"`  
2. `-dld` : Download latest Delivery  
3. Name of product you want to download (as a string seperated by #)  
    Eg. `"productName#geography#roster-granularity#format#saveToS3#convertToParquet"`  
        3a. productName : Name of the product Eg. Genealogy Parent-Child Data US  
        3b. geaography : Name of the region Eg. United States  
        3c. roster-granularity :   
        3d. Format : file format in which you want downloaded data Eg. CSV  
        3e. (OPTIONAL) savetoS3 : Boolean value if you want to save data in a s3 bucket or not (Default = True). The files on local drive will be deleted once the data has been uploaded to S3 bucket. If the argument given is `False` then data stays on local Drive.    
        3f. (OPTIONAL) convertToParquet : Boolean Value if you want to convert file to parquet.  
4. `-a`: API Key  
5. `-s` : Secret Key  
6. `--download-path` : location in local drive where you want to download data  
7. `--s3-access` : S3 access key  
8. `--s3-secret` : S3 secret key  
9. `--s3-bucket-name` : S3 bucket name  
10. `--s3-key-postfix` : sub bucket directory</br>
