package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.lock.CommonLock;
import org.example.service.RedisService;
import org.example.vo.ScanVo;
import org.springframework.stereotype.Service;

/**
* @author 杨镇宇
* @date 2025/2/17 11:07
* @version 1.0
*/
@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    @CommonLock(
            key = "'order_lock:' + #path",
            expire = 30,
            retryTimes = 0,
            retryInterval = 500,
            message = "测试-触发并发操作"
    )
    @Override
    public boolean testLock(ScanVo scanVo,String path) throws InterruptedException {
        Thread.sleep(2000);
        log.info("");
        return Boolean.TRUE;
    }
}
