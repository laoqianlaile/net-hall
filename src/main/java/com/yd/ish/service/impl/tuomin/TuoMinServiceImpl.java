package com.yd.ish.service.impl.tuomin;

import com.yd.ish.common.interfaces.ITuoMinService;
import com.yd.ish.common.util.StringUtils;
import com.yd.svrplatform.util.ReadProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
/**
 *  名称：TuoMinServiceImpl.java  数据脱敏实现类
 *  * <p>功能：获取数据脱敏后的值 <br>
 *  * @author 许永峰
 *  * @version 0.1	2019年4月02日	许永峰创建
 */
@Service("TuoMinServiceImpl")
public class TuoMinServiceImpl implements ITuoMinService {
    private static final Logger logger = LoggerFactory.getLogger(TuoMinServiceImpl.class);
    //中文名称
    public static final String CHINESE_NAME = "CHINESE_NAME";
    //身份证号
    public static final String ID_CARD = "ID_CARD";
    //固定电话
    public static final String FIXED_PHONE = "FIXED_PHONE";
    //手机号码
    public static final String MOBILE_PHONE = "MOBILE_PHONE";
    //地址
    public static final String ADDRESS = "ADDRESS";
    //电子邮箱
    public static final String EMAIL = "EMAIL";
    //银行卡号
    public static final String BANK_CARD = "BANK_CARD";
    //个人账号
    public static final String PERSON_ACCOUNT = "PERSON_ACCOUNT";
    //单位账号
    public static final String ORG_ACCOUNT = "ORG_ACCOUNT";
    //地址脱敏敏感信息长度
    public static final int   tuomin_address_length = 8;
    @Override
    public  String getTMValue(String value, String tmgz) {
        switch (tmgz) {
            case CHINESE_NAME: {
                logger.info("脱敏之后的值："+chineseName(value));
                return chineseName(value);
            }
            case ID_CARD: {
                return idCardNum(value);
            }
            case FIXED_PHONE: {
                logger.info("脱敏之后的值："+fixedPhone(value));
                return fixedPhone(value);
            }
            case MOBILE_PHONE: {
                logger.info("脱敏之后的值："+mobilePhone(value));
                return mobilePhone(value);
            }
            case ADDRESS: {
                String addressLength = ReadProperty.getString("address_length");
                logger.info("脱敏之后的值："+address(value, tuomin_address_length));
                return address(value, tuomin_address_length);
            }
            case EMAIL: {
                logger.info("脱敏之后的值："+email(value));
                return email(value);
            }
            case BANK_CARD: {
                logger.info("脱敏之后的值："+bankCard(value));
                return bankCard(value);
            }
            case PERSON_ACCOUNT: {
                logger.info("脱敏之后的值："+personAccount(value));
                return personAccount(value);
            }
            case ORG_ACCOUNT: {
                logger.info("脱敏之后的值："+orgAccount(value));
                return orgAccount(value);
            }
            default:
                logger.info("未找到脱敏规则");
                return value;
        }

    }
    /**
     * 【中文姓名】只显示第一个汉字，其他隐藏为2个星号，比如：李**
     *
     * @param fullName
     * @return
     */
    public static String chineseName(String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return "";
        }
        String name = StringUtils.left(fullName, 1);
        return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
    }

    /**
     * 【身份证号】显示前六位最、后四位，其他隐藏。共计18位或者15位，比如：220102********1234
     *
     * @param id
     * @return
     */
    public static String idCardNum(String id) {
        if (StringUtils.isBlank(id)) {
            return "";
        }
        return StringUtils.left(id, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(id, 4), StringUtils.length(id), "*"), "******"));
    }

    /**
     * 【固定电话 】后四位，其他隐藏，比如1234
     *
     * @param num
     * @return
     */
    public static String fixedPhone(String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }
        return StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*");
    }

    /**
     * 【手机号码】前三位，后四位，其他隐藏，比如135****6810
     *
     * @param num
     * @return
     */
    public static String mobilePhone(String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }
        return StringUtils.left(num, 3).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*"), "***"));
    }

    /**
     * 【地址】只显示到地区，不显示详细地址，比如：北京市海淀区****
     *
     * @param address
     * @param sensitiveSize 敏感信息长度
     * @return
     */
    public static String address(String address, int sensitiveSize) {
        if (StringUtils.isBlank(address)) {
            return "";
        }
        int length = StringUtils.length(address);
        return StringUtils.rightPad(StringUtils.left(address, length - sensitiveSize), length, "*");
    }

    /**
     * 【电子邮箱 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示，比如：d**@126.com>
     *
     * @param email
     * @return
     */
    public static String email(String email) {
        if (StringUtils.isBlank(email)) {
            return "";
        }
        int index = StringUtils.indexOf(email, "@");
        if (index <= 1)
            return email;
        else
            return StringUtils.rightPad(StringUtils.left(email, 1), index, "*").concat(StringUtils.mid(email, index, StringUtils.length(email)));
    }

    /**
     * 【银行卡号】前六位，后四位，其他用星号隐藏每位1个星号，比如：6222600**********1234>
     *
     * @param cardNum
     * @return
     */
    public static String bankCard(String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return "";
        }
        return StringUtils.left(cardNum, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), "*"), "******"));
    }

    /**
     * 【个人账号】后四位，其他隐藏，比如1234
     *
     * @param num
     * @return
     */
    public static String personAccount(String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }
        return StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*");
    }

    /**
     * 【单位账号】后四位，其他隐藏，比如1234
     *
     * @param num
     * @return
     */
    public static String orgAccount(String num) {
        if (StringUtils.isBlank(num)) {
            return "";
        }
        return StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*");
    }
}
