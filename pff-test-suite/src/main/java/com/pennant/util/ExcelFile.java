package com.pennant.util;

import java.io.IOException;
import java.io.PrintWriter;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;

public class ExcelFile {
	static final String TAB = "\t";
	static final String folderPath = "D:/ScheduleResults/";

	public static void writeExcel(String fileName, FinScheduleData schedule) {
		PrintWriter out = null;

		try {
			out = new PrintWriter(folderPath + fileName + ".xls");

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

	public static void printSchedule(FinScheduleData schedule) {
		try {
			System.out
					.print("Date \t CPZ \t PFT \t RVW \t RPY \t Rate\t DisAmount \t DWPAY \t");
			System.out
					.print("OPBAL \t CLOSBAL \t NODAYS \t DAYSFACTOR \t ProfitCalc \t ProfitPaid \t");
			System.out
					.print("PrincipalPaid \t RepayAmount \t ProfitBalance \tCpzAmount \t");
			System.out.println("ProfitFraction \t SchdMethod");

			for (FinanceScheduleDetail item : schedule
					.getFinanceScheduleDetails()) {
				System.out.print(item.getSchDate() + TAB);
				System.out.print((item.isCpzOnSchDate() ? "Y" : "N") + TAB);
				System.out.print((item.isPftOnSchDate() ? "Y" : "N") + TAB);
				System.out.print((item.isRvwOnSchDate() ? "Y" : "N") + TAB);
				System.out.print((item.isRepayOnSchDate() ? "Y" : "N") + TAB);
				System.out.print(item.getActRate() + TAB);
				System.out.print(item.getDisbAmount() + TAB);
				System.out.print(item.getDownPaymentAmount() + TAB);
				System.out.print(item.getBalanceForPftCal() + TAB);
				System.out.print(item.getClosingBalance() + TAB);
				System.out.print(item.getNoOfDays() + TAB);
				System.out.print(item.getDayFactor() + TAB);
				System.out.print(item.getProfitCalc() + TAB);
				System.out.print(item.getProfitSchd() + TAB);
				System.out.print(item.getPrincipalSchd() + TAB);
				System.out.print(item.getRepayAmount() + TAB);
				System.out.print(item.getProfitBalance() + TAB);
				System.out.print(item.getCpzAmount() + TAB);
				System.out.print(item.getProfitFraction() + TAB);
				System.out.println(item.getSchdMethod());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
