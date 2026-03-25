package com.jcloud.customer.WmsBaseData.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BaseSysCodeVo {
    @ApiModelProperty(value = "主键")
    @TableId(value = "id")
    private String id;

    @ApiModelProperty(value = "组织")
    private String org_id;

    @ApiModelProperty(value = "创建人")
    @TableField(value = "create_by",fill = FieldFill.INSERT)
    private String create_by;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Date create_time;

    @ApiModelProperty(value = "修改人")
    @TableField(value = "update_by",fill = FieldFill.UPDATE)
    private String update_by;

    @ApiModelProperty(value = "修改时间")
    @TableField(value = "update_time",fill = FieldFill.UPDATE)
    private Date update_time;

    @ApiModelProperty(value = "代码编码")
    private String code;

    @ApiModelProperty(value = "代码类型")
    private String code_type;

    @ApiModelProperty(value = "代码名称")
    private String name;

    @ApiModelProperty(value = "语言标识")
    private String lang_mark;

    @ApiModelProperty(value = "文件")
    private String file_info;

    @ApiModelProperty(value = "层级，默认为0")
    private Integer level;

    @ApiModelProperty(value = "父级代码")
    private String parent_code;

    @ApiModelProperty(value = "父级id")
    private String parent_id;

    @ApiModelProperty(value = "父级名称")
    private String parent_name;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "顶层及所有子级共有id")
    private String root_id;

    private List<BaseSysCodeVo> children;
}
