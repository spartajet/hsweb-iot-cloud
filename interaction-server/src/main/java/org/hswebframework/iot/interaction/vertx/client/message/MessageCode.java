package org.hswebframework.iot.interaction.vertx.client.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The enum Message code.
 *
 * @author zhouhao
 * @since 1.0.0
 */
@AllArgsConstructor
@Getter
public enum MessageCode {
    /**
     * Auth fail message code.
     */
    AUTH_FAIL(40102, "授权失败"),

    /**
     * No auth param message code.
     */
    NO_AUTH_PARAM(40101, "授权参数错误"),

    /**
     * Param format error message code.
     */
    PARAM_FORMAT_ERROR(40002, "参数格式错误"),
    /**
     * Success message code.
     */
    SUCCESS(0, "成功"),
    /**
     * Un registered client message code.
     */
    UN_REGISTERED_CLIENT(40301, "未注册的客户端")
    ,
    /**
     * Un support type message code.
     */
    UN_SUPPORT_TYPE(40001, "不支持的操作类型");

    /**
     * The Code.
     */
    private int code;

    /**
     * The Message.
     */
    private String message;
}
