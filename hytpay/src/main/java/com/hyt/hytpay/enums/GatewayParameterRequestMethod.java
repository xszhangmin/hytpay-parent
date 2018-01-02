package com.hyt.hytpay.enums;

/**
 * 向网关发送或接收到的网关的数据的请求方式类型
 * 
 * @author zhangmin
 *
 */
public enum GatewayParameterRequestMethod {
	Get(0), Post(1), Both(2), ;

	private Integer code;

	GatewayParameterRequestMethod(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}
}
