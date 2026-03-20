package org.example.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 参数配置表
 * </p>
 *
 * @author yangzhenyu
 * @since 2024-12-23 02:57:25
 */
@Getter
@Setter
@TableName("t_param_config")
@ApiModel(value = "TParamConfig对象", description = "参数配置表")
public class TParamConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("顺序号")
    @TableId(value = "SEQUENCE_NO", type = IdType.AUTO)
    private Integer sequenceNo;

    @ApiModelProperty("ID")
    @TableField("PK_STD_PRODUCT_CONFIG")
    private String pkStdProductConfig;

    @ApiModelProperty("主code")
    @TableField("MAIN_CODE")
    private String mainCode;

    @ApiModelProperty("子code")
    @TableField("SON_CODE")
    private String sonCode;

    @ApiModelProperty("子code名称")
    @TableField("CODE_NAME")
    private String codeName;

    @ApiModelProperty("备注")
    @TableField("REMARKS")
    private String remarks;

    @ApiModelProperty("变更人编码")
    @TableField("FK_USER_UPDATE")
    private String fkUserUpdate;

    @ApiModelProperty("变更人姓名")
    @TableField("USER_NAME_UPDATE")
    private String userNameUpdate;

    @ApiModelProperty("变更时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

    @ApiModelProperty("创建时间")
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("创建人编号")
    @TableField("FK_USER_CREATE")
    private String fkUserCreate;

    @ApiModelProperty("创建人姓名")
    @TableField("USER_NAME_CREATE")
    private String userNameCreate;

    @ApiModelProperty("删除标志, 未删除:0, 已删除:1")
    @TableField("DELETE_FLAG")
    private String deleteFlag;

    @ApiModelProperty("删除时间")
    @TableField("DELETE_TIME")
    private Date deleteTime;

    @ApiModelProperty("删除人编号")
    @TableField("FK_USER_DELETE")
    private String fkUserDelete;

    @ApiModelProperty("删除人姓名")
    @TableField("USER_NAME_DELETE")
    private String userNameDelete;


}
