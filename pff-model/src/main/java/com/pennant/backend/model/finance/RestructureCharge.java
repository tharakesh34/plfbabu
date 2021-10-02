package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class RestructureCharge extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -5722811453434523809L;

	private long id;
	private long restructureId;
	private int chargeSeq;
	@XmlElement
	private String alocType;
	private String alocTypeDesc;
	@XmlElement
	private boolean capitalized = false;
	@XmlElement
	private String feeCode;
	@XmlElement
	private BigDecimal actualAmount = BigDecimal.ZERO;
	private String taxType = "";
	private BigDecimal cgst = BigDecimal.ZERO;
	private BigDecimal sgst = BigDecimal.ZERO;
	private BigDecimal ugst = BigDecimal.ZERO;
	private BigDecimal igst = BigDecimal.ZERO;
	private BigDecimal cess = BigDecimal.ZERO;
	private BigDecimal tdsAmount = BigDecimal.ZERO;
	private BigDecimal totalAmount = BigDecimal.ZERO;
	private RestructureCharge befImage;
	private LoggedInUser userDetails;

	public RestructureCharge() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<>();
		excludeFields.add("alocTypeDesc");

		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRestructureId() {
		return restructureId;
	}

	public void setRestructureId(long restructureId) {
		this.restructureId = restructureId;
	}

	public int getChargeSeq() {
		return chargeSeq;
	}

	public void setChargeSeq(int chargeSeq) {
		this.chargeSeq = chargeSeq;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public BigDecimal getCgst() {
		return cgst;
	}

	public void setCgst(BigDecimal cgst) {
		this.cgst = cgst;
	}

	public BigDecimal getSgst() {
		return sgst;
	}

	public void setSgst(BigDecimal sgst) {
		this.sgst = sgst;
	}

	public BigDecimal getUgst() {
		return ugst;
	}

	public void setUgst(BigDecimal ugst) {
		this.ugst = ugst;
	}

	public BigDecimal getIgst() {
		return igst;
	}

	public void setIgst(BigDecimal igst) {
		this.igst = igst;
	}

	public BigDecimal getCess() {
		return cess;
	}

	public void setCess(BigDecimal cess) {
		this.cess = cess;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getTdsAmount() {
		return tdsAmount;
	}

	public void setTdsAmount(BigDecimal tdsAmount) {
		this.tdsAmount = tdsAmount;
	}

	public boolean isCapitalized() {
		return capitalized;
	}

	public void setCapitalized(boolean capitalized) {
		this.capitalized = capitalized;
	}

	public String getFeeCode() {
		return feeCode;
	}

	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}

	public String getAlocType() {
		return alocType;
	}

	public void setAlocType(String alocType) {
		this.alocType = alocType;
	}

	public String getAlocTypeDesc() {
		return alocTypeDesc;
	}

	public void setAlocTypeDesc(String alocTypeDesc) {
		this.alocTypeDesc = alocTypeDesc;
	}

	public RestructureCharge getBefImage() {
		return befImage;
	}

	public void setBefImage(RestructureCharge befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

}
