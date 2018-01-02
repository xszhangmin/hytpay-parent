package com.hyt.hytpay.events;


import com.hyt.hytpay.enums.GatewayType;
import com.hyt.hytpay.enums.PaymentNotifyMethod;
import com.hyt.hytpay.models.Order;

/**
 * 支付成功网关事件数据
 * 
 * @author zhangmin
 *
 */
public class PaymentSucceedEventArgs extends PaymentEventArgs {

	private static final long serialVersionUID = 6846802073052680431L;

	Order order;

	GatewayType gatewayType;

	PaymentNotifyMethod paymentNotifyMethod;

	public PaymentSucceedEventArgs(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 订单数据
	 * 
	 * @return
	 */
	public Order getOrder() {
		return gateway.getOrder();
	}

	/**
	 * 支付网关类型
	 * 
	 * @return
	 */
	public GatewayType getGatewayType() {
		return gateway.getGatewayType();
	}

	/**
	 * 支付通知的返回方式
	 * 
	 * @return 目前的支付网关在支付成功后会以Get或Post方式将支付结果返回给商户。POST方式的返回一般是通过网关服务器发送，
	 *         这里可能要求商户输出字符标记表示已成功接收到支付结果
	 *         。而另一种是通过GET方式将用户返回到商户的网站，这时如果以POST数据时的方式来处理将会如果这样用户会感到很奇怪
	 *         ，这时显示支付成功的页面将会更合适。所以可以通过PaymentNotifyMethod属性来判断输出标记已成功接收的字符串。
	 *         支付结果的发送方式
	 *         ，以决定是应该输出标记已成功接收的字符串还是向用户显示支付成功的页面。服务器发送通知时属性为ServerNotify
	 *         ，如果是用户通过浏览器跳转到接收网关通知的页面属性为AutoReturn。
	 * 
	 */
	public PaymentNotifyMethod getPaymentNotifyMethod() {
		return gateway.getPaymentNotifyMethod();
	}

}
