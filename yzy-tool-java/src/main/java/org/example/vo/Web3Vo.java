package org.example.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;

/**
* @author 杨镇宇
* @date 2026/3/20 23:29
* @version 1.0
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Web3Vo {

    @ApiModelProperty("私钥")
    private String privateKey;
    @ApiModelProperty("地址")
    private String address;
    @ApiModelProperty("转账金额")
    private BigDecimal amount;}
