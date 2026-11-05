-- Excel 导入配置表
CREATE TABLE IF NOT EXISTS `excel_import_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `table_name` VARCHAR(100) NOT NULL COMMENT '表名',
  `table_comment` VARCHAR(200) DEFAULT NULL COMMENT '表描述',
  `entity_name` VARCHAR(100) NOT NULL COMMENT '实体类名',
  `package_name` VARCHAR(200) NOT NULL COMMENT '包名',
  `module_name` VARCHAR(100) NOT NULL COMMENT '模块名',
  `title_rows` INT DEFAULT 0 COMMENT '标题行数',
  `head_rows` INT DEFAULT 1 COMMENT '表头行数',
  `need_verify` TINYINT DEFAULT 0 COMMENT '是否需要校验',
  `status` VARCHAR(10) DEFAULT '1' COMMENT '状态 1-启用 0-停用',
  `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_table_name` (`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Excel导入配置表';

-- Excel 导入字段配置表
CREATE TABLE IF NOT EXISTS `excel_import_field` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_id` BIGINT NOT NULL COMMENT '配置ID',
  `column_name` VARCHAR(100) NOT NULL COMMENT 'Excel列名',
  `column_comment` VARCHAR(200) DEFAULT NULL COMMENT '字段描述',
  `field_name` VARCHAR(100) NOT NULL COMMENT 'Java字段名',
  `java_type` VARCHAR(50) NOT NULL COMMENT 'Java类型',
  `column_length` INT DEFAULT 50 COMMENT '字段长度',
  `order_num` INT NOT NULL COMMENT '列顺序',
  `is_required` TINYINT DEFAULT 0 COMMENT '是否必输',
  `date_format` VARCHAR(50) DEFAULT NULL COMMENT '日期格式',
  `replace_value` VARCHAR(500) DEFAULT NULL COMMENT '替换值',
  `default_value` VARCHAR(200) DEFAULT NULL COMMENT '默认值',
  `validate_rule` VARCHAR(200) DEFAULT NULL COMMENT '校验规则',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `idx_config_id` (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Excel导入字段配置表';

-- 示例数据：物品主数据导入配置
INSERT INTO `excel_import_config` (`table_name`, `table_comment`, `entity_name`, `package_name`, `module_name`, `title_rows`, `head_rows`, `need_verify`) VALUES
('epd_item_master', '物品主数据', 'EpdItemMaster', 'com.jcloud.erp.epd', 'epd', 0, 1, 1);

-- 获取刚插入的配置ID
SET @config_id = LAST_INSERT_ID();

-- 插入字段配置
INSERT INTO `excel_import_field` (`config_id`, `column_name`, `column_comment`, `field_name`, `java_type`, `column_length`, `order_num`, `is_required`, `date_format`, `replace_value`) VALUES
(@config_id, '物品编码', '物品编码', 'itemCode', 'String', 50, 1, 1, NULL, NULL),
(@config_id, '物品名称', '物品名称', 'itemName', 'String', 200, 2, 1, NULL, NULL),
(@config_id, '规格型号', '规格型号', 'specification', 'String', 200, 3, 0, NULL, NULL),
(@config_id, '单位', '单位', 'unit', 'String', 50, 4, 1, NULL, NULL),
(@config_id, '单价', '单价', 'price', 'Double', 10, 5, 0, NULL, NULL),
(@config_id, '分类', '分类', 'category', 'String', 50, 6, 0, NULL, '电子_1,机械_2,其他_3'),
(@config_id, '创建日期', '创建日期', 'createDate', 'Date', NULL, 7, 0, 'yyyy-MM-dd', NULL),
(@config_id, '是否启用', '是否启用', 'status', 'String', 10, 8, 1, NULL, '启用_1,停用_0');
