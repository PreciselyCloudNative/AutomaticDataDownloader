package com.precisely.pdx.sdmTos3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.precisely.pdx.sdk.DataDeliveryClient;
import com.precisely.pdx.sdk.exceptions.DataDeliveryClientException;
import com.precisely.pdx.sdk.models.DataDeliveriesSearchResult;
import com.precisely.pdx.sdk.models.LicenseInfo;
import com.precisely.pdx.sdk.models.ProductSubscription;
import com.precisely.pdx.sdk.models.ProxyConnectionInfo;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.util.HashMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

import static com.precisely.pdx.sdmTos3.ExtractUtils.extractDelivery;

/**
 * A simple application that demonstrates the usage of the SDK.
 */
public class Application {

    private static final File downloadDirectory = Paths.get(System.getProperty("user.home"), "Downloads").toFile();
    private static final String APP_ID = "PDX_DEMO_APP_" + VersionUtility.getVersionInfo();
    static ArrayList<File> table = new ArrayList<>();
    private static String apiHostName;


    /**
     * The main method
     *
     * @param commandLineArguments The command line argument passed to the main method
     */
    public static void main(final String[] commandLineArguments) {
        try {
            final CommandLineParser parser = new DefaultParser();
            final Options options = ApacheCLIUtility.constructOptions();
            final CommandLine commandLine = parser.parse(options, commandLineArguments);
            BasicAWSCredentials credentials = null;
            String command = null;
            String value = null;
            String apiKey = null;
            String secret = null;
            String downloadPath = null;
            String directoryName = null;
            String bucketName = null;
            String keyPostfix = null;
            String suffix = null;
            String clipath = null;
            String datavintage = null;
            String hasHeader = "true";
            if (commandLine.hasOption("v")) {
                System.out.println("The version of the SDK Sample app is " + VersionUtility.getVersionInfo());
                System.exit(0);
            }
            if (commandLine.hasOption("h")) {
                ApacheCLIUtility.displayInformation();
                System.exit(0);
            }
            if (!commandLine.hasOption('a')) {
                ApacheCLIUtility.displayInformation();
                System.exit(0);
            }
            if (!commandLine.hasOption('s')) {
                ApacheCLIUtility.displayInformation();
                System.exit(0);
            }
            if (commandLine.hasOption("ah")) {
                String hostName = commandLine.getOptionValue("ah");
                if (hostName != null && !hostName.trim().isEmpty()) {
                    apiHostName = hostName;
                }
            }
            if ((commandLine.hasOption("lp") && !commandLine.hasOption("ld") && !commandLine.hasOption("lld")
                    && !commandLine.hasOption("dd") && !commandLine.hasOption("dld") && !commandLine.hasOption("dldl"))
                    || (!commandLine.hasOption("lp") && commandLine.hasOption("ld") && !commandLine.hasOption("lld")
                    && !commandLine.hasOption("dd") && !commandLine.hasOption("dld") && !commandLine.hasOption("dldl"))
                    || (!commandLine.hasOption("lp") && !commandLine.hasOption("ld") && commandLine.hasOption("lld")
                    && !commandLine.hasOption("dd") && !commandLine.hasOption("dld") && !commandLine.hasOption("dldl"))
                    || (!commandLine.hasOption("lp") && !commandLine.hasOption("ld") && !commandLine.hasOption("lld")
                    && commandLine.hasOption("dd") && !commandLine.hasOption("dld") && !commandLine.hasOption("dldl"))
                    || (!commandLine.hasOption("lp") && !commandLine.hasOption("ld") && !commandLine.hasOption("lld")
                    && !commandLine.hasOption("dd") && commandLine.hasOption("dld") && !commandLine.hasOption("dldl"))
                    || (!commandLine.hasOption("lp") && !commandLine.hasOption("ld") && !commandLine.hasOption("lld")
                    && !commandLine.hasOption("dd") && !commandLine.hasOption("dld") && commandLine.hasOption("dldl"))) {
                if (commandLine.hasOption("a") && commandLine.hasOption("s")) {
                    apiKey = commandLine.getOptionValue("a");
                    secret = commandLine.getOptionValue("s");
                    if (commandLine.hasOption("lp")) {
                        command = "lp";
                    } else if (commandLine.hasOption("ld")) {
                        command = "ld";
                        value = commandLine.getOptionValue("ld");
                    } else if (commandLine.hasOption("lld")) {
                        command = "lld";
                        value = commandLine.getOptionValue("lld");
                    } else if (commandLine.hasOption("dld")) {
                        command = "dld";
                        value = commandLine.getOptionValue("dld");
                        if (commandLine.hasOption("s3a") && commandLine.hasOption("s3s")
                                && commandLine.hasOption("s3bucket") && commandLine.hasOption("s3post")) {
                            credentials = new BasicAWSCredentials(commandLine.getOptionValue("s3a"),
                                    commandLine.getOptionValue("s3s"));
                            bucketName = commandLine.getOptionValue("s3bucket");
                            keyPostfix = commandLine.getOptionValue("s3post");
                        } else {
                            System.out.println("-s3a, -s3s ,s3bucket, and s3post options are required for dld options");
                            System.exit(0);
                        }
                        if (commandLine.hasOption("d")) {
                            downloadPath = commandLine.getOptionValue("d");
                        } else {
                            downloadPath = downloadDirectory.getAbsolutePath();
                        }
                        if (commandLine.hasOption("hh")) {
                            hasHeader = commandLine.getOptionValue("hh");
                        }
                    } else if (commandLine.hasOption("dldl")) {
                        command = "dldl";
                        value = commandLine.getOptionValue("dldl");

                        if (commandLine.hasOption("suffix")) {
                            suffix = commandLine.getOptionValue("suffix");
                        }

                        if (commandLine.hasOption("directoryName")) {
                            directoryName = commandLine.getOptionValue("directoryName");
                        }

                        if (commandLine.hasOption("dv")) {
                            datavintage = commandLine.getOptionValue("dv");
                        }

                        if (commandLine.hasOption("d")) {
                            downloadPath = commandLine.getOptionValue("d");
                        } else {
                            downloadPath = downloadDirectory.getAbsolutePath();
                        }
                        if (commandLine.hasOption("cli")) {
                            clipath = commandLine.getOptionValue("cli");
                        }
                        if (commandLine.hasOption("hh")) {
                            hasHeader = commandLine.getOptionValue("hh");
                        }
                    }
                    else {
                        command = "dd";
                        value = commandLine.getOptionValue("dd");
                        if (commandLine.hasOption("d")) {
                            downloadPath = commandLine.getOptionValue("d");
                        } else {
                            downloadPath = downloadDirectory.getAbsolutePath();

                        }
                    }
                } else {
                    System.out.println("-a and -s options are required for lp or ld or lld or dd options");
                    System.exit(0);
                }

            } else {
                ApacheCLIUtility.displayInformation();
                System.exit(0);
            }
            if (!commandLine.hasOption("a") || !commandLine.hasOption("s")) {
                System.out.println("-a and -s options are required for lp or ld or lld or dd options");
                System.exit(0);
            }

//            if (commandLine.hasOption("lp")) {
//                command = "lp";
//            } else if (commandLine.hasOption("ld")) {
//                command = "ld";
//                value = commandLine.getOptionValue("ld");
//            } else if (commandLine.hasOption("lld")) {
//                command = "lld";
//                value = commandLine.getOptionValue("lld");
//            } else {
//                command = "dd";
//                value = commandLine.getOptionValue("dd");
//                if (commandLine.hasOption("d")) {
//                    downloadPath = commandLine.getOptionValue("d");
//                } else {
//                    downloadPath = downloadDirectory.getAbsolutePath();
//                }
//            }
            ProxyConnectionInfo proxyInfo = getProxyConfigurations(commandLine);
            Application app = new Application();
            app.run(command, value, apiKey, secret, downloadPath, proxyInfo, credentials, bucketName, keyPostfix, suffix, directoryName, clipath, datavintage, hasHeader);
        } catch (Exception ex) {
            System.out.println(ex);
            ApacheCLIUtility.displayInformation();
            ApacheCLIUtility.displayProvidedCommandLineArguments(commandLineArguments, System.out);
        }
    }

    private static ProxyConnectionInfo getProxyConfigurations(CommandLine commandLine) {
        URL url = null;
        String userName = null;
        String password = null;
        if (commandLine.hasOption('c')) {
            String urlString = commandLine.getOptionValue("c");
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                System.out.println(
                        "Not a valid URL. Provide proxy url with port number to connect with proxy server. Example: https://www.proxyserver.com:3000. If proxy server is not running on specific port then url would be like https://www.proxyserver.com");
                System.exit(0);
            }
        }
        if (commandLine.hasOption('u')) {
            userName = commandLine.getOptionValue("u");
        }
        if (commandLine.hasOption('p')) {
            password = commandLine.getOptionValue("p");
        }
        if (null != url) {
            return new ProxyConnectionInfo(userName, password, url);
        }
        return null;
    }

    /**
     * Method that invokes the command passed by the user with command options
     *
     * @param command      The option passed by the user to run
     * @param value        The option value passed by the user
     * @param apiKey       The API Key
     * @param secret       The API Secret
     * @param downloadPath The folder path where the download will happen
     */
    private void run(
            final String command, final String value, final String apiKey, final String secret,
            String downloadPath, ProxyConnectionInfo proxyInfo, BasicAWSCredentials credentials,
            String bucketName, String keyPostfix, String suffix, String directoryName, String clipath, String datavintage, String hasHeader) throws Exception {
        // determine the method to execute
        switch (command) {
            case "lp":
                listProducts(apiKey, secret, proxyInfo);
                break;
            case "ld":
                listDeliveries(value, apiKey, secret, proxyInfo);
                break;
            case "lld":
                listLatestDeliveries(value, apiKey, secret, proxyInfo);
                break;
            case "dd":
                download(value, apiKey, secret, downloadPath, proxyInfo);
                break;
            case "dldl":
                downloadLatestDeliveryList(value, apiKey, secret, proxyInfo, downloadPath, directoryName, suffix, clipath, datavintage,hasHeader);
                break;
            case "dld":
                downloadLatest(value, apiKey, secret, proxyInfo, downloadPath, credentials, bucketName, keyPostfix, hasHeader);

                System.exit(0);
                break;
        }
    }

    /**
     * Sample code to get product information for all the products the user is
     * subscribed too
     *
     * @param apiKey The API Key
     * @param secret The API Secret
     */
    private void listProducts(final String apiKey, final String secret, final ProxyConnectionInfo proxyInfo)
            throws DataDeliveryClientException {
        DataDeliveryClient client = createDataDeliveryClient(apiKey, secret, proxyInfo);
        // get the list of product subscriptions
        final List<ProductSubscription> productSubscriptions = client.getAvailableProducts();

        for (ProductSubscription productSubscription : productSubscriptions) {
            LicenseInfo licenseInfo = productSubscription.getLicenseInfo();
            String message = "ProductSubscription [productName=" + productSubscription.getProductName()
                    + ", geography=" + productSubscription.getGeography() +
                    ", rosterGranularity=" + productSubscription.getRosterGranularity() +
                    ", updateFrequency=" + productSubscription.getUpdateFrequency();
            if (licenseInfo != null) {
                message += ", licenseInfo=[startDate=" + licenseInfo.getStartDate() +
                        ",endDate=" + (licenseInfo.getEndDate() != null ? licenseInfo.getEndDate() : "") +
                        ", updatesAllowed=" + licenseInfo.isUpdatesAllowed() + "]";
            }
            message += "]";
            System.out.println(message);
        }

    }

    /**
     * Sample code to get product additional information from the first page from
     * the paginated list for the specified product including delivery info /
     * download URLs
     *
     * @param productInfo The value of the ld option parameter passed
     * @param apiKey      The API Key
     * @param secret      The API Secret
     */
    private void listDeliveries(String productInfo,
                                final String apiKey,
                                final String secret,
                                ProxyConnectionInfo proxyInfo) throws Exception {
        final String[] pieces = productInfo.split("#");
        if (pieces.length != 3 && pieces.length != 4) {
            System.out.println("The argument value provided  for ld should be proper.");
            System.out.println(
                    "The ld option takes an argument which is composed of productName, geography and roster granularity and an optional min release date separated by #.The date has to be in the format yyyy-MM-dd.");
            System.exit(0);
        }
        final String productName = pieces[0];
        final String geography = pieces[1];
        final String rosterGranularity = pieces[2];
        DataDeliveriesSearchResult dataDeliveriesSearchResult;
        DataDeliveryClient client = createDataDeliveryClient(apiKey, secret, proxyInfo);
            // get the product info including delivery information available for download
            int pageNumber = 1;
            if (pieces.length == 3) {
                dataDeliveriesSearchResult = client.getDeliveries(productName, pageNumber, rosterGranularity, geography);
            } else {
                Temporal minReleaseDate = LocalDate.parse(pieces[3]);
                dataDeliveriesSearchResult = client.getDeliveries(productName, pageNumber, rosterGranularity, geography, minReleaseDate);
            }

        writeToConsole(dataDeliveriesSearchResult);
    }

    /**
     * Sample code to get the latest product additional information from the first page
     * from the paginated list for the specified product including delivery info /
     * download URLs
     *
     * @param productInfo The value of the lld option parameter passed
     * @param apiKey      The API Key
     * @param secret      The API Secret
     */
    private void listLatestDeliveries(String productInfo,
                                      final String apiKey,
                                      final String secret,
                                      ProxyConnectionInfo proxyInfo) throws Exception {
        final String[] pieces = productInfo.split("#");
        if (pieces.length != 4) {
            System.out.println("The argument value provided  for lld should be proper.");
            System.out.println(
                    "The lld option takes an argument which is composed of productName, geography and roster granularity and userPreference separated by #.");
            System.exit(0);
        }
        int pageNumber = 1;
        final String productName = pieces[0];
        final String geography = pieces[1];
        final String rosterGranularity = pieces[2];
        boolean userPreference = "true".equalsIgnoreCase(pieces[3]);
        DataDeliveriesSearchResult dataDeliveriesSearchResult;
        DataDeliveryClient client = createDataDeliveryClient(apiKey, secret, proxyInfo);
            // get the product info including the latest delivery information available for download
            dataDeliveriesSearchResult = client.getLatestDeliveries(productName, pageNumber, rosterGranularity, geography, userPreference);

        writeToConsole(dataDeliveriesSearchResult);
    }
    /**
     * Sample code to download all the product files from the first page from the
     * paginated list of files available for the product
     *
     * @param productInfo  The value of the dd option parameter passed
     * @param apiKey       The API Key
     * @param secret       The API Secret
     * @param downloadPath The folder path where the download will happen
     */
    private void downloadLatest(String productInfo, final String apiKey, final String secret,
                                ProxyConnectionInfo proxyInfo, final String downloadPath,
                                BasicAWSCredentials credentials, final String bucketName,
                                final String keyPostfix, String hasHeader) throws DataDeliveryClientException, IOException, ArchiveException {
        final List<String> deliveryURLs = new ArrayList<>();
        final List<String> vintage = new ArrayList<>();
        final String[] pieces = productInfo.split("#");
        int pageNumber = 1;
        String productName = null;
        String geography = null;
        String rosterGranularity = null;
        boolean convert = true;
        boolean saveToS3 = true;
        final String format;
        if (pieces.length == 4) {
            productName = pieces[0];
            geography = pieces[1];
            rosterGranularity = pieces[2];
            format = pieces[3];
        }
        else if (pieces.length == 5) {
            productName = pieces[0];
            geography = pieces[1];
            rosterGranularity = pieces[2];
            format = pieces[3];
            saveToS3 = parseBoolean(pieces[4]);
        }
        else if (pieces.length == 6) {
            productName = pieces[0];
            geography = pieces[1];
            rosterGranularity = pieces[2];
            format = pieces[3];
            saveToS3 = parseBoolean(pieces[4]);
            convert = parseBoolean(pieces[5]);
        }
        else {
            format = null;
            System.out.println("The argument value provided  for lld should be proper.");
            System.out.println(
                    "The lld option takes an argument which is composed of productName, geography and roster granularity and userPreference separated by #.");
            System.exit(0);
        }
        DataDeliveriesSearchResult dataDeliveriesSearchResult;
        DataDeliveryClient client = null;
        if (null != proxyInfo) {
            client = new DataDeliveryClient(apiKey, secret, APP_ID, proxyInfo.getProxyUrl(), proxyInfo.getUserName(),
                    proxyInfo.getPassword());
        } else {
            client = new DataDeliveryClient(apiKey, secret, APP_ID);
        }

        // get the Latest Product info including delivery information available for download
        dataDeliveriesSearchResult = client.getLatestDeliveries(productName, pageNumber, rosterGranularity, geography);
        if (dataDeliveriesSearchResult.getTotalDeliveries() > 0) {
            dataDeliveriesSearchResult.getDeliveries().forEach(
                    deliveryInfo -> {
                        if (deliveryInfo.getDataFormat().equalsIgnoreCase(format)) {
                            if (deliveryInfo.getDownloadUrl() != null && !"".equalsIgnoreCase(deliveryInfo.getDownloadUrl()))
                                deliveryURLs.add(deliveryInfo.getDownloadUrl());
                            vintage.add(deliveryInfo.getVintage());
//                            vintage[0] = deliveryInfo.getVintage();
                        }
                    }
            );
        }
        else {
            System.out.println("Product Latest Info is not available");
            System.exit(0);
        }
        String productDirectory = productName.replace("_", "-").replace(" ", "-").replace("&", "-and-").replace(":", "-").replace("--", "-").toUpperCase();
        String urlPostfix = keyPostfix + productDirectory + "/" + vintage.get(0);
        if (format.equalsIgnoreCase("Spectrum Platform Data") || format.equalsIgnoreCase("Geocoding")
                || format.equalsIgnoreCase("Interactive")) {
            productDirectory = "reference-data";
            urlPostfix = keyPostfix + productDirectory + "/" + vintage.get(0) + "/" + deliveryURLs.get(0).replaceAll(".*/(.+)\\?.*", "$1");
        }
        int fileIndex = 1;
        if (deliveryURLs.size() > 0) {
//             download the files
            for (final String downloadUrl : deliveryURLs) {
                final String fileName = downloadUrl.replaceAll(".*/(.+)\\?.*", "$1");
                if (checkDir(bucketName, urlPostfix, credentials)) {
                    System.out.println(String.format("Downloading file %d of %d %s to %s", fileIndex, deliveryURLs.size(),
                            fileName, downloadPath));
                    //creating sub directories

                    File directory_temp1 = Paths.get(downloadPath, productDirectory).toFile();
                    if (!directory_temp1.exists()) {
                        directory_temp1.mkdir();
                    }
                    File directory_temp2 = Paths.get(directory_temp1.getPath(), vintage.get(0)).toFile();
                    if (!directory_temp2.exists()) {
                        directory_temp2.mkdir();
                    }
                    File directory_temp3 = Paths.get(directory_temp2.getPath(), fileName).toFile();
                    System.out.println(directory_temp1);
                    // create the file output stream.......Add checkdir checkpoint here
                    try (final FileOutputStream fileOutput = new FileOutputStream(directory_temp3)) {
                        // call the SDK to download the file
                        client.downloadProductFile(downloadUrl, fileOutput, (url, totalBytes, bytesDownloaded) -> {
                            System.out.println(String.format("Progress for %s: %s%%", fileName,
                                    Math.round((float) bytesDownloaded / totalBytes * 100)));
                            return true;
                        }, (url, totalBytes, bytesDownloaded) -> {
                            while (bytesDownloaded != totalBytes) {
                                continue;
                            }
                            if (totalBytes == bytesDownloaded) {
                                System.out.println("Complete Callback is done.");
                                return true;
                            }
                            return false;
                        });

                        System.out.println(String.format("Progress for %s: %s%%", fileName, "100"));
                    } catch (final Exception ex) {
                        System.out.println("An error occurred saving the file.");
                    }

                    fileIndex++;

                } else {
                    System.out.println("Data set already exists");
                    System.exit(0);
                }
            }

            boolean itr = true;
            decompressAndConvert(Paths.get(downloadPath).toFile(), itr, convert, hasHeader);
            if (table.size() != 0) {
                for (File t : table) {
                    itr = false;
                    decompressAndConvert(t, itr, convert, hasHeader);
                }
            }
            System.out.println("All downloads complete");
            if(saveToS3) {
                createBucket(bucketName, credentials); //fix arguments
                uploadDir(downloadPath, bucketName, keyPostfix, true, credentials);
            }
        } else {
            System.out.println("No delivery is found with provided arguments.");
        }
        if(saveToS3) {
            File[] files = Paths.get(downloadPath).toFile().listFiles();
            for (File f : files) {
                FileUtils.deleteDirectory(f);
            }
        }
    }

    private void downloadLatestDeliveryList(String productInfo, final String apiKey, final String secret,
                                            ProxyConnectionInfo proxyInfo, final String downloadPath, String directoryName, String suffix, String clipath,
                                            String datavintage, String hasHeader) throws DataDeliveryClientException, IOException, ArchiveException {


        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("Geocoding Residential Delivery Indicator","RDI");
        datamap.put("Geocoding LACSLink Database","LACSLink");

        final String[] products = productInfo.split(",");
        final String dataVintage = datavintage;
        System.out.println("name :::"+ directoryName);

        for (int i = 0; i < products.length; i++) {

            final List<String> deliveryURLs = new ArrayList<>();
            final List<String> vintage = new ArrayList<>();
            final String[] pieces = products[i].split("#");
            int pageNumber = 1;
            String productName = null;
            String geography = null;
            String rosterGranularity = null;
            boolean convert = true;
            boolean saveToS3 = true;
            final String format;
            if (pieces.length == 4) {
                productName = pieces[0];
                geography = pieces[1];
                rosterGranularity = pieces[2];
                format = pieces[3];
            } else if (pieces.length == 5) {
                productName = pieces[0];
                geography = pieces[1];
                rosterGranularity = pieces[2];
                format = pieces[3];
                saveToS3 = parseBoolean(pieces[4]);
            } else if (pieces.length == 6) {
                productName = pieces[0];
                geography = pieces[1];
                rosterGranularity = pieces[2];
                format = pieces[3];
                saveToS3 = parseBoolean(pieces[4]);
                convert = parseBoolean(pieces[5]);
            } else {
                format = null;
                System.out.println("The argument value provided  for lld should be proper.");
                System.out.println(
                        "The lld option takes an argument which is composed of productName, geography and roster granularity and userPreference separated by #.");
                System.exit(0);
            }
            DataDeliveriesSearchResult dataDeliveriesSearchResult;
            DataDeliveryClient client = null;
            if (null != proxyInfo) {
                client = new DataDeliveryClient(apiKey, secret, APP_ID, proxyInfo.getProxyUrl(), proxyInfo.getUserName(),
                        proxyInfo.getPassword());
            } else {
                client = new DataDeliveryClient(apiKey, secret, APP_ID);
            }

            if (format.equalsIgnoreCase("Spectrum Platform Data") || format.equalsIgnoreCase("Geocoding")
                    || format.equalsIgnoreCase("Interactive")) {



                    System.out.println("data downloading");
                    // get the Latest Product info including delivery information available for download
                    dataDeliveriesSearchResult = client.getLatestDeliveries(productName, pageNumber, rosterGranularity, geography);
                    if (dataDeliveriesSearchResult.getTotalDeliveries() > 0) {
                        dataDeliveriesSearchResult.getDeliveries().forEach(
                                deliveryInfo -> {
                                    if (deliveryInfo.getDataFormat().equalsIgnoreCase(format)) {
                                        if (deliveryInfo.getDownloadUrl() != null && !"".equalsIgnoreCase(deliveryInfo.getDownloadUrl()))
                                            deliveryURLs.add(deliveryInfo.getDownloadUrl());
                                        if (datavintage==null ) {
                                            vintage.add(deliveryInfo.getVintage());
                                            System.out.println("main");
                                        }
                                        else{
                                            vintage.add(dataVintage);
                                            System.out.println("not main");
                                        }
                                    }
                                }
                        );
                    } else {
                        System.out.println("Product Latest Info is not available");
                        System.exit(0);
                    }

//                String folder ="";
//                if(suffix==null){
//                    folder = vintage.get(0) ;
//                }
//                else{
//                    folder =vintage.get(0) + "." + suffix;
//                }
//
//                String valueX = datamap.get(productName);
//
//                File folderY = new File(downloadPath + "/" + folder);
//
//                boolean download= true;
//
//                if (folderY.exists()) {
//                    File[] files = folderY.listFiles();
//                    for (File file : files) {
//                        System.out.println("file.getName()"+ file.getName());
//                        if (file.getName().startsWith(valueX)) {
//                            download = false;
//                            break;
//                        }
//                    }
//                }


                boolean download= true;
//                if (download){

                    String productDirectory = "";

                    String urlPostfix = vintage.get(0) + "/" + deliveryURLs.get(0).replaceAll(".*/(.+)\\?.*", "$1");
                    String name="";
                    int fileIndex = 1;
                    if (deliveryURLs.size() > 0) {
                        //             download the files

                        for (final String downloadUrl : deliveryURLs) {
                            final String fileName = downloadUrl.replaceAll(".*/(.+)\\?.*", "$1");

                            String textfilepath = "";
                            String dir_name = vintage.get(0);

                            if (directoryName != null) {
                                dir_name = directoryName;
                            }

                            if (suffix == null) {
                                textfilepath = downloadPath + "/" + dir_name;
                            } else {
                                textfilepath = downloadPath + "/" + dir_name + "." + suffix;
                            }

                            System.out.println("textfile "+ textfilepath);

                            File folderY = new File(textfilepath);
                            if (folderY.exists() && folderY.isDirectory()) {
                                File[] files = folderY.listFiles();
                                for (File file : files) {

                                    if (file.isFile()) {

                                        String fname1 = file.getName();
                                        File filee1 = new File(fname1);
                                        String nameWithoutExtension1 = filee1.getName().substring(0, filee1.getName().lastIndexOf('.'));

                                        String fname2 = fileName;
                                        File filee2 = new File(fname2);
                                        String nameWithoutExtension2 = filee2.getName().substring(0, filee2.getName().lastIndexOf('.'));

                                        if (nameWithoutExtension1.equals(nameWithoutExtension2)) {
                                            download = false;
                                            break;
                                        }
                                    }
                                }
                            }

                            if (download) {
                                name = fileName;
                                System.out.println(String.format("Downloading file %d of %d %s to %s", fileIndex, deliveryURLs.size(),
                                        fileName, downloadPath));
                                //creating sub directories

                                File directory_temp1 = Paths.get(downloadPath, productDirectory).toFile();
                                if (!directory_temp1.exists()) {
                                    directory_temp1.mkdir();
                                }
                                File directory_temp2 = Paths.get(directory_temp1.getPath(), dir_name).toFile();
                                if (!directory_temp2.exists()) {
                                    directory_temp2.mkdir();
                                }
                                File directory_temp3 = Paths.get(directory_temp2.getPath(), fileName).toFile();
                                System.out.println(directory_temp1);
//                        // create the file output stream.......Add checkdir checkpoint here
                                try (final FileOutputStream fileOutput = new FileOutputStream(directory_temp3)) {
//                            // call the SDK to download the file
                                    client.downloadProductFile(downloadUrl, fileOutput, (url, totalBytes, bytesDownloaded) -> {
                                        System.out.println(String.format("Progress for %s: %s%%", fileName,
                                                Math.round((float) bytesDownloaded / totalBytes * 100)));
                                        return true;
                                    }, (url, totalBytes, bytesDownloaded) -> {
                                        while (bytesDownloaded != totalBytes) {
                                            continue;
                                        }
                                        if (totalBytes == bytesDownloaded) {
                                            System.out.println("Complete Callback is done.");
                                            return true;
                                        }
                                        return false;
                                    });

                                    System.out.println(String.format("Progress for %s: %s%%", fileName, "100"));
                                } catch (final Exception ex) {
                                    System.out.println("An error occurred saving the file.");
                                }

                                fileIndex++;
                            } else {
                                System.out.println("Data already exists: " +  productName);
                            }

                        }

                        if (download) {

                            System.out.println("All downloads complete");


                            String dir_name = vintage.get(0);

                            if (directoryName != null) {
                                dir_name = directoryName;
                            }

                            String sourcePath = downloadPath + "/" + dir_name + "/";
                            String destPath = "";

                            if (suffix == null) {
                                destPath = downloadPath + "/" + dir_name;
                            } else {
                                destPath = downloadPath + "/" + dir_name + "." + suffix;
                            }

                            System.out.println("destpath:::::::"+ destPath);


                            String os = System.getProperty("os.name");
                            System.out.println("Operating System: " + os);

                            String command ="";

                            if(os.startsWith("Windows")){
                                command = clipath+ "/cli.cmd extract --s " + sourcePath + " --d " + destPath;
                            }
                            else if (os.startsWith("Linux")) {
                                command = clipath+ "/cli.sh extract --s " + sourcePath + " --d " + destPath;
                            }
//                            String command = "lib/ga-sdk-dist-5.1.164/cli/cli.cmd extract --s " + sourcePath + " --d " + destPath;
                            System.out.println("Extracting ::::"+ destPath);

                            try {
                                Process process = Runtime.getRuntime().exec(command);
                                int exitCode = process.waitFor();
                                if (exitCode == 0) {
                                    System.out.println("Command executed successfully.");
                                } else {
                                    System.out.println("Command failed with exit code " + exitCode);
                                }
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            //                            String folderPath = downloadPath;
                            String folderPath = "";

                            if (suffix == null) {
                                folderPath = downloadPath + "/" + dir_name;
                            } else {
                                folderPath = downloadPath + "/" + dir_name + "." + suffix;
                            }

                            String fileName = name; // replace with your file name
                            File filee = new File(fileName);
                            String nameWithoutExtension = filee.getName().substring(0, filee.getName().lastIndexOf('.'));

                            String fileN = nameWithoutExtension + ".txt"; // replace with desired file name

                            File folder1 = new File(folderPath);
                            File file = new File(folder1, fileN);

                            try {
                                file.createNewFile();
                                System.out.println("File created successfully at location: " + file.getAbsolutePath());
                            } catch (IOException e) {
                                System.out.println("An error occurred while creating the file.");
                                e.printStackTrace();
                            }
                        }
                    }
                    if (download) {
                        System.out.println("Deleting spd file");

                        String dir_name = vintage.get(0);
                        if (directoryName != null) {
                            dir_name = directoryName;
                        }

                        String directory = downloadPath + "/" + dir_name;
                        File[] filess = Paths.get(directory).toFile().listFiles();
                        if (filess != null) {
                            for (File file : filess) {
                                if (file.getName().endsWith(".spd")){
                                    file.delete();
                                }
                            }
                        }
                        Paths.get(directory).toFile().delete();
                    }

//                else{
//                    System.out.println("Data already exists: " + productName);
//                }
            }
            else{
                System.out.println("Incorrect data format: " + format);
            }
        }
    }

    /**
     * Sample code to download all the product files from the first page from the
     * paginated list of files available for the product
     *
     * @param productInfo  The value of the dd option parameter passed
     * @param apiKey       The API Key
     * @param secret       The API Secret
     * @param downloadPath The folder path where the download will happen
     */
    private void download(String productInfo,
                          final String apiKey,
                          final String secret,
                          final String downloadPath,
                          ProxyConnectionInfo proxyInfo) throws Exception {
        final String[] pieces = productInfo.split("#");
        if (pieces.length != 6 && pieces.length != 7) {
            System.out.println("The argument value provided  for dd should be proper.");
            System.out.println(
                    "The dd option takes an argument which is composed of productName, geography and roster granularity,format,version,vintage and an optional min release date separated by #.The date has to be in the format yyyy-MM-dd.");
            System.exit(0);
        }
        final String productName = pieces[0];
        final String geography = pieces[1];
        final String rosterGranularity = pieces[2];
        final String format = pieces[3];
        final String version = pieces[4];
        final String vintage = pieces[5];
        final List<String> deliveryURLs = new ArrayList<>();
        DataDeliveryClient client = createDataDeliveryClient(apiKey, secret, proxyInfo);
            // get the product info including delivery information available for download
            int pageNumber = 1;
            DataDeliveriesSearchResult dataDeliveriesSearchResult;
            if (pieces.length == 6) {
                dataDeliveriesSearchResult = client.getDeliveries(productName, pageNumber, rosterGranularity, geography);
            } else {
                Temporal minReleaseDate = LocalDate.parse(pieces[6]);
                dataDeliveriesSearchResult = client.getDeliveries(productName, pageNumber, rosterGranularity, geography, minReleaseDate);
            }
            if (dataDeliveriesSearchResult.getTotalDeliveries() > 0) {
                dataDeliveriesSearchResult.getDeliveries().forEach(deliveryInfo -> {
                    if (deliveryInfo.getDataFormat().equalsIgnoreCase(format) &&
                            deliveryInfo.getVersion().equals(version) &&
                            deliveryInfo.getVintage().equals(vintage) &&
                            deliveryInfo.getDownloadUrl() != null &&
                            !"".equalsIgnoreCase(deliveryInfo.getDownloadUrl())) {
                        deliveryURLs.add(deliveryInfo.getDownloadUrl());
                    }
                });
            }

            int fileIndex = 1;
            if (deliveryURLs.size() <= 0) {
                System.out.println("No delivery is found with provided arguments.");
                return;
            }
            // download the files
            for (final String downloadUrl : deliveryURLs) {
                final String fileName = downloadUrl.replaceAll(".*/(.+)\\?.*", "$1");

                System.out.printf("Downloading file %d of %d %s to %s%n", fileIndex, deliveryURLs.size(),
                        fileName, downloadPath);

                // create the file output stream
                try (final FileOutputStream fileOutput = new FileOutputStream(Paths.get(downloadPath, fileName).toFile())) {
                    // call the SDK to download the file
                    client.downloadProductFile(downloadUrl, fileOutput, (url, totalBytes, bytesDownloaded) -> {
                        System.out.printf("Progress for %s: %s%%%n", fileName,
                                Math.round((float) bytesDownloaded / totalBytes * 100));
                        return true;
                    }, (url, totalBytes, bytesDownloaded) -> {
                        System.out.println("Complete Callback is done.");
                        return true;
                    });

                    System.out.printf("Progress for %s: %s%%%n", fileName, "100");
                } catch (final Exception ex) {
                    System.out.println("An error occurred saving the file.");
                }

                fileIndex++;
            }

            System.out.println("All downloads complete");


    }

    private void uploadDir(String dir_path, String bucket_name, String key_prefix, boolean recursive, AWSCredentials credentials) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
        TransferManager transfer_mgr = TransferManagerBuilder.standard().withS3Client(s3).build();
        System.out.format("Uploading all files from %s to S3 bucket %s...\n", dir_path, bucket_name);
        try {

            MultipleFileUpload xfer = transfer_mgr.uploadDirectory(bucket_name,
                    key_prefix, new File(dir_path), recursive);
            com.precisely.pdx.sdmTos3.XferMgrProgress.showTransferProgress(xfer);
            com.precisely.pdx.sdmTos3.XferMgrProgress.waitForCompletion(xfer);

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " + "means your request made it "
                    + "to Amazon S3, but was rejected with an error response" + " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
            System.err.println(ase.getErrorMessage());
            System.exit(1);
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " + "means the client encountered "
                    + "an internal error while trying to " + "communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }

    }

    private void createBucket(String bucket_name, AWSCredentials credentials) {
        System.out.format("Checking if Bucket %s already exists...\n", bucket_name);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
        if (s3.doesBucketExistV2(bucket_name)) {
            System.out.format("Bucket %s already exists.\n", bucket_name);

        } else {
            System.out.format("Creating Bucket %s.\n", bucket_name);
            try {
                s3.createBucket(bucket_name);
            } catch (AmazonS3Exception e) {
                System.err.println(e.getErrorMessage());
            }
        }
    }

    private boolean checkDir(String bucket_name, String key_prefix, AWSCredentials credentials) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
        int dirSize = s3.listObjects(bucket_name, key_prefix).getObjectSummaries().size();
        if (dirSize == 0) {
            return true;
        } else {
            return false;
        }

    }

    private void decompressAndConvert(File extractedDelivery, boolean itr_local, boolean convert, String hasHeader) throws IOException, ArchiveException {
        File[] files = extractedDelivery.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                decompressAndConvert(file.getAbsoluteFile(), convert, itr_local, hasHeader);
            }
            String ext = FilenameUtils.getExtension(file.getName()).toLowerCase();
            if (ext.contains("7z") || ext.contains("zip")) {
                extractDelivery(file);
                if(convert){
                    parquet(extractedDelivery, hasHeader);
                }
                if (itr_local) {
                    table.add(new File(file.getParentFile().toString()));
                }
            }
            if (convert & (ext.contains("csv") || ext.contains("txt"))) {
                if (file.getName() != "copyright.txt") {
                    parquet(extractedDelivery, hasHeader);
                }
                else{
                    continue;
                }
            }
        }


    }

    private void parquet(File extractedDelivery, String hasHeader) throws IOException {
        File[] files = extractedDelivery.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                parquet(file, hasHeader);
            }
            String ext = FilenameUtils.getExtension(file.getName());
            if (file.getName().toLowerCase().contains("header".toLowerCase())) {
                System.out.println("File: " + file.getName() + " is a header file in this data set");
                com.precisely.pdx.sdmTos3.ConvertUtils.csvToParquet(file, "true", "false");
            } else if (ext.equalsIgnoreCase("txt") || ext.equalsIgnoreCase("csv")) {
                if (!file.getName().equalsIgnoreCase("copyright.txt")) {
                    com.precisely.pdx.sdmTos3.ConvertUtils.csvToParquet(file, hasHeader, "true");
                    System.out.println(file);
                }
            }
        }
    }

    public static boolean parseBoolean(String b) {
        return "true".equalsIgnoreCase(b) ? true : false;
    }

    private DataDeliveryClient createDataDeliveryClient(String apiKey, String secret, ProxyConnectionInfo proxyInfo) throws DataDeliveryClientException {
        DataDeliveryClient client;
        if (null != proxyInfo) {
            System.out.println("Proxy connection info: " + proxyInfo);
            client = new DataDeliveryClient(apiKey, secret, APP_ID, proxyInfo.getProxyUrl(), proxyInfo.getUserName(),
                    proxyInfo.getPassword());
        } else {
            client = new DataDeliveryClient(apiKey, secret, APP_ID);
        }
        if (apiHostName != null) {
            client.setApiHost(apiHostName);
        }
        return client;
    }

    private void writeToConsole(DataDeliveriesSearchResult dataDeliveriesSearchResult) {
        if (dataDeliveriesSearchResult != null && dataDeliveriesSearchResult.getDeliveries() != null) {

            dataDeliveriesSearchResult.getDeliveries().forEach(deliveryInfo ->
                    System.out.println("DeliveryInfo [version=" + deliveryInfo.getVersion() +
                            ", vintage=" + deliveryInfo.getVintage() +
                            ", actualReleaseDate=" + deliveryInfo.getActualReleaseDate()
                            + ", dataFormat=" + deliveryInfo.getDataFormat() +
                            ", language=" + deliveryInfo.getLanguage() +
                            ", platform=" + deliveryInfo.getPlatform() + ", " +
                            "os=" + deliveryInfo.getOs() +
                            ", downloadUrl=" + deliveryInfo.getDownloadUrl() +
                            ", size=" + deliveryInfo.getSize() + "]"));

        }
    }
}