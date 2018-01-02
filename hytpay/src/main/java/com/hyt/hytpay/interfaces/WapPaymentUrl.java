package com.hyt.hytpay.interfaces;

import java.util.Map;

/**
 * 支付订单通过url提交
 * 
 * @author zhangmin
 *
 */
public interface WapPaymentUrl {

	/**
	 * 创建包含支付订单数据的url地址
	 * 
	 * @param redirect_url
	 *            微信自定义跳转路径
	 * @return
	 * @throws Exception
	 */
	String buildWapPaymentUrl(Map<String, String> map) throws Exception;
}
