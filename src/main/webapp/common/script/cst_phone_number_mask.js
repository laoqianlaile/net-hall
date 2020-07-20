//手机号4~8位掩码
(function (cst) { 
    
     cst.phonemask=function(options){
    	 options.text(options.text().replace(/^(\d{3})\d{4}(\d{4})$/,'$1****$2'));
     }
})(cst);