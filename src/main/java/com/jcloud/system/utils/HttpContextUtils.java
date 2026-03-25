package com.jcloud.system.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpContextUtils {
	public static HttpServletRequest getHttpServletRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	public static HttpSession getHttpSession () {
		HttpSession session = getHttpServletRequest().getSession();
		return session;
	}

	/**
	 * 获取组织
	 * @return
	 */
	public static Object getOrgId () {
		HttpSession session = getHttpServletRequest().getSession();
		Object orgid = session.getAttribute("orgid");
		return orgid;
	}

	//获取pda产数
	// xturl -- 地址
	// line_code -- 产线
	// dot_code -- 采集点
	// lookup_code -- 装配点
	// fiscalDate -- 会计期
	// inventory_code -- 仓库
}
