package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class FinanceWriteoff implements Serializable {
	
    private static final long serialVersionUID = -1477748770396649402L;
    
    private String finReference;
    private int seqNo = 0;
	private BigDecimal writtenoffPri = BigDecimal.ZERO;
	private BigDecimal writtenoffPft = BigDecimal.ZERO;
	private BigDecimal curODPri = BigDecimal.ZERO;
	private BigDecimal curODPft = BigDecimal.ZERO;
	private BigDecimal unPaidSchdPri = BigDecimal.ZERO;
	private BigDecimal unPaidSchdPft = BigDecimal.ZERO;
	private BigDecimal penaltyAmount = BigDecimal.ZERO;
	private BigDecimal provisionedAmount = BigDecimal.ZERO;
	
	private Date writeoffDate;
	private BigDecimal writeoffPrincipal = BigDecimal.ZERO;
	private BigDecimal writeoffProfit = BigDecimal.ZERO;
	private BigDecimal adjAmount = BigDecimal.ZERO;
	private String remarks;
	private long linkedTranId = 0;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public String getFinReference() {
    	return finReference;
    }
	public void setFinReference(String finReference) {
    	this.finReference = finReference;
    }
	
	public BigDecimal getWrittenoffPri() {
    	return writtenoffPri;
    }
	public void setWrittenoffPri(BigDecimal writtenoffPri) {
    	this.writtenoffPri = writtenoffPri;
    }
	
	public BigDecimal getWrittenoffPft() {
    	return writtenoffPft;
    }
	public void setWrittenoffPft(BigDecimal writtenoffPft) {
    	this.writtenoffPft = writtenoffPft;
    }
	
	public BigDecimal getCurODPri() {
    	return curODPri;
    }
	public void setCurODPri(BigDecimal curODPri) {
    	this.curODPri = curODPri;
    }
	
	public BigDecimal getCurODPft() {
    	return curODPft;
    }
	public void setCurODPft(BigDecimal curODPft) {
    	this.curODPft = curODPft;
    }
	
	public BigDecimal getUnPaidSchdPri() {
    	return unPaidSchdPri;
    }
	public void setUnPaidSchdPri(BigDecimal unPaidSchdPri) {
    	this.unPaidSchdPri = unPaidSchdPri;
    }
	
	public BigDecimal getUnPaidSchdPft() {
    	return unPaidSchdPft;
    }
	public void setUnPaidSchdPft(BigDecimal unPaidSchdPft) {
    	this.unPaidSchdPft = unPaidSchdPft;
    }
	
	public void setPenaltyAmount(BigDecimal penaltyAmt) {
	    this.penaltyAmount = penaltyAmt;
    }
	public BigDecimal getPenaltyAmount() {
	    return penaltyAmount;
    }
	
	public BigDecimal getWriteoffPrincipal() {
    	return writeoffPrincipal;
    }
	public void setWriteoffPrincipal(BigDecimal writeoffPrincipal) {
    	this.writeoffPrincipal = writeoffPrincipal;
    }
	
	public BigDecimal getWriteoffProfit() {
    	return writeoffProfit;
    }
	public void setWriteoffProfit(BigDecimal writeoffProfit) {
    	this.writeoffProfit = writeoffProfit;
    }
	
	public void setAdjAmount(BigDecimal adjAmount) {
	    this.adjAmount = adjAmount;
    }
	public BigDecimal getAdjAmount() {
	    return adjAmount;
    }
	
	public void setWriteoffDate(Date writeoffDate) {
	    this.writeoffDate = writeoffDate;
    }
	public Date getWriteoffDate() {
	    return writeoffDate;
    }
	
	public void setRemarks(String remarks) {
	    this.remarks = remarks;
    }
	public String getRemarks() {
	    return remarks;
    }
	
	public BigDecimal getProvisionedAmount() {
	    return provisionedAmount;
    }
	public void setProvisionedAmount(BigDecimal provisionedAmount) {
	    this.provisionedAmount = provisionedAmount;
    }
	
	public long getLinkedTranId() {
	    return linkedTranId;
    }
	public void setLinkedTranId(long linkedTranId) {
	    this.linkedTranId = linkedTranId;
    }
	
	public int getSeqNo() {
	    return seqNo;
    }
	public void setSeqNo(int seqNo) {
	    this.seqNo = seqNo;
    }
	
}
