package com.tydic.scaffold.common.controller;

import com.github.pagehelper.Page;
import com.tydic.core.rational.Return;
import com.tydic.core.util.collection.Lists;
import com.tydic.framework.config.mybatis.pagehelper.SimplePageInfo;
import com.tydic.framework.mybatis.dynamicsql.service.OuterSqlConfigService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author jianghuaishuang
 * @desc 动态sql查询组件
 * @date 2019-08-05 9:50
 */
@Controller
@RequestMapping("/common/outerSqlConfig")
public class OuterSqlConfigController {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("outerSqlConfigServiceImpl")
	private OuterSqlConfigService service;

	@ApiOperation(value = "动态sql查询", tags = {"动态sql"})
	@ApiImplicitParams(value = {
			@ApiImplicitParam(paramType = "path", name = "tagCode", value = "数据字典类型", required = true, dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "parentId", value = "可选参数parentId", dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "code", value = "可选参数code", dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "value", value = "可选参数value", dataType = "String"),
			@ApiImplicitParam(paramType = "query", name = "pageNum", value = "可选分页参数-页码", dataType = "int"),
			@ApiImplicitParam(paramType = "query", name = "pageSize", value = "可选分页参数-分页大小", dataType = "int")
	})
	@GetMapping("/qry/{tagCode}")
	@ResponseBody
	public Return<?> qryByDynamicSql(@PathVariable("tagCode") String tagCode, @RequestParam(defaultValue = "-1") int pageNum, @RequestParam(defaultValue = "-1")int pageSize, HttpServletRequest request) {
		try {
			Map param = rebuildParamWithAuthInfo(request);
			List list = service.qryByTagCodeAndParams(tagCode,pageNum,pageSize, param);
			if(list instanceof Page){
				return Return.success(new SimplePageInfo(list));
			}
			return Return.success(list);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return Return.success(Lists.of());
		}
	}





	/**
	 * 构造请求map, 并添加权限信息 进行过滤
	 *
	 * @param req
	 * @return
	 */
	protected Map rebuildParamWithAuthInfo(HttpServletRequest req) {
		Map<String, Object> param = rebuildParam(req);

		//todo  添加权限信息
/*		AhtelOauthUserDetails oAuthUserDetails = (AhtelOauthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		OauthUser oauthUser = oAuthUserDetails.getUser();
		param.put("_loginAcct", oauthUser.getLoginAcct());
		param.put("_loginLatnId", oauthUser.getLatnId());
		param.put("_dataArea", oauthUser.getDataArea());*/
		return param;
	}

	protected <K, V> Map<K, V> rebuildParam(HttpServletRequest req) {
		Map<String, Object> param = new HashMap<>();
		Map<String, String[]> map = req.getParameterMap();
		Iterator<Map.Entry<String, String[]>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String[]> entry = iter.next();
			String key = entry.getKey().toString();
			Object val = entry.getValue();
			if (val instanceof Object[] && ((Object[]) val).length == 1) {
				if (((Object[]) val)[0] != null) {
					param.put(key, ((Object[]) val)[0].toString().trim());
				} else {
					param.put(key, ((Object[]) val)[0]);
				}
			} else if (val instanceof Object[] && ((Object[]) val).length > 1) {
				if (key.endsWith("[]")) {
					param.put(key.substring(0, key.length() - 2), val);
				} else {
					param.put(key, val);
				}
			} else {
				if (val != null) {
					param.put(key, val.toString().trim());
				} else {
					param.put(key, null);
				}
			}
		}

		return (Map<K, V>) param;
	}
}
