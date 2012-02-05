package com.shopzilla.feeds;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: shitalm
 * Date: 2/2/12
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Utils {


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
            System.out.println("file name = " + fileName);
            try {
                in = new FileInputStream(fileName);
            } catch (FileNotFoundException ex) {
                System.out.println("Didn't find file at given path. Attempting to read from class path");
                in = ClassLoader.getSystemResourceAsStream(fileName);
                if(in == null) throw ex;
                System.out.println(in.available());
            }
        } else {
            in = new URL(urlString).openStream();
        }
        if(buffered)
            return new BufferedInputStream(in);
        else
            return in;

    }

}
