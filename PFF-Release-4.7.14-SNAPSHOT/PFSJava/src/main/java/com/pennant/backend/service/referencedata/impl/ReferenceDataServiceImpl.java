package com.pennant.backend.service.referencedata.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.dao.referencedata.ReferenceDataDAO;
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
import com.pennant.backend.service.referencedata.ReferenceDataService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webservice.model.ReferenceData;

public class ReferenceDataServiceImpl implements ReferenceDataService {

	private static final Logger logger = Logger.getLogger(ReferenceDataServiceImpl.class);

	private ReferenceDataDAO referenceDataDAO;

	/**
	 * Method for process currency Master data and update into the respective table
	 * 
	 */
	@Override
	public void processCurrencyDetails(String pffTableName, List<ReferenceData> currencyList) {
		logger.debug("Entering");

		try {
			List<Currency> saveMasterList = new ArrayList<Currency>();
			List<Currency> updateMasterList = new ArrayList<Currency>();

			// fetch all existing currencies
			List<Currency> existingCcyList = getReferenceDataDAO().fetchCurrecnyDetails();

			if(currencyList != null && !currencyList.isEmpty()) {
				for(ReferenceData masterData: currencyList) {
					if(checkCurrecnyExist(masterData, existingCcyList)) {
						Currency currency = new Currency();
						currency.setCcyCode(masterData.getT24Code());
						currency.setCcyDesc(masterData.getDesc());

						updateMasterList.add(currency);
					} else {
						Currency currency = new Currency();
						currency.setCcyCode(masterData.getT24Code());
						currency.setCcyDesc(masterData.getDesc());
						//currency.setCcyNumber();
						currency.setCcySwiftCode(masterData.getT24Code());
						//currency.setCcyEditField(2);
						currency.setCcyMinorCcyUnits(BigDecimal.valueOf(100));
						currency.setCcyIsIntRounding(false);
						currency.setCcySpotRate(BigDecimal.ZERO);
						currency.setCcyIsReceprocal(false);
						currency.setCcyUserRateBuy(BigDecimal.ZERO);
						currency.setCcyUserRateSell(BigDecimal.ZERO);
						currency.setCcyIsAlwForLoans(false);
						currency.setCcyIsAlwForDepo(false);
						currency.setCcyIsAlwForAc(false);
						currency.setCcyIsActive(false);
						currency.setVersion(1);
						currency.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						currency.setRecordType("");
						currency.setLastMntBy(1000);
						currency.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						currency.setRoleCode("");
						currency.setNextRoleCode("");
						currency.setTaskId("");
						currency.setNextTaskId("");
						currency.setWorkflowId(0);

						saveMasterList.add(currency);
					}

					// save or update the masters data
					if(!saveMasterList.isEmpty()) {
						getReferenceDataDAO().saveCurrencyMaster(saveMasterList);
					} 
					if(!updateMasterList.isEmpty()) {
						getReferenceDataDAO().updateCurrencyMaster(updateMasterList);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void processIndustryDetail(String pffTableName, List<ReferenceData> industryList) {
		logger.debug("Entering");

		try {
			List<Industry> saveMasterList = new ArrayList<Industry>();
			List<Industry> updateMasterList = new ArrayList<Industry>();

			//Fetch Existing Industry Details
			List<Industry> existingIndustryCodes = getReferenceDataDAO().fetchIndustryDetails();

			if(industryList != null && !industryList.isEmpty()) {
				for(ReferenceData masterData: industryList) {
					if(checkIndustryExist(masterData, existingIndustryCodes)) {
						Industry industry = new Industry();
						industry.setIndustryCode(masterData.getT24Code());
						industry.setIndustryDesc(masterData.getDesc());

						updateMasterList.add(industry);
					} else {
						Industry industry = new Industry();
						industry.setIndustryCode(masterData.getT24Code());
						industry.setIndustryDesc(masterData.getDesc());
						industry.setIndustryIsActive(true);
						industry.setVersion(1);

						industry.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						industry.setRecordType("");
						industry.setLastMntBy(1000);
						industry.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						industry.setRoleCode("");
						industry.setNextRoleCode("");
						industry.setTaskId("");
						industry.setNextTaskId("");
						industry.setWorkflowId(0);

						saveMasterList.add(industry);
					}

					// save or update the masters data
					if(!saveMasterList.isEmpty()) {
						getReferenceDataDAO().saveIndustryMaster(saveMasterList);
					} 
					if(!updateMasterList.isEmpty()) {
						getReferenceDataDAO().updateIndustryMaster(updateMasterList);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}


	@Override
	public void processCustomerTypeDetails(String pffTableName, List<ReferenceData> custTypeList) {
		logger.debug("Entering");

		try {
			List<CustomerType> saveMasterList = new ArrayList<CustomerType>();
			List<CustomerType> updateMasterList = new ArrayList<CustomerType>();

			//Fetch Existing Industry Details
			List<CustomerType> existingCustTypeCodes = getReferenceDataDAO().fetchCustTypeDetails();

			if(custTypeList != null && !custTypeList.isEmpty()) {
				for(ReferenceData masterData: custTypeList) {
					if(checkCustTypeExist(masterData, existingCustTypeCodes)) {
						CustomerType custType = new CustomerType();
						custType.setCustTypeCode(masterData.getT24Code());
						custType.setCustTypeDesc(masterData.getDesc());

						updateMasterList.add(custType);
					} else {
						CustomerType custType = new CustomerType();
						custType.setCustTypeCode(masterData.getT24Code());
						custType.setCustTypeDesc(masterData.getDesc());
						custType.setCustTypeIsActive(true);
						custType.setVersion(1);

						custType.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						custType.setRecordType("");
						custType.setLastMntBy(1000);
						custType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						custType.setRoleCode("");
						custType.setNextRoleCode("");
						custType.setTaskId("");
						custType.setNextTaskId("");
						custType.setWorkflowId(0);

						saveMasterList.add(custType);
					}

					// save or update the masters data
					if(!saveMasterList.isEmpty()) {
						getReferenceDataDAO().saveCustTypeDetails(saveMasterList);
					} 
					if(!updateMasterList.isEmpty()) {
						getReferenceDataDAO().updateCustTypeDetails(updateMasterList);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void processSectorDetails(String pffTableName, List<ReferenceData> sectorRefList) {
		logger.debug("Entering");

		try {
			List<Sector> saveSectorList = new ArrayList<Sector>();
			List<Sector> updateSectorList = new ArrayList<Sector>();

			//Fetch Existing Sector Details
			List<Sector> existingSectorCodes = getReferenceDataDAO().fetchSectorDetails();

			if(sectorRefList != null && !sectorRefList.isEmpty()) {
				for(ReferenceData masterData: sectorRefList) {
					if(checkSectorExist(masterData, existingSectorCodes)) {
						Sector sector = new Sector();
						sector.setSectorCode(masterData.getT24Code());
						sector.setSectorDesc(masterData.getDesc());

						updateSectorList.add(sector);
					} else {
						Sector sector = new Sector();
						sector.setSectorCode(masterData.getT24Code());
						sector.setSectorDesc(masterData.getDesc());
						sector.setSectorIsActive(true);
						sector.setVersion(1);

						sector.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						sector.setRecordType("");
						sector.setLastMntBy(1000);
						sector.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						sector.setRoleCode("");
						sector.setNextRoleCode("");
						sector.setTaskId("");
						sector.setNextTaskId("");
						sector.setWorkflowId(0);

						saveSectorList.add(sector);
					}

					// save or update the masters data
					if(!saveSectorList.isEmpty()) {
						getReferenceDataDAO().saveSectorDetails(saveSectorList);
					} 
					if(!updateSectorList.isEmpty()) {
						getReferenceDataDAO().updateSectorDetails(updateSectorList);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void processCountryDetails(String pffTableName, List<ReferenceData> countryRefList) {
		logger.debug("Entering");

		try {
			List<Country> saveCountryList = new ArrayList<Country>();
			List<Country> updateCountryList = new ArrayList<Country>();

			//Fetch Existing Country Details
			List<Country> existingCountryCodes = getReferenceDataDAO().fetchCountryDetails();

			if(countryRefList != null && !countryRefList.isEmpty()) {
				for(ReferenceData masterData: countryRefList) {
					if(checkCountryExist(masterData, existingCountryCodes)) {
						Country country = new Country();
						country.setCountryCode(masterData.getT24Code());
						country.setCountryDesc(masterData.getDesc());

						updateCountryList.add(country);
					} else {
						Country country = new Country();
						country.setCountryCode(masterData.getT24Code());
						country.setCountryDesc(masterData.getDesc());
						country.setCountryParentLimit(BigDecimal.ZERO);
						country.setCountryResidenceLimit(BigDecimal.ZERO);
						country.setCountryRiskLimit(BigDecimal.ZERO);
						country.setCountryIsActive(true);
						country.setVersion(1);

						country.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						country.setRecordType("");
						country.setLastMntBy(1000);
						country.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						country.setRoleCode("");
						country.setNextRoleCode("");
						country.setTaskId("");
						country.setNextTaskId("");
						country.setWorkflowId(0);

						saveCountryList.add(country);
					}

					// save or update the masters data
					if(!saveCountryList.isEmpty()) {
						getReferenceDataDAO().saveCountryDetails(saveCountryList);
					} 
					if(!updateCountryList.isEmpty()) {
						getReferenceDataDAO().updateCountryDetails(updateCountryList);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void processMaritalStsDetails(String pffTableName, List<ReferenceData> maritalStsRefList) {
		logger.debug("Entering");

		try {
			List<MaritalStatusCode> saveMaritalStsList = new ArrayList<MaritalStatusCode>();
			List<MaritalStatusCode> updateMaritalStsList = new ArrayList<MaritalStatusCode>();

			//Fetch Existing Country Details
			List<MaritalStatusCode> existingMaritalStsCodes = getReferenceDataDAO().fetchMaritalStsDetails();

			if(maritalStsRefList != null && !maritalStsRefList.isEmpty()) {
				for(ReferenceData masterData: maritalStsRefList) {
					if(checkMaritalStsExist(masterData, existingMaritalStsCodes)) {
						MaritalStatusCode maritalStsCode = new MaritalStatusCode();
						maritalStsCode.setMaritalStsCode(masterData.getT24Code());
						maritalStsCode.setMaritalStsDesc(masterData.getDesc());

						updateMaritalStsList.add(maritalStsCode);
					} else {
						MaritalStatusCode maritalStsCode = new MaritalStatusCode();
						maritalStsCode.setMaritalStsCode(masterData.getT24Code());
						maritalStsCode.setMaritalStsDesc(masterData.getDesc());
						maritalStsCode.setMaritalStsIsActive(true);
						maritalStsCode.setVersion(1);

						maritalStsCode.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						maritalStsCode.setRecordType("");
						maritalStsCode.setLastMntBy(1000);
						maritalStsCode.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						maritalStsCode.setRoleCode("");
						maritalStsCode.setNextRoleCode("");
						maritalStsCode.setTaskId("");
						maritalStsCode.setNextTaskId("");
						maritalStsCode.setWorkflowId(0);

						saveMaritalStsList.add(maritalStsCode);
					}

					// save or update the masters data
					if(!saveMaritalStsList.isEmpty()) {
						getReferenceDataDAO().saveMaritalStsDetails(saveMaritalStsList);
					} 
					if(!updateMaritalStsList.isEmpty()) {
						getReferenceDataDAO().updateMaritalStsDetails(updateMaritalStsList);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}


	@Override
	public void processBranchesDetails(String pffTableName, List<ReferenceData> branchesList) {
		logger.debug("Entering");

		try {
			List<Branch> saveBranchList = new ArrayList<Branch>();
			List<Branch> updateBranchList = new ArrayList<Branch>();

			//Fetch Existing Country Details
			List<Branch> existingBranchCodes = getReferenceDataDAO().fetchBranchDetails();

			if(branchesList != null && !branchesList.isEmpty()) {
				for(ReferenceData masterData: branchesList) {
					if(checkBranchExist(masterData, existingBranchCodes)) {
						Branch branch = new Branch();
						branch.setBranchCode(masterData.getT24Code());
						branch.setBranchDesc(masterData.getDesc());

						updateBranchList.add(branch);
					} else {
						Branch branch = new Branch();
						branch.setBranchCode(masterData.getT24Code());
						branch.setBranchDesc(masterData.getDesc());
						branch.setBranchIsActive(true);
						branch.setVersion(1);

						branch.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						branch.setRecordType("");
						branch.setLastMntBy(1000);
						branch.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						branch.setRoleCode("");
						branch.setNextRoleCode("");
						branch.setTaskId("");
						branch.setNextTaskId("");
						branch.setWorkflowId(0);

						saveBranchList.add(branch);
					}

					// save or update the masters data
					if(!saveBranchList.isEmpty()) {
						getReferenceDataDAO().saveBranchDetails(saveBranchList);
					} 
					if(	!updateBranchList.isEmpty()) {
						getReferenceDataDAO().updateBranchDetails(updateBranchList);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	@Override
	public void processSalutationDetails(String pffTableName, List<ReferenceData> salutationList) {
		logger.debug("Entering");

		try {
			List<Salutation> saveSalutationList = new ArrayList<Salutation>();
			List<Salutation> updateSalutationList = new ArrayList<Salutation>();

			//Fetch Existing Country Details
			List<Salutation> existingSalutationCodes = getReferenceDataDAO().fetchSalutationDetails();

			if(salutationList != null && !salutationList.isEmpty()) {
				for(ReferenceData masterData: salutationList) {
					if(checkSalutationExist(masterData, existingSalutationCodes)) {
						Salutation salutation = new Salutation();
						salutation.setSalutationCode(masterData.getT24Code());
						salutation.setSaluationDesc(masterData.getDesc());

						updateSalutationList.add(salutation);
					} else {
						Salutation salutation = new Salutation();
						salutation.setSalutationCode(masterData.getT24Code());
						salutation.setSaluationDesc(masterData.getDesc());
						salutation.setSalutationIsActive(true);
						salutation.setVersion(1);

						salutation.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						salutation.setRecordType("");
						salutation.setLastMntBy(1000);
						salutation.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						salutation.setRoleCode("");
						salutation.setNextRoleCode("");
						salutation.setTaskId("");
						salutation.setNextTaskId("");
						salutation.setWorkflowId(0);

						saveSalutationList.add(salutation);
					}

					// save or update the masters data
					if(!saveSalutationList.isEmpty()) {
						getReferenceDataDAO().saveSalutationDetails(saveSalutationList);
					} 
					if(!updateSalutationList.isEmpty()) {
						getReferenceDataDAO().updateSalutationDetails(updateSalutationList);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	@Override
    public void processLanguageDetails(String pffTableName, List<ReferenceData> languageList) {
		logger.debug("Entering");

		try {
			List<Language> saveLanguageList = new ArrayList<Language>();
			List<Language> updateLanguageList = new ArrayList<Language>();

			//Fetch Existing Country Details
			List<Language> existingLanguageCodes = getReferenceDataDAO().fetchLanguageDetails();

			if(languageList != null && !languageList.isEmpty()) {
				for(ReferenceData masterData: languageList) {
					if(checkLanguageExist(masterData, existingLanguageCodes)) {
						Language language = new Language();
						language.setLngCode(masterData.getT24Code());
						language.setLngDesc(masterData.getDesc());

						updateLanguageList.add(language);
					} else {
						Language language = new Language();
						language.setLngCode(masterData.getT24Code());
						language.setLngDesc(masterData.getDesc());
						language.setVersion(1);

						language.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						language.setRecordType("");
						language.setLastMntBy(1000);
						language.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						language.setRoleCode("");
						language.setNextRoleCode("");
						language.setTaskId("");
						language.setNextTaskId("");
						language.setWorkflowId(0);

						saveLanguageList.add(language);
					}

					// save or update the masters data
					if(!saveLanguageList.isEmpty()) {
						getReferenceDataDAO().saveLanguageDetails(saveLanguageList);
					} 
					if(!updateLanguageList.isEmpty()) {
						getReferenceDataDAO().updateLanguageDetails(updateLanguageList);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	@Override
    public void processSegmentDetails(String pffTableName, List<ReferenceData> segmentList) {
		logger.debug("Entering");

		try {
			List<Segment> saveSegmentList = new ArrayList<Segment>();
			List<Segment> updateSegmentList = new ArrayList<Segment>();

			//Fetch Existing Country Details
			List<Segment> existingSegmentCodes = getReferenceDataDAO().fetchSegmentDetails();

			if(segmentList != null && !segmentList.isEmpty()) {
				for(ReferenceData masterData: segmentList) {
					if(checkSegmentExist(masterData, existingSegmentCodes)) {
						Segment segment = new Segment();
						segment.setSegmentCode(masterData.getT24Code());
						segment.setSegmentDesc(masterData.getDesc());

						updateSegmentList.add(segment);
					} else {
						Segment segment = new Segment();
						segment.setSegmentCode(masterData.getT24Code());
						segment.setSegmentDesc(masterData.getDesc());
						segment.setSegmentIsActive(true);
						segment.setVersion(1);

						segment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						segment.setRecordType("");
						segment.setLastMntBy(1000);
						segment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						segment.setRoleCode("");
						segment.setNextRoleCode("");
						segment.setTaskId("");
						segment.setNextTaskId("");
						segment.setWorkflowId(0);

						saveSegmentList.add(segment);
					}

					// save or update the masters data
					if(!saveSegmentList.isEmpty()) {
						getReferenceDataDAO().saveSegmentDetails(saveSegmentList);
					} 
					if(!updateSegmentList.isEmpty()) {
						getReferenceDataDAO().updateSegmentDetails(updateSegmentList);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	@Override
    public void processGenDepartmentDetails(String pffTableName, List<ReferenceData> genDepartmentList) {
		logger.debug("Entering");

		try {
			List<GeneralDepartment> saveDepartmentList = new ArrayList<GeneralDepartment>();
			List<GeneralDepartment> updateDepartmentList = new ArrayList<GeneralDepartment>();

			//Fetch Existing Country Details
			List<GeneralDepartment> existingGenDepartmentCodes = getReferenceDataDAO().fetchgenDepartmentDetails();

			if(genDepartmentList != null && !genDepartmentList.isEmpty()) {
				for(ReferenceData masterData: genDepartmentList) {
					if(checkgenDepartmentExist(masterData, existingGenDepartmentCodes)) {
						GeneralDepartment genDepartment = new GeneralDepartment();
						genDepartment.setGenDepartment(masterData.getT24Code());
						genDepartment.setGenDeptDesc(masterData.getDesc());

						updateDepartmentList.add(genDepartment);
					} else {
						GeneralDepartment genDepartment = new GeneralDepartment();
						genDepartment.setGenDepartment(masterData.getT24Code());
						genDepartment.setGenDeptDesc(masterData.getDesc());
						genDepartment.setVersion(1);

						genDepartment.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						genDepartment.setRecordType("");
						genDepartment.setLastMntBy(1000);
						genDepartment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						genDepartment.setRoleCode("");
						genDepartment.setNextRoleCode("");
						genDepartment.setTaskId("");
						genDepartment.setNextTaskId("");
						genDepartment.setWorkflowId(0);

						saveDepartmentList.add(genDepartment);
					}

					// save or update the masters data
					if(!saveDepartmentList.isEmpty()) {
						getReferenceDataDAO().saveDepartmentDetails(saveDepartmentList);
					} 
					if(!updateDepartmentList.isEmpty()) {
						getReferenceDataDAO().updateDepartmentDetails(updateDepartmentList);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	@Override
    public void processIncomeTypeDetails(String pffTableName, List<ReferenceData> incomeTypeList) {
		logger.debug("Entering");

		try {
			List<IncomeType> saveIncomeTypeList = new ArrayList<IncomeType>();
			List<IncomeType> updateIncomeTypeList = new ArrayList<IncomeType>();

			//Fetch Existing Country Details
			List<IncomeType> existingIncomeTypeCodes = getReferenceDataDAO().fetchIncomeTypeDetails();

			if(incomeTypeList != null && !incomeTypeList.isEmpty()) {
				for(ReferenceData masterData: incomeTypeList) {
					if(checkIncomeTypeExist(masterData, existingIncomeTypeCodes)) {
						IncomeType incomeType = new IncomeType();
						incomeType.setIncomeTypeCode(masterData.getT24Code());
						incomeType.setIncomeTypeDesc(masterData.getDesc());

						updateIncomeTypeList.add(incomeType);
					} else {
						IncomeType incomeType = new IncomeType();
						incomeType.setIncomeExpense("INCOME");//FIXME: hardCoded
						incomeType.setCategory("SALRIED");
						incomeType.setIncomeTypeCode(masterData.getT24Code());
						incomeType.setIncomeTypeDesc(masterData.getDesc());
						incomeType.setIncomeTypeIsActive(true);
						incomeType.setVersion(1);

						incomeType.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						incomeType.setRecordType("");
						incomeType.setLastMntBy(1000);
						incomeType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						incomeType.setRoleCode("");
						incomeType.setNextRoleCode("");
						incomeType.setTaskId("");
						incomeType.setNextTaskId("");
						incomeType.setWorkflowId(0);

						saveIncomeTypeList.add(incomeType);
					}

					// save or update the masters data
					if(!saveIncomeTypeList.isEmpty()) {
						getReferenceDataDAO().saveIncomeTypeDetails(saveIncomeTypeList);
					} 
					if(!updateIncomeTypeList.isEmpty()) {
						getReferenceDataDAO().updateIncomeTypeDetails(updateIncomeTypeList);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	@Override
    public void processTargetDetails(String pffTableName, List<ReferenceData> targetList) {
		logger.debug("Entering");

		try {
			List<TargetDetail> saveTargetDetailList = new ArrayList<TargetDetail>();
			List<TargetDetail> updateTargetDetailList = new ArrayList<TargetDetail>();

			//Fetch Existing Country Details
			List<TargetDetail> existingTargetCodes = getReferenceDataDAO().fetchTargetDetails();

			if(targetList != null && !targetList.isEmpty()) {
				for(ReferenceData masterData: targetList) {
					if(checkTargetExist(masterData, existingTargetCodes)) {
						TargetDetail targetDetail = new TargetDetail();
						targetDetail.setTargetCode(masterData.getT24Code());
						targetDetail.setTargetDesc(masterData.getDesc());

						updateTargetDetailList.add(targetDetail);
					} else {
						TargetDetail targetDetail = new TargetDetail();
						targetDetail.setTargetCode(masterData.getT24Code());
						targetDetail.setTargetDesc(masterData.getDesc());
						targetDetail.setActive(true);
						targetDetail.setVersion(1);

						targetDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						targetDetail.setRecordType("");
						targetDetail.setLastMntBy(1000);
						targetDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						targetDetail.setRoleCode("");
						targetDetail.setNextRoleCode("");
						targetDetail.setTaskId("");
						targetDetail.setNextTaskId("");
						targetDetail.setWorkflowId(0);

						saveTargetDetailList.add(targetDetail);
					}

					// save or update the masters data
					if(!saveTargetDetailList.isEmpty()) {
						getReferenceDataDAO().saveTargetDetails(saveTargetDetailList);
					} 
					if(!updateTargetDetailList.isEmpty()) {
						getReferenceDataDAO().updateTargetDetails(updateTargetDetailList);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	private boolean checkCurrecnyExist(ReferenceData masterData, List<Currency> existingCcyList) {
		for (Currency currency : existingCcyList) {
			if (StringUtils.equals(masterData.getT24Code(), currency.getCcyCode())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkIndustryExist(ReferenceData masterData, List<Industry> existingIndustries){
		for (Industry industry : existingIndustries) {
			if (StringUtils.equals(masterData.getT24Code(), industry.getIndustryCode())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkCustTypeExist(ReferenceData masterData, List<CustomerType> existingCustTypeList){
		for (CustomerType customerType : existingCustTypeList) {
			if (StringUtils.equals(masterData.getT24Code(), customerType.getCustTypeCode())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkSectorExist(ReferenceData masterData, List<Sector> existingSectorList){
		for (Sector sector : existingSectorList) {
			if (StringUtils.equals(masterData.getT24Code(), sector.getSectorCode())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkCountryExist(ReferenceData masterData, List<Country> existingCountryList){
		for (Country country : existingCountryList) {
			if (StringUtils.equals(masterData.getT24Code(), country.getCountryCode())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkMaritalStsExist(ReferenceData masterData, List<MaritalStatusCode> existingMaritalStsList){
		for (MaritalStatusCode maritalStatusCode : existingMaritalStsList) {
			if (StringUtils.equals(masterData.getT24Code(), maritalStatusCode.getMaritalStsCode())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkBranchExist(ReferenceData masterData, List<Branch> existingBranchList){
		for (Branch branch : existingBranchList) {
			if (StringUtils.equals(masterData.getT24Code(), branch.getBranchCode())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkSalutationExist(ReferenceData masterData, List<Salutation> existingSalutationList){
		for (Salutation salutation : existingSalutationList) {
			if (StringUtils.equals(masterData.getT24Code(), salutation.getSalutationCode())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkLanguageExist(ReferenceData masterData, List<Language> existingLanguageList){
		for (Language language : existingLanguageList) {
			if (StringUtils.equals(masterData.getT24Code(), language.getLngCode())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkSegmentExist(ReferenceData masterData, List<Segment> existingSegmentList){
		for (Segment segment : existingSegmentList) {
			if (StringUtils.equals(masterData.getT24Code(), segment.getSegmentCode())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkgenDepartmentExist(ReferenceData masterData, List<GeneralDepartment> existingDepartmentList){
		for (GeneralDepartment genDepartment : existingDepartmentList) {
			if (StringUtils.equals(masterData.getT24Code(), genDepartment.getGenDepartment())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkIncomeTypeExist(ReferenceData masterData, List<IncomeType> existingIncomeTypeList){
		for (IncomeType incomeType : existingIncomeTypeList) {
			if (StringUtils.equals(masterData.getT24Code(), incomeType.getIncomeTypeCode())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkTargetExist(ReferenceData masterData, List<TargetDetail> existingTargetList){
		for (TargetDetail targetDetail : existingTargetList) {
			if (StringUtils.equals(masterData.getT24Code(), targetDetail.getTargetCode())) {
				return true;
			}
		}
		return false;
	}

	public ReferenceDataDAO getReferenceDataDAO() {
		return referenceDataDAO;
	}

	public void setReferenceDataDAO(ReferenceDataDAO referenceDataDAO) {
		this.referenceDataDAO = referenceDataDAO;
	}

	/**
	 * Method for process GetReference data and do the below operations<br>
	 * 1. Validate the MDM codes (Exists or not)<br>
	 * 2. if new record found save the record with respective MCM table<br>
	 * 3. update the records if already exists<br>
	 * 
	 * @param tableName
	 * @param referenceDataList
	 * 
	 */
	@Override
	public void processMCMDetails(String tableName, List<ReferenceData> referenceDataList) {
		logger.debug("Entering");

		List<ReferenceData> saveMCMList = new ArrayList<ReferenceData>();
		List<ReferenceData> updateMCMList = new ArrayList<ReferenceData>();

		// fetch all existing MDM codes
		List<ReferenceData> existingMDMCodes = getReferenceDataDAO().fetchMDMCodes(tableName);

		if(referenceDataList != null && !referenceDataList.isEmpty()) {
			for(ReferenceData masterData: referenceDataList) {
				if(checkMDMExists(masterData, existingMDMCodes)) {
					updateMCMList.add(masterData);
				} else {
					saveMCMList.add(masterData);
				}

				// save or update the masters data
				if(!saveMCMList.isEmpty()) {
					getReferenceDataDAO().saveMCMMasters(saveMCMList, tableName);
				} 
				if(!updateMCMList.isEmpty()) {
					getReferenceDataDAO().updateMCMMasters(updateMCMList, tableName);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for validate MDM codes are exists or not
	 * 
	 * @param masterData
	 * @param existingMDMCodes
	 * @return
	 */
	private boolean checkMDMExists(ReferenceData masterData, List<ReferenceData> existingMDMCodes) {
		for (ReferenceData refData : existingMDMCodes) {
			if (StringUtils.equals(masterData.getMdmCode(), refData.getMdmCode())) {
				return true;
			}
		}
		return false;
	}

}
