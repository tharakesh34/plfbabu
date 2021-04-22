package com.pennant.backend.model.spreadsheet;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.api.model.Sheet;
import org.zkoss.zss.model.SName;

/**
 * clone a sheet and its named ranges.
 */
public class SheetCopier {

	/**
	 * clone a sheet and its named ranges.
	 */
	public static Sheet clone(String sheetName, Sheet sourceSheet) {
		Sheet targetSheet = Ranges.range(sourceSheet).cloneSheet(sheetName);
		copyNamedRange(sourceSheet, targetSheet);
		return targetSheet;
	}

	/**
	 * copy named ranges of source sheet to target sheet
	 * 
	 * @param sourceSheet
	 * @param targetSheet
	 */
	private static void copyNamedRange(Sheet sourceSheet, Sheet targetSheet) {
		List<SName> namedRangeList = new ArrayList<SName>(sourceSheet.getBook().getInternalBook().getNames()); //to avoid ConcurrentModificationException
		for (SName definedName : namedRangeList) {
			SName clonedNamedRange = null;
			definedName.getApplyToSheetName();
			if (sourceSheet.getSheetName().equals(definedName.getRefersToSheetName())) {
				clonedNamedRange = sourceSheet.getBook().getInternalBook().createName(definedName.getName(),
						targetSheet.getSheetName());
				//shift formulas to the target sheet
				String refersToFormula = definedName.getRefersToFormula();
				refersToFormula = refersToFormula.replace(sourceSheet.getSheetName(), targetSheet.getSheetName());
				clonedNamedRange.setRefersToFormula(refersToFormula);
			}
		}
	}
}