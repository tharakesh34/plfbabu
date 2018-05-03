package com.pennant.backend.dao.referencedata;

import java.util.List;

import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.TargetDetail;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.webservice.model.ReferenceData;

public interface ReferenceDataDAO {

	String getPFFMasterName(String category);

	String getMasterCode(String mdmCode, String localTableName);

	void saveMCMMasters(List<ReferenceData> saveMasterList, String localTableName);

	void updateMCMMasters(List<ReferenceData> updateMasterList, String localTableName);

	List<Currency> fetchCurrecnyDetails();
	List<Industry> fetchIndustryDetails();
	List<ReferenceData> fetchMDMCodes(String tableName);
	List<CustomerType> fetchCustTypeDetails();
	
	void saveCurrencyMaster(List<Currency> saveMasterList);
	void saveIndustryMaster(List<Industry> saveMasterList);

	void updateCurrencyMaster(List<Currency> updateMasterList);
	void updateIndustryMaster(List<Industry> updateMasterList);

	void saveCustTypeDetails(List<CustomerType> saveMasterList);
	void updateCustTypeDetails(List<CustomerType> updateMasterList);

	List<Sector> fetchSectorDetails();

	void saveSectorDetails(List<Sector> saveSectorList);
	void updateSectorDetails(List<Sector> updateSectorList);

	List<Country> fetchCountryDetails();

	void saveCountryDetails(List<Country> saveCountryList);
	void updateCountryDetails(List<Country> updateCountryList);

	List<MaritalStatusCode> fetchMaritalStsDetails();

	void saveMaritalStsDetails(List<MaritalStatusCode> saveMaritalStsList);
	void updateMaritalStsDetails(List<MaritalStatusCode> updateMaritalStsList);

	List<Branch> fetchBranchDetails();

	void saveBranchDetails(List<Branch> saveBranchList);
	void updateBranchDetails(List<Branch> updateBranchList);

	List<Salutation> fetchSalutationDetails();

	void saveSalutationDetails(List<Salutation> salutationList);
	void updateSalutationDetails(List<Salutation> salutationList);

	List<Language> fetchLanguageDetails();

	void saveLanguageDetails(List<Language> languageList);
	void updateLanguageDetails(List<Language> languageList);

	List<Segment> fetchSegmentDetails();

	void saveSegmentDetails(List<Segment> saveSegmentList);
	void updateSegmentDetails(List<Segment> updateSegmentList);

	List<GeneralDepartment> fetchgenDepartmentDetails();

	void saveDepartmentDetails(List<GeneralDepartment> saveGenDepartmentList);
	void updateDepartmentDetails(List<GeneralDepartment> updateGenDepartmentList);

	List<IncomeType> fetchIncomeTypeDetails();

	void saveIncomeTypeDetails(List<IncomeType> saveIncomeTypeList);
	void updateIncomeTypeDetails(List<IncomeType> updateIncomeTypeList);

	List<TargetDetail> fetchTargetDetails();

	void saveTargetDetails(List<TargetDetail> saveTargetDetailList);
	void updateTargetDetails(List<TargetDetail> updateTargetDetailList);




 
}
