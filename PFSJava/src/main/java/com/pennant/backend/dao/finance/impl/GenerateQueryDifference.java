package com.pennant.backend.dao.finance.impl;

import java.util.HashMap;
import java.util.Map;

public class GenerateQueryDifference {

	public static void main(String[] args) {
		StringBuilder QueryDiffOne = new StringBuilder();
		StringBuilder QueryDiffTwo = new StringBuilder();
		String firstSqlQuery = getSelectFirstQuery();
		String secondSqlQuery = getSelectSecondQuery();
		String[] sqlQueryOne = firstSqlQuery.split(",");
		String[] sqlQueryTwo = secondSqlQuery.split(",");
		Map<String, Integer> firstQueryNotMatches = getQueryNotMatches(sqlQueryOne, sqlQueryTwo);
		for (String key : firstQueryNotMatches.keySet()) {
			if (firstQueryNotMatches.get(key) == 0) {
				QueryDiffOne.append(key);
				QueryDiffOne.append(" , ");
			}
		}

		System.out.println("Query 1 Exceed Columns : " + QueryDiffOne);
		Map<String, Integer> secondQueryNotMatches = getQueryNotMatches(sqlQueryTwo, sqlQueryOne);
		for (String key : secondQueryNotMatches.keySet()) {
			if (secondQueryNotMatches.get(key) == 0) {
				QueryDiffTwo.append(key);
				QueryDiffTwo.append(" , ");
			}
		}
		System.out.println("Query 2 Exceed Columns : " + QueryDiffTwo);
		//	System.out.println("Qeury Size : "+sqlQueryOne.length);
	}

	private static Map<String, Integer> getQueryNotMatches(String[] sqlQueryOne, String[] sqlQueryTwo) {
		Map<String, Integer> defaultQueryColumn = new HashMap<String, Integer>();
		int k = 0;
		while (k < sqlQueryOne.length) {

			defaultQueryColumn.put(sqlQueryOne[k], 0);
			k++;
		}
		for (int i = 0; i < sqlQueryTwo.length; i++) {
			for (int j = 0; j < sqlQueryOne.length; j++) {
				if (sqlQueryTwo[i].trim().equalsIgnoreCase(sqlQueryOne[j].trim())) {
					defaultQueryColumn.put(sqlQueryOne[j], 1);

				}
			}
		}
		return defaultQueryColumn;
	}

	private static String getSelectFirstQuery() {
		return null;
	}

	private static String getSelectSecondQuery() {
		return null;
	}
}
