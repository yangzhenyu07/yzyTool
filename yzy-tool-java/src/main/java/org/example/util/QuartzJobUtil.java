package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.example.config.job.DynamicJob;
import org.example.util.vo.TaskBaseInfo;
import org.quartz.*;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * @author 杨镇宇
 * @date 2025/8/21 15:13
 * @version 1.0
 */

@Slf4j
public class QuartzJobUtil {
    private static final String DEFAULT_GROUP = Scheduler.DEFAULT_GROUP;
    /**
     * 创建定时任务
     */
    public static void createJob(Scheduler scheduler, TaskBaseInfo quartzBean)  {
        try {
            String group = StringUtils.hasText(quartzBean.getJobGroup()) ? quartzBean.getJobGroup() : DEFAULT_GROUP;

            JobDetail jobDetail = JobBuilder.newJob(DynamicJob.class)
                    .withIdentity(quartzBean.getQuartzName(), group)
                    .build();
            jobDetail.getJobDataMap().put("job", quartzBean);

            Trigger trigger;
            if (StringUtils.isEmpty(quartzBean.getTaskCron())) {
                // 单次任务
                trigger = getSimpleTrigger(quartzBean, group);
            } else {
                // cron 任务
                trigger = getCronTrigger(quartzBean, group);
            }

            scheduler.scheduleJob(jobDetail, trigger);
            log.info("创建任务 [{}] 成功，组名 [{}]", quartzBean.getQuartzName(), group);
        } catch (Exception e){
            log.error("创建任务 [{}] 失败", quartzBean.getQuartzName(), e);
        }
    }

    /**
     * 更新任务
     */
    public static void updateJob(Scheduler scheduler, TaskBaseInfo quartzBean) {
        try {
            String group = StringUtils.hasText(quartzBean.getJobGroup()) ? quartzBean.getJobGroup() : DEFAULT_GROUP;
            TriggerKey triggerKey = TriggerKey.triggerKey(quartzBean.getQuartzName(), group);            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                    .cronSchedule(quartzBean.getTaskCron())
                    .withMisfireHandlingInstructionDoNothing();

            CronTrigger newTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(scheduleBuilder)
                    .build();

            scheduler.rescheduleJob(triggerKey, newTrigger);
            log.info("更新任务 [{}] 成功，新cron={}，组名 [{}]", quartzBean.getQuartzName(), quartzBean.getTaskCron(), group);
        } catch (SchedulerException e) {
            log.error("更新任务 [{}] 失败，新cron={}，组名 [{}]", quartzBean.getQuartzName(), quartzBean.getTaskCron(), quartzBean.getJobGroup(), e);        }
    }

    /**
     * 立即执行一次
     */
    public static void runJobNow(Scheduler scheduler, TaskBaseInfo quartzBean)  {
        String group = StringUtils.hasText(quartzBean.getJobGroup()) ? quartzBean.getJobGroup() : DEFAULT_GROUP;
        JobKey jobKey = JobKey.jobKey(quartzBean.getQuartzName(), group);
        try {
            scheduler.triggerJob(jobKey, buildJobDataMap(quartzBean));
            log.info("立即执行任务 [{}]", quartzBean.getQuartzName());
        }catch (Exception e){
            log.error("立即执行任务失败",e);
        }
    }

    /**
     * 根据任务名称暂停定时任务
     *
     * @param scheduler 调度器
     */
    public static void pauseScheduleJob(Scheduler scheduler, TaskBaseInfo quartzBean) {
        String group = StringUtils.hasText(quartzBean.getJobGroup()) ? quartzBean.getJobGroup() : DEFAULT_GROUP;
        JobKey jobKey = JobKey.jobKey(quartzBean.getQuartzName(), group);
        try {
            scheduler.pauseJob(jobKey);
            log.info("根据任务名称【{}】暂停定时任务", quartzBean.getQuartzName());

        } catch (SchedulerException e) {
            log.error("根据任务名称暂停定时任务失败",e);
        }


    }

    /**
     * 根据任务名称恢复定时任务
     *
     * @param scheduler 调度器
     */
    public static void resumeScheduleJob(Scheduler scheduler, TaskBaseInfo quartzBean) {
        String group = StringUtils.hasText(quartzBean.getJobGroup()) ? quartzBean.getJobGroup() : DEFAULT_GROUP;
        JobKey jobKey = JobKey.jobKey(quartzBean.getQuartzName(), group);
        try {
            scheduler.resumeJob(jobKey);
            log.info("启动定时任务:【{}】", quartzBean.getQuartzName());

        } catch (SchedulerException e) {
            log.error("启动定时任务出错:" , e);
        }
    }


    /**
     * 删除任务
     */
    public static void deleteJob(Scheduler scheduler, TaskBaseInfo quartzBean)  {
        String group = StringUtils.hasText(quartzBean.getJobGroup()) ? quartzBean.getJobGroup() : DEFAULT_GROUP;
        JobKey jobKey = JobKey.jobKey(quartzBean.getQuartzName(), group);;
        try {
            scheduler.deleteJob(jobKey);
            log.info("删除任务 [{}] 成功", quartzBean.getQuartzName());
        }catch (Exception e){
            log.error("删除任务出错:" , e);

        }

    }

    // ==================== private helper ====================
    private static SimpleTrigger getSimpleTrigger(TaskBaseInfo bean, String group) {
        return TriggerBuilder.newTrigger()
                .withIdentity(bean.getQuartzName(), group)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withRepeatCount(0)) // 执行一次
                .startNow()
                .build();
    }

    /**
     * 生成一个基于 cron 表达式 的 Quartz 触发器（CronTrigger）。
     * 支持设置任务的 开始时间、结束时间。
     * 设置了 错过执行时不补跑 的策略
     * @param bean
     * @startTime 任务开始时间
     * @endTime 任务结束时间，如果不设置，表示永久执行
     * @return
     */
    private static CronTrigger getCronTrigger(TaskBaseInfo bean, String group) {
        // SimpleDateFormat 非线程安全，不要定义成全局静态
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info("当前时间:{}, cron:{},startTime:{},endTime:{}",
                sdf.format(new Date()), bean.getTaskCron(),sdf.format(bean.getStartTime()),sdf.format(bean.getEndTime()));
        Date startTime = bean.getStartTime();
        Date endTime = bean.getEndTime();
        // 处理 错过触发时间 的策略。这里选择 不补执行，而是等下一个符合 cron 表达式的时间点再跑。
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .cronSchedule(bean.getTaskCron())
                .withMisfireHandlingInstructionDoNothing();

        TriggerBuilder<CronTrigger> builder = TriggerBuilder.newTrigger()
                .withIdentity(bean.getQuartzName(), group)
                .withSchedule(scheduleBuilder);

        if (startTime != null) builder.startAt(startTime);
        if (endTime != null) builder.endAt(endTime);

        /**
         * 示例
         * TaskBaseInfo task = new TaskBaseInfo();
         * task.setQuartzName("job1");
         * task.setTaskCron("0 0/5 * * * ?"); // 每5分钟执行一次
         * task.setStartTime(new Date());      // 从现在开始
         * task.setEndTime(null);              // 没有结束时间
         *
         * CronTrigger trigger = getCronTrigger(task);
         * 这个 trigger 的含义就是：
         *
         * 从现在开始，每 5 分钟执行一次 job1，
         * 永远执行下去（因为没设置 endTime），
         *
         * 如果某次错过触发（比如服务器宕机），就直接等下一次执行，不补跑。
         * 注意的是 当传startTime时 cron 表达式并不要求“从 startTime 立刻跑一次”，
         * 而是 “找到第一个 ≥ startTime 且符合 cron 的时间点
         */
        return builder.build();
    }

    private static JobDataMap buildJobDataMap(TaskBaseInfo bean) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("job", bean);
        return jobDataMap;
    }
}
