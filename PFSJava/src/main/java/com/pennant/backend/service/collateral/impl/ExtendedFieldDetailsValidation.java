package com.pennant.backend.service.collateral.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.VASConsatnts;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtendedFieldDetailsValidation {

	private static final Logger logger = LogManager.getLogger(ExtendedFieldDetailsValidation.class);

	private ExtendedFieldRenderDAO extendedFieldRenderDAO;

	public ExtendedFieldRenderDAO getExtendedFieldRenderDAO() {
		return extendedFieldRenderDAO;
	}

	public ExtendedFieldDetailsValidation(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public List<AuditDetail> vaildateDetails(List<AuditDetail> auditDetails, String method, String usrLanguage,
			String tableName) {

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
			tempExtendedFieldRender = getExtendedFieldRenderDAO().getExtendedFieldDetails(
					extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(), tableName, "_Temp");
		}

		ExtendedFieldRender befExtendedFieldRender = getExtendedFieldRenderDAO().getExtendedFieldDetails(
				extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(), tableName, "");
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

		if (extendedFieldRender.isNewRecord()) { // for New record or new record into work flow

			if (!extendedFieldRender.isWorkflow()) {// With out Work flow only new records
				if (befExtendedFieldRender != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (extendedFieldRender.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type
																									// is new
					if (befExtendedFieldRender != null || tempExtendedFieldRender != null) { // if records already
																								// exists in the main
																								// table
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befExtendedFieldRender == null || tempExtendedFieldRender != null) {
						auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!extendedFieldRender.isWorkflow()) { // With out Work flow for update and delete

				if (befExtendedFieldRender == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldExtendedFieldRender != null
							&& !oldExtendedFieldRender.getLastMntOn().equals(befExtendedFieldRender.getLastMntOn())) {
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

				if (tempExtendedFieldRender == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempExtendedFieldRender != null && oldExtendedFieldRender != null
						&& !oldExtendedFieldRender.getLastMntOn().equals(tempExtendedFieldRender.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !extendedFieldRender.isWorkflow()) {
			extendedFieldRender.setBefImage(befExtendedFieldRender);
		}
		return auditDetail;
	}

	public List<ErrorDetail> validateExtendedFieldData(ExtendedFieldDetail exdConfigDetail,
			ExtendedFieldData exdFieldData) {
		logger.debug(Literal.ENTERING);
		List<ErrorDetail> errors = new ArrayList<ErrorDetail>();
		String fieldName = exdFieldData.getFieldName();
		String fieldValue = Objects.toString(exdFieldData.getFieldValue(), "");

		switch (exdConfigDetail.getFieldType()) {
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
			if (!StringUtils.equals(exdConfigDetail.getFieldType(), ExtendedFieldConstants.FIELDTYPE_TIME)) {
				errors = dateValidation(exdConfigDetail, dateValue, errors);
			}
			exdFieldData.setFieldValue(dateValue);
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
			errors = dateValidation(exdConfigDetail, dateTimeVal, errors);
			exdFieldData.setFieldValue(dateTimeVal);
			break;
		case ExtendedFieldConstants.FIELDTYPE_AMOUNT:
			BigDecimal decimalValue = BigDecimal.ZERO;
			try {
				// PSD: 147032 amount is getting changed after for 16 character value.
				if (fieldValue != null) {
					String value = StringUtils.trimToNull(fieldValue);
					decimalValue = new BigDecimal(value);
				} else {
					String[] valueParm = new String[2];
					valueParm[0] = fieldName;
					valueParm[1] = "number";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90299", "", valueParm)));
				}

			} catch (Exception e) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = "number";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90299", "", valueParm)));
			}
			if (fieldValue.length() > exdConfigDetail.getFieldLength() + 2) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(exdConfigDetail.getFieldLength());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
			}
			exdFieldData.setFieldValue(decimalValue);
			break;
		case ExtendedFieldConstants.FIELDTYPE_INT:
			int intValue = 0;
			if (fieldValue.length() > exdConfigDetail.getFieldLength()) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(exdConfigDetail.getFieldLength());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
				return errors;
			}
			try {
				intValue = Integer.parseInt(fieldValue);
				if (exdConfigDetail.getFieldMaxValue() > 0 || exdConfigDetail.getFieldMinValue() > 0) {
					if (!(Long.valueOf(fieldValue) >= exdConfigDetail.getFieldMinValue()
							&& Long.valueOf(fieldValue) <= exdConfigDetail.getFieldMaxValue())) {
						String valueParm[] = new String[3];
						valueParm[0] = exdConfigDetail.getFieldName();
						valueParm[1] = String.valueOf(exdConfigDetail.getFieldMinValue());
						valueParm[2] = String.valueOf(exdConfigDetail.getFieldMaxValue());
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65031", "", valueParm)));
					}
				}
			} catch (Exception e) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = "number";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90299", "", valueParm)));
			}
			exdFieldData.setFieldValue(intValue);
			break;
		case ExtendedFieldConstants.FIELDTYPE_LONG:
			long longValue = 0;
			if (fieldValue.length() > exdConfigDetail.getFieldLength()) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(exdConfigDetail.getFieldLength());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
				return errors;
			}
			try {
				longValue = Long.parseLong(fieldValue);
				if (exdConfigDetail.getFieldMaxValue() > 0 || exdConfigDetail.getFieldMinValue() > 0) {
					if (!(Long.valueOf(fieldValue) >= exdConfigDetail.getFieldMinValue()
							&& Long.valueOf(fieldValue) <= exdConfigDetail.getFieldMaxValue())) {
						String valueParm[] = new String[3];
						valueParm[0] = exdConfigDetail.getFieldName();
						valueParm[1] = String.valueOf(exdConfigDetail.getFieldMinValue());
						valueParm[2] = String.valueOf(exdConfigDetail.getFieldMaxValue());
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65031", "", valueParm)));
					}
				}
			} catch (Exception e) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = "number";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90299", "", valueParm)));
			}
			exdFieldData.setFieldValue(longValue);
			break;
		case ExtendedFieldConstants.FIELDTYPE_TEXT:
		case ExtendedFieldConstants.FIELDTYPE_MULTILINETEXT:
		case ExtendedFieldConstants.FIELDTYPE_UPPERTEXT:
			if (fieldValue.length() > exdConfigDetail.getFieldLength()) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(exdConfigDetail.getFieldLength());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
				return errors;
			}

			if (StringUtils.isNotBlank(exdConfigDetail.getFieldConstraint())) {
				if (PennantRegularExpressions.getRegexMapper(exdConfigDetail.getFieldConstraint()) != null) {
					Pattern pattern = Pattern
							.compile(PennantRegularExpressions.getRegexMapper(exdConfigDetail.getFieldConstraint()));
					Matcher matcher = pattern.matcher(fieldValue);
					if (matcher.matches() == false) {
						String[] valueParm = new String[1];
						valueParm[0] = fieldName;
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90322", "", valueParm)));
					}
				}
			}
			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_UPPERTEXT, exdConfigDetail.getFieldType())) {
				exdFieldData.setFieldValue(fieldValue.toUpperCase());
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
			exdFieldData.setFieldValue(fieldValue.substring(0, 8));
			break;
		case ExtendedFieldConstants.FIELDTYPE_DECIMAL:
			double reqValue = 0;
			String decValue = Objects.toString(fieldValue, "");
			if (decValue.contains(".")) {
				String bfrPression = decValue.substring(0, decValue.indexOf("."));
				String aftrPression = decValue.substring(decValue.indexOf(".") + 1, decValue.length());
				// checking the before and after pression values
				if (bfrPression.length() > exdConfigDetail.getFieldLength()
						|| aftrPression.length() > exdConfigDetail.getFieldPrec()) {
					String[] valueParm = new String[2];
					valueParm[0] = fieldName;
					valueParm[1] = String.valueOf(exdConfigDetail.getFieldLength()) + ","
							+ exdConfigDetail.getFieldPrec();
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
					return errors;
				}
			} else if (decValue.length() > exdConfigDetail.getFieldLength()) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(exdConfigDetail.getFieldLength()) + "," + exdConfigDetail.getFieldPrec();
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
				return errors;
			}
			try {
				reqValue = Double.parseDouble(decValue);
			} catch (Exception e) {
				logger.error("Exception : ", e);
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = "number";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90299", "", valueParm)));
				return errors;
			}
			if (exdConfigDetail.getFieldMaxValue() > 0 || exdConfigDetail.getFieldMinValue() > 0) {
				if (Math.round(reqValue) > exdConfigDetail.getFieldMaxValue()
						|| Math.round(reqValue) < exdConfigDetail.getFieldMinValue()) {
					String[] valueParm = new String[3];
					valueParm[0] = fieldName;
					valueParm[1] = String.valueOf(exdConfigDetail.getFieldMinValue());
					valueParm[2] = String.valueOf(exdConfigDetail.getFieldMaxValue());
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65031", "", valueParm)));
					return errors;
				}
			}
			exdFieldData.setFieldValue(reqValue);
			break;
		case ExtendedFieldConstants.FIELDTYPE_BOOLEAN:
			Boolean value;
			if (StringUtils.isNotBlank(fieldValue)) {
				if (StringUtils.equals(fieldValue, "true") || StringUtils.equals(fieldValue, "false")) {
					if (App.DATABASE == Database.POSTGRES) {
						value = fieldValue.equals("true") ? true : false;
						exdFieldData.setFieldValue(value);
					} else {
						int boolValue = fieldValue.equals("true") ? 1 : 0;
						exdFieldData.setFieldValue(boolValue);
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
			String name = exdConfigDetail.getFieldList();
			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_BASERATE, exdConfigDetail.getFieldType())) {
				name = "BaseRate";
			}
			if (name != null && name.contains(PennantConstants.DELIMITER_COMMA)) {
				String[] values = name.split(PennantConstants.DELIMITER_COMMA);
				name = values[0];
			}
			if (fieldValue.length() > exdConfigDetail.getFieldLength()) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(exdConfigDetail.getFieldLength());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
			}
			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(name);
			if (moduleMapping != null) {
				String[] lovFields = moduleMapping.getLovFields();
				Object[][] filters = moduleMapping.getLovFilters();
				int count = 0;
				if (!StringUtils.contains(moduleMapping.getTableName(), "Builder")) {
					if (filters != null) {
						if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_EXTENDEDCOMBO,
								exdConfigDetail.getFieldType())) {
							count = extendedFieldRenderDAO.validateExtendedComboBoxData(moduleMapping.getTableName(),
									lovFields[0], filters, fieldValue);
						} else {
							count = extendedFieldRenderDAO.validateMasterData(moduleMapping.getTableName(),
									lovFields[0], (String) filters[0][0], fieldValue);
						}
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
			if (StringUtils.equals(ExtendedFieldConstants.FIELDTYPE_RADIO, exdConfigDetail.getFieldType())) {
				if (fieldValue.length() > exdConfigDetail.getFieldLength()) {
					String[] valueParm = new String[2];
					valueParm[0] = fieldName;
					valueParm[1] = String.valueOf(exdConfigDetail.getFieldLength());
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
					return errors;
				}
			}
			String[] values = new String[0];
			boolean isValid = false;
			String staticList = exdConfigDetail.getFieldList();
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
			if (fieldValue.length() > (exdConfigDetail.getFieldLength() - exdConfigDetail.getFieldPrec())) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = String.valueOf(exdConfigDetail.getFieldLength() - exdConfigDetail.getFieldPrec());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm)));
			}
			if (exdConfigDetail.getFieldMaxValue() > 0 || exdConfigDetail.getFieldMinValue() > 0) {
				if (Integer.valueOf(fieldValue) > exdConfigDetail.getFieldMaxValue()
						|| Integer.valueOf(fieldValue) < exdConfigDetail.getFieldMinValue()) {
					String[] valueParm = new String[3];
					valueParm[0] = fieldName;
					valueParm[1] = String.valueOf(exdConfigDetail.getFieldMinValue());
					valueParm[2] = String.valueOf(exdConfigDetail.getFieldMaxValue());
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65031", "", valueParm)));
				}
			}
			break;
		case ExtendedFieldConstants.FIELDTYPE_PERCENTAGE:
			double percentage = 0;
			/*
			 * if (fieldValue.length() > (exdConfigDetail.getFieldLength() - exdConfigDetail.getFieldPrec())) { String[]
			 * valueParm = new String[2]; valueParm[0] = fieldName; valueParm[1] =
			 * String.valueOf(exdConfigDetail.getFieldLength() - exdConfigDetail.getFieldPrec());
			 * errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90300", "", valueParm))); }
			 */
			try {
				percentage = Double.valueOf(fieldValue);
				if (percentage < 0 || percentage > 100) {
					String[] valueParm = new String[3];
					valueParm[0] = fieldName;
					valueParm[1] = "0";
					valueParm[2] = "100";
					errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65031", "", valueParm)));
				}
				if (exdConfigDetail.getFieldMaxValue() > 0 || exdConfigDetail.getFieldMinValue() > 0) {
					if (percentage > exdConfigDetail.getFieldMaxValue()
							|| percentage < exdConfigDetail.getFieldMinValue()) {
						String[] valueParm = new String[3];
						valueParm[0] = fieldName;
						valueParm[1] = String.valueOf(exdConfigDetail.getFieldMinValue());
						valueParm[2] = String.valueOf(exdConfigDetail.getFieldMaxValue());
						errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65031", "", valueParm)));
					}
				}
			} catch (Exception e) {
				String[] valueParm = new String[2];
				valueParm[0] = fieldName;
				valueParm[1] = "number";
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("90299", "", valueParm)));
			}

			exdFieldData.setFieldValue(percentage);

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
			String key1 = exdConfigDetail.getFieldList();
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
			String multiStaticList = exdConfigDetail.getFieldList();
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
		String[] value = exdConfigDetail.getFieldConstraint().split(",");
		switch (value[0]) {
		case "RANGE":
			if (value[1] != null && value[2] != null) {
				if (dateValue.before(DateUtility.parse(value[1], PennantConstants.dateFormat))
						|| dateValue.after(DateUtility.parse(value[2], PennantConstants.dateFormat))) {
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
					DateUtility.addDays(SysParamUtil.getAppDate(), Integer.parseInt(value[1]))) > 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + dateValue;
				valueParm[1] = String
						.valueOf(DateUtility.addDays(SysParamUtil.getAppDate(), Integer.parseInt(value[1])));
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65027", "", valueParm)));
			}
			break;
		case "PAST_DAYS":
			if (DateUtility.compare(dateValue,
					DateUtility.addDays(SysParamUtil.getAppDate(), -(Integer.parseInt(value[1])))) < 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + dateValue;
				valueParm[1] = String
						.valueOf(DateUtility.addDays(SysParamUtil.getAppDate(), -(Integer.parseInt(value[1]))));
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm)));
			}
			break;
		case "FUTURE_TODAY":
			if (DateUtility.compare(dateValue, SysParamUtil.getAppDate()) < 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + dateValue;
				valueParm[1] = String.valueOf(SysParamUtil.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm)));
			}
			break;
		case "PAST_TODAY":
			if (DateUtility.compare(dateValue, SysParamUtil.getAppDate()) > 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + DateUtility.formatToLongDate(dateValue);
				valueParm[1] = String.valueOf(SysParamUtil.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65027", "", valueParm)));
			}
			break;
		case "FUTURE":
			if (DateUtility.compare(SysParamUtil.getAppDate(), dateValue) >= 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + DateUtility.formatToLongDate(dateValue);
				valueParm[1] = String.valueOf(SysParamUtil.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("91125", "", valueParm)));
			}
			break;
		case "PAST":
			if (DateUtility.compare(SysParamUtil.getAppDate(), dateValue) <= 0) {
				String valueParm[] = new String[2];
				valueParm[0] = exdConfigDetail.getFieldName() + ":" + DateUtility.formatToLongDate(dateValue);
				valueParm[1] = String.valueOf(SysParamUtil.getAppDate());
				errors.add(ErrorUtil.getErrorDetail(new ErrorDetail("65027", "", valueParm)));
			}
			break;

		default:
			break;
		}
		return errors;
	}

}
