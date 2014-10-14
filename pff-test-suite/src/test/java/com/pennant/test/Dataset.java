package com.pennant.test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.DateUtility;

public class Dataset {
	public static boolean loaded;
	public static Workbook schedule;

	public static void load() throws BiffException, IOException {
		URL url = (new Dataset()).getClass().getClassLoader()
				.getResource("dataset-schedule.xls");
		schedule = Workbook.getWorkbook(new File(url.getPath()));

		loaded = true;
	}

	public static Sheet getSchedule(String name) throws BiffException,
			IOException {
		if (!loaded) {
			load();
		}

		return schedule.getSheet(name);
	}

	public static String getString(Cell[] data, int index) {
		return StringUtils.trimToNull(data[index].getContents());
	}

	public static BigDecimal getBigDecimal(Cell[] data, int index) {
		return new BigDecimal(data[index].getContents());
	}

	public static Long getLong(Cell[] data, int index) {
		return new Long(data[index].getContents());
	}

	public static Date getDate(Cell[] data, int index) {
		return DateUtility.getDate(data[index].getContents());
	}

	public static void close() {
		if (schedule != null) {
			schedule.close();
			schedule = null;
		}
	}
}
