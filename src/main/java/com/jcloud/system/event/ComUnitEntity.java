package com.jcloud.system.event;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件表
 * @Auther: yang
 * @Date: 2023/4/4 14:03
 * @Description:
 */
@Data
@TableName("sys_com_unit")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComUnitEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @TableId(type =  IdType.AUTO)
	private Long unitId;

    private String unitName;

	private Long regionId;

    private Long parentId;

    private Integer dataStatus;

	private String dataTable;

    private Integer unitType;

    private String unitContent;

    private Integer unitStatus;

    private Integer unitRight;

    private Integer unitPublish;

    private String createBy;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createDate;

    private String updateBy;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

	private String remark;

	private String unitCode;

	private String unitFile;

    private Long dataId;

    private Long rightdataId;

    private Integer rightdataType;
}