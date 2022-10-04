package com.pennant.pff.mandate;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public class AccountTypes {

	private static List<ValueLabel> typeList = null;

	public static final String CA = "11";
	public static final String SA = "10";
	public static final String CC = "12";
	public static final String NRE = "13";
	public static final String NRO = "14";

	private AccountTypes() {
		super();
	}

	public static List<ValueLabel> getList() {
		typeList = new ArrayList<ValueLabel>(5);
		typeList.add(new ValueLabel(CA, Labels.getLabel("label_Cheque_CA")));
		typeList.add(new ValueLabel(SA, Labels.getLabel("label_Cheque_SA")));
		typeList.add(new ValueLabel(CC, Labels.getLabel("label_Cheque_CC")));
		typeList.add(new ValueLabel(NRE, Labels.getLabel("label_Cheque_NRE")));
		typeList.add(new ValueLabel(NRO, Labels.getLabel("label_Cheque_NRO")));

		return typeList;
	}

}
