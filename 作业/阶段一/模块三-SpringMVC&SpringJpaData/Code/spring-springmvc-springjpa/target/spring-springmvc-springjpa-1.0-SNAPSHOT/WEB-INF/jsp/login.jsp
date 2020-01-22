<%--
  Created by IntelliJ IDEA.
  User: zhudan
  Date: 2020/1/22
  Time: 08:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page isELIgnored="false" contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录</title>
    <script type="text/javascript" src="/js/jquery.min.js"></script>
    <script>
        $(function () {
            var message = "${message}";
            if(message != undefined && message != null && message != ""){
                alert(message);
            }
        });
    </script>
</head>
<body style="text-align:center">
<h3>登录</h3>
<div style="margin: 0 auto;">
    <form method="post" action="/login">
        <label style="width: 20px;">用户名：</label>
        <input type="text" name="name" id="name" />
        <br/>
        <label style="width: 20px;">密&nbsp;&nbsp;&nbsp;码：</label>
        <input type="password" name="password" id="password" />
        <br/><br/>
        <input type="submit" value="登录"/>
    </form>
</div>
</body>
</html>
