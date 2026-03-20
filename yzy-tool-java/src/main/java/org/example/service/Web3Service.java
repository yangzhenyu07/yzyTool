package org.example.service;

import org.example.vo.Web3Vo;

import java.math.BigDecimal;

/**
* @author 杨镇宇
* @date 2026/3/20 23:33
* @version 1.0
*/

public interface Web3Service {

    BigDecimal query(Web3Vo scanVo);

    boolean transfer(Web3Vo scanVo);
}
