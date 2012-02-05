package com.shopzilla.feeds;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: shitalm
 * Date: 1/27/12
 * Time: 8:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class FeedsServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        String url = request.getParameter("url");
        ShopzillaFeedConvertor convertor = new ShopzillaFeedConvertor();
        response.setContentType("text/csv");
        try {
            convertor.convert(url, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new ServletException(e);
        }
    }
}
