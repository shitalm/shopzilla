package com.shopzilla.feeds;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.servlet.http.HttpUtils;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts Shopzilla XML feed to CSV format
 */
public class ShopzillaFeedConvertor extends DefaultHandler {
    // getting SAXParserFactory instance
    private static SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    private HashMap<String, String> offer = null;
    private StringBuffer curValue = null;
    private String xsize = null, ysize = null;
    private PrintWriter writer = null;
    private int index = 0,
                curFetchCount = 0, maxFetchCount=100000,
                includedResultsIteration = 0, 
                fetchCountIteration = 0, totalResultsIteration = 1;
;


    public void convert(String urlString, OutputStream out) throws Exception {
        URL url = null;
        InputStream in = null;

        try {
            this.writer = new PrintWriter(new BufferedOutputStream(out));

            emitHeader();

            if (urlString.startsWith("file://")) {
                in = Utils.getInputStream(urlString, true);
            } else {
                convertURL(urlString, out);
                return;
            }
            convertXMLToCSV(in, out);

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            // TODO some error needs to go back to client
            throw e;
        } finally {
            try {
                if (in != null) in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void convertURL(String urlString, OutputStream out) throws Exception {
        HTTPParams params = new HTTPParams(urlString);

        int requestedMaxFetchCount = params.getAttributeValueAsInt("results");
        if(requestedMaxFetchCount != 0) {
            maxFetchCount = requestedMaxFetchCount;
        }
        System.err.println("Will download " + maxFetchCount + " offers");
        String categoryId = params.getAttributeValue("categoryId");
        if(categoryId == null || categoryId == "") {
            iterate(params, out);
            return;
        }
        System.err.println("Need to fetch child categories");
        List<Category> list = new Taxonomy().getStaticChildCategories(categoryId);
        if(list.size() == 0) {
            System.err.println(categoryId + " does not have child categories");
            list.add(new Category(categoryId));
        }
        System.err.println("Will fetch offers for " + list.size() + " categories");
        for(Category category : list) {
            params.setAttributeValue("categoryId", category.id);
            System.out.println(params.getURL());
            iterate(params, out);
        }

    }

    private void iterate(HTTPParams params, OutputStream out) throws Exception {
        //System.out.println("Query args:\n" + Utils.printQueryMap(queryMap));
        int iter = 0;
        fetchCountIteration = 0;
        totalResultsIteration = 1;
        while(fetchCountIteration < totalResultsIteration && curFetchCount < maxFetchCount) {
            index = fetchCountIteration;
            params.setAttributeValue("start", index + "");
            String newURLString = params.getURL();
            System.err.println("new URL string=\n" + newURLString);
            InputStream in = Utils.getInputStream(newURLString, true);

//            InputStream in = null;
            convertXMLToCSV(in, out);
            if (in != null) in.close();
            fetchCountIteration += includedResultsIteration;
            curFetchCount += includedResultsIteration;
            System.err.println("Iter = " + ++iter + " includedResultsIteration=" + includedResultsIteration
                    + " iterarion fetch count=" + fetchCountIteration + " current fetch count = " + curFetchCount);

            if(includedResultsIteration == 0) {
                // there are no more records to fetch
                System.err.println("No more items to fetch");
                break;
            }
            includedResultsIteration = 0; // reset for the next iteration

        }

    }

    public void convertXMLToCSVMock(InputStream in, OutputStream out) {
        includedResultsIteration = 250;
    }

    public void convertXMLToCSV(InputStream in, OutputStream out) throws SAXException, ParserConfigurationException, IOException {


        // Getting SAXParser object from AXParserFactory instance
        SAXParser saxParser = saxParserFactory.newSAXParser();

        // Parsing XML Document by calling parse method of SAXParser class
        saxParser.parse(new InputSource(in), this);
        writer.flush();
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //System.out.println("Start element =" + qName);
        if (qName.equalsIgnoreCase("Offer")) {
            offer = new HashMap<String, String>();
            for (int i = 0; i < attributes.getLength(); i++) {
                offer.put(attributes.getQName(i).toLowerCase(), attributes.getValue(i));
            }
        } else if (qName.equalsIgnoreCase("Image")) {
            xsize = attributes.getValue("xsize");
            ysize = attributes.getValue("ysize");
        } else if (qName.equalsIgnoreCase("Offers")) {
            totalResultsIteration = Utils.parseInt(attributes.getValue("totalResults"));
            includedResultsIteration = Utils.parseInt(attributes.getValue("includedResults"));
            System.err.println("totalResultsIteration=" + totalResultsIteration + " includedResultsIteration=" + includedResultsIteration);
        }

        curValue = new StringBuffer(300);
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //System.out.println("End element = "+ qName);
        if (offer == null) return;  // Unless we are inside offer element we are not interested
        if (qName.equalsIgnoreCase("Images")) return;   // we don't have to process outer Images container

        if (qName.equalsIgnoreCase("Offer")) {
            emitOffer(offer);
            offer = null;
            curValue = null;
            return;
        }
        if (curValue == null) return;

        if (qName.equalsIgnoreCase("Image")) {
            // We need to check whether sizes are one of the 60x60 or 100x100 or 160x160 or 400x400
            if (!xsize.equals(ysize)) return;
            // check whether its a supported size
            if (!(xsize.equals("60") || xsize.equals("100") || xsize.equals("160") || xsize.equals("400")))
                return;
            String elemName = "image-" + xsize + "x" + ysize;
            //System.out.println("Putting " + elemName + "=" + curValue + " in offer");

            offer.put(elemName, curValue.toString());
            xsize = null;
            ysize = null;
            return;
        }

        // we have a new element (other than image) value for offer
        //System.out.println("Putting " + qName.toLowerCase() + "=" + curValue + " in offer");
        offer.put(qName.toLowerCase(), curValue.toString());

    }

    private void emitValue(String key, boolean emitSeparator) {
        String value = offer.get(key.toLowerCase());
        // remove all new lines as they screw up csv format
        if (value != null) {
            value = value.replaceAll("\n||\r", "");
            if (value.contains("\"")) {
                value = value.replaceAll("\"", "\"\"");
                value = "\"" + value + "\"";
            } else if (value.contains(",")) {
                value = "\"" + value + "\"";
            }
            value = value.trim();   // remove leading and trailing spaces as well
        }
        value = (value == null ? "" : value);
        writer.write(value);
        if (emitSeparator) writer.write(",");
    }

    private void emitValue(String key) {
        emitValue(key, true);
    }

    /**
     * Order of the attributes below are deeply tied with order of headers in emitHeader()
     * Any change below needs to be co-ordinated with that method.
     */

    private void emitOffer(HashMap<String, String> offer) {
        emitValue("productId");
        emitValue("merchantId");
        emitValue("categoryId");
        emitValue("id");
        emitValue("title");
        emitValue("description");
        emitValue("manufacturer");
        emitValue("url");
        emitValue("image-60x60");
        emitValue("image-100x100");
        emitValue("image-160x160");
        emitValue("image-400x400");
        emitValue("sku");
        emitValue("detailURL");
        emitValue("price");
        emitValue("originalPrice");
        emitValue("markedDownPercent");
        emitValue("bidded");
        emitValue("merchantProductId");
        emitValue("merchantName");
        emitValue("merchantLogoURL");
        emitValue("condition");
        emitValue("stock");
        emitValue("shipAmount");
        emitValue("shipType");
        emitValue("shipWeight", false);
        writer.println();

    }

    /**
     * Order of the attributes below are deeply tied with order of emitting in emitOffer()
     * Any change below needs to be co-ordinated with that method.
     */

    private void emitHeader() {
        writer.print("productId,merchantId,categoryId,id,title,description,manufacturer,url,");
        writer.print("image60x60,image100x100,image160x160,image400x400,");
        writer.print("sku,detailURL,price,originalPrice,markedDownPercent,bidded,");
        writer.print("merchantProductId,merchantName,merchantLogoURL,");
        writer.print("condition,stock,shipAmount,shipType,shipWeight");
        writer.println();

    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        if (offer == null) return;  // Unless we are inside offer element we are not interested
        curValue.append(chars, start, length);
        //System.out.println("Start=" + start + " length = "+ length + " value=" + curValue);
    }

    public static void main(String[] args) throws Exception {
        //FileReader reader = new FileReader("/Users/shitalm/Documents/work/test/feeds/test/shopzilla.xml");
        ShopzillaFeedConvertor convertor = new ShopzillaFeedConvertor();
        //convertor.convert("file://Users/shitalm/Documents/work/test/feeds/resources/shopzilla.xml", System.out);
        //String url = "http://catalog.bizrate.com/services/catalog/v1/us/product?apiKey=bfc9253adedf4ad6880d24ee17eb59d6&publisherId=6866&placementId=1&categoryId=&keyword=acer+aspire+laptops&productId=&productIdType=&offersOnly=true&merchantId=&brandId=&biddedOnly=true&minPrice=&maxPrice=&minMarkdown=&zipCode=&freeShipping=&start=0&results=3&backfillResults=0&startOffers=0&resultsOffers=0&sort=relevancy_desc&attFilter=&attWeights=&attributeId=&resultsAttribute=1&resultsAttributeValues=1&showAttributes=&showProductAttributes=&minRelevancyScore=100&maxAge=&showRawUrl=&imageOnly=true&format=xml&callback=callback";
        String url = "http://catalog.bizrate.com/services/catalog/v1/us/product?apiKey=bfc9253adedf4ad6880d24ee17eb59d6&publisherId=6866&&categoryId=&keyword=laptop&productId=&productIdType=&offersOnly=true&biddedOnly=true&start=0&results=30000&sort=relevancy_desc&minRelevancyScore=100&imageOnly=true&format=xml";
        convertor.convert(url, System.out);

    }
}
