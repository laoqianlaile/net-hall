/************************************************************************************************
 * 华信永道住房公积金平台公共脚本库
 * 已经过浏览器兼容性测试：IE（6、8、9）、Firefox8+、Chrome14+
 * 此文件应在jQuery库之后，所有其他ydl库文件之前加载
 * @author 杨乔 2011-03-01
 ************************************************************************************************/

/**
 * 是否启用调试模式
 * 在开发环境下应设为true，将输出前端调试日志，并且启用系统设置中的调试选项
 * 在生产环境下应设为false，禁止输出前端调试日志（必要时可设置__forceConsoleOut=true以隐式输出），禁用系统设置中的调试选项
 */
var __isDebugMode;

/**
 * 是否强制所有日志从脚本控制台输出
 * 设为true时，可用于在生产环境下（__isDebugMode==false时）调试，不影响系统正常使用
 * 开发环境下应设为false，开发人员可以自行选择输出目标
 */
var __forceConsoleOut;


/************************************************************************************************
 * JS内部类型原型扩展
 ************************************************************************************************/

/**
 * 字符串去左右空白字符（包括空格、制表符、回车、换行、换页符等）
 * 直接在字符串变量或字符串直接量上使用
 * （只有IE不支持String.trim()，其他浏览器的String对象已经包含了此方法。）
 * @example stringValue.rtrim(); ' hello world '.trim();
 * @returns {String} 去空白字符后的字符串
 */
if (!String.prototype.trim) String.prototype.trim = function() {
	return this.replace(/^[　\s]+|[　\s]+$/g, '');
};
if (!String.prototype.ltrim) String.prototype.ltrim = function() {
	return this.replace(/^[　\s]+/, '');
};
if (!String.prototype.rtrim) String.prototype.rtrim = function() {
	return this.replace(/[　\s]+$/, '');
};
String.prototype.delSpace = function() {
	return this.replace(/[　\s]/g, '');
};

/**
 * 计算字符串中的中文字符数（不含全角标点符号和特殊字符）
 * @example '你好China'.hzLength(); //返回值是2
 * @returns {Number} 字符串中的中文字符数
 */
if (!String.prototype.hzLength) String.prototype.hzLength = function() {
	return this.replace(/[^\u4e00-\u9fa5]/g, '').length;
};

/**
 * 按全角两个字符，半角一个字符计算的字符串长度
 * @example '你好China'.length2();	//返回值是9
 * @returns {Number} 长度值
 */
if (!String.prototype.length2) String.prototype.length2 = function() {
	return this.replace(/[^\x00-\xff]/g, '..').length;
};

/**
 * 将yyyy-MM-dd格式的日期字符串转成日期对象
 * @example var d = '2011-03-01'.toDate()
 * @returns {Date} 日期对象，如果字符串不是正确的日期格式，返回null
 */
if (!String.prototype.toDate) String.prototype.toDate = function() {
	if (!this) return null;
	var ms = Date.parse(this.replace(/-/g, '/').replace(/^(\d{4})(\d{2})(\d{2})$/, '$1/$2/$3'));
	if (isNaN(ms)) return null;
	else return new Date(ms);
};

/************************************************************************************************
 * 公共API（YongDao Library）
 ************************************************************************************************/

var ydl = {};

(function(ydl, $, undefined) {

//调试信息输出目标
	var DEBUG_TARGET_NONE = 0;		//不输出信息
	var DEBUG_TARGET_CONSOLE = 1;	//浏览器脚本控制台
	var DEBUG_TARGET_WINDOW = 2;	//新浏览器窗口
	var DEBUG_TARGET_ALERT = 3;		//alert对话框

//全局数据容器
	ydl.data = {};

	/**
	 * 服务器上下文路径
	 * 尝试从数据总线中的_contexPath值、当前窗口或父窗口的meta标签取值，取不到则设为空串
	 * meta标签写法：<meta name="contexPath" value="<%= _contexPath %>" />
	 */
	try {
		if (window._poolSelect && _poolSelect['_contexPath']) ydl.contexPath = _poolSelect['_contexPath'];
		else if (top != window && top.$('meta[name=contexPath]').attr('content')) ydl.contexPath = top.$('meta[name=contexPath]').attr('content');
		else ydl.contexPath = $('meta[name=contexPath]').attr('content');
	}
	catch (e) {
		ydl.contexPath = $('meta[name=contexPath]').attr('content');
	}

	/**
	 * 当前页面参数对象
	 * 例如当前页面的url是http://xxxxxx/parsepage?$page=aaa/bbb/ccc.ydpx&para1=111&para2=222
	 * 则ydl.common.parameters['$page']返回'aaa/bbb/ccc.ydpx'，如果指定的参数名不存在将返回undefined
	 */
	var parameters = {};
	var searchArray = window.location.search.substring(1).split('&');
	for (var i = 0; i < searchArray.length; i++) {
		var paraArray = searchArray[i].split('=');
		parameters[paraArray[0]] = paraArray[1];
	}

	/**
	 * 读写cookie
	 * 注意：cookie会随每次HTTP请求发送到服务器，如果不需要发送请使用ydl.localData保存本地数据
	 * @example ydl.cookie('cookie_key', 'cookie_value');	//将名字为cookie_key的cookie值设置为cookie_value，如果该cookie不存在则创建
	 * @example ydl.cookie('cookie_key');					//读取指定名字的cookie值
	 * @example ydl.cookie('cookie_key', null);			//删除名字为cookie_key的cookie，如果创建cookie时指定了domain和path，删除时也要同样指定
	 * @example ydl.cookie('cookie_key', 'cookie_value', {expires: 7, path: '/', domain: 'yondervision.com', secure: true}); //创建时指定cookie的参数
	 * @param {String} key cookie的名字
	 * @param {String} value 要保存到cookie中的值，如果设为null将删除此cookie
	 * @param {Object} [options] cookie选项 {
	 *		{Number} [expires=365] cookie多少天后过期
	 *		{String} [path] cookie所属应用路径
	 *		{String} [domain] cookie所属域名
	 * 		{Boolean} [secure=false] 是否只在https连接上发送cookie
	 * 		{Boolean} [raw=false] 读取cookie时是否返回未解码的原始数据
	 * @return {String} 返回cookie的值，读取时没有找到返回null
	 */
	ydl.cookie = function(key, value, options) {
		//设置cookie
		if (arguments.length > 1 && (value === null || typeof value !== "object")) {
			//缺省选项
			options = $.extend({
				expires: 365,
				domain: location.hostname,
				path: ydl.contexPath
			}, options);
			//设置过期，删除cookie
			if (value === null) options.expires = -1;
			//设置多少天后过期
			if (typeof options.expires === 'number') {
				var days = options.expires;
				var t = options.expires = new Date();
				t.setDate(t.getDate() + days);
			}
			return (document.cookie = [
				encodeURIComponent(key), '=',
				options.raw ? String(value) : encodeURIComponent(String(value)),
				options.expires ? '; expires=' + options.expires.toUTCString() : '',
				options.path ? '; path=' + options.path : '',
				options.domain ? '; domain=' + options.domain : '',
				options.secure ? '; secure' : ''
			].join(''));
		}
		//读取cookie的值
		options = value || {};
		var result = new RegExp('(?:^|; )' + encodeURIComponent(key) + '=([^;]*)').exec(document.cookie);
		return result ? (options.raw ? result[1] : decodeURIComponent(result[1])) : null;
	};

	/**
	 * 在浏览器端存储数据
	 * IE6使用userData保存，IE7+、Firefox、chrome、opera使用localStorage保存，如果这两种都不支持就使用cookie保存
	 * @example ydl.localData('data_key', 'data_value');	//将名字为data_key的值设置为data_value，如果该存储项不存在则创建
	 * @example ydl.localData('data_key');					//读取指定名字的本地存储项的值
	 * @example ydl.localData('data_key', null);			//删除名字为data_key的存储项
	 * @param {String} key 存储项的名字
	 * @param {String} [value] 要保存的值，如果设为null将删除此存储项
	 * @return {String} 返回存储项的值，读取时没有找到返回null
	 */
	ydl.localData = function(key, value) {
		if (window.localStorage) {
			//使用localStorage
			if (value === null) {	//删除
				window.localStorage.removeItem(key);
				return null;
			}
			else if (value === undefined) {	//读取
				return window.localStorage.getItem(key);
			}
			else {	//设置
				window.localStorage.setItem(key, value);
				return value;
			}
		}
		else {
			//使用userdata
			try {
				var documentElement = top.document.documentElement;
				documentElement.addBehavior('#default#userdata');
				if (value === null) {	//删除
					documentElement.removeAttribute('value');
					documentElement.save(key);
					return null;
				}
				else if (value === undefined) {	//读取
					documentElement.load(key);
					return documentElement.getAttribute('value');
				}
				else {	//设置
					documentElement.setAttribute('value', value);
					documentElement.save(key);
					return value;
				}
			}
			catch (ex) {
				return ydl.cookie(key, value);
			}
		}
	}

	/**
	 * 平台参数默认值
	 */
	ydl.localData('pageconfig_strictMode', '0');
	ydl.localData('operconfig_theme', 'default');
	ydl.localData('operconfig_changeFocus', 'enter');
	ydl.localData('operconfig_fontSize', '12px');
	ydl.localData('operconfig_showCustomFunc', '1');
	ydl.localData('operconfig_autoHideMenu', '0');
	ydl.localData('operconfig_popupMessage', '1');

	__isDebugMode = 0;
	__forceConsoleOut = 1;

//设置检查SQL执行结果选项默认值
	if (__isDebugMode) ydl.localData('localconfig_sqlCheck') === null && ydl.localData('localconfig_sqlCheck', '0');	//开发环境
	else ydl.localData('localconfig_sqlCheck', '0');	//生产环境
//设置调试信息输出目标默认值
	ydl.localData('localconfig_debugTarget') === null && ydl.localData('localconfig_debugTarget', DEBUG_TARGET_NONE);

	/**
	 * 平台公共数据
	 */
	ydl.common = {
		//是否IE6
		isIe6: $.browser.msie && parseInt($.browser.version) <= 6,

		//键盘码（可用于在keypress事件处理函数中与event.which的值比较，注意为了兼容非IE浏览器，不应与event.keyCode比较）
		keys: {
			'A': 65, 'B': 66, 'C': 67, 'D': 68, 'E': 69, 'F': 70, 'G': 71, 'H': 72, 'I': 73, 'J': 74, 'K': 75, 'L': 76, 'M': 77,
			'N': 78, 'O': 79, 'P': 80, 'Q': 81, 'R': 82, 'S': 83, 'T': 84, 'U': 85, 'V': 86, 'W': 87, 'X': 88, 'Y': 89, 'Z': 90,
			'Num0': 48, 'Num1': 49, 'Num2': 50, 'Num3': 51, 'Num4': 52, 'Num5': 53, 'Num6': 54, 'Num7': 55, 'Num8': 56, 'Num9': 57,
			'NumPad0': 96, 'NumPad1': 97, 'NumPad2': 98, 'NumPad3': 99, 'NumPad4': 100, 'NumPad5': 101, 'NumPad6': 102, 'NumPad7': 103, 'NumPad8': 104, 'NumPad9': 105,
			'F1': 112, 'F2': 113, 'F3': 114, 'F4': 115, 'F5': 116, 'F6': 117, 'F7': 118, 'F8': 119, 'F9': 120, 'F10': 121, 'F11': 122, 'F12': 123,
			'Enter': 13, 'Esc': 27, 'PageUp': 33, 'PageDown': 34, 'End': 35, 'Home': 36, 'Tab': 9, 'Backspace': 8, 'Delete': 46,
			'Left': 37, 'Up': 38, 'Right': 39, 'Down': 40
		},

		//页面参数
		parameters: parameters,

		//下拉列表空白选项
		blankOption: '<option value="">请选择...</option>',

		//函数
		closure: {
			/**
			 * zTree对服务器返回的原始数据进行预处理
			 * @param {String} treeId 树id，用于区分同一个页面上多个zTree实例
			 * @param {Object} parentNode 进行异步加载的父节点JSON数据对象
			 * @param {String} childNodes 获取到的数据转换后的JSON数据对象
			 */
			zTreeDataFilter: function(treeId, parentNode, childNodes) {
				var errorMessage = '获取' + treeId + '数据失败！';
				try {
					//Assert: 服务器返回的JSON对象含有returnCode和message属性
					if ($.isPlainObject(childNodes) && childNodes.returnCode && childNodes.returnCode != 0) {
						if (childNodes.returnCode == -1 && !childNodes.message) childNodes.message = '登录超时，请退出系统并重新登录！';
						alert(errorMessage + '（错误代码：' + childNodes.returnCode + '）' + ('\n\n' + childNodes.message || ''));
						//如果返回码是-1，自动退出登录
						if (childNodes.returnCode == -1) ydl.logout();
						return {id: 'ztree-error', name: '加载数据出错'};
					}
					else return childNodes;
				}
				catch (ex) {
					ydl.log('服务器返回：' + ydl.getMember(childNodes));
					alert(errorMessage);
					return {id: 'ztree-error', name: '加载数据出错'};
				}
			},

			/**
			 * zTree异步提交失败后处理
			 * @param {Object} event 标准event对象
			 * @param {String} treeId 树id，用于区分同一个页面上多个zTree实例
			 * @param {Object} treeNode 父节点JSON数据对象
			 * @param {Object} XMLHttpRequest 标准xmlHttpRequest对象
			 * @param {String} textStatus 请求状态，success或error
			 * @param {Object} errorThrown 只有当异常发生时才会被传递
			 */
			zTreeOnAsyncError: function(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
				ydl.log('服务器返回：\n' + XMLHttpRequest.responseText);
				alert('获取' + treeId + '数据失败：服务器返回' + errorThrown.status);
			}
		}
	};

	/**
	 * 按id查找对象（代替jQuery的$('#id')，当不需要jQuery对象时可提高性能）
	 * @param {String} elementId id字符串
	 * @param {Object} wnd Window对象，指定在哪个window中查找，缺省为当前window
	 * @returns {Object} 如果elementId不是字符串，返回输入的参数本身；否则按id查找，返回唯一的对象，未找到则返回null
	 */
	ydl.getElementById = function(elementId, wnd) {
		if (typeof elementId === 'string') return (wnd || window).document.getElementById(elementId);
		return elementId;
	};

	/**
	 * 按name查找对象
	 * @param {String} elementName name字符串
	 * @param {Object} wnd Window对象，指定在哪个window中查找，缺省为当前window
	 * @returns {NodeList} 如果elementName不是字符串，返回输入的参数本身；否则按name查找，返回对象集合，未找到则返回空集合
	 */
	ydl.getElementsByName = function(elementName, wnd) {
		if (typeof elementName === 'string') return (wnd || window).document.getElementsByName(elementName);
		return elementName;
	};

	/**
	 * 按标签名查找对象
	 * @param {String} elementTagName 标签名
	 * @param {Object} wnd Window对象，指定在哪个window中查找，缺省为当前window
	 * @returns {NodeList} 如果elementTagName不是字符串，返回输入的参数本身；否则按tagName查找，返回对象集合，未找到则返回空集合
	 */
	ydl.getElementsByTagName = function(elementTagName, wnd) {
		if (typeof elementTagName === 'string') return (wnd || window).document.getElementsByTagName(elementTagName);
		return elementTagName;
	};

	/**
	 * 获取一组相同name属性的单选框中被选中的选项信息
	 * @param {String|Object} radio 单选框的name属性，或者一组单选框的dom对象list或jQuery对象
	 * @returns {Object} 包含被选中的单选框的索引、值、文本的对象，没有选中的选择则返回索引-1，值和文本为空
	 */
	ydl.getCheckedRadio = function(radio) {
		var radios = (typeof radio === 'string' ? document.getElementsByName(radio) : radio instanceof jQuery ? radio.get() : radio);
		for (var i = 0; i < radios.length; i++) {
			if (radios[i].checked) return {
				index: i,
				value: radios[i].value,
				text: $('label[for="' + radios[i].id.replace(':', '\\:') + '"]').text()
			};
		}
		return {
			index: -1,
			value: '',
			text: ''
		};
	};

	/**
	 * 获取一组相同name属性的复选框中被选中的选项信息
	 * @param {String|Object} radio 复选框的name属性，或者一组复选框的dom对象list或jQuery对象
	 * @returns {Array} 包含被选中的复选框的索引、值、文本的对象数组，没有选中的选项则返回空数组
	 */
	ydl.getCheckedCheckbox = function(checkbox) {
		var checkboxes = (typeof checkbox === 'string' ? document.getElementsByName(checkbox) : checkbox instanceof jQuery ? checkbox.get() : checkbox);
		var result = [];
		for (var i = 0; i < checkboxes.length; i++) {
			if (checkboxes[i].checked) result.push({
				index: i,
				value: checkboxes[i].value,
				text: $('label[for="' + checkboxes[i].id.replace(':', '\\:') + '"]').text()
			});
		}
		return result;
	};

	/**
	 * 设置一组复选框的选中状态
	 * @param {String} id 生成这一组复选框的多值组件ID
	 * @param {Array|String} values 要选中项的值，字符串数组或者用半角逗号分隔开的字符串
	 * @returns Array 包含所有选中复选框的DOM对象数组，没有选中的则返回空数组
	 */
	ydl.setCheckedCheckbox = function(id, values) {
		if (typeof values === 'string') values = values.split(',');
		var checkboxes = [];
		$('input[name=' + id + ']').each(function() {
			if ($.inArray(this.value, values) >= 0) {
				this.checked = true;
				checkboxes.push(this);
			}
			else this.checked = false;
		});
		return checkboxes;
	};

	/**
	 * 设置一组单选框的选中状态
	 * @param {String} id 生成这一组单选框的多值组件ID
	 * @param {String} values 要选中项的值
	 * @returns Object 选中的单选框DOM对象，没有选中项则返回null
	 */
	ydl.setCheckedRadio = function(id, value) {
		var radios = document.getElementsByName(id);
		for (var i = 0; i < radios.length; i++) {
			var radio = radios[i];
			if (radio.value == value) {
				radio.checked = true;
				return radio;
			}
		}
		return null;
	};

	/**
	 * 通过多种方式返回DOM对象（jQuery对象拆箱）
	 * @param {Object|String} element DOM对象（可以是对象数组），或JQuery对象，或元素的id
	 * @returns {Object} DOM对象，未找到返回undefined
	 */
	ydl.getDom = function(element) {
		var ele = ydl.getElementById(element);
		if (ele) return ele instanceof jQuery ? ele.get() : ele;
		else return undefined;
	};

	/**
	 * 获取对象全部成员信息（调试程序用）
	 * @param {Object} obj 对象
	 * @returns {String} 如果obj不是对象返回obj本身，否则返回格式化的对象成员信息{name:value}
	 */
	ydl.getMember = function(obj) {
//	if ((__isDebugMode || __forceConsoleOut) && ydl.localData('localconfig_debugTarget') != DEBUG_TARGET_NONE) {
		var objType = $.type(obj);
		if (objType == 'object' || objType == 'array') {
			if (obj.nodeType || $.isWindow(obj)) return String(obj);
			else {
				var indent = arguments.length > 1 ? ++arguments[1] : 1;	//缩进级别
				if (indent > 10) return '...';	//限制9层递归，防止死循环
				var indentChar = '    '; //缩进字符
				var indentString = ydl.string(indentChar, indent - 1);
				var memberInfo = '';
				if (objType === 'array') {	//数组
					for (var i = 0, l = obj.length; i < l; i++) memberInfo += indentChar + indentString + i + ': ' + arguments.callee(obj[i], indent) + '\n';
					return '[\n' + memberInfo + indentString + ']';
				}
				else {	//对象
					for (var member in obj) memberInfo += indentChar + indentString + member + ': ' + arguments.callee(obj[member], indent) + '\n';
					return '{\n' + memberInfo + indentString + '}';
				}
			}
		}
		else if ($.isFunction(obj)) {	//函数
			var funcString = String(obj);
			return funcString.substring(0, funcString.indexOf('{')) + '{...}';
		}
		else if (objType === 'string') return '"' + obj + '"';	//字符串
		else return String(obj);	//其他类型
//	}
//	else return obj;
	};


	/**
	 * 格式化日期
	 * @example ydl.formatDate(date); //返回类似'2011-03-26 09:12:30'格式的日期字符串
	 * @example ydl.formatDate(date, '今天是yyyy年M月d日礼拜E，现在已经aah点m分了'); //返回'今天是2011年3月26日礼拜六，现在已经下午4点38分了'
	 * @param {Date} date 日期对象
	 * @param {String} [pattern='yyyy-MM-dd HH:mm:ss'] 自定义格式代码（兼容java.text.SimpleDateFormat），其中部分英文字母将被替换，详见下面注释
	 * @returns {String} 按pattern指定的格式返回，没有指定pattern时返回yyyy-MM-dd HH:mm:ss格式
	 */
	ydl.formatDate = function(date, pattern) {
		var d = {
			'M': String(date.getMonth() + 1),	//M=月份（1-12），MM=两位月份（01-12）
			'd': String(date.getDate()),		//d=日期（1-31），dd=两位日期（01-31）
			'H': String(date.getHours()),		//H=24小时制的小时（0-23），HH=两位小时（00-23）
			'h': String(date.getHours() < 13 ? date.getHours() : date.getHours() - 12),		//h=12小时制的小时（0-12），hh=两位小时（00-12）
			'm': String(date.getMinutes()),		//m=分钟（0-59），mm=两位分钟（00-59）
			's': String(date.getSeconds())		//s=秒钟（0-59），ss=两位秒钟（00-59）
		};
		pattern = pattern || 'yyyy-MM-dd HH:mm:ss';
		for (var r in d) pattern = pattern.replace(new RegExp(r + r, 'g'), ('0' + d[r]).substring(d[r].length - 1)).replace(new RegExp(r, 'g'), d[r]);
		return pattern = pattern.replace(/E/g, '日一二三四五六'.charAt(date.getDay()))	//E=中文星期
			.replace(/aa/g, date.getHours() < 12 ? '上午' : '下午')						//aa=中文上下午标志（上午，下午）
			.replace(/a/g, date.getHours() < 12 ? 'AM' : 'PM')							//a=上下午标志（AM、PM）
			.replace(/yyyy/g, date.getFullYear()).replace(/yy/g, ('' + date.getFullYear()).substring(2));	//yyyy=四位年份，yy=两位年份
	};


	/**
	 * 检查日期是否合法
	 * @param {String[]|Number[]} arr [年, 月, 日]或者[月, 日]
	 * @returns {Boolean} 是否合法日期
	 */
	function checkValidDate(arr) {
		for (var i = 0; i < arr.length; i++) {
			if (typeof arr[i] === 'string') arr[i] = arr[i].replace(/^0+/, '');
		}
		var y, m, d;
		switch (arr.length) {
			case 2:
				y = 2000;	//一个任意的闰年
				m = parseInt(arr[0]) - 1;
				d = parseInt(arr[1]);
				break;
			case 3:
				y = parseInt(arr[0]);
				m = parseInt(arr[1]) - 1;
				d = parseInt(arr[2]);
				break;
			default:
				ydl.log('checkValidDate出错: date参数数组长度不正确！', 'red');
				return false;
		}
		//检查原理：Date构造函数支持超出正常范围的月、日值，自动向下一年、月进位，
		//　　　　　检测构造后的年月日是否与输入参数一致就可以知道参数是否在正常范围内。
		var date = new Date(y, m, d);
		return !isNaN(date) && date.getFullYear() == y && date.getMonth() == m && date.getDate() == d;
	}

	/**
	 * 检查日期字符串是否是正确的日期
	 * @param {String} dateString 日期字符串（YYYY-MM-DD格式）
	 * @returns {Boolean} 是否正确
	 */
	ydl.isValidDate = function(dateString) {
		var dateArr = dateString.split('-');
		if (dateArr.length < 3) return false;
		return checkValidDate(dateArr);
	};

	/**
	 * 获取客户端当前日期字符串
	 * @returns {String} yyyy-MM-dd格式的当前日期
	 */
	ydl.today = function() {
		return ydl.formatDate(new Date(), 'yyyy-MM-dd');
	};

	/**
	 * 日期计算
	 * @param {String|Date} d 日期对象或者yyyy-MM-dd格式的日期字符串
	 * @param {Number} v 整数，与日期相加的值，正数为向后增加日期，负数为向前减少日期
	 * @param {String} [unit] 增加或减少数量的单位，'y'=年，'m'=月，'d'=日，可省略，缺省为'd'
	 * @returns {String} yyyy-MM-dd格式的日期字符串，如果原始日期不正确，则返回NaN
	 */
	ydl.dateAdd = function(d, v, unit) {
		if (!unit) unit = 'd';
		if (typeof d === 'string') d = d.toDate();
		if (!d)  return NaN;
		switch (unit.toLowerCase()) {
			case 'y': d = new Date(d.getFullYear() + v, d.getMonth(), d.getDate()); break;
			case 'm': d = new Date(d.getFullYear(), d.getMonth() + v, d.getDate()); break;
			case 'd': d = new Date(d.getFullYear(), d.getMonth(), d.getDate() + v); break;
		}
		return ydl.formatDate(d, 'yyyy-MM-dd');
	};

	/**
	 * 计算两个日期之间的日期差
	 * @param {String|Date} d1 第一个日期，yyyy-MM-dd格式的字符串或日期对象
	 * @param {String|Date} d2 第二个日期，yyyy-MM-dd格式的字符串或日期对象
	 * @param {String} unit 返回值的单位，'y'=年，'m'=月，'d'=日，可省略，缺省为'd'
	 * @returns {Number} 两个日期的差，d1早于d2时返回正数，否则返回负数，由unit参数确定单位
	 */
	ydl.dateDiff = function(d1, d2, unit) {
		if (!unit) unit = 'd';
		if (typeof d1 === 'string') d1 = d1.toDate();
		if (typeof d2 === 'string') d2 = d2.toDate();
		switch (unit.toLowerCase()) {
			case 'y': return d2.getFullYear() - d1.getFullYear(); break;
			case 'm': return (d2.getFullYear() - d1.getFullYear()) * 12 + d2.getMonth() - d1.getMonth(); break;
			case 'd': return (d2.getTime() - d1.getTime()) / (1000 * 3600 * 24); break;
		}
	};

	/**
	 * 通过表单字段查找字段描述
	 * @param {Object|String} field 表单字段DOM对象，或字段的JQuery对象，或字段id
	 * @param {String} [defaultLabel] 默认的字段描述，当无法在页面中找到字段对应label和th时使用
	 * @returns {String} 返回与field关联的label文本或列表标题行th的文本，未找到则返回默认字段描述
	 */
	ydl.getLabel = function(field, defaultLabel) {
		var ret = '';
		var f = ydl.getDom(field);
		if (f === undefined) {
			if (document.getElementsByName(field).length > 0) {
				ret = $('label[for=' + field + ']').text();
			}
			return ret.replace(/[:：]$/g, '') || defaultLabel || '';
		}
		if (f instanceof Array) f = f[0];
		defaultLabel = defaultLabel || f.id || '';
		//assert: 列表元素为<table class="datalist">，输入字段为td的直接子节点
		var fieldParent = f.parentNode;
		if (fieldParent.tagName == 'TD' && $(fieldParent).closest('table').hasClass('datalist')) {
			try {
				var $tr = $(fieldParent.parentNode);
				ret = $tr.parent().children(':first').children(':eq(' + $tr.children().index(fieldParent) + ')').text().trim().replace(/\*$/, '');
			}
			catch (ex) {
				ydl.log('ydl.getLabel出错：获取标签失败' + ydl.error(ex), 'red');
				ret = '';
			}
		}
		else {
			ret = $('label[for=' + f.id.replace(/:/g, '\\:') + ']').text();
		}
		return ret == '' ? defaultLabel : ret.replace(/[:：]$/g, ''); //返回前去掉末尾的冒号
	};

	/**
	 * 获取指定表单字段的值
	 * @param {String|Object} 表单字段的id属性或dom对象或jQuery对象
	 * @param {String} [defaultValue] 默认值，当没有找到字段，或字段值为空时返回
	 * @param {Boolean} [getText=false] 是否获取文本信息，如果为true，对于select、checkbox、radio将返回文字描述而不是value
	 * @returns {String} 字段值字符串或默认值，多选的复选框返回用逗号分隔的值，没有取到值，也没有指定默认值时返回空串
	 */
	ydl.getValue = function(field, defaultValue, getText) {
		//支持ydl.getValue(field, getText, defaultValue)方式
		if (typeof defaultValue == 'boolean') {
			var bGetText = defaultValue;
			defaultValue = getText;
			getText = bGetText;
		}
		var r;
		var f = ydl.getDom(field);
		if (f === undefined) {
			var $checked = $('input[name=' + field + ']:checked');
			if ($checked.length == 0) r = '';
			else r = $checked.map(function() {
				return getText ? $('label[for=' + this.id.replace(/\:/, '\\:') + ']').text() : this.value;
			}).get().join(',');
		}
		else {
			if ($(f).hasClass('money')) r = ydl.delComma(f.value);
			else if (f.type == 'select-one') {
				if (getText) {
					var $selected = $('option:selected', f);
					if ($selected.val() == '' && $selected.text() == '请选择...') r = '';
					else r = $selected.text();
				}
				else r = f.value;
			}
			else r = f.value;
		}
		return r || defaultValue || '';
	};

	/**
	 * 页面跳转
	 * @param {String} url 要跳转的网址，可以是相对或绝对路径
	 * @param {Object} target 目标window对象，可省略，缺省为当前window
	 * @returns undefined
	 */
	ydl.go = function(url, target) {
		(target || window).location.href = url;
	};

	/**
	 * 退出当前登录
	 */
	ydl.logout = function() {
		top.location.href = ydl.contexPath + '/index.jsp?flg=1';
	};

//新窗口和对话框默认宽度和高度（单位：px）
	var DEFAULT_WIDTH = 640;
	var DEFAULT_HEIGHT = 480;

	/**
	 * 在新窗口打开页面
	 * 注意：该方法的弹出窗口会被浏览器默认设置拦截，需在客户端浏览器禁用弹出窗口拦截功能。
	 * @param {String} url 新窗口页面地址
	 * @param {String} [name=''] 窗口名称，可用作<a>或<form>的target，如果已存在则直接返回该窗口引用。
	 * @param {Object|String} [paras] 窗口参数 { 'width': 窗口宽度, 'height': 窗口高度, 'top': 上边距, 'left': 左边距 }，或者window.open方法默认参数
	 * @returns {Object} 新窗口对象
	 */
	ydl.openWindow = function(url, name, paras) {
		//支持使用ydl.openWindow(url, paras)方式调用
		if (typeof name !== 'string') {
			paras = name;
			name = 'win' + ydl.uuid();
		}
		if (typeof paras === 'string') {
			//将paras作为window.open默认窗口参数打开新窗口
			return window.open(url, name, paras);
		}
		else {
			if (paras === undefined) paras = {};
			var width = paras.width ? (paras.width > screen.availWidth ? screen.availWidth : paras.width) : DEFAULT_WIDTH;
			var height = paras.height ? (paras.height > screen.availHeight ? screen.availHeight : paras.height) : DEFAULT_HEIGHT;
			//默认位置居中
			var left = paras.left ? (paras.left > screen.availWidth ? screen.availWidth - width : paras.left) :
				parseInt((screen.availWidth - width) / 2);
			var top = paras.top ? (paras.top > screen.availHeight ? screen.availHeight - height : paras.top) :
				parseInt((screen.availHeight - height) / 2);
			return window.open(url, name, 'width=' + width + ', left=' + left + ', height=' + height + ', top=' + top +
				', location=no, menubar=no, resizable=yes, scrollbars=yes, status=no, toolbar=no'
			);
		}
	};

//模态对话框
	ydl.dialog = {};

	/**
	 * 在模态对话框中打开页面
	 * 如果指定了层模式，在页面内展示对话框，否则用弹出窗口展示。
	 * 浏览器兼容性：IE、Opera、Firefox两种模式均正常；Chrome层模式正常，窗口模式存在bug，建议均使用层模式。
	 * @param {String} url 对话框页面地址
	 * @param {Object} [inputData] 传递给对话框的参数，在对话框页面中通过ydl.dialog.arguments()获取。
	 * @param {Function} [callback(data)] 回调函数，在对话框关闭时执行并将对话框返回的数据作为参数。
	 * @param {Object} [paras] 窗口参数 { 'width': 窗口宽度, 'height': 窗口高度, 'layermode': 是否层模式，默认为modalDialogIsLayerMode }
	 * @returns undefined
	 */
	ydl.dialog.open = function(url, inputData, callback, paras) {
		/** 配置选项：模态对话框默认是否为层模式 */
		var modalDialogIsLayerMode = true;
		if (!paras) paras = {};
		var layerMode = paras.layermode === undefined ? modalDialogIsLayerMode : paras.layermode;
		var width = paras.width ? (paras.width > screen.availWidth ? screen.availWidth : paras.width) : DEFAULT_WIDTH;
		var height = paras.height ? (paras.height > screen.availHeight ? screen.availHeight : paras.height) : DEFAULT_HEIGHT;
		//如果浏览器不支持showModalDialog方法，将始终以层模式展示
		if (layerMode || !window.showModalDialog) {
			var $div = $('<div></div>').bgiframe();
			$div.appendTo($('body')).append(
				//在iframe中加载页面
				$('<iframe class="modalDialog" style="width: 100%; height: 100%;" frameborder="0"></iframe>')
					.load(function() {
						//将对话框标题设置为页面标题
						$div.dialog('option', 'title', this.contentWindow.document.title);
					})
			).dialog({
				width: width,
				height: height,
				modal: true,
				open: function() {
					$div.children('iframe').attr('src', url);
					showOverlay(true);
					if (paras.open) paras.open();
				},
				close: function() {
					//执行回调函数
					var callbackArguments = $div.data('callbackArguments');
					if (callback && callbackArguments) callback.apply(this, callbackArguments);
					//关闭对话框时同时删除DOM节点
					try {
						delete window.modalDialogArguments;
					}
					catch (ex) {
						window.modalDialogArguments = null;
					}
					$div.children('iframe').remove();
					$div.dialog('destroy').remove();
					showOverlay(false);
					if (paras.close) paras.close();
				}
			});
			//设置对话框参数
			window.modalDialogArguments = {
				dialogArgs: inputData,
				dialogHandler: $div,
				dialogCallback: callback
			};
		}
		else {
			//window对象的打开模态对话框方法，非W3C标准
			window.showModalDialog(ydl.contexPath + '/platform/dialogFrame.jsp?url=' +
				encodeURIComponent(url + (url.indexOf('?') >= 0 ? '&' : '?') + 'uuid=' + ydl.uuid()), {
				'dialogArgs': inputData,
				'dialogCallback': callback
			}, 'dialogWidth:' + width + 'px; dialogHeight:' + height + 'px; center:yes; status:no; scroll:yes; resizable:yes;');
		}

		//显示隐藏主框架覆盖层
		function showOverlay(show) {
			if (parent.document.title === 'Flw_waittaskNew') {
				var overlay = ydl.getElementById('modal-dialog-overlay', parent);
				if (show) {
					var $tabNav = $('ul.ui-tabs-nav', parent.document);
					if (!overlay) $('<div id="modal-dialog-overlay" class="ui-widget-overlay" style="z-index: 1001; width: 100%; height: ' +
						$tabNav[0].offsetHeight + 'px; top: ' + $tabNav.offset().top + 'px" />').appendTo(parent.document.body);
				}
				else $(overlay).remove();
			}
		}
	};

	/**
	 * 在模态对话框内的页面使用，获取从父页面传递来的参数
	 * @returns {Object} 参数对象
	 */
	ydl.dialog.arguments = function() {
		if (parent.location.href.indexOf('/platform/dialogFrame.jsp') > 0) return parent.dialogArguments.dialogArgs;	//窗口模式
		else return parent.modalDialogArguments.dialogArgs;	//层模式
	};

	/**
	 * 在模态对话框内的页面使用，关闭对话框并执行回调函数
	 * @returns undefined
	 */
	ydl.dialog.close = function() {
		var args = parent.modalDialogArguments;
		if (args) {
			args.dialogHandler.data('callbackArguments', arguments);
			args.dialogHandler.dialog('close');
		}
		else {
			top.close();
			parent.dialogArguments.dialogCallback && parent.dialogArguments.dialogCallback.apply(this, arguments);
		}
	};

	/**
	 * 将页面容器转换为对话框
	 * @param {String} containers 容器ID，多个ID之间用半角逗号分隔
	 * @param {Object} options 对话框选项，常用属性包括id、title、width、height、buttons等
	 * @returns {Object} 对话框的jQuery对象
	 */
	ydl.makeDialog = function(containers, options) {
		var containers = $.map(containers.split(','), function(value) {
			return '#' + value.trim();
		}).join(',');
		return $('<div' + (options && options.id ? ' id="' + options.id + '"' : '') +
			'></div>').append($(containers)).dialog($.extend({
			title: '对话框',
			autoOpen: false,
			modal: true,
			width: 600,
			height: 400
		}, options || {}));
	};

	/**
	 * 读写操作员个性化配置参数
	 * @param {String} paraName 参数名
	 * @param {String} [paraValue] 要设置的参数值，如果不提供此参数，将返回现有参数值
	 * @returns {String} 参数值
	 */
	ydl.operConfig = function(paraName, paraValue) {
		var url = ydl.contexPath + '/platform/portal/oper/operConfig.jsp';
		var localDataName = 'operconfig_' + paraName;

		if (paraValue === undefined) {
			return ydl.localData(localDataName);
		}
		else if (paraName != '' && ydl.localData(localDataName) != paraValue) {
			ydl.localData(localDataName, paraValue);
			ydl.ajax(url, {
				paraname: paraName,
				paravalue: paraValue
			}, function() {
				top.__pageConfig.oper[paraName] = paraValue;
			}, {async: true});
		}
		return paraValue;
	}

	/**
	 * 对任意对象中的表单元素使用serialize方法
	 * @param {Object|String} element 页面元素的id、dom对象或jQuery对象
	 * @returns {String} 指定元素内的表单对象名值字符串：'name1=value1&name2=value2&……'
	 */
	ydl.serialize = function(element) {
		return $(ydl.getDom(element)).find(':input[name]:enabled').clone().wrap('<form></form>').serialize();
	};

	/**
	 * 获取任意对象中的表单元素，返回name，value数组，可以在ajax提交时作为参数使用
	 * @param {Object|String} element 页面元素的id、dom对象或jQuery对象
	 * @param {Boolean} [includeDisabled=false] 是否包含禁用的对象，缺省为false
	 * @returns {Object} 对象数组：[{name: 'name', value: 'value'}]
	 */
	ydl.getInputs = function(element, includeDisabled) {
		var inputsArray = [];
		$(ydl.getDom(element)).find(':input[name]' + (includeDisabled ? '' : ':enabled')).each(function() {
			if ($.inArray(this.type, ['text', 'hidden', 'textarea', 'select-one', 'password']) >= 0) {
				inputsArray.push({name: this.name, value: this.value});
			}
			else if ($.inArray(this.type, ['checkbox', 'radio']) >= 0) {
				if (this.checked) inputsArray.push({name: this.name, value: this.value});
			}
		});
		return inputsArray;
	};

	$.fn.getInputs = function(includeDisabled) {
		var result = [];
		this.each(function() {
			result = result.concat(ydl.getInputs(this, includeDisabled));
		});
		return result;
	};

	/**
	 * 动态创建隐藏表单字段，可使用jQuery表单对象的append()方法或给innerHTML属性赋值的方式添加到表单中
	 * @example //fields是字符串数组，以数组元素为name创建input，默认value为空
	 *			var inputs = ydl.createInputs(['f1', 'f2']);
	 *			//结果：'<input type="hidden" name="f1" /><input type="hidden" name="f2" />'
	 * @example //fields是对象数组，以数组元素的name和value属性创建input，允许有相同的name
	 *			var inputs = ydl.createInputs([{name: 'f1', value: 1}, {name: 'f2', value: 2}]);
	 *			//结果：'<input type="hidden" name="f1" value="1" /><input type="hidden" name="f2" value="2" />'
	 * @example //fields是对象，以对象的属性名和属性值作为name和value创建input，不允许有相同的name
	 *			var inputs = ydl.createInputs({'f1': 1, 'f2': 2});
	 *			//结果：'<input type="hidden" name="f1" value="1" /><input type="hidden" name="f2" value="2" />'
	 * @example //fields是对象，对象属性值是数组，以属性名作为name，属性值的不同数组元素作为value创建一组name相同的input
	 *			var inputs = ydl.createInputs({'f1': 1, 'f2': [2, 3, 4]});
	 *			//结果：'<input type="hidden" name="f1" value="1" /><input type="hidden" name="f2" value="2" />
	 *					 <input type="hidden" name="f2" value="3" /><input type="hidden" name="f2" value="4" />'
	 * @param {Object|Array} fields 表单元素名数组或名/值对象；如果名/值对象中的值是数组，将为每个值分别创建name相同的input
	 * @returns {String} html字符串，包含根据fields参数创建的一组hidden类型的input
	 */
	ydl.createInputs = function(fields) {
		var html = '', i, l;
		if ($.isArray(fields)) {	//处理数组
			for (i = 0, l = fields.length; i < l; i++) {
				if (typeof fields[i] === 'object') {	//处理数组元素是对象{name:'', value:''}的
					html += '<input type="hidden" name="' + fields[i].name + '" value="' + fields[i].value + '" />\n';
				}
				else html += '<input type="hidden" name="' + fields[i] + '" />\n';	//字符串数组，数组元素作为name，默认value为空
			}
		}
		else if ($.isPlainObject(fields)) {	//处理对象，属性名作为name，属性值作为value
			for (var fieldName in fields) {
				if ($.isArray(fields[fieldName])) {	//处理对象属性值是数组的，name相同
					for (i = 0, l = fields[fieldName].length; i < l; i++)
						html += '<input type="hidden" name="' + fieldName + '" value="' + fields[fieldName][i] + '" />\n';
				}
				else html += '<input type="hidden" name="' + fieldName + '" value="' + fields[fieldName] + '" />\n';
			}
		}
		else if (typeof fields === 'string') html += '<input type="hidden" name="' + field + '" />';	//处理字符串，作为name创建单个input
		else ydl.log('ydl.createInputs出错：field参数类型不正确', 'red');
		return html;
	};

	/**
	 * 动态创建表单
	 * @example //fields是字符串数组，以数组元素为name创建input，value为空，返回表单引用，可以操纵表单元素或提交表单
	 *			var f = ydl.createForm('/test.do', ['f1', 'f2']); f.f1.value = 1; f.f2.value = 2; f.submit();
	 * @example //fields是对象数组，以数组元素的name和value属性创建input，允许有相同的name，返回表单引用，本例直接提交
	 *			ydl.createForm('/test.do', [{name: 'f1', value: 1}, {name: 'f2', value: 2}]).submit();
	 * @param {String} action：表单的action
	 * @param {Object|Array} fields 表单元素名数组或名/值对象，规则与ydl.createInputs方法的fields参数相同
	 * @param {String} [id] 表单id，可省略，缺省为随机生成
	 * @param {Boolean} [includeFile=false] 表单中是否包含文件，如果设为true，将设置form的enctype="multipart/form-data"
	 * @returns {Object} 返回表单DOM对象。如果指定了id，先检查是否已存在，如存在则直接返回，不重复创建
	 */
	ydl.createForm = function(action, fields, id, includeFile) {
		if (typeof id === 'boolean') {	//支持createForm(action, fields, includeFile)方式调用
			includeFile = id;
			id = undefined;
		}
		if (id === undefined) id = 'form' + ydl.uuid();	//保证form的id不重复
		if (!document.getElementById(id)) {
			var html = '<form id="' + id + '" method="post" action="' + action + '"' + (includeFile ? ' enctype="multipart/form-data"' : '') + '>\n';
			html += ydl.createInputs(fields);
			html += '</form>';
			ydl.log('动态创建表单：\n' + html);
			$('body').append(html);
		}
		return document.getElementById(id);
	};

	/**
	 * 创建表格行
	 * @param {Array} cells 行内所有单元格的内容，可以是HTML代码，或者DOM对象、jQuery对象，
	 * 		或者是指定了text、className的对象
	 * @param {Boolean} isTableHead 是否表头
	 * @returns {Object} 表格行的jQuery对象
	 */
	ydl.createRow = function(cells, isTableHead) {
		var $tr = $('<tr></tr>');
		var cellTag = isTableHead ? 'th' : 'td';
		$.each(cells, function(index) {
			var $cell = $('<' + cellTag + '></' + cellTag + '>');
			if ($.isPlainObject(cells[index])) {
				$cell.attr('class', cells[index].className || cells[index].type).append(cells[index].text); //type属性为向前兼容而保留，不推荐使用
			}
			else {
				$cell.append(cells[index]);
			}
			$tr.append($cell);
		});
		return $tr;
	};

	/**
	 * 创建下拉列表选项
	 * @example var options = ydl.createOptions({'1': '男', '0': '女'});
	 * @example var options = ydl.createOptions([{value: '1', text: '男'}, {value: '0', text: '女'}]);
	 * @example var options = ydl.createOptions([{code: '1', desc: '男'}, {code: '0', desc: '女'}], 'desc', 'code');
	 * @param {Object|Array} options 对象{value: text}，或数组[{text:'', value:''}]
	 * @param {String} [textAttr='text'] 数组元素的选项文本属性名
	 * @param {String} [valueAttr='value'] 数组元素的选项值属性名
	 * @returns {String} 一组option标签的html代码
	 */
	ydl.createOptions = function(options, textAttr, valueAttr) {
		var html = [];
		if ($.isArray(options)) {
			textAttr = textAttr || 'text';
			valueAttr = valueAttr || 'value';
			for (var i = 0, l = options.length; i < l; i++)
				html.push('<option value="' + options[i][valueAttr] + '">' + options[i][textAttr] + '</option>');
		}
		else if (typeof options === 'object') {
			for (var o in options) html.push('<option value="' + o + '">' + options[o] + '</option>');
		}
		return html.join('');
	};

	/**
	 * 根据指定的值设置下拉列表的选择项
	 * 如果有多个选项具有相同的值，只选中第一个
	 * @param {Object|String} select 下拉列表DOM对象，或jQuery对象，或ID属性值
	 * @param {String} value 要选择的值
	 * @returns {Number} 返回选择项的索引，如果没找到指定的值，返回-1
	 */
	ydl.selectByValue = function(select, value) {
		select = ydl.getDom(select);
		if (select instanceof Array) {
			var r = -1;
			for (var i = 0; i < select.length; i++) {
				r = arguments.callee(select[i], value);
			}
			return r;
		}
		if (select.tagName && select.tagName == 'SELECT') {
			var index = -1;
			var options = select.options;
			if (!value) value = '';
			else value = ('' + value).rtrim();
			for (var i = 0, l = options.length; i < l; i++) {
				if (options[i].value !== undefined && options[i].value.rtrim() == value) {
					options[i].selected = true;
					index = i;
					//给组合框赋值
					var $next = $(select).next();
					if ($next.is('.ui-autocomplete-input')) {
						$next.val($(options[i]).text());
					}
					break;
				}
			}
			index == -1 && ydl.log('ydl.selectByValue：列表' + (select.id || '') + '中未找到值"' + value + '"', 'orange');
			return index;
		}
		else ydl.log('ydl.selectByValue出错：指定的对象不是select', 'red');
	};

	/**
	 * 根据指定的值设置下拉列表的选择项
	 * @example $('#the-select').selectByValue('02')
	 * @param {String} value 要选择的值
	 * @returns {Number} 返回选择项的索引，如果没找到指定的值，返回-1
	 */
	$.fn.selectByValue = function(value) {
		ydl.selectByValue(this, value);
		return this;
	};


	/**
	 * 根据指定的选项文本设置下拉列表的选择项
	 * 如果有多个选项具有相同的文本，只选中第一个
	 * @param {Object|String} select 下拉列表DOM对象，或jQuery对象，或ID属性值
	 * @param {String} value 要选择的选项文本
	 * @returns {Number} 返回选择项的索引，如果没找到指定的文本，返回-1
	 */
	ydl.selectByText = function(select, text) {
		select = ydl.getDom(select);
		if (select instanceof Array) {
			var r = -1;
			for (var i = 0; i < select.length; i++) {
				r = arguments.callee(select[i], text);
			}
			return r;
		}
		if (select.tagName && select.tagName == 'SELECT') {
			var index = -1;
			var options = select.options;
			if (!text) text = '';
			else text = ('' + text).rtrim();
			for (var i = 0, l = options.length; i < l; i++) {
				if ($(options[i]).text().rtrim() == text) {
					options[i].selected = true;
					index = i;
					//给组合框赋值
					var $next = $(select).next();
					if ($next.is('.ui-autocomplete-input')) {
						$next.val($(options[i]).text());
					}
					break;
				}
			}
			index == -1 && ydl.log('ydl.selectByText：列表' + (select.id || '') + '中未找到文本"' + text + '"', 'orange');
			return index;
		}
		else ydl.log('ydl.selectByText出错：指定的对象不是select', 'red');
	};
	$.fn.selectByText = function(text) {
		ydl.selectByText(this, text);
		return this;
	};


	/**
	 * 根据下拉列表的选项值查询选项文本
	 * @param {String|Object} select 下拉列表的id、DOM对象或jQuery对象
	 * @param {String} value 要查找的选项值
	 * @returns {String} 选项值对应的选项文本，如果没有找到返回选项值本身
	 */
	ydl.getSelectTextByValue = function(select, value) {
		select = ydl.getDom(select);
		if (!select) return value;
		else return $('option[value=' + value + ']', select).text().trim() || value;
	};

	/**
	 * 将字符串中的特殊字符转为实体引用
	 * xml: 小于号转为&lt; 大于号转为&gt; &符号转为&amp; 双引号转为&quot;
	 * html: 小于号转为&lt; 大于号转为&gt; &符号转为&amp; 双引号转为&quot; 空格转为&nbsp; 回车换行转为BR标签
	 * @param {String} inputString 带有特殊字符的字符串
	 * @param {String} [type='xml'] 转换类型（xml或html）
	 * @returns {String} 转换后的字符串，不含HTML标签
	 */
	ydl.convertTags = function(inputString, type) {
		var result = inputString.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
		if (type == 'html') result = result.replace(/  /g, ' &nbsp;').replace(/\r\n|\r|\n/g, '<br />');
		return result;
	};

	/**
	 * 检查字符串中是否包含全角字符
	 * @example if (ydl.hasSbc(field.value)) alert('该字段不能包含全角字符！');
	 * @example var sbc = ydl.hasSbc(field.value); alert('该字段包含' + sbc + '个全角字符！');
	 * @param {String} str 要检查的字符串
	 * @returns {Number} 全角字符的个数，也可以直接当作布尔值使用，包含全角字符为true，不含全角字符为false
	 */
	ydl.hasSbc = function(str) {
		return str.length2() - str.length;
	};

	/**
	 * 生成唯一ID，可用作对象标识或随机字符串
	 * @param {Boolean} [isStandard=false] 如果设为true，创建符合RFC-4122(v4)标准的UUID字符串，否则返回1970年1月1日至今的毫秒数
	 * @returns {String|Number} 如果isStandard=true，返回########-####-####-####-############格式的字符串，否则返回一个整数
	 */
	ydl.uuid = function(isStandard) {
		if (!isStandard) return new Date().getTime();
		else {
			var i, s = [], itoh = '0123456789abcdef';
			for (i = 0; i < 36; i++) s[i] = Math.floor(Math.random() * 0x10);
			s[14] = 4;
			s[19] = (s[19] & 0x3) | 0x8;
			for (i = 0; i < 36; i++) s[i] = itoh.charAt(s[i]);
			s[8] = s[13] = s[18] = s[23] = '-';
			return s.join('');
		}
	};

	/**
	 * 创建指定范围内的随机数
	 * @param {Number} [min] 返回值下限（含）
	 * @param {Number} [max] 返回值上限（含）
	 * @param {Number} [declen] 小数位数（0-20）
	 * @returns {Number|String} 无参数时返回0-1之间的浮点数；一个参数时返回0到参数之间的随机整数；
	 *			两个参数时返回下限到上限之间的随机整数；三个参数时返回下限到上限之间保留指定小数位的字符串格式的随机数
	 */
	ydl.random = function(min, max, declen) {
		if (arguments.length === 0) return Math.random();
		else if (arguments.length === 1) {
			max = min;
			min = 0;
		}
		var num = min + (Math.random() * (max - min));
		return declen === undefined ? Math.round(num) : num.toFixed(declen);
	};

	/**
	 * 对数组进行排序，返回新数组，不改变原数组
	 * @example //对数字数组排序，返回排序后的数组
	 *			var s1 = [28, 67, 19, 41, 26]; var s2 = ydl.sort(s1);
	 * @example //根据一组文本框的size属性排序，返回排序后的input对象数组
	 *			var inputs1 = $('#div input').get(); var inputs2 = ydl.sort(inputs1, 'size');
	 * @example //根据表格第三列单元格中的数字对表格的所有行排序，返回排序后的表格行对象数组
	 *			var trs1 = $('#the-table tr').get(); var trs2 = ydl.sort(trs1, function(tr) {
	 *				return parseInt($(tr).children(':eq(2)').text());
	 *			});
	 * @param {Array} arr 要排序的数组
	 * @param {String|Function} [attr] 如果arr是对象数组，此参数指定用来排序的属性名，或取得属性值的方法
	 * @param {Boolean} [desc=false] 是否倒序
	 * @returns {Array} 排序后的新数组
	 */
	ydl.sort = function(arr, attr, desc) {
		var newArr = [].concat(arr);
		switch (typeof attr) {
			case 'string':
				//用数组元素的指定属性值排序
				return desc ? newArr.sort(function(a, b) {
					return a[attr] == b[attr] ? 0 : (b[attr] > a[attr] ? 1 : -1);
				}) : newArr.sort(function(a, b) {
					return a[attr] == b[attr] ? 0 : (b[attr] < a[attr] ? 1 : -1);
				});
				break;
			case 'function':
				//用指定函数获取的值排序
				return desc ? newArr.sort(function(a, b) {
					return attr(b) == attr(a) ? 0 : (attr(b) > attr(a) ? 1 : -1);
				}) : newArr.sort(function(a, b) {
					return attr(b) == attr(a) ? 0 : (attr(b) < attr(a) ? 1 : -1);
				});
				break;
			default:
				//直接用数组元素数值排序，如果是字符串，按字符编码排序
				if (typeof attr === 'boolean') desc = attr;
				return desc ? newArr.sort(function(a, b) {
					return a == b ? 0 : (b > a ? 1 : -1);
				}) : newArr.sort();
		}
	};

	/**
	 * 在对象数组中查找包含指定属性值的数组元素
	 * @param {String} value 要查找的值
	 * @param {Array} array 在哪个对象数组中查找
	 * @param {String} attr 要查找的值所在的属性名
	 * @param {String} [ret] 返回的属性值，可以省略，缺省为返回整个对象
	 * @returns {Object} 找到的数组元素，如果没找到则返回空串或null（根据是否提供ret参数决定）
	 */
	ydl.matchArray = function(value, array, attr, ret) {
		if (!array) return ret ? '' : null;
		for (var i = 0; i < array.length; i++) {
			if (array[i][attr] == value) return ret ? (array[i] ? array[i][ret] : '') : array[i];
		}
		return ret ? '' : null;
	};

	/**
	 * 利用合并数组元素的方式连接字符串类（不推荐使用！）
	 * 在IE6浏览器中，由于字符串连接运算符“+”的性能较差，使用合并数组的方式连接字符串会比较快；
	 * 但是IE8以上版本和Firefox、Opera、Chrome等现代浏览器中都对“+”运算符进行了优化，使其成为连接字符串的最快方式，使用本方法反而会降低性能；
	 * 所以，本方法仅应在循环中连接特别大的字符串，同时指定要求兼容IE6的情况下使用，否则一般情况下均应直接使用“+”连接字符串。
	 * @example var sb = new ydl.StringBuilder('hello'); sb.append(' world'); alert(sb.toString());
	 * @example alert(new ydl.StringBuilder('hello').append(' world').toString());	//不要忘记new运算符
	 * @param {String} [str] 初始字符串
	 * @returns {Object} 当前新创建的实例对象
	 */
	ydl.StringBuilder = function(str) {
		var _arr = [];
		str && _arr.push(str);
		this.append = function(str) {
			_arr.push(str);
			return _this;
		};
		this.toString = function() {
			return _arr.join('');
		};
		var _this = this;
		return this;
	};

	/**
	 * 数字或字符串前用字符补足位数
	 * @param {String|Number} input 输入的字符串或数字
	 * @param {Number} n 补齐后的位数
	 * @param {String} [ch='0'] 补齐用的字符
	 * @returns {String} 如果输入字符串的长度小于补齐后的位数，用ch补齐
	 */
	ydl.pad = function(input, n, ch) {
		ch = ch === undefined ? '0' : ch;
		var i = (input += '').length;
		while(i++ < n) input = ch + input;
		return input;
	};

	/**
	 * 创建连续相同字符组成的字符串
	 * @example var s = ydl.string('x', 10); //返回'xxxxxxxxxx'
	 * @param {String|Number} str 包含的字符（允许多个字符）
	 * @param {Number} count 重复的次数
	 * @returns {String} 包含数量为count个str的字符串
	 */
	ydl.string = function(str, count) {
		return new Array(parseInt(count) + 1).join(' ').replace(/ /g, str);
	};

	/**
	 * 从身份证号码获取信息
	 * @param {String} idno 身份证号码，可以是15位或18位
	 * @param {Bool} [includeDistrict] 是否需要返回行政区信息，缺省为false
	 * @returns {Object} 包含出生日期birthdate（yyyy-MM-dd字符串）、年龄age（整数）、性别sex（1=男，2=女）的对象
	 * 		行政区信息province（省）、city（市）、region（区）、district（省市区）
	 */
	ydl.idCardInfo = function(idno, includeDistrict) {
		if (!idno) return null;
		var $tmpInput = $('<input type="hidden" value="' + idno + '" />').appendTo('body');
		if (!ydl.validator($tmpInput, {type: 'idcard', silent: true, focus: false, message: '%v 不是合法的身份证号码。'})) {
			$tmpInput.remove();
			return null;
		}
		$tmpInput.remove();
		var y = idno.length == 18 ? idno.substr(6, 4) : '19' + idno.substr(6, 2);
		var m = idno.substr(idno.length == 18 ? 10 : 8, 2);
		var d = idno.substr(idno.length == 18 ? 12 : 10, 2);
		var birthdate = y + '-' + m + '-' + d;
		var returnInfoObj = {
			birthdate: birthdate,
			age: ydl.dateDiff(birthdate, poolSelect['CURRENT_SYSTEM_DATE'] || ydl.today(), 'y'),
			sex: parseInt(idno.substr(idno.length == 18 ? 16 : 14, 1)) % 2 == 0 ? 2 : 1
		};
		if (includeDistrict) {
			ydl.loadJs('district');
			returnInfoObj.province = ydl.data.district[idno.substr(0, 2) + '0000'];
			returnInfoObj.city = ydl.data.district[idno.substr(0, 4) + '00'];
			returnInfoObj.region = ydl.data.district[idno.substr(0, 6)];
			returnInfoObj.district = returnInfoObj.province + returnInfoObj.city + returnInfoObj.region;
		}
		return returnInfoObj;
	};

	/**
	 * 15位和18位身份证号转换
	 * @param {String} idno 身份证号
	 * @returns {String} 转换后的身份证号
	 */
	ydl.convertIdCard = function(idno) {
		if (idno.length == 15 && /^\d{15}$/.test(idno)) {
			idno = idno.replace(/^(\d{6})(\d{9})$/, '$119$2');
			var s1 = '79A584216379A5842', s2 = '10X98765432', s = 0;
			for (var i = 0; i < 17; i++) s += parseInt(idno.charAt(i)) * parseInt(s1.charAt(i), 16);
			return idno + s2.charAt(s % 11);
		}
		else if (idno.length == 18) {
			return idno.replace(/^(\d{6})\d{2}(\d{9})(\d|x|X)$/, '$1$2');
		}
		else return idno;
	};

	/**
	 * 动态加载指定js文件
	 * @param {String} jsFileName 需要加载的js文件名关键词
	 * @returns undefined
	 */
	ydl.loadJs = function(jsFileName) {
		if (!document.getElementById(jsFileName)) {		//判断js文件是否已加载
			var scriptTag = document.createElement('script');
			scriptTag.id = jsFileName;
			scriptTag.type = 'text/javascript';
			scriptTag.src = ydl.contexPath + '/scripts/ydl.' + jsFileName + '.js';
			$('head').append(scriptTag);
		}
	};

	/**
	 * 数字加千分隔
	 * @param {String|Number} number 数字或数字形式的字符串
	 * @returns {String} 整数位每三位加一个逗号
	 */
	ydl.addComma = function(number) {
		number += '';
		return number.indexOf(',') == -1 ? 	number.split('').reverse().join('').replace(/(\d{3})(?!\d*\.\d+)/g, '$1,').split('').reverse().join('').replace(/^(-?),/, '$1') : number;
	};

	/**
	 * 数字去千分隔
	 * @param {String} number 数字形式的字符串
	 * @returns {String} 去掉数字中的所有逗号
	 */
	ydl.delComma = function(number) {
		return (number + '').replace(/,/g, '');
	};

	/**
	 * 人民币金额转大写
	 * @example ydl.capitalMoney(1020.03);			//返回'壹仟零贰拾元零叁分'
	 * @example ydl.capitalMoney(1020.03, false);	//返回'壹仟零佰贰拾零元零角叁分'
	 * @param {String|Number} money 金额数字（最多2位小数）
	 * @param {Boolean} [readable=true] 是否返回易读形式
	 * @returns {String} 如果readable=false，返回固定位数的大写形式，否则返回口述金额时习惯的形式
	 */
	ydl.capitalMoney = function(money, readable) {
		if (typeof money == 'number') money = money.toFixed(2);
		else money = parseFloat(ydl.delComma(money.replace(/￥/g, ''))).toFixed(2);
		money = money.replace('.', '');
		if (money == '000') return "零元整";
		var sNumber = '零壹贰叁肆伍陆柒捌玖';
		var sUnit = '分角元拾佰仟万拾佰仟亿拾佰仟万拾佰仟';
		var rmb = '';
		for (var i = 0; i < money.length; i++) rmb += sNumber.substr(money.substr(i, 1) - '0', 1) + sUnit.substr(money.length - i - 1, 1);
		if (readable || readable === undefined) {
			var filterUnit = function(s) {
				return s.replace('零亿', '亿').replace('零万', '万').replace('零仟', '零').replace('零佰', '零').replace('零拾', '零');
			};
			var filterZero = function(s) {
				while (s.indexOf('零零') >= 0) s = s.replace('零零', '零');
				return s;
			};
			rmb = filterUnit(filterZero(filterUnit(rmb.replace('零角', '零').replace('零分', '零'))));
			rmb = filterZero(filterUnit(rmb.replace('亿万', '亿零').replace('零万', '零'))).replace('零元', '元');
			if (rmb.charAt(rmb.length - 1) == '零') rmb = rmb.substr(0, rmb.length - 1) + '整';
		}
		return rmb;
	};

	/**
	 * 设置表单元素的布尔属性
	 * @param {String|Object} id 表单对象ID
	 * @param {String} attrName 属性名，可以是'readonly', 'disabled', 'hidden', 'required'
	 * @param {Boolean} [isSet=true] 是否设置属性，true为添加属性，false为取消属性
	 * @returns {Object} id指定的DOM对象
	 */
	ydl.attr = function(id, attrName, isSet) {
		var ele = ydl.getDom(id);
		if (!ele) {
			ydl.log('ydl.attr出错：找不到指定的表单对象', 'red');
			return null;
		}
		if (ele.length && ele.tagName != 'SELECT') {
			for (var i = 0; i < ele.length; i++) arguments.callee(ele[i], attrName, isSet);
			return typeof id === 'string' ? ele : id;
		}
		var tagName = ele.tagName.toLowerCase();
		var eleType = ele.type;
		attrName = attrName.toLowerCase();
		isSet = isSet || isSet === undefined;

		switch (attrName) {
			case 'readonly':	//只读
				setReadOnly();
				break;
			case 'disabled':	//禁用
				if (tagName == 'button') {
					if (isSet) $(ele).button('disable');
					else $(ele).button('enable');
				}
				else setReadOnly();
				break;
			case 'hidden':
				if (isSet) $(ele).addClass('hide');
				else $(ele).removeClass('hide').removeClass('ui-helper-hidden').show();
				break;
			case 'required':
				var $td = $(ele).closest('td');
				var $table = $(ele).closest('table');
				var solveEm = $table.length > 0 && !$table.hasClass('datalist') && $td.length > 0;
				if (isSet) {
					$(ele).attr('required', 'required');
					if(solveEm){
						$td.find('em').remove();
						$td.append('<em>*</em>');
					}
				}
				else {
					$(ele).removeAttr('required');
					if(solveEm){
						var del = true;
						$td.find(':input').each(function(){
							if($(this).attr('required') == 'required') del = false;
						});
						if(del) $td.find('em').remove();
					}
				}
				break;
		}
		return typeof id === 'string' ? ele : id;

		function setReadOnly() {
			var readOnlyRule = [
				{tag: 'input', type: 'text', attr: 'readOnly'},
				{tag: 'select', type: 'select-one', attr: 'disabled'},
				{tag: 'input', type: 'checkbox', attr: 'disabled'},
				{tag: 'input', type: 'radio', attr: 'disabled'},
				{tag: 'textarea', type: 'textarea', attr: 'readOnly'},
				{tag: 'input', type: 'password', attr: 'readOnly'},
				{tag: 'input', type: 'file', attr: 'readOnly'}
			];
			for (var i = 0, l = readOnlyRule.length; i < l; i++) {
				if (tagName === readOnlyRule[i].tag && eleType === readOnlyRule[i].type) {
					var $next = $(ele).next();
					if ($next.is('.ui-autocomplete-input')) {
						$next[0][readOnlyRule[i].attr] = isSet;
						if (isSet) $next.next().button('disable');
						else $next.next().button('enable');
					}
					else ele[readOnlyRule[i].attr] = isSet;
					break;
				}
			}
		}
	};

	/**
	 * 删除表单对象的布尔属性
	 * @param {String|Object} id 表单对象ID
	 * @param {String} attrName 属性名，可以是'readonly', 'disabled', 'hidden', 'required', 'checked', 'selected'
	 * @returns {Object} id指定的DOM对象
	 */
	ydl.removeAttr = function(id, attrName) {
		return ydl.attr(id, attrName, false);
	};

	/**
	 * 设置表格容器行的可见性
	 * @param {Object|String} start 开始行中包含的组件DOM对象、jQuery对象或ID属性
	 * @param {Object|String} end 结束行中包含的组件DOM对象、jQuery对象或ID属性
	 * @param {Boolean} [visible] 是否可见，可省略，缺省为true，即显示
	 * @returns {Object} 包含所有操作行(tr)的jQuery对象，没有选择行时返回null
	 */
	ydl.setRowsVisible = function(start, end, visible) {
		var start = ydl.getDom(start) || document.getElementById('group_' + start);
		var end = ydl.getDom(end) || document.getElementById('group_' + end);
		if (!start) {
			ydl.log('ydl.setRowsVisible出错：找不到指定的开始组件', 'red');
			return null;
		}
		$start = start.tagName == 'TR' ? $(start) : $(start).closest('tr');
		$end = end ? (end.tagName == 'TR' ? $(end).next() : $(end).closest('tr').next()) : null;
		return $start.nextUntil($end).andSelf().each(function() {
			if (visible || visible === undefined) $(this).show();
			else $(this).hide();
		});
	};

	/**
	 * 跨列合并单元格
	 * @param {String|Object} firstCell 指定第一个单元格，可以是单元格或单元格内组件的ID、DOM对象或jQuery对象
	 * @param {Number} cellCount 要合并的单元格数
	 * @returns {Object} 合并后的单元格的jQuery对象
	 */
	ydl.mergeCell = function(firstCell, cellCount) {
		var td = ydl.getDom(firstCell);
		if (!td) return;
		var $td = td.tagName == 'TD' || td.tagName == 'TH' ? $(td) : $(td).closest('td');
		var $nextTd = $td.next();
		for (var i = 0; i < cellCount; i++) {
			$nextTd.contents().appendTo($td);
			$nextTd.remove();
			$nextTd = $td.next();
		}
		//如果含有多个必填标志，只保留最后一个
		var $em = $td.children('em');
		if ($em.length > 1) $em.filter(':lt(' + ($em.length - 1) + ')').remove();
		return $td.attr('colspan', $td[0].colSpan + cellCount - 1);
	};

	/**
	 * 合并多个表格行，将同一列的单元格合并，跨不同列的单元格除外
	 * @param {String|Object} firstRow 指定第一行，可以是表格、表格行或表格行内任意组件的ID、DOM对象或jQuery对象
	 * @param {Number} rowCount 要合并的行数
	 * @returns undefined
	 */
	ydl.mergeRows = function(firstRow, rowCount) {
		var row = ydl.getDom(firstRow);
		if (!row) return;
		if (row.tagName == 'TABLE') row = row.rows[0];
		else if (row.tagName != 'TR') row = $(row).closest('tr')[0];
		var $row = $(row);	//当前行
		var $cells = [];	//所有行中的单元格
		var colspan = [];	//各单元格跨列数
		for (var r = 0; r < rowCount; r++) {
			colspan.push([]);
			$cells.push($row.children().each(function() {
				colspan[r].push(this.colSpan);
				if (this.colSpan > 1) for (var i = 0; i < this.colSpan - 1; i++) colspan[r].push(0);
			}));
			$row = $row.next();
		}
		function getCell(rowNo, col) {
			var c = 0;
			for (var i = 0, il = $cells[rowNo].length; i < il; i++) {
				c += $cells[rowNo].eq(i)[0].colSpan;
				if (c >= col + 1) return $cells[rowNo].eq(i);
			}
		}
		for (var c = 0, cl = colspan[0].length; c < cl; c++) {
			var $cellThisRow = getCell(0, c);
			var $cellNextRow;
			var cols = colspan[0][c];
			for (var r = 1; r < rowCount; r++) {
				$cellNextRow = getCell(r, c);
				if (cols != 0 && colspan[r][c] == cols) {
					$cellThisRow.append($cellNextRow.contents())[0].rowSpan++;
					$cellNextRow.remove();
					//如果含有多个必填标志，只保留最后一个
					var $em = $cellThisRow.children('em');
					if ($em.length > 1) $em.filter(':lt(' + ($em.length - 1) + ')').remove();
				}
				else {
					cols = colspan[r][c];
					$cellThisRow = $cellNextRow;
				}
			}
		}
	};


	/**
	 * 表单数据校验方法
	 * @param {Object|jQuery|String} field 表单字段DOM对象，或字段的JQuery对象，或字段id
	 * @param {Object} [def] 表单字段规则定义，对象 {
	 *		{Boolean} silent 静默模式，如果设为true，不符合校验规则时不弹出警告对话框，而是通过div显示信息。缺省为false，使用alert弹出信息。
	 * 		{String} desc 字段描述，缺省为getLabel的值
	 * 		{String} message 校验失败后的自定义提示信息，可省略，可以用%f引用字段名描述，%v引用输入值，%l引用下限值，%h引用上限值
	 *		{Boolean} focus 校验失败后是否使被校验字段获得焦点，缺省为true
	 * 		{Boolean} required 检查字段值是否非空
	 * 		{String} type 字段类型，可以是以下字符串之一：date,time,month,longmonth,day,longday,yyyymm,number,int,float,
	 * 			validchar,money,phone,phones,email,zipcode,idcard,hanzi,ipv4，详细规则见相应check方法的注释，与rule和date不能同时使用
	 *      {Boolean} negative 是否允许负数，与type=int,float,money配合使用，缺省为false
	 * 		{Number|Array} length 字符串长度，或长度范围数组[最小长度, 最大长度]
	 * 		{Number|Array} length2 字符串长度，或长度范围数组[最小长度, 最大长度]，按全角两个字符计算
	 * 		{Number} declen 小数位数，与type=float或money配合使用，缺省时float不检查小数位，money默认2位
	 * 		{Array} range 数值范围数组[最小值, 最大值]，仅应用于数值类型
	 * 		{String|Boolean|Function|Object} rule 检查规则，与type和date不能同时使用，允许的值详见checkField方法的注释
	 * 		{Array} date 检查日期是否合法，数组[年, 月, 日]，年可省略。与type和rule不能同时使用
	 * 		{Boolean} uppercase 校验前是否转为大写
	 *		{Boolean} lowercase 校验前是否转为小写
	 * 		{Boolean} keepspace 是否保留前后空格（如果不加这个参数缺省为校验前去掉前后空格）
	 * 		{Boolean} addcomma 通过验证后是否给金额添加千分隔逗号，与type=money配合使用（如果不加这个参数缺省为校验前去掉千分隔）
	 * 		{String|Array|Object} except 例外值，字符串、字符串数组或正则表达式，应与type或rule或date配合使用
	 * }
	 * @returns {Boolean} 如果提供了def参数，按def定义的规则验证，符合规则返回true，否则提示并返回false；如果没有提供def参数则返回是否为空
	 */
	var _validatorDefault = {
		keepspace: false,
		uppercase: false,
		lowercase: false,
		addcomma: false,
		silent: true,
		focus: true
	};
	ydl.validator = function(inputField, def) {
		//清除页面上的校验错误提示
		if (inputField == 'reset' && def === undefined) {
			$('input,select,textarea').each(function() {
				$(this).removeData('errorMessages').removeClass('error');
			});
			$('#validator_tip').hide();
			return;
		}

		//修改缺省值
		if ($.isPlainObject(inputField)) {
			$.extend(_validatorDefault, inputField);
			return;
		}

		try {
			//应用缺省值
			if (!def) def = {required: true};
			def = $.extend({}, _validatorDefault, def);
			//ydl.log('校验规则：' + ydl.getMember(def));
			//表单字段DOM对象
			var field = ydl.getDom(inputField);
			//处理多个对象
			if (field instanceof Array) {
				var r = true;
				for (var i = 0; i < field.length; i++) {
					r = arguments.callee(field[i], def) && r;
					if (!(def && def.silent || r)) break;
				}
				return r;
			}
			if (!field) throw '字段 ' + String(inputField) + ' 没找到';
			//如果字段被隐藏，不做校验，直接返回true
			//if ($(field).is(':hidden')) return true;
			//表单字段值
			var fieldValue = field.value;
			//本次检查实例信息
			//var instance = (ydl.getMember(def) + '').replace('rule: false', 'rule: true');
			var instance = '';
			//ydl.log(instance, 'green');
			//表单字段描述
			var fieldDesc = def && def.desc ? def.desc : ydl.getLabel(field, '输入字段');
			//自定义提示信息
			var message = def && def.message ? def.message : undefined;
			ydl.log('正在检查：' + field.id + '(' + fieldDesc + ') = "' + fieldValue + '"');

			//如果没有keepspace=true参数，文本框去掉前后空格
			if ((!def || def && !def.keepspace) && field.type == 'text') field.value = fieldValue = fieldValue.delSpace();

			//如果存在字段定义，按照定义验证字段，符合规则返回true，不符合规则返回false
			if (def) {

				//检查例外值
				if (def.except) {
					var exceptType = $.type(def.except);
					//字符串
					if (exceptType === 'string' && !checkField(fieldValue != def.except, '%f不允许输入%v')) return false;
					//字符串数组
					if (exceptType === 'array') {
						for (var i = def.except.length; i--; ) if (!checkField(fieldValue != def.except[i], '%f不允许输入%v')) return false;
					}
					//正则表达式
					if (exceptType === 'regexp' && !checkField(function() {
						return !def.except.test(fieldValue);
					}, '%f不允许输入%v')) return false;
				}

				//检查是否非空
				if (def.required && !checkBlank()) return false;

				//改变大小写
				if (def.uppercase) field.value = fieldValue.toUpperCase();
				if (def.lowercase) field.value = fieldValue.toLowerCase();

				//检查最大、最小长度
				if (!checkLength(def.length, false)) return false;
				if (!checkLength(def.length2, true)) return false;

				//检查字段类型
				if (fieldValue != '' && def.type) {
					switch (def.type.toLowerCase()) {
						case 'date': 		if (!checkDate()) return false; break;
						case 'time': 		if (!checkTime()) return false; break;
						case 'number': 		if (!checkNumber()) return false; break;
						case 'int': 		if (!checkInt()) return false; break;
						case 'float': 		if (!checkFloat()) return false; break;
						case 'month': 		if (!checkMonth(false)) return false; break;
						case 'longmonth': 	if (!checkMonth(true)) return false; break;
						case 'day': 		if (!checkDay(false)) return false; break;
						case 'longday': 	if (!checkDay(true)) return false; break;
						case 'yyyymm': 		if (!checkYearAndMonth()) return false; break;
						case 'validchar': 	if (!checkValidChar()) return false; break;
						case 'money': 		if (!checkMoney()) return false; break;
						case 'phone': 		if (!checkPhone()) return false; break;
						case 'phones': 		if (!checkPhones()) return false; break;
						case 'email': 		if (!checkEmail()) return false; break;
						case 'zipcode': 	if (!checkZipCode()) return false; break;
						case 'idcard': 		if (!checkIdCard()) return false; break;
						case 'hanzi': 		if (!checkHanzi()) return false; break;
						case 'ipv4': 		if (!checkIpv4()) return false; break;
						case 'orgcode':		if (!checkOrgCode()) return false; break;
					}
				}
				//检查规则
				if (def.rule !== undefined && !checkField(def.rule, def.message)) {
					return false;
				}
				//检查日期是否合法
				if (def.date) {
					return checkField(checkValidDate(def.date), '%f不是正确的日期，请重新输入！');
				}

				//检查数值范围
				if (fieldValue != '' && $.isArray(def.range) && !isNaN(def.range[0]) && !isNaN(def.range[1])) {
					if (message !== undefined) message = message.replace(/%l/gi, def.range[0]).replace(/%h/gi, def.range[1]);
					if (!checkField(!isNaN(fieldValue) && parseFloat(fieldValue) >= parseFloat(def.range[0]) &&
						parseFloat(fieldValue) <= parseFloat(def.range[1]),
						'【' + fieldDesc + '】应为 ' + '%l' + ' 到 ' + '%h' + ' 之间的数字，请重新输入！',{rangeMin:def.range[0],rangeMax:def.range[1]})) return false;
				}

				//如果设置了addcomma=true，加千分隔
				if (fieldValue != '' && def.addcomma) field.value = ydl.addComma(fieldValue);

				return true;
			}
			//不存在字段定义则默认检查是否非空
			else return checkBlank();
		}
		catch (ex) {
			ydl.log('ydl.validator出错: ' + ydl.error(ex), 'red');
			return false;
		}

		/**
		 * 检查字段值是否符合规则
		 * @param {String|Boolean|RegExp|Function} rule 检查规则，可以是字符串（比较是否相同）、布尔表达式、正则表达式对象、或者验证是否符合规则的函数
		 * @param {String} [msg] 不符合规则时的提示信息（可用%f引用字段名描述，%v引用输入值），缺省为message参数的内容
		 * @returns {Boolean} 如果字段值不符合规则显示提示信息并返回false，符合规则返回true
		 */
		function checkField(rule, msg, info) {
			//def定义的message参数优先于check方法的提示信息，如果都没定义，显示默认信息
			msg = message === undefined ? (
				msg === undefined ? '【' + fieldDesc + '】格式不正确，请重新输入！' :
					msg.replace(/%f/gi, '【' + fieldDesc + '】').replace(/%v/gi, fieldValue)
			) : message.replace(/%f/gi, '【' + fieldDesc + '】').replace(/%v/gi, fieldValue);

			instance = msg;
			if(info != undefined){
				$.map(info,function(value,key){
					if(key == 'fieldLength')	msg = msg.replace(/%n/gi, info.fieldLength);
					else if(key == 'rangeMin')	msg = msg.replace(/%l/gi, info.rangeMin);
					else if(key == 'rangeMax')	msg = msg.replace(/%h/gi, info.rangeMax);
				});
			}
			var ok;	//是否符合规则
			switch ($.type(rule)) {
				case 'function':	ok = rule(field); break;			//函数
				case 'regexp':		ok = rule.test(fieldValue); break;	//正则表达式
				case 'boolean':		ok = rule; break;					//逻辑表达式
				default:			ok = (fieldValue == rule);			//匹配字符串或数字
			}
			ydl.log('规则：' + $.type(rule) + /*'|' + String(rule) +*/ '|' + ok);
			var $field = $(field);
			var $next = $field.next();	//用来判断combo
			var $ele = $next.is('.ui-autocomplete-input') ? $next : $field;	//提示错误消息的目标对象
			//保存同一个字段多次校验错误信息的对象
			var errorMessages = $ele.data('errorMessages');
			if (!errorMessages) errorMessages = {};
			//清除上次发生的错误消息
			$.map(errorMessages,function(value,key){
				if(value.value != fieldValue) delete errorMessages[key];
			});
			if (ok) {
				if (errorMessages[fieldValue+instance]) {
					delete errorMessages[fieldValue+instance];
				}
			}
			else {
				if (!def || def.focus !== false) {
					try {
						//激活tab页
						var $tabPanel = $field.closest('.ui-tabs-panel');
						if ($tabPanel.length > 0) $tabPanel.parent().tabs('select', '#' + $tabPanel[0].id);
						//设置焦点，选中已输入的内容
						$ele.focus();
						$ele.select();
					}
					catch (ex) {
						ydl.log('ydl.validator: ' + field.id + '(' + fieldDesc + ')设置焦点失败，可能被隐藏', 'orange');
					}
				}
				//如果指定了静默模式，在动态层中显示，否则弹出alert对话框
				if (def && def.silent) {
					errorMessages[fieldValue+instance] = {value: fieldValue, message: msg};
				}
				else {
					$ele.addClass('error');
					alert(msg);
					$ele.removeClass('error');
				}
			}
			if (def && def.silent) {
				if ($.isEmptyObject(errorMessages)) $ele.removeClass('error');
				else $ele.addClass('error');
			}
			$ele.data('errorMessages', errorMessages);
			return ok;
		}

		//检查是否非空
		function checkBlank() {
			return checkField(fieldValue != '', '%f为必填项，请输入！');
		}
		//检查日期格式yyyy-mm-dd
		function checkDate() {
			field.value = fieldValue = field.value.replace(/^(\d{4})(\d{2})(\d{2})$/, '$1-$2-$3'); //允许输入的年月日之间不加横线，自动添加
			if (!checkField(/^(\d{4})-(0[1-9]|1[012])-(0[1-9]|[12]\d|3[01])$/, '%f必须是正确的日期！\n（yyyy-mm-dd）')) return false;
			return checkField(checkValidDate([fieldValue.substr(0, 4), fieldValue.substr(5, 2), fieldValue.substr(8, 2)]), '%f不是正确的日期，请重新输入！');
		}
		//检查时间格式hh:mm:ss
		function checkTime() {
			return checkField(/^([01]\d|2[0-3]):([0-5]\d):([0-5]\d)$/, '%f必须是正确的时间格式！\n（hh:mm:ss）');
		}
		//检查月份格式（1-12）
		//@param {Boolean} longFormat 是否长格式（01-12）
		function checkMonth(longFormat) {
			var zero = longFormat ? '0' : '';
			return checkField(new RegExp('^(' + zero + '[1-9]|1[012])$'), '%f必须是正确的月份！\n（' + zero + '1～12）');
		}
		//检查日期格式（1-31）
		//@param {Boolean} longFormat 是否长格式（01-31）
		function checkDay(longFormat) {
			var zero = longFormat ? '0' : '';
			return checkField(new RegExp('^(' + zero + '[1-9]|[12]\\d|3[01])$'), '%f必须是正确的日期！\n（' + zero + '1～31）');
		}
		//检查年月格式yyyymm
		function checkYearAndMonth() {
			return checkField(/^\d{4}(0[1-9]|1[012])$/, '%f必须是yyyymm格式（四位数字年份加两位数字月份）');
		}
		//检查数字格式
		function checkNumber() {
			return checkField(/^\d+$/, '%f必须输入数字！');
		}
		//检查整数格式
		function checkInt() {
			return checkField(new RegExp('^(' + (def.negative ? '-?' : '') + '[1-9]\\d*|0)$'), '%f必须是' + (def.negative ? '' : '正') + '整数！');
		}
		//检查浮点数格式（含负数、小数）
		function checkFloat() {
			return checkField(new RegExp('^(' + (def.negative ? '-?' : '') + '(([1-9]\\d*)|0)(\\.\\d{1,' + (def.declen || '') + '})?)$'),
				'%f必须是数字！\n' + (def.declen ? '不超过' + def.declen + '位小数，' : '') + (def.negative ? '允许负数' : ''));
		}
		//检查字符型格式（不能包含' " \等特殊字符）
		function checkValidChar() {
			return checkField(function() {
				return !/['"\\\|~]/.test(fieldValue);
			}, '%f中不能含有以下字符！\n（\' " \\ | ~）');
		}
		//检查汉字格式（必须全部是汉字）
		function checkHanzi() {
			return checkField(/^([\u4e00-\u9fa5])+$/, '%f必须全部是汉字！');
		}
		//检查金额格式（大于等于零的数字，不超过2位小数），如果没有设置keepcomma参数，检查前去掉千分隔
		function checkMoney() {
			if (!def.keepcomma) field.value = fieldValue = fieldValue.replace(/[, ]/g, '');
			return checkField(new RegExp('^(' + (def.negative ? '-?' : '') +
				'(([1-9]\\d*)|0)' + (def.declen == '0' ? '' : '(\\.\\d{1,' + (def.declen || '2') + '})?') + ')$'),
				'%f必须是正确的金额格式！\n（最多' + (def.declen || '2') + '位小数，' + (def.negative ? '' : '不') + '允许负数）');
		}
		//检查电话号码格式，如13812341234、010-12345678、(0431)1234567-1234等，详细规则如下：
		//	1、可以是1开头的11位数字（手机号）
		//	2、可以是“区号-电话号-分机号”或者是“(区号)电话号-分机号”格式
		//	3、区号是0开头的3～4位数字，可以没有区号
		//	4、电话号是5～8位数字，不能以0开头
		//	5、分机号是1～8位数字，可以没有分机号
		function checkPhone() {
			field.value = fieldValue = fieldValue.replace(/（/g, '(').replace(/）/g, ')');
			return checkField(/^(1\d{10}|(0\d{2,3}-|\(0\d{2,3}\))?[1-9]\d{4,7}(-\d{1,8})?)$/,
				'%f必须是正确的电话号码格式！\n（例如13812345678、010-12345678、(010)12345678-1234等）');
		}
		//检查多个电话号码，以半角分号（;）隔开，规则同上
		//检查前将其它分隔符（；，,空格）转换为半角分号（;）
		function checkPhones() {
			return checkField(function() {
				var regex = /^(1\d{10}|(0\d{2,3}-|\(0\d{2,3}\))?[1-9]\d{4,7}(-\d{1,8})?)$/;
				field.value = fieldValue = fieldValue.replace(/[；，, ]/g, ';').replace(/（/g, '(').replace(/）/g, ')');
				var arr = fieldValue.split(';');
				for (var i = 0; i < arr.length; i++) {
					if (!regex.test(arr[i])) return false;
				}
				return true;
			}, '%f中含有格式不正确的电话号码！');
		}
		//检查电子邮箱格式（xxx@xxx.xxx）
		function checkEmail() {
			return checkField(/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/, '%f必须是正确的电子信箱格式！\n（例如 username@website.com）');
		}
		//检查邮政编码（6位数字）
		function checkZipCode() {
			return checkField(/^\d{6}$/, '%f必须是正确的邮政编码！\n（6位数字）');
		}
		//检查身份证号码（15位或18位数字，18位的最后一位可以是X，并判断出生日期和校验位是否正确）
		function checkIdCard() {
			field.value = fieldValue = fieldValue.toUpperCase();
			return checkField(function() {
				if (/^(\d{15}|\d{17}(\d|X))$/.test(fieldValue)) {
					//检查出生日期
					if (!checkValidDate((fieldValue.length == 15 ? '19' + fieldValue.substr(6, 6) :
						fieldValue.substr(6, 8)).match(/^(\d{4})(\d{2})(\d{2})$/).slice(1))) return false;
					//检查校验位
					if (fieldValue.length == 18) {
						var s1 = '79A584216379A5842', s2 = '10X98765432', s = 0;
						for (var i = 0; i < 17; i++) s += parseInt(fieldValue.charAt(i)) * parseInt(s1.charAt(i), 16);
						return s2.charAt(s % 11) == fieldValue.charAt(17);
					}
					else return true;
				}
				else return false;
			}, '%f必须是正确的身份证号码！\n（15或18位数字，最后一位可以是X，出生日期和校验位必须正确）');
		}
		//检查ipv4地址
		function checkIpv4() {
			return checkField(/^(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.){3}(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))$/, '%f必须是合法的IP地址，例如192.168.0.1');
		}
		//检查组织机构代码
		function checkOrgCode() {
			return checkField(function() {
				if (!/^[A-Za-z0-9]{8}-?[0-9Xx]$/.test(fieldValue)) return false;
				if (fieldValue.charAt(8) != '-') fieldValue = fieldValue.replace(/^(.{8})(.+)$/, '$1-$2');
				field.value = fieldValue.toUpperCase();
				var W = [3, 7, 9, 10, 5, 8, 4, 2];
				var sum = 0;
				for (i = 0; i < 8; i++) {
					var c = fieldValue.substr(i, 1);
					if (c >= '0' && c <= '9') {
						sum += W[i] * parseInt(c);
					}
					else if (c >= 'A' && c <= 'Z') {
						sum += W[i] * (c.charCodeAt(0) - 'A'.charCodeAt(0) + 10);
					}
					else return false;
				}
				var checksum = 11 - sum % 11;
				if (checksum == 10) checksum = 'X';
				else if (checksum == 11) checksum = '0';
				return fieldValue.charAt(fieldValue.length - 1) == checksum;
			}, '%f必须是正确的组织机构代码！\n（例如ABC12345-1、40000000X）');
		}

		//检查字符长度
		//@param {Number|Number[]} defLength 规则定义的长度范围
		//@param {Boolean} useLength2 是否按全角两个字符计算
		function checkLength(defLength, useLength2) {

			var fieldLength;
			if (def.type == 'money' || $(field).attr('data-type') == 'money') {
				fieldValue = ydl.delComma(fieldValue);	//金额取长度前去掉千分隔符
				fieldLength = useLength2 ? fieldValue.replace(/\./g, '').length2() : fieldValue.replace(/\./g, '').length;
			}
			else fieldLength = useLength2 ? fieldValue.length2() : fieldValue.length;
			var tip = useLength2 ? '\n（汉字按2个字符计算）' : '';
			if (defLength !== undefined) {
				if (!isNaN(defLength)) {
					if (message !== undefined) message = message.replace(/%h/gi, defLength);
					if (!checkField(fieldLength == parseInt(defLength), '【' + fieldDesc + '】的长度应为 ' + defLength +
						' 个字符，您输入了 ' + '%n' + ' 个字符，请重新输入！' + tip,{fieldLength:fieldLength})) return false;
				}
				if ($.isArray(defLength) && !isNaN(defLength[0]) && !isNaN(defLength[1])) {
					//var info = 'rule-length:【' + fieldDesc + '】的长度应在 ' + defLength[0] + ' 到 ' + defLength[1] + ' 个字符之间，您输入了n个字符，请重新输入！' + tip;//20120910
					if (message !== undefined) message = message.replace(/%l/gi, defLength[0]).replace(/%h/gi, defLength[1]);
					if (!checkField(fieldLength >= parseInt(defLength[0]) && fieldLength <= parseInt(defLength[1]),
						'【' + fieldDesc + '】的长度应在 ' + defLength[0] + ' 到 ' + defLength[1] + ' 个字符之间，您输入了 ' +
						'%n' + ' 个字符，请重新输入！' + tip,{fieldLength:fieldLength})) return false;
				}
			}
			return true;
		}
	};

	/**
	 * 表单数据校验方法
	 * 该方法针对jQuery对象使用，可以是多个对象
	 * @param {Object} [def] 与ydl.validator的def参数相同
	 * @returns {Boolean} 是否通过校验，如果是多个对象，只要有一个没有通过校验即返回false
	 */
	$.fn.validator = function(def) {
		var result = true;
		this.each(function() {
			result &= ydl.validator(this, def);
			if ((!def || !def.silent) && !result) return false;
		});
		return !!result;
	};

	/**
	 * Ajax 查询/提交数据
	 * 要求服务器返回固定格式的JSON数据：{"returnCode":0, "message":"", "data":{}}
	 * returnCode=0成功，-1登录超时，其他非0值均应在message中提供错误信息
	 * @param {String} url 提交的地址
	 * @param {Object|String} [data] 提交到服务器的数据，对象{name1:value1,name2:value2}，或者字符串'name1=value1&name2=value2'
	 * @param {Function} [success(data)] 成功后执行的回调函数，将服务器返回的JSON对象作为参数
	 * @param {Object} [options] jQuery的ajax选项，以下是常用选项，其他选项请参考jQuery文档 {
	 *		{String} [type='POST'] 提交类型（'GET'或'POST'）
	 *		{Boolean} [async=false] 是否异步提交（缺省为false）
	 *		{String} [dataType='json'] 返回数据类型（xml, json, script, html，缺省为json）
	 *		{Function} [handleError] 处理错误的函数，在服务器成功返回了json数据但返回码非0时执行，与error函数的区别是后者在服务器没有成功返回数据时执行
	 *		{Object} [errorCode] 错误码和错误信息对照表，例如{'3': '更新出错', '5': '删除出错'}，如果提供了此参数，将忽略服务器返回的message信息，使用此处的定义显示
	 *		{Boolean} [silent=false] 是否弹出提示信息框（超时信息不受影响）
	 * }
	 * @returns undefined
	 */
	ydl.ajax = function(url, data, success, options) {
		//特殊请求不输出日志
		//var showLog = url.indexOf('systemMessage.jsp') == -1 && url.indexOf('taskList.jsp') == -1 && url.indexOf('customFunc.jsp') == -1;
		var showLog = true;
		if (showLog) ydl.log('Ajax Request：' + url + '\n' + ydl.getMember(data));
		var t = ydl.timer.start();
		var isSync = !(options && options.async);
		if (isSync) ydl.displayRunning();
		var dataType = options && options.dataType || 'json';
		$.ajax($.extend(options, {
			data: data,
			async: !isSync,
			dataType: dataType,
			type: options && options.type ? options.type.toUpperCase() : 'POST',
			url: url + (url.indexOf('?') >= 0 ? '&' : '?') + 'uuid=' + ydl.uuid(),
			error: function(jqXHR, textStatus, errorThrown) {
				if (showLog) ydl.log('Ajax Response：(' + ydl.timer.stop(t) + 'ms)\n' + jqXHR.responseText);
				if (!options || options.silent !== true) alert('对不起，您的操作出错了！' + ({
					'timeout': '服务器超时',
					'error': '服务器出错',
					'abort': '操作已取消',
					'parsererror': '服务器返回的数据格式不正确'
				}[textStatus] || textStatus) + '\n\n' + errorThrown);
				if (isSync) ydl.displayRunning(false);
			},
			success: function(data, textStatus, jqXHR) {
				if (showLog) ydl.log('Ajax Response：(' + ydl.timer.stop(t) + 'ms)\n' + (dataType == 'json' ? ydl.getMember(data) : jqXHR.responseText));
				if (data.returnCode && data.returnCode != 0) {
					if (data.returnCode == -1) {
						alert(data.message ? data.message :'登录超时，请退出系统并重新登录！');
						ydl.logout();	//如果返回码是-1，自动退出登录
					}
					else if (options && options.handleError) {
						options.handleError(data);	//回调错误处理函数
					}
					else {
						if (!options || options.silent !== true) alert('提示信息：\n\n' + ((options && options.errorCode && options.errorCode[data.returnCode] !== undefined ?
							options.errorCode[data.returnCode] : data.message) || ''));
					}
				}
				else success && success(data.data ? data.data : data, data);	//回调成功处理函数
				if (isSync) ydl.displayRunning(false);
			}
		}));
	};

	/**
	 * 获取服务器信息
	 * @returns {Object} 包含服务器日期、时间等属性的对象
	 */
	ydl.getServerInfo = function() {
		var result = null;
		ydl.ajax(ydl.contexPath + '/platform/getServerInfo.jsp', null, function(data) {
			result = data;
		});
		return result;
	};


	/**
	 * 从datalist列表行中获取指定字段名的单元格
	 * @example $(row).getCell('opername'); //获取本行中列id为opername的单元格
	 * @example $(row).getCell(3); 			//获取本行中第3个单元格
	 * @example $(row).getCell('opername', 'select');  //获取本行中列id为opername的单元格中的下拉列表
	 * @example $(row).getCell('opername').children('select');  //与上一个例子相同
	 * @param {String|Number} id 在datalist组件中定义的字段id，或者列顺序号（包括复选框、行号和隐藏列）
	 * @param {String} children jQuery选择符，用来选择单元格的子节点
	 * @returns {Object} 单元格（TD）的jQuery对象
	 */
	$.fn.getCell = function(id, children) {
		try {
			if (this[0].tagName === 'TR') {
				var $td;
				if (typeof id === 'string') {
					var $table = this.closest('table');
					var index = $table.find('th').index($table.find('th._' + id));
					$td = this.find('td:eq(' + index + ')');
				}
				else $td = $(this[0].cells[id]);
				if (children) return $td.find(children);
				else return $td;
			}
			else {
				ydl.log('$.fn.getCell出错：该方法只能针对表格行使用', 'red');
				return null;
			}
		}
		catch (ex) {
			ydl.log('$.fn.getCell出错：' + ydl.error(ex), 'red');
			return null;
		}
	};

	/**
	 * 显示隐藏表格列
	 * @param {String|Object} table 表格的id属性，或者表格DOM对象，或者表格jQuery对象
	 * @param {String|Number} col 表格列id，或者列索引
	 * @param {Boolean} [isShow=true] 是否显示该列
	 * @returns {Number} 选择列的索引
	 */
	ydl.columnVisible = function(table, col, isShow) {
		if (isShow === undefined) isShow = true;
		table = ydl.getDom(table);
		$table = $(table);
		if (typeof col === 'string') col = $table.find('th').index($table.find('th._' + col));
		$table.find('tr').each(function() {
			if (isShow) $(this.cells[col]).removeClass('hide');
			else $(this.cells[col]).addClass('hide');
		});
		return col;
	};

	/**
	 * 给列表添加列选择器
	 * @param {String} table 表格容器或动态列表组件的id属性，或DOM对象，或jQuery对象
	 * @param {Array} defaultCols 初始时显示的列
	 * @param {Object} [parent] 选择器的父容器，如果省略，显示在列表前面
	 * @returns {Object} 选择器对象
	 */
	ydl.listColSelector = function(table, defaultCols, parent) {
		table = ydl.getDom(table);
		if (!table) return null;
		var $list = $(table);
		var tableId = table.id || 'list' + ydl.uuid();
		var check = '';
		var cols = $list.find('tr:first').children().each(function(index, th) {
			check += '<li><input type="checkbox" value="' + index + '" id="chk_' + tableId + '_' + index +
				'" /><label for="chk_' + tableId + '_' + index + '">' + $(th).text() + '</label></li>';
		}).length;
		var $ul = $('<ul class="multivalue horizontal"><li><input type="checkbox" value="A" id="chk_' + tableId +
			'_all" /><label for="chk_' + tableId + '_all">（所有列）</label></li>' + check +
			'</ul>').find('input').click(function() {
			if (this.value == 'A') {
				if (this.checked) {
					$(this).parent().siblings().children('input').attr('checked', 'checked');
					$list.find('td,th').show();
				}
				else {
					$(this).parent().siblings().children('input').removeAttr('checked');
					$list.find('td,th').hide();
				}
			}
			else {
				var col = parseInt(this.value) + 1;
				var $col = $list.find('td:nth-child(' + cols + 'n+' + col + '),th:nth-child(' + cols + 'n+' + col + ')')
				if (this.checked) $col.show();
				else {
					$('#chk_' + tableId + '_all')[0].checked = false;
					$col.hide();
				}
			}
		}).end();
		if (parent) $ul.appendTo(parent);
		else $list.before($('<div class="container fit"></div>').append($ul));
		ydl.unifyWidth($ul);
		for (var i = 0; i < cols; i++) {
			if ($.inArray(i, defaultCols) == -1) $list.find('td:nth-child(' + cols + 'n+' + (i + 1) +
				'),th:nth-child(' + cols + 'n+' + (i + 1) + ')').hide();
			else $('#chk_' + tableId + '_' + i)[0].checked = true;
		}
	};


	/**
	 * 在当前列表表格下面创建页码栏
	 * 注意此方法的调用者和返回值是列表表格对象（table），而不是页码栏对象（div）
	 * @example $(listTable).pagebar({……});	//初始化页码栏
	 * @example $(listTable).pagebar({currentPage:1, pageCount: 10, totalCount: 110});	//更新页码信息（页码栏已初始化后使用）
	 * @param {Object} options 选项 {
	 *		{String|Number} currentPage 当前页码初始值
	 *		{String|Number} pageCount 总页码初始值
	 *		{String|Number} totalCount 总记录数初始值
	 *		{Function} gotoPage(pageNo) 页面跳转时执行的函数，传入用户输入的页码作为参数，返回值为对象{currentPage, pageCount, totalCount}
	 *		{Object} buttons 其他按钮 {
	 *			'按钮id': {text: '按钮显示文字', click: 点击按钮时执行的函数, icon: '按钮图标样式名（ui-icon）'}
	 * 		}
	 * }
	 * @returns {Object} 列表表格jQuery对象
	 */
	$.fn.pagebar = function(options) {
		var listId = this[0].id;
		var barId = 'list_' + listId + '_buttons';
		if (document.getElementById(barId)) {
			var $bar = $('#' + barId);
			pageInfo(options.currentPage, options.pageCount, options.totalCount);
			return this;
		}
		else {
			//创建页码栏对象
			var disableFirst = options.currentPage <= 1 ? ' disabled="disabled"' : '';
			var disableLast = options.currentPage >= options.pageCount ? ' disabled="disabled"' : '';
			var $bar = $('<div class="page-buttons" id="' + barId + '"></div>')
				.append($('<button type="button" class="first"' + disableFirst + '>&nbsp;</button>').click(function() {
					gotoPage(1);
				}).button({icons: {primary: 'ui-icon-seek-first'}}).removeClass('ui-corner-all').addClass('ui-corner-left'))
				.append($('<button type="button" class="prev"' + disableFirst + '>&nbsp;</button>').click(function() {
					gotoPage($bar.data('currentPage') - 1);
				}).button({icons: {primary: 'ui-icon-seek-prev'}}).removeClass('ui-corner-all'))
				.append('<span>' + (options.totalCount == 0 ? '' : options.currentPage + ' / ') + options.pageCount + ' 页 共 ' + options.totalCount + ' 条</span>')
				.append($('<button type="button" class="next"' + disableLast + '>&nbsp;</button>').click(function() {
					gotoPage($bar.data('currentPage') + 1);
				}).button({icons: {secondary: 'ui-icon-seek-next'}}).removeClass('ui-corner-all'))
				.append($('<button type="button" class="last"' + disableLast + '>&nbsp;</button>').click(function() {
					gotoPage($bar.data('pageCount'));
				}).button({icons: {secondary: 'ui-icon-seek-end'}}).removeClass('ui-corner-all').addClass('ui-corner-right'))
				.append($('<input type="text" size="3" maxlength="5" class="ui-corner-left" />').keydown(function(event) {
					event.which == ydl.common.keys.Enter && $(this).next().click();
				}))
				.append($('<button type="button" class="go">转到</button>').click(function() {
					var pageNo = parseInt($(this).prev().val().trim());
					if (!isNaN(pageNo)) gotoPage(pageNo);
				}).button().removeClass('ui-corner-all').addClass('ui-corner-right'));
			//添加其他按钮
			if (options.buttons) $.each(options.buttons, function(key, value) {
				if (value) {
					var $button = $('<button type="button" class="button" id="' + barId + '_' + key + '">' +
						value.text + '</button>').click(value.click);
					value.icon && $button.button({icons: {primary: 'ui-icon-' + value.icon}});
					$bar.append($button);
				}
			});
			//保存初始值
			$bar.data('currentPage', parseInt(options.currentPage));
			$bar.data('pageCount', parseInt(options.pageCount));
			$bar.data('totalCount', parseInt(options.totalCount));
			return this.after($bar);
		}
		//更新页码信息
		function pageInfo(currentPage, pageCount, totalCount) {
			$bar.data('currentPage', parseInt(currentPage));
			$bar.data('pageCount', parseInt(pageCount));
			$bar.data('totalCount', parseInt(totalCount));
			$('#' + barId + '>span').text((totalCount == 0 ? '' : currentPage + ' / ') + pageCount + ' 页 共 ' + totalCount + ' 条');
			//$('#' + barId + '>input').val(currentPage);
			//设置页码栏按钮可用性
			$('.first,.prev', $bar).button($bar.data('currentPage') <= 1 ? 'disable' : 'enable');
			$('.next,.last', $bar).button($bar.data('currentPage') >= $bar.data('pageCount') ? 'disable' : 'enable');
		}
		//翻页方法
		function gotoPage(pageNo) {
			if (isNaN(pageNo) || pageNo < 1 || pageNo > $bar.data('pageCount')) {
				alert('页码 ' + pageNo + ' 输入错误或超出有效范围！');
				$('#' + barId + '>input').focus();
			}
			else {
				//执行翻页动作
				var r = options.gotoPage(pageNo);
				//ydl.log('翻页方法返回值：\n' + ydl.getMember(r));
				if (r && r.currentPage) {
					$bar.data('currentPage', parseInt(r.currentPage));
					$bar.data('pageCount', parseInt(r.pageCount));
					$bar.data('totalCount', parseInt(r.totalCount));
					//设置页码
					pageInfo(r.currentPage, r.pageCount, r.totalCount);
				}
			}
		}
	};


	/**
	 * 计时器
	 * ydl.timer.start() 开始计时，返回计时器ID
	 * ydl.timer.output(timerId) 输出指定的计时器从开始至今经过的毫秒数
	 * ydl.timer.stop(timerId) 停止计时，输出指定的计时器从开始到停止经过的毫秒数
	 * @example var t = ydl.timer.start(); doSomething(); ydl.log(ydl.timer.output(t)); doSomething(); ydl.log(ydl.timer.stop(t));
	 */
	ydl.timer = {
		duration: {},
		start: function() {
			var timerId = ydl.uuid();
			this.duration[timerId] = new Date();
			return timerId;
		},
		output: function(timerId) {
			if (this.duration[timerId]) return new Date() - this.duration[timerId];
		},
		stop: function(timerId) {
			if (this.duration[timerId]) {
				var duration = new Date() - this.duration[timerId];
				delete this.duration[timerId];
				return duration;
			}
		}
	};

	/**
	 * 获取错误信息
	 * @param {Object} exception javascript错误对象
	 * @param {String} [member='message'] 错误对象属性名
	 * @returns {String} 如果提供了属性名，返回对应的属性值；如果属性名为all则返回全部属性（字符串，每行一个属性name=value）
	 */
	ydl.error = function(exception, member) {
		if (typeof exception === 'string') return exception;
		else {
			member = member || 'message';
			switch (member.toLowerCase()) {
				case 'message': return (exception.message || exception.description);
				case 'all': return ydl.getMember(exception);
				default: return exception[member];
			}
		}
	};

	/**
	 * 输出JS调试日志
	 * 此方法输出的信息只应用于开发人员调试程序，要显示给用户的信息请使用alert()或者ydl.message()
	 * @example ydl.log('hello world!');			//使用默认方式输出信息
	 * @example ydl.log('hello world!', 'red');		//使用红色字体输出信息
	 * @example ydl.log('hello world!', 1);			//指定从脚本控制台输出信息
	 * @example ydl.log('hello world!', 'blue', 3);	//同时指定字体颜色和输出目标
	 * @example var s2 = ydl.log(a) + b;			//输出a的值；ydl.log的返回值就是第一个参数的值，所以可以在表达式内部使用而不影响表达式的计算
	 * @param {String} message 要输出的日志信息
	 * @param {String} [color='#000'] 输出的文字颜色，HTML颜色格式，仅在target=2时使用
	 * @param {Number} [target=config_debug_target] 输出的目标
	 * @returns {String|Object} 返回输入的message参数本身
	 */
	ydl.log = function(message, color, target) {
		if (__isDebugMode || __forceConsoleOut) {
			var msg = '' + message;
			//支持ydl.log(message, target)方式调用
			if ($.type(color) === 'number') {
				target = color;
				color = '#000';
			}
			else if (color === undefined) color = '#000';
			//强制从脚本控制台输出
			if (__forceConsoleOut) target = DEBUG_TARGET_CONSOLE;
			//没有指定目标时从本地存储中取默认值
			else if (target === undefined) target = ydl.localData('localconfig_debugTarget');

			switch (parseInt(target)) {
				case DEBUG_TARGET_NONE:
					//不输出信息
					break;
				case DEBUG_TARGET_CONSOLE:
					//通过浏览器脚本控制台输出（IE8+、Opera、Firefox、Chrome均正常，IE6、IE7不支持）
					window.console && console.log('[' + ydl.formatDate(new Date(), 'HH:mm:ss') + '] ' + msg);
					break;
				case DEBUG_TARGET_WINDOW:
					//打开一个新浏览器窗口输出（所有主流浏览器均支持，输出时不影响脚本继续运行）
					if (!ydl.debugConsole || ydl.debugConsole.closed) ydl.debugConsole = ydl.openWindow('', 'debug' + location.host.replace(/[\.:]/g, ''), {
						width: parseInt(screen.availWidth / 3),
						height: screen.availHeight - 50,
						left: parseInt(screen.availWidth / 3 * 2 - 30),
						top: 1
					});
					//向窗口输出调试信息
					try {
						var consoleDoc = ydl.debugConsole.document;
						msg = ydl.convertTags(msg, 'html');
						//第一个等号或冒号前面的部分加粗显示
						var match = msg.match(/[=＝:：]/);
						var eqPos = match == null ? 0 : match.index;
						if (eqPos > 0) msg = '<b>' + msg.substr(0, eqPos) + '</b>' + msg.substr(eqPos);
						msg = '<p style="margin: 0 0 5px; font: 12px/1.2em 宋体; color: #999">[' + ydl.formatDate(new Date(), 'HH:mm:ss') +
							'] <span style="color: ' + color + '"> ' + msg + '</span></p>';
						consoleDoc.write(msg);
						if (consoleDoc.title == '') consoleDoc.title = '调试信息：' + location.host;
						ydl.debugConsole.scrollTo(0, consoleDoc.body.scrollHeight);
						//ydl.debugConsole.focus();	//设置焦点到调试信息窗口（会影响含focus、blur事件的代码调试）
					}
					catch (ex) {
						ydl.log('Log：' + ydl.error(ex), DEBUG_TARGET_CONSOLE);
					}
					break;
				case DEBUG_TARGET_ALERT:
					//用alert对话框输出（所有主流浏览器均支持，输出时会中断脚本执行）
					alert('[' + ydl.formatDate(new Date()) + '] 调试信息：\n\n' + msg);
					break;
			}
		}
		return message;
	};

	/**
	 * 将下拉列表转为组合框
	 * @example $(select).combobox({
	 * 		{Boolean} [button] 是否显示下拉按钮
	 * 		{Boolean} [fuzzy] 是否模糊查询（启用后在整个选项文本中查找关键字，否则只在选项文本开头查找关键字）
	 * 		{Boolean} [mark] 是否在下拉选项中标记匹配的关键字（启用后可能会降低性能）
	 *		{Boolean} [dirty] 是否允许输入脏数据（列表中不存在的数据）
	 * });
	 */
	if ($.widget) $.widget('ui.combobox', {
		_create: function() {
			//默认选项
			var settings = {
				button: true,
				fuzzy: true,
				mark: false,
				dirty: false
			};
			this.options && $.extend(settings, this.options);
			var	self = this;
			//隐藏原始的select
			var $select = this.element.hide();
			//是否禁用
			var isDisabled = $select.attr('disabled');
			//创建输入文本框
			var $input = this.input = $('<input type="text" />')
				.css('width', $select.width() + 20)
				.insertAfter($select)
				//显示默认选中的值
				.val($select.children(':selected').text())
				.autocomplete({
					delay: 0,
					minLength: 0,
					source: function(request, response) {
						//在列表选项文本中匹配输入的关键字
						var regTerm = $.ui.autocomplete.escapeRegex(request.term);
						var matcher = new RegExp((settings.fuzzy ? '' : '^') + regTerm, 'i');
						if (settings.mark) {
							var matcherMark = new RegExp((settings.fuzzy ? '' : '^') + '(?![^&;]+;)(?!<[^<>]*)(' + regTerm + ')(?![^<>]*>)(?![^&;]+;)', 'gi');
							response($select.children('option').map(function() {
								var text = $(this).text();
								if (this.value && (!request.term || matcher.test(text))) return {
									label: text.replace(matcherMark, '<strong>$1</strong>'),	//标注关键字
									value: text,
									option: this
								};
							}));
						}
						else response($select.children('option').map(function() {
							var text = $(this).text();
							if (this.value && (!request.term || matcher.test(text))) return {
								label: text,
								value: text,
								option: this
							};
						}));
					},
					//选择时同步select的选择项
					select: function(event, ui) {
						ui.item.option.selected = true;
						$select.change();
						self._trigger('selected', event, {
							item: ui.item.option
						});
					},
					change: function(event, ui) {
						if (!ui.item) {
							var matcher = new RegExp('^' + $.ui.autocomplete.escapeRegex($input.val()) + '$', 'i');
							var valid = false;
							$select.children('option').each(function() {
								var text = $(this).text();
								//检查输入的值是否在列表中存在
								if (text.match(matcher) || this.value.match(matcher)) {
									this.selected = valid = true;
									$input.val(text);
									return false;
								}
							});
							//如果输入的值在列表中不存在
							if (!valid) {
								if (settings.dirty) {
									//将输入值作为新的选项添加进列表
									var term = $input.val();
									$select.append(
										$('<option />').attr('value', term).text(term)
									).val(term);
								}
								else {
									//将选项设为空值（如果选项列表中没有空值，将不改变当前选择）
									$select.val('');
									$input.val($select.children(':selected').text());
									$input.data('autocomplete').term = '';
								}
								return false;
							}
						}
					}
				});
			//禁用文本框
			isDisabled && $input.attr('disabled', 'disabled');
			//设置列表显示样式
			$input.data('autocomplete')._renderItem = function(ul, item) {
				return $('<li></li>')
					.data('item.autocomplete', item)
					.append('<a>' + item.label + '</a>')
					.appendTo(ul);
			};
			//空选项文本在获得焦点时清除，失去焦点时显示
			$input.focus(function() {
				if ($(this).val() === $('option[value=""]', $select).text()) {
					$(this).val('');
				}
			})
				.blur(function() {
					if ($(this).val() === '') $(this).val($('option[value=""]', $select).text());
				});
			//添加下拉按钮
			if (settings.button) {
				this.button = $('<button type="button">&nbsp;</button>')
					.attr('tabIndex', -1)
					.attr('title', '显示全部选项')
					.insertAfter($input)
					.button({
						icons: { primary: 'ui-icon-triangle-1-s' },
						text: false
					})
					.removeClass('ui-corner-all')
					.addClass('ui-corner-right ui-button-icon combobox-button')
					.click(function() {
						//如果下拉列表可见，单击时将其关闭
						if ($input.autocomplete('widget').is(':visible')) {
							$input.autocomplete('close');
							return;
						}
						$(this).blur();
						//空字符串显示全部选项
						$input.autocomplete('search', '');
						$input.focus();
					});
				//禁用下拉按钮
				isDisabled && this.button.button('disable');
			}
		},
		destroy: function() {
			this.input.remove();
			this.button.remove();
			this.element.show();
			$.Widget.prototype.destroy.call(this);
		}
	});

	/**
	 * 初始化金额输入框
	 * 获得焦点时去除千分符，失去焦点时添加千分符
	 */
	$.fn.moneyinput = function() {
		this.each(function() {
			var input = this;
			var $input = $(this);
			if (input && input.type && input.type === 'text') {
				$input.addClass('money');
				if (!$input.attr('maxlength')) $input.attr('maxlength', 15);
				$input.attr('size', parseInt($input.attr('maxlength')) + 6);
				function addComma() {
					if (input.value !== '' && input.value.indexOf(',') < 0) {
						var declen = $input.attr('data-declen');
						input.value = ydl.addComma(parseFloat(input.value).toFixed(declen === undefined || declen === '' ? 2 : parseInt(declen)));
						if (input.value == 'NaN') input.value = '';
					}
				}
				$input/*.keypress(function(event) {	//在ydl.ydpx.js中做了统一允许输入字符控制，此处不需要了
				return (event.which == ydl.common.keys.Backspace || event.which == ydl.common.keys.Enter ||
					event.which == ydl.common.keys.Tab || event.which == 0 || /[-\d\.]/.test(String.fromCharCode(event.which)));
			})*/.focus(function() {
					input.value = ydl.delComma(input.value);
					input.select();
				}).blur(addComma);
				addComma();
			}
			else ydl.log('$.fn.moneyinput：要转换为金额输入框的对象不是文本框', 'red');
		});
	};


	/**
	 * 生成信息提示条，替换当前对象
	 * @param {String} [type='default'] 信息类型：default、highlight、error
	 * @param {String} [icon=''] 图标名：alert、info等（参考jquery ui的.ui-icon样式）
	 */
	$.fn.infoBar = function(type, icon) {
		if (!icon) {
			switch (type) {
				case 'highlight': icon = 'alert'; break;
				case 'error': icon = 'circle-close'; break;
				default: icon = 'info';
			}
		}
		this.each(function() {
			var $ele = $(this);
			$ele.replaceWith('<div id="' + $ele.attr('id') + '" class="ui-state-' + (type || 'default') + ' ui-corner-all" style="padding: 5px;">' +
				(icon ? '<span class="ui-icon ui-icon-' + icon + ' info-icon"></span>' : '') +
				'<p style="font-weight: normal;">' + $ele.html() + '</p></div>');
		});
	};

	/**
	 * 给IE6元素添加bgiframe，使其不能被其他元素遮挡
	 * @example $('element').bgiframe();
	 * @param {Object} s 初始化参数
	 */
	$.fn.bgiframe = ydl.common.isIe6 ? function(s) {
		s = $.extend({
			top     : 'auto',	//.currentStyle.borderTopWidth
			left    : 'auto',	//.currentStyle.borderLeftWidth
			width   : 'auto',	//offsetWidth
			height  : 'auto',	//offsetHeight
			opacity : true,
			src     : 'javascript:false;'
		}, s);
		function prop(n) {
			return n && n.constructor === Number ? n + 'px' : n;
		}
		var html = '<iframe class="bgiframe" frameborder="0" tabindex="-1" src="' + s.src + '"' +
			'style="display:block; position:absolute; z-index:-1;' + (s.opacity !== false ? 'filter:Alpha(Opacity=\'0\');' : '') +
			'top:' + (s.top == 'auto' ? 'expression(((parseInt(this.parentNode.currentStyle.borderTopWidth) || 0) * -1) + \'px\')' : prop(s.top)) + ';' +
			'left:' + (s.left == 'auto' ? 'expression(((parseInt(this.parentNode.currentStyle.borderLeftWidth) || 0) * -1) + \'px\')': prop(s.left)) + ';'+
			'width:' + (s.width == 'auto' ? 'expression(this.parentNode.offsetWidth + \'px\')' : prop(s.width)) + ';' +
			'height:' + (s.height == 'auto' ? 'expression(this.parentNode.offsetHeight + \'px\')': prop(s.height)) + ';' +
			'"/>';
		return this.each(function() {
			$(this).children('iframe.bgiframe').length === 0 && this.insertBefore(document.createElement(html), this.firstChild);
		});
	} : function() { return this; };


	/**
	 * 向系统消息框中添加一条消息
	 * @param {String} msg 消息内容文本，可以包含HTML代码
	 * @param {String} [sender='系统消息'] 消息来源
	 * @param {String} [style] 显示样式（'highlight'或'error'，可空）
	 * @returns undefined
	 */
	ydl.message = function(msg, sender, style) {
		top.messageBox && top.messageBox.add && top.messageBox.add(msg, sender, style);
	};


	/**
	 * 显示正在运行动画，并禁止点击页面
	 * @param {Boolean} [isRunning=true] 是否正在运行
	 * @returns undefined
	 */
	ydl.displayRunning = function(isRunning) {
		if (isRunning === undefined) isRunning = true;
		var overlay = top.document.getElementById('page-running-overlay');
		if (isRunning) $(overlay).show();
		else $(overlay).hide();
	};

	/**
	 * 在组件后面添加按钮
	 * 用于表单元素
	 * @param {String} buttonText 按钮文本
	 * @param {Function} clickFunc(field) 点击按钮时执行的函数，参数为组件DOM对象
	 * @param {String} [buttonId] 按钮的ID，缺省没有ID
	 */
	$.fn.addButton = function(buttonText, clickFunc, buttonId) {
		var id = this.length > 0 || !buttonId ? '' : ' id="' + buttonId + '"';
		this.each(function() {
			var $this = $(this);
			$this.after($('<button type="button"' + id + '>' + buttonText + '</button>').button().click(function() {
				clickFunc.call($this[0], $this[0]);
			}));
		});
		return this;
	};

	/**
	 * 选项设置对话框，用于设置每个字符具有不同意义的选项值
	 * @param {Array} defData 定义选项值字符串每个字符的不同意义的数组
	 * [
	 * 		{Object|String} 一个字符所代表的意义 {
	 * 			{String} label 文字描述
	 * 			{String} [defaultValue] 默认值
	 * 			{Object} [values] {'数据值':'显示值'} 如果提供了此参数将使用下拉列表，否则使用复选框
	 * 			{Function} [onchange] 设置改变时执行的函数
	 * 		}
	 * ]
	 * @param {String|Object} [input] 选项字符串初始值，或者选项文本框DOM对象或jQuery对象
	 * @param {Function} callback(data) 点击确定按钮后的回调函数，传入选项值作为参数。如果input为String，此函数不可省略。
	 * @param {String} [title] 对话框标题
	 * @returns undefined
	 * @author 姬爽 2012-7
	 */
	ydl.optionDialog = function(defData, input, callback, title) {
		//从组件获取的初始值数组
		if (typeof input == 'object') input = ydl.getDom(input);
		var inputArray = (typeof input == 'object' ? input.value : input).split('');
		//解析数据
		var $table = $('<table class="container fit"></table>');
		$.map(defData, function(dataObj, index) {
			inputArray[index] = inputArray[index] || dataObj.defaultValue ;
			var $tr = $('<tr></tr>').append('<td width="40%"></td><td width="60%"></td>');
			var $component;//需要创建的组件
			//如果是checkbox类型
			if (!dataObj.values || typeof defData[index] === 'string') {
				$('td:eq(0)', $tr).append('<label for="dialog_checkbox' + index + '">' + (dataObj.label || defData[index]) + '：</label>');
				$component = $('<input type="checkbox" id="dialog_checkbox' + index + '" />');
				if (inputArray[index] === '1') $component.attr('checked', 'checked');
			}
			//如果是select类型
			else {
				$('td:eq(0)', $tr).append('<label for="dialog_select' + index + '">' + dataObj.label + '：</label>');
				$component = $('<select id="dialog_select' + index + '"></select>');
				var options = ydl.common.blankOption;//空选项
				$.each(dataObj.values, function(key, value) {
					options += '<option value="' + key + '" ' +
						(inputArray[index] == key ? 'selected="selected"' : '') + '>' + key + ' - ' + value + '</option>';
				});
				$component.append(options);
			}
			//如果存在onchange绑定事件时
			if (dataObj.onchange) $component.change(dataObj.onchange);
			//将组件加到该行第二列上
			$('td:eq(1)', $tr).append($component);
			$table.append($tr);
		});
		//打开对话框
		var $dialog = $('<div></div>').append($table).dialog({
			title: (typeof input == 'string' ? title : title || ydl.getLabel(input)) || '选项对话框',
			modal: true,
			width: 500,
			buttons: {
				'确定': function() {
					//向input组件填充选择数据
					var toInputData = '';
					if (!ydl.validator($('select', $dialog), {required: true})) return;
					$('input,select', $dialog).each(function() {
						if ($(this).is('input')) toInputData += this.checked ? '1' : '0';
						else toInputData += this.value;
					});
					//如果校验成功，调用用户定义函数，关闭对话框
					if (input.value !== undefined) input.value = toInputData;
					if (callback && callback(toInputData) === false) return false;
					$(this).dialog('close');
				},
				'取消': function() {
					$(this).dialog('close');
				}
			},
			close: function() {
				$(this).dialog('destroy').remove();
			}
		});
	};

	/**
	 * 使用对话框向用户显示需要确认的数据
	 * @param {String|Array} inputIds 页面输入组件ID，多个ID之间用半角逗号分隔，或是字符串数组
	 * @param {String} [message] 确认提示信息
	 * @returns {Boolean} 用户确认的结果
	 */
	ydl.confirmData = function(inputIds, message) {
		var inputList = [];
		$.each($.isArray(inputIds) ? inputIds : inputIds.split(','), function(index, id) {
			inputList.push({
				//label: ydl.getLabel(id),
				label: ydl.getLabel($('#'+id)),
				value: ydl.getValue(id, true)
			});
		});
		//lm(inputList, 'blue');
		if (inputList.length > 0) {
			var dialogResult = false;
			ydl.dialog.open(ydl.contexPath + '/platform/commonDialog.jsp', {
				title: message || '请确认以下信息',
				init: function($dialog) {
					with (this) {
						$dialog.append('<table class="container fit"><col width="30%" /><col width="70%" /><tr class="ui-widget-header"><th>项目</th><th>内容</th></tr>' + $.map(inputList, function(item) {
							return '<tr><td class="has-label"><label>' + item.label + '：</label></td><td>' + item.value + '</td></tr>';
						}).join('') + '</table>');
					}
				},
				buttons: {
					'确定': function() {
						this.ydl.dialog.close(true);
					},
					'取消': function() {
						this.ydl.dialog.close(false);
					}
				}
			}, function(data) {
				dialogResult = data;
			}, {
				layermode: false
			});
			return dialogResult;
		}
		else return true;
	};

	/**
	 * 提示信息框
	 * @param {String} message
	 * @returns undefined
	 */
	ydl.alert = function(message) {
		$('<div>' + message + '</div>').appendTo('body').dialog({
			title: '提示信息',
			width: 400,
			height: 300,
			autoOpen: true,
			buttons: {
				'确定': function() {
					$(this).dialog('close');
				}
			}
		});
	};

	/**
	 * 检查表单输入组件上是否存在校验错误信息
	 * @param {String|Object} field 表单输入组件的ID或DOM对象或jQuery对象（只支持单个对象）
	 * @returns {Boolean} 是否存在校验错误信息
	 */
	ydl.hasErrorMessage = function(field) {
		var errMsg = $(ydl.getDom(field)).data('errorMessages');
		return !$.isEmptyObject(errMsg);
	};

	/**
	 * 清除表单输入组件上的校验错误信息
	 * @param {String|Object} field 表单输入组件的ID或DOM对象或jQuery对象（支持多个对象）
	 * @returns undefined
	 */
	ydl.delErrorMessage = function(field) {
		var $field = $(ydl.getDom(field));
		$field.removeData('errorMessages').removeClass('error');
		$('#validator_tip').hide();
	};


	/**
	 * 页面加载后的默认处理
	 */
	$(function() {

		//ydl.validator错误提示信息处理
		$('body').on('focus', 'input,select,textarea', function(e) {
			var $field = $(this);
			var errorMessages = $field.data('errorMessages');
			if (errorMessages !== undefined && !$.isEmptyObject(errorMessages)) {
				var errorMessageString = $.map(errorMessages, function(value,key) {
					return value.message;
				}).join('<br />');
				//提示信息div
				if (!document.getElementById('validator_tip')) $('body').append('<div id="validator_tip" class="ui-state-error" style="z-index:9999;display:none;"><span></span><div></div></div>');
				var position = $field.offset();
				var $tip = $('#validator_tip').children('span').html(errorMessageString).end();
				var width = parseInt($tip.css('width'));
				var left = position.left;
				if(position.left + width > document.documentElement.clientWidth){
					left = document.documentElement.clientWidth - width;
					$('div',$tip).css('left',20+position.left-left);
				}
				else $('div',$tip).css('left','20px');
				$tip.css('left', left + 'px').css('top', position.top - $tip.outerHeight(true) - 4 + 'px').show();
			}
		}).on('blur', 'input,select,textarea', function(e) {
			$('#validator_tip').hide();
		});

		//-----设置jQuery默认选项-----
		//IE6下关闭全部动画效果以提高速度
		if (ydl.common.isIe6) $.fx.off = true;

		//日期控件默认选项
		if ($.datepicker) {
			$.datepicker.regional['zh-CN'] = {
				closeText: '关闭',
				prevText: '&#x3c;上月',
				nextText: '下月&#x3e;',
				currentText: '本月',
				monthNames: ['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'],
				monthNamesShort: ['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'],
				dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],
				dayNamesShort: ['周日','周一','周二','周三','周四','周五','周六'],
				dayNamesMin: ['日','一','二','三','四','五','六'],
				weekHeader: '周',
				dateFormat: 'yy-mm-dd',
				firstDay: 1,
				isRTL: false,
				showMonthAfterYear: true,
				yearSuffix: '年',
				changeMonth: true,
				changeYear: true,
				yearRange:'c-100:c+100',
				showOn: 'button',
				buttonImageOnly: true,
				buttonImage: ydl.contexPath + '/ui/themes_yd/' +
					(ydl.themes && ydl.themes.lib[ydl.localData('operconfig_theme')] ?
						ydl.themes.lib[ydl.localData('operconfig_theme')].yd : 'official') + '/icon_calendar.png',
				beforeShow: function(input, inst) {
					//只读文本框不弹出日历选择
					if (input.readOnly || input.disabled) inst.dpDiv.addClass('hide');
					else inst.dpDiv.removeClass('hide');
				},
				onSelect: function(dateText, inst) {
					$(inst.input).change();
					inst.input.focus();
				}
			};
			$.datepicker.setDefaults($.datepicker.regional['zh-CN']);
		}

		//日期控件焦点离开时自动调整格式
		$('body').on('blur','input.hasDatepicker', function() {
			var s = this.value;
			if (/^\d{8}$/.test(s)) {
				this.value = s.substr(0, 4) + '-' + s.substr(4, 2) + '-' + s.substr(6, 2);
				$(this).change();
			}
		});

		//ajax默认选项
		$.ajaxSetup({
			cache: false,
			contentType:"application/x-www-form-urlencoded; charset=utf-8"
		});

		//全局快捷键
		var strictMode = ydl.localData('pageconfig_strictMode') == '1';
		$(window).keydown(function(event) {
			//ctrl+M跳转到菜单码输入（显示菜单树）
			if (event.ctrlKey && event.which == ydl.common.keys.M) {
				try {
					top.menu.open();
					top.document.getElementById('menu-code').focus();
					top.document.getElementById('menu-code').select();
					return false;
				}
				catch(ex) {}
			}
			else if (event.altKey) {
				//alt+1~9执行快捷菜单
				if (event.which >= ydl.common.keys.Num1 && event.which <= ydl.common.keys.Num9) {
					if (top.$('#menu-usedcustom').is(':visible')) {
						top.$('#customFunc a:eq(' + (event.which - ydl.common.keys.Num1) + ')').click();
					}
				}
				//alt+M隐藏菜单树
				else if (event.which == ydl.common.keys.M) {
					try {
						if (top.$('#left-panel').is(':visible')) top.menu.close();
						else top.menu.open();
					}
					catch(ex) {}
				}
			}
			//禁用F5键（刷新）
			else if (strictMode && event.which == ydl.common.keys.F5) return false;
			//禁用Backspace后退
			else if (event.which == ydl.common.keys.Backspace &&
				!$(event.target).is('input:not([readonly]):not([disabled]):not([type=checkbox],[type=radio]),textarea:not([readonly]):not([disabled])')) return false;
			else if (event.which == ydl.common.keys.Esc) {
				event.preventDefault();
			}
		})
		//禁用右键菜单
		if (strictMode) window.oncontextmenu = function(event) {
			return false;
		};
	});


})(ydl, jQuery);


/**
 * 常用方法的别名
 */
var $$ = ydl.getElementById;
var $n = ydl.getElementsByName;
var $t = ydl.getElementsByTagName;
var lo = ydl.log;
var lm = function(obj, color) {
	ydl.log(ydl.getMember(obj), color);
	return obj;
};
var ll = function(label, obj, color) {
	ydl.log(label + '：' + ydl.getMember(obj), color);
	return obj;
};
