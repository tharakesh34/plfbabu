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
			put("AccountPurpose",new ModuleListcode("AccountPurpose",getHeading("AccountPurpose"),PennantStaticListUtil.getAccountPurpose(), getHeadings(new String[]{"PurposeCode","PurposeDescription"})));
			put("WeekName",new ModuleListcode("WeekName",getHeading("WeekName"),PennantStaticListUtil.getWeekName(), getHeadings(new String[]{"WeekCode","WeekName"})));
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
