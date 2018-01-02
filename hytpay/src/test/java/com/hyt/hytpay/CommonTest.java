package com.hyt.hytpay;

import org.junit.Test;

import java.util.SortedMap;
import java.util.TreeMap;

public class CommonTest {

	@Test
	public void TestSortedMap() {
		SortedMap<String, String> sortedMap = new TreeMap<String, String>();
		sortedMap.put("c", "c");
		sortedMap.put("d", "d");
		sortedMap.put("w", "w");
		sortedMap.put("q", "q");
		sortedMap.put("h", "h");
		sortedMap.put("e", "e");
		sortedMap.forEach((key, val) -> {
			System.out.print(key);
		});
	}
}
