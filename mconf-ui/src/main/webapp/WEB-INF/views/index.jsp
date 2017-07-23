<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="library/master.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="renderer" content="webkit">

    <title>蚂蚁配置 - 微配置</title>

    <meta name="keywords" content="">
    <meta name="description" content="">

    <!--[if lt IE 9]>
    <meta http-equiv="refresh" content="0;ie.html" />
    <![endif]-->

    <link rel="shortcut icon" href="favicon.ico">
    <link href="${ctx}/res/css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
    <link href="${ctx}/res/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <link href="${ctx}/res/css/animate.css" rel="stylesheet">
    <link href="${ctx}/res/css/style.css?v=4.1.0" rel="stylesheet">
</head>

<body class="fixed-sidebar full-height-layout gray-bg" style="overflow:hidden">
    <div id="wrapper">
        <!--左侧导航开始-->
        <nav class="navbar-default navbar-static-side" role="navigation">
            <div class="nav-close"><i class="fa fa-times-circle"></i>
            </div>
            <div class="sidebar-collapse">
                <ul class="nav" id="side-menu">
                    <li class="nav-header">
                        <div class="dropdown profile-element">
                            <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                                <span class="clear">
                                    <span class="block m-t-xs" style="font-size:20px;">
                                        <i class="fa fa-map-signs"></i>
                                        <strong class="font-bold">蚂蚁配置</strong>
                                    </span>
                                </span>
                            </a>
                        </div>
                        <div class="logo-element">微配置</div>
                    </li>
                    <li class="hidden-folded padder m-t m-b-sm text-muted text-xs">
                        <span class="ng-scope">监控统计</span>
                    </li>
                    <li>
                        <a class="J_menuItem" href="${ctx}/web/main">
                            <i class="fa fa-home"></i>
                            <span class="nav-label">首页</span>
                        </a>
                    </li>
                    <li class="line dk"></li>
                    <li class="hidden-folded padder m-t m-b-sm text-muted text-xs">
                        <span class="ng-scope">蚂蚁视角</span>
                    </li>
	              	<li>
	                	<a class="J_menuItem" href="${ctx}/web/apps">
	                    	<i class="fa fa-paper-plane"></i>
	                        <span class="nav-label">应用列表</span>
	                        <span class="label label-info pull-right">${statistics.app}</span>
	               		</a>
	              	</li>
	              	<li>
	                	<a class="J_menuItem" href="${ctx}/web/confs">
	                    	<i class="fa fa-sliders"></i>
	                        <span class="nav-label">配置文件</span>
	                        <span class="label label-warning pull-right">${statistics.conf}</span>
	               		</a>
	              	</li>
	              	<li>
	                	<a class="J_menuItem" href="${ctx}/web/datas">
	                    	<i class="fa fa-bitbucket"></i>
	                        <span class="nav-label">数据中心</span>
	                        <span class="label label-warning pull-right">${statistics.data}</span>
	               		</a>
	              	</li>
                </ul>
            </div>
        </nav>
        <!--左侧导航结束-->
        <!--右侧部分开始-->
        <div id="page-wrapper" class="gray-bg dashbard-1">
            <div class="row border-bottom">
                <nav class="navbar navbar-static-top" role="navigation" style="margin-bottom: 0">
                    <div class="navbar-header"><a class="navbar-minimalize minimalize-styl-2 btn btn-info " href="#"><i class="fa fa-bars"></i> </a>
                        <form role="search" class="navbar-form-custom" method="post" action="search_results.html">
                            <div class="form-group">
                                <input type="text" placeholder="请输入关键词搜索……" class="form-control" name="top-search" id="top-search">
                            </div>
                        </form>
                    </div>
                    <ul class="nav navbar-top-links navbar-right">
                        <li>
                        	<div class="ibox-tools">
                                <a class="dropdown-toggle" data-toggle="dropdown" href="#" style="font-size: 12px">${msg.language_tip}</a>
                                <ul class="dropdown-menu dropdown-user">
                                    <li><a href="${ctx}/web/setmsg?msgType=msg_zh_CN">简体中文</a></li>
                                    <li><a href="${ctx}/web/setmsg?msgType=msg_en_US">English</a></li>
                                </ul>
                            </div>
                        </li>
                    </ul>
                </nav>
            </div>
            <div class="row J_mainContent" id="content-main">
                <iframe id="J_iframe" width="100%" height="100%" src="${ctx}/web/main" frameborder="0"></iframe>
            </div>
        </div>
        <!--右侧部分结束-->
    </div>

    <!-- 全局js -->
    <script src="${ctx}/res/js/jquery.min.js?v=2.1.4"></script>
    <script src="${ctx}/res/js/bootstrap.min.js?v=3.3.6"></script>
    <script src="${ctx}/res/js/plugins/metisMenu/jquery.metisMenu.js"></script>
    <script src="${ctx}/res/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>
    <script src="${ctx}/res/js/plugins/layer/layer.min.js"></script>

    <!-- 自定义js -->
    <script src="${ctx}/res/js/hAdmin.js?v=4.1.0"></script>
    <script type="text/javascript" src="${ctx}/res/js/index.js"></script>

    <!-- 第三方插件 -->
    <script src="${ctx}/res/js/plugins/pace/pace.min.js"></script>

</body>

</html>
