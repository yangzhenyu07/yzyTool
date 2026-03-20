package org.example.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

/**
* @author 杨镇宇
* @date 2025/9/8 14:05
* @version 1.0
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class QuartzReqVo {
    @NotEmpty(message ="任务唯一标识不能为空")
    @ApiModelProperty("任务唯一标识")
    private String quartzName;     // 任务唯一标识
    @ApiModelProperty("Cron表达式，空则表示一次性任务")
    private String taskCron;       // Cron表达式，空则表示一次性任务
    @ApiModelProperty("任务开始时间")
    private Date startTime;        // 任务开始时间
    @ApiModelProperty("任务结束时间，如果任务结束时间不设置，表示永久跑")
    private Date endTime;          // 任务结束时间，如果任务结束时间不设置，表示永久跑
    @NotEmpty(message ="任务类型不能为空")
    @ApiModelProperty("任务类型")
    private String jobType;
    @ApiModelProperty("任务组")
    private String jobGroup;       // 任务组

}
