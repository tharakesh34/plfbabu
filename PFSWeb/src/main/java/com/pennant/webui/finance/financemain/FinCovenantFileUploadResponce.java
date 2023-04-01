package com.pennant.webui.finance.financemain;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.zkoss.util.media.Media;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinCovenantTypeDAO;
import com.pennant.backend.dao.finance.covenant.CovenantTypeDAO;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantType;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.staticlist.AppStaticList;

public class FinCovenantFileUploadResponce extends BasicDao<FinCovenantType> implements ProcessRecord {

	private DataSource dataSource;

	private List<FinCovenantType> finCovenantType = new ArrayList<FinCovenantType>();
	private List<Covenant> CovenantTypeList = new ArrayList<Covenant>();
	FinCovenantTypeDAO finCovenantTypeDAO;
	String[] allowedRoles;
	List<DocumentType> documentData;
	Map<String, String> covenanatTypeMap = new HashMap<>();
	@Autowired
	CovenantTypeDAO covenantTypeDAO;
	FinanceDetail financeDetail;
	boolean isExists = false;
	private transient List<Property> listFrequency = AppStaticList.getFrequencies();
	private transient List<Property> listAlertType = AppStaticList.getAlertsFor();

	public CovenantTypeDAO getCovenantTypeDAO() {
		return covenantTypeDAO;
	}

	public void setCovenantTypeDAO(CovenantTypeDAO covenantTypeDAO) {
		this.covenantTypeDAO = covenantTypeDAO;
	}

	public FinCovenantFileUploadResponce() {
		super();
	}

	public List<FinCovenantType> finCovenantFileUploadResponceData(Object... params) throws Exception {
		long userId = (Long) params[0];
		DataEngineStatus status = (DataEngineStatus) params[1];
		File file = (File) params[2];
		Media media = (Media) params[3];
		String configName = status.getName();
		allowedRoles = (String[]) params[5];
		documentData = (List<DocumentType>) params[6];
		covenanatTypeMap = new HashMap<>();
		String name = "";

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		status.reset();
		status.setFileName(name);
		status.setRemarks("initiated Collateral upload  file [ " + name + " ] processing..");

		finCovenantType = new ArrayList<>();
		DataEngineImport dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true,
				SysParamUtil.getAppDate(), status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(SysParamUtil.getAppDate());
		Map<String, Object> filterMap = new HashMap<>();
		Map<String, Object> parametersMap = new HashMap<>();
		dataEngine.setParameterMap(parametersMap);
		dataEngine.setFilterMap(filterMap);
		dataEngine.setProcessRecord(this);
		dataEngine.importData(configName);

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				if (status.getStatus().equals("F")) {
					MessageUtil.showError(status.getRemarks());
				}
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		return finCovenantType;
	}

	public List<Covenant> covenantFileUploadResponceData(Object... params) throws Exception {
		long userId = (Long) params[0];
		DataEngineStatus status = (DataEngineStatus) params[1];
		File file = (File) params[2];
		Media media = (Media) params[3];
		String configName = status.getName();
		allowedRoles = (String[]) params[5];
		documentData = (List<DocumentType>) params[6];
		financeDetail = (FinanceDetail) params[7];
		covenanatTypeMap = new HashMap<>();
		String name = "";

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		status.reset();
		status.setFileName(name);
		status.setRemarks("initiated Collateral upload  file [ " + name + " ] processing..");

		CovenantTypeList = new ArrayList<>();
		DataEngineImport dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true,
				SysParamUtil.getAppDate(), status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(SysParamUtil.getAppDate());
		Map<String, Object> filterMap = new HashMap<>();
		Map<String, Object> parametersMap = new HashMap<>();
		dataEngine.setParameterMap(parametersMap);
		dataEngine.setFilterMap(filterMap);
		dataEngine.setProcessRecord(this);
		dataEngine.importData(configName);

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				if (status.getStatus().equals("F")) {
					MessageUtil.showError(
							status.getRemarks().equals("") ? "DATA NOT EXIST IN FILE " : status.getRemarks());
				}
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		return CovenantTypeList;
	}

	private String getStringValue(MapSqlParameterSource record, String key) {
		Object value = record.getValue(key);

		if (value == null) {
			return "";
		}

		return value.toString();
	}

	private int getIntValue(MapSqlParameterSource record, String key) {
		String value = getStringValue(record, key);

		if ("".equals(value)) {
			return 0;
		}

		return Integer.parseInt(value);
	}

	private boolean getBooleanValue(MapSqlParameterSource record, String key) {
		int value = getIntValue(record, key);

		return value == 0 ? false : true;

	}

	@Override
	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource record, Table table)
			throws Exception {
		try {
			if (ImplementationConstants.COVENANT_MODULE_NEW) {

				Date maturityDate = financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate();
				Date loanStartDt = financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate();
				Covenant covenantTypeData = new Covenant();

				covenantTypeData.setCategory(getStringValue(record, "Catagory"));
				covenantTypeData.setCode(getStringValue(record, "CovenantType"));
				covenantTypeData.setDescription(getStringValue(record, "Description"));
				covenantTypeData.setAllowWaiver(getBooleanValue(record, "AlwWaiver"));
				covenantTypeData.setInternalUse(getBooleanValue(record, "IsInternal"));
				covenantTypeData.setMandatoryRole(getStringValue(record, "MandatoryRole"));
				covenantTypeData.setOtc(getBooleanValue(record, "AlwOtc"));
				covenantTypeData.setDocumentReceived(getBooleanValue(record, "DocumentReceived"));
				covenantTypeData.setReceivableDate(
						DateUtil.getDate(getStringValue(record, "ReceivableDate"), "E MMM dd HH:mm:ss Z yyy"));
				covenantTypeData.setFrequency(getStringValue(record, "Frequency"));
				covenantTypeData.setAlertsRequired(getBooleanValue(record, "AlertsRequired"));
				covenantTypeData.setAlertType(getStringValue(record, "AlertType"));
				covenantTypeData.setRemarks(getStringValue(record, "Remarks"));
				covenantTypeData.setAllowPostPonement(getBooleanValue(record, "AlwPostpone"));
				covenantTypeData.setAdditionalField1(getStringValue(record, "Remarks"));
				covenantTypeData.setAdditionalField2(getStringValue(record, "AdditionaliField2"));
				if (isNumeric(getStringValue(record, "AdditionaliField3"))) {
					covenantTypeData.setAdditionalField3(
							(int) Double.parseDouble(getStringValue(record, "AdditionaliField3")) + "");
				} else {
					covenantTypeData.setAdditionalField3(getStringValue(record, "AdditionaliField3"));
				}

				covenantTypeData.setPdd(getBooleanValue(record, "Pdd"));
				covenantTypeData.setExtendedDate(
						DateUtil.getDate(getStringValue(record, "extendedDate"), "E MMM dd HH:mm:ss Z yyy"));

				CovenantType covenantType = covenantTypeDAO.getCovenantTypeId(covenantTypeData.getCode(),
						covenantTypeData.getCategory(), "");

				if (covenantType == null) {
					throw new AppException("Invalid covenantType Code : " + covenantTypeData.getCategory()
							+ " And  Covenant Type : " + covenantTypeData.getCode());
				}
				covenantTypeData.setCovenantType(covenantType.getCovenantType());
				covenantTypeData.setCovenantTypeDescription(covenantType.getDescription());
				if (StringUtils.isBlank(covenantType.getAllowedPaymentModes())) {
					covenantTypeData.setAllowedPaymentModes("");
				} else {
					covenantTypeData.setAllowedPaymentModes(covenantType.getAllowedPaymentModes());
				}
				if (StringUtils.isBlank(covenantTypeData.getDescription())) {
					covenantTypeData.setDescription(covenantType.getDescription());
				}
				if (StringUtils.equals(getStringValue(record, "Gracedays"), "default")) {
					covenantTypeData.setGraceDays(covenantType.getGraceDays());
				} else {
					covenantTypeData.setGraceDays(getIntValue(record, "Gracedays"));
				}
				if (StringUtils.equals(getStringValue(record, "AlertDays"), "default")) {
					covenantTypeData.setAlertDays(covenantType.getAlertDays());
				} else {
					covenantTypeData.setAlertDays(getIntValue(record, "AlertDays"));
				}
				if (StringUtils.isBlank(covenantTypeData.getAlertType()) && covenantTypeData.isAlertsRequired()) {
					covenantTypeData.setAlertType(covenantType.getAlertType());
				}
				if (StringUtils.isBlank(covenantTypeData.getFrequency())) {
					covenantTypeData.setFrequency(covenantType.getFrequency());
				}

				covenantTypeData.setCovenantTypeCode(getStringValue(record, "CovenantType"));

				if (covenantType.getId() == 0 || (Long) covenantType.getId() == null) {
					throw new AppException("Invalid covenantType " + covenantTypeData.getCovenantType());
				}
				if (covenantTypeData.getDescription().toCharArray().length > 500) {
					throw new AppException("description length should be less than 500 charecters");
				}
				covenantTypeData.setCovenantTypeId(covenantType.getId());
				covenantTypeData.setId(Long.MIN_VALUE);

				DocumentType docType = finCovenantTypeDAO.isCovenantTypeExists(covenantTypeData.getCovenantType());
				if (docType != null) {
					covenantTypeData.setCovenantTypeDescription(docType.getDocTypeDesc());
				}

				if (StringUtils.isNotBlank(((String) record.getValue("MandatoryRole")))) {
					SecurityRole secRole = finCovenantTypeDAO
							.isMandRoleExists((String) record.getValue("MandatoryRole"), allowedRoles);
					if (secRole == null) {
						throw new AppException("Invalid Mandatory Role : " + (String) record.getValue("MandatoryRole"));
					} else {
						covenantTypeData.setMandRoleDescription(secRole.getRoleDesc());
					}
				}
				if (StringUtils.isBlank(covenantTypeData.getCovenantType())) {
					throw new AppException("Covenant Type is Mandatory");
				}
				if (StringUtils.isBlank(covenantTypeData.getMandatoryRole()) && !covenantTypeData.isPdd()
						&& !covenantTypeData.isOtc()) {
					throw new AppException("Please select either PDD or OTC or Mandatory Role");
				}
				if (StringUtils.isNotBlank(covenantTypeData.getMandatoryRole()) && covenantTypeData.isPdd()
						&& covenantTypeData.isOtc()) {
					throw new AppException("only one should be selected either PDD or OTC or Mandatory Role");
				}
				if (StringUtils.isNotBlank(covenantTypeData.getMandatoryRole()) && covenantTypeData.isPdd()) {
					throw new AppException("Please select either PDD or Mandatory Role");
				}
				if (StringUtils.isNotBlank(covenantTypeData.getMandatoryRole()) && covenantTypeData.isOtc()) {
					throw new AppException("Please select either OTC or Mandatory Role");
				}
				if (covenantTypeData.isDocumentReceived() && StringUtils.isBlank(covenantTypeData.getFrequency())) {
					throw new AppException("Covenant Frequency Type is Mandatory");
				}

				int graceDays = covenantTypeData.getGraceDays();
				if (!(graceDays >= 0 && graceDays < 30) && !StringUtils.equals(covenantTypeData.getFrequency(), "O")) {
					throw new AppException("Covenant Grace Days should be less then/equal to 30");
				}

				if (StringUtils.isNotBlank(covenantTypeData.getFrequency())
						&& !StringUtils.equals(covenantTypeData.getFrequency(), "O")) {
					if (StringUtils.equals(covenantTypeData.getFrequency(), "M")) {
						if (!(covenantTypeData.getAlertDays() >= 0 && covenantTypeData.getAlertDays() < 30)) {
							throw new AppException("Covenant Aletrs Days should be less then/equal to 30");
						}
					}
					if (StringUtils.equals(covenantTypeData.getFrequency(), "Q")) {
						if (!(covenantTypeData.getAlertDays() >= 0 && covenantTypeData.getAlertDays() < 90)) {
							throw new AppException("Covenant Aletrs Days should be less then/equal to 90");
						}
					}
					if (StringUtils.equals(covenantTypeData.getFrequency(), "H")) {
						if (!(covenantTypeData.getAlertDays() >= 0 && covenantTypeData.getAlertDays() < 180)) {
							throw new AppException("Covenant Aletrs Days should be less then/equal to 180");
						}
					}
					if (StringUtils.equals(covenantTypeData.getFrequency(), "A")) {
						if (!(covenantTypeData.getAlertDays() >= 0 && covenantTypeData.getAlertDays() < 365)) {
							throw new AppException("Covenant Aletrs Days should be less then/equal to 365");
						}
					}
				}

				if (StringUtils.isNotBlank(covenantTypeData.getDocumentReceivedDate() + "")
						&& covenantTypeData.isDocumentReceived()) {
					if (DateUtil.compare(covenantTypeData.getDocumentReceivedDate(),
							SysParamUtil.getAppDate()) > 0) {
						throw new AppException("Future Date is not allowed");
					}
				}

				Date frequencyDate = this.financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate();

				Date appDate = SysParamUtil.getAppDate();

				if (DateUtil.compare(appDate, frequencyDate) < 0) {
					frequencyDate = DateUtil.addMonths(frequencyDate, 1);
				}

				if ("M".equals(covenantTypeData.getFrequency())) {
					frequencyDate = DateUtil.addMonths(frequencyDate, 1);
				} else if ("Q".equals(covenantTypeData.getFrequency())) {
					frequencyDate = DateUtil.addMonths(frequencyDate, 3);
				} else if ("H".equals(covenantTypeData.getFrequency())) {
					frequencyDate = DateUtil.addMonths(frequencyDate, 6);
				} else if ("A".equals(covenantTypeData.getFrequency())) {
					frequencyDate = DateUtil.addMonths(frequencyDate, 12);
				}

				if (frequencyDate != null) {
					if ("O".equals(covenantTypeData.getFrequency()) && covenantTypeData.isAlertsRequired()) {
						covenantTypeData.setNextFrequencyDate(covenantTypeData.getDocumentReceivedDate());
					} else {
						covenantTypeData.setNextFrequencyDate(frequencyDate);
					}

					Date covenantNextFrequencyDate = covenantTypeData.getNextFrequencyDate();

					int covenantGraceDays = 0;
					if (covenantTypeData.getGraceDays() > 0) {
						covenantGraceDays = covenantTypeData.getGraceDays();
					}

					if (covenantNextFrequencyDate != null) {
						covenantTypeData
								.setGraceDueDate(DateUtil.addDays(covenantNextFrequencyDate, covenantGraceDays));
					}

				} else {
					covenantTypeData.setNextFrequencyDate(null);
					covenantTypeData.setGraceDueDate(null);
				}

				if (covenantTypeData.isPdd()) {
					if (covenantTypeData.getReceivableDate() == null) {
						throw new AppException("Receivable date is mandatory.");
					} else if (DateUtil.compare(covenantTypeData.getReceivableDate(), loanStartDt) < 0
							|| DateUtil.compare(covenantTypeData.getReceivableDate(), maturityDate) > 0) {
						throw new AppException("Receivable date is after loan start date " + loanStartDt
								+ " and before maturity date " + maturityDate);
					}
				}
				if (covenantTypeData.isAllowPostPonement()) {
					if (covenantTypeData.getExtendedDate() == null) {
						throw new AppException("Extended date is mandatory.");
					} else if (DateUtil.compare(covenantTypeData.getExtendedDate(), appDate) < 0) {
						throw new AppException("Past Date is not allowed For Extended Date");
					}
				} else {
					covenantTypeData.setExtendedDate(null);
				}
				if (StringUtils.isNotBlank(covenantTypeData.getFrequency())) {
					isExists = false;
					for (Property key : listFrequency) {
						if (StringUtils.equals((String) key.getKey(), covenantTypeData.getFrequency())) {
							isExists = true;
							break;
						}
					}
					if (!isExists) {
						throw new AppException(" Invalid Frequency " + covenantTypeData.getFrequency());
					}
				}
				if (covenantTypeData.isAlertsRequired()) {

					if (StringUtils.isNotBlank(covenantTypeData.getAlertType())) {
						isExists = false;
						for (Property key : listAlertType) {
							if (StringUtils.equals((String) key.getKey(), covenantTypeData.getAlertType())) {
								isExists = true;
								break;
							}
						}
						if (!isExists) {
							throw new AppException(" Invalid Alert Type " + covenantTypeData.getAlertType());
						}
					}
					if (!StringUtils.equals(covenantTypeData.getAlertType(), "Customer")
							&& StringUtils.isNotBlank(covenantTypeData.getAlertType())
							&& covenantTypeData.isAlertsRequired()) {
						if (StringUtils.isBlank(getStringValue(record, "AlertRoles"))) {
							covenantTypeData.setAlertToRoles(covenantType.getAlertToRoles());
						} else {
							covenantTypeData.setAlertToRoles(getStringValue(record, "AlertRoles"));
						}
					} else {
						covenantTypeData.setAlertToRoles("");
					}
					if (StringUtils.isNotBlank(covenantType.getAlertToRoles())
							&& StringUtils.isNotBlank(getStringValue(record, "AlertRoles"))
							&& !StringUtils.equals(covenantTypeData.getAlertType(), "Customer")
							&& StringUtils.isNotBlank(covenantTypeData.getAlertToRoles())) {
						isExists = false;
						String[] roles = covenantTypeData.getAlertToRoles().split(",");
						if (roles.length > 5) {
							throw new AppException("The Number of roles should not exceeded more then 5 ");
						}
						for (String role : roles) {
							isExists = false;
							List<String> rules = covenantTypeDAO.getRules();
							for (String newRole : rules) {
								if (StringUtils.equals(newRole, role)) {
									isExists = true;
									break;
								}
							}
							if (!isExists) {
								throw new AppException(" Invalid Alert Role - " + role);
							}
						}
					}

				} else {
					covenantTypeData.setGraceDays(0);
					covenantTypeData.setAlertDays(0);
					covenantTypeData.setNotifyTo("");
					covenantTypeData.setAlertType(null);
				}
				if (covenantTypeData.isAllowWaiver()) {
					covenantTypeData.setReceivableDate(null);
					covenantTypeData.setDocumentReceived(false);
					covenantTypeData.setDocumentReceivedDate(null);
					covenantTypeData.setFrequency(null);
					covenantTypeData.setNextFrequencyDate(null);
					covenantTypeData.setGraceDays(0);
					covenantTypeData.setGraceDueDate(null);
					covenantTypeData.setNotifyTo(null);
					covenantTypeData.setAlertDays(0);
					covenantTypeData.setMandatoryRole(null);
					covenantTypeData.setExtendedDate(null);
					covenantTypeData.setAdditionalField3(null);
					covenantTypeData.setAdditionalField2(null);
					covenantTypeData.setPdd(false);
				}
				if (covenanatTypeMap.containsKey(covenantTypeData.getCovenantType())) {
					throw new AppException(
							"Duplicate covenantType in file: " + (String) record.getValue("CovenantType"));
				} else {
					covenanatTypeMap.put(covenantTypeData.getCovenantTypeCode(), covenantTypeData.getCovenantType());
				}

				covenantTypeData.setRecordType(PennantConstants.RCD_ADD);
				covenantTypeData.setModule("Loan");
				CovenantTypeList.add(covenantTypeData);

			} else {
				FinCovenantType finCovenantTypeData = new FinCovenantType();

				finCovenantTypeData.setCovenantType(getStringValue(record, "CovenantType"));
				finCovenantTypeData.setDescription(getStringValue(record, "Description"));
				finCovenantTypeData.setMandRole(getStringValue(record, "MandRole"));
				finCovenantTypeData.setAlwWaiver(getBooleanValue(record, "AlwWaiver"));
				finCovenantTypeData.setAlwPostpone(getBooleanValue(record, "AlwPostpone"));
				finCovenantTypeData.setPostponeDays(getIntValue(record, "PostponeDays"));
				finCovenantTypeData.setAlwOtc(getBooleanValue(record, "AlwOtc"));
				finCovenantTypeData.setInternalUse(getBooleanValue(record, "IsInternal"));

				DocumentType docType = finCovenantTypeDAO.isCovenantTypeExists(finCovenantTypeData.getCovenantType());
				if (docType == null) {
					throw new AppException("Invalid covenantType : " + (String) record.getValue("CovenantType"));
				} else {
					finCovenantTypeData.setCovenantTypeDesc(docType.getDocTypeDesc());
				}

				if (StringUtils.isNotBlank(((String) record.getValue("MandRole")))) {
					SecurityRole secRole = finCovenantTypeDAO.isMandRoleExists((String) record.getValue("MandRole"),
							allowedRoles);
					if (secRole == null) {
						throw new AppException("Invalid Mandatory Role : " + (String) record.getValue("MandRole"));
					} else {
						finCovenantTypeData.setMandRoleDesc(secRole.getRoleDesc());
					}
				}

				if (finCovenantTypeData.isAlwWaiver()) {
					finCovenantTypeData.setAlwPostpone(false);
					finCovenantTypeData.setPostponeDays(0);
					finCovenantTypeData.setAlwOtc(false);
					finCovenantTypeData.setMandRole("");
					finCovenantTypeData.setMandRoleDesc("");
				} else if (finCovenantTypeData.getMandRole().equals("")) {
					if (!finCovenantTypeData.isAlwOtc() && !finCovenantTypeData.isAlwPostpone()
							&& !finCovenantTypeData.isAlwWaiver()) {
						throw new AppException("Either AlwPostPone Or AlwOtc or AlwWaver is 1");
					}
				}

				if (StringUtils.isNotEmpty(finCovenantTypeData.getMandRole())) {
					finCovenantTypeData.setAlwPostpone(false);
					finCovenantTypeData.setAlwOtc(false);
					finCovenantTypeData.setAlwWaiver(false);
					finCovenantTypeData.setPostponeDays(0);
				} else if (finCovenantTypeData.isAlwPostpone()) {
					finCovenantTypeData.setMandRole("");
					finCovenantTypeData.setMandRoleDesc("");
					finCovenantTypeData.setAlwOtc(false);
					finCovenantTypeData.setAlwWaiver(false);
				} else if (finCovenantTypeData.isAlwOtc()) {
					finCovenantTypeData.setMandRole("");
					finCovenantTypeData.setMandRoleDesc("");
					finCovenantTypeData.setAlwPostpone(false);
					finCovenantTypeData.setAlwWaiver(false);
					finCovenantTypeData.setPostponeDays(0);
				}

				if (documentData != null) {
					if (finCovenantTypeData.isAlwPostpone()) {
						int count = 0;
						for (int i = 0; i < documentData.size(); i++) {
							count++;
							if (finCovenantTypeData.getCovenantType().equals(documentData.get(i).getDocTypeCode())) {
								count = 0;
								break;
							}
						}
						if (count > 0) {
							throw new AppException("Alw Postponement not allowed for this Covenant type.");
						}
					}
					Date receivableDate;
					Date maxCovreceiveDate = DateUtil.addDays(SysParamUtil.getAppDate(),
							+SysParamUtil.getValueAsInt("FUTUREDAYS_COV_RECEIVED_DATE"));
					if (finCovenantTypeData.isAlwPostpone()) {
						receivableDate = DateUtil.addDays(SysParamUtil.getAppDate(),
								finCovenantTypeData.getPostponeDays());
						finCovenantTypeData.setReceivableDate(receivableDate);
						if (finCovenantTypeData.isAlwPostpone() && finCovenantTypeData.getPostponeDays() <= 0) {
							throw new AppException("Postpone Days should be greater than zero.");
						} else {
							if (DateUtil.compare(finCovenantTypeData.getReceivableDate(),
									SysParamUtil.getAppDate()) > 0
									&& DateUtil.compare(finCovenantTypeData.getReceivableDate(),
											maxCovreceiveDate) < 0) {
							} else {
								throw new AppException("Receivable date :" + finCovenantTypeData.getReceivableDate()
										+ " is after application date : " + SysParamUtil.getAppDate() + ", "
										+ " and before max covenant received date : " + maxCovreceiveDate);
							}

						}
					}

					if (finCovenantTypeData.isAlwOtc()) {
						int otcCount = 0;
						for (int i = 0; i < documentData.size(); i++) {
							otcCount++;
							if (finCovenantTypeData.getCovenantType().equals(documentData.get(i).getDocTypeCode())) {
								otcCount = 0;
								break;
							}
						}
						if (otcCount > 0) {
							throw new AppException("Alw Otc not allowed for this Covenant type.");
						}
					}
				}

				if (covenanatTypeMap.containsKey(finCovenantTypeData.getCovenantType())) {
					throw new AppException(
							"Duplicate covenantType in file: " + (String) record.getValue("CovenantType"));
				} else {
					covenanatTypeMap.put(finCovenantTypeData.getCovenantType(), finCovenantTypeData.getCovenantType());
				}

				finCovenantTypeData.setRecordType(PennantConstants.RCD_ADD);

				finCovenantType.add(finCovenantTypeData);
			}
		} catch (Exception e) {
			throw new AppException(e.getMessage());

		}

	}

	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public FinCovenantTypeDAO getFinCovenantTypeDAO() {
		return finCovenantTypeDAO;
	}

	public void setFinCovenantTypeDAO(FinCovenantTypeDAO finCovenantTypeDAO) {
		this.finCovenantTypeDAO = finCovenantTypeDAO;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
