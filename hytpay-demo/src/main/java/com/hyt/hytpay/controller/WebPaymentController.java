package com.hyt.hytpay.controller;

import com.hyt.hytpay.enums.GatewayTradeType;
import com.hyt.hytpay.enums.GatewayType;
import com.hyt.hytpay.gateways.GatewayBase;
import com.hyt.hytpay.gateways.Gateways;
import com.hyt.hytpay.models.PaymentSetting;
import com.unionpay.acp.sdk.SDKConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/webpayment")
public class WebPaymentController {

	@Autowired
	Gateways gateways;

	public WebPaymentController(Gateways gateways) {
		this.gateways = gateways;
	}

	@GetMapping("/createorder")
	public void createOrder(Integer type) throws IOException, Exception {
		GatewayType gatewayType = GatewayType.Alipay;
		if (type == 0) {
			gatewayType = GatewayType.Alipay;
		}
		if (type == 1) {
			gatewayType = GatewayType.WeChatPay;
		}
		if (type == 2) {
			SDKConfig.getConfig().loadPropertiesFromSrc();
			gatewayType = GatewayType.UnionPay;
		}

		GatewayBase gateway = gateways.get(gatewayType, GatewayTradeType.Web);
		PaymentSetting paymentSetting = new PaymentSetting(gateway);
		paymentSetting.getOrder().setOrderAmount(0.01);
		paymentSetting.getOrder().setOrderNo(
				new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
		paymentSetting.getOrder().setPaymentDate(new Date());
		paymentSetting.getOrder().setSubject("webpay");
		paymentSetting.payment(null);
	}
}
