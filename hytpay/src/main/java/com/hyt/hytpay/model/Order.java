package com.hyt.hytpay.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * 订单的金额、编号
 * 
 * @author zhangmin
 *
 */
public class Order {

	double orderAmount;
	String orderNo;
	String tradeNo;
	String subject;
	Date paymentDate;

	public Order() {
	}

	public Order(String orderNo, double orderAmount, String subject,
			Date paymentDate) {
		this.orderNo = orderNo;
		this.orderAmount = orderAmount;
		this.subject = subject;
		this.paymentDate = paymentDate;
	}

	public double getOrderAmount() {
		if (orderAmount < 0.01) {
			throw new IllegalArgumentException("OrderAmount-订单金额没有设置");
		}

		return orderAmount;

	}

	public void setOrderAmount(double orderAmount) {
		if (orderAmount < (double) 0.01) {
			throw new IllegalArgumentException("OrderAmount-订单金额必须大于或等于0.01");
		}
		this.orderAmount = orderAmount;
	}

	public String getOrderNo() {
		if (StringUtils.isBlank(orderNo)) {
			throw new IllegalArgumentException("OrderNo-订单订单编号没有设置");
		}

		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		if (StringUtils.isBlank(orderNo)) {
			throw new IllegalArgumentException("OrderNo-订单订单编号不能为空");
		}
		this.orderNo = orderNo;
	}

	public String getTradeNo() {
		if (StringUtils.isBlank(tradeNo)) {
			throw new IllegalArgumentException("TradeNo-交易流水号不能为空");
		}
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		if (StringUtils.isBlank(tradeNo)) {
			throw new IllegalArgumentException("TradeNo-交易流水号不能为空");
		}
		this.tradeNo = tradeNo;
	}

	/**
	 * 订单主题，订单主题为空时将使用订单orderNo作为主题
	 * 
	 * @return
	 */
	public String getSubject() {
		if (StringUtils.isBlank(subject)) {
			return orderNo;
		}

		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

}
