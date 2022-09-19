package com.pennant.pff.accounting;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public class AccountingUtil {

	public static List<ValueLabel> getManualEntries() {

		List<ValueLabel> list = new ArrayList<>(4);

		list.add(new ValueLabel(TransactionType.NONE.code(), Labels.getLabel("label_Manual_Entries_None")));
		list.add(new ValueLabel(TransactionType.DEBIT.code(), Labels.getLabel("label_Manual_Entries_Debit")));
		list.add(new ValueLabel(TransactionType.CREDIT.code(), Labels.getLabel("label_Manual_Entries_Credit")));
		list.add(new ValueLabel(TransactionType.BOTH.code(), Labels.getLabel("label_Manual_Entries_Both")));

		return list;
	}

	public static List<ValueLabel> getGLAccountStatus() {
		List<ValueLabel> list = new ArrayList<>(2);

		list.add(new ValueLabel(HostAccountStatus.OPEN.code(), Labels.getLabel("label_GL_Account_Status_Open")));
		list.add(new ValueLabel(HostAccountStatus.CLOSE.code(), Labels.getLabel("label_GL_Account_Status_Close")));

		return list;
	}

	public static List<ValueLabel> getJVPurposeList() {
		List<ValueLabel> list = new ArrayList<>(1);

		list.add(new ValueLabel(PostAgainst.LOAN.code(), Labels.getLabel("label_Finance")));

		return list;
	}

	public static List<ValueLabel> getpostingPurposeList() {
		List<ValueLabel> list = new ArrayList<>(1);

		list.add(new ValueLabel(PostAgainst.LOAN.code(), Labels.getLabel("label_Finance")));

		return list;
	}
}
