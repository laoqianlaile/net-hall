/*********************\ 
 * Description: 电子档案前端处理 
 * Author: zhc 
 * Date: 2017-09-29 * \
 * v0.1  2019-08-15  许永峰修改 解决档案3.0系统在申报业务提交报错后无法再次编辑档案界面问题 #2097
 ************************/
'use strict';

var erecord = {};

(function(){
	// 上传图片大小最大值 1MB
	var sizeMax = 9;
	// 长传图片大小最小值 0KB
	var sizeMin = 0;
	// 图片异步加载延迟时间
	var imgLoadTime = 1000;
	/**
	 *  新版电子档案
	 * type="NZR"
	 */
	var options_NZR={
		rw:"w" , // 读写标志
		type:"NZR", // 调用电子档案的标志NZR 新版电子档案  ODZ PKG版电子档案
		ywflbm:"043101", // 业务分类编码
        isFinish:"",// 是否一步办结的业务：0-否，1-是，非必传，缺省为0
	}
	/**
	 * PKG版电子档案初始化参数
	 * type="ODZ"
	 */
	var options_ODZ={
		rw:"w" , // 读写标志
		type:"ODZ", // 调用电子档案的标志NZR 新版电子档案  ODZ PKG版电子档案
		scanKey:"0",// 扫描关键字
		flowid:"415",// 业务系统对应的流程ID
		stepid:"3",// 业务系统对应的节点ID
		isRepeat:"",// 是否复用 0-否；1-是
	}
/**
 * 初始化上传电子档案模块
 * 
 * @param {Object}
 */
erecord.initErecord = function(options, checkedObj) {
	// 通过调用标志判断定义调用的后台的参数
	var params={};
	params["_POOLKEY"]=poolSelect["_POOLKEY"];
	if("NZR" == options.type){
		// NZR 新版电子档案
		params["ywflbm"]=options.ywflbm;
		params["bcfs"]=0;
        //2019-3-21  当第二个参数存在时并且不为空时  将参数放到xstjdata里传递给后台 start
        if (checkedObj&& !(checkedObj == 'null')) {
            checkedObj = JSON.stringify(checkedObj)
            if (checkedObj)params['xstjdata'] = checkedObj;
        }
        ///2019-3-21  当第二个参数存在时并且不为空时  将参数放到xstjdata里传递给后台 end
	}else if("ODZ" == options.type){
		// ODZ PKG版电子档案
		// 上传的电子档案是否复用，若复用上传扫描key,若不复用扫描key为空
		if(options.isRepeat == "1"){
			params["scanKey"]=options.scanKey; // 扫描关键字
		}else{
			params["scanKey"]=""; // 扫描关键字
		}
		params["flowid"]=options.flowid; // 业务系统对应的流程ID
		params["stepid"]=options.stepid; // 业务系统对应的节点ID
        //2019-3-21 start 当第二个参数存在时并且不为空时 将参数放到xstjdata里传递给后台
        if (checkedObj&& !(checkedObj == 'null')) {
            checkedObj = JSON.stringify(checkedObj)
            if (checkedObj)params['xstjdata'] = checkedObj;
        }
        //2019-3-21 end 当第二个参数存在时并且不为空时 将参数放到xstjdata里传递给后台
	}else{
		ydl.alert({
			'icon' : 'info',
			'message' : '获取业务材料信息失败',
			'desc' : '不支持该类型的业务材料！'
		});
		return false;
	};
	ydl.ajax(ydl.contexPath + "/erecord/scanning/beforehand", params, function(data) {
        // 2019年3月1日 修改返回字段，新版电子档案增加扫描流水号放至总线中 start
        if("NZR" == options.type){
            ydl.attribute.set("_smlsh",data.smlsh);
        }
        options["oriData"] = JSON.parse(data.materiallist);
        // v0.1  孙一宁 2019/08/15 新增start
        options["smlsh"] = data.smlsh;
        //  孙一宁 2019/08/15 新增end
        // options["oriData"] = data;
        // end
		initErecord(options);
	});
}
/**
 * 流程提交前校验，如果无图片则只校验是否必填，有图片则校验图片数是否符合指定范围
 * 
 * @param {boolean}
 * isAlert 是否以ydl.alert()的形式提示校验结果，如果不显示则至返回校验结果，默认显示
 * options 判断影像校验通过是够提交，并提供提交参数
 */
erecord.valSubmitRequired = function(isAlert,options){
	if(isAlert == undefined) isAlert = true;
	var isSubmit =false;
	if(options != undefined){
	   isSubmit = true;
	};
	var result = {'result': true , 'info': []};
	// 电子档案加载标志
	var submitflag = false;
	$('#e_record .nav-tabs li a').each(function(){
		if(!submitflag){
			submitflag = true;
		}
		var $this = $(this);
		var thisname = $this.find('span:not(.badge)').text();
		var sfbs = $this.data('sfbs');
		var thisId = $this.attr('href');
		var thisLength = $(thisId+' li').length;
		if(thisLength == 0){
			// 如果当前项目无图片，校验是否为必填
			if(sfbs){
				// 如果是必填则记录校验未通过信息
				if(result['result']) result['result'] = false;
				result['info'].push({
					'text':'"'+thisname+'"必须上传档案。',
					'id' : thisId
				});
			}else{
				// 如果是非必填则直接校验下一组
				return true;
			}
		}else{
			// 如果当前项目有图片，校验是否符合图片最大最小数目
			var thisResult = erecord.valErecord(thisId.substring(1));
			if(thisResult.result){
				// 符合图片最大最小数目继续校验下一组
				return true;
			}else{
				// 不符合图片最大最小数目则记录校验未通过信息
				var thisInfo = thisResult['info'][0];
				if(result['result']) result['result'] = false;
				result['info'].push({
					'text' : thisInfo.text,
					'zdsmfs' : thisInfo.zdsmfs,
					'zxsmfs' : thisInfo.zxsmfs,
					'filesNum' : thisInfo.filesNum,
					'id' : thisId
				});
			}
		}
	});
	// 校验电子档案是否加载
	if(submitflag == false){
		if(result['result']) result['result'] = false;
		ydl.alert({
			'icon' : 'info',
			'message' : '提交失败',
			'desc' : '未上传业务材料无法提交！'
		});
		return result;
	}
	// 校验通过，数据提交，调用提交接口
	if(isSubmit == true && result['result']){
		var save = erecord.valSave(options,true);
		if(result['result'] && !save) {
			result['result'] = false;
			result['info'].push({
				'text' : "上传电子档案材料失败，请刷新重试！"
			});
		}

	}
	// 如果没通过校验，提示校验结果
	if(isAlert == true && !result['result']){
		var infoText = '';
		$.each(result['info'] , function(){
			infoText += '<br/>　　'+this.text;
		});
		ydl.alert({
			'icon' : 'info',
			'message' : '校验未通过。',
			'desc' : infoText
		});
	}
	return result;
}
/**
 * 提交图片调用电子档案提交接口
 * @param {Object}
 *            options 保存参数
 * @param {boolean}
 *            isSubmit 是否提交保存
 */
erecord.valSave = function(options,isSubmit){
	// 所有图片的路径信息
	var allFilePath = "";
	var returnFlag = false;
	$("#e_record .tab-content li").each(function(){
		var fileId=$(this).data('fid');
		allFilePath = allFilePath+fileId+",";
	});
	var params={};
	params["_POOLKEY"]=poolSelect["_POOLKEY"];
	// 20190319 定义保存接口url
	var url = "";
	if("NZR" == options.type){
		// NZR 新版电子档案
		params["ywflbm"]=options.ywflbm;
		params["bcfs"]=0;
		// 20190325 一步办结业务调用保存接口，申报业务调用暂存接口
        if("1" == options.isFinish){
            url = "/erecord/scanning/save";
		}else{
            // 20190319 新电子档案调用暂存接口
            url = "/erecord/scanning/storage";
		}
	}else if("ODZ" == options.type){
		// ODZ PKG版电子档案
		params["scanKey"]=options.scanKey; // 扫描关键字
		params["flowid"]=options.flowid; // 业务系统对应的流程ID
		params["stepid"]=options.stepid; // 业务系统对应的节点ID
		// 暂存是0提交是1，默认是暂存
		params["savetype"]=isSubmit?"1":"0";
        // 20190319 PKG电子档案调用保存接口
        url = "/erecord/scanning/save"
	}else{
		ydl.alert({
			'icon' : 'info',
			'message' : '获取业务材料信息失败',
			'desc' : '不支持该类型的业务材料！'
		});
		return false;
	};
	if(allFilePath!=""){
		params["filePath"]=allFilePath.substring(0,allFilePath.length-1);
		// 页面存在图片则调用提交接口
		$.ajax({
			type: "POST",
			async:false,
			dataType: "json",
			// 20190319 注释以下代码
			// url: ydl.contexPath + "/erecord/scanning/save",
            url: ydl.contexPath + url,
            data: params,
			success: function (result) {
				if(result && result.returnCode!=null && result.returnCode =='0'){
            		console.log("电子档案，提交成功");
            		returnFlag=true;
            	}else{
            		console.log(result);
            	}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				// 状态码
				console.log(XMLHttpRequest.status);
				// 错误信息   
				console.log("电子档案，提交失败");
			}
		});
	}
	// 2019-05-16 若不需要上传图片，返回标识设为true
	else{
        returnFlag = true;
	}
	return returnFlag;
}
/**
 * 校验图片最大最小数目
 * 
 * @param {String|Object}
 *            tabIds 需要校验的电子档案id，默认不写则全部校验
 * @param {boolean}
 *            info 是否显示详细校验结果，如不显示则直接返回校验结果true或false，默认显示
 */
erecord.valErecord = function(tabId , info){
	
	info = info == false ? false : true ;
	var result = info ? {'result' : true , 'info' : []} : true ;
	
	var $allTab;
	// 校验指定id的电子档案
	if(tabId){
		$allTab = $('#e_record .tab-content #'+tabId);
	}else{
		// 校验所有的电子档案
		$allTab = $('#e_record .tab-content .tab-pane');
	}

	$allTab.each(function(){
		// 当前电子档案
		var $this = $(this);
		// 最大扫描份数
		var zdsmfs = $this.data('zdsmfs');
		// 最小扫描份数
		var zxsmfs = $this.data('zxsmfs');
		
		// 如果无限制则直接校验下一组
		if( zdsmfs == 0 && zxsmfs == 0 ) return true;
		
		// 当前总共图片数
		var filesNum = $this.find('.eFileSl').length;
		// 当前最大扫描份数校验信息
		var zdInfo = '';
		// 当前最小扫描份数校验信息
		var zxInfo = '';
		
		// 判断上传图片数是否超过最大扫描份数
		if( (zdsmfs == 0 ? false : ( filesNum > zdsmfs) ) ){
			// 是否显示详细信息
			if(info){
				// 显示详细信息，更改校验结果，记录信息
				if(result['result']) result['result'] = false;
				zdInfo = '最多上传 ' + zdsmfs + ' 个图片。';
			}else{
				// 不显示，直接返回结果false，结束校验
				if(result) result = false;
				return false;
			}
		}
		// 判断上传图片数是否超过最小扫描份数
		if( (zxsmfs == 0 ? false : ( filesNum < zxsmfs) ) ){
			// 是否显示详细信息
			if(info){
				// 显示详细信息，更改校验结果，记录信息
				if(result['result']) result['result'] = false;
				zxInfo = '至少上传 ' + zxsmfs + ' 个图片';
			}else{
				// 不显示，直接返回结果false，结束校验
				if(result) result = false;
				return false;
			}
		}
		
		// 统计当前最终的校验结果
		if(info && !result['result']){
			// 显示当前名称
			var filesParName = $('#e_record a[href="#' + $this.prop('id') + '"] span:not(.badge)').text();
			result['info'].push({
							'text': '"'+filesParName+'"'+zxInfo+(zxInfo ? ( zdInfo ? '，' : '。' ) : '')+zdInfo+'当前已上传图片数为 '+filesNum+' 个。' ,
							'zdsmfs': zdsmfs ,
							'zxsmfs': zxsmfs ,
							'filesNum': filesNum ,
							'id':  $this.prop('id')
						});
		}else if(tabId && info){
			// 如果是单个id校验并显示校验信息，则无论是否通过均返回部分校验信息
			result['info'].push({ 'zdsmfs': zdsmfs , 'zxsmfs': zxsmfs , 'filesNum': filesNum , 'id' : $this.prop('id') });
		}
		
	});
	
	// 返回校验结果
	return result;
}
/**
 * 初始化上传电子档案模块主方法
 * 
 * @param {Object}
 *            options 相关参数，包括下面这些对象等
 * @param {String}
 *            [options.oriData] 接口获得的原始数据
 * @param {Boolean}
 *            [options.rw] 是否只读
 * @param {String}
 *            [options.ywflbm] String 业务分类编码
 */
function initErecord(options, checkedObj) {
	
	// 判断是否为ie8浏览器
	var isIE8 = /MSIE 8\.\d/.test(navigator.userAgent);
	var isEdit = options.rw == "w" ? true : false;		// 是否可编辑
	var oriData = options.oriData;						// 处理前的原始材料数据
	var clData = [];									// 整理后的材料数据
	
	// 如果是ie8则重新设置上传按钮大小
	if(isIE8) $('#eFile').width(40);
	
	// 整理出需要显示的所有数据
	getCl(oriData.tree);
	
	// 显示刚刚上传电子档案
	var createFile = function (vf) {
		// 图片ID
		var tpid= vf.tpid;
		// 图片名称
		var ytpmc = vf.ytpmc;
		return '<li data-fid="' + tpid +
			'">' + (isEdit ? '<button type="button" class="close" title="删除此业务材料"><span>&times;</span></button>' : '') +
			'<div class="eFileSl"><img src="'+ydl.contexPath +'/erecord/imageCapture/downloadFyImg?tpid=' +
			vf.tpid + '&_POOLKEY=' + poolSelect["_POOLKEY"] + '" title="' + ytpmc +
			'"/></div><p><span data-original="' + ytpmc + '">' + ytpmc + '</span></p></li>';
	};
	
	// 显示列表中已存在的电子档案
	var createListFile = function (vf) {
		// 图片ID
		var tpid= vf.tpid;
		// 图片名称
		var ytpmc = vf.ytpmc;
		
		return '<li data-fid="' + tpid +
			'">' + (isEdit ? '<button type="button" class="close" title="删除此业务材料"><span>&times;</span></button>' : '') +
			'<div class="eFileSl"><img src="'+ydl.contexPath+'/common/image/load-list.gif" data-read="false" title="' + ytpmc +'" id="' + tpid +
			'"/></div><p><span data-original="' + ytpmc + '">' + ytpmc + '</span></p></li>';
	};

    $('#e_record .nav-tabs').html('');
    $('#e_record .tab-content').html('')
	
	// 生成主要结构
	lm(clData);
	var navTabHtml = '';
	var navTabNnm = 0;
	var contentHtml = '';
	$.each(clData,function(k,v){
		var clInfo = v;
		var cl = clInfo.cl;
		if(cl && cl.length > 0){
			$.each(cl , function(key,val){
//2019-3-21 判断显示条件  显示条件为1和为空时显示内容 为0时不显示内容
                if (val.xstj == true || (val.xstj) == "") {
                    // 左侧页签结构
                    navTabHtml += '<li' + (navTabNnm === 0 ? ' class="active"' : '') + '><a data-toggle="tab" data-clbm="' +
                        val.clbm + '" data-jdbm="' + clInfo.jdbm + '" data-sfbs="' + val.sfbs +
                        '" href="#e_record_item_' + navTabNnm + '"><span>' +
                        (navTabNnm + 1) + '. ' + val.clmc + '</span>' + (val.sfbs == 1 ? '<em>*</em>' : '' ) + '<span class="badge ' +
                        (isIE8 ? (val.imageInfo.length == 0 ? 'hidden' : '') : '') + '">' +
                        (val.imageInfo.length || '') + '</span></a></li>';

                    // 右侧具体内容结构
                    contentHtml += '<div id="e_record_item_' + navTabNnm +
                        '" data-clbm="' + val.clbm + '" data-jdbm="' + clInfo.jdbm +
                        '" data-sffy="' + val.sffy + '" data-zdsmfs="' + val.zdsmfs +
                        '" data-zxsmfs="' + val.zxsmfs + '" class="tab-pane' +
                        (navTabNnm === 0 ? ' active' : '') + '"><ul>' +
                        $.map(val.imageInfo, createListFile).join('') +
                        '</ul></div>';

                    navTabNnm++;
                }

                //2019-3-21 添加判断 如果显示的内容为空则将添加文件按钮设置成不可点击 start
                if (navTabNnm == 0) {
                    $("#eFile").attr({"disabled": "disabled"});
                } else if ($("#eFile").prop("disabled")==true) {
                    $("#eFile").removeAttr("disabled");
                }
                //2019-3-21 添加判断 如果显示的内容为空则将添加文件按钮设置成不可点击 end
			});
		}
	});

	// 整理出需要显示的所有数据
	function getCl(jdData){
		$.each(jdData , function(){
			// 如当前分支没有子分支或者上传材料直接查询下一分支
			if(!this.cl && !this.dir) return true;
			if(this.cl){
				clData.push({'jdbm':this.jdbm , 'sxh':this.sxh , 'cl':this.cl});
			}
			if(this.dir){
				getCl(this.dir);
			}
		});
	}
	// 获取并显示当前显示的业务材料信息
	var $eFileBase = $('#eFileBase');
	if(isEdit){
		// 显示图片上传按钮区
		$eFileBase.removeClass('hidden');
	}
	function eFileInfo(){
		// 如果只读则不做任何处理
		if(!isEdit) return;
		$('#eFileText').html('图片大小不能超过'+sizeMax+'MB');
		var $contentActive = $('#e_record .tab-content .active');
		// 如果没有任何数据，则不做处理
		if ($contentActive.length == 0) return;
		// 当前名称
		var eFileName = $('#e_record a[href="#' + $contentActive.prop('id') + '"] span:not(.badge)').text();
		// 最大上传份数
		var eFileMax= $contentActive.data('zdsmfs');
		// 最小上传份数
		var eFileMin = $contentActive.data('zxsmfs');
		
		var html=( eFileMin == 0 && eFileMax == 0 ) ? '图片大小不能超过'+sizeMax+'MB' : ( '"<span id="eFileName" >'+eFileName+'</span>"' +
					( eFileMin == 0 ? '' : '至少上传 <span id="eFileMin" >'+eFileMin+'</span> 个图片' ) + ( eFileMax == 0 ? '。' : (eFileMin == 0 ? '' : '，') ) ) +
					( eFileMax == 0 ? '' : '最多上传 <span id="eFileMax" >'+eFileMax+'</span> 个图片。' ) + '图片大小不能超过'+sizeMax+'MB';
		
		$('#eFileText').html(html);
		
	}
	// 设置当前显示分支的信息
	eFileInfo();
	
	// 显示整个业务材料组件
	var $widget = $('#e_record').removeClass('hidden');
	
	// 生成上传表单
	var $e_record_form = $('<form id="e_record_form" action="" enctype="multipart/form-data" method="post" class=""></form>');
	if(isIE8){
		// 如果是ie8则增加一堆特殊处理
		$e_record_form.css({'display':'none'}).appendTo('#page_main');
	}else{
		var $e_record_form = $('<form id="e_record_form" action="" enctype="multipart/form-data" method="post" class=""></form>');
	}
	
	// 选择图片后自动提交
	$('#e_record').unbind("click").on('click','#eFile',function(){
		// 如果已达到最大上传图片数，则不进行图片提交
		var $contentActive = $('#e_record .tab-content .active');
		var valRes = erecord.valErecord($contentActive.prop('id'));
		lm(valRes);
		var fliesParName = $('#e_record a[href="#' + $contentActive.prop('id') + '"] span:not(.badge)').text();
		if( valRes['info'][0] && (valRes['info'][0].filesNum >= valRes['info'][0].zdsmfs) ){
			ydl.alert({'message':'上传失败','desc':'"'+fliesParName+'"最多上传 '+valRes['info'][0].zdsmfs+' 个图片。'});
			return false;
		}
	});
	$('#e_record').unbind("change").on('change','#eFile',function () {

		// 校验上传的图片格式只能为JPG或JPEG
		var filepath = $(this).val();
		var extStart = filepath.lastIndexOf(".");
		var ext = filepath.substring(extStart, filepath.length).toUpperCase();
		if (ext != ".JPG" && ext != ".JPEG") {
			ydl.alert("图片限于jpeg,jpg格式");
			return false;
		} 
		// 前端校验图片大小(非IE)
		if(!isIE8){
			var dom = document.getElementById("eFile");
			var fileSize = dom.files[0].size;
			if(fileSize>sizeMax*1024*1024){
				ydl.alert("图片大小不能大于"+sizeMax+"MB");
				return false;
			}
			if(fileSize<=sizeMin*1024){
				ydl.alert("图片大小应大于"+sizeMin+"KB");
				return false;
			}
		}
		var $contentActive = $('#e_record .tab-content .active');
		
		// 显示遮罩层
		$RecCover.removeClass('hidden');
		// 获取input并将其放入表单
		var $thisInput = $(this);
		$e_record_form.append($thisInput);
		
		// 上传接口地址
		var postUrl=ydl.contexPath + '/erecord/imageCapture/uploadImg';
		
		// 上传图片名称
		//var filename = $(this).val().replace(/[^\\\/]*[\\\/]+/g,'');
		// 重新生成图片名
		var myDate = new Date();
		// 材料编码+当前时间毫秒+后缀
		var filename = $contentActive.data('clbm')+"_"+formatDate()+ext;
		
		// 左侧对应的标签
		var $badge = $('#e_record a[href="#' + $contentActive.prop('id') + '"] .badge');
		
		// 提交参数
		if(poolSelect){
		    if(poolSelect["_POOLKEY"]){
		        postUrl+="?_POOLKEY="+poolSelect["_POOLKEY"];
		        // 判断上传类型
			    if("NZR" == options.type){
					// NZR 新版业务材料
			    	 // postUrl+="&sxh=1"; //顺序号后台自动生成
					postUrl+="&ywflbm="+options.ywflbm;		// 业务分类编码
			        postUrl+="&smlx="+oriData.smlx;		// 扫描类型
			        postUrl+="&clbm="+$contentActive.data('clbm');		// 材料编码
			        postUrl+="&smsjdbm="+$contentActive.data('jdbm');	// 所在树节点编码
			        postUrl+="&sffy="+$contentActive.data('sffy');		// 是否复用
			        postUrl+="&zdsmfs="+$contentActive.data('zdsmfs');	// 最大扫描份数
			        postUrl+="&zxsmfs="+$contentActive.data('zxsmfs');	// 最小扫描份数
			        postUrl+="&filename="+filename;		// 上传图片名
				}else if("ODZ" == options.type){
					// ODZ PKG版业务材料
			        postUrl+="&clbm="+$contentActive.data('clbm');		// 材料编码
			        postUrl+="&flowid="+options.flowid;	// 业务系统对应的流程ID
			        postUrl+="&stepid="+options.stepid;	// 业务系统对应的节点ID
			        postUrl+="&filename="+filename;		// 上传图片名
				}else{
					ydl.alert({
						'icon' : 'info',
						'message' : '上传业务材料信息失败',
						'desc' : '不支持该类型的业务材料！'
					});
					return false;
				};
		       
		    }else return;
		}else return;
		
		$e_record_form.prop('action', postUrl);
		$e_record_form.ajaxSubmit({
        	dataType: 'json',
        	async: true,
        	success: function (ret) {
        
				if (ret.returnCode == '0') {
					
					// 上传成功，显示已添加图片
					$contentActive.find('ul').append(createFile({'tpid':ret.data , 'ytpmc':filename}));
					// 更新图片总数
					$badge.text($contentActive.find('li').length);
					if(isIE8){
						$badge.removeClass('hidden');
					}
					$e_record_form[0].reset();
					resize();
					var $eTabContent = $('#e_record .tab-content');
					$eTabContent.scrollTop($eTabContent.height);
				}
				else {
					ydl.alert('上传业务材料出错：' + ret.message);
				}
				$eFileBase.append($thisInput);
				$RecCover.addClass('hidden');
				$e_record_form[0].reset();
			},
			error: function (ret) {
				ydl.alert('上传业务材料出错：' + ret.message);
				$eFileBase.append($thisInput);
				$RecCover.addClass('hidden');
				$e_record_form[0].reset();
			}
		});
		
	});
	
	// 删除附件图片
	$('#e_record .tab-content').unbind("click").on('click', 'button', function () {
		
		if (confirm('请确认是否要删除此附件图片？')) {
			$RecCover.removeClass('hidden');
			var $li = $(this).closest('li');
			var itemid = $li.closest('.tab-pane').prop('id');
			var fid = $li.data('fid');
			// 删除接口地址
			var delUrl=ydl.contexPath + '/erecord/imageCapture/imageCaptureDelete';
			if(poolSelect["_POOLKEY"]){
				delUrl+="?_POOLKEY="+poolSelect["_POOLKEY"];
		        // 判断上传类型
			    if("NZR" == options.type){
					// NZR 新版业务材料
			    	delUrl+="&tpid="+fid;	// 图片id
				}else if("ODZ" == options.type){
					// ODZ PKG版业务材料
					var $contentActive = $('#e_record .tab-content .active');
					delUrl+="&tpid="+fid;	// 图片id
					delUrl+="&clbm="+$contentActive.data('clbm');	// 材料编码
					delUrl+="&scanKey="+options.scanKey;	// 扫描关键字
					delUrl+="&flowid="+options.flowid;	// 业务系统对应的流程ID
					delUrl+="&stepid="+options.stepid;	// 业务系统对应的节点ID
				}else{
					ydl.alert({
						'icon' : 'info',
						'message' : '删除业务材料出错',
						'desc' : '不支持该类型的业务材料！'
					});
					return false;
				};
			}else {
		    	ydl.alert({
					'icon' : 'info',
					'message' : '删除业务材料出错',
					'desc' : '请刷新重试！'
				});
		    	return false;
		    };
			$.ajax(delUrl, {
	        	dataType: 'json',
	        	async: true,
	        	success: function (ret) {
	        
					if (ret.returnCode == '0') {
						
						// 左侧对应的标签
						var $contentActive = $('#e_record .tab-content .active');
						var $badge = $('#e_record a[href="#' + $contentActive.prop('id') + '"] .badge');
						
						// 移除页面元素
						$li.remove();
						// 更新图片总数
						$badge.text( ($contentActive.find('li').length || '' ) );
						if(isIE8){
							if($contentActive.find('li').length == 0) $badge.addClass('hidden');
						}
						$e_record_form[0].reset();
						resize();

					}
					else {
						ydl.alert('删除业务材料出错：' + ret.message);
						
					}
					$RecCover.addClass('hidden');
				},
				error: function (ret) {
					ydl.alert('删除业务材料出错：' + ret.message);
					$RecCover.addClass('hidden');
				}
			});
			
		}
		return false;
	})

	
	// 框架的整体遮罩层对象
	var $RecCover = $('#eRecCover');
	// 点击后的小图图片外层div对象
	var $imgYLClick;
	// 大图图片对象
	var $img;
	// 大图遮罩对象
	var $imgCover;
	// 大图弹出框主体部分对象
	var $e_rec_modalBody;
	// 大图旋转
	function bigImgRot(fagnxiang){
		var classNum = parseInt($img.attr('class').match(/transform(\d)/)[1]);
		if(classNum == (fagnxiang == 'left' ? 1 : 4) ) classNum = (fagnxiang == 'left' ? 4 : 1);
		else classNum = (fagnxiang == 'left' ? classNum-1 : classNum+1);
		// 旋转图片
		$img.removeClass().addClass('transform'+classNum);
	}
	// 大图缩放
	function bigImgSF(SF){
		var oldHeight = parseInt($img.width());
		// $img.height('auto').stop(true).animate({width: oldHeight * (SF == 'F'
		// ? 1.3 : 0.7) }, 500);
		$img.height('auto').width( oldHeight *  (SF == 'F' ? 1.3 : 0.7) );
	}
	// 大图恢复原始状态
	function bigImgYS($image){
		var $modalBody = $e_rec_modalBody;
		var modalWidth =  $modalBody.width();
		var modalHeight = $modalBody.height();
		$image.height('auto').width('auto');
		var imageWidth = $image.width();
		var imageHeight = $image.height();
		if( imageWidth < modalWidth && imageHeight < modalWidth ){
			// 小于弹出框尺寸的图片居中处理
			$image.css({'top':(modalHeight - imageHeight)/2,'left':'auto'});
		}else{
			// 超宽或超高图片
			if( (imageWidth - modalWidth) >= (imageHeight - modalHeight) ){
				$image.width('100%').css({'top':(modalHeight - $image.height())/2,'left':'auto'});;
			}else{
				$image.height('100%').css({'top':'auto','left':'auto'});
			}
		}
		$image.removeClass($img.attr('class').match(/transform(\d)/)[0]).addClass('transform1');
	}
	
	// 生成大图对话框
	var $showBigImg = $('').dialog({
		'close' : true,
		'size'	: 'lg',
		'id'	: 'e_record_modal',
		'buttons':{
			'向左旋转':function(){
						bigImgRot('left');
					},
			'向右旋转':function(){
						bigImgRot('right');
					},
			'放大':function(){
						bigImgSF('F');
					},
			'缩小':function(){
						bigImgSF('S');
					},
			'下载':function(){
					},
			'恢复':function(){
						bigImgYS($img);
					}
			/*
			 * , '恢复原始位置':function(){
			 * $img.css({'top':'auto','left':'auto'}).removeClass($img.attr('class').match(/transform(\d)/)[0]).addClass('transform1'); }
			 */
		},
		'create': function(){
					// 按钮样式预处理
					var buttons = {
							'向左旋转' : 'eButImgLeft',
							'向右旋转' : 'eButImgRight',
							'放大' : 'eButImgBig',
							'缩小' : 'eButImgSmall',
							'下载' : 'eButImgDownload'
					};
					var $buttons = $('#e_record_modal .modal-footer button');
					// 预处理按钮样式
					$buttons.each(function(){
						var $this = $(this);
						var thisText = $this.text()
						if(buttons[thisText]){
							$this.text('').addClass('eButImg '+buttons[thisText]).attr('title',thisText);
							// 预处理下载按钮
							if(thisText == '下载'){
								$this.wrap('<a id="eDownloadImg" href=""></a>');
							}
						}
					});
					// 创建并添加遮罩层
					$imgCover = $('<div id="eBigImgCover" ></div>');
					$e_rec_modalBody = $('#e_record_modal .modal-body');
					$e_rec_modalBody.after($imgCover);
		},
		'show' : function(){
					// 设置弹出框高度
					$e_rec_modalBody.height($(window).height()-200);
		},
		'shown'	: function(){
					// 支持拖拽
					// $img =
					// $('#e_record_bigImg').draggable();
					$img = $e_rec_modalBody.find('img').draggable();
					// 重置大图位置
					bigImgYS($img);
					// 隐藏遮罩层
					$imgCover.addClass('hidden');
		},
		'hidden' : function(){
					// 删除复制的大图
					$img.remove();
					// 显示遮罩层
					$imgCover.removeClass('hidden');
		}
	});
	// 生成内容并绑定点击缩略图弹出大图事件
	$('#e_record .nav-tabs').append(navTabHtml);
	$('#e_record .tab-content').append(contentHtml).on('click','li > div' , function(){
		$imgYLClick = $(this)
		var $YLimg = $imgYLClick.find('img');
		// $('#e_record_bigImg').attr('src' , $YLimg.attr('src'));
		// 将图片复制进大图区域
		$e_rec_modalBody.append($YLimg.clone());
		$e_rec_modalBody.find('img').addClass('transform1')
		// 设置下载链接
		$('#eDownloadImg').prop('href', $YLimg.prop('src') + '&isDownLoad=1&tpmc='+$imgYLClick.next('p').find('span').data('original'));
		$showBigImg.dialog('option','title', $YLimg.attr('title'));
		$showBigImg.dialog('open');
	});
	// 调整元素高度
	// $('#e_record
	// .nav-tabs').append('<li><a>aaa</a></li><li><a>aaa</a></li><li><a>aaa</a></li><li><a>aaa</a></li><li><a>aaa</a></li><li><a>aaa</a></li><li><a>aaa</a></li><li><a>aaa</a></li><li><a>aaa</a></li><li><a>aaa</a></li><li><a>aaa</a></li>');
	
	var leftHeight = $('#e_record .nav-tabs').height();
	var $rightBase = $('#e_record .tab-content');
	var basicHeight = parseFloat($rightBase.css('max-height'));
	// 如果左侧菜单超过右侧菜单高度最大值，则重新计算右侧菜单高度
	if(leftHeight > basicHeight){
		var filesHeight = 228;
		$rightBase.css({ 'max-height' : basicHeight + Math.ceil( (leftHeight-basicHeight)/filesHeight )*filesHeight })
	}
	resize();
	function resize() {	
		$('#e_record .nav-tabs').height('auto').height($('#e_record .items').height());
	}
	// 初始化后的回调
	$widget.trigger('load');
	
	// 切换tab时设置当前显示分支的信息，同时计算整体高度
	$('#e_record ul a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
		eFileInfo();
		resize();
		syncLoadImg();
	});
	syncLoadImg();
	/**
	 * 异步加载图片
	 */
	var i ,array;
	function syncLoadImg() {
		// 数量清0
		i = 0;
		array = [];
		var $contentActive = $('#e_record .tab-content .active');
		// 如果没有任何数据，则不做处理
		if ($contentActive.length == 0) return;
		// 当前屏幕可显示图片总数
		//totalImg= $('#e_record #' + $contentActive.prop('id') + ' img[data-read="false"]').length;
		$('#e_record #' + $contentActive.prop('id') + ' img[data-read="false"]').each(function (index,element){
			array[index]=$(this).attr("id");
		});
		changepic();
	}
	/**
	 * 更换图片
	 */
	function changepic() {
		if (i >= array.length)  return;
		var obj = document.getElementById(array[i]);
		obj.setAttribute("src", ydl.contexPath +'/erecord/imageCapture/downloadFyImg?tpid=' +
				array[i] + '&_POOLKEY=' + poolSelect["_POOLKEY"]);
		obj.setAttribute("data-read", "true");
		i++;
		setTimeout(changepic, imgLoadTime);
	}
	/**
	 * 获取毫秒日期为 2014051208124800891
	*/
	function formatDate() {
		var date = new Date();
	    var y = date.getFullYear();
	    var m = date.getMonth()+1;
	    var d = date.getDate();
	    var h = date.getHours();
	    var min = date.getMinutes();
	    var s = date.getSeconds();
	    var ss = date.getMilliseconds();
	    if(m < 10)
	        m = "0" + m;
	    if(d < 10)
	        d = "0" + d;
	    if(min < 10)
	    	min = "0" + min;
	    if(s < 10)
	    	s = "0" + s;
	    // 2位随机数
	    var num=parseInt(Math.random()*(99-10+1)+10,10);
	    return (y+""+m + "" + d+""+h+""+min+""+s+""+ss+""+num); 
	}

	//v0.1 初始化后的回调函数  孙一宁 2019/08/15 新增start
	var initCallBack = options.initCallBack;
	if(initCallBack && $.isFunction(initCallBack)){
        initCallBack(options.smlsh);
	}
   //初始化后的回调函数  孙一宁 2019/08/15 新增end
};
})();
/**
 * 引入CSS公共方法
 */
var dynamicLoading = {
  css: function(path){
	 if(!path || path.length === 0){
	  throw new Error('argument "path" is required !');
	 }
	 var head = document.getElementsByTagName('head')[0];
     var link = document.createElement('link');
     link.href = path;
     link.rel = 'stylesheet';
     link.type = 'text/css';
     head.appendChild(link);
 },
 js: function(path){
	 if(!path || path.length === 0){
	  throw new Error('argument "path" is required !');
	 }
	 var head = document.getElementsByTagName('head')[0];
	 var script = document.createElement('script');
	 script.src = path;
	 script.type = 'text/javascript';
	 head.appendChild(script);
  }
}
var baseCSS = ydl.contexPath+'/common/css/';
//引入电子档案CSS
dynamicLoading.css( baseCSS  +"erecord.css");


