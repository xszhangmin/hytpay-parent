package com.hyt.hytpay;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.hyt.hytpay.utils.MatrixToImageWriter;
import com.hyt.hytpay.utils.Utility;
import org.junit.Test;

import java.io.File;
import java.util.Hashtable;
import java.util.SortedMap;
import java.util.TreeMap;

public class WeChatPaymentTest {

	/**
	 * 将网关数据转换成XML
	 * 
	 * @throws Exception
	 */
	@Test
	public void TestConvertGatewayParameterDataToXml() throws Exception {
		SortedMap<String, String> sortedMap = new TreeMap<String, String>();
		sortedMap.put("c", "c");
		sortedMap.put("d", "d");
		sortedMap.put("w", "w");
		sortedMap.put("q", "q");
		sortedMap.put("h", "h");
		sortedMap.put("e", "e");
		System.out.print(Utility.mapToXml(sortedMap));
	}

	@Test
	public void TestBuildQRCodeImage() throws Exception {
		int width = 300; // 二维码图片宽度
		int height = 300; // 二维码图片高度
		String format = "png";// 二维码的图片格式

		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 内容所使用字符集编码

		BitMatrix bitMatrix = new MultiFormatWriter().encode("www.zhangmin.com",
				BarcodeFormat.QR_CODE, width, height, hints);
		// 生成二维码
		File outputFile = new File("/Users/zhangmin/test" + File.separator
				+ "TestBuildQRCodeImage.png");
		MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile);
	}

}
