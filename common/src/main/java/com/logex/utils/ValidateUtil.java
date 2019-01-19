package com.logex.utils;

import android.text.TextUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据验证工具类
 */
public class ValidateUtil {
    public final static int TEXT_SHORT = 1; // 数据过短
    public final static int TEXT_LONG = 2; // 数据过长
    public final static int TEXT_ILLEGAL = 3; // 数据非法
    public final static int TEXT_NORMAL = 0; // 数据正常

    /**
     * 验证是否是手机号
     *
     * @param telNum 手机号 新增移动198 166联通 199电信 14*号段
     * @return true 验证通过
     */
    public static boolean isMobile(String telNum) {
        String regex = "^((13[0-9])|(14[0-9])|(15[0-9])|(166)|(17[0-9])|(18[0-9])|(198)|(199))\\d{8}$";
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(telNum);
        return m.matches();
    }

    /**
     * 验证密码是否有效
     *
     * @param pwd 密码
     * @return 是否有效
     */
    public static int isPwdValid(String pwd) {
        if (pwd.length() < 6) {
            return TEXT_SHORT;
        } else if (pwd.length() > 16) {
            return TEXT_LONG;
        } else if (!pwd.matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$")) {
            return TEXT_ILLEGAL;
        } else {
            return TEXT_NORMAL;
        }
    }

    /**
     * 是否是邮箱
     *
     * @param strEmail 邮箱
     * @return 是否是邮箱
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /**
     * 判断list非空
     *
     * @param list 集合
     * @return false为空 true不为空
     */
    public static <T> boolean isListNonEmpty(List<T> list) {
        return list != null && list.size() > 0;
    }

    /**
     * 判断list为空
     *
     * @param list 集合
     * @return true 空
     */
    public static <T> boolean isListEmpty(List<T> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 将手机号中间4位换成****
     *
     * @param phone 手机号
     * @return 替换后的手机号
     */
    public static String phoneNumberReplace(String phone) {
        if (phone == null) return "";
        try {
            StringBuilder sb = new StringBuilder(phone.trim());
            return sb.replace(3, 7, "****").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return phone;
        }
    }

    /**
     * 功能：判断字符串是否为数字
     *
     * @param str 字符串
     * @return true数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 功能：判断字符串是否为日期格式
     *
     * @param strDate 字符串
     * @return true 是日期格式
     */
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        return m.matches();
    }

    /**
     * 从字符串提取手机号码
     *
     * @param sParam 目标字符串
     * @return 手机号
     */
    public static String getPhoneFromStr(String sParam) {
        if (TextUtils.isEmpty(sParam)) return "";
        Pattern pattern = Pattern.compile("(1|861)(3|5|7|8)\\d{9}$*");
        Matcher matcher = pattern.matcher(sParam);
        StringBuilder bf = new StringBuilder();
        while (matcher.find()) {
            bf.append(matcher.group()).append(",");
        }
        int len = bf.length();
        if (len > 0) {
            bf.deleteCharAt(len - 1);
        }
        return bf.toString();
    }
}
