package com.jcloud.erp.epd.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * Copyright erp
 * 
 * @version V1.0.0
 * @author: wxc
 * @date:2026-04-24
 * @description:
 *		渚涘簲鍟嗗疄浣撶被
 */
@Data
@TableName("epd_vendor_def")
@ToString
public class EpdVendorDef extends Model<EpdVendorDef> {

	/**
	 * 涓婚敭
	 */
	@TableId(value = "uu_id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
	private Integer uu_id;
	/**
	 * 闆嗗洟ID
	 */
	@TableField(value = "group_id")
	private String group_id;
	/**
	 * 鍒涘缓浜篒D
	 */
	@TableField(value = "create_user_id")
	private String create_user_id;
	/**
	 * 鍒涘缓鏃堕棿
	 */
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private Date create_time;
	/**
	 * 鏇存柊浜篒D
	 */
	@TableField(value = "modify_user_id")
	private String modify_user_id;
	/**
	 * 鏇存柊鏃堕棿
	 */
	@TableField(value = "modify_time", fill = FieldFill.UPDATE)
	private Date modify_time;
	/**
	 * 宸ヤ綔娴両D
	 */
	@TableField(value = "flow_id")
	private String flow_id;
	/**
	 * 閫昏緫鍒犻櫎鏍囪
	 */
	@TableField(value = "is_deleted")
	private Boolean is_deleted;
	/**
	 * 渚涘簲鍟嗕唬鐮?	 */
	@TableField(value = "vendor_code")
	private String vendor_code;
	/**
	 * 渚涘簲鍟嗗悕绉?	 */
	@TableField(value = "vendor_name")
	private String vendor_name;
	/**
	 * 渚涘簲鍟嗙畝绉?	 */
	@TableField(value = "vendor_abb")
	private String vendor_abb;
	/**
	 * 瀹㈠晢鍒嗙被
	 */
	@TableField(value = "c_v_flag")
	private String c_v_flag;
	/**
	 * 鍐呴儴鏍囪
	 */
	@TableField(value = "internal_flag")
	private String internal_flag;
	/**
	 * 鎵€鍦ㄥ浗瀹朵唬鐮?	 */
	@TableField(value = "country_code")
	private String country_code;
	/**
	 * 鎵€鍦ㄧ渷浠戒唬鐮?	 */
	@TableField(value = "province_code")
	private String province_code;
	/**
	 * 鎵€鍦ㄥ煄甯備唬鐮?	 */
	@TableField(value = "city_code")
	private String city_code;
	/**
	 * 鎵€鍦ㄥ尯鍘夸唬鐮?	 */
	@TableField(value = "county_code")
	private String county_code;
	/**
	 * 鎵€鍦ㄨ涓?	 */
	@TableField(value = "industry_code")
	private String industry_code;
	/**
	 * 浼佷笟鎬ц川
	 */
	@TableField(value = "nature_code")
	private String nature_code;
	/**
	 * 浼佷笟娉曚汉
	 */
	@TableField(value = "legal_man")
	private String legal_man;
	/**
	 * 寮€鎴烽摱琛?	 */
	@TableField(value = "bank")
	private String bank;
	/**
	 * 閾惰璐﹀彿
	 */
	@TableField(value = "bank_account")
	private String bank_account;
	/**
	 * 閭斂缂栫爜
	 */
	@TableField(value = "postal_code")
	private String postal_code;
	/**
	 * 鑱旂郴鐢佃瘽
	 */
	@TableField(value = "phone")
	private String phone;
	/**
	 * 浼犵湡
	 */
	@TableField(value = "fax")
	private String fax;
	/**
	 * 閫氳鍦板潃
	 */
	@TableField(value = "address")
	private String address;
	/**
	 * 鐢靛瓙閭欢鍦板潃
	 */
	@TableField(value = "e_mail")
	private String e_mail;
	/**
	 * 缃戠珯鍦板潃
	 */
	@TableField(value = "websit")
	private String websit;
	/**
	 * 缁熶竴绀句細淇＄敤浠ｇ爜
	 */
	@TableField(value = "tax_registration_no")
	private String tax_registration_no;
	/**
	 * 璐у竵绫诲埆
	 */
	@TableField(value = "currency_code")
	private String currency_code;
	/**
	 * 绋庣爜浠ｇ爜
	 */
	@TableField(value = "tax_code")
	private String tax_code;
	/**
	 * 澶囨敞璇存槑
	 */
	@TableField(value = "remark")
	private String remark;
	/**
	 * 鏈夋晥鏍囪
	 */
	@TableField(value = "valid_flag")
	private String valid_flag;
	/**
	 * 鍒涘缓鏂瑰紡
	 */
	@TableField(value = "create_type")
	private String create_type;
	/**
	 * 鍒嗛厤浜篒D
	 */
	@TableField(value = "assign_user_id")
	private Long assign_user_id;
	/**
	 * 鍒嗛厤浜轰唬鐮?	 */
	@TableField(value = "assign_user_code")
	private String assign_user_code;
	/**
	 * 鍒嗛厤浜?	 */
	@TableField(value = "assign_user_man")
	private String assign_user_man;
	/**
	 * 鍒嗛厤鏃堕棿
	 */
	@TableField(value = "assign_time")
	private Date assign_time;
	/**
	 * 鍒嗛厤鏉ユ簮缁勭粐
	 */
	@TableField(value = "assign_from_org")
	private String assign_from_org;
	/**
	 * 閫夋嫨鏍囪
	 */
	@TableField(value = "select_flag")
	private String select_flag;
	/**
	 * 鍥哄畾鐢佃瘽
	 */
	@TableField(value = "telephone")
	private String telephone;
	/**
	 * 涓婄骇閮ㄩ棬
	 */
	@TableField(value = "master_dept")
	private String master_dept;
	/**
	 * 淇¤獕搴?	 */
	@TableField(value = "trust")
	private Integer trust;
	/**
	 * 渚涘簲鍟嗗垎绫?	 */
	@TableField(value = "class_code")
	private String class_code;
	/**
	 * 鍔╄鐮?	 */
	@TableField(value = "mne_code")
	private String mne_code;
	/**
	 * 鍙戣揣鍦板潃
	 */
	@TableField(value = "ship_address")
	private String ship_address;
	/**
	 * 鍐呴儴浼氳涓讳綋
	 */
	@TableField(value = "internal_acct_org")
	private String internal_acct_org;
	/**
	 * 閲囪喘缁勭粐
	 */
	@TableField(value = "pm_org")
	private String pm_org;

	@Override
	public Serializable pkVal() {
		return this.uu_id;
	}

}