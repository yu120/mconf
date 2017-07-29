<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="library/master.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title> - 主页</title>
    <meta name="keywords" content="">
    <meta name="description" content="">

    <link rel="shortcut icon" href="favicon.ico">
    <link href="${ctx}/res/css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
    <link href="${ctx}/res/css/font-awesome.css?v=4.4.0" rel="stylesheet">
    <link href="${ctx}/res/css/plugins/footable/footable.core.css" rel="stylesheet">

    <link href="${ctx}/res/css/animate.css" rel="stylesheet">
    <link href="${ctx}/res/css/style.css?v=4.1.0" rel="stylesheet">

</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content">
    	<%-- ${msg.welcome_tip} --%>
    	<div class="row">
    		<div class="col-sm-12">
                <div class="row">
                    <div class="col-sm-12">
                        <div class="row row-sm text-center">
                            <div class="col-xs-6">
                                <div class="panel padder-v item">
                                    <div class="h1 text-info font-thin h1">${nodeNum}</div>
                                    <span class="text-muted text-xs">数据节点</span>
                                </div>
                            </div>
                            <div class="col-xs-6">
                            	<a href="${ctx}/web/apps">
                                	<div class="panel padder-v item bg-info">
                                    	<div class="h1 text-fff font-thin h1">${appNum}</div>
                                    	<span class="text-muted text-xs">应用数量</span>
                                	</div>
                                </a>
                            </div>
                            <div class="col-xs-6">
                                <div class="panel padder-v item bg-primary">
                                    <div class="h1 text-fff font-thin h1">${envNum}</div>
                                    <span class="text-muted text-xs">配置环境</span>
                                </div>
                            </div>
                            <div class="col-xs-6">
                                <a href="${ctx}/web/confs">
	                                <div class="panel padder-v item">
	                                    <div class="font-thin h1">${confNum}</div>
	                                    <span class="text-muted text-xs">配置文件</span>
	                                </div>
	                        	</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
    	</div>
    </div>
    <!-- 全局js -->
    <script src="${ctx}/res/js/jquery.min.js?v=2.1.4"></script>
    <script src="${ctx}/res/js/bootstrap.min.js?v=3.3.6"></script>
    <script src="${ctx}/res/js/plugins/layer/layer.min.js"></script>
    <!-- Flot -->
    <script src="${ctx}/res/js/plugins/flot/jquery.flot.js"></script>
    <script src="${ctx}/res/js/plugins/flot/jquery.flot.tooltip.min.js"></script>
    <script src="${ctx}/res/js/plugins/flot/jquery.flot.resize.js"></script>
    <script src="${ctx}/res/js/plugins/flot/jquery.flot.pie.js"></script>
    <!-- 自定义js -->
    <script src="${ctx}/res/js/content.js"></script>
    
</body>

</html>


