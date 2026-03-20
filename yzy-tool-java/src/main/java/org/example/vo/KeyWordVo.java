package org.example.vo;

import lombok.*;

/**
 * @author 杨镇宇
 * @date 2024/6/13 15:41
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class KeyWordVo {

    private String filePath;
    private int lineNo;
    private String line;


    @Override
    public String toString() {
        return filePath + "," + lineNo + "," + line;
    }
}
