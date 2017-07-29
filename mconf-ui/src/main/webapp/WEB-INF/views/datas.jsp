<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="library/master.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>数据中心 - 蚂蚁配置</title>
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
                <div class="col-sm-12">
                    <div class="ibox float-e-margins">
                        <div class="ibox-title">
                            <h5><font color="gray">蚂蚁视角 >> </font>数据中心</h5>
                            <div class="ibox-tools">
                                <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
                                <ul class="dropdown-menu dropdown-user">
                                    <li><a href="#">选项 01</a></li>
                                    <li><a href="#">选项 02</a></li>
                                </ul>
                            </div>
                        </div>
                        <div class="ibox-content">
                        	<div class="row">
	                            <div class="col-sm-8 m-b-xs">
	                                <a href="#">
		                        		<button class="btn btn-info" type="button"><i class="fa fa-check"></i>添加</button>
		                        	</a>
		                        	<a href="#">
		                        		<button class="btn btn-danger" type="button"><i class="fa fa-trash-o"></i>删除</button>
		                        	</a>
	                            </div>
	                            <div class="col-sm-4">
	                            	<form action="${ctx}/web/datas">
	                                	<div class="input-group">
		                                    <input type="text" name="keywords" placeholder="请输入关键词开始搜索……" value="${keywords}" class="input-sm form-control">
		                                    <span class="input-group-btn">
		                                    	<button type="submit" class="btn btn-sm btn-info"> 搜索</button>
		                                    </span>
	                                	</div>
	                                </form>
	                            </div>
	                        </div>
                        	
                            <table class="footable table table-stripped toggle-arrow-tiny" data-page-size="8">
                                <thead>
                                <tr>
                                    <th data-toggle="true">Data ID</th>
                                    <th>Configure</th>
                                    <th>Version(<i class="fa fa-vimeo"></i>)</th>
                                    <th>Group</th>
                                    <th>Environment</th>
                                    <th>Application</th>
                                    <th>Data Node</th>
                                    <th data-hide="all">Data</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:choose>
                                	<c:when test="${not empty datas}">
                                		<c:forEach items="${datas}" var="data" varStatus="dataid">
			                                <tr>
			                                    <td>${data.data}</td>
			                                    <td>${data.conf}</td>
			                                    <td><span class="badge badge-warning"><i class="fa fa-vimeo"></i> ${data.version}</span></td>
			                                    <td><span class="badge badge-blue">${data.group}</span></td>
			                                    <td><span class="badge badge-green">${data.env}</span></td>
			                                    <td>${data.app}</td>
			                                    <td><span class="badge badge-info">${data.node}</span></td>
			                                    <td>
			                                    	<ul>
			                                    		<c:forEach items="${data.body}" var="kv">
			                                    			<li style="list-style:none;">
			                                    				<span class="badge badge-warning">${kv.key}</span>
			                                    				<font color="gray"> :</font>
			                                    				<span class="badge badge-blue">${kv.value}</span>
			                                    			</li>			                                    		
			                                    		</c:forEach>
			                                    	</ul>
			                                    </td>
			                                </tr>
		                                </c:forEach>
                                	</c:when>
                                	<c:otherwise>
                                		<tr>
                                			<td colspan="9" style="color: gray">${msg.common_notfound}</td>
                                		</tr>
                                	</c:otherwise>
                                </c:choose>
                                </tbody>
                                <tfoot>
                                <tr>
                                    <td colspan="8">
                                        <span style="color: gray">共 ${datas.size()} 条记录！</span>
                                        <ul class="pagination pull-right"></ul>
                                    </td>
                                </tr>
                                </tfoot>
                            </table>

                        </div>
                    </div>
                </div>
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

