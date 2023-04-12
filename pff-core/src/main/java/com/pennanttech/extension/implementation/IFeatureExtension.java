package com.pennanttech.extension.implementation;

import java.util.Map;

public interface IFeatureExtension {
	Map<String, Object> getCustomConstants();

	Map<String, Object> getCustomerExtensions();

	Map<String, Object> getMandateExtensions();

	Map<String, Object> getPresentmentExtensions();

	Map<String, Object> getAccountingExtensions();

	Map<String, Object> getFeeExtensions();

	Map<String, Object> getDPDExtensions();

	Map<String, Object> getPartnerBankExtensions();

	Map<String, Object> getReceiptExtensions();

	Map<String, Object> getLPPExtensions();
}
