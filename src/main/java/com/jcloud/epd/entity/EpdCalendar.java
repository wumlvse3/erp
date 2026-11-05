package com.jcloud.epd.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("epd_calendar")
public class EpdCalendar extends Model<EpdCalendar> {

    @TableId(value = "uu_id", type = IdType.AUTO)
    private Integer uu_id;

    @TableField(value = "inv_org")
    private String inv_org;

    @TableField(value = "group_id")
    private String group_id;

    @TableField(value = "calendar_id")
    private String calendar_id;

    @TableField(value = "calendar_date")
    private Date calendar_date;

    @TableField(value = "week_day")
    private String week_day;

    @TableField(value = "work_status")
    private Integer work_status;

    @TableField(value = "festival_name")
    private String festival_name;

    @TableField(value = "is_festival")
    @JsonDeserialize(using = BooleanOrIntegerDeserializer.class)
    private Integer is_festival;

    @TableField(value = "is_cycle")
    @JsonDeserialize(using = BooleanOrIntegerDeserializer.class)
    private Integer is_cycle;

    @TableField(value = "day_of_month")
    private Integer day_of_month;

    @TableField(value = "month_of_year")
    private Integer month_of_year;

    @TableField(value = "lunar_date")
    private String lunar_date;

    @TableField(value = "lunar_month")
    private Integer lunar_month;

    @TableField(value = "lunar_day")
    private Integer lunar_day;

    @TableField(value = "is_legal_holiday")
    @JsonDeserialize(using = BooleanOrIntegerDeserializer.class)
    private Integer is_legal_holiday;

    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date create_date;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String create_by;

    @TableField(value = "update_date", fill = FieldFill.UPDATE)
    private Date update_date;

    @TableField(value = "update_by", fill = FieldFill.UPDATE)
    private String update_by;

    @Override
    public Serializable pkVal() {
        return this.uu_id;
    }
}