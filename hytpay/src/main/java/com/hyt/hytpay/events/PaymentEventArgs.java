package com.hyt.hytpay.events;

import com.hyt.hytpay.enums.GatewayParameterRequestMethod;
import com.hyt.hytpay.gateways.GatewayBase;
import com.hyt.hytpay.gateways.GatewayParameter;
import com.hyt.hytpay.utils.Utility;

import java.util.EventObject;
import java.util.List;

public class PaymentEventArgs extends EventObject {

	private static final long serialVersionUID = 6788550405058932439L;

	GatewayBase gateway;
	String notifyServerHostAddress;
	List<GatewayParameter> gatewayParameterData;

	public PaymentEventArgs(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
		gateway = (GatewayBase) source;
		notifyServerHostAddress = Utility.getHttpServletRequest()
				.getRemoteHost();
	}

	public GatewayBase getGateway() {
		return gateway;
	}

	public String getNotifyServerHostAddress() {
		return notifyServerHostAddress;
	}

	public List<GatewayParameter> getGatewayParameterData() {
		return gateway.getGatewayParameterData();
	}

	/**
	 * 获得网关的参数值。没有参数值时返回空字符串，Get方式的值均为未解码。
	 * 
	 * @param gatewayParameterName
	 * @return
	 */
	public String getGatewayParameterValue(String gatewayParameterName) {
		return gateway.getGatewayParameterValue(gatewayParameterName);
	}

	/**
	 * 获得网关的参数值。没有参数值时返回空字符串，Get方式的值均为未解码。
	 * 
	 * @param gatewayParameterName
	 *            网关的参数名称
	 * @param gatewayParameterRequestMethod
	 *            网关的数据的请求方法的类型
	 * @return
	 */
	public String getGatewayParameterValue(String gatewayParameterName,
			GatewayParameterRequestMethod gatewayParameterRequestMethod) {
		return gateway.getGatewayParameterValue(gatewayParameterName,
				gatewayParameterRequestMethod);
	}
}
