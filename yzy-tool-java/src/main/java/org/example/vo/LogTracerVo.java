package org.example.vo;

import lombok.*;

/**
* @author 杨镇宇
* @date 2024/11/26 10:49
* @version 1.0
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LogTracerVo {
    private String traceId;
    private String ip;
}
