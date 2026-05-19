package org.example.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;


/**
 * <p>
 * 测试表
 * </p>
 *
 * @author yangzhenyu
 * @since 2026-05-14 11:52:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TUserTestVo {


    @ApiModelProperty("code")
    @NotEmpty(message ="code不能为空")
    private String code;

    @ApiModelProperty("姓名")
    @NotEmpty(message ="姓名不能为空")
    private String name;


}
