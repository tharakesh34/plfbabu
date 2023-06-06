package com.pennanttech.external.app.util;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.pennapps.core.resource.Literal;

public class TextFileUtil {
	private static final Logger logger = LogManager.getLogger(TextFileUtil.class);

	public void writeDataToFile(String fileName, List<StringBuilder> builders) throws Exception {
		logger.debug(Literal.ENTERING);

		if (builders == null || builders.isEmpty()) {
			return;
		}

		File file = new File(fileName);

		if (!file.exists()) {
			file.createNewFile();
		}

		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			int cnt = 0;
			for (StringBuilder stringBuilder : builders) {
				cnt = cnt + 1;
				outputStream.write(stringBuilder.toString().getBytes());
				if (cnt != builders.size()) {
					outputStream.write(System.lineSeparator().getBytes());
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	public List<StringBuilder> readDataFromFile(File file) {
		logger.debug(Literal.ENTERING);

		try (Scanner sc = new Scanner(file)) {
			List<StringBuilder> dataList = new ArrayList<>();
			while (sc.hasNextLine()) {
				StringBuilder sb = new StringBuilder();
				sb.append(sc.nextLine());
				dataList.add(sb);
			}

			logger.debug(Literal.LEAVING);
			return dataList;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return new ArrayList<>();
		}
	}

	public static void appendData(StringBuilder builder, String appender, int times) {
		for (int i = 0; i < times; i++) {
			builder.append(appender);
		}
	}

	public static Date getDateItem(String[] lineDataStrings, int position) {
		try {
			if (lineDataStrings.length >= position) {
				return new Date(lineDataStrings[position - 1]);// FIXME
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static BigDecimal getBigDecimalItem(String[] lineDataStrings, int position) {
		if (lineDataStrings.length >= position) {
			if (!"".equals(lineDataStrings[position - 1])) {
				return new BigDecimal(lineDataStrings[position - 1]);
			}
		}
		return new BigDecimal(0);
	}

	public static String getItem(String[] lineDataStrings, int position) {
		if (lineDataStrings.length >= position) {
			return lineDataStrings[(position - 1)];
		}
		return "";
	}

	public static long getLongItem(String[] lineDataStrings, int position) {
		if (lineDataStrings.length >= position) {
			if (!"".equals(lineDataStrings[position - 1])) {
				return Long.parseLong(lineDataStrings[position - 1]);
			}
		}
		return 0L;
	}
}
