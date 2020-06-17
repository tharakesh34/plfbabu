package com.pennant.datamigration.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public class DRCorrection implements Serializable
{
    private static final long serialVersionUID = 1183720618731771888L;
    private FinScheduleData finScheduleData;
    private List<FinAdvancePayments> finAdvancePayments;
    private List<FinReceiptHeader> finReceiptHeaders;
    private List<ReceiptAllocationDetail> receiptAllocationDetails;
    private List<FinRepayHeader> finRepayHeaders;
    private List<RepayScheduleDetail> repayScheduleDetails;
    private List<PresentmentHeader> presentmentHeaders;
    private List<PresentmentDetail> presentmentDetails;
    private List<ManualAdvise> manualAdvises;
    private List<ManualAdviseMovements> manualAdviseMovements;
    private List<PaymentHeader> paymentHeaders;
    private List<PaymentDetail> paymentDetails;
    private List<PaymentInstruction> paymentInstructions;
    private Provision provision;
    private List<FinFeeScheduleDetail> finFeeScheduleDetails;
    private List<FinExcessAmount> finExcessAmounts;
    private List<FinExcessMovement> finExcessMovements;
    private List<ReturnDataSet> postEntries;
    private DRFinanceDetails drFinanceDetail;
    private ExcessCorrections excessCorrections;
    private Date appDate;
    private int bpiIdx;
    private int rchIdx;
    private int rcdIdx;
    private int rphIdx;
    private int rpdIdx;
    private int rsdIdx;
    private int radIdx;
    private BigDecimal radPri;
    private BigDecimal radInt;
    private BigDecimal rphPri;
    private BigDecimal rphInt;
    private BigDecimal rpdPri;
    private BigDecimal rpdInt;
    private BigDecimal rsdPri;
    private BigDecimal rsdInt;
    private BigDecimal fsdPri;
    private BigDecimal fsdInt;
    private BigDecimal bpiDeduct;
    private List<FinFeeReceipt> ffrList;
    private DRUpdateCorrection drUpdateCorrection;
    
    public DRCorrection() {
        this.finScheduleData = new FinScheduleData();
        this.finAdvancePayments = new ArrayList<FinAdvancePayments>(1);
        this.finReceiptHeaders = new ArrayList<FinReceiptHeader>(1);
        this.receiptAllocationDetails = new ArrayList<ReceiptAllocationDetail>(1);
        this.finRepayHeaders = new ArrayList<FinRepayHeader>(1);
        this.repayScheduleDetails = new ArrayList<RepayScheduleDetail>(1);
        this.presentmentDetails = new ArrayList<PresentmentDetail>(1);
        this.manualAdvises = new ArrayList<ManualAdvise>(1);
        this.manualAdviseMovements = new ArrayList<ManualAdviseMovements>(1);
        this.finExcessAmounts = new ArrayList<FinExcessAmount>(1);
        this.finExcessMovements = new ArrayList<FinExcessMovement>(1);
        this.postEntries = new ArrayList<ReturnDataSet>(1);
        this.drFinanceDetail = new DRFinanceDetails();
        this.excessCorrections = new ExcessCorrections();
        this.bpiIdx = -1;
        this.rchIdx = -1;
        this.rcdIdx = -1;
        this.rphIdx = -1;
        this.rpdIdx = -1;
        this.rsdIdx = -1;
        this.radIdx = -1;
        this.radPri = BigDecimal.ZERO;
        this.radInt = BigDecimal.ZERO;
        this.rphPri = BigDecimal.ZERO;
        this.rphInt = BigDecimal.ZERO;
        this.rpdPri = BigDecimal.ZERO;
        this.rpdInt = BigDecimal.ZERO;
        this.rsdPri = BigDecimal.ZERO;
        this.rsdInt = BigDecimal.ZERO;
        this.fsdPri = BigDecimal.ZERO;
        this.fsdInt = BigDecimal.ZERO;
        this.bpiDeduct = BigDecimal.ZERO;
        this.ffrList = new ArrayList<FinFeeReceipt>(1);
        this.drUpdateCorrection = new DRUpdateCorrection();
    }
    
    public FinScheduleData getFinScheduleData() {
        return this.finScheduleData;
    }
    
    public void setFinScheduleData(final FinScheduleData finScheduleData) {
        this.finScheduleData = finScheduleData;
    }
    
    public List<FinAdvancePayments> getFinAdvancePayments() {
        return this.finAdvancePayments;
    }
    
    public void setFinAdvancePayments(final List<FinAdvancePayments> finAdvancePayments) {
        this.finAdvancePayments = finAdvancePayments;
    }
    
    public List<FinReceiptHeader> getFinReceiptHeaders() {
        return this.finReceiptHeaders;
    }
    
    public void setFinReceiptHeaders(final List<FinReceiptHeader> finReceiptHeaders) {
        this.finReceiptHeaders = finReceiptHeaders;
    }
    
    public List<ReceiptAllocationDetail> getReceiptAllocationDetails() {
        return this.receiptAllocationDetails;
    }
    
    public void setReceiptAllocationDetails(final List<ReceiptAllocationDetail> receiptAllocationDetails) {
        this.receiptAllocationDetails = receiptAllocationDetails;
    }
    
    public List<FinRepayHeader> getFinRepayHeaders() {
        return this.finRepayHeaders;
    }
    
    public void setFinRepayHeaders(final List<FinRepayHeader> finRepayHeaders) {
        this.finRepayHeaders = finRepayHeaders;
    }
    
    public List<RepayScheduleDetail> getRepayScheduleDetails() {
        return this.repayScheduleDetails;
    }
    
    public void setRepayScheduleDetails(final List<RepayScheduleDetail> repayScheduleDetails) {
        this.repayScheduleDetails = repayScheduleDetails;
    }
    
    public List<PresentmentHeader> getPresentmentHeaders() {
        return this.presentmentHeaders;
    }
    
    public void setPresentmentHeaders(final List<PresentmentHeader> presentmentHeaders) {
        this.presentmentHeaders = presentmentHeaders;
    }
    
    public List<PresentmentDetail> getPresentmentDetails() {
        return this.presentmentDetails;
    }
    
    public void setPresentmentDetails(final List<PresentmentDetail> presentmentDetails) {
        this.presentmentDetails = presentmentDetails;
    }
    
    public List<ManualAdvise> getManualAdvises() {
        return this.manualAdvises;
    }
    
    public void setManualAdvises(final List<ManualAdvise> manualAdvises) {
        this.manualAdvises = manualAdvises;
    }
    
    public List<ManualAdviseMovements> getManualAdviseMovements() {
        return this.manualAdviseMovements;
    }
    
    public void setManualAdviseMovements(final List<ManualAdviseMovements> manualAdviseMovements) {
        this.manualAdviseMovements = manualAdviseMovements;
    }
    
    public List<PaymentHeader> getPaymentHeaders() {
        return this.paymentHeaders;
    }
    
    public void setPaymentHeaders(final List<PaymentHeader> paymentHeaders) {
        this.paymentHeaders = paymentHeaders;
    }
    
    public List<PaymentDetail> getPaymentDetails() {
        return this.paymentDetails;
    }
    
    public void setPaymentDetails(final List<PaymentDetail> paymentDetails) {
        this.paymentDetails = paymentDetails;
    }
    
    public List<PaymentInstruction> getPaymentInstructions() {
        return this.paymentInstructions;
    }
    
    public void setPaymentInstructions(final List<PaymentInstruction> paymentInstructions) {
        this.paymentInstructions = paymentInstructions;
    }
    
    public Provision getProvision() {
        return this.provision;
    }
    
    public void setProvision(final Provision provision) {
        this.provision = provision;
    }
    
    public List<FinFeeScheduleDetail> getFinFeeScheduleDetails() {
        return this.finFeeScheduleDetails;
    }
    
    public void setFinFeeScheduleDetails(final List<FinFeeScheduleDetail> finFeeScheduleDetails) {
        this.finFeeScheduleDetails = finFeeScheduleDetails;
    }
    
    public List<FinExcessAmount> getFinExcessAmounts() {
        return this.finExcessAmounts;
    }
    
    public void setFinExcessAmounts(final List<FinExcessAmount> finExcessAmounts) {
        this.finExcessAmounts = finExcessAmounts;
    }
    
    public List<FinExcessMovement> getFinExcessMovements() {
        return this.finExcessMovements;
    }
    
    public void setFinExcessMovements(final List<FinExcessMovement> finExcessMovements) {
        this.finExcessMovements = finExcessMovements;
    }
    
    public List<ReturnDataSet> getPostEntries() {
        return this.postEntries;
    }
    
    public void setPostEntries(final List<ReturnDataSet> postEntries) {
        this.postEntries = postEntries;
    }
    
    public DRFinanceDetails getDrFinanceDetail() {
        return this.drFinanceDetail;
    }
    
    public void setDrFinanceDetail(final DRFinanceDetails drFinanceDetail) {
        this.drFinanceDetail = drFinanceDetail;
    }
    
    public Date getAppDate() {
        return this.appDate;
    }
    
    public void setAppDate(final Date appDate) {
        this.appDate = appDate;
    }
    
    public ExcessCorrections getExcessCorrections() {
        return this.excessCorrections;
    }
    
    public void setExcessCorrections(final ExcessCorrections excessCorrections) {
        this.excessCorrections = excessCorrections;
    }
    
    public List<FinFeeReceipt> getFfrList() {
        return this.ffrList;
    }
    
    public void setFfrList(final List<FinFeeReceipt> ffrList) {
        this.ffrList = ffrList;
    }
    
    public DRUpdateCorrection getDrUpdateCorrection() {
        return this.drUpdateCorrection;
    }
    
    public void setDrUpdateCorrection(final DRUpdateCorrection drUpdateCorrection) {
        this.drUpdateCorrection = drUpdateCorrection;
    }
    
    public int getBpiIdx() {
        return this.bpiIdx;
    }
    
    public void setBpiIdx(final int bpiIdx) {
        this.bpiIdx = bpiIdx;
    }
    
    public int getRchIdx() {
        return this.rchIdx;
    }
    
    public void setRchIdx(final int rchIdx) {
        this.rchIdx = rchIdx;
    }
    
    public int getRcdIdx() {
        return this.rcdIdx;
    }
    
    public void setRcdIdx(final int rcdIdx) {
        this.rcdIdx = rcdIdx;
    }
    
    public int getRphIdx() {
        return this.rphIdx;
    }
    
    public void setRphIdx(final int rphIdx) {
        this.rphIdx = rphIdx;
    }
    
    public int getRpdIdx() {
        return this.rpdIdx;
    }
    
    public void setRpdIdx(final int rpdIdx) {
        this.rpdIdx = rpdIdx;
    }
    
    public int getRsdIdx() {
        return this.rsdIdx;
    }
    
    public void setRsdIdx(final int rsdIdx) {
        this.rsdIdx = rsdIdx;
    }
    
    public int getRadIdx() {
        return this.radIdx;
    }
    
    public void setRadIdx(final int radIdx) {
        this.radIdx = radIdx;
    }
    
    public BigDecimal getRadPri() {
        return this.radPri;
    }
    
    public void setRadPri(final BigDecimal radPri) {
        this.radPri = radPri;
    }
    
    public BigDecimal getRadInt() {
        return this.radInt;
    }
    
    public void setRadInt(final BigDecimal radInt) {
        this.radInt = radInt;
    }
    
    public BigDecimal getRphPri() {
        return this.rphPri;
    }
    
    public void setRphPri(final BigDecimal rphPri) {
        this.rphPri = rphPri;
    }
    
    public BigDecimal getRphInt() {
        return this.rphInt;
    }
    
    public void setRphInt(final BigDecimal rphInt) {
        this.rphInt = rphInt;
    }
    
    public BigDecimal getRpdPri() {
        return this.rpdPri;
    }
    
    public void setRpdPri(final BigDecimal rpdPri) {
        this.rpdPri = rpdPri;
    }
    
    public BigDecimal getRpdInt() {
        return this.rpdInt;
    }
    
    public void setRpdInt(final BigDecimal rpdInt) {
        this.rpdInt = rpdInt;
    }
    
    public BigDecimal getRsdPri() {
        return this.rsdPri;
    }
    
    public void setRsdPri(final BigDecimal rsdPri) {
        this.rsdPri = rsdPri;
    }
    
    public BigDecimal getRsdInt() {
        return this.rsdInt;
    }
    
    public void setRsdInt(final BigDecimal rsdInt) {
        this.rsdInt = rsdInt;
    }
    
    public BigDecimal getFsdPri() {
        return this.fsdPri;
    }
    
    public void setFsdPri(final BigDecimal fsdPri) {
        this.fsdPri = fsdPri;
    }
    
    public BigDecimal getFsdInt() {
        return this.fsdInt;
    }
    
    public void setFsdInt(final BigDecimal fsdInt) {
        this.fsdInt = fsdInt;
    }
    
    public BigDecimal getBpiDeduct() {
        return this.bpiDeduct;
    }
    
    public void setBpiDeduct(final BigDecimal bpiDeduct) {
        this.bpiDeduct = bpiDeduct;
    }
}