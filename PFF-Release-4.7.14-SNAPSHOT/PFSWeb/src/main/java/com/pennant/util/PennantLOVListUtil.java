package com.pennant.util;

import java.util.HashMap;
import java.util.Map;

import com.pennant.backend.model.ModuleListcode;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

public class PennantLOVListUtil {

	
	private static Map<String, ModuleListcode> moduleMap = new HashMap<String, ModuleListcode>() {
		private static final long serialVersionUID = -3549857310897774789L;
	{
			put("AccountPurpose",new ModuleListcode("AccountPurpose",getHeading("Account Purpose"),PennantStaticListUtil.getAccountPurpose(), getHeadings(new String[]{"PurposeCode","PurposeDescription"})));
			put("WeekName",new ModuleListcode("WeekName",getHeading("WeekName"),PennantStaticListUtil.getWeekName(), getHeadings(new String[]{"WeekCode","WeekName"})));
			put("EarlyPayMethod",new ModuleListcode("EarlyPayMethod",getHeading("Early Payment Method"),PennantStaticListUtil.getEarlyPayEffectOn(), getHeadings(new String[]{"EarlyPayMethod_Code","EarlyPayMethod_Desc"})));
			put("RepaymentMethod",new ModuleListcode("RepaymentMethod",getHeading("Repayment Method"),PennantStaticListUtil.getRepayMethods(), getHeadings(new String[]{"label_RepayMethod","label_RepayMethodDesc"})));
			put("FrequencyDaysMethod",new ModuleListcode("FrequencyDaysMethod",getHeading("Frequency Days Method"),PennantStaticListUtil.getFrequencyDays(), getHeadings(new String[]{"label_FrequencyDaysMethod","label_FrequencyDaysMethodDesc"})));
			put("AccountTypeDisbModes",new ModuleListcode("AccountTypeDisbModes",getHeading("Account Type Modes"),PennantStaticListUtil.getPaymentTypes(false), getHeadings(new String[]{"label_AccountTypeCode","label_AccountTypeDescription"})));
			put("AccountTypeRecptModes",new ModuleListcode("AccountTypeRecptModes",getHeading("Account Type Modes"),PennantStaticListUtil.getPaymentTypes(true), getHeadings(new String[]{"label_AccountTypeCode","label_AccountTypeDescription"})));
			put("AccountTypePayModes",new ModuleListcode("AccountTypeModes",getHeading("Account Type Modes"),PennantStaticListUtil.getPaymentTypes(false), getHeadings(new String[]{"label_AccountTypeCode","label_AccountTypeDescription"})));
	}
	};
	
	
	public static String getHeading(String heading){
		return 	PennantJavaUtil.getLabel(heading);
	}
	
	public static String[] getHeadings(String[] labels){
		String[] headings = new String[labels.length];
		for (int i = 0; i < labels.length; i++) {
			headings[i] = PennantJavaUtil.getLabel(labels[i]);
		}
		return 	headings;
	}
	
	public static ModuleListcode  getModuleMap(String listCode){
		return moduleMap.get(listCode);
	}
	
}
