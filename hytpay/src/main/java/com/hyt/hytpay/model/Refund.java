package com.hyt.hytpay.model;

import com.hyt.hytpay.utils.Utility;

import java.util.Date;

public class Refund {

	double orderAmount;
	double refundAmount;
	String orderNo;
	String tradeNo;
	String outRefoundNo;
	String refundDesc;
	String refoundNo;
	Date paymentDate;
	boolean refoundStatus;

	public double getOrderAmount() {
		if (orderAmount < 0.01) {
			throw new IllegalArgumentException("OrderAmount-订单金额没有设置");
		}
		return orderAmount;
	}

	public void setOrderAmount(double orderAmount) {
		if (orderAmount < 0.01) {
			throw new IllegalArgumentException("OrderAmount-订单金额必须大于或等于0.01");
		}
		this.orderAmount = orderAmount;
	}

	public double getRefundAmount() {
		if (refundAmount < 0.01) {
			throw new IllegalArgumentException("RefundAmount-退款金额没有设置");
		}
		return refundAmount;
	}

	public void setRefundAmount(double refundAmount) {
		if (refundAmount < 0.01) {
			throw new IllegalArgumentException("RefundAmount-订单金额必须大于或等于0.01");
		}
		this.refundAmount = refundAmount;
	}

	public String getOrderNo() {
		if (Utility.isBlankOrEmpty(orderNo)) {
			throw new IllegalArgumentException("OrderNo-订单订单编号没有设置");
		}
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		if (Utility.isBlankOrEmpty(orderNo)) {
			throw new IllegalArgumentException("OrderNo-订单订单编号不能为空");
		}
		this.orderNo = orderNo;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getOutRefoundNo() {
		if (Utility.isBlankOrEmpty(outRefoundNo)) {
			throw new IllegalArgumentException("outRefoundNo-商户退款单号没有设置");
		}
		return outRefoundNo;
	}

	public void setOutRefoundNo(String outRefoundNo) {
		if (Utility.isBlankOrEmpty(outRefoundNo)) {
			throw new IllegalArgumentException("outRefoundNo-商户退款单号不能为空");
		}
		this.outRefoundNo = outRefoundNo;
	}

	public String getRefundDesc() {
		return refundDesc;
	}

	public void setRefundDesc(String refundDesc) {
		this.refundDesc = refundDesc;
	}

	public String getRefoundNo() {
		return refoundNo;
	}

	public void setRefoundNo(String refoundNo) {
		this.refoundNo = refoundNo;
	}

	public Date getPaymentDate() {
		if (paymentDate == null) {
			throw new IllegalArgumentException("PaymentDate-订单创建时间未赋值");
		}
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		if (paymentDate == null) {
			throw new IllegalArgumentException("PaymentDate-订单创建时间未赋值");
		}
		this.paymentDate = paymentDate;
	}

	public boolean isRefoundStatus() {
		return refoundStatus;
	}

	public void setRefoundStatus(boolean refoundStatus) {
		this.refoundStatus = refoundStatus;
	}

}
