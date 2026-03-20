package org.example.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

/**
* @description: TODO
* @author 杨镇宇
* @date 2024/9/24 9:52
* @version 1.0
*/

public class jasyptUtil {

    /**
     * jasypt只要配置项中使用了 ENC(...) 格式，Jasypt 都能够自动进行解密。
     * 因此，除了数据库密码，你还可以加密诸如 API 密钥、JWT 秘钥等任何敏感数据。
     */
    private static final String PASSWORD = "yzy_@user!1Ux";
    private static final String KEY = "PBEWITHMD5ANDDES";
    /**
     * 加密
     *
     * @param plaintext 明文
     * @return 密文
     */
    public static String encrypt(String plaintext) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        // 指定算法
        config.setAlgorithm(KEY);
        // 指定秘钥，和jvm的jasypt.encryptor.password保持一致
        config.setPassword(PASSWORD);
        encryptor.setConfig(config);
        // 生成加密数据
        return encryptor.encrypt(plaintext);
    }

    /**
     * 解密
     *
     * @param data 加密后数据
     * @return 明文
     */
    public static String decrypt(String data) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm(KEY);
        // 指定秘钥，和jvm的jasypt.encryptor.password保持一致
        config.setPassword(PASSWORD);
        encryptor.setConfig(config);
        // 解密数据
        return encryptor.decrypt(data);
    }

    public static void main(String[] args) {
        System.out.println(encrypt("yzy@1234"));
        System.out.println(decrypt("CgwyMkKNHNItXT39rGiO/PoiO/i93iCr"));
    }


}
