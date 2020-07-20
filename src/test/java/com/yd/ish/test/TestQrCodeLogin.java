package com.yd.ish.test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.yd.ish.common.util.Httpclient;
import com.yd.ish.util.DESUtil;

public class TestQrCodeLogin {
	public static void main(String[] args) {
//		String loginkey = "7eca9358-c4fb-11e7-85ff-00ffaabbccdd";
//		String key = "YDISH";
//		String encStr="{\"loginKey\":\""+loginkey+"\",\"sfzh\":\"220802198509202443\",\"xingming\":\"于美存\",\"returnCode\":\"0\",\"message\":\"成功\"}";
//		byte[] b;
//		try {
//			b = DESUtil.encryption(encStr, key);
//			byte[] c=DESUtil.bcd_to_asc(b);
//			String loginKey_mw = new String(c, "utf-8");
//			System.out.println(">>>>>"+loginKey_mw);
//			
//			String sr=Httpclient.sendPost("http://localhost:8080/ish/login/person/qrCodeLoginInfo",
//					"aa=" + loginKey_mw);
//			String a = URLEncoder.encode(sr, "utf-8");
//			System.out.println(a);
//			try {
//				Thread.sleep(1000*3);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
////			String sr1=Httpclient.sendPost("http://localhost:8080/ish/login/person/qrCodeLoginStatus",
////					"loginKey=" + loginkey+"&loginStatus=0&returnCode=0");
////			String a1 = URLEncoder.encode(sr1, "utf-8");
////			System.out.println(a1);
//
//			byte[] midbytes = loginKey_mw.getBytes("utf-8");
//			int i = midbytes.length;
//			byte[] d=DESUtil.asc_to_bcd(midbytes,i) ;
//			String kk = DESUtil.decryption(d, key);
//			System.out.println(">>>>>"+kk);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		String a = "17;08:00;09:00;夏季时间段";
		String[] aa =a.split(";");
		System.out.println(aa[0]);
	}
}
