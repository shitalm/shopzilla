package com.shopzilla.feeds;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.StringTokenizer;

import static org.testng.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: shitalm
 * Date: 1/27/12
 * Time: 9:45 AM
 * To change this template use File | Settings | File Templates.
 */
@Test
public class ShopzillaFeedConvertorTest {

    @Test(enabled = true)
    public void testConversion() throws Exception {
//        InputStream in = ClassLoader.getSystemResourceAsStream("shopzilla.xml");
        ShopzillaFeedConvertor convertor = new ShopzillaFeedConvertor();
        StringBuffer buffer = new StringBuffer(10000);
        OutputStream out =  new ByteArrayOutputStream(10000);

        convertor.convert("file://shopzilla.xml", out);
        String output = out.toString();
        StringTokenizer tokenizer = new StringTokenizer(output, "\r\n");
        assertEquals(tokenizer.countTokens(), 4);   // header and 3 offers
        StringTokenizer header = new StringTokenizer(tokenizer.nextToken(), ",");
        System.out.println("header = " + header);
        System.out.flush();
        assertEquals(header.countTokens(), 6);     // 6 mandatory columns
        // Check few header column names
        for(int i=1; header.hasMoreTokens(); i++) {
            String headerColumn = header.nextToken();
            if(i == 1) assertEquals(headerColumn, "title");
            if(i == 2) assertEquals(headerColumn, "description");
            if(i == 3) assertEquals(headerColumn, "url");
            if(i == 4) assertEquals(headerColumn, "image60x60");
            if(i == 5) assertEquals(headerColumn, "sku");
            if(i == 6) assertEquals(headerColumn, "price");

            
        }
        // check few columns of first offer
        StringTokenizer firstOffer = new StringTokenizer(tokenizer.nextToken(), ",");
        for(int i=1; firstOffer.hasMoreTokens(); i++) {
            String element = firstOffer.nextToken();
            if(i == 1) assertEquals(element, "Acer Aspire Ethos AS8951G-9630");
            // Following two URL strings are not exactly same as in input xml
            // input xml has "&amp;" while following URLs have "&"
            // it seems default character encoding from byte array to character stream is causing this behavior
            if(i == 3) assertEquals(element, "http://rd.bizrate.com/rd?t=http%3A%2F%2Flink.mercent.com%2Fredirect.ashx%3Fmr%3AmerchantID%3DMSFT%26mr%3AtrackingCode%3DBEA8C409-78FE-E011-B18D-001B21A69EB0%26mr%3AtargetUrl%3Dhttp%3A%2F%2Fwww.microsoftstore.com%2Fstore%2Fmsstore%2Fen_US%2Fpd%2FproductID.237488000%253fWT.mc_id%253dmercent&mid=24106&cat_id=462&atom=10039&prod_id=4003042957&oid=4009717441&pos=1&b_id=18&bid_type=0&bamt=295d11ad54194830&cobrand=1&rf=af1&af_assettype_id=10&af_creative_id=6&af_id=6866&af_placement_id=1");
            if(i == 4) assertEquals(element, "http://image11.bizrate-images.com/resize?sq=60&uid=4009717441&mid=24106");
        }
        
    }

    @Test (enabled = true)
    public void URLTest() throws IOException {
        OutputStream out = null;
        ShopzillaFeedConvertor convertor = new ShopzillaFeedConvertor();
        String url = "http://catalog.bizrate.com/services/catalog/v1/us/product?apiKey=bfc9253adedf4ad6880d24ee17eb59d6&publisherId=6866&&categoryId=10000000&keyword=&productId=&productIdType=&offersOnly=true&biddedOnly=true&start=0&results=300&sort=relevancy_desc&imageOnly=true&format=xml";

        //String url = "file://shopzilla.xml";
        try {
            out = new FileOutputStream(new File("feed.csv"));
            convertor.convert(url, out);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if(out != null) out.close();
        }

    }

}
