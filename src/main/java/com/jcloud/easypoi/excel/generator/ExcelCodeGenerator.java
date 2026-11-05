package com.jcloud.easypoi.excel.generator;

import com.jcloud.easypoi.excel.config.entity.ExcelImportConfig;
import com.jcloud.easypoi.excel.config.entity.ExcelImportField;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExcelCodeGenerator {

    private static final String BASE_PATH;
    private static final String TEMPLATE_PATH;
    private static final String DEFAULT_PACKAGE = "com.jcloud";

    static {
        // 获取当前类所在的项目根目录
        String userDir = System.getProperty("user.dir");
        BASE_PATH = userDir + "/src/main/java/";
        TEMPLATE_PATH = userDir + "/src/main/resources/templates/excel/";
    }

    public static String generateEntityCode(ExcelImportConfig config) {
        StringBuilder sb = new StringBuilder();

        String packageName = getPackageName(config) + ".entity";
        String entityName = getEntityName(config) + "Import";

        sb.append("package ").append(packageName).append(";\n\n");

        sb.append("import com.jcloud.easypoi.excel.annotation.Excel;\n");
        sb.append("import com.jcloud.easypoi.excel.annotation.ExcelTarget;\n");
        sb.append("import com.jcloud.easypoi.handler.inter.IExcelModel;\n");
        sb.append("import com.baomidou.mybatisplus.annotation.TableName;\n");
        sb.append("import com.baomidou.mybatisplus.annotation.TableField;\n");
        sb.append("import org.hibernate.validator.constraints.NotBlank;\n");
        sb.append("import org.hibernate.validator.constraints.Length;\n");
        sb.append("import javax.validation.constraints.DecimalMin;\n");
        sb.append("import javax.validation.constraints.DecimalMax;\n");
        sb.append("import javax.validation.constraints.NotNull;\n");
        sb.append("import javax.validation.constraints.Digits;\n");
        sb.append("import java.io.Serializable;\n");

        boolean hasDate = false;
        boolean hasBigDecimal = false;
        for (ExcelImportField field : config.getFieldList()) {
            if ("Date".equals(field.getJavaType())) hasDate = true;
            if ("BigDecimal".equals(field.getJavaType())) hasBigDecimal = true;
        }
        hasDate = true; // 确保导入Date类型（createTime需要）
        if (hasBigDecimal) sb.append("import java.math.BigDecimal;\n");
        sb.append("import java.util.Date;\n");

        sb.append("\n");
        sb.append("@ExcelTarget(\"").append(toCamelCase(entityName)).append("\")\n");
        sb.append("@TableName(\"").append(config.getTableName()).append("\")\n");
        sb.append("public class ").append(entityName).append(" implements Serializable, IExcelModel {\n\n");
        sb.append("    private static final long serialVersionUID = 1L;\n\n");
        sb.append("    /**\n");
        sb.append("     * 错误信息\n");
        sb.append("     */\n");
        sb.append("    private String errorMsg;\n\n");
        sb.append("    /**\n");
        sb.append("     * Excel行号（不持久化）\n");
        sb.append("     */\n");
        sb.append("    @TableField(exist = false)\n");
        sb.append("    private Integer rowNum;\n\n");
        sb.append("    public Integer getRowNum() { return rowNum; }\n");
        sb.append("    public void setRowNum(Integer rowNum) { this.rowNum = rowNum; }\n\n");

        for (ExcelImportField field : config.getFieldList()) {
            String dbColumnName = toUnderline(field.getFieldName());

            sb.append("    /**\n");
            sb.append("     * ").append(field.getColumnComment()).append("\n");
            sb.append("     */\n");
            sb.append("    @TableField(\"").append(dbColumnName).append("\")\n");

            sb.append("    @Excel(name = \"").append(field.getColumnName()).append("\"");
            sb.append(", orderNum = \"").append(field.getOrderNum()).append("\"");
            sb.append(", isImportField = \"true\"");

            if ("Date".equals(field.getJavaType())) {
                String format = field.getDateFormat() != null && !field.getDateFormat().isEmpty()
                        ? normalizeDateFormat(field.getDateFormat()) : "yyyy-MM-dd";
                sb.append(", importFormat = \"").append(format).append("\"");
                sb.append(", format = \"").append(format).append("\"");
            } else if (field.getDateFormat() != null && !field.getDateFormat().isEmpty()) {
                // 即使字段类型是String，如果配置了dateFormat，也添加importFormat属性用于Excel日期解析
                String format = normalizeDateFormat(field.getDateFormat());
                sb.append(", importFormat = \"").append(format).append("\"");
                sb.append(", format = \"").append(format).append("\"");
            }

            if (isNumericType(field.getJavaType())) {
                sb.append(", type = 10");
            }

            if (field.getReplaceValue() != null && !field.getReplaceValue().isEmpty()) {
                String[] replaceParts = field.getReplaceValue().split(",");
                StringBuilder replaceSb = new StringBuilder();
                replaceSb.append(", replace = {");
                for (int i = 0; i < replaceParts.length; i++) {
                    if (i > 0) replaceSb.append(", ");
                    replaceSb.append("\"").append(replaceParts[i].trim()).append("\"");
                }
                replaceSb.append("}");
                sb.append(replaceSb);
            }

            if ("Y".equals(field.getUniqueFlag())) {
                sb.append(", unique = true");
            }

            sb.append(")\n");

            // 根据明细定义的验证规则添加校验注解
            if (Boolean.TRUE.equals(field.getIsRequired())) {
                if ("String".equals(field.getJavaType())) {
                    sb.append("    @NotBlank(message = \"不能为空\")\n");
                } else if ("Integer".equals(field.getJavaType())) {
                    sb.append("    @NotNull(message = \"不能为空，且必须为整数\")\n");
                } else if ("Long".equals(field.getJavaType())) {
                    sb.append("    @NotNull(message = \"不能为空，且必须为整数\")\n");
                } else if ("Double".equals(field.getJavaType()) || "Float".equals(field.getJavaType()) || "BigDecimal".equals(field.getJavaType())) {
                    sb.append("    @NotNull(message = \"不能为空，且必须为数字\")\n");
                } else if ("Date".equals(field.getJavaType())) {
                    String format = field.getDateFormat() != null ? normalizeDateFormat(field.getDateFormat()) : "yyyy-MM-dd";
                    sb.append("    @NotNull(message = \"不能为空，且日期格式必须为").append(format).append("\")\n");
                }
            }

            // 长度校验
            if ("String".equals(field.getJavaType()) && field.getColumnLength() != null && field.getColumnLength() > 0) {
                sb.append("    @Length(max = ").append(field.getColumnLength()).append(", message = \"长度不能超过").append(field.getColumnLength()).append("\")\n");
            }

            sb.append("    private ").append(field.getJavaType()).append(" ").append(field.getFieldName()).append(";\n\n");

            String pascalName = toPascalCase(field.getFieldName());
            sb.append("    public ").append(field.getJavaType()).append(" get").append(pascalName).append("() { return ").append(field.getFieldName()).append("; }\n");
            sb.append("    public void set").append(pascalName).append("(").append(field.getJavaType()).append(" ").append(field.getFieldName()).append(") { this.").append(field.getFieldName()).append(" = ").append(field.getFieldName()).append("; }\n\n");
        }

        // 检查明细中是否已存在 create_user_id 和 create_time 字段
        boolean hasCreateUserId = false;
        boolean hasCreateTime = false;
        for (ExcelImportField field : config.getFieldList()) {
            if ("createUserId".equals(field.getFieldName()) || "create_user_id".equals(field.getFieldName())) {
                hasCreateUserId = true;
            }
            if ("createTime".equals(field.getFieldName()) || "create_time".equals(field.getFieldName())) {
                hasCreateTime = true;
            }
        }

        // 自动添加 create_user_id 和 create_time 字段（如果明细中没有）
        if (!hasCreateUserId) {
            sb.append("    /**\n");
            sb.append("     * 创建人ID\n");
            sb.append("     */\n");
            sb.append("    @TableField(\"create_user_id\")\n");
            sb.append("    private String createUserId;\n\n");
            sb.append("    public String getCreateUserId() { return createUserId; }\n");
            sb.append("    public void setCreateUserId(String createUserId) { this.createUserId = createUserId; }\n\n");
        }

        if (!hasCreateTime) {
            sb.append("    /**\n");
            sb.append("     * 创建时间\n");
            sb.append("     */\n");
            sb.append("    @TableField(\"create_time\")\n");
            sb.append("    private java.util.Date createTime;\n\n");
            sb.append("    public java.util.Date getCreateTime() { return createTime; }\n");
            sb.append("    public void setCreateTime(java.util.Date createTime) { this.createTime = createTime; }\n\n");
        }

        sb.append("    @Override\n");
        sb.append("    public String getErrorMsg() { return errorMsg; }\n");
        sb.append("    @Override\n");
        sb.append("    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }\n\n");

        sb.append("}\n");

        return sb.toString();
    }

    public static String generateControllerCode(ExcelImportConfig config) {
        StringBuilder sb = new StringBuilder();

        String packageName = getPackageName(config) + ".controller";
        String entityName = getEntityName(config) + "Import";
        String entityPackage = getPackageName(config) + ".entity";
        String controllerName = getEntityName(config) + "ImportController";

        sb.append("package ").append(packageName).append(";\n\n");

        sb.append("import com.jcloud.easypoi.excel.ExcelImportUtil;\n");
        sb.append("import com.jcloud.easypoi.excel.entity.ImportParams;\n");
        sb.append("import com.jcloud.easypoi.excel.entity.result.ExcelImportResult;\n");
        sb.append("import ").append(entityPackage).append(".").append(entityName).append(";\n");
        sb.append("import ").append(getPackageName(config)).append(".service.").append(getEntityName(config)).append("ImportService;\n");
        sb.append("import com.jcloud.system.utils.R;\n");
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        sb.append("import org.springframework.web.bind.annotation.PostMapping;\n");
        sb.append("import org.springframework.web.bind.annotation.RequestMapping;\n");
        sb.append("import org.springframework.web.bind.annotation.RequestParam;\n");
        sb.append("import org.springframework.web.bind.annotation.RestController;\n");
        sb.append("import org.springframework.web.multipart.MultipartFile;\n\n");

        sb.append("import java.util.List;\n\n");

        sb.append("@RestController\n");
        sb.append("@RequestMapping(\"/").append(config.getTableName()).append("\")\n");
        sb.append("public class ").append(controllerName).append(" {\n\n");

        sb.append("    @Autowired\n");
        sb.append("    private ").append(getEntityName(config)).append("ImportService importService;\n\n");

        sb.append("    @PostMapping(\"/execute\")\n");
        sb.append("    public R importExcel(@RequestParam(\"file\") MultipartFile file) {\n");
        sb.append("        try {\n");
        sb.append("            ImportParams params = new ImportParams();\n");
        sb.append("            params.setTitleRows(").append(config.getTitleRows()).append(");\n");
        sb.append("            params.setHeadRows(").append(config.getHeadRows()).append(");\n");
        sb.append("            params.setNeedVerify(\"").append(config.getNeedVerify()).append("\");\n\n");

        if ("Y".equals(config.getNeedVerify())) {
            sb.append("            ExcelImportResult<").append(entityName).append("> importResult = ExcelImportUtil.importExcelMore(\n");
            sb.append("                    file.getInputStream(),\n");
            sb.append("                    ").append(entityName).append(".class,\n");
            sb.append("                    params\n");
            sb.append("            );\n\n");

            sb.append("            // 调用Service处理并插入数据库\n");
            sb.append("            return importService.importData(importResult.getList(), importResult.getFailList(), importResult.isVerifyFail());\n");
        } else {
            sb.append("            List<").append(entityName).append("> list = ExcelImportUtil.importExcel(\n");
            sb.append("                    file.getInputStream(),\n");
            sb.append("                    ").append(entityName).append(".class,\n");
            sb.append("                    params\n");
            sb.append("            );\n\n");

            sb.append("            // 调用Service处理并插入数据库\n");
            sb.append("            return importService.importData(list, null, false);\n");
        }

        sb.append("        } catch (Exception e) {\n");
        sb.append("            return R.error(\"导入失败: \" + e.getMessage());\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    public static String generateMapperCode(ExcelImportConfig config) {
        StringBuilder sb = new StringBuilder();

        String packageName = getPackageName(config) + ".mapper";
        String entityName = getEntityName(config) + "Import";
        String entityPackage = getPackageName(config) + ".entity";
        String mapperName = getEntityName(config) + "ImportMapper";

        sb.append("package ").append(packageName).append(";\n\n");

        sb.append("import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n");
        sb.append("import ").append(entityPackage).append(".").append(entityName).append(";\n\n");

        sb.append("public interface ").append(mapperName).append(" extends BaseMapper<").append(entityName).append("> {\n\n");
        sb.append("}\n");

        return sb.toString();
    }

    public static String generateServiceCode(ExcelImportConfig config) {
        StringBuilder sb = new StringBuilder();

        String packageName = getPackageName(config) + ".service";
        String entityName = getEntityName(config) + "Import";
        String entityPackage = getPackageName(config) + ".entity";
        String mapperPackage = getPackageName(config) + ".mapper";
        String mapperName = getEntityName(config) + "ImportMapper";
        String serviceName = getEntityName(config) + "ImportService";
        String serviceImplName = getEntityName(config) + "ImportServiceImpl";

        // 生成Service接口
        sb.append("package ").append(packageName).append(";\n\n");

        sb.append("import ").append(entityPackage).append(".").append(entityName).append(";\n");
        sb.append("import com.jcloud.system.utils.R;\n");
        sb.append("import java.util.List;\n\n");

        sb.append("public interface ").append(serviceName).append(" {\n\n");
        sb.append("    /**\n");
        sb.append("     * 处理并导入Excel数据\n");
        sb.append("     * @param dataList 成功解析的数据列表\n");
        sb.append("     * @param failList 校验失败的数据列表\n");
        sb.append("     * @param isVerifyFail 是否有校验失败的数据\n");
        sb.append("     * @return 导入结果\n");
        sb.append("     */\n");
        sb.append("    R importData(List<").append(entityName).append("> dataList, List<").append(entityName).append("> failList, boolean isVerifyFail);\n\n");
        sb.append("}\n");

        return sb.toString();
    }

    public static String generateServiceImplCode(ExcelImportConfig config) {
        StringBuilder sb = new StringBuilder();

        String packageName = getPackageName(config) + ".service.impl";
        String entityName = getEntityName(config) + "Import";
        String entityPackage = getPackageName(config) + ".entity";
        String mapperPackage = getPackageName(config) + ".mapper";
        String mapperName = getEntityName(config) + "ImportMapper";
        String serviceName = getEntityName(config) + "ImportService";
        String serviceImplName = getEntityName(config) + "ImportServiceImpl";

        sb.append("package ").append(packageName).append(";\n\n");

        sb.append("import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;\n");
        sb.append("import ").append(entityPackage).append(".").append(entityName).append(";\n");
        sb.append("import ").append(mapperPackage).append(".").append(mapperName).append(";\n");
        sb.append("import ").append(getPackageName(config)).append(".service.").append(serviceName).append(";\n");
        sb.append("import com.jcloud.system.entity.UserEntity;\n");
        sb.append("import com.jcloud.system.service.SqlService;\n");
        sb.append("import com.jcloud.system.shiro.ShiroUtils;\n");
        sb.append("import com.jcloud.system.utils.R;\n");
        sb.append("import org.slf4j.Logger;\n");
        sb.append("import org.slf4j.LoggerFactory;\n");
        sb.append("import org.springframework.beans.factory.annotation.Autowired;\n");
        sb.append("import org.springframework.stereotype.Service;\n");
        sb.append("import org.springframework.transaction.annotation.Transactional;\n\n");

        sb.append("import java.util.ArrayList;\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Map;\n\n");

        sb.append("@Service\n");
        sb.append("public class ").append(serviceImplName).append(" extends ServiceImpl<").append(mapperName).append(", ").append(entityName).append("> implements ").append(serviceName).append(" {\n\n");

        sb.append("    public static final Logger LOGGER = LoggerFactory.getLogger(").append(serviceImplName).append(".class);\n\n");

        sb.append("    @Autowired\n");
        sb.append("    private SqlService sqlService;\n\n");

        sb.append("    @Override\n");
        sb.append("    @Transactional\n");
        sb.append("    public R importData(List<").append(entityName).append("> dataList, List<").append(entityName).append("> failList, boolean isVerifyFail) {\n");
        sb.append("        // 校验失败，收集错误信息并返回\n");
        sb.append("        if (isVerifyFail && failList != null && !failList.isEmpty()) {\n");
        sb.append("            List<String> errorLines = new ArrayList<>();\n");
        sb.append("            errorLines.add(\"数据校验失败，共\" + failList.size() + \"条数据有误：\");\n");
        sb.append("            for (int i = 0; i < failList.size(); i++) {\n");
        sb.append("                ").append(entityName).append(" fail = failList.get(i);\n");
        sb.append("                String msg = fail.getErrorMsg();\n");
        sb.append("                if (msg != null && !msg.isEmpty()) {\n");
        sb.append("                    // 替换EasyPOI的通用错误提示为更友好的提示\n");
        sb.append("                    msg = msg.replace(\"Excel 值获取失败\", \"格式不正确\");\n");
        sb.append("                    Integer rowNum = fail.getRowNum() != null ? fail.getRowNum() : (i + 2);\n");
        sb.append("                    errorLines.add(\"第\" + rowNum + \"行：\" + msg);\n");
        sb.append("                }\n");
        sb.append("            }\n");
        sb.append("            return R.error(String.join(\"\\n\", errorLines));\n");
        sb.append("        }\n\n");

        sb.append("        if (dataList == null || dataList.isEmpty()) {\n");
        sb.append("            return R.error(\"导入数据为空\");\n");
        sb.append("        }\n\n");

        sb.append("        // 获取当前用户和时间\n");
        sb.append("        UserEntity user = ShiroUtils.getUserEntity();\n");
        sb.append("        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");\n");
        sb.append("        String now = sdf.format(new java.util.Date());\n\n");

        // 检查 createTime 字段类型
        boolean createTimeIsDateType = false;
        boolean hasCreateTimeInFields = false;
        for (ExcelImportField field : config.getFieldList()) {
            if ("createTime".equals(field.getFieldName()) || "create_time".equals(field.getFieldName())) {
                hasCreateTimeInFields = true;
                createTimeIsDateType = "Date".equals(field.getJavaType());
                break;
            }
        }
        if (!hasCreateTimeInFields) {
            createTimeIsDateType = true; // 自动添加的 createTime 是 Date 类型
        }

        sb.append("        // 数据处理和校验\n");
        sb.append("        List<String> errors = new ArrayList<>();\n");
        sb.append("        for (int i = 0; i < dataList.size(); i++) {\n");
        sb.append("            ").append(entityName).append(" data = dataList.get(i);\n");
        sb.append("            Integer rowNum = data.getRowNum(); // 从实体类获取真实的 Excel 行号\n\n");

        // 生成唯一性校验代码
        if (config.getFieldList() != null) {
            for (ExcelImportField field : config.getFieldList()) {
                if ("Y".equals(field.getUniqueFlag())) {
                    String fieldName = field.getFieldName();
                    String columnName = toUnderline(fieldName); // 将驼峰转为下划线（如 purOrg -> pur_org）
                    String columnComment = field.getColumnComment() != null ? field.getColumnComment() : fieldName;
                    String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                    
                    sb.append("            // 唯一性校验：").append(columnComment).append("\n");
                    sb.append("            if (data.").append(getterName).append("() != null && !data.").append(getterName).append("().isEmpty()) {\n");
                    sb.append("                Object count").append(fieldName).append(" = sqlService.getOnef(\"select count(1) from ").append(config.getTableName()).append(" where ").append(columnName).append(" = '\" + data.").append(getterName).append("() + \"'\");\n");
                    sb.append("                long cnt").append(fieldName).append(" = count").append(fieldName).append(" != null ? Long.parseLong(count").append(fieldName).append(".toString()) : 0;\n");
                    sb.append("                if (cnt").append(fieldName).append(" > 0) {\n");
                    sb.append("                    errors.add(\"第\" + rowNum + \"行：").append(columnComment).append("[\" + data.").append(getterName).append("() + \"]已存在，不允许重复\");\n");
                    sb.append("                }\n");
                    sb.append("            }\n\n");
                }
            }
        }

        sb.append("            // TODO: 在此添加其他数据处理和校验逻辑\n\n");

        sb.append("            // 示例1：必输字段校验\n");
        sb.append("            // if (data.getCode() == null || data.getCode().isEmpty()) {\n");
        sb.append("            //     errors.add(\"第\" + rowNum + \"行：编码不能为空\");\n");
        sb.append("            // }\n\n");

        sb.append("            // 示例2：根据code查询name并赋值\n");
        sb.append("            // if (data.getCode() != null && !data.getCode().isEmpty()) {\n");
        sb.append("            //     Map<String, Object> exist = sqlService.getOne(\"select name from some_table where code = '\" + data.getCode() + \"'\");\n");
        sb.append("            //     if (exist == null) {\n");
        sb.append("            //         errors.add(\"第\" + rowNum + \"行：编码[\" + data.getCode() + \"]不存在\");\n");
        sb.append("            //     } else {\n");
        sb.append("            //         data.setName(exist.get(\"name\") != null ? exist.get(\"name\").toString() : \"\");\n");
        sb.append("            //     }\n");
        sb.append("            // }\n\n");

        sb.append("            // 示例3：校验code是否在数据库中存在\n");
        sb.append("            // if (data.getCode() != null && !data.getCode().isEmpty()) {\n");
        sb.append("            //     Object count = sqlService.getOnef(\"select count(1) from some_table where code = '\" + data.getCode() + \"'\");\n");
        sb.append("            //     long cnt = count != null ? Long.parseLong(count.toString()) : 0;\n");
        sb.append("            //     if (cnt == 0) {\n");
        sb.append("            //         errors.add(\"第\" + rowNum + \"行：编码[\" + data.getCode() + \"]在数据库中不存在\");\n");
        sb.append("            //     }\n");
        sb.append("            // }\n\n");

        sb.append("            // 示例4：设置默认值\n");
        sb.append("            // if (data.getStatus() == null || data.getStatus().isEmpty()) {\n");
        sb.append("            //     data.setStatus(\"1\");\n");
        sb.append("            // }\n\n");

        sb.append("            // 设置生成人和生成时间\n");
        sb.append("            data.setCreateUserId(user.getUserId().toString());\n");
        if (createTimeIsDateType) {
            sb.append("            data.setCreateTime(new java.util.Date());\n");
        } else {
            sb.append("            data.setCreateTime(now);\n");
        }
        sb.append("        }\n\n");

        sb.append("        // 处理校验失败，阻止导入\n");
        sb.append("        if (!errors.isEmpty()) {\n");
        sb.append("            StringBuilder sb = new StringBuilder(\"数据处理失败，共\" + errors.size() + \"个错误：\\n\");\n");
        sb.append("            for (String error : errors) {\n");
        sb.append("                sb.append(error).append(\"\\n\");\n");
        sb.append("            }\n");
        sb.append("            return R.error(sb.toString().trim());\n");
        sb.append("        }\n\n");

        sb.append("        // 批量插入数据库\n");
        sb.append("        boolean flag = this.saveBatch(dataList);\n");
        sb.append("        if (flag) {\n");
        sb.append("            LOGGER.info(\"批量插入成功，共插入\" + dataList.size() + \"条数据\");\n");
        sb.append("            return R.ok(\"导入成功，共插入\" + dataList.size() + \"条数据\");\n");
        sb.append("        } else {\n");
        sb.append("            return R.error(\"数据插入失败\");\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    public static void writeToFile(String code, String relativePath) throws IOException {
        String fullPath = BASE_PATH + relativePath;
        File file = new File(fullPath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(code);
        }
    }

    public static String generateAndSave(ExcelImportConfig config) throws IOException {
        // 按 orderNum 排序字段列表
        if (config.getFieldList() != null) {
            config.getFieldList().sort((a, b) -> {
                int sortA = a.getOrderNum() != null ? a.getOrderNum() : 0;
                int sortB = b.getOrderNum() != null ? b.getOrderNum() : 0;
                return Integer.compare(sortA, sortB);
            });
        }

        String entityCode = generateEntityCode(config);
        String controllerCode = generateControllerCode(config);
        String mapperCode = generateMapperCode(config);
        String serviceCode = generateServiceCode(config);
        String serviceImplCode = generateServiceImplCode(config);

        String packageName = getPackageName(config);
        String entityName = getEntityName(config) + "Import";
        String controllerName = getEntityName(config) + "ImportController";
        String mapperName = getEntityName(config) + "ImportMapper";
        String serviceName = getEntityName(config) + "ImportService";
        String serviceImplName = getEntityName(config) + "ImportServiceImpl";

        String entityPath = packageName.replace(".", "/") + "/entity/" + entityName + ".java";
        String controllerPath = packageName.replace(".", "/") + "/controller/" + controllerName + ".java";
        String mapperPath = packageName.replace(".", "/") + "/mapper/" + mapperName + ".java";
        String servicePath = packageName.replace(".", "/") + "/service/" + serviceName + ".java";
        String serviceImplPath = packageName.replace(".", "/") + "/service/impl/" + serviceImplName + ".java";

        writeToFile(entityCode, entityPath);
        writeToFile(controllerCode, controllerPath);
        writeToFile(mapperCode, mapperPath);
        writeToFile(serviceCode, servicePath);
        writeToFile(serviceImplCode, serviceImplPath);

        // 生成Excel模板文件
        String templatePath = TEMPLATE_PATH + getEntityName(config) + "_import_template.xlsx";
        generateExcelTemplate(config, templatePath);

        return "生成成功:\n- 实体类: " + entityPath + "\n- Controller: " + controllerPath + "\n- Mapper: " + mapperPath + "\n- Service接口: " + servicePath + "\n- Service实现: " + serviceImplPath + "\n- Excel模板: " + templatePath;
    }

    public static void generateExcelTemplate(ExcelImportConfig config, String relativePath) throws IOException {
        String fullPath = relativePath;
        File file = new File(fullPath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("导入模板");

            // 创建表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);

            // 创建必填样式（红色字体）
            CellStyle requiredStyle = workbook.createCellStyle();
            requiredStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            requiredStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            requiredStyle.setBorderBottom(BorderStyle.THIN);
            requiredStyle.setBorderTop(BorderStyle.THIN);
            requiredStyle.setBorderLeft(BorderStyle.THIN);
            requiredStyle.setBorderRight(BorderStyle.THIN);
            Font requiredFont = workbook.createFont();
            requiredFont.setBold(true);
            requiredFont.setColor(IndexedColors.RED.getIndex());
            requiredStyle.setFont(requiredFont);

            // 创建普通表头样式（白色背景）
            CellStyle normalHeaderStyle = workbook.createCellStyle();
            normalHeaderStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            normalHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            normalHeaderStyle.setBorderBottom(BorderStyle.THIN);
            normalHeaderStyle.setBorderTop(BorderStyle.THIN);
            normalHeaderStyle.setBorderLeft(BorderStyle.THIN);
            normalHeaderStyle.setBorderRight(BorderStyle.THIN);
            Font normalFont = workbook.createFont();
            normalFont.setBold(true);
            normalFont.setFontHeightInPoints((short) 11);
            normalHeaderStyle.setFont(normalFont);

            // 创建标题行（如果有标题行数配置）
            int currentRow = 0;
            if (config.getTitleRows() != null && config.getTitleRows() > 0) {
                for (int i = 0; i < config.getTitleRows(); i++) {
                    Row titleRow = sheet.createRow(currentRow++);
                    Cell titleCell = titleRow.createCell(0);
                    titleCell.setCellValue(config.getTableComment() != null ? config.getTableComment() : config.getTableName());
                    CellStyle titleStyle = workbook.createCellStyle();
                    Font titleFont = workbook.createFont();
                    titleFont.setBold(true);
                    titleFont.setFontHeightInPoints((short) 14);
                    titleStyle.setFont(titleFont);
                    titleCell.setCellStyle(titleStyle);
                }
            }

            // 创建表头行
            Row headerRow = sheet.createRow(currentRow++);
            for (int i = 0; i < config.getFieldList().size(); i++) {
                ExcelImportField field = config.getFieldList().get(i);
                Cell cell = headerRow.createCell(i);
                String headerName = field.getColumnComment() != null && !field.getColumnComment().isEmpty()
                        ? field.getColumnComment() : field.getColumnName();
                if (field.getIsRequired() != null && field.getIsRequired()) {
                    headerName = "*" + headerName;
                    cell.setCellStyle(requiredStyle);
                } else {
                    cell.setCellStyle(normalHeaderStyle);
                }
                cell.setCellValue(headerName);
                // 设置列宽
                sheet.setColumnWidth(i, 20 * 256);
            }

            // 创建示例数据行
            Row sampleRow = sheet.createRow(currentRow);
            for (int i = 0; i < config.getFieldList().size(); i++) {
                ExcelImportField field = config.getFieldList().get(i);
                String sampleValue = getSampleValue(field);
                if (sampleValue != null && !sampleValue.isEmpty()) {
                    Cell cell = sampleRow.createCell(i);
                    // 日期类型需要设置单元格格式为日期
                    if ("Date".equals(field.getJavaType())) {
                        CellStyle dateStyle = workbook.createCellStyle();
                        CreationHelper createHelper = workbook.getCreationHelper();
                        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
                        cell.setCellStyle(dateStyle);
                        // 解析示例日期并设置为日期类型
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = sdf.parse(sampleValue);
                            cell.setCellValue(date);
                        } catch (Exception e) {
                            cell.setCellValue(sampleValue);
                        }
                    } else {
                        cell.setCellValue(sampleValue);
                    }
                }
            }

            // 写入文件
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    private static String getSampleValue(ExcelImportField field) {
        if (field.getReplaceValue() != null && !field.getReplaceValue().isEmpty()) {
            String[] parts = field.getReplaceValue().split(",");
            if (parts.length > 0) {
                String[] kv = parts[0].split("_");
                return kv.length > 0 ? kv[0] : "";
            }
        }
        String javaType = field.getJavaType();
        if (javaType == null) return "";
        if (javaType.contains("Date")) {
            return "2024-01-01";
        }
        switch (javaType) {
            case "Integer":
            case "Long":
                return "1";
            case "Double":
            case "Float":
            case "BigDecimal":
                return "0.0";
            case "String":
            default:
                return "";
        }
    }

    private static String getPackageName(ExcelImportConfig config) {
        if (config.getPackageName() != null && !config.getPackageName().isEmpty()) {
            return config.getPackageName();
        }
        return DEFAULT_PACKAGE;
    }

    private static String getEntityName(ExcelImportConfig config) {
        if (config.getEntityName() != null && !config.getEntityName().isEmpty()) {
            return config.getEntityName();
        }
        if (config.getTableName() != null && !config.getTableName().isEmpty()) {
            return toPascalCaseFromUnderline(config.getTableName());
        }
        return "Unknown";
    }

    private static String getModuleName(ExcelImportConfig config) {
        if (config.getModuleName() != null && !config.getModuleName().isEmpty()) {
            return config.getModuleName();
        }
        if (config.getTableName() != null && !config.getTableName().isEmpty()) {
            String[] parts = config.getTableName().split("_");
            if (parts.length > 1) {
                return parts[0];
            }
        }
        return "default";
    }

    private static String toPascalCaseFromUnderline(String name) {
        if (name == null || name.isEmpty()) return name;
        String[] parts = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    sb.append(part.substring(1).toLowerCase());
                }
            }
        }
        return sb.toString();
    }

    private static boolean isNumericType(String javaType) {
        return "Integer".equals(javaType) || "Long".equals(javaType) ||
               "Double".equals(javaType) || "Float".equals(javaType) ||
               "BigDecimal".equals(javaType);
    }

    private static String toCamelCase(String name) {
        if (name == null || name.isEmpty()) return name;
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private static String toPascalCase(String name) {
        if (name == null || name.isEmpty()) return name;
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * 规范化日期格式，将小写的 mm（分钟）转换为大写的 MM（月份）
     */
    private static String normalizeDateFormat(String format) {
        if (format == null || format.isEmpty()) {
            return "yyyy-MM-dd";
        }
        // 将 yyyy-mm-dd 转换为 yyyy-MM-dd（只替换小写 mm，不影响已有的 MM）
        return format.replace("mm", "MM");
    }

    private static String toUnderline(String name) {
        if (name == null || name.isEmpty()) return name;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append("_");
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
