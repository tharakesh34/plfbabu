package com.pennant.backend.service.referencedata;

import java.util.List;

import com.pennant.webservice.model.ReferenceData;

public interface ReferenceDataService {

	void processMCMDetails(String string, List<ReferenceData> value);
	
	void processCurrencyDetails(String pffTableName, List<ReferenceData> currencyList);
	
	void processIndustryDetail(String pffTableName, List<ReferenceData> industryList);
	
	void processCustomerTypeDetails(String pffTableName, List<ReferenceData> custTypeList);
	
	void processSectorDetails(String pffTableName, List<ReferenceData> sectorList);
	
	void processCountryDetails(String pffTableName, List<ReferenceData> countryList);
	
	void processMaritalStsDetails(String pffTableName, List<ReferenceData> maritalStsList);

	void processBranchesDetails(String pffTableName, List<ReferenceData> branchesList);

	void processSalutationDetails(String pffTableName, List<ReferenceData> salutationList);

	void processLanguageDetails(String pffTableName, List<ReferenceData> languageList);

	void processSegmentDetails(String pffTableName, List<ReferenceData> segmentList);

	void processGenDepartmentDetails(String pffTableName, List<ReferenceData> departmentList);

	void processIncomeTypeDetails(String pffTableName, List<ReferenceData> incomeTypeList);

	void processTargetDetails(String pffTableName, List<ReferenceData> targetList);

}
