package com.pennant.pff.mandate;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public class ChequeSatus {

	private static List<ValueLabel> statusList = null;

	private ChequeSatus() {
		super();
	}

	public static final String NEW = "NEW";
	public static final String PRESENT = "PRESENT";
	public static final String BOUNCE = "BOUNCE";
	public static final String REALISE = "REALISE";
	public static final String REALISED = "REALISED";
	public static final String FAILED = "FAILED";
	public static final String CANCELLED = "CANCELLED";

	static {
		statusList = new ArrayList<ValueLabel>(5);
		statusList.add(new ValueLabel(NEW, Labels.getLabel("label_Finance_Cheque_Status_New")));
		statusList.add(new ValueLabel(PRESENT, Labels.getLabel("label_Finance_Cheque_Status_Presented")));
		statusList.add(new ValueLabel(BOUNCE, Labels.getLabel("label_Finance_Cheque_Status_Bounced")));
		statusList.add(new ValueLabel(REALISED, Labels.getLabel("label_Finance_Cheque_Status_Realised")));
		statusList.add(new ValueLabel(CANCELLED, Labels.getLabel("label_Finance_Cheque_Status_Cancelled")));
	}

	public static List<ValueLabel> getList() {
		return statusList;
	}

}
