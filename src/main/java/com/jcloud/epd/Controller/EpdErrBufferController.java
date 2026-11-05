package com.jcloud.epd.Controller;

import cn.hutool.json.JSONUtil;
import com.jcloud.system.entity.UserEntity;
import com.jcloud.system.service.SqlService;
import com.jcloud.system.shiro.ShiroUtils;
import com.jcloud.system.utils.ObjectUtils;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/errbuffer")
public class EpdErrBufferController {
    @Autowired
    SqlService sqlService;


    @RequestMapping(value = "getErrbufferData", method = RequestMethod.GET)
    public R getErrbufferData(@RequestParam Map<String, Object> queryMap) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            String  offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            UserEntity user = ShiroUtils.getUserEntity();
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            try {
                String inv_org="";
                String parent_code="";
                String level="";
                String item_flag="";
                String bom_flag="";
                String rou_flag="";
                String parent_flag="";
                if (queryList != null && !queryList.isEmpty()) {
                    for (Map<String, Object> queryKey : queryList) {
                        if (queryKey != null && !queryKey.isEmpty() && !queryKey.get("field").toString().isEmpty()&& !queryKey.get("value").toString().isEmpty()) {
                            String condStr =" ";
                            if(queryKey.get("field").toString().equals("inv_org") )
                            {
                                inv_org=(String)queryKey.get("value");

                            } else if( queryKey.get("field").toString().equals("parent_code")) {
                                parent_code = (String)queryKey.get("value");
                            } else if( queryKey.get("field").toString().equals("level")) {
                                level = (String)queryKey.get("value");
                            } else if( queryKey.get("field").toString().equals("item_flag")) {
                                item_flag = (String)queryKey.get("value");
                            } else if( queryKey.get("field").toString().equals("bom_flag")) {
                                bom_flag = (String)queryKey.get("value");
                            } else if( queryKey.get("field").toString().equals("rou_flag")) {
                                rou_flag = (String)queryKey.get("value");
                            } else if( queryKey.get("field").toString().equals("parent_flag")) {
                                parent_flag = (String)queryKey.get("value");
                            }
                        }
                    }
                }
                String selAll="";
                if(!level.isEmpty()&&!"".equals(level)){
                    selAll ="select * from epd_err_buffer where inv_org='"+inv_org+"' and parent_code='"+parent_code+"' and level='"+level+"' and create_user_id='"+user.getUserId()+"' order by err_seq_no ";
                }else{
                     selAll ="select * from epd_err_buffer where inv_org='"+inv_org+"' and parent_code='"+parent_code+"' and create_user_id='"+user.getUserId()+"' order by err_seq_no ";

                }
                 List<Map<String, Object>> allStat = sqlService.getAll(selAll);
                int total = allStat != null ? allStat.size() : 0;
                int offsetInt = Integer.parseInt(offset);
                int sizeInt = Integer.parseInt(size);
                if (allStat != null && offsetInt < allStat.size()) {
                    int endIndex = Math.min(offsetInt + sizeInt, allStat.size());
                    allStat = allStat.subList(offsetInt, endIndex);
                } else {
                    allStat = new ArrayList<>();
                }
                Map<String, Object> result = new HashMap<>();
                result.put("rows", allStat);
                result.put("total", total);
                return R.ok().setdata(result);
            } catch (Exception e) {
                e.printStackTrace();
                return R.error(e.getMessage());
            }
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return R.error("URL解码失败");
        }
    }

    @RequestMapping(value = "check", method = RequestMethod.GET)
    public R check(@RequestParam Map<String, Object> queryMap) {
        try {
            String yangQuery = ObjectUtils.oGetS(queryMap.get("yang_query"));
            String  offset=ObjectUtils.oGetS(queryMap.get("offset"));
            String size=ObjectUtils.oGetS(queryMap.get("size"));
            yangQuery = URLDecoder.decode(yangQuery, "UTF-8");
            UserEntity user = ShiroUtils.getUserEntity();
            List<Map<String, Object>> queryList = JSONUtil.parseArray(yangQuery).toBean(List.class);
            try {
                String inv_org="";
                String parent_code="";
                String item_flag="";
                String bom_flag="";
                String rou_flag="";
                String parent_flag="";
                if (queryList != null && !queryList.isEmpty()) {
                    for (Map<String, Object> queryKey : queryList) {
                        if (queryKey != null && !queryKey.isEmpty() && !queryKey.get("field").toString().isEmpty()&& !queryKey.get("value").toString().isEmpty()) {
                            String condStr =" ";
                            if(queryKey.get("field").toString().equals("inv_org") )
                            {
                                inv_org=(String)queryKey.get("value");

                            } else if( queryKey.get("field").toString().equals("parent_code")) {
                                parent_code = (String)queryKey.get("value");
                            }else if( queryKey.get("field").toString().equals("item_flag")) {
                                item_flag = (String)queryKey.get("value");
                            } else if( queryKey.get("field").toString().equals("bom_flag")) {
                                bom_flag = (String)queryKey.get("value");
                            } else if( queryKey.get("field").toString().equals("rou_flag")) {
                                rou_flag = (String)queryKey.get("value");
                            } else if( queryKey.get("field").toString().equals("parent_flag")) {
                                parent_flag = (String)queryKey.get("value");
                            }
                        }
                    }
                }
                sqlService.deleteOne("delete from epd_err_buffer where inv_org='"+inv_org+"' and create_user_id='"+user.getUserId()+"'");
                long err_seq_no=0;
               Date date=new java.util.Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timeStr = sdf.format(date);
                //检查物料定义
                if("N".equals(parent_flag)){
                    if("Y".equals(item_flag)){
                        //检查物料定义重复
                        Map<String, Object> map = sqlService.getOne("SELECT t.item_name, t.item_model, t.item_spec, COUNT(*) AS duplicate_count FROM epd_item_master t WHERE inv_org ='" + inv_org + "' GROUP BY item_name, item_model, item_spec HAVING COUNT(*) > 1");
                        if(!map.isEmpty()){
                            err_seq_no++;
                            String message="物料名称："+map.get("item_name")+"，物料型号："+map.get("item_model")+"，物料规格："+map.get("item_spec")+"，已经重复存在"+map.get("duplicate_count")+"条！";
                            sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                        }
                        //检查批量政策不为直接批量时，批量数量不能为零
                        List<Map<String, Object>> lotList = sqlService.getAll("SELECT item_code, item_name, lot_policy, lot_qty FROM epd_item_master WHERE inv_org ='" + inv_org + "' AND IFNULL(lot_policy, 'DIR') <> 'DIR' AND (lot_qty IS NULL OR lot_qty = 0)");
                        for(Map<String, Object> lotItem : lotList){
                            err_seq_no++;
                            String message="物料编码："+lotItem.get("item_code")+"，物料名称："+lotItem.get("item_name")+"，批量政策："+lotItem.get("lot_policy")+"（非直接批量），但批量数量为零！";
                            sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','"+lotItem.get("item_code")+"','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                        }
                        //检查MRP标记为按订货点时，再订货数量不能为零
                        List<Map<String, Object>> mrpList = sqlService.getAll("SELECT item_code, item_name, mrp_flag, reorder_qty FROM epd_item_master WHERE inv_org ='" + inv_org + "' AND mrp_flag = 'M' AND (reorder_qty IS NULL OR reorder_qty = 0)");
                        for(Map<String, Object> mrpItem : mrpList){
                            err_seq_no++;
                            String message="物料编码："+mrpItem.get("item_code")+"，物料名称："+mrpItem.get("item_name")+"，MRP标记为按订货点，但再订货数量为零！";
                            sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','"+mrpItem.get("item_code")+"','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                        }
                    }
                  //检查工艺路线数据合法性（只检查m_p_flag='M'的制造件）
                  if("Y".equals(rou_flag)){
                      List<Map<String, Object>> routingList = sqlService.getAll("SELECT DISTINCT routing_no FROM epd_routing_head WHERE inv_org = '" + inv_org + "' UNION SELECT DISTINCT routing_no FROM epd_routing_detail WHERE inv_org = '" + inv_org + "' UNION SELECT DISTINCT routing_no FROM epd_item_master WHERE inv_org = '" + inv_org + "' AND routing_no IS NOT NULL AND IFNULL(m_p_flag, 'P') = 'M'");

                      for(Map<String, Object> routing : routingList){
                          String routingNo = (String) routing.get("routing_no");
                          if(routingNo == null || routingNo.trim().isEmpty()){
                              continue;
                          }

                          //检查1：工艺路线头表有记录但明细表无记录
                          Long headCount = sqlService.getCount("SELECT 1 FROM epd_routing_head WHERE inv_org = '" + inv_org + "' AND routing_no = '" + routingNo + "'");
                          Long detailCount = sqlService.getCount("SELECT 1 FROM epd_routing_detail WHERE inv_org = '" + inv_org + "' AND routing_no = '" + routingNo + "'");

                          if(headCount > 0 && detailCount == 0){
                              err_seq_no++;
                              String message = "工艺路线:" + routingNo + "在工艺路线头文件中存在,但工艺路线明细中不存在";
                              sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                              continue;
                          }

                          //检查2：工艺路线头表无记录但明细表有记录
                          if(headCount == 0 && detailCount > 0){
                              err_seq_no++;
                              String message = "工艺路线:" + routingNo + "在工艺路线明细中存在,但工艺路线头文件中不存在";
                              sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                              continue;
                          }

                          //检查3：物料主文件中是否引用了该工艺路线
                          Long itemCount = sqlService.getCount("SELECT 1 FROM epd_item_master WHERE inv_org = '" + inv_org + "' AND routing_no = '" + routingNo + "' AND IFNULL(m_p_flag, 'P') = 'M' ");
                          if(itemCount == 0){
                              err_seq_no++;
                              String message = "工艺路线:" + routingNo + "在物料主文件中没有被引用";
                              sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                          }

                          //检查4：工序计划中的工作中心是否为空或不存在
                          List<Map<String, Object>> wcList = sqlService.getAll("SELECT IFNULL(wc_code, ' ') AS wc_code, op_no FROM epd_routing_detail a WHERE inv_org = '" + inv_org + "' AND routing_no = '" + routingNo + "' AND NOT EXISTS (SELECT wc_code FROM epd_work_center b WHERE b.inv_org = '" + inv_org + "' AND b.wc_code = a.wc_code)");

                          for(Map<String, Object> wc : wcList){
                              String wcCode = (String) wc.get("wc_code");
                              String opNo = String.valueOf(wc.get("op_no"));
                              if(" ".equals(wcCode) || wcCode.trim().isEmpty()){
                                  err_seq_no++;
                                  String message = "工艺路线:" + routingNo + ",工序:" + opNo + "工序计划工作中心为空，请补充数据。";
                                  sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                              } else {
                                  err_seq_no++;
                                  String message = "工艺路线:" + routingNo + ",工序:" + opNo + ",工序计划工作中心:" + wcCode + "不存在，请补充数据。";
                                  sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                              }
                          }

                          //检查5：工序计划中的工种是否为空或不存在
                          List<Map<String, Object>> jobKindList = sqlService.getAll("SELECT IFNULL(job_type, ' ') AS job_type, op_no FROM epd_routing_detail a WHERE inv_org = '" + inv_org + "' AND routing_no = '" + routingNo + "'  AND NOT EXISTS (SELECT job_type FROM epd_job_type_def b WHERE b.inv_org = '" + inv_org + "' AND b.job_type = a.job_type)");

                          for(Map<String, Object> jobKind : jobKindList){
                              String jobType = (String) jobKind.get("job_type");
                              String opNo = String.valueOf(jobKind.get("op_no"));

                              if(" ".equals(jobType) || jobType.trim().isEmpty()){
                                  err_seq_no++;
                                  String message = "工艺路线:" + routingNo + ",工序:" + opNo + "工种为空，请补充数据。";
                                  sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                              } else {
                                  err_seq_no++;
                                  String message = "工艺路线:" + routingNo + ",工序:" + opNo + ",工种:" + jobType + "不存在，请补充数据。";
                                  sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                              }
                          }
                      }
                  }
                  //BOM检查
                  if("Y".equals(bom_flag)){
                      //BOM检查1：子件在物料主文件中不存在
                      List<Map<String, Object>> childList = sqlService.getAll("SELECT DISTINCT component_code FROM epd_bom WHERE inv_org = '" + inv_org + "' AND component_code NOT IN (SELECT item_code FROM epd_item_master WHERE inv_org = '" + inv_org + "')");
                      for(Map<String, Object> child : childList){
                          String componentCode = (String) child.get("component_code");
                          err_seq_no++;
                          String message = "在bom中,子件: " + componentCode + " 在物料主定义中不存在";
                          sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','"+componentCode+"','G','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                      }

                      //BOM检查2：父件在物料主文件中不存在
                      List<Map<String, Object>> parentList = sqlService.getAll("SELECT DISTINCT parent_code FROM epd_bom WHERE inv_org = '" + inv_org + "' AND parent_code NOT IN (SELECT item_code FROM epd_item_master WHERE inv_org = '" + inv_org + "')");
                      for(Map<String, Object> parent : parentList){
                          String parentCode = (String) parent.get("parent_code");
                          err_seq_no++;
                          String message = "在bom中,父件: " + parentCode + " 在物料主定义中不存在";
                          sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','"+parentCode+"','G','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                      }

                      //BOM检查3：叶子节点不是采购件（m_p_flag='M'表示制造件，叶子节点应该是采购件）
                      List<Map<String, Object>> leafList = sqlService.getAll("SELECT DISTINCT component_code FROM epd_bom WHERE inv_org = '" + inv_org + "' AND component_code NOT IN (SELECT DISTINCT parent_code FROM epd_bom WHERE inv_org = '" + inv_org + "') AND component_code IN (SELECT item_code FROM epd_item_master WHERE inv_org = '" + inv_org + "' AND IFNULL(m_p_flag, 'P') = 'M')");
                      for(Map<String, Object> leaf : leafList){
                          String componentCode = (String) leaf.get("component_code");
                          err_seq_no++;
                          String message = "在bom中,叶子节点物料: " + componentCode + " 不是采购件";
                          sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','"+componentCode+"','W','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                      }

                      //BOM检查4：中间父件是采购件（中间节点不应该是采购件）
                      List<Map<String, Object>> middleList = sqlService.getAll("SELECT DISTINCT parent_code FROM epd_bom WHERE inv_org = '" + inv_org + "' AND parent_code IN (SELECT item_code FROM epd_item_master WHERE inv_org = '" + inv_org + "' AND IFNULL(m_p_flag, 'P') = 'P')");
                      for(Map<String, Object> middle : middleList){
                          String parentCode = (String) middle.get("parent_code");
                          err_seq_no++;
                          String message = "在bom中,中间父件: " + parentCode + " 是采购件";
                          sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','"+parentCode+"','G','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','')");
                      }
                  }

                }else{
                    //parent_flag不为N，只检查指定parent_code及其BOM子项相关的数据
                    //使用递归查询获取所有层级的BOM子件
                    List<String> itemCodes = new ArrayList<>();
                    itemCodes.add(parent_code);

                    //递归查询所有层级的子件
                    String recursiveSql = "WITH RECURSIVE BomTree AS (" +
                        "SELECT component_code, parent_code, 1 AS lvl " +
                        "FROM epd_bom WHERE inv_org = '" + inv_org + "' AND parent_code = '" + parent_code + "' " +
                        "UNION ALL " +
                        "SELECT b.component_code, b.parent_code, bt.lvl + 1 " +
                        "FROM epd_bom b INNER JOIN BomTree bt ON b.parent_code = bt.component_code AND b.inv_org = '" + inv_org + "' " +
                        ") SELECT DISTINCT component_code FROM BomTree";

                    List<Map<String, Object>> bomChildren = sqlService.getAll(recursiveSql);
                    for(Map<String, Object> child : bomChildren){
                        String cc = (String) child.get("component_code");
                        if(cc != null && !cc.trim().isEmpty() && !itemCodes.contains(cc)){
                            itemCodes.add(cc);
                        }
                    }

                    //构建IN子句
                    StringBuilder inClause = new StringBuilder();
                    for(int i = 0; i < itemCodes.size(); i++){
                        if(i > 0) inClause.append(",");
                        inClause.append("'").append(itemCodes.get(i)).append("'");
                    }
                    String itemInSql = inClause.toString();

                    //物料定义检查
                    if("Y".equals(item_flag)){
                        //检查物料定义重复
                        Map<String, Object> map = sqlService.getOne("SELECT t.item_name, t.item_model, t.item_spec, COUNT(*) AS duplicate_count FROM epd_item_master t WHERE inv_org ='" + inv_org + "' AND item_code IN (" + itemInSql + ") GROUP BY item_name, item_model, item_spec HAVING COUNT(*) > 1");
                        if(map != null && !map.isEmpty()){
                            err_seq_no++;
                            String message = "物料名称："+map.get("item_name")+"，物料型号："+map.get("item_model")+"，物料规格："+map.get("item_spec")+"，已经重复存在"+map.get("duplicate_count")+"条！";
                            sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                        }
                        //检查批量政策不为直接批量时，批量数量不能为零
                        List<Map<String, Object>> lotList = sqlService.getAll("SELECT item_code, item_name, lot_policy, lot_qty FROM epd_item_master WHERE inv_org ='" + inv_org + "' AND item_code IN (" + itemInSql + ") AND IFNULL(lot_policy, 'DIR') <> 'DIR' AND (lot_qty IS NULL OR lot_qty = 0)");
                        for(Map<String, Object> lotItem : lotList){
                            err_seq_no++;
                            String message="物料编码："+lotItem.get("item_code")+"，物料名称："+lotItem.get("item_name")+"，批量政策："+lotItem.get("lot_policy")+"（非直接批量），但批量数量为零！";
                            sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','"+lotItem.get("item_code")+"','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                        }
                        //检查MRP标记为按订货点时，再订货数量不能为零
                        List<Map<String, Object>> mrpList = sqlService.getAll("SELECT item_code, item_name, mrp_flag, reorder_qty FROM epd_item_master WHERE inv_org ='" + inv_org + "' AND item_code IN (" + itemInSql + ") AND mrp_flag = 'M' AND (reorder_qty IS NULL OR reorder_qty = 0)");
                        for(Map<String, Object> mrpItem : mrpList){
                            err_seq_no++;
                            String message="物料编码："+mrpItem.get("item_code")+"，物料名称："+mrpItem.get("item_name")+"，MRP标记为按订货点，但再订货数量为零！";
                            sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','"+mrpItem.get("item_code")+"','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                        }
                    }

                    //工艺路线检查（只检查m_p_flag='M'的制造件）
                    if("Y".equals(rou_flag)){
                        //获取parent_code和子件中制造件关联的所有工艺路线号
                        List<Map<String, Object>> routingList = sqlService.getAll("SELECT DISTINCT routing_no FROM epd_item_master WHERE inv_org = '" + inv_org + "' AND item_code IN (" + itemInSql + ") AND routing_no IS NOT NULL AND routing_no != '' AND IFNULL(m_p_flag, 'P') = 'M'");

                        for(Map<String, Object> routing : routingList){
                            String routingNo = (String) routing.get("routing_no");
                            if(routingNo == null || routingNo.trim().isEmpty()){
                                continue;
                            }

                            //检查1：工艺路线头表有记录但明细表无记录
                            Long headCount = sqlService.getCount("SELECT 1 FROM epd_routing_head WHERE inv_org = '" + inv_org + "' AND routing_no = '" + routingNo + "'");
                            Long detailCount = sqlService.getCount("SELECT 1 FROM epd_routing_detail WHERE inv_org = '" + inv_org + "' AND routing_no = '" + routingNo + "'");

                            if(headCount > 0 && detailCount == 0){
                                err_seq_no++;
                                String message = "工艺路线:" + routingNo + "在工艺路线头文件中存在,但工艺路线明细中不存在";
                                sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                                continue;
                            }

                            //检查2：工艺路线头表无记录但明细表有记录
                            if(headCount == 0 && detailCount > 0){
                                err_seq_no++;
                                String message = "工艺路线:" + routingNo + "在工艺路线明细中存在,但工艺路线头文件中不存在";
                                sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                                continue;
                            }

                            //检查3：物料主文件中是否引用了该工艺路线
                            Long itemCount = sqlService.getCount("SELECT 1 FROM epd_item_master WHERE inv_org = '" + inv_org + "' AND routing_no = '" + routingNo + "' AND IFNULL(m_p_flag, 'P') = 'M' ");
                            if(itemCount == 0){
                                err_seq_no++;
                                String message = "工艺路线:" + routingNo + "在物料主文件中没有被引用";
                                sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                            }

                            //检查4：工序计划中的工作中心是否为空或不存在
                            List<Map<String, Object>> wcList = sqlService.getAll("SELECT IFNULL(wc_code, ' ') AS wc_code, op_no FROM epd_routing_detail a WHERE inv_org = '" + inv_org + "' AND routing_no = '" + routingNo + "' AND NOT EXISTS (SELECT wc_code FROM epd_work_center b WHERE b.inv_org = '" + inv_org + "' AND b.wc_code = a.wc_code)");

                            for(Map<String, Object> wc : wcList){
                                String wcCode = (String) wc.get("wc_code");
                                String opNo = String.valueOf(wc.get("op_no"));
                                if(" ".equals(wcCode) || wcCode.trim().isEmpty()){
                                    err_seq_no++;
                                    String message = "工艺路线:" + routingNo + ",工序:" + opNo + "工序计划工作中心为空，请补充数据。";
                                    sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                                } else {
                                    err_seq_no++;
                                    String message = "工艺路线:" + routingNo + ",工序:" + opNo + ",工序计划工作中心:" + wcCode + "不存在，请补充数据。";
                                    sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                                }
                            }

                            //检查5：工序计划中的工种是否为空或不存在
                            List<Map<String, Object>> jobKindList = sqlService.getAll("SELECT IFNULL(job_type, ' ') AS job_type, op_no FROM epd_routing_detail a WHERE inv_org = '" + inv_org + "' AND routing_no = '" + routingNo + "' AND NOT EXISTS (SELECT job_type FROM epd_job_type_def b WHERE b.inv_org = '" + inv_org + "' AND b.job_type = a.job_type)");

                            for(Map<String, Object> jobKind : jobKindList){
                                String jobType = (String) jobKind.get("job_type");
                                String opNo = String.valueOf(jobKind.get("op_no"));

                                if(" ".equals(jobType) || jobType.trim().isEmpty()){
                                    err_seq_no++;
                                    String message = "工艺路线:" + routingNo + ",工序:" + opNo + "工种为空，请补充数据。";
                                    sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                                } else {
                                    err_seq_no++;
                                    String message = "工艺路线:" + routingNo + ",工序:" + opNo + ",工种:" + jobType + "不存在，请补充数据。";
                                    sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','','E','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                                }
                            }
                        }
                    }

                    //BOM检查（多级展开）
                    if("Y".equals(bom_flag)){
                        //递归查询所有层级的BOM关系
                        String bomRecursiveSql = "WITH RECURSIVE BomTree AS (" +
                            "SELECT component_code, parent_code, 1 AS lvl " +
                            "FROM epd_bom WHERE inv_org = '" + inv_org + "' AND parent_code = '" + parent_code + "' " +
                            "UNION ALL " +
                            "SELECT b.component_code, b.parent_code, bt.lvl + 1 " +
                            "FROM epd_bom b INNER JOIN BomTree bt ON b.parent_code = bt.component_code AND b.inv_org = '" + inv_org + "' " +
                            ") SELECT * FROM BomTree";

                        List<Map<String, Object>> allBomRecords = sqlService.getAll(bomRecursiveSql);

                        //收集所有层级的子件和父件
                        List<String> allComponentCodes = new ArrayList<>();
                        List<String> allParentCodesInTree = new ArrayList<>();
                        for(Map<String, Object> bom : allBomRecords){
                            String cc = (String) bom.get("component_code");
                            String pc = (String) bom.get("parent_code");
                            if(cc != null && !cc.trim().isEmpty() && !allComponentCodes.contains(cc)){
                                allComponentCodes.add(cc);
                            }
                            if(pc != null && !pc.trim().isEmpty() && !allParentCodesInTree.contains(pc)){
                                allParentCodesInTree.add(pc);
                            }
                        }

                        //BOM检查1：子件在物料主文件中不存在（所有层级的子件）
                        if(!allComponentCodes.isEmpty()){
                            StringBuilder compInClause = new StringBuilder();
                            for(int i = 0; i < allComponentCodes.size(); i++){
                                if(i > 0) compInClause.append(",");
                                compInClause.append("'").append(allComponentCodes.get(i)).append("'");
                            }
                            List<Map<String, Object>> childList = sqlService.getAll("SELECT DISTINCT item_code FROM epd_item_master WHERE inv_org = '" + inv_org + "' AND item_code IN (" + compInClause.toString() + ")");
                            List<String> existItems = new ArrayList<>();
                            for(Map<String, Object> item : childList){
                                existItems.add((String) item.get("item_code"));
                            }
                            for(String compCode : allComponentCodes){
                                if(!existItems.contains(compCode)){
                                    err_seq_no++;
                                    String message = "在bom中,子件: " + compCode + " 在物料主定义中不存在";
                                    sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','"+compCode+"','G','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                                }
                            }
                        }

                        //BOM检查2：父件在物料主文件中不存在（根父件+所有中间父件）
                        List<String> allParentCodesToCheck = new ArrayList<>();
                        if(!allParentCodesInTree.contains(parent_code)){
                            allParentCodesToCheck.add(parent_code);
                        }
                        allParentCodesToCheck.addAll(allParentCodesInTree);

                        if(!allParentCodesToCheck.isEmpty()){
                            StringBuilder parentInClause = new StringBuilder();
                            for(int i = 0; i < allParentCodesToCheck.size(); i++){
                                if(i > 0) parentInClause.append(",");
                                parentInClause.append("'").append(allParentCodesToCheck.get(i)).append("'");
                            }
                            List<Map<String, Object>> parentItemList = sqlService.getAll("SELECT DISTINCT item_code FROM epd_item_master WHERE inv_org = '" + inv_org + "' AND item_code IN (" + parentInClause.toString() + ")");
                            List<String> existParentItems = new ArrayList<>();
                            for(Map<String, Object> item : parentItemList){
                                existParentItems.add((String) item.get("item_code"));
                            }
                            for(String pCode : allParentCodesToCheck){
                                if(!existParentItems.contains(pCode)){
                                    err_seq_no++;
                                    String message = "在bom中,父件: " + pCode + " 在物料主定义中不存在";
                                    sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','"+pCode+"','G','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                                }
                            }
                        }

                        //BOM检查3：叶子节点不是采购件（所有层级的叶子节点）
                        //叶子节点：在allComponentCodes中但不在allParentCodesInTree中的物料
                        List<String> leafNodes = new ArrayList<>();
                        for(String compCode : allComponentCodes){
                            if(!allParentCodesInTree.contains(compCode)){
                                leafNodes.add(compCode);
                            }
                        }
                        if(!leafNodes.isEmpty()){
                            StringBuilder leafInClause = new StringBuilder();
                            for(int i = 0; i < leafNodes.size(); i++){
                                if(i > 0) leafInClause.append(",");
                                leafInClause.append("'").append(leafNodes.get(i)).append("'");
                            }
                            List<Map<String, Object>> leafList = sqlService.getAll("SELECT item_code, m_p_flag FROM epd_item_master WHERE inv_org = '" + inv_org + "' AND item_code IN (" + leafInClause.toString() + ") AND IFNULL(m_p_flag, 'P') = 'M'");
                            for(Map<String, Object> leaf : leafList){
                                String componentCode = (String) leaf.get("item_code");
                                err_seq_no++;
                                String message = "在bom中,叶子节点物料: " + componentCode + " 不是采购件";
                                sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','"+componentCode+"','W','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                            }
                        }

                        //BOM检查4：中间父件是采购件（所有层级的中间父件）
                        //中间父件：既在allComponentCodes中又在allParentCodesInTree中的物料
                        List<String> middleNodes = new ArrayList<>();
                        for(String compCode : allComponentCodes){
                            if(allParentCodesInTree.contains(compCode)){
                                middleNodes.add(compCode);
                            }
                        }
                        if(!middleNodes.isEmpty()){
                            StringBuilder middleInClause = new StringBuilder();
                            for(int i = 0; i < middleNodes.size(); i++){
                                if(i > 0) middleInClause.append(",");
                                middleInClause.append("'").append(middleNodes.get(i)).append("'");
                            }
                            List<Map<String, Object>> middleList = sqlService.getAll("SELECT item_code, m_p_flag FROM epd_item_master WHERE inv_org = '" + inv_org + "' AND item_code IN (" + middleInClause.toString() + ") AND IFNULL(m_p_flag, 'P') = 'P'");
                            for(Map<String, Object> middle : middleList){
                                String componentCode = (String) middle.get("item_code");
                                err_seq_no++;
                                String message = "在bom中,中间父件: " + componentCode + " 是采购件";
                                sqlService.insertOne("insert into epd_err_buffer(group_id,err_seq_no,message,item_code,level,create_user_id,create_time,inv_org,parent_code) values(' ',"+err_seq_no+",'"+message+"','"+componentCode+"','G','"+user.getUserId()+"','"+timeStr+"','"+inv_org+"','"+parent_code+"')");
                            }
                        }
                    }
                }
                return R.ok("检查完成!");
            } catch (Exception e) {
                e.printStackTrace();
                return R.error(e.getMessage());
            }
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return R.error("URL解码失败");
        }
    }
}
