package com.pennant.backend.model.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FinanceWriteoff implements Serializable {

	private static final long serialVersionUID = -1477748770396649402L;

	private long finID;
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

	private BigDecimal writtenoffSchFee = BigDecimal.ZERO;

	private BigDecimal unpaidSchFee = BigDecimal.ZERO;

	private Date writeoffDate;
	private BigDecimal writeoffPrincipal = BigDecimal.ZERO;
	private BigDecimal writeoffProfit = BigDecimal.ZERO;
	private BigDecimal writeoffSchFee = BigDecimal.ZERO;
	private BigDecimal adjAmount = BigDecimal.ZERO;
	private String remarks;
	private long linkedTranId = 0;
	private Long receiptID;

	public FinanceWriteoff() {
		super();
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

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

	public BigDecimal getWrittenoffSchFee() {
		return writtenoffSchFee;
	}

	public void setWrittenoffSchFee(BigDecimal writtenoffSchFee) {
		this.writtenoffSchFee = writtenoffSchFee;
	}

	public BigDecimal getWriteoffSchFee() {
		return writeoffSchFee;
	}

	public void setWriteoffSchFee(BigDecimal writeoffSchFee) {
		this.writeoffSchFee = writeoffSchFee;
	}

	public BigDecimal getUnpaidSchFee() {
		return unpaidSchFee;
	}

	public void setUnpaidSchFee(BigDecimal unpaidSchFee) {
		this.unpaidSchFee = unpaidSchFee;
	}

	public Long getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(Long receiptID) {
		this.receiptID = receiptID;
	}

	public Map<String, Object> getDeclaredFieldValues() {
		Map<String, Object> map = new HashMap<>();
		getDeclaredFieldValues(map);

		return map;
	}

	public Map<String, Object> getDeclaredFieldValues(Map<String, Object> map) {
		for (int i = 0; i < this.getClass().getDeclaredFields().length; i++) {
			try {
				// "ae_" Should be in small case only, if we want to change the case we need to update the configuration
				// fields as well.
				map.put("fw_" + this.getClass().getDeclaredFields()[i].getName(),
						this.getClass().getDeclaredFields()[i].get(this));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// Nothing TO DO
			}
		}

		return map;
	}

}
