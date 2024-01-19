# Precisely Data Experience Java SDK Sample App

The Precisely Data Experience Java SDK Sample App is a sample program that shows how to consume the PDX Java-SDK.

This application is not intended for production use.

## System Requirements
The following items are required to build and run the sample application.
1. Java JDK 11.0.14
2. Gradle Build
3. Hadoop (Installation Guide: https://gist.github.com/vorpal56/5e2b67b6be3a827b85ac82a63a5b3b2e)  
      i. Add hadoop.dll file inside %HADOOP_HOME%/bin

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
`-dld "productName#geography#rosterGranularity#dataFormat" -a <apiKey> -s <secret> --download-path <c:\downloads\> --s3-access <s3-access-key> --s3-secret <s3-secret key> --s3-bucket-name <s3-bucket-name> --s3-key-postfix <postfix>`

download referential data 

`-drd "productName#geography#rosterGranularity#dataFormat" -a <apiKey> -s <secret> --download-path <c:\downloads\>  --s3-access <s3-access-key> --d  --suffix  --cli  --dv  --directoryName`

## Architecture

### 1. Download Latest Delivery (DLD)

Download latest deliver command will download the latest vintage precisely has to offer for the requested product list . </br>
#### The arguments are as follows:

1. jar file location  
    Example: `java -jar "C:\DataDownloader\build\libs\data-downloader-3.0.3-all.jar"`  
2. `-dld` : Download latest Delivery  
3. Name of product you want to download (as a string seperated by #)  
    Example: `"productName#geography#roster-granularity#format#saveToS3#convertToParquet"`  
        i. productName : Name of the product. Example: Genealogy Parent-Child Data US  
        ii. geaography : The geographic region or extent of the product. Example: United States  
        iii. roster-granularity : The aggregate at which the data is stored in the file. Example: All USA
        iv. Format : file format in which you want downloaded data. Example: CSV  
        v. (OPTIONAL) savetoS3 : Boolean value if you want to save data in a s3 bucket or not (Default = True). The files on local drive will be deleted once the data has been uploaded to S3 bucket. If the argument given is `False` then data stays on local Drive.    
        vi. (OPTIONAL) convertToParquet : Boolean Value if you want to convert the downloaded files to parquet.  
4. `-a`: Automatic Downloader API Key  
5. `-s` : Automatic Downloader Shared Secret Key  
6. `--download-path` : location in local drive where you want to download data  
7. `--s3-access` : S3 access key  
8. `--s3-secret` : S3 secret key  
9. `--s3-bucket-name` : S3 bucket name  
10. `--s3-key-postfix` : sub bucket directory</br>

#### The below diagram shows the working of DLD:

![DLD_working_Final](https://user-images.githubusercontent.com/30530766/162287652-bfaebc7d-0343-4ca4-bc33-7c572a090455.jpg)


### 2. Download Referential Data (DRD)

Download latest deliver list command will download the latest vintage precisely has to offer for the requested product list and also extract the data. </br>
#### The arguments are as follows:

1. jar file location  
   Example: `java -jar "C:\DataDownloader\build\libs\data-downloader-3.0.3-all.jar"`
2. `--drd` : Download Referential Data
3. Name of product you want to download (as a string seperated by #)  
   Example: `"productName#geography#roster-granularity#format"`  
   i. productName : Name of the product. Example: Genealogy Parent-Child Data US  
   ii. geaography : The geographic region or extent of the product. Example: United States  
   iii. roster-granularity : The aggregate at which the data is stored in the file. Example: All USA
   iv. Format : file format in which you want downloaded data. Example: CSV

    Multiple products you want to download( as a string seperated by ,)    
   Example: `"productName1#geography1#roster-granularity1#format1,productName2#geography2#roster-granularity2#format2"`

4. `-a`: Automatic Downloader API Key
5. `-s` : Automatic Downloader Shared Secret Key
6. `-d` : location in local drive where you want to download data
7. `-suffix` : suffix for the download directory
8. `-cli` : path to the cli directory
9. `-dv` : vintage you want to download for the products
10. `-directoryName` : name of the directory where you want to download the products

#### The below diagram shows the working of DLDL:

![dldl](https://github.com/PreciselyCloudNative/AutomaticDataDownloader/assets/86220719/da4f04be-1886-453b-8f7d-64701c443c86)

