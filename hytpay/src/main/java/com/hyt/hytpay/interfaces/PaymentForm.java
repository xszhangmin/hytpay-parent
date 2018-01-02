package com.hyt.hytpay.interfaces;

/**
 * 支付订单通过form表单提交的HTML代码
 * 
 * @author zhangmin
 *
 */
public interface PaymentForm {

	/**
	 * 创建包含支付订单数据的form表单的HTML代码
	 * 
	 * @return
	 * @throws Exception
	 */
	String buildPaymentForm() throws Exception;
}
