/*
登录页面上引用的：
<OBJECT classid="clsid:E1D396DC-D064-4846-8B50-A3301BDD6243" id="ePass" name = "ePass" width=0 height=0></OBJECT>
*/


function getEncyData()
{
  //return true;  
  var DigestOutPut;
	var bFlag=false;
	var RandomData;
	var ErrCode;
	var epUserName;

	try
	{
		ePass.OpenDevice(1,"");
		ePass.VerifyPIN (0,"BJEPASSNDTALENT");
		ePass.ChangeDir(768,0,"BJTALENT");
		ePass.OpenFile(0,1); 
		RandomData = ePass.GenRandom(0);
		DigestOutPut = ePass.HashToken(1,2,RandomData);	
		login.EncyCode.value=DigestOutPut;
		login.RandomCode.value=RandomData;	
		ePass.OpenFile (0,3);
		epUserName = ePass.Read (0,0,0,20);//获取epass用户名
//		alert("DigestOutPut:"+DigestOutPut+":RandomData:"+RandomData+":epUserName:"+epUserName);
		login.epUserName.value=epUserName;
		ePass.CloseDevice();
		bFlag = true;
	}
	catch(e)
	{
		bFlag = true;
		ErrCode = e.number&0xFFFF;
		if(ErrCode==0x01)
		{
			alert("加密设备的驱动程序没有安装或者是您的电脑不支持USB接口!");
			bFlag = false;
		}
		else if(ErrCode==0x02||ErrCode==0x03)
		{
			alert("您安装的加密设备的驱动程序版本不正确!");
			bFlag = false;
		}
		else if(ErrCode==0x06||ErrCode==0x07)
		{
			alert("加密设备没有连接,请检查连接线!");
			bFlag = false;
		}
		else if(ErrCode==0x09||ErrCode==0x0A)
		{
			alert("加密设备设备重储存的用户信息可能被损坏!");
			bFlag = false;
		}
		else{
			alert("加密设备出现不可预知的错误!请联系程序供应商!"+e);
			bFlag = false;
		}
	}
	return {'bFlag':bFlag,'epUserName':epUserName};
}