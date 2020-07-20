/*
 * Web Front Lib
 * web前端基础脚本库
 * Copyright 华信永道. All rights reserved.
 * @author: 万隆 wanlong@yondervision.com.cn
 * @version: 1.1.0
 */

/**
 * 声明wfl包
 */

var wfl = wfl || {exist: true,version: "1.0"};

/****************************************************************
 * 对语言层面的封装，包括类型判断、模块扩展、继承基类以及对象自定义事件的支持。
 * @namespace wfl.lang
 */
wfl.lang = wfl.lang || {};

/**
 * 判断目标参数是否为function或Function实例
 * @name wfl.lang.isFunction
 * @grammar wfl.lang.isFunction(source)
 * @param {Any} source 目标参数
 * @说明: chrome下,'function' == typeof /a/ 为true

 * @return {boolean} 类型判断结果
 */
wfl.lang.isFunction = wfl.isFunction = function(source){
	return Object.prototype.toString.call(source) == '[object Function]';	
	//	return (source instanceof Object) && (source instanceof Function)
};

/**
 * 判断目标参数是否string类型或String对象
 * @name wfl.lang.isString
 * @function
 * @grammar wfl.lang.isString(source)
 * @param {Any} source 目标参数

 * @return {Boolean} 返回判断结果
 */
wfl.lang.isString = wfl.isString = function(source){
	return Object.prototype.toString.call(source) == '[object String]';
};

/**
 * 判断目标参数是否number类型或Number对象
 * @name wfl.lang.isNumber
 * @function
 * @grammar wfl.lang.isNumber(source)
 * @param {Any} source 目标参数

 * @return {Boolean} 返回判断结果
 * @remark 用本函数判断NaN会返回false，尽管在Javascript中是Number类型。
 */
wfl.lang.isNumber = wfl.isNumber = function(source){
	return Object.prototype.toString.call(source) == '[object Number]' && isFinite(source);
};

/**
 * 判断目标参数是否为日期对象
 * @name wfl.lang.isDate
 * @function
 * @grammar wfl.lang.isDate(source)
 * @param {Any} source 目标参数

 * @return {Boolean} 返回判断结果
 */
wfl.lang.isDate = wfl.isDate = function(source){
	// return o instanceof Date;
	return {}.toString.call(source) === '[object Date]' && source.toString !== 'Invalid Date' && !isNaN(source); 
};

/**
 * 判断目标参数是否为Array对象
 * @name wfl.lang.isArray
 * @function
 * @grammar wfl.lang.isArray(source)
 * @param {Any} source 目标参数

 * @return {Boolean} 返回判断结果
 */
wfl.lang.isArray = wfl.isArray = function(source){
	return Object.prototype.toString.call(source) == '[object Array]';
	//	return source instanceof Object && source instanceof Array
};

/**
 * 判断目标参数是否为Boolean对象
 * @name wfl.lang.isBoolean
 * @function
 * @grammar wfl.lang.isBoolean(source)
 * @param {Any} source 目标参数

 * @return {Boolean} 返回判断结果
 */
wfl.lang.isBoolean = wfl.isBoolean = function(source){
	return typeof source === 'boolean';	
};

/**
 * 判断目标参数是否为Object对象
 * @name wfl.lang.isObject
 * @function
 * @grammar wfl.lang.isObject(source)
 * @param {Any} source 目标参数

 * @return {Boolean} 返回判断结果
 */
wfl.lang.isObject = wfl.isObject = function(source){
	return !!(source && 'object' == typeof source);
	//return 'function' == typeof source || !!(source && 'object' == typeof source);
	//return Object.prototype.toString().call(source) == '[object Object]';
};

/****************************************************************
 * 判断浏览器类型和特性的属性
 * @namespace wfl.browser
 */
wfl.browser = wfl.browser || {};

/**
 * 判断是否为chrome浏览器
 * @grammar wfl.browser.chrome
 * @property chrome chrome版本号
 * @return {Number} chrome版本号
 */
wfl.browser.chrome = /chrome\/(\d+\.\d+)/i.test(navigator.userAgent) ? + RegExp['\x241'] : undefined;

/**
 * 判断是否为firefox浏览器
 * @grammar wfl.browser.firefox
 * @property firefox firefox版本号
 * @return {Number} firefox版本号
 */
wfl.browser.firefox = /firefox\/(\d+\.\d+)/i.test(navigator.userAgent) ? + RegExp['\x241'] : undefined;

/**
 * 判断是否为IE浏览器
 * @grammar wfl.browser.ie
 * @property ie ie版本号
 * @return {Number} ie版本号,(如IE8版本浏览器将返回数字8)
 */
wfl.browser.ie = wfl.ie = /msie (\d+\.\d+)/i.test(navigator.userAgent) ? (document.documentMode || + RegExp['\x241']) : undefined;

/**
 * 判断是否为opera浏览器
 * @grammar wfl.browser.opera
 * @return {Number} opera版本号
 */
wfl.browser.opera = /opera(\/| )(\d+(\.\d+)?)(.+?(version\/(\d+(\.\d+)?)))?/i.test(navigator.userAgent) ?  + ( RegExp["\x246"] || RegExp["\x242"] ) : undefined;

/**
 * 判断是否为safari浏览器, 支持ipad, 其中,一段典型的ipad UA 如下:
 * Mozilla/5.0(iPad; U; CPU iPhone OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B314 Safari/531.21.10
 * @grammar wfl.browser.safari
 * @return {Number} safari版本号
 */
(function(){
    var ua = navigator.userAgent;
	wfl.browser.safari = /(\d+\.\d)?(?:\.\d)?\s+safari\/?(\d+\.\d+)?/i.test(ua) && !/chrome/i.test(ua) ? + (RegExp['\x241'] || RegExp['\x242']) : undefined;
})();

/**
 * 判断是否为gecko内核
 * @property isGecko 
 * @grammar wfl.browser.isGecko
 * @returns {Boolean} 布尔值
 */
wfl.browser.isGecko = /gecko/i.test(navigator.userAgent) && !/like gecko/i.test(navigator.userAgent);

/**
 * 判断是否为webkit内核
 * @property isWebkit 
 * @grammar wfl.browser.isWebkit
 * @returns {Boolean} 布尔值
 */
wfl.browser.isWebkit = /webkit/i.test(navigator.userAgent);

/****************************************************************
 * 操作平台的方法
 * @namespace wfl.platform
 * @navigator.userAgent
 */
wfl.platform = wfl.platform || {};

/**
 * 判断是否为android平台
 */
wfl.platform.isAndroid = /android/i.test(navigator.userAgent);

/**
 * 判断是否为ipad平台
 */
wfl.platform.isIpad = /ipad/i.test(navigator.userAgent);

/**
 * 判断是否为iphone平台
 */
wfl.platform.isIphone = /iphone/i.test(navigator.userAgent);

/**
 * 判断是否为windows平台
 */
wfl.platform.isWindows = /windows/i.test(navigator.userAgent);

/**
 * 判断是否为macintosh平台
 */
wfl.platform.isMacintosh = /macintosh/i.test(navigator.userAgent);


/****************************************************************
 * 操作数组的方法
 * @namespace wfl.array
 */

wfl.array = wfl.array || {};

/**
 * 查询数组中指定元素的索引位置
 * @name wfl.array.indexOf
 * @function
 * @grammar wfl.array.indexOf(source, match[, fromindex])
 * @param {Array} source 需要查询的数组
 * @param {Any} match 查询项
 * @param {number} [fromindex] 查询的起始位索引位置

 * @return {number} 返回第一个匹配的指定元素的索引位置, 找不到返回-1
 */
wfl.array.indexOf = function(source, match, fromindex){
	var i = Math.abs(fromindex | 0),
		len = source.length,
		iterator = match;
	for( ; i<len; i++){
		if(i in source && source[i] === iterator){
			return i;
		}	
	}
	return -1;
};

/**
 * 判断一个数组中是否包含给定元素
 * @name wfl.array.contains
 * @function
 * @grammar wfl.array.contains(source, obj)
 * @param {Array} source 需要查询的数组
 * @param {Any} obj 查询项

 * @return {boolean} true || false 判断结果
 */
wfl.array.contains = function(source, obj){
	return (wfl.array.indexOf(source, obj) >= 0);
};

/**
 * 遍历数组中所有元素
 * @name wfl.array.each
 * @function
 * @grammar wfl.each(source, iterator[, thisObject])
 * @param {Array} source 需要遍历的数组
 * @param {Function} iterator 对每个数组元素进行调用的函数，该函数有两个参数，第一个为数组索引值，第二个为数组元素，function (index, item)
 * @param {Object} [thisObject] 函数调用时的this指针，如果没有此参数，默认是当前遍历的数组

 * @return {Array} 返回遍历的数组
 * @说明: 函数interator返回false以停止遍历
 */
 wfl.array.each = wfl.array.forEach = function(source, iterator, thisObject){
	var i, len = source.length, item, returnValue;
	if(typeof iterator == 'function'){
		for(i = 0 ; i<len; i++){
			item = source[i];		
			returnValue = iterator.call(thisObject || source, i, item);
			if(returnValue === false) {
				break;
			}
		}
	}
	return source;
 };

/**
 * 清空一个数组
 * @name wfl.array.empty
 * @functon
 * @grammar wfl.array.empty(source)
 * @param {Array} source 需要清空的数组
 */
wfl.array.empty = function(source){
	source.length = 0;
};

/**
 * 从数组中筛选符合条件的元素
 * @name wfl.array.fliter
 * @function
 * @grammar wfl.array.fliter(source, interator[, thisObject])
 * @param {Array} source 需要筛选的数组
 * @param {Function} interator 对每个数组元素进行筛选的函数，该函数有两个参数，第一个为数组索引值，第二个为数组元素，function (index, item)，函数需要返回true或false
 * @param {Object} [thisObject] 函数调用时的this指针，如果没有此参数，默认是当前遍历的数组

 * @return {Array} 筛选出的数组, 不改变原数组
 */
wfl.array.fliter = function(source, interator, thisObject){
	var i, len = source.length, item,
		result = [],
		resultIndex = 0;
	if(typeof interator == 'function'){
		for(i = 0; i < len; i++){
			item = source[i];
			if(interator.call(thisObject || source, i, item) === true){
				result[resultIndex++] = item;
			}
		}
	}
	return result;
};

/**
 * 从数组中寻找符合条件的第一个元素
 * @name wfl.array.find
 * @function
 * @grammar wfl.array.find(source, interator[, thisObject])
 * @param {Array} source 需要查询的数组
 * @param {Function} interator 对每个数组元素进行筛选的函数，该函数有两个参数，第一个为数组索引值，第二个为数组元素，function (index, item)，函数需要返回true或false
 * @param {Object} [thisObject] 函数调用时的this指针，如果没有此参数，默认是当前遍历的数组

 * @return {Any || null} 返回查询到的第一个匹配的元素, 如果没有找到返回null
 */
wfl.array.find = function(source, interator, thisObject){
	 var i, len = source.length, item;
	 if(typeof interator == 'function'){
		for(i = 0; i < len; i++){
			item = source[i];
			if(interator.call(thisObject || source, i, item) === true){
				return item;
			}
		}
	}
	return null;
};

/**
 * 遍历数组中所有元素，将每一个元素应用方法进行转换，并返回转换后的新数组。
 * @name wfl.array.map
 * @function
 * @grammar wfl.array.map(source, interator[, thisObject])
 * @param {Array} source 需要遍历的数组
 * @param {Function} interator 对每个数组元素进行筛选的函数，该函数有两个参数，第一个为数组索引值，第二个为数组元素，function (index, item)
 * @param {Object} [thisObject] 函数调用时的this指针，如果没有此参数，默认是当前遍历的数组

 * @return {Array} 转换后的数组, 不改变原数组
 */
 wfl.array.map = function(source, interator, thisObject){
	var i, len = source.length, item,
		result = [];
	if(typeof interator == 'function'){
		for(i = 0; i < len; i++){
			item = source[i];
			result[i] = interator.call(thisObject || source, i, item);
		}
	}
	return result;
 };

/**
 * 移除数组中的项(by match)
 * @name wfl.array.remove
 * @function
 * @grammar wfl.array.remove(source, match)
 * @param {Array} source 需要遍历的数组
 * @param {Any} match 要移除的项

 * @return {Array} 移除后的数组
 */
 wfl.array.remove = function(source, match){
	var i, len = source.length;
	for(i = 0; i < len; i++){
		if(i in source && source[i] === match){
			source.splice(i, 1);
		}
	}
	return source;
 };

/**
 * 移除数组中的项(by index)
 * @name wfl.array.removeAt
 * @function
 * @grammar wfl.array.removeAt(source, match)
 * @param {Array} source 需要遍历的数组
 * @param {Any} match 要移除的项

 * @return {Any} 被移除的数组项
 */
 wfl.array.removeAt = function(source, index){
	return source.splice(index, 1)[0];
 };

/**
 * 过滤数组中的相同项。如果两个元素相同，会删除后一个元素。
 * @name wfl.array.unique
 * @grammar wfl.array.unique(source[, deep])
 * @param {Array} source 要过滤的数组
 * @param {Boolean} [deep] 是否改变原数组, 设置为true将改变原数组, 反之则相反, 默认为false

 * @return {Array} 过滤后的数组
 */
wfl.array.unique = function(source, deep){
	var i, j, len = source.length,
		result = source.slice(0),
		deep = deep || false,
		returnValue = deep ? source : result;
	for(i = 0; i < len; i++){
		var item1 = returnValue[i];
		for(j = 0; j < len; j++){
			if(i == j){
				continue;
			}
			var item2 = returnValue[j];
			if(item1 === item2){
				returnValue.splice(i > j ? i : j, 1);
			}
		}
	}
	return returnValue;
};

/****************************************************************
 * 操作字符串的方法
 * @namespace wfl.string
 */
wfl.string = wfl.string || {};

/**
 * 删除字符串两端的空白字符
 * @name wfl.string.trim
 * @function
 * @grammar wfl.string.trim(source)
 * @param {String} source 目标字符串

 * @return {String} 删除字符串两端空白字符后的字符串
 */
wfl.string.trim = wfl.trim = function(source){
	return String(source).replace(/(^\s*)|(\s*$)/g, "");
	//return source.replace(/^(\s|\u00A0)+/,"").replace(/(\s|\u00A0)+$/,"");
};

/**
 * 获取目标字符串在gbk编码下的字节长度
 * @name wfl.string.len
 * @function
 * @grammar wfl.string.len(source)
 * @param {String} source 目标字符串
 * @remark /[\x00-\xff]/ 匹配所有ASCII字符, 全角和中文字符是非ASCII字符占两个字节
 * @note Unicode字符用16位表示占两个字节, 一个ASCII码值占一个字节(8个二进制位)

 * @return {Number} 字符串的长度
 */
wfl.string.len = function(source){
	return String(source).replace(/[^\x00-\xff]/g, "**").length;
};

/**
 * 判断两个字符串是否相等
 * @name wfl.string.compare
 * @function
 * @grammar wfl.string.compare(source1, source2[, fuzzy])
 * @param {String} source1 目标字符串1
 * @param {String} source2 目标字符串2
 * @param {Any} [fuzzy] 是否执行模糊匹配, 将忽略大小写

 * @return {Boolean} 返回判断结果true||false
 */
wfl.string.compare = function(source1, source2, fuzzy){
	var result = false;			
	if(typeof source1 == 'string' && typeof source2 == 'string'){
		source1 = fuzzy ? source1.toLowerCase() : source1;
		source2 = fuzzy ? source2.toLowerCase() : source2;	
		if(source1 === source2 && source1.length === source2.length ){
			result = true;
		}
	}
	return result;
};

/**
 * 查找一个字符串在另一个字符串中出现的次数
 * @name wfl.string.intag
 * @function
 * @grammar wfl.string.intag(source, tag[, fuzzy])
 * @param {String} source 被检索字符串
 * @param {String} tag 目标字符串
 * @param {Any} [fuzzy] 是否执行模糊匹配, 将忽略大小写
 * @remark 采用两种方式实现: match||indexOf

 * @return {Number} 返回出现的次数, 未查询到匹配返回0
 */
wfl.string.intag = function(source, tag, fuzzy){
	//match实现
	/*
	var mode = fuzzy ? "gi" : "g",
		pattern = new RegExp(tag, mode);
	return source.match(pattern) ?  source.match(pattern).length : 0;
	*/

	//indexOf实现
	var count = 0,	
		index = 0;
	source = fuzzy ? source.toLowerCase() : String(source);
	tag = fuzzy ? tag.toLowerCase() : String(tag);
	do{		
		index = source.indexOf(tag, index);	
		if(index != -1){
			count += 1;		
			index += tag.length;
		}
	}while(index != -1);	
	return count;
};

/**
 * 对目标字符串进行html编码
 * @name wfl.string.encodeHTML
 * @function
 * @grammar wfl.string.encodeHTML(source)
 * @param {String} source 目标字符串

 * @return {String} html编码后的字符串
 */
wfl.string.encodeHTML = wfl.encodeHTML = function(source){
	return String(source).replace(/&/, "&amp;")
				 .replace(/</g, "&lt;")
				 .replace(/>/g, "&gt;")
				 .replace(/"/g, "&quot;")
				 .replace(/'/g, "&#39;");
};

/**
 * 对目标字符串进行html解码
 * @name wfl.string.decodeHTML
 * @function
 * @grammar wfl.string.encodeHTML(source)
 * @param {String} source 目标字符串

 * @return {String} html解码后的字符串
 */
wfl.string.decodeHTML = wfl.decodeHTML = function(source){
	var result = String(source).replace(/&amp;/g, "&")
				 .replace(/&lt;/g, "<")
				 .replace(/&gt;/g, ">")
				 .replace(/&quot;/g, '"');
	//处理被转义的中文或全角字符
	return result.replace(/&#([\d]+);/g, function(str0, str1){
		// str1 匹配[\d]+ (包含单引号)
		return String.fromCharCode(parseInt(str1, 10));
	});
};

/**
 * 将目标字符串中英文单词的首字母转换为大写
 * @name wfl.string.ToUpper
 * @function
 * @grammar wfl.string.ToUpper(source)
 * @param {String} source 目标字符串

 * @return {String} 转换后的字符串

 */
wfl.string.toUpper = function(source){
	return String(source).replace(/\b\w+\b/g, function(words){
		return words.substr(0, 1).toUpperCase() + words.substr(1);
	});
};

/**
 * 去掉字符串中的html标签
 * @name wfl.string.stripHtmlTags
 * @function
 * @grammar wfl.string.stripHtmlTags(source)
 * @param {String} source 目标字符串

 * @return {String} 去掉html标签后的字符串
 */
wfl.string.stripHtmlTags = function(source){
	return String(source).replace(/<\/?\w+>/g, "");
	//return String(source || '').replace(/<[^>]+>/g, '');
};

/**
 * 对字符串做安全转义,转义字符包括: 单引号,双引号,左右小括号,斜杠,反斜杠,上引号.
 * @name wfl.string.escape
 * @function
 * @grammar wfl.string.escape(source)
 * @param {String} source 目标字符串

 * @return {String} 转义后的字符串
 */
wfl.string.escape = function(source){
	return String(source).replace(/['"<>\/\\`]/g, function(character){
		return "&#" + character.charCodeAt(0) + ";";
	});
};

/**
 * 对js片段做安全转义,编码低于255的都将转换成\x加16进制数
 * @name wfl.string.escapeJS
 * @function
 * @grammar wfl.string.escapeJS(source)
 * @param {String} source 目标字符串

 * @return {String} 转义后的字符串
 */
wfl.string.escapeJS = function(source){
	var i, len = source.length, charCode, result = [];
	for(i = 0; i < len; i++){
		charCode = source.charCodeAt(i);
		if(charCode > 255){
			result.push(source.charAt(i));
		}else{
			result.push("\\x" + charCode.toString(16));
		}
	}
	return result.join('');
};

/**
 * 将字符串中常见的全角字符转换成半角字符
 * @name wfl.string.toHalfWidth
 * @function
 * @grammar wfl.string.toHalfWidth(source)
 * @param {String} source 目标字符串

 * @return {String} 转换后的字符串
 */
wfl.string.toHalfWidth = function(source){
	return String(source).replace(/[\uFF01-\uFF5E]/g, function(character){
		return String.fromCharCode(character.charCodeAt(0) - 65248);
	}).replace(/\u3000/g, " ");
};


/****************************************************************
 * 操作number的方法
 * @namespace wfl.number
 */
wfl.number = wfl.number || {};

/**
 * 对目标数字进行0补齐处理
 * @name wfl.number.fill
 * @function
 * @grammar wfl.number.fill(source, length)
 * @param {number} source 目标数字
 * @param {number} length 输出长度

 * @return {string} 返回0补齐后的结果字符串
 */
 wfl.number.fill = function(source, length){
	var flag = (source < 0), source = String(Math.abs(source)), result = source;
	if(source.length < length){
		result = (new Array(length - source.length + 1)).join("0") + source;
	}
	return flag ? "-" : "" + result;
 };

/**
 * 为目标数字添加逗号分隔
 * @name wfl.number.addComma
 * @function
 * @grammar wfl.number.addComma(source[, length])
 * @param {number} source 目标数字
 * @param {number} length 逗号与逗号之间间隔的数字位数, 默认为3
 * @remark 精度支持到十万亿, 超出范围数值将不准确

 * @return {String} 添加逗号分隔后的字符串
 */
 wfl.number.addComma = function(source, length){
	/*
	if (!length || length < 1) {
        length = 3;
    }
    source = String(source).split(".");
    source[0] = source[0].replace(new RegExp('(\\d)(?=(\\d{'+length+'})+$)','ig'),"$1,");
    return source.join(".");
	*/

	length = !length || length < 1 ? 3 : length;	
	source = String(source).split(".");	
	var len = source[0].length, temp = source[0], array = [];
	do{
		array.push(temp.substr(len-length, length));
		temp = source[0].substr(0, len-length);
		len -= length;
	}while(len >= length);
	if(temp.length > 0){
		array.push(temp);
	}
	return array.reverse().join(",") + (source[1] && source[1].length > 0 ? "." + source[1] : "");
 };

/**
 * 删除目标字符串中的逗号分隔符
 * @name wfl.delComma
 * @function
 * @grammar wfl.delComma(source)
 * @param {String} source 目标字符串

 * @return {String} 删除逗号分隔后的字符串
 */
wfl.delComma = function(source){
	source = String(source).split(".");
	if(!source || source.length > 2){
		return source;
	}
	var decimal = source[1] ? "." + source[1] : "";
	return source[0].replace(/[\x2C]/g, "") + decimal;
};

/**
 * 生成随机数
 * @note 范围[min, max]
 * @function
 * @grammar wfl.number.random(min, max)
 * @param {number} min 最小值
 * @param {number} max 最大值

 * @return {Number} 返回生成的范围内的随机数
 */
wfl.number.random = function(min, max){
	return Math.floor(Math.random() * (max - min + 1) + min);
};

/****************************************************************
 * 操作日期的方法
 * @namespace wfl.date
 */
wfl.date = wfl.date || {};

/**
 * 对目标日期对象进行格式化
 * @name wfl.date.format
 * @function
 * @grammar wfl.date.format(source, pattern)
 * @param {Date} source 目标日期对象
 * @param {String} pattern 日期格式化规则字符串,影响的字符包括(y,Y,m,M,d,D,h,H,s,S,w,W),其中月份必须为大写M, 分钟必须是小写m
 * @example wfl.date.format(new Date(), "yyyy年MM月dd日 星期W") 将返回格式如: 2012-11-23 星期五
	
 * @return {String} 格式化后的字符串
 */
wfl.date.format = function(source, pattern){
	if(!wfl.isDate(source) || typeof pattern != 'string'){
		return source.toString();
	}
	var year = source.getFullYear(),
		month = source.getMonth() + 1,
		_date = source.getDate(),
		day = source.getDay(),
		hours = source.getHours(),
		minutes = source.getMinutes(),
		seconds = source.getSeconds(),
		fill = wfl.number.fill;

	return pattern.replace(/yyyy/gi, fill(year, 4)).replace(/yy/gi, fill(parseInt(year.toString().slice(2), 10), 2))
		.replace(/MM/g, fill(month, 2)).replace(/M/g, month)
		.replace(/dd/gi, fill(_date, 2)).replace(/d/gi, _date)
		.replace(/hh/gi, fill(hours, 2)).replace(/h/gi, hours)
		.replace(/mm/g, fill(minutes, 2)).replace(/m/g, minutes)
		.replace(/ss/gi, fill(seconds, 2)).replace(/s/gi, seconds)
		.replace(/w/gi, "日一二三四五六".charAt(day));
};

/**
 * 将目标字符串转换成日期对象
 * @name wfl.date.parse
 * @function
 * @grammar wfl.date.parse(source)
 * @param {String} source 目标字符串
 * @note 1.短日期可以使用“/”或“-”作为日期分隔符，但是必须用月/日/年的格式来表示，例如"2012/12/21"
 * @note 2.小时、分钟、和秒钟之间用冒号分隔, "10:"、"10:11"、和 "10:11:12" 都是有效的,可省略
 * @note 3.逗号和空格被视为分隔符。允许使用多个分隔符。

 * @return {Date} 日期对象
 */
wfl.date.parse = function(source){
	//return new Date(source.replace(/-/g, "/"));
	var reg = new RegExp("^\\d+(\\-|\\/)\\d+(\\-|\\/)\\d+\x24");
    if ('string' == typeof source) {
        if (reg.test(source) || isNaN(Date.parse(source))) {
            var d = source.split(/ |T/),
                d1 = d.length > 1 
                        ? d[1].split(/[^\d]/) 
                        : [0, 0, 0],
                d0 = d[0].split(/[^\d]/);
            return new Date(d0[0] - 0, 
                            d0[1] - 1, 
                            d0[2] - 0, 
                            d1[0] - 0, 
                            d1[1] - 0, 
                            d1[2] - 0);
        } else {
            return new Date(source);
        }
    }    
    return new Date();
};

/**
 * 比较两个日期的大小
 * @name wfl.date.compare
 * @function
 * @grammar wfl.date.compare(date1, date2)
 * @param {String|Date} date1 待比较目标日期, 
 * @param {String|Date} date2 待比较目标日期,
 * @remark 参数1大于参数2, 返回1; 参数1小于参数2, 返回-1; 参数1等于参数2, 返回0;

 * @return {Number|NaN} 返回比较结果, 若参数错误或无法比较返回NaN
 */
wfl.date.compare = function(date1, date2){
	var result;
	if((typeof date1 == 'string' || wfl.isDate(date1)) && (typeof date2 == 'string' || wfl.isDate(date2))){
		date1 = Date.parse(wfl.date.parse(date1));
		date2 = Date.parse(wfl.date.parse(date2));
		result = date1 > date2 ? 1 : (date1 < date2 ? -1 : (date1 == date2 ? 0 : NaN));
	}
	return !isNaN(result) ? result : NaN;
}

/**
 * 计算两个日期之间的差值
 * @name wfl.date.difference
 * @function
 * @grammar wfl.date.difference(date1, date2[, unit, fixed])
 * @param {String|Date} date1 目标日期
 * @param {String|Date} date2 目标日期
 * @param {Number} [unit] 输出单位,默认是毫秒, 0-毫秒, 1-秒, 2-分钟, 3-小时, 4-天 
 * @param {Number} [fixed] 小数位数, 默认为保留2位小数, 如不指定小数位, 将自动取舍, 如(2012-12-13 10:00:59和12/10/2012 23:24:59)之间相差2天, 但(2012-12-13 10:00:59和12/10/2012 13:24:59)之间相差3天

 * @return {Number|NaN} 返回日期间的差值, 参数错误或无法计算时返回NaN

 */
wfl.date.diff = function(date1, date2, unit, fixed){
	unit = unit || 0;
	fixed = fixed || 0;
	var result;
	if((typeof date1 == 'string' || wfl.isDate(date1)) && (typeof date2 == 'string' || wfl.isDate(date2))){
		date1 = Date.parse(wfl.date.parse(date1));
		date2 = Date.parse(wfl.date.parse(date2));
		result = Math.abs(date1 - date2);
		switch(unit){
		case 1:
			result /= 1000;
			break;
		case 2:
			result /= (1000*60);
			break;
		case 3:
			result /= (1000*60*60);
			break;
		case 4:
			result /= (1000*60*60*24);
			break;
		default:
			break;
		}
	}
	return result ? result.toFixed(fixed) : NaN;
}

/**
 * 将日期常用数据存入数组
 * @name wfl.date.toArray
 * @function
 * @grammar wfl.date.toArray(source)
 * @param {String|Date} date1 目标日期

 * @return {Array} 返回转换后的数组, 参数错误将返回空数组
 * @remark Array[0]-年, Array[1]-月, Array[2]-日, Array[3]-时, Array[4]-分, Array[5]-秒, Array[6]-毫秒数, Array[7]-星期
 */
wfl.date.toArray = function(source){
	var result = [];
	if(typeof source == 'string' || wfl.isDate(source)){
		source = wfl.date.parse(source);
		if(source){
			result[0] = source.getFullYear();
			result[1] = source.getMonth() + 1;
			result[2] = source.getDate();
			result[3] = source.getHours();
			result[4] = source.getMinutes();
			result[5] = source.getSeconds();
			result[6] = source.getTime();
			result[7] = source.getDay();
		}
	}		
	return result;
}

/**
 * 日期计算

 */
wfl.date.calculate = function(date1, date2, pattern){
	
}

/**
 * 判断是否为闰年
 * @name wfl.date.isLeapyear
 * @function
 * @grammar wfl.date.isLeapyear([year])
 * @param {Number} [year] 目标年份, 为空表示判断当年年份是否是闰年

 * @return {Boolean} 返回结果true|false
 */
wfl.date.isLeapyear = function(year){
	year = Number(year) || (new Date()).getFullYear();
	return (year%4==0&&((year%100!=0)||(year%400==0)));
}

/****************************************************************
 * 操作原生对象的方法
 * @namespace wfl.object
 */
wfl.object = wfl.object || {};

/**
 * 检测一个对象是否是空的
 * @name wfl.object.isEmpty
 * @function
 * @grammar wfl.object.isEmpty(source)
 * @param {Object} source 目标对象
 * @remark 如果操作了Object|Array的原型, 该结果有可能返回false

 * @return {Boolean} 返回判断结果true|false
 */
wfl.object.isEmpty = function(source){
	for(var key in source){
		return false;
	}
	return true;
};

/**
 * 获取目标对象的值列表
 * @name wfl.object.getValues
 * @function
 * @grammar wfl.object.getValues(source)
 * @param {Object} source 目标对象

 * @return {Object} 返回目标对象值列表数组
 */
 wfl.object.getValues = function(source){
	var key, result = [], len = 0;
	for(key in source){
		if(source.hasOwnProperty(key)){
			result[len++] = source[key];
		}
	}
	return result;
 };

/**
 * 获取目标对象的键名列表
 * @name wfl.object.getKeys
 * @function
 * @grammar wfl.object.getKeys(source)
 * @param {Object} source 目标对象

 * @return {Object} 返回目标对象键名列表数组
 */
 wfl.object.getKeys = function(source){
	var key, result = [], len = 0;
	for(key in source){
		if(source.hasOwnProperty(key)){
			result[len++] = key;
		}
	}
	return result;
 };

/**
 * 将源对象的所有属性拷贝到目标对象中
 * @name wfl.object.extend
 * @function
 * @grammar wfl.object.extend(target, source)
 * @param {Object} target 目标对象
 * @param {Object} source 源对象

 * @return {Object} 返回源对象target
 */
wfl.object.extend = function(target, source){
	if(wfl.isObject(source) && wfl.isObject(target)){	
		for(var key in source){
			if(source.hasOwnProperty(key)){
				target[key] = source[key];
			}
		}
	}
	return target;
};

/**
 * 对一个object进行深度拷贝

 */
 
/**
 * 合并源对象的属性到目标对象
 *
 */

/**
 * 遍历对象中的所有元素
 * @name wfl.object.each
 * @function
 * @grammar wfl.object.each(source, iterator)
 * @param {Object} source 目标对象
 * @param {Function} iterator 对每个对象元素进行调用的函数，该函数有两个参数，第一个为对象key，第二个为对象value，function (key, item)

 * @return {Object} 返回遍历后的对象
 */
wfl.object.each = function(source, iterator){
	var key, item, returnValue;
	if(typeof iterator == 'function'){
		for(key in source){
			if(source.hasOwnProperty(key)){
				item = source[key];
				returnValue = iterator.call(source, key, item);
				if(returnValue === false){
					break;
				}
			}
		}
	}
	return source;
};

/**
 * 遍历object中所有元素，将每一个元素应用方法进行转换，返回转换后的新object。
 * @name wfl.object.map
 * @function
 * @grammar wfl.object.map(source, iterator)
 * @param {Object} source 目标对象
 * @param {Function} iterator 对每个object元素进行处理的函数，该函数有两个参数，第一个为对象key，第二个为对象value，function (key, item)

 * @return {Object} 返回转换后的新对象
 */
wfl.object.map = function(source, iterator){
	var key, item, result = {};
	for(key in source){
		if(source.hasOwnProperty(key)){
			item = source[key];
			result[key] = iterator(key, item);
		}
	}
	return result;
};


/****************************************************************
 * 操作dom的方法
 * @namespace wfl.dom
 */
wfl.dom = wfl.dom || {};

/**
 * 从文档中获取指定的DOM元素
 * @name wfl.dom.gId
 * @function
 * @grammar wfl.dom.gId(id)
 * @param {String} id 元素的id属性或DOM元素
 * @remark 节点: nodeType=1->元素element, nodeType=9->文档document

 * @return {HTMLElement|null} 返回获取的DOM元素, 如果不存在或参数不合法, 返回null
 */
wfl.dom.gId = wfl.gId = function(id){
	if(!id){
		return null;
	}
	if(wfl.lang.isString(id)){
		return document.getElementById(id);
	} else if (id.nodeName && (id.nodeType == 1 || id.nodeType == 9)) {
        return id;
    }
	return null;
};

/**
 * 获取目标元素所属的document对象

 * @note: ownerDocument 属性返回节点所属的根元素

 */
wfl.dom.getDocument = function(element){
	element = wfl.dom.gId(element);
	return element.nodeType == 9 ? element : element.ownerDocument || element.document;
}

/**
 * 从文档中获取指定HTML标签名称的DOM元素
 * @name wfl.dom.gTag
 * @function
 * @grammar wfl.dom.gTag(tag[, index])
 * @param {String} tag HTML标签名称
 * @param {Number} [index] 元素索引, 从0开始
 * @return {HTMLElement} 返回DOM元素集合|单个HTML元素, 如果不存在或参数不合法, 返回空数组
 */
wfl.dom.gTag = wfl.gTag = function(tag, index){
	var result = [],
		index = !wfl.lang.isNumber(index) ? -1 : Math.abs(index);
	if(wfl.lang.isString(tag)){
		result = document.getElementsByTagName(tag);
	}
	var len = result.length;
	return index > -1 ? result[index >= len ? len-1 : index] : result;
};

/****************************************************************
 * 操作page的方法
 * @namespace wfl.page
 */
wfl.page = wfl.page || {};

/**
 * 在页面上动态加载一个外部CSS文件
 */

/**
 * 在页面上动态加载一个外部JS文件
 */


/****************************************************************
 * 操作xml的方法
 * @namespace wfl.xml
 */
wfl.xml = wfl.xml || {};

/**
 * 创建XMLHttpRequest对象
 */
wfl.createXHR = function() {
	if (window.ActiveXObject) {
		try {
			return new ActiveXObject("Msxml2.XMLHTTP");
		} catch (e) {
			try {
				return new ActiveXObject("Microsoft.XMLHTTP");
			} catch (e) {}
		}
	}
	if (window.XMLHttpRequest) {
		return new XMLHttpRequest();
	}
	return null;
}

/**
 *加载XML文件
 */
wfl.loadXMLDoc = wfl.xml.loadXMLDoc = function(xmlFile){
	var xmlDoc = null;
	if (window.ActiveXObject) { // for ie
		xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
		xmlDoc.async = false;
		xmlDoc.load(xmlFile);
		return xmlDoc;
	}else{
		try{ // for firefox opera
			xmlDoc = document.implementation.createDocument("","",null);
			xmlDoc.async = false;
			xmlDoc.loadXML(xmlFile);
			return xmlDoc;
		}catch(e){
			try{ //for chrome
				var xmlhttp = new window.XMLHttpRequest();
				xmlhttp.open("GET",xmlFile,false);
				xmlhttp.send(null);
				xmlDoc = xmlhttp.responseXML;
				return xmlDoc;
			}catch(e){}
		}
	}	
	return xmlDoc;
}

/**
 * 加载XMLString
 */
wfl.loadXMLString = wfl.xml.loadXMLString = function(xmlString){
	var xmlDoc = null;
	if(window.ActiveXObject){
		xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
		xmlDoc.async = "false";
		xmlDoc.loadXML(xmlString);		
	}else if(document.implementation && document.implementation.createDocument){		
		parser = new DOMParser();
		xmlDoc = parser.parseFromString(xmlString, "text/xml");		
	}else{
		alert("您的浏览器不支持xml文件操作,推荐升级浏览器版本或使用其他浏览器!");
	}
	return xmlDoc;
}


/**
 * 将目标字符串格式化成标准XML格式字符串
 * @name wfl.foramtXMLString
 * @function
 * @grammar wfl.foramtXMLString(source)
 * @param {String} source 目标字符串, 格式:<key>value</>
 * @param {Boolean} strict 是否在字符串开头和结尾处增加<xml></xml>, xml字符串的必然被"<xml>"和"</xml>"包围才可以被解析
 * @return 返回格式化后的字符串, 格式为 <key>value</key>
 */
wfl.foramtXMLString = wfl.xml.foramtXMLString = function(source, strict){	
	if(!source || !wfl.isString(source)) return;
	var strict = strict || true;
	var pattern=/<\w+>(.*?)<\/>/g;
	var tempArray = source.match(pattern);
	for(var i=0; i<tempArray.length; i++){
		var tempStr = tempArray[i].replace(/<(\w+)>(.*?)<\/>/,"<$1>$2</$1>");
		source = source.replace(tempArray[i], tempStr);		
	}
	return strict ? ("<xml>" + source + "</xml>") : source;
}
