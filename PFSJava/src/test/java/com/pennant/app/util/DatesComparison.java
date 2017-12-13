package com.pennant.app.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import com.pennant.backend.model.finance.FinanceDisbursement;

public class DatesComparison {

	public static void main(String args[]) {
		try {
			List<FinanceDisbursement> disbList = getRequiredData();
			DatesComparison comparison = new DatesComparison();
			
			List<FinanceDisbursement> apacheDisbList = comparison.getSortedDatesUsingApache(disbList);
			System.out.println("Apache commons-lang Sorting.....Result");
			for(FinanceDisbursement finDisb:apacheDisbList) {
				System.out.println(DateUtility.formatDate(finDisb.getDisbDate(), "dd-MMM-yyyy"));
			}
			
			List<FinanceDisbursement> finalDisbList = comparison.getSortedDatesUsingDateutility(disbList);
			System.out.println("DateUtility Sorting.....Result");
			for(FinanceDisbursement finDisb:finalDisbList) {
				System.out.println(DateUtility.formatDate(finDisb.getDisbDate(), "dd-MMM-yyyy"));
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public List<FinanceDisbursement> getSortedDatesUsingDateutility(List<FinanceDisbursement> disbursementDetails) {
		if (disbursementDetails != null && disbursementDetails.size() > 1) {
			Collections.sort(disbursementDetails, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement detail1, FinanceDisbursement detail2) {
					return DateUtility.compare(detail1.getDisbDate(), detail2.getDisbDate());
				}
			});
		}
		return disbursementDetails;
	}
	
	public List<FinanceDisbursement> getSortedDatesUsingApache(List<FinanceDisbursement> disbursementDetails) {
		if (disbursementDetails != null && disbursementDetails.size() > 1) {
			Collections.sort(disbursementDetails, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement detail1, FinanceDisbursement detail2) {
					return DateUtils.truncatedCompareTo(detail1.getDisbDate(), detail2.getDisbDate(), Calendar.DATE);
				}
			});
		}
		return disbursementDetails;
	}
	
	private static List<FinanceDisbursement> getRequiredData() throws ParseException {
		List<FinanceDisbursement> disbList = new ArrayList<>();

		FinanceDisbursement finDisbursement1 = new FinanceDisbursement();
		finDisbursement1.setDisbDate(DateUtility.parse("01-01-2017", "dd-MM-yyyy"));
		finDisbursement1.setDisbType("C");
		disbList.add(finDisbursement1);

		FinanceDisbursement finDisbursement2 = new FinanceDisbursement();
		finDisbursement2.setDisbDate(DateUtility.parse("01-06-2017", "dd-MM-yyyy"));
		finDisbursement2.setDisbType("C");
		disbList.add(finDisbursement2);

		FinanceDisbursement finDisbursement3 = new FinanceDisbursement();
		finDisbursement3.setDisbDate(DateUtility.parse("01-05-2017", "dd-MM-yyyy"));
		finDisbursement3.setDisbType("C");
		disbList.add(finDisbursement3);

		FinanceDisbursement finDisbursement4 = new FinanceDisbursement();
		finDisbursement4.setDisbDate(DateUtility.parse("01-03-2017", "dd-MM-yyyy"));
		finDisbursement4.setDisbType("C");
		disbList.add(finDisbursement4);

		FinanceDisbursement finDisbursement5 = new FinanceDisbursement();
		finDisbursement5.setDisbDate(DateUtility.parse("01-02-2017", "dd-MM-yyyy"));
		finDisbursement5.setDisbType("C");
		disbList.add(finDisbursement5);

		FinanceDisbursement finDisbursement6 = new FinanceDisbursement();
		finDisbursement6.setDisbDate(DateUtility.parse("01-04-2017", "dd-MM-yyyy"));
		finDisbursement6.setDisbType("C");
		disbList.add(finDisbursement6);
		return disbList;
	}
}
