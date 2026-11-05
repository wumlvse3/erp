package com.jcloud.easypoi.demo.entity;

import com.jcloud.easypoi.excel.annotation.Excel;
import com.jcloud.easypoi.excel.annotation.ExcelTarget;
import java.io.Serializable;
import java.util.Date;

@ExcelTarget("userImport")
public class UserImportEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "姓名", orderNum = "1", isImportField = "true")
    private String name;

    @Excel(name = "年龄", orderNum = "2", type = 10, isImportField = "true")
    private Integer age;

    @Excel(name = "邮箱", orderNum = "3", isImportField = "true")
    private String email;

    @Excel(name = "手机号", orderNum = "4", isImportField = "true")
    private String phone;

    @Excel(name = "性别", orderNum = "5", replace = {"男_1", "女_2"}, isImportField = "true")
    private String gender;

    @Excel(name = "出生日期", orderNum = "6", format = "yyyy-MM-dd", isImportField = "true")
    private Date birthday;

    @Excel(name = "地址", orderNum = "7", isImportField = "true")
    private String address;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
