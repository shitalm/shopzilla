package com.shopzilla.feeds;



import javax.servlet.http.HttpUtils;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: shitalm
 * Date: 2/4/12
 * Time: 10:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class ShopzillaURL {
    private String
            host = "http://catalog.bizrate.com/services/catalog/v1/us/taxonomy";
    private String apiKey;
    //= "bfc9253adedf4ad6880d24ee17eb59d6";
    private String publisherId;
    //= "6866";
    private String categoryId = "";
    private String fetchCount = "100000";
    private String format = "xml";

    private HTTPParams params= new HTTPParams(host, "");

    public ShopzillaURL(String apiKey, String publisherId) {
        this.apiKey = apiKey;
        this.publisherId = publisherId;
    }

    String getCategoryQueryURL(String categoryId) {
        this.categoryId = categoryId;
        params.clearQueryParams();
        params.setAttributeValue("apiKey", apiKey);
        params.setAttributeValue("publisherId", publisherId);
        params.setAttributeValue("categoryId", categoryId);
        params.setAttributeValue("results", fetchCount);
        params.setAttributeValue("format", format);

        return params.getURL();
    }

}
