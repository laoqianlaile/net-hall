var hfUrl = {
  serviceUrl: 'http://www.gdzwfw.gov.cn/portal/branch-hall?orgCode=693981103',  //服务网网址
  chargeUrl: 'http://www.hzgjj.cn/pages/cms/hzgjj/html/index.html',             //管理中心网址
  governmentUrl: 'http://www.gd.gov.cn',                                         //省政府网址
  findError: 'javasrcript:;',          //尾部右下角第一个
  aboutUrl: 'javasrcript:;'            //尾部右下角第二个
}; 

var hfName = {
  chargeName: '惠州住房公积金管理中心',
  footerChargeName: '住房公积金管理中心'
};

//设置服务网网址链接
$('#service-url').attr('href',hfUrl.serviceUrl);
//设置管理中心网址
$('#charge-url').attr('href',hfUrl.chargeUrl);
//设置省政府网址
$('#province-url').attr('href',hfUrl.governmentUrl);
//尾部右下角第一个
$('#find-error').attr('href',hfUrl.findError);
$('#find-error').css('display','inline-block');
//尾部右下角第二个
$('#about-url').attr('href',hfUrl.aboutUrl);
$('#about-url').css('display','inline-block');

// 头部中心名称
$('#charge-url').text(hfName.chargeName);
// 尾部中心名称
$('#foorCharge').text(hfName.footerChargeName);