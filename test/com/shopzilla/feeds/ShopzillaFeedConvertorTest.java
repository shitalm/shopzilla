package com.shopzilla.feeds;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public void testConversion() throws IOException, SAXException, ParserConfigurationException {
        InputStream in = ClassLoader.getSystemResourceAsStream("shopzilla.xml");
        ShopzillaFeedConvertor convertor = new ShopzillaFeedConvertor();
        StringBuffer buffer = new StringBuffer(10000);
        OutputStream out =  new ByteArrayOutputStream(10000);

        convertor.convertXMLToCSV(in, out);
        String output = out.toString();
        StringTokenizer tokenizer = new StringTokenizer(output, "\r\n");
        assertEquals(tokenizer.countTokens(), 4);   // header and 3 offers
        StringTokenizer header = new StringTokenizer(tokenizer.nextToken(), ",");
        assertEquals(header.countTokens(), 26);     // 26 columns in the csv feed
        // Check few header column names
        for(int i=0; header.hasMoreTokens(); i++) {
            String headerColumn = header.nextToken();
            if(i == 4) assertEquals(headerColumn, "title");
            if(i == 7) assertEquals(headerColumn, "url");
            if(i == 8) assertEquals(headerColumn, "image60x60");
            if(i == 26) assertEquals(headerColumn, "shipWeight");
            
        }
        // check few columns of first offer
        StringTokenizer firstOffer = new StringTokenizer(tokenizer.nextToken(), ",");
        for(int i=0; firstOffer.hasMoreTokens(); i++) {
            String element = firstOffer.nextToken();
            if(i == 4) assertEquals(element, "Acer Aspire Ethos AS8951G-9630");
            // Following two URL strings are not exactly same as in input xml
            // input xml has "&amp;" while following URLs have "&"
            // it seems default character encoding from byte array to character stream is causing this behavior
            if(i == 7) assertEquals(element, "http://rd.bizrate.com/rd?t=http%3A%2F%2Flink.mercent.com%2Fredirect.ashx%3Fmr%3AmerchantID%3DMSFT%26mr%3AtrackingCode%3DBEA8C409-78FE-E011-B18D-001B21A69EB0%26mr%3AtargetUrl%3Dhttp%3A%2F%2Fwww.microsoftstore.com%2Fstore%2Fmsstore%2Fen_US%2Fpd%2FproductID.237488000%253fWT.mc_id%253dmercent&mid=24106&cat_id=462&atom=10039&prod_id=4003042957&oid=4009717441&pos=1&b_id=18&bid_type=0&bamt=295d11ad54194830&cobrand=1&rf=af1&af_assettype_id=10&af_creative_id=6&af_id=6866&af_placement_id=1");
            if(i == 8) assertEquals(element, "http://image11.bizrate-images.com/resize?sq=60&uid=4009717441&mid=24106");
        }
        
    }

}
