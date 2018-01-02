package com.hyt.hytpay.providers;


import com.hyt.hytpay.enums.GatewayType;
import com.hyt.hytpay.gateways.GatewayBase;
import com.hyt.hytpay.gateways.GatewayParameter;
import com.hyt.hytpay.interfaces.*;
import com.hyt.hytpay.model.Refund;
import com.hyt.hytpay.utils.HttpClientUtil;
import com.hyt.hytpay.utils.Utility;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信支付网关
 * 
 * @author zhangmin
 *
 */
public class WeChatPayGataway extends GatewayBase implements PaymentQRCode,
		WapPaymentUrl, AppParams, QueryNow, RefundReq {

	final String payGatewayUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	final String queryGatewayUrl = "https://api.mch.weixin.qq.com/pay/orderquery";
	final String refundGatewayUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	final String refundqueryGatewayUrl = "https://api.mch.weixin.qq.com/pay/refundquery";

	/**
	 * 初始化微信支付网关
	 */
	public WeChatPayGataway() {
	}

	/**
	 * 初始化微信支付网关
	 * 
	 * @param gatewayParameterData
	 *            网关通知的数据集合
	 */
	public WeChatPayGataway(List<GatewayParameter> gatewayParameterData) {
		super(gatewayParameterData);
	}

	@Override
	public GatewayType getGatewayType() {
		return GatewayType.WeChatPay;
	}

	@Override
	public String getPaymentQRCodeContent() throws Exception {
		// TODO Auto-generated method stub
		initPaymentOrderParameter("NATIVE", "127.0.0.1");
		String xmlString = convertGatewayParameterDataToXml();
		String resultXml = HttpClientUtil.doPost(payGatewayUrl, xmlString,
				"text/xml");
		return getWeixinPaymentUrl(resultXml);
	}

	/**
	 * https://pay.weixin.qq.com/wiki/doc/api/H5.php?chapter=15_4
	 */
	@Override
	public String buildWapPaymentUrl(Map<String, String> map) throws Exception {
		// TODO Auto-generated method stub
		String redirect_url = map.getOrDefault("redirect_url", "");
		initPaymentOrderParameter("MWEB", Utility.getClientIP());
		String xmlString = convertGatewayParameterDataToXml();
		String resultXml = HttpClientUtil.doPost(payGatewayUrl, xmlString,
				"text/xml");
		String url = getWeixinPaymentUrl(resultXml);
		redirect_url = Utility.isBlankOrEmpty(redirect_url) ? getMerchant()
				.getReturnUrl().toString() : redirect_url;
		redirect_url = URLEncoder.encode(redirect_url, "UTF-8");
		if (Utility.isBlankOrEmpty(redirect_url)) {
			return url;
		} else {
			return String.format("%s&redirect_url=%s", url, redirect_url);
		}

	}

	@Override
	public Map<String, String> buildPayParams() throws Exception {
		// TODO Auto-generated method stub
		initPaymentOrderParameter("APP", "127.0.0.1");
		String xmlString = convertGatewayParameterDataToXml();
		String resultString = HttpClientUtil.doPost(payGatewayUrl, xmlString,
				"text/xml");
		getWeixinPaymentUrl(resultString);

		String prepayid = getGatewayParameterValue("prepay_id");
		clearGatewayParameterData();

		setGatewayParameterValue("appid", getMerchant().getAppId());
		setGatewayParameterValue("partnerid", getMerchant().getPartner());
		setGatewayParameterValue("prepayid", prepayid);
		setGatewayParameterValue("package", "Sign=WXPay");
		setGatewayParameterValue("noncestr", Utility.generateUUID());
		setGatewayParameterValue("timestamp", Utility.getCurrentTimestampMs());
		setGatewayParameterValue("sign", getSign());

		Map<String, String> resParam = new HashMap<String, String>();
		resParam.put("prepayid", prepayid);
		resParam.put("noncestr", getGatewayParameterValue("noncestr"));
		resParam.put("sign", getGatewayParameterValue("sign"));
		resParam.put("timestamp", getGatewayParameterValue("timestamp"));
		resParam.put("partnerid", getGatewayParameterValue("partnerid"));
		return resParam;
	}

	@Override
	public boolean queryNow() throws Exception {
		// TODO Auto-generated method stub
		initQueryOrderParameter();
		String xmlString = convertGatewayParameterDataToXml();
		String resultXml = HttpClientUtil.doPost(queryGatewayUrl, xmlString,
				"text/xml");
		return checkQueryResult(resultXml);
	}

	@Override
	public Refund buildRefund(Refund refund) throws Exception {
		// TODO Auto-generated method stub
		setGatewayParameterValue("appid", getMerchant().getAppId());
		setGatewayParameterValue("mch_id", getMerchant().getPartner());
		setGatewayParameterValue("nonce_str", Utility.generateUUID());
		setGatewayParameterValue("sign_type", "MD5");
		if (!Utility.isBlankOrEmpty(refund.getTradeNo())) {
			setGatewayParameterValue("transaction_id", refund.getTradeNo());
		} else {
			setGatewayParameterValue("out_trade_no", refund.getOrderNo());
		}
		setGatewayParameterValue("out_refund_no", refund.getRefoundNo());
		setGatewayParameterValue("total_fee",
				(int) (refund.getOrderAmount() * 100));
		setGatewayParameterValue("refund_fee",
				(int) (refund.getRefundAmount() * 100));
		if (!Utility.isBlankOrEmpty(refund.getRefundDesc())) {
			setGatewayParameterValue("refund_desc", refund.getRefundDesc());
		}
		setGatewayParameterValue("sign", getSign()); // 签名需要在最后设置，以免缺少参数。
		String xmlString = convertGatewayParameterDataToXml();
		String resultString = HttpClientUtil.doPost(refundGatewayUrl,
				xmlString, "text/xml");
		getWeixinPaymentUrl(resultString);
		if (getGatewayParameterValue("result_code") == "SUCCESS") {
			refund.setTradeNo(getGatewayParameterValue("transaction_id"));
			refund.setRefoundNo(getGatewayParameterValue("refund_id"));
			refund.setRefoundStatus(true);
		}
		return refund;
	}

	@Override
	public Refund buildRefundQuery(Refund refund) throws Exception {
		// TODO Auto-generated method stub
		setGatewayParameterValue("appid", getMerchant().getAppId());
		setGatewayParameterValue("mch_id", getMerchant().getPartner());
		setGatewayParameterValue("nonce_str", Utility.generateUUID());
		setGatewayParameterValue("sign_type", "MD5");
		setGatewayParameterValue("out_refund_no", refund.getRefoundNo());
		setGatewayParameterValue("sign", getSign()); // 签名需要在最后设置，以免缺少参数。
		String xmlString = convertGatewayParameterDataToXml();
		String resultString = HttpClientUtil.doPost(refundqueryGatewayUrl,
				xmlString, "text/xml");
		getWeixinPaymentUrl(resultString);
		if (getGatewayParameterValue("result_code") == "SUCCESS") {
			refund.setTradeNo(getGatewayParameterValue("transaction_id"));
			refund.setRefoundNo(getGatewayParameterValue("refund_id"));
			refund.setRefoundNo(getGatewayParameterValue("out_refund_no"));
			refund.setOrderAmount(Double
					.parseDouble(getGatewayParameterValue("total_fee")) * 0.01);
			refund.setRefundAmount(Double
					.parseDouble(getGatewayParameterValue("refund_fee")) * 0.01);
			refund.setRefoundStatus(true);
		}
		return refund;
	}

	private boolean checkQueryResult(String resultXml) throws Exception {
		// TODO Auto-generated method stub
		// 需要先清除之前查询订单的参数，否则会对接收到的参数造成干扰。
		clearGatewayParameterData();
		readResultXml(resultXml);
		if (isSuccessResult()) {
			if (!Utility.isBlankOrEmpty(getGatewayParameterValue("total_fee"))) {
				if (getOrder().getOrderNo().equalsIgnoreCase(
						getGatewayParameterValue("out_trade_no"))
						&& getOrder().getOrderAmount() == Integer
								.parseInt(getGatewayParameterValue("total_fee")) / 100.0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected boolean checkNotifyData() throws Exception {
		// TODO Auto-generated method stub
		if (isSuccessResult()) {
			readNotifyOrderParameter();
			return true;
		}
		return false;
	}

	@Override
	public void writeSucceedFlag() throws Exception {
		// TODO Auto-generated method stub
		clearGatewayParameterData();
		initProcessSuccessParameter();
		Utility.getHttpServletResponse().getWriter()
				.write(convertGatewayParameterDataToXml());
	}

	/**
	 * 初始化表示已成功接收到支付通知的数据
	 */
	private void initProcessSuccessParameter() {
		// TODO Auto-generated method stub
		setGatewayParameterValue("return_code", "SUCCESS");
	}

	private void readNotifyOrderParameter() {
		// TODO Auto-generated method stub
		getOrder().setOrderNo(getGatewayParameterValue("out_trade_no"));
		getOrder().setOrderAmount(
				Integer.parseInt(getGatewayParameterValue("total_fee")) * 0.01);
		getOrder().setTradeNo(getGatewayParameterValue("transaction_id"));
	}

	/**
	 * 初始化支付订单的参数
	 * 
	 * @throws Exception
	 */
	private void initQueryOrderParameter() throws Exception {
		// TODO Auto-generated method stub
		setGatewayParameterValue("appid", getMerchant().getAppId());
		setGatewayParameterValue("mch_id", getMerchant().getPartner());
		setGatewayParameterValue("out_trade_no", getOrder().getOrderNo());
		setGatewayParameterValue("nonce_str", Utility.generateUUID());
		setGatewayParameterValue("sign", getSign()); // 签名需要在最后设置，以免缺少参数。
	}

	/**
	 * 获得微信支付的URL
	 * 
	 * @param resultString
	 * @return 创建订单返回的数据
	 * @throws Exception
	 */
	private String getWeixinPaymentUrl(String resultXml) throws Exception {
		// TODO Auto-generated method stub
		// 需要先清除之前创建订单的参数，否则会对接收到的参数造成干扰。
		clearGatewayParameterData();
		readResultXml(resultXml);
		if (isSuccessResult()) {
			return Utility.isBlankOrEmpty(getGatewayParameterValue("code_url")) ? getGatewayParameterValue("mweb_url")
					: getGatewayParameterValue("code_url");
		}

		return null;
	}

	/**
	 * 是否是成功的结果
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean isSuccessResult() throws Exception {
		// TODO Auto-generated method stub
		if (validateResult() && validateSign()) {
			return true;
		}

		return false;
	}

	/**
	 * 验证返回的结果
	 * 
	 * @return
	 */
	private boolean validateResult() {
		// TODO Auto-generated method stub
		if (getGatewayParameterValue("return_code").equalsIgnoreCase("SUCCESS")
				&& getGatewayParameterValue("result_code").equalsIgnoreCase(
						"SUCCESS")) {
			return true;
		}

		return false;
	}

	/**
	 * 验证签名
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean validateSign() throws Exception {
		// TODO Auto-generated method stub
		if (getGatewayParameterValue("sign").equalsIgnoreCase(getSign())) {
			return true;
		}

		return false;
	}

	/**
	 * 读取结果的XML
	 * 
	 * @param resultXml
	 * @throws Exception
	 */
	private void readResultXml(String resultXml) throws Exception {
		// TODO Auto-generated method stub
		Utility.xmlToMap(resultXml).forEach((key, val) -> {
			setGatewayParameterValue(key, val);
		});
	}

	/**
	 * 清除网关的数据
	 */
	private void clearGatewayParameterData() {
		// TODO Auto-generated method stub
		getGatewayParameterData().clear();
	}

	/**
	 * 将网关数据转换成XML
	 * 
	 * @return
	 * @throws Exception
	 */
	private String convertGatewayParameterDataToXml() throws Exception {
		// TODO Auto-generated method stub
		return Utility.mapToXml(getSortedGatewayParameter());
	}

	/**
	 * 初始化支付订单的参数
	 * 
	 * @param trade_type
	 * @param spbill_create_ip
	 * @throws Exception
	 */
	private void initPaymentOrderParameter(String trade_type,
			String spbill_create_ip) throws Exception {

		setGatewayParameterValue("appid", getMerchant().getAppId());
		setGatewayParameterValue("mch_id", getMerchant().getPartner());
		setGatewayParameterValue("nonce_str", Utility.generateUUID());
		setGatewayParameterValue("body", getOrder().getSubject());
		setGatewayParameterValue("out_trade_no", getOrder().getOrderNo());
		setGatewayParameterValue("total_fee", (int) (getOrder()
				.getOrderAmount() * 100));
		setGatewayParameterValue("spbill_create_ip", spbill_create_ip);
		setGatewayParameterValue("notify_url", getMerchant().getNotifyUrl()
				.toString());
		setGatewayParameterValue("trade_type", trade_type);
		setGatewayParameterValue("sign", getSign()); // 签名需要在最后设置，以免缺少参数。
	}

	private String getSign() throws Exception {
		// TODO Auto-generated method stub
		StringBuilder signBuilder = new StringBuilder();
		getSortedGatewayParameter().forEach((key, val) -> {
			if (!Utility.isBlankOrEmpty(val) && !key.equalsIgnoreCase("sign")) {
				signBuilder.append(String.format("%s=%s&", key, val));
			}
		});
		signBuilder.append(String.format("key=%s", getMerchant().getKey()));
		return Utility.getMD5(signBuilder.toString()).toUpperCase();
	}

}
