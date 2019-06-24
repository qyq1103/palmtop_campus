layui.use([ 'jquery', 'form', 'table', 'laydate', 'layer', 'element', 'upload' ], function() {

	var $ = layui.$;
	var form = layui.form;
	var table = layui.table;
	var laydate = layui.laydate;
	var layer = layui.layer;
	var upload = layui.upload;

	/** 自定义验证规则 */
	form.verify({
		account : [ /[\S]+/, '请输入账号' ],
		password : [ /[\S]+/, '请输入密码' ],
		password3_15 : [ /^\w{3,15}$/, '请输入3-15位字母、数字或 _' ],

	});

	/** 弹出层默认参数 */
	layer.config({
		skin : 'layui-layer-molv',
		resize : false
	});

	/** 登录页面 */
	// 默认获取焦点
	$('.login-box input[name = "account"]').focus();
	// 立即登录
	form.on('submit(login)', function(data) {
		$.ajax({
			url : '/admin/login',
			type : 'post',
			data : data.field,
			dataType : 'json',
			beforeSend : function() {
				layer.load(2);
			},
			success : function(data) {
				layer.closeAll();
				if (data.status == 'ok') {
					top.location = data.msg;
				} else if (data.status == 'fail') {
					layer.msg(data.msg, {
						icon : 5
					});
				}
			}
		});
		return false;
	});
	/** 获取选中行单行id */
	function checkSingle(id) {
		var data = table.checkStatus(id).data;
		if (data.length < 1) {
			layer.msg('请选择一条记录！', {
				icon : 5
			});
			return;
		}
		if (data.length > 1) {
			layer.msg('只能选择一条记录！', {
				icon : 5
			});
			return;
		}
		return data[0].id;
	}
	/** 获取选中行多行ids */
	function checkMultiple(id) {
		var data = table.checkStatus(id).data;
		if (data.length < 1) {
			layer.msg('请选择一条或多条记录！', {
				icon : 5
			});
			return;
		}
		var ids = '';
		for ( var i in data) {
			ids += (data[i].id) + ',';
		}
		return ids;
	}
	/**
	 * ================================以下内容为管理员的信息处理===================================
	 */
	// 增加管理员提交
	form.on('submit(saveAdministrators)', function(data) {
		$.ajax({
			url : '/admin/index/saveAdministrators',
			type : 'post',
			data : data.field,
			dataType : 'json',
			beforeSend : function() {
				layer.load(2);
			},
			success : function(data) {
				layer.closeAll('loading');
				if (data.status == '0') {
					$('.layui-laypage-btn', window.parent.document).click();
					parent.layer.msg(data.msg, {
						icon : 6
					});
					parent.layer.closeAll('iframe');
				} else if (data.status == '-1') {
					layer.msg(data.msg, {
						icon : 5
					});
				} else if (data.status == 'over') {
					top.location = data.msg;
				}
			}
		});
		return false;
	});
	// 编辑提交
	form.on('submit(editAdministrators)', function(data) {
		$.ajax({
			url : '/admin/index/editAdministrators',
			type : 'post',
			data : data.field,
			dataType : 'json',
			beforeSend : function() {
				layer.load(2);
			},
			success : function(data) {
				layer.closeAll('loading');
				if (data.status == '0') {
					$('.layui-laypage-btn', window.parent.document).click();
					parent.layer.msg(data.msg, {
						icon : 6
					});
					parent.layer.closeAll('iframe');
				} else if (data.status == '-1') {
					layer.msg(data.msg, {
						icon : 5
					});
				} else if (data.status == 'over') {
					top.location = data.msg;
				}
			}
		});
		return false;
	});
	/** ==================================以下为教师信息管理的处理================================ */
	// 增加教师信息提交
	form.on('submit(saveTeachers)', function(data) {
		$.ajax({
			url : '/admin/teacher/saveTeachers',
			type : 'post',
			data : data.field,
			dataType : 'json',
			beforeSend : function() {
				layer.load(2);
			},
			success : function(data) {
				layer.closeAll('loading');
				if (data.status == '0') {
					$('.layui-laypage-btn', window.parent.document).click();
					parent.layer.msg(data.msg, {
						icon : 6
					});
					parent.layer.closeAll('iframe');
				} else if (data.status == '-1') {
					layer.msg(data.msg, {
						icon : 5
					});
				} else if (data.status == 'over') {
					top.location = data.msg;
				}
			}
		});
		return false;
	});
	// 编辑提交
	form.on('submit(editTeachers)', function(data) {
		$.ajax({
			url : '/admin/teacher/editTeachers',
			type : 'post',
			data : data.field,
			dataType : 'json',
			beforeSend : function() {
				layer.load(2);
			},
			success : function(data) {
				layer.closeAll('loading');
				if (data.status == '0') {
					$('.layui-laypage-btn', window.parent.document).click();
					parent.layer.msg(data.msg, {
						icon : 6
					});
					parent.layer.closeAll('iframe');
				} else if (data.status == '-1') {
					layer.msg(data.msg, {
						icon : 5
					});
				} else if (data.status == 'over') {
					top.location = data.msg;
				}
			}
		});
		return false;
	});
	// 下载模板
	$('#expTeacher').click(function() {
		location = '/admin/teacher/downloadTeachers';
	});
	// 导入Excel
	var uploader = WebUploader.create({
		auto : true,
		swf : '/static/js/Uploader.swf',
		server : '/admin/teacher/impteacher',
		pick : {
			id : '#impTeacher',
			multiple : false
		},
		accept : {
			extensions : 'xls,xlsx'
		},
		duplicate : true
	});
	uploader.on('uploadBeforeSend', function(object, data, headers) {
		headers['X-Requested-With'] = 'XMLHttpRequest';
		layer.load(2);
	});
	uploader.on('uploadSuccess', function(file, response) {
		layer.closeAll();
		if (response.status == 'ok') {
			$('#queryTeacher', window.parent.document).click();
			layer.msg(response.msg, {
				icon : 6
			});
		} else if (response.status == 'fail') {
			layer.open({
				type : 2,
				area : [ '800px', '600px' ],
				title : '错误信息',
				content : '/admin/teacher/err'
			});
		} else if (response.status == 'over') {
			top.location = response.msg;
		}
	});
	uploader.on('error', function(type) {
		if (type == 'Q_TYPE_DENIED') {
			layer.msg('文件格式错误！', {
				icon : 5
			});
		}
	});
	// 查询教师信息
	form.on('submit(queryTeacher)', function(data) {
		table.reload('teacher', {
			where : data.field,
			done : function(res, curr, count) {
				if (res.status == 'over') {
					top.location = res.msg;
				}
			}
		});
		return false;
	});

	/**
	 * ==================================
	 * 以下为学生信息管理的处理=================================
	 */
	// 增加学生信息提交
	form.on('submit(saveStudents)', function(data) {
		$.ajax({
			url : '/admin/student/saveStudents',
			type : 'post',
			data : data.field,
			dataType : 'json',
			beforeSend : function() {
				layer.load(2);
			},
			success : function(data) {
				layer.closeAll('loading');
				if (data.status == '0') {
					$('.layui-laypage-btn', window.parent.document).click();
					parent.layer.msg(data.msg, {
						icon : 6
					});
					parent.layer.closeAll('iframe');
				} else if (data.status == '-1') {
					layer.msg(data.msg, {
						icon : 5
					});
				} else if (data.status == 'over') {
					top.location = data.msg;
				}
			}
		});
		return false;
	});
	// 编辑提交
	form.on('submit(editStudents)', function(data) {
		$.ajax({
			url : '/admin/student/editStudents',
			type : 'post',
			data : data.field,
			dataType : 'json',
			beforeSend : function() {
				layer.load(2);
			},
			success : function(data) {
				layer.closeAll('loading');
				if (data.status == '0') {
					$('.layui-laypage-btn', window.parent.document).click();
					parent.layer.msg(data.msg, {
						icon : 6
					});
					parent.layer.closeAll('iframe');
				} else if (data.status == '-1') {
					layer.msg(data.msg, {
						icon : 5
					});
				} else if (data.status == 'over') {
					top.location = data.msg;
				}
			}
		});
		return false;
	});
	// 下载模板
	$('#expStudent').click(function() {
		location = '/admin/student/downloadStudents';
	});
	// 导入Excel
	var uploader = WebUploader.create({
		auto : true,
		swf : '/static/js/Uploader.swf',
		server : '/admin/student/impstudent',
		pick : {
			id : '#impStudent',
			multiple : false
		},
		accept : {
			extensions : 'xls,xlsx'
		},
		duplicate : true
	});
	uploader.on('uploadBeforeSend', function(object, data, headers) {
		headers['X-Requested-With'] = 'XMLHttpRequest';
		layer.load(2);
	});
	uploader.on('uploadSuccess', function(file, response) {
		layer.closeAll();
		if (response.status == 'ok') {
			$('#queryStudent', window.parent.document).click();
			layer.msg(response.msg, {
				icon : 6
			});
		} else if (response.status == 'fail') {
			layer.open({
				type : 2,
				area : [ '800px', '600px' ],
				title : '错误信息',
				content : '/admin/student/err'
			});
		} else if (response.status == 'over') {
			top.location = response.msg;
		}
	});
	uploader.on('error', function(type) {
		if (type == 'Q_TYPE_DENIED') {
			layer.msg('文件格式错误！', {
				icon : 5
			});
		}
	});
	// 查询学生信息
	form.on('submit(queryStudent)', function(data) {
		table.reload('student', {
			where : data.field,
			done : function(res, curr, count) {
				if (res.status == 'over') {
					top.location = res.msg;
				}
			}
		});
		return false;
	});

	/**
	 * ==================================以下为成绩管理的处理=================================
	 */

	// 编辑提交
	form.on('submit(editScore)', function(data) {
		$.ajax({
			url : '/admin/score/editScore',
			type : 'post',
			data : data.field,
			dataType : 'json',
			beforeSend : function() {
				layer.load(2);
			},
			success : function(data) {
				layer.closeAll('loading');
				if (data.status == '0') {
					$('.layui-laypage-btn', window.parent.document).click();
					parent.layer.msg(data.msg, {
						icon : 6
					});
					parent.layer.closeAll('iframe');
				} else if (data.status == '-1') {
					layer.msg(data.msg, {
						icon : 5
					});
				} else if (data.status == 'over') {
					top.location = data.msg;
				}
			}
		});
		return false;
	});
	// 下载模板
	$('#expScore').click(function() {
		location = '/admin/score/downloadScore';
	});
	// 导入Excel
	var uploader = WebUploader.create({
		auto : true,
		swf : '/static/js/Uploader.swf',
		server : '/admin/score/impscore',
		pick : {
			id : '#impScore',
			multiple : false
		},
		accept : {
			extensions : 'xls,xlsx'
		},
		duplicate : true
	});
	uploader.on('uploadBeforeSend', function(object, data, headers) {
		headers['X-Requested-With'] = 'XMLHttpRequest';
		layer.load(2);
	});
	uploader.on('uploadSuccess', function(file, response) {
		layer.closeAll();
		if (response.status == 'ok') {
			$('#queryScore', window.parent.document).click();
			layer.msg(response.msg, {
				icon : 6
			});
		} else if (response.status == 'fail') {
			layer.open({
				type : 2,
				area : [ '800px', '600px' ],
				title : '错误信息',
				content : '/admin/score/err'
			});
		} else if (response.status == 'over') {
			top.location = response.msg;
		}
	});
	uploader.on('error', function(type) {
		if (type == 'Q_TYPE_DENIED') {
			layer.msg('文件格式错误！', {
				icon : 5
			});
		}
	});
	// 查询成绩
	form.on('submit(queryScore)', function(data) {
		table.reload('score', {
			where : data.field,
			done : function(res, curr, count) {
				if (res.status == 'over') {
					top.location = res.msg;
				}
			}
		});
		return false;
	});
	/**
	 * ================================以下内容为校园咨询的信息处理===================================
	 */
	// 增加提交
	form.on('submit(saveAdvisory)', function(data) {
		$.ajax({
			url : '/admin/advisory/saveAdvisory',
			type : 'post',
			data : data.field,
			dataType : 'json',
			beforeSend : function() {
				layer.load(2);
			},
			success : function(data) {
				layer.closeAll('loading');
				if (data.status == '0') {
					$('.layui-laypage-btn', window.parent.document).click();
					parent.layer.msg(data.msg, {
						icon : 6
					});
					parent.layer.closeAll('iframe');
				} else if (data.status == '-1') {
					layer.msg(data.msg, {
						icon : 5
					});
				} else if (data.status == 'over') {
					top.location = data.msg;
				}
			}
		});
		return false;
	});
	// 编辑提交
	form.on('submit(editAdvisory)', function(data) {
		$.ajax({
			url : '/admin/advisory/editAdvisory',
			type : 'post',
			data : data.field,
			dataType : 'json',
			beforeSend : function() {
				layer.load(2);
			},
			success : function(data) {
				layer.closeAll('loading');
				if (data.status == '0') {
					$('.layui-laypage-btn', window.parent.document).click();
					parent.layer.msg(data.msg, {
						icon : 6
					});
					parent.layer.closeAll('iframe');
				} else if (data.status == '-1') {
					layer.msg(data.msg, {
						icon : 5
					});
				} else if (data.status == 'over') {
					top.location = data.msg;
				}
			}
		});
		return false;
	});
	/**
	 * ================================
	 * 以下内容为通知的信息处理===================================
	 */
	// 增加提交
	form.on('submit(saveNotices)', function(data) {
		$.ajax({
			url : '/admin/notices/saveNotices',
			type : 'post',
			data : data.field,
			dataType : 'json',
			beforeSend : function() {
				layer.load(2);
			},
			success : function(data) {
				layer.closeAll('loading');
				if (data.status == '0') {
					$('.layui-laypage-btn', window.parent.document).click();
					parent.layer.msg(data.msg, {
						icon : 6
					});
					parent.layer.closeAll('iframe');
				} else if (data.status == '-1') {
					layer.msg(data.msg, {
						icon : 5
					});
				} else if (data.status == 'over') {
					top.location = data.msg;
				}
			}
		});
		return false;
	});
	// 编辑提交
	form.on('submit(editNotices)', function(data) {
		$.ajax({
			url : '/admin/notices/editNotices',
			type : 'post',
			data : data.field,
			dataType : 'json',
			beforeSend : function() {
				layer.load(2);
			},
			success : function(data) {
				layer.closeAll('loading');
				if (data.status == '0') {
					$('.layui-laypage-btn', window.parent.document).click();
					parent.layer.msg(data.msg, {
						icon : 6
					});
					parent.layer.closeAll('iframe');
				} else if (data.status == '-1') {
					layer.msg(data.msg, {
						icon : 5
					});
				} else if (data.status == 'over') {
					top.location = data.msg;
				}
			}
		});
		return false;
	});
	/**
	 * ================================
	 * 以下内容为新闻的信息处理===================================
	 */
	// 增加提交
	form.on('submit(saveNews)', function(data) {
		$.ajax({
			url : '/admin/news/saveNews',
			type : 'post',
			data : data.field,
			dataType : 'json',
			beforeSend : function() {
				layer.load(2);
			},
			success : function(data) {
				layer.closeAll('loading');
				if (data.status == '0') {
					$('.layui-laypage-btn', window.parent.document).click();
					parent.layer.msg(data.msg, {
						icon : 6
					});
					parent.layer.closeAll('iframe');
				} else if (data.status == '-1') {
					layer.msg(data.msg, {
						icon : 5
					});
				} else if (data.status == 'over') {
					top.location = data.msg;
				}
			}
		});
		return false;
	});
	// 普通图片上传
	var uploadInst = upload.render({
		elem : '#upload_btn',
		url : '/admin/news/uploadImage',
		multiple : true,
		// auto:false,
		// accept:'file',
		// exts:'jpg|png',
		before : function(obj) {
			// 预读本地文件示例，不支持ie8
			obj.preview(function(index, file, result) {
				$('#upload_img').attr('src', result); // 图片链接（base64）
				console.log(result);
				document.getElementById('img_base64').value = result;
			});
		},
		done : function(res) {
			// 如果上传失败
			if (res.code > 0) {
				return layer.msg('添加失败');
			}
			// 上传成功
			document.getElementById('img_url').value = res.url;
		},
		error : function() {
			// 演示失败状态，并实现重传
			var demoText = $('#demoText');
			demoText.html('<span style="color: #FF5722;">添加失败</span> <a class="layui-btn layui-btn-xs demo-reload">重试</a>');
			demoText.find('.demo-reload').on('click', function() {
				uploadInst.upload();
			});
		}
	});

});