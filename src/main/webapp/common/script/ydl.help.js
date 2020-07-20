/******************************************\
 * Description: 页面帮助向导功能           *  
 * Author: yushanshan						  *
 * Date: 2017-09-29						  *
\******************************************/
'use strict';
if(typeof ydl === "undefined") alert("ydl.help.js必须在ydl.base.js之后加载");

(function(){
	
	/**
	 * 页面帮助向导
	 * @param {Array} steps 引导提示信息[{
		* @param {String} steps.element 这个属性类似于jquery的选择器,可以通过jquery选择器的方式来选择你需要选中的对象进行指引
		* @param {String} steps.intro 这里是每个引导框具体的文字内容，中间可以编写HTML代码
		* @param {String} steps.intro 这里可以规定引导框相对于选中对象出现的位置 top, bottom, left, right
	 * }, ...]
	 */
	ydl.help = function(steps){
		//初始化组件
		var intro = introJs();
		
		//配置信息
		var settings = {
            prevLabel: '上一步', //上一步按钮
            nextLabel: '下一步', //下一步按钮
            skipLabel: '退出', //跳过按钮
            doneLabel: '退出', //结束按钮
            showStepNumbers: false, //是否显示步骤数
            showBullets: false, //是否显示导航条
            steps: steps //对应的数组，顺序出现每一步引导提示
		}
		
		//设置配置并显示第一步
    	var $startThis = intro.setOptions(settings).start();
    	
    	//如果当前步骤为隐藏状态则跳过
    	$.each($startThis._introItems, function(i, item) {
    		if($(item.element).is(':hidden')) {
    			intro.nextStep();
    		} 
    		else {
    			return false;
    		}
    	});
        
    	//取消absolute
        $('.introjs-fixParent').css('cssText', 'position: static !important;');
		
		//步骤切换前执行
		intro.onbeforechange(function() {
			//当前对象
			var $this = this;
			//如果该步骤处于隐藏状态则跳过
			if(($this._introItems.length > $this._currentStep) && $($this._introItems[$this._currentStep].element).is(':hidden')) {
				if($this._direction == 'backward') { //上一步
					//如果为第一步且不可见则退出
					$this._currentStep == 0 ? intro.exit() : intro.previousStep();
				}
				else if($this._direction == 'forward') { //下一步
					intro.nextStep();
				}
			} 
		});	 
	}
	
})();


