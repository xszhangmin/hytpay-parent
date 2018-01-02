package com.hyt.hytpay.providers;


import com.hyt.hytpay.enums.GatewayType;
import com.hyt.hytpay.gateways.GatewayBase;
import com.hyt.hytpay.gateways.GatewayParameter;

import java.util.List;

public class NullGateway extends GatewayBase {

	/**
	 * 初始化未知网关
	 */
	public NullGateway() {
	}

	/**
	 * 初始化未知网关
	 * 
	 * @param gatewayParameterData
	 *            网关通知的数据集合
	 */
	public NullGateway(List<GatewayParameter> gatewayParameterData) {
		super(gatewayParameterData);
	}

	@Override
	public GatewayType getGatewayType() {
		return GatewayType.Alipay;
	}

	@Override
	protected boolean checkNotifyData() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void writeSucceedFlag() {
		// TODO Auto-generated method stub
	}

}
