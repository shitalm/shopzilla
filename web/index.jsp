<%--
  Created by IntelliJ IDEA.
  User: shitalm
  Date: 1/25/12
  Time: 8:35 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title>Shopzilla Feed Convertor</title></head>
  <body>
  This application converts Shopzilla XML feeds to the csv format. <br><br>
  <form method="post" action="/shopzilla">
      Enter the feed URL
      <input type="text" name="url" size="300">
      <input type="submit">
  </form>
  </body>
</html>