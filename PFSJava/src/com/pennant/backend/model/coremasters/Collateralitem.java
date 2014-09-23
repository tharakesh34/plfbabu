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
 *																							*
 * FileName    		:  Collateralitem.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-02-2013    														*
 *                                                                  						*
 * Modified Date    :  20-02-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-02-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.coremasters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>Collateralitem table</b>.<br>
 *
 */
public class Collateralitem implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String hYCUS = null;
	private String hYCLC;
	private String hYDLP;
	private String hYDLR;
	private String hYDBNM;
	private String hYAB;
	private String hYAN;
	private String hYAS;
	private String hYCLP;
	private String hYCLR;
	private boolean hYCCM;
	private String lovDescHYCLPName;
	private String lovDescHYCNAName;
	private String lovDescHYCCMName;
	private String hYCNA;
	private String hYCLO;
	private String lovDescHYCLOName;
	private String hYDPC;
	private String lovDescHYDPCName;
	private boolean hYCPI;
	private String lovDescHYCPIName;
	private Date hYCXD;
	private Date hYLRD;
	private String hYFRQ;
	private String lovDescHYFRQName;
	private Date hYNRD;
	private BigDecimal hYNOU = BigDecimal.ONE;
	private BigDecimal hYUNP;
	private String hYCCY = "BHD";
	private String lovDescHYCCYName = "Bahraini Dinar";
	private BigDecimal hYCLV;
	private BigDecimal hYSVM;
	private BigDecimal hYMCV;
	private BigDecimal hYBKV;
	private BigDecimal hYTOTA;
	private BigDecimal hYISV;
	private Date hYIXD;
	private String hYNR1;
	private String hYNR2;
	private String hYNR3;
	private String hYNR4;
	private Date hYDLM;
	private int version;
	private long lastMntBy;
	private Timestamp lastMntOn;
	private boolean newRecord=false;
	private String lovValue;
	private Collateralitem befImage;
	private LoginUserDetails userDetails;

	private String recordStatus;
	private String roleCode="";
	private String nextRoleCode= "";
	private String taskId="";
	private String nextTaskId= "";
	private String recordType;
	private String userAction = "Save";
	private long workflowId = 0;

	public boolean isNew() {
		return isNewRecord();
	}

	public Collateralitem() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("Collateralitem");
	}

	public Collateralitem(String id) {
		this.setId(id);
	}

	//Getter and Setter methods
	
	public String getId() {
		return hYCUS;
	}
	
	public void setId (String id) {
		this.hYCUS = id;
	}
	
	public String getHYCUS() {
		return hYCUS;
	}
	public void setHYCUS(String hYCUS) {
		this.hYCUS = hYCUS;
	}
	
	
		
	
	public String getHYCLC() {
		return hYCLC;
	}
	public void setHYCLC(String hYCLC) {
		this.hYCLC = hYCLC;
	}
	
	
		
	
	public String getHYDLP() {
		return hYDLP;
	}
	public void setHYDLP(String hYDLP) {
		this.hYDLP = hYDLP;
	}
	
	
		
	
	public String getHYDLR() {
		return hYDLR;
	}
	public void setHYDLR(String hYDLR) {
		this.hYDLR = hYDLR;
	}
	
	
		
	
	public String getHYDBNM() {
		return hYDBNM;
	}
	public void setHYDBNM(String hYDBNM) {
		this.hYDBNM = hYDBNM;
	}
	
	
		
	
	public String getHYAB() {
		return hYAB;
	}
	public void setHYAB(String hYAB) {
		this.hYAB = hYAB;
	}
	
	
		
	
	public String getHYAN() {
		return hYAN;
	}
	public void setHYAN(String hYAN) {
		this.hYAN = hYAN;
	}
	
	
		
	
	public String getHYAS() {
		return hYAS;
	}
	public void setHYAS(String hYAS) {
		this.hYAS = hYAS;
	}
	
	
		
	
	public String getHYCLP() {
		return hYCLP;
	}
	public void setHYCLP(String hYCLP) {
		this.hYCLP = hYCLP;
	}
	
	
		
	
	public String getHYCLR() {
		return hYCLR;
	}
	public void setHYCLR(String hYCLR) {
		this.hYCLR = hYCLR;
	}
			
	public boolean ishYCCM() {
    	return hYCCM;
    }

	public void sethYCCM(boolean hYCCM) {
    	this.hYCCM = hYCCM;
    }
	
		
	public String getLovDescHYCLPName() {
    	return lovDescHYCLPName;
    }

	public void setLovDescHYCLPName(String lovDescHYCLPName) {
    	this.lovDescHYCLPName = lovDescHYCLPName;
    }

	public String getLovDescHYCNAName() {
    	return lovDescHYCNAName;
    }

	public void setLovDescHYCNAName(String lovDescHYCNAName) {
    	this.lovDescHYCNAName = lovDescHYCNAName;
    }

	public String getLovDescHYCCMName() {
		return this.lovDescHYCCMName;
	}

	public void setLovDescHYCCMName (String lovDescHYCCMName) {
		this.lovDescHYCCMName = lovDescHYCCMName;
	}
	
		
	
	public String getHYCNA() {
		return hYCNA;
	}
	public void setHYCNA(String hYCNA) {
		this.hYCNA = hYCNA;
	}
	
	
		
	
	public String getHYCLO() {
		return hYCLO;
	}
	public void setHYCLO(String hYCLO) {
		this.hYCLO = hYCLO;
	}
	

	public String getLovDescHYCLOName() {
		return this.lovDescHYCLOName;
	}

	public void setLovDescHYCLOName (String lovDescHYCLOName) {
		this.lovDescHYCLOName = lovDescHYCLOName;
	}
	
		
	
	public String getHYDPC() {
		return hYDPC;
	}
	public void setHYDPC(String hYDPC) {
		this.hYDPC = hYDPC;
	}
	

	public String getLovDescHYDPCName() {
		return this.lovDescHYDPCName;
	}

	public void setLovDescHYDPCName (String lovDescHYDPCName) {
		this.lovDescHYDPCName = lovDescHYDPCName;
	}
	
		
	
	public boolean isHYCPI() {
		return hYCPI;
	}
	public void setHYCPI(boolean hYCPI) {
		this.hYCPI = hYCPI;
	}
	

	public String getLovDescHYCPIName() {
		return this.lovDescHYCPIName;
	}

	public void setLovDescHYCPIName (String lovDescHYCPIName) {
		this.lovDescHYCPIName = lovDescHYCPIName;
	}
	
		
	
	public Date getHYCXD() {
		return hYCXD;
	}
	public void setHYCXD(Date hYCXD) {
		this.hYCXD = hYCXD;
	}
	
	
		
	
	public Date getHYLRD() {
		return hYLRD;
	}
	public void setHYLRD(Date hYLRD) {
		this.hYLRD = hYLRD;
	}
	
	
		
	
	public String getHYFRQ() {
		return hYFRQ;
	}
	public void setHYFRQ(String hYFRQ) {
		this.hYFRQ = hYFRQ;
	}
	

	public String getLovDescHYFRQName() {
		return this.lovDescHYFRQName;
	}

	public void setLovDescHYFRQName (String lovDescHYFRQName) {
		this.lovDescHYFRQName = lovDescHYFRQName;
	}
	
		
	
	public Date getHYNRD() {
		return hYNRD;
	}
	public void setHYNRD(Date hYNRD) {
		this.hYNRD = hYNRD;
	}
	
	
		
	
	public BigDecimal getHYNOU() {
		return hYNOU;
	}
	public void setHYNOU(BigDecimal hYNOU) {
		this.hYNOU = hYNOU;
	}
	
	
		
	
	public BigDecimal getHYUNP() {
		return hYUNP;
	}
	public void setHYUNP(BigDecimal hYUNP) {
		this.hYUNP = hYUNP;
	}
	
	
		
	
	public String getHYCCY() {
		return hYCCY;
	}
	public void setHYCCY(String hYCCY) {
		this.hYCCY = hYCCY;
	}
	

	public String getLovDescHYCCYName() {
		return this.lovDescHYCCYName;
	}

	public void setLovDescHYCCYName (String lovDescHYCCYName) {
		this.lovDescHYCCYName = lovDescHYCCYName;
	}
	
		
	
	public BigDecimal getHYCLV() {
		return hYCLV;
	}
	public void setHYCLV(BigDecimal hYCLV) {
		this.hYCLV = hYCLV;
	}
	
	
		
	
	public BigDecimal getHYSVM() {
		return hYSVM;
	}
	public void setHYSVM(BigDecimal hYSVM) {
		this.hYSVM = hYSVM;
	}
	
	
		
	
	public BigDecimal getHYMCV() {
		return hYMCV;
	}
	public void setHYMCV(BigDecimal hYMCV) {
		this.hYMCV = hYMCV;
	}
	
	
		
	
	public BigDecimal getHYBKV() {
		return hYBKV;
	}
	public void setHYBKV(BigDecimal hYBKV) {
		this.hYBKV = hYBKV;
	}
	
	
		
	
	public BigDecimal getHYTOTA() {
		return hYTOTA;
	}
	public void setHYTOTA(BigDecimal hYTOTA) {
		this.hYTOTA = hYTOTA;
	}
	
	
		
	
	public BigDecimal getHYISV() {
		return hYISV;
	}
	public void setHYISV(BigDecimal hYISV) {
		this.hYISV = hYISV;
	}
	
	
		
	
	public Date getHYIXD() {
		return hYIXD;
	}
	public void setHYIXD(Date hYIXD) {
		this.hYIXD = hYIXD;
	}
	
	
		
	
	public String getHYNR1() {
		return hYNR1;
	}
	public void setHYNR1(String hYNR1) {
		this.hYNR1 = hYNR1;
	}
	
	
		
	
	public String getHYNR2() {
		return hYNR2;
	}
	public void setHYNR2(String hYNR2) {
		this.hYNR2 = hYNR2;
	}
	
	
		
	
	public String getHYNR3() {
		return hYNR3;
	}
	public void setHYNR3(String hYNR3) {
		this.hYNR3 = hYNR3;
	}
	
	
		
	
	public String getHYNR4() {
		return hYNR4;
	}
	public void setHYNR4(String hYNR4) {
		this.hYNR4 = hYNR4;
	}
	
	
		
	
	public Date getHYDLM() {
		return hYDLM;
	}
	public void setHYDLM(Date hYDLM) {
		this.hYDLM = hYDLM;
	}
	
	
		
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public long getLastMntBy() {
		return lastMntBy;
	}
	
	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public Collateralitem getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(Collateralitem beforeImage){
		this.befImage=beforeImage;
	}

	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	public String getRecordStatus() {
		return recordStatus;
	}
	
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	public String getRoleCode() {
		return roleCode;
	}
	
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	public String getNextRoleCode() {
		return nextRoleCode;
	}
	
	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}
	
	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}
	
	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}
	
	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	// Overidden Equals method to handle the comparision
	public boolean equals(Collateralitem collateralitem) {
		return getId() == collateralitem.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Collateralitem) {
			Collateralitem collateralitem = (Collateralitem) obj;
			return equals(collateralitem);
		}
		return false;
	}
}
