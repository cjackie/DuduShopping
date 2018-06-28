<%--
  Created by IntelliJ IDEA.
  User: chaojiewang
  Date: 4/26/18
  Time: 9:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
    <div id="main"></div>

    <script type="text/javascript" src="${pageContext.request.contextPath}/react-bundle.js"></script>
    <script type="text/javascript">
      DuduShopping.render({id: "main"});
    </script>
  </body>
</html>
