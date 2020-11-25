package com.tydic.framework.config.mybatis.pagehelper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageSerializable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author jianghuaishuang
 * @desc 简化分页返回的参数;
 * @date 2019/5/22 17:27
 */
public class SimplePageInfo<T> extends PageSerializable<T> {
	@Getter
	@Setter
	protected long    pageSize;

	@Getter @Setter
	protected long    pageNum;

	public SimplePageInfo(List<T> list) {
		this.list = list;
		if(list instanceof Page){
			Page page = (Page)list ;
			this.total = page.getTotal();
			this.pageNum = page.getPageNum();
			this.pageSize = page.getPageSize();

		} else {
			this.total = list.size();
		}
	}
}