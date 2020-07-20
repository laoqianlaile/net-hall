(function(cst) {
	var fj_dialog = null;
	var isFormInputChange = false;
	var createPicArea = function(data, options, $up_btn) {
		var editable = options.editable;
		var isMultiple = options.isMultiple;
		if (typeof (options.editable) == 'undefined')
			editable = true;
		var type = options.type;
		if ($up_btn.find(".badge").length == 0)
			$up_btn.html($up_btn.html() + "<span class='badge'>" + data.length
					+ "</span>");
		else
			$up_btn.find(".badge").text(data.length);
		$up_btn.data("wjsl", data.length);
		var infoTag = editable ? 'textarea' : 'span';
		var createFile = function(vf) {
			try {
				return '<li data-fid="'
						+ vf.fileid
						+ '">'
						+ (editable ? '<button type="button" class="close" title="删除此附件"><span>&times;</span></button>'
								: '')
						+ '<a class="_'
						+ vf.fn.substr(vf.fn.lastIndexOf('.') + 1
								|| vf.fn.length) + '" href="' + ydl.contexPath
						+ '/fileInteractive/getfile/' + type + '/' + vf.fileid
						+ '"></a><p><' + infoTag + ' data-original="'
						+ vf.remark + '">' + vf.remark + '</' + infoTag
						+ '></p></li>';
			} catch (e) {
				ydl.alert(e)
				return "error"
			}

		};
		var aryHtml = []
		for (var i = 0; i < data.length; i++) {
			aryHtml.push(createFile(data[i]));
		}
		var contentAry = [];
		contentAry
				.push("<div id='attachment_item_new' class='tab-pane active' style='margin-left: 10px'><ul>")
		contentAry.push(aryHtml.join(""));
		if (editable) {
			if (data.length > 0 && !isMultiple) {

			} else {
				contentAry.push("<li>");
				contentAry
						.push("<a class='add'   href=\"#\" data-itemid='00001'></a>");
				contentAry.push("<p><span>点击加号添加附件</span></p>");
				contentAry.push("</li>");
			}
		}
		contentAry.push("</ul></div>");

		$('#flow_attachmentDialog').html("<div class=\"tab-content\" ></div>");
		$('#flow_attachmentDialog .tab-content').html(contentAry.join(""));

	}// end createPicArea
	var createPicAreaEvent = function(options, $up_btn) {
		var type = options.type;

		$('#flow_attachmentDialog').off("click", "**");
		$('#flow_attachmentDialog').off("change", "**");
		$('#flow_attachmentDialog').on('click', '.add', function() {
			var acceptType = $up_btn.data("acceptType");
			$('#attachment_form input').attr("accept", acceptType);
			// 选择文件
			attachment_form.reset();
			$('#attachment_form input').data("up_btn", $up_btn);
			$('#attachment_form input').click();
		}).on(
				'click',
				'button',
				function() {
					// 选择文件
					var $li = $(this).closest('li');
					var fid = $li.data('fid');

					$.ajax(ydl.contexPath + '/fileInteractive/delete/' + type
							+ '/' + fid, {
						method : 'GET',
						dataType : 'json',
						success : function(ret) {
							if (ret.returnCode == 0) {
								var wjsl = $up_btn.data("wjsl");
								wjsl--;
								// 删除成功，减少文件数
								var $badge = $up_btn.find(' .badge');
								$badge.text(wjsl);
								$up_btn.data("wjsl", wjsl);
								// 移除页面元素
								$li.remove();
								if (wjsl == 0 && fj_dialog)
									fj_dialog.dialog("close");

							} else {
								ydl.alert({
									'icon' : 'error',
									'message' : '删除附件出错：' + ret.message
								});
							}
						},
						error : function(jqXHR, textStatus, errorThrown) {
							ydl.alert({
								'icon' : 'error',
								'message' : '删除附件出错：' + textStatus + ' - '
										+ errorThrown
							});
						}
					});// end ajax
				})
		// 修改备注
		.on(
				'change',
				'textarea',
				function() {
					var $text = $(this);
					var fid = $text.closest('li').data('fid');
					$.ajax(ydl.contexPath + '/fileInteractive/modi/' + type
							+ '/' + fid, {
						method : 'POST',
						dataType : 'json',
						data : {
							remark : this.value
						},
						success : function(ret) {
							if (ret.returnCode != 0) {
								$text.val($text.data('original'));
								ydl.alert({
									'icon' : 'error',
									'message' : '修改备注出错：' + ret.message
								});
							}
						},
						error : function(jqXHR, textStatus, errorThrown) {
							$text.val($text.data('original'));
							ydl.alert({
								'icon' : 'error',
								'message' : '修改备注出错：' + textStatus + ' - '
										+ errorThrown
							});
						}
					});// end ajax
				})
		// 备注不允许回车和双引号
		.on(
				'keydown',
				'textarea',
				function(event) {
					if (event.which === ydl.common.keys.Enter
							|| event.which === ydl.common.keys.Quote) {
						return false;
					}
				});

	}

	var submitFileForm = function(options) {
		var type = options.type;
		var materialCode = options.materialCode;
		var isMultiple = options.isMultiple;
		var callback = options.callback;

		$('#attachment_form').ajaxSubmit(
				{
					dataType : 'json',
					async : true,
					success : function(ret) {
						if (ret.returnCode == '00000000') {
							var data = ret.data;
							var $up_btn = $('#attachment_form input').data(
									"up_btn");
							$up_btn.data("groupid", ret.groupId);
							$up_btn.data("wjsl", data.length);
							if (options.callback)
								options.callback(ret.groupId, data, $up_btn);
							var $up_btn_tmp = $('#attachment_form input').data(
									"up_btn")

							createPicArea(data, options, $up_btn_tmp);
							createPicAreaEvent(options, $up_btn_tmp);
							sendflag = true;
						} else {
							ydl.alert({
								'icon' : 'warn',
								'message' : ret.message
							});
						}
					}
				});
	}// end submitFileForm

	var list_ajax_callback = function(options, $up_btn, jsonData) {
		if (jsonData.returnCode == "00000000") {
			var data = jsonData.data
			var $dialog = fj_dialog
			if (!$dialog) {
				$dialog = $(
						'<div id=flow_attachmentDialog class="container-fluid flow_attachment"></div>')
						.dialog({
							title : options.title ? options.title : "附件管理11",
							close : true,
							size : 'lg',
							height : 100,
							hidden : function() {
								$dialog.remove();
							}
						});
				fj_dialog = $dialog;

			}
			$dialog.dialog('open');
			setTimeout(function() {
				createPicArea(data, options, $up_btn);
				createPicAreaEvent(options, $up_btn);
			}, 200)

		}
	}// list_ajax_callback
	// ========================开始====
	$.fn.upfile = function(options) {
		var $up_btn = $(this);
		var type = options.type;
		var materialCode = options.materialCode;
		var isMultiple = options.isMultiple;
		var callback = options.callback;
		var acceptType = options.accept ? options.accept : "*.*"
		$up_btn.data("acceptType", acceptType);
		if (!$(this).data("groupid"))
			$(this).data("groupid", 0);
		$up_btn.data("wjsl", 0);
		var setSl = function() {
			var cnt = $up_btn.data("wjsl");
			if (!cnt)
				cnt = 0;
			if (cnt == 0)
				cnt = '';
			var html = $up_btn.html();
			if ($up_btn.find(".badge").length == 0) {
				$up_btn.html(html + "<span class='badge'>" + cnt + "</span>");
			} else {
				$up_btn.find(".badge").text(cnt);
			}
		}
		setSl();
		if (options.groupId) {
			$up_btn.data("groupid", groupId);
		}
		var groupId = $up_btn.data("groupid");
		if (!groupId)
			groupId = 0;
		if (groupId != 0) {
			var url = ydl.contexPath + '/fileInteractive/list/' + type + "/"
					+ groupId;
			$.ajax(url, {
				method : 'GET',
				dataType : 'json',
				data : {
					_r : (new Date()) + ""
				},
				success : function(jsonData) {
					$up_btn.data("wjsl", jsonData.data.length);
					setSl();
				}
			}); // end ajax

		}
		$(this).click(
				function() {
					var curGroupId = $up_btn.data("groupid");
					if (curGroupId == "")
						curGroupId = "0";
					var editable = options.editable;
					if (!editable && curGroupId == "0") {
						ydl.alert({
							'icon' : 'warn',
							'message' : '无上传权限'
						});
						return;
					}
					$('#attachment_form input').unbind("change").change(
							function() {
								submitFileForm(options);
								attachment_form.reset();
							});

					$('#attachment_form input').attr("multiple", isMultiple);
					$('#attachment_form input').attr("accept", acceptType);
					var postUrl = ydl.contexPath + '/fileInteractive/upFile/'
							+ type + "/" + materialCode + "/" + curGroupId;
					if (poolSelect)
						if (poolSelect["_POOLKEY"])
							postUrl += "?_POOLKEY=" + poolSelect["_POOLKEY"];
					$('#attachment_form').prop('action', postUrl);
					if (curGroupId == 0) {
						$('#attachment_form input').data("up_btn", $up_btn);
						attachment_form.reset();
						$('#attachment_form input').click();
					} else {
						var url = ydl.contexPath + '/fileInteractive/list/'
								+ type + "/" + curGroupId;
						$.ajax(url, {
							method : 'GET',
							dataType : 'json',
							data : {
								_r : (new Date()) + ""
							},
							success : function(jsonData) {
								list_ajax_callback(options, $up_btn, jsonData)
							}
						}); // end ajax

					}// end if
				});// $(this).click

		return this;
	}

	// ==========================主程序结束

})(cst);

(function(cst) {
	var fj_dialog_one = null;
	var isFormInputChange = false;
	var submitFileForm = function(e, options) {
		var files = e.target.files;
		var filename = "";
		if(files.length > 0){
			filename = files[0].name;
		}
		var type = options.type;
		var materialCode = options.materialCode;
		var callback = options.callback;
		$('#attachment_form').ajaxSubmit(
				{
					dataType : 'json',
					async : true,
					success : function(ret) {
						if (ret.returnCode == '00000000') {

							var $up_btn = $('#attachment_form input').data(
									"up_btn");
							if ($up_btn.find(".badge").length == 0)
								$up_btn.html($up_btn.html()
										+ "<span class='badge'>1</span>");
							else
								$up_btn.find(".badge").text(1);
							$up_btn.data("fileid", ret.fileid);
							sendflag = true;
							if (options.callback){
								options.callback(ret.fileid,filename, ret, $up_btn);
							}
						} else {
							ydl.alert({
								'icon' : 'warn',
								'message' : ret.message
							});
						}
					}
				});
	}// end submitFileForm
	var createPicArea = function(data, options) {

		var editable = options.editable
		if (typeof (options.editable) == 'undefined')
			editable = true;
		var type = options.type;
		var infoTag = editable ? 'textarea' : 'span';

		var createFile = function(vf) {
			try {
				return '<li data-fid="'
						+ vf.fileid
						+ '">'
						+ '<a class="_'
						+ vf.fn.substr(vf.fn.lastIndexOf('.') + 1
								|| vf.fn.length) + '" href="' + ydl.contexPath
						+ '/fileInteractiveOne/getfile/' + type + '/'
						+ vf.fileid + '"></a><p><' + infoTag
						+ ' data-original="' + vf.remark + '">' + vf.remark
						+ '</' + infoTag + '></p></li>';
			} catch (e) {
				ydl.alert(e)
				return "error"
			}

		};
		var aryHtml = []
		aryHtml.push(createFile(data));
		var contentAry = [];
		contentAry
				.push("<div id='attachment_item_new' class='tab-pane active' style='margin-left: 10px'><ul>")
		contentAry.push(aryHtml.join(""));
		contentAry.push("</ul></div>");
		$('#flow_attachmentDialog').html("<div class=\"tab-content\" ></div>");
		$('#flow_attachmentDialog .tab-content').html(contentAry.join(""));

	}// end createPicArea

	var createPicAreaEvent = function(options, $up_btn) {
		var type = options.type;
		$('#flow_attachmentDialog').off("click", "**");
		$('#flow_attachmentDialog').off("change", "**");
		$('#flow_attachmentDialog').on('click', '.add', function() {
			// 选择文件
			attachment_form.reset();
			$('#attachment_form input').data("up_btn", $up_btn);
			$('#attachment_form input').click();
		})
		// 修改备注
		.on(
				'change',
				'textarea',
				function() {
					var $text = $(this);
					var fid = $text.closest('li').data('fid');
					$.ajax(ydl.contexPath + '/fileInteractiveOne/modi/' + type
							+ '/' + fid, {
						method : 'POST',
						dataType : 'json',
						data : {
							remark : this.value
						},
						success : function(ret) {
							if (ret.returnCode != 0) {
								$text.val($text.data('original'));
								ydl.alert({
									'icon' : 'error',
									'message' : '修改备注出错：' + ret.message
								});
							}
						},
						error : function(jqXHR, textStatus, errorThrown) {
							$text.val($text.data('original'));
							ydl.alert({
								'icon' : 'error',
								'message' : '修改备注出错：' + textStatus + ' - '
										+ errorThrown
							});
						}
					});// end ajax
				})
		// 备注不允许回车和双引号
		.on(
				'keydown',
				'textarea',
				function(event) {
					if (event.which === ydl.common.keys.Enter
							|| event.which === ydl.common.keys.Quote) {
						return false;
					}
				});

	}// createPicAreaEvent

	var list_ajax_callback = function(options, $up_btn, jsonData) {
		if (jsonData.returnCode == "00000000") {
			var data = jsonData.data
			var $dialog = fj_dialog_one
			if (!$dialog) {
				$dialog = $(
						'<div id=flow_attachmentDialog class="container-fluid flow_attachment"></div>')
						.dialog({
							title : options.title ? options.title : "附件管理",
							close : true,
							size : 'lg',
							width : 200,
							hidden : function() {
								$dialog.remove();
							}
						});
				fj_dialog_one = $dialog;

			}
			$dialog.dialog('open');
			setTimeout(function() {
				createPicArea(jsonData.data, options)
				createPicAreaEvent(options, $up_btn);
			}, 200)

		}
	}// list_ajax_callback

	// ========================开始====
	//上传文件默认最大值为20MB
	var defaultSizeMax = 20 * 1024 * 1024;
	//上传文件默认最小值为0KB
	var defaultSizeMin = 0 ;	
	$.fn.upfileOne = function(options) {
		var $up_btn = $(this);
		var type = options.type;
		var materialCode = options.materialCode;
		var callback = options.callback;
		var acceptType = options.accept ? options.accept : "*.*";
		var sizeMax = options.sizeMax ? options.sizeMax:defaultSizeMax;
		var sizeMin = options.sizeMin ? options.sizeMin:defaultSizeMin;
		var fileId = $up_btn.data("fileid");
		if (fileId) {
			if ($up_btn.find(".badge").length == 0)
				$up_btn.html($up_btn.html() + "<span class='badge'>1</span>");
			else
				$up_btn.find(".badge").text(1);
		}
		$('#attachment_form input').attr("multiple", false);

		$up_btn.data("acceptType", acceptType);

		$(this).click(
				function() {
					$('#attachment_form input').unbind("change").change(
							function(e) {
								var fileSize = this.files[0].size;
								if(fileSize>(sizeMax * 1024 *1024)){
									ydl.alert({
										'icon' : 'warn',
										'message' : '上传文件大小不能超过'+sizeMax+'MB,请重新上传！'
									});
									return false;
								}else if(fileSize<sizeMin){
									ydl.alert({
										'icon' : 'warn',
										'message' : '上传文件大小不能小于'+sizeMin+'MB,请重新上传！'
									});
									return false;
								}else{
									submitFileForm(e, options);
								}
								
							});
					var fileId = $up_btn.data("fileid");
					var acceptType = $up_btn.data("acceptType");
					$('#attachment_form input').attr("accept", acceptType);
					if (!fileId) {
						var editable = options.editable;
						if (!editable) {
							ydl.alert({
								'icon' : 'warn',
								'message' : '无上传权限'
							});
							return;
						}
						var postUrl = ydl.contexPath
								+ '/fileInteractiveOne/upFile/' + type + "/"
								+ materialCode + "/";
						if (poolSelect)
							if (poolSelect["_POOLKEY"])
								postUrl += "?_POOLKEY="
										+ poolSelect["_POOLKEY"];
						$('#attachment_form').prop('action', postUrl);
						$('#attachment_form input').data("up_btn", $up_btn);
						attachment_form.reset();
						$('#attachment_form input').click();
					} else {

						var url = ydl.contexPath + '/fileInteractiveOne/list/'
								+ type + "/" + fileId;
						$.ajax(url, {
							method : 'GET',
							dataType : 'json',
							data : {
								_r : (new Date()) + ""
							},
							success : function(jsonData) {
								list_ajax_callback(options, $up_btn, jsonData)
							}
						}); // end ajax

					}// end if
				});// $(this).click

		return this;
	}

	// ==========================主程序结束

})(cst);

// 下载
(function(cst) {

	cst.ydDownload = function(options) {
		var $up_btn = $(this);
		var type = options.type;
		var zipname = options.zipname;
		var fileids = options.fileids;
		var url = ydl.contexPath + '/fileInteractiveDownLoad/jsondata/' + type;
		$.ajax(url, {
			method : 'POST',
			dataType : 'json',
			data : {
				_r : (new Date()) + "",
				zipname : zipname,
				fileids : fileids
			},
			success : function(jsonData) {
				if (jsonData.returnCode == "00000000") {
					// =====================
					var subPage = {
						close : function() {
							$dialog.dialog('close');
						}
					};
					var $dialog = $('<div class="text-center"></div>').append(
							"<a href='" + ydl.contexPath
									+ '/fileInteractiveDownLoad/download/'
									+ jsonData.poolkey + "'>点这里下载</a>")
							.appendTo($('body')).dialog({
								title : "下载窗口",
								close : true,
								size : 'sm',
								hidden : function() {
									$dialog.remove();
								}
							});
					$dialog.dialog('open');

					// =====================
				} else {
					ydl.alert({
						'icon' : 'warn',
						'message' : jsonData.message
					});
				}
			}// end success
		}); // end ajax
	}

})(cst);
