//流程可视化组件
var down  = ydl.contexPath+'/image/kuan.png'//设置显示下箭头的路径
var right = ydl.contexPath+'/image/zhai.png'//设置显示右箭头的路径
var add = ydl.contexPath+'/image/add.jpg'//设置显示添加的路径
var add_hover = ydl.contexPath+'/image/add_hover.jpg'//设置显示添加的路径
var FlowViewer = function(id){
	var fv = this;
	this.id = id||"mainpanel";
	this.name = "";
	this.showType = "label";//lable--上边框隐藏，可下拉//plane--面板显示
	this.isomit = true;//true--未出现当前状态节点时，有不配的选项，不显示。false--显示。此选项在设置jsonData参数时有效。
	this.RW = "R";
	this.CurrentStep = "";
	this.rtcontent;
	this.jsonData = [];
	this.SetRW = function(rw){
		this.RW = rw;
	}
	this.Style = function(){
			$("head").append(
					"<style type='text/css'>"+
						"#"+this.id+">div{color:white;-moz-user-select:none;-webkit-user-select:none;-ms-user-select:none;-khtml-user-select:none;user-select:none;}"+
						".flow-chart-end-over div{background-position:0 -378px;}"+
						/*".down{width:100px;height:24px;line-height:24px;float:left;background:url("+down+") no-repeat 50%}"+
						".right{width:24px;height:100px;line-height:100px;float:left;background:url("+right+") no-repeat 50%}"+
						".before{background:green}"+
						".stop{background:red}"+
						".edit{background:gray}"+
						".add{cursor:pointer;background:url("+add+") no-repeat;background-size:100%}"+
						".after{background:yellow;color:green}"+
						".edit:hover{cursor:pointer;background:blue}"+
						".add:hover{cursor:pointer;background:url("+add_hover+") no-repeat;background-size:100%}"+*/
					"</style>"
			);
	};
	this.GetData = function(){
		var stepinfo = '['+$.map($(".edit"),function(step){
							var stepinfo = $(step);
							return '{'+
								'"xh":"'+(stepinfo.attr("xh")||"")+'",'+
								'"bzms":"'+stepinfo.find("span").text()+'",'+
								'"bzbz":"'+stepinfo.attr("title")+'"'+
							'}';
						}).join(",")+']';
		var data = '{'+
			'"kshlcmc":"'+this.name+'",'+
			'"stepinfo":'+stepinfo+
		'}';
		return data;
	};
	this.SetData = function(kshlcmc,jd){
		this.jsonData = jd||[];
		ydl.ajax(ydl.contexPath+"/lcksh/jsondata/steplist",
			{kshlcmc:kshlcmc},
			function(data){
				if(data){
					fv.CreateModel();
					$.each(data.steplist,function(index,item){
						fv.name = item.kshlcmc;
						fv.AddOrUpdateMainStep(item);
					});
				}
			}
		);
		/*ydl.sendCommand("SelectByKshlcmc",{
				kshlcmc:kshlcmc
			},function(data){
				if(data.result){
					fv.CreateModel();
					$.each($.parseJSON(data.steplist),function(index,item){
						fv.name = item.kshlcmc;
						fv.AddOrUpdateMainStep(item);
					});
				}else{
					alert(data.reason);
				}
		});*/
		
	};
	// 管理端-获取数据加载流程
	this.SetDataForManager = function(kshlcmc,jd){
		this.jsonData = jd||[];
		ydl.ajax(ydl.contexPath+"/lcksh/jsondata/steplist",
				{kshlcmc:kshlcmc},
				function(data){
					if(data){
						fv.CreateModel();
						$.each(data.steplist,function(index,item){
							fv.name = item.kshlcmc;
							fv.AddOrUpdateStepForManager(item);
						});
					}
				}
		);
		/*ydl.sendCommand("SelectByKshlcmc",{
				kshlcmc:kshlcmc
			},function(data){
				if(data.result){
					fv.CreateModel();
					$.each($.parseJSON(data.steplist),function(index,item){
						fv.name = item.kshlcmc;
						fv.AddOrUpdateMainStep(item);
					});
				}else{
					alert(data.reason);
				}
		});*/
		
	};
	// 获取数据加载流程
	this.SaveData = function(name,type,callback){
		if(type=='add'){
			if(name)this.name = name;
			var lckshjsondata = this.GetData();
			var jsondatas=eval("("+lckshjsondata+")");
			if(jsondatas.stepinfo.length==0){
				ydl.alert({'icon':'warn','message':'无添加的节点无法保存'});
				return false;
			}
			ydl.ajax(ydl.contexPath+"/lcksh/jsondata/add",{lckshjsondata:lckshjsondata},
					function(data){
				if(data.result){
					if (callback){
			            ydl.alert("保存成功！",callback());
					}else{
						ydl.alert("保存成功！");
					}
				}else{
					ydl.alert({'icon':'error','message':data.reason},function (){ fv.name='';});
				}
			});
		}else{
			var lckshjsondata = this.GetData();
			var jsondatas=eval("("+lckshjsondata+")");
			if(jsondatas.stepinfo.length==0){
				ydl.alert({'icon':'warn','message':'无添加的节点无法保存'});
				return false;
			}
			ydl.ajax(ydl.contexPath+"/lcksh/jsondata/update",{lckshjsondata:lckshjsondata},
					function(data){
				if(data.result){
					ydl.alert("保存成功！");
				}else{
					ydl.alert({'icon':'error','message':data.reason});
				}
			});
		}
		/*ydl.sendCommand("ADD",{
					lckshjsondata:lckshjsondata
				},
				function(data){
					if(data.result){
						alert("保存成功！");
					}else{
						alert(data.reason);
					}
		});*/
	};
	this.CreateModel = function(){
		this.name = "";
		$("#"+this.id+",#pageFlowChart").remove();
		if(this.showType == "plane"){
			$("#"+(this.rtcontent||"page_tab_0")).append('<div id="'+this.id+'" ><div></div></div>');
			var mainpanel = $("#"+this.id+" > div");
			mainpanel.empty();
			mainpanel.append('<div id="pageFlowChart" class="expand"><div><ul style="display:block;" class="flow-chart"></ul></div></div>');
		}else{
			$("#pageTabs").before('<div id="pageFlowChart" class=""><div><ul style="display:none;" class="flow-chart"></ul></div><i></i></div>');
			$('#pageFlowChart i').click(function () {
				$(this).prev().children('.flow-chart').slideToggle(function () {
					$(this).parent().parent().toggleClass('expand');
				});
			});
		}
		$(".flow-chart").append('<li class="flow-chart-start"><div></div><span>开始</span></li>');
		if(this.RW=="W"){
			//添加样式需要增加一个
			$(".flow-chart").append('<li class="flow-chart-add add"><div></div><span>新增</span></li>');
		}
		$(".flow-chart").append('<li class="flow-chart-end end"><div></div><span>结束</span></li>');
	}
	//添加或更新主线步骤
	this.AddOrUpdateMainStep = function(stepinfo){
		if($(".select").hasClass("edit")){
			$(".select").attr({
				xh:stepinfo.xh,
				title:stepinfo.bzbz
			}).find("span").text(stepinfo.bzms);
		}else{
			var laststep = this.RW=="W"?$(".add"):$(".end");
			var jd = this.jsonData;
			var cs;
			//当返回值为json对象时
			if(jd && jd.length > 0){
				var t1 = stepinfo.bzbz.split(",");
				$.each(t1,function(index,item){
					$.each(jd,function(index1,item1){
						if(item == item1.bzbz){
							cs = item1;
						}
					});
					
				});
				if(cs){//首信返回数据中，找到对应步骤信息
					laststep.before('<li class="'+(this.RW=="W"?"flow-chart-nextall edit":
						cs.tgbz?"flow-chart-prev"://当前步骤是通过状态，设置为通过
						$(".flow-chart-current").index()==-1?"flow-chart-current":"flow-chart-nextall")+//是否已设置当前步骤标志
							 '" title="'+stepinfo.bzbz+'" xh="'+stepinfo.xh+'"><div></div><span>'+
							 stepinfo.bzms+'</span>'+
							 (cs.date?'<span>'+cs.date+'</span>':'')+
							 '</li>');
				}else{//当首信未返回数据 且 是当前步骤之前的节点时，不显示
					if(!this.isomit || $(".flow-chart-current").index()!=-1){
						laststep.before('<li class="flow-chart-nextall edit"'+//是否已设置当前步骤标志
								 ' title="'+stepinfo.bzbz+'" xh="'+stepinfo.xh+'"><div></div><span>'+
								 stepinfo.bzms+'</span>'+
								 '</li>');	
					}
				}
				if(this.RW=="R" && $(".flow-chart-nextall,.flow-chart-current").index()==-1){
					laststep.removeClass("flow-chart-end").addClass("flow-chart-end-over");
				}else{
					laststep.removeClass("flow-chart-end-over").addClass("flow-chart-end");
				}
			}
			//否则按设置当前步骤方式
			else{
				// 显示流程结束
				if(this.CurrentStep=='end'){
					laststep.before('<li class="flow-chart-prev"'
							+ '" title="'+stepinfo.bzbz+'" xh="'+stepinfo.xh+'"><div></div><span>'+
								stepinfo.bzms+'</span></li>');
					laststep.removeClass("flow-chart-end").addClass("flow-chart-end-over");
				}else{
					laststep.before('<li class="'+(this.RW=="W"?"flow-chart-nextall edit":
						this.CurrentStep==stepinfo.bzbz?"flow-chart-current":
							!this.CurrentStep?"flow-chart-nextall":
								$(".flow-chart-current").index()==-1?"flow-chart-prev":"flow-chart-nextall")+
								'" title="'+stepinfo.bzbz+'" xh="'+stepinfo.xh+'"><div></div><span>'+
								stepinfo.bzms+'</span></li>');
				}
			}
		}
	};
	//添加或更新步骤-管理端
	this.AddOrUpdateStepForManager = function(stepinfo){
		if($(".select").hasClass("edit")){
			$(".select").attr({
				xh:stepinfo.xh,
				title:stepinfo.bzbz
			}).find("span").text(stepinfo.bzms);
		}else{
			var laststep = this.RW=="W"?$(".add"):$(".end");
			laststep.before('<li class="flow-chart-edit edit'
					+ '" title="'+stepinfo.bzbz 
					+'" xh="'+stepinfo.xh+'"><div></div><span>'
					+	stepinfo.bzms+'</span></li>');
		}
	};
	this.DeleteFlow = function(callback){
		//var fv = this;
		var jsondatas=eval("("+this.GetData()+")");
		if(jsondatas.stepinfo.length==0){
			ydl.alert({'icon':'warn','message':'未选择流程无法删除!'});
			return false;
		}else if(fv.name){
			 ydl.ajax(ydl.contexPath+"/lcksh/jsondata/delete",
				 {kshlcmc:fv.name},
				 function(data){
					if(data.result){
						fv.CreateModel();
						if(callback){
							callback();
						}
					}else{
						ydl.alert({'icon':'error','message':data.reason});
					}
				 }
			  );
			
			/*ydl.sendCommand("DELETE",{
				kshlcmc:fv.name
			},function(data){
				if(data.result){
					fv.CreateModel();
				}else{
					alert(data.reason);
				}
			});*/
		}else{
			fv.CreateModel();
		}
	};
	this.DeleteStep = function(){
		if(!$(".select").hasClass("add")){
			ydl.customDialog({
				title:"确认提示",
				text:"确实要删除【"+$(".select").text()+"】吗？",
				buttons:"确定,取消",
				callback:function(button){
					switch (button) {
						case 0: $(".select:not(.add)").remove();
								  break;
	    				case 1: break;
					};
				}
			});
			//$(".select:not(.add)").next().remove();
		}
	};
	this.SetCurrentStep = function(bzbz){
		this.CurrentStep = bzbz;
	};
	this.SetShowType = function(showType1){
		this.showType = showType1;
	}
	this.ShowNoSet = function(){
		this.isomit = false;
	}
	this.RanderTo = function(rt){
		this.rtcontent = rt;
	}
	this.Style();
}