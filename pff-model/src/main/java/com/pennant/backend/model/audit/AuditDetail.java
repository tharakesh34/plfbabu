/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : AuditDetail.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.audit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.ClassUtil;

public class AuditDetail implements java.io.Serializable {
	private static final Logger logger = LogManager.getLogger(AuditDetail.class);
	private static final long serialVersionUID = 4576632220854658678L;

	private long auditId = Long.MIN_VALUE;
	private Timestamp auditDate;
	private String auditTranType;
	private int auditSeq;
	private String auditField;
	private String auditValue;
	private Object befImage;
	private Object modelData;
	private boolean extended;

	private Timestamp lovDescRcdInTime;
	private Timestamp lovDescRcdOutTime;
	private String lovDescRecordStatus;
	private String lovDescRoleCode = "";
	private String lovDescNextRoleCode = "";
	private Object lovDescAuditRef;

	private List<ErrorDetail> errors = new ArrayList<>();

	public AuditDetail() {
		super();
	}

	public AuditDetail copyEntity() {
		AuditDetail entity = new AuditDetail();
		entity.setAuditId(this.auditId);
		entity.setAuditDate(this.auditDate);
		entity.setAuditTranType(this.auditTranType);
		entity.setAuditSeq(this.auditSeq);
		entity.setAuditField(this.auditField);
		entity.setAuditValue(this.auditValue);
		entity.setExtended(this.extended);
		entity.setLovDescRcdInTime(this.lovDescRcdInTime);
		entity.setLovDescRcdOutTime(this.lovDescRcdOutTime);
		entity.setLovDescRecordStatus(this.lovDescRecordStatus);
		entity.setLovDescRoleCode(this.lovDescRoleCode);
		entity.setLovDescNextRoleCode(this.lovDescNextRoleCode);
		return entity;
	}

	@SuppressWarnings("unchecked")
	public AuditDetail(String auditTranType, int auditSeq, Object befImage, Object modelData) {
		super();

		this.auditTranType = auditTranType;
		this.auditSeq = auditSeq;
		this.befImage = befImage;
		this.modelData = modelData;

		String[] fields = { "", "" };

		if (ClassUtil.isMethodExists(modelData, "getExcludeFields")) {
			try {
				Method method = modelData.getClass().getMethod("getExcludeFields");
				Object object = method.invoke(modelData);

				if (object.getClass().isInstance(String.class)) {
					fields = getFieldDetails(modelData, (String) object);
				} else if (object.getClass().isInstance(new HashSet<String>())) {
					fields = getFieldDetails(modelData, (HashSet<String>) object);
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				logger.warn("Unable to fetch the excluded fields.", e);
			}
		} else {
			fields = getFieldDetails(modelData);
		}

		this.auditField = fields[0];
		this.auditValue = fields[1];
	}

	public AuditDetail(String auditTranType, int auditSeq, String auditField, String auditValue, Object befImage,
			Object modelData) {
		super();

		this.auditTranType = auditTranType;
		this.auditSeq = auditSeq;
		this.auditField = auditField;
		this.auditValue = auditValue;
		this.befImage = befImage;
		this.modelData = modelData;
	}

	// New methods for copying the properties of AuditHeader
	public AuditDetail getNewCopyInstance() {
		AuditDetail auditDetail = new AuditDetail();
		BeanUtils.copyProperties(this, auditDetail);
		if (this.auditDate != null) {
			BeanUtils.copyProperties(this.auditDate, auditDetail.auditDate);
		}
		if (this.befImage != null) {
			auditDetail.setBefImage(new Object());
			BeanUtils.copyProperties(this.befImage, auditDetail.befImage);
		}
		if (this.modelData != null) {
			auditDetail.setModelData(new Object());
			BeanUtils.copyProperties(this.modelData, auditDetail.modelData);
		}
		if (this.lovDescRcdInTime != null) {
			BeanUtils.copyProperties(this.lovDescRcdInTime, auditDetail.lovDescRcdInTime);
		}
		if (this.lovDescRcdOutTime != null) {
			BeanUtils.copyProperties(this.lovDescRcdOutTime, auditDetail.lovDescRcdOutTime);
		}
		if (this.lovDescAuditRef != null) {
			auditDetail.setLovDescAuditRef(new Object());
			BeanUtils.copyProperties(this.lovDescAuditRef, auditDetail.lovDescAuditRef);
		}
		if (errors != null) {
			auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
			BeanUtils.copyProperties(errors, auditDetail.errors);
		}
		return auditDetail;
	}

	public Object getBefImage() {
		return befImage;
	}

	public void setBefImage(Object befImage) {
		this.befImage = befImage;
	}

	public Object getModelData() {
		return modelData;
	}

	public void setModelData(Object modelData) {
		this.modelData = modelData;
	}

	public long getId() {
		return auditId;
	}

	public void setId(long id) {
		this.auditId = id;
	}

	public long getAuditId() {
		return auditId;
	}

	public void setAuditId(long auditId) {
		this.auditId = auditId;
	}

	public Timestamp getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Timestamp auditDate) {
		this.auditDate = auditDate;
	}

	public String getAuditTranType() {
		return auditTranType;
	}

	public void setAuditTranType(String auditTranType) {
		this.auditTranType = auditTranType;
	}

	public int getAuditSeq() {
		return auditSeq;
	}

	public void setAuditSeq(int auditSeq) {
		this.auditSeq = auditSeq;
	}

	public String getAuditField() {
		return auditField;
	}

	public void setAuditField(String auditField) {
		this.auditField = auditField;
	}

	public String getAuditValue() {
		return auditValue;
	}

	public void setAuditValue(String auditValue) {
		this.auditValue = auditValue;
	}

	public List<ErrorDetail> getErrorDetails() {
		return errors;
	}

	public void setErrorDetails(List<ErrorDetail> errorDetails) {
		if (errorDetails == null) {
			return;
		}
		this.errors = errorDetails;
	}

	public Timestamp getLovDescRcdInTime() {
		return lovDescRcdInTime;
	}

	public void setLovDescRcdInTime(Timestamp lovDescRcdInTime) {
		this.lovDescRcdInTime = lovDescRcdInTime;
	}

	public Timestamp getLovDescRcdOutTime() {
		return lovDescRcdOutTime;
	}

	public void setLovDescRcdOutTime(Timestamp lovDescRcdOutTime) {
		this.lovDescRcdOutTime = lovDescRcdOutTime;
	}

	public String getLovDescRecordStatus() {
		return lovDescRecordStatus;
	}

	public void setLovDescRecordStatus(String lovDescRecordStatus) {
		this.lovDescRecordStatus = lovDescRecordStatus;
	}

	public String getLovDescRoleCode() {
		return lovDescRoleCode;
	}

	public void setLovDescRoleCode(String lovDescRoleCode) {
		this.lovDescRoleCode = lovDescRoleCode;
	}

	public String getLovDescNextRoleCode() {
		return lovDescNextRoleCode;
	}

	public void setLovDescNextRoleCode(String lovDescNextRoleCode) {
		this.lovDescNextRoleCode = lovDescNextRoleCode;
	}

	public void setErrorDetail(ErrorDetail errorDetails) {
		if (errorDetails == null) {
			return;
		}
		errors.add(errorDetails);
	}

	public void addErrorDetails(List<ErrorDetail> errorDetails) {
		if (errorDetails == null) {
			return;
		}
		errors.addAll(errorDetails);
	}

	/*
	 * public void setErrorDetail(long errorCode) { setErrorDetail(new ErrorDetail("Key", String.valueOf(errorCode),
	 * null, null)); }
	 */

	public void setLovDescAuditRef(Object lovDescAuditRef) {
		this.lovDescAuditRef = lovDescAuditRef;
	}

	public Object getLovDescAuditRef() {
		return lovDescAuditRef;
	}

	public boolean isExtended() {
		return extended;
	}

	public void setExtended(boolean extended) {
		this.extended = extended;
	}

	/**
	 * Method for Getting Field List for any object(VO)
	 */
	public static String[] getFieldDetails(Object detailObject, String excludeField) {
		String[] auditField = new String[2];
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		if (detailObject != null) {
			ArrayList<String> arrayFields = getFieldList(detailObject);
			for (int j = 0; j < arrayFields.size(); j++) {
				if (!excludeField.contains(arrayFields.get(j))) {

					fields.append(arrayFields.get(j));
					values.append(":" + arrayFields.get(j));

					if (j < arrayFields.size() - 1) {
						fields.append(" , ");
						values.append(" , ");
					}
				}

			}
		}
		auditField[0] = fields.toString();
		if (auditField[0].trim().endsWith(",")) {
			auditField[0] = auditField[0].substring(0, auditField[0].trim().length() - 1);
		}
		auditField[1] = values.toString();
		if (auditField[1].trim().endsWith(",")) {
			auditField[1] = auditField[1].substring(0, auditField[1].trim().length() - 1);
		}
		return auditField;
	}

	/*
	 * The below variable and methods duplicated from PennantJavaUtil.java to move the AuditDetail class into model
	 * layer.
	 * 
	 * TODO The below methods needs to be validate
	 */
	private static String excludeFields = "serialVersionUID,newRecord,lovValue,befImage,userDetails,userAction,loginAppCode,loginUsrId,loginGrpCode,loginRoleCd,customerQDE,auditDetailMap,lastMaintainedUser,lastMaintainedOn,";

	/**
	 * Method for Getting Field List for any object(VO)
	 */
	public static String[] getFieldDetails(Object detailObject) {
		String[] auditField = new String[2];
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		if (detailObject != null) {
			ArrayList<String> arrayFields = getFieldList(detailObject);
			for (int j = 0; j < arrayFields.size(); j++) {

				fields.append(arrayFields.get(j));
				values.append(":" + arrayFields.get(j));

				if (j < arrayFields.size() - 1) {
					fields.append(" , ");
					values.append(" , ");
				}
			}
		}
		auditField[0] = fields.toString();
		auditField[1] = values.toString();

		return auditField;
	}

	/**
	 * Method for Separating fields from LOV fields and Unused excluded fields
	 */
	private static ArrayList<String> getFieldList(Object object) {
		Field[] fields = null;

		if (object instanceof AbstractWorkflowEntity) {
			fields = ClassUtil.getAllFields(object);
		} else {
			fields = ClassUtil.getFields(object);
		}

		ArrayList<String> arrayFields = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			if (!excludeFields.contains(fields[i].getName() + ",") && !fields[i].getName().startsWith("lovDesc")
					&& !fields[i].getName().startsWith("list") && !fields[i].getName().endsWith("List")) {
				arrayFields.add(fields[i].getName());
			}
		}

		return arrayFields;
	}

	/**
	 * Method for Getting Field List for any object(VO)
	 */
	public static String[] getFieldDetails(Object detailObject, Set<String> excludeFields) {
		String[] auditField = new String[2];
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		if (detailObject != null) {
			ArrayList<String> arrayFields = getFieldList(detailObject);
			for (int j = 0; j < arrayFields.size(); j++) {
				if (!excludeFields.contains(arrayFields.get(j))) {

					fields.append(arrayFields.get(j));
					values.append(":" + arrayFields.get(j));

					if (j < arrayFields.size() - 1) {
						fields.append(" , ");
						values.append(" , ");
					}
				}

			}
		}
		auditField[0] = fields.toString();
		if (auditField[0].trim().endsWith(",")) {
			auditField[0] = auditField[0].substring(0, auditField[0].trim().length() - 1);
		}
		auditField[1] = values.toString();
		if (auditField[1].trim().endsWith(",")) {
			auditField[1] = auditField[1].substring(0, auditField[1].trim().length() - 1);
		}
		return auditField;
	}

}
