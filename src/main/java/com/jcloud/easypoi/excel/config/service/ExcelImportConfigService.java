package com.jcloud.easypoi.excel.config.service;

import com.jcloud.easypoi.excel.config.entity.ExcelImportConfig;
import com.jcloud.easypoi.excel.config.entity.ExcelImportField;
import com.jcloud.easypoi.excel.config.mapper.ExcelImportConfigMapper;
import com.jcloud.easypoi.excel.generator.ExcelCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class ExcelImportConfigService {

    @Autowired
    private ExcelImportConfigMapper configMapper;

    public ExcelImportConfig getById(Long id) {
        ExcelImportConfig config = configMapper.selectById(id);
        if (config != null) {
            config.setFieldList(configMapper.selectFieldsByConfigId(id));
        }
        return config;
    }

    public ExcelImportConfig getByTableName(String tableName) {
        ExcelImportConfig config = configMapper.selectByTableName(tableName);
        if (config != null) {
            config.setFieldList(configMapper.selectFieldsByConfigId(config.getId()));
        }
        return config;
    }

    public List<ExcelImportConfig> listAll() {
        List<ExcelImportConfig> list = configMapper.selectAll();
        for (ExcelImportConfig config : list) {
            config.setFieldList(configMapper.selectFieldsByConfigId(config.getId()));
        }
        return list;
    }

    @Transactional
    public Long save(ExcelImportConfig config) {
        config.setCreateTime(new Date());
        if (config.getStatus() == null) {
            config.setStatus("1");
        }
        if (config.getNeedVerify() == null) {
            config.setNeedVerify("N");
        }
        if (config.getTitleRows() == null) {
            config.setTitleRows(0);
        }
        if (config.getHeadRows() == null) {
            config.setHeadRows(1);
        }
        configMapper.insert(config);

        if (config.getFieldList() != null && !config.getFieldList().isEmpty()) {
            for (int i = 0; i < config.getFieldList().size(); i++) {
                ExcelImportField field = config.getFieldList().get(i);
                field.setConfigId(config.getId());
                // 如果 sortOrder 为 null 或未设置，才按数组顺序计算；否则保留用户手工修改的值
                if (field.getSortOrder() == null || field.getSortOrder() == 0) {
                    field.setSortOrder((i + 1) * 10);
                }
                if (field.getOrderNum() == null || field.getOrderNum() == 0) {
                    field.setOrderNum((i + 1) * 10);
                }
                if (field.getIsRequired() == null) {
                    field.setIsRequired(false);
                }
                if (field.getColumnLength() == null) {
                    field.setColumnLength(50);
                }
                if (field.getUniqueFlag() == null || field.getUniqueFlag().isEmpty()) {
                    field.setUniqueFlag("N");
                }
                configMapper.insertField(field);
            }
        }

        // 生成实体类和Controller
        generateCode(config);

        return config.getId();
    }

    @Transactional
    public int update(ExcelImportConfig config) {
        config.setUpdateTime(new Date());
        configMapper.update(config);

        configMapper.deleteFieldsByConfigId(config.getId());
        if (config.getFieldList() != null) {
            for (int i = 0; i < config.getFieldList().size(); i++) {
                ExcelImportField field = config.getFieldList().get(i);
                field.setId(null);
                field.setConfigId(config.getId());
                // 如果 sortOrder 为 null 或未设置，才按数组顺序计算；否则保留用户手工修改的值
                if (field.getSortOrder() == null || field.getSortOrder() == 0) {
                    field.setSortOrder((i + 1) * 10);
                }
                if (field.getOrderNum() == null || field.getOrderNum() == 0) {
                    field.setOrderNum((i + 1) * 10);
                }
                if (field.getUniqueFlag() == null || field.getUniqueFlag().isEmpty()) {
                    field.setUniqueFlag("N");
                }
                configMapper.insertField(field);
            }
        }

        // 重新生成实体类和Controller
        generateCode(config);

        return 1;
    }

    private void generateCode(ExcelImportConfig config) {
        try {
            // 生成代码前按 orderNum 排序字段列表
            if (config.getFieldList() != null) {
                config.getFieldList().sort((a, b) -> {
                    int sortA = a.getOrderNum() != null ? a.getOrderNum() : 0;
                    int sortB = b.getOrderNum() != null ? b.getOrderNum() : 0;
                    return Integer.compare(sortA, sortB);
                });
            }
            ExcelCodeGenerator.generateAndSave(config);
        } catch (IOException e) {
            throw new RuntimeException("生成代码失败: " + e.getMessage(), e);
        }
    }

    @Transactional
    public int delete(Long id) {
        configMapper.deleteFieldsByConfigId(id);
        return configMapper.deleteById(id);
    }

    public List<ExcelImportConfig> listActive() {
        return configMapper.selectActiveConfigs();
    }

    public List<java.util.Map<String, Object>> getTableColumns(String tableName) {
        List<java.util.Map<String, Object>> columns = configMapper.selectTableColumns(tableName);
        for (java.util.Map<String, Object> col : columns) {
            String dataType = String.valueOf(col.get("dataType")).toLowerCase();
            col.put("javaType", mapSqlTypeToJavaType(dataType));
            col.put("fieldName", toCamelCase(String.valueOf(col.get("columnName"))));
            col.put("isRequired", "NO".equals(col.get("isNullable")));
            col.put("orderNum", 0);
            col.put("sortOrder", 0);
        }
        return columns;
    }

    private String mapSqlTypeToJavaType(String sqlType) {
        switch (sqlType.toLowerCase()) {
            case "varchar":
            case "char":
            case "text":
            case "longtext":
            case "mediumtext":
            case "tinytext":
                return "String";
            case "int":
            case "integer":
            case "tinyint":
            case "smallint":
            case "mediumint":
                return "Integer";
            case "bigint":
                return "Long";
            case "decimal":
            case "double":
            case "float":
            case "numeric":
                return "Double";
            case "date":
            case "datetime":
            case "timestamp":
            case "time":
                return "Date";
            case "bit":
                return "Boolean";
            default:
                return "String";
        }
    }

    private String toCamelCase(String name) {
        if (name == null || name.isEmpty()) return name;
        String[] parts = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                sb.append(parts[i].toLowerCase());
            } else {
                sb.append(parts[i].substring(0, 1).toUpperCase());
                sb.append(parts[i].substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }
}
