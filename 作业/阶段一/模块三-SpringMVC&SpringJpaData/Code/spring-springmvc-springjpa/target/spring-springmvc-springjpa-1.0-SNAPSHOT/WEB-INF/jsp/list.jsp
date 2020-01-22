<%--
  Created by IntelliJ IDEA.
  User: zhudan
--%>
<%@ page isELIgnored="false" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>简历表</title>
    <script type="text/javascript" src="/js/jquery.min.js"></script>

    <script>
        $(function () {

        });

        function add() {
            $("#id").val("");
            $("#name").val("");
            $("#address").val("");
            $("#phone").val("");
            popBox();
        }

        function update(id, name, address, phone) {
            $("#id").val(id);
            $("#name").val(name);
            $("#address").val(address);
            $("#phone").val(phone);
            popBox();
        }

        /*点击弹出按钮*/
        function popBox() {
            var popBox = document.getElementById("popBox");
            var popLayer = document.getElementById("popLayer");
            popBox.style.display = "block";
            popLayer.style.display = "block";

        };

        /*点击关闭按钮*/
        function closeBox() {
            var popBox = document.getElementById("popBox");
            var popLayer = document.getElementById("popLayer");
            popBox.style.display = "none";
            popLayer.style.display = "none";

            $("#id").val("");
            $("#name").val("");
            $("#address").val("");
            $("#phone").val("");
        }

        function deleteResume(id) {
            $.ajax({
                url: '/resume/' + id,
                type: 'DELETE',
                contentType: 'application/json;charset=utf-8',
                success: function (data) {
                    if (data == "success") {
                        alert("删除成功");
                        window.location.reload();
                    } else {
                        alert("删除失败");
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    // $("#p_test").innerHTML = "there is something wrong!";
                    alert(XMLHttpRequest.status);
                    alert(XMLHttpRequest.readyState);
                    alert(textStatus);
                }
            });
        }

        function submit() {
            var id = $("#id").val();
            var name = $("#name").val();
            var address = $("#address").val();
            var phone = $("#phone").val();

            if(id == undefined || id == null || id == ''){
                insertResume(name, address, phone);
            } else {
                updateResume(id, name, address, phone);
            }
        }

        function updateResume(id, name, address, phone) {
            $.ajax({
                url: '/resume/' + id + '/' + name + '/' + address + '/' + phone,
                type: 'PUT',
                contentType: 'application/json;charset=utf-8',
                success: function (data) {
                    closeBox();
                    if (data == "success") {
                        alert("更新成功");
                        window.location.reload();
                    } else {
                        alert("更新失败");
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    // $("#p_test").innerHTML = "there is something wrong!";
                    alert(XMLHttpRequest.status);
                    alert(XMLHttpRequest.readyState);
                    alert(textStatus);
                }
            });
        }

        function insertResume(name, address, phone) {
            $.ajax({
                url: '/resume/' + name + '/' + address + '/' + phone,
                type: 'GET',
                contentType: 'application/json;charset=utf-8',
                success: function (data) {
                    closeBox();
                    if (data == "success") {
                        alert("添加成功");
                        window.location.reload();
                    } else {
                        alert("添加失败");
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    // $("#p_test").innerHTML = "there is something wrong!";
                    alert(XMLHttpRequest.status);
                    alert(XMLHttpRequest.readyState);
                    alert(textStatus);
                }
            });
        }

    </script>

    <style>
        div{
            padding:10px 10px 0 10px;
        }
        table{
            border-right:1px solid #000;
            border-bottom:1px solid #000
        }
        table td, table th{
            border-left:1px solid #000;
            border-top:1px solid #000;
            padding: 5px 20px;
        }

        /*背景层*/
        #popLayer {
            display: none;
            background-color: #B3B3B3;
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
            z-index: 10;
            -moz-opacity: 0.8;
            opacity:.80;
            filter: alpha(opacity=80);
        }

        /*弹出层*/
        #popBox {
            display: none;
            background-color: #FFFFFF;
            z-index: 11;
            width: 200px;
            height: 200px;
            position:fixed;
            top:0;
            right:0;
            left:0;
            bottom:0;
            margin:auto;
        }

        #popBox .close{
            text-align: center;
        }

        /*关闭按钮*/
        #popBox .close a {
            text-decoration: none;
            color: #2D2C3B;
        }
        input[type="button"]{
            font-size: 16px;
            margin: 5px 10px;
        }

    </style>
</head>
<body>
    <div style="text-align: right">
        <a href="/logout">退出</a>
    </div>
    <div>
        <input type="button" name="popBox" value="新增" onclick="popBox()">
        <div id="popLayer"></div>
        <div id="popBox">
            <div class="content">
                <span>姓名：<input type="text" id="name"></span>
                <span>地址：<input type="text" id="address"></span>
                <span>电话：<input type="text" id="phone"></span>
                <input type="hidden" id="id">
            </div>
            <div class="close">
                <input type="button" value="关闭" onclick="closeBox()">
                <input type="button" value="确定" onclick="submit()">
            </div>
        </div>
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <th >id</th>
                <th>姓名</th>
                <th>地址</th>
                <th>手机</th>
                <th>操作</th>
            </tr>
            <c:forEach items="${list}" var="item" varStatus="loop">
                <tr>
                    <td>${item.id}</td>
                    <td>${item.name}</td>
                    <td>${item.address}</td>
                    <td>${item.phone}</td>
                    <td>
                        <input type="button" value="编辑"
                               onclick="update(${item.id},'${item.name}','${item.address}','${item.phone}')">
                        <input type="button" value="删除" onclick="deleteResume(${item.id})">
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</body>
</html>
