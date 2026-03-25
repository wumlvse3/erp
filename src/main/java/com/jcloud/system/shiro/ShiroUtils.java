package com.jcloud.system.shiro;


import com.jcloud.system.entity.UserEntity;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;


/**
 * Shiro工具类
 */
public class ShiroUtils {
    /**  加密算法 */
    public final static String hashAlgorithmName = "md5";
    /**  循环次数 */
    public final static int hashIterations = 16;

    /**
     * md5转换
     * @param password   密码
     * @param salt   账号
     * @return
     */
    public static String md5(String password, String salt) {
        return new SimpleHash(hashAlgorithmName, password, salt, hashIterations).toString();
    }

    public static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static UserEntity getUserEntity() {
        return (UserEntity) SecurityUtils.getSubject().getPrincipal();
    }

    public static Long getId() {
        return getUserEntity().getUserId();
    }


    public static void setSessionAttribute(Object key, Object value) {
        getSession().setAttribute(key, value);
    }

    public static Object getSessionAttribute(Object key) {
        return getSession().getAttribute(key);
    }

    /**
     * 获取组织
     * @return
     */
    public static String getOrgId(){
        Object orgId = getSession().getAttribute("orgId");
        if(orgId == null) return "0";
        else return orgId.toString();
    }

    public static boolean isLogin() {
        return SecurityUtils.getSubject().getPrincipal() != null;
    }

    public static void logout() {
        SecurityUtils.getSubject().logout();
    }

    public static String getKaptcha(String key) {
        Object kaptcha = getSessionAttribute(key);
        if(kaptcha == null){
            return "0";
        }
        return kaptcha.toString();
    }

    public static void main(String[] args) {
        System.out.println(md5("17444466","S00000341"));
    }
}