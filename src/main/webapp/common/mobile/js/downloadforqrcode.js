//android的下载跳转地址
var androidUrl = 'http://zfgjjwt.jian.gov.cn:9080/ish/common/download/jagjj.apk';
//ios的下载跳转地址
var iosUrl = 'https://apps.apple.com/cn/app/%E6%8B%89%E7%BB%93%E5%B0%94/id1435303295';
var main = new Vue({
    el: '#app',
    data: function(){
        return {
            isshow: false,
            urlKeyid: '',
            requestParams: null, //getTitleId 的请求参数
            requestUrl: '',
            
            location: '', //跳转地址
            btnText: '',
            shareUrl: '',
            systemType: '',
            showShare: false
        }
    },
    created: function(){
        var _self = this;
        weui.loading('加载中');
        _self.getSystemType();
    },
    methods:{
        getRootPath: function (){
        	var curWwwPath=window.document.location.href;
        	var pathName=window.document.location.pathname;
        	var pos=curWwwPath.indexOf(pathName);
        	var localhostPaht=curWwwPath.substring(0,pos);
        	var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
        	return(localhostPaht+projectName);
        },
        getSystemType: function(){
            var _self = this;
            console.log(_self.getRootPath());
            // var backgroundImgs = {
            //     androidImgUrl: _self.getRootPath() + '/download_android.png',
            //     iosImgUrl: _self.getRootPath() + '/download_ios.png'
            // };
            var u = navigator.userAgent,
                app = navigator.appVersion;
            var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1; //g
            var isIOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
            if (isAndroid) {
               
               //_self.requestParams.phonelx = 2;
               _self.btnText = '下载';
               //_self.shareUrl = _self.getRootPath() + '/img/dl3.png';
               _self.systemType = 'android';
               //修改为固定的url，不通过ajax请求
               _self.location = androidUrl; //android的下载跳转地址
               if(androidUrl) {
            	   $('.pic-word-no-url-word').addClass('hide');
            	   $('a.download-btn, .pic-word').removeClass('hide');
               }
            }
            if (isIOS) {

                //_self.requestParams.phonelx = 1;
                _self.btnText = '下载';
               //_self.shareUrl = _self.getRootPath() + '/img/dl1.png';
                _self.systemType = 'ios';
                //修改为固定的url，不通过ajax请求
                //_self.location = ''; //ios的下载跳转地址
                _self.location = iosUrl; //ios的下载跳转地址
                if(iosUrl) {
                	$('.pic-word-no-url-word').addClass('hide');
                	$('a.download-btn, .pic-word').removeClass('hide');
                }
            }
            //修改为不通过ajax请求的下载地址
            //_self.getUrl();
            setTimeout(function(){
                _self.isshow = true;
                weui.loading().hide();
            }, 300);
        },
        getUrl: function(){
            var _self = this;
            weui.loading('加载中');
    		$.ajax({
    			type: 'post',
    			url: _self.requestUrl,
    			data: _self.requestParams,
    			datatype: "json",
    			success: function(response) {
                    response = typeof response == 'string' ? JSON.parse(response) : response;
                    _self.isshow = true;
                    _self.location = response.Url;
                    weui.loading().hide();
    			},
    			error: function(error){
    				weui.loading().hide();
    				weui.alert('网络繁忙，请稍后再试！');
    			}
    		});
        },
        isWeixin: function(){
            var _self = this;
            var ua = navigator.userAgent.toLowerCase();
            if(ua.match(/MicroMessenger/i)=="micromessenger") {
                return true;
            } else {
                return false;
            }
        },
        //下载功能
        download: function(){
            var _self = this;
            console.log(_self.location);
            if(_self.systemType == 'android'){
                if(_self.isWeixin()){
                    _self.showShare = true;
                } else {
                    _self.showShare = false;
                    window.location.href = _self.location;
                }
            }
            if(_self.systemType == 'ios'){
                _self.showShare = true;
                window.location.href = _self.location;
                //weui.alert(_self.getRootPath());
                // weui.alert('IOS版本敬请期待');
            }
        },
        hideShare: function(){
            var _self = this;
            _self.showShare = false;
        }
    }
});
