package org.example.util.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* @author 杨镇宇
* @date 2025/8/21 15:30
* @version 1.0
*/

@Builder
@Data
public class TaskBaseInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String quartzName;     // 任务唯一标识
    private String taskCron;       // Cron表达式，空则表示一次性任务
    private Date startTime;        // 任务开始时间
    private Date endTime;          // 任务结束时间，如果任务结束时间不设置，表示永久跑
    private String jobType;
    private String jobGroup;       // 任务组
}