package com.shopzilla.feeds;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: shitalm
 * Date: 2/2/12
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    private static final Logger log = Logger.getLogger(Utils.class.getName());


    public static int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return 0;
        }
    }


    public static InputStream getInputStream(String urlString, boolean buffered) throws Exception {
        InputStream in = null;

        if (urlString.startsWith("file://")) {
            String fileName = urlString.substring(7, urlString.length());
            log.info("file name = " + fileName);
            in = ClassLoader.getSystemResourceAsStream(fileName);
            if (in == null) {
                log.info("Didn't find file in class path. Attempting to read from file system");
                in = new FileInputStream(fileName);
            }
        } else {
            in = new URL(urlString).openStream();
        }
        if (buffered)
            return new BufferedInputStream(in);
        else
            return in;

    }

}
