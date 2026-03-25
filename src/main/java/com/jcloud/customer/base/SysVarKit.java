package com.jcloud.customer.base;

/**
 * Copyright 2016-2019 长沙湘北智造信息技术有限公司
 *
 * @version xbzz V2.0.28
 * @author 湘北智造-框架开发组
 * @date：2019年10月17日
 * @description：常量工具类
 *
 */
public class SysVarKit {
	public static String NO = "0";
	public static String YES = "1";

	/**
	 * 请求返回对象中 提示语句的标识
	 */
	public static String Msg = "msg";
	/**
	 * 手机验证码
	 */
	public static String CACHE_SMS = "cacheSms";
	/**
	 * 用户登录会话token PC端
	 */
	public static String CACHE_TOKEN = "cacheToken";
	/**
	 * 用户登录会话token 手机端
	 */
	public static String CACHE_TOKEN_MOBILE = "cacheTokenMobile";
	/**
	 * 用户功能权限信息
	 */
	public static String CACHE_AUTHORITY = "cacheAuthority";

	/**
	 * 需要数据过滤的注册过的接口
	 */
	public static String DATA_INTERFACE_AUTHORITY = "dataInterfaceAuthority";
	/**
	 * token 失效时间
	 */
	public static String TOKEN_EXPIRATION = "tokenExpiration";
	/**
	 * 验证码 失效时间
	 */
	public static String SMS_EXPIRATION = "smsExpiration";


	/**
	 * 用户权限信息
	 */
	public static String CACHE_AUTHORITY_LIST = "cacheAuthorityList";
	/**
	 * 超级管理员在数据库中的用户ID
	 */
	public static String SUPER_MANAGER_ID = "system";

	/**
	 * 用于操作不同的redis数据库
	 * 该标识是后台管理界面专用
	 */
	public static String REDIS_ADMIN = "redis_admin";

	/**
	 * 用户接口数据权限
	 */
	public static String DATA_AUTHORITY = "dataAuthority";
	/**
	 * 用户菜单权限
	 */
	public static String MODULE_AUTHORITY = "moduleAuthority";
	/**
	 * 用户按钮权限
	 */
	public static String MODULE_BOTTON_AUTHORITY = "moduleBottonAuthority";
	/**
	 * 用户列表字段权限
	 */
	public static String MODULE_COLUMN_AUTHORITY = "moduleColumnAuthority";
	/**
	 * 用户表单可编辑字段权限
	 */
	public static String MODULE_EDITABLE_FORM_AUTHORITY = "moduleEditableFormAuthority";
	/**
	 * 用户表单只读字段权限
	 */
	public static String MODULE_READONLY_FORM_AUTHORITY = "moduleReadonlyFormAuthority";

	/**
	 * 所有功能菜单
	 */
	public static final String CACHE_MODULE_KEY = "allModules";
	/**
	 * 用户所属的角色
	 */
	public static final Object CACHE_USER_ROLE_KEY = "userRoles";
}
