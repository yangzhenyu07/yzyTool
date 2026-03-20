package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * zip 文件工具类
* @author 杨镇宇
* @date 2025/2/14 14:31
* @version 1.0
*/
@Slf4j
public class ZipFileUtils {

    /**
     * 解压zip文件到指定目录
     * @param fileZip
     * @param pathToDest
     * @return
     */
    public static String unZip(String fileZip,String pathToDest){
        //创建存放目录
        File directory = new File(pathToDest);
        if (!directory.exists()){
            directory.mkdir();
        }
        try (
            FileInputStream fis = new FileInputStream(fileZip);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis))
        ){

            ZipEntry entry;
            //从ZipInputStream读取每个条目，只到没有
            while ((entry = zis.getNextEntry()) != null){
                log.info("Unzipping :" + entry.getName());
                if (entry.isDirectory()){
                    continue;
                }
                if (StringUtils.isNotBlank(entry.getName())){
                    int size;
                    byte [] buffer = new byte[2048];
                    File fileOut = new File(pathToDest + "/" + entry.getName());
                    try (
                            FileOutputStream fos = new FileOutputStream(fileOut);
                            BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length)
                            ){
                        while ((size = zis.read(buffer,0,buffer.length)) != -1){
                            bos.write(buffer,0,size);
                        }
                        bos.flush();

                    }
                    return pathToDest + "/" + entry.getName();
                }
            }

        }catch (IOException e){
            log.error("错误",e);
        }
        return null;

    }
}
