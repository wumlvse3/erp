package com.jcloud.customer.WmsBaseData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.customer.WmsBaseData.entity.MtlProjectHead;
import com.jcloud.customer.WmsBaseData.vo.MtlProjectLineView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MtlProjectHeadMapper extends BaseMapper<MtlProjectHead> {
    @Select("select mph1.org_id,mph1.project_id as id,mph1.project_code as items_code,mph1.project_name as items_name,0 as parent_id,0 as parent_code  from mtl_project_head mph1\n" +
            "union all\n" +
            "select mph.org_id,mpl.line_id as id,mpl.items_code,mpl.items_name,IFNULL(mpl.parent_line_id,mpl.project_id) as parent_id,IFNULL(mpl.parent_code,mph.items_code) as parent_code\n" +
            "  from mtl_project_head mph\n" +
            "\tleft join mtl_project_line mpl on mph.project_id = mpl.project_id")
    List<MtlProjectLineView> getProjectTreeData();
}