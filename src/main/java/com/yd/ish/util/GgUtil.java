package com.yd.ish.util;

import com.alibaba.druid.util.StringUtils;
import com.yd.biz.exception.TransSingleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 名称：GgUtil
 * <p>
 * 功能： 内管公共工具类<br>
 * 
 * @author 陈海冬
 * @version 0.1 陈海冬 2016-03-16 长春 新增
 */

public class GgUtil {

	private static final Logger logger = LoggerFactory.getLogger(GgUtil.class);

	/** 成功失败-1-成功 */
	// private static final int RESULT_SUC = 1;
	/** 成功失败-0-成功 */
	// private static final int RESULT_ERR = 0;

	/**
	 * StrToDate 字符串转换成日期.
	 * <p>
	 * <br>
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param dateFormat
	 *            日期字符串日期格式
	 * @throws TransSingleException
	 * @return Date 返回转换后的日期
	 */
	public static Date strToDate(String dateStr, String dateFormat) throws TransSingleException {
		if (StringUtils.isEmpty(dateStr)) {
			return null;
		}
		Date date = null;
		SimpleDateFormat formatter;
		formatter = new SimpleDateFormat(dateFormat);
		try {
			date = formatter.parse(dateStr);
		} catch (ParseException e) {
			logger.debug("日期转换失败");
			e.printStackTrace();
			throw new TransSingleException("日期格式转换失败");
		}
		return date;
	}

	/**
	 * DateToStr 日期格式化成字符串
	 * <p>
	 * DateToStr 日期格式化成字符串<br>
	 * 
	 * @param date
	 *            要转换的日期
	 * @param dateFormat
	 *            转换成字符串的格式
	 * @return
	 */
	public static String dateToStr(Date date, String dateFormat) {
		if (date == null) {
			return "";
		}
		String dateStr = "";
		SimpleDateFormat dateFormater = new SimpleDateFormat(dateFormat);
		dateStr = dateFormater.format(date);
		return dateStr;
	}

	/**
	 * 数字金额大写转换，思想先写个完整的然后将如零拾替换成零 要用到正则表达式
	 */
	public static String digitUppercase(double n) {
		if (n == 0) {
			return "";
		}
		String fraction[] = { "角", "分" };
		String digit[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
		String unit[][] = { { "元", "万", "亿" }, { "", "拾", "佰", "仟" } };

		String head = n < 0 ? "负" : "";
		n = Math.abs(n);

		String s = "";
		for (int i = 0; i < fraction.length; i++) {
			s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
		}
		if (s.length() < 1) {
			s = "整";
		}
		int integerPart = (int) Math.floor(n);

		for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
			String p = "";
			for (int j = 0; j < unit[1].length && n > 0; j++) {
				p = digit[integerPart % 10] + unit[1][j] + p;
				integerPart = integerPart / 10;
			}
			s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
		}
		return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$",
				"零元整");
	}

	/**
	 * 身份证号 15转18位
	 */
	final static int[] wi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
	static int[] vi = { 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 };
	private static int[] ai = new int[18];

	public static String uptoeighteen(String fifteencardid) {
		String eightcardid = fifteencardid.substring(0, 6);
		eightcardid = eightcardid + "19";
		eightcardid = eightcardid + fifteencardid.substring(6, 15);
		eightcardid = eightcardid + getVerify(eightcardid);
		return eightcardid;
	}

	public static String getVerify(String eightcardid) {
		int remaining = 0;
		if (eightcardid.length() == 18) {
			eightcardid = eightcardid.substring(0, 17);
		}
		if (eightcardid.length() == 17) {
			int sum = 0;
			for (int i = 0; i < 17; i++) {
				String k = eightcardid.substring(i, i + 1);
				ai[i] = Integer.parseInt(k);
			}
			for (int i = 0; i < 17; i++) {
				sum = sum + wi[i] * (ai[i]);
			}
			remaining = sum % 11;
		}
		return remaining == 2 ? "X" : String.valueOf(vi[remaining]);
	}
}
