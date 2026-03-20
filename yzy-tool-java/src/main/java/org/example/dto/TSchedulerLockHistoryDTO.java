package org.example.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 分布式锁定时器调度历史表
 * </p>
 *
 * @author yangzhenyu
 * @since 2024-12-19 02:49:13
 */
@Data
public class TSchedulerLockHistoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("顺序号")
    private Integer sequenceNo;

    @ApiModelProperty("ID")
    private String pkTSchedulerLockHistory;

    @ApiModelProperty("ID")
    private String fkTSchedulerLock;

    @ApiModelProperty("锁的名称（唯一标识任务）")
    private String keyName;

    @ApiModelProperty("锁的名称")
    private String keyValue;

    @ApiModelProperty("SUCCESS:成功，ERROR:失败")
    private String resultState;

    @ApiModelProperty("获取锁的节点信息")
    private String resultBy;

    @ApiModelProperty("TRACE_ID")
    private String traceId;

    @ApiModelProperty("错误信息")
    private String errorMessage;

    @ApiModelProperty("创建时间")
    private Date createTime;


}
