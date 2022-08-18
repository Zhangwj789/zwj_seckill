<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="p" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>无标题文档</title>
</head>
<body style="background:#f0f9fd;">
<div class="lefttop"><span></span>管理菜单</div>
<dl class="leftmenu">
    <dd>
        <div class="title">
            <span><img src="${ctx}/static/images/leftico01.png"/></span>管理员管理
        </div>
        <ul class="menuson">
            <li><cite></cite><a href="${ctx}/admin/listAdminPage">管理员列表</a><i></i></li>
        </ul>
        <div class="title">
            <span><img src="${ctx}/static/images/leftico01.png"/></span>秒杀管理
        </div>
        <ul class="menuson">
            <li><cite></cite><a href="${ctx}/product/listPageSeckillProducts">秒杀信息列表</a><i></i></li>
            <%--<li><cite></cite><a href="#">进行中全部进度</a><i></i></li>--%>
        </ul>
    </dd>
</dl>
</body>
</html>

