package com.hyt.hytpay.interfaces;

/**
 * 支付订单通过url提交
 * 
 * @author zhangmin
 *
 */
public interface PaymentUrl {

	/**
	 * 创建包含支付订单数据的url地址
	 * 
	 * @return
	 * @throws Exception
	 */
	String buildPaymentUrl() throws Exception;
}
