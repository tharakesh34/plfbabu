package com.pennanttech.pff.overdue.constants;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.ValueLabel;

public class ChargeType {

	private ChargeType() {
		super();
	}

	public static final String NA = "N";
	/**
	 * Fixed amount one time >> On every installment past due
	 */
	public static final String FLAT = "F";
	public static final String PERC_ONE_TIME = "P";
	public static final String PERC_ON_DUE_DAYS = "D";
	public static final String PERC_ON_PD_MTH = "M";
	/**
	 * Fixed amount on every past due month >> On every installment past due every month
	 */
	public static final String FLAT_ON_PD_MTH = "A";
	public static final String RULE = "R";
	public static final String PERC_ON_EFF_DUE_DAYS = "E";

	private static List<ValueLabel> chargeTypes = new ArrayList<>();

	public static List<ValueLabel> list() {

		if (!chargeTypes.isEmpty()) {
			return chargeTypes;
		}

		chargeTypes = new ArrayList<ValueLabel>(6);
		chargeTypes.add(new ValueLabel(FLAT, Labels.getLabel("label_FlatOneTime")));
		chargeTypes.add(new ValueLabel(FLAT_ON_PD_MTH, Labels.getLabel("label_FixedAmtOnEveryPastDueMonth")));
		chargeTypes.add(new ValueLabel(PERC_ONE_TIME, Labels.getLabel("label_PercentageOneTime")));
		chargeTypes.add(new ValueLabel(PERC_ON_PD_MTH, Labels.getLabel("label_PercentageOnEveryPastDueMonth")));
		chargeTypes.add(new ValueLabel(PERC_ON_DUE_DAYS, Labels.getLabel("label_PercentageOnDueDays")));
		chargeTypes
				.add(new ValueLabel(PERC_ON_EFF_DUE_DAYS, Labels.getLabel("label_PercentageOnDueDaysOnEffectiveDate")));
		if (ImplementationConstants.ALW_LPP_RULE_FIXED) {
			chargeTypes.add(new ValueLabel(RULE, Labels.getLabel("label_FixedByDueDays")));
		}

		return chargeTypes;
	}

}
