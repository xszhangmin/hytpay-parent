package com.hyt.hytpay.controller;

import com.hyt.hytpay.enums.GatewayType;
import com.hyt.hytpay.gateways.GatewayBase;
import com.hyt.hytpay.gateways.Gateways;
import com.hyt.hytpay.models.PaymentSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/querypayment")
public class QueryPaymentController {

	@Autowired
	Gateways gateways;

	public QueryPaymentController(Gateways gateways) {
		this.gateways = gateways;
	}

	@GetMapping("/createquery")
	public void createQuery(Integer type) throws IOException, Exception {
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

		// 查询时需要设置订单的Id与金额，在查询结果中将会核对订单的Id与金额，如果不相符会返回查询失败。
		paymentSetting.getOrder().setOrderAmount(0.01);
		paymentSetting.getOrder().setOrderNo("yourorderno");

		if (paymentSetting.queryNow()) {
			// 订单已支付
		}
	}

}
