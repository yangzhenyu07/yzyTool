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
 * 分布式锁定时器调度历史表
 * </p>
 *
 * @author yangzhenyu
 * @since 2024-12-19 02:49:13
 */
@Getter
@Setter
@Builder
@TableName("t_scheduler_lock_history")
@ApiModel(value = "TSchedulerLockHistory对象", description = "分布式锁定时器调度历史表")
public class TSchedulerLockHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("顺序号")
    @TableId(value = "SEQUENCE_NO", type = IdType.AUTO)
    private Integer sequenceNo;

    @ApiModelProperty("ID")
    @TableField("PK_T_SCHEDULER_LOCK_HISTORY")
    private String pkTSchedulerLockHistory;

    @ApiModelProperty("ID")
    @TableField("FK_T_SCHEDULER_LOCK")
    private String fkTSchedulerLock;

    @ApiModelProperty("锁的名称（唯一标识任务）")
    @TableField("KEY_NAME")
    private String keyName;

    @ApiModelProperty("锁的名称")
    @TableField("KEY_VALUE")
    private String keyValue;

    @ApiModelProperty("SUCCESS:成功，ERROR:失败")
    @TableField("RESULT_STATE")
    private String resultState;

    @ApiModelProperty("获取锁的节点信息")
    @TableField("RESULT_BY")
    private String resultBy;

    @ApiModelProperty("TRACE_ID")
    @TableField("TRACE_ID")
    private String traceId;

    @ApiModelProperty("错误信息")
    @TableField("ERROR_MESSAGE")
    private String errorMessage;

    @ApiModelProperty("创建时间")
    @TableField(value = "CREATE_TIME", fill = FieldFill.INSERT)
    private Date createTime;


}
