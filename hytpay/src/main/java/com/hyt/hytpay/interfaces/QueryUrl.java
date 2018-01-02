package com.hyt.hytpay.interfaces;

/**
 * 通过url地址来查询订单
 * 
 * @author milanyangbo
 *
 */
public interface QueryUrl {

	/**
	 * 创建查询订单的url地址
	 * 
	 * @return
	 */
	String buildQueryUrl();
}
