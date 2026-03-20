package org.example.annotation.vo;

import lombok.Builder;
import lombok.Data;

/**
* 历史
* @author 杨镇宇
* @date 2024/12/19 10:59
* @version 1.0
*/
@Builder
@Data
public class LockHistoryVo {
    private String status;
    private String id;
    private String keyName;
    private String keyValue;
    private String message;

}
