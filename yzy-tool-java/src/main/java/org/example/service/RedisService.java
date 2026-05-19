package org.example.service;

import org.example.vo.ScanVo;

/**
* @author 杨镇宇
* @date 2025/2/17 11:07
* @version 1.0
*/

public interface RedisService {

    boolean testLock(ScanVo scanVo,String path) throws InterruptedException;
}
