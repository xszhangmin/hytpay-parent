package com.hyt.hytpay.model;

import com.hyt.hytpay.enums.GatewayType;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;

/**
 * 商户数据
 * 
 * @author zhangmin
 *
 */
public class Merchant {

	String partner;
	String key;
	String email;
	String appId;
	URI notifyUrl;
	URI returnUrl;
	String privateKeyPem;
	String publicKeyPem;
	GatewayType gatewayType;

	public Merchant() {
	}

	public Merchant(String userName, String key, URI notifyUrl,
			GatewayType gatewayType) {
		this.partner = userName;
		this.key = key;
		this.notifyUrl = notifyUrl;
		this.gatewayType = gatewayType;
	}

	public String getPartner() {
		if (StringUtils.isBlank(partner)) {
			throw new IllegalArgumentException("Partner-商户帐号没有设置");
		}
		return partner;
	}

	public void setPartner(String partner) {
		if (StringUtils.isBlank(partner)) {
			throw new IllegalArgumentException("Partner-商户帐号不能为空");
		}
		this.partner = partner;
	}

	public String getKey() {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("Key-商户密钥没有设置");
		}
		return key;
	}

	public void setKey(String key) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("Key-商户密钥不能为空");
		}

		this.key = key;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public URI getNotifyUrl() {
		if (notifyUrl == null) {
			throw new IllegalArgumentException("NotifyUrl-网关通知Url没有设置");
		}
		return notifyUrl;
	}

	public void setNotifyUrl(URI notifyUrl) {
		if (notifyUrl == null) {
			throw new IllegalArgumentException("NotifyUrl-网关通知Url不能为空");
		}
		this.notifyUrl = notifyUrl;
	}

	public URI getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(URI returnUrl) {
		this.returnUrl = returnUrl;
	}

	public GatewayType getGatewayType() {
		return gatewayType;
	}

	public void setGatewayType(GatewayType gatewayType) {
		this.gatewayType = gatewayType;
	}

	public String getPrivateKeyPem() {
		return privateKeyPem;
	}

	public void setPrivateKeyPem(String privateKeyPem) {
		this.privateKeyPem = privateKeyPem;
	}

	public String getPublicKeyPem() {
		return publicKeyPem;
	}

	public void setPublicKeyPem(String publicKeyPem) {
		this.publicKeyPem = publicKeyPem;
	}
}
