package com.pennant.backend.model.receiptupload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.ErrorDetail;

@XmlType(propOrder = { "allocationType", "referenceCode", "paidAmount", "waivedAmount" })

@XmlAccessorType(XmlAccessType.NONE)
public class UploadAlloctionDetail extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -4601315178356280082L;

	private long UploadDetailId = Long.MIN_VALUE;
	private long uploadAlloctionDetailId = Long.MIN_VALUE;

	private String rootId;
	@XmlElement(name = "allocationItem")
	private String allocationType;
	@XmlElement
	private String referenceCode;
	@XmlElement
	private BigDecimal paidAmount = BigDecimal.ZERO;
	private String strPaidAmount;
	@XmlElement
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private String strWaivedAmount;
	private List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>(1);

	public UploadAlloctionDetail copyEntity() {
		UploadAlloctionDetail entity = new UploadAlloctionDetail();
		entity.setUploadDetailId(this.UploadDetailId);
		entity.setUploadAlloctionDetailId(this.uploadAlloctionDetailId);
		entity.setRootId(this.rootId);
		entity.setAllocationType(this.allocationType);
		entity.setReferenceCode(this.referenceCode);
		entity.setPaidAmount(this.paidAmount);
		entity.setStrPaidAmount(this.strPaidAmount);
		entity.setWaivedAmount(this.waivedAmount);
		entity.setStrWaivedAmount(this.strWaivedAmount);
		this.errorDetails.stream().forEach(e -> entity.getErrorDetails().add(e));
		entity.setRecordStatus(super.getRecordStatus());
		entity.setRoleCode(super.getRoleCode());
		entity.setNextRoleCode(super.getNextRoleCode());
		entity.setTaskId(super.getTaskId());
		entity.setNextTaskId(super.getNextTaskId());
		entity.setRecordType(super.getRecordType());
		entity.setWorkflowId(super.getWorkflowId());
		entity.setUserAction(super.getUserAction());
		entity.setVersion(super.getVersion());
		entity.setLastMntBy(super.getLastMntBy());
		entity.setLastMntOn(super.getLastMntOn());
		return entity;
	}

	// Getter and Setter

	public String getAllocationType() {
		return allocationType;
	}

	public void setAllocationType(String allocationType) {
		this.allocationType = allocationType;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getWaivedAmount() {
		return waivedAmount;
	}

	public void setWaivedAmount(BigDecimal waivedAmount) {
		this.waivedAmount = waivedAmount;
	}

	public long getUploadAlloctionDetailId() {
		return uploadAlloctionDetailId;
	}

	public void setUploadAlloctionDetailId(long uploadAlloctionDetailId) {
		this.uploadAlloctionDetailId = uploadAlloctionDetailId;
	}

	public long getUploadDetailId() {
		return UploadDetailId;
	}

	public void setUploadDetailId(long uploadDetailId) {
		UploadDetailId = uploadDetailId;
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public List<ErrorDetail> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(List<ErrorDetail> errorDetails) {
		this.errorDetails = errorDetails;
	}

	public void setErrorDetail(ErrorDetail errorDetail) {

		if (errorDetail != null) {
			if (errorDetails == null) {
				errorDetails = new ArrayList<ErrorDetail>();
			}
			this.errorDetails.add(errorDetail);
		}
	}

	public String getStrPaidAmount() {
		return strPaidAmount;
	}

	public void setStrPaidAmount(String strPaidAmount) {
		this.strPaidAmount = strPaidAmount;
	}

	public String getStrWaivedAmount() {
		return strWaivedAmount;
	}

	public void setStrWaivedAmount(String strWaivedAmount) {
		this.strWaivedAmount = strWaivedAmount;
	}

}