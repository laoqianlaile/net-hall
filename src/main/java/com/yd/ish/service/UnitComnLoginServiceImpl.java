package com.yd.ish.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

 
 
import com.yd.basic.service.ILoginService;
import com.yd.svrplatform.comm_mdl.context.MainContext;
import com.yd.svrplatform.comm_mdl.context.UserContext;
import com.yd.svrplatform.exception.PlatRuntimeException;
import com.yd.svrplatform.jdbc.BaseBean;
import com.yd.svrplatform.jdbc.PersistentBiz;
import com.yd.svrplatform.util.DataPool;

@Service("unitcomn") 
public class UnitComnLoginServiceImpl implements ILoginService {
	private static final Logger logger = LoggerFactory.getLogger(UnitComnLoginServiceImpl.class);
	@Autowired
	PersistentBiz pt;
	
	@Override
	public String getOperIDKey(){
		return "deputycode";
	}
	
	//登录校验
	@Override
	public UserContext check(Map<String,String> loginmodel) throws PlatRuntimeException{
		String unitAccnum = loginmodel.get("unitaccnum");
		String deputyCode =  loginmodel.get("deputycode");
		String password =  loginmodel.get("password");
	 
	    if(unitAccnum==null || "".equals(unitAccnum)){
	    	logger.debug("loginmodel={}",loginmodel);
	    	throw new PlatRuntimeException("PT000001","单位帐号不能为空");
	    }
	    if(deputyCode==null || "".equals(deputyCode)){
	    	logger.debug("loginmodel={}",loginmodel);
	    	throw new PlatRuntimeException("PT000001","专办员帐号不能为空");
	    }
	    if(password==null || "".equals(password)){
	    	logger.debug("loginmodel={}",loginmodel);
	    	throw new PlatRuntimeException("PT000001","密码不能为空");
	    }
 
		//开发测试用 String sql="select deputyrole,deputyname,deputyidcardnum,deputypwd,state from nt001 where unitaccnum='"+unitAccnum+"' and deputycode='"+deputyCode+"' and freeuse1='0'";
		String sql="select deputyrole,deputyname,deputyidcardnum,deputypwd,state from nt001 where unitaccnum='"+unitAccnum+"' and freeuse1='0'";
		
		BaseBean[] beans=null;
		beans=pt.query(sql);
		if(beans==null||beans.length==0){
			throw new PlatRuntimeException("PT000001","单位【"+unitAccnum+"】下不存在专办员【"+deputyCode+"】的信息");
		}
		if(!beans[0].get("state").equals("2")){
			throw new PlatRuntimeException("PT000001","专办员非正常状态");
		}
		if(beans[0].get("deputypwd")==null){
			throw new PlatRuntimeException("PT000001","专办员密码为空");
		}
		if(!beans[0].get("deputypwd").toString().trim().equals(password)){
			throw new PlatRuntimeException("PT000001","专办员密码输入错误");
		}
		UserContext user=new UserContext();
		
		//add by hj
//		user.setAttribute("taskurl", "WAITTASK_task.ydpx");//点击任务图标设置任务页面
//    	user.setAttribute("homeurl", "HOMEPAGE_01.ydpx");//点击首页图标返回首页
    	user.setAttribute("messurl", "SPSXTX_0103_01.ydpx");//点击新消图标返回息页面
    	
		user.setOperId(deputyCode);
		user.setOperName(beans[0].get("deputyname").toString());
		user.setOrgId("00000000");
		user.setOrgName("默认机构"); 
		//
		MainContext mainContext = MainContext.currentMainContext();
		DataPool datapool = mainContext.getDataPool();
		if(null==datapool)
			datapool = new DataPool();
		datapool.put("unitaccnum", unitAccnum);
		mainContext.setDataPool(datapool);
		user.setAttribute(ILoginService.EXTINFOKEY, mainContext.getDataPool());
	 
		return user;
	}
	//查询菜单
	public String getMenuTree(){
		// 开发测试用 begin
		String systemType="";
		MainContext mainContext = MainContext.currentMainContext();
		DataPool datapool = mainContext.getDataPool();
		if( datapool.getString("unitaccnum").equals("1000")){
			systemType = "0";//单位
		}else if(datapool.getString("unitaccnum").equals("1001")){
			systemType = "1";//个人
		}
		// 开发测试用 end
	    BaseBean[] menuBeans = menu_list(systemType,true);
	    StringBuffer strMenuTree = new StringBuffer("[");
	    for (int i = 0; i < menuBeans.length; i++) {
	    	String funcid = menuBeans[i].get("funcid").toString();
	    	String url = (menuBeans[i].get("url")==null)?"": menuBeans[i].get("url").toString();
	    	//检查权限
	    	//if (!user.checkRole(menuBeans[i])) continue;
	    	if (i > 0) strMenuTree.append("\t,");
	    	if (url.equals("@engine")) url =  "/flow/menu/" + funcid;
			else if(url.endsWith("jsp")) url = "/" + url;
	    	strMenuTree.append("{id: '" + funcid + "', pid: '" + menuBeans[i].get("parentfuncid") + 
	    		"', name: '" + menuBeans[i].get("funcname") + "', url: '" + url + "'}");
	    }
	    strMenuTree.append("]");
	    
	    String tree="{LEFT: "+strMenuTree+", "
	    		+ "UP: [{id: 'tmenu_01', pid: '$$$$$$$$', name: '查询业务', url: '#'}, {id: 'tmenu_02', pid: '$$$$$$$$', name: '单位信息管理', url: '#'},{id: 'tmenu_03', pid: '$$$$$$$$', name: '预约业务', url: ''},{id: 'tmenu_0301', pid: 'tmenu_03', name: '预约提取', url: '#'},{id: 'tmenu_0302', pid: 'tmenu_03', name: '预约贷款', url: '#'},{id: 'tmenu_04', pid: '$$$$$$$$', name: '服务设定', url: '#'},{id: 'tmenu_05', pid: '$$$$$$$$', name: '帮助中心', url: '#'}],"
	    		+ "RIGHT: [{id: 'rmenu_01', pid: '$$$$$$$$', name: '单位邮箱', url: '#'},{id: 'rmenu_02', pid: '$$$$$$$$', name: '事项提醒', url: '#'},{id: 'rmenu_03', pid: '$$$$$$$$', name: '地图导航', url: '#'},{id: 'rmenu_04', pid: '$$$$$$$$', name: '功能导航', url: '#'},{id: 'rmenu_05', pid: '$$$$$$$$', name: '服务记录', url: '#'},{id: 'rmenu_06', pid: '$$$$$$$$', name: '客服申请', url: '#'},{id: 'rmenu_07', pid: '$$$$$$$$', name: '投诉建议', url: '#'}]}";
		return tree;
		
//		return strMenuTree;
	}
	/**
	 * 获取功能列表
	 * 
	 * @param isCrop
	 *            是否单位登录（true=单位，false=个人）
	 * @param isMenu
	 *            是否菜单（true=菜单，false=快捷图标）
	 * @return 符合条件的查询结果
	 */
	public  BaseBean[] menu_list(String systemtype, boolean isMenu) {
		BaseBean[] beans = null;
		String sql = "select funcid, funcname, url, deputyrole, freeuse1,freeuse4,systemtype ";
		if (isMenu) {
			sql += " ,parentfuncid ";
		}
		//sql += "from rsp101 where actflg='1'";
		sql += "from rsp101 where actflg='1' and freeuse5 <> '2'";//--js 20140731
		if (!isMenu) {
			sql += " and freeuse5='1' ";
		}
		String systpyes = "";
		if (systemtype.equals("3")) {
			systpyes = "'" + systemtype + "','5'";
		} else {
			systpyes = "'" + systemtype + "','4','5'";
		}
		sql += "  and systemtype in (" + systpyes + ") order by parentfuncid, levelsort";
		try {
			beans = pt.query(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlatRuntimeException("PT000001","执行menu_list 时出错【" + e.getMessage() + "】");
		}
		return beans;
	}


}
