package org.example.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Utility class for retrieving the local machine's IP address.
 * Supports both Linux and Windows environments.
 *
 * @author 杨镇宇
 * @date 2024/11/26 10:42
 * @version 1.0
 */
public class IpUtil {

    private static class SingleIpUtil {
        //去所有空格
        public static String removeSpaces(String str){
            String dest = "";  // 定义一个空字符串 dest 用来存储处理后的结果
            if (str != null) {  // 判断输入的字符串是否为空
                dest = str.replaceAll("\\s+", "");  // 使用正则表达式去除所有空格（包括所有空白字符，如空格、制表符等）
                return dest;  // 返回处理后的字符串
            } else {
                return dest;  // 如果输入的字符串为空，则返回空字符串
            }
        }

        private static String getIp() {

            String ip = "";
            try {
                String os = System.getProperty("os.name").toLowerCase();
                Process process;
                boolean linuxFlag = os.contains("nix") || os.contains("nux") || os.contains("mac");

                if (os.contains("win")) {
                    // Windows system: use ipconfig command
                    process = Runtime.getRuntime().exec("ipconfig");
                } else if (linuxFlag) {
                    // Linux or macOS system: use hostname -I command
                    process = Runtime.getRuntime().exec("hostname -I");
                } else {
                    return "Unsupported OS";
                }

                // Read the output with correct encoding (GBK for Windows)
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
                String line;
                boolean found = false;
                while ((line = reader.readLine()) != null) {
                    if (os.contains("win") && line.contains("IPv4")) {
                        // Extract the IPv4 address from the line containing "IPv4"
                        ip = line.split(":")[1].trim();
                        found = true;
                        break;
                    }
                    // On Linux/macOS, get the first non-empty line
                    if (linuxFlag && !line.trim().isEmpty()) {
                        ip = line.trim();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return "IP address not found.";
                }
                return removeSpaces(ip);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error fetching IP";
            }
        }
    }

    private IpUtil() {
        // Prevent instantiation
    }

    public static String getIp() {
        return SingleIpUtil.getIp();
    }
}
