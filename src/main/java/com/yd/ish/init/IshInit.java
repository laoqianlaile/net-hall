package com.yd.ish.init;

import com.yd.ish.dataanalysis.service.initMemcached.InitTJFuncToMemcached;
import com.yd.org.service.InitMemcached.InitAllFuncToMemcached;
import com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp;
import com.yd.svrplatform.spring.ApplicationContextHelper;
import com.yd.svrplatform.util.ReadProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Repository;

@Repository
public class IshInit implements ApplicationListener<ApplicationEvent> {
	    private static boolean _isFirst=true;
	    private static final Logger logger = LoggerFactory.getLogger(IshInit.class);
		private static final String dataanalysis_flag = ReadProperty.getString("dataanalysis_flag");
	    @Override
	    public void onApplicationEvent(ApplicationEvent event) { 
	        if(!_isFirst) return;
	        set_isFirst(false);
//	        _isFirst=false;
	        load();
	    }
	@DependsOn({"InitAllFuncToMemcached","InitTJFuncToMemcached"})
	    public static void load(){
	       logger.info("[+]IshInit.load");
	       
	       ParamConfigImp paramConfigImp=(ParamConfigImp) ApplicationContextHelper.getBean("paramConfigImp");
	       paramConfigImp.load();
//	       TransEngine.getInstance().execute("SPNRJS", mainContext);
	        logger.info("[-]IshInit.load");

//			ServletContext context = this.getServletConfig().getServletContext();
//			YdpxUtil.setContexPath(context.getContextPath());
//			YdpxUtil.setWebRoot(context.getRealPath("/"));
//			logger.info("YdpxInitServlet: ContexPath = " + YdpxUtil.getContexPath());
//			logger.debug("YdpxInitServlet: ContexPath = " + YdpxUtil.getContexPath());
 			logger.info("[+]Insert Page info.load");
//	        nrjsService.insertPageInfo();
	        logger.info("[-]Insert Page info.load");
	        if("1".equals(dataanalysis_flag)){
				logger.info("[+]IshInit.InitAllFuncToMemcached ");
				//初始化菜单信息到memcached
				InitAllFuncToMemcached initAllFuncToMemcached=(InitAllFuncToMemcached) ApplicationContextHelper.getBean("InitAllFuncToMemcached");
				initAllFuncToMemcached.initAllFuncToMemcached();
				logger.info("[-]IshInit.InitAllFuncToMemcached ");
				logger.info("[+]IshInit.InitTJFuncToMemcached ");
				//初始化推荐信息到memcached
				InitTJFuncToMemcached initTJFuncToMemcached=(InitTJFuncToMemcached) ApplicationContextHelper.getBean("InitTJFuncToMemcached");
				initTJFuncToMemcached.initTJFuncToMemcached();
				logger.info("[-]IshInit.InitTJFuncToMemcached ");
			}
	    }
	    
		public static boolean is_isFirst() {
			return _isFirst;
		}
		public static void set_isFirst(boolean _isFirst) {
			IshInit._isFirst = _isFirst;
		}
}
