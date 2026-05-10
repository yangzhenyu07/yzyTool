package org.example.exception;

/**
 * {@code TemporaryResultCode} 是一个临时结果码实现，用于在不改变原始 {@link ResultCode} 的情况下，
 * 为其自定义描述信息。主要场景为需要临时返回不同于标准描述内容但编码及成功状态保持一致的情况。
 *
 * <p>
 * 此类不可变，并且不支持对描述信息的后续修改。
 * </p>
 *
 * @author 杨镇宇
 * @version 1.0
 * @since 2024/12/11
 */
public class TemporaryResultCode implements ResultCode {

    /**
     * 原始的结果码对象。
     */
    private final ResultCode originalResultCode;

    /**
     * 自定义的描述信息，用于替换原始结果码的描述。
     */
    private final String customDesc;

    /**
     * 构造一个 {@code TemporaryResultCode} 实例。
     *
     * @param originalResultCode 原始结果码对象，不能为空。
     * @param customDesc         自定义描述信息，不能为空。
     * @throws NullPointerException 如果 {@code originalResultCode} 或 {@code customDesc} 为 {@code null}。
     */
    public TemporaryResultCode(ResultCode originalResultCode, String customDesc) {
        if (originalResultCode == null) {
            throw new NullPointerException("originalResultCode must not be null");
        }
        if (customDesc == null) {
            throw new NullPointerException("customDesc must not be null");
        }
        this.originalResultCode = originalResultCode;
        this.customDesc = customDesc;
    }

    /**
     * 判断该结果码是否表示成功状态。
     *
     * @return 如果原始 ResultCode 为成功，返回 {@code true}，否则返回 {@code false}。
     */
    @Override
    public boolean success() {
        return originalResultCode.success();
    }

    /**
     * 获取结果码字符串（与原始结果码一致）。
     *
     * @return 结果码字符串。
     */
    @Override
    public String code() {
        return originalResultCode.code();
    }

    /**
     * 获取描述信息。
     * 始终返回构造时传入的自定义描述。
     *
     * @return 描述信息字符串。
     */
    @Override
    public String desc() {
        return customDesc;
    }

    /**
     * 设置描述信息。该方法不被支持，调用将抛出异常。
     *
     * @param desc 描述信息（无效）
     * @throws UnsupportedOperationException 无条件抛出，表示不支持修改描述信息。
     */
    @Override
    public void setDesc(String desc) {
        throw new UnsupportedOperationException("TemporaryResultCode does not support setDesc");
    }
}
