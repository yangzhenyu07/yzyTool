package org.example.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import org.hibernate.validator.constraints.NotEmpty;
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
public class ScanVo {
    /**
     * 路径
     */
    @ApiModelProperty("路径")
    @NotEmpty(message ="路径不能为空")
    private String path;
    /**
     * 正则内容
     */
    @ApiModelProperty("正则内容")
    private String pattern;
    /**
     * 查询的值
     */
    @ApiModelProperty("查询的值")
    private String key;

    public String check(){

        if (StringUtils.isBlank(pattern) && StringUtils.isBlank(key)){
            return "正则内容与查询的值不能都为空";
        }
        return "";
    }

}
