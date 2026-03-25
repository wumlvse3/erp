package com.jcloud.system.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcloud.system.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jcloud.system.entity.SysFile;
import com.jcloud.system.service.SysFileService;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (SysFile)表控制层
 *
 * @author Bianxs
 * @since 2024-02-19 10:36:36
 */
@RestController
@RequestMapping("sysFile")
@Api(tags = {""})
public class SysFileController {
    /**
     * 服务对象
     */
    @Resource
    private SysFileService sysFileService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param sysFile 查询实体
     * @return 所有数据
     */
    @GetMapping
    @ApiOperation("分页查询所有数据")
    public R selectAll(Page<SysFile> page, SysFile sysFile) {
        return R.ok().setdata(this.sysFileService.page(page, new QueryWrapper<>(sysFile)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    @ApiOperation("通过主键查询单条数据")
    public R selectOne(@PathVariable Serializable id) {
        return R.ok().setdata(this.sysFileService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysFile 实体对象
     * @return 新增结果
     */
    @PostMapping
    @ApiOperation("新增数据")
    public R insert(@RequestBody SysFile sysFile) {
        return R.ok().setdata(this.sysFileService.save(sysFile));
    }

    /**
     * 修改数据
     *
     * @param sysFile 实体对象
     * @return 修改结果
     */
    @PutMapping
    @ApiOperation("修改数据")
    public R update(@RequestBody SysFile sysFile) {
        return R.ok().setdata(this.sysFileService.updateById(sysFile));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    @ApiOperation("删除数据")
    public R delete(@RequestParam("idList") List<Long> idList) {
        return R.ok().setdata(this.sysFileService.removeByIds(idList));
    }

    /**
    * 生成指定目录服务器文件至记录表
    *
     * @param path
    * @return: No such property: code for class: Script1
    * @author: CPL
    * @date: 2024/2/19 10:36
    */
    @ApiOperation("生成文件记录")
    @RequestMapping(value = "/general/{path}",method = RequestMethod.GET)
    public R generalFileRecord(@PathVariable("path") String path) {

        return this.sysFileService.generalFileRecord(path);
    }
}

