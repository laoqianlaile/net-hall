var winWidth = document.documentElement.clientWidth;
if('axios' in window){
	axios.defaults.headers['Content-Type'] = 'application/x-www-form-urlencoded';
	axios.defaults.headers['Accept'] = 'application/json, text/javascript';
}
;(function(designWidth, maxWidth) {
	var doc = document,
	win = window,
	docEl = doc.documentElement,
	remStyle = document.createElement("style"),
	tid;

	function refreshRem() {
		var width = docEl.getBoundingClientRect().width;
		maxWidth = maxWidth || 540;
		width>maxWidth && (width=maxWidth);
		var rem = width * 100 / designWidth;
		remStyle.innerHTML = 'html{font-size:' + rem + 'px;}';
	}

	if (docEl.firstElementChild) {
		docEl.firstElementChild.appendChild(remStyle);
	} else {
		var wrap = doc.createElement("div");
		wrap.appendChild(remStyle);
		doc.write(wrap.innerHTML);
		wrap = null;
	}
	//要等 wiewport 设置好后才能执行 refreshRem，不然 refreshRem 会执行2次；
	refreshRem();

	win.addEventListener("resize", function() {
		clearTimeout(tid); //防止执行两次
		tid = setTimeout(refreshRem, 300);
	}, false);

	win.addEventListener("pageshow", function(e) {
		if (e.persisted) { // 浏览器后退的时候重新计算
			clearTimeout(tid);
			tid = setTimeout(refreshRem, 300);
		}
	}, false);

	if (doc.readyState === "complete") {
		doc.body.style.fontSize = "16px";
	} else {
		doc.addEventListener("DOMContentLoaded", function(e) {
			doc.body.style.fontSize = "16px";
		}, false);
	}
})(750, winWidth);

window.wxCommonFun = {
	/**
		 * 
		 * @desc   获取上下文根
		 * @return {String}
	*/
	contexPath : function(){
		var path = location.pathname.split('/')[1];
		return path === undefined || path.length == 0 ? '' : '/' + path
	},
	/**
		 * 
		 * @desc   获取url参数
		 * @param  {String} name 参数名
		 * @return {String}
	*/
	getRequest : function(name){
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
		var r = decodeURI(window.location.search).substr(1).match(reg); 
		if (r != null) return unescape(r[2]); return null; 
	},
	/**
		 * 
		 * @desc   数据脱敏
		 * @param  {String} str 数据
		 * @param  {Number} frontLen 保留开始几位
		 * @param  {Number} endLen 保留结束几位
		 * @param  {String} sep 脱敏符号
		 * @return {String}
	*/
	infoHiding : function(str, frontLen, endLen, sep){
		if(typeof str != 'string'){
			str = String(str)
		}
		var frontNum = frontLen ? frontLen : 3;
		var endNum = endLen ? endLen : 4;
		var sepType = sep ? sep : '*';
		var len = str.length - frontNum - endNum;
		var sepStr = '';
			for (var i = 0; i < len; i++) {
			sepStr += sepType;
		}
		return str.substring(0,frontNum) + sepStr + str.substring(str.length - endNum);
	},
	
	/**
		 * 
		 * @desc   格式化金额数字
		 * @param  {Number} number 要格式化的数字
		 * @param  {Number} decimals 保留几位小数
		 * @param  {String} dec_point 小数点符号
		 * @param  {String} thousands_sep 千分位符号
		 * @return {String}
	 */
	digitFormatForFinance : function (number, decimals, dec_point, thousands_sep) {
	    number = (number + '').replace(/[^0-9+-Ee.]/g, '');
	    var n = !isFinite(+number) ? 0 : +number,
	        prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
	        sep = (typeof thousands_sep === 'undefined') ? ',' : thousands_sep,
	        dec = (typeof dec_point === 'undefined') ? '.' : dec_point,
	        s = '',
	        toFixedFix = function (n, prec) {
	            var k = Math.pow(10, prec);
	            return '' + Math.ceil(n * k) / k;
	        };
	    s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
	    var re = /(-?\d+)(\d{3})/;
	    while (re.test(s[0])) {
	        s[0] = s[0].replace(re, "$1" + sep + "$2");
	    }
	    if ((s[1] || '').length < prec) {
	        s[1] = s[1] || '';
	        s[1] += new Array(prec - s[1].length + 1).join('0');
	    }
	    return s.join(dec);
	},

	/**
		 * 
		 * @desc   获取数组中满足name值得对象的信息
		 * @param  {Array} arr 原数组
		 * @param  {String} name 数组信息name
		 * @return {Object}
	 */
	getArrInfo: function(arr, name){
		var val = arr.filter(function (x) {
			return x.name === name
		})
		return val.length == 0 ? '' : val[0];
	},

	/**
		 * 
		 * @desc   数组中对象排序方法 使用： Array.sort(wxCommonFun.objCompare("prop"))
		 * @param  {String} prop 对象中的比较值
		 * @return {Boolean}
	 */
	objCompare: function(prop){
		return function (obj1, obj2) {
			var val1 = obj1[prop];
			var val2 = obj2[prop];
			if (!isNaN(Number(val1)) && !isNaN(Number(val2))) {
				val1 = Number(val1);
				val2 = Number(val2);
			}
			if (val1 < val2) {
				return -1;
			} else if (val1 > val2) {
				return 1;
			} else {
				return 0;
			}            
		}
	},
	
	/**
		 * 
		 * @desc   接口请求
		 * @param  {Function} successFun 成功返回函数
		 * @param  {Function} errorFun 失败返回函数
		 * @param  {Object} para 请求的参数
		 * @param  {String} ajaxUrl 请求的地址
		 * @param  {String} postType 请求类型
	 */
	_ajaxFun: function(successFun, errorFun, para, ajaxUrl, postType){
		weui.loading('加载中');
		if('axios' in window){
			axios({
				method: postType ? postType : 'get',
				url: ajaxUrl,
				params: para
			})
			.then(function (response) {
				response = typeof response == 'string' ? JSON.parse(response) : response;
				if(response.data.__errcode == '0'){
					successFun(response.data);
				} else {
					weui.alert(response.data.__errmsg);
				}
				weui.loading().hide()
			})
			.catch(function (error) {
				errorFun();
				weui.loading().hide();
				weui.alert('系统忙，请稍后再试！');
				console.error(error);
			});
		}else{
			$.ajax({
				type: postType ? postType : 'get',
				url: ajaxUrl,
				data: para,
				datatype: "json",
				success: function(response) {
					response = typeof response == 'string' ? JSON.parse(response) : response;
					if(response.__errcode == '0'){
						successFun(response);
					} else {
						weui.alert(response.__errmsg);
					}
					weui.loading().hide()
				},
				error: function(error){
					errorFun();
					weui.loading().hide();
					weui.alert('系统忙，请稍后再试！');
					console.error(error);
				}
			});
		}
	},

	_ajaxFunExtLoading: function(successFun, errorFun, para, ajaxUrl, postType){
		$.ajax({
			type: postType ? postType : 'get',
			url: ajaxUrl,
			data: para,
			datatype: "json",
			success: function(response) {
				response = typeof response == 'string' ? JSON.parse(response) : response;
				if(response.__errcode == '0'){
					successFun(response);
				} else {
					weui.alert(response.__errmsg);
				}
			},
			error: function(error){
				errorFun();
				weui.loading().hide();
				weui.alert('系统忙，请稍后再试！');
				console.error(error);
			}
		});
	},
	
	/**
		 * 
		 * @desc   隐藏分享功能按钮
		 * @param  {boolean} b true是隐藏，false是不隐藏
	 */
	hideWxMenu : function(b){
		if(!b){
			document.addEventListener('WeixinJSBridgeReady',function onBridgeReady(){
				WeixinJSBridge.call('showOptionMenu');
			});
			try {
				WeixinJSBridge.call('showOptionMenu');
			}
			catch (e) {
				console.log(e)
			}
		}else{
			document.addEventListener('WeixinJSBridgeReady',function onBridgeReady(){
				WeixinJSBridge.call('hideOptionMenu');
			});
			try {
				WeixinJSBridge.call('hideOptionMenu');
			 }
			 catch (e) {
				console.log(e)
			 }
		}
	},
	
	/**
		 * 
		 * @desc   弥补软键盘缩回后页面不回滚的问题  可能存在未知bug
	 */
	autoScrollBack : function(){
		var bfscrollboolean = false;
		var bfscrolltop = document.body.scrollTop;//获取软键盘唤起前浏览器滚动部分的高度
		$("input[type=text], input[type=tel], input[type=number], input[type=password], textarea").focus(function(){
			//获取焦点时触发事件
			interval = setInterval(function(){//设置一个计时器，时间设置与软键盘弹出所需时间相近
				bfscrolltop = document.body.scrollTop;//获取焦点后将浏览器内所有内容高度赋给浏览器滚动部分高度
			},100)
	        bfscrollboolean = true;
		}).blur(function(){//设定输入框失去焦点时的事件
			clearInterval(interval);//清除计时器
	        bfscrollboolean = false;
	        setTimeout(function(){
	        	if(!bfscrollboolean){
	        		document.body.scrollTop = bfscrolltop; //将软键盘唤起前的浏览器滚动部分高度重新赋给改变后的高度
	        	}
	        },100)
		});
	}
};

//添加数组去重方法
Array.prototype.unique = function() {
    var n = []; // 存放已遍历的满足条件的元素
    for (var i = 0; i < this.length; i++) {
        // indexOf()判断当前元素是否已存在
        if (n.indexOf(this[i]) == -1) n.push(this[i]);
    }
    return n;
};

//添加对象数组分组
Array.prototype.sortObjGroup=function(){
	var arr=[],hash={},result=[],n=-1,len=this.length;
	arr=this.sort(function(a,b){
		return a-b;
	})
	for(var i=0;i<len;i++){
		if(!hash[arr[i]]){
			n++;
			hash[arr[i]]=true;
			result[n]=[];
		};
		result[n].push(arr[i]);
	}
	return result;
}

//添加数组删除方法
Array.prototype.remove = function(val) {
	var index = this.indexOf(val);
	if (index > -1) {
		this.splice(index, 1);
	}
};

//Vue自定义全局指令(数据列表)
window.outstandDataArr = [];
window.exceptOutstandDataBool = false;
Vue.directive('creat-outstand-data', {
	inserted: function (el) {
		var str = el.getAttribute('show-outstand-data');
		outstandDataArr = outstandDataArr.concat(str.split(','));
	}
});
Vue.directive('except-outstand', {
	inserted: function (el) {
		exceptOutstandDataBool = true;
	}
});

window.addEventListener('pageshow', function () {
	window.wxCommonFun.hideWxMenu(false)
});
