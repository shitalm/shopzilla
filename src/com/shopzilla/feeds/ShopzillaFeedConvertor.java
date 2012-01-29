package com.shopzilla.feeds;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URL;
import java.util.HashMap;

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




    public void convert(String urlString, OutputStream out) {
        URL url = null;
        InputStream in = null;

        try {

            if(urlString.startsWith("file://")) {
                in = new FileInputStream(urlString.substring(6, urlString.length()));
            } else {
                url = new URL(urlString);
                in = url.openStream();
            }
            convertXMLToCSV(in, out);

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            // TODO some error needs to go back to client
        } finally {
            try {
                if (in != null) in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void convertXMLToCSV(InputStream in, OutputStream out) throws SAXException, ParserConfigurationException, IOException {
        this.writer = new PrintWriter(new BufferedOutputStream(out));

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        emitHeader();
        
        // Getting SAXParser object from AXParserFactory instance
        SAXParser saxParser = saxParserFactory.newSAXParser();

        // Parsing XML Document by calling parse method of SAXParser class
        saxParser.parse(new InputSource(reader), this);
        writer.flush();
    }

//    @Override
//    public void startElement()


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //System.out.println("Start element =" + qName);
        if (qName.equalsIgnoreCase("Offer")) {
            offer = new HashMap<String, String>();
            for(int i = 0; i < attributes.getLength(); i++) {
                offer.put(attributes.getQName(i).toLowerCase(), attributes.getValue(i));
            }
        } else if (qName.equalsIgnoreCase("Image")) {
            xsize = attributes.getValue("xsize");
            ysize = attributes.getValue("ysize");
        }

        curValue = new StringBuffer(300);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //System.out.println("End element = "+ qName);
        if (offer == null) return;  // Unless we are inside offer element we are not interested
        if (qName.equalsIgnoreCase("Images")) return;   // we don't have to process outer Images container

        if(qName.equalsIgnoreCase("Offer")) {
            emitOffer(offer);
            offer = null;
            curValue = null;
            return;
        }
        if(curValue == null) return;

        if (qName.equalsIgnoreCase("Image")) {
            // We need to check whether sizes are one of the 60x60 or 100x100 or 160x160 or 400x400
            if(!xsize.equals(ysize)) return;
            // check whether its a supported size
            if( !(xsize.equals("60") || xsize.equals("100") || xsize.equals("160") || xsize.equals("400")))
                return;
            String elemName = "image-" + xsize + "x" + ysize;
            //System.out.println("Putting " + elemName + "=" + curValue + " in offer");
            
            offer.put(elemName, curValue.toString());
            xsize = null; ysize = null;
            return;
        }

        // we have a new element (other than image) value for offer
        //System.out.println("Putting " + qName.toLowerCase() + "=" + curValue + " in offer");
        offer.put(qName.toLowerCase(), curValue.toString());

    }

    private void emitValue(String key, boolean emitSeparator) {
        String value = offer.get(key.toLowerCase());
        // remove all new lines as they screw up csv format
        if(value!= null) {
            value = value.replaceAll("\n||\r", "");
            if(value.contains("\"")) {
                value = value.replaceAll("\"", "\"\"");
                value = "\"" + value + "\"";
            } else if(value.contains(",")) {
                value = "\"" + value + "\"";
            }
            value = value.trim();   // remove leading and trailing spaces as well
        }
        value =  (value == null ? "" : value);
        writer.write(value);
        if(emitSeparator) writer.write(",");
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
        String url = "http://catalog.bizrate.com/services/catalog/v1/us/product?apiKey=bfc9253adedf4ad6880d24ee17eb59d6&publisherId=6866&placementId=1&categoryId=&keyword=acer+aspire+laptops&productId=&productIdType=&offersOnly=true&merchantId=&brandId=&biddedOnly=true&minPrice=&maxPrice=&minMarkdown=&zipCode=&freeShipping=&start=0&results=3&backfillResults=0&startOffers=0&resultsOffers=0&sort=relevancy_desc&attFilter=&attWeights=&attributeId=&resultsAttribute=1&resultsAttributeValues=1&showAttributes=&showProductAttributes=&minRelevancyScore=100&maxAge=&showRawUrl=&imageOnly=true&format=xml&callback=callback";
        convertor.convert(url, System.out);
        
    }
}
