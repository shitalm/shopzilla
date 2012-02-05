package com.shopzilla.feeds;


import javax.servlet.http.HttpUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: shitalm
 * Date: 2/5/12
 * Time: 9:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class HTTPParams {
    private Hashtable<String, String[]> queryMap = new Hashtable<String, String[]>();
    private String hostStr = null;

    public HTTPParams(String host, String queryString) {
        this.hostStr = host;
        queryMap = HttpUtils.parseQueryString(queryString);
    }

    public HTTPParams(String urlString) {
        hostStr = urlString.substring(0, urlString.indexOf("?"));
        String queryString = urlString.substring(urlString.indexOf("?") + 1);
        queryMap = HttpUtils.parseQueryString(queryString);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(1000);
        for (Object entry : queryMap.entrySet()) {
            Map.Entry mapEntry = (Map.Entry) entry;
            buf.append(mapEntry.getKey()).append(":").append(Arrays.toString((String[]) mapEntry.getValue())).append("\n");
        }
        return buf.toString();

    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public String getQueryString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> entry : queryMap.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            for (String valueElement : entry.getValue()) {
                sb.append(String.format("%s=%s",
                        urlEncode(entry.getKey()),
                        urlEncode(valueElement)));
            }
        }
        return sb.toString();
    }

    public String getAttributeValue(String attrName) {
        String values[] = queryMap.get(attrName);
        if (values != null) return values[0];
        return null;
    }

    public int getAttributeValueAsInt(String attrName) {
        try {
            return Integer.parseInt(getAttributeValue(attrName));
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return 0;
        }

    }

    public void setAttributeValue(String attrName, String attrValue) {
        queryMap.put(attrName, new String[]{attrValue});
    }

    public String getHost() {
        return hostStr;
    }

    public String getURL() {
        return hostStr + "?" + getQueryString();
    }

    public void clearQueryParams() {
        queryMap.clear();
    }

}
