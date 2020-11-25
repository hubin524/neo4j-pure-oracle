package com.tydic.scaffold.temp.controller;

import com.tydic.core.rational.Return;
import com.tydic.framework.config.mybatis.pagehelper.SimplePageInfo;
import com.tydic.framework.config.validation.group.ValidateGroups;
import com.tydic.framework.spring.mvc.desensitization.constraints.DesensitizeSupport;
import com.tydic.scaffold.temp.model.Demo;
import com.tydic.scaffold.temp.service.DemoService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019/5/9 9:32
 */
@Profile({"dev","test"})
@RestController
@RequestMapping("/temp/demo")
@DesensitizeSupport
public class DemoController {
	Logger logger = LoggerFactory.getLogger(this.getClass());


	private DemoService demoService;

	@Autowired
	public void setDemoService(DemoService demoService) {
		this.demoService = demoService;
	}

	@ApiOperation(value = "分页查询", tags = {"样例demo"})
	@GetMapping
	public Return<SimplePageInfo<Demo>> qryDemo(Demo param, @ApiParam(required = true) @RequestParam(defaultValue = "1") int pageNum, @ApiParam(required = true) @RequestParam(defaultValue = "5") int pageSize) {
		SimplePageInfo<Demo> pageInfo = new SimplePageInfo(demoService.qryDemo(param, pageNum, pageSize));
		return Return.success(pageInfo);
	}

	@ApiOperation(value = "根据id获取", tags = {"样例demo"})
	@GetMapping("/{demoId}")
	public Return<Demo> getDemo(@PathVariable("demoId") Long demoId) {
		return Return.success(demoService.getDemo(demoId));
	}

	@ApiOperation(value = "新增", tags = {"样例demo"})
	@ApiImplicitParams(value = {
			@ApiImplicitParam(paramType = "query", name = "demoName", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "age", required = true, dataType = "int"),
			@ApiImplicitParam(paramType = "query", name = "birthdate", required = true, dataType = "Date")
	})
	@PostMapping
//	@PreAuthorize("hasPermission('permission','DEMO_BTN_ADD')")
	public Return save(@Validated({ValidateGroups.Add.class}) @ApiIgnore Demo demo) {
		try {
			demoService.save(demo);
			return Return.success("保存成功");
		} catch (Exception e) {
			logger.error(e.getMessage());
			return Return.failure("保存失败");
		}

	}

	@ApiOperation(value = "更新", tags = {"样例demo"})
	@ApiImplicitParams(value = {
			@ApiImplicitParam(paramType = "query", name = "demoId", required = true, dataType = "Long"),
			@ApiImplicitParam(paramType = "query", name = "age", required = true, dataType = "int"),
			@ApiImplicitParam(paramType = "query", name = "birthdate", dataType = "Date")
	})
	@PutMapping
	public Return update(@ApiIgnore @Validated({ValidateGroups.Edit.class}) Demo demo) {
		try {
			demoService.update(demo);
			return Return.success("更新成功");
		} catch (Exception e) {
			logger.error(e.getMessage());
			return Return.failure("更新失败");
		}

	}

	@ApiOperation(value = "删除", tags = {"样例demo"})
	@PutMapping("/state/{demoId}")
	public Return delete(@PathVariable("demoId") Long demoId) {
		demoService.deleteDemo(demoId);
		return Return.success("删除成功");
	}
}
