package com.precisely.pdx.sdmTos3;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Stream;

public class ConvertUtils {
    static SparkSession spark = SparkSession
            .builder()
            .appName("SparkSample")
            .config(new SparkConf().set("spark.sql.legacy.allowUntypedScalaUDF", "true"))
            .master("local[*]").getOrCreate();

    public static ArrayList<File> without_header = new ArrayList<>();
    public static String delimiter_final = "";

    public static String[] GetStringArray(ArrayList<String> arr) {
        // declaration and initialise String Array
        String str[] = new String[arr.size()];
        // ArrayList to Array Conversion
        for (int j = 0; j < arr.size(); j++) {
            // Assign each value to String array
            str[j] = arr.get(j);
        }
        return str;
    }

    public static ArrayList<String> columnRenamed(Dataset<Row> ds, String primaryKey, String prefix) {
        ArrayList<String> c = new ArrayList<>();
        String[] col_name = ds.columns();
        for (String col1 : col_name) {
            if (col1.equalsIgnoreCase(primaryKey)) {
                c.add(col1);
            } else {
                c.add(prefix + col1);
            }
        }
        return c;
    }

    public static String guessDelimiterHeader(File csv, String isHeader) throws IOException {
        String[] delimiters = {"|", ",", "\t", ";", " "};
        String first_line = "";
        try (Stream<String> lines = Files.lines(csv.toPath())) {
            if (isHeader.equalsIgnoreCase("true")) {
                first_line = lines.findFirst().get();
            }
            for (String delimiter : delimiters) {
                if (first_line.contains(delimiter)) {
                    delimiter_final = delimiter;
                    return delimiter;
                }
            }
        }
        // No delimiter found, maybe the data is 1 column?
        return "|";
    }

    public static void csvToParquet(File file, String isHeader, String data_has_header) throws IOException {


        String primaryKeys = "PBKEY";
        String data_set_name = FilenameUtils.removeExtension(file.getName()).replace("-", "_").replaceAll("\\__+", "").toLowerCase();
        Dataset<Row> ds = spark.read().format("csv").option("delimiter", guessDelimiterHeader(file, isHeader)).option("header", isHeader).load(file.getAbsolutePath());
        if (isHeader.equalsIgnoreCase("true") && data_has_header.equalsIgnoreCase("true")) {
//            String prefix = AbbreviationOfDataSets.valueOf(data_set_name).getAbbreviation() + data_set_vintage + "_";
            String prefix = "";
            ArrayList<String> col = columnRenamed(ds, primaryKeys, prefix);
            String[] renamed_col = GetStringArray(col);
            Dataset<Row> renamedColumns = ds.toDF(renamed_col);
            System.out.println("Converting file : " + file.getName() + " to parquet");
            renamedColumns.write().mode("overwrite").format("parquet").option("compression", "snappy").save(file.getParent() + "/" + data_set_name);
            System.out.println("Success file : " + file.getName() + " to parquet");
            FileUtils.forceDelete(file);
            System.out.println("Deleting file's csv version : " + file.getName());
        } else if (isHeader.equalsIgnoreCase("false") && data_has_header.equalsIgnoreCase("true")) {
            without_header.add(file);
        } else if (isHeader.equalsIgnoreCase("true") && data_has_header.equalsIgnoreCase("false") && !without_header.isEmpty()) {
//            String prefix = AbbreviationOfDataSets.valueOf(data_set_name).getAbbreviation() + data_set_vintage + "_";
            String prefix = "";
            ArrayList<String> col = columnRenamed(ds, primaryKeys, prefix);
            String[] renamed_col = GetStringArray(col);
            for (File w_file : without_header) {
                Dataset<Row> newDf = spark.read().format("csv").option("delimiter", guessDelimiterHeader(file, "false")).option("header", "false").load(w_file.getAbsolutePath());
                Dataset<Row> renamedColumns = newDf.toDF(renamed_col);
                data_set_name = FilenameUtils.removeExtension(w_file.getName()).toLowerCase();
                renamedColumns.write().mode("overwrite").format("parquet").option("compression", "snappy").save(w_file.getParent() + "/" + data_set_name);
                System.out.println("Success file : " + w_file.getName() + " to parquet");
                FileUtils.forceDelete(w_file);
                System.out.println("Deleting file's csv version : " + w_file.getName());
            }
            without_header.clear();

        } else if (isHeader.equalsIgnoreCase("false") && data_has_header.equalsIgnoreCase("false")) {
            ds.write().mode("overwrite").format("parquet").option("compression", "snappy").save(file.getParent() + "/" + data_set_name);
            System.out.println("Success file : " + file.getName() + " to parquet");
            FileUtils.forceDelete(file);
            System.out.println("Deleting file's csv version : " + file.getName());
        }
    }

}
