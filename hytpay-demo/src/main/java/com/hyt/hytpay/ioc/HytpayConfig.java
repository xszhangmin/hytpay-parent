package com.hyt.hytpay.ioc;

import com.hyt.hytpay.exceptions.GatewayException;
import com.hyt.hytpay.gateways.Gateways;
import com.hyt.hytpay.gateways.GatewaysImpl;
import com.hyt.hytpay.properties.AlipayProperties;
import com.hyt.hytpay.properties.UnionPayProperties;
import com.hyt.hytpay.properties.WeChatPaymentProperties;
import com.hyt.hytpay.providers.AlipayGateway;
import com.hyt.hytpay.providers.UnionPayGateway;
import com.hyt.hytpay.providers.WeChatPayGataway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Component
public class HytpayConfig {

	@Autowired
	private AlipayProperties alipayProperties;

	@Autowired
	private WeChatPaymentProperties weChatPaymentProperties;

	@Autowired
	private UnionPayProperties unionPayProperties;

	@Bean("prototype")
	public Gateways gateways() throws GatewayException, URISyntaxException {

		Gateways gateways = new GatewaysImpl();
		AlipayGateway alipayGateway = new AlipayGateway();
		alipayGateway.getMerchant().setAppId(alipayProperties.getAppid());
		alipayGateway.getMerchant()
				.setEmail(alipayProperties.getSeller_email());
		alipayGateway.getMerchant().setPartner(alipayProperties.getPartner());
		alipayGateway.getMerchant().setKey(alipayProperties.getKey());
		alipayGateway.getMerchant().setPrivateKeyPem(
				alipayProperties.getPrivatekeypem());
		alipayGateway.getMerchant().setPublicKeyPem(
				alipayProperties.getPublicKeypem());
		alipayGateway.getMerchant().setNotifyUrl(
				new URI(alipayProperties.getNotifyurl()));
		alipayGateway.getMerchant().setReturnUrl(
				new URI(alipayProperties.getReturnurl()));
		gateways.add(alipayGateway);

		WeChatPayGataway weChatPayGataway = new WeChatPayGataway();
		weChatPayGataway.getMerchant().setAppId(
				weChatPaymentProperties.getAppid());
		weChatPayGataway.getMerchant().setPartner(
				weChatPaymentProperties.getMch_id());
		weChatPayGataway.getMerchant().setKey(weChatPaymentProperties.getKey());
		weChatPayGataway.getMerchant().setNotifyUrl(
				new URI(weChatPaymentProperties.getNotifyurl()));
		weChatPayGataway.getMerchant().setReturnUrl(
				new URI(weChatPaymentProperties.getReturnurl()));
		gateways.add(weChatPayGataway);

		UnionPayGateway unionPayGateway = new UnionPayGateway();
		unionPayGateway.getMerchant().setPartner(unionPayProperties.getMerid());
		unionPayGateway.getMerchant().setNotifyUrl(
				new URI(unionPayProperties.getNotifyurl()));
		unionPayGateway.getMerchant().setReturnUrl(
				new URI(unionPayProperties.getReturnurl()));
		gateways.add(unionPayGateway);
		return gateways;

	}
}
