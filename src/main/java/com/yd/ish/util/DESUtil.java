package com.yd.ish.util; 

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

public class DESUtil {

	private static int pc_1_cp[] = { 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36 };
	private static int pc_1_dp[] = { 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4 };
	private static int pc_2p[] = { 14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50,
			36, 29, 32 };

	private static int ls_countp[] = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };

	private static int[][] C = new int[17][28];
	private static int[][] D = new int[17][28];
	private static int[][] K = new int[17][48];

	private static byte[] FDES(byte[] key, byte[] text) {
		int[] tmp = new int[64];
		tmp = Fexpand0(key);
		Fsetkeystar(tmp);
		return Fencrypt0(text);
	}

	private static int[] Fexpand0(byte[] in) {
		int divide;
		int i, j;
		int[] out = new int[64];
		for (i = 0; i < 8; i++) {
			divide = 7;
			for (j = 0; j < 8; j++) {
				out[8 * i + j] = (in[i] >> divide) & 1;
				divide--;
			}
		}
		return out;
	}

	private static void Fsetkeystar(int[] bits) {
		int i, j;

		for (i = 0; i < 28; i++)
			C[0][i] = bits[pc_1_cp[i] - 1];
		for (i = 0; i < 28; i++)
			D[0][i] = bits[pc_1_dp[i] - 1];
		for (j = 0; j < 16; j++) {
			FLS(C[j], C[j + 1], ls_countp[j]);
			FLS(D[j], D[j + 1], ls_countp[j]);
			Fson(C[j + 1], D[j + 1], K[j + 1]);
		}
	}

	private static void FLS(int[] bits, int[] buffer, int count) {
		int i;
		for (i = 0; i < 28; i++) {
			buffer[i] = bits[(i + count) % 28];
		}
	}

	private static void Fson(int[] cc, int[] dd, int[] kk) {
		int i;
		int[] buffer = new int[56];
		for (i = 0; i < 28; i++)
			buffer[i] = cc[i];

		for (i = 28; i < 56; i++)
			buffer[i] = dd[i - 28];

		for (i = 0; i < 48; i++)
			kk[i] = buffer[pc_2p[i] - 1];
	}

	private static byte[] Fencrypt0(byte[] text) {
		int[] ll = new int[64];
		int[] rr = new int[64];
		int[] LL = new int[64];
		int[] RR = new int[64];
		int[] tmp = new int[64];
		int i, j;
		Fiip(text, ll, rr);

		for (i = 1; i < 17; i++) {
			FF(i, ll, rr, LL, RR);
			for (j = 0; j < 32; j++) {
				ll[j] = LL[j];
				rr[j] = RR[j];
			}
		}

		_Fiip(tmp, rr, ll);

		return Fcompress0(tmp);
	}

	private static byte[] Fcompress0(int[] out) {
		int times;
		int i, j;
		byte[] in = new byte[8];
		for (i = 0; i < 8; i++) {
			times = 7;
			in[i] = 0;
			for (j = 0; j < 8; j++) {
				in[i] = (byte) (in[i] + (out[i * 8 + j] << times));
				times--;
			}
		}
		return in;
	}

	private static int iip_tab_p[] = { 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43,
			35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7 };
	private static int _iip_tab_p[] = { 40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43,
			11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25 };

	private static void Fiip(byte[] text, int[] ll, int[] rr) {
		int i;
		int[] buffer = new int[64];
		buffer = Fexpand0(text);

		for (i = 0; i < 32; i++)
			ll[i] = buffer[iip_tab_p[i] - 1];

		for (i = 0; i < 32; i++)
			rr[i] = buffer[iip_tab_p[i + 32] - 1];
	}

	private static int e_r_p[] = { 32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31,
			32, 1 };

	private static int local_PP[] = { 16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25 };
	private static int ccom_SSS_p[][][] = {
			{ { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 }, { 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 }, { 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },
					{ 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 } },
			{ { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10 }, { 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 }, { 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },
					{ 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 } },
			{ { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 }, { 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 }, { 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },
					{ 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 } },
			{ { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 }, { 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 }, { 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },
					{ 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 } },
			{ { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 }, { 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 }, { 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },
					{ 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 } },
			{ { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 }, { 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 }, { 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },
					{ 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 } },
			{ { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 }, { 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 }, { 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },
					{ 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 } },
			{ { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 }, { 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 }, { 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },
					{ 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } } };

	private static void FF(int n, int[] ll, int[] rr, int[] LL, int[] RR) {
		int i;
		int[] buffer = new int[64];
		int[] tmp = new int[64];
		for (i = 0; i < 48; i++)
			buffer[i] = rr[e_r_p[i] - 1];
		for (i = 0; i < 48; i++)
			buffer[i] = (buffer[i] + K[n][i]) & 1;

		Fs_box(buffer, tmp);

		for (i = 0; i < 32; i++)
			buffer[i] = tmp[local_PP[i] - 1];

		for (i = 0; i < 32; i++)
			RR[i] = (buffer[i] + ll[i]) & 1;

		for (i = 0; i < 32; i++)
			LL[i] = rr[i];
	}

	private static void Fs_box(int[] aa, int[] bb) {
		int i, j, k, m;
		int y, z;
		int[] ss = new int[8];
		m = 0;
		for (i = 0; i < 8; i++) {
			j = 6 * i;
			y = aa[j] * 2 + aa[j + 5];
			z = aa[j + 1] * 8 + aa[j + 2] * 4 + aa[j + 3] * 2 + aa[j + 4];
			ss[i] = ccom_SSS_p[i][y][z];
			y = 3;
			for (k = 0; k < 4; k++) {
				bb[m++] = (ss[i] >> y) & 1;
				y--;
			}
		}
	}

	private static void _Fiip(int[] text, int[] ll, int[] rr) {
		int i;
		int[] tmp = new int[64];
		for (i = 0; i < 32; i++)
			tmp[i] = ll[i];
		for (i = 32; i < 64; i++)
			tmp[i] = rr[i - 32];
		for (i = 0; i < 64; i++)
			text[i] = tmp[_iip_tab_p[i] - 1];
	}

	/*
	 * 加密和解密采用定长处理模式(每次处理8个字符)
	 */

	/* 加密 */
	private static byte[] ENCRYPT(byte[] key, byte[] s, int len) {
		int i;
		
		//将明文长度除于8并向上舍入，将结果乘以8作为结果的byte数组的大小
		double factor = (double) len / 8.0d;
		BigDecimal bigDecimal = new BigDecimal( factor ).setScale( 0, BigDecimal.ROUND_UP );
		int size = bigDecimal.intValue() * 8;
		
		byte[] result = new byte[size];
		for (i = 0; i < len; i += 8) {
			byte[] ss = new byte[8];
			if( len - i < 8 ) {
				System.arraycopy( s, i, ss, 0, len - i );
			} else {
				System.arraycopy(s, i, ss, 0, 8);
			}
			
			byte[] ret = FDES(key, ss);
			//System.arraycopy(ret, 0, result, i, 8);
			System.arraycopy( ret, 0, result, i, ret.length );
		}
		return result;
	}
	
	/* 解密 */
	private static byte[] DECRYPT(byte[] key, byte[] s, int len) {
		int i;
		byte[] result = new byte[len];
		for (i = 0; i < len; i += 8) {
			byte[] ss = new byte[8];
			System.arraycopy(s, i, ss, 0, 8);
			byte[] ret = _FDES(key, ss);
			System.arraycopy(ret, 0, result, i, 8);
		}
		return result;
	}

	private static byte[] _FDES(byte[] key, byte[] mtext) {
		int[] tmp = new int[64];
		tmp = Fexpand0(key);
		Fsetkeystar(tmp);
		return Fdiscrypt0(mtext);
	}

	private static byte[] Fdiscrypt0(byte[] mtext) {
		int[] ll = new int[64];
		int[] rr = new int[64];
		int[] LL = new int[64];
		int[] RR = new int[64];
		int[] tmp = new int[64];
		int i, j;
		Fiip(mtext, ll, rr);

		for (i = 16; i > 0; i--) {
			FF(i, ll, rr, LL, RR);
			for (j = 0; j < 32; j++) {
				ll[j] = LL[j];
				rr[j] = RR[j];
			}
		}

		_Fiip(tmp, rr, ll);

		return Fcompress0(tmp);
	}

	/*** 0 加密，1 解密 
	 * @throws UnsupportedEncodingException ***/
	private static byte[] reportdes(byte[] souchar, byte[] key, int len, int flag) throws UnsupportedEncodingException {
		byte[] workingkey = new byte[17];
		byte[] acKey = new byte[17];
		int i;

		for (byte b : acKey) {
			b = 0x00;
		}
		
		for (byte b : workingkey) {
			b = 0x00;
		}
		
		if (key == null || key[0] == 0){
			byte[] defaultKey = "12345678".getBytes();
			System.arraycopy(defaultKey, 0, acKey, 0, 8);
		}else{
			System.arraycopy(key, 0, acKey, 0, key.length);
		}
		
		i = acKey.length;
		asc_to_bcd(workingkey, acKey, i, 0);

		if (flag == 0)
			return ENCRYPT(workingkey, souchar, len);
		else
			return DECRYPT(workingkey, souchar, len);
	}

	
	private static void asc_to_bcd (byte[] bcd_buf ,byte[] ascii_buf ,int conv_len ,int type)
	{
		int cnt ;
		int cnt2 = 0;
		char ch,ch1 ;
		
		if ((conv_len & 0x01)==0 && type==0)
			ch1 = 0 ;
		else
			ch1 = 0x55 ;
		for ( cnt = 0 ; cnt < conv_len ; cnt++ ) {
			if ( ascii_buf[cnt] >= 'a' ){
				ch = (char) (ascii_buf[cnt] - 'a' + 10) ;
			}
			else if ( ascii_buf[cnt] >= 'A' ){
				ch = (char) (ascii_buf[cnt]- 'A' + 10) ;
			}
			else if ( ascii_buf[cnt] >= '0' ){
				ch = (char) (ascii_buf[cnt]-'0') ;
			}
			else{
				ch = 0;
			}
			if (ch1 == 0x55){
				ch1 = ch ;
			}else {
				bcd_buf[cnt2++] = (byte) ((ch1 << 4) | ch) ;
				ch1 = 0x55 ;
			}
		}
		if ( ch1 != 0x55 )
			bcd_buf[cnt2] = (byte) (ch1 << 4 | 0x0F) ;
		return;
	}
	
	private static byte asc_to_bcd(byte asc) {
		byte bcd;

		if ((asc >= '0') && (asc <= '9'))
			bcd = (byte) (asc - '0');
		else if ((asc >= 'A') && (asc <= 'F'))
			bcd = (byte) (asc - 'A' + 10);
		else if ((asc >= 'a') && (asc <= 'f'))
			bcd = (byte) (asc - 'a' + 10);
		else
			bcd = (byte) (asc - 48);
		return bcd;
	}

	public static byte[] asc_to_bcd(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = asc_to_bcd(ascii[j++]);
			bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
		}
		return bcd;
	}
	
	public static byte[] bcd_to_asc(byte[] bytes) {
		char temp[] = new char[bytes.length * 2], val;
		byte result[] = new byte[bytes.length * 2];

		for (int i = 0; i < bytes.length; i++) {
			val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

			val = (char) (bytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		
		for(int i=0;i<temp.length;i++){
			result[i]=(byte)temp[i];
		}
		
		return result;
	}
	
	/**
	 * 密钥的加密解密
	 * @param souchar
	 * @param key
	 * @param len
	 * @param flag 0加密,1解密
	 * @return
	 */
	public static byte[] keydes(byte[] souchar , byte[] key , int len , int flag)
	{

		int 	i=0 ;
		byte[]	aa = new byte[17] ;
		byte[]	bb = new byte[17] ;

		for(byte b : aa){
			b = 0x00;
		}
		for(byte b : bb){
			b = 0x00;
		}

		if(flag == 0) {
			byte[] destchar = ENCRYPT(key, souchar, len);
			aa = bcd_to_asc(destchar);
			return aa;
		}
		else {
			i = souchar.length;
			bb = asc_to_bcd(souchar,i) ;
			byte[] destchar = DECRYPT( key, bb, len ) ;
			return destchar;
		}
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		//ICBC:51248960/5BF07D2A32DF838B
		//CCB :96853724/888133EEFF7E6813
		// 密钥的加密解密
//		byte[] encKey = DESUtil.keydes("12345678".getBytes(), "12345678".getBytes(), 8, 0);
//		String encKeyS = new String(encKey);
//		System.out.println(">>>"+encKeyS);
//		byte[] decKey = DESUtil.keydes(encKeyS.getBytes(), "12345678".getBytes(), 8, 1);
//		System.out.println(new String(decKey));
		
		String key = "YDISH";
		String encStr="{\"loginKey\":\"5e54c044-c436-11e7-9495-00ffaabbccdd\",\"sfzh\":\"220802198509202443\",\"xingming\":\"工期\",\"returnCode\":\"0\",\"message\":\"成功\"}";
		byte[] b = DESUtil.encryption(encStr, key);
		byte[] c=bcd_to_asc(b);
		String loginKey_mw = new String(c, "utf-8");
		System.out.println(">>>>>"+loginKey_mw);
		byte[] midbytes = loginKey_mw.getBytes("utf-8");
		int i = midbytes.length;
		byte[] d=asc_to_bcd(midbytes,i) ;
		String kk = DESUtil.decryption(d, key);
		System.out.println(">>>>>"+kk);
		
//		String kkk = "<?xml version=\"1.0\" encoding=\"GBK\"?><CMS><eb><pub><TransCode>Q00021</TransCode><TranDate>20130525</TranDate><TranTime>072639566000</TranTime><SerialNo>0000117164</SerialNo></pub><in><Accnum>1614028409200102561</Accnum><ReqReserved1></ReqReserved1><ReqReserved2></ReqReserved2></in></eb></CMS>";
//		b = DESUtil.encryption(kkk, "1234567812345678");
//		kk = DESUtil.decryption(b, "1234567812345678");
//		System.out.println(b.length);
//		System.out.println(kk);
		
	}
	
	/**
	 * 加密
	 * @param msg 明文
	 * @param key 密钥
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] encryption(String msg,String key) throws UnsupportedEncodingException{
		//[padding模式]:明文长度,不是8的倍数的,需要用空格补齐
		/*if(msg.getBytes().length%8!=0){
			msg = msg + "        ".substring(msg.getBytes().length%8);
		}*/
		byte[] ret = reportdes(msg.getBytes("utf-8"),key.getBytes("utf-8"),msg.getBytes("utf-8").length,0);
		return ret;
	} 
	
	/**
	 * 解密
	 * @param msg 明文
	 * @param key 密钥
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String decryption(byte[] msg,String key) throws UnsupportedEncodingException{
		byte[] ret = reportdes(msg,key.getBytes("utf-8"),msg.length,1);
		return (new String(ret,"utf-8")).trim();
	}
	
}

