/**
 * APP扫码登录插件js库
 * author: 柏慧敏
 * date: 2020-02-06
 * version: 1.0
 */
'use strict';

var personCode = {};

(function (fr, $, window, document, _undefined) {
    //二维码图片字符串
    var qrcode = '';
    //二维码图片元素id
    var imgId = '';
    //定时任务key
    var intervalKey;
    //唯一流水号login_Key
    var login_Key = '';
    //全局默认参数
    var defaults = {
        width: 300,
        height: 300,
        interval: 1000,
        timeOut: 120
    };
    //全局参数
    var settings = {};
    //轮询开始时间
    var startTime = 0;
    var endTime = 0;

	/**
	 * 清空全局
	 */
	var clear = function(){
		if(intervalKey){
			clearInterval();
		}
		settings = {};
		qrcode = '';
		imgId = '';
        login_Key = '';
	}
	

    /**
     * 设置全局参数
     */
    var initSettings = function (opts) {
        //设置全局参数
        settings = {};
        settings = $.extend({}, opts, defaults);
        if(opts.width){
            settings.width = opts.width;
        }
        if(opts.height){
            settings.height = opts.height;
        }
        if(opts.interval){
            settings.interval = opts.interval;
        }
        if(opts.timeOut){
            settings.timeOut = opts.timeOut;
        }
    };


    /**
     * 清空定时任务标识
     */
    var clearInterval = function () {
        window.clearInterval(intervalKey);
        intervalKey = undefined;
        startTime = 0;
        endTime = 0;
    };


    /**
     * 判断轮询是否已超时
     * 超时则移除轮询
     */
    var timeOutClearInterval = function () {
        console.log('intervalKey=' + intervalKey);
        if (intervalKey) {
            var now = new Date().getTime();
            if (now >= endTime) {
                console.log('结束轮询认证结果, 共执行[' + (now - startTime) + ']毫秒');
                clearInterval();
            }
        }
    };

    /**
     * 查询用户信息
     * @param login_Key
     * @returns {{}}
     */
    var queryQrCodeLoginInfo = function (login_Key) {
        var _url = settings.context + '/login/queryQrCodeLoginInfo';
        var _result = {};
        $.ajax({
            url: _url,
            data: {loginKey: login_Key},
            type: 'post',
            async: false,
            success: function (data) {
                _result = data;
                console.log('loginKey=[' + login_Key + ']的认证结果如下:');
                console.log(_result);
            },
            error: function (xhr) {
                console.log(xhr);
            }
        });
        return _result;
    };

    /**
     * 查询授权登录信息
     * @param login_Key
     * @returns {{}}
     */
    var queryQrCodeLoginStatus = function (login_Key) {
        var _url = settings.context + '/login/queryQrCodeLoginStatus';
        var _result = {};
        $.ajax({
            url: _url,
            data: {loginKey: login_Key},
            type: 'post',
            async: false,
            success: function (data) {
                _result = data;
                console.log('loginKey=[' + login_Key + ']的认证结果如下:');
                console.log(_result);
            },
            error: function (xhr) {
                console.log(xhr);
            }
        });
        return _result;
    };

    /**
     * 获取用户信息
     */
    fr.UserInfoCallBack = function () {
        //如果轮询已经超时，先停止轮询并清空轮询参数
        timeOutClearInterval();
        var _result = queryQrCodeLoginInfo(login_Key);
        // 成功获取到返回信息
        if ('0' === _result.returnCode) {
            //停止轮询，并清空轮询参数
            if (intervalKey) {
                clearInterval();
            }
            // 成功返回用户信息（fhbz:0-成功；其他-失败）
            if (_result.fhbz == '0' && true === settings.cycleQuery && settings.callBack3 && typeof settings.callBack3 === 'function') {
                //启动轮询，获取授权登录结果
                intervalQueryResult();
            }
            // 调用回调函数
            settings.callBack2.apply(null, [_result]);
        } else {// 未获取到返回信息
            if (settings.alwaysExecute === true) {//总是执行回调函数
                settings.callBack2.apply(null, [_result]);
            } else if(!intervalKey){//超时时执行回调函数
                _result.timeOutFlag = true;
                settings.callBack2.apply(null, [_result, settings]);
            }
        }
    };

    /**
     * 获取授权登录结果
     */
    fr.ResultCallBack = function () {
        //如果轮询已经超时，先停止轮询并清空轮询参数
        timeOutClearInterval();
        var _result = queryQrCodeLoginStatus(login_Key);
        // 成功获取到返回信息
        if ('0' === _result.returnCode) {
            // 停止轮询，并清空轮询参数
            if (intervalKey) {
                clearInterval();
            }
            // 调用回调函数
            settings.callBack3.apply(null, [_result]);
        } else{//验证失败
            if (settings.alwaysExecute === true) {//总是执行回调函数
                settings.callBack3.apply(null, [_result]);
            } else if(!intervalKey){//超时时执行回调函数
                _result.timeOutFlag = true;
                settings.callBack3.apply(null, [_result, settings]);
            }
        }
    };

    /**
     * 设置二维码图片
     */
    var setQrcodeImg = function (imgId, qrcode) {
        var $img = $('#' + imgId);
        $img.attr('src', 'data:image/png;base64,' + qrcode);
    };

    /**
     * 开始轮询获取用户信息
     * @param interval
     */
    var intervalQueryUserInfo = function () {
        startTime = new Date().getTime();
        endTime = startTime + settings.timeOut * 1000;
        console.log('开始轮询获取用户信息, startTime=[' + startTime + '], endTime=[' + endTime + ']');
        intervalKey = window.setInterval("personCode.UserInfoCallBack()", settings.interval);
    };

    /**
     * 开始轮询获取授权登录信息
     * @param interval
     */
    var intervalQueryResult = function () {
        startTime = new Date().getTime();
        endTime = startTime + settings.timeOut * 1000;
        console.log('开始轮询获取授权登录结果, startTime=[' + startTime + '], endTime=[' + endTime + ']');
        intervalKey = window.setInterval("personCode.ResultCallBack()", settings.interval);
    };

    /**
     * 生成二维码
     * @param opts
     * @returns {{}}
     */
    fr.generateQrCode = function (opts) {
		clear();
        var _result = {};
        initSettings(opts);
        var _url = settings.context + '/login/getQrCode';
        //避免回调函数在这里执行
        var param = $.extend({}, settings, {});
        param.callBack1 = undefined;
        param.callBack2 = undefined;
        param.callBack3 = undefined;

        $.ajax({
            url: _url,
            type: 'post',
            async: true,
            data: param,
            success: function (data) {
                _result = data;
                // 成功返回信息
                if (_result.loginKey) {
                    // 唯一标示login_Key赋值
                    login_Key = _result.loginKey;
                    if (settings.imgId && typeof settings.imgId === 'string') {
                        setQrcodeImg(settings.imgId, _result.qrcode);
                    }
                    // 如果设置了启用轮询
                    if (true === settings.cycleQuery && settings.callBack2 && typeof settings.callBack2 === 'function') {
                        //启动轮询查询用户信息
                        intervalQueryUserInfo();
                    }
                }
                // 调用回调函数
                settings.callBack1.apply(null, [_result, settings]);
            },
            error: function (xhr) {
                console.log(xhr);
            }
        });
    };

})(personCode, jQuery, window, document, undefined);