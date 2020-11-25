package com.tydic.scaffold.temp.model;

import com.tydic.framework.config.validation.group.ValidateGroups;
import com.tydic.framework.spring.mvc.desensitization.constraints.Desensitize;
import com.tydic.framework.spring.mvc.desensitization.constraints.DesensitizeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author jianghuaishuang
 * @desc
 * @date 2019/5/9 9:36
 */
@Data
@Table(name = "TB_TEMP_DEMO")
@ApiModel("Demo:demo信息实体")
public class Demo {

	@Id
	@KeySql(sql = "select SEQ_TEMP_DEMO.nextval from dual")
	@ApiModelProperty(value = "主键id")
	@NotNull(message = "id不能为空", groups = {ValidateGroups.Edit.class})
	private Long demoId;

	@Desensitize(type = DesensitizeType.NAME)
	@ApiModelProperty(value = "姓名")
	@NotNull(message = "name不能为空", groups = {ValidateGroups.Add.class})
	private String demoName;

	@ApiModelProperty(value = "年龄")
	@NotNull(message = "年龄不能为空", groups = {ValidateGroups.Add.class, ValidateGroups.Edit.class})
	private Integer age;

	@ApiModelProperty(value = "生日")
	@NotNull(message = "生日不能为空", groups = {ValidateGroups.Add.class})
	private Date birthdate;
}
