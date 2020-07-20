/************************************************************************************************
 * 华信永道住房公积金平台公共脚本库
 * ydl.themes.js 皮肤主题支持库（需要jQuery UI库）
 * @modified 修改人 修改时间 修改内容描述
 ************************************************************************************************/

if (!ydl) ydl = {};

(function(ydl, $, undefined) {

/**
 * 平台样式主题
 */
ydl.themes = {
	//当前选择的主题
	'current': 'official',
	//主题信息库（desc: 主题描述，ui: 对应的jQueryUI主题名称，yd: 对应的平台主题名称）
	'lib': {
		'default': 	{desc: '默认主题', ui: 'official', 		yd: 'official'}
	}
};

/**
 * 应用主题
 * @param {String} [themeName='default'] 主题名称
 * @returns undefined
 */
ydl.themes.use = function(themeName) {
	themeName = (themeName && ydl.themes.lib[themeName]) ? themeName : 'default';
	if (ydl.themes.current != themeName) {
		ydl.themes.current = themeName;
		//加载样式表
		var css = document.getElementById('jqueryui-themes-css');
		if (css) css.href = ydl.contexPath + '/ui/themes_ui/' + ydl.themes.lib[themeName].ui + '/jquery-ui.css';
		css = document.getElementById('platform-themes-css');
		if (css) css.href = ydl.contexPath + '/ui/themes_yd/' + ydl.themes.lib[themeName].yd + '/yd_styles.css?uuid=' + ydl.uuid();
		//更新日期控件选择图标
		$('img.ui-datepicker-trigger').attr('src', ydl.contexPath + '/ui/themes_yd/' + ydl.themes.lib[themeName].yd + '/icon_calendar.png');
		//递归应用到所有frames
		$('frame, iframe').each(function() {
			try {
				this.contentWindow.ydl.themes.use(themeName);
			}
			catch (ex) {}
		});
	}
};

/**
 * 设置列表行交替颜色
 * @param {Object} [table] 表格对象（DOM或jQuery对象或选择符），缺省为页面中所有datalist样式的表格
 * @returns 所选表格的jQuery对象
 */
ydl.themes.rowColor = function(table) {
	var $table = table ? $(table) : $('table.datalist');
	$table.each(function(index, ele) {
		//根据list容器的subtr参数判断不同记录所在行
		var listSubTr = document.getElementById('list_' + ele.id + '_subtr');
		if (ele.id && listSubTr) {
			var listSubTrValue = parseInt(listSubTr.value);
			$('tr:not(.ui-widget-header):visible', ele).each(function(i) {
				if (Math.floor(i / listSubTrValue) % 2 == 1) $(this).addClass('alt');
				else $(this).removeClass('alt');
			});
		}
		//一般列表，按奇偶行交替
		else {
			$('tr:not(.ui-widget-header):visible:odd', ele).addClass('alt');
			$('tr:not(.ui-widget-header):visible:even', ele).removeClass('alt');
		}
		//可以选择表格行
		var $ele = $(ele);
		var $trs = $ele.find('tr');
		if ($ele.hasClass('row-selectable') || $ele.hasClass('row-tracking')) {
			$trs.mouseover(function() {
				$(this).addClass('hover');
			}).mouseout(function() {
				$(this).removeClass('hover');
			});
		}
	});
	return $table;
};


$(function() {

	//应用样式主题（加载样式表）
	//ydl.themes.use(ydl.localData('operconfig_theme'));
	//if ($.fn.button) $('button').button();
	$('button').each(function () {
		if ($(this).children('span').length == 0) $(this).wrapInner('<span></span>');
	});
	
});

})(ydl, jQuery);
