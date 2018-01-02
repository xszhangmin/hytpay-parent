package com.hyt.hytpay.providers;

import com.hyt.hytpay.enums.GatewayType;
import com.hyt.hytpay.enums.PaymentNotifyMethod;
import com.hyt.hytpay.gateways.GatewayBase;
import com.hyt.hytpay.gateways.GatewayParameter;
import com.hyt.hytpay.interfaces.*;
import com.hyt.hytpay.model.Refund;
import com.hyt.hytpay.utils.Utility;
import com.unionpay.acp.sdk.AcpService;
import com.unionpay.acp.sdk.LogUtil;
import com.unionpay.acp.sdk.SDKConfig;
import com.unionpay.acp.sdk.SDKConstants;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 银联网关
 * 
 * @author zhangmin
 *
 */
public class UnionPayGateway extends GatewayBase implements PaymentForm,
		WapPaymentForm, AppParams, QueryNow, RefundReq {

	/**
	 * 初始化中国银联网关
	 */
	public UnionPayGateway() {
	}

	/**
	 * 初始化中国银联网关
	 * 
	 * @param gatewayParameterData
	 *            网关通知的数据集合
	 */
	public UnionPayGateway(List<GatewayParameter> gatewayParameterData) {
		super(gatewayParameterData);
	}

	@Override
	public GatewayType getGatewayType() {
		return GatewayType.UnionPay;
	}

	@Override
	public String buildPaymentForm() throws Exception {
		// TODO Auto-generated method stub
		Map<String, String> requestData = new HashMap<String, String>();

		/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
		requestData.put("version", "5.0.0"); // 版本号，全渠道默认值
		requestData.put("encoding", getCharset()); // 字符集编码，可以使用UTF-8,GBK两种方式
		requestData.put("signMethod", "01"); // 签名方法
												// SDKConfig.getConfig().getSignMethod()
		requestData.put("txnType", "01"); // 交易类型 ，01：消费
		requestData.put("txnSubType", "01"); // 交易子类型， 01：自助消费
		requestData.put("bizType", "000201"); // 业务类型，B2C网关支付，手机wap支付
		requestData.put("channelType", "07"); // 渠道类型，这个字段区分B2C网关支付和手机wap支付；07：PC,平板
												// 08：手机

		/*** 商户接入参数 ***/
		requestData.put("merId", getMerchant().getPartner()); // 商户号码，请改成自己申请的正式商户号或者open上注册得来的777测试商户号
		requestData.put("accessType", "0"); // 接入类型，0：直连商户
		requestData.put("orderId", getOrder().getOrderNo()); // 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
		requestData.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss")
				.format(getOrder().getPaymentDate())); // 订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		requestData.put("currencyCode", "156"); // 交易币种（境内商户一般是156 人民币）
		double txnAmt = getOrder().getOrderAmount() * 100;
		requestData.put("txnAmt", String.valueOf((int) txnAmt)); // 交易金额，单位分，不要带小数点
		// requestData.put("reqReserved", "透传字段");
		// //请求方保留域，如需使用请启用即可；透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节。出现&={}[]符号时可能导致查询接口应答报文解析失败，建议尽量只传字母数字并使用|分割，或者可以最外层做一次base64编码(base64编码之后出现的等号不会导致解析失败可以不用管)。

		// 前台通知地址 （需设置为外网能访问 http https均可），支付成功后的页面 点击“返回商户”按钮的时候将异步通知报文post到该地址
		// 如果想要实现过几秒中自动跳转回商户页面权限，需联系银联业务申请开通自动返回商户权限
		// 异步通知参数详见open.unionpay.com帮助中心 下载 产品接口规范 网关支付产品接口规范 消费交易 商户通知
		requestData.put("frontUrl", getMerchant().getReturnUrl().toString());

		// 后台通知地址（需设置为【外网】能访问 http
		// https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，失败的交易银联不会发送后台通知
		// 后台通知参数详见open.unionpay.com帮助中心 下载 产品接口规范 网关支付产品接口规范 消费交易 商户通知
		// 注意:1.需设置为外网能访问，否则收不到通知 2.http https均可 3.收单后台通知后需要10秒内返回http200或302状态码
		// 4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200，那么银联会间隔一段时间再次发送。总共发送5次，每次的间隔时间为0,1,2,4分钟。
		// 5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d
		// 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
		requestData.put("backUrl", getMerchant().getNotifyUrl().toString());

		// 订单超时时间。
		// 超过此时间后，除网银交易外，其他交易银联系统会拒绝受理，提示超时。
		// 跳转银行网银交易如果超时后交易成功，会自动退款，大约5个工作日金额返还到持卡人账户。
		// 此时间建议取支付时的北京时间加15分钟。
		// 超过超时时间调查询接口应答origRespCode不是A6或者00的就可以判断为失败。
		requestData.put("payTimeout", new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date().getTime() + 15 * 60 * 1000));

		// ////////////////////////////////////////////////
		//
		// 报文中特殊用法请查看 PCwap网关跳转支付特殊用法.txt
		//
		// ////////////////////////////////////////////////

		/** 请求参数设置完毕，以下对请求参数进行签名并生成html表单，将表单写入浏览器跳转打开银联页面 **/
		Map<String, String> submitFromData = AcpService.sign(requestData,
				getCharset()); // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

		String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl(); // 获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.frontTransUrl
		String html = AcpService.createAutoFormHtml(requestFrontUrl,
				submitFromData, getCharset()); // 生成自动跳转的Html表单

		LogUtil.writeLog("打印请求HTML，此为请求报文，为联调排查问题的依据：" + html);
		// 将生成的html写到浏览器中完成自动跳转打开银联支付页面；这里调用signData之后，将html写到浏览器跳转到银联页面之前均不能对html中的表单项的名称和值进行修改，如果修改会导致验签不通过
		return html;
	}

	@Override
	public String buildWapPaymentForm() throws Exception {
		// TODO Auto-generated method stub
		return buildPaymentForm();
	}

	@Override
	public Map<String, String> buildPayParams() throws Exception {
		// TODO Auto-generated method stub
		// 组装请求报文
		Map<String, String> param = new HashMap<String, String>();
		// 版本号
		param.put("version", "5.0.0");
		// 字符集编码 默认"UTF-8"
		param.put("encoding", "UTF-8");
		// 签名方法 01 RSA
		param.put("signMethod", "01");
		// 交易类型 01-消费
		param.put("txnType", "01");
		// 交易子类型 01:自助消费 02:订购 03:分期付款
		param.put("txnSubType", "01");
		// 业务类型
		param.put("bizType", "000201");
		// 渠道类型，07-PC，08-手机
		param.put("channelType", "08");
		// 前台通知地址 ，控件接入方式无作用
		param.put("frontUrl", getMerchant().getReturnUrl().toString());
		// 后台通知地址
		param.put("backUrl", getMerchant().getNotifyUrl().toString());
		// 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
		param.put("accessType", "0");
		// 商户号码，请改成自己的商户号
		param.put("merId", getMerchant().getPartner());
		// 商户订单号，8-40位数字字母
		param.put("orderId", getOrder().getOrderNo());
		// 订单发送时间，取系统时间
		param.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss")
				.format(getOrder().getPaymentDate()));
		// 交易金额，单位分
		double txnAmt = getOrder().getOrderAmount() * 100;
		param.put("txnAmt", String.valueOf((int) txnAmt));
		// 交易币种
		param.put("currencyCode", "156");
		// 请求方保留域，透传字段，查询、通知、对账文件中均会原样出现
		// param.Add("reqReserved", "透传信息");
		// 订单描述，可不上送，上送时控件中会显示该信息
		// param.Add("orderDesc", "订单描述");

		param = AcpService.sign(param, getCharset());

		Map<String, String> resmap = AcpService.post(param, SDKConfig
				.getConfig().getAppRequestUrl(), getCharset());
		Map<String, String> resParam = new HashMap<String, String>();
		resParam.put("tn", resmap.get("tn"));
		return resParam;
	}

	@Override
	public boolean queryNow() throws Exception {
		// TODO Auto-generated method stub
		Map<String, String> data = new HashMap<String, String>();

		/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
		data.put("version", "5.0.0"); // 版本号，全渠道默认值
		data.put("encoding", getCharset()); // 字符集编码，可以使用UTF-8,GBK两种方式
		data.put("signMethod", "01"); // 签名方法
										// SDKConfig.getConfig().getSignMethod()
		data.put("txnType", "00"); // 交易类型 00-默认
		data.put("txnSubType", "00"); // 交易子类型 默认00
		data.put("bizType", "000201"); // 业务类型 B2C网关支付，手机wap支付

		/*** 商户接入参数 ***/
		data.put("merId", getMerchant().getPartner()); // 商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		data.put("accessType", "0"); // 接入类型，商户接入固定填0，不需修改

		/*** 要调通交易以下字段必须修改 ***/
		data.put("orderId", getOrder().getOrderNo()); // ****商户订单号，每次发交易测试需修改为被查询的交易的订单号
		data.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss")
				.format(getOrder().getPaymentDate())); // ****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间

		/** 请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文-------------> **/

		Map<String, String> reqData = AcpService.sign(data, getCharset());// 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

		String url = SDKConfig.getConfig().getSingleQueryUrl();// 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的
																// acpsdk.singleQueryUrl
		// 这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		Map<String, String> rspData = AcpService.post(reqData, url,
				getCharset());

		/** 对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考-------------> **/
		// 应答码规范参考open.unionpay.com帮助中心 下载 产品接口规范 《平台接入接口规范-第5部分-附录》
		if (!rspData.isEmpty()) {
			if (AcpService.validate(rspData, getCharset())) {
				LogUtil.writeLog("验证签名成功");
				if ("00".equals(rspData.get("respCode"))) {// 如果查询交易成功
					// 处理被查询交易的应答码逻辑
					String origRespCode = rspData.get("origRespCode");
					if ("00".equals(origRespCode)) {
						// 交易成功，更新商户订单状态
						// TODO
						return true;
					} else if ("03".equals(origRespCode)
							|| "04".equals(origRespCode)
							|| "05".equals(origRespCode)) {
						// 需再次发起交易状态查询交易
						// TODO
						return false;
					} else {
						// 其他应答码为失败请排查原因
						// TODO
						return false;
					}
				} else {// 查询交易本身失败，或者未查到原交易，检查查询交易报文要素
						// TODO
					return false;
				}
			} else {
				LogUtil.writeErrorLog("验证签名失败");
				// TODO 检查验证签名失败的原因
				return false;
			}
		} else {
			// 未返回正确的http状态
			LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
			return false;
		}
	}

	@Override
	public Refund buildRefund(Refund refund) {
		// TODO Auto-generated method stub
		Map<String, String> data = new HashMap<String, String>();

		/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
		data.put("version", "5.0.0"); // 版本号
		data.put("encoding", getCharset()); // 字符集编码 可以使用UTF-8,GBK两种方式
		data.put("signMethod", SDKConfig.getConfig().getSignMethod()); // 签名方法
		data.put("txnType", "04"); // 交易类型 04-退货
		data.put("txnSubType", "00"); // 交易子类型 默认00
		data.put("bizType", "000201"); // 业务类型 B2C网关支付，手机wap支付
		data.put("channelType", "07"); // 渠道类型，07-PC，08-手机

		/*** 商户接入参数 ***/
		data.put("merId", getMerchant().getPartner()); // 商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		data.put("accessType", "0"); // 接入类型，商户接入固定填0，不需修改
		data.put("orderId", refund.getRefoundNo()); // 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费
		data.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss")
				.format(refund.getPaymentDate())); // 订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
		data.put("currencyCode", "156"); // 交易币种（境内商户一般是156 人民币）
		double txnAmt = refund.getRefundAmount() * 100;
		data.put("txnAmt", String.valueOf((int) txnAmt)); // ****退货金额，单位分，不要带小数点。退货金额小于等于原消费金额，当小于的时候可以多次退货至退货累计金额等于原消费金额
		// data.put("reqReserved", "透传信息");
		// //请求方保留域，如需使用请启用即可；透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节。出现&={}[]符号时可能导致查询接口应答报文解析失败，建议尽量只传字母数字并使用|分割，或者可以最外层做一次base64编码(base64编码之后出现的等号不会导致解析失败可以不用管)。
		data.put("backUrl", ""); // 后台通知地址，后台通知参数详见open.unionpay.com帮助中心
									// 下载 产品接口规范 网关支付产品接口规范 退货交易
									// 商户通知,其他说明同消费交易的后台通知

		/*** 要调通交易以下字段必须修改 ***/
		data.put("origQryId", refund.getTradeNo()); // ****原消费交易返回的的queryId，可以从消费交易后台通知接口中或者交易状态查询接口中获取

		/** 请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文-------------> **/
		Map<String, String> reqData = AcpService.sign(data, getCharset());// 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String url = SDKConfig.getConfig().getBackRequestUrl();// 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的
																// acpsdk.backTransUrl

		Map<String, String> rspData = AcpService.post(reqData, url,
				getCharset());// 这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

		/** 对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考-------------> **/
		// 应答码规范参考open.unionpay.com帮助中心 下载 产品接口规范 《平台接入接口规范-第5部分-附录》
		if (!rspData.isEmpty()) {
			if (AcpService.validate(rspData, getCharset())) {
				LogUtil.writeLog("验证签名成功");
				String respCode = rspData.get("respCode");
				if ("00".equals(respCode)) {
					// 交易已受理，等待接收后台通知更新订单状态,也可以主动发起 查询交易确定交易状态。
					// TODO
					refund.setTradeNo(rspData.get("origQryId"));
					refund.setRefoundNo(rspData.get("queryId"));
					refund.setRefoundStatus(true);
				} else if ("03".equals(respCode) || "04".equals(respCode)
						|| "05".equals(respCode)) {
					// 后续需发起交易状态查询交易确定交易状态
					// TODO
				} else {
					// 其他应答码为失败请排查原因
					// TODO
				}
			} else {
				LogUtil.writeErrorLog("验证签名失败");
				// TODO 检查验证签名失败的原因
			}
		} else {
			// 未返回正确的http状态
			LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
		}

		return refund;

	}

	@Override
	public Refund buildRefundQuery(Refund refund) {
		// TODO Auto-generated method stub
		Map<String, String> data = new HashMap<String, String>();

		/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
		data.put("version", "5.0.0"); // 版本号，全渠道默认值
		data.put("encoding", getCharset()); // 字符集编码，可以使用UTF-8,GBK两种方式
		data.put("signMethod", "01"); // 签名方法
										// SDKConfig.getConfig().getSignMethod()
		data.put("txnType", "00"); // 交易类型 00-默认
		data.put("txnSubType", "00"); // 交易子类型 默认00
		data.put("bizType", "000201"); // 业务类型 B2C网关支付，手机wap支付

		/*** 商户接入参数 ***/
		data.put("merId", getMerchant().getPartner()); // 商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		data.put("accessType", "0"); // 接入类型，商户接入固定填0，不需修改

		/*** 要调通交易以下字段必须修改 ***/
		data.put("orderId", refund.getRefoundNo()); // 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则，重新产生，不同于原消费
		data.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss")
				.format(refund.getPaymentDate())); // 订单发送时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效

		/** 请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文-------------> **/

		Map<String, String> reqData = AcpService.sign(data, getCharset());// 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

		String url = SDKConfig.getConfig().getSingleQueryUrl();// 交易请求url从配置文件读取对应属性文件acp_sdk.properties中的
																// acpsdk.singleQueryUrl
		// 这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		Map<String, String> rspData = AcpService.post(reqData, url,
				getCharset());

		/** 对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考-------------> **/
		// 应答码规范参考open.unionpay.com帮助中心 下载 产品接口规范 《平台接入接口规范-第5部分-附录》
		if (!rspData.isEmpty()) {
			if (AcpService.validate(rspData, getCharset())) {
				LogUtil.writeLog("验证签名成功");
				if ("00".equals(rspData.get("respCode"))) {// 如果查询交易成功
					// 处理被查询交易的应答码逻辑
					String origRespCode = rspData.get("origRespCode");
					if ("00".equals(origRespCode)) {
						// 交易成功，更新商户订单状态
						// TODO
						refund.setRefoundNo(rspData.get("queryId"));
						refund.setRefoundStatus(true);
					} else if ("03".equals(origRespCode)
							|| "04".equals(origRespCode)
							|| "05".equals(origRespCode)) {
						// 需再次发起交易状态查询交易
						// TODO
					} else {
						// 其他应答码为失败请排查原因
						// TODO
					}
				} else {// 查询交易本身失败，或者未查到原交易，检查查询交易报文要素
						// TODO
				}
			} else {
				LogUtil.writeErrorLog("验证签名失败");
				// TODO 检查验证签名失败的原因
			}
		} else {
			// 未返回正确的http状态
			LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
		}
		return refund;
	}

	/**
	 * 获取请求参数中所有的信息 当商户上送frontUrl或backUrl地址中带有参数信息的时候，
	 * 这种方式会将url地址中的参数读到map中，会导多出来这些信息从而致验签失败
	 * ，这个时候可以自行修改过滤掉url中的参数或者使用getAllRequestParamStream方法。
	 * 
	 * @param request
	 * @return
	 */
	public Map<String, String> getAllRequestParam(
			final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				// 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				if (res.get(en) == null || "".equals(res.get(en))) {
					// System.out.println("======为空的字段名===="+en);
					res.remove(en);
				}
			}
		}
		return res;
	}

	/**
	 * 获取请求参数中所有的信息。
	 * 非struts可以改用此方法获取，好处是可以过滤掉request.getParameter方法过滤不掉的url中的参数。
	 * struts可能对某些content
	 * -type会提前读取参数导致从inputstream读不到信息，所以可能用不了这个方法。理论应该可以调整struts配置使不影响，但请自己去研究。
	 * 调用本方法之前不能调用req.getParameter("key");这种方法，否则会导致request取不到输入流。
	 * 
	 * @param request
	 * @return
	 */
	public Map<String, String> getAllRequestParamStream(
			final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		try {
			String notifyStr = new String(IOUtils.toByteArray(request
					.getInputStream()), getCharset());
			LogUtil.writeLog("收到通知报文：" + notifyStr);
			String[] kvs = notifyStr.split("&");
			for (String kv : kvs) {
				String[] tmp = kv.split("=");
				if (tmp.length >= 2) {
					String key = tmp[0];
					String value = URLDecoder.decode(tmp[1], getCharset());
					res.put(key, value);
				}
			}
		} catch (UnsupportedEncodingException e) {
			LogUtil.writeLog("getAllRequestParamStream.UnsupportedEncodingException error: "
					+ e.getClass() + ":" + e.getMessage());
		} catch (IOException e) {
			LogUtil.writeLog("getAllRequestParamStream.IOException error: "
					+ e.getClass() + ":" + e.getMessage());
		}
		return res;
	}

	@Override
	protected boolean checkNotifyData() throws Exception {
		// TODO Auto-generated method stub
		String encoding = Utility.getHttpServletRequest().getParameter(
				SDKConstants.param_encoding);
		// 获取银联通知服务器发送的后台通知参数
		Map<String, String> reqParam = getAllRequestParamStream(Utility
				.getHttpServletRequest());
		LogUtil.printRequestLog(reqParam);

		// 重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
		if (!AcpService.validate(reqParam, encoding)) {
			LogUtil.writeLog("验证签名结果[失败].");
			// 验签失败，需解决验签问题
			return false;
		} else {
			getOrder().setOrderNo(reqParam.getOrDefault("orderId", ""));
			double txnAmt = Double.parseDouble(reqParam.getOrDefault("txnAmt",
					"0.0")) * 0.01;
			getOrder().setOrderAmount(txnAmt);
			getOrder().setTradeNo(reqParam.getOrDefault("queryId", ""));

			LogUtil.writeLog("验证签名结果[成功].");
			// 【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态

			// String orderId = reqParam.get("orderId"); //
			// 获取后台通知的数据，其他字段也可用类似方式获取
			// String respCode = reqParam.get("respCode");
			// 判断respCode=00、A6后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。
			return true;
		}
	}

	@Override
	public void writeSucceedFlag() throws Exception {
		// TODO Auto-generated method stub
		if (getPaymentNotifyMethod() == PaymentNotifyMethod.ServerNotify) {
			Utility.getHttpServletResponse().getWriter().write("ok");
		}
	}

}
