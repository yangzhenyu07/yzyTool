package org.example.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.exception.ExceptionEnum;
import org.example.exception.throwtype.RunException;
import org.example.service.ToolService;
import org.example.util.KeyWordScan;
import org.example.vo.KeyWordVo;
import org.example.vo.ScanVo;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* @author 杨镇宇
* @date 2024/6/13 15:38
* @version 1.0
*/
@Service
@Slf4j
public class ToolServiceImpl implements ToolService {

    private static final Set<String> excludeKeywords = new HashSet<>();
    static {
        excludeKeywords.add("*");
    }

    public  List<File> findFilesInDirectory(String localPath, String prefix) {
        try {
            List<File> fileList = new ArrayList<>();
            File file = new File(localPath);
            if (file.exists()) {
                File[] files = file.listFiles();
                if (null != files) {
                    for (File subFile : files) {
                        if (subFile.isDirectory()) {
                            fileList.addAll(findFilesInDirectory(subFile.getAbsolutePath(), prefix));
                        } else {
                            if (StringUtils.isNotEmpty(prefix)) {
                                if (subFile.getName().endsWith(prefix)) {
                                    fileList.add(subFile);
                                }
                            } else {
                                fileList.add(subFile);
                            }
                        }
                    }
                }
            } else {
                throw new FileNotFoundException(localPath);
            }
            return fileList;
        } catch (Exception e) {
            log.error( "遍历路径{}发生异常", localPath,e);
            throw  new RunException(ExceptionEnum.ERROR_MSG,"遍历路径" + localPath + "发生异常");

        }
    }
    //正则筛选
    public List<KeyWordVo> keyWordScanPattern(ScanVo scanVo) {
        List<KeyWordVo> list = Lists.newArrayList();
        Set<String> includeKeywords = Sets.newHashSet();
        includeKeywords.add(scanVo.getKey());

        KeyWordScan keyWordScan = new KeyWordScan();
        List<File> fileList = findFilesInDirectory(scanVo.getPath(), ".vue");
        for (File file : fileList) {
            List<KeyWordVo> keyWordVoList = keyWordScan.analyseFile(file, includeKeywords, excludeKeywords, scanVo.getPattern());
            if (CollectionUtils.isNotEmpty(keyWordVoList)){
                log.info("scan:{}",keyWordVoList);
                list.addAll(keyWordVoList);
            }
        }
        List<File> fileListJs = findFilesInDirectory(scanVo.getPath(), ".js");
        for (File file : fileListJs) {
            List<KeyWordVo> keyWordVoList = keyWordScan.analyseFile(file, includeKeywords, excludeKeywords, scanVo.getPattern());
            if (CollectionUtils.isNotEmpty(keyWordVoList)){
                log.info("scan:{}",keyWordVoList);
                list.addAll(keyWordVoList);

            }
        }
        List<File> fileListHtml = findFilesInDirectory(scanVo.getPath(), ".html");
        for (File file : fileListHtml) {
            List<KeyWordVo> keyWordVoList = keyWordScan.analyseFile(file, includeKeywords, excludeKeywords, scanVo.getPattern());
            if (CollectionUtils.isNotEmpty(keyWordVoList)){
                log.info("scan:{}",keyWordVoList);
                list.addAll(keyWordVoList);

            }
        }
        return list;

    }
    //非正则筛选
    public List<KeyWordVo> keyWordScan(ScanVo scanVo) {
        List<KeyWordVo> list = Lists.newArrayList();
        Set<String> includeKeywords = Sets.newHashSet();
        includeKeywords.add(scanVo.getKey());

        KeyWordScan keyWordScan = new KeyWordScan();
        List<File> fileList = findFilesInDirectory(scanVo.getPath(), ".vue");
        for (File file : fileList) {
            List<KeyWordVo> keyWordVoList = keyWordScan.analyseFile(file, includeKeywords, excludeKeywords, null);
            if (CollectionUtils.isNotEmpty(keyWordVoList)){
                log.info("scan:{}",keyWordVoList);
                list.addAll(keyWordVoList);
            }
        }
        List<File> fileListJs = findFilesInDirectory(scanVo.getPath(), ".js");
        for (File file : fileListJs) {
            List<KeyWordVo> keyWordVoList = keyWordScan.analyseFile(file, includeKeywords, excludeKeywords, null);
            if (CollectionUtils.isNotEmpty(keyWordVoList)){
                log.info("scan:{}",keyWordVoList);
                list.addAll(keyWordVoList);
            }
        }
        List<File> fileListHtml = findFilesInDirectory(scanVo.getPath(), ".html");
        for (File file : fileListHtml) {
            List<KeyWordVo> keyWordVoList = keyWordScan.analyseFile(file, includeKeywords, excludeKeywords, null);
            if (CollectionUtils.isNotEmpty(keyWordVoList)){
                log.info("scan:{}",keyWordVoList);
                list.addAll(keyWordVoList);
            }
        }
        return list;

    }
    @Override
    public List<KeyWordVo> scan(ScanVo scanVo) {
        if (StringUtils.isNotBlank(scanVo.getPattern())){
            return keyWordScanPattern(scanVo);
        }else {
            return keyWordScan(scanVo);
        }
    }
}
