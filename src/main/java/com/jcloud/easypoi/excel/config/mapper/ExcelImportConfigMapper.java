package com.jcloud.easypoi.excel.config.mapper;

import com.jcloud.easypoi.excel.config.entity.ExcelImportConfig;
import com.jcloud.easypoi.excel.config.entity.ExcelImportField;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExcelImportConfigMapper {

    @Select("SELECT id, table_name, table_comment, entity_name, package_name, module_name, " +
            "title_rows, head_rows, need_verify, status, create_by, create_time, " +
            "update_by, update_time, remark FROM excel_import_config WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "tableName", column = "table_name"),
        @Result(property = "tableComment", column = "table_comment"),
        @Result(property = "entityName", column = "entity_name"),
        @Result(property = "packageName", column = "package_name"),
        @Result(property = "moduleName", column = "module_name"),
        @Result(property = "titleRows", column = "title_rows"),
        @Result(property = "headRows", column = "head_rows"),
        @Result(property = "needVerify", column = "need_verify"),
        @Result(property = "status", column = "status"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateBy", column = "update_by"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "remark", column = "remark")
    })
    ExcelImportConfig selectById(Long id);

    @Select("SELECT id, table_name, table_comment, entity_name, package_name, module_name, " +
            "title_rows, head_rows, need_verify, status, create_by, create_time, " +
            "update_by, update_time, remark FROM excel_import_config WHERE table_name = #{tableName}")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "tableName", column = "table_name"),
        @Result(property = "tableComment", column = "table_comment"),
        @Result(property = "entityName", column = "entity_name"),
        @Result(property = "packageName", column = "package_name"),
        @Result(property = "moduleName", column = "module_name"),
        @Result(property = "titleRows", column = "title_rows"),
        @Result(property = "headRows", column = "head_rows"),
        @Result(property = "needVerify", column = "need_verify"),
        @Result(property = "status", column = "status"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateBy", column = "update_by"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "remark", column = "remark")
    })
    ExcelImportConfig selectByTableName(String tableName);

    @Select("SELECT id, table_name, table_comment, entity_name, package_name, module_name, " +
            "title_rows, head_rows, need_verify, status, create_by, create_time, " +
            "update_by, update_time, remark FROM excel_import_config ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "tableName", column = "table_name"),
        @Result(property = "tableComment", column = "table_comment"),
        @Result(property = "entityName", column = "entity_name"),
        @Result(property = "packageName", column = "package_name"),
        @Result(property = "moduleName", column = "module_name"),
        @Result(property = "titleRows", column = "title_rows"),
        @Result(property = "headRows", column = "head_rows"),
        @Result(property = "needVerify", column = "need_verify"),
        @Result(property = "status", column = "status"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateBy", column = "update_by"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "remark", column = "remark")
    })
    List<ExcelImportConfig> selectAll();

    @Insert("INSERT INTO excel_import_config (table_name, table_comment, entity_name, package_name, module_name, " +
            "title_rows, head_rows, need_verify, status, create_by, create_time, remark) " +
            "VALUES (#{tableName}, #{tableComment}, #{entityName}, #{packageName}, #{moduleName}, " +
            "#{titleRows}, #{headRows}, #{needVerify}, #{status}, #{createBy}, #{createTime}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ExcelImportConfig config);

    @Update("UPDATE excel_import_config SET table_comment = #{tableComment}, entity_name = #{entityName}, " +
            "package_name = #{packageName}, module_name = #{moduleName}, title_rows = #{titleRows}, " +
            "head_rows = #{headRows}, need_verify = #{needVerify}, status = #{status}, " +
            "update_by = #{updateBy}, update_time = #{updateTime}, remark = #{remark} " +
            "WHERE id = #{id}")
    int update(ExcelImportConfig config);

    @Delete("DELETE FROM excel_import_config WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT id, config_id, column_name, column_comment, field_name, java_type, " +
            "column_length, order_num, is_required, date_format, replace_value, default_value, validate_rule, sort_order, unique_flag " +
            "FROM excel_import_field WHERE config_id = #{configId} ORDER BY order_num ASC")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "configId", column = "config_id"),
        @Result(property = "columnName", column = "column_name"),
        @Result(property = "columnComment", column = "column_comment"),
        @Result(property = "fieldName", column = "field_name"),
        @Result(property = "javaType", column = "java_type"),
        @Result(property = "columnLength", column = "column_length"),
        @Result(property = "orderNum", column = "order_num"),
        @Result(property = "isRequired", column = "is_required"),
        @Result(property = "dateFormat", column = "date_format"),
        @Result(property = "replaceValue", column = "replace_value"),
        @Result(property = "defaultValue", column = "default_value"),
        @Result(property = "validateRule", column = "validate_rule"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "uniqueFlag", column = "unique_flag")
    })
    List<ExcelImportField> selectFieldsByConfigId(Long configId);

    @Insert("INSERT INTO excel_import_field (config_id, column_name, column_comment, field_name, java_type, " +
            "column_length, order_num, is_required, date_format, replace_value, default_value, validate_rule, sort_order, unique_flag) " +
            "VALUES (#{configId}, #{columnName}, #{columnComment}, #{fieldName}, #{javaType}, " +
            "#{columnLength}, #{orderNum}, #{isRequired}, #{dateFormat}, #{replaceValue}, #{defaultValue}, #{validateRule}, #{sortOrder}, #{uniqueFlag})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertField(ExcelImportField field);

    @Delete("DELETE FROM excel_import_field WHERE config_id = #{configId}")
    int deleteFieldsByConfigId(Long configId);

    @Select("SELECT id, table_name, table_comment, entity_name, package_name, module_name, " +
            "title_rows, head_rows, need_verify, status, create_by, create_time, " +
            "update_by, update_time, remark FROM excel_import_config WHERE status = '1'")
    @Results({
        @Result(property = "id", column = "id", id = true),
        @Result(property = "tableName", column = "table_name"),
        @Result(property = "tableComment", column = "table_comment"),
        @Result(property = "entityName", column = "entity_name"),
        @Result(property = "packageName", column = "package_name"),
        @Result(property = "moduleName", column = "module_name"),
        @Result(property = "titleRows", column = "title_rows"),
        @Result(property = "headRows", column = "head_rows"),
        @Result(property = "needVerify", column = "need_verify"),
        @Result(property = "status", column = "status"),
        @Result(property = "createBy", column = "create_by"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateBy", column = "update_by"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "remark", column = "remark")
    })
    List<ExcelImportConfig> selectActiveConfigs();

    @Select("SELECT COLUMN_NAME as columnName, COLUMN_COMMENT as columnComment, DATA_TYPE as dataType, CHARACTER_MAXIMUM_LENGTH as columnLength, " +
            "IS_NULLABLE as isNullable, COLUMN_KEY as columnKey " +
            "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = #{tableName} ORDER BY ORDINAL_POSITION")
    @Results({
        @Result(property = "columnName", column = "columnName"),
        @Result(property = "columnComment", column = "columnComment"),
        @Result(property = "dataType", column = "dataType"),
        @Result(property = "columnLength", column = "columnLength"),
        @Result(property = "isNullable", column = "isNullable"),
        @Result(property = "columnKey", column = "columnKey")
    })
    List<java.util.Map<String, Object>> selectTableColumns(String tableName);
}
