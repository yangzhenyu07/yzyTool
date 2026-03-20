package org.example.service;

import org.example.util.vo.TaskBaseInfo;

/**
 * @author 杨镇宇
 * @date 2025/8/21 15:11
 * @version 1.0
 */

public interface QuartzJobService {

    void job(TaskBaseInfo taskBaseInfo);
}
