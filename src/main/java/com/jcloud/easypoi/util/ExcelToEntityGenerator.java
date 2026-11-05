package com.jcloud.easypoi.util;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelToEntityGenerator {

    private static final Map<String, String> JAVA_TYPE_MAP = new HashMap<>();

    static {
        JAVA_TYPE_MAP.put("string", "String");
        JAVA_TYPE_MAP.put("integer", "Integer");
        JAVA_TYPE_MAP.put("int", "Integer");
        JAVA_TYPE_MAP.put("long", "Long");
        JAVA_TYPE_MAP.put("double", "Double");
        JAVA_TYPE_MAP.put("float", "Double");
        JAVA_TYPE_MAP.put("boolean", "Boolean");
        JAVA_TYPE_MAP.put("date", "Date");
        JAVA_TYPE_MAP.put("bigdecimal", "BigDecimal");
    }

    /**
     * 从 Excel 文件生成实体类代码
     *
     * @param excelFilePath Excel 文件路径
     * @param className     实体类名
     * @param packageName   包名
     * @return 实体类代码
     */
    public static String generateFromExcel(String excelFilePath, String className, String packageName) throws Exception {
        File file = new File(excelFilePath);
        try (FileInputStream fis = new FileInputStream(file)) {
            return generateFromInputStream(fis, className, packageName, file.getName().endsWith(".xlsx"));
        }
    }

    /**
     * 从输入流生成实体类代码
     *
     * @param inputStream Excel 输入流
     * @param className   实体类名
     * @param packageName 包名
     * @param isXlsx      是否为 xlsx 格式
     * @return 实体类代码
     */
    public static String generateFromInputStream(InputStream inputStream, String className, String packageName, boolean isXlsx) throws Exception {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        return generateFromSheet(sheet, className, packageName);
    }

    /**
     * 从 Sheet 生成实体类代码
     *
     * @param sheet       Excel Sheet
     * @param className   实体类名
     * @param packageName 包名
     * @return 实体类代码
     */
    public static String generateFromSheet(Sheet sheet, String className, String packageName) {
        StringBuilder sb = new StringBuilder();

        // 包声明
        sb.append("package ").append(packageName).append(";\n\n");

        // 导入
        sb.append("import com.jcloud.easypoi.excel.annotation.Excel;\n");
        sb.append("import com.jcloud.easypoi.excel.annotation.ExcelTarget;\n");
        sb.append("import java.io.Serializable;\n");
        sb.append("import java.util.Date;\n");
        sb.append("import java.math.BigDecimal;\n\n");

        // 类声明
        String targetId = toCamelCase(className) + "Import";
        sb.append("@ExcelTarget(\"").append(targetId).append("\")\n");
        sb.append("public class ").append(className).append(" implements Serializable {\n\n");
        sb.append("    private static final long serialVersionUID = 1L;\n\n");

        // 读取表头行（默认第一行为表头）
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new RuntimeException("Excel 文件为空，未找到表头行");
        }

        int orderNum = 1;
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null) continue;

            String columnName = getCellValueAsString(cell);
            if (columnName == null || columnName.trim().isEmpty()) continue;

            columnName = columnName.trim();
            String fieldName = toCamelCase(columnName);
            String javaType = guessJavaType(sheet, i);

            // 生成字段和注解
            sb.append("    /**\n");
            sb.append("     * ").append(columnName).append("\n");
            sb.append("     */\n");

            // 生成 @Excel 注解
            sb.append("    @Excel(name = \"").append(columnName).append("\", orderNum = \"").append(orderNum).append("\", isImportField = \"true\"");

            // 如果是日期类型，添加 format
            if ("Date".equals(javaType)) {
                sb.append(", format = \"yyyy-MM-dd\"");
            }

            // 如果是数字类型，添加 type=10
            if ("Double".equals(javaType) || "Integer".equals(javaType) || "Long".equals(javaType) || "BigDecimal".equals(javaType)) {
                sb.append(", type = 10");
            }

            sb.append(")\n");

            // 生成字段声明
            sb.append("    private ").append(javaType).append(" ").append(fieldName).append(";\n\n");

            // 生成 getter
            sb.append("    public ").append(javaType).append(" get").append(toPascalCase(fieldName)).append("() { return ").append(fieldName).append("; }\n");

            // 生成 setter
            sb.append("    public void set").append(toPascalCase(fieldName)).append("(").append(javaType).append(" ").append(fieldName).append(") { this.").append(fieldName).append(" = ").append(fieldName).append("; }\n\n");

            orderNum++;
        }

        sb.append("}\n");

        return sb.toString();
    }

    /**
     * 获取单元格值
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                double numVal = cell.getNumericCellValue();
                if (numVal == (int) numVal) {
                    return String.valueOf((int) numVal);
                }
                return String.valueOf(numVal);
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * 猜测 Java 类型
     */
    private static String guessJavaType(Sheet sheet, int columnIndex) {
        // 读取前几行数据来猜测类型
        for (int i = 1; i <= Math.min(10, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            Cell cell = row.getCell(columnIndex);
            if (cell == null) continue;

            int cellType = cell.getCellType();
            if (cellType == Cell.CELL_TYPE_STRING) {
                String value = cell.getStringCellValue();
                if (value == null || value.trim().isEmpty()) continue;

                // 检查是否为数字字符串
                try {
                    Double.parseDouble(value);
                    return "Double";
                } catch (NumberFormatException e) {
                    // 检查是否为日期
                    if (value.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
                        return "Date";
                    }
                    return "String";
                }
            } else if (cellType == Cell.CELL_TYPE_NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return "Date";
                }
                double val = cell.getNumericCellValue();
                if (val == (int) val) {
                    return "Integer";
                }
                return "Double";
            } else if (cellType == Cell.CELL_TYPE_BOOLEAN) {
                return "Boolean";
            }
        }

        // 默认返回 String
        return "String";
    }

    /**
     * 转换为驼峰命名
     */
    private static String toCamelCase(String name) {
        if (name == null || name.isEmpty()) return name;

        // 移除特殊字符
        name = name.replaceAll("[^\\w\\s\\u4e00-\\u9fa5]", "");

        // 如果是纯中文，使用拼音或直接使用中文拼音首字母
        if (name.matches("[\\u4e00-\\u9fa5]+")) {
            return toPinyinCamelCase(name);
        }

        // 包含空格或下划线，转换为驼峰
        String[] parts = name.split("[\\s_]+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                sb.append(parts[i].toLowerCase());
            } else {
                sb.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    sb.append(parts[i].substring(1).toLowerCase());
                }
            }
        }
        return sb.toString();
    }

    /**
     * 转换为 PascalCase（首字母大写）
     */
    private static String toPascalCase(String name) {
        if (name == null || name.isEmpty()) return name;
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    /**
     * 中文转拼音驼峰（简化版）
     */
    private static String toPinyinCamelCase(String chinese) {
        // 简化处理：使用中文首字母
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chinese.length(); i++) {
            char c = chinese.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                sb.append(c);
            } else if (c >= '0' && c <= '9') {
                sb.append(c);
            } else {
                // 中文字符，使用 Unicode 编码作为占位
                // 实际项目中可以集成 pinyin4j 库
                sb.append("field").append(i);
            }
        }
        return sb.length() > 0 ? sb.toString() : "field";
    }

    /**
     * 打印生成的代码到控制台
     */
    public static void printGeneratedCode(String excelFilePath, String className, String packageName) throws Exception {
        String code = generateFromExcel(excelFilePath, className, packageName);
        System.out.println("========== 生成的实体类代码 ==========");
        System.out.println(code);
        System.out.println("=====================================");
    }

    /**
     * 主方法 - 用于测试
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("使用方法:");
            System.out.println("java ExcelToEntityGenerator <Excel文件路径> <实体类名> <包名>");
            System.out.println("示例:");
            System.out.println("java ExcelToEntityGenerator C:/data/物品导入模板.xlsx EpdItemMaster com.jcloud.erp.epd.entity");
            return;
        }

        try {
            printGeneratedCode(args[0], args[1], args[2]);
        } catch (Exception e) {
            System.err.println("生成失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
