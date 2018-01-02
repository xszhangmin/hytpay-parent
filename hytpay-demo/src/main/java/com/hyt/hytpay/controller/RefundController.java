package com.hyt.hytpay.controller;

import com.hyt.hytpay.enums.GatewayType;
import com.hyt.hytpay.gateways.GatewayBase;
import com.hyt.hytpay.gateways.Gateways;
import com.hyt.hytpay.models.PaymentSetting;
import com.hyt.hytpay.models.Refund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/refund")
public class RefundController {

	@Autowired
	Gateways gateways;

	public RefundController(Gateways gateways) {
		this.gateways = gateways;
	}

	@GetMapping("/createrefund")
	public void createRefund(Integer type) throws IOException, Exception {
		GatewayType gatewayType = GatewayType.Alipay;
		if (type == 0) {
			gatewayType = GatewayType.Alipay;
		}
		if (type == 1) {
			gatewayType = GatewayType.WeChatPay;
		}
		if (type == 2) {
			gatewayType = GatewayType.UnionPay;
		}

		GatewayBase gateway = gateways.get(gatewayType);
		PaymentSetting paymentSetting = new PaymentSetting(gateway);

		Refund refund = new Refund();
		refund.setOutRefoundNo("0000000000");

		paymentSetting.buildRefund(refund);
		paymentSetting.buildRefundQuery(refund);
	}

}
