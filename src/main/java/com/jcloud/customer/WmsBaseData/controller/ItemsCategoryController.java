package com.jcloud.customer.WmsBaseData.controller;

import com.jcloud.customer.WmsBaseData.entity.ItemsCategory;
import com.jcloud.customer.WmsBaseData.service.Impl.ItemsCategoryServiceImpl;
import com.jcloud.system.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/itemsCategory")
public class ItemsCategoryController {
    @Autowired
    ItemsCategoryServiceImpl itemsCategoryService;

    @RequestMapping(value = "getFistLevelData",method = RequestMethod.GET)
    public R getFistLevelData(){
        return R.ok().setdata(itemsCategoryService.getFistLevelData());
    }

    @RequestMapping(value = "getAllLevelData",method = RequestMethod.GET)
    public R getAllLevelData(){
        return R.ok().setdata(itemsCategoryService.getAllLevelData());
    }

    @RequestMapping(value = "/addObj",method = RequestMethod.POST)
    public R addObj(@RequestBody ItemsCategory items_category){
        return itemsCategoryService.addObj(items_category);
    }

    @RequestMapping(value = "/updateObj",method = RequestMethod.POST)
    public R updateObj(@RequestBody ItemsCategory items_category){
        return itemsCategoryService.updateObj(items_category);
    }

    //通过父级id查询
    @RequestMapping(value = "/getChildList",method = RequestMethod.POST)
    public R getChildList(@RequestBody Map map){
        String parent_id= (String) map.get("id");
        String category_code= (String) map.get("code");
        String category_name=(String) map.get("label");
        return itemsCategoryService.getChildList(parent_id,category_code,category_name);
    }

    //根据id查询详情
    @RequestMapping(value = "/findOne/{id}",method = RequestMethod.GET)
    public R findOne(@PathVariable String id){
        return itemsCategoryService.findOne(id);
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public R deleteObj(@PathVariable("id") String id){
        return itemsCategoryService.deleteById(id);
    }
}
