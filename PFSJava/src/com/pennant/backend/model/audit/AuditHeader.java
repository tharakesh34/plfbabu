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
 * FileName    		:  AuditHeader.java														*                           
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.Entity;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.PennantConstants;

public class AuditHeader implements java.io.Serializable,Entity {

	private static final long serialVersionUID = -53442181146054373L;

	private long auditId = Long.MIN_VALUE;
	private Timestamp auditDate;
	private long auditUsrId ; 
	private String auditModule;
	private String auditBranchCode;
	private String auditDeptCode;
	private String auditTranType;
	private String auditCustNo;
	private String auditAccNo;
	private String auditLoanNo;
	private String auditReference;
	private String auditSystemIP;
	private String auditSessionID;
	private boolean overide;
	private String  auditInfo;
	private String auditOveride;
	private String auditError;
	private boolean auditPrinted=false;
	private boolean auditRecovered=false;
	private String auditErrorForRecocvery;
	private ArrayList<ErrorDetails> infoMessage;
	private ArrayList<ErrorDetails> overideMessage;
	private ArrayList<ErrorDetails> errorMessage;
	private Object modelData;
	private AuditDetail auditDetail;
	private List<AuditDetail> auditDetails;
	private int overideCount=0;
	private String usrLanguage;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	private int processStatus=0;
	private boolean nextProcess=false;
	
	public AuditHeader(){
		super();	
	}
	
	public long getId() {
		return auditId;
	}
	
	public boolean isNew() {
		return (getId() == Long.MIN_VALUE);
	}
	
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
	public long getAuditUsrId() {
		return auditUsrId;
	}

	public void setAuditUsrId(long auditUsrId) {
		this.auditUsrId = auditUsrId;
	}

	public String getAuditModule() {
		return auditModule;
	}
	public void setAuditModule(String auditModule) {
		this.auditModule = auditModule;
	}

	public String getAuditBranchCode() {
		return auditBranchCode;
	}
	public void setAuditBranchCode(String auditBranchCode) {
		this.auditBranchCode = auditBranchCode;
	}
	public String getAuditDeptCode() {
		return auditDeptCode;
	}
	public void setAuditDeptCode(String auditDeptCode) {
		this.auditDeptCode = auditDeptCode;
	}
	public String getAuditTranType() {
		return auditTranType;
	}
	public void setAuditTranType(String auditTranType) {
		this.auditTranType = auditTranType;
	}
	public String getAuditCustNo() {
		return auditCustNo;
	}
	public void setAuditCustNo(String auditCustNo) {
		this.auditCustNo = auditCustNo;
	}
	public String getAuditAccNo() {
		return auditAccNo;
	}
	public void setAuditAccNo(String auditAccNo) {
		this.auditAccNo = auditAccNo;
	}
	public String getAuditLoanNo() {
		return auditLoanNo;
	}
	public void setAuditLoanNo(String auditLoanNo) {
		this.auditLoanNo = auditLoanNo;
	}	
	public String getAuditSystemIP() {
		return auditSystemIP;
	}
	public void setAuditSystemIP(String auditSystemIP) {
		this.auditSystemIP = auditSystemIP;
	}
	public String getAuditReference() {
		return auditReference;
	}
	public void setAuditReference(String auditReference) {
		this.auditReference = auditReference;
	}
	public String getAuditSessionID() {
		return auditSessionID;
	}
	public void setAuditSessionID(String auditSessionID) {
		this.auditSessionID = auditSessionID;
	}
	public boolean isAuditPrinted() {
		return auditPrinted;
	}
	public void setAuditPrinted(boolean auditPrinted) {
		this.auditPrinted = auditPrinted;
	}
	public boolean isAuditRecovered() {
		return auditRecovered;
	}
	public void setAuditRecovered(boolean auditRecovered) {
		this.auditRecovered = auditRecovered;
	}
	public String getAuditErrorForRecocvery() {
		return auditErrorForRecocvery;
	}
	public void setAuditErrorForRecocvery(String auditErrorForRecocvery) {
		this.auditErrorForRecocvery = auditErrorForRecocvery;
	}
	public Object getModelData() {
		return modelData;
	}
	public void setModelData(Object modelData) {
		this.modelData = modelData;
	}
	
	public boolean isOveride() {
		return overide;
	}
	public void setOveride(boolean overide) {
		this.overide = overide;
	}
	
	public String getAuditInfo() {
		return auditInfo;
	}
	public void setAuditInfo(String auditInfo) {
		this.auditInfo = auditInfo;
	}

	public String getAuditOveride() {
		return auditOveride;
	}
	
	public void setAuditOveride(String auditOveride) {
		this.auditOveride = auditOveride;
	}
	
	public String getAuditError() {
		return auditError;
	}
	public void setAuditError(String auditError) {
		this.auditError = auditError;
	}
		
	public ArrayList<ErrorDetails> getInfoMessage() {
		return infoMessage;
	}
	
	public void setInfoMessage(ArrayList<ErrorDetails> infoMessage) {
		if (infoMessage!=null && infoMessage.size()>0){
			for (int i = 0; i < infoMessage.size(); i++) {
				setInfoMessage((ErrorDetails)infoMessage.get(i));
			}
		}else{
			this.auditInfo=null;
		}	
	}
	
	private void setInfoMessage(ErrorDetails infoMessage) {
		if (infoMessage!=null){
			
			if (this.infoMessage==null){
				this.auditInfo=null;
				this.infoMessage= new ArrayList<ErrorDetails>();
				this.infoMessage.add(infoMessage);
			}else{
				this.infoMessage.add(infoMessage);
			}
			
			if (auditInfo==null){
				auditInfo=infoMessage.getError();
			}else{
				auditInfo.concat("\n");
				auditInfo.concat(infoMessage.getError());
			}
		}
	}
	
	public ArrayList<ErrorDetails> getOverideMessage() {

		return overideMessage;
	}
	
	public void setOverideMessage(ArrayList<ErrorDetails> overideMessage) {
		if (overideMessage!=null && overideMessage.size()>0){
			for (int i = 0; i < overideMessage.size(); i++) {
				setOverideMessage((ErrorDetails)overideMessage.get(i));
			}
		}else{
			this.auditOveride=null;
		}	
	}

	private void setOverideMessage(ErrorDetails overideMessage) {
		if (overideMessage!=null){
			
			if (this.overideMessage==null){
				this.auditOveride=null;
				this.overideMessage= new ArrayList<ErrorDetails>();
				this.overideMessage.add(overideMessage);
			}else{
				this.overideMessage.add(overideMessage);
			}
			
			if (auditOveride==null){
				auditOveride=overideMessage.getError();
			}else{
				auditOveride.concat("\n");
				auditOveride.concat(overideMessage.getError());
			}
			checkOveride(overideMessage);
		}
	}
	
	
	public ArrayList<ErrorDetails> getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(ArrayList<ErrorDetails> errorMessage) {
		if (errorMessage!=null && errorMessage.size()>0){
			for (int i = 0; i < overideMessage.size(); i++) {
				setErrorMessage((ErrorDetails)errorMessage.get(i));
			}
		}else{
			this.auditError=null;
		}
	}
	
	private void setErrorMessage(ErrorDetails errorMessage) {
		if (errorMessage!=null){
			
			if (this.errorMessage==null){
				this.auditError=null;
				this.errorMessage= new ArrayList<ErrorDetails>();
				this.errorMessage.add(errorMessage);
			}else{
				this.errorMessage.add(errorMessage);
			}
			
			if (auditError==null){
				auditError=errorMessage.getError();
			}else{
				auditError.concat("\n");
				auditError.concat(errorMessage.getError());
			}
		}
	}
	
	
	public void setErrorList(ArrayList<ErrorDetails> errorDetails) {
		
		if(errorDetails!=null && errorDetails.size()!=0){
			for (int i = 0; i < errorDetails.size(); i++) {
				setErrorDetails(errorDetails.get(i));
			}
		}
	}
	
	
	public void setErrorDetails(ErrorDetails errorDetails) {

		if (errorDetails!=null){

			if (StringUtils.trimToEmpty(errorDetails.getErrorSeverity()).equalsIgnoreCase(PennantConstants.ERR_SEV_INFO)){
				setInfoMessage(errorDetails);
			}else if (StringUtils.trimToEmpty(errorDetails.getErrorSeverity()).equalsIgnoreCase(PennantConstants.ERR_SEV_WARNING)){
				setOverideMessage(errorDetails);
			} else if (StringUtils.trimToEmpty(errorDetails.getErrorSeverity()).equalsIgnoreCase(PennantConstants.ERR_SEV_ERROR)){
				setErrorMessage(errorDetails);
			}
		}
	}
	
	@Deprecated
	public AuditHeader(String reference,String custNo,String accNo,String loanNo,AuditDetail auditDetail, LoginUserDetails userDetails){
		
		this.auditReference=reference;
		this.auditCustNo=custNo;
		this.auditAccNo=accNo;
		this.auditLoanNo=loanNo;
		
		if (userDetails!=null){
			this.auditUsrId =  userDetails.getLoginUsrID();
			this.auditBranchCode = userDetails.getLoginBranchCode();
			this.auditDeptCode =userDetails.getLoginDeptCode();
			this.auditSystemIP = userDetails.getLoginIP();
			this.auditSessionID = userDetails.getLoginSessionID();
			this.usrLanguage = userDetails.getUsrLanguage();
		}
		
		this.auditTranType = auditDetail.getAuditTranType();
		this.auditModule = auditDetail.getModelData().getClass().getSimpleName();
		this.auditDetail= auditDetail;
		
	}

	public AuditHeader(String reference,String custNo,String accNo,String loanNo,AuditDetail auditDetail, LoginUserDetails userDetails,HashMap<String, ArrayList<ErrorDetails>> overideMap){
		
		this.auditReference=reference;
		this.auditCustNo=custNo;
		this.auditAccNo=accNo;
		this.auditLoanNo=loanNo;
		
		if (userDetails!=null){
			this.auditUsrId =  userDetails.getLoginUsrID();
			this.auditBranchCode = userDetails.getLoginBranchCode();
			this.auditDeptCode =userDetails.getLoginDeptCode();
			this.auditSystemIP = userDetails.getLoginIP();
			this.auditSessionID = userDetails.getLoginSessionID();
			this.usrLanguage = userDetails.getUsrLanguage();
		}
		
		this.auditTranType = auditDetail.getAuditTranType();
		this.auditModule = auditDetail.getModelData().getClass().getSimpleName();
		this.auditDetail= auditDetail;
		this.overideMap=overideMap;
	}

	@Deprecated
	public AuditHeader(String tranType, String reference,String custNo,String accNo,String loanNo,Object objData){
		
		this.auditTranType=tranType;
		this.auditReference=reference;
		this.auditCustNo=custNo;
		this.auditAccNo=accNo;
		this.auditLoanNo=loanNo;
		LoginUserDetails userDetails = null;
		
		try {
			if (objData.getClass().getMethod( "getUserDetails")!=null){
				userDetails = (LoginUserDetails) objData.getClass().getMethod( "getUserDetails").invoke(objData);
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		if (userDetails!=null){
			this.auditUsrId =  userDetails.getLoginUsrID();
			this.auditBranchCode = userDetails.getLoginBranchCode();
			this.auditDeptCode =userDetails.getLoginDeptCode();
			this.auditSystemIP = userDetails.getLoginIP();
			this.auditSessionID = userDetails.getLoginSessionID();
			this.usrLanguage = userDetails.getUsrLanguage();
		}
		
		this.auditModule = objData.getClass().getSimpleName();
		this.modelData= objData;
		
	}

	
	public int getOverideCount() {
		return overideCount;
	}
	public void setOverideCount(int overideCount) {
		this.overideCount = overideCount;
	}
	
	public String getUsrLanguage() {
		return usrLanguage;
	}

	public void setUsrLanguage(String usrLanguage) {
		this.usrLanguage = usrLanguage;
	}
	public List<AuditDetail> getAuditDetails() {
		return auditDetails;
	}
	public void setAuditDetails(List<AuditDetail> auditDetails) {
		this.auditDetails = auditDetails;
	}

	
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
	
	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	
	public void addAuditDetail(AuditDetail auditDetail) {
		if(auditDetail!=null){
			if(this.auditDetails==null){
				this.auditDetails = new ArrayList<AuditDetail>();
			}
			this.auditDetails.add(auditDetail);
		}
		
	}
	
	public AuditDetail getAuditDetail() {
		return auditDetail;
	}
	public void setAuditDetail(AuditDetail auditDetail) {
		this.auditDetail = auditDetail;
	}

	public int getProcessStatus() {
		return processStatus;
	}
	public void setProcessStatus(int processStatus) {
		this.processStatus = processStatus;
	}
	public boolean isNextProcess() {
		return nextProcess;
	}
	public void setNextProcess(boolean nextProcess) {
		this.nextProcess = nextProcess;
	}

	
	private void checkOveride(ErrorDetails overideMessage){
		
		if(overideMap!=null){
			
			if(overideMap.containsKey(overideMessage.getErrorField())){
				ArrayList<ErrorDetails> overDetails =this.overideMap.get(overideMessage.getErrorField());
				
				for (int i = 0; i < overDetails.size(); i++) {
					if(!isEqual(overideMessage, overDetails.get(i))){
						this.overideMap.remove(overideMessage.getErrorField());
						break;
					}
				}
			}
		}
	}
	
	
	private boolean isEqual(ErrorDetails oldDetails,ErrorDetails newDetails){
		boolean isSame=true;
		if(StringUtils.trimToEmpty(oldDetails.getErrorCode()).equals(StringUtils.trimToEmpty(newDetails.getErrorCode()))){
			if(oldDetails.getErrorFieldValues()==null && newDetails.getErrorFieldValues()==null){
				isSame=true;
			}else  if(oldDetails.getErrorFieldValues()==null || newDetails.getErrorFieldValues()==null){
				isSame=false;
			}else{
				for (int i = 0; i < oldDetails.getErrorFieldValues().length; i++) {
					if(!oldDetails.getErrorFieldValues()[i].equals(newDetails.getErrorFieldValues()[i])){
						isSame=false;
						break;
					}
				}
			}
		}
		return isSame;
	}
}