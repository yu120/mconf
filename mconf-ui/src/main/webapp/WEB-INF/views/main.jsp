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
                                    <div class="h1 text-info font-thin h1">634</div>
                                    <span class="text-muted text-xs">TPS</span>
                                    <div class="top text-right w-full">
                                        <i class="fa fa-caret-down text-warning m-r-sm"></i>
                                    </div>
                                </div>
                            </div>
                            <div class="col-xs-6">
                                <div class="panel padder-v item bg-info">
                                    <div class="h1 text-fff font-thin h1">34</div>
                                    <span class="text-muted text-xs">平均响应耗时</span>
                                    <div class="top text-right w-full">
                                        <i class="fa fa-caret-down text-warning m-r-sm"></i>
                                    </div>
                                </div>
                            </div>
                            <div class="col-xs-6">
                                <div class="panel padder-v item bg-primary">
                                    <div class="h1 text-fff font-thin h1">34123</div>
                                    <span class="text-muted text-xs">今日交易量</span>
                                    <div class="top text-right w-full">
                                        <i class="fa fa-caret-down text-warning m-r-sm"></i>
                                    </div>
                                </div>
                            </div>
                            <div class="col-xs-6">
                                <div class="panel padder-v item">
                                    <div class="font-thin h1">20%</div>
                                    <span class="text-muted text-xs">交易量增长</span>
                                    <div class="bottom text-left">
                                        <i class="fa fa-caret-up text-warning m-l-sm"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
    	</div>
		<div class="row">
			<c:forEach var="no" begin="1" end="3" step="1">
				<div class="col-sm-4">
	                <div class="ibox float-e-margins">
	                    <div class="ibox-title">
	                        <h5>CoreGateway <small>gw.in.HttpGateway</small></h5>
	                        <div class="ibox-tools">内部网关</div>
	                    </div>
	                    <div class="ibox-content">
	                        <h5>VM(10.12.32.173:${8081+no})</h5>
	                        <table class="table table-stripped small m-t-md">
	                            <tbody>
	                                <tr>
	                                    <td><i class="fa fa-circle text-navy"> TPS</i></td>
	                                    <td>234,34</td>
	                                </tr>
	                                <tr>
	                                    <td><i class="fa fa-circle text-navy"> 平均响应耗时</i></td>
	                                    <td>42ms</td>
	                                </tr>
	                                <tr>
	                                    <td class="no-borders"><i class="fa fa-circle text-navy"> 交易量</i></td>
	                                    <td class="no-borders">12332</td>
	                                </tr>
	                            </tbody>
	                        </table>
	                        <div class="stat-percent font-bold text-navy">98% <i class="fa fa-bolt"></i></div>
	                        <small>更新时间:2017-05-12 13:32:51</small>
	                    </div>
	                </div>
	            </div>
			</c:forEach>
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


