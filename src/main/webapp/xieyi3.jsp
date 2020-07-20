<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Insert title here</title>	
<style>
#div1{
	font-size:20px;
}

</style>
<SCRIPT LANGUAGE="JavaScript">

	function willing(){
		window.opener.document.all.returnval.value = '1';
		window.close();
	}
	function backmain(){
		window.opener.document.all.returnval.value = '2';
        window.close();
	}
//-->
</SCRIPT>
</head>
<body>
<div id='div1'>
<BR><BR><BR><BR><BR>
<BR>&nbsp;&nbsp;&nbsp;&nbsp;如您办理了这次网上公积金还贷提取业务，该房屋共有人如需提取该笔住房贷款还款的，办理时间应在同一个月内；
<BR>&nbsp;&nbsp;&nbsp;&nbsp;另外，今年内您本人、配偶方及其他共有人将<font color='red'>不能再以其他住房还贷名义申请提取公积金。</font>
<BR><BR>
<BR><BR><BR><BR>
<CENTER><INPUT TYPE="button" VALUE="继续提交" ONCLICK="willing()"><INPUT TYPE="button" VALUE="放弃提交" ONCLICK="backmain()"></CENTER>
</div>
</body>
</html>