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

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.util.PennantJavaUtil;

public class AuditDetail  implements java.io.Serializable,Entity {

	private static final long serialVersionUID = 4576632220854658678L;

	private long auditId = Long.MIN_VALUE;
	private Timestamp auditDate;
	private String auditTranType;
	private int auditSeq;
	private String auditField;
	private String auditValue;
	private Object befImage;
	private Object modelData;


	private Timestamp lovDescRcdInTime;
	private Timestamp lovDescRcdOutTime;
	private String lovDescRecordStatus;
	private String lovDescRoleCode="";
	private String lovDescNextRoleCode= "";
	private Object lovDescAuditRef;

	private ArrayList<ErrorDetails> errorDetails;

	public AuditDetail() {
		super();
	}

	public AuditDetail(long auditId, Timestamp auditDate, String auditTranType, int auditSeq, String auditField,String auditValue,
			Object befImage,Object modelData) {
		super();
		this.auditId = auditId;
		this.auditDate = auditDate;
		this.auditTranType = auditTranType;
		this.auditSeq = auditSeq;
		this.auditField = auditField;
		this.auditValue = auditValue;
		this.befImage=befImage;
		this.modelData=modelData;
	}

	public AuditDetail(String auditTranType, int auditSeq, String auditField,String auditValue,Object befImage,Object modelData) {
		super();
		this.auditTranType = auditTranType;
		this.auditSeq = auditSeq;
		this.auditField = auditField;
		this.auditValue = auditValue;
		this.befImage=befImage;
		this.modelData=modelData;
	}

	@SuppressWarnings("unchecked")
    public AuditDetail(String auditTranType, int auditSeq, Object befImage,Object modelData) {
		super();
		this.auditTranType = auditTranType;
		this.auditSeq = auditSeq;
		this.befImage=befImage;
		this.modelData=modelData;
		String[] fields=null;

		try {
			Method method = modelData.getClass().getMethod("getExcludeFields");
			Object object= null;
			if(method!=null){
				object=method.invoke(modelData);
				if(object.getClass().isInstance(String.class)){
					fields = PennantJavaUtil.getFieldDetails(modelData,(String) object);
				}else  if(object.getClass().isInstance(new HashSet<String>())){
					fields = PennantJavaUtil.getFieldDetails(modelData,(HashSet<String>) object);
				}
			}



		} catch (Exception e) {;
		}

		if(fields==null){
			fields = PennantJavaUtil.getFieldDetails(modelData);
		}
		this.auditField = fields[0];
		this.auditValue = fields[1];
		
		
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
		return (getId() == Long.MIN_VALUE);
	}

	@Override
	public long getId() {
		return auditId;
	}

	@Override
	public void setId(long id) {
		this.auditId=id;
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

	public ArrayList<ErrorDetails> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(ArrayList<ErrorDetails> errorDetails) {
		this.errorDetails = errorDetails;
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

	public void setErrorDetail(ErrorDetails errorDetail) {

		if(errorDetail!=null){
			if (this.errorDetails == null){
				this.errorDetails= new ArrayList<ErrorDetails>();
			}
			this.errorDetails.add(errorDetail);
		}
	}
	

	public void setErrorDetail(ErrorDetails errorDetail,String errorField) {

		if(errorDetail!=null){
			if (this.errorDetails == null){
				this.errorDetails= new ArrayList<ErrorDetails>();
			}
			this.errorDetails.add(errorDetail);
		}
	}

	public void setLovDescAuditRef(Object lovDescAuditRef) {
		this.lovDescAuditRef = lovDescAuditRef;
	}

	public Object getLovDescAuditRef() {
		return lovDescAuditRef;
	}
}
