layui.use([ 'jquery', 'form', 'table', 'laydate', 'layer', 'element' ],
		function() {

			var $ = layui.$;
			var form = layui.form;
			var table = layui.table;
			var laydate = layui.laydate;
			var layer = layui.layer;

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
			/** 获取选中行单行学号 */
			function checkNum(num) {
				var data = table.checkStatus(num).data;
				if (data.length < 1) {
					layer.msg('请选择一条记录！', {
						icon : 5
					});
					return;
				} else if (data.length > 1) {
					layer.msg('只能选择一条记录！', {
						icon : 5
					});
					return;
				}
				else
					return data[0].studentnumber;
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
							$('.layui-laypage-btn', window.parent.document)
									.click();
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
							$('.layui-laypage-btn', window.parent.document)
									.click();
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
			
		/*	// 删除
			$('#deleteAdmintrators').click(function() {
				var ids = checkMultiple('tableLanting');
				if (ids) {
					layer.confirm('您确定要删除所选数据吗？', {
						title : '提示',
						closeBtn : 0,
						icon : 0
					}, function(index) {
						$.ajax({
							url : '/admin/index/deleteAdministrators',
							type : 'post',
							data : {
								ids : ids
							},
							dataType : 'json',
							beforeSend : function() {
								layer.load(2);
							},
							success : function(data) {
								layer.closeAll('loading');
								if (data.status == '0') {
									$('.layui-laypage-btn').click();
									layer.msg(data.msg, {
										icon : 6
									});
								} else if (data.status == '-1') {
									layer.msg(data.msg, {
										icon : 5
									});
								} else if (data.status == 'over') {
									top.location = data.url;
								}
							}
						});
						layer.close(index);
					});
				}
			});*/
			// 下载模板
			$('#expStudent').click(function() {
				location = '/admin/lantingview/download';
			});
			// 导入Excel
			var uploader = WebUploader.create({
				auto : true,
				swf : '/static/js/Uploader.swf',
				server : '/admin/lantingview/imp',
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
					$('#queryfoods').click();
					layer.msg(response.msg, {
						icon : 6
					});
				} else if (response.status == 'fail') {
					layer.open({
						type : 2,
						area : [ '800px', '600px' ],
						title : '错误信息',
						content : '/admin/lantingview/err'
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

			/** 后台成绩信息页面 */
			$('#addScore').click(function() {
				layer.open({
					type : 2,
					area : [ '800px', '580px' ],
					title : '增加学生成绩信息',
					content : '/admin/scoreput/addScore'
				});
			});
			// 增加成绩提交
			form.on('submit(saveScore_1)', function(data) {
				// var s= $('#is_show option:selected').text();
				// alert(s+"sssssssdwdwasssssssss");
				$.ajax({
					url : '/admin/scoreput/saveScore',
					type : 'post',
					data : data.field,
					dataType : 'json',
					beforeSend : function() {
						layer.load(0);
					},
					success : function(data) {
						layer.closeAll('loading');
						if (data.status == '0') {
							$('.layui-laypage-btn', window.parent.document)
									.click();
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
			// 删除成绩
			$('#deleteScore_1').click(function() {
				var ids = checkMultiple('tableLanting');
				if (ids) {
					layer.confirm('您确定要删除所选数据吗？', {
						title : '提示',
						closeBtn : 0,
						icon : 0
					}, function(index) {
						$.ajax({
							url : '/admin/scoreput/deleteScore',
							type : 'post',
							data : {
								ids : ids
							},
							dataType : 'json',
							beforeSend : function() {
								layer.load(2);
							},
							success : function(data) {
								layer.closeAll('loading');
								if (data.status == '0') {
									$('.layui-laypage-btn').click();
									layer.msg(data.msg, {
										icon : 6
									});
								} else if (data.status == '-1') {
									layer.msg(data.msg, {
										icon : 5
									});
								} else if (data.status == 'over') {
									top.location = data.url;
								}
							}
						});
						layer.close(index);
					});
				}
			});
			// 查询成绩信息
			form.on('submit(queryScore)', function(data) {
				table.reload('tableLanting', {
					where : data.field,
					done : function(res, curr, count) {
						if (res.status == 'over') {
							top.location = res.msg;
						}
					}
				});
				return false;
			});
			// 编辑成绩信息
			$('#editScore').click(function() {
				var id = checkSingle('tableLanting');
				if (id) {
					layer.open({
						type : 2,
						area : [ '700px', '500px' ],
						title : '修改学生成绩信息',
						content : '/admin/scoreput/editPage?id=' + id
					});
				}
			});
			// 成绩编辑提交
			form.on('submit(editsaveScore_1)', function(data) {
				$.ajax({
					url : '/admin/scoreput/editScore',
					type : 'post',
					data : data.field,
					dataType : 'json',
					beforeSend : function() {
						layer.load(2);
					},
					success : function(data) {
						layer.closeAll('loading');
						if (data.status == '0') {
							$('.layui-laypage-btn', window.parent.document)
									.click();
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
			// 下载成绩模板
			$('#expScore').click(function() {
				location = '/admin/scoreput/downloadScore';
			});

			// 导入成绩Excel
			var uploader = WebUploader.create({
				auto : true,
				swf : '/static/js/Uploader.swf',
				server : '/admin/scoreput/imp_1',
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
					$('#queryScore').click();
					layer.msg(response.msg, {
						icon : 6
					});
				} else if (response.status == 'fail') {
					layer.open({
						type : 2,
						area : [ '800px', '700px' ],
						title : '错误信息',
						content : '/admin/scoreput/err'
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
			/** 后台成绩管理页面 */
			// 查询成绩管理信息
			form.on('submit(queryScoreMan)', function(data) {
				table.reload('tableLanting', {
					where : data.field,
					done : function(res, curr, count) {
						if (res.status == 'over') {
							top.location = res.msg;
						}
					}
				});
				return false;
			});
			// 删除成绩管理
			$('#deleteScoreMan').click(function() {
				var ids = checkMultiple('tableLanting');
				if (ids) {
					layer.confirm('您确定要删除所选数据吗？', {
						title : '提示',
						closeBtn : 0,
						icon : 0
					}, function(index) {
						$.ajax({
							url : '/admin/scoreput/deleteScore',
							type : 'post',
							data : {
								ids : ids
							},
							dataType : 'json',
							beforeSend : function() {
								layer.load(2);
							},
							success : function(data) {
								layer.closeAll('loading');
								if (data.status == '0') {
									$('.layui-laypage-btn').click();
									layer.msg(data.msg, {
										icon : 6
									});
								} else if (data.status == '-1') {
									layer.msg(data.msg, {
										icon : 5
									});
								} else if (data.status == 'over') {
									top.location = data.url;
								}
							}
						});
						layer.close(index);
					});
				}
			});
			// 下载成绩单
			$('#expScoreMan').click(function() {
				location = '/admin/scoreman/download';
			});
			/** 后台年级代码添加 */
			$('#addCode').click(function() {
				layer.open({
					type : 2,
					area : [ '500px', '300px' ],
					title : '增加年级代码信息',
					content : '/admin/scoreput/addCode'
				});
			});
			form.on('submit(saveCode)', function(data) {
				$.ajax({
					url : '/admin/scoreput/saveCode',
					type : 'post',
					data : data.field,
					dataType : 'json',
					beforeSend : function() {
						layer.load(2);
					},
					success : function(data) {
						layer.closeAll('loading');
						if (data.status == '0') {
							$('.layui-laypage-btn', window.parent.document)
									.click();
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

		});