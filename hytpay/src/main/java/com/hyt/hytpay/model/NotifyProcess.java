package com.hyt.hytpay.model;

import com.hyt.hytpay.enums.GatewayParameterRequestMethod;
import com.hyt.hytpay.gateways.GatewayBase;
import com.hyt.hytpay.gateways.GatewayParameter;
import com.hyt.hytpay.providers.AlipayGateway;
import com.hyt.hytpay.providers.NullGateway;
import com.hyt.hytpay.providers.UnionPayGateway;
import com.hyt.hytpay.providers.WeChatPayGataway;
import com.hyt.hytpay.utils.Utility;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 网关通知的处理类，通过对返回数据的分析识别网关类型
 * 
 * @author zhangmin
 *
 */
public class NotifyProcess {

	// 需要验证的参数名称数组，用于识别不同的网关类型。
	// 检查是否在发回的数据中，需要保证参数名称跟其他各个网关验证的参数名称不重复。
	// 建议使用网关中返回的不为空的参数名，并使用尽可能多的参数名。
	static String[] alipayGatewayVerifyParmaNames = { "notify_type",
			"notify_id", "notify_time", "sign", "sign_type" };
	static String[] alipayWapGatewayVerifyParmaNames = { "auth_app_id",
			"method", "seller_id", "sign", "sign_type" };
	static String[] weixinpayGatewayVerifyParmaNames = { "return_code",
			"appid", "mch_id", "nonce_str", "result_code" };
	static String[] unionpayGatewayVerifyParmaNames = { "respMsg", "merId",
			"respCode", "orderId", "queryId" };

	/**
	 * 验证网关的类型
	 * 
	 * @return
	 * @throws Exception
	 */
	public static GatewayBase getGateway() throws Exception {
		List<GatewayParameter> gatewayParameterData = readNotifyData();
		if (isAlipayGateway(gatewayParameterData)) {
			return new AlipayGateway(gatewayParameterData);
		}

		if (isWeixinpayGateway(gatewayParameterData)) {
			return new WeChatPayGataway(gatewayParameterData);
		}

		if (isUnionPayGateway(gatewayParameterData)) {
			return new UnionPayGateway(gatewayParameterData);
		}

		return new NullGateway(gatewayParameterData);
	}

	/**
	 * 是否是支付宝网关
	 * 
	 * @param gatewayParameterData
	 * @return
	 */
	private static boolean isAlipayGateway(
			List<GatewayParameter> gatewayParameterData) {
		// TODO Auto-generated method stub
		return existParameter(alipayGatewayVerifyParmaNames,
				gatewayParameterData)
				|| existParameter(alipayWapGatewayVerifyParmaNames,
						gatewayParameterData);
	}

	/**
	 * 是否是微信支付网关
	 * 
	 * @param gatewayParameterData
	 * @return
	 */
	private static boolean isWeixinpayGateway(
			List<GatewayParameter> gatewayParameterData) {
		// TODO Auto-generated method stub
		return existParameter(weixinpayGatewayVerifyParmaNames,
				gatewayParameterData);
	}

	/**
	 * 是否是银联支付网关
	 * 
	 * @param gatewayParameterData
	 * @return
	 */
	private static boolean isUnionPayGateway(
			List<GatewayParameter> gatewayParameterData) {
		// TODO Auto-generated method stub
		return existParameter(unionpayGatewayVerifyParmaNames,
				gatewayParameterData);
	}

	/**
	 * 网关参数数据项中是否存在指定的所有参数名
	 * 
	 * @param parmaName
	 *            参数名数组
	 * @param gatewayParameterData
	 *            数据项
	 * @return
	 */
	private static boolean existParameter(String[] parmaName,
			List<GatewayParameter> gatewayParameterData) {
		// TODO Auto-generated method stub
		int compareCount = 0;
		for (String item : parmaName) {
			GatewayParameter existsParam = gatewayParameterData.stream()
					.filter(p -> p.getName().equals(item)).findFirst()
					.orElse(null);

			if (existsParam != null) {
				compareCount++;
			}
		}

		if (compareCount == parmaName.length) {
			return true;
		}

		return false;
	}

	/**
	 * 读取网关发回的数据。Get方式传入QueryString的值均为未解码
	 * 
	 * @return
	 * @throws Exception
	 */
	private static List<GatewayParameter> readNotifyData() throws Exception {
		// TODO Auto-generated method stub
		List<GatewayParameter> gatewayParameters = new ArrayList<GatewayParameter>();
		readQueryString(gatewayParameters);
		readForm(gatewayParameters);
		readWeixinpayXml(gatewayParameters);
		return gatewayParameters;
	}

	/**
	 * 设置网关的数据
	 * 
	 * @param gatewayParameterList
	 *            保存网关参数的集合
	 * @param gatewayParameterName
	 *            网关的参数名称
	 * @param gatewayParameterValue
	 *            网关的参数值
	 * @param gatewayParameterRequestMethod
	 *            网关的参数的请求方式的类型
	 */
	private static void setGatewayParameterValue(
			List<GatewayParameter> gatewayParameterList,
			String gatewayParameterName, String gatewayParameterValue,
			GatewayParameterRequestMethod gatewayParameterRequestMethod) {

		GatewayParameter existsParam = gatewayParameterList.stream()
				.filter(p -> p.getName().equals(gatewayParameterName))
				.findFirst().orElse(null);

		if (existsParam == null) {
			GatewayParameter param = new GatewayParameter(gatewayParameterName,
					gatewayParameterValue, gatewayParameterRequestMethod);
			gatewayParameterList.add(param);
		} else {
			if (existsParam.getValue().equals(gatewayParameterValue)) {
				existsParam
						.setRequestMethod(GatewayParameterRequestMethod.Both);
			} else {
				existsParam.setRequestMethod(gatewayParameterRequestMethod);
				existsParam.setValue(gatewayParameterValue);
			}
		}
	}

	/**
	 * 读取GET提交的查询字符串中的数据
	 * 
	 * @param gatewayParameterList
	 */
	private static void readQueryString(
			List<GatewayParameter> gatewayParameterList) {
		// TODO Auto-generated method stub
		HttpServletRequest request = Utility.getHttpServletRequest();
		String queryString = request.getQueryString();
		String[] kvs = queryString.split("&");
		for (String kv : kvs) {
			String[] tmp = kv.split("=");
			if (tmp.length >= 2) {
				setGatewayParameterValue(gatewayParameterList, tmp[0], tmp[1],
						GatewayParameterRequestMethod.Get);
			}
		}
	}

	/**
	 * 读取POST提交的Form表单的数据
	 * 
	 * @param gatewayParameterList
	 */
	private static void readForm(List<GatewayParameter> gatewayParameterList) {
		// TODO Auto-generated method stub
		HttpServletRequest request = Utility.getHttpServletRequest();
		Map<String, String[]> params = request.getParameterMap();
		for (String key : params.keySet()) {
			String[] values = params.get(key);
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				setGatewayParameterValue(gatewayParameterList, key, value,
						GatewayParameterRequestMethod.Post);
			}
		}
	}

	/**
	 * 读取微信支付的通知
	 * 
	 * @param gatewayParameterList
	 * @throws Exception
	 */
	private static void readWeixinpayXml(
			List<GatewayParameter> gatewayParameterList) throws Exception {
		// TODO Auto-generated method stub

		if (isWeixinpayNotify()) {
			String resultXml = new String(IOUtils.toByteArray(Utility
					.getHttpServletRequest().getInputStream()));

			Utility.xmlToMap(resultXml).forEach(
					(key, val) -> {
						setGatewayParameterValue(gatewayParameterList, key,
								val, GatewayParameterRequestMethod.Post);
					});
		}
	}

	/**
	 * 是否是微信支付的通知
	 * 
	 * @return
	 */
	private static boolean isWeixinpayNotify() {
		HttpServletRequest request = Utility.getHttpServletRequest();
		String requestType = request.getMethod();
		String contentType = request.getContentType();
		String userAgent = request.getHeader("User-Agent");
		if (requestType.equalsIgnoreCase("POST")
				&& contentType.equalsIgnoreCase("text/xml")
				&& userAgent.equalsIgnoreCase("Mozilla/4.0")) {
			return true;
		}
		return false;
	}
}
