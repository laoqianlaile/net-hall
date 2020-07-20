//手机验证码发送
(function (cst) {
    var count;
    var curCount;//当前剩余秒数
    var InterValObj; //timer变量，控制时间
     cst.verification=function(options){
    	 var before = options.before;
         var pid = '#'+options.pid;
         var ywmc = options.ywmc;
         var xhid = '#'+options.xhid;
         var cmd = options.cmd;
         var fail = options.fail;
         var func = options.func;
         count = ydl.mudic.getName("ish.gg.other.dxyzm.cfddsc");
         //添加发送验证码按钮
         //$(pid).append('<div class="col-sm-4 col-md-2"><button id="b_send" type="button" class="btn" style="background-color:transparent; border-style:none; font-size:12px; color:#FF8000; outline:none"></button></div>');
         //$('#b_send').text("点击免费获取验证码").parent().css('padding-left', '0');
         //$('#b_send').click(function(){
         $(pid).addButton('获取短信验证码',function(){
             //发送验证码给职工
        	 var _r=false;
        	 if(before){
        		 _r=before();
        	 }else{
        		_r=true; 
        	 }
        	 if(_r){
        	     if (pageTabs[0].checkForm()) {
	        		 ydl.sendCommand(cmd,{"ywmc":ywmc},function(data,code,msg){
	                	 if('00000000'==code){
    						$(xhid).val(data["yzmxh"]);
                			sendMessage();
                			//ydl.alert("短信已发送请输入序号为：【"+data["yzmxh"]+"】的短信验证码");	// 使用下传数据中的字段值
                			ydl.alert("短信已发送!");	// 使用下传数据中的字段值
	                     }
	                	 else{
	                		 if(fail){
	                    		 fail();
	                    	 }
	                		 ydl.alert(msg);
	                	 }
	                 });
        	     }
        	 }
         },"b_dxyzm");
     }
     function checkForm() {
			alert();
		}
    //设置button效果，开始计时
    function sendMessage() {
        curCount = count;
        $("#b_dxyzm").attr("disabled", "true");//禁用按钮
        $("#b_dxyzm").text(curCount + "秒后重新发送");
        InterValObj = window.setInterval(SetRemainTime, 1000); //启动计时器，1秒执行一次

    }
    //timer处理函数
    function SetRemainTime() {
        if (curCount == 0) {
            window.clearInterval(InterValObj);//停止计时器
            $("#b_dxyzm").removeAttr("disabled");//启用按钮
            $("#b_dxyzm").text("重新发送验证码");
        }
        else {
            curCount--;
            $("#b_dxyzm").text(curCount + "秒后重新发送");
        }
    }
  
})(cst);