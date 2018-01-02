package com.hyt.hytpay.events;


import com.hyt.hytpay.enums.GatewayType;
import com.hyt.hytpay.enums.PaymentNotifyMethod;
import com.hyt.hytpay.gateways.GatewayBase;
import com.hyt.hytpay.models.Merchant;
import com.hyt.hytpay.models.NotifyProcess;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关返回的支付通知数据的接受
 * 
 * @author zhangmin
 *
 */
public class PaymentNotify {

	List<Merchant> merchantList;

	public PaymentNotify() {
		this(new ArrayList<Merchant>());
	}

	public PaymentNotify(Merchant merchant) {
		merchantList = new ArrayList<Merchant>();
	}

	public PaymentNotify(List<Merchant> merchantList) {
		this.merchantList = merchantList;
	}

	/**
	 * 网关返回的支付通知验证失败时触发
	 */
	PaymentFailedListener paymentFailed;

	/**
	 * 网关返回的支付通知验证成功时触发
	 */
	PaymentSucceedListener paymentSucceed;

	/**
	 * 返回通知消息的网关无法识别时触发
	 */
	UnknownGatewayListener unknownGateway;

	public void setPaymentFailed(PaymentFailedListener paymentFailed) {
		this.paymentFailed = paymentFailed;
	}

	public void setPaymentSucceed(PaymentSucceedListener paymentSucceed) {
		this.paymentSucceed = paymentSucceed;
	}

	public void setUnknownGateway(UnknownGatewayListener unknownGateway) {
		this.unknownGateway = unknownGateway;
	}

	protected void onPaymentFailed(PaymentFailedEventArgs e) {
		if (paymentFailed != null) {
			paymentFailed.handleEvent(e);
		}
	}

	protected void onPaymentSucceed(PaymentSucceedEventArgs e) {
		if (paymentSucceed != null) {
			paymentSucceed.handleEvent(e);
		}
	}

	protected void onUnknownGateway(UnknownGatewayEventArgs e) {
		if (unknownGateway != null) {
			unknownGateway.handleEvent(e);
		}
	}

	/**
	 * 接收并验证网关的支付通知
	 * 
	 * @param paymentNotifyMethod
	 * @throws Exception
	 */
	public void received(PaymentNotifyMethod paymentNotifyMethod)
			throws Exception {
		GatewayBase gateway = NotifyProcess.getGateway();
		gateway.setPaymentNotifyMethod(paymentNotifyMethod);
		if (gateway.getGatewayType() != GatewayType.None) {
			gateway.setMerchant(getMerchant(gateway.getGatewayType()));
			if (gateway.validateNotify()) {
				onPaymentSucceed(new PaymentSucceedEventArgs(gateway));
				gateway.writeSucceedFlag();
			} else {
				onPaymentFailed(new PaymentFailedEventArgs(gateway));
			}
		} else {
			gateway.setPaymentNotifyMethod(PaymentNotifyMethod.None);
			onUnknownGateway(new UnknownGatewayEventArgs(gateway));
		}
	}

	/**
	 * 添加商户数据。与添加的商户数据重复的网关将会被删除
	 * 
	 * @param merchant
	 *            商户数据
	 */
	public void addMerchant(Merchant merchant) {
		removeMerchant(merchant.getGatewayType());
		merchantList.add(merchant);
	}

	/**
	 * 获得商户数据。网关存在多个商户数据时返回第一个，无法找到返回null
	 * 
	 * @param gatewayType
	 *            网关类型
	 * @return 网关存在多个商户数据时返回第一个，无法找到返回null
	 */
	public Merchant getMerchant(GatewayType gatewayType) {
		return merchantList.stream()
				.filter(m -> m.getGatewayType() == gatewayType).findFirst()
				.get();
	}

	/**
	 * 删除商户数据
	 * 
	 * @param gatewayType
	 *            网关类型
	 */
	public void removeMerchant(GatewayType gatewayType) {
		Merchant removeMerchant = merchantList.stream()
				.filter(m -> m.getGatewayType() == gatewayType).findFirst()
				.get();
		if (removeMerchant != null) {
			merchantList.remove(removeMerchant);
		}
	}

}
