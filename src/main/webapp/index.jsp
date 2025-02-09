<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>主页</title>
    <script type="text/javascript" src="http://apps.bdimg.com/libs/jquery/2.1.1/jquery.min.js"></script>
</head>
<body>
<div style="width:100%;text-align:center">
    <h1>测试</h1>

    <form>
        姓名：<input type="text" name="username"><br><br>
        年龄：<input type="text" name="age"><br><br>
        <button type="button" onclick="getJson()">测试</button>
    </form>
</div>
</body>
<script>
    function getJson() {
        $.ajax({
            url: "/user/getJson",
            type: "POST",
            dataType: "json",
            async: false,
            data: $("form").serialize(),
            success: function (data) {
                alert(data.nameAndAge);
            }
        });
    }
</script>
</html>