package com.shopzilla.feeds;


import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: shitalm
 * Date: 2/4/12
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class Taxonomy {

    private static final Logger log = Logger.getLogger(Taxonomy.class.getName());
    private ShopzillaURL url;

    public Taxonomy() {}

    public Taxonomy(ShopzillaURL url) {
        this.url = url;
    }

    List<Category> getStaticChildCategories(String categoryId) throws Exception {
        String url = "file://taxonomy.xml";
        return getChildCategories(url, categoryId);
    }

    List<Category> getChildCategories(String categoryId) throws Exception {
        return getChildCategories(url.getCategoryQueryURL(categoryId), categoryId);
    }

    List<Category> getChildCategories(String url, String categoryId) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        //dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        log.fine("Downloading categories using URL: " + url);
        Document doc = dBuilder.parse(Utils.getInputStream(url, true));
        return getChildCategories(doc, categoryId);

    }


    private List<Category> getChildCategories(Document doc, String categoryId) throws Exception {

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        String xpathExpr = "//Category[@id='"+ categoryId + "']//Category/@id";
        System.out.println("Xpath expr = " + xpathExpr);
        NodeList nodes = (NodeList)xpath.evaluate(xpathExpr, doc, XPathConstants.NODESET);
        List<Category> list = new ArrayList<Category>();
        for(int i=0; i < nodes.getLength(); i++) {
            list.add(new Category(nodes.item(i).getNodeValue()));
        }
        System.out.println("Category count = " + list.size());
        System.err.println(Arrays.toString(list.toArray()));
        return list;

    }


    public static void main(String[] args) throws Exception {
        String categoryId = "10000000";
        String apiKey = System.getenv("apiKey");
        String pubId = System.getenv("publisherId");
        if(apiKey == null || pubId == null) System.err.println("Please provide api key and publisher id as env properties");
        Taxonomy taxonomy = new Taxonomy(new ShopzillaURL(apiKey, pubId));
        //String url = "file://taxonomy.xml";
        List<Category> list = taxonomy.getChildCategories(categoryId);
        System.err.println(Arrays.toString(list.toArray()));
    }
}

class Category {
    String id, name;

    public Category(String id) {
        this.id = id;
    }

    public String toString() {
        return id;
    }
}

