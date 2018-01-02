package com.hyt.hytpay.enums;

/**
 * 网关的交易类型
 * 
 * @author zhangmin
 *
 */
public enum GatewayTradeType {

	/**
	 * 未知交易类型
	 */
	None(-1),

	/**
	 * 电脑网站支付
	 */
	Web(0),

	/**
	 * 手机网站支付
	 */
	Wap(1),

	/**
	 * App支付
	 */
	APP(2),

	/**
	 * 二维码支付
	 */
	QRCode(3),

	/**
	 * 公众号支付
	 */
	Public(4),

	/**
	 * 条码支付
	 */
	BarCode(5),

	/**
	 * 小程序支付
	 */
	Applet(6), ;

	private Integer code;

	GatewayTradeType(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}
}
