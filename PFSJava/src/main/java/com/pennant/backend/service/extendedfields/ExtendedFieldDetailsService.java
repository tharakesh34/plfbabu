package com.pennant.backend.service.extendedfields;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.collateral.impl.ExtendedFieldDetailsValidation;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
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
import com.pennanttech.pennapps.pff.sampling.dao.SamplingDAO;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pff.core.TableType;

public class ExtendedFieldDetailsService {
	private static final Logger logger = Logger.getLogger(ExtendedFieldDetailsService.class);

	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private ScriptValidationService scriptValidationService;
	private ExtendedFieldHeaderDAO extendedFieldHeaderDAO;
	private ExtendedFieldDetailDAO extendedFieldDetailDAO;
	private ExtendedFieldDetailsValidation extendedFieldDetailsValidation;
	private AuditHeaderDAO auditHeaderDAO;

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

	public List<Map<String, ExtendedFieldData>> getExtendedFildValueLableMap(String tableName, String reference, String tableType) {
		logger.debug("Entering");

		ExtendedFieldHeader fieldHeader = getExtendedFieldHeader(tableName);

		if (fieldHeader == null) {
			return null;
		}

		List<ExtendedFieldDetail> fieldDetailsList = fieldHeader.getExtendedFieldDetails();
		if (fieldDetailsList != null && !fieldDetailsList.isEmpty()) {
			return null;
		}

		List<Map<String, Object>> fiildValueMap = extendedFieldRenderDAO.getExtendedFieldMap(reference, tableName, tableType);

		List<Map<String, ExtendedFieldData>> resultList = new ArrayList<>();

		if (fiildValueMap != null && !fiildValueMap.isEmpty()) {
			for (Map<String, Object> map : fiildValueMap) {
				resultList.add(getResultMap(fieldDetailsList, map));
			}
		}
		logger.debug("Leaving");
		return resultList;
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
		
		List<Map<String, Object>> fiildValueMap = extendedFieldRenderDAO.getExtendedFieldMap(reference, tableName, tableType);
		
		List<ExtendedFieldData> resultList = new ArrayList<>();
		
		if (fiildValueMap != null && !fiildValueMap.isEmpty()) {
			for (Map<String, Object> map : fiildValueMap) {
				resultList.add(getExtendedFieldData(fieldDetailsList, map));
			}
		}
		logger.debug("Leaving");
		return resultList;
	}

	private ExtendedFieldData getExtendedFieldData(List<ExtendedFieldDetail> fieldDetailsList, Map<String, Object> map) {
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
	
	public Map<String, ExtendedFieldData> getCollateralMap(String tableName, String reference, String type) {
		logger.debug(Literal.ENTERING);

		Map<String, ExtendedFieldData> resultList = new HashMap<>();
		ExtendedFieldHeader fieldHeader;
		String tempTableName=tableName;
		if (tempTableName.startsWith("verification")) {
			tempTableName = tempTableName.replace("verification", "collateral");
			tempTableName = tempTableName.replace("tv", "ed");
		}

		fieldHeader = getExtendedFieldHeader(tempTableName);

		if (fieldHeader == null) {
			return resultList;
		}

		List<ExtendedFieldDetail> fieldDetailsList = fieldHeader.getExtendedFieldDetails();
		if (CollectionUtils.isEmpty(fieldDetailsList)) {
			return resultList;
		}

		Map<String, Object> fieldMap = extendedFieldRenderDAO.getCollateralMap(reference, tableName, type);

		resultList = getResultMap(fieldDetailsList, fieldMap);
		logger.debug(Literal.LEAVING);
		return resultList;
	}

	private Map<String, ExtendedFieldData> getResultMap(List<ExtendedFieldDetail> fieldDetailsList,
			Map<String, Object> map) {
		Map<String, ExtendedFieldData> resultMap = new HashMap<>();
		ExtendedFieldData fieldData = null;

		for (ExtendedFieldDetail detail : fieldDetailsList) {
			if (map.containsKey(detail.getFieldName().toLowerCase())) {
				fieldData = new ExtendedFieldData();
				fieldData.setFieldValue(map.get(detail.getFieldName().toLowerCase()));
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
			tableName = getTableName(ExtendedFieldConstants.MODULE_LOAN, finCategory,
					FinanceConstants.FINSER_EVENT_ORG);
		}

		if (tableName != null) {
			return extendedFieldRenderDAO.getExtendedFieldMap(reference, tableName, "_view");
		}

		return new ArrayList<>();
	}

	public List<AuditDetail> setExtendedFieldsAuditData(List<ExtendedFieldRender> details, String tranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (int i = 0; i < details.size(); i++) {
			ExtendedFieldRender extendedFieldRender = details.get(i);
			auditDetails.add(setExtendedFieldAuditData(extendedFieldRender, tranType, method, i + 1));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	public List<AuditDetail> setExtendedFieldsAuditData(ExtendedFieldRender extendedFieldRender, String tranType,
			String method) {
		logger.debug(Literal.ENTERING);
		int auditSeq = 1;
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditDetails.add(setExtendedFieldAuditData(extendedFieldRender, tranType, method, auditSeq));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public AuditDetail setExtendedFieldAuditData(ExtendedFieldRender extendedFieldRender, String tranType,
			String method, int auditSeq) {
		logger.debug(Literal.ENTERING);

		if (extendedFieldRender == null) {
			return null;
		}
		if (StringUtils.isEmpty(StringUtils.trimToEmpty(extendedFieldRender.getRecordType()))) {
			return null;
		}

		boolean isRcdType = false;
		if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			isRcdType = true;
		} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
			extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			if (extendedFieldRender.isWorkflow()) {
				isRcdType = true;
			}
		} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
			extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		}

		if ("saveOrUpdate".equals(method) && (isRcdType)) {
			extendedFieldRender.setNewRecord(true);
		}

		if (!tranType.equals(PennantConstants.TRAN_WF)) {
			if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					|| extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				tranType = PennantConstants.TRAN_DEL;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// Audit Details Preparation
		HashMap<String, Object> auditMapValues = (HashMap<String, Object>) extendedFieldRender.getMapValues();
		auditMapValues.put("Reference", extendedFieldRender.getReference());
		auditMapValues.put("SeqNo", extendedFieldRender.getSeqNo());
		auditMapValues.put("Version", extendedFieldRender.getVersion());
		auditMapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
		auditMapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
		auditMapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
		auditMapValues.put("RoleCode", extendedFieldRender.getRoleCode());
		auditMapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
		auditMapValues.put("TaskId", extendedFieldRender.getTaskId());
		auditMapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
		auditMapValues.put("RecordType", extendedFieldRender.getRecordType());
		auditMapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
		extendedFieldRender.setAuditMapValues(auditMapValues);

		String[] fields = PennantJavaUtil.getExtendedFieldDetails(extendedFieldRender);
		AuditDetail auditDetail = new AuditDetail(tranType, auditSeq, fields[0], fields[1],
				extendedFieldRender.getBefImage(), extendedFieldRender);
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
	public List<AuditDetail> processingExtendedFieldDetailList(List<AuditDetail> deatils,
			ExtendedFieldHeader extFldHeader, String type) {
		logger.debug(Literal.ENTERING);

		String tableName = getTableName(extFldHeader.getModuleName(), extFldHeader.getSubModuleName(),
				extFldHeader.getEvent());

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < deatils.size(); i++) {
			ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) deatils.get(i).getModelData();
			if (StringUtils.isEmpty(extendedFieldRender.getRecordType())) {
				continue;
			}
			if (StringUtils.equals(extendedFieldRender.getRecordStatus(), PennantConstants.RCD_STATUS_SUBMITTED)
					&& StringUtils.equals(extendedFieldRender.getRecordType(), PennantConstants.RECORD_TYPE_UPD)) {
				if (!extendedFieldRenderDAO.isExists(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
						tableName + type)) {
					extendedFieldRender.setNewRecord(true);
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
				extendedFieldRender.setRoleCode("");
				extendedFieldRender.setNextRoleCode("");
				extendedFieldRender.setTaskId("");
				extendedFieldRender.setNextTaskId("");
			}

			// Table Name addition for Audit
			extendedFieldRender.setTableName(tableName);
			extendedFieldRender.setWorkflowId(0);

			if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (extendedFieldRender.isNewRecord()) {
				saveRecord = true;
				if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (extendedFieldRender.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = extendedFieldRender.getRecordType();
				recordStatus = extendedFieldRender.getRecordStatus();
				extendedFieldRender.setRecordType("");
				extendedFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			// Add Common Fields
			HashMap<String, Object> mapValues = (HashMap<String, Object>) extendedFieldRender.getMapValues();
			if (saveRecord || updateRecord) {
				if (saveRecord) {
					mapValues.put("Reference", extendedFieldRender.getReference());
					mapValues.put("SeqNo", extendedFieldRender.getSeqNo());
				}

				mapValues.put("Version", extendedFieldRender.getVersion());
				mapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
				mapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
				mapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
				mapValues.put("RoleCode", extendedFieldRender.getRoleCode());
				mapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
				mapValues.put("TaskId", extendedFieldRender.getTaskId());
				mapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
				mapValues.put("RecordType", extendedFieldRender.getRecordType());
				mapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
			}

			if (saveRecord) {
				extendedFieldRenderDAO.save(extendedFieldRender.getMapValues(), type, tableName.toString());
			}

			if (updateRecord) {
				extendedFieldRenderDAO.update(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
						extendedFieldRender.getMapValues(), type, tableName.toString());
			}

			if (deleteRecord) {
				extendedFieldRenderDAO.delete(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(), type,
						tableName.toString());
			}
			if (approveRec) {
				extendedFieldRender.setRecordType(rcdType);
				extendedFieldRender.setRecordStatus(recordStatus);
			}

			// Setting Extended field is to identify record related to Extended
			// fields
			extendedFieldRender.setBefImage(extendedFieldRender);
			deatils.get(i).setExtended(true);
			deatils.get(i).setModelData(extendedFieldRender);
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
			ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) deatils.get(i).getModelData();
			if (StringUtils.isEmpty(extendedFieldRender.getRecordType())) {
				continue;
			}
			if (StringUtils.equals(extendedFieldRender.getRecordStatus(), PennantConstants.RCD_STATUS_SUBMITTED)
					&& StringUtils.equals(extendedFieldRender.getRecordType(), PennantConstants.RECORD_TYPE_UPD)) {
				if (!extendedFieldRenderDAO.isExists(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
						tableName + type)) {
					extendedFieldRender.setNewRecord(true);
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
				extendedFieldRender.setRoleCode("");
				extendedFieldRender.setNextRoleCode("");
				extendedFieldRender.setTaskId("");
				extendedFieldRender.setNextTaskId("");
			}

			// Table Name addition for Audit
			extendedFieldRender.setTableName(tableName);
			extendedFieldRender.setWorkflowId(0);

			if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (extendedFieldRender.isNewRecord()) {
				saveRecord = true;
				if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (extendedFieldRender.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = extendedFieldRender.getRecordType();
				recordStatus = extendedFieldRender.getRecordStatus();
				extendedFieldRender.setRecordType("");
				extendedFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			// Add Common Fields
			HashMap<String, Object> mapValues = (HashMap<String, Object>) extendedFieldRender.getMapValues();
			if (saveRecord || updateRecord) {
				if (saveRecord) {
					mapValues.put("Reference", extendedFieldRender.getReference());
					mapValues.put("SeqNo", extendedFieldRender.getSeqNo());
				}

				mapValues.put("Version", extendedFieldRender.getVersion());
				mapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
				mapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
				mapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
				mapValues.put("RoleCode", extendedFieldRender.getRoleCode());
				mapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
				mapValues.put("TaskId", extendedFieldRender.getTaskId());
				mapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
				mapValues.put("RecordType", extendedFieldRender.getRecordType());
				mapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
			}

			if (saveRecord) {
				extendedFieldRenderDAO.save(extendedFieldRender.getMapValues(), type, tableName.toString());
			}

			if (updateRecord) {
				extendedFieldRenderDAO.update(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
						extendedFieldRender.getMapValues(), type, tableName.toString());
			}

			if (deleteRecord) {
				extendedFieldRenderDAO.delete(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(), type,
						tableName.toString());
			}
			if (approveRec) {
				extendedFieldRender.setRecordType(rcdType);
				extendedFieldRender.setRecordStatus(recordStatus);
			}

			// Setting Extended field is to identify record related to Extended
			// fields
			extendedFieldRender.setBefImage(extendedFieldRender);
			deatils.get(i).setExtended(true);
			deatils.get(i).setModelData(extendedFieldRender);
		}
		logger.debug(Literal.LEAVING);
		return deatils;
	}

	public List<AuditDetail> processingExtendedFieldDetailList(List<AuditDetail> details, String module, String event,
			String type) {
		logger.debug(Literal.ENTERING);
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < details.size(); i++) {
			if (details.get(i) != null) {
				ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) details.get(i).getModelData();
				if (StringUtils.isEmpty(extendedFieldRender.getRecordType())) {
					continue;
				}
				String tableName = getTableName(module, extendedFieldRender.getTypeCode(), event);
				saveRecord = false;
				updateRecord = false;
				deleteRecord = false;
				approveRec = false;
				String rcdType = "";
				String recordStatus = "";
				if (StringUtils.isEmpty(type)) {
					approveRec = true;
					extendedFieldRender.setRoleCode("");
					extendedFieldRender.setNextRoleCode("");
					extendedFieldRender.setTaskId("");
					extendedFieldRender.setNextTaskId("");
				}

				// Table Name addition for Audit
				extendedFieldRender.setTableName(tableName);
				// extendedFieldRender.setWorkflowId(0);

				if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					deleteRecord = true;
				} else if (extendedFieldRender.isNewRecord()) {
					saveRecord = true;
					if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
						extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
						extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
						extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}

				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					if (approveRec) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
					updateRecord = true;
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
					if (approveRec) {
						deleteRecord = true;
					} else if (extendedFieldRender.isNew()) {
						saveRecord = true;
					} else {
						updateRecord = true;
					}
				}
				if (approveRec) {
					rcdType = extendedFieldRender.getRecordType();
					recordStatus = extendedFieldRender.getRecordStatus();
					extendedFieldRender.setRecordType("");
					extendedFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				}

				// Add Common Fields
				HashMap<String, Object> mapValues = (HashMap<String, Object>) extendedFieldRender.getMapValues();
				if (saveRecord || updateRecord) {
					if (saveRecord) {
						mapValues.put("Reference", extendedFieldRender.getReference());
						mapValues.put("SeqNo", extendedFieldRender.getSeqNo());
					}

					mapValues.put("Version", extendedFieldRender.getVersion());
					mapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
					mapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
					mapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
					mapValues.put("RoleCode", extendedFieldRender.getRoleCode());
					mapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
					mapValues.put("TaskId", extendedFieldRender.getTaskId());
					mapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
					mapValues.put("RecordType", extendedFieldRender.getRecordType());
					mapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
				}

				if (saveRecord) {
					extendedFieldRenderDAO.save(extendedFieldRender.getMapValues(), type, tableName.toString());
				}

				if (updateRecord) {
					extendedFieldRenderDAO.update(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
							extendedFieldRender.getMapValues(), type, tableName.toString());
				}

				if (deleteRecord) {
					extendedFieldRenderDAO.delete(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
							type, tableName.toString());
				}
				if (approveRec) {
					extendedFieldRender.setRecordType(rcdType);
					extendedFieldRender.setRecordStatus(recordStatus);
				}

				// Setting Extended field is to identify record related to
				// Extended fields
				extendedFieldRender.setBefImage(extendedFieldRender);
				details.get(i).setExtended(true);
				details.get(i).setModelData(extendedFieldRender);
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
		List<CollateralSetup> collList = sampling.getCollSetupList();
		for (int i = 0; i < deatils.size(); i++) {
			ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) deatils.get(i).getModelData();
			if (StringUtils.isEmpty(extendedFieldRender.getRecordType())) {
				continue;
			}
			String collRef=samplingDAO.getCollateralRef(sampling, extendedFieldRender.getReference(), "collaterals");
			
			ExtendedFieldHeader extHeader = extHeaderMap.get(collRef);

			StringBuilder tableName = new StringBuilder();
			tableName.append(moduleCode);
			tableName.append("_");
			tableName.append(extHeader.getSubModuleName());
			tableName.append("_tv");

		/*	extendedFieldRender.setReference(String
					.valueOf(samplingDAO.getCollateralLinkId(sampling.getId(), extendedFieldRender.getReference())));*/

			if (StringUtils.equals(extendedFieldRender.getRecordStatus(), PennantConstants.RCD_STATUS_SUBMITTED)
					&& StringUtils.equals(extendedFieldRender.getRecordType(), PennantConstants.RECORD_TYPE_UPD)) {
				if (!extendedFieldRenderDAO.isExists(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
						tableName + type.getSuffix())) {
					extendedFieldRender.setNewRecord(true);
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
				extendedFieldRender.setRoleCode("");
				extendedFieldRender.setNextRoleCode("");
				extendedFieldRender.setTaskId("");
				extendedFieldRender.setNextTaskId("");
			}

			// Table Name addition for Audit
			extendedFieldRender.setTableName(tableName.toString());
			extendedFieldRender.setWorkflowId(0);

			if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (extendedFieldRender.isNewRecord()) {
				saveRecord = true;
				if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (extendedFieldRender.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = extendedFieldRender.getRecordType();
				recordStatus = extendedFieldRender.getRecordStatus();
				extendedFieldRender.setRecordType("");
				extendedFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			// Add Common Fields
			HashMap<String, Object> mapValues = (HashMap<String, Object>) extendedFieldRender.getMapValues();
			if (saveRecord || updateRecord) {
				if (saveRecord) {
					mapValues.put("Reference", extendedFieldRender.getReference());
					mapValues.put("SeqNo", extendedFieldRender.getSeqNo());
				}

				mapValues.put("Version", extendedFieldRender.getVersion());
				mapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
				mapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
				mapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
				mapValues.put("RoleCode", extendedFieldRender.getRoleCode());
				mapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
				mapValues.put("TaskId", extendedFieldRender.getTaskId());
				mapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
				mapValues.put("RecordType", extendedFieldRender.getRecordType());
				mapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
			}

			if (saveRecord) {
				extendedFieldRenderDAO.save(extendedFieldRender.getMapValues(), type.getSuffix(), tableName.toString());
			}

			if (updateRecord) {
				extendedFieldRenderDAO.update(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
						extendedFieldRender.getMapValues(), type.getSuffix(), tableName.toString());
			}

			if (deleteRecord) {
				extendedFieldRenderDAO.delete(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
						type.getSuffix(), tableName.toString());
			}
			if (approveRec) {
				extendedFieldRender.setRecordType(rcdType);
				extendedFieldRender.setRecordStatus(recordStatus);
			}

			// Setting Extended field is to identify record related to Extended
			// fields
			extendedFieldRender.setBefImage(extendedFieldRender);
			deatils.get(i).setExtended(true);
			deatils.get(i).setModelData(extendedFieldRender);
		}
		logger.debug(Literal.LEAVING);
		return deatils;
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
			fieldRender.setTableName(tableName.toString());

			if (StringUtils.isEmpty(tableType)) {
				fieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			} else {
				fieldRender.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}

			// Audit Details Preparation
			HashMap<String, Object> auditMapValues = (HashMap<String, Object>) fieldRender.getMapValues();
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
		extendedFieldRenderDAO.deleteList(reference, tableName.toString(), tableType);

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	public List<AuditDetail> delete(List<AuditDetail> details, String module, String reference, String event,
			String tableType) {
		logger.debug(Literal.ENTERING);
		List<String> tableNames = new ArrayList<>();
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

			details.get(i).setExtended(true);
			detail.setReference(reference);
			detail.setTableName(tableName.toString());
			detail.setWorkflowId(0);

			if (tableNames.contains(detail.getTypeCode())) {
				continue;
			}
			tableNames.add(detail.getTypeCode());
			extendedFieldRenderDAO.deleteList(reference, tableName.toString(), tableType);
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

	public List<AuditDetail> validateSamplingDetails(List<AuditDetail> deatils, Sampling sampling, String method,
			String usrLanguage, String moduleCode) {
		logger.debug(Literal.ENTERING);

		if (deatils != null && deatils.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			Map<String, ExtendedFieldHeader> extHeaderMap = sampling.getExtFieldHeaderList();
			List<CollateralSetup> collList = sampling.getCollSetupList();
			for (int i = 0; i < deatils.size(); i++) {
				if (deatils.get(i) != null) {
					ExtendedFieldRender render = (ExtendedFieldRender) deatils.get(i).getModelData();
					String collRef=null;
					collRef = samplingDAO.getCollateralRef(sampling, render.getReference(), "collaterals");
					ExtendedFieldHeader extHeader = extHeaderMap.get(collRef);
					StringBuilder tableName = new StringBuilder();
					tableName.append(moduleCode);
					tableName.append("_");
					tableName.append(extHeader.getSubModuleName());
					tableName.append("_tv");

					AuditDetail auditDetail = validate(deatils.get(i), method, usrLanguage, tableName.toString());
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
		ExtendedFieldRender tempRender = null;

		if (render.isWorkflow()) {
			tempRender = extendedFieldRenderDAO.getExtendedFieldDetails(render.getReference(), render.getSeqNo(),
					tableName, "_Temp");
		}

		ExtendedFieldRender befExtRender = extendedFieldRenderDAO.getExtendedFieldDetails(render.getReference(),
				render.getSeqNo(), tableName, "");
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
		}
		errParm[1] = PennantJavaUtil.getLabel("label_SeqNo") + ":" + valueParm[1];

		if (render.isNew()) { // for New record or new record into work flow

			if (!render.isWorkflow()) {// With out Work flow only new records
				if (befExtRender != null) { // Record Already Exists in the
												// table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (render.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if
																							// records
																						// type
																						// is
																						// new
					if (befExtRender != null || tempRender != null) { // if
																			// records
																		// already
																		// exists
																		// in
																		// the
																		// main
																		// table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befExtRender == null || tempRender != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
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

				if (tempRender == null) { // if records not exists in the Work
												// flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempRender != null && oldExRender != null
						&& !oldExRender.getLastMntOn().equals(tempRender.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
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
				dateValue = DateUtility.parse(fieldValue, PennantConstants.APIDateFormatter);
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
			exrFldData.setFieldValue(String.valueOf(DateUtility.getSqlDate(dateValue)));
			break;
		case ExtendedFieldConstants.FIELDTYPE_DATETIME:
			Date dateTimeVal = null;
			try {
				dateTimeVal = DateUtility.parse(fieldValue, PennantConstants.APIDateFormatter);
			} catch (Exception e) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = "Date";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90299", "", valueParm)));
				return errors;
			}
			errors = dateValidation(deatils, dateTimeVal, errors);
			exrFldData.setFieldValue(String.valueOf(DateUtility.getSqlDate(dateTimeVal)));
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
			exrFldData.setFieldValue(Math.round((Integer.valueOf(fieldValue) / Math.pow(10, deatils.getFieldPrec()))));
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
			String key = deatils.getFieldList();
			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BASERATE, deatils.getFieldType())) {
				key = "BaseRate";
			}
			if (key != null && key.contains(PennantConstants.DELIMITER_COMMA)) {
				String[] values = key.split(PennantConstants.DELIMITER_COMMA);
				key = values[0];
			}
			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(key);
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
			if (fieldValue.length() > (deatils.getFieldLength() - deatils.getFieldPrec())) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(deatils.getFieldLength() - deatils.getFieldPrec());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
			}
			if (Integer.valueOf(fieldValue) < 0 || Integer.valueOf(fieldValue) > 100) {
				String[] valueParm = new String[3];
				valueParm[0] = fieldName;
				valueParm[1] = "0";
				valueParm[2] = "100";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm)));
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
				if (dateValue.before(DateUtility.getUtilDate(value[1], PennantConstants.dateFormat))
						|| dateValue.after(DateUtility.getUtilDate(value[2], PennantConstants.dateFormat))) {
					String valueParm[] = new String[3];
					valueParm[0] = exdConfigDetail.getFieldName();
					valueParm[1] = String.valueOf(DateUtility.getDate(value[1]));
					valueParm[2] = String.valueOf(DateUtility.getDate(value[2]));
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm)));
				}
			}
			break;
		case "FUTURE_DAYS":
			if (DateUtility.compare(dateValue,
					DateUtility.addDays(DateUtility.getAppDate(), Integer.parseInt(value[1]))) > 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + dateValue;
				valueParm[1] = String
						.valueOf(DateUtility.addDays(DateUtility.getAppDate(), Integer.parseInt(value[1])));
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", "", valueParm)));
			}
			break;
		case "PAST_DAYS":
			if (DateUtility.compare(dateValue,
					DateUtility.addDays(DateUtility.getAppDate(), -(Integer.parseInt(value[1])))) < 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + dateValue;
				valueParm[1] = String
						.valueOf(DateUtility.addDays(DateUtility.getAppDate(), -(Integer.parseInt(value[1]))));
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm)));
			}
			break;
		case "FUTURE_TODAY":
			if (DateUtility.compare(dateValue, DateUtility.getAppDate()) < 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + dateValue;
				valueParm[1] = String.valueOf(DateUtility.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm)));
			}
			break;
		case "PAST_TODAY":
			if (DateUtility.compare(dateValue, DateUtility.getAppDate()) > 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + DateUtility.formatToLongDate(dateValue);
				valueParm[1] = String.valueOf(DateUtility.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("30565", "", valueParm)));
			}
			break;
		case "FUTURE":
			if (DateUtility.compare(DateUtility.getAppDate(), dateValue) >= 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + DateUtility.formatToLongDate(dateValue);
				valueParm[1] = String.valueOf(DateUtility.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParm)));
			}
			break;
		case "PAST":
			if (DateUtility.compare(DateUtility.getAppDate(), dateValue) <= 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + DateUtility.formatToLongDate(dateValue);
				valueParm[1] = String.valueOf(DateUtility.getAppDate());
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

	public List<ErrorDetail> validateExtendedFieldDetails(List<ExtendedField> extendedFieldData, String module,
			String subModule, String event) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		// get the ExtendedFieldHeader for given module and subModule
		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(module,
				subModule, event, "");
		List<ExtendedFieldDetail> extendedFieldDetails = null;

		// based on ExtendedFieldHeader moduleId get the ExtendedFieldDetails
		// List
		if (extendedFieldHeader != null) {
			extendedFieldDetails = extendedFieldDetailDAO.getExtendedFieldDetailById(extendedFieldHeader.getModuleId(),
					"");
			extendedFieldHeader.setExtendedFieldDetails(extendedFieldDetails);
		}
		// if configuration is not available and end user gives extDetails
		// through API
		// Extended fields is not applicable for Current Module
		else if (extendedFieldData != null && !extendedFieldData.isEmpty()) {
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
		if (extendedFieldData == null || extendedFieldData.isEmpty()) {
			if (extendedDetailsCount > 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "ExtendedDetails";
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
				return errorDetails;
			}
		}
		// iterates the loop and check the each fieldName and fieldValue,
		// because both are required
		if (extendedFieldData != null && !extendedFieldData.isEmpty()) {
			List<String> fieldList = new ArrayList<String>();
			for (ExtendedField details : extendedFieldData) {
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

						// if fieldValue is blank then sets fieldValue is
						// Mandatory
						if (StringUtils.isBlank(Objects.toString(extFieldData.getFieldValue(), ""))) {
							String[] valueParm = new String[1];
							valueParm[0] = "fieldValue";
							errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
							return errorDetails;
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
		if (extendedFieldData != null) {
			// get the ExtendedField--List from JSON
			for (ExtendedField details : extendedFieldData) {
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
		logger.debug(Literal.LEAVING);
		return errorDetails;
	}

	/**
	 * Method Getting the extended field render details
	 * 
	 * @param String
	 *            reference
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
		ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
		if (extFieldMap != null) {
			extendedFieldRender.setReference(String.valueOf(extFieldMap.get("Reference")));
			extFieldMap.remove("Reference");
			extendedFieldRender.setSeqNo(Integer.valueOf(String.valueOf(extFieldMap.get("SeqNo"))));
			extFieldMap.remove("SeqNo");
			extendedFieldRender.setVersion(Integer.valueOf(String.valueOf(extFieldMap.get("Version"))));
			extFieldMap.remove("Version");
			extendedFieldRender.setLastMntOn((Timestamp) extFieldMap.get("LastMntOn"));
			extFieldMap.remove("LastMntOn");
			extendedFieldRender.setLastMntBy(Long.valueOf(String.valueOf(extFieldMap.get("LastMntBy"))));
			extFieldMap.remove("LastMntBy");
			extendedFieldRender
					.setRecordStatus(StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null") ? ""
							: String.valueOf(extFieldMap.get("RecordStatus")));
			extFieldMap.remove("RecordStatus");
			extendedFieldRender.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null") ? ""
					: String.valueOf(extFieldMap.get("RoleCode")));
			extFieldMap.remove("RoleCode");
			extendedFieldRender
					.setNextRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null") ? ""
							: String.valueOf(extFieldMap.get("NextRoleCode")));
			extFieldMap.remove("NextRoleCode");
			extendedFieldRender.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null") ? ""
					: String.valueOf(extFieldMap.get("TaskId")));
			extFieldMap.remove("TaskId");
			extendedFieldRender.setNextTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")), "null")
					? "" : String.valueOf(extFieldMap.get("NextTaskId")));
			extFieldMap.remove("NextTaskId");
			extendedFieldRender.setRecordType(StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")), "null")
					? "" : String.valueOf(extFieldMap.get("RecordType")));
			extFieldMap.remove("RecordType");
			extendedFieldRender.setWorkflowId(Long.valueOf(String.valueOf(extFieldMap.get("WorkflowId"))));
			extFieldMap.remove("WorkflowId");
			extendedFieldRender.setMapValues(extFieldMap);
		}

		return extendedFieldRender;
	}

	/**
	 * Method for Updating extended field details
	 * 
	 * @param financeDetail
	 * @param suffix
	 */
	public void updateFinExtendedDetails(FinanceDetail financeDetail, String suffix) {
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		String auditTranType = PennantConstants.TRAN_WF;
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		// process Extended field details
		// Get the ExtendedFieldHeader for given module and subModule
		String event = null;
		if (financeDetail.getExtendedFieldHeader() != null) {
			event = financeDetail.getExtendedFieldHeader().getEvent();
		}
		ExtendedFieldHeader extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
				ExtendedFieldConstants.MODULE_LOAN, finMain.getFinCategory(), event, "");
		financeDetail.setExtendedFieldHeader(extendedFieldHeader);

		List<ExtendedField> extendedFields = financeDetail.getExtendedDetails();
		if (extendedFieldHeader != null) {
			int seqNo = 0;
			ExtendedFieldRender exdFieldRender = new ExtendedFieldRender();
			exdFieldRender.setReference(finMain.getFinReference());
			exdFieldRender.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			exdFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			exdFieldRender.setLastMntBy(finMain.getUserDetails().getUserId());
			exdFieldRender.setSeqNo(++seqNo);
			exdFieldRender.setNewRecord(false);
			exdFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			exdFieldRender.setVersion(1);
			exdFieldRender.setTypeCode(financeDetail.getExtendedFieldHeader().getSubModuleName());

			if (extendedFields != null) {
				for (ExtendedField extendedField : extendedFields) {
					Map<String, Object> mapValues = new HashMap<String, Object>();
					for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
						exdFieldRender.setMapValues(mapValues);
					}
				}
				if (extendedFields.isEmpty()) {
					Map<String, Object> mapValues = new HashMap<String, Object>();
					exdFieldRender.setMapValues(mapValues);
				}
			} else {
				Map<String, Object> mapValues = new HashMap<String, Object>();
				exdFieldRender.setMapValues(mapValues);
			}
			financeDetail.setExtendedFieldRender(exdFieldRender);
		}

		if (financeDetail.getExtendedFieldRender() != null) {
			auditDetailMap.put("LoanExtendedFieldDetails",
					setExtendedFieldsAuditData(financeDetail.getExtendedFieldRender(), auditTranType, "saveOrUpdate"));
		}

		if (financeDetail.getExtendedFieldRender() != null) {
			List<AuditDetail> details = auditDetailMap.get("LoanExtendedFieldDetails");
			details = processingExtendedFieldDetailList(details, ExtendedFieldConstants.MODULE_LOAN,
					financeDetail.getExtendedFieldHeader().getEvent(), suffix);
			auditDetails.addAll(details);
		}
		AuditHeader auditHeader = getAuditHeader(financeDetail.getFinScheduleData().getFinanceMain(),
				PennantConstants.TRAN_WF);
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
	}

	private AuditHeader getAuditHeader(FinanceMain finMain, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, finMain.getBefImage(), finMain);
		return new AuditHeader(finMain.getFinReference(), null, null, null, auditDetail, finMain.getUserDetails(),
				new HashMap<String, ArrayList<ErrorDetail>>());
	}

	/**
	 * @param extendedFieldRenderDAO
	 *            the extendedFieldRenderDAO to set
	 */
	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	/**
	 * @param scriptValidationService
	 *            the scriptValidationService to set
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

}
