package com.hyt.hytpay.interfaces;

import com.alipay.api.AlipayApiException;

/**
 * 向支付网关查询url发送需要查询的订单数据，支付网关在查询url页面输出查询结果
 * 
 * @author zhangmin
 *
 */
public interface QueryNow {
	/**
	 * 查询订单是否支付成功。
	 * 
	 * @return 服务器在查询url页面输出返回查询数据
	 * @throws AlipayApiException
	 */
	boolean queryNow() throws Exception;

}
