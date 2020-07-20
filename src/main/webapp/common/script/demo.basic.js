/************************************************************************************************
 * demo脚本库
 ************************************************************************************************/
'use strict';
var demo = {};

(function(demo, $, undefined){
	
	/**
	 * 根据字典项转换释义
	 * @param {String} value 原始值
	 * @param {Array} paraValue 多级字典值
	 * @param {String} value 翻译后的显示值
	 */
	function keyValue(value, paraValue) {
		// 字符串转数组
		var arrs = value.split(',');
		// 二维数组转obj
		var dicts = ydl.arr2obj(paraValue, 0, function (d) {return d[1]});
		// value置空
		value = '';
		var valueArr = [];
		// 翻译字段变为对应释义字典数据字符串
		$.each(arrs, function(index, arr){
			$.each(dicts, function(dictkey, dictValue){
				if(arr == dictkey) valueArr.push(dictValue);
			});
		});
		// 转换为以逗号分隔字符串
		value = valueArr.join(',');
		return value;
	}
	
	/**
	 * 反显表单数据
	 * @param {Object} data 表单json数据,key值与id值相对应
	 */
	demo.fillForm = function(data){
		$.each(data,function (key, value) {
			// 赋值
	    	ydl.setValue(key,value);
	    });
	};
	
	/**
	 * 根据json数据生成静态列表
	 * @param {String} id 表格id
	 * @param {Json} data 表格json数据（json数据按照表头配置顺序排列）
	 * @param {Object} paraph 配置信息
	 */
	demo.fillList = function(id, data, paraph){
		var html = '';
		// 生成表格
		$.each(data, function(index, message){
			html += '<tr>';
			$.each(message, function(key, value){
				if(key.split('_')[1] != 'url') {
					var isExitKey = false;
					// 判断是否存在配置信息			
					if(paraph){
						$.each(paraph, function(paraKey, paraValue){
							// 判断字段是否需转换
							if(key == paraKey){
								isExitKey = true;
								// 判断字段是否数组，为数组时默认转换字典释义
								if(paraValue instanceof Array) {
									// 根据字典项转换释义
									html += '<td><p class="form-control-static">'+ keyValue(value, paraValue) +'</p></td>';
								}
								// 为对象时根据配置信息生成列html结构
								else if(paraValue instanceof Object) {
									// 根据字典项转换释义
									if(paraValue.type == 'dict') {
										html += '<td><p class="form-control-static">'+ keyValue(value, paraValue.dict) +'</p></td>';
									}
									// 链接列
									else if(paraValue.type == 'link') {
										var dictValue = '', isExit = false, mValue = '';
										// 存在多级字典						
										if(paraValue.dict) {
											// 判断value
											if(value.length > 0) {
												dictValue = keyValue(value, paraValue.dict);
											}
											else if(value.length < 1 && paraValue.text && (paraValue.text).length > 0) {
												dictValue = keyValue(paraValue.text, paraValue.dict);
											}
											else {
												$.each(message,function(messageKey, messageValue){
													if(messageKey == (value + '_url')) {
														isExit = true;
														mValue = messageValue;
														dictValue = keyValue(messageValue, paraValue.dict);
													}
												});
												if(isExit == false && paraValue.url && (paraValue.url).length > 0) {
													dictValue = keyValue(paraValue.url, paraValue.dict);
												}
											}
											// 判断url
											if(isExit == false && paraValue.url && (paraValue.url).length > 0) {
												mValue = paraValue.url;
											}
											html += '<td><div><a href="'+ mValue +'">'+ dictValue +'</a></div></td>';
										}
										// 不存在多级字典	
										else {
											// 判断value
											if(value.length > 0) {
												dictValue = value;
											}
											else if(value.length < 1 && paraValue.text && (paraValue.text).length > 0) {
												dictValue = paraValue.text;
											}
											else {
												$.each(message,function(messageKey, messageValue){
													if(messageKey == (value + '_url')) {
														isExit = true;
														mValue = messageValue;
														dictValue = messageValue;
													}
												});
												if(isExit == false && paraValue.url && (paraValue.url).length > 0) {
													dictValue = paraValue.url;
												}
											}
											// 判断url
											if(isExit == false && paraValue.url && (paraValue.url).length > 0) {
												mValue = paraValue.url;
											}
											html += '<td><div><a href="'+ mValue +'">'+ dictValue +'</a></div></td>';
										}
									}
									// 按钮列
									else if(paraValue.type == 'button') {
										var dictValue = '', isExit = false;
										// 存在多级字典						
										if(paraValue.dict) {	
											// 判断value
											if(value.length > 0) {
												dictValue = keyValue(value, paraValue.dict);
											}
											else if(value.length < 1 && paraValue.text && (paraValue.text).length > 0) {
												dictValue = keyValue(paraValue.text, paraValue.dict);
											}	
											html += '<td><div>';
											$.each(dictValue.split(','),function(bIndex,bText){
												if(bText < 1) bText = '未命名';
												var bClass = 'b' + bIndex + '-' + key;
												html += '<button type="button" class="btn btn-primary btn-xs '+ bClass +'" style="margin-right: 3px;">'+ bText +'</button>';
											});
											html += '<div style="clear:both"></div></div></td>';
										}
										// 不存在多级字典	
										else {
											// 判断value
											if(value.length > 0) {
												dictValue = value;
											}
											else if(value.length < 1 && paraValue.text && (paraValue.text).length > 0) {
												dictValue = paraValue.text;
											}
											html += '<td><div>';
											$.each(dictValue.split(','),function(bIndex,bText){
												if(bText < 1) bText = '未命名';
												var bClass = 'b' + bIndex + '-' + key;
												html += '<button type="button" class="btn btn-primary btn-xs '+ bClass +'" style="margin-right: 3px;">'+ bText +'</button>';
											});
											html += '<div style="clear:both"></div></div></td>';
										}
									}
								}
							}
						});
					}
					if(isExitKey == false) html += '<td><p class="form-control-static">'+ value +'</p></td>';
				}
			});
			html += '</tr>';
		});
		$(html).appendTo($('#'+ id +' tbody'));
		//设置表格页码栏总条数
		$('#page_bar_'+ id +'').find('.totalCount').text('共 '+data.length+' 条');
		// 调整表体列宽与表头一致
		ydl.listColWidth(id);
	};
	
})(demo, jQuery)