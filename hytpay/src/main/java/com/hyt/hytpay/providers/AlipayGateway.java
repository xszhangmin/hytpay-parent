package com.hyt.hytpay.providers;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.hyt.hytpay.enums.GatewayType;
import com.hyt.hytpay.enums.PaymentNotifyMethod;
import com.hyt.hytpay.gateways.GatewayBase;
import com.hyt.hytpay.gateways.GatewayParameter;
import com.hyt.hytpay.interfaces.*;
import com.hyt.hytpay.models.Refund;
import com.hyt.hytpay.utils.Utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlipayGateway extends GatewayBase implements PaymentForm,
		WapPaymentUrl, AppParams, QueryNow, RefundReq {

	final String payGatewayUrl = "https://mapi.alipay.com/gateway.do";
//	final String openapiGatewayUrl = "https://openapi.alipay.com/gateway.do";
	final String openapiGatewayUrl = "https://openapi.alipaydev.com/gateway.do";//支付宝沙箱测试网关，正式环境需要换掉
	final String emailRegexString = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
	String pageEncoding = "";

	/**
	 * 初始化支付宝网关
	 */
	public AlipayGateway() {
		pageEncoding = getCharset();
	}

	/**
	 * 初始化支付宝网关
	 * 
	 * @param gatewayParameterData
	 *            网关通知的数据集合
	 */
	public AlipayGateway(List<GatewayParameter> gatewayParameterData) {
		super(gatewayParameterData);
		pageEncoding = getCharset();
	}

	@Override
	public GatewayType getGatewayType() {
		return GatewayType.Alipay;
	}

	@Override
	public String buildPaymentForm() throws Exception {
		// TODO Auto-generated method stub
		AlipayClient alipayClient = getAopClient(); // 获得初始化的AlipayClient

		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();// 创建API对应的request
		alipayRequest.setReturnUrl(getMerchant().getReturnUrl().toString());
		alipayRequest.setNotifyUrl(getMerchant().getNotifyUrl().toString());// 在公共参数中设置回跳和通知地址

		AlipayTradePagePayModel model = new AlipayTradePagePayModel();
		model.setSubject(getOrder().getSubject());
		model.setOutTradeNo(getOrder().getOrderNo());
		model.setTimeoutExpress("30m");
		model.setTotalAmount(String.valueOf(getOrder().getOrderAmount()));
		model.setProductCode("FAST_INSTANT_TRADE_PAY");
		alipayRequest.setBizModel(model);

		return alipayClient.pageExecute(alipayRequest).getBody(); // 调用SDK生成表单
	}

	@Override
	public String buildWapPaymentUrl(Map<String, String> map)
			throws AlipayApiException {
		// TODO Auto-generated method stub
		AlipayClient alipayClient = getAopClient(); // 获得初始化的AlipayClient

		AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();// 创建API对应的request
		alipayRequest.setReturnUrl(getMerchant().getReturnUrl().toString());
		alipayRequest.setNotifyUrl(getMerchant().getNotifyUrl().toString());// 在公共参数中设置回跳和通知地址

		AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
		model.setSubject(getOrder().getSubject());
		model.setOutTradeNo(getOrder().getOrderNo());
		model.setTimeoutExpress("30m");
		model.setTotalAmount(String.valueOf(getOrder().getOrderAmount()));
		model.setProductCode("QUICK_WAP_PAY");
		alipayRequest.setBizModel(model);

		return alipayClient.pageExecute(alipayRequest).getBody(); // 调用SDK生成表单
	}

	@Override
	public Map<String, String> buildPayParams() throws AlipayApiException {
		// TODO Auto-generated method stub
		AlipayClient alipayClient = getAopClient(); // 获得初始化的AlipayClient

		AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();// 创建API对应的request
		alipayRequest.setReturnUrl(getMerchant().getReturnUrl().toString());
		alipayRequest.setNotifyUrl(getMerchant().getNotifyUrl().toString());// 在公共参数中设置回跳和通知地址

		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setSubject(getOrder().getSubject());
		model.setOutTradeNo(getOrder().getOrderNo());
		model.setTimeoutExpress("30m");
		model.setTotalAmount(String.valueOf(getOrder().getOrderAmount()));
		model.setProductCode("QUICK_MSECURITY_PAY");
		alipayRequest.setBizModel(model);

		Map<String, String> resParam = new HashMap<String, String>();
		resParam.put("body", alipayClient.pageExecute(alipayRequest).getBody());
		return resParam;
	}

	@Override
	public boolean queryNow() throws AlipayApiException {
		// TODO Auto-generated method stub
		AlipayClient alipayClient = getAopClient(); // 获得初始化的AlipayClient

		AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();// 创建API对应的request类

		AlipayTradeQueryModel model = new AlipayTradeQueryModel();
		model.setOutTradeNo(getOrder().getOrderNo());
		alipayRequest.setBizModel(model);

		AlipayTradeQueryResponse response = alipayClient.execute(alipayRequest);// 通过alipayClient调用API，获得对应的response类

		if (((response.getTradeStatus().equalsIgnoreCase("TRADE_FINISHED") || response
				.getTradeStatus().equalsIgnoreCase("TRADE_SUCCESS")))) {
			double orderAmount = Double.parseDouble(response.getTotalAmount());
			if (getOrder().getOrderAmount() == orderAmount
					&& getOrder().getOrderNo().equals(response.getOutTradeNo())) {
				return true;
			}
			return false;
		}
		return false;

	}

	@Override
	public Refund buildRefund(Refund refund) throws AlipayApiException {
		// TODO Auto-generated method stub
		AlipayClient alipayClient = getAopClient();
		AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
		AlipayTradeRefundModel model = new AlipayTradeRefundModel();
		model.setOutTradeNo(refund.getOrderNo());
		if (!Utility.isBlankOrEmpty(refund.getTradeNo())) {
			model.setTradeNo(refund.getTradeNo());
		}
		model.setOutRequestNo(refund.getRefoundNo());
		model.setRefundAmount(String.valueOf(refund.getOrderAmount()));
		if (!Utility.isBlankOrEmpty(refund.getRefundDesc())) {
			model.setRefundReason(refund.getRefundDesc());
		}
		alipayRequest.setBizModel(model);
		AlipayTradeRefundResponse response = alipayClient
				.execute(alipayRequest);
		if (response.getCode() == "10000") {
			refund.setTradeNo(response.getTradeNo());
			refund.setRefoundStatus(true);
		}
		return refund;
	}

	@Override
	public Refund buildRefundQuery(Refund refund) throws AlipayApiException {
		// TODO Auto-generated method stub
		AlipayClient alipayClient = getAopClient();
		AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();
		AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
		model.setOutTradeNo(refund.getOrderNo());
		if (!Utility.isBlankOrEmpty(refund.getTradeNo())) {
			model.setTradeNo(refund.getTradeNo());
		}
		model.setOutRequestNo(refund.getRefoundNo());
		alipayRequest.setBizModel(model);
		AlipayTradeFastpayRefundQueryResponse response = alipayClient
				.execute(alipayRequest);
		if (response.getCode() == "10000"
				&& !Utility.isBlankOrEmpty(response.getRefundAmount())) {
			double refundAmount = Double
					.parseDouble(response.getRefundAmount());
			refund.setRefundAmount(refundAmount);
			refund.setTradeNo(response.getTradeNo());
			refund.setRefoundStatus(true);
		}
		return refund;
	}

	@Override
	protected boolean checkNotifyData() throws AlipayApiException {
		// TODO Auto-generated method stub
		if (validateAlipayNotifyRSASign()) {
			return validateTrade();
		}
		return false;
	}

	@Override
	public void writeSucceedFlag() throws IOException {
		// TODO Auto-generated method stub
		if (getPaymentNotifyMethod() == PaymentNotifyMethod.ServerNotify) {
			Utility.getHttpServletResponse().getWriter().write("success");
		}
	}

	public AlipayClient getAopClient() {
		return new DefaultAlipayClient(openapiGatewayUrl, getMerchant()
				.getAppId(), getMerchant().getPrivateKeyPem(), "json",
				getCharset(), getMerchant().getPublicKeyPem(), "RSA2");
	}

	/**
	 * 验证支付宝通知的签名
	 * 
	 * @return
	 * @throws AlipayApiException
	 */
	private boolean validateAlipayNotifyRSASign() throws AlipayApiException {
		// TODO Auto-generated method stub
		boolean checkSign = AlipaySignature.rsaCheckV1(
				getSortedGatewayParameter(), getMerchant().getPublicKeyPem(),
				getCharset());
		if (checkSign) {
			return true;
		}
		return false;
	}

	/**
	 * 验证支付状态
	 * 
	 * @return
	 */
	private boolean validateTrade() {
		// TODO Auto-generated method stub
		// 支付状态是否为成功。TRADE_FINISHED（普通即时到账的交易成功状态，TRADE_SUCCESS（开通了高级即时到账或机票分销产品后的交易成功状态）
		if (getGatewayParameterValue("trade_status").equalsIgnoreCase(
				"TRADE_FINISHED")
				|| getGatewayParameterValue("trade_status").equalsIgnoreCase(
						"TRADE_SUCCESS")) {
			String orderAmount = getGatewayParameterValue("total_amount");
			orderAmount = Utility.isBlankOrEmpty(orderAmount) ? getGatewayParameterValue("total_fee")
					: orderAmount;
			getOrder().setOrderAmount(Double.parseDouble(orderAmount));
			getOrder().setOrderNo(getGatewayParameterValue("out_trade_no"));
			getOrder().setTradeNo(getGatewayParameterValue("trade_no"));
			return true;
		}
		return false;
	}

}
