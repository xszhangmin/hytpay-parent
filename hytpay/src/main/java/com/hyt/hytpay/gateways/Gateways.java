package com.hyt.hytpay.gateways;

import com.hyt.hytpay.enums.GatewayTradeType;
import com.hyt.hytpay.enums.GatewayType;
import com.hyt.hytpay.exceptions.GatewayException;
import com.hyt.hytpay.models.Merchant;

import java.util.List;

public interface Gateways {

	/**
	 * 添加网关
	 * 
	 * @param gateway
	 * @return
	 * @throws GatewayException
	 *
	 */
	boolean add(GatewayBase gateway) throws GatewayException;

	/**
	 * 通过网关类型获取网关
	 * 
	 * @param gatewayType
	 * @return
	 * @throws GatewayException
	 */
	GatewayBase get(GatewayType gatewayType) throws GatewayException;

	/**
	 * 通过网关类型,交易类型获取网关
	 * 
	 * @param gatewayType
	 * @param gatewayTradeType
	 * @return
	 * @throws GatewayException
	 */
	GatewayBase get(GatewayType gatewayType, GatewayTradeType gatewayTradeType)
			throws GatewayException;

	/**
	 * 获取网关列表
	 * 
	 * @return
	 */
	List<GatewayBase> getList();

	/**
	 * 商户信息
	 * 
	 * @return
	 */
	List<Merchant> getMerchants();

}
