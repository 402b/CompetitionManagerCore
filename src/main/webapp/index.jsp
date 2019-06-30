<%@ page import="java.io.File" %>
<%@ page import="core.FileManager" %>
<html>
<body>
<h2>Hello World!</h2>
<%
        FileManager.INSTANCE.checkFolder();
%>
</body>
</html>
