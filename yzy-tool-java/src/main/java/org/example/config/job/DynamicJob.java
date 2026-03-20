package org.example.config.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.service.QuartzJobService;
import org.example.util.vo.TaskBaseInfo;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by lxs on 2021/7/14 14:29
 * :@DisallowConcurrentExecution : 此标记用在实现Job的类上面,意思是不允许并发执行.
 * :注意org.quartz.threadPool.threadCount线程池中线程的数量至少要多个,否则@DisallowConcurrentExecution不生效
 * :假如Job的设置时间间隔为3秒,但Job执行时间是5秒,设置@DisallowConcurrentExecution以后程序会等任务执行完毕以后再去执行,否则会在3秒时再启用新的线程执行
 * @author lai
 */
@DisallowConcurrentExecution
@Component
@Slf4j
public class DynamicJob implements Job {


    @Autowired
    private QuartzJobService quartzJobService;

    /**
     * 核心方法,Quartz Job真正的执行逻辑.
     *
     * @param executorContext executorContext JobExecutionContext中封装有Quartz运行所需要的所有信息
     * @throws JobExecutionException execute()方法只允许抛出JobExecutionException异常
     */
    @Override
    public void execute(JobExecutionContext executorContext) {
        //JobDetail中的JobDataMap是共用的,从getMergedJobDataMap获取的JobDataMap是全新的对象
        Object object = executorContext.getMergedJobDataMap().get("job");
        log.info("quartz 执行任务{}", executorContext.getJobDetail().getKey());
        if (!(object instanceof TaskBaseInfo)) {
            log.error("任务信息获取失败，类型不匹配");
            return;
        }
        TaskBaseInfo taskInfo = (TaskBaseInfo) object;
        //任务执行
        quartzJobService.job(taskInfo);
        log.info("quartz 参数:{}",object);

    }


}
