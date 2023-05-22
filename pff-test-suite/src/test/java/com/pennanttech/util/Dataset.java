package com.pennanttech.util;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.pennanttech.pennapps.core.util.DateUtil;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class Dataset {
	public static boolean loaded;
	public static Workbook schedule;

	public static void load() throws BiffException, IOException {
		URL url = (new Dataset()).getClass().getClassLoader().getResource("TestScenarios.xls");
		schedule = Workbook.getWorkbook(new File(url.getPath()));

		PrintFactory.folderPath = StringUtils.substringBeforeLast(url.getPath(), "/") + "/reports/";

		loaded = true;
	}

	public static Sheet getSchedule(String name) throws BiffException, IOException {
		synchronized (Dataset.class) {
			if (!loaded) {
				load();
			}
		}

		return schedule.getSheet(name);
	}

	public static String getString(Cell[] data, int index) {
		return StringUtils.trimToNull(data[index].getContents());
	}

	public static int getInt(Cell[] data, int index) {
		return Integer.parseInt(data[index].getContents());
	}

	public static Long getLong(Cell[] data, int index) {
		return Long.parseLong(data[index].getContents());
	}

	public static BigDecimal getBigDecimal(Cell[] data, int index) {
		return new BigDecimal(data[index].getContents());
	}

	public static Date getDate(Cell[] data, int index) {
		if (null == getString(data, index)) {
			return null;
		}

		return DateUtil.getDate(data[index].getContents());
	}

	public static boolean getBoolean(Cell[] data, int index) {
		return Boolean.parseBoolean(data[index].getContents());
	}

	public static void close() {
		if (schedule != null) {
			schedule.close();
			schedule = null;
		}
	}
}
