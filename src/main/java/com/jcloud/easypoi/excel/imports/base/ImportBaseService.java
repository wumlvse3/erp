package com.jcloud.easypoi.excel.imports.base;

import com.jcloud.easypoi.excel.annotation.Excel;
import com.jcloud.easypoi.excel.annotation.ExcelCollection;
import com.jcloud.easypoi.excel.annotation.ExcelEntity;
import com.jcloud.easypoi.excel.entity.ImportParams;
import com.jcloud.easypoi.excel.entity.params.ExcelCollectionParams;
import com.jcloud.easypoi.excel.entity.params.ExcelImportEntity;
import com.jcloud.easypoi.exception.excel.ExcelImportException;
import com.jcloud.easypoi.exception.excel.enums.ExcelImportEnum;
import com.jcloud.easypoi.handler.inter.II18nHandler;
import com.jcloud.easypoi.util.PoiPublicUtil;
import com.jcloud.easypoi.util.PoiReflectorUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.*;

public class ImportBaseService {

    protected II18nHandler i18nHandler;

    public void addEntityToMap(String targetId, Field field, ExcelImportEntity excelEntity,
                               Class<?> pojoClass, List<Method> getMethods,
                               Map<String, ExcelImportEntity> temp, ExcelEntity excelEntityAnn) throws Exception {
        Excel excel = field.getAnnotation(Excel.class);
        excelEntity = new ExcelImportEntity();
        excelEntity.setType(excel.type());
        excelEntity.setSaveUrl(excel.savePath());
        excelEntity.setSaveType(excel.imageType());
        excelEntity.setReplace(excel.replace());
        excelEntity.setDatabaseFormat(excel.databaseFormat());
        excelEntity.setSuffix(excel.suffix());
        excelEntity.setImportField(Boolean
                .valueOf(PoiPublicUtil.getValueByTargetId(excel.isImportField(), targetId, "false")));
        excelEntity.setFixedIndex(excel.fixedIndex());
        excelEntity.setName(PoiPublicUtil.getValueByTargetId(excel.name(), targetId, null));
        if (StringUtils.isNoneEmpty(excel.groupName())) {
            excelEntity.setName(excel.groupName() + "_" + excelEntity.getName());
        }
        if (excelEntityAnn != null && excelEntityAnn.show()) {
            excelEntity.setName(excelEntityAnn.name() + "_" + excelEntity.getName());
        }
        if (i18nHandler != null) {
            excelEntity.setName(i18nHandler.getLocaleName(excelEntity.getName()));
        }
        excelEntity.setMethod(PoiReflectorUtil.fromCache(pojoClass).getSetMethod(field.getName()));
        if (StringUtils.isNotEmpty(excel.importFormat())) {
            excelEntity.setFormat(excel.importFormat());
        } else {
            excelEntity.setFormat(excel.format());
        }
        excelEntity.setDict(excel.dict());
        excelEntity.setEnumImportMethod(excel.enumImportMethod());
        excelEntity.setUnique(excel.unique());
        if (getMethods != null) {
            List<Method> newMethods = new ArrayList<>();
            newMethods.addAll(getMethods);
            newMethods.add(excelEntity.getMethod());
            excelEntity.setMethods(newMethods);
        }
        if (excelEntity.getFixedIndex() != -1) {
            temp.put("FIXED_" + excelEntity.getFixedIndex(), excelEntity);
        } else {
            temp.put(excelEntity.getName(), excelEntity);
        }
    }

    public void getAllExcelField(String targetId, Field[] fields,
                                 Map<String, ExcelImportEntity> excelParams,
                                 List<ExcelCollectionParams> excelCollection, Class<?> pojoClass,
                                 List<Method> getMethods, ExcelEntity excelEntityAnn) throws Exception {
        ExcelImportEntity excelEntity = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (PoiPublicUtil.isNotUserExcelUserThis(null, field, targetId)) {
                continue;
            }
            if (PoiPublicUtil.isCollection(field.getType())) {
                ExcelCollection excel = field.getAnnotation(ExcelCollection.class);
                ExcelCollectionParams collection = new ExcelCollectionParams();
                collection.setName(field.getName());
                Map<String, ExcelImportEntity> temp = new HashMap<>();
                ParameterizedType pt = (ParameterizedType) field.getGenericType();
                Class<?> clz = (Class<?>) pt.getActualTypeArguments()[0];
                collection.setType(clz);
                getExcelFieldList(StringUtils.isNotEmpty(excel.id()) ? excel.id() : targetId,
                        PoiPublicUtil.getClassFields(clz), clz, temp, null);
                collection.setExcelParams(temp);
                collection.setExcelName(PoiPublicUtil.getValueByTargetId(
                        field.getAnnotation(ExcelCollection.class).name(), targetId, null));
                additionalCollectionName(collection);
                excelCollection.add(collection);
            } else if (PoiPublicUtil.isJavaClass(field) || field.getType().isEnum()) {
                addEntityToMap(targetId, field, excelEntity, pojoClass, getMethods, excelParams, excelEntityAnn);
            } else {
                List<Method> newMethods = new ArrayList<>();
                if (getMethods != null) {
                    newMethods.addAll(getMethods);
                }
                newMethods.add(PoiReflectorUtil.fromCache(pojoClass).getGetMethod(field.getName()));
                ExcelEntity excel = field.getAnnotation(ExcelEntity.class);
                if (excel.show() && StringUtils.isEmpty(excel.name())) {
                    throw new ExcelImportException("if use ExcelEntity ,name mus has value");
                }
                getAllExcelField(StringUtils.isNotEmpty(excel.id()) ? excel.id() : targetId,
                        PoiPublicUtil.getClassFields(field.getType()), excelParams, excelCollection,
                        field.getType(), newMethods, excel);
            }
        }
    }

    private void additionalCollectionName(ExcelCollectionParams collection) {
        Set<String> keys = new HashSet<>();
        keys.addAll(collection.getExcelParams().keySet());
        for (String key : keys) {
            collection.getExcelParams().put(collection.getExcelName() + "_" + key,
                    collection.getExcelParams().get(key));
            collection.getExcelParams().remove(key);
        }
    }

    public void getExcelFieldList(String targetId, Field[] fields, Class<?> pojoClass,
                                  Map<String, ExcelImportEntity> temp,
                                  List<Method> getMethods) throws Exception {
        ExcelImportEntity excelEntity = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (PoiPublicUtil.isNotUserExcelUserThis(null, field, targetId)) {
                continue;
            }
            if (PoiPublicUtil.isJavaClass(field) || field.getType().isEnum()) {
                addEntityToMap(targetId, field, excelEntity, pojoClass, getMethods, temp, null);
            } else {
                List<Method> newMethods = new ArrayList<>();
                if (getMethods != null) {
                    newMethods.addAll(getMethods);
                }
                ExcelEntity excel = field.getAnnotation(ExcelEntity.class);
                newMethods.add(PoiReflectorUtil.fromCache(pojoClass).getSetMethod(field.getName()));
                getExcelFieldList(StringUtils.isNotEmpty(excel.id()) ? excel.id() : targetId,
                        PoiPublicUtil.getClassFields(field.getType()), field.getType(), temp,
                        newMethods);
            }
        }
    }

    public Object getFieldBySomeMethod(List<Method> list, Object t) throws Exception {
        Method m;
        for (int i = 0; i < list.size() - 1; i++) {
            m = list.get(i);
            t = m.invoke(t, new Object[]{});
        }
        return t;
    }

    public void saveThisExcel(ImportParams params, Class<?> pojoClass, boolean isXSSFWorkbook,
                              Workbook book) throws Exception {
        String path = getSaveExcelUrl(params, pojoClass);
        File savefile = new File(path);
        if (!savefile.exists()) {
            savefile.mkdirs();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyMMddHHmmss");
        FileOutputStream fos = new FileOutputStream(
                path + File.separator + format.format(new Date()) + "_" + Math.round(Math.random() * 100000)
                        + (isXSSFWorkbook ? ".xlsx" : ".xls"));
        book.write(fos);
        fos.close();
    }

    public String getSaveExcelUrl(ImportParams params, Class<?> pojoClass) throws Exception {
        String url = "";
        if (ImportParams.SAVE_URL.equals(params.getSaveUrl())) {
            url = pojoClass.getName().split("\\.")[pojoClass.getName().split("\\.").length - 1];
            return params.getSaveUrl() + File.separator + url;
        }
        return params.getSaveUrl();
    }

    public void setFieldBySomeMethod(List<Method> setMethods, Object object,
                                     Object value) throws Exception {
        Object t = getFieldBySomeMethod(setMethods, object);
        setMethods.get(setMethods.size() - 1).invoke(t, value);
    }

    public void setValues(ExcelImportEntity entity, Object object, Object value) throws Exception {
        if (value == null) {
            return;
        }
        if (entity.getMethods() != null) {
            setFieldBySomeMethod(entity.getMethods(), object, value);
        } else {
            entity.getMethod().invoke(object, value);
        }
    }
}
