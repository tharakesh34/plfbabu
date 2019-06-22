package com.pennant.backend.model.receiptupload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.ErrorDetail;

@XmlType(propOrder = { "allocationType", "referenceCode", "paidAmount", "waivedAmount" })

@XmlAccessorType(XmlAccessType.NONE)
public class UploadAlloctionDetail extends AbstractWorkflowEntity implements Entity {

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
	@XmlElement
	private BigDecimal waivedAmount = BigDecimal.ZERO;
	private List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>(1);

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

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setId(long id) {
		// TODO Auto-generated method stub

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

}