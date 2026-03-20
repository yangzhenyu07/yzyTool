package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.service.QuartzJobService;
import org.example.util.vo.TaskBaseInfo;
import org.springframework.stereotype.Service;

/**
 * @author 杨镇宇
 * @date 2025/8/21 15:11
 * @version 1.0
 */
@Slf4j
@Service
public class QuartzJobServiceImpl implements QuartzJobService {
    @Override
    public void job(TaskBaseInfo taskBaseInfo) {
        String jobType = taskBaseInfo.getJobType();
        if ("1".equals(jobType)){
            log.info("111111111111111111111111");
        }else if ("2".equals(jobType)){
            log.info("2222222222222222222222222");
        }

    }
}
