package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.config.web3.Web3jClientFactory;
import org.example.service.AccountService;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * web3 账户类
 * @author 杨镇宇
 * @date 2026/3/20 01:41
 * @version 1.0
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    @Resource
    private  Web3jClientFactory web3jClientFactory;



    // ========== 基础功能 ==========
    /**
     * 生成新账户 在本地生成一个新的以太坊钱包（私钥 + 地址）
     */
    public Credentials generateAccount() {
        try {
            return Credentials.create(Keys.createEcKeyPair());
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("生成账户失败", e);
        }
    }

    /**
     * 从私钥加载账户
     */
    public Credentials loadAccount(String privateKey) {
        return Credentials.create(privateKey);
    }

    /**
     * 查询余额（策略模式：不同链的单位转换可扩展）
     */
    public BigDecimal getBalance(String address) {
        try {
            Web3j web3j = web3jClientFactory.getClient();
            EthGetBalance balanceResp = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            if (balanceResp == null || balanceResp.getBalance() == null) {
                throw new RuntimeException("获取余额失败");
            }
            BigInteger balanceWei = balanceResp.getBalance();
            // 策略模式扩展点：可根据nodeKey切换单位转换策略（如ETH/BNB/TRX）
            return Convert.fromWei(balanceWei.toString(), Convert.Unit.ETHER);
        } catch (Exception e) {
            throw new RuntimeException("查询余额失败", e);
        }
    }

}
