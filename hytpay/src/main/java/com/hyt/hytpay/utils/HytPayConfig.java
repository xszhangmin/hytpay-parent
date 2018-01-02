package com.hyt.hytpay.utils;

public class HytPayConfig {

	/**
	 * HTTP(S) 连接超时时间，单位毫秒
	 *
	 * @return
	 */
	public static int getHttpConnectTimeoutMs() {
		return 6 * 1000;
	}

	/**
	 * HTTP(S) 读数据超时时间，单位毫秒
	 *
	 * @return
	 */
	public static int getHttpReadTimeoutMs() {
		return 8 * 1000;
	}

}
