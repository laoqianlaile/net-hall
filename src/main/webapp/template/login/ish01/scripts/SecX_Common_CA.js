/////const //////////////////////////
//分组加密算法标识
var SGD_SM1_ECB   = 0x00000101;  // SM1算法ECB加密模式
var SGD_SM1_CBC   = 0x00000102;  // SM1算法CBC加密模式
var SGD_SSF33_ECB = 0x00000201;  // SSF33算法ECB加密模式
var SGD_SSF33_CBC = 0x00000202;  // SSF33算法CBC加密模式
var SGD_SMS4_ECB  = 0x00000401;  // SMS4算法ECB加密模式
var SGD_SMS4_CBC  = 0x00000402;  // SMS4算法CBC加密模式
var SGD_3DES_ECB  = 0x00002001;  // 3DES算法ECB加密模式
var SGD_3DES_CBC  = 0x00002002;  // 3DES算法CBC加密模式

//签名算法标识
var SGD_SM3_RSA    = 0x00010001;  // 基于SM3算法和RSA算法的签名
var SGD_SHA1_RSA   = 0x00010002;  // 基于SHA_1算法和RSA算法的签名
var SGD_SHA256_RSA = 0x00010004;  // 基于SHA_256算法和RSA算法的签名
var SGD_SM3_SM2    = 0x00020101;  // 基于SM2算法和SM3算法的签名

//密码杂凑算法标识
var SGD_SM3	   = 0x00000001;  // SM3杂凑算法
var SGD_SHA1   = 0x00000002;  // SHA_1杂凑算法
var SGD_SHA256 = 0x00000004;  // SHA_256杂凑算法

//产生密钥对时的密钥标识
var KEY_TYPE_RSA1024 = 1;  // RSA1024位
var KEY_TYPE_RSA2048 = 2;  // RSA2048位
var KEY_TYPE_SM2_256 = 3;  // SM2 256位

//获取设备信息
var DEVICE_TYPE_LABEL     = 1;    // 设备标签
var DEVICE_TYPE_FREESPACE = 2;    // 剩余空间
var DEVICE_TYPE_SERIALNUM = 3;    // 硬件设备序列号
var DEVICE_TYPE_TYPE      = 4;    //硬件类型 返回 RSA 或 SM2
var DEVICE_TYPE_KEY_TYPE  = 115;  //SM2设备返回20 RSA设备返回10
var DEVICE_TYPE_VID_PID   = 116;  //设备的VID PID

//获取的证书信息类型
var CERT_VERSION     = 1;  // 证书版本 返回V1 V2 V3
var CERT_SERIAL      = 2;  // 证书序列号
var CERT_SIGN_METHOD = 3;  // 获取证书类型 返回 rsa或sm2
var CERT_ISSUER_C    = 4;  // 证书发放者国家名 多个之间用&&&隔开
var CERT_ISSUER_O    = 5;  // 证书发放者组织名
var CERT_ISSUER_OU   = 6;  // 证书发放者部门名
var CERT_ISSUER_ST   = 7;  // 证书发放者省州名
var CERT_ISSUER_CN   = 8;  // 证书发放者通用名
var CERT_ISSUER_L    = 9;  // 证书发放者城市名
var CERT_ISSUER_E    = 10; // 证书发放者EMAIL地址
var CERT_NOT_BEFORE  = 11; // 证书有效期起始 格式YYYYMMDDHHMMSS
var CERT_NOT_AFTER   = 12; // 证书有效期截止 格式YYYYMMDDHHMMSS
var CERT_SUBJECT_C   = 13; // 用户国家名
var CERT_SUBJECT_O   = 14; // 用户组织名
var CERT_SUBJECT_OU  = 15; // 用户部门名
var CERT_SUBJECT_ST  = 16; // 用户省州名
var CERT_SUBJECT_CN  = 17; // 用户通用名
var CERT_SUBJECT_L   = 18; // 用户城市名
var CERT_SUBJECT_E   = 19; // 用户EMAIL地址
var CERT_PUBKEY      = 20; // 证书公钥
var CERT_SUBJECT_DN  = 33; // 用户DN
var CERT_ISSUER_DN   = 34; // 颁发者DN
var CERT_UNIQUEID    = 35; // 唯一实体ID
/////define object  /////////////////////////////////
function initCA(){
	try {
		if (window.ActiveXObject) {
			alert("CA控件加载成功！");
			document.writeln("<OBJECT classid=\"CLSID:3F367B74-92D9-4C5E-AB93-234F8A91D5E6\" height=1 id=XTXAPP  style=\"HEIGHT: 1px; LEFT: 10px; TOP: 28px; WIDTH: 1px\" width=1 VIEWASTEXT>");
			document.writeln("</OBJECT>");
		} else {
			alert("232323");
			document.writeln("<embed id=XTXAPP0 type=application/x-xtx-axhost clsid={3F367B74-92D9-4C5E-AB93-234F8A91D5E6} event_OnUsbkeyChange=OnUsbKeyChange width=1 height=1 />");
			XTXAPP = document.getElementById("XTXAPP0");
		}
	}
	catch(e) {
		alert("ActiveXObject XTXAppCOM.dll可能没有注册成功！"+e.message);
	}

}


/////组件接口转换为脚本接口////////////////////////

//获取用户列表 并填充到证书列表

function GetList(strListID)
{

	//alert("===输入参数："+strListID);
	//var objListID = eval(strListID);
	//alert("objListID = "+objListID);
	var strUserList = XTXAPP.SOF_GetUserList();
	//alert(" strUserList ="+strUserList);
	//while (1) {
	var i = strUserList.indexOf("&&&");
	//alert("i ="+i);
	//if (i <= 0 ) {
	//	break;
	//}
	var strOneUser = strUserList.substring(0, i);
	//alert("strOneUser  ="+strOneUser);
	var strName = strOneUser.substring(0, strOneUser.indexOf("||"));
	//alert("strName  ="+strName);
	var strUniqueID = strOneUser.substring(strOneUser.indexOf("||") + 2, strOneUser.length);
	//	alert("strUniqueID ="+strUniqueID);
	//var objItem = new Option(strName, strUniqueID);
	//alert("objItem ="+objItem);

	//	var sid = document.getElementByname("danwei").value;
	//sid.value="123";
	//	alert("sid = "+sid);
	//	var myOption = document.createElement("Option");
	//	      sid.options.appendChild(myOption);
	//strListID.options.appendChild(objItem);
	var len = strUserList.length;
	//	alert("len = "+len);
	strUserList = strUserList.substring(i + 3,len);
	//alert("end StrUserList = "+strUserList);
	testAdd(strName,strUniqueID);
	//testAdd("贵港市","1234");
	//}
	//
	//var objListID = null;
	//return;
}

function testAdd(danName,danValue){
	// alert("+++进入testAdd方法！");
	var sid = document.getElementById("CertList");
	var myOption = document.createElement("Option");
	sid.options.appendChild(myOption)
	// alert("09090909+danName = "+danName+"  danValue = "+danValue);
	myOption.text = danName;
	myOption.value = danValue;
	// alert("END testAddoption");
}
//清空证书列表
function RemoveUserList(strListID)
{
	var objListID = eval(strListID);
	var i;
	var n = objListID.length;
	for(i = 0; i < n; i++) {
		objListID.remove(0);
	}
}
//重新填充用户列表
function ChangeUserList(strListID)
{
	RemoveUserList(strListID);
	GetList(strListID);
}


function GetUserList()
{
	return XTXAPP.SOF_GetUserList();
}


//设置加密算法
function SetEncryptMethod(encMethod)
{
	return XTXAPP.SOF_SetEncryptMethod(encMethod);
}

//设置签名算法
function SetSignMethod(signMethod)
{
	return XTXAPP.SOF_SetSignMethod(signMethod);
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
	XTXAPP.SOF_SetEncryptMethod(SGD_3DES_ECB);
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
	return XTXAPP.SOF_SignMessage(0,CertID,InData)
}

//验证p7签名
function VerifyDatabyP7(P7Data)
{
	return XTXAPP.SOF_VerifySignedMessage(P7Data,"");
}

//p7数字信封加密
function EncodeP7Enveloped(Cert,InData)
{

	return XTXAPP.SOF_EncryptDataEx(Cert,InData);  //P7数字信封加密 和SVS BCA_ALL互通
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

//文件对称加密
function SymEncryptFile(sKey, InFile, OutFile)
{
	return XTXAPP.SOF_SymEncryptFile(sKey, InFile, OutFile);

}

//文件对称解密
function SymDecryptFile(sKey, InFile, OutFile)
{
	return XTXAPP.SOF_SymDecryptFile(sKey, InFile, OutFile);

}

//公钥加密
function PubKeyEncrypt(Cert, InData)
{
	return XTXAPP.SOF_PubKeyEncrypt(Cert, InData);
}

//私钥解密
function PriKeyDecrypt(CertID, InData)
{
	return XTXAPP.SOF_PriKeyDecrypt(CertID, InData);
}

//文件摘要
function HashFile(hashAlg, InFile)
{
	return XTXAPP.SOF_HashFile(hashAlg, InFile);
}

function GetLastError()
{
	return XTXAPP.SOF_GetLastError();
}

function GetLastErrorMsg()
{
	var code = XTXAPP.SOF_GetLastError();
	var msg = XTXAPP.SOF_GetLastErrMsg();

	return "错误码[" + code + "] 错误描述[" + msg + "]";
}

//判断证书有效期天数
function CheckValid(userCert){
	//alert("CheckValid---");
	var strNotBefore = XTXAPP.SOF_GetCertInfo(userCert,CERT_NOT_BEFORE);
	//alert("strNotBefore---"+strNotBefore);
	var notBeforeDate = strNotBefore.substring(0,4)+"/"+strNotBefore.substring(4,6)+"/"+strNotBefore.substring(6,8);
	//alert("notBeforeDate---"+notBeforeDate);
	//var nowDate = new Date().Format("yyyy/MM/dd");
	var nowDate = new Date();
	//alert("nowDate---"+nowDate);
	var days = (Date.parse(notBeforeDate) - Date.parse(nowDate)) / (1000*60*60*24);
	//alert("days---"+days);
	if (days > 0) {
		alert("您的证书尚未生效!距离生效日期还剩" + days + "天!");
		return false;
	}
	var strNotAfter = XTXAPP.SOF_GetCertInfo(userCert, CERT_NOT_AFTER);
	alert("strNotAfter---"+strNotAfter);
	var notAfterDate = strNotAfter.substring(0,4)+"/"+strNotAfter.substring(4,6)+"/"+strNotAfter.substring(6,8);
	var nowDate = new Date().Format("yyyy/MM/dd");
	days = (Date.parse(notAfterDate) - Date.parse(nowDate)) / (1000*60*60*24);

	if (days <= -45) {
		alert("您的证书已过期 "+ -days +" 天，超过了最后使用期限,请尽快更新!");
		return false;
	}

	if (days >= 0 && days <= 60) {
		alert("您的证书还有" + days + "天过期，请尽快更新!");
		return true;
	}

	if (days < 0) {
		alert("您的证书已过期 "+ -days +" 天，请尽快更新!");
	}

	return true;
}
function login_submit(){
	//alert("进login_submit==");
	var ret;
	var strContainerName = LoginForm.CertList.value;
	var strPin = LoginForm.capwd.value;
	//alert("strContainerName ="+strContainerName);
	//LoginForm.unitcr.value="00405801";
	LoginForm.unitcr.value=strContainerName;
	//alert("-=-=-=-=-=-=-=-=-=-=-");
	//alert("strPin = "+strPin+" strContainerName ="+strContainerName);
	ret = Login(LoginForm,strContainerName,strPin);
	LoginForm.capwd.value = "";
	//alert("ret  ="+ret);
	if(!ret)
	{
		return false;
	}
	else
		return true;
}
function Login(strFormName,strContainerName,strPin) {
	var ret;
	var objForm = eval(strFormName);

	if (objForm == null) {
		alert("Form Error");
		return false;
	}
	if (strPin == null || strPin == "") {
		alert("请输入Key的保护口令");
		return false;
	}
	if (strPin.length < 6 || strPin.length > 16) {
		alert("密码长度应该在6-16位之间");
		return false;
	}

	//校验密码
	if(!XTXAPP.SOF_Login(strContainerName,strPin))
	{
		var retryCount = XTXAPP.SOF_GetPinRetryCount(strContainerName);
		if (retryCount > 0) {
			alert("校验证书密码失败!您还有" + retryCount + "次机会重试!");
			return false;
		} else if (retryCount == 0) {
			alert("您的证书密码已被锁死,请联系BJCA进行解锁!");
			return false;
		} else {
			alert("登录失败!");
			return false;
		}
	}
	// alert("---ywb111----");
	//导出客户端证书
	var userCert = GetSignCert(strContainerName);
	if (userCert == null || userCert == "") {
		alert("导出用户证书失败!");
		return false;
	}
	//alert("---22222----");
	//检查证书有效期
//	if (!CheckValid(userCert)) {
//		alert("----3-3-3-3-3-");
//		return false;
//	}
	//alert("---555555----");
	//验证服务端签名
//	if(!VerifySignedData(strServerCert,strServerRan,strServerSignedData))
//    {
//        alert("验证服务器端信息失败!");
//        return false;
//    }
//	alert("---66666----");
	//对随机数做签名
	var strClientSignedData = SignedData(strContainerName,strServerRan);
	if (strClientSignedData == null || strClientSignedData == "") {
		alert("客户端签名失败!");
		return false;
	}
	//alert("---ywb----");
	//Add a hidden item ...
	var strSignItem = "<input type=\"hidden\" name=\"UserSignedData\" value=\"\">";
	if (objForm.UserSignedData == null) {
		objForm.insertAdjacentHTML("BeforeEnd",strSignItem);
	}
	var strCertItem = "<input type=\"hidden\" name=\"UserCert\" value=\"\">";
	if (objForm.UserCert == null) {
		objForm.insertAdjacentHTML("BeforeEnd",strCertItem);
	}
	var strContainerItem = "<input type=\"hidden\" name=\"ContainerName\" value=\"\">";
	if (objForm.ContainerName == null) {
		objForm.insertAdjacentHTML("BeforeEnd",strContainerItem);
	}
	//alert("strContainerName = "+strContainerName);
	objForm.UserSignedData.value = strClientSignedData;
	objForm.UserCert.value =  userCert;
	objForm.ContainerName.value = strContainerName;

	return true;

}


