package com.pennant.backend.model.financemanagement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

@XmlType(propOrder = { "vasProduct", "mandatory" })
@XmlAccessorType(XmlAccessType.NONE)
public class FinTypeVASProducts extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 1L;

	private String finType;
	@XmlElement
	private String vasProduct;
	private String vasProductDesc;
	@XmlElement
	private boolean mandatory;
	private String mandatoryDesc;
	private boolean newRecord = false;
	private FinTypeVASProducts befImage;
	private LoggedInUser userDetails;
	private String productType;
	private String productCtg;
	private String recAgainst;
	private String productCtgDesc;
	private String manufacturerDesc;
	private BigDecimal vasFee;
	private String feePaymentMode;
	private String vasReference;
	private HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("vasProductDesc");
		excludeFields.add("mandatoryDesc");
		excludeFields.add("productType");
		excludeFields.add("productCtg");
		excludeFields.add("productCtgDesc");
		excludeFields.add("manufacturerDesc");
		excludeFields.add("recAgainst");
		excludeFields.add("vasFee");
		excludeFields.add("feePaymentMode");
		excludeFields.add("vasReference");
		return excludeFields;
	}

	public FinTypeVASProducts copyEntity() {
		FinTypeVASProducts entity = new FinTypeVASProducts();
		entity.setFinType(this.finType);
		entity.setVasProduct(this.vasProduct);
		entity.setVasProductDesc(this.vasProductDesc);
		entity.setMandatory(this.mandatory);
		entity.setMandatoryDesc(this.mandatoryDesc);
		entity.setNewRecord(this.newRecord);
		entity.setBefImage(this.befImage == null ? null : this.befImage.copyEntity());
		entity.setUserDetails(this.userDetails);
		entity.setProductType(this.productType);
		entity.setProductCtg(this.productCtg);
		entity.setRecAgainst(this.recAgainst);
		entity.setProductCtgDesc(this.productCtgDesc);
		entity.setManufacturerDesc(this.manufacturerDesc);
		entity.setVasFee(this.vasFee);
		this.auditDetailMap.entrySet().stream().forEach(e -> {
			List<AuditDetail> newList = new ArrayList<AuditDetail>();
			if (e.getValue() != null) {
				e.getValue().forEach(
						auditDetail -> newList.add(auditDetail == null ? null : auditDetail.getNewCopyInstance()));
				entity.getAuditDetailMap().put(e.getKey(), newList);
			} else
				entity.getAuditDetailMap().put(e.getKey(), null);
		});

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

	public boolean isNew() {
		return isNewRecord();
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;
	}

	public String getVasProduct() {
		return vasProduct;
	}

	public void setVasProduct(String vasProduct) {
		this.vasProduct = vasProduct;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public FinTypeVASProducts getBefImage() {
		return befImage;
	}

	public void setBefImage(FinTypeVASProducts befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getVasProductDesc() {
		return vasProductDesc;
	}

	public void setVasProductDesc(String vasProductDesc) {
		this.vasProductDesc = vasProductDesc;
	}

	public String getMandatoryDesc() {
		return mandatoryDesc;
	}

	public void setMandatoryDesc(String mandatoryDesc) {
		this.mandatoryDesc = mandatoryDesc;
	}

	public HashMap<String, List<AuditDetail>> getAuditDetailMap() {
		return auditDetailMap;
	}

	public void setAuditDetailMap(HashMap<String, List<AuditDetail>> auditDetailMap) {
		this.auditDetailMap = auditDetailMap;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProductCtg() {
		return productCtg;
	}

	public void setProductCtg(String productCtg) {
		this.productCtg = productCtg;
	}

	public String getProductCtgDesc() {
		return productCtgDesc;
	}

	public void setProductCtgDesc(String productCtgDesc) {
		this.productCtgDesc = productCtgDesc;
	}

	public String getManufacturerDesc() {
		return manufacturerDesc;
	}

	public void setManufacturerDesc(String manufacturerDesc) {
		this.manufacturerDesc = manufacturerDesc;
	}

	public String getRecAgainst() {
		return recAgainst;
	}

	public void setRecAgainst(String recAgainst) {
		this.recAgainst = recAgainst;
	}

	public BigDecimal getVasFee() {
		return vasFee;
	}

	public void setVasFee(BigDecimal vasFee) {
		this.vasFee = vasFee;
	}

	public String getFeePaymentMode() {
		return feePaymentMode;
	}

	public void setFeePaymentMode(String feePaymentMode) {
		this.feePaymentMode = feePaymentMode;
	}

	public String getVasReference() {
		return vasReference;
	}

	public void setVasReference(String vasReference) {
		this.vasReference = vasReference;
	}

}
