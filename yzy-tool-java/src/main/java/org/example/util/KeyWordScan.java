package org.example.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.exception.ExceptionEnum;
import org.example.exception.throwtype.RunException;
import org.example.vo.KeyWordVo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 杨镇宇
 * @date 2024/6/13 15:31
 * @version 1.0
 */
@Slf4j
public class KeyWordScan {

    public static List<KeyWordVo> analyseFile(File sourceFile, Set<String> includeKeywords, Set<String> excludeKeyWords, String regulation) {
        List<KeyWordVo> keyWordVoList = new ArrayList<>();
        FileInputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        Pattern pattern = null;
        if (StringUtils.isNotEmpty(regulation)) {
            pattern = Pattern.compile(regulation);
        }
        try {
            inputStream = new FileInputStream(sourceFile);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            int lineNo = 0;
            String filePath = sourceFile.getAbsolutePath();
            while ((line = bufferedReader.readLine()) != null) {
                lineNo++;
                if (lineRegulator(line, includeKeywords, excludeKeyWords, pattern)) {
                    KeyWordVo keyWordVo = new KeyWordVo();
                    keyWordVo.setFilePath(filePath);
                    keyWordVo.setLineNo(lineNo);
                    keyWordVo.setLine(line);
                    keyWordVoList.add(keyWordVo);
                }
            }
            return keyWordVoList;
        } catch (Exception e) {
            log.error( "行解析文件:{} 报错:{}", sourceFile.getName(), e.getMessage(),e);
            String format = MessageFormat.format("行解析文件:{} 报错:{}", sourceFile.getName(), e.getMessage());
            throw  new RunException(ExceptionEnum.ERROR_MSG,format);

        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
            }
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (Exception e) {
            }
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
            }
        }
    }

    private static boolean lineRegulator(String line, Set<String> includeKeywords, Set<String> excludeKeyWords, Pattern pattern) {
        if (StringUtils.isEmpty(line)) {
            return false;
        }
        if (pattern == null) {
            for (String excludeKeyword : excludeKeyWords) {
                if (line.contains(excludeKeyword)) {
                    return false;
                }
            }
            for (String includeKeyword : includeKeywords) {
                if (line.contains(includeKeyword)) {
                    return true;
                }
            }
        } else {
            Matcher matcher = pattern.matcher(line);
            return matcher.matches();
        }
        return false;
    }

}
