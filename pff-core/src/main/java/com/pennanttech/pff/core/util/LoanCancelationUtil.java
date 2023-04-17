package com.pennanttech.pff.core.util;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public class LoanCancelationUtil {

	private LoanCancelationUtil() {
		super();
	}

	private static List<ValueLabel> cancelTypes;

	public static final String LOAN_CANCEL = "C";
	public static final String LOAN_CANCEL_REBOOK = "CR";
	public static final String LOAN_CANCEL_REMARKS = "Loan Cancellation Reversal";

	/**
	 * Method for Fetching Types of Loan Cancellation
	 * 
	 * @return
	 */
	public static List<ValueLabel> getLoancancelTypes() {
		if (cancelTypes == null) {
			cancelTypes = new ArrayList<>(2);
			cancelTypes.add(new ValueLabel(LOAN_CANCEL, Labels.getLabel("label_CancelType_Cancellation.label")));
			cancelTypes
					.add(new ValueLabel(LOAN_CANCEL_REBOOK, Labels.getLabel("label_CancelType_CancelRebooked.label")));
		}

		return cancelTypes;
	}

}
