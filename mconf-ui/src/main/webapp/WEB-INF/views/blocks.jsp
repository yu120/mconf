<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="library/master.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>${msg.menu_auth_title} - ${msg.menu_auth_router}</title>
    <meta name="keywords" content="">
    <meta name="description" content="">

    <link rel="shortcut icon" href="favicon.ico">
    <link href="${ctx}/res/css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
    <link href="${ctx}/res/css/font-awesome.css?v=4.4.0" rel="stylesheet">
    <link href="${ctx}/res/css/plugins/footable/footable.core.css" rel="stylesheet">

 	<!-- Sweet Alert -->
    <link href="${ctx}/res/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
    
    <link href="${ctx}/res/css/animate.css" rel="stylesheet">
    <link href="${ctx}/res/css/style.css?v=4.1.0" rel="stylesheet">

</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content animated fadeInRight">
    	<div class="row">
    		<c:forEach items="${confconfs}" var="confconf">
				<div class="col-sm-4">
	                <div class="ibox float-e-margins">
	                    <div class="ibox-title">
	                        <h5>${confconf.conf} <small>${confconf.app}</small></h5>
	                        <div class="ibox-tools">节点：${confconf.node}</div>
	                    </div>
	                    <div class="ibox-content">
	                        <h5><a href="#" style="color: gray">查看所有配置项(<i class="fa text-danger">${confconf.subNum}</i>)</a></h5>
	                        <table class="table table-stripped small m-t-md">
	                        	<thead>
	                        		<tr>
	                                	<th>Attribute Key</th>
	                                    <th>Attribute Value</th>
	                               	</tr>
	                        	</thead>
	                            <tbody>
	                            	<c:forEach items="${confconf.attributes}" var="attr">
	                            		<tr>
	                                    	<td><i class="fa fa-circle text-navy"> ${attr.key}</i></td>
	                                    	<td><i class="fa text-danger"> ${attr.value}</i></td>
	                                	</tr>
	                            	</c:forEach>
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
    <script src="${ctx}/res/js/plugins/footable/footable.all.min.js"></script>

    <!-- 自定义js -->
    <script src="${ctx}/res/js/content.js?v=1.0.0"></script>
    <!-- Sweet alert -->
    <script src="${ctx}/res/js/plugins/sweetalert/sweetalert.min.js"></script>
    <script>
        $(document).ready(function() {
            $('.footable').footable();
	        $('.demo3').click(function () {
	            swal({
	                title: "您确定要删除该配置数据项吗",
	                text: "删除后将无法恢复，请谨慎操作！",
	                type: "warning",
	                showCancelButton: true,
	                confirmButtonColor: "#DD6B55",
	                confirmButtonText: "删除",
	                closeOnConfirm: false
	            }, function () {
	            	swal("删除成功！", "您已经永久删除了该配置数据项。", "success");
	            });
	        });
        });
    </script>

</body>

</html>

