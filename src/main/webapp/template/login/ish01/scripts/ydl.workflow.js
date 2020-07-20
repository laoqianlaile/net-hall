/************************************************************************************************
 * 华信永道住房公积金平台公共脚本库
 * ydl.workflow.js 工作流框架支持库
 * @modified 修改人 修改时间 修改内容描述
 ************************************************************************************************/

if (!ydl) ydl = {};

(function(ydl, $, undefined) {

	ydl.workflow = {};

	/**
	 * 设置流程参数
	 * @param {String} name 参数名
	 * @param {String} value 参数值
	 * @returns {String} 参数值
	 */
	ydl.workflow.setAttribute = function(name, value) {
		if (parent.attribute && $.isPlainObject(parent.attribute)) {
			parent.attribute[name] = value;
		}
		else ydl.log('setAttribute：设置流程参数失败', 'red');
		return value;
	};

	/**
	 * 打开工作流人机节点指定的标签
	 * @param {String|Number} tab 标签ID（英文名）或标签索引（从0开始）
	 * @returns {Object} 标签页面window对象
	 */
	ydl.workflow.openTab = function(tab) {
		if (parent.document.title == 'Flw_waittaskNew') return parent.getTab(tab, true);
		else ydl.log('openTab：当前页面不在工作流人机节点中', 'red');
	};

	/**
	 * 获取工作流人机节点指定的标签页
	 * @param {String|Number} tab 标签ID（英文名）或标签索引（从0开始）
	 * @param {Function} [onload(tab)] 标签页面加载完成后执行的函数，将标签页面window对象作为参数
	 * @returns {Object} 标签页面window对象
	 */
	ydl.workflow.getTab = function(tab, onload) {
		if (parent.document.title == 'Flw_waittaskNew') return parent.getTab(tab, false, onload);
		else ydl.log('getTab：当前页面不在工作流人机节点中', 'red');
	};

	/**
	 * 启用工作流人机节点指定的标签页
	 * @param {String|Number} tab 标签ID（英文名）或标签索引（从0开始）
	 * @returns {Object} 标签页面window对象
	 */
	ydl.workflow.enableTab = function(tab) {
		if (parent.document.title == 'Flw_waittaskNew') return parent.enableTab(tab, true);
		else ydl.log('enableTab：当前页面不在工作流人机节点中', 'red');
	};

	/**
	 * 禁用工作流人机节点指定的标签页
	 * @param {String|Number} tab 标签ID（英文名）或标签索引（从0开始）
	 * @returns {Object} 标签页面window对象
	 */
	ydl.workflow.disableTab = function(tab) {
		if (parent.document.title == 'Flw_waittaskNew') return parent.enableTab(tab, false);
		else ydl.log('disableTab：当前页面不在工作流人机节点中', 'red');
	};

	/**
	 * 检查工作流人机节点标签页是否被禁用
	 * @param {String|Number} tab 标签ID（英文名）或标签索引（从0开始）
	 * @returns {Boolean} 页面标签是否被禁用
	 */
	ydl.workflow.isDisabled = function(tab) {
		if (parent.document.title == 'Flw_waittaskNew') return parent.tabDisabled(tab);
		else ydl.log('isDisabled：当前页面不在工作流人机节点中', 'red');
	};

	/**
	 * 获取指定id的标签索引
	 * @param {String} tabId 标签id
	 * @returns {Number} 标签索引，没找到则返回-1
	 */
	ydl.workflow.findTab = function(tabId) {
		if (parent.document.title == 'Flw_waittaskNew') return parent.tabIndex(tab);
		else ydl.log('findTab：当前页面不在工作流人机节点中', 'red');
	};

	/**
	 * 向工作流人机节点添加标签页
	 * @param {String} label 标签描述
	 * @param {String} url 标签页面地址，与流程设计器中定义规则相同
	 * @param {Boolean} [pageCheck=true] 是否校验页面表单
	 * @param {String} [id] 标签ID，缺省为自动生成随机串
	 * @returns {Number} 新增标签页面的索引
	 */
	ydl.workflow.addTab = function(label, url, pageCheck, id) {
		if (parent.document.title == 'Flw_waittaskNew') return parent.addTab(label, url, pageCheck, id);
		else ydl.log('addTab：当前页面不在工作流人机节点中', 'red');
	};

	/**
	 * 提交当前流程节点
	 * @returns undefined
	 */
	ydl.workflow.submit = function() {
		if (parent.document.title == 'Flw_waittaskNew') parent.doSubmit();
	};

	/**
	 * 隐藏工作流按钮
	 * @param {String} [id] 按钮id，缺省为全部按钮（为向前兼容，允许使用b-前缀）
	 * @param {Boolean} [isHide=true] 是否隐藏（该参数不建议使用，为向前兼容而保留）
	 */
	ydl.workflow.hideButton = function(id, isHide) {
		if (parent.document.title == 'Flw_waittaskNew') {
			var $buttons = parent.$('div.buttons' + (id ? ' #' + id.replace('-', '_') : ' button'));
			if (isHide === undefined || isHide === true) $buttons.hide();
			else $buttons.show();
		}
	};

	/**
	 * 显示工作流按钮
	 * @param {String} [id] 按钮id，缺省为全部按钮
	 */
	ydl.workflow.showButton = function(id) {
		if (parent.document.title == 'Flw_waittaskNew') {
			parent.$('div.buttons' + (id ? ' #' + id : ' button')).show();
		}
	};

	/**
	 * 禁用工作流按钮
	 * @param {String} [id] 按钮id，缺省为全部按钮
	 */
	ydl.workflow.disableButton = function(id) {
		if (parent.document.title == 'Flw_waittaskNew') {
			parent.$('div.buttons' + (id ? ' #' + id : ' button')).button('disable');
		}
	};

	/**
	 * 启用工作流按钮
	 * @param {String} [id] 按钮id，缺省为全部按钮
	 */
	ydl.workflow.enableButton = function(id) {
		if (parent.document.title == 'Flw_waittaskNew') {
			parent.$('div.buttons' + (id ? ' #' + id : ' button')).button('enable');
		}
	};

	/**
	 * 双敲复核检查
	 * @param {Boolean} [needConfirm] 是否需要用户确认，缺省为false，即不通过检查返回false，设为true会弹出提示信息，用户点击确定返回true，否则才返回false
	 * @returns {Boolean} 是否检查通过
	 */
	ydl.workflow.checkDoubleInput = function(needConfirm) {
		var passed = true;
		if (window.poolSelect && poolSelect['sq'] == 'y') {
			$('form input:not([type=hidden],:checkbox,:radio,:button,[readonly])').each(function() {
				lo('sq='+this.value);
				passed = ydl.validator(this, {
					silent: true,
					required: poolSelect[this.id] !== '' && poolSelect[this.id] !== undefined,
					rule: $(this).hasClass('money') && ydl.delComma(this.value) == poolSelect[this.id] || this.value == poolSelect[this.id],
					message: '%f录入的值“%v”与上一步骤中录入的值“' + poolSelect[this.id] + '”不一致！'
				}) && passed;
			});
			lo('passed='+passed);
			if (!passed) {
				if (needConfirm) return confirm('当前页面输入的值与上一步骤输入的不一致，是否以当前页面为准？\n\n点击【确定】以当前页面输入为准\n点击【取消】修改输入的值');
				else return false;
			}
		}
		return passed;
	};


	/**
	 * 设置或获取是否在审批页面选择了撤回或不同意
	 * 如果指定了setValue参数则设置这个值，不指定此参数为获取当前值
	 * @param {Boolean} [setValue] 设置值
	 * @returns {Boolean} 是否撤回或不同意
	 */
	ydl.workflow.cancelOrNotAgree = function(setValue) {
		if (parent.document.title == 'Flw_waittaskNew') {
			if (setValue !== undefined) return parent.isCancelOrNotAgree = setValue;
			else return parent.isCancelOrNotAgree;
		}
		else {
			ydl.log('cancelOrNotAgree：当前页面不在工作流人机节点中', 'red');
			return false;
		}
	};


	/**
	 * 发送隐式报文
	 * @param {String} tranCode 交易码
	 * @param {Object} [paras] 要发送的参数，如果省略，将根据上传报文定义的字段从页面中取值
	 * @param {String} [type] 接口名称（如socket、cics等，在ex_interface表中定义，缺省为socket）
	 * @param {Function} [fn] 异步提交回调函数，如果未提供此参数为同步提交
	 * @returns {Object} 后台交易返回值，如果发送失败返回null；如果提供了异步回调函数将始终返回null，需要在回调函数中获取返回值
	 */
	ydl.workflow.sendMessage = function(tranCode, paras, type, fn) {
		if (typeof paras === 'string') {
			type = paras;
			paras = null;
		}
		var message = '';
		//通过参数生成报文
		if ($.isPlainObject(paras)) {
			message = $.map(paras, function(value, key) {
				return '<' + key + '>' + value + '</>';
			}).join('');
		}
		else if ($.isArray(paras)) {
			message = $.map(paras, function(value, index) {
				return '<' + value.name + '>' + value.value + '</>';
			}).join('');
		}
		else {
			//查询报文字段
			var dictIds = null;
			ydl.ajax(ydl.contexPath + '/platform/workflow/sendMessage.jsp', {
				task: 'upitem',
				trancode: tranCode
			}, function(data) {
				dictIds = data;
			});
			if (dictIds == null) return null;
			else {
				//从页面中获取字段值，创建报文信息
				for (var i = 0; i < dictIds.length; i++) {
					message += '<' + dictIds[i] + '>' + ydl.getValue(dictIds[i]) + '</>';
				}
			}
		}
		//发送报文
		var result = null;
		ydl.ajax(ydl.contexPath + '/platform/workflow/sendMessage.jsp', {
			task: 'send',
			trancode: tranCode,
			type: type || 'socket',
			message: message
		}, fn || function(data) {
			result = data.data || data;
			$.each(result, function(key, value) {
				if (value == 'null' || value == '1899-12-31') result[key] = '';
			});
		}, {
			async: fn !== undefined,
			handleError: fn
		});
		/*	//返回错误，显示错误信息页签
            var instanceid = poolSelect['_IS'];
            if (instanceid && result.returnCode !== undefined && result.returnCode !== 0) {
                var tabId = 'pf19error';
                var index = findTab(tabId);
                if (index == -1) {
                    index = ydl.workflow.addTab('错误返回', 'platform/workflow/flw_waittask/dispErrMes.jsp?instanceid=' + instanceid, false, tabId);
                    getTab(tabId, true, function(tab) {
                        if (tab.errorCount == 0) {
                            //没有错误时删除页签
                            $tabs.tabs('remove', index);
                            pageInfo.pop();
                            ydl.log('无后台返回错误信息，' + tabId + '页签已删除。');
                        }
                        else {
                            //更新最大序号
                            $n('_maxSeq')[0].value = tab.maxSeq;
                        }
                    });
                }
                else $$('#tab-' + index).contentWindow.location.reload(true);
            }
        */
		return result;
	};

	/**
	 * 在页面上用列表显示后台下传文件内容
	 * @param {String|Object} container 显示列表的容器ID，或容器的DOM对象或jQuery对象
	 * @param {String} fn 数据集名称，由genTranFileDataByFileName函数的第二个参数指定
	 * @param {String|Array} colNames 列名字符串数组，或使用半角逗号分隔的多个列名
	 * @returns {Number} 记录行数
	 */
	ydl.workflow.listTranFile = function(container, fn, colNames) {
		container = ydl.getDom(container);
		if (!container) {
			ydl.log('ydl.workflow.listTranFile出错：找不到指定的容器', 'red');
			return 0;
		}
		//下传文件内容已读取到数据总线中，本函数直接从页面数据总线中取值
		var list = '';
		colNames = $.isArray(colNames) ? colNames : colNames.split(',');
		var cols = colNames.length;
		for (var i = 0; i < cols; i++) list += '<th><label class="sortable">' + colNames[i] + '</label></th>';
		list = '<tr class="ui-widget-header"><th>序号</th>' + list + '</tr>';
		var rownum = parseInt(poolSelect[fn + '_rownum']);
		for (var row = 1; row <= rownum; row++) {
			list += '<tr><td>' + row + '</td>';
			for (var i = 1; i <= cols; i++) {
				list += '<td>' + (poolSelect[fn + i + '[' + row + ']'] || '') + '</td>';
			}
			list += '</tr>';
		}
		$list = $('<table class="container datalist fit">' + list + '</table>');
		$(container).empty().append($list);
		ydl.themes.rowColor($list);
		//排序
		var $labels = $list.find('th label');
		$labels.click(function() {
			var desc = $(this).data('sortdesc') || false;
			var colIndex = $labels.index(this);
			var $trs = $('tr:gt(0)', $list).remove();
			$list.append(ydl.sort($trs.get(), function(tr) {
				var cellText = $(tr.cells[colIndex]).text();
				return isNaN(cellText) ? cellText : parseFloat(cellText);
			}, desc));
			$(this).data('sortdesc', !desc);
			ydl.themes.rowColor($list);
		});
		return rownum;
	};

	/**
	 * 多选一公共对话框
	 * 要求后台交易返回结果：如果没查到记录，返回非0的errflag和errreason，否则返回errflag=0
	 * 要显示的字段中每条记录的值之间用半角分号分隔
	 * @param {String} transcode 交易码
	 * @param {Object} paras 上传参数，{'字段名': '字段值'}
	 * @param {Array} fields 需要显示的字段，[{id: '字段名', label: '字段描述', hidden: true}]
	 * @param {String} [selectField] 选择返回的字段名，缺省为显示字段中的第一个
	 * @param {String} [message] 对话框中的提示信息
	 * @returns {String} 选择返回的字段值
	 */
	ydl.workflow.selectDialog = function(transcode, paras, fields, selectField, message) {
		var result = '';
		var data = ydl.workflow.sendMessage(transcode, paras);
		var splitter = ';';
		if (data) {
			if (data.errflag == 0) {
				//如果没有指定返回字段名，取显示字段的第一个字段作为返回字段
				if (selectField === undefined) selectField = fields[0]['id'];
				if (data[selectField] === undefined || data[selectField] == '') alert('没有找到符合条件的记录！');
				else {
					var recordCount = data[selectField].split(splitter).length;
					if (recordCount == 1) result = data[selectField];
					else {
						var selectData = '';
						var isHidden = [];
						fields = $.map(fields, function(field, index) {
							var fieldData = [field['label']].concat(data[field['id']].split(splitter));
							if (field['id'] == selectField) selectData = fieldData;
							isHidden[index] = field['hidden'] || false;
							return [fieldData];
						});
						ydl.dialog.open(ydl.contexPath + '/parsepage?$page=common/selectDialog.ydpx', {
							fields: fields,
							message: message,
							isHidden: isHidden
						}, function(data) {
							alert(data + selectData[data]);
							result = selectData[data];
						}, {layermode: false, width: 800, height: 400});
					}
				}
			}
			else alert(data.errreason);
		}
		return result;
	};

	/**
	 * 通过证件号查询客户号功能
	 * @param {String} certiType 证件类型
	 * @param {String} certiNum 证件号码
	 * @returns {String} 个人客户号，如果没找到返回空串
	 */
	ydl.workflow.selectCustId = function(certiType, certiNum) {
		return ydl.workflow.selectDialog('160023', {
			certitype: certiType,
			certinum: certiNum
		}, [
			{id: 'certinums', label: '证件号'},
			{id: 'custids', label: '客户号'},
			{id: 'accnames', label: '姓名'},
			{id: 'phones', label: '电话'},
			{id: 'unitaccnames', label: '单位名称'},
		], 'custids', '您输入的证件号码对应多条个人信息，请选择正确的一条');
	};

	/**
	 * 通过证件号查询个人帐号功能
	 * @param {String} certiType 证件类型
	 * @param {String} certiNum 证件号码
	 * @returns {String} 个人帐号，如果没找到返回空串
	 */
	ydl.workflow.selectAccnum = function(certiType, certiNum) {
		return ydl.workflow.selectDialog('160025', {
			certitype: certiType,
			certinum: certiNum
		}, [
			{id: 'certinums', label: '证件号'},
			{id: 'accnums', label: '个人帐号'},
			{id: 'accnames', label: '姓名'},
			{id: 'phones', label: '电话'},
			{id: 'unitaccnames', label: '单位名称'},
		], 'accnums', '您输入的证件号码对应多条个人帐号，请选择正确的一条');
	};

	/**
	 * 本地授权
	 * @param {String} roleids 可以授权的角色代码，多个代码用半角逗号分隔
	 * @returns {Object} 包含success（是否授权成功）、operid、opername、roleid（授权人代码、姓名、角色）的对象
	 */
	ydl.workflow.checkAuth = function(roleids) {
		if (!roleids) return { success: false, message: '没有指定授权角色代码' };

		var orgid = '';
		if (poolSelect["_BRANCHKIND"] == 'A') {
			orgid = poolSelect["_BANKCODE"];
		}
		else if (poolSelect["_BRANCHKIND"] == 'B') {
			orgid = poolSelect["_NODEBANK"];
		}
		else {
			orgid = poolSelect["_BRANCHID"];
		}

		var resultObj;
		ydl.dialog.open(ydl.contexPath + '/platform/checkAuth.jsp?roleids=' + roleids +
			'&loginid=' + poolSelect["_LOGINID"] + '&orgid=' + orgid, null, function(r) {
			resultObj = r;
		}, {
			width: 400,
			height: 200,
			layermode: false
		});
		if (resultObj) return resultObj;
		else return {success: false, returnCode: 6, message: '授权动作已取消'};
	};

	/**
	 * 设置人机节点提交后的提示信息
	 * 写入总线_SUCCESSMESSAGE变量，在提交成功页面engineSuccess.jsp上显示
	 * 参数顺序不分先后
	 * @param {String} [message] 文本提示信息
	 * @param {Array|Object} [list] 列表信息：{'字段描述': '字段值'}或['字段id', '字段id', {'字段描述': '字段值'}]
	 * @return {String} _SUCCESSMESSAGE变量的值：文本提示信息|字段名~字段值~字段名~字段值
	 */
	ydl.workflow.setSuccessInfo = function() {
		var splitter = '~';
		var reg = /\||~/g;
		var message = [], list = [];
		for (var i = 0; i < arguments.length; i++) {
			var para = arguments[i];
			//文本提示信息
			if (typeof para == 'string') {
				message.push((para || '').replace(reg, ' '));
			}
			//列表信息
			else if ($.isArray(para)) {
				$.each(para, function(index, item) {
					if (typeof item == 'string') {
						list.push((ydl.getLabel(item) || '').replace(reg, ' ') + splitter +
							(ydl.getValue(item, true) || '').replace(reg, ' '));
					}
					else if ($.isPlainObject(item)) {
						$.each(item, function(key, value) {
							list.push(key + splitter + ('' + value).replace(reg, ' '));
						});
					}
				});
			}
			else if ($.isPlainObject(para)) {
				$.each(para, function(key, value) {
					list.push(key + splitter + ('' + value).replace(reg, ' '));
				});
			}
		}
		var msg = message.join(splitter) + '|' + list.join(splitter);
		ydl.workflow.setAttribute('_SUCCESSMESSAGE', msg);
		return msg;
	};


	/**
	 * 个人不良信用记录查询，如果没有不良信用记录，直接返回0
	 * 如果有不良信用记录，弹出对话框进行警告，由用户选择继续办理业务还是拒绝办理业务
	 * @param {String} custid 个人客户号
	 * @returns {Number} 0=无不良信用记录，1=继续办理，2=拒绝办理
	 */
	ydl.workflow.badCredit = function(custid) {
		var result = 2;
		ydl.ajax(ydl.contexPath + '/ajax', {
			'$page': 'common/commonAjax.ydpx',
			'ajax_query_id': 'getBadCredit',
			'badcredit_custid': custid
		}, function(data) {
			if (data.length > 0) {
				ydl.dialog.open(ydl.contexPath + '/platform/commonDialog.jsp', {
					title: '不良信用记录警告',
					init: function($dialog) {
						with (this) {
							$dialog.append('<div class="fit icon_info_alert ui-state-error">警告：此人存在不良信用记录!</div>');
							$dialog.append('<table class="container datalist fit">' +
								'<tr class="ui-widget-header"><th>姓名</th><th>证件类型</th><th>证件号码</th><th>不良记录类型</th><th>备注</th></tr>' +
								$.map(data, function(item) {
									return '<tr><td>' + item.accname + '</td><td>' + item.certitype + '</td><td>' + item.certinum + '</td><td>' + item.badcretype + '</td><td>' + item.remark + '</td></tr>';
								}).join('') + '</table>'
							);
						}
					},
					buttons: {
						'继续办理业务': function() {
							this.ydl.dialog.close(1);
						},
						'拒绝办理业务': function() {
							this.ydl.dialog.close(2);
						}
					}
				}, function(data) {
					result = data;
				}, {
					layermode: false,
					height: 300
				});
			}
		}, {
			handleError: function(data) {
				if (data.message == 'norecord') result = 0;
				else alert(data.message);
			}
		});
		return result;
	};


	/**
	 * 在portal_pageconfig中配置的自定义查询结果
	 */
	ydl.workflow.parameter = top.customQuery;


	/**
	 * 发送命令
	 * @param {Object} [paras] 上传参数
	 * @param {Boolean} [createInput=false] 是否根据返回值自动创建隐藏字段
	 * @param {Object} [ajaxOptions] Ajax配置参数，与ydl.ajax()的参数相同
	 * @returns {Object} 返回数据，查询失败返回null
	 */
	ydl.workflow.sendCommand = function (paras, createInput, ajaxOptions) {
		var r = null;
		//取当前页面字段值
		var inputs = {};
		$.each($('form').getInputs(true), function (index, item) {
			if (item.name.charAt(0) != '_' && item.name != 'DATAlISTGHOST') inputs[item.name] = item.value;
		});
		//覆盖优先级：总线 < 当前页面字段输入的值 < 手工指定的参数值
		ydl.ajax('command.summer', $.extend({}, poolSelect, inputs, paras), function (data) {
			r = data;
		}, ajaxOptions);
		if (createInput && r) {
			//添加隐藏域
			$('form').append($.map(r, function (value, key) {
				if (key == 'url') return null;		//不包含command查询返回的url
				else if (key.indexOf('$') >= 0) return null;
				else if ($('[name=' + key + ']').length > 0) {	//页面中已有的字段直接赋值
					$('[name=' + key + ']').val(value);
					return null;
				}
				else return '<input type="hidden" name="' + key + '" value="' + value + '" />';
			}).join(''));
		}
		return r;
	};

})(ydl, jQuery);