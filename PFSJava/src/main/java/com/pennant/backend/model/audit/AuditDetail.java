/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  AuditDetail.java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.model.audit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.ClassUtil;

public class AuditDetail implements java.io.Serializable, Entity {
	private static final Logger	logger				= Logger.getLogger(AuditDetail.class);
	private static final long	serialVersionUID	= 4576632220854658678L;

	private long				auditId				= Long.MIN_VALUE;
	private Timestamp			auditDate;
	private String				auditTranType;
	private int					auditSeq;
	private String				auditField;
	private String				auditValue;
	private Object				befImage;
	private Object				modelData;
	private boolean				extended;

	private Timestamp			lovDescRcdInTime;
	private Timestamp			lovDescRcdOutTime;
	private String				lovDescRecordStatus;
	private String				lovDescRoleCode		= "";
	private String				lovDescNextRoleCode	= "";
	private Object				lovDescAuditRef;

	private List<ErrorDetails>	errors				= new ArrayList<>();

	public AuditDetail() {
		super();
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
					fields = PennantJavaUtil.getFieldDetails(modelData, (String) object);
				} else if (object.getClass().isInstance(new HashSet<String>())) {
					fields = PennantJavaUtil.getFieldDetails(modelData, (HashSet<String>) object);
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				logger.warn("Unable to fetch the excluded fields.", e);
			}
		} else {
			fields = PennantJavaUtil.getFieldDetails(modelData);
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
			auditDetail.setErrorDetails(new ArrayList<ErrorDetails>());
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

	@Override
	public boolean isNew() {
		return getId() == Long.MIN_VALUE;
	}

	@Override
	public long getId() {
		return auditId;
	}

	@Override
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

	public List<ErrorDetails> getErrorDetails() {
		return errors;
	}

	public void setErrorDetails(List<ErrorDetails> errorDetails) {
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

	public void setErrorDetail(ErrorDetails errorDetails) {
		if (errorDetails == null) {
			return;
		}

		if (errors == null) {
			errors = new ArrayList<>();
		}

		errors.add(errorDetails);
	}

	public void setErrorDetail(long errorCode) {
		setErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, String.valueOf(errorCode), null, null));
	}

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
}
