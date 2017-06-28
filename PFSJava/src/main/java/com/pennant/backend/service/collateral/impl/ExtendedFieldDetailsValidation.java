package com.pennant.backend.service.collateral.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.staticparms.ExtendedFieldData;
import com.pennant.backend.model.staticparms.ExtendedFieldRender;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pff.core.model.ModuleMapping;

public class ExtendedFieldDetailsValidation {

	private ExtendedFieldRenderDAO	extendedFieldRenderDAO;
	
	public ExtendedFieldRenderDAO getExtendedFieldRenderDAO() {
		return extendedFieldRenderDAO;
	}

	public ExtendedFieldDetailsValidation(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public List<AuditDetail> vaildateDetails(List<AuditDetail> auditDetails, String method, String usrLanguage, String tableName) {

		if (auditDetails != null && auditDetails.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validate(auditDetails.get(i), method, usrLanguage, tableName);
				details.add(auditDetail);
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

	public AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage, String tableName) {

		ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) auditDetail.getModelData();
		ExtendedFieldRender tempExtendedFieldRender = null;
		if (extendedFieldRender.isWorkflow()) {
			tempExtendedFieldRender = getExtendedFieldRenderDAO().getExtendedFieldDetails(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),tableName, "_Temp");
		}

		ExtendedFieldRender befExtendedFieldRender = getExtendedFieldRenderDAO().getExtendedFieldDetails(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),tableName, "");
		ExtendedFieldRender oldExtendedFieldRender = extendedFieldRender.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		valueParm[0] = extendedFieldRender.getReference();
		valueParm[1] = String.valueOf(extendedFieldRender.getSeqNo());

		errParm[0] = PennantJavaUtil.getLabel("label_CollateralReference") + ":" + valueParm[0];
		if (StringUtils.startsWith(tableName, CollateralConstants.MODULE_NAME)) {
			errParm[0] = PennantJavaUtil.getLabel("label_CollateralReference") + ":" + valueParm[0];
		} else if (StringUtils.startsWith(tableName, AssetConstants.EXTENDEDFIELDS_MODULE)) {
			errParm[0] = PennantJavaUtil.getLabel("label_AssetType") + ":" + valueParm[0];
		} else if (StringUtils.startsWith(tableName, VASConsatnts.MODULE_NAME)) {
			errParm[0] = PennantJavaUtil.getLabel("label_VASReference") + ":" + valueParm[0];
		}
		errParm[1] = PennantJavaUtil.getLabel("label_SeqNo") + ":" + valueParm[1];

		if (extendedFieldRender.isNew()) { // for New record or new record into work flow

			if (!extendedFieldRender.isWorkflow()) {// With out Work flow only new records  
				if (befExtendedFieldRender != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (extendedFieldRender.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befExtendedFieldRender != null || tempExtendedFieldRender != null) { // if records already exists in the main table
						auditDetail
								.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befExtendedFieldRender == null || tempExtendedFieldRender != null) {
						auditDetail
								.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!extendedFieldRender.isWorkflow()) { // With out Work flow for update and delete

				if (befExtendedFieldRender == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldExtendedFieldRender != null
							&& !oldExtendedFieldRender.getLastMntOn().equals(befExtendedFieldRender.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41003", errParm,
									null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41004", errParm,
									null));
						}
					}
				}
			} else {

				if (tempExtendedFieldRender == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempExtendedFieldRender != null && oldExtendedFieldRender != null
						&& !oldExtendedFieldRender.getLastMntOn().equals(tempExtendedFieldRender.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !extendedFieldRender.isWorkflow()) {
			extendedFieldRender.setBefImage(befExtendedFieldRender);
		}
		return auditDetail;
	}

	public List<ErrorDetails> validateExtendedFieldData(ExtendedFieldDetail exdConfigDetail, ExtendedFieldData exdFieldData) {
		List<ErrorDetails> errors = new ArrayList<ErrorDetails>();
		String fieldName = exdFieldData.getFieldName();
		String fieldValue = exdFieldData.getFieldValue();

		switch (exdConfigDetail.getFieldType()) {
		case ExtendedFieldConstants.FIELDTYPE_DATE:
			Date dateValue = null;
			try {
				dateValue = DateUtility.parse(fieldValue, PennantConstants.APIDateFormatter);
			} catch (Exception e) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = "Date";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90299", "", valueParm)));
				return errors;
			}
			errors = dateValidation(exdConfigDetail, dateValue, errors);
			exdFieldData.setFieldValue(String.valueOf(DateUtility.getSqlDate(dateValue)));
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
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90299", "", valueParm)));
			}
			if(fieldValue.length() > exdConfigDetail.getFieldLength()) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(exdConfigDetail.getFieldLength());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90300", "", valueParm)));
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_INT:
			if(fieldValue.length() > exdConfigDetail.getFieldLength()) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(exdConfigDetail.getFieldLength());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90300", "", valueParm)));
			}
			try {
				Integer.parseInt(fieldValue);
			} catch (Exception e) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = "number";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90299", "", valueParm)));
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_TEXT:
		case ExtendedFieldConstants.FIELDTYPE_MULTILINETEXT:
		if(fieldValue.length() > exdConfigDetail.getFieldLength()) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(exdConfigDetail.getFieldLength());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90300", "", valueParm)));
			}
			
			if(StringUtils.isNotBlank(exdConfigDetail.getFieldConstraint())) {
				if(PennantRegularExpressions.getRegexMapper(exdConfigDetail.getFieldConstraint())!=null){
				Pattern pattern = Pattern.compile(PennantRegularExpressions.getRegexMapper(
						exdConfigDetail.getFieldConstraint()));
				Matcher matcher = pattern.matcher(fieldValue);
				if (matcher.find() == false) {
					String[] valueParm = new String[0];
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90322", "", valueParm)));
				}
				}
			}
			break;
			case ExtendedFieldConstants.FIELDTYPE_ADDRESS:
			if(fieldValue.length() > 100) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(100);
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90300", "", valueParm)));
			}
			break;
			/*case ExtendedFieldConstants.FIELDTYPE_PHONE:
				if(fieldValue.length() > 10) {
					String[] valueParm = new String[2];
					valueParm[0] = fieldName;
					valueParm[1] = String.valueOf(10);
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90300", "", valueParm)));
				}*/
		case ExtendedFieldConstants.FIELDTYPE_BOOLEAN:
			if (StringUtils.isNotBlank(fieldValue)) {
				if (StringUtils.equals(fieldValue, "true") || StringUtils.equals(fieldValue, "false")
						|| StringUtils.equals(fieldValue, "0") || StringUtils.equals(fieldValue, "1")) {
					if(!(StringUtils.equals(fieldValue, "0") || StringUtils.equals(fieldValue, "1"))){
					int i = fieldValue == "true" ? 1 : 0;
					exdFieldData.setFieldValue(String.valueOf(i));
					}
				} else {
					String[] valueParm = new String[0];
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90322", "", valueParm)));
				}
			}
				
			break;
		case ExtendedFieldConstants.FIELDTYPE_EXTENDEDCOMBO:
			String key = exdConfigDetail.getFieldList();
			if(key != null && key.contains(PennantConstants.DELIMITER_COMMA)) {
				String[] values = key.split(PennantConstants.DELIMITER_COMMA);
				key = values[0];
			}
			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(key);
			if(moduleMapping != null) {
				String[] lovFields = moduleMapping.getLovFields();
				String[][] filters = moduleMapping.getLovFilters();
				int count=0;
				if(filters !=null){
				 count = extendedFieldRenderDAO.validateMasterData(moduleMapping.getTableName(), lovFields[0], filters[0][0], fieldValue);
				} else {
					 count = extendedFieldRenderDAO.validateMasterData(moduleMapping.getTableName(), lovFields[0],null , fieldValue);
				}
				if(count <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = fieldName;
					valueParm[1] = fieldValue;
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90224", "", valueParm)));
				}
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_STATICCOMBO:
			String[] values = new String[0];
			boolean isValid = false;
			String staticList = exdConfigDetail.getFieldList();
			if(staticList != null && staticList.contains(PennantConstants.DELIMITER_COMMA)) {
				values = staticList.split(PennantConstants.DELIMITER_COMMA);
			}
			
			if (values.length > 0) {
				for (int i = 0; i <= values.length-1; i++) {
					if (StringUtils.equals(fieldValue, values[i])) {
						isValid = true;
					}
				}
			}
			
			if(!isValid) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = fieldValue;
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90224", "", valueParm)));
			}
			break;
			case ExtendedFieldConstants.FIELDTYPE_ACTRATE:
			if (fieldValue.length() > (exdConfigDetail.getFieldLength()-exdConfigDetail.getFieldPrec())) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(exdConfigDetail.getFieldLength()-exdConfigDetail.getFieldPrec());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90300", "", valueParm)));
			}
			if (Integer.valueOf(fieldValue) > exdConfigDetail.getFieldMaxValue()
					|| Integer.valueOf(fieldValue) < exdConfigDetail.getFieldMinValue()) {
				String[] valueParm = new String[3];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(exdConfigDetail.getFieldMinValue());
				valueParm[2] = String.valueOf(exdConfigDetail.getFieldMaxValue());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90318", "", valueParm)));
			}
			break;
		default:
			break;
		}
		return errors;
	}

	private List<ErrorDetails> dateValidation(ExtendedFieldDetail exdConfigDetail, Date dateValue, List<ErrorDetails> errors) {
		String[] value = exdConfigDetail.getFieldConstraint().split(",");
		switch (value[0]) {
		case "RANGE":
			if(value[1] !=null && value[2] != null){
			if(dateValue.before(DateUtility.getUtilDate(value[1], PennantConstants.dateFormat)) ||
					dateValue.after(DateUtility.getUtilDate(value[2], PennantConstants.dateFormat))) {
				String valueParm[] = new String[3];
				valueParm[0] = exdConfigDetail.getFieldName();
				valueParm[1] = String.valueOf(DateUtility.getDate(value[1]));
				valueParm[2] = String.valueOf(DateUtility.getDate(value[2]));
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("90318", "", valueParm)));
			}
			}
			break;
		case "FUTURE_DAYS":
			if(DateUtility.compare(dateValue, DateUtility.addDays(DateUtility.getAppDate(), Integer.parseInt(value[1]))) > 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName()+":"+ dateValue;
				valueParm[1] = String.valueOf(DateUtility.addDays(DateUtility.getAppDate(), Integer.parseInt(value[1])));
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("30565", "", valueParm)));
			}
			break;
		case "PAST_DAYS":
			if(DateUtility.compare(dateValue, DateUtility.addDays(DateUtility.getAppDate(), -(Integer.parseInt(value[1])))) < 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName()+":"+ dateValue;
				valueParm[1] = String.valueOf(DateUtility.addDays(DateUtility.getAppDate(), -(Integer.parseInt(value[1]))));
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("91121", "", valueParm)));
			}
			break;
		case "FUTURE_TODAY":
			if(DateUtility.compare(dateValue, DateUtility.getAppDate()) < 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName()+":"+ dateValue;
				valueParm[1] = String.valueOf(DateUtility.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("91121", "", valueParm)));
			}
			break;
		case "PAST_TODAY":
			if(DateUtility.compare(dateValue, DateUtility.getAppDate()) > 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName()+":"+ DateUtility.formatToLongDate(dateValue);
				valueParm[1] = String.valueOf(DateUtility.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("30565", "", valueParm)));
			}
			break;
		case "FUTURE":
			if(DateUtility.compare( DateUtility.getAppDate(),dateValue) >= 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName()+":"+ DateUtility.formatToLongDate(dateValue);
				valueParm[1] = String.valueOf(DateUtility.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("91121", "", valueParm)));
			}
			break;
		case "PAST":
			if(DateUtility.compare(DateUtility.getAppDate(),dateValue) <= 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName()+":"+ DateUtility.formatToLongDate(dateValue);
				valueParm[1] = String.valueOf(DateUtility.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetails("30565", "", valueParm)));
			}
			break;

		default:
			break;
		}
		return errors;
	}

}
