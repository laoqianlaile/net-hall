//预览
(function (cst) { 
    
     cst.ydPreview=function(options){
          var $up_btn=$(this); 
          var type=options.type;
          var zipname=options.zipname;
          var fileids=options.fileids;
          var url=ydl.contexPath + '/fileInteractivePreview/jsondata/' +type; 
          $.ajax(url,{ 
                       method: 'POST',
				       dataType: 'json',
				       data:{
				          _r:(new Date())+"",
				          fileids:fileids
				       },
			           success:function(jsonData){  
			           		ydl.dialog.open(ydl.contexPath + '/common/html/preview.jsp', 
			           		$.extend(ydl.getInputs(true), {
								'poolkey': jsonData.poolkey,
								'currentPreview' : jsonData.currentPreview,
								'totalPreview' : jsonData.totalPreview,
								'suffix' : jsonData.suffix,
								'fid' : fileids,
								'reqtype' : jsonData.reqtype,
								'zipname' : zipname,
								'realName' : jsonData.realName
							}), null, { width: 1200, height: 500 });
			           }//end  success
			       }
		); //end ajax 
     }
     
     
     cst.ydRePrintVoucher=function(options){ // type:接口实现类  fileid:文件id值
          var type=options.type;
          var fileid=options.fileid;
          //var flowid = options.flowid;
          //var stepid = options.stepid;
          //var url=ydl.contexPath + '/rePrint/jsondata/' +type+'/'+flowid+'/'+stepid; 
          var url=ydl.contexPath + '/rePrint/jsondata/' +type; 
		 
		  var curHeight=$(document.body).height()-150;
	 
          $.ajax(url,{ 
          			   method:'post',
          			   dataType:'json',
          			   data:{fileid:fileid},
			           success:function(jsonData){  
			           		if(jsonData.returnCode=='00000000'){
			           			var printCount = jsonData.printCount;
				           		var fileType = jsonData.fileType;
				           		var poolkey = jsonData.poolkey;
				           		 
								ydl.dialog.open(ydl.contexPath+"/rePrint/pdf/"+poolkey+"/"+printCount+"/"+fileType+"/"+fileid+"/"+type,"","",{
									'height':curHeight,open:function(){$('.modalDialog').closest('.modal-dialog').find('.modal-title').text('');}});
								
								
			           		}else{
			           			ydl.alert(jsonData.message);
			           		}
			           		
			           }//end  success
			       }
		); //end ajax 
     }
     
})(cst);
