package com.pennant.util;

import java.util.List;

public class CSVUtils {
	// https://tools.ietf.org/html/rfc4180
	private static String followCVSformat(String value) {

		String result = value;
		if (result.contains("\"")) {
			result = result.replace("\"", "\"\"");
		}
		return result;

	}

	public static byte[] writeLine(List<String> values, char separators, char customQuote) {

		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (String value : values) {
			if (!first && !"\n".equals(value)) {
				sb.append(separators);
			}
			if (customQuote == ' ') {
				sb.append(followCVSformat(value));
			} else {
				sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
			}
			first = false;
			if ("\n".equals(value)) {
				first = true;
			}
		}
		return sb.toString().getBytes();
	}
}
