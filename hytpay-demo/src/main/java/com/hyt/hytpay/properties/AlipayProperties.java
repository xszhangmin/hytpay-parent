package com.hyt.hytpay.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {

	private String appid;
	private String partner;
	private String seller_email;
	private String key;
	private String privatekeypem;
	private String publicKeypem;
	private String notifyurl;
	private String returnurl;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getSeller_email() {
		return seller_email;
	}

	public void setSeller_email(String seller_email) {
		this.seller_email = seller_email;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getNotifyurl() {
		return notifyurl;
	}

	public void setNotifyurl(String notifyurl) {
		this.notifyurl = notifyurl;
	}

	public String getReturnurl() {
		return returnurl;
	}

	public void setReturnurl(String returnurl) {
		this.returnurl = returnurl;
	}

	public String getPrivatekeypem() {
		return privatekeypem;
	}

	public void setPrivatekeypem(String privatekeypem) {
		this.privatekeypem = privatekeypem;
	}

	public String getPublicKeypem() {
		return publicKeypem;
	}

	public void setPublicKeypem(String publicKeypem) {
		this.publicKeypem = publicKeypem;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}
}
