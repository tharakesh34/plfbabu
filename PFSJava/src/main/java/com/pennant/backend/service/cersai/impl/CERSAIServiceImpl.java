package com.pennant.backend.service.cersai.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.cersai.CERSAIDAO;
import com.pennant.backend.model.cersai.CersaiAddCollDetails;
import com.pennant.backend.model.cersai.CersaiAssetOwners;
import com.pennant.backend.model.cersai.CersaiBorrowers;
import com.pennant.backend.model.cersai.CersaiChargeHolder;
import com.pennant.backend.model.cersai.CersaiFileInfo;
import com.pennant.backend.model.cersai.CersaiHeader;
import com.pennant.backend.model.cersai.CersaiImmovableAsset;
import com.pennant.backend.model.cersai.CersaiIntangibleAsset;
import com.pennant.backend.model.cersai.CersaiModifyCollDetails;
import com.pennant.backend.model.cersai.CersaiMovableAsset;
import com.pennant.backend.model.cersai.CersaiSatisfyCollDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.cersai.CERSAIService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class CERSAIServiceImpl implements CERSAIService {
	private static Logger logger = LogManager.getLogger(CERSAIServiceImpl.class);

	@Autowired
	private CERSAIDAO cersaiDao;

	@Override
	public void logFileInfo(CersaiFileInfo fileInfo) {
		cersaiDao.logFileInfo(fileInfo);
	}

	@Override
	public List<String> getotalRecords(String downloadType) {
		List<String> collaterals = cersaiDao.getotalRecords();
		List<String> upcollaterals = new ArrayList<String>();

		String tableName = null;

		for (String cc : collaterals) {
			List<CersaiAddCollDetails> ccd = cersaiDao.getCollateralDetailsByRef(cc);
			if (!ccd.isEmpty()) {
				String collateralType = ccd.get(0).getCollateralType();

				if (collateralType != null) {
					tableName = getTableName(CollateralConstants.MODULE_NAME, collateralType);
				}

				List<Map<String, Object>> extDetails = cersaiDao.getExtendedFieldMap(cc, tableName, "");
				for (Map<String, Object> extMap : extDetails) {
					if (extMap.get("SECRTCRTNDATE") != null) {
						upcollaterals.add(cc);
						break;
					}
				}
			}
		}

		return upcollaterals;
	}

	@Override
	public void updateFileStatus(CersaiFileInfo fileInfo) {
		cersaiDao.updateFileStatus(fileInfo);
	}

	@Override
	public CersaiHeader processFileHeader(long totalRecords, String downloadtype) {
		logger.debug(Literal.ENTERING);

		String fileType = "";
		CersaiHeader ch = new CersaiHeader();

		switch (downloadtype) {
		case "ADD":
			fileType = "SI_REGN";
			break;
		case "MODIFY":
			fileType = "SI_MDFN";
			break;
		case "SATISFY":
			fileType = "SI_SATN";
			break;
		default:
			break;
		}
		ch.setFileType(fileType);
		ch.setTotalRecords(totalRecords);
		ch.setFileHeader("FH");
		ch.setFileDate(SysParamUtil.getAppDate());

		Long batchId = cersaiDao.saveHeader(ch);
		ch.setBatchId(batchId);

		logger.debug(Literal.LEAVING);
		return ch;

	}

	@Override
	public List<CersaiAddCollDetails> processCollateralDetails(String collateralRef, long batchId, long borrowerCount,
			long assetOwnerCount, int serialNo, String batchRef) {
		logger.debug(Literal.ENTERING);

		String tableName = null;
		Long siTypeId = null;
		String narration = null;
		String typeOfCharge = null;
		Date siCreationDate = null;
		List<CersaiAddCollDetails> ccd = cersaiDao.getCollateralDetailsByRef(collateralRef);
		String collateralType = ccd.get(0).getCollateralType();

		if (collateralType != null) {
			tableName = getTableName(CollateralConstants.MODULE_NAME, collateralType);
		}
		List<Map<String, Object>> extDetails = cersaiDao.getExtendedFieldMap(collateralRef, tableName, "");

		for (Map<String, Object> extMap : extDetails) {
			if (extMap.containsKey("SI_TYPE_ID") && extMap.get("SI_TYPE_ID") != null) {
				siTypeId = Long.valueOf((String) extMap.get("SI_TYPE_ID"));
			}
			if (extMap.containsKey("NARRATION") && extMap.get("NARRATION") != null) {
				narration = String.valueOf(extMap.get("NARRATION"));
			}
			if (extMap.containsKey("TYPOFCHRG") && extMap.get("TYPOFCHRG") != null) {
				typeOfCharge = String.valueOf(extMap.get("TYPOFCHRG"));
			}
			if (extMap.containsKey("SECRTCRTNDATE") && extMap.get("SECRTCRTNDATE") != null) {
				siCreationDate = (Date) extMap.get("SECRTCRTNDATE");
			}
		}

		for (CersaiAddCollDetails cDtl : ccd) {
			cDtl.setRowType("SI");
			cDtl.setSerialNumber(serialNo);
			cDtl.setNoOfBrrowers(borrowerCount);
			cDtl.setNoOfAssetOwners(assetOwnerCount);
			cDtl.setNoOfConsortiumMemebers(0);
			cDtl.setSiTypeId(siTypeId);
			cDtl.setSiTypeOthers(null);
			cDtl.setFinancingTypeId("Sole");
			cDtl.setNarration(narration);
			cDtl.setTypeOfCharge(typeOfCharge);
			cDtl.setTpm(false);
			if (assetOwnerCount > 0) {
				cDtl.setTpm(true);
			}
			cDtl.setSiCreationDate(siCreationDate);
			cDtl.setBatchRefNumber(batchRef);
			cDtl.setBatchId(batchId);
		}

		cersaiDao.saveCersaiCollateralDetails(ccd);
		logger.debug(Literal.LEAVING);
		return ccd;
	}

	@Override
	public List<CersaiBorrowers> getCustomerDetails(String collateralRef) {
		return cersaiDao.getBorrowersByCollateralRef(collateralRef);
	}

	@Override
	public List<CersaiAssetOwners> getAssetOwnerDetails(String collateralRef) {
		return cersaiDao.getAssetOwnersByCollateralRef(collateralRef);
	}

	@Override
	public List<CersaiBorrowers> processBorrowers(List<CersaiBorrowers> borrowers, long batchId) {
		logger.debug(Literal.ENTERING);
		int i = 1;
		for (CersaiBorrowers cbr : borrowers) {
			cbr.setRowType("BOR");
			cbr.setSerialNumber(i++);
			cbr.setCountry("IN");
			List<Map<String, Object>> extDetails = getExtendedfields(cbr.getCustCif(), cbr.getCustCtgCode(),
					ExtendedFieldConstants.MODULE_CUSTOMER);

			for (Map<String, Object> extMap : extDetails) {
				if (extMap.containsKey("CIN") && extMap.get("CIN") != null) {
					cbr.setBorrowerRegNumber(String.valueOf(extMap.get("CIN")));
				}
			}

		}
		cersaiDao.saveBorrowerDetails(borrowers);
		logger.debug(Literal.LEAVING);
		return borrowers;
	}

	@Override
	public List<CersaiAssetOwners> processAssetOwners(List<CersaiAssetOwners> assetOwners, long batchId) {
		logger.debug(Literal.ENTERING);
		int i = 1;

		for (CersaiAssetOwners ca : assetOwners) {
			ca.setRowType("ASO");
			ca.setSerialNumber(i++);
			ca.setCountry("IN");
			List<Map<String, Object>> extDetails = getExtendedfields(ca.getCustCif(), ca.getCustCtgCode(),
					ExtendedFieldConstants.MODULE_CUSTOMER);

			for (Map<String, Object> extMap : extDetails) {
				if (extMap.containsKey("CIN") && extMap.get("CIN") != null) {
					ca.setAssetOwnerRegNumber(String.valueOf(extMap.get("CIN")));
				}
			}
		}
		cersaiDao.saveAssetOwnerDetails(assetOwners);
		logger.debug(Literal.LEAVING);
		return assetOwners;
	}

	@Override
	public CersaiMovableAsset processMovable(String collateralRef, long batchId, String collateralType) {
		logger.debug(Literal.ENTERING);
		CersaiMovableAsset cma = new CersaiMovableAsset();
		String remarks = cersaiDao.getRemarks(collateralRef);

		cma.setRowType("MOV");
		cma.setAssetUniqueId(collateralRef);
		cma.setAssetDescription(remarks);
		cma.setBatchId(batchId);

		List<Map<String, Object>> extDetails = getExtendedfields(collateralRef, collateralType,
				CollateralConstants.MODULE_NAME);

		for (Map<String, Object> extMap : extDetails) {
			if (extMap.containsKey("ASSET_CATEGORY_ID") && extMap.get("ASSET_CATEGORY_ID") != null) {
				cma.setAssetCategoryId(Long.valueOf((String) extMap.get("ASSET_CATEGORY_ID")));
			}

			if (extMap.containsKey("ASSET_TYPE_ID") && extMap.get("ASSET_TYPE_ID") != null) {
				cma.setAssetTypeId(Long.valueOf((String) extMap.get("ASSET_TYPE_ID")));
				if (cma.getAssetTypeId() == 3 || cma.getAssetTypeId() == 7 || cma.getAssetTypeId() == 14) {
					cma.setAssetTypeOthers("Others");// FIX ME
				}
			}

			if (extMap.containsKey("ASSET_SUB_TYPE_ID") && extMap.get("ASSET_SUB_TYPE_ID") != null) {
				cma.setAssetSubTypeId(Long.valueOf((String) extMap.get("ASSET_SUB_TYPE_ID")));
			}

			if (extMap.containsKey("ASSET_SERIAL_NO") && extMap.get("ASSET_SERIAL_NO") != null) {
				cma.setAssetSerialNumber(String.valueOf(extMap.get("ASSET_SERIAL_NO")));
			}

			if (extMap.containsKey("ASSET_MAKE") && extMap.get("ASSET_MAKE") != null) {
				cma.setAssetMake(String.valueOf(extMap.get("ASSET_MAKE")));
			}

			if (extMap.containsKey("ASSET_MODEL") && extMap.get("ASSET_MODEL") != null) {
				cma.setAssetModel(String.valueOf(extMap.get("ASSET_MODEL")));
			}

			if (extMap.containsKey("ADDRESS_LINE_1") && extMap.get("ADDRESS_LINE_1") != null) {
				cma.setAddressLine1(String.valueOf(extMap.get("ADDRESS_LINE_1")));
			}

			if (extMap.containsKey("ADDRESS_LINE_2") && extMap.get("ADDRESS_LINE_2") != null) {
				cma.setAddressLine2(String.valueOf(extMap.get("ADDRESS_LINE_2")));
			}

			if (extMap.containsKey("ADDRESS_LINE_3") && extMap.get("ADDRESS_LINE_3") != null) {
				cma.setAddressLine3(String.valueOf(extMap.get("ADDRESS_LINE_3")));
			}

			if (extMap.containsKey("CITY") && extMap.get("CITY") != null) {
				cma.setCity(String.valueOf(extMap.get("CITY")));
			}

			if (extMap.containsKey("DISTRICT") && extMap.get("DISTRICT") != null) {
				cma.setDistrict(String.valueOf(extMap.get("DISTRICT")));
			}

			if (extMap.containsKey("STATE") && extMap.get("STATE") != null) {
				cma.setState(String.valueOf(extMap.get("STATE")));
			}

			if (extMap.containsKey("PIN_CODE") && extMap.get("PIN_CODE") != null) {
				cma.setPincode(Long.valueOf((String) extMap.get("PIN_CODE")));
			}

			if (extMap.containsKey("COUNTRY") && extMap.get("COUNTRY") != null) {
				cma.setCountry(String.valueOf(extMap.get("COUNTRY")));
			}
		}

		cersaiDao.saveMovableAsset(cma);
		logger.debug(Literal.LEAVING);
		return cma;
	}

	@Override
	public CersaiImmovableAsset processImmovable(String collateralRef, long batchId, String collateralType) {
		logger.debug(Literal.ENTERING);
		CersaiImmovableAsset cima = new CersaiImmovableAsset();
		String remarks = cersaiDao.getRemarks(collateralRef);

		cima.setRowType("IMM");
		cima.setAssetUniqueId(collateralRef);
		cima.setAssetDescription(remarks);
		cima.setBatchId(batchId);

		List<Map<String, Object>> extDetails = getExtendedfields(collateralRef, collateralType,
				CollateralConstants.MODULE_NAME);

		for (Map<String, Object> extMap : extDetails) {
			if (extMap.containsKey("ASSET_CATEGORY_ID") && extMap.get("ASSET_CATEGORY_ID") != null) {
				cima.setAssetCategoryId(Long.valueOf((String) extMap.get("ASSET_CATEGORY_ID")));
			}

			if (extMap.containsKey("ASSET_TYPE_ID") && extMap.get("ASSET_TYPE_ID") != null) {
				cima.setAssetTypeId(Long.valueOf((String) extMap.get("ASSET_TYPE_ID")));
				if (cima.getAssetTypeId() == 3 || cima.getAssetTypeId() == 7 || cima.getAssetTypeId() == 14) {
					cima.setAssetTypeOthers("Others");// FIX ME
				}
			}

			if (extMap.containsKey("ASSET_SUB_TYPE_ID") && extMap.get("ASSET_SUB_TYPE_ID") != null) {
				cima.setAssetSubTypeId(Long.valueOf((String) extMap.get("ASSET_SUB_TYPE_ID")));
			}

			if (extMap.containsKey("SURVEY_NUMBER") && extMap.get("SURVEY_NUMBER") != null) {
				cima.setSurveyNumber(String.valueOf(extMap.get("SURVEY_NUMBER")));
			}

			if (extMap.containsKey("PLOT_NUMBER") && extMap.get("PLOT_NUMBER") != null) {
				cima.setPlotNumber(String.valueOf(extMap.get("PLOT_NUMBER")));
			}

			if (extMap.containsKey("ASSET_AREA") && extMap.get("ASSET_AREA") != null) {
				cima.setAssetArea((BigDecimal) extMap.get("ASSET_AREA"));
			}

			if (extMap.containsKey("ASSET_AREA_UNIT") && extMap.get("ASSET_AREA_UNIT") != null) {
				cima.setAssetAreaUnit(String.valueOf(extMap.get("ASSET_AREA_UNIT")));
			}

			if (extMap.containsKey("HOUSE_NUMBER") && extMap.get("HOUSE_NUMBER") != null) {
				cima.setHouseNumber(String.valueOf(extMap.get("HOUSE_NUMBER")));
			}

			if (extMap.containsKey("FLOOR_NUMBER") && extMap.get("FLOOR_NUMBER") != null) {
				cima.setFloorNumber(String.valueOf(extMap.get("FLOOR_NUMBER")));
			}

			if (extMap.containsKey("BUILDING_NAME") && extMap.get("BUILDING_NAME") != null) {
				cima.setBuildingName(String.valueOf(extMap.get("BUILDING_NAME")));
			}

			if (extMap.containsKey("PROJECT_NAME") && extMap.get("PROJECT_NAME") != null) {
				cima.setProjectName(String.valueOf(extMap.get("PROJECT_NAME")));
			}

			if (extMap.containsKey("STREET_NAME") && extMap.get("STREET_NAME") != null) {
				cima.setStreetName(String.valueOf(extMap.get("STREET_NAME")));
			}

			if (extMap.containsKey("POCKET") && extMap.get("POCKET") != null) {
				cima.setPocket(String.valueOf(extMap.get("POCKET")));
			}

			if (extMap.containsKey("LOCALITY") && extMap.get("LOCALITY") != null) {
				cima.setLocality(String.valueOf(extMap.get("LOCALITY")));
			}

			if (extMap.containsKey("CITY") && extMap.get("CITY") != null) {
				cima.setCity(String.valueOf(extMap.get("CITY")));
			}

			if (extMap.containsKey("DISTRICT") && extMap.get("DISTRICT") != null) {
				cima.setDistrict(String.valueOf(extMap.get("DISTRICT")));
			}

			if (extMap.containsKey("STATE") && extMap.get("STATE") != null) {
				cima.setState(String.valueOf(extMap.get("STATE")));
			}

			if (extMap.containsKey("PIN_CODE") && extMap.get("PIN_CODE") != null) {
				cima.setPincode(Long.valueOf((String) extMap.get("PIN_CODE")));
			}

			if (extMap.containsKey("COUNTRY") && extMap.get("COUNTRY") != null) {
				cima.setCountry(String.valueOf(extMap.get("COUNTRY")));
			}
		}

		cima.setLatitudeLongitude1(null);
		cima.setLatitudeLongitude2(null);
		cima.setLatitudeLongitude3(null);
		cima.setLatitudeLongitude4(null);

		cersaiDao.saveImmovableAsset(cima);
		logger.debug(Literal.LEAVING);
		return cima;

	}

	@Override
	public CersaiIntangibleAsset processInTangible(String collateralRef, long batchId, String collateralType) {
		logger.debug(Literal.ENTERING);
		CersaiIntangibleAsset cia = new CersaiIntangibleAsset();
		String remarks = cersaiDao.getRemarks(collateralRef);

		cia.setRowType("INT");
		cia.setAssetUniqueId(collateralRef);
		cia.setAssetDescription(remarks);
		cia.setBatchId(batchId);
		cia.setAssetSubTypeId(null);

		List<Map<String, Object>> extDetails = getExtendedfields(collateralRef, collateralType,
				CollateralConstants.MODULE_NAME);

		for (Map<String, Object> extMap : extDetails) {
			if (extMap.containsKey("ASSET_CATEGORY_ID") && extMap.get("ASSET_CATEGORY_ID") != null) {
				cia.setAssetCategoryId(Long.valueOf((String) extMap.get("ASSET_CATEGORY_ID")));
			}

			if (extMap.containsKey("ASSET_TYPE_ID") && extMap.get("ASSET_TYPE_ID") != null) {
				cia.setAssetTypeId(Long.valueOf((String) extMap.get("ASSET_TYPE_ID")));
				if (cia.getAssetTypeId() == 3 || cia.getAssetTypeId() == 7 || cia.getAssetTypeId() == 14) {
					cia.setAssetTypeOthers("Others");// FIX ME
				}
			}

			if (extMap.containsKey("ASSET_SERIAL_NUMBER") && extMap.get("ASSET_SERIAL_NUMBER") != null) {
				cia.setAssetSerialNumber(String.valueOf(extMap.get("ASSET_SERIAL_NUMBER")));
			}

			if (extMap.containsKey("DIARYNUMBER") && extMap.get("DIARYNUMBER") != null) {
				cia.setDairyNumber(String.valueOf(extMap.get("DIARYNUMBER")));
			}

			if (extMap.containsKey("ASSET_CLASS") && extMap.get("ASSET_CLASS") != null) {
				cia.setAssetClass(String.valueOf(extMap.get("ASSET_CLASS")));
			}

			if (extMap.containsKey("ASSET_TITLE") && extMap.get("ASSET_TITLE") != null) {
				cia.setAssetTitle(String.valueOf(extMap.get("ASSET_TITLE")));
			}

			if (extMap.containsKey("PATENT_NUMBER") && extMap.get("PATENT_NUMBER") != null) {
				cia.setPatentNumber(String.valueOf(extMap.get("PATENT_NUMBER")));
			}

			if (extMap.containsKey("PATENT_DATE") && extMap.get("PATENT_DATE") != null) {
				Date patentDate = (Date) extMap.get("PATENT_DATE");
				cia.setPatentDate(patentDate);
			}

			if (extMap.containsKey("LICENSE_NUMBER") && extMap.get("LICENSE_NUMBER") != null) {
				cia.setLicenseNumber(String.valueOf(extMap.get("LICENSE_NUMBER")));
			}

			if (extMap.containsKey("LICENSE_ISSUING_AUTHORITY") && extMap.get("LICENSE_ISSUING_AUTHORITY") != null) {
				cia.setLicenseIssuingAuthority(String.valueOf(extMap.get("LICENSE_ISSUING_AUTHORITY")));
			}

			if (extMap.containsKey("LICENSE_CATEGORY") && extMap.get("LICENSE_CATEGORY") != null) {
				cia.setLicenseCategory(String.valueOf(extMap.get("LICENSE_CATEGORY")));
			}

			if (extMap.containsKey("DESIGN_NUMBER") && extMap.get("DESIGN_NUMBER") != null) {
				cia.setDesignNumber(String.valueOf(extMap.get("DESIGN_NUMBER")));
			}

			if (extMap.containsKey("DESIGN_CLASS") && extMap.get("DESIGN_CLASS") != null) {
				cia.setDesignClass(String.valueOf(extMap.get("DESIGN_CLASS")));
			}

			if (extMap.containsKey("TRADEMARK_APPLICATION_NUMBER")
					&& extMap.get("TRADEMARK_APPLICATION_NUMBER") != null) {
				cia.setTradeMarkAppNumber(String.valueOf(extMap.get("ADDRESSLINE3")));
			}

			if (extMap.containsKey("TRADEMARK_APPLICATION_DATE") && extMap.get("TRADEMARK_APPLICATION_DATE") != null) {
				Date taDate = (Date) extMap.get("TRADEMARK_APPLICATION_DATE");
				cia.setTradeMarkAppDate(taDate);
			}
		}

		cersaiDao.saveIntangibleAsset(cia);
		logger.debug(Literal.LEAVING);
		return cia;
	}

	@Override
	public List<String> getSatisfyingRecords(String downloadType) {
		return cersaiDao.getSatisfyingRecords(downloadType);
	}

	@Override
	public CersaiSatisfyCollDetails processSatisfyCollaterals(String collateralRef, long batchId, int serialNo,
			String batchRef) {
		logger.debug(Literal.ENTERING);
		CersaiSatisfyCollDetails ccd = cersaiDao.getSatisfyCollDetailsByRef(collateralRef);

		ccd.setRowType("SI");
		ccd.setSerialNumber(serialNo);
		ccd.setBatchId(batchId);
		ccd.setReasonCode("1");
		ccd.setReasonOthers(null);
		ccd.setReasonForDelay(null);
		ccd.setBatchRefNumber(batchRef);

		cersaiDao.saveSatisfyCollateral(ccd);

		logger.debug(Literal.LEAVING);
		return ccd;
	}

	private String getTableName(String module, String subModuleName) {
		StringBuilder sb = new StringBuilder();
		sb.append(module);
		sb.append("_");
		sb.append(subModuleName);
		sb.append("_ED");
		return sb.toString();
	}

	public CERSAIDAO getCersaiDao() {
		return cersaiDao;
	}

	public void setCersaiDao(CERSAIDAO cersaiDao) {
		this.cersaiDao = cersaiDao;
	}

	@Override
	public CersaiModifyCollDetails processModifyCollaterals(String collateralRef, long batchId, int serialNo,
			String batchRef) {
		logger.debug(Literal.ENTERING);

		String finref = "";
		BigDecimal totAmt = BigDecimal.ZERO;
		Long siTypeId = null;
		String typeOfCharge = null;
		Date revSecDate = null;

		CersaiModifyCollDetails ccd = cersaiDao.getModifyCollDetailsByRef(collateralRef);
		List<FinanceMain> fm = cersaiDao.getFinanceByCollateralRef(collateralRef);

		for (FinanceMain main : fm) {
			finref = finref.equals("") ? main.getFinReference() : finref + ", " + main.getFinReference();
			totAmt = totAmt.add(main.getFinAssetValue());
		}

		String collateralType = ccd.getCollateralType();
		List<Map<String, Object>> extDetails = getExtendedfields(collateralRef, collateralType,
				CollateralConstants.MODULE_NAME);

		for (Map<String, Object> extMap : extDetails) {
			if (extMap.containsKey("SI_TYPE_ID") && extMap.get("SI_TYPE_ID") != null) {
				siTypeId = Long.valueOf((String) extMap.get("SI_TYPE_ID"));
			}
			if (extMap.containsKey("TYPOFCHRG") && extMap.get("TYPOFCHRG") != null) {
				typeOfCharge = String.valueOf(extMap.get("TYPOFCHRG"));
			}
			if (extMap.containsKey("REVSECRTCRTNDATE") && extMap.get("REVSECRTCRTNDATE") != null) {
				revSecDate = (Date) extMap.get("REVSECRTCRTNDATE");
			}
		}

		ccd.setRowType("SI");
		ccd.setSerialNumber(serialNo);
		ccd.setDocExecuDate(revSecDate);
		ccd.setModifyType("OTHERS");
		ccd.setFinancingTypeId("SOLE");
		ccd.setTotalSecuredAmt(totAmt);
		ccd.setTypeOfCharge(typeOfCharge);
		ccd.setEntityMISToken(finref);
		ccd.setNarration(null);
		ccd.setSiTypeId(siTypeId);
		ccd.setEntityCode("JC137");
		ccd.setOfficeCode(null);
		ccd.setOfficeName("AXIS FINANCE LIMITED");
		ccd.setAddressLine1("AXIS HOUSE C 2 GROUND FLOOR");
		ccd.setAddressLine2("WADIA INTERNATIONAL CENTER");
		ccd.setAddressLine3("PANDURANG BUDHKAR MARG WORLI");
		ccd.setCity("MUMBAI");
		ccd.setDistrict("MUMBAI");
		ccd.setState("MAHARASTRA");
		ccd.setPincode(400025);
		ccd.setCountry("IND");
		ccd.setBatchRefNumber(batchRef);
		ccd.setBatchId(batchId);

		cersaiDao.saveModifyCollateral(ccd);
		logger.debug(Literal.LEAVING);
		return ccd;
	}

	private List<Map<String, Object>> getExtendedfields(String reference, String subModule, String module) {
		String tableName = "";

		if (subModule != null) {
			tableName = getTableName(module, subModule);
		}

		return cersaiDao.getExtendedFieldMap(reference, tableName, "");
	}

	public String getAssetCategory(Long id) {
		return cersaiDao.getAssetCategory(id);
	}

	@Override
	public List<String> getModifyRecords() {
		List<String> collaterals = cersaiDao.getModifyRecords();
		List<String> upCollaterals = new ArrayList<String>();

		String tableName = null;

		for (String cc : collaterals) {
			List<CersaiAddCollDetails> ccd = cersaiDao.getCollateralDetailsByRef(cc);
			if (!ccd.isEmpty()) {
				String collateralType = ccd.get(0).getCollateralType();

				if (collateralType != null) {
					tableName = getTableName(CollateralConstants.MODULE_NAME, collateralType);
				}

				List<Map<String, Object>> extDetails = cersaiDao.getExtendedFieldMap(cc, tableName, "");
				for (Map<String, Object> extMap : extDetails) {
					if (extMap.get("REVSECRTCRTNDATE") != null) {
						upCollaterals.add(cc);
						break;
					}
				}
			}
		}
		return upCollaterals;
	}

	@Override
	public void logFileInfoException(long id, String finReference, String reason) {
		cersaiDao.logFileInfoException(id, finReference, reason);

	}

	@Override
	public String generateFileSeq() {
		return cersaiDao.generateFileSeq();
	}

	@Override
	public CersaiChargeHolder getChargeHolderDetails() {
		return cersaiDao.getChargeHolderDetails();

	}

	public String generateBatchRef() {
		return cersaiDao.generateBatchRef();
	}
}
