package com.yd.ish.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MoneyUtil {
	
	 /**
     * 金额增加千分符
     * @param je 金额
     * @return 增加千分符后金额
     */
	public static String addComma(String je){
		//给金额增加千分符。
		BigDecimal money = new BigDecimal(je);		      
		DecimalFormat d1 =new DecimalFormat("#,##0.00");
		return d1.format(money);
	}
	
	 /**
     * 金额小写转大写
     * @param je 金额
     * @return 大写金额
     */
	public static String numberToChineseRMB(String je) {
		// 大写
		String[] CurrNumDx = new String[] { "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "零" };
		// 小写
		String[] CurrNumXx = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
		// 整数部分单位
		String[] CurrUnitZs = new String[] { "仟", "佰", "拾", "亿", "仟", "佰", "拾", "万", "仟", "佰", "拾", "元" };
		// 小数部分单位
		String[] CurrUnitXs = new String[] { "角", "分" };
		// 将字符串转成double类型避免整数缺少小数点
		double dJine = Double.parseDouble(je);
		// 格式化金额,避免科学计数
		DecimalFormat format = new DecimalFormat("#0.00");
		String strJine = format.format(dJine);
		// 以小数点拆分整数和小数两部分,存入数组
		String strArrJine[] = strJine.split("\\.");
		// 拼接整数部分大写字符串
		String pjZhengshu = "";
		// 拼接小数部分大写字符串
		String pjXiaoshu = "";

		// 遍历整数部分各数字
		StringBuffer sbdx = new StringBuffer();
		for (int i = 0; i < strArrJine[0].length(); i++) {
			// 处理整数部分
			String tmpZhengshu = strArrJine[0].substring(i, i + 1);
			for (int j = 0; j < CurrNumXx.length; j++) {
				// 如果数字等于CurrNumXx里的元素
				if (CurrNumXx[j].equals(tmpZhengshu)) {
					// 得到元拾佰仟等货币单位
					String tmpZs = CurrUnitZs[CurrUnitZs.length - strArrJine[0].length() + i];
					// 拼接整数部分
					sbdx.append(CurrNumDx[j]);
					sbdx.append(tmpZs);
					// 满足条件退出内循环
					break;
				}
			}
		}
		pjZhengshu = sbdx.toString();
		// 按照书写习惯格式化字符串
		pjZhengshu = pjZhengshu.replace("零拾", "零");
		pjZhengshu = pjZhengshu.replace("零佰", "零");
		pjZhengshu = pjZhengshu.replace("零仟", "零");
		pjZhengshu = pjZhengshu.replace("零零零", "零");
		pjZhengshu = pjZhengshu.replace("零零", "零");
		pjZhengshu = pjZhengshu.replace("零万", "万");
		pjZhengshu = pjZhengshu.replace("零亿", "亿");
		pjZhengshu = pjZhengshu.replace("零元", "元");

		if (pjZhengshu.equals("元")) {
			pjZhengshu = "零元";
		}
		if ((pjZhengshu.equals("零元") == true) && (strArrJine[1].equals("00") == false)) {
			pjZhengshu = "";
		}
		// 遍历小数部分各数字
		StringBuffer sbxx = new StringBuffer();
		for (int i = 0; i < strArrJine[1].length(); i++) {
			// 处理小数部分
			String tmpXiaoshu = strArrJine[1].substring(i, i + 1);
			
			for (int j = 0; j < CurrNumXx.length; j++) {
				// 如果数字等于CurrNumXx里的元素
				if (CurrNumXx[j].equals(tmpXiaoshu)) {
					// 得到角分货币单位
					String tmpXs = CurrUnitXs[CurrUnitXs.length - strArrJine[1].length() + i];
					// 拼接小数部分
					sbxx.append(CurrNumDx[j]);
					sbxx.append(tmpXs);
					// 满足条件退出内循环
					break;
				}
			}
		}
		pjXiaoshu = sbxx.toString();
		pjXiaoshu = pjXiaoshu.replace("零分", "");
		pjXiaoshu = pjXiaoshu.replace("零角", "");
		// 形如1.03,角位插入“零”
		if ((pjZhengshu.indexOf("元") > 0)
				&& (pjXiaoshu.indexOf("分") > 0)
				&& (pjXiaoshu.indexOf("角") == -1)) {
			pjXiaoshu = "零" + pjXiaoshu;
		}

		// 整数部分和小数部分拼接
		String ResultStrJiner = pjZhengshu + pjXiaoshu;
		// 小数不是以分结尾加上“整”
		if (!ResultStrJiner.endsWith("分")) {
			ResultStrJiner += "整";
		}

		return ResultStrJiner;
	}
}
