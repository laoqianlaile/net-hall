
//引导提示
ydl.helpShow = function(tabno) {
	//分组号
	if(!tabno || tabno.length < 1) tabno = '0';
	//按钮对象
	var $helpbtn = $('.sys-help');
	if($helpbtn.data('info') && $helpbtn.data('info').length > 0) { //数据已存在则直接初始化组件
		//绑定click事件，初始化组件
		$('.sys-help').unbind('click').click(function() { 
			ydl.help(JSON.parse($helpbtn.data('info'))[tabno]); 
		});
	}
	else { //数据未存在则请求数据
		ydl.ajax(ydl.contexPath + '/help/helpinfo/getHelpinfo', {flowid: poolSelect._WF, stepid: poolSelect._ST}, function(data) {
			//真实数据
			var tabSteps = data;
			//测试数据
//			var tabSteps = {
//				'0': [
//					    {
//							 position: "",
//							 element: "#b_query",
//							 intro: "1这里是输入框的提示"
//					    }
//				],
//				'1': [
//				      	{
//				      		position: "",
//				      		element: "#b_list_a\\:1",
//				      		intro: "2这里是输入框的提示"
//				      	}
//				]
//			};
			//格式化数据
			if(!isEmptyValue(tabSteps)){
				$.each(tabSteps, function(key, steps) {
					$.each(steps, function(i, step) {
						//添加图标样式
						step['intro'] = '<div class="introjs-tooltip-img"></div>' +
						'<div class="introjs-tooltip-content">' + step.intro + '</div>';
						//特殊处理金额
						if ($(step.element).hasClass('money')) step.element = $(step.element).next()[0];
					});
				});
				//暂存数据
				$helpbtn.data('info', JSON.stringify(tabSteps));
				//绑定click事件，初始化组件
				$('.sys-help').show().click(function() { 
					console.log(tabSteps[tabno])
					ydl.help(tabSteps[tabno]);
				});
			}
		});
	}
}
var isEmptyValue = function(value) {
    var type;
    if(value == null) { // 等同于 value === undefined || value === null
        return true;
    }
    type = Object.prototype.toString.call(value).slice(8, -1);
    switch(type) {
    case 'String':
        return !$.trim(value);
    case 'Array':
        return !value.length;
    case 'Object':
        return $.isEmptyObject(value); // 普通对象使用 for...in 判断，有 key 即为 false
    default:
        return false; // 其他对象均视作非空
    }
};
var cst = {};

(function (cst) { 
     var baseURL = ydl.contexPath+'/common/script/';
	 document.write('<script type="text/javascript" src="'+ baseURL  +'cst_file_tool.js"></script>');
	 document.write('<script type="text/javascript" src="'+ baseURL  +'cst_file_preview.js"></script>');
     document.write('<script type="text/javascript" src="'+ baseURL  +'cst_phone_verification.js"></script>');
     document.write('<script type="text/javascript" src="'+ baseURL  +'cst_phone_number_mask.js"></script>');
     document.write('<script type="text/javascript" src="'+ baseURL  +'cst_flow_viewer.js"></script>');
     document.write('<script type="text/javascript" src="'+ baseURL  +'cst_base64.js"></script>');
     document.write('<script type="text/javascript" src="'+ baseURL  +'cst_icon_selecter.js"></script>');
     document.write('<script type="text/javascript" src="'+ baseURL  +'cst_erecord.js"></script>');
     document.write('<script type="text/javascript" src="'+ baseURL  +'cst_print.js"></script>');
     
     $('#ico_tasks, #ico_message').hide();//隐藏页头的2个小图标
     $('#page_header_buttons').css('background-image', 'none');//隐藏页头的2个小图标前面的竖线
     //判断logo
	 $(function(){
		 
		 var loginType = poolSelect._LONGINTYPE || ydl.sessionData('loginType');
		 
		 if(loginType == 'person' || loginType == 'card' || loginType == 'account' ){
			 
			 //个人logo
			 //$('#page_header .logo img').attr('src', ydl.contexPath+'/image/logo1.png').css({'width':'auto'});
			 $('#page_header .logo img').addClass('logo_geren');
			 ydl.sessionData('loginType',loginType);
			 
		 }else if(loginType == 'auth' || loginType == 'cert' || loginType == 'temp' ){
			 
			 //单位logo
			 //$('#page_header .logo img').attr('src', ydl.contexPath+'/image/logo2.png').css({'width':'auto'});
			 $('#page_header .logo img').addClass('logo_danwei');
			 ydl.sessionData('loginType',loginType);
			 
		 }
		 
		 $('#page_header .logo img').css('display','block');
		 
	 });
     
    //根据项目需求重新设置的特殊字符校验
     ydl.validator.set('validchar', function(fieldValue, options){
     	return {result: !/['"\\\|~&$\^<>]/.test(fieldValue), message: '%f中不能含有以下字符！\n（单引号、双引号、反斜线、竖线、波浪线、＆符号、$符号、^符号、<符号、>符号）'};
     });
     
     //$(window).resize(searchWidthEdit).resize();
     
     // 日期控件不可以输入
     $('body').on('keydown','input.form-control.date',function(e){
    	    
		var keyCode = e.keyCode;
		
		//除回车与tab键之外禁止其他按键
		if(keyCode != '9' && keyCode != '13' ) return false;
		
	 });
     
     //校验显示方式
     ydl.validator.set('message.show', function ($field, messages, $messageHolder) {
 		$field.tips({
 			type: 'error',
 			tip: messages,
 			key: true
 		});
 	});
 	ydl.validator.set('message.clear', function ($field, $messageHolder) {
 		$field.tips('removeAll', 'error');
 	});
     
})(cst);
/*
function searchWidthEdit(){
	var $searchInfo = $('#search-info');
	var $window = $(window);
	if($window.width() <= 1170 && !$searchInfo.hasClass('search-info-sm')) {
		$searchInfo.addClass('search-info-sm');
	}else if($window.width() > 1170 && $searchInfo.hasClass('search-info-sm')){
		$searchInfo.removeClass('search-info-sm');
	}
}
*/
function addCustomCard(options){

	if(options.rows.length > 4){
		alert("卡片行数超过4行");
		return;
	}
		
	var rows = '';
	
	$.each(options.rows , function(k){
		
		//文字行
		if(this.type == 'textRow'){
			
			rows += '<div class = "cus-card-row cus-card-row-first "><label class="'+(this.isShow ? '':'hide')+'">'
					+this.label+'：'+'</label><span class="'+(this.isRed == true ? 'custom-card-text-red' : '')+ (this.isShow ? '':' hide ') +'">'+this.text+'</span></div>';
				
		//空行	
		}else if(this.type == 'nullRow'){
			
			rows += '<div class = "cus-card-row"></div>';
			
		//按钮行
		}else if(this.type == 'buttonRow'){
			
			var btnRow = '';
			
			$.each(this.btns , function(){
				
				btnRow += '<button type="button" class = "btn '+(this.isShow ? '':'hide')+' " id="'+this.id+'" ><span class="glyphicon glyphicon-play"></span>'
							+this.text+'</button>';
				
				var btn = this;
				
				$('#'+options.id).on('click',"#"+this.id,function(){
					btn.onClick();
				});
			});
			
			rows += '<div class = "cus-card-row">'+btnRow+'</div>';
			
		}
	
	});
	
	$('#'+options.id).addClass('custom-card').append('<div class="panel panel-default ydpx-container" ><img src="'+options.img+'" />'+rows+'</div>');

}
	//ydl.validator.setDefault({idcardlength: 18});

/*********************    以下是为了蚂蚁金融演示准备的，后期需要删除      *************************/
//把页签变成步骤形式
ydl.tabToStep = function () {
	$('#pageTabs>.nav').addClass('nav-steps').children('li').each(function (i) {
		$(this).children('a').prepend('<span>STEP' + (i + 1) + '</span>');
	});
	var $steps = $('#pageTabs>.nav-steps>li');
	var currentStep = $steps.filter('.active').index();
	var maxStep = $steps.length - 1;
	var $nextButton = $('<button type="button" id="b_prev_step" class="btn btn-primary"><span class="glyphicon glyphicon-arrow-right"></span>下一步</button>')
		.prependTo('#page_flow_buttons').click(function () {
			$steps.eq(currentStep + 1).children('a').tab('show');
		});
	var $prevButton = $('<button type="button" id="b_next_step" class="btn btn-primary"><span class="glyphicon glyphicon-arrow-left"></span>上一步</button>')
		.prependTo('#page_flow_buttons').click(function () {
			$steps.eq(currentStep - 1).children('a').tab('show');
		});
	var $submitButtons = $('#page_flow_buttons .submit-button').addClass('hide');
	$prevButton.hide();
	$steps.find('a').on('shown.bs.tab', function (e) {
		currentStep = $(e.target).parent().index();
		$prevButton[currentStep == 0 ? 'hide' : 'show']();
		$nextButton[currentStep == maxStep ? 'hide' : 'show']();
		$submitButtons[currentStep == maxStep ? 'removeClass' : 'addClass']('hide');
		window.scrollTo(0, 0);
	});
};





/*********************以下为内管自定义函数*************请勿覆盖*************************************************************************************/
var ywxt={};
(function(ywxt){
	/**
	 * 获取PF45数据字典
	 * @param {String|mulTypeId} 
	 * @returns jsonstr
	 */
	ywxt.CrtOptionByPf45 = function (pf45Id, pageObjId) {
		
			$('#'+pageObjId).empty();
			
			var options_default = ydl.createOptions({'': '请选择...'} );
			
			$('#'+pageObjId).append(options_default);
			
			ydl.ajax('demo/json/CrtMulData',{'multype':pf45Id},function(data){
			
				var options = ydl.createOptions(data,'itemval','itemid');	
	
	    		$('#'+pageObjId).append(options);

	    		$.each(poolSelect, function(key, value){
	    			if(key == pageObjId) ydl.setValue(key, value);
	    		});
	    		
	    		return data ;
				
			},{async:true}); 
		
            
	}

	/**
	 * 电子档案处理开始
	 */
	if (typeof ywxt.edoc === 'undefined') ywxt.edoc = {};
	var EDOC_PANEL_ID = 'edocPanel';
	var EDOC_OBJECT_ID = 'dev_MagCardDevice';
	var EDOC_URL="";
	/**
	 * 显示电子档案控件
	 * @param {Object} ywcs 要传给电子档案系统的业务参数
	 * @returns undefined
	 */
	ywxt.edoc.show = function(ywcs){
		var paras ={};
		if(ywcs!= undefined){
			if (ywcs instanceof Object ){
				paras = ywcs;
			}else{
				alert("初始化电子档案失败，传入参数【类型】有误");
				return ;
			}
		}
		
		var edoc_type = poolSelect['EDOC_TYPE'];
		if(edoc_type != undefined && edoc_type !=""){
			var $edocPanel = $('#' + EDOC_PANEL_ID);
		    if ($edocPanel.length > 0) $edocPanel.show();
		    else{
		    	 
		    	 var docjson = {};
		    	 if(poolSelect['EDOC_SMLSH'] ==undefined || poolSelect['EDOC_SMLSH'] == "" ){
		    		 alert("获取电子档案【扫描流水号失败】");
		    		 return ;
		    	 }
		    	 paras.edoc_smlsh = poolSelect['EDOC_SMLSH'];
		    	 paras.edoc_type = poolSelect['EDOC_TYPE'];
		    		
	    		ydl.ajax(ydl.contexPath + '/edocCon/task/get_edoc_info', paras, function(data){ 
	    			
	    			//alert(JSON.stringify(data.data))
	    			if(data.data.tree && data.data.tree.length>0){
	    				init();
	    			    var edoc = document.getElementById(EDOC_OBJECT_ID);
	    				edoc.Smlsh =poolSelect['EDOC_SMLSH'];
		    			edoc.url= data.edocUrl;
		    			EDOC_URL = data.edocUrl;
		    			docjson.data=data.data;
	    				edoc.LoadTree(JSON.stringify(docjson) )
	    					
	    			}else{
	    				//加载电子档案控件失败
	    				alert("加载电子档案控件失败，请联系系统管理员");
	    			}
	    			
	    		});
		    } 
		}
	};
})(ywxt);

// 可定制修改动态列表按钮描述,可在custom.js中使用 20180627
//默认按钮描述
/*
{
    'datalist.buttons.add': '添加',
    'datalist.buttons.delete': '删除',
    'datalist.buttons.delall': '清空',
    'datalist.buttons.save': '保存',
    'datalist.buttons.refresh': '刷新',
    'datalist.buttons.import': '导入',
    'datalist.buttons.export': '导出',
    'datalist.buttons.print': '打印',
    'datalist.buttons.batch.basic': '原始数据',
    'datalist.buttons.batch.error': '批量错误',
    'datalist.buttons.batch.export': '导出错误数据'
}*/
ydl.textResources.set('datalist.buttons.add', '录入');

