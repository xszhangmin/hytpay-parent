package com.hyt.hytpay.gateways;


import com.hyt.hytpay.enums.GatewayParameterRequestMethod;
import com.hyt.hytpay.enums.GatewayTradeType;
import com.hyt.hytpay.enums.GatewayType;
import com.hyt.hytpay.enums.PaymentNotifyMethod;
import com.hyt.hytpay.model.Merchant;
import com.hyt.hytpay.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 支付网关的抽象基类
 * 
 * @author zhangmin
 *
 */
public abstract class GatewayBase {

	/**
	 * 商家数据
	 */
	Merchant merchant;

	/**
	 * 订单数据
	 */
	Order order;

	/**
	 * 编码
	 */
	String charset = "utf-8";

	/**
	 * 支付类型
	 */
	GatewayTradeType gatewayTradeType = GatewayTradeType.None;

	/**
	 * 支付网关的类型
	 */
	GatewayType gatewayType;

	/**
	 * 支付通知的返回方式 目前的支付网关在支付成功后会以Get或Post方式将支付结果返回给商户。
	 * POST方式的返回一般是通过网关服务器发送，这里可能要求商户输出字符标记表示已成功接收到支付结果。
	 * 而另一种是通过GET方式将用户返回到商户的网站，这时如果以POST数据时的方式来处理将会输出标记已成功接收的字符串。
	 * 如果这样用户会感到很奇怪，这时显示支付成功的页面将会更合适。所以可以通过PaymentNotifyMethod属性来判断
	 * 支付结果的发送方式，以决定是应该输出标记已成功接收的字符串还是向用户显示支付成功的页面。
	 * 服务器发送通知时属性为ServerNotify，如果是用户通过浏览器跳转到接收网关通知的页面属性为AutoReturn。
	 */
	PaymentNotifyMethod paymentNotifyMethod;

	/**
	 * 支付网关的Get、Post数据的集合。Get方式传入QueryString的值均为未解码
	 */
	List<GatewayParameter> gatewayParameterData;

	final String formItem = "<input type='hidden' name='%s' value='%s'> ";

	protected GatewayBase() {
		this(new ArrayList<GatewayParameter>());
	}

	protected GatewayBase(List<GatewayParameter> gatewayParameterData) {
		this.gatewayParameterData = gatewayParameterData;
	}

	public Merchant getMerchant() {
		if (merchant == null) {
			merchant = new Merchant();
		}
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Order getOrder() {
		if (order == null) {
			order = new Order();
		}
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public abstract GatewayType getGatewayType();

	public PaymentNotifyMethod getPaymentNotifyMethod() {
		return paymentNotifyMethod;
	}

	public void setPaymentNotifyMethod(PaymentNotifyMethod paymentNotifyMethod) {
		this.paymentNotifyMethod = paymentNotifyMethod;
	}

	public List<GatewayParameter> getGatewayParameterData() {
		return gatewayParameterData;
	}

	public void setGatewayParameterData(
			List<GatewayParameter> gatewayParameterData) {
		this.gatewayParameterData = gatewayParameterData;
	}

	/**
	 * 创建Form HTML代码
	 * 
	 * @param url
	 *            网关的Url
	 * @return
	 */
	protected String getFormHtml(String url) {

		StringBuilder html = new StringBuilder();
		html.append("<body>").append(" \r\n");
		html.append("<form name='Gateway' method='post' action ='" + url + "'>")
				.append(" \r\n");
		for (GatewayParameter item : gatewayParameterData) {
			if (item.requestMethod == GatewayParameterRequestMethod.Post
					|| item.requestMethod == GatewayParameterRequestMethod.Both) {
				html.append(
						String.format(formItem, item.getName(), item.getValue()))
						.append(" \r\n");
			}
		}
		html.append("</form>").append(" \r\n");
		html.append("<script language='javascript' type='text/javascript'>")
				.append(" \r\n");
		html.append("document.Gateway.submit();").append(" \r\n");
		html.append("</script>").append(" \r\n");
		html.append("</body>").append(" \r\n");
		return html.toString();
	}

	/**
	 * 获得按字母升序排序后的网关参数的集合
	 * 
	 * @return
	 */
	protected SortedMap<String, String> getSortedGatewayParameter() {
		SortedMap<String, String> sortedMap = new TreeMap<String, String>();
		for (GatewayParameter item : gatewayParameterData) {
			sortedMap.put(item.getName(), item.getValue());
		}
		return sortedMap;
	}

	/**
	 * 验证订单是否支付成功
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean validateNotify() throws Exception {
		if (checkNotifyData()) {
			return true;
		}

		return false;
	}

	/**
	 * 设置网关的数据
	 * 
	 * 设置的参数存在时，如果参数的值不一致则保存新的参数值。
	 * 
	 * @param gatewayParameterName
	 *            网关的参数名称
	 * @param gatewayParameterValue
	 *            网关的参数值
	 */
	public void setGatewayParameterValue(String gatewayParameterName,
			Object gatewayParameterValue) {
		setGatewayParameterValue(gatewayParameterName, gatewayParameterValue,
				GatewayParameterRequestMethod.Both);
	}

	/**
	 * 设置网关的数据
	 * 
	 * 设置的参数存在时，如果参数的值不一致则保存新的参数值。
	 * 
	 * @param gatewayParameterName
	 *            网关的参数名称
	 * @param gatewayParameterValue
	 *            网关的参数值
	 */
	public void setGatewayParameterValue(String gatewayParameterName,
			String gatewayParameterValue) {
		setGatewayParameterValue(gatewayParameterName, gatewayParameterValue,
				GatewayParameterRequestMethod.Both);
	}

	/**
	 * 设置网关的数据
	 * 
	 * 当设置的参数存在时，如果参数的值不一致则修改为新的参数值。
	 * 
	 * @param gatewayParameterName
	 *            网关的参数名称
	 * @param gatewayParameterValue
	 *            网关的参数值
	 * @param gatewayParameterRequestMethod
	 *            网关的参数的请求方法的类型
	 */
	public void setGatewayParameterValue(String gatewayParameterName,
			Object gatewayParameterValue,
			GatewayParameterRequestMethod gatewayParameterRequestMethod) {
		setGatewayParameterValue(gatewayParameterName,
				gatewayParameterValue.toString(), gatewayParameterRequestMethod);
	}

	/**
	 * 设置网关的数据
	 * 
	 * 当设置的参数存在时，如果参数的值不一致则修改为新的参数值。
	 * 
	 * @param gatewayParameterName
	 *            网关的参数名称
	 * @param gatewayParameterValue
	 *            网关的参数值
	 * @param gatewayParameterRequestMethod
	 *            网关的参数的请求方法的类型
	 */
	public void setGatewayParameterValue(String gatewayParameterName,
			String gatewayParameterValue,
			GatewayParameterRequestMethod gatewayParameterRequestMethod) {
		GatewayParameter existsParam = gatewayParameterData.stream()
				.filter(p -> p.name.equals(gatewayParameterName)).findFirst()
				.orElse(null);

		if (existsParam == null) {
			GatewayParameter param = new GatewayParameter(gatewayParameterName,
					gatewayParameterValue, gatewayParameterRequestMethod);
			gatewayParameterData.add(param);
		} else {
			if (existsParam.getValue().equals(gatewayParameterValue)) {
				if (existsParam.getRequestMethod() != gatewayParameterRequestMethod) {
					existsParam
							.setRequestMethod(GatewayParameterRequestMethod.Both);
				}

			} else {
				existsParam.setRequestMethod(gatewayParameterRequestMethod);
				existsParam.setValue(gatewayParameterValue);
			}
		}
	}

	/**
	 * 获得网关的参数值。没有参数值时返回空字符串，Get方式的值均为未解码。
	 * 
	 * @param gatewayParameterName
	 * @return
	 */
	public String getGatewayParameterValue(String gatewayParameterName) {
		return getGatewayParameterValue(gatewayParameterName,
				GatewayParameterRequestMethod.Both);
	}

	/**
	 * 获得网关的参数值。没有参数值时返回空字符串，Get方式的值均为未解码。
	 * 
	 * @param gatewayParameterName
	 *            网关的参数名称
	 * @param gatewayParameterRequestMethod
	 *            网关的数据的请求方法的类型
	 * @return
	 */
	public String getGatewayParameterValue(String gatewayParameterName,
			GatewayParameterRequestMethod gatewayParameterRequestMethod) {
		GatewayParameter parameter = gatewayParameterData
				.stream()
				.filter(p -> p.getName().equals(gatewayParameterName)
						&& (gatewayParameterRequestMethod.getCode() == 3 ? true
								: p.getRequestMethod() == gatewayParameterRequestMethod))
				.findFirst().orElse(null);
		if (parameter != null) {
			return parameter.getValue();
		}
		return "";
	}

	/**
	 * 检验网关返回的通知，确认订单是否支付成功
	 * 
	 * @return
	 * @throws AlipayApiException
	 */
	protected abstract boolean checkNotifyData() throws Exception;

	/**
	 * 当接收到支付网关通知并验证无误时按照支付网关要求格式输出表示成功接收到网关通知的字符串
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract void writeSucceedFlag() throws Exception;

	public GatewayTradeType getGatewayTradeType() {
		return gatewayTradeType;
	}

	public void setGatewayTradeType(GatewayTradeType gatewayTradeType) {
		this.gatewayTradeType = gatewayTradeType;
	}
}