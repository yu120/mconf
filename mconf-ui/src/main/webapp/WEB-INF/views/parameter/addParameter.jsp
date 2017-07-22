<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../library/master.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>${msg.menu_governor_title} - ${menu_governor_parameter}</title>
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
                            <h5>${msg.menu_governor_parameter}</h5>
                            <div class="ibox-tools">
                                <a class="dropdown-toggle" data-toggle="dropdown" href="#"><i class="fa fa-wrench"></i></a>
                                <ul class="dropdown-menu dropdown-user">
                                    <li><a href="#">选项 01</a></li>
                                    <li><a href="#">选项 02</a></li>
                                </ul>
                            </div>
                        </div>
                        <div class="ibox-content">
	                        <form method="post" class="form-horizontal" action="${ctx}/parameter/addParameter">
	                            <div class="form-group">
	                                <label class="col-sm-2 control-label">参数KEY</label>
	                                <div class="col-sm-10">
	                                    <input type="text" name="key" placeholder="请输入参数KEY…" class="form-control">
	                                </div>
	                            </div>
	                            <div class="hr-line-dashed"></div>
	                            <div class="form-group">
	                                <label class="col-sm-2 control-label">参数标题</label>
	                                <div class="col-sm-10">
	                                    <input type="text" name="title" placeholder="请输入参数标题…" class="form-control">
	                                </div>
	                            </div>
	                            <div class="hr-line-dashed"></div>
	                            <div class="form-group">
	                                <label class="col-sm-2 control-label">参数值类型</label>
	                                <div class="col-sm-10">
	                                    <select class="form-control m-b" name="type">
	                                        <option>string</option>
	                                        <option>int</option>
	                                        <option>long</option>
	                                        <option>short</option>
	                                        <option>float</option>
	                                        <option>double</option>
	                                        <option>byte</option>
	                                        <option>bigdecimal</option>
	                                        <option>boolean</option>
	                                    </select>
	                                </div>
	                            </div>
	                            <div class="hr-line-dashed"></div>
	                            <div class="form-group">
	                                <label class="col-sm-2 control-label">参数值长度</label>
	                                <div class="col-sm-10">
	                                    <input type="text" placeholder="请输入参数值长度…" name="length" class="form-control">
	                                </div>
	                            </div>
	                            <div class="hr-line-dashed"></div>
	                            <div class="form-group">
	                                <label class="col-sm-2 control-label">是否必须</label>
	
	                                <div class="col-sm-10">
	                                    <select class="form-control m-b" name="status">
	                                        <option>必须</option>
	                                        <option>非必须</option>
	                                    </select>
	                                </div>
	                            </div>
	                            <div class="hr-line-dashed"></div>
	                            <div class="form-group">
	                                <label class="col-sm-2 control-label">服务中心</label>
	                                <div class="col-sm-10">
	                                    <select class="form-control m-b" name="sdc">
	                                        <option>Micro Service</option>
	                                    </select>
	                                </div>
	                            </div>
	                            <div class="hr-line-dashed"></div>
	                            <div class="form-group">
	                                <label class="col-sm-2 control-label">备注信息</label>
	                                <div class="col-sm-10">
	                                    <textarea id="ccomment" name="remarks" placeholder="请输入备注信息…" class="form-control" required="" aria-required="true"></textarea>
	                                </div>
	                            </div>
	                            <div class="hr-line-dashed"></div>
	                            <div class="form-group">
	                                <div class="col-sm-4 col-sm-offset-2">
	                                    <button class="btn btn-primary" type="submit">确认添加</button>
	                                    <button class="btn btn-white" type="submit">取消</button>
	                                </div>
	                            </div>
	                        </form>
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

