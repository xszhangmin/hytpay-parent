package com.hyt.hytpay.gateways;

import com.hyt.hytpay.enums.GatewayParameterRequestMethod;
import org.apache.commons.lang3.StringUtils;

/**
 * 支付网关的Get与Post的数据
 * 
 * @author zhangmin
 *
 */
public class GatewayParameter {

	String name;

	String value;

	GatewayParameterRequestMethod requestMethod;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("parameterName-参数名不能为空");
		}
		this.name = name;
	}

	public GatewayParameter() {
	}

	public GatewayParameter(String parameterName, String parameterValue,
			GatewayParameterRequestMethod parameterType) {
		this.name = parameterName;
		this.value = parameterValue;
		this.requestMethod = parameterType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public GatewayParameterRequestMethod getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(GatewayParameterRequestMethod requestMethod) {
		this.requestMethod = requestMethod;
	}
}
