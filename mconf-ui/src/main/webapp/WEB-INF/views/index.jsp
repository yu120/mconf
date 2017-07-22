<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="library/master.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="renderer" content="webkit">

    <title>${msg.title_fu} - ${msg.title_ab}</title>

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
                                        <strong class="font-bold">${msg.title_fu}</strong>
                                    </span>
                                </span>
                            </a>
                        </div>
                        <div class="logo-element">${msg.title_ab}</div>
                    </li>
                    <li class="hidden-folded padder m-t m-b-sm text-muted text-xs">
                        <span class="ng-scope">${msg.menu_statistics_title}</span>
                    </li>
                    <li>
                        <a class="J_menuItem" href="${ctx}/web/main">
                            <i class="fa fa-home"></i>
                            <span class="nav-label">${msg.menu_statistics_home}</span>
                        </a>
                    </li>
                    <li class="line dk"></li>
                    <li class="hidden-folded padder m-t m-b-sm text-muted text-xs">
                        <span class="ng-scope">${msg.menu_auth_title}</span>
                    </li>
	              	<li>
	                	<a class="J_menuItem" href="${ctx}/api/apis">
	                    	<i class="fa fa-cart-arrow-down"></i>
	                        <span class="nav-label">${msg.menu_auth_api}</span>
	                        <span class="label label-info pull-right">${statistics.api}</span>
	               		</a>
	              	</li>
	              	<li>
	                	<a class="J_menuItem" href="${ctx}/consumer/consumers">
	                    	<i class="fa fa-users"></i>
	                        <span class="nav-label">${msg.menu_auth_consumer}</span>
	                        <span class="label label-primary pull-right">${statistics.consumer}</span>
	               		</a>
	              	</li>
	              	<li>
	                	<a class="J_menuItem" href="${ctx}/router/routers">
	                    	<i class="fa fa-sitemap"></i>
	                        <span class="nav-label">${msg.menu_auth_router}</span>
	                        <span class="label label-warning pull-right">${statistics.router}</span>
	               		</a>
	              	</li>
	              	<li class="line dk"></li>
                    <li class="hidden-folded padder m-t m-b-sm text-muted text-xs">
                        <span class="ng-scope">${msg.menu_governor_title}</span>
                    </li>
	              	<li>
	                	<a class="J_menuItem" href="${ctx}/parameter/parameters">
	                    	<i class="fa fa-exchange"></i>
	                        <span class="nav-label">${msg.menu_governor_parameter}</span>
	                        <span class="label label-success pull-right">${statistics.parameter}</span>
	               		</a>
	              	</li>
	              	<li>
	                	<a class="J_menuItem" href="${ctx}/throttle/throttles">
	                    	<i class="fa fa-bitbucket"></i>
	                        <span class="nav-label">${msg.menu_governor_throttle}</span>
	               		</a>
	              	</li>
                    
                    <li class="line dk"></li>
                    <li class="hidden-folded padder m-t m-b-sm text-muted text-xs">
                        <span class="ng-scope">${msg.menu_sys_title}</span>
                    </li>
                    <li>
                        <a class="J_menuItem" href="${ctx}/sysconf/sysconfs">
                            <i class="fa fa-cogs"></i>
                            <span class="nav-label">${msg.menu_sys_sysconf}</span>
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
                                <input type="text" placeholder="${msg.search_tip}" class="form-control" name="top-search" id="top-search">
                            </div>
                        </form>
                    </div>
                    <ul class="nav navbar-top-links navbar-right">
                        <li class="dropdown">
                            <a class="dropdown-toggle count-info" data-toggle="dropdown" href="#">
                                <i class="fa fa-bell"></i> <span class="label label-primary">2</span>
                            </a>
                            <ul class="dropdown-menu dropdown-alerts">
                                <li>
                                    <a href="#">
                                        <div>
                                            <i class="fa fa-envelope fa-fw"></i> 您有16条未读消息
                                            <span class="pull-right text-muted small">4分钟前</span>
                                        </div>
                                    </a>
                                </li>
                                <li class="divider"></li>
                                <li>
                                    <a href="#">
                                        <div>
                                            <i class="fa fa-qq fa-fw"></i> 3条新回复
                                            <span class="pull-right text-muted small">12分钟钱</span>
                                        </div>
                                    </a>
                                </li>
                                <li class="divider"></li>
                                <li>
                                    <div class="text-center link-block">
                                        <a class="J_menuItem" href="#">
                                            <strong>查看所有 </strong>
                                            <i class="fa fa-angle-right"></i>
                                        </a>
                                    </div>
                                </li>
                            </ul>
                        </li>
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
