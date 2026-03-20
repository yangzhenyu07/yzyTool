package org.example.service;

import org.web3j.crypto.Credentials;

import java.math.BigDecimal;

/**
* @author 杨镇宇
* @date 2026/3/20 01:40
* @version 1.0
*/

public interface AccountService {

    Credentials loadAccount(String privateKey);

    BigDecimal getBalance(String address);
}
