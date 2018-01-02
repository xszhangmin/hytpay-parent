package com.hyt.hytpay.enums;

/**
 * 支付通知的返回方式
 * 
 * @author zhangmin
 *
 */
public enum PaymentNotifyMethod {
	/**
	 * 未知
	 */
	None(0),

	/**
	 * 浏览器自动返回
	 */
	AutoReturn(1),

	/**
	 * 服务器异步通知
	 */
	ServerNotify(2), ;

	private Integer code;

	PaymentNotifyMethod(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}
}
