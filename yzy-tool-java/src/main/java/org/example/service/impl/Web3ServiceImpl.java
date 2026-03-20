package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.config.web3.TransactionUtils;
import org.example.service.AccountService;
import org.example.service.Web3Service;
import org.example.vo.Web3Vo;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
* @author 杨镇宇
* @date 2026/3/20 23:33
* @version 1.0
*/
@Slf4j
@Service
public class Web3ServiceImpl implements Web3Service {

    @Resource
    private AccountService accountService;
    @Resource
    private TransactionUtils transactionUtils;
    @Override
    public BigDecimal query(Web3Vo scanVo) {
        String address = scanVo.getAddress();
        BigDecimal balance = accountService.getBalance(address);

        return balance;
    }

    @Override
    public boolean transfer(Web3Vo scanVo) {
        String address = scanVo.getAddress();
        String privateKey = scanVo.getPrivateKey();

        try {
            Credentials credentials = accountService.loadAccount(privateKey);

            transactionUtils.transfer(credentials,address,scanVo.getAmount());
        }catch (Exception e){
            log.error("转账失败",e);
            return false;
        }

        return true;
    }
}
