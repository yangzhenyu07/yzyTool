package org.example.config.web3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;

/**
 * 交易工具类
 * @author 杨镇宇
 * @date 2026/3/20 02:31
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionUtils {

    private final Web3jClientFactory clientFactory;

    /**
     * ETH 转账（标准流程）
     *
     * @param credentials 钱包凭证（钱包某账户的私钥）
     * @param toAddress   收款地址
     * @param amount      转账金额（单位：ETH）
     * @return 交易哈希
     */
    public String transfer(Credentials credentials, String toAddress, BigDecimal amount) {
        try {
            Web3j web3j = clientFactory.getClient();

            log.info("开始转账: from={}, to={}, amount={} ETH",
                    credentials.getAddress(), toAddress, amount);

            TransactionReceipt receipt = Transfer.sendFunds(
                    web3j,
                    credentials,
                    toAddress,
                    amount,
                    Convert.Unit.ETHER
            ).send();

            log.info("转账成功: txHash={}", receipt.getTransactionHash());

            return receipt.getTransactionHash();

        } catch (Exception e) {
            log.error("转账失败: to={}, amount={}", toAddress, amount, e);
            throw new RuntimeException("转账失败", e);
        }
    }
}