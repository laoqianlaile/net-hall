/////const //////////////////////////

/////define object  /////////////////////////////////

function initCA(){
	try
	{
		document.writeln("<OBJECT classid=\"clsid:3F367B74-92D9-4C5E-AB93-234F8A91D5E6\" height=1 id=XTXAPP  style=\"HEIGHT: 1px; LEFT: 10px; TOP: 28px; WIDTH: 1px\" width=1 VIEWASTEXT>");
		document.writeln("</OBJECT>");

	}
	catch(e)
	{
		alert("ActiveXObject XTXAppCOM.dll可能没有注册成功！"+e.message);
	}
}
var errorMsg = "请确认插入正确的证书介质或重新登录系统！";

/////组件接口转换为脚本接口////////////////////////

//得到用户列表
function GetUserList() {

	return XTXAPP.SOF_GetUserList();

}

//得到用户信息
function GetCertBasicinfo(Cert, Type)
{

	return XTXAPP.SOF_GetCertInfo(Cert,Type);

}
function GetExtCertInfoByOID(Cert, oid)
{

	return XTXAPP.SOF_GetCertInfoByOid(Cert,oid);
}


//根据证书惟一标识，获取Base64编码的证书字符串。指定获取加密（交换）证书。
function GetExchCert(strContainerName)
{
	var UserCert = XTXAPP.SOF_ExportExChangeUserCert(strContainerName);

	return UserCert;
}

//签名证书
function GetSignCert(strContainerName)
{
	var UserCert = XTXAPP.SOF_ExportUserCert(strContainerName);

	return UserCert;
}


//xml签名
function SignedDataXML(signdata,ContainerName)
{
	return XTXAPP.SOF_SignDataXML(ContainerName,signdata);
}

function SignedData(sContainerName,sInData)
{
	if (sContainerName != null)
		return XTXAPP.SOF_SignData(sContainerName,sInData);
	else
		return "";
}


function VerifySignedData(cert,indata,signvalue)
{
	return XTXAPP.SOF_VerifySignedData(cert,indata,signvalue);

}


function EncryptData(sKey,inData)
{

	var ret = XTXAPP.SOF_SymEncryptData(sKey,inData);
	return ret;

}


function DecryptData(sKey,inData)
{

	var ret = XTXAPP.SOF_SymDecryptData(sKey,inData);
	return ret;

}

function GenerateRandom(RandomLen)
{

	return XTXAPP.SOF_GenRandom(RandomLen);
}


//文件签名 返回签名数据
function SignFile(sFileName,sContainerName)
{
	return XTXAPP.SOF_SignFile(sContainerName,sFileName)
}

function VerifySignFile(sFileName,sCert,SignData)
{
	return XTXAPP.SOF_VerifySignedFile(sCert,sFileName,SignData);
}

//修改密码
function ChangeUserPassword(strContainerName,oldPwd,newPwd)
{
	return XTXAPP.SOF_ChangePassWd(strContainerName,oldPwd,newPwd);
}

//xml签名
function SignedDataXML(signdata,ContainerName)
{
	return XTXAPP.SOF_SignDataXML(ContainerName,signdata);
}

//xml验证签名
function VerifySignXML(signxml)
{
	return XTXAPP.SOF_VerifySignedDataXML(signxml);
}

//p7签名
function SignByP7(CertID,InData)
{
	return XTXAPP.SOF_SignMessage(1,CertID,InData)
}

//验证p7签名
function VerifyDatabyP7(P7Data)
{
	return XTXAPP.SOF_VerifySignedMessage(P7Data,"");
}

//p7数字信封加密
function EncodeP7Enveloped(Cert,InData)
{

	return XTXAPP.SOF_EncryptDataEx(Cert,InData);
}

//p7数字信封解密
function DecodeP7Enveloped(CertID,InData)
{
	return XTXAPP.SOF_DecryptData(CertID,InData);
}

//base64编码
function Base64Encode(InData)
{
	return XTXAPP.SOF_Base64Encode(InData);
}

//base64解码
function Base64Decode(InData)
{
	return XTXAPP.SOF_Base64Decode(InData);
}

function SetEncryptMethod(InData)
{
	return XTXAPP.SOF_SetEncryptMethod(InData);
}



//判断证书有效期天数
function alertValidDay(cert){
	var endDate = XTXAPP.SOF_GetCertBasicinfo(cert,12);
	var nowDate = new Date().Format("yyyy/MM/dd");
	var days = (Date.parse(endDate)-Date.parse(nowDate))/(1000*60*60*24);
	if(days<=60&&days>0){
		alert("您的证书还有"+days+"天过期,请尽快更新");
	}
	if(days<=-45){
		alert("您的证书已经过期"+(-days)+"天,超过了最后使用期限,请尽快更新");
		return false;
	}
	if(days<=0){
		alert("您的证书已经过期"+(-days)+"天,请尽快更新");
	}
	return true;
}

function OnFormSubmit(){
	var ContainerName=$("input[name='_CONTAINERNAME']").val();
	if(!ContainerName||ContainerName=="null"||ContainerName==""){
		return true;
	}
	var cert=GetSignCert(ContainerName);
	if(cert==""){
		alert("获得用户证书失败");
		return false;
	}
	$("input[name='_CERTINFO']").val(cert);
	if($("input[name='_KEYINFO']").val()==""){
		return true;
	}
	var text=$("input[name='_KEYINFO']").val().split(",");
	var msg="";
	for(var i=0;i<text.length;i++){
		var v=$('#'+text[i]).val();
		if(!v){
			v=poolSelect[text[i]];
		}
		if(!v&&v!=""){
			alert("页面和数据总线中没有变量["+text[i]+"]");
			return false;
		}
		if(msg.length>0){
			msg+=",";
		}
		msg+=v;
	}
	$("input[name='_TEXTINFO']").val(msg);
	var sign=SignedData(ContainerName,msg);
	if(sign==""){
		alert("信息签名失败");
		return false;
	}
	$("input[name='_SIGNINFO']").val(sign);
	return true;
}