
/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : InterfaceServiceLog.java * * Author : PENNANT TECHONOLOGIES * * * Modified Date : 22-10-2019 * *
 * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-08-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.externalinterface;

import java.sql.Timestamp;
import java.util.Date;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

public class InterfaceServiceLog extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1221821037156917579L;
	private long seqId = Long.MIN_VALUE;
	private String reference;
	private String serviceName;
	private String endPoint;
	private String request;
	private String response;
	private Date reqSentOn;
	private Date respReceivedOn;
	private String status;
	private String errorCode;
	private String errorDesc;

	private String ref_num;
	private String interface_Name;
	private String records_Processed;
	private Timestamp start_Date;
	private String status_Desc;
	private String interface_Info;
	private Date end_Date;
	private Date eodDate;
	private InterfaceServiceLog befImage;

	public InterfaceServiceLog() {
		super();
	}

	public InterfaceServiceLog(String id) {
		super();
		this.setId(id);
	}

	private void setId(String id) {
		this.reference = id;

	}

	public long getSeqId() {
		return seqId;
	}

	public void setSeqId(long seqId) {
		this.seqId = seqId;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public InterfaceServiceLog getBefImage() {
		return befImage;
	}

	public void setBefImage(InterfaceServiceLog befImage) {
		this.befImage = befImage;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public Date getEnd_Date() {
		return end_Date;
	}

	public void setEnd_Date(Date end_Date) {
		this.end_Date = end_Date;
	}

	public Date getReqSentOn() {
		return reqSentOn;
	}

	public void setReqSentOn(Date reqSentOn) {
		this.reqSentOn = reqSentOn;
	}

	public Date getRespReceivedOn() {
		return respReceivedOn;
	}

	public void setRespReceivedOn(Date respReceivedOn) {
		this.respReceivedOn = respReceivedOn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getRef_num() {
		return ref_num;
	}

	public void setRef_num(String ref_num) {
		this.ref_num = ref_num;
	}

	public String getInterface_Name() {
		return interface_Name;
	}

	public void setInterface_Name(String interface_Name) {
		this.interface_Name = interface_Name;
	}

	public String getRecords_Processed() {
		return records_Processed;
	}

	public void setRecords_Processed(String records_Processed) {
		this.records_Processed = records_Processed;
	}

	public Timestamp getStart_Date() {
		return start_Date;
	}

	public void setStart_Date(Timestamp start_Date) {
		this.start_Date = start_Date;
	}

	public String getStatus_Desc() {
		return status_Desc;
	}

	public void setStatus_Desc(String status_Desc) {
		this.status_Desc = status_Desc;
	}

	public String getInterface_Info() {
		return interface_Info;
	}

	public void setInterface_Info(String interface_Info) {
		this.interface_Info = interface_Info;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getEodDate() {
		return eodDate;
	}

	public void setEodDate(Date eodDate) {
		this.eodDate = eodDate;
	}

}
