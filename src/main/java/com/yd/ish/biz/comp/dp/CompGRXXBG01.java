package com.yd.ish.biz.comp.dp;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.yd.basic.expression.IshExpression;
import com.yd.biz.comp.BaseComp;
import com.yd.biz.exception.TransSingleException;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResHead;
import com.yd.svrplatform.comm_mdl.external.parse.XmlResObj;
import com.yd.svrplatform.util.DataPool;

/**
 * 名称：CompGRXXBG01
 * <p>
 * 功能：获取个人基本信息<br>
 * 
 * @brief 功能简述 获取个人信息
 * @author 许永峰
 * @version 版本号 修改人 修改时间 地点 原因
 * @note 0.1 许永峰 2018年7月31日 长春 创建
 */
@Component("CompGRXXBG01")
public class CompGRXXBG01 extends BaseComp {

	private static final Logger logger = LoggerFactory.getLogger(CompGRXXBG01.class);

	@Override
	public int execute() {
		/* 入口参数赋值开始 */
		String grzh=getString("grzh");
		/* 入口参数赋值开始 */
		logger.info("[+]调用接口获取个人基本信息开始");
		DataPool pool = super.getMainContext().getDataPool();
		//若入口参数grzh不为空，将grzh的值赋给接口上传参数
		if(grzh != null && !"".equals(grzh)){
			pool.put("grzh", grzh);
		}
		//若入口参数为空，将user中的个人账号赋给接口上传参数
		else{
			pool.put("grzh", IshExpression.getRealUserExtInfo("grzh"));
		}
		// 调用接口获取个人基本信息
		XmlResObj data = super.sendExternal("BSP_DP_GETGRXX_01");
		XmlResHead head = data.getHead();
		if (!XmlResHead.TR_SUCCESS.equals(head.getParticular_code())) {
			throw new TransSingleException(head.getParticular_info());
		}
		Map<String, Object> body = data.getBody();
		
		String xingbie = returnString(body.get("xingbie"));
		String hj = returnString(body.get("hj"));
		String gddhhm = returnString(body.get("gddhhm"));
		String csny = returnString(body.get("csny"));
		String hyzk = returnString(body.get("hyzk"));
		String zhiye = returnString(body.get("zhiye"));
		String zhichen = returnString(body.get("zhichen"));
		String zhiwu = returnString(body.get("zhiwu"));
		String xueli = returnString(body.get("xueli"));
		String yzbm = returnString(body.get("yzbm"));
		String jtzz = returnString(body.get("jtzz"));
		String jtysr = returnString(body.get("jtysr"));
		String zjhm = returnString(body.get("zjhm"));
		String zjlx = returnString(body.get("zjlx"));
		String xingming = returnString(body.get("xingming"));
		String sjhm = returnString(body.get("sjhm"));
		String grjcjs = returnString(body.get("grjcjs"));
		logger.info("[-]调用接口获取个人基本信息结束");

		/* 出口参数赋值开始 */
		setOutParam("xingbie", xingbie);// 性别
		setOutParam("hj", hj);// 户籍
		setOutParam("gddhhm", gddhhm);// 固定电话号码
		setOutParam("csny", csny);// 出生年月
		setOutParam("hyzk", hyzk);// 婚姻状况
		setOutParam("zhiye", zhiye);// 职业
		setOutParam("zhichen", zhichen);// 职称
		setOutParam("zhiwu", zhiwu);// 职务
		setOutParam("xueli", xueli);// 学历
		setOutParam("yzbm", yzbm);// 邮政编码
		setOutParam("jtzz", jtzz);// 家庭住址
		setOutParam("jtysr", jtysr);// 家庭月收入
		setOutParam("zjlx", zjlx);// 证件类型
		setOutParam("xingming", xingming);// 姓名
		setOutParam("zjhm", zjhm);// 证件号码
		setOutParam("sjhm", sjhm);// 手机号码
		setOutParam("grjcjs", grjcjs);//个人缴存基数
		/* 出口参数赋值结束 */
		return 0;
	}
	public String returnString(Object str){
		return str ==null || str.toString().isEmpty() ? "" : str.toString();
	}
}
