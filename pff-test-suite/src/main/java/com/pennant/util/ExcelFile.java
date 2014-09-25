package com.pennant.util;

import java.io.IOException;
import java.io.PrintWriter;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;

public class ExcelFile {
	final String TAB = "\t";

	public void writeExcel(String name, FinScheduleData schedule) {
		PrintWriter out = null;

		try {
			out = new PrintWriter(name);

			out.print("Date \t CPZ \t PFT \t RVW \t RPY \t Rate\t DisAmount \t DWPAY \t");
			out.print("OPBAL \t CLOSBAL \t NODAYS \t DAYSFACTOR \t ProfitCalc \t ProfitPaid \t");
			out.print("PrincipalPaid \t RepayAmount \t ProfitBalance \tCpzAmount \t");
			out.print("ProfitFraction \t SchdMethod");

			for (FinanceScheduleDetail item : schedule
					.getFinanceScheduleDetails()) {
				out.write("\n");
				out.write(item.getSchDate() + TAB);
				out.write((item.isCpzOnSchDate() ? "Y" : "N") + TAB);
				out.write((item.isPftOnSchDate() ? "Y" : "N") + TAB);
				out.write((item.isRvwOnSchDate() ? "Y" : "N") + TAB);
				out.write((item.isRepayOnSchDate() ? "Y" : "N") + TAB);
				out.write(item.getActRate() + TAB);
				out.write(item.getDisbAmount() + TAB);
				out.write(item.getDownPaymentAmount() + TAB);
				out.write(item.getBalanceForPftCal() + TAB);
				out.write(item.getClosingBalance() + TAB);
				out.write(item.getNoOfDays() + TAB);
				out.write(item.getDayFactor() + TAB);
				out.write(item.getProfitCalc() + TAB);
				out.write(item.getProfitSchd() + TAB);
				out.write(item.getPrincipalSchd() + TAB);
				out.write(item.getRepayAmount() + TAB);
				out.write(item.getProfitBalance() + TAB);
				out.write(item.getCpzAmount() + TAB);
				out.write(item.getProfitFraction() + TAB);
				out.write(item.getSchdMethod());
			}

			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			out = null;
		}
	}
}
