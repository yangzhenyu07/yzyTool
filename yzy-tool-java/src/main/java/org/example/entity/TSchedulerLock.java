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
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 分布式锁定时器调度表
 * </p>
 *
 * @author yangzhenyu
 * @since 2024-12-18 04:57:06
 */
@Getter
@Setter
@Builder
@TableName("t_scheduler_lock")
@ApiModel(value = "TSchedulerLock对象", description = "分布式锁定时器调度表")
public class TSchedulerLock implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("顺序号")
    @TableId(value = "SEQUENCE_NO", type = IdType.AUTO)
    private Integer sequenceNo;

    @ApiModelProperty("ID")
    @TableField("PK_T_SCHEDULER_LOCK")
    private String pkTSchedulerLock;

    @ApiModelProperty("锁的名称（唯一标识任务）")
    @TableField("KEY_NAME")
    private String keyName;

    @ApiModelProperty("锁的名称")
    @TableField("KEY_VALUE")
    private String keyValue;

    @ApiModelProperty("锁的有效期（过期时间）")
    @TableField("LOCK_UNTIL")
    private Date lockUntil;

    @ApiModelProperty("获取锁的节点信息")
    @TableField("LOCKED_BY")
    private String lockedBy;

    @ApiModelProperty("TRACE_ID")
    @TableField("TRACE_ID")
    private String traceId;

    @ApiModelProperty("锁状态，0=未锁，1=已锁")
    @TableField("LOCK_STATE")
    private Integer lockState;

    @ApiModelProperty("版本号，支持乐观锁")
    @TableField("LOCK_VERSION")
    private Integer lockVersion;

    @ApiModelProperty("释放变更时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

    @ApiModelProperty("创建时间")
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;


}
