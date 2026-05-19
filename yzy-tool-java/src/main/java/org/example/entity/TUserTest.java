package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 测试表
 * </p>
 *
 * @author yangzhenyu
 * @since 2026-05-14 11:52:16
 */
@Getter
@Setter
@TableName("t_user_test")
@ApiModel(value = "TUserTest对象", description = "测试表")
public class TUserTest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("顺序号")
    @TableId(value = "PK_ID", type = IdType.AUTO)
    private Long pkId;

    @ApiModelProperty("CODE")
    @TableField("CODE")
    private String code;

    @ApiModelProperty("姓名")
    @TableField("NAME")
    private String name;


}
