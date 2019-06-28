package com.pennanttech.pff.settlementprocess.webui;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.zkoss.util.media.Media;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pff.settlement.dao.SettlementProcessDAO;
import com.pennanttech.pff.settlementprocess.model.SettlementProcess;

public class SettlementProcessUploadResponce extends BasicDao<SettlementProcess> implements ProcessRecord {
	SettlementProcessDAO settlementProcessDAO;
	private DataSource dataSource;
	private FinanceMainDAO financeMainDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public SettlementProcessUploadResponce() {
		super();
	}

	public void collateralFileUploadProcessResponseFile(Object... params) throws Exception {
		long userId = (Long) params[0];
		DataEngineStatus status = (DataEngineStatus) params[1];
		File file = (File) params[2];
		Media media = (Media) params[3];
		String configName = status.getName();
		String name = "";

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		status.reset();
		status.setFileName(name);
		status.setRemarks("initiated Collateral upload  file [ " + name + " ] processing..");

		DataEngineImport dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true,
				DateUtility.getAppDate(), status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(DateUtility.getAppDate());
		Map<String, Object> filterMap = new HashMap<>();
		Map<String, Object> parametersMap = new HashMap<>();
		dataEngine.setParameterMap(parametersMap);
		dataEngine.setFilterMap(filterMap);
		dataEngine.setProcessRecord(this);
		dataEngine.importData(configName);

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

	}

	@Override
	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource record, Table table) {
		try {
			MapSqlParameterSource settlementMapdata = new MapSqlParameterSource();

			String subPayByManufacturer = ((String) record.getValue("SubPayByManfacturer")).replace(" %", "");
			settlementMapdata.addValue("RequestBatchId", attributes.getStatus().getId());
			settlementMapdata.addValue("SettlementRef", (String) record.getValue("SettlementRef"));
			settlementMapdata.addValue("CustomerRef", (String) record.getValue("CustomerRef"));
			settlementMapdata.addValue("EMIOffer", (String) record.getValue("EMIOffer"));
			settlementMapdata.addValue("SubPayByManfacturer",
					new BigDecimal(subPayByManufacturer));
			settlementMapdata.addValue("SubvensionAmount",
					new BigDecimal((String) record.getValue("SubvensionAmount")));
			settlementMapdata.addValue("CustName", (String) record.getValue("CustName"));
			settlementMapdata.addValue("CustMobile", (String) record.getValue("CustMobile"));
			settlementMapdata.addValue("CustAddress", (String) record.getValue("CustAddress"));
			settlementMapdata.addValue("CustEmail", (String) record.getValue("CustEmail"));
			settlementMapdata.addValue("StoreName", (String) record.getValue("StoreName"));
			settlementMapdata.addValue("StoreAddress", (String) record.getValue("StoreAddress"));
			settlementMapdata.addValue("StoreCity", (String) record.getValue("StoreCity"));
			settlementMapdata.addValue("StoreCountry", "");
			settlementMapdata.addValue("StoreState", (String) record.getValue("StoreState"));
			settlementMapdata.addValue("Issuer", (String) record.getValue("Issuer"));
			settlementMapdata.addValue("Category", (String) record.getValue("Category"));
			settlementMapdata.addValue("Description", (String) record.getValue("Description"));
			settlementMapdata.addValue("Serial", (String) record.getValue("Serial"));
			settlementMapdata.addValue("Manufacturer", (String) record.getValue("Manufacturer"));
			settlementMapdata.addValue("TransactionAmount",
					new BigDecimal((String) record.getValue("TransactionAmount")));
			settlementMapdata.addValue("Acquirer", (String) record.getValue("Acquirer"));
			settlementMapdata.addValue("ManufactureId", Long.valueOf((String) record.getValue("ManufactureId")));
			settlementMapdata.addValue("TerminalId", (String) record.getValue("TerminalId"));
			settlementMapdata.addValue("SettlementBatch", (String) record.getValue("SettlementBatch"));
			settlementMapdata.addValue("BankInvoice", (String) record.getValue("BankInvoice"));
			settlementMapdata.addValue("AuthCode", (String) record.getValue("AuthCode"));
			settlementMapdata.addValue("HostReference", (String) record.getValue("HostReference"));
			settlementMapdata.addValue("TransactionDateTime", DateUtility.getDate((String) record.getValue("TransactionDateTime"), "MMM dd, yyyy  hh:mm:ss"));
			settlementMapdata.addValue("SettlementDateTime", DateUtility.getDate((String) record.getValue("SettlementDateTime"), "MMM dd, yyyy  hh:mm:ss"));
			settlementMapdata.addValue("BillingInvoice", (String) record.getValue("BillingInvoice"));
			settlementMapdata.addValue("TransactionStatus", (String) record.getValue("TransactionStatus"));
			settlementMapdata.addValue("Reason", (String) record.getValue("Reason"));
			settlementMapdata.addValue("ProductCategory", (String) record.getValue("ProductCategory"));
			settlementMapdata.addValue("ProductSubCategory1", (String) record.getValue("ProductSubCategory1"));
			settlementMapdata.addValue("ProductSubCategory2", (String) record.getValue("ProductSubCategory2"));
			settlementMapdata.addValue("ModelName", (String) record.getValue("ModelName"));
			settlementMapdata.addValue("MaxValueOfProduct",
					new BigDecimal((String) record.getValue("MaxValueOfProduct")));
			settlementMapdata.addValue("MerchantName", (String) record.getValue("MerchantName"));

			validate(settlementMapdata);
			
			settlementProcessDAO.saveSettlementProcessRequest(settlementMapdata);
		} catch (Exception e) {
			throw new AppException(e.getMessage());

		}

	}

	private void validate(MapSqlParameterSource settlementMapdata) {
		FinanceMain finMain = null;
		if (settlementMapdata.getValue("HostReference") == null || settlementMapdata.getValue("HostReference").equals("")) {
			throw new AppException("HostReference is mandatory");
		} else {
			 finMain = financeMainDAO
					.getFinanceMainByOldFinReference(String.valueOf(settlementMapdata.getValue("HostReference")), true);
			if (finMain == null) {
				throw new AppException("HostReference is not avilable in PLF or inacive");
			}
		}
		List<ExtendedField> extData = new ArrayList<>();
		
		if(settlementMapdata.getValue("TerminalId") == null){
			throw new AppException("TID is mandatory");
		} else {
			
		}
		if(settlementMapdata.getValue("ManufactureId") == null) {
			throw new AppException("MID is mandatory");
		}
		if(settlementMapdata.getValue("TerminalId")!=null && settlementMapdata.getValue("ManufactureId")!=null){
			if(finMain!=null) {
				extData = extendedFieldDetailsService.getExtndedFieldDetails(
						ExtendedFieldConstants.MODULE_LOAN,
						finMain.getFinCategory(),
						FinanceConstants.FINSER_EVENT_ORG, finMain.getFinReference());
				}
			Map<String, Object> mapValues = new HashMap<String, Object>();
			if (extData != null) {
				for (ExtendedField extendedField : extData) {
					for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
					}
				}
			}
			String mid = (String) mapValues.get("MID");
			if(!StringUtils.equals(mid,settlementMapdata.getValue("ManufactureId").toString())) {
				throw new AppException("In valid MID");
			}
			String tid = (String) mapValues.get("TID");
			if(!StringUtils.equals(tid,settlementMapdata.getValue("TerminalId").toString())) {
				throw new AppException("In valid TID");
			}
		}
	}

	public void settlementFileDownload(Object... params) throws Exception {
		long userId = (Long) params[0];
		String userName = (String) params[1];
		String batchId = (String) params[2];
		
		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("REQUESTBATCHID", batchId);
		Map<String, Object> parameterMap = new HashMap<>();
		
		DataEngineExport dataEngine = null;
		dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true,
				SysParamUtil.getAppValueDate());

		genetare(dataEngine, userName, filterMap, parameterMap);
		
	}
	
	protected DataEngineStatus genetare(DataEngineExport dataEngine, String userName, Map<String, Object> filterMap,
			Map<String, Object> parameterMap) throws Exception {
		dataEngine.setFilterMap(filterMap);
		dataEngine.setParameterMap(parameterMap);
		dataEngine.setUserName(userName);
		dataEngine.setValueDate(SysParamUtil.getAppValueDate());
		return dataEngine.exportData("SETTLEMENT_REQUEST_DOWNLOAD");
	}
	
	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.dataSource = dataSource;
	}

	public void setSettlementProcessDAO(SettlementProcessDAO settlementProcessDAO) {
		this.settlementProcessDAO = settlementProcessDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
