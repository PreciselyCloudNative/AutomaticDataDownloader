package com.precisely.pdx.sdmTos3;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * A utility class which exposes command line parsing functionality
 */
class ApacheCLIUtility {

    /**
     * Write "help" to the provided OutputStream.
     */
    static void printHelp(final Options options,
                          final int printedRowWidth,
                          final String header,
                          final String footer,
                          final int spacesBeforeOption,
                          final int spacesBeforeOptionDescription,
                          final boolean displayUsage) {
        final String commandLineSyntax = "java -jar pdx-sdk-sample-commandline-" + VersionUtility.getVersionInfo() + "-full.jar ";
        PrintWriter writer = new PrintWriter(System.out);
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(writer, printedRowWidth, commandLineSyntax, header, options, spacesBeforeOption,
                spacesBeforeOptionDescription, footer, displayUsage);
        writer.flush();
    }

    /**
     * Construct and provide Options.
     *
     * @return Options expected from command-line.
     */
    static Options constructOptions() {
        final Options options = new Options();

        Option help = new Option("h", "help", false,
                "Prints the detail help and usage information of this demo utility");
        options.addOption(help);

        Option version = new Option("v", "version", false, "prints the version information of this demo utility");
        options.addOption(version);

        Option listProducts = Option.builder("lp").argName("list-products").required(false).longOpt("list-products")
                .desc("The command to list products").hasArg(false).build();
        options.addOption(listProducts);

        Option listDeliveries = Option.builder("ld")
                .argName("productName#geography#roster-granularity#min-release-date").required(false)
                .longOpt("list-deliveries")
                .desc("The option takes an argument which is composed of productName, geography and roster granularity and an optional min release date separated by #.The date has to be in the format yyyy-MM-dd.")
                .hasArg().build();
        options.addOption(listDeliveries);

        Option listLatestDeliveries = Option.builder("lld")
                .argName("productName#geography#roster-granularity#userPreference").required(false)
                .longOpt("list-latest-deliveries")
                .desc("The option takes an argument which is composed of productName, geography and roster granularity and an userPreference boolean flag separated by #.")
                .hasArg().build();
        options.addOption(listLatestDeliveries);

        Option downloadDelivery = Option.builder("dd")
                .argName("productName#geography#roster-granularity#format#version#vintage#min-release-date")
                .required(false).longOpt("download-delivery")
                .desc("The option takes an argument which is composed of productName, geography and roster granularity,format,version,vintage and an optional min release date separated by #.The date has to be in the format yyyy-MM-dd.")
                .hasArg().build();
        options.addOption(downloadDelivery);

        Option downloadLatestDelivery = Option.builder("dld")
                .argName("productName#geography#roster-granularity#format#saveToS3#convertToParquet")
                .required(false).longOpt("download-latest-delivery")
                .desc("The option takes an argument which is composed of productName and format separated by #.")
                .hasArg().build();
        options.addOption(downloadLatestDelivery);

        Option downloadLatestDeliveryList = Option.builder("dldl")
                .argName("productName#geography#roster-granularity#format")
                .required(false).longOpt("download-latest-delivery-list")
                .desc("The option takes an argument which is composed of productName and format separated by #.")
                .hasArg().build();
        options.addOption(downloadLatestDeliveryList);

        Option downloadPath = Option.builder("d").argName("download-path").required(false).longOpt("download-path")
                .desc("The takes an argument which is folder location to download delivery. If not provided then Downloads folder is used in user's home directory")
                .hasArg().build();
        options.addOption(downloadPath);

        Option apiKey = Option.builder("a").argName("apiKey").longOpt("api-key")
                .desc("Your API key, which can be accessed from the PDX portal.").hasArg().required(false).build();
        options.addOption(apiKey);

        Option secret = Option.builder("s").argName("secret").longOpt("shared-secret").required(false)
                .desc("Your shared secret, which can be accessed from the PDX portal.").hasArg().build();
        options.addOption(secret);

        Option s3access = Option.builder("s3a").argName("s3access").longOpt("s3-access")
                .desc("Your S3 access key, which can be accessed from the AWS account.").hasArg().required(false).build();
        options.addOption(s3access);
        Option s3secret = Option.builder("s3s").argName("s3secret").longOpt("s3-secret")
                .desc("Your S3 secret key, which can be accessed from the AWS account.").hasArg().required(false).build();
        options.addOption(s3secret);

        Option s3BucketName = Option.builder("s3bucket").argName("s3BucketName").longOpt("s3-bucket-name")
                .desc("Your S3 bucket name, which can be accessed from the AWS account.").hasArg().required(false).build();
        options.addOption(s3BucketName);
        Option s3KeyPostfix = Option.builder("s3post").argName("s3KeyPostfix").longOpt("s3-key-postfix")
                .desc("Your S3 post bucket name url, which can be accessed from the AWS account.").hasArg().required(false).build();
        options.addOption(s3KeyPostfix);
        Option hasHeader = Option.builder("hh").argName("hasHeader").longOpt("has-header")
                .desc("Your S3 secret key, which can be accessed from the AWS account.").hasArg().required(false).build();
        options.addOption(hasHeader);

        Option suffix = Option.builder("suffix").argName("suffix").longOpt("suffix")
                .desc("Your suffix for the download directory.").hasArg().required(false).build();
        options.addOption(suffix);

        Option apiHost = Option.builder("ah").argName("apiHost").longOpt("api-host").required(false)
                .desc("The host name for the SDK API. Default - api.precisely.com").hasArg().build();
        options.addOption(apiHost);

        setProxyOptions(options);

        return options;
    }

    private static void setProxyOptions(final Options options) {
        Option proxyUrl = Option.builder("c").argName("proxyUrl").longOpt("proxyUrl").required(false).desc(
                        "Complete proxy url with port number to connect with proxy server. Example: https://www.proxyserver.com:3000")
                .hasArg().build();
        options.addOption(proxyUrl);
        Option proxyUserName = Option.builder("u").argName("userName").longOpt("userName").required(false)
                .desc("Your username to connect with your proxy server.").hasArg().build();
        options.addOption(proxyUserName);

        Option proxyPassword = Option.builder("p").argName("password").longOpt("proxy-password").required(false)
                .desc("Your password to connect with your proxy server.").hasArg().build();
        options.addOption(proxyPassword);

    }

    /**
     * Write the provided number of blank lines to the provided OutputStream.
     *
     * @param numberBlankLines Number of blank lines to write.
     * @param out              OutputStream to which to write the blank lines.
     */
    static void displayBlankLines(final int numberBlankLines, final OutputStream out) {
        try {
            for (int i = 0; i < numberBlankLines; ++i) {
                out.write("\n".getBytes());
            }
        } catch (IOException ioEx) {
            for (int i = 0; i < numberBlankLines; ++i) {
                System.out.println();
            }
        }
    }

    /**
     * Display application header.
     *
     * @param out OutputStream to which header should be written.
     */
    static void displayHeader(final OutputStream out) {
        final String header = "[Sample Demo App to showcase SDM SDK usage] \n";
        try {
            out.write(header.getBytes());
        } catch (IOException ioEx) {
            System.out.println(header);
        }
    }

    /**
     * Display command-line arguments without processing them in any further way.
     *
     * @param commandLineArguments Command-line arguments to be displayed.
     */
    static void displayProvidedCommandLineArguments(final String[] commandLineArguments, final OutputStream out) {
        final StringBuffer buffer = new StringBuffer();
        try {
            out.write(("CommandLine Argument passed by you were : ").getBytes());
            for (final String argument : commandLineArguments) {
                buffer.append(argument).append(" ");
            }
            out.write((buffer + "\n").getBytes());
        } catch (IOException ioEx) {
            System.err.println("WARNING: Exception encountered trying to write to OutputStream:\n" + ioEx.getMessage());
            System.out.println(buffer);
        }
    }

    static void displayInformation() {
        displayBlankLines(1, System.out);
        displayHeader(System.out);
        displayBlankLines(2, System.out);
        System.out.println("-- HELP --");
        displayBlankLines(1, System.out);
        printHelp(constructOptions(), 80, "HELP", " -- End OF HELP -- ", 3, 5, true);

    }

}
