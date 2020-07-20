/************************************************************************************************
 * 华信永道住房公积金平台公共脚本库
 * ydl.ydpx.js YDPX页面容器组件支持库
 * @modified 修改人 修改时间 修改内容描述
 ************************************************************************************************/

(function(ydl, $, undefined) {

	if (!ydl) ydl = {};

	/**
	 * 页面组件初始化（由页面解析引擎生成调用代码）
	 */
	ydl.init = {};


	/**
	 * 提供全部参数以初始化隐式提交，或者提供ajaxId以手动触发隐式提交（需已初始化）
	 * 服务器返回的结果如果是单条记录，可以指定一个或多个target，将不同字段的值填充到不同的组件；
	 * 如果返回多条记录，可以填充到一个下拉列表或者一组复选框
	 * 后三个参数为向前兼容而保留，不再建议使用，应改为在页面中编写ajax_before_ajaxId、ajax_after_ajaxId、ajax_complete_ajaxId函数，
	 * 其中ajaxId为初始化函数中定义的实际值
	 * 可以通过ydl.init.ajax(ajaxId)或ydl.init.ajax(ajaxId, {paras})触发隐式提交，paras为附加的参数
	 * @param {String} ajaxId 隐式提交的唯一标识
	 * @param {String} triggerId 在onchange事件中触发提交的组件id；如果是null，将不绑定事件，而是通过代码触发
	 * @param {Array} target 查询结果填充的目标组件，对象数组[{id:'组件id或name', label:'显示值字段', value:'数据值字段'}]，可以是空数组[]
	 * @param {Function} beforeAjax 提交前的回调函数，返回false可以阻止提交
	 * @param {Function} afterAjax(data) 服务器返回结果后的回调函数，以服务器返回的data作为参数，返回false可以阻止默认的回填数据动作
	 * @param {Function} ajaxComplete(isFilled) 将返回的结果填充到表单中之后执行的回调函数，传入Boolean参数指明回填数据是否正常完成
	 * @returns {Undefined|Object} 初始化时返回undefined，手工触发时返回服务器返回的数据
	 */
	ydl.init.ajax = function(ajaxId, triggerId, target, beforeAjax, afterAjax, ajaxComplete) {
		if (!beforeAjax) beforeAjax = window['ajax_before_' + ajaxId];
		if (!afterAjax) afterAjax = window['ajax_after_' + ajaxId];
		if (!ajaxComplete) ajaxComplete = window['ajax_complete_' + ajaxId];
		//全局对象
		if (!ydl.data.ajaxFunctions) ydl.data.ajaxFunctions = {};
		var ajaxFunctions = ydl.data.ajaxFunctions;
		//触发指定的隐式提交，传入附加数据
		if (arguments.length <= 2 && ajaxFunctions[ajaxId]) return ajaxFunctions[ajaxId](arguments[1]);
		else {
			//初始化隐式提交
			ajaxFunctions[ajaxId] = function(extraParas) {
				var returnData = null;
				if (!beforeAjax || beforeAjax() !== false) {
					//发送请求，提交表单内所有字段，不包括列表容器和datalist中的字段
					var paras = $('.container:not(.datalist)').getInputs(true).concat([
						//{name: '$page', value: ydl.common.parameters['$page']},
						{name: '$page', value: poolSelect['$page']},
						{name: 'ajax_query_id', value: ajaxId},
						{name: '_PROCID', value: document.getElementsByName('_PROCID')[0].value},
						{name: '_DATAPOOL_', value: document.getElementsByName('_DATAPOOL_')[0].value},
					]);
					//附加提交数据
					if (extraParas) {
						if ($.isArray(extraParas)) paras = paras.concat(extraParas);
						else if ($.isPlainObject(extraParas)) paras = paras.concat($.map(extraParas, function(value, key) {
							return {name: key, value: value};
						}));
					}
					ydl.ajax(ydl.contexPath + '/ajax', paras, function(data) {
							//成功返回结果
							var isFilled = false;
							returnData = data;
							if (!afterAjax || afterAjax(data) !== false) {
								//处理每个target
								for (var i = 0, l = target.length; i < l; i++) {
									var t = target[i];
									ydl.log('target[' + i + '] = ' + ydl.getMember(t));
									var ele = document.getElementById(t.id);
									if (ele && ele.tagName && ele.tagName !== 'UL') {	//填充唯一组件
										if (ele.type === 'select-one') {	//下拉列表
											if (t.label === '') ydl.selectByValue(ele, data[0][t.value]);
											else $(ele).html(ydl.common.blankOption + ydl.createOptions(data, t.label, t.value));
										}
										else ele.value = data[0][t.value] === '1899-12-31' ? '' : data[0][t.value];	//文本框等

									}
									else {
										ele = document.getElementsByName(t.id);
										var el = ele.length;
										if (el > 0 && ele[0].type === 'checkbox') {	//填充一组checkbox
											var objData = {};
											$.each(data, function(index, value) {
												objData[value[t.value]] = 0;
											});
											for (var e = 0; e < el; e++) ele[e].checked = (ele[e].value in objData);
										}
										else ydl.log('ydl.init.ajax出错：找不到目标组件' + t.id, 'red');
									}
								}
								isFilled = true;
							}
							if (ajaxComplete) ajaxComplete(isFilled);
						},
						{	//查询结果集为空时的处理
							handleError: function(data) {
								for (var i = 0, l = target.length; i < l; i++) {
									var t = target[i];
									ydl.log('target[' + i + '] = ' + ydl.getMember(t));
									var ele = document.getElementById(t.id);
									if (ele) {	//填充唯一组件
										if (ele.type === 'select-one') {	//下拉列表
											if (t.label === '') ydl.selectByValue(ele, '');
											else $(ele).html('');
										}
										else ele.value = '';
									}
									else {
										ele = document.getElementsByName(t.id);
										var el = ele.length;
										if (el > 0) {	//填充多个组件
											for (var e = 0; e < el; e++) {
												if (ele[e].type == 'text') ele.value = '';
											}
										}
									}
								}
								var ret = true;
								if (ajaxComplete) ret = ajaxComplete(false);
								if (ret !== false && data.message) alert(data.message);
							},
							//同步提交
							async: false
						});
				}
				return returnData;
			};
			//绑定onchange事件
			if (triggerId) {
				var $trigger = $('#' + triggerId);
				$trigger.change(function() {
					if (this.value != '' && checkFieldAttr($(this), false)) ajaxFunctions[ajaxId]();
				});
			}
		}
	};

	/**
	 * 动态扩展行列表（datalist组件），支持分页、增删改查、支持同一个页面上多个datalist
	 * 回调函数：
	 * datalist_validate_列表ID(inputs, rows)：翻页、保存等提交动作前执行，参数为新增和修改过的表单对象数组和表格行数组，可用于执行表单校验，返回false可以阻止提交；
	 * 		如果dialogEdit>0，在点击对话框的确定按钮时也将调用此函数，但是只传递inputs参数，rows为undefined
	 * datalist_callback_列表ID(rows)：初始化、翻页、保存、新增等动作完成后执行，rows参数为列表行数组，如果是新增动作只包含新增的行，可用于初始化数据展现
	 * datalist_changed_列表ID()：如果列表内容有变化，在成功更新之后执行
	 * datalist_buttonclick_列表ID(buttonType)：点击按钮时执行，返回false可阻止按钮动作，buttonType:'add'、'save'、'import'、'export'
	 * datalist_dialoginit_列表ID($editor)：在dialogEdit>0时使用，初始化编辑对话框时执行，参数为对话框容器的jQuery对象
	 * datalist_dialogopen_列表ID($editor, isAdd)：在dialogEdit>0时使用，打开编辑对话框时执行，$editor参数为对话框容器的jQuery对象，isAdd=true为添加记录，isAdd=false为编辑记录
	 * @param {Object} options 参数 {
	 * 		{String} id 列表id
	 * 		{String} caption 列表标题
	 * 		{Boolean} paging 是否分页
	 * 		{Number} currentPage 当前页码
	 *		{Number} pageSize 每页记录数
	 *		{Number} maxSize 结果集最大记录数
	 *		{Number} totalCount 总记录数
	 *		{Number} pageCount 总页数
	 * 		{Boolean} allowImport 是否允许批量导入记录
	 * 		{Boolean} allowExport 是否允许批量导出记录
	 * 		{Boolean} allowInsert 是否允许手工增加记录
	 * 		{Boolean} allowUpdate 是否允许修改记录
	 * 		{Boolean} allowDelete 是否允许删除记录
	 * 		{Boolean} dialogEdit 编辑记录对话框表单列数（0为不使用对话框编辑，大于0的数字使用对话框编辑）
	 * 		{Boolean} showRowId 是否显示行号
	 * 		{Array} columns 列定义，数组元素为对象 [{
	 *			{String} id 列字段名
	 *			{String} desc 列描述
	 *			{String} dataType 列数据类型，与ydl.validator中type参数相同
	 *			{String} maxLength 列最大可输入字符长度
	 *			{Boolean} required 是否必填
	 *			{Number} colType 列类型：1=只读，2=可编辑，3=条件字段
	 *			{Boolean} hidden 是否隐藏该列
	 *			{Object} [dict] 数据字典{'数据值': '显示值'}
	 *		}]
	 *		{Array} data 初始数据（对象数组，每个数组元素为一条记录，对象的属性名为字段名，属性值为字段值；如果不分页为全部数据，分页为第一页数据）
	 * }
	 * @param {String} [method='init'] 要执行的方法：'init'=初始化，'update'=更新数据，'refresh'=刷新(返回第一页)，'save'=保存
	 * @author 陈海冬 2011-05
	 */
	ydl.init.dataList = function(options, method) {
		//记录状态
		var FLAG_ORIGINAL = 0;	//原有记录（不提交）
		var FLAG_NEW = 1;		//新增记录
		var FLAG_DELETED = 2;	//原记录删除
		var FLAG_ABORTED = 3;	//新记录删除（不提交）
		var FLAG_MODIFIED = 4;	//原记录修改
		//列类型
		var COLUMN_READONLY = 1;	//只读（不提交）
		var COLUMN_EDITABLE = 2;	//可编辑（提交）
		var COLUMN_TERM = 3;		//条件字段（提交但不可编辑）

		method = method || 'init';
		var tableId = options.id;
		if (!tableId) return true;	//针对listTranFile生成的表格，没有id，忽略
		var table = document.getElementById(tableId);
		var $table = $(table);

		switch (method) {
			//初始化列表
			case 'init':
				//将当前列表参数保存到全局对象中
				if (!ydl.data.dataList) ydl.data.dataList = {};
				var gOptions = ydl.data.dataList[tableId] = {
					'caption': options.caption || options.desc,
					'currentPage': options.pageCount == 0 ? 0 : 1,
					'allowImport': options.allowImport,
					'allowExport': options.allowExport,
					'allowInsert': options.allowInsert,
					'allowUpdate': options.allowUpdate,
					'allowDelete': options.allowDelete,
					'dialogEdit': options.dialogEdit,
					'showRowId': options.showRowId,
					'paging': options.paging,
					'pageSize': options.pageSize,
					'pageCount': options.pageCount,
					'totalCount': options.totalCount,
					'maxSize': options.maxSize,
					'columns': options.columns,
					'data': options.data,
					'dictCache': {}	//数据字典缓存
				};

				//启用对话框编辑
				if (gOptions.dialogEdit && gOptions.allowUpdate) {
					var $editor = $('#' + tableId + '_editor');
					gOptions.customDialogEdit = $editor.length > 0;	//是否使用自定义对话框编辑
					if (!gOptions.customDialogEdit) {	//使用自动生成的对话框编辑
						$editor = $('<table id="' + tableId + '_editor" class="container fit"></table>').appendTo('body');
						var editorFormHtml = '';	//表单html代码
						var editorFormInputCount = 0;	//输入域的数量
						var editorFormCols = gOptions.dialogEdit;	//表单列数
						var editorFormCurrentCol = 0;	//表单当前列
						$.each(gOptions.columns, function(index, col) {
							if (col.colType == COLUMN_EDITABLE) {
								editorFormInputCount++;
								editorFormCurrentCol = editorFormInputCount % editorFormCols;
								var colId = tableId + '_editor_' + col.id;
								if (editorFormCols == 1 || editorFormCurrentCol == 1) editorFormHtml += '<tr>';
								editorFormHtml += '<td><label for="' + colId + '">' + col.desc + '：</label></td><td>' + (col.dict ?
									'<select id="' + colId + '"' + (col.required ? ' required="required"' : '') +
									'>' + ydl.common.blankOption + ydl.createOptions(col.dict) + '</select>' :
									'<input id="' + colId + '" name="' + col.id + '" type="text"' +
									(col.maxLength ? ' maxlength="' + (col.dataType == 'money' && parseInt(col.maxLength) > 15 ? '15' : col.maxLength) + '"' : '') +
									//size属性不建议使用，为向前兼容而保留
									(col.size ? ' size="' + col.size + '"' : (col.maxLength ? (col.maxLength > 40 ? ' style="width: 85%"' : ' size="' + col.maxLength + '"') : '')) +
									(col.required ? ' required="required"' : '') +
									(col.dataType ? ' data-type="' + col.dataType + '"' : '') +
									(col.decLen ? ' data-declen="' + col.decLen + '"' : '') +
									' />') + (col.required ? '<em>*</em>' : '') + '</td>';
								if (editorFormCols == 1 || editorFormCurrentCol == 0) editorFormHtml += '</tr>';
							}
						});
						if (editorFormCurrentCol != 0) editorFormHtml += ydl.string('<td><label></label></td><td></td>', editorFormCols - editorFormCurrentCol) + '</tr>';
						$editor.append(editorFormHtml);
						$editor.find(':input').each(function() {
							var $input = $(this);
							var dataType = $input.attr('data-type');
							if (dataType == 'money') $input.moneyinput();
							else if (dataType == 'date') $input.datepicker();
							$input.change(function() {
								checkFieldAttr($input, false);
							});
						});
					}
					var $editorDialog = $('<div id="' + tableId + '_editor_dialog"></div>').append($editor.addClass('datalist-editor-dialog')).dialog({
						title: gOptions.caption + ' 编辑记录',
						width: (document.body.clientWidth || top.$$('main-frame').clientWidth) - 100,
						autoOpen: false,
						modal: true,
						create: function(event, ui) {
							$('#ui-dialog-title-' + tableId + '_editor_dialog').next('.ui-dialog-titlebar-close').hide();
							var dialogInitFunc = window['datalist_dialoginit_' + tableId];
							if (dialogInitFunc && $.isFunction(dialogInitFunc)) dialogInitFunc($editor);
						},
						open: function(event, ui) {
							$table.find('tr').removeClass('selected');
							$editorDialog.data('row').addClass('selected');
							var dialogOpenFunc = window['datalist_dialogopen_' + tableId];
							if (dialogOpenFunc && $.isFunction(dialogOpenFunc)) dialogOpenFunc($editorDialog, $editorDialog.data('task') == 'add');
						},
						buttons: {
							'确定': function() {
								//校验
								var validateFunc = window['datalist_validate_' + tableId];
								if (validateFunc && $.isFunction(validateFunc) && !validateFunc($editorDialog.find(':input').get())) return;
								if (!ydl.formValidate($editorDialog)) {
									$editorDialog.find('.error:first').focus();
									return false;
								}
								//更新行内数据
								var $tr = $editorDialog.data('row');
								$.each(gOptions.columns, function(index, col) {
									var inputId = tableId + '_editor_' + col.id;
									var input = $$(inputId);
									if (input) {
										var $input = $(input);
										var $td = $tr.getCell(col.id);
										if (input.type === 'select-one') {
											$td.children('input').val($input.val());
											$td.children('span').text($input.val() == '' ? '' : input.options[input.selectedIndex].innerHTML);
										}
										else {
											$td.children('input').val(input.value);
											$td.children('span').text($input.attr('data-type') == 'money' ? ydl.addComma(input.value) : input.value);
										}
									}
								});

								//设置页码栏宽度
								var width = parseInt(table.clientWidth) - 12;
								$table.next().css({'max-width': width, 'width': width});

								$editorDialog.dialog('close');
							},
							'取消': function() {
								if ($editorDialog.data('task') == 'add') $editorDialog.data('row').remove();
								$editorDialog.dialog('close');
							}
						}
					});
					//修改记录，取行内数据
					$table.on('click', 'tr:gt(0)', function(event) {
						if (!$(this).hasClass('ui-widget-header') && gOptions.allowUpdate && gOptions.dialogEdit && !(event.target.tagName == 'INPUT' && event.target.type == 'checkbox')) {
							var $tr = $(this);
							$.each(gOptions.columns, function(index, col) {
								var $input = $('#' + tableId + '_editor_' + col.id);
								if ($input.hasClass('money')) $input.val(ydl.delComma($tr.getCell(col.id, ':input').val()));
								else $input.val($tr.getCell(col.id, ':input').val());
								if ($input.hasClass('money') && $input.val() != '') $input.val(ydl.addComma(parseFloat($input.val()).toFixed($input.attr('data-declen') || 2)));
							});
							$editorDialog.data('task', 'modify').data('row', $tr).dialog('open');
						}
					});
				}
				updatePage();
				//按钮回调函数
				var buttonClickFunc = window['datalist_buttonclick_' + tableId];
				if (!$.isFunction(buttonClickFunc)) buttonClickFunc = null;
				//创建页码栏
				$table.pagebar({
					currentPage: parseInt(options.currentPage),
					pageCount: parseInt(options.pageCount),
					totalCount: parseInt(options.totalCount),
					gotoPage: saveData,
					buttons: {
						'add': {
							icon: 'document',
							text: '添加',
							click: function() {
								if (!buttonClickFunc || buttonClickFunc('add') !== false) insertRow(false);
							}
						},
						'delete': {
							icon: 'trash',
							text: '删除',
							click: function() {
								if (!buttonClickFunc || buttonClickFunc('delete') !== false) {
									var $checkboxes = $table.find('td:nth-child(' + table.rows[0].cells.length + 'n+1) :checkbox:visible');
									var checkedCount = 0;
									$.each($checkboxes, function() {
										if (this.checked) checkedCount++;
									});
									if (checkedCount == 0) alert('请先选择要删除的记录。');
									else if (saveData(gOptions.currentPage, true)) alert('删除成功！');
								}
							}
						},
						'save': {
							icon: 'disk',
							text: '保存',
							click: function() {
								if (!buttonClickFunc || buttonClickFunc('save') !== false) {
									if (saveData(gOptions.currentPage, true)) alert('保存成功！');
								}
							}
						},
						'import': {
							icon: 'folder-open',
							text: '导入',
							click: function() {
								if (!buttonClickFunc || buttonClickFunc('import') !== false) {
									ydl.dialog.open(ydl.contexPath + '/platform/listUploadInput.jsp', [
										{name: 'dynamicTable_id', value: tableId},
										{name: 'dynamicTable_pageSize', value: gOptions.pageSize},
										//{name: 'dynamicTable_page', value: ydl.common.parameters["$page"]}
										{name: 'dynamicTable_page', value: poolSelect["$page"]},
										{name: '_IS', value: poolSelect["_IS"]}
									].concat($('table:not(.datalist) :input[name]:enabled, div.container>:input[name]:enabled').map(function() {
										return { name: this.name, value: this.value };
									}).get()), function(data) {
										if (data && data.returnCode == 0 && data.data) {
											ydl.init.dataList(data.data, 'update');
											//回调changed函数
											var changedFunc = window['datalist_changed_' + tableId];
											if (changedFunc && $.isFunction(changedFunc)) changedFunc();
										}
									}, { width: 700, height: 400 });
								}
							}
						},
						'export': {
							icon: 'circle-arrow-s',
							text: '导出',
							click: function() {
								var paras = [
									//{name: '$page', value: ydl.common.parameters['$page']},
									{name: '$page', value: poolSelect['$page']},
									{name: 'export_componentType', value: 'datalist'},
									{name: 'export_componentId', value: tableId},
									{name: 'export_currentPage', value: gOptions.currentPage},
									{name: 'export_pageCount', value: gOptions.pageCount}
								];
								$('table:not(.datalist),div').each(function(index, ele) {
									paras = paras.concat(ydl.getInputs(ele));
								});
								ydl.dialog.open(ydl.contexPath + '/platform/listExport.jsp', paras, null, { width: 500, height: 300 });
							}
						}
					}
				});
				showButtons();
				$table.next().css('width',$table.css('width'));
				//回车添加新记录
			function inputKeyDown(event) {
				if (event.which == ydl.common.keys.Enter) {
					var $allInput = $(':input:visible:not(:disabled,[readonly=readonly])', $table);
					if ($allInput.index(this) == $allInput.length - 1) {
						if (!window['datalist_add_' + tableId] || window['datalist_add_' + tableId]()) insertRow(false);
						return false;
					}
				}
			}
				if (options.allowInsert) $table.on('keydown', ':input:visible', inputKeyDown);
				callback();
				break;

			//更新当前页
			case 'update':
				var gOptions = ydl.data.dataList[tableId];
				$.extend(gOptions, options);
				showButtons();
				updatePage();
				$table.pagebar({
					currentPage: parseInt(gOptions.currentPage),
					pageCount: parseInt(gOptions.pageCount),
					totalCount: parseInt(gOptions.totalCount)
				});
				callback();
				break;

			//刷新
			case 'refresh':
				if (!ydl.data.dataList || !ydl.data.dataList[tableId]) return true;
				return saveData(options.currentPage || 1);

			//保存
			case 'save':
				var gOptions = ydl.data.dataList[tableId];
				if (!buttonClickFunc || buttonClickFunc('save') !== false) {
					return saveData(gOptions.currentPage, false);
				}
		}

		//回调函数
		function callback(rows) {
			var callbackFunc = window['datalist_callback_' + tableId];
			if (callbackFunc && $.isFunction(callbackFunc)) {
				if (rows) callbackFunc(rows);
				else callbackFunc(table.rows);
			}
		}

		//从字典中查找值
		function getDictData(dict, key) {
			if ($.isArray(dict)) {
				for (var i = 0; i < dict.length; i++) {
					if (dict[i].value == key) return dict[i].text;
				}
			}
			else return dict[key];
		}

		//根据修改权限设置按钮可见性
		function showButtons() {
			var gOptions = ydl.data.dataList[tableId];
			if (gOptions.allowInsert) $('#list_' + tableId + '_buttons_add').removeClass('hide');
			else $('#list_' + tableId + '_buttons_add').addClass('hide');
			if (gOptions.allowInsert || gOptions.allowDelete) $('#list_' + tableId + '_buttons_delete').removeClass('hide');
			else $('#list_' + tableId + '_buttons_delete').addClass('hide');
			if (gOptions.allowImport) $('#list_' + tableId + '_buttons_import').removeClass('hide');
			else $('#list_' + tableId + '_buttons_import').addClass('hide');
			if (gOptions.allowExport) $('#list_' + tableId + '_buttons_export').removeClass('hide');
			else $('#list_' + tableId + '_buttons_export').addClass('hide');
			if (gOptions.allowInsert || gOptions.allowUpdate || gOptions.allowDelete) $('#list_' + tableId + '_buttons_save').removeClass('hide');
			else $('#list_' + tableId + '_buttons_save').addClass('hide');
		}

		//更新一页数据（结果集记录取自options，列定义取自全局对象）
		function updatePage() {
			var gOptions = ydl.data.dataList[tableId];

			//ydl.log('ydl.data.dataList["' + tableId + '"] = \n' + ydl.getMember(gOptions));
			//清空表格原有内容
			$table.children().remove();
			//添加列表标题
			gOptions.caption && $table.append('<caption class="ui-widget-header">' + gOptions.caption + '</caption>');
			//添加复选框列，含行标记隐藏域
			var $tr = $('<tr class="ui-widget-header"></tr>').append(
				$('<th class="check' + (gOptions.allowDelete || gOptions.allowInsert ? '' : ' hide') + '"></th>').append(
					$('<span class="ui-icon ui-icon-trash" style="cursor:pointer;" title="删除"></span>').click(function() {
						//点击全选、取消全选
						var $checkboxes = $table.find('td:nth-child(' + table.rows[0].cells.length + 'n+1) ._datalist_delete_checkbox:visible');
						if ($checkboxes.filter(':checked').length === $checkboxes.length) $checkboxes.removeAttr('checked');
						else $checkboxes.attr('checked', 'checked');
					})
				)
			);
			//添加行号列标题
			if (gOptions.showRowId) $tr.append('<th class="__rownumber">行号</th>');
			//添加数据列标题
			var i, l;
			for (i = 0, l = gOptions.columns.length; i < l; i++) {
				$tr.append('<th class="_' + gOptions.columns[i].id + (gOptions.columns[i].hidden ? ' hide' : '') + '">' +
					gOptions.columns[i].desc + (gOptions.columns[i].required ? '<em>*</em>' : '') + '</th>');
			}
			//插入表头
			$table.append($tr);
			//处理结果集每一行记录
			for (i = 0, l = gOptions.data.length; i < l; i++) {
				insertRow(true, gOptions.data[i], i + 1);
			}
			if (gOptions.allowUpdate && gOptions.dialogEdit) $table.addClass('row-selectable');
			else $table.removeClass('row-selectable');
			ydl.themes.rowColor(table);
			//未显示全部记录提示
			if (!gOptions.paging && gOptions.totalCount > gOptions.maxSize) {
				$table.append('<tr class="ui-state-highlight _datalist-tip"><td colspan="' + table.rows[0].cells.length +
					'"><span class="ui-icon ui-icon-info" style="float:left"></span>查询结果共 ' +
					gOptions.totalCount + ' 条记录，本页只显示前 ' + gOptions.maxSize + ' 条记录。</td></tr>');
			}
		}

		//添加一行记录（isOriginal：是否原有数据；data：options.data中的一条记录；index：行序号）
		function insertRow(isOriginal, data, index) {
			!isOriginal && $table.find('tr._datalist-tip').remove();
			if (index === undefined) index = table.tBodies[0].rows.length;
			var gOptions = ydl.data.dataList[tableId];
			var tr = table.insertRow(index);
			//第一列，复选框和行标记
			var td = tr.insertCell(0);
			if (!gOptions.allowDelete && !gOptions.allowInsert) td.className = 'hide';
			td.innerHTML = '<input type="checkbox" class="_datalist_delete_checkbox' + (isOriginal && !gOptions.allowDelete ? ' hide' : '') + '" />' +
				'<input type="hidden" name="dynamicTable_flag" value="' + (isOriginal ? FLAG_ORIGINAL : FLAG_NEW) + '" />';
			var colNum = 1;
			//行号列
			if (gOptions.showRowId) {
				td = tr.insertCell(1);
				td.className = 'code';
				td.innerHTML = index + gOptions.pageSize * ((gOptions.currentPage > 0 ? gOptions.currentPage : 1) - 1);
				colNum++;
			}
			//数据列
			for (var colIndex = 0, colCount = gOptions.columns.length; colIndex < colCount; colIndex++) {
				var gColumn = gOptions.columns[colIndex];
				var columnId = gColumn.id;
				var colData = isOriginal ? (data[columnId] ? data[columnId].replace(/"/g, '&quot;') || '' : '') : '';
				td = tr.insertCell(colIndex + colNum);
				var fieldId = tableId + ':' + columnId + ':' + index;
				//单元格样式
				var colStyle = '';
				if ($.inArray(gColumn.dataType, ['date', 'time', 'month', 'longmonth', 'day', 'longday', 'zipcode', 'idcard', 'ipv4', 'yyyymm']) >= 0) colStyle = 'code';
				else if ($.inArray(gColumn.dataType, ['number', 'int', 'float']) >= 0) colStyle = 'number';
				else if (gColumn.dataType == 'money') colStyle = 'money';

				//处理列数据文本
				var colDataText = (gColumn.dict ? (isOriginal ? (getDictData(gColumn.dict, colData) || '') : '') : colData);
				if (gColumn.dataType === 'money' && colDataText != '') colDataText = ydl.addComma(parseFloat(colDataText).toFixed(gColumn.decLen || 2));
				//可编辑
				if (gColumn.colType == COLUMN_EDITABLE && gOptions.allowUpdate) {
					if (gOptions.dialogEdit) {
						//对话框编辑，输出input:hidden + span
						if (colStyle) td.className = colStyle;
						td.innerHTML = '<input type="hidden" id="' + fieldId + '" name="' + columnId + '" value="' + colData + '" />' +
							'<span>' + colDataText + '</span>';
					}
					else {
						//有字典，输出select
						if (gColumn.dict) {
							//缓存列表选项
							if (!gOptions.dictCache[columnId]) gOptions.dictCache[columnId] = ydl.common.blankOption + ydl.createOptions(gColumn.dict);
							td.innerHTML = '<select id="' + fieldId + '" name="' + columnId + '"' +
								(gColumn.required ? 'required="required"' : '') + '>' + gOptions.dictCache[columnId] + '</select>';
							isOriginal && ydl.selectByValue(document.getElementById(fieldId), colData);
						}
						//无字典，输出input:text
						else {
							td.innerHTML = '<input type="text" id="' + fieldId + '" name="' + columnId + '" value="' + colData + '"' +
								(gColumn.maxLength ? ' maxlength="' + gColumn.maxLength + '"' : '') +
								(gColumn.size ? ' size="' + gColumn.size + '"' : (gColumn.maxLength ? ' size="' + (gColumn.maxLength > 40 ? '40' : gColumn.maxLength) + '"' : '')) +
								//(gColumn.maxLength || gColumn.size ? ' size="' + (gColumn.size || gColumn.maxLength) + '"' : '') +
								(gColumn.required ? ' required="required"' : '') + (colStyle ? ' class="' + colStyle + '"' : '') + ' />';
							if (gColumn.dataType === 'money') $('#' + fieldId.replace(/:/g, '\\:')).moneyinput();
							else if (gColumn.dataType === 'date') $('#' + fieldId.replace(/:/g, '\\:')).datepicker();
						}
					}
				}
				//不可编辑，只读输出span，条件字段输出input:hidden + span
				else {
					if (gColumn.dict) td.title = colData;
					if (colStyle) td.className = colStyle;
					td.innerHTML = (gColumn.colType == COLUMN_TERM ?
						'<input type="hidden" id="' + fieldId + '" name="' + columnId +	'" value="' + colData + '" />' : '') +
						'<span>' + colDataText + '</span>';
				}
				//隐藏列
				if (gColumn.hidden) td.className = 'hide';
			}
			//新增记录
			if (!isOriginal) {
				ydl.themes.rowColor(table);
				//行内除复选框外的第一个输入域设置焦点
				var $field = $(':input:visible', tr);
				$field.eq($field[0].type == 'checkbox' ? 1 : 0).focus();
				//打开编辑对话框
				if (gOptions.dialogEdit) {
					$editorDialog.find(':input').val('');
					$editorDialog.data('task', 'add').data('row', $(tr)).dialog('open');
				}
				callback(tr);	//执行回调函数
			}
			//if (top.pageResize) top.pageResize();
		}

		//保存并提交（pageNo：保存后跳转的页码，fromSaveButton：是否从保存按钮调用此函数，注意对false和undefined的处理有区别）
		function saveData(pageNo, fromSaveButton) {
			var gOptions = ydl.data.dataList[tableId];
			//提交的数据
			var postData = [
				{name: 'dynamicTable_id', value: tableId},
				{name: 'dynamicTable_currentPage', value: gOptions.currentPage},
				{name: 'dynamicTable_pageSize', value: gOptions.pageSize},
				{name: 'dynamicTable_nextPage', value: pageNo},
				//{name: 'dynamicTable_page', value: ydl.common.parameters["$page"]},
				{name: 'dynamicTable_page', value: poolSelect["$page"]},
				{name: 'dynamicTable_paging', value: gOptions.paging},
				{name: 'dynamicTable_configSqlCheck', value: ydl.localData('localconfig_sqlCheck')},
				{name: '_DATAPOOL_', value: document.getElementsByName('_DATAPOOL_')[0].value}
			];
			//where条件
			if (options.where) postData.push({name: 'whereClause', value: options.where});
			postData.push({name: 'errorFilter', value: options.errorFilter || '1=1'});
			//处理选中删除的记录标志
			var $checks = $table.find('._datalist_delete_checkbox:checked');
			var $flag = $table.find('input[name=dynamicTable_flag]');
			if (gOptions.allowDelete || gOptions.allowInsert) {
				$checks.each(function() {
					var flag = this.nextSibling;
					if (flag.value == FLAG_ORIGINAL) flag.value = FLAG_DELETED;	//原数据删除
					else if (flag.value == FLAG_NEW) flag.value = FLAG_ABORTED;	//新增数据删除
				});
			}
			//处理修改过的记录标志
			var colIndex, colCount, dataIndex, dataCount;
			if (gOptions.allowUpdate) {
				var $rows = $table.find('tr:gt(0):not(._datalist-tip)');
				for (dataIndex = 0, dataCount = gOptions.data.length; dataIndex < dataCount; dataIndex++) {
					var flag = $flag[dataIndex];
					if (flag.value == FLAG_ORIGINAL) {	//只检查未删除的原有记录
						var rowModified = false;
						for (colIndex = 0, colCount = gOptions.columns.length; colIndex < colCount; colIndex++) {
							var gColumn = gOptions.columns[colIndex];
							if (gColumn.colType == COLUMN_EDITABLE) {	//只检查可编辑列
								if (gOptions.data[dataIndex][gColumn.id] !== undefined) {
									var currentData = $rows.eq(dataIndex).find(':input[name=' + gColumn.id + ']').val();
									var originalData = gOptions.data[dataIndex][gColumn.id];
									if (gColumn.dataType === 'money' && parseFloat(ydl.delComma(currentData)) != parseFloat(originalData) ||
										gColumn.dataType !== 'money' && currentData.trim() != originalData.trim()) {
										rowModified = true;
										break;
									}
								}
							}
						}
						if (rowModified) flag.value = FLAG_MODIFIED;
					}
				}
			}
			//ydl.log('flag = ' + $flag.map(function(){return this.value}).get().join());
			//查找需要提交的表格行和行内的表单元素，供表单校验回调函数使用
			var inputs = [];
			var rows = [];
			$table.find('._datalist_delete_checkbox:not(:checked)').each(function() {	//检查没有选中删除的行
				var $tr = $(this).closest('tr');
				var flag = $tr.find('input[name=dynamicTable_flag]').val();
				if (flag == FLAG_NEW || flag == FLAG_MODIFIED) {
					inputs = inputs.concat($tr.find(':input[name]:visible').get());
					rows = rows.concat($tr[0]);
				}
			});
			if (rows.length === 0 && $checks.length == 0 && fromSaveButton) {
				ydl.log('列表中的数据没有改动，无需提交保存。', 'orange');
				alert('保存成功！');
				return false;
			}
			//放弃更改标志
			var cancelUpdate = rows.length > 0 && fromSaveButton === undefined && !confirm('列表中的数据有改动，是否保存？\n\n点击【确定】自动保存更改\n点击【取消】放弃更改');
			if (!cancelUpdate) {
				//根据required、maxlength、datatype属性做数据校验
				for (var i = 0; i < rows.length; i++) {
					var allBlank = true;	//判断是否一行中所有字段的值都为空
					var hasEditable = false;	//是否有可编辑的字段
					for (colIndex = 0, colCount = gOptions.columns.length; colIndex < colCount; colIndex++) {
						var gColumn = gOptions.columns[colIndex];
						var $field = $(rows[i]).find(':input[name=' + gColumn.id + ']');
						if (gColumn.colType == COLUMN_EDITABLE) {
							hasEditable = true;
							allBlank = allBlank && ($field.val() == '');
							var rule = {addcomma: false};
							if (gColumn.required) rule['required'] = true;
							if (gColumn.maxLength != '') rule['length2'] = [0, gColumn.maxLength];
							if (gColumn.dataType != '') rule['type'] = gColumn.dataType;
							if (!ydl.validator($field, rule)) return false;
						}
					}
					//如果是新记录，并且全部为空白，自动删除，不提交
					if (hasEditable && allBlank) {
						if ($(rows[i]).find('input[name=dynamicTable_flag]').val() == FLAG_NEW) {
							$(rows[i]).find('input[name=dynamicTable_flag]').val(FLAG_ABORTED);
							$(rows[i]).remove();
							rows.splice(i--, 1);
						}
					}
				}
				if (rows.length === 0 && $checks.length == 0 && fromSaveButton) {
					alert('列表中的数据没有改动，无需保存。');
					return false;
				}

				//提交前执行页面内定义的回调函数，传入新增和修改的表单对象数组，返回false可以阻止提交
				var validateFunc = window['datalist_validate_' + tableId];
				if (validateFunc && $.isFunction(validateFunc) && !validateFunc(inputs, rows)) return false;

				//每一列是否是字符类型
				var ischar = {};
				for (colIndex = 0, colCount = gOptions.columns.length; colIndex < colCount; colIndex++) {
					var gColumn = gOptions.columns[colIndex];
					ischar[gColumn.id] = gColumn.ischar;
				}

				//向提交数据中添加列表数据（字段名前加“列表ID:”以防重名）
				var changed = false;
				$flag.each(function() {
					if (this.value == FLAG_NEW || this.value == FLAG_DELETED || this.value == FLAG_MODIFIED) {
						changed = true;
						$(this).closest('tr').find(':input[name]').each(function() {
							postData.push({
								name: tableId + ':' + this.name,
								//数字类型的空值转成null，避免sql出错
								value: this.value == '' && !ischar[this.name] ? 'null' : this.value
							});
						});
					}
				});
				//向提交数据中添加列表外数据（不含其他列表容器和动态列表组件）
				$('table:not(.datalist) :input[name]:enabled,div.container>:input[name]:enabled,div>input[type=hidden]').each(function() {
					postData.push({
						name: this.name,
						value: this.value
					});
				});
			}
			//同步提交
			var success = false;
			if ($checks.length == 0 || $checks.length > 0 && (cancelUpdate || confirm('请确认是否要删除选择的 ' + $checks.length + ' 条记录？'))) {
				ydl.ajax(ydl.contexPath + "/dynamictable", postData, function(returnData) {
					//更新全局对象
					$.extend(ydl.data.dataList[tableId], {
						data: returnData.data,
						currentPage: returnData.currentPage,
						pageCount: returnData.pageCount,
						totalCount: returnData.totalCount,
						pageSize: returnData.pageSize
					});
					//更新列表
					updatePage();
					//设置页码
					$table.pagebar({
						currentPage: parseInt(returnData.currentPage),
						pageCount: parseInt(returnData.pageCount),
						totalCount: parseInt(returnData.totalCount)
					});
					callback();
					success = true;
					//保存成功后的回调函数
					var changedFunc = window['datalist_changed_' + tableId];
					if (changed && changedFunc && $.isFunction(changedFunc)) changedFunc();
				}, {async: false});
			}
			else {
				//取消删除
				$table.find('._datalist_delete_checkbox:checked').removeAttr('checked').each(function(index, ele) {
					var flag = ele.nextSibling;
					if (flag.value == FLAG_DELETED) flag.value = FLAG_ORIGINAL;
					else if (flag.value == FLAG_ABORTED) flag.value = FLAG_NEW;
				});
			}

			//设置页码栏宽度
			var width = parseInt(table.clientWidth) - 12;
			$table.next().css({'max-width': width, 'width': width});

			return success;
		}
	};

	/**
	 * 初始化数据列表（list容器），支持分页、排序，支持同一个页面上多个list
	 * 在每次翻页或排序之前调用页面中定义的回调函数：list_validate_组件ID(rows)，传入列表所有的表格行作为参数，返回false可阻止提交
	 * 在每次翻页或排序之后调用页面中定义的回调函数：list_callback_组件ID(rows)，传入列表所有的表格行作为参数
	 * 每个list组件的table标签后跟随一个隐藏<div id="list_组件ID_parameter">，其中包含list中所有hidden字段，和以下hidden参数：
	 * list_组件ID_subtr：每条记录占用的表格行数
	 * list_组件ID_dataset：list组件相关的数据集ID，用于翻页
	 * list_组件ID_currentpage：当前页码
	 * list_组件ID_paging：是否分页
	 * list_组件ID_pagesize：每页记录数
	 * list_组件ID_pagecount：总页数
	 * list_组件ID_totalcount：总记录数
	 * list_组件ID_maxsize：最大记录数，结果集中超过这个数量的记录不返回
	 * @param {String} listId 列表ID
	 * @param {Object} [paras] 查询条件
	 * @modified 姬爽 2012-07 添加筛选功能
	 */
	ydl.init.list = function(listId, paras) {
		var $list = $('#' + listId);
		//全局对象
		if (!ydl.data.listParameter) ydl.data.listParameter = {};
		var listParameter = ydl.data.listParameter[listId];
		if (paras) {
			//条件查询
			listParameter.parameters = paras;
			var r = doQuery(1);
			$list.pagebar({currentPage: r.currentPage, pageCount: r.pageCount, totalCount: r.totalCount});
		}
		else {
			//初始化
			if (listParameter) return;	//如果列表已初始化则退出（翻页时）
			else {
				listParameter = ydl.data.listParameter[listId] = {
					sort: '',		//排序条件子句
					filter: '',		//筛选条件子句
					curfilter: '',	//当前点击的筛选字段ID
					filterIds: [],	//当前所有已筛选字段ID数组
					parameters: null	//查询参数
				};
				ydl.log('列表' + listId + '已初始化。');
			}
			//如果列表可翻页，且尚未创建页码栏，则创建页码栏
			if (ele('paging') && ele('paging').value == 'true') {
				if (!ele('buttons')) $list.pagebar({
					currentPage: ele('currentpage').value,
					pageCount: ele('pagecount').value,
					totalCount: ele('totalcount').value,
					gotoPage: doQuery,
					buttons: {
						'export': {
							icon: 'circle-arrow-s',
							text: '导出',
							click: function() {
								listExport(ele('currentpage').value,ele('pagecount').value);
							}
						}
					}
				});
			}
			//不分页时，如果显示的记录数小于实际记录数，在表格下方显示提示信息
			else {
				if (parseInt(ele('maxrows').value) < parseInt(ele('totalcount').value)) {
					var cells = 0;	//计算最后一行合并前的单元格数
					$list.find('tr:last td').each(function() {
						cells += this.colSpan;
					});
					$list.append('<tr><td colspan="' + cells + '" class="ui-state-highlight _list-tip"><span class="ui-icon ui-icon-info info-icon"></span>查询结果共 ' +
						ele('totalcount').value + ' 条记录，本页只显示前 ' + ele('maxrows').value + ' 条记录。</td></tr>');
				}
				/*
                //添加导出按钮
                $list.after('<div class="buttons"><button type="button" id="'+listId+'_buttons_export" class="button">导出</button></div>');
                $('#'+listId+'_buttons_export').button({icons: {primary: 'ui-icon-circle-arrow-s'}}).click(function(){
                    listExport('1','1');
                });
                */
			}
			//初始化排序标签
			initSort();
			//初始化筛选
			initFilterData();
			//设置表格行背景色
			ydl.themes.rowColor(document.getElementById(listId));
			//回调函数
			window['list_callback_' + listId] && window['list_callback_' + listId](document.getElementById(listId).rows);
		}
		//导出
		function listExport(currentpage,pagecount){
			var paras = [
				//{name: '$page', value: ydl.common.parameters['$page']},
				{name: '$page', value: poolSelect['$page']},
				{name: 'export_componentType', value: 'list'},
				{name: 'export_componentId', value: listId},
				{name: 'export_currentPage', value: currentpage},
				{name: 'export_pageCount', value: pagecount}
			];
			$('table:not(.datalist),div').each(function(index, ele) {
				paras = paras.concat(ydl.getInputs(ele));
			});
			ydl.dialog.open(ydl.contexPath + '/platform/listExport.jsp', paras, null, { width: 500, height: 250 });
		}
		//取列表参数值
		function ele(paraName) {
			return document.getElementById('list_' + listId + '_' + paraName) || '';
		}
		//初始化排序标签
		//排序标签含两个class：<label class="sortable _sortname_xxxxx">，其中xxxxx是排序字段名，也可以是"表名.字段名"格式
		//在SQL语句的合适位置添加：getPoolValue("数据集ID_order_by", "默认排序字段名")
		function initSort() {
			var sort = listParameter.sort;
			var $list = $('#' + listId);
			//给标签添加升降序标记
			var sortArr = sort.split(/[,\s]+/);
			$list.find('label._sortname_' + sortArr[0].replace('.', '\\.')).after('<span style="float: left" class="ui-icon ui-icon-arrowthick-1-' +
				(sortArr.length > 1 && sortArr[1] === 'desc' ? 's' : 'n') + ' _list_sort_icon"></span>');
			//绑定点击标签排序事件
			$list.find('label.sortable').click(function() {
				doSort(this.className.match(/\b_sortname_(.+)\b/)[1]);
			});
		}

		//执行排序
		function doSort(field, isAsc) {
			var sort = listParameter.sort;
			//点击后将当前标签所定义的字段名排在最前面，再次点击切换desc
			if (isAsc === undefined) {
				var regex1 = new RegExp('\\b' + field.replace('.', '\\.') + ' desc\\b');
				var regex2 = new RegExp('\\b' + field.replace('.', '\\.') + '\\b');
				if (regex1.test(sort)) sort = field + ',' + sort.replace(regex1, '');
				else if (regex2.test(sort)) sort = field + ' desc,' + sort.replace(regex2, '');
				else sort = field + ',' + sort;
			}
			else {
				var regex3 = new RegExp('\\b' + field.replace('.', '\\.') + '( desc)?\\b');
				if (regex3.test(sort)) sort = field + (isAsc ? ',' : ' desc,') + sort.replace(regex3, '');
				else sort = field + (isAsc ? ',' : ' desc,') + sort;
			}
			listParameter.sort = sort.replace(/,,/g, ',').replace(/,$/, '');
			doQuery(ele('currentpage').value);
		}

		//初始化筛选
		function initFilterData() {
			var $list = $('#' + listId);
			//初始化筛选图标
			$list.find('label.sortable').each(function(index) {
				//默认时和该字段已有筛选条件时使用不同图标
				var filterIcon = $.inArray(this.className.match(/\b_sortname_(.+)\b/)[1], listParameter.filterIds) < 0 ? 'ui-icon-triangle-1-s' : 'ui-icon-circle-triangle-s';
				$(this).after('<span style="float: right; cursor: pointer;" class="ui-icon ' + filterIcon + ' _list_filter_icon"></span>');
			});
			//创建筛选浮动层
			if (!$$('filter_floatdiv')) {
				$(document.body).append('<div style="position: absolute; box-shadow: 3px 3px 6px rgb(102, 102, 102); width: 17em; padding: 2px;" class="light-border light-bg" id="filter_floatdiv">' +
					'<div style="margin-bottom:2px;"><label>当前列排序：</label><button id="sort_asc">升序</button><button id="sort_desc">降序</button></div>' +
					'<div><label>清除筛选：</label><button id="filter_data_allclear">全部</button><button id="filter_data_clear">当前列</button></div>' +
					'<ul id="filter_data" style="height:200px; overflow: auto; margin:2px;" class="light-border light-bg">' +
					'<li><input type="checkbox" id="filter_data_all" /><label for="filter_data_all">&lt;全选&gt;</label></li>' +
					'</ul>' +
					'<div class="buttons"><button id="filter_done">确定</button><button id="filter_cancel">取消</button></div>' +
					'</div>');
				$('#filter_floatdiv button').button();
				//升序
				$('#sort_asc').click(function() {
					doSort(listParameter.curfilter, true);
				});
				//降序
				$('#sort_desc').click(function() {
					doSort(listParameter.curfilter, false);
				});
				//全部选择与取消
				$('#filter_data_all').change(function() {
					$('input:gt(0)', $('#filter_data')).each(function() {
						if ($('#filter_data_all').attr('checked')) $(this).attr('checked', 'checked');
						else $(this).attr('checked', null);
					});
				});
				//清除全部筛选条件
				$('#filter_data_allclear').click(function() {
					listParameter.filter = '';
					//删除所有筛选字段
					listParameter.filterIds = [];
					$list.pagebar(doQuery(1));	//查询并更新
				});
				//清除当前列筛选条件
				$('#filter_data_clear').click(function() {
					clearFilter();
					//删除当前列的筛选字段
					listParameter.filterIds.splice($.inArray(listParameter.curfilter, listParameter.filterIds), 1);
					$list.pagebar(doQuery(1));	//查询并更新
				});
				//点击确定按钮
				$('#filter_done').click(function() {
					//删除当前列原有筛选条件
					clearFilter();
					//组织筛选条件
					var filter = [];
					$('input:gt(0)', $('#filter_data')).each(function() {
						//从data属性中取字段原有值（要求当查询出的数据在页面上被js修改时，在td上用$td.data('filter', $td.text())保存原始值）
						var filterValue = $(this).data('filter') === undefined ? $(this).next().text() : $(this).data('filter');
						//if (this.checked) filter.push(listParameter.curfilter + "='" + filterValue + "'" +
						//	(filterValue == '' ? ' or ' + listParameter.curfilter + " is null" : ''));
						if (this.checked) filter.push(listParameter.curfilter + (filterValue == '' ? ' is null' : "='" + filterValue + "'"));
					});
					//可多列筛选
					if (filter.length == 0) $filterDiv.hide();
					else {
						//修改筛选SQL
						listParameter.filter += (listParameter.filter === '' ? '' : ' and ') + '(' + filter.join(' or ') + ')';
						//添加不重复的筛选字段
						if ($.inArray(listParameter.curfilter, listParameter.filterIds) < 0)
							listParameter.filterIds[listParameter.filterIds.length] = listParameter.curfilter;
						$list.pagebar(doQuery(1));	//查询并更新
					}
				});
				//点击取消按钮
				$('#filter_cancel').click(function() {
					$filterDiv.hide();
				});
			}
			var $filterDiv = $('#filter_floatdiv').hide();
			//获得总列数
			var sumCols = 0;
			$('tr:eq(0) th', $list).each(function() {
				sumCols += this.colSpan;
			});
			//点击筛选图标时
			$list.find('._list_filter_icon').each(function() {
				//获得当前为第几列
				var colnum = 0;
				$(this).parent().prevAll().andSelf().each(function() {
					colnum += this.colSpan;
				});
				$(this).click(function() {
					clickIcon($(this), colnum);
				});
			});
			//点击筛选图标时
			function clickIcon($icon, colnum) {
				//显示浮动层时
				if ($filterDiv.is(':hidden')) {
					//设置浮动层的位置
					var position = $icon.parent().position();
					var top = position.top + parseInt($icon.parent().css('height')) + 6;
					var firstTdLeft = $icon.position().left + parseInt($icon.css('width')) - parseInt($filterDiv.css('width'));
					var left = firstTdLeft < 20 ? position.left : firstTdLeft;
					$filterDiv.css('top', top).css('left', left);
					//从当前列中查到不重复的内容，向浮动层里面添加筛选项
					var i = 0;
					var filterData = [];
					$('li:gt(0)', $('#filter_data')).remove();
					$('#filter_data_all').removeAttr('checked');
					//将待筛选数据排序
					$.each(ydl.sort($list.find('td:nth-child(' + sumCols + 'n+' + colnum + ')').map(function() {
						return {text: $(this).text(), data: $(this).data('filter')};
					}).get(), 'text'), function(index, tdItem) {
						//生成复选框
						if ($.inArray(tdItem.text, filterData) < 0) {
							$('#filter_data').append('<li><input type="checkbox" id="filter_data_' + i + '" style="margin-right:2px;">' +
								'<label for = "filter_data_' + i + '" >' + tdItem.text + '</label></li>')
								.find('#filter_data_' + i).data('filter', tdItem.data);
							filterData[i++] = tdItem.text;
						}
					});
					//获取当前列筛选的字段
					listParameter.curfilter = $icon.prev().get(0).className.match(/\b_sortname_(.+)\b/)[1];
					$filterDiv.show();
				}
				//隐藏浮动层时
				else $filterDiv.hide();
			}
			//清除当前列筛选条件
			function clearFilter() {
				listParameter.filter = $.map(listParameter.filter.split(' and '), function(item) {
					return item.split(/[( =]/)[1] == listParameter.curfilter ? null : item;
				}).join(' and ');
			}
		}

		//跳转到指定页面
		function doQuery(pageNo) {
			//提交前的校验
			if (window['list_validate_' + listId] && !window['list_validate_' + listId](document.getElementById(listId).rows)) return;
			//要提交的数据
			var postData = [
				//{name: '$page', value: ydl.common.parameters['$page']},
				{name: '$page', value: poolSelect['$page']},
				{name: 'list_id', value: listId},
				{name: 'dataset_id', value: ele('dataset').value},
				{name: 'list_page_no', value: pageNo},
				{name: ele('dataset').value + '_order_by', value: listParameter.sort},
				{name: ele('dataset').value + '_filter', value: listParameter.filter}
			];
			if (listParameter.parameters) {
				if ($.isArray(listParameter.parameters)) postData = postData.concat(listParameter.parameters);
				else if ($.isPlainObject(listParameter.parameters)) {
					postData = postData.concat($.map(listParameter.parameters, function(value, key) {
						return {name: key, value: value};
					}));
				}
			}
			ydl.log('postData = ' + ydl.getMember(postData), 'blue');
			ydl.ajax(ydl.contexPath + '/parsepage', $('form:first').serialize() + '&' + $.param(postData), function(data) {
				if (data.indexOf('<table id="' + listId + '"') >= 0) {
					//删除原列表参数div
					$(ele('parameter')).remove();
					//替换列表和参数内容
					$(data).replaceAll('#' + listId);
					//初始化排序标签
					initSort();
					//初始化筛选
					initFilterData();
					//设置表格行背景色
					ydl.themes.rowColor(document.getElementById(listId));
					//初始化按钮
					$('#' + listId + ' button').button();
					//回调函数
					window['list_callback_' + listId] && window['list_callback_' + listId](document.getElementById(listId).rows);
					//列表中第一个输入域设置焦点
					$('#' + listId + ' :input:first').focus();
					//设置页码栏宽度
					var minwidth = $.browser.msie && $.browser.version < 9.0 ? 0 : 2;
					var width = parseInt($('#'+listId).css('width')) - minwidth;
					$('#'+listId).next().next().width(width).css('max-width',width);
				}
				else alert('服务器出错！');
			}, {
				dataType: 'html',
				async: false
			});


			return {
				currentPage: ele('currentpage').value,
				pageCount: ele('pagecount').value,
				totalCount: ele('totalcount').value
			};
		}
	};

	/**
	 * 公共表单校验
	 * @param {String|Object} form 表单DOM对象（form）或其他任何容器
	 * @returns {Boolean} 是否通过校验
	 */
	ydl.formValidate = function(form, setFocus) {
		if (setFocus === undefined) setFocus = true;
		var $eles = $(':input', ydl.getDom(form));
		var r = true;
		for (var i = 0; i < $eles.length; i++) {
			r = checkFieldAttr($eles.eq(i), setFocus) && r;
		}
		return r;
	};


	/**
	 * 使一组单选框或复选框标签的宽度统一
	 * @param {Object|String} group 多值组件的ID，或者解析后的ul标签的DOM对象或jQuery对象
	 * @returns {Number} 统一的宽度（像素）
	 */
	ydl.unifyWidth = function(group) {
		var ul = typeof group === 'string' ? $$('group_' + group) : ydl.getDom(group);
		if (!ul || $(ul).hasClass('vertical')) return 0;
		var width = 0;
		var $labels = $('label', ul).each(function() {
			var offsetWidth = this.offsetWidth || this.clientWidth;
			//lo('offsetWidth='+offsetWidth,'blue');
			//隐藏时取不到offsetWidth值，利用克隆的可见对象获取
			if (offsetWidth == 0 || $(this).is(':hidden')) {
				var $clone = $(this).clone().css('display', 'block').css('position', 'absolute')
					.css('left', '-9999px').appendTo('body');
				offsetWidth = $clone[0].offsetWidth;
				$clone.remove();
				//lo('offsetWidth='+offsetWidth,'red');
			}
			if (offsetWidth > width) width = offsetWidth - 16;
		});
		if (width > 0) $labels.css('width', width + 'px');
		return width;
	};


	/**
	 * 根据字段属性检查输入值是否符合要求，不符合则显示提示
	 * @param {Object} $ele 字段的jQuery对象
	 * @param {Boolean} [setFocus] 校验后是否设置焦点，缺省为false
	 * @returns {Boolean} 符合要求返回true，否则返回false
	 */
	function checkFieldAttr($ele, setFocus) {
		if (!$ele.hasClass('_nocheck')) {	//不检查标志，例如0303模板中的修改前字段
			//检查具有required属性的字段是否为空
			if ($ele.attr('required') === 'required' && !ydl.validator($ele, {required: true, focus: setFocus})) return false;
			//检查字段类型
			if ($ele.attr('data-type') && !ydl.validator($ele, {type: $ele.attr('data-type'), declen: $ele.attr('data-declen'), focus: setFocus, keepcomma: true})) return false;
			//按半角字符检查最大长度
			if ($ele.attr('maxlength') && !ydl.validator($ele, {length2: [0, parseInt($ele.attr('maxlength'))], focus: setFocus})) return false;
		}
		return true;
	}

	/**
	 * ydpx页面初始化（页面加载之后执行）
	 * @param {Function} initComponents 组件初始化脚本
	 */
	ydl.ydpxInit = function(initComponents) {
		var initTimer = ydl.timer.start();

		//try {

		//执行组件初始化脚本
		initComponents && initComponents();

		//初始化日期控件默认值
		$('.hasDatepicker').each(function (){
			if(this.value === '1899-12-31') this.value = '';
		});

		//默认输入后检查表单字段值
		$('body').on('change', ':input', function() {
			checkFieldAttr($(this), false);
		});

		//限制不同数据类型可输入的字符
		$('body').on('keypress', 'input[type=text]', function(event) {
			var which = event.which;
			if (which != 0 && which != ydl.common.keys.Backspace && which != ydl.common.keys.Enter) {
				var dataType = $(this).attr('data-type');
				var validChar = '';
				if (dataType) {
					switch (dataType) {
						case 'date': validChar = '0123456789-'; break;
						case 'time': validChar = '0123456789:'; break;
						case 'month': case 'longmonth': case 'day': case 'longday': case 'yyyymm':
						case 'number': case 'int': case 'zipcode': validChar = '0123456789'; break;
						case 'float': case 'ipv4': validChar = '0123456789.'; break;
						case 'money': validChar = '0123456789.'; break;	//暂时不考虑负号的输入
						case 'phone': validChar = '0123456789()-'; break;
						case 'phones': validChar = '0123456789()- ;,'; break;
						case 'idcard': validChar = '0123456789Xx'; break;
					}
					var pressChar = event.char || String.fromCharCode(event.which);
					if (validChar && pressChar && validChar.indexOf(pressChar) == -1) return false;
				}
			}
		});

		//初始化双敲复核
		if (window.poolSelect && poolSelect['sq'] == 'y') {
			$('form :input').each(function() {
				if (poolSelect[this.name] != '' && !$(this).is('[readonly],[disabled],:hidden') && this.type != 'radio' && this.type != 'checkbox') {
					if (this.type == 'select-one') ydl.selectByValue(this, '');
					else this.value = '';
				}
			});
		}

		/*		//处理面包屑
                if ($('div.page-header').length == 1) {
                    var $div = $('div.page-header div');
                    if ($div.length == 0) $div = $('div.page-header').prepend('<div></div>').children('div');
                    if ($div.html().trim() == '') {
                        try {
                            var bread = [];
                            var selectedMenu = top.topMenuTree.getSelectedNodes()[0];
                            while (selectedMenu) {
                                bread.push(selectedMenu.name);
                                selectedMenu = selectedMenu.getParentNode();
                            }
                            $div.html(bread.reverse().join(' &gt; '));
                        }
                        catch (ex) {}
                    }
                }
        */
		//初始化组件
		ydl.themes && ydl.themes.rowColor();
		$('table.container label').parent().each(function() {
			if (this.tagName === 'TD' && this.innerHTML.indexOf('<a ') == -1) $(this).addClass('has-label');
		});

		//设置table容器列宽度
		$('table.container:not(.datalist)').each(function() {
			var cellCount = 0;
			if (this.rows.length > 0) {
				var cells = this.rows[0].cells;
				for (var i = 0; i < cells.length; i++) cellCount += cells[i].colSpan;
			}
			if (cellCount % 2 == 0) {
				var colStyle = '';
				var groupCount = cellCount / 2;
				var labelWidth = (0.3 * 100 / groupCount).toFixed(2);
				var fieldWidth = (0.7 * 100 / groupCount).toFixed(2);
				for (var i = 0; i < groupCount; i++) colStyle += '<col style="width:' + labelWidth + '%" /><col style="width:' + fieldWidth + '%" />';
				$(this).prepend(colStyle);
			}
		})
		//包含日期控件和组合框的表格容器单元格样式设置为不换行，避免文本框右侧图标折行
			.find('.combobox-button, .ui-datepicker-trigger, em').each(function() {
			$(this).parent().css('white-space', 'nowrap');
		});

		//统一同一组中的单选框或复选框标签宽度
		$('ul.multivalue.horizontal').each(function() {
			ydl.unifyWidth(this);
		});

		var $inputs = $('form :input:visible:not(:disabled,[readonly=readonly],[tabindex=-1])');
		if (ydl.localData('operconfig_changeFocus') == 'enter') {
			//回车自动跳转到下一个输入域，直到提交按钮
			$inputs.on('keydown', function(event) {
				if (event.which == ydl.common.keys.Enter) {
					var $thisInput = $(this);
					if ($thisInput[0].type == 'submit') {
						$thisInput.click();
						return false;
					}
					//多行文本框中回车不跳转
					else if ($thisInput[0].tagName != 'TEXTAREA') {
						var $allInputs = $('form :input:visible:not(:disabled,[readonly=readonly],[tabindex=-1])');
						var $nextInput = $allInputs.eq($allInputs.index($thisInput) + 1);
						if ($nextInput.length > 0) {
							$nextInput.focus();
						}
						else {
							if (parent.document.title == 'Flw_waittaskNew') {
								//跳转到流程控制页的提交按钮
								$thisInput.blur().change();
								parent.document.getElementById('b_submit').focus();
							}
							else {
								//跳转到本页中的提交按钮
								$('form [type=submit]').focus();
							}
						}
						return false;
					}
				}
			});
		}
		//默认将第一个表单输入域（没有绑定focus事件处理函数的）设为焦点
		var inputEvents = $inputs.eq(0).data('events');
		if (!inputEvents || !inputEvents['focus']) {
			//$inputs.eq(0).focus();
		}
		//文本输入域获得焦点时自动选择
//		$('body').on('focus', 'input:text,input:password,textarea', function() {
//		this.select();
//	});

		//执行页面中的表单校验函数
		function runFormValidate(form) {
			if (window.form_validate) {
				try {
					return form_validate(form);
				}
				catch (ex) {
					//ydl.log('表单校验时出错：' + ydl.error(ex), 'red');
					throw ex;
					return false;
				}
			}
			else return true;
		}

		//表单提交时
		$('form').submit(function() {
			var r = true;
			$('#page-tabs ul:first li:visible').each( function(index) {
				if ($(this).data('stat') != '1') {
					$('#page-tabs').tabs('select', index);
					alert('请处理页面：'+$(this).find('a').text());
					r = false;
					return false;
				}
			});
			if (!r) return false;

			//保存datalist
			var datalistSaved = true;
			$('table.datalist').each(function() {
				try {
					var gOptions = ydl.data.dataList[this.id];
					if (gOptions.allowInsert || gOptions.allowUpdate || gOptions.allowDelete)
						datalistSaved = datalistSaved && ydl.init.dataList({id: this.id}, 'refresh');
					return datalistSaved;
				}
				catch (ex) {
					ydl.log('刷新列表时出错，可能具有datalist样式的表格并非动态列表');
				}
			});
			if (!datalistSaved) return false;
			//数据校验
			if ($('input[name=_APPLY]').val() == '1' || ydl.formValidate(this) && runFormValidate(this)) {
				ydl.displayRunning();
				//去掉金额字段千分隔符
				$('form :input').each(function() {
					if ($(this).hasClass('money')) this.value = ydl.delComma(this.value);
				});

				$('button.flow_submit').prop('disabled', true);
				var form = document.forms[0];
				if ($(form).attr('data-ajax') == 'false') {
					$(form).find('select').prop('disabled', false);
					//正常表单提交
					return true;
				}
				else {
					//通过Ajax提交表单
					ydl.ajax(form.action, $(form).serialize(), function (data, oriData) {
						//提交成功
						if (oriData.message) alert(oriData.message);
						if(poolSelect["_TYPE"] == "Voucher") history.back();
						else window.location.replace(ydl.contexPath + (data.url || '/platform/welcome.jsp'));
					}, {
						async: true,
						handleError: function (data) {
							//普通错误
							if (data.returnCode == 1) alert(data.message);
							//批量错误
							else if (data.returnCode == 2) {
								if (data.message) alert(data.message);
								$('#page-tabs').data('errorFile', data.data.errorFile).bind('tabsload', function (event, ui) {
									$$('batchErrorFrame').src = ydl.contexPath + '/platform/batchError.jsp?errorFile=' + $('#page-tabs').data('errorFile');
								});
								if ($('#page-tabs ul.ui-tabs-nav a:contains(错误信息)').length == 0) {
									$('#page-tabs').tabs('add', ydl.contexPath + '/platform/batchErrorFrame.jsp', '错误信息');
								}
							}
						},
						complete: function () {
							$('button.flow_submit').prop('disabled', false);
							ydl.displayRunning(false);
						}
					});
					return false;
				}
			}
			else return false;
		});

		//调用页面中的pageOnload函数
		window.pageOnload && pageOnload();

		//设置页码栏宽度
		$('.page-buttons').each(function () {
			var $this = $(this);
			setTimeout(function() {
				//list容器与datalist容器
				var width = $this.prev().width() - ($this.prev().hasClass('container') ? ($.browser.msie && $.browser.version < 9.0 ? 0 : 2) : 12);
				$this.width(width).css('max-width', width);
			}, 100);
		});

		//}
		//catch (ex) {
		//	alert('初始化页面时出错！\n\n' + ydl.error(ex));
		//	ydl.log('ydl.ydpxInit: ' + ydl.error(ex), 'red');
		//}

		//隐藏页面加载进度条
		if ($$('page-loading-overlay')) $('#page-loading-overlay').remove();
		ydl.displayRunning(false);
		ydl.log(window.location.pathname + ' 初始化：' + ydl.timer.stop(initTimer) + 'ms');
	};


	/**
	 * 初始化自动完成功能
	 * @param {String} input 定义触发自动完成的文本输入组件ID，或DOM对象，或jQuery对象
	 * @param {String} ajaxId 定义查询语句的隐式提交ID，如果带有“common/”前缀为公共查询
	 * @param {Object} [paras] {
	 * 		{Number} [col=0] 查询返回多个字段时，指定选择哪个字段值，从0开始，缺省为0
	 * 		{Array} [colWidth] 定义各列显示宽度的数组，数组元素个数应与查询语句返回字段数相同，单位像素px
	 * 		{Number} [minLength=2] 触发查询需要输入的字符串最小长度
	 * 		{Function} [onChange] change事件处理函数
	 * 		{Function} [validate] 提交查询前的校验函数，return false可阻止提交
	 * }
	 * @returns undefined
	 * @author 姬爽 2012-7
	 */
	ydl.autoComplete = function(input, ajaxId, paras) {
		input = ydl.getDom(input);
		var $input = $(input);

		//初始化自动完成相关参数
		$input.autocomplete({
			minLength: paras.minLength || 2,
			source: function(request, response) {
				//发送请求，提交表单内所有字段，不包括列表容器和datalist中的字段
				var ajaxParas = $('.container:not(.datalist)').getInputs(true).concat([
					//{name: '$page', value: ajaxId.indexOf('common/') == 0 ? 'common/commonAjax.ydpx' : ydl.common.parameters['$page']},
					{name: '$page', value: ajaxId.indexOf('common/') == 0 ? 'common/commonAjax.ydpx' : poolSelect['$page']},
					{name: 'ajax_query_id', value: ajaxId.indexOf('common/') == 0 ? ajaxId.substr(7) : ajaxId},
					{name: 'term', value: request.term}
				]);
				var source = [];
				ydl.ajax(ydl.contexPath + '/ajax', ajaxParas, function(data) {
					//将{'字段名':'字段值'}转换为{label:'xxx',value:'xxx',autoshow:'xxx'}
					//label:隐藏检索数据,
					//value:选择显示值,返回col指定的字段,
					//autoshow:自动完成显示列表值,暂默认为查询的所有字段,以空格分隔
					source = $.map(data, function(record, index) {
						var arrRecord = $.map(record, function(value, key) {
							return record[key];
						});
						var label = arrRecord.join(' ');
						//lm(arrRecord, 'blue');
						return {
							label: label + ' ' + input.value,
							value: arrRecord[paras.col || 0],
							autoshow: paras.colWidth ? $.map(arrRecord, function(field, index) {
								return '<span class="light-border" style="display: inline-block; overflow: hidden; margin-right: 5px; border-width: 0 1px 0 0; width:' + (paras.colWidth)[index] + 'px">' + field + '</span>';
							}).join('') : label
						};
					});
					//lm(source,'red');
					response(source);
				}, {
					handleError: function() {},
					async: true
				});
			},
			focus: function(event, ui) {
				$input.val(ui.item.value);
			},
			change: function(event, ui) {
				if (paras.onChange) paras.onChange(event);
			},
			search: function(event, ui) {
				//检查字段类型
				if ($input.attr('data-type') && !ydl.validator($input, {type: $input.attr('data-type'), declen: $input.attr('data-declen'), focus: false, keepcomma: true})) return false;
				return !paras.validate || paras.validate && paras.validate(input);
			}
		})
		//设置自动完成显示列表显示样式
			.data('autocomplete')._renderItem = function(ul, item) {
			return $('<li></li>')
				.data('item.autocomplete', item)
				.append('<a>' + item.autoshow + '</a>')
				.appendTo(ul);
		};
	};

	/**
	 * 给列表添加总计行
	 * @param {Object} options 参数 { = {list:list,source:source,cols:cols,[label:label],refresh:refresh}
	 * 		{String|Object} list 列表容器或动态列表组件的id，或DOM对象，或jQuery对象
	 * 		{String} source 数据来源，可以是当前页面中的隐式提交ID，或者隐式报文交易码
	 * 		{Array|Object} cols 要显示总计的列ID（如['money','age']），或列序号（如[2,4,5]），
	 * 		或对应关系（如列ID和返回字段名对应{'money':'summoney', 'age': 'sumage'}或列序号和返回字段名对应{'2':'sum2','4':'sum4'}）
	 * 		说明：列序号为除去删除列与行号列之外的第一列（序列号0）开始算起,暂包含已隐藏的列--2012/07/05
	 * 		{String} [label='总计'] 自定义行标签，缺省为“总计”，也可以自定义为“平均值”等其他内容
	 * 		{Boolean} refresh 是否更新数据
	 * 		{Object} paras 隐式报文附加参数
	 * }
	 * @returns {Object} 新添加行的DOM对象（tr）
	 * @author 姬爽 2012-7
	 */
	ydl.addSumRow = function(options) {
		var list = ydl.getDom(options.list);
		var cols = options.cols;
		var listId = list.id;
		//初始化时，或者需要更新数据刷新时
		if (!ydl.data.addSumRow || !ydl.data.addSumRow[listId] || options.refresh){
			var data;//隐式提交或者隐式报文返回数据
			//source是隐式提交id时
			if (ydl.data.ajaxFunctions && ydl.data.ajaxFunctions[options.source]) {
				data = ydl.init.ajax(options.source,$('.container:not(.datalist)').getInputs(true));
				//查询出错时
				if(data == null) {
					ydl.log('隐式提交查询出错','red');
					return;
				}
				else data = data[0];
			}
			//source 是隐式报交易码时
			else {
				data = options.paras ? ydl.workflow.sendMessage(source, options.paras) : ydl.workflow.sendMessage(source);
				//查询出错时
				if(data == null) {
					ydl.log('隐式报交易查询出错','red');
					return;
				}
			}
			//得到列表容器或者动态列表组件的页面总列数(包含页面隐藏列)
			var sumcols = 0;
			$('#'+listId +' tr:eq(0) th').each(function() {
				sumcols += this.colSpan;
			});
			var $tr = $('<tr>' + ydl.string('<td></td>', sumcols) + '</tr>');

			//待修改--隐藏 页面上已隐藏的列数, 列表容器多行标题行时
			$('#'+listId +' tr:eq(0) th').each(function(index) {
				//lm($(this).is(':hidden'),'red')
				//if($(this).hasClass('hide')) $tr.children().eq(index).addClass('hide');
				if($(this).is(':hidden')) $tr.children().eq(index).addClass('hide');
			});

			//默认为list容器，算页面上真正的序列号时用,包含隐藏列
			var delnum = 0;
			//若为datalist容器时,转换cols格式为[2,4]
			if(ydl.data.dataList && ydl.data.dataList[listId]) {
				var showRowId = ydl.data.dataList[listId].showRowId;
				delnum = showRowId ? 2 : 1;
				//转换cols格式 {'money':'summoney', 'age': 'sumage'}->['money','age']或者{'2':'sum2','4':'sum4'}->[2,4]
				if(typeof cols[0] != 'number' && typeof cols[0] != 'string'){
					cols = $.map(cols, function(value, key) {
						if(/^[\d]+$/.test(key)) return parseInt(key);
						else return key;
					});
				}
				//cols 为['money','age']格式时
				if(typeof cols[0] == 'string'){
					var columns = ydl.data.dataList[listId].columns
					cols = $.map(columns, function(column, index) {
						if($.inArray(column.id,cols) >= 0) return index;
					});
				}
			}
			//处理返回数据
			var arrRecord = $.map(data, function(value, key) {
				return data[key];
			});
			//向总计行填充数据
			for(var index = 0 ; index < cols.length ; index ++){
				$tr.children().eq(cols[index] + delnum).text(arrRecord[index]);
			}
			//添加一行总计标识,暂无考虑删除列问题
			var $selectLabelTr = $('<tr class="ui-state-disabled"><td colspan = "'+sumcols+'">'+ (options.label ? options.label:'总计') + '：</td></tr>');
			var tfoot = $('<tfoot></tfoot>').append($selectLabelTr).append($tr);
			$(list).append(tfoot);
			//将当前 addSumRow 保存到全局对象中
			if (!ydl.data.addSumRow) ydl.data.addSumRow = {};
			ydl.data.addSumRow[listId] = {
				source: tfoot.html()
			};
		}
		//已初始化 且不需要刷新时
		else {
			var addSumRowHtml = $.map(ydl.data.addSumRow[listId], function(value, key) {
				return '<tfoot>' + value + '</tfoot>';
			});
			$('#' + listId).append(addSumRowHtml[0]);
		}
	};

	/**
	 * 显示或隐藏扩展面板
	 * @param {String} type 类型(edoc=电子档案)
	 * @param {Boolean} show 显示或隐藏
	 * @param {String|Object} [data] 向右边栏传递的数据
	 * @returns undefined
	 */
	ydl.showPanel = function(type, show, data) {
		if (type == 'edoc') {
			if(show){
				var frame = top.document.getElementById('right-frame');
				if (frame) {
					var url = ydl.contexPath + '/platform/edoc/main.jsp';
					if(data){
						url+='?';
						for(var name in data){
							url+=name+'='+data[name]+'&';
						}
						url=url.substring(0,url.length-1);
					}
					frame.src=url;
					top.rside.show();
				}
				else alert('无法打开电子档案面板。');
			}else{
				top.rside.hide();
			}
		}
	};

	/**
	 * 初始化标题行折叠功能
	 * @param {String} containerId 表格容器id
	 * @param {Boolean} foldOnInit 初始化时是否折叠状态
	 * @returns {Object} 该容器中所有标题行的jQuery对象集合
	 * @author 姬爽 2012-8
	 */
	ydl.foldHeader = function(containerId, foldOnInit) {
		//遍历表格容器标题行
		var headers = [];
		$('#' + containerId + ' tr.ui-widget-header').each(function() {
			var $tr = $(this).css('cursor', 'pointer');
			var $icon = $('<span class="ui-icon ui-icon-triangle-1-s" style="float: left;"></span>');
			$('td', this).prepend($icon);
			$(this).click(function() {
				//已展开
				if ($icon.hasClass('ui-icon-triangle-1-s')) {
					$tr.nextUntil('tr.ui-widget-header').addClass('hide');
					$icon.removeClass('ui-icon-triangle-1-s').addClass('ui-icon-triangle-1-e');
				}
				//未展开
				else {
					$tr.nextUntil('tr.ui-widget-header').removeClass('hide');
					$icon.removeClass('ui-icon-triangle-1-e').addClass('ui-icon-triangle-1-s');
				}
			});
			headers.push($tr);
			if (foldOnInit) $tr.click();
		});
		return $(headers);
	};

	/**
	 * 静态列表数据导出excel文件
	 * @param {String|Object} table 列表表格的ID属性，或DOM对象或jQuery对象
	 * @returns undefined
	 */
	ydl.listExport = function(table) {
		table = ydl.getDom(table);
		var data = [{name: 'filename', value: 'list'}];
		$('tr', table).each(function() {
			data.push({name: 'row', value: $('td:visible,th:visible', this).map(function() {
					return $(this).text().replace(/"/g, '&quot;').replace(/~/g, '～');
				}).get().join('~')});
		});
		var form = ydl.createForm(ydl.contexPath + '/exportStaticData', data);
		form.submit();
		$(form).remove();
	};

})(ydl, jQuery);