package com.hyt.hytpay.interfaces;

import com.alipay.api.AlipayApiException;
import com.hyt.hytpay.model.Refund;

public interface RefundReq {
	/**
	 * 创建退款
	 * 
	 * @param refund
	 * @return
	 * @throws AlipayApiException
	 * @throws Exception
	 */
	Refund buildRefund(Refund refund) throws AlipayApiException, Exception;

	/**
	 * 查询退款结果
	 * 
	 * @param refund
	 * @return
	 * @throws AlipayApiException
	 * @throws Exception
	 */
	Refund buildRefundQuery(Refund refund) throws AlipayApiException, Exception;
}
