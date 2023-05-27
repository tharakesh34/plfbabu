package com.pennant.pff.extension;

import com.pennanttech.extension.FeatureExtension;
import com.pennanttech.pff.Module;
import com.pennanttech.pff.npa.NpaScope;
import com.pennanttech.pff.provision.ProvisionBook;
import com.pennanttech.pff.provision.ProvisionReversalStage;

public class NpaAndProvisionExtension {
	public static boolean ALLOW_NPA;
	public static boolean ALLOW_PROVISION;
	public static NpaScope NPA_SCOPE;
	public static ProvisionBook PROVISION_BOOKS;
	public static boolean PROVISION_REVERSAL_REQ;
	public static ProvisionReversalStage PROVISION_REVERSAL_STAGE;
	public static boolean ALLOW_EXTENDEDFIELDS_IN_WORKFLOW;
	public static boolean PROVISION_POSTINGS_REQ;

	public static boolean ALLOW_MANUAL_PROVISION;
	public static boolean NPA_FOR_WRIREOFF_LOANS;

	static {
		ALLOW_NPA = getValueAsBoolean("ALLOW_NPA", false);
		ALLOW_PROVISION = getValueAsBoolean("ALLOW_PROVISION", false);
		NPA_SCOPE = (NpaScope) getValueAsObject("NPA_SCOPE", NpaScope.LOAN);
		PROVISION_REVERSAL_REQ = getValueAsBoolean("PROVISION_REVERSAL_REQ", false);
		PROVISION_BOOKS = (ProvisionBook) getValueAsObject("PROVISION_BOOKS", ProvisionBook.NO_PROVISION);
		PROVISION_REVERSAL_STAGE = (ProvisionReversalStage) getValueAsObject("PROVISION_REVERSAL_STAGE",
				ProvisionReversalStage.SOM);
		ALLOW_EXTENDEDFIELDS_IN_WORKFLOW = getValueAsBoolean("ALLOW_EXTENDEDFIELDS_IN_WORKFLOW", false);
		PROVISION_POSTINGS_REQ = getValueAsBoolean("PROVISION_POSTINGS_REQ", true);
		ALLOW_MANUAL_PROVISION = getValueAsBoolean("ALLOW_MANUAL_PROVISION", false);
		NPA_FOR_WRIREOFF_LOANS = getValueAsBoolean("NPA_FOR_WRIREOFF_LOANS", false);
	}

	private static boolean getValueAsBoolean(String key, boolean defaultValue) {
		return FeatureExtension.getValueAsBoolean(Module.NPA_PROVISION, key, defaultValue);
	}

	public static int getValueAsInt(String key, int defaultValue) {
		return FeatureExtension.getValueAsInt(Module.NPA_PROVISION, key, defaultValue);
	}

	public static Object getValueAsObject(String key, Object defaultValue) {
		return FeatureExtension.getValueAsObject(Module.NPA_PROVISION, key, defaultValue);
	}

}
