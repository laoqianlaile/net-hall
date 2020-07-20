package com.yd.ish.biz.comp.ln;

import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.biz.comp.BaseComp;

import java.util.Map;

/**
 * 名称：CompXMXQ01
 * <p>功能：获取开发商信息<br>
 * @brief 获取开发商信息
 * @author 柏慧敏
 * @version V0.1 柏慧敏 2019-06-04 长春 新建
 * @note
 */
@Component("CompXMXQ01")
public class CompXMXQ01 extends BaseComp{

	private static final Logger logger = LoggerFactory.getLogger(CompXMXQ01.class);

	@Override
	public int execute() {

		/*入口参数赋值开始*/
    	/*入口参数赋值结束*/
		//调用接口获取开发商信息
		XmlResObj data = super.sendExternal("BSP_LN_GETKFSXX_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String, Object> body = data.getBody();
		String kfsmc = body.get("kfsmc") == null ? "" : body.get("kfsmc").toString();
		String dwlx = body.get("dwlx") == null ? "" : body.get("dwlx").toString();
		String zcdz = body.get("zcdz") == null ? "" : body.get("zcdz").toString();
		String bgdz = body.get("bgdz") == null ? "" : body.get("bgdz").toString();
		String dwfrdbxm = body.get("dwfrdbxm") == null ? "" : body.get("dwfrdbxm").toString();
		String dwfrdbzjlx = body.get("dwfrdbzjlx") == null ? "" : body.get("dwfrdbzjlx").toString();
		String dwfrdbzjhm = body.get("dwfrdbzjhm") == null ? "" : body.get("dwfrdbzjhm").toString();
		String sjhm = body.get("sjhm") == null ? "" : body.get("sjhm").toString();
		String dwdh = body.get("dwdh") == null ? "" : body.get("dwdh").toString();
		String zczb = body.get("zczb") == null ? "" : body.get("zczb").toString();
		String dwslrq = body.get("dwslrq") == null ? "" : body.get("dwslrq").toString();
		String kfzz = body.get("kfzz") == null ? "" : body.get("kfzz").toString();
		String tyshxydm = body.get("tyshxydm") == null ? "" : body.get("tyshxydm").toString();
		String jgxydm = body.get("jgxydm") == null ? "" : body.get("jgxydm").toString();
		String sfsqbzj = body.get("sfsqbzj") == null ? "" : body.get("sfsqbzj").toString();
		String bzjbl = body.get("bzjbl") == null ? "" : body.get("bzjbl").toString();
		String jyfw = body.get("jyfw") == null ? "" : body.get("jyfw").toString();

    	/*出口参数赋值开始*/
		setOutParam("kfsmc",kfsmc);//开发商名称
		setOutParam("dwlx",dwlx);//单位类型
		setOutParam("zcdz",zcdz);//注册地址
		setOutParam("bgdz",bgdz);//办公地址
		setOutParam("dwfrdbxm",dwfrdbxm);//单位法人代表姓名
		setOutParam("dwfrdbzjlx",dwfrdbzjlx);//单位法人代表证件类型
		setOutParam("dwfrdbzjhm",dwfrdbzjhm);//单位法人代表证件号码
		setOutParam("sjhm",sjhm);//手机号码
		setOutParam("dwdh",dwdh);//单位电话
		setOutParam("zczb",zczb);//注册资本
		setOutParam("dwslrq",dwslrq);//单位设立日期
		setOutParam("kfzz",kfzz);//开发资质
		setOutParam("tyshxydm",tyshxydm);//统一社会信用代码
		setOutParam("jgxydm",jgxydm);//机构信用代码
		setOutParam("sfsqbzj",sfsqbzj);//是否收取保证金
		setOutParam("bzjbl",bzjbl);//保证金比例
		setOutParam("jyfw",jyfw);//经营范围
    	/*出口参数赋值结束*/

    	return 0;
   }

}
