package com.shopzilla.feeds;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: shitalm
 * Date: 2/4/12
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class Taxonomy {

    List<Category> getStaticChildCategories(String categoryId) throws Exception {
        String url = "file://taxonomy.xml";
        return getChildCategories(url, categoryId);
    }

    List<Category> getChildCategories(String categoryId) throws Exception {
        return getChildCategories(new ShopzillaURL().getCategoryQueryURL(categoryId), categoryId);
    }

    List<Category> getChildCategories(String url, String categoryId) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        //dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
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
        Taxonomy taxonomy = new Taxonomy();
        //String url = "file://taxonomy.xml";
        String url = new ShopzillaURL().getCategoryQueryURL(categoryId);
        System.out.println("URL=" + url);
        List<Category> list = taxonomy.getChildCategories(url, categoryId);
        System.out.println(Arrays.toString(list.toArray()));
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

