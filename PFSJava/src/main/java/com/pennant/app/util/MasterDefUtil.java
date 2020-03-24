package com.pennant.app.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.MasterDef;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.service.ConfigurationService;
import com.pennanttech.pennapps.service.ConfigurationService.ConfigType;

public class MasterDefUtil {

	/**
	 * Enumerates the document type codes
	 */
	public enum DocType {
		AADHAAR,
		PAN,
		PASSPORT,
		VOTER_ID,
		DRIVING_LICENCE,
		RATION_CARD,
		CORPORATE_ID_NUMBER,
		TAX_IDENTIFICATION_NUMBER,
		DIRECTOR_IDENTIFICATION_NUMBER,
		SERVICE_TAX_REG_NO;

	}

	public enum AddressType {
		HOME, BUS, CURRES, PER,RESIOFF;
	}

	private static List<MasterDef> masterDefList;

	public static void setMasterDefList(List<MasterDef> masterDefList) {
		MasterDefUtil.masterDefList = masterDefList;
		loadDocumentTypes();
		loadAddressTypes();
	}

	private static Map<String, String> documentTypes;

	public static String getDocCode(DocType docType) {
		if (documentTypes == null) {
			loadDocumentTypes();
		}

		return documentTypes.get(docType.name());
	}

	private static void loadDocumentTypes() {
		if (masterDefList == null) {
			loadMasterDef();
		}

		documentTypes = new HashMap<>();

		for (MasterDef masterDef : masterDefList) {
			if ("DOC_TYPE".equals(masterDef.getMasterType())) {
				documentTypes.put(masterDef.getKeyType(), masterDef.getKeyCode());
			}
		}
	}

	private static Map<String, String> addressTypes;

	public static String getAddressCode(AddressType addrType) {
		if (addressTypes == null) {
			loadAddressTypes();
		}

		return addressTypes.get(addrType.name());
	}

	private static void loadAddressTypes() {
		if (masterDefList == null) {
			loadMasterDef();
		}

		addressTypes = new HashMap<>();

		for (MasterDef masterDef : masterDefList) {
			if ("ADDR_TYPE".equals(masterDef.getMasterType())) {
				addressTypes.put(masterDef.getKeyType(), masterDef.getKeyCode());
			}
		}
	}

	public static void loadMasterDef() {
		SpringBeanUtil.getBean(ConfigurationService.class).reloadConfigurations(ConfigType.MASTER_DEF);
	}
}
