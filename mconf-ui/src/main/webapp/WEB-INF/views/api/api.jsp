<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../library/master.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>${msg.menu_auth_title} - ${msg.menu_auth_api}</title>
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
                            <h5>${msg.menu_auth_api}</h5>
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
	                            <div class="col-sm-12">
	                                <div class="m-b-md">
	                                    <a href="project_detail.html#" class="btn btn-white btn-xs pull-right">${msg.edit_tip}</a>
	                                    <h2>${api.apiId}（${api.title}）</h2>
	                                </div>
	                                <dl class="dl-horizontal">
	                                    <dt>${msg.common_status}：</dt>
	                                    <dd>
	                                    	<c:choose>
												<c:when test="${api.status}">
													<span class="badge badge-info">${msg.api_status_enable}</span>
												</c:when>
												<c:otherwise>
													<span class="badge">${msg.api_status_notEnable}</span>
												</c:otherwise>
											</c:choose>
	                                    </dd>
	                                </dl>
	                            </div>
	                        </div>
	                        <div class="row">
	                            <div class="col-sm-5">
	                                <dl class="dl-horizontal">
	                                    <dt>${msg.api_apiId}：</dt>
	                                    <dd>${api.apiId}</dd>
	                                    <dt>${msg.common_sdc}：</dt>
	                                    <dd>${api.sdc}</dd>
	                                    <dt>${msg.common_remarks}：</dt>
	                                    <dd>
	                                    	<c:choose>
												<c:when test="${not empty api.remarks}">${api.remarks}</c:when>
												<c:otherwise><font color="gray">${msg.common_notInformation}</font></c:otherwise>
											</c:choose>
	                                    </dd>
	                                </dl>
	                            </div>
	                            <div class="col-sm-7" id="cluster_info">
	                                <dl class="dl-horizontal">
	                                    <dt>${msg.api_title}：</dt>
	                                    <dd>${api.title}</dd>
	                                    <dt>${msg.common_operateTime}：</dt>
	                                    <dd>${api.operateTime}</dd>
	                                </dl>
	                            </div>
	                        </div>
	                        <div class="row m-t-sm">
	                            <div class="col-sm-12">
	                                <div class="panel blank-panel">
	                                    <div class="panel-heading">
	                                        <div class="panel-options">
	                                            <ul class="nav nav-tabs">
	                                                <li><a href="#tab-1" data-toggle="tab">${msg.api_reqData}/${msg.api_resData}</a></li>
	                                                <li><a href="#tab-2" data-toggle="tab">${msg.api_reqHeaders}</a></li>
	                                                <li><a href="#tab-3" data-toggle="tab">${msg.api_resHeaders}</a></li>
	                                            </ul>
	                                        </div>
	                                    </div>
	
	                                    <div class="panel-body">
	                                        <div class="tab-content">
	                                        	<div class="tab-pane active" id="tab-1">
	                                                <div class="feed-activity-list">
	                                                    <div class="feed-element">
	                                                        <div class="media-body ">
	                                                            <small class="pull-right text-navy">${msg.api_example}</small>
	                                                            <strong>${msg.api_reqData}</strong>
	                                                            <br>
	                                                            <small class="text-muted">
		                                                            <c:choose>
																		<c:when test="${not empty api.reqData}">${api.reqData}</c:when>
																		<c:otherwise><font color="gray">${msg.common_notInformation}</font></c:otherwise>
																	</c:choose>
	                                                            </small>
	                                                        </div>
	                                                        <br>
	                                                        <div class="media-body ">
	                                                            <small class="pull-right text-navy">${msg.api_example}</small>
	                                                            <strong>${msg.api_resData}</strong>
	                                                            <br>
	                                                            <small class="text-muted">
	                                                            	<c:choose>
																		<c:when test="${not empty api.resData}">${api.resData}</c:when>
																		<c:otherwise><font color="gray">${msg.common_notInformation}</font></c:otherwise>
																	</c:choose>
	                                                            </small>
	                                                        </div>
	                                                    </div>
	                                                </div>
	                                            </div>
	                                            <div class="tab-pane" id="tab-2">
	                                                <table class="table table-striped">
	                                                    <thead>
	                                                        <tr>
	                                                            <th>${msg.api_param_key}</th>
	                                                            <th>${msg.api_param_name}</th>
	                                                            <th>${msg.api_param_must}</th>
	                                                            <th>${msg.api_param_defValue}</th>
	                                                            <th>${msg.api_param_dataType}</th>
	                                                            <th>${msg.common_remarks}</th>
	                                                        </tr>
	                                                    </thead>
	                                                    <tbody>
	                                                    	<c:forEach items="${api.reqHeaders}" var="reqHeader">
		                                                        <tr>
		                                                            <td>${reqHeader.value.key}</td>
		                                                            <td>${reqHeader.value.name}</td>
		                                                            <td>${reqHeader.value.must}</td>
		                                                            <td>
		                                                            	<c:choose>
																			<c:when test="${not empty reqHeader.value.defValue}">${reqHeader.value.defValue}</c:when>
																			<c:otherwise><font color="gray">——</font></c:otherwise>
																		</c:choose>
		                                                            </td>
		                                                            <td>${reqHeader.value.dataType}</td>
		                                                            <td>
		                                                            	<c:choose>
																			<c:when test="${not empty reqHeader.value.remarks}">${reqHeader.value.remarks}</c:when>
																			<c:otherwise><font color="gray">${msg.common_notInformation}</font></c:otherwise>
																		</c:choose>
		                                                            </td>
		                                                        </tr>
	                                                        </c:forEach>
	                                                    </tbody>
	                                                </table>
	                                            </div>
	                                            <div class="tab-pane" id="tab-3">
	                                                <table class="table table-striped">
	                                                    <thead>
	                                                        <tr>
	                                                            <th>${msg.api_param_key}</th>
	                                                            <th>${msg.api_param_name}</th>
	                                                            <th>${msg.api_param_must}</th>
	                                                            <th>${msg.api_param_defValue}</th>
	                                                            <th>${msg.api_param_dataType}</th>
	                                                            <th>${msg.common_remarks}</th>
	                                                        </tr>
	                                                    </thead>
	                                                    <tbody>
	                                                    	<c:forEach items="${api.resHeaders}" var="resHeader">
		                                                        <tr>
		                                                            <td>${resHeader.value.key}</td>
		                                                            <td>${resHeader.value.name}</td>
		                                                            <td>${resHeader.value.must}</td>
		                                                            <td>
		                                                            	<c:choose>
																			<c:when test="${not empty resHeader.value.defValue}">${resHeader.value.defValue}</c:when>
																			<c:otherwise><font color="gray">——</font></c:otherwise>
																		</c:choose>
		                                                            </td>
		                                                            <td>${resHeader.value.dataType}</td>
		                                                            <td>
		                                                            	<c:choose>
																			<c:when test="${not empty resHeader.value.remarks}">${resHeader.value.remarks}</c:when>
																			<c:otherwise><font color="gray">${msg.common_notInformation}</font></c:otherwise>
																		</c:choose>
		                                                            </td>
		                                                        </tr>
	                                                        </c:forEach>
	                                                    </tbody>
	                                                </table>
	                                            </div>
	                                        </div>
	                                    </div>
	                                </div>
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

