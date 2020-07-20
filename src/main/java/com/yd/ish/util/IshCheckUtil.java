package com.yd.ish.util; 

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.yd.biz.util.PatternUtil;

public class IshCheckUtil {
	
	 /**
     * 校验身份证号码
     * @param str 身份证号
     * @return 身份证号错误信息
     */
	public static String checkIdCard(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		int len = str.length();
		if (len != 18)
			return "身份证号长度不正确";

		str = str.toUpperCase();
		String ruleStr = "^(\\d{15}|\\d{17}(\\d|X))$";
		if (PatternUtil.runRule(str, ruleStr)) {
			String strDate = (len == 15) ? "19" + str.substring(6, 12) : str.substring(6, 14);
			try {
				Calendar cal = Calendar.getInstance();
				cal.setTime(sdf.parse(strDate));
				if ((cal.get(5) != Integer.parseInt(strDate.substring(6, 8)))
						|| (cal.get(2) + 1 != Integer.parseInt(strDate.substring(4, 6)))
						|| (cal.get(1) != Integer.parseInt(strDate.substring(0, 4))))
					return "身份证号日期不正确";
			} catch (ParseException e) {
				return "身份证号日期格式不正确";
			}
			if (len == 18) {
				int[] s1 = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
				char[] s2 = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
				int s = 0;
				for (int i = 0; i < 17; ++i)
					s += Integer.parseInt(str.substring(i, i + 1)) * s1[i];
				if (s2[(s % 11)] != str.charAt(17))
					return "身份证号校验位不正确";
			}
			return "";
		}
		return "身份证号格式不正确";
	}
	
}

