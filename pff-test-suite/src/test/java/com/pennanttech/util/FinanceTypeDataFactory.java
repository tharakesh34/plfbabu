package com.pennanttech.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.pennant.backend.model.rmtmasters.FinanceType;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class FinanceTypeDataFactory {
	public FinanceTypeDataFactory() {
		super();
	}

	private Sheet getSheet(String sheetName) throws BiffException, IOException {
		URL url = (new Dataset()).getClass().getClassLoader().getResource("TestScenarios.xls");
		Workbook workbook = Workbook.getWorkbook(new File(url.getPath()));

		Sheet loanTypeSheet = workbook.getSheet("FT_" + sheetName);

		return loanTypeSheet;
	}

	public FinanceType getFianceType(String finType) {
		FinanceType financeType = new FinanceType();

		try {
			Sheet loanTypeSheet = getSheet(finType);
			Cell[] values = loanTypeSheet.getColumn(2);

			financeType.setFinType(Dataset.getString(values, 1));
			financeType.setFinCategory(Dataset.getString(values, 2));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return financeType;
	}
}
