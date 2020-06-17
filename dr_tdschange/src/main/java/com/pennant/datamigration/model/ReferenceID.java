package com.pennant.datamigration.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.rmtmasters.FinanceType;

public class ReferenceID implements Serializable
{
    private static final long serialVersionUID = 1183720618731771888L;
    private List<FinanceType> finTypes;
    private long manualAdviseID;
    private long excessID;
    private long excessMovementID;
    private long presentmentID;
    private long repayID;
    private Date appDate;
    private List<FeeTypeVsGLMapping> feeVsGLList;
    private long linkedTranID;
    private int tranOrder;
    private String account;
    private String accountType;
    private BigDecimal postAmount;
    private String tranDesc;
    private String drOrcr;
    private long madMovementID;
    private BigDecimal odcReceived;
    private BigDecimal bounceReceived;
    private BigDecimal totalBankAmount;
    private boolean isRecalReq;
    private boolean cutOffRecalReq;
    private BigDecimal cutOffResetAmount;
    private long receiptID;
    private Date receiptDate;
    private BigDecimal mamAmount;
    private int feeSeq;
    private boolean mainPostings;
    private String entityCode;
    private String bvCode;
    private long receiptSeqID;
    private List<Long> seqAdvpayment;
    private List<Long> seqPostings;
    private List<Long> seqFinFeeDetail;
    private List<Long> seqManualAdvise;
    private List<Long> seqPaymentHeader;
    private List<Long> seqPaymentDetails;
    private List<Long> seqManualAdviseMovements;
    private List<Long> seqFinReceiptHeader;
    private List<Long> seqFinReceiptDetail;
    private List<Long> seqReceiptAllocationDetail;
    private List<Long> seqFinExcessAmount;
    private List<Long> seqFinFeeReceipts;
    private List<Long> seqFinRepayHeader;
    private Date monthStart;
    private boolean skipCorrection;
    private int iDRFrom;
    private String roundAdjMth;
    private int valueToadd;
    private boolean isNewExcess = false;
    private Date miscDate1;
    
    public ReferenceID() {
        this.finTypes = new ArrayList<FinanceType>(1);
        this.manualAdviseID = 333L;
        this.excessID = 0L;
        this.excessMovementID = 0L;
        this.presentmentID = 0L;
        this.repayID = 0L;
        this.appDate = DateUtility.getAppDate();
        this.feeVsGLList = new ArrayList<FeeTypeVsGLMapping>(1);
        this.linkedTranID = -1L;
        this.tranOrder = 0;
        this.postAmount = BigDecimal.ZERO;
        this.madMovementID = 0L;
        this.odcReceived = BigDecimal.ZERO;
        this.bounceReceived = BigDecimal.ZERO;
        this.totalBankAmount = BigDecimal.ZERO;
        this.isRecalReq = false;
        this.cutOffRecalReq = false;
        this.cutOffResetAmount = BigDecimal.ZERO;
        this.receiptID = -1L;
        this.receiptDate = null;
        this.mamAmount = BigDecimal.ZERO;
        this.feeSeq = 0;
        this.receiptSeqID = -1L;
        this.seqAdvpayment = new ArrayList<Long>(1);
        this.seqPostings = new ArrayList<Long>(1);
        this.seqFinFeeDetail = new ArrayList<Long>(1);
        this.seqManualAdvise = new ArrayList<Long>(1);
        this.seqPaymentHeader = new ArrayList<Long>(1);
        this.seqPaymentDetails = new ArrayList<Long>(1);
        this.seqManualAdviseMovements = new ArrayList<Long>(1);
        this.seqFinReceiptHeader = new ArrayList<Long>(1);
        this.seqFinReceiptDetail = new ArrayList<Long>(1);
        this.seqReceiptAllocationDetail = new ArrayList<Long>(1);
        this.seqFinExcessAmount = new ArrayList<Long>(1);
        this.seqFinFeeReceipts = new ArrayList<Long>(1);
        this.seqFinRepayHeader = new ArrayList<Long>(1);
        this.skipCorrection = false;
        this.iDRFrom = 0;
        this.valueToadd = 0;
    }
    
    public long getManualAdviseID() {
        return this.manualAdviseID;
    }
    
    public void setManualAdviseID(final long manualAdviseID) {
        this.manualAdviseID = manualAdviseID;
    }
    
    public long getExcessID() {
        return this.excessID;
    }
    
    public void setExcessID(final long excessID) {
        this.excessID = excessID;
    }
    
    public long getExcessMovementID() {
        return this.excessMovementID;
    }
    
    public void setExcessMovementID(final long excessMovementID) {
        this.excessMovementID = excessMovementID;
    }
    
    public long getPresentmentID() {
        return this.presentmentID;
    }
    
    public void setPresentmentID(final long presentmentID) {
        this.presentmentID = presentmentID;
    }
    
    public long getRepayID() {
        return this.repayID;
    }
    
    public void setRepayID(final long repayID) {
        this.repayID = repayID;
    }
    
    public Date getAppDate() {
        return this.appDate;
    }
    
    public void setAppDate(final Date appDate) {
        this.appDate = appDate;
    }
    
    public List<FinanceType> getFinTypes() {
        return this.finTypes;
    }
    
    public void setFinTypes(final List<FinanceType> finTypes) {
        this.finTypes = finTypes;
    }
    
    public List<FeeTypeVsGLMapping> getFeeVsGLList() {
        return this.feeVsGLList;
    }
    
    public void setFeeVsGLList(final List<FeeTypeVsGLMapping> feeVsGLList) {
        this.feeVsGLList = feeVsGLList;
    }
    
    public int getTranOrder() {
        return this.tranOrder;
    }
    
    public void setTranOrder(final int tranOrder) {
        this.tranOrder = tranOrder;
    }
    
    public String getAccount() {
        return this.account;
    }
    
    public void setAccount(final String account) {
        this.account = account;
    }
    
    public String getAccountType() {
        return this.accountType;
    }
    
    public void setAccountType(final String accountType) {
        this.accountType = accountType;
    }
    
    public BigDecimal getPostAmount() {
        return this.postAmount;
    }
    
    public void setPostAmount(final BigDecimal postAmount) {
        this.postAmount = postAmount;
    }
    
    public String getTranDesc() {
        return this.tranDesc;
    }
    
    public void setTranDesc(final String tranDesc) {
        this.tranDesc = tranDesc;
    }
    
    public String getDrOrcr() {
        return this.drOrcr;
    }
    
    public void setDrOrcr(final String drOrcr) {
        this.drOrcr = drOrcr;
    }
    
    public long getMadMovementID() {
        return this.madMovementID;
    }
    
    public void setMadMovementID(final long madMovementID) {
        this.madMovementID = madMovementID;
    }
    
    public BigDecimal getOdcReceived() {
        return this.odcReceived;
    }
    
    public void setOdcReceived(final BigDecimal odcReceived) {
        this.odcReceived = odcReceived;
    }
    
    public BigDecimal getBounceReceived() {
        return this.bounceReceived;
    }
    
    public void setBounceReceived(final BigDecimal bounceReceived) {
        this.bounceReceived = bounceReceived;
    }
    
    public BigDecimal getTotalBankAmount() {
        return this.totalBankAmount;
    }
    
    public void setTotalBankAmount(final BigDecimal totalBankAmount) {
        this.totalBankAmount = totalBankAmount;
    }
    
    public boolean isRecalReq() {
        return this.isRecalReq;
    }
    
    public void setRecalReq(final boolean isRecalReq) {
        this.isRecalReq = isRecalReq;
    }
    
    public boolean isCutOffRecalReq() {
        return this.cutOffRecalReq;
    }
    
    public void setCutOffRecalReq(final boolean cutOffRecalReq) {
        this.cutOffRecalReq = cutOffRecalReq;
    }
    
    public BigDecimal getCutOffResetAmount() {
        return this.cutOffResetAmount;
    }
    
    public void setCutOffResetAmount(final BigDecimal cutOffResetAmount) {
        this.cutOffResetAmount = cutOffResetAmount;
    }
    
    public long getLinkedTranID() {
        return this.linkedTranID;
    }
    
    public void setLinkedTranID(final long linkedTranID) {
        this.linkedTranID = linkedTranID;
    }
    
    public long getReceiptID() {
        return this.receiptID;
    }
    
    public void setReceiptID(final long receiptID) {
        this.receiptID = receiptID;
    }
    
    public int getFeeSeq() {
        return this.feeSeq;
    }
    
    public void setFeeSeq(final int feeSeq) {
        this.feeSeq = feeSeq;
    }
    
    public boolean isMainPostings() {
        return this.mainPostings;
    }
    
    public void setMainPostings(final boolean mainPostings) {
        this.mainPostings = mainPostings;
    }
    
    public Date getReceiptDate() {
        return this.receiptDate;
    }
    
    public void setReceiptDate(final Date receiptDate) {
        this.receiptDate = receiptDate;
    }
    
    public BigDecimal getMamAmount() {
        return this.mamAmount;
    }
    
    public void setMamAmount(final BigDecimal mamAmount) {
        this.mamAmount = mamAmount;
    }
    
    public List<Long> getSeqAdvpayment() {
        return this.seqAdvpayment;
    }
    
    public void setSeqAdvpayment(final List<Long> seqAdvpayment) {
        this.seqAdvpayment = seqAdvpayment;
    }
    
    public List<Long> getSeqPostings() {
        return this.seqPostings;
    }
    
    public void setSeqPostings(final List<Long> seqPostings) {
        this.seqPostings = seqPostings;
    }
    
    public List<Long> getSeqFinFeeDetail() {
        return this.seqFinFeeDetail;
    }
    
    public void setSeqFinFeeDetail(final List<Long> seqFinFeeDetail) {
        this.seqFinFeeDetail = seqFinFeeDetail;
    }
    
    public List<Long> getSeqManualAdvise() {
        return this.seqManualAdvise;
    }
    
    public void setSeqManualAdvise(final List<Long> seqManualAdvise) {
        this.seqManualAdvise = seqManualAdvise;
    }
    
    public List<Long> getSeqPaymentHeader() {
        return this.seqPaymentHeader;
    }
    
    public void setSeqPaymentHeader(final List<Long> seqPaymentHeader) {
        this.seqPaymentHeader = seqPaymentHeader;
    }
    
    public List<Long> getSeqPaymentDetails() {
        return this.seqPaymentDetails;
    }
    
    public void setSeqPaymentDetails(final List<Long> seqPaymentDetails) {
        this.seqPaymentDetails = seqPaymentDetails;
    }
    
    public List<Long> getSeqManualAdviseMovements() {
        return this.seqManualAdviseMovements;
    }
    
    public void setSeqManualAdviseMovements(final List<Long> seqManualAdviseMovements) {
        this.seqManualAdviseMovements = seqManualAdviseMovements;
    }
    
    public List<Long> getSeqFinReceiptHeader() {
        return this.seqFinReceiptHeader;
    }
    
    public void setSeqFinReceiptHeader(final List<Long> seqFinReceiptHeader) {
        this.seqFinReceiptHeader = seqFinReceiptHeader;
    }
    
    public List<Long> getSeqFinReceiptDetail() {
        return this.seqFinReceiptDetail;
    }
    
    public void setSeqFinReceiptDetail(final List<Long> seqFinReceiptDetail) {
        this.seqFinReceiptDetail = seqFinReceiptDetail;
    }
    
    public List<Long> getSeqReceiptAllocationDetail() {
        return this.seqReceiptAllocationDetail;
    }
    
    public void setSeqReceiptAllocationDetail(final List<Long> seqReceiptAllocationDetail) {
        this.seqReceiptAllocationDetail = seqReceiptAllocationDetail;
    }
    
    public List<Long> getSeqFinExcessAmount() {
        return this.seqFinExcessAmount;
    }
    
    public void setSeqFinExcessAmount(final List<Long> seqFinExcessAmount) {
        this.seqFinExcessAmount = seqFinExcessAmount;
    }
    
    public List<Long> getSeqFinFeeReceipts() {
        return this.seqFinFeeReceipts;
    }
    
    public void setSeqFinFeeReceipts(final List<Long> seqFinFeeReceipts) {
        this.seqFinFeeReceipts = seqFinFeeReceipts;
    }
    
    public List<Long> getSeqFinRepayHeader() {
        return this.seqFinRepayHeader;
    }
    
    public void setSeqFinRepayHeader(final List<Long> seqFinRepayHeader) {
        this.seqFinRepayHeader = seqFinRepayHeader;
    }
    
    public String getEntityCode() {
        return this.entityCode;
    }
    
    public void setEntityCode(final String entityCode) {
        this.entityCode = entityCode;
    }
    
    public String getBvCode() {
        return this.bvCode;
    }
    
    public void setBvCode(final String bvCode) {
        this.bvCode = bvCode;
    }
    
    public long getReceiptSeqID() {
        return this.receiptSeqID;
    }
    
    public void setReceiptSeqID(final long receiptSeqID) {
        this.receiptSeqID = receiptSeqID;
    }
    
    public Date getMonthStart() {
        return this.monthStart;
    }
    
    public void setMonthStart(final Date monthStart) {
        this.monthStart = monthStart;
    }
    
    public boolean isSkipCorrection() {
        return this.skipCorrection;
    }
    
    public void setSkipCorrection(final boolean skipCorrection) {
        this.skipCorrection = skipCorrection;
    }
    
    public int getiDRFrom() {
        return this.iDRFrom;
    }
    
    public void setiDRFrom(final int iDRFrom) {
        this.iDRFrom = iDRFrom;
    }
    
    public String getRoundAdjMth() {
        return this.roundAdjMth;
    }
    
    public void setRoundAdjMth(final String roundAdjMth) {
        this.roundAdjMth = roundAdjMth;
    }
    
    public int getValueToadd() {
        return this.valueToadd;
    }
    
    public void setValueToadd(final int valueToadd) {
        this.valueToadd = valueToadd;
    }

	public boolean isNewExcess() {
		return isNewExcess;
	}

	public void setNewExcess(boolean isNewExcess) {
		this.isNewExcess = isNewExcess;
	}

	public Date getMiscDate1() {
		return miscDate1;
	}

	public void setMiscDate1(Date miscDate1) {
		this.miscDate1 = miscDate1;
	}
}