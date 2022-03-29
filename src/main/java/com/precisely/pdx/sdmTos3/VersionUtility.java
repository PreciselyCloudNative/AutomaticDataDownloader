package com.precisely.pdx.sdmTos3;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author Ruchika Sharma
 */
public class VersionUtility {
    public static String getVersionInfo() {
        Enumeration<URL> resources;
        try {
            resources = Thread.currentThread().getContextClassLoader()
                    .getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL manifestUrl = resources.nextElement();
                Manifest manifest = new Manifest(manifestUrl.openStream());
                Attributes mainAttributes = manifest.getMainAttributes();
                String versionInfo = mainAttributes.getValue("Specification-Version");
                if (versionInfo != null) {
                    return versionInfo;

                }
            }
        } catch (IOException e) {
            System.out.println("PDX SDK Sample App : Unable to retrieve version information.");
        }
        return null;
    }
}
