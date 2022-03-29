package com.precisely.pdx.sdmTos3;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.examples.Expander;

import java.io.File;
import java.io.IOException;

public class ExtractUtils {
    private static Expander expander = new Expander();

    /**
     * Extracts a zip alongside the zip under a folder of the same name without the extension
     */
    public static void extractDelivery(File zip) throws IOException, ArchiveException {
        File targetDirectory = new File(zip.getParent());
        //System.out.println("Here"+FilenameUtils.getBaseName(zip.getName()));
        extractDelivery(zip, targetDirectory);
    }

    public static void extractDelivery(File zip, File targetDirectory) throws IOException, ArchiveException {
        targetDirectory.mkdirs();
        System.out.println("Extracting " + zip.getAbsolutePath() + " to " + targetDirectory.getAbsolutePath() + "...");
        expander.expand(zip, targetDirectory);
        System.out.println("Successfully Extracted");
        System.out.println("Deleting " + zip.getAbsolutePath() + "...");
        zip.delete();
    }
}
