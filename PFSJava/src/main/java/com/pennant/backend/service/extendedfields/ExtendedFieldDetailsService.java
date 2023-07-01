package com.pennant.backend.service.extendedfields;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.collateral.impl.ExtendedFieldDetailsValidation;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.service.extendedfieldsExtension.ExtendedFieldExtensionService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.sampling.dao.SamplingDAO;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;

public class ExtendedFieldDetailsService {
	private static final Logger logger = LogManager.getLogger(ExtendedFieldDetailsService.class);

	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private ScriptValidationService scriptValidationService;
	private ExtendedFieldHeaderDAO extendedFieldHeaderDAO;
	private ExtendedFieldDetailDAO extendedFieldDetailDAO;
	private ExtendedFieldDetailsValidation extendedFieldDetailsValidation;
	private AuditHeaderDAO auditHeaderDAO;
	@Autowired(required = false)
	private DedupParmService dedupParmService;
	private ExtendedFieldExtensionService extendedFieldExtensionService;

	@Autowired
	protected SamplingDAO samplingDAO;

	public Object getLoanOrgExtendedValue(String reference, String key) {
		Object value = null;
		List<Map<String, Object>> list = getLoanOrgExtendedFilds(reference);

		if (CollectionUtils.isEmpty(list)) {
			return value;
		}

		for (Map<String, Object> map : list) {
			if (map.containsKey(key)) {
				value = map.get(key);
				break;
			}
		}

		return value;
	}

	public List<ExtendedFieldData> getExtendedFildValueLableList(String tableName, String reference, String tableType) {
		logger.debug("Entering");

		ExtendedFieldHeader fieldHeader = getExtendedFieldHeader(tableName);

		if (fieldHeader == null) {
			return null;
		}

		List<ExtendedFieldDetail> fieldDetailsList = fieldHeader.getExtendedFieldDetails();
		if (fieldDetailsList != null && !fieldDetailsList.isEmpty()) {
			return null;
		}

		List<Map<String, Object>> fiildValueMap = extendedFieldRenderDAO.getExtendedFieldMap(reference, tableName,
				tableType);

		List<ExtendedFieldData> resultList = new ArrayList<>();

		if (fiildValueMap != null && !fiildValueMap.isEmpty()) {
			for (Map<String, Object> map : fiildValueMap) {
				resultList.add(getExtendedFieldData(fieldDetailsList, map));
			}
		}
		logger.debug("Leaving");
		return resultList;
	}

	private ExtendedFieldData getExtendedFieldData(List<ExtendedFieldDetail> fieldDetailsList,
			Map<String, Object> map) {
		ExtendedFieldData extendedFieldData = null;

		for (ExtendedFieldDetail detail : fieldDetailsList) {
			if (map.containsKey(detail.getFieldName())) {
				extendedFieldData = new ExtendedFieldData();
				extendedFieldData.setFieldValue(map.get(detail.getFieldName()));
				extendedFieldData.setFieldName(detail.getFieldName());
				extendedFieldData.setFieldType(detail.getFieldType());
				extendedFieldData.setFieldLabel(detail.getFieldLabel());
			}
		}
		return extendedFieldData;
	}

	public Map<String, Object> getCollateralMap(String tableName, String reference) {
		logger.debug(Literal.ENTERING);
		Map<String, Object> finalMap = new HashMap<>();

		Map<String, Object> originalMap = extendedFieldRenderDAO.getCollateralMap(reference, tableName, "");

		if (MapUtils.isNotEmpty(originalMap)) {
			for (Entry<String, Object> object : originalMap.entrySet()) {
				finalMap.put(object.getKey().toLowerCase(), object.getValue());
			}
		}

		return finalMap;
	}

	public Map<String, ExtendedFieldData> getCollateralFields(String tableName, String reference) {
		logger.debug(Literal.ENTERING);
		Map<String, ExtendedFieldData> resultList = new HashMap<>();

		String tempTableName = tableName;
		if (tempTableName.startsWith("verification")) {
			tempTableName = tempTableName.replace("verification", "collateral");
			tempTableName = tempTableName.replace("tv", "ed");
		}

		ExtendedFieldHeader fieldHeader = getExtendedFieldHeader(tempTableName);

		if (fieldHeader == null) {
			return resultList;
		}

		List<ExtendedFieldDetail> fieldDetailsList = fieldHeader.getExtendedFieldDetails();
		if (CollectionUtils.isEmpty(fieldDetailsList)) {
			return resultList;
		}

		return getResultMap(fieldDetailsList, getCollateralMap(tableName, reference));
	}

	private Map<String, ExtendedFieldData> getResultMap(List<ExtendedFieldDetail> fieldDetailsList,
			Map<String, Object> map) {
		Map<String, ExtendedFieldData> resultMap = new HashMap<>();
		ExtendedFieldData fieldData = null;

		for (ExtendedFieldDetail detail : fieldDetailsList) {
			String key = detail.getFieldName().toLowerCase();
			if (map.containsKey(key)) {
				fieldData = new ExtendedFieldData();
				fieldData.setFieldValue(map.get(key));
				fieldData.setFieldName(detail.getFieldName());
				fieldData.setFieldType(detail.getFieldType());
				fieldData.setFieldLabel(detail.getFieldLabel());
				fieldData.setFieldPrec(detail.getFieldPrec());
				resultMap.put(detail.getFieldName(), fieldData);
			}
		}
		return resultMap;
	}

	private ExtendedFieldHeader getExtendedFieldHeader(String tableName) {
		String[] strings = StringUtils.split(tableName, "_");
		boolean isLoan = false;
		String moduleName = null;
		String subModuleName = null;
		String eventCode = null;
		int extendedType = ExtendedFieldConstants.EXTENDEDTYPE_EXTENDEDFIELD;

		if (strings.length >= 3) {
			moduleName = strings[0];
			if (ExtendedFieldConstants.MODULE_LOAN.equalsIgnoreCase(moduleName)) {
				isLoan = true;
			}
			if (ExtendedFieldConstants.MODULE_VERIFICATION.equalsIgnoreCase(moduleName)) {
				extendedType = ExtendedFieldConstants.EXTENDEDTYPE_TECHVALUATION;
			}
			subModuleName = strings[1];
			if (isLoan) {
				eventCode = strings[2];
			}
		}

		ExtendedFieldHeader extFldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(moduleName,
				subModuleName, eventCode, "_AView");
		if (extFldHeader != null) {
			extFldHeader.setExtendedFieldDetails(extendedFieldDetailDAO
					.getExtendedFieldDetailById(extFldHeader.getModuleId(), extendedType, "_AView"));
		}
		return extFldHeader;
	}

	public List<Map<String, Object>> getLoanOrgExtendedFilds(String reference) {
		logger.debug(Literal.ENTERING);

		String tableName = null;

		String finCategory = extendedFieldRenderDAO.getCategory(reference);
		if (finCategory != null) {
			tableName = getTableName(ExtendedFieldConstants.MODULE_LOAN, finCategory, FinServiceEvent.ORG);
		}

		if (tableName != null) {
			return extendedFieldRenderDAO.getExtendedFieldMap(reference, tableName, "_view");
		}

		return new ArrayList<>();
	}

	public List<AuditDetail> setExtendedFieldsAuditData(List<ExtendedFieldRender> list, String tranType, String method,
			String module) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (int i = 0; i < list.size(); i++) {
			ExtendedFieldRender efr = list.get(i);
			AuditDetail auditDetail = setExtendedFieldAuditData(efr, tranType, method, i + 1, module);
			if (auditDetail != null) {
				auditDetails.add(auditDetail);
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	public List<AuditDetail> setExtendedFieldsAuditData(ExtendedFieldRender efr, String tranType, String method,
			String module) {
		logger.debug(Literal.ENTERING);
		int auditSeq = 1;
		List<AuditDetail> auditDetails = new ArrayList<>();

		AuditDetail auditDetail = setExtendedFieldAuditData(efr, tranType, method, auditSeq, module);
		if (auditDetail == null) {
			return auditDetails;
		}

		auditDetails.add(auditDetail);
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public List<AuditDetail> setExtendedFieldsAuditData(ExtendedFieldHeader efh, ExtendedFieldRender render,
			String tranType, String method, String module) {
		logger.debug(Literal.ENTERING);
		int auditSeq = 1;
		List<AuditDetail> auditDetails = new ArrayList<>();

		if (render.getTableName() == null) {
			render.setTableName(getTableName(efh.getModuleName(), efh.getSubModuleName(), efh.getEvent()));
		}

		AuditDetail auditDetail = setExtendedFieldAuditData(render, tranType, method, auditSeq, module);
		if (auditDetail == null) {
			return auditDetails;
		}

		auditDetails.add(auditDetail);
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public AuditDetail setExtendedFieldAuditData(ExtendedFieldRender efr, String tranType, String method, int auditSeq,
			String module) {
		logger.debug(Literal.ENTERING);

		if (efr == null) {
			return null;
		}
		if (StringUtils.isEmpty(StringUtils.trimToEmpty(efr.getRecordType()))) {
			return null;
		}

		boolean isRcdType = false;
		if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			efr.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			isRcdType = true;
		} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)
				|| efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_EDT)) {
			efr.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			if (efr.isWorkflow() && !efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_EDT)) {
				isRcdType = true;
			}
		} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
			efr.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		}

		if ("saveOrUpdate".equals(method) && (isRcdType)) {
			efr.setNewRecord(true);
		}

		if (!tranType.equals(PennantConstants.TRAN_WF)) {
			if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					|| efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				tranType = PennantConstants.TRAN_DEL;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// Audit Details Preparation
		Map<String, Object> auditMapValues = efr.getMapValues();
		auditMapValues.put("Reference", efr.getReference());
		auditMapValues.put("SeqNo", efr.getSeqNo());
		auditMapValues.put("Version", efr.getVersion());
		auditMapValues.put("LastMntOn", efr.getLastMntOn());
		auditMapValues.put("LastMntBy", efr.getLastMntBy());
		auditMapValues.put("RecordStatus", efr.getRecordStatus());
		auditMapValues.put("RoleCode", efr.getRoleCode());
		auditMapValues.put("NextRoleCode", efr.getNextRoleCode());
		auditMapValues.put("TaskId", efr.getTaskId());
		auditMapValues.put("NextTaskId", efr.getNextTaskId());
		auditMapValues.put("RecordType", efr.getRecordType());
		auditMapValues.put("WorkflowId", efr.getWorkflowId());

		// FIXME:Need to rechecks for which case InstructioinUid Required.
		if (StringUtils.equals(ExtendedFieldConstants.MODULE_LOAN, module)) {
			String tableName = StringUtils.trimToEmpty(efr.getTableName()).toUpperCase();
			if (tableName.startsWith("LOAN_") && tableName.endsWith("_ED")) {
				auditMapValues.put("InstructionUID", efr.getInstructionUID());
			}
		}

		efr.setAuditMapValues(auditMapValues);

		String[] fields = PennantJavaUtil.getExtendedFieldDetails(efr);
		AuditDetail auditDetail = new AuditDetail(tranType, auditSeq, fields[0], fields[1], efr.getBefImage(), efr);
		auditDetail.setExtended(true);

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Extended FieldDetails
	 * 
	 * @param deatils
	 * @param collateralSetup
	 * @param type
	 * @return
	 */
	public List<AuditDetail> processingExtendedFieldDetailList(List<AuditDetail> deatils, ExtendedFieldHeader efh,
			String type, long instructionUID) {
		logger.debug(Literal.ENTERING);

		String tableName = getTableName(efh.getModuleName(), efh.getSubModuleName(), efh.getEvent());

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < deatils.size(); i++) {
			ExtendedFieldRender efr = (ExtendedFieldRender) deatils.get(i).getModelData();
			if (StringUtils.isEmpty(efr.getRecordType())) {
				continue;
			}
			boolean exists = extendedFieldRenderDAO.isExists(efr.getReference(), efr.getSeqNo(), tableName + type);

			if (PennantConstants.RCD_STATUS_SUBMITTED.equals(efr.getRecordStatus())
					&& PennantConstants.RECORD_TYPE_UPD.equals(efr.getRecordType())) {
				if (!exists) {
					efr.setNewRecord(true);
				} else {
					efr.setNewRecord(false);
				}
			}
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				efr.setRoleCode("");
				efr.setNextRoleCode("");
				efr.setTaskId("");
				efr.setNextTaskId("");
				efr.setWorkflowId(0);
			}

			// Table Name addition for Audit
			efr.setTableName(tableName);
			efr.setWorkflowId(0);

			if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (efr.isNewRecord()) {
				saveRecord = true;
				if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					efr.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					efr.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					efr.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(efr.getRecordType()) && exists) {
					// If saved record has been updated then it should be updated in the table.
					updateRecord = true;
					saveRecord = false;
				}

			} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (efr.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = efr.getRecordType();
				recordStatus = efr.getRecordStatus();
				efr.setRecordType("");
				efr.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			// Add Common Fields
			Map<String, Object> mapValues = efr.getMapValues();
			if (saveRecord || updateRecord) {
				if (saveRecord) {
					mapValues.put("Reference", efr.getReference());
					mapValues.put("SeqNo", efr.getSeqNo());
				}

				mapValues.put("Version", efr.getVersion());
				mapValues.put("LastMntOn", efr.getLastMntOn());
				mapValues.put("LastMntBy", efr.getLastMntBy());
				mapValues.put("RecordStatus", efr.getRecordStatus());
				mapValues.put("RoleCode", efr.getRoleCode());
				mapValues.put("NextRoleCode", efr.getNextRoleCode());
				mapValues.put("TaskId", efr.getTaskId());
				mapValues.put("NextTaskId", efr.getNextTaskId());
				mapValues.put("RecordType", efr.getRecordType());
				mapValues.put("WorkflowId", efr.getWorkflowId());
				if (StringUtils.equals(ExtendedFieldConstants.MODULE_LOAN, efh.getModuleName())) {
					efr.setInstructionUID(instructionUID);
					mapValues.put("InstructionUID", efr.getInstructionUID());
				}
			}

			if (saveRecord) {
				extendedFieldRenderDAO.save(efr.getMapValues(), type, tableName);
			}

			if (updateRecord) {
				extendedFieldRenderDAO.update(efr.getReference(), efr.getSeqNo(), efr.getMapValues(), type, tableName);
			}

			if (deleteRecord) {
				extendedFieldRenderDAO.delete(efr.getReference(), efr.getSeqNo(), type, tableName);
			}
			if (approveRec) {
				efr.setRecordType(rcdType);
				efr.setRecordStatus(recordStatus);
			}

			// Setting Extended field is to identify record related to Extended
			// fields
			if (deatils.get(i).getBefImage() != null) {
				ExtendedFieldRender befImage = (ExtendedFieldRender) deatils.get(i).getBefImage();
				befImage.setTableName(tableName);
			}

			efr.setBefImage(efr);
			deatils.get(i).setExtended(true);
			deatils.get(i).setModelData(efr);
		}
		logger.debug(Literal.LEAVING);
		return deatils;
	}

	/**
	 * Method For Preparing List of AuditDetails for Extended FieldDetails
	 * 
	 * @param deatils
	 * @param extFldHeader
	 * @param tableName
	 * @param type
	 * @return
	 */
	public List<AuditDetail> processingExtendedFieldDetailList(List<AuditDetail> deatils, String tableName,
			String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < deatils.size(); i++) {
			ExtendedFieldRender efr = (ExtendedFieldRender) deatils.get(i).getModelData();
			String recordType = efr.getRecordType();

			if (StringUtils.isEmpty(recordType)) {
				continue;
			}

			if (PennantConstants.RCD_STATUS_APPROVED.equals(efr.getRecordStatus())
					&& PennantConstants.RECORD_TYPE_UPD.equals(recordType)) {
				if (!extendedFieldRenderDAO.isExists(efr.getReference(), efr.getSeqNo(), tableName + type)) {
					efr.setNewRecord(true);
				}
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				efr.setRoleCode("");
				efr.setNextRoleCode("");
				efr.setTaskId("");
				efr.setNextTaskId("");
			}

			// Table Name addition for Audit
			efr.setTableName(tableName);
			efr.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(recordType)) {
				deleteRecord = true;
			} else if (efr.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(recordType)) {
					efr.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(recordType)) {
					efr.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(recordType)) {
					efr.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(recordType)) {
					// If saved record has been updated then it should be updated in the table.
					updateRecord = true;
					saveRecord = false;
				}

			} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(recordType)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(recordType)) {
				if (!extendedFieldRenderDAO.isExists(efr.getReference(), efr.getSeqNo(), tableName + type)) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(recordType)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (efr.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = recordType;
				recordStatus = efr.getRecordStatus();
				efr.setRecordType("");
				efr.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			// Add Common Fields
			Map<String, Object> mapValues = efr.getMapValues();

			if (saveRecord || updateRecord) {
				if (saveRecord) {
					mapValues.put("Reference", efr.getReference());
					mapValues.put("SeqNo", efr.getSeqNo());
				}

				mapValues.put("Version", efr.getVersion());
				mapValues.put("LastMntOn", efr.getLastMntOn());
				mapValues.put("LastMntBy", efr.getLastMntBy());
				mapValues.put("RecordStatus", efr.getRecordStatus());
				mapValues.put("RoleCode", efr.getRoleCode());
				mapValues.put("NextRoleCode", efr.getNextRoleCode());
				mapValues.put("TaskId", efr.getTaskId());
				mapValues.put("NextTaskId", efr.getNextTaskId());
				mapValues.put("RecordType", recordType);
				mapValues.put("WorkflowId", efr.getWorkflowId());
			}

			if (saveRecord) {
				extendedFieldRenderDAO.save(efr.getMapValues(), type, tableName);
			}

			if (updateRecord) {
				extendedFieldRenderDAO.update(efr.getReference(), efr.getSeqNo(), efr.getMapValues(), type, tableName);
			}

			if (deleteRecord) {
				extendedFieldRenderDAO.delete(efr.getReference(), efr.getSeqNo(), type, tableName);
			}
			if (approveRec) {
				efr.setRecordType(rcdType);
				efr.setRecordStatus(recordStatus);
			}

			// Setting Extended field is to identify record related to Extended
			// fields
			efr.setBefImage(efr);
			deatils.get(i).setExtended(true);
			deatils.get(i).setModelData(efr);
		}

		logger.debug(Literal.LEAVING);
		return deatils;
	}

	public List<AuditDetail> processingExtendedFieldDetailList(List<AuditDetail> details, String module, String event,
			String type, long instructionUID) {
		logger.debug(Literal.ENTERING);
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < details.size(); i++) {
			if (details.get(i) != null) {
				ExtendedFieldRender efr = (ExtendedFieldRender) details.get(i).getModelData();

				if (StringUtils.isEmpty(efr.getRecordType())) {
					continue;
				}

				String tableName = getTableName(module, efr.getTypeCode(), event);
				int maxSeqNo = extendedFieldRenderDAO.getMaxSeqNoByRef(efr.getReference(), tableName);

				if (!FinServiceEvent.REALIZATION.equals(event)) {
					if ((FinServiceEvent.ADDDISB.equals(event))
							|| (PennantConstants.RCD_STATUS_SUBMITTED.equals(efr.getRecordStatus())
									&& PennantConstants.RECORD_TYPE_UPD.equals(efr.getRecordType()))) {
						if (!extendedFieldRenderDAO.isExists(efr.getReference(), maxSeqNo + 1, tableName + type)) {
							efr.setNewRecord(true);
						}
					}
				}

				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				if (StringUtils.isEmpty(type)) {
					approveRec = true;
					efr.setRoleCode("");
					efr.setNextRoleCode("");
					efr.setTaskId("");
					efr.setNextTaskId("");
				}

				// Table Name addition for Audit
				efr.setTableName(tableName);
				// extendedFieldRender.setWorkflowId(0);

				if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (efr.isNewRecord()) {
					saveRecord = true;
					if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						efr.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						efr.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						efr.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (efr.isNewRecord()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				if (approveRec) {
					rcdType = efr.getRecordType();
					recordStatus = efr.getRecordStatus();
					efr.setRecordType("");
					efr.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					extendedFieldRenderDAO.delete(efr.getReference(), efr.getSeqNo(), "_Temp", tableName);
				}

				// Add Common Fields
				Map<String, Object> mapValues = efr.getMapValues();
				if (saveRecord || updateRecord || approveRec) {
					if (saveRecord) {
						mapValues.put("Reference", efr.getReference());
						mapValues.put("SeqNo", efr.getSeqNo());
					}

					mapValues.put("SeqNo", efr.getSeqNo());
					mapValues.put("Version", efr.getVersion());
					mapValues.put("LastMntOn", efr.getLastMntOn());
					mapValues.put("LastMntBy", efr.getLastMntBy());
					mapValues.put("RecordStatus", efr.getRecordStatus());
					mapValues.put("RoleCode", efr.getRoleCode());
					mapValues.put("NextRoleCode", efr.getNextRoleCode());
					mapValues.put("TaskId", efr.getTaskId());
					mapValues.put("NextTaskId", efr.getNextTaskId());
					mapValues.put("RecordType", efr.getRecordType());
					mapValues.put("WorkflowId", efr.getWorkflowId());
					if (StringUtils.equals(ExtendedFieldConstants.MODULE_LOAN, module)) {
						efr.setInstructionUID(instructionUID);
						mapValues.put("InstructionUID", efr.getInstructionUID());
					}
				}

				if (saveRecord) {
					extendedFieldRenderDAO.save(efr.getMapValues(), type, tableName);
				}

				if (updateRecord) {
					extendedFieldRenderDAO.update(efr.getReference(), efr.getSeqNo(), efr.getMapValues(), type,
							tableName);
				}

				if (deleteRecord) {
					extendedFieldRenderDAO.delete(efr.getReference(), efr.getSeqNo(), type, tableName);
				}
				if (approveRec) {
					efr.setRecordType(rcdType);
					efr.setRecordStatus(recordStatus);
				}

				// Setting Extended field is to identify record related to
				// Extended fields
				efr.setBefImage(efr);
				details.get(i).setExtended(true);
				details.get(i).setModelData(efr);
			}
		}
		logger.debug(Literal.LEAVING);
		return details;
	}

	public List<AuditDetail> processingSamplingExtendedFieldDetailList(List<AuditDetail> deatils, Sampling sampling,
			String moduleCode, TableType type) {

		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		Map<String, ExtendedFieldHeader> extHeaderMap = sampling.getExtFieldHeaderList();
		for (int i = 0; i < deatils.size(); i++) {
			ExtendedFieldRender efr = (ExtendedFieldRender) deatils.get(i).getModelData();
			String reference = efr.getReference();
			if (StringUtils.isEmpty(efr.getRecordType())) {
				continue;
			}

			String linkId = StringUtils.trimToEmpty(reference);
			linkId = linkId.replaceAll("S", "");
			String collRef = samplingDAO.getCollateralRef(sampling, linkId);

			ExtendedFieldHeader extHeader = extHeaderMap.get(collRef);

			StringBuilder tableName = new StringBuilder();
			tableName.append(moduleCode);
			tableName.append("_");
			tableName.append(extHeader.getSubModuleName());
			tableName.append("_tv");

			if (StringUtils.equals(efr.getRecordStatus(), PennantConstants.RCD_STATUS_SUBMITTED)
					&& StringUtils.equals(efr.getRecordType(), PennantConstants.RECORD_TYPE_UPD)) {
				if (!extendedFieldRenderDAO.isExists(reference, efr.getSeqNo(),
						tableName.toString() + type.getSuffix())) {
					efr.setNewRecord(true);
				}
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				efr.setRoleCode("");
				efr.setNextRoleCode("");
				efr.setTaskId("");
				efr.setNextTaskId("");
			}

			// Table Name addition for Audit
			efr.setTableName(tableName.toString());
			efr.setWorkflowId(0);

			if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (efr.isNewRecord()) {
				saveRecord = true;
				if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					efr.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					efr.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					efr.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (efr.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = efr.getRecordType();
				recordStatus = efr.getRecordStatus();
				efr.setRecordType("");
				efr.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			// Add Common Fields
			Map<String, Object> mapValues = efr.getMapValues();
			if (saveRecord || updateRecord) {
				if (saveRecord) {
					if (!StringUtils.startsWith(reference, "S")) {
						mapValues.put("Reference", "S".concat(reference));
					} else {
						mapValues.put("Reference", reference);
					}
					mapValues.put("SeqNo", efr.getSeqNo());
				}

				mapValues.put("Version", efr.getVersion());
				mapValues.put("LastMntOn", efr.getLastMntOn());
				mapValues.put("LastMntBy", efr.getLastMntBy());
				mapValues.put("RecordStatus", efr.getRecordStatus());
				mapValues.put("RoleCode", efr.getRoleCode());
				mapValues.put("NextRoleCode", efr.getNextRoleCode());
				mapValues.put("TaskId", efr.getTaskId());
				mapValues.put("NextTaskId", efr.getNextTaskId());
				mapValues.put("RecordType", efr.getRecordType());
				mapValues.put("WorkflowId", efr.getWorkflowId());
			}

			if (saveRecord) {
				extendedFieldRenderDAO.save(efr.getMapValues(), type.getSuffix(), tableName.toString());
			}

			if (updateRecord) {
				if (approveRec) {
					// Handle on approve after resubmit(for got to add
					// collaterals on initial approve)
					if (extendedFieldRenderDAO.isExists(reference, efr.getSeqNo(), tableName + type.getSuffix())) {
						extendedFieldRenderDAO.update(reference, efr.getSeqNo(), efr.getMapValues(), type.getSuffix(),
								tableName.toString());
					} else {
						extendedFieldRenderDAO.save(efr.getMapValues(), type.getSuffix(), tableName.toString());
					}
				}

				extendedFieldRenderDAO.update(reference, efr.getSeqNo(), efr.getMapValues(), type.getSuffix(),
						tableName.toString());
			}

			if (deleteRecord) {
				extendedFieldRenderDAO.delete(reference, efr.getSeqNo(), type.getSuffix(), tableName.toString());
			}
			if (approveRec) {
				efr.setRecordType(rcdType);
				efr.setRecordStatus(recordStatus);
			}

			// Setting Extended field is to identify record related to Extended
			// fields
			efr.setBefImage(efr);
			deatils.get(i).setExtended(true);
			deatils.get(i).setModelData(efr);
		}
		logger.debug(Literal.LEAVING);
		return deatils;
	}

	public List<AuditDetail> delete(ExtendedFieldHeader efh, String reference, int seqNo, String tableType,
			String tranType, List<AuditDetail> deatils) {

		StringBuilder tableName = new StringBuilder();
		tableName.append(efh.getModuleName());
		tableName.append("_");
		tableName.append(efh.getSubModuleName());
		if (efh.getEvent() != null) {
			tableName.append("_");
			tableName.append(PennantStaticListUtil.getFinEventCode(efh.getEvent()));
		}
		tableName.append("_ED");

		return deleteDetails(reference, seqNo, tableType, tranType, deatils, tableName.toString());
	}

	private List<AuditDetail> deleteDetails(String reference, int seqNo, String tableType, String tranType,
			List<AuditDetail> deatils, String tableName) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldRender efr;
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		for (int i = 0; i < deatils.size(); i++) {
			efr = (ExtendedFieldRender) deatils.get(i).getModelData();
			efr.setTableName(tableName);

			if (StringUtils.isEmpty(tableType)) {
				efr.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			} else {
				efr.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}

			// Audit Details Preparation
			Map<String, Object> auditMapValues = efr.getMapValues();
			auditMapValues.put("Reference", reference);
			auditMapValues.put("SeqNo", efr.getSeqNo());
			auditMapValues.put("Version", efr.getVersion());
			auditMapValues.put("LastMntOn", efr.getLastMntOn());
			auditMapValues.put("LastMntBy", efr.getLastMntBy());
			auditMapValues.put("RecordStatus", efr.getRecordStatus());
			auditMapValues.put("RoleCode", efr.getRoleCode());
			auditMapValues.put("NextRoleCode", efr.getNextRoleCode());
			auditMapValues.put("TaskId", efr.getTaskId());
			auditMapValues.put("NextTaskId", efr.getNextTaskId());
			auditMapValues.put("RecordType", efr.getRecordType());
			auditMapValues.put("WorkflowId", efr.getWorkflowId());

			// Audit Saving Purpose
			efr.setAuditMapValues(auditMapValues);
			efr.setBefImage(efr);

			String[] fields = PennantJavaUtil.getExtendedFieldDetails(efr);
			AuditDetail auditDetail = new AuditDetail(tranType, i + 1, fields[0], fields[1], efr.getBefImage(), efr);
			auditDetail.setExtended(true);

			auditList.add(auditDetail);
		}
		extendedFieldRenderDAO.deleteList(reference, seqNo, tableName, tableType);

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	public List<AuditDetail> delete(ExtendedFieldHeader extFldHeader, String reference, String tableType,
			String tranType, List<AuditDetail> deatils) {

		StringBuilder tableName = new StringBuilder();
		tableName.append(extFldHeader.getModuleName());
		tableName.append("_");
		tableName.append(extFldHeader.getSubModuleName());
		if (extFldHeader.getEvent() != null) {
			tableName.append("_");
			tableName.append(PennantStaticListUtil.getFinEventCode(extFldHeader.getEvent()));
		}
		tableName.append("_ED");

		return deleteDetails(reference, tableType, tranType, deatils, tableName.toString());
	}

	public List<AuditDetail> delete(ExtendedFieldHeader extFldHeader, String reference, String tableName,
			String tableType, String tranType, List<AuditDetail> deatils) {
		return deleteDetails(reference, tableType, tranType, deatils, tableName);
	}

	public List<AuditDetail> delete(ExtendedFieldHeader extFldHeader, String reference, String tableName,
			String tableType, String tranType, AuditDetail deatils) {
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditDetails.add(deatils);
		return deleteDetails(reference, tableType, tranType, auditDetails, tableName);
	}

	private List<AuditDetail> deleteDetails(String reference, String tableType, String tranType,
			List<AuditDetail> deatils, String tableName) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldRender fieldRender;
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		for (int i = 0; i < deatils.size(); i++) {
			fieldRender = (ExtendedFieldRender) deatils.get(i).getModelData();
			fieldRender.setTableName(tableName);

			if (StringUtils.isEmpty(tableType)) {
				fieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			} else {
				fieldRender.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}

			// Audit Details Preparation
			Map<String, Object> auditMapValues = fieldRender.getMapValues();
			auditMapValues.put("Reference", reference);
			auditMapValues.put("SeqNo", fieldRender.getSeqNo());
			auditMapValues.put("Version", fieldRender.getVersion());
			auditMapValues.put("LastMntOn", fieldRender.getLastMntOn());
			auditMapValues.put("LastMntBy", fieldRender.getLastMntBy());
			auditMapValues.put("RecordStatus", fieldRender.getRecordStatus());
			auditMapValues.put("RoleCode", fieldRender.getRoleCode());
			auditMapValues.put("NextRoleCode", fieldRender.getNextRoleCode());
			auditMapValues.put("TaskId", fieldRender.getTaskId());
			auditMapValues.put("NextTaskId", fieldRender.getNextTaskId());
			auditMapValues.put("RecordType", fieldRender.getRecordType());
			auditMapValues.put("WorkflowId", fieldRender.getWorkflowId());

			// Audit Saving Purpose
			fieldRender.setAuditMapValues(auditMapValues);
			fieldRender.setBefImage(fieldRender);

			String[] fields = PennantJavaUtil.getExtendedFieldDetails(fieldRender);
			AuditDetail auditDetail = new AuditDetail(tranType, i + 1, fields[0], fields[1], fieldRender.getBefImage(),
					fieldRender);
			auditDetail.setExtended(true);

			auditList.add(auditDetail);
		}
		extendedFieldRenderDAO.deleteList(reference, tableName, tableType);

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	public List<AuditDetail> delete(List<AuditDetail> details, String module, String reference, String event,
			String tableType) {
		logger.debug(Literal.ENTERING);
		List<String> tableNames = new ArrayList<>();
		if (details != null) {
			for (int i = 0; i < details.size(); i++) {
				ExtendedFieldRender detail = (ExtendedFieldRender) details.get(i).getModelData();

				// Table Name identification
				StringBuilder tableName = new StringBuilder();
				tableName.append(module);
				tableName.append("_");
				tableName.append(detail.getTypeCode());
				if (StringUtils.trimToNull(event) != null) {
					tableName.append("_");
					tableName.append(PennantStaticListUtil.getFinEventCode(event));
				}
				tableName.append("_ED");

				if (StringUtils.equals(reference, detail.getReference())) {
					details.get(i).setExtended(true);
					detail.setReference(reference);
					detail.setTableName(tableName.toString());
					detail.setWorkflowId(0);
				}

				if (tableNames.contains(detail.getTypeCode())) {
					continue;
				}
				tableNames.add(detail.getTypeCode());
				extendedFieldRenderDAO.deleteList(reference, tableName.toString(), tableType);
			}
		}

		logger.debug(Literal.LEAVING);
		return details;
	}

	public List<AuditDetail> validateExtendedDdetails(ExtendedFieldHeader extFldHeader, List<AuditDetail> details,
			String method, String usrLanguage) {
		String tableName = getTableName(extFldHeader.getModuleName(), extFldHeader.getSubModuleName(),
				extFldHeader.getEvent());
		return vaildateDetails(details, method, usrLanguage, tableName);

	}

	public List<AuditDetail> vaildateDetails(List<AuditDetail> deatils, String method, String usrLanguage,
			String tableName) {
		logger.debug(Literal.ENTERING);

		if (deatils != null && deatils.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < deatils.size(); i++) {
				if (deatils.get(i) != null) {
					AuditDetail auditDetail = validate(deatils.get(i), method, usrLanguage, tableName);
					details.add(auditDetail);
				}
			}
			return details;
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<AuditDetail>();
	}

	public List<AuditDetail> validateCollateralDedup(ExtendedFieldRender aExetendedFieldRender, String queryCode,
			String querySubCode, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		DedupParm dedupParm = this.dedupParmService.getApprovedDedupParmById(queryCode,
				FinanceConstants.DEDUP_COLLATERAL, querySubCode);

		String sqlQuery = "Select T1.CollateralRef, T2.CUSTSHRTNAME From CollateralSetup_Temp T1"
				+ " Inner Join Customers T2 On T2.CustId = T1.DEPOSITORID" + " Inner Join Collateral_" + querySubCode
				+ "_ED_Temp  T3 On T3.REFERENCE = T1.COLLATERALREF " + dedupParm.getSQLQuery() + " union all "
				+ " Select T1.CollateralRef, T2.CUSTSHRTNAME From CollateralSetup T1"
				+ " Inner Join Customers T2 On T2.CustId = T1.DEPOSITORID" + " Inner Join Collateral_" + querySubCode
				+ "_ED  T3 On T3.REFERENCE = T1.COLLATERALREF " + dedupParm.getSQLQuery()
				+ " And NOT EXISTS (SELECT 1 FROM Collateral_" + querySubCode
				+ "_ED_TEMP  WHERE REFERENCE = T1.CollateralRef)";

		List<CollateralSetup> collateralSetupList = this.dedupParmService.queryExecution(sqlQuery,
				aExetendedFieldRender.getMapValues());

		List<AuditDetail> auditDetails = new ArrayList<>();
		AuditDetail auditDetail = new AuditDetail();
		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = aExetendedFieldRender.getReference();
		errParm[0] = PennantJavaUtil.getLabel("label_Collateral") + ":" + valueParm[0];

		if (CollectionUtils.isNotEmpty(collateralSetupList)) {
			boolean recordFound = true;
			if (collateralSetupList.size() == 1) {
				if (StringUtils.isNotBlank(aExetendedFieldRender.getReference()) && StringUtils
						.equals(collateralSetupList.get(0).getCollateralRef(), aExetendedFieldRender.getReference())) {
					recordFound = false;
				}
			} else {
				recordFound = false;
				for (CollateralSetup collateralSetup : collateralSetupList) {
					if (!(StringUtils.isNotBlank(aExetendedFieldRender.getReference()) && StringUtils
							.equals(collateralSetup.getCollateralRef(), aExetendedFieldRender.getReference()))) {
						recordFound = true;
					}
				}
			}
			if (recordFound) {
				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "WCOLL01", errParm, null));
				auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
				auditDetails.add(auditDetail);
				return auditDetails;
			}
		}

		logger.debug(Literal.LEAVING);
		return Collections.emptyList();
	}

	public List<AuditDetail> validateSamplingDetails(List<AuditDetail> deatils, Sampling sampling, String method,
			String usrLanguage, String moduleCode) {
		logger.debug(Literal.ENTERING);

		if (deatils != null && deatils.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			Map<String, ExtendedFieldHeader> extHeaderMap = sampling.getExtFieldHeaderList();
			for (int i = 0; i < deatils.size(); i++) {
				if (deatils.get(i) != null) {
					ExtendedFieldRender render = (ExtendedFieldRender) deatils.get(i).getModelData();
					String linkId = StringUtils.trimToEmpty(render.getReference());
					linkId = linkId.replaceAll("S", "");

					String collRef = samplingDAO.getCollateralRef(sampling, linkId);
					ExtendedFieldHeader extHeader = extHeaderMap.get(collRef);

					StringBuilder table = new StringBuilder();
					table.append(moduleCode);
					table.append("_");
					table.append(extHeader.getSubModuleName());
					table.append("_tv");

					AuditDetail auditDetail = validate(deatils.get(i), method, usrLanguage,
							table.toString().toLowerCase());
					details.add(auditDetail);
				}
			}
			return details;
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<AuditDetail>();
	}

	public AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage, String tableName) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldRender render = (ExtendedFieldRender) auditDetail.getModelData();
		String reference = render.getReference();
		int seqNo = render.getSeqNo();
		ExtendedFieldRender tempRender = null;

		if (render.isWorkflow()) {
			tempRender = extendedFieldRenderDAO.getExtendedFieldDetails(reference, render.getSeqNo(), tableName,
					"_Temp");
		}

		ExtendedFieldRender befExtRender = extendedFieldRenderDAO.getExtendedFieldDetails(reference, render.getSeqNo(),
				tableName, "");

		if (befExtRender != null) {
			Map<String, Object> extFieldMap = extendedFieldRenderDAO.getExtendedField(reference, seqNo, tableName, "");
			if (extFieldMap != null) {
				Map<String, Object> modifiedExtMap = new HashMap<>();
				for (Entry<String, Object> entrySet : extFieldMap.entrySet()) {
					modifiedExtMap.put(entrySet.getKey().toUpperCase(), entrySet.getValue());
				}

				// Audit Details Preparation For Before Image
				modifiedExtMap.put("Reference", extFieldMap.get("Reference"));
				modifiedExtMap.put("SeqNo", extFieldMap.get("SeqNo"));
				modifiedExtMap.put("Version", extFieldMap.get("Version"));
				modifiedExtMap.put("LastMntOn", extFieldMap.get("LastMntOn"));
				modifiedExtMap.put("LastMntBy", extFieldMap.get("LastMntBy"));
				modifiedExtMap.put("RecordStatus", extFieldMap.get("RecordStatus"));
				modifiedExtMap.put("RoleCode", extFieldMap.get("RoleCode"));
				modifiedExtMap.put("NextRoleCode", extFieldMap.get("NextRoleCode"));
				modifiedExtMap.put("TaskId", extFieldMap.get("TaskId"));
				modifiedExtMap.put("NextTaskId", extFieldMap.get("NextTaskId"));
				modifiedExtMap.put("RecordType", extFieldMap.get("RecordType"));
				modifiedExtMap.put("WorkflowId", extFieldMap.get("WorkflowId"));

				befExtRender.setMapValues(modifiedExtMap);
				befExtRender.setAuditMapValues(modifiedExtMap);
			}
		}
		ExtendedFieldRender oldExRender = render.getBefImage();

		if (tempRender == null && befExtRender == null) {
			render.setNewRecord(true);
			render.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		}

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = render.getReference();
		valueParm[1] = String.valueOf(render.getSeqNo());

		errParm[0] = PennantJavaUtil.getLabel("label_CollateralReference") + ":" + valueParm[0];
		if (StringUtils.startsWith(tableName, CollateralConstants.MODULE_NAME)) {
			errParm[0] = PennantJavaUtil.getLabel("label_CollateralReference") + ":" + valueParm[0];
		} else if (StringUtils.startsWith(tableName, AssetConstants.EXTENDEDFIELDS_MODULE)) {
			errParm[0] = PennantJavaUtil.getLabel("label_AssetType") + ":" + valueParm[0];
		} else if (StringUtils.startsWith(tableName, VASConsatnts.MODULE_NAME)) {
			errParm[0] = PennantJavaUtil.getLabel("label_VASReference") + ":" + valueParm[0];
		} else if (StringUtils.startsWith(tableName, ExtendedFieldConstants.MODULE_CUSTOMER)) {
			errParm[0] = PennantJavaUtil.getLabel("label_Module_Customer") + ":" + valueParm[0];
		} else if (StringUtils.startsWith(tableName, ExtendedFieldConstants.MODULE_LOAN)) {
			errParm[0] = PennantJavaUtil.getLabel("label_Module_Loan") + ":" + valueParm[0];
		} else if (StringUtils.startsWith(tableName, ExtendedFieldConstants.MODULE_ORGANIZATION)) {
			errParm[0] = PennantJavaUtil.getLabel("label_OrganizationDialog_OrganizationId.value") + ":" + valueParm[0];
		}
		errParm[1] = PennantJavaUtil.getLabel("label_SeqNo") + ":" + valueParm[1];

		if (render.isNewRecord()) { // for New record or new record into work flow

			if (!render.isWorkflow()) {// With out Work flow only new records
				if (befExtRender != null) { // Record Already Exists in the
											// table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (render.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befExtRender != null || tempRender != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befExtRender == null && tempRender == null) {
						// FIXME: commented the below line, showing an alert while modifying the approved records
						// auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm,
						// null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!render.isWorkflow()) { // With out Work flow for update and
										// delete

				if (befExtRender == null) { // if records not exists in the main
											// table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldExRender != null && !oldExRender.getLastMntOn().equals(befExtRender.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}
			} else {

				if (tempRender == null && !render.getRecordType().equals("EDIT")) { // if records not exists in the Work
					// flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		if (tempRender != null && befExtRender == null) {
			render.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		}

		String roleCode = render.getRoleCode();
		if (tempRender == null && befExtRender != null && (FinanceConstants.KNOCKOFFCAN_MAKER.equals(roleCode)
				|| (FinanceConstants.KNOCKOFFCAN_APPROVER.equals(roleCode)))) {
			render.setNewRecord(false);
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !render.isWorkflow()) {
			render.setBefImage(befExtRender);
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public List<ErrorDetail> validateExtendedFieldData(ExtendedFieldDetail deatils, ExtendedFieldData exrFldData) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errors = new ArrayList<ErrorDetail>();
		String fieldName = exrFldData.getFieldName();
		String fieldValue = Objects.toString(exrFldData.getFieldValue(), "");

		switch (deatils.getFieldType()) {
		case ExtendedFieldConstants.FIELDTYPE_DATE:
		case ExtendedFieldConstants.FIELDTYPE_TIME:
			Date dateValue = null;
			try {
				dateValue = DateUtil.parse(fieldValue, PennantConstants.APIDateFormatter);
			} catch (Exception e) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = "Date";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90299", "", valueParm)));
				return errors;
			}
			if (!StringUtils.equals(deatils.getFieldType(), ExtendedFieldConstants.FIELDTYPE_TIME)) {
				errors = dateValidation(deatils, dateValue, errors);
			}
			exrFldData.setFieldValue(dateValue);
			break;
		case ExtendedFieldConstants.FIELDTYPE_DATETIME:
			Date dateTimeVal = null;
			try {
				dateTimeVal = DateUtil.parse(fieldValue, PennantConstants.APIDateFormatter);
			} catch (Exception e) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = "Date";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90299", "", valueParm)));
				return errors;
			}
			errors = dateValidation(deatils, dateTimeVal, errors);
			exrFldData.setFieldValue(String.valueOf(DateUtil.getSqlDate(dateTimeVal)));
			break;
		case ExtendedFieldConstants.FIELDTYPE_AMOUNT:
			try {
				double rateValue = Double.parseDouble(fieldValue);
				@SuppressWarnings("unused")
				BigDecimal decimalValue = BigDecimal.valueOf(rateValue);
			} catch (Exception e) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = "number";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90299", "", valueParm)));
			}
			if (fieldValue.length() > deatils.getFieldLength() + 2) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(deatils.getFieldLength());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_INT:
		case ExtendedFieldConstants.FIELDTYPE_LONG:
			if (fieldValue.length() > deatils.getFieldLength()) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(deatils.getFieldLength());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
				return errors;
			}
			try {
				Integer.parseInt(fieldValue);
				if (deatils.getFieldMaxValue() > 0 || deatils.getFieldMinValue() > 0) {
					if (!(Long.valueOf(fieldValue) >= deatils.getFieldMinValue()
							&& Long.valueOf(fieldValue) <= deatils.getFieldMaxValue())) {
						String valueParm[] = new String[3];
						valueParm[0] = deatils.getFieldName();
						valueParm[1] = String.valueOf(deatils.getFieldMinValue());
						valueParm[2] = String.valueOf(deatils.getFieldMaxValue());
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
					}
				}
			} catch (Exception e) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = "number";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90299", "", valueParm)));
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_TEXT:
		case ExtendedFieldConstants.FIELDTYPE_MULTILINETEXT:
		case ExtendedFieldConstants.FIELDTYPE_UPPERTEXT:
			if (fieldValue.length() > deatils.getFieldLength()) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(deatils.getFieldLength());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
				return errors;
			}

			if (StringUtils.isNotBlank(deatils.getFieldConstraint())) {
				if (PennantRegularExpressions.getRegexMapper(deatils.getFieldConstraint()) != null) {
					Pattern pattern = Pattern
							.compile(PennantRegularExpressions.getRegexMapper(deatils.getFieldConstraint()));
					Matcher matcher = pattern.matcher(fieldValue);
					if (matcher.matches() == false) {
						String[] valueParm = new String[1];
						valueParm[0] = fieldName;
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90322", "", valueParm)));
					}
				}
			}
			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_UPPERTEXT, deatils.getFieldType())) {
				exrFldData.setFieldValue(fieldValue.toUpperCase());
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_ADDRESS:
			if (fieldValue.length() > 100) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(100);
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_PHONE:
			if (fieldValue.length() > 10) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(10);
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
				return errors;
			}
			if (StringUtils.isNotBlank(fieldValue)) {
				if (!(fieldValue.matches("\\d{10}"))) {
					String[] valueParm = new String[1];
					valueParm[0] = fieldName;
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90322", "", valueParm)));
					return errors;
				}
			}
			exrFldData.setFieldValue(fieldValue.substring(0, 8));
			break;
		case ExtendedFieldConstants.FIELDTYPE_DECIMAL:
			if (fieldValue.length() > deatils.getFieldLength()) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(deatils.getFieldLength());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
				return errors;
			}
			if (StringUtils.contains(fieldValue, ".")) {
				String[] valueParm = new String[1];
				valueParm[0] = fieldName;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90322", "", valueParm)));
				return errors;
			}
			exrFldData.setFieldValue((Long.valueOf(fieldValue) / Math.pow(10, deatils.getFieldPrec())));
			if (deatils.getFieldMaxValue() > 0 || deatils.getFieldMinValue() > 0) {
				if (Long.valueOf(fieldValue) > deatils.getFieldMaxValue()
						|| Long.valueOf(fieldValue) < deatils.getFieldMinValue()) {
					String[] valueParm = new String[3];
					valueParm[0] = fieldName;
					valueParm[1] = String.valueOf(deatils.getFieldMinValue());
					valueParm[2] = String.valueOf(deatils.getFieldMaxValue());
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
				}
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_BOOLEAN:
			if (StringUtils.isNotBlank(fieldValue)) {
				if (StringUtils.equals(fieldValue, "true") || StringUtils.equals(fieldValue, "false")
						|| StringUtils.equals(fieldValue, "0") || StringUtils.equals(fieldValue, "1")) {
					if (!(StringUtils.equals(fieldValue, "0") || StringUtils.equals(fieldValue, "1"))) {
						int i = fieldValue.equals("true") ? 1 : 0;
						exrFldData.setFieldValue(String.valueOf(i));
					}
				} else {
					String[] valueParm = new String[1];
					valueParm[0] = fieldName;
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90322", "", valueParm)));
				}
			}

			break;
		case ExtendedFieldConstants.FIELDTYPE_EXTENDEDCOMBO:
		case ExtendedFieldConstants.FIELDTYPE_BASERATE:
			String name = deatils.getFieldList();
			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BASERATE, deatils.getFieldType())) {
				name = "BaseRate";
			}
			if (name != null && name.contains(PennantConstants.DELIMITER_COMMA)) {
				String[] values = name.split(PennantConstants.DELIMITER_COMMA);
				name = values[0];
			}
			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(name);
			if (moduleMapping != null) {
				String[] lovFields = moduleMapping.getLovFields();
				Object[][] filters = moduleMapping.getLovFilters();
				int count = 0;
				if (!StringUtils.contains(moduleMapping.getTableName(), "Builder")) {
					if (filters != null) {
						count = extendedFieldRenderDAO.validateMasterData(moduleMapping.getTableName(), lovFields[0],
								(String) filters[0][0], fieldValue);
					} else {
						count = extendedFieldRenderDAO.validateMasterData(moduleMapping.getTableName(), lovFields[0],
								null, fieldValue);
					}
				} else {
					if (filters != null) {
						count = extendedFieldRenderDAO.validateMasterData(moduleMapping.getTableName(), lovFields[1],
								(String) filters[0][0], fieldValue);
					} else {
						count = extendedFieldRenderDAO.validateMasterData(moduleMapping.getTableName(), lovFields[1],
								null, fieldValue);
					}
				}
				if (count <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = fieldName;
					valueParm[1] = fieldValue;
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParm)));
				}
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_STATICCOMBO:
		case ExtendedFieldConstants.FIELDTYPE_RADIO:
			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_RADIO, deatils.getFieldType())) {
				if (fieldValue.length() > deatils.getFieldLength()) {
					String[] valueParm = new String[2];
					valueParm[0] = fieldName;
					valueParm[1] = String.valueOf(deatils.getFieldLength());
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
					return errors;
				}
			}
			String[] values = new String[0];
			boolean isValid = false;
			String staticList = deatils.getFieldList();
			if (staticList != null && staticList.contains(PennantConstants.DELIMITER_COMMA)) {
				values = staticList.split(PennantConstants.DELIMITER_COMMA);
			}

			if (values.length > 0) {
				for (int i = 0; i <= values.length - 1; i++) {
					if (StringUtils.equals(fieldValue, values[i])) {
						isValid = true;
					}
				}
			}

			if (!isValid) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = fieldValue;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParm)));
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_ACTRATE:
			if (fieldValue.length() > (deatils.getFieldLength() - deatils.getFieldPrec())) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(deatils.getFieldLength() - deatils.getFieldPrec());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
			}
			if (deatils.getFieldMaxValue() > 0 || deatils.getFieldMinValue() > 0) {
				if (Integer.valueOf(fieldValue) > deatils.getFieldMaxValue()
						|| Integer.valueOf(fieldValue) < deatils.getFieldMinValue()) {
					String[] valueParm = new String[3];
					valueParm[0] = fieldName;
					valueParm[1] = String.valueOf(deatils.getFieldMinValue());
					valueParm[2] = String.valueOf(deatils.getFieldMaxValue());
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
				}
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_PERCENTAGE:
			BigDecimal fValue = new BigDecimal(fieldValue);
			/*
			 * if (fieldValue.length() > (deatils.getFieldLength() - deatils.getFieldPrec())) { String[] valueParm = new
			 * String[2]; valueParm[0] = fieldName; valueParm[1] = String.valueOf(deatils.getFieldLength() -
			 * deatils.getFieldPrec()); errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm))); }
			 */
			if (fValue.compareTo(BigDecimal.ZERO) < 0 || fValue.compareTo(new BigDecimal(100)) > 0) {
				String[] valueParm = new String[3];
				valueParm[0] = fieldName;
				valueParm[1] = "0";
				valueParm[2] = "100";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
			}
			if (deatils.getFieldMaxValue() > 0 || deatils.getFieldMinValue() > 0) {
				if (fValue.compareTo(new BigDecimal(deatils.getFieldMaxValue())) > 0
						|| fValue.compareTo(new BigDecimal(deatils.getFieldMinValue())) < 0) {
					String[] valueParm = new String[3];
					valueParm[0] = fieldName;
					valueParm[1] = String.valueOf(deatils.getFieldMinValue());
					valueParm[2] = String.valueOf(deatils.getFieldMaxValue());
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
				}
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_FRQ:
			ErrorDetail errorDetail = FrequencyUtil.validateFrequency(fieldValue);
			if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = fieldValue;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90207", "", valueParm)));
				return errors;
			}

			break;
		case ExtendedFieldConstants.FIELDTYPE_ACCOUNT:
			if (fieldValue.length() > LengthConstants.LEN_ACCOUNT) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(LengthConstants.LEN_ACCOUNT);
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_MULTIEXTENDEDCOMBO:
			String key1 = deatils.getFieldList();
			String[] types = fieldValue.split(PennantConstants.DELIMITER_COMMA);
			if (key1 != null && key1.contains(PennantConstants.DELIMITER_COMMA)) {
				String[] values1 = key1.split(PennantConstants.DELIMITER_COMMA);
				key1 = values1[0];
			}
			ModuleMapping moduleMapping1 = PennantJavaUtil.getModuleMap(key1);
			if (moduleMapping1 != null) {
				String[] lovFields = moduleMapping1.getLovFields();
				Object[][] filters = moduleMapping1.getLovFilters();
				for (String type : types) {
					int count = 0;
					if (!StringUtils.contains(moduleMapping1.getTableName(), "Builder")) {
						if (filters != null) {
							count = extendedFieldRenderDAO.validateMasterData(moduleMapping1.getTableName(),
									lovFields[0], (String) filters[0][0], type);
						} else {
							count = extendedFieldRenderDAO.validateMasterData(moduleMapping1.getTableName(),
									lovFields[0], null, type);
						}
					} else {
						if (filters != null) {
							count = extendedFieldRenderDAO.validateMasterData(moduleMapping1.getTableName(),
									lovFields[1], (String) filters[0][0], type);
						} else {
							count = extendedFieldRenderDAO.validateMasterData(moduleMapping1.getTableName(),
									lovFields[1], null, type);
						}
					}
					if (count <= 0) {
						String[] valueParm = new String[2];
						valueParm[0] = fieldName;
						valueParm[1] = type;
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParm)));
					}
				}

			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_MULTISTATICCOMBO:
			String[] values1 = new String[0];
			String[] fieldvalues = new String[0];
			String multiStaticList = deatils.getFieldList();
			if (multiStaticList != null && multiStaticList.contains(PennantConstants.DELIMITER_COMMA)) {
				values1 = multiStaticList.split(PennantConstants.DELIMITER_COMMA);
			}
			if (fieldValue != null && fieldValue.contains(PennantConstants.DELIMITER_COMMA)) {
				fieldvalues = fieldValue.split(PennantConstants.DELIMITER_COMMA);
			}
			if (values1.length > 0) {
				for (int i = 0; i <= fieldvalues.length - 1; i++) {
					boolean isValid1 = false;
					for (int j = 0; j <= values1.length - 1; j++) {
						if (StringUtils.equals(fieldvalues[i], values1[j])) {
							isValid1 = true;
						}
					}
					if (!isValid1) {
						String[] valueParm = new String[2];
						valueParm[0] = fieldName;
						valueParm[1] = fieldValue;
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParm)));
					}
				}
			}

			break;
		default:
			break;
		}

		logger.debug(Literal.LEAVING);
		return errors;
	}

	private List<ErrorDetail> dateValidation(ExtendedFieldDetail exdConfigDetail, Date dateValue,
			List<ErrorDetail> errors) {
		logger.debug(Literal.ENTERING);
		String[] value = exdConfigDetail.getFieldConstraint().split(",");
		switch (value[0]) {
		case "RANGE":
			if (value[1] != null && value[2] != null) {
				if (dateValue.before(DateUtil.parse(value[1], PennantConstants.dateFormat))
						|| dateValue.after(DateUtil.parse(value[2], PennantConstants.dateFormat))) {
					String valueParm[] = new String[3];
					valueParm[0] = exdConfigDetail.getFieldName();
					valueParm[1] = String.valueOf(DateUtil.getDate(value[1]));
					valueParm[2] = String.valueOf(DateUtil.getDate(value[2]));
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
				}
			}
			break;
		case "FUTURE_DAYS":
			if (DateUtil.compare(dateValue,
					DateUtil.addDays(SysParamUtil.getAppDate(), Integer.parseInt(value[1]))) > 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + dateValue;
				valueParm[1] = String.valueOf(DateUtil.addDays(SysParamUtil.getAppDate(), Integer.parseInt(value[1])));
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", "", valueParm)));
			}
			break;
		case "PAST_DAYS":
			if (DateUtil.compare(dateValue,
					DateUtil.addDays(SysParamUtil.getAppDate(), -(Integer.parseInt(value[1])))) < 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + dateValue;
				valueParm[1] = String
						.valueOf(DateUtil.addDays(SysParamUtil.getAppDate(), -(Integer.parseInt(value[1]))));
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm)));
			}
			break;
		case "FUTURE_TODAY":
			if (DateUtil.compare(dateValue, SysParamUtil.getAppDate()) < 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + dateValue;
				valueParm[1] = String.valueOf(SysParamUtil.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm)));
			}
			break;
		case "PAST_TODAY":
			if (DateUtil.compare(dateValue, SysParamUtil.getAppDate()) > 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + DateUtil.formatToLongDate(dateValue);
				valueParm[1] = String.valueOf(SysParamUtil.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", "", valueParm)));
			}
			break;
		case "FUTURE":
			if (DateUtil.compare(SysParamUtil.getAppDate(), dateValue) >= 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + DateUtil.formatToLongDate(dateValue);
				valueParm[1] = String.valueOf(SysParamUtil.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm)));
			}
			break;
		case "PAST":
			if (DateUtil.compare(SysParamUtil.getAppDate(), dateValue) <= 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + DateUtil.formatToLongDate(dateValue);
				valueParm[1] = String.valueOf(SysParamUtil.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", "", valueParm)));
			}
			break;

		default:
			break;
		}

		logger.debug(Literal.LEAVING);
		return errors;
	}

	public ScriptErrors getPostValidationErrors(String postValidation, Map<String, Object> mapValues) {
		return scriptValidationService.getPostValidationErrors(postValidation, mapValues);
	}

	private String getTableName(String module, String subModuleName, String event) {
		StringBuilder sb = new StringBuilder();
		sb.append(module);
		sb.append("_");
		sb.append(subModuleName);
		if (StringUtils.trimToNull(event) != null) {
			sb.append("_");
			sb.append(PennantStaticListUtil.getFinEventCode(event));
		}
		sb.append("_ED");
		return sb.toString();
	}

	public List<ErrorDetail> validateExtendedFieldDetails(List<ExtendedField> ef, String module, String subModule,
			String event) {
		return validateExtendedFieldDetails(ef, module, subModule, event, false);
	}

	public List<ErrorDetail> validateExtendedFieldDetails(List<ExtendedField> ef, String module, String subModule,
			String event, boolean isDedupe) {
		logger.debug(Literal.ENTERING);

		String extEvent = null;
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		if (!(VerificationType.TV.getValue().equals(event))) {
			extEvent = event;
		}
		// get the ExtendedFieldHeader for given module and subModule
		ExtendedFieldHeader efh = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(module, subModule, extEvent,
				"");
		List<ExtendedFieldDetail> extendedFieldDetails = null;

		// based on ExtendedFieldHeader moduleId get the ExtendedFieldDetails
		// List
		if (VerificationType.TV.getValue().equals(event) && efh != null) {
			extendedFieldDetails = extendedFieldDetailDAO.getExtendedFieldDetailById(efh.getModuleId(),
					ExtendedFieldConstants.EXTENDEDTYPE_TECHVALUATION, "");
			efh.setExtendedFieldDetails(extendedFieldDetails);
		} else if (efh != null) {
			extendedFieldDetails = extendedFieldDetailDAO.getExtendedFieldDetailById(efh.getModuleId(), "");
			efh.setExtendedFieldDetails(extendedFieldDetails);
		}
		// if configuration is not available and end user gives extDetails
		// through API
		// Extended fields is not applicable for Current Module
		else if (CollectionUtils.isNotEmpty(ef)) {
			String[] valueParm = new String[2];
			valueParm[0] = "Extended fields";
			valueParm[1] = module;
			errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90329", "", valueParm)));
			return errorDetails;
		}

		// list out the number of mandatory fields from configuration
		int extendedDetailsCount = 0;
		if (extendedFieldDetails != null) {
			for (ExtendedFieldDetail detail : extendedFieldDetails) {
				if (detail.isFieldMandatory()) {
					extendedDetailsCount++;
				}
			}
		}
		if (CollectionUtils.isEmpty(ef)) {
			if (extendedDetailsCount > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "ExtendedDetails";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
				return errorDetails;
			}
		}
		// iterates the loop and check the each fieldName and fieldValue,
		// because both are required
		if (CollectionUtils.isNotEmpty(ef)) {
			List<String> fieldList = new ArrayList<String>();
			for (ExtendedField details : ef) {
				int exdMandConfigCount = 0;
				if (details.getExtendedFieldDataList() != null) {
					if (details.getExtendedFieldDataList().isEmpty()) {
						String[] valueParm = new String[1];
						valueParm[0] = "fieldName";
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
						return errorDetails;
					}
					for (ExtendedFieldData extFieldData : details.getExtendedFieldDataList()) {
						// if fieldName is blank then sets FieldName is
						// Mandatory
						if (StringUtils.isBlank(extFieldData.getFieldName())) {
							String[] valueParm = new String[1];
							valueParm[0] = "fieldName";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
							return errorDetails;
						}

						if (ImplementationConstants.CUSTOMER_PAN_VALIDATION_STOP && !isDedupe) {
							errorDetails.add(validateUCICNumber(subModule, extFieldData));

							if (CollectionUtils.isNotEmpty(errorDetails)) {
								return errorDetails;
							}
						}

						boolean isFeild = false;
						if (extendedFieldDetails != null) {
							for (ExtendedFieldDetail detail : extendedFieldDetails) {

								// if both fields(in configuraion and json) are
								// equal then it validates the fields and makes
								// isFiels=true
								if (StringUtils.equals(detail.getFieldName(), extFieldData.getFieldName())) {
									// if same field given more than one time it
									// raises the Error
									if (fieldList.contains(extFieldData.getFieldName())) {
										errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90297", "", null)));
										return errorDetails;
									} else {
										fieldList.add(extFieldData.getFieldName());
									}

									if (detail.isFieldMandatory() && StringUtils
											.isBlank(Objects.toString(extFieldData.getFieldValue(), ""))) {
										String[] valueParm = new String[1];
										valueParm[0] = "fieldValue";
										errorDetails
												.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
										return errorDetails;
									}

									if (detail.isFieldMandatory()) {
										exdMandConfigCount++;
									}
									// validate the field with configuration
									// that is already mentioned
									List<ErrorDetail> errList = getExtendedFieldDetailsValidation()
											.validateExtendedFieldData(detail, extFieldData);
									errorDetails.addAll(errList);
									isFeild = true;
									break;
								}
							}
							// if field is no there in configuration then is
							// sets the error like
							// Extended details should be matched with
							// configured extended details in {0}.
							if (!isFeild) {
								String[] valueParm = new String[1];
								valueParm[0] = module + " setup";
								errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90265", "", valueParm)));
								return errorDetails;
							}
						}
					}
				}
				// extendedDetailsCount :: mandatory fields from configuration
				// exdMandConfigCount :: mandatory fields from JSON
				// these are not match then sets the error like
				// Request should contain configured mandatory extended details.
				if (extendedDetailsCount != exdMandConfigCount) {
					errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90297", "", null)));
					return errorDetails;
				}
			}

		}

		Map<String, Object> mapValues = new HashMap<String, Object>();
		if (ef != null) {
			// get the ExtendedField--List from JSON
			for (ExtendedField details : ef) {
				// get the ExtendedFieldData--List from ExtendedField
				if (details.getExtendedFieldDataList() != null) {
					for (ExtendedFieldData extFieldData : details.getExtendedFieldDataList()) {
						// get the each EXTFD that are given in json
						// exdFldConfig ::EXTFieldDetailslist from configuration

						for (ExtendedFieldDetail detail : extendedFieldDetails) {

							if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BASERATE, detail.getFieldType())
									&& StringUtils.equals(extFieldData.getFieldName(), detail.getFieldName())) {
								extFieldData.setFieldName(extFieldData.getFieldName().concat("_BR"));
							}
							if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_PHONE, detail.getFieldType())
									&& StringUtils.equals(extFieldData.getFieldName(), detail.getFieldName())) {
								extFieldData.setFieldName(extFieldData.getFieldName().concat("_SC"));
							}
							mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
						}
					}
				}
			}
		}

		if (efh != null && efh.isPostValidationReq()) {
			for (ExtendedFieldDetail extendedFieldDetail : extendedFieldDetails) {
				if (!mapValues.containsKey(extendedFieldDetail.getFieldName())) {
					mapValues.put(extendedFieldDetail.getFieldName(), "");
				}
			}

			ScriptErrors scriptErrors = getPostValidationErrors(efh.getPostValidation(), mapValues);
			if (scriptErrors != null) {
				List<ScriptError> errorsList = scriptErrors.getAll();
				for (ScriptError error : errorsList) {
					errorDetails.add(new ErrorDetail("", "90909", "", error.getValue(), null, null));
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return errorDetails;
	}

	/**
	 * Method Getting the extended field render details
	 * 
	 * @param String reference
	 **/
	public ExtendedFieldRender getExtendedFieldRender(String module, String subModule, String reference) {

		// Extended Field Details
		StringBuilder tableName = new StringBuilder();
		tableName.append(module);
		tableName.append("_");
		tableName.append(subModule);
		tableName.append("_ED");

		Map<String, Object> extFieldMap = extendedFieldRenderDAO.getExtendedField(reference, tableName.toString(),
				"_View");
		ExtendedFieldRender efr = new ExtendedFieldRender();
		if (extFieldMap != null) {
			efr.setReference(String.valueOf(extFieldMap.get("Reference")));
			extFieldMap.remove("Reference");
			efr.setSeqNo(Integer.valueOf(String.valueOf(extFieldMap.get("SeqNo"))));
			extFieldMap.remove("SeqNo");
			efr.setVersion(Integer.valueOf(String.valueOf(extFieldMap.get("Version"))));
			extFieldMap.remove("Version");
			efr.setLastMntOn((Timestamp) extFieldMap.get("LastMntOn"));
			extFieldMap.remove("LastMntOn");
			efr.setLastMntBy(Long.valueOf(String.valueOf(extFieldMap.get("LastMntBy"))));
			extFieldMap.remove("LastMntBy");
			efr.setRecordStatus(StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null") ? ""
					: String.valueOf(extFieldMap.get("RecordStatus")));
			extFieldMap.remove("RecordStatus");
			efr.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null") ? ""
					: String.valueOf(extFieldMap.get("RoleCode")));
			extFieldMap.remove("RoleCode");
			efr.setNextRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null") ? ""
					: String.valueOf(extFieldMap.get("NextRoleCode")));
			extFieldMap.remove("NextRoleCode");
			efr.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null") ? ""
					: String.valueOf(extFieldMap.get("TaskId")));
			extFieldMap.remove("TaskId");
			efr.setNextTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")), "null") ? ""
					: String.valueOf(extFieldMap.get("NextTaskId")));
			extFieldMap.remove("NextTaskId");
			efr.setRecordType(StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")), "null") ? ""
					: String.valueOf(extFieldMap.get("RecordType")));
			extFieldMap.remove("RecordType");
			efr.setWorkflowId(Long.valueOf(String.valueOf(extFieldMap.get("WorkflowId"))));
			extFieldMap.remove("WorkflowId");
			efr.setMapValues(extFieldMap);
		}

		return efr;
	}

	/**
	 * Method for Updating extended field details
	 * 
	 * @param fd
	 * @param suffix
	 */
	public void updateFinExtendedDetails(FinanceDetail fd, String suffix) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
		String auditTranType = PennantConstants.TRAN_WF;

		long userId = fm.getUserDetails().getUserId();
		String finReference = fm.getFinReference();

		List<AuditDetail> auditDetails = new ArrayList<>();

		// process Extended field details
		// Get the ExtendedFieldHeader for given module and subModule
		String event = null;
		if (fd.getExtendedFieldHeader() != null) {
			event = fd.getExtendedFieldHeader().getEvent();
		}
		String finCategory = fm.getFinCategory();
		ExtendedFieldHeader efh = extendedFieldHeaderDAO
				.getExtendedFieldHeaderByModuleName(ExtendedFieldConstants.MODULE_LOAN, finCategory, event, "");
		fd.setExtendedFieldHeader(efh);

		List<ExtendedField> extendedFields = fd.getExtendedDetails();

		long instructionUID = Long.MIN_VALUE;
		if (efh != null) {
			int seqNo = 0;
			ExtendedFieldRender efr = new ExtendedFieldRender();
			efr.setReference(finReference);
			efr.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			efr.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			efr.setLastMntBy(userId);
			efr.setSeqNo(++seqNo);
			efr.setNewRecord(false);
			efr.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			efr.setVersion(1);
			efr.setTypeCode(fd.getExtendedFieldHeader().getSubModuleName());
			efr.setTableName(getTableName(efh.getModuleName(), efh.getSubModuleName(),
					ExtendedFieldConstants.MODULE_ORGANIZATION));

			schdData.getFinServiceInstructions().forEach(l1 -> efr.setInstructionUID(l1.getInstructionUID()));

			if (extendedFields != null) {
				for (ExtendedField extendedField : extendedFields) {
					Map<String, Object> mapValues = new HashMap<>();
					for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
						efr.setMapValues(mapValues);
					}
				}

				if (extendedFields.isEmpty()) {
					Map<String, Object> mapValues = new HashMap<String, Object>();
					efr.setMapValues(mapValues);
				}
			} else {
				Map<String, Object> mapValues = new HashMap<String, Object>();
				efr.setMapValues(mapValues);
			}

			fd.setExtendedFieldRender(efr);
		}

		if (fd.getExtendedFieldRender() != null) {
			auditDetailMap.put("LoanExtendedFieldDetails", setExtendedFieldsAuditData(fd.getExtendedFieldRender(),
					auditTranType, "saveOrUpdate", ExtendedFieldConstants.MODULE_LOAN));
		}

		if (fd.getExtendedFieldRender() != null) {
			List<AuditDetail> details = auditDetailMap.get("LoanExtendedFieldDetails");
			details = processingExtendedFieldDetailList(details, ExtendedFieldConstants.MODULE_LOAN,
					fd.getExtendedFieldHeader().getEvent(), suffix, instructionUID);
			auditDetails.addAll(details);
		}

		AuditHeader auditHeader = getAuditHeader(schdData.getFinanceMain(), PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
	}

	private AuditHeader getAuditHeader(FinanceMain finMain, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, finMain.getBefImage(), finMain);
		return new AuditHeader(finMain.getFinReference(), null, null, null, auditDetail, finMain.getUserDetails(),
				new HashMap<String, List<ErrorDetail>>());
	}

	// Extended field Extended combobox Description--04-09-2019
	public String getExtFieldDesc(String sql) {
		return this.extendedFieldDetailDAO.getExtFldDesc(sql);
	}

	public List<ExtendedField> getExtndedFieldDetails(String moduleName, String subModuleName, String event,
			String reference) {

		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(moduleName,
				subModuleName, event, "");
		List<ExtendedField> extendedDetails = new ArrayList<ExtendedField>();
		if (extendedFieldHeader != null) {
			StringBuilder tableName = new StringBuilder();
			tableName.append(extendedFieldHeader.getModuleName());
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			if (event != null) {
				tableName.append("_");
				tableName.append(StringUtils.trimToEmpty(PennantStaticListUtil.getFinEventCode(event)));
			}
			tableName.append("_ED");

			List<Map<String, Object>> renderMapList = extendedFieldRenderDAO.getExtendedFieldMap(reference,
					tableName.toString(), "_View ");

			if (renderMapList != null) {

				for (Map<String, Object> mapValues : renderMapList) {
					mapValues.remove("Reference");
					mapValues.remove("SeqNo");
					mapValues.remove("Version");
					mapValues.remove("LastMntOn");
					mapValues.remove("LastMntBy");
					mapValues.remove("RecordStatus");
					mapValues.remove("RoleCode");
					mapValues.remove("NextRoleCode");
					mapValues.remove("TaskId");
					mapValues.remove("NextTaskId");
					mapValues.remove("RecordType");
					mapValues.remove("WorkflowId");
					mapValues.get("storeId");
					List<ExtendedFieldData> extendedFieldDataList = new ArrayList<ExtendedFieldData>();
					for (Entry<String, Object> entry : mapValues.entrySet()) {
						ExtendedFieldData exdFieldData = new ExtendedFieldData();
						if (StringUtils.isNotBlank(String.valueOf(entry.getValue()))
								&& !StringUtils.equals(String.valueOf(entry.getValue()), "null")) {
							exdFieldData.setFieldName(entry.getKey().toLowerCase());
							exdFieldData.setFieldValue(entry.getValue());
							extendedFieldDataList.add(exdFieldData);
						}
					}
					ExtendedField extendedField = new ExtendedField();
					extendedField.setExtendedFieldDataList(extendedFieldDataList);
					extendedDetails.add(extendedField);
				}
			}
		}
		return extendedDetails;
	}

	/*
	 * flag= true get all extended details where UpdateCpID ==null; flag= false :get all extended details where
	 * UpdateCpID !=null
	 */
	public Map<String, String> getAllExtndedFieldDetails(String moduleName, String subModuleName, String event,
			String type, boolean flag) {

		Map<String, String> orgMap = new HashMap<>();
		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(moduleName,
				subModuleName, event, "");
		if (extendedFieldHeader != null) {
			StringBuilder tableName = new StringBuilder();
			tableName.append(extendedFieldHeader.getModuleName());
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			if (StringUtils.isNotBlank(event)) {
				tableName.append("_");
				tableName.append(StringUtils.trimToEmpty(PennantStaticListUtil.getFinEventCode(event)));
			}
			tableName.append("_ED");
			if (flag) {
				orgMap = extendedFieldRenderDAO.getAllExtendedFieldMap(tableName.toString(), type);
			} else {
				orgMap = extendedFieldRenderDAO.getAllExtendedFieldMapForUpdateCpid(tableName.toString(), type);
			}

		}
		return orgMap;
	}

	public List<AuditDetail> setExtendedFieldsAuditData(ExtendedFieldRender extendedFieldRender, String tranType,
			String method) {
		logger.debug(Literal.ENTERING);
		int auditSeq = 1;
		List<AuditDetail> auditDetails = new ArrayList<>();

		AuditDetail auditDetail = setExtendedFieldAuditData(extendedFieldRender, tranType, method, auditSeq);
		if (auditDetail == null) {
			return auditDetails;
		}

		auditDetails.add(auditDetail);
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public AuditDetail setExtendedFieldAuditData(ExtendedFieldRender efr, String tranType, String method,
			int auditSeq) {
		logger.debug(Literal.ENTERING);

		if (efr == null) {
			return null;
		}
		if (StringUtils.isEmpty(StringUtils.trimToEmpty(efr.getRecordType()))) {
			return null;
		}

		boolean isRcdType = false;
		if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			efr.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			isRcdType = true;
		} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
			efr.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			if (efr.isWorkflow()) {
				isRcdType = true;
			}
		} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
			efr.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		}

		if ("saveOrUpdate".equals(method) && (isRcdType)) {
			efr.setNewRecord(true);
		}

		if (!tranType.equals(PennantConstants.TRAN_WF)) {
			if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					|| efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				tranType = PennantConstants.TRAN_DEL;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// Audit Details Preparation
		Map<String, Object> auditMapValues = efr.getMapValues();
		auditMapValues.put("Reference", efr.getReference());
		auditMapValues.put("SeqNo", efr.getSeqNo());
		auditMapValues.put("Version", efr.getVersion());
		auditMapValues.put("LastMntOn", efr.getLastMntOn());
		auditMapValues.put("LastMntBy", efr.getLastMntBy());
		auditMapValues.put("RecordStatus", efr.getRecordStatus());
		auditMapValues.put("RoleCode", efr.getRoleCode());
		auditMapValues.put("NextRoleCode", efr.getNextRoleCode());
		auditMapValues.put("TaskId", efr.getTaskId());
		auditMapValues.put("NextTaskId", efr.getNextTaskId());
		auditMapValues.put("RecordType", efr.getRecordType());
		auditMapValues.put("WorkflowId", efr.getWorkflowId());
		efr.setAuditMapValues(auditMapValues);

		String[] fields = PennantJavaUtil.getExtendedFieldDetails(efr);
		AuditDetail auditDetail = new AuditDetail(tranType, auditSeq, fields[0], fields[1], efr.getBefImage(), efr);
		auditDetail.setExtended(true);

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	public void processingExtendedFieldDetailList(ExtendedFieldRender efr, String tableName, String event,
			String type) {
		logger.debug(Literal.ENTERING);
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		if (StringUtils.isEmpty(efr.getRecordType())) {
			return;
		}
		if ((FinServiceEvent.ADDDISB.equals(event))
				|| (PennantConstants.RCD_STATUS_SUBMITTED.equals(efr.getRecordStatus())
						&& PennantConstants.RECORD_TYPE_UPD.equals(efr.getRecordType()))) {
			if (!extendedFieldRenderDAO.isExists(efr.getReference(), efr.getSeqNo(), tableName + type)) {
				efr.setNewRecord(true);
			}
		}

		saveRecord = false;
		updateRecord = false;
		deleteRecord = false;
		approveRec = false;
		String rcdType = "";
		String recordStatus = "";
		if (StringUtils.isEmpty(type)) {
			approveRec = true;
			efr.setRoleCode("");
			efr.setNextRoleCode("");
			efr.setTaskId("");
			efr.setNextTaskId("");
		}

		// Table Name addition for Audit
		efr.setTableName(tableName);

		if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
			deleteRecord = true;
		} else if (efr.isNewRecord()) {
			saveRecord = true;
			if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				efr.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				efr.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				efr.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			}

		} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
			if (approveRec) {
				saveRecord = true;
			} else {
				updateRecord = true;
			}
		} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
			updateRecord = true;
		} else if (efr.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
			if (approveRec) {
				deleteRecord = true;
			} else if (efr.isNew()) {
				saveRecord = true;
			} else {
				updateRecord = true;
			}
		}
		if (approveRec) {
			rcdType = efr.getRecordType();
			recordStatus = efr.getRecordStatus();
			efr.setRecordType("");
			efr.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		}

		// Add Common Fields
		Map<String, Object> mapValues = efr.getMapValues();
		if (saveRecord || updateRecord) {
			mapValues.put("Reference", efr.getReference());
			mapValues.put("SeqNo", efr.getSeqNo());
			mapValues.put("LastMntBy", efr.getLastMntBy());
			mapValues.put("LastMntOn", new Timestamp(System.currentTimeMillis()));
			mapValues.put("RoleCode", efr.getRoleCode());
			mapValues.put("NextRoleCode", efr.getNextRoleCode());
			mapValues.put("TaskId", efr.getTaskId());
			mapValues.put("NextTaskId", efr.getNextTaskId());
			mapValues.put("RecordStatus", efr.getRecordStatus());
			mapValues.put("RecordType", efr.getRecordType());
			mapValues.put("WorkflowId", efr.getWorkflowId());
			mapValues.put("InstructionUID", efr.getInstructionUID());
		}

		if (saveRecord) {
			mapValues.put("Version", efr.getVersion() + 1);
			extendedFieldRenderDAO.save(efr.getMapValues(), type, tableName);
		}

		if (updateRecord) {
			extendedFieldRenderDAO.update(efr.getReference(), efr.getSeqNo(), efr.getMapValues(), type, tableName);
		}

		if (deleteRecord) {
			extendedFieldRenderDAO.delete(efr.getReference(), efr.getSeqNo(), type, tableName);
		}
		if (approveRec) {
			efr.setRecordType(rcdType);
			efr.setRecordStatus(recordStatus);
		}

		efr.setBefImage(efr);

		logger.debug(Literal.LEAVING);
	}

	public long getInstructionUID(ExtendedFieldRender efr) {

		long instructionUID = efr.getInstructionUID();

		if (instructionUID != Long.MIN_VALUE) {
			return instructionUID;
		}

		instructionUID = Long.valueOf(ReferenceGenerator.generateNewServiceUID());

		efr.setInstructionUID(instructionUID);

		return instructionUID;
	}

	public long getInstructionUID(ExtendedFieldRender efr, ExtendedFieldExtension efe) {

		long instructionUID = efr.getInstructionUID();

		if (instructionUID == Long.MIN_VALUE) {
			instructionUID = Long.valueOf(ReferenceGenerator.generateNewServiceUID());
		}

		efr.setInstructionUID(instructionUID);

		if (efe.getInstructionUID() == Long.MIN_VALUE) {
			efe.setInstructionUID(instructionUID);
		}

		return instructionUID;
	}

	public String getUCICNumber(String tableName, Object ucic) {
		return extendedFieldRenderDAO.getUCICNumber(tableName, ucic);
	}

	private ErrorDetail validateUCICNumber(String subModule, ExtendedFieldData extFieldData) {
		Object fieldValue = extFieldData.getFieldValue();

		if ("UCIC".equalsIgnoreCase(extFieldData.getFieldName()) && fieldValue != null) {
			String reference = extendedFieldRenderDAO.getUCICNumber(subModule, fieldValue);

			if (reference != null) {
				String[] valueParm = new String[2];
				valueParm[0] = "FieldValue:" + fieldValue;
				valueParm[1] = "CustCif:" + reference;
				return ErrorUtil.getErrorDetail(new ErrorDetail("41018", "", valueParm));
			}
		}

		return null;
	}

	/**
	 * @param extendedFieldRenderDAO the extendedFieldRenderDAO to set
	 */
	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	/**
	 * @param scriptValidationService the scriptValidationService to set
	 */
	public void setScriptValidationService(ScriptValidationService scriptValidationService) {
		this.scriptValidationService = scriptValidationService;
	}

	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	public void setExtendedFieldDetailDAO(ExtendedFieldDetailDAO extendedFieldDetailDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
	}

	public void setExtendedFieldDetailsValidation(ExtendedFieldDetailsValidation extendedFieldDetailsValidation) {
		this.extendedFieldDetailsValidation = extendedFieldDetailsValidation;
	}

	public ExtendedFieldDetailsValidation getExtendedFieldDetailsValidation() {
		if (extendedFieldDetailsValidation == null) {
			this.extendedFieldDetailsValidation = new ExtendedFieldDetailsValidation(extendedFieldRenderDAO);
		}
		return extendedFieldDetailsValidation;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public String getExtFieldDesc(String tableName, String value) {
		return this.extendedFieldDetailDAO.getExtFldDesc(tableName, value);

	}

	public String getExtFieldIndustryMargin(String tableName, String type, String industry, String segment,
			String product) {
		return this.extendedFieldDetailDAO.getExtFldIndustryMargin(tableName, type, industry, segment, product);

	}

	public List<Map<String, Object>> getExtendedFieldMap(String reference, String tableName, String type) {
		return extendedFieldRenderDAO.getExtendedFieldMap(reference, tableName, type);
	}

	public ExtendedFieldExtension getExtendedFieldExtension(String externalRef, String modeStatus, String finEvent) {
		return extendedFieldExtensionService.getExtendedFieldExtension(externalRef, modeStatus, finEvent,
				TableType.VIEW);
	}

	public void setExtendedFieldExtensionService(ExtendedFieldExtensionService extendedFieldExtensionService) {
		this.extendedFieldExtensionService = extendedFieldExtensionService;
	}

	public void saveCollateralExtendedFields(ExtendedFieldRender extendedFieldRender) {
		extendedFieldRenderDAO.update(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
				extendedFieldRender.getMapValues(), "", extendedFieldRender.getTableName());
	}
}
