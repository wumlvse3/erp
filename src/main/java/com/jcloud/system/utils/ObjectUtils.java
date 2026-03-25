package com.jcloud.system.utils;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ObjectUtils {

    /**
     * 转换为字符串，null为‘’
     * @param a
     * @return
     */
    public static String oGetS(Object a) {
        String b  = a == null?"":a.toString();
        return b;
    }

    /**
     * 转换为字符串，null为‘’
     * @param a
     * @return
     */
    public static Integer oGetI(Object a) {
        Integer b  = a == null?0:Integer.valueOf(a.toString());
        return b;
    }

    /**
     * 判断转换
     *
     * @param a
     * @return
     */
    public static boolean getFT(Object a) {
        try {
            boolean b = a == null ? false : (boolean) a;
            return b;
        } catch (Exception e) {
            return false;
        }

    }


	public static  Map<String,Object> lowerMap(Map<String,Object> mainM){
		Map<String,Object> dynimicReportM = new HashMap<String, Object>(0);
			for (Entry<String, Object> entry : mainM.entrySet()) {
				String key = entry.getKey();
				dynimicReportM.put(key.toLowerCase(), entry.getValue());
			}
	
		return dynimicReportM;
	}
	
	public static List<Map<String,Object>>  lowerList(List<Map<String,Object>> itemsM){
		
		List<Map<String,Object>> list = new ArrayList();
		for (Map<String, Object> map : itemsM) {
			if(map==null)continue;
			Map<String, Object> temp = new HashMap<String, Object>();
			for (Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				temp.put(key.toLowerCase(), entry.getValue());
			}
			list.add(temp);
		}
	
		return list;
	}


	/**
	 * 字符转实体不分页
	 * @param rows
	 * @return
	 */
	public  static List<Map<String, Object>> getList(List<Map<String, Object>> rows){
		List<Map<String,Object>> list = new ArrayList();
		for (Map<String, Object> map : rows) {
			Map<String, Object> temp = new HashMap<String, Object>();
			for (Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				String[] s = key.split("_");
				key = s[0];
				if(s.length >1){
					for (int i = 1;i < s.length;i++){
						key+=s[1].toUpperCase().charAt(0) + s[i].substring(1);
					}
				}
				temp.put(key, entry.getValue());
			}
			list.add(temp);
		}
		return list;
	}


	/**
	 * 将字符转实体加分页效果
	 * @param pageUtils
	 * @return
	 */
	public  static PageUtils getPageUtils(PageUtils pageUtils){
		List<Map<String,Object>> rows = (List<Map<String, Object>>) pageUtils.getRows();
		List<Map<String,Object>> list = new ArrayList();
		for (Map<String, Object> map : rows) {
			Map<String, Object> temp = new HashMap<String, Object>();
			for (Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				String[] s = key.split("_");
				key = s[0];
				if(s.length >1){
					for (int i = 1;i < s.length;i++){
						key+=s[1].toUpperCase().charAt(0) + s[i].substring(1);
					}
				}
				temp.put(key, entry.getValue());
			}
			list.add(temp);
		}
		PageUtils pageUtils1 = new PageUtils(list, pageUtils.getTotal());
		return pageUtils1;
	}

	public static Map<String,String> getClassColumnAndName(Class tClass){
		Field[] fields = tClass.getDeclaredFields();
		Map<String,String> res = new HashMap<>();
		for(Field field:fields){
			if(field.isAnnotationPresent(Column.class)){
				String name = field.getAnnotation(Column.class).name();
				String code = field.getName();
				res.put(code,name);
			}
		}
		return res;
	}

    public static String getIdStr() {
        return IdWorker.getIdStr();
    }
}
