package org.example.annotation.vo;

import lombok.Builder;
import lombok.Data;

/**
*
* @author 杨镇宇
* @date 2024/12/19 10:50
* @version 1.0
*/
@Builder
@Data
public class TryLockVo {

    private boolean status;

    private String id;
}
