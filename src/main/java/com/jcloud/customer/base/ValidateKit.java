package com.jcloud.customer.base;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证工具集
 * 
 * @author liangyi
 *
 */
public class ValidateKit {
	public static boolean checkNullArray(String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			String ar = arr[i];
			if(checkNull(ar)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			// System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 为空返回true
	 * 
	 * @param para
	 * @return
	 */
	public static boolean checkNull(String para) {
		if (para != null && !para.trim().equals("") && !para.trim().toLowerCase().equals("null")) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkSet(Set set) {
		if (set != null && set.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean checkList(List list) {
		if (list != null&&list.size()>0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean checkMap(Map map) {
		if (map != null && map.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证手机号码是否符合要求 中国电信号段 133、149、153、173、177、180、181、189、199 中国联通号段
	 * 130、131、132、145、155、156、166、175、176、185、186 中国移动号段
	 * 134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188、198
	 * 其他号段 14号段以前为上网卡专属号段，如中国联通的是145，中国移动的是147等等。 虚拟运营商 电信：1700、1701、1702
	 * 移动：1703、1705、1706 联通：1704、1707、1708、1709、171 卫星通信：1349
	 * 
	 * @param phone
	 */
	public static String[] isPhone(String phone) {
		String result[] = new String[2];
		String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
		if (phone.length() != 11) {
			result[0] = SysVarKit.NO;// false
			result[1] = "手机号应为11位数！";
		} else {
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(phone);
			boolean isMatch = m.matches();
			if (!isMatch) {
				result[0] = SysVarKit.NO;// false
				result[1] = "请填入正确的手机号！";
			} else {
				result[0] = SysVarKit.YES;// true
				result[1] = "手机号正确！";
			}
		}
		return result;
	}

	/**
	 * 验证手机号码
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean isPhoneSimple(String phone) {
		String result[] = isPhone(phone);
		return SysVarKit.YES.equals(result[0]) ? true : false;
	}

	public static void main(String[] args) {
		System.out.println(isPhoneSimple("11111111111"));

	}
}
