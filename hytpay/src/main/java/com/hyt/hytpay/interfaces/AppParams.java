package com.hyt.hytpay.interfaces;

import java.util.Map;

/**
 * 手机端SDK支付
 * 
 * @author zhangmin
 *
 */
public interface AppParams {
	/**
	 * 创建手机端SDK支付需要信息
	 * 
	 * @return
	 * @throws Exception
	 */
	Map<String, String> buildPayParams() throws Exception;
}
