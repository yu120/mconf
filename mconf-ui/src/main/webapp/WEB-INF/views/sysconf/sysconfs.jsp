<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../library/master.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>${msg.menu_sys_title} - ${msg.menu_sys_sysconf}</title>
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
                            <h5>${msg.menu_sys_sysconf}</h5>
                            <div class="ibox-tools">
                                <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
                                <ul class="dropdown-menu dropdown-user">
                                    <li><a href="#">选项 01</a></li>
                                    <li><a href="#">选项 02</a></li>
                                </ul>
                            </div>
                        </div>
                        <div class="ibox-content">
                        	<a href="#">
                        		<button class="btn btn-info" type="button"><i class="fa fa-check"></i>${msg.add_tip}</button>
                        	</a>
                        	<a href="#">
                        		<button class="btn btn-danger" type="button"><i class="fa fa-trash-o"></i>${msg.delete_tip}</button>
                        	</a>
                        	
                            <table class="footable table table-stripped toggle-arrow-tiny" data-page-size="8">
                                <thead>
                                <tr>
                                    <th data-toggle="true">${msg.sysconf_key}</th>
                                    <th>${msg.sysconf_title}</th>
                                    <th>${msg.sysconf_value}</th>
                                    <th>${msg.sysconf_group}</th>
                                    <th>${msg.common_status}</th>
                                    <th data-hide="all">${msg.common_operateTime}</th>
                                    <th data-hide="all">${msg.common_remarks}</th>
                                    <th width="130px">${msg.common_operate}</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:choose>
                                	<c:when test="${not empty sysconfs}">
                                		<c:forEach items="${sysconfs}" var="sysconf">
			                                <tr>
			                                    <td><span class="badge badge-primary">${sysconf.key}</span></td>
			                                    <td>
			                                    	<c:choose>
														<c:when test="${not empty sysconf.title}">${sysconf.title}</c:when>
														<c:otherwise><font color="gray">——</font></c:otherwise>
													</c:choose>
			                                    </td>
			                                    <td>${sysconf.value}</td>
			                                    <td>
			                                    	<c:choose>
														<c:when test="${not empty sysconf.group}">${sysconf.group}</c:when>
														<c:otherwise><font color="gray">——</font></c:otherwise>
													</c:choose>
			                                    </td>
												<td>
													<c:choose>
														<c:when test="${sysconf.status}">
															<span class="badge badge-info">${msg.sysconf_status_enable}</span>
														</c:when>
														<c:otherwise>
															<span class="badge">${msg.sysconf_status_notEnable}</span>
														</c:otherwise>
													</c:choose>
												</td>
												<td>
													<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${sysconf.operateTime}" type="both"/> 
												</td>
												<td>
													<c:choose>
														<c:when test="${not empty sysconf.remarks}">${sysconf.remarks}</c:when>
														<c:otherwise><font color="gray">${msg.common_notInformation}</font></c:otherwise>
													</c:choose>
												</td>
												<td>
			                                    	<button class="btn btn-info btn-xs" type="button"><i class="fa fa-paste"></i> ${msg.edit_tip}</button>
			                                    	<a href="#">
			                                    		<button class="btn btn-danger btn-xs" type="button"><i class="fa fa-times"></i> ${msg.delete_tip}</button>
			                                    	</a>
			                                    </td>
			                                </tr>
		                                </c:forEach>
                                	</c:when>
                                	<c:otherwise>
                                		<tr>
                                			<td colspan="8" style="color: gray">${msg.common_notfound}</td>
                                		</tr>
                                	</c:otherwise>
                                </c:choose>
                                </tbody>
                                <tfoot>
                                <tr>
                                    <td colspan="8">
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

