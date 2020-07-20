/************************************************************************************************
 * ��������ס��������ƽ̨�����ű���
 * ydl.themes.js Ƥ������֧�ֿ⣨��ҪjQuery UI�⣩
 * @modified �޸��� �޸�ʱ�� �޸���������
 ************************************************************************************************/

if (!ydl) ydl = {};

(function(ydl, $, undefined) {

/**
 * ƽ̨��ʽ����
 */
ydl.themes = {
	//��ǰѡ�������
	'current': 'official',
	//������Ϣ�⣨desc: ����������ui: ��Ӧ��jQueryUI�������ƣ�yd: ��Ӧ��ƽ̨�������ƣ�
	'lib': {
		'default': 	{desc: 'Ĭ������', ui: 'official', 		yd: 'official'}
	}
};

/**
 * Ӧ������
 * @param {String} [themeName='default'] ��������
 * @returns undefined
 */
ydl.themes.use = function(themeName) {
	themeName = (themeName && ydl.themes.lib[themeName]) ? themeName : 'default';
	if (ydl.themes.current != themeName) {
		ydl.themes.current = themeName;
		//������ʽ��
		var css = document.getElementById('jqueryui-themes-css');
		if (css) css.href = ydl.contexPath + '/ui/themes_ui/' + ydl.themes.lib[themeName].ui + '/jquery-ui.css';
		css = document.getElementById('platform-themes-css');
		if (css) css.href = ydl.contexPath + '/ui/themes_yd/' + ydl.themes.lib[themeName].yd + '/yd_styles.css?uuid=' + ydl.uuid();
		//�������ڿؼ�ѡ��ͼ��
		$('img.ui-datepicker-trigger').attr('src', ydl.contexPath + '/ui/themes_yd/' + ydl.themes.lib[themeName].yd + '/icon_calendar.png');
		//�ݹ�Ӧ�õ�����frames
		$('frame, iframe').each(function() {
			try {
				this.contentWindow.ydl.themes.use(themeName);
			}
			catch (ex) {}
		});
	}
};

/**
 * �����б��н�����ɫ
 * @param {Object} [table] ������DOM��jQuery�����ѡ�������ȱʡΪҳ��������datalist��ʽ�ı��
 * @returns ��ѡ����jQuery����
 */
ydl.themes.rowColor = function(table) {
	var $table = table ? $(table) : $('table.datalist');
	$table.each(function(index, ele) {
		//����list������subtr�����жϲ�ͬ��¼������
		var listSubTr = document.getElementById('list_' + ele.id + '_subtr');
		if (ele.id && listSubTr) {
			var listSubTrValue = parseInt(listSubTr.value);
			$('tr:not(.ui-widget-header):visible', ele).each(function(i) {
				if (Math.floor(i / listSubTrValue) % 2 == 1) $(this).addClass('alt');
				else $(this).removeClass('alt');
			});
		}
		//һ���б�����ż�н���
		else {
			$('tr:not(.ui-widget-header):visible:odd', ele).addClass('alt');
			$('tr:not(.ui-widget-header):visible:even', ele).removeClass('alt');
		}
		//����ѡ������
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

	//Ӧ����ʽ���⣨������ʽ��
	//ydl.themes.use(ydl.localData('operconfig_theme'));
	//if ($.fn.button) $('button').button();
	$('button').each(function () {
		if ($(this).children('span').length == 0) $(this).wrapInner('<span></span>');
	});
	
});

})(ydl, jQuery);
