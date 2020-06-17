package com.pennant.datamigration.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SourceDataSummary implements Serializable
{
    private static final long serialVersionUID = 1183720618731771888L;
    private String fm_FinReference;
    private long fm_CustID;
    private String fm_FinBranch;
    private int fm_NumberOfTerms;
    private boolean fm_Cpz;
    private BigDecimal fm_RepayProfitRate;
    private BigDecimal fm_TotalGrossPft;
    private BigDecimal fm_CpzAmount;
    private BigDecimal fm_TotalRepayAmt;
    private BigDecimal fm_FirstRepay;
    private BigDecimal fm_LastRepay;
    private Date fm_FinStartDate;
    private BigDecimal fm_FinAmount;
    private BigDecimal fm_FinRepaymentAmount;
    private BigDecimal fm_FeeChargeAmt;
    private BigDecimal fm_FinAssetValue;
    private BigDecimal fm_FinCurrAssetValue;
    private Boolean fm_JointAc;
    private long fm_JointCustID;
    private long fm_MandateID;
    private Boolean fm_AlowBPI;
    private String fm_BPITreatment;
    private Boolean fm_AlwMultiDisb;
    private BigDecimal fm_BPIAmount;
    private BigDecimal fm_DeductFeeDisb;
    private int fdd_TotDisbCount;
    private BigDecimal fdd_TotDisbAmount;
    private BigDecimal fdd_TotFeeChargeAmt;
    private BigDecimal fdd_FirstDisbAmount;
    private int fap_TotInstructions;
    private BigDecimal fap_TotInstructAmount;
    private BigDecimal fsd_ProfitSchd;
    private BigDecimal fsd_PrincipalSchd;
    private BigDecimal fsd_RepayAmount;
    private BigDecimal fsd_SchdPftPaid;
    private BigDecimal fsd_SchdPriPaid;
    private BigDecimal fsd_CpzAmount;
    private BigDecimal fsd_FirstRepay;
    private BigDecimal fsd_LastRepay;
    private BigDecimal rch_TotalReceiptAmount;
    private BigDecimal rch_TotalWaivedAmount;
    private BigDecimal rch_TotalFeeAmount;
    private Boolean rcd_ReceiptIDFound;
    private BigDecimal rcd_TotalReceiptAmount;
    private BigDecimal rad_TotalAllocated;
    private BigDecimal rad_TOTPAID_NIA;
    private BigDecimal rad_TOTPAID_PRIN;
    private BigDecimal rad_TOTPAID_CHDIS;
    private BigDecimal rad_TOTPAID_INT;
    private BigDecimal rad_TOTPAID_TPF;
    private BigDecimal rad_TOTPAID_IT;
    private BigDecimal rad_TOTPAID_PF;
    private BigDecimal rad_TOTPAID_ODC;
    private BigDecimal rad_TOTPAID_FRE;
    private BigDecimal rad_TOTPAID_DOCC;
    private BigDecimal rad_TOTPAID_APF;
    private BigDecimal rad_TOTPAID_GINS;
    private BigDecimal rad_TOTPAID_EXS;
    private BigDecimal rad_TOTPAID_PAC;
    private BigDecimal rad_TOTPAID_INS;
    private BigDecimal rad_TOTPAID_RI;
    private BigDecimal rad_TOTPAID_CS;
    private BigDecimal rad_TOTPAID_CRS;
    private BigDecimal rad_TOTPAID_INST;
    private BigDecimal rad_TOTPAID_ADMIN;
    private BigDecimal rad_TOTPAID_SC;
    private BigDecimal rph_TotalRepayAmt;
    private BigDecimal rph_TotalPriAmount;
    private BigDecimal rph_TotalPftAmount;
    private Boolean rph_ReceiptSeqIDFound;
    private Boolean rph_RepayIDFound;
    private BigDecimal rpsd_ProfitSchd;
    private BigDecimal rpsd_ProfitSchdPaid;
    private BigDecimal rpsd_PrincipalSchd;
    private BigDecimal rpsd_PrincipalSchdPaid;
    private Boolean pd_PresentmentIDFound;
    private Boolean pd_MandateIDFound;
    private BigDecimal pd_TotalSchAmtDue;
    private BigDecimal pd_TotalPresentmentAmt;
    private Boolean pd_ReceiptIDFound;
    private Boolean ma_FeeTypeIDFound;
    private BigDecimal ma_TotalAdviseAmount;
    private BigDecimal ma_TotalPaidAmount;
    private Boolean ma_ReceiptIDFound;
    private Boolean mam_AdviseIDFound;
    private BigDecimal mam_TotalMovementAmount;
    private BigDecimal mam_TotalPaidAmount;
    private Boolean mam_ReceiptIDFound;
    private BigDecimal fod_TotalODAmount;
    private BigDecimal fod_TotalODPrincipal;
    private BigDecimal fod_TotalODProfit;
    private BigDecimal fod_TotalPenaltyAmount;
    private BigDecimal fod_TotalPenaltyPaid;
    private BigDecimal fod_TotalPenaltyBal;
    private int prv_DueDays;
    private long prv_DPDBucketID;
    private long prv_NPABucketID;
    private BigDecimal prv_PrincipalDue;
    private BigDecimal prv_ProfitDue;
    private BigDecimal prv_ProvisionAmtCal;
    private Boolean ffd_FeeTypeIDFound;
    private BigDecimal ffd_TotalDisbCalFee;
    private BigDecimal ffd_TotalActualFee;
    private BigDecimal ffd_TotalPaidFee;
    private BigDecimal fea_Amount;
    private BigDecimal fea_UtilizedAmount;
    private BigDecimal fea_BalanceAmount;
    private String errors;
    private String warnings;
    private String information;
    private BigDecimal rcd_ExcessUtilised;
    private BigDecimal rcd_AdvanceUtilised;
    private BigDecimal rph_TotalExcessAmount;
    private BigDecimal rph_TotalAdvanceAmount;
    private BigDecimal rsd_PriPaid;
    private BigDecimal rsd_PftPaid;
    private BigDecimal rsd_TdsPaid;
    private BigDecimal rsd_LppPaid;
    private BigDecimal rsd_LpiPaid;
    private BigDecimal rsd_PriWaived;
    private BigDecimal rsd_PftWaived;
    private BigDecimal rsd_LppWaived;
    private BigDecimal rsd_LpiWaived;
    
    public SourceDataSummary() {
        this.fm_CustID = 0L;
        this.fm_NumberOfTerms = 0;
        this.fm_Cpz = false;
        this.fm_RepayProfitRate = BigDecimal.ZERO;
        this.fm_TotalGrossPft = BigDecimal.ZERO;
        this.fm_CpzAmount = BigDecimal.ZERO;
        this.fm_TotalRepayAmt = BigDecimal.ZERO;
        this.fm_FirstRepay = BigDecimal.ZERO;
        this.fm_LastRepay = BigDecimal.ZERO;
        this.fm_FinAmount = BigDecimal.ZERO;
        this.fm_FinRepaymentAmount = BigDecimal.ZERO;
        this.fm_FeeChargeAmt = BigDecimal.ZERO;
        this.fm_FinAssetValue = BigDecimal.ZERO;
        this.fm_FinCurrAssetValue = BigDecimal.ZERO;
        this.fm_JointAc = false;
        this.fm_JointCustID = 0L;
        this.fm_MandateID = 0L;
        this.fm_AlowBPI = false;
        this.fm_AlwMultiDisb = false;
        this.fm_BPIAmount = BigDecimal.ZERO;
        this.fm_DeductFeeDisb = BigDecimal.ZERO;
        this.fdd_TotDisbCount = 0;
        this.fdd_TotDisbAmount = BigDecimal.ZERO;
        this.fdd_TotFeeChargeAmt = BigDecimal.ZERO;
        this.fdd_FirstDisbAmount = BigDecimal.ZERO;
        this.fap_TotInstructions = 0;
        this.fap_TotInstructAmount = BigDecimal.ZERO;
        this.fsd_ProfitSchd = BigDecimal.ZERO;
        this.fsd_PrincipalSchd = BigDecimal.ZERO;
        this.fsd_RepayAmount = BigDecimal.ZERO;
        this.fsd_SchdPftPaid = BigDecimal.ZERO;
        this.fsd_SchdPriPaid = BigDecimal.ZERO;
        this.fsd_CpzAmount = BigDecimal.ZERO;
        this.fsd_FirstRepay = BigDecimal.ZERO;
        this.fsd_LastRepay = BigDecimal.ZERO;
        this.rch_TotalReceiptAmount = BigDecimal.ZERO;
        this.rch_TotalWaivedAmount = BigDecimal.ZERO;
        this.rch_TotalFeeAmount = BigDecimal.ZERO;
        this.rcd_TotalReceiptAmount = BigDecimal.ZERO;
        this.rad_TotalAllocated = BigDecimal.ZERO;
        this.rad_TOTPAID_NIA = BigDecimal.ZERO;
        this.rad_TOTPAID_PRIN = BigDecimal.ZERO;
        this.rad_TOTPAID_CHDIS = BigDecimal.ZERO;
        this.rad_TOTPAID_INT = BigDecimal.ZERO;
        this.rad_TOTPAID_TPF = BigDecimal.ZERO;
        this.rad_TOTPAID_IT = BigDecimal.ZERO;
        this.rad_TOTPAID_PF = BigDecimal.ZERO;
        this.rad_TOTPAID_ODC = BigDecimal.ZERO;
        this.rad_TOTPAID_FRE = BigDecimal.ZERO;
        this.rad_TOTPAID_DOCC = BigDecimal.ZERO;
        this.rad_TOTPAID_APF = BigDecimal.ZERO;
        this.rad_TOTPAID_GINS = BigDecimal.ZERO;
        this.rad_TOTPAID_EXS = BigDecimal.ZERO;
        this.rad_TOTPAID_PAC = BigDecimal.ZERO;
        this.rad_TOTPAID_INS = BigDecimal.ZERO;
        this.rad_TOTPAID_RI = BigDecimal.ZERO;
        this.rad_TOTPAID_CS = BigDecimal.ZERO;
        this.rad_TOTPAID_CRS = BigDecimal.ZERO;
        this.rad_TOTPAID_INST = BigDecimal.ZERO;
        this.rad_TOTPAID_ADMIN = BigDecimal.ZERO;
        this.rad_TOTPAID_SC = BigDecimal.ZERO;
        this.rph_TotalRepayAmt = BigDecimal.ZERO;
        this.rph_TotalPriAmount = BigDecimal.ZERO;
        this.rph_TotalPftAmount = BigDecimal.ZERO;
        this.rph_ReceiptSeqIDFound = false;
        this.rph_RepayIDFound = false;
        this.rpsd_ProfitSchd = BigDecimal.ZERO;
        this.rpsd_ProfitSchdPaid = BigDecimal.ZERO;
        this.rpsd_PrincipalSchd = BigDecimal.ZERO;
        this.rpsd_PrincipalSchdPaid = BigDecimal.ZERO;
        this.pd_PresentmentIDFound = false;
        this.pd_MandateIDFound = false;
        this.pd_TotalSchAmtDue = BigDecimal.ZERO;
        this.pd_TotalPresentmentAmt = BigDecimal.ZERO;
        this.pd_ReceiptIDFound = false;
        this.ma_FeeTypeIDFound = false;
        this.ma_TotalAdviseAmount = BigDecimal.ZERO;
        this.ma_TotalPaidAmount = BigDecimal.ZERO;
        this.ma_ReceiptIDFound = false;
        this.mam_AdviseIDFound = false;
        this.mam_TotalMovementAmount = BigDecimal.ZERO;
        this.mam_TotalPaidAmount = BigDecimal.ZERO;
        this.mam_ReceiptIDFound = false;
        this.fod_TotalODAmount = BigDecimal.ZERO;
        this.fod_TotalODPrincipal = BigDecimal.ZERO;
        this.fod_TotalODProfit = BigDecimal.ZERO;
        this.fod_TotalPenaltyAmount = BigDecimal.ZERO;
        this.fod_TotalPenaltyPaid = BigDecimal.ZERO;
        this.fod_TotalPenaltyBal = BigDecimal.ZERO;
        this.prv_DueDays = 0;
        this.prv_DPDBucketID = 0L;
        this.prv_NPABucketID = 0L;
        this.prv_PrincipalDue = BigDecimal.ZERO;
        this.prv_ProfitDue = BigDecimal.ZERO;
        this.prv_ProvisionAmtCal = BigDecimal.ZERO;
        this.ffd_FeeTypeIDFound = false;
        this.ffd_TotalDisbCalFee = BigDecimal.ZERO;
        this.ffd_TotalActualFee = BigDecimal.ZERO;
        this.ffd_TotalPaidFee = BigDecimal.ZERO;
        this.fea_Amount = BigDecimal.ZERO;
        this.fea_UtilizedAmount = BigDecimal.ZERO;
        this.fea_BalanceAmount = BigDecimal.ZERO;
        this.errors = "";
        this.warnings = "";
        this.information = "";
        this.rcd_ExcessUtilised = BigDecimal.ZERO;
        this.rcd_AdvanceUtilised = BigDecimal.ZERO;
        this.rph_TotalExcessAmount = BigDecimal.ZERO;
        this.rph_TotalAdvanceAmount = BigDecimal.ZERO;
        this.rsd_PriPaid = BigDecimal.ZERO;
        this.rsd_PftPaid = BigDecimal.ZERO;
        this.rsd_TdsPaid = BigDecimal.ZERO;
        this.rsd_LppPaid = BigDecimal.ZERO;
        this.rsd_LpiPaid = BigDecimal.ZERO;
        this.rsd_PriWaived = BigDecimal.ZERO;
        this.rsd_PftWaived = BigDecimal.ZERO;
        this.rsd_LppWaived = BigDecimal.ZERO;
        this.rsd_LpiWaived = BigDecimal.ZERO;
    }
    
    public String getFm_FinReference() {
        return this.fm_FinReference;
    }
    
    public void setFm_FinReference(final String fm_FinReference) {
        this.fm_FinReference = fm_FinReference;
    }
    
    public long getFm_CustID() {
        return this.fm_CustID;
    }
    
    public void setFm_CustID(final long fm_CustID) {
        this.fm_CustID = fm_CustID;
    }
    
    public String getFm_FinBranch() {
        return this.fm_FinBranch;
    }
    
    public void setFm_FinBranch(final String fm_FinBranch) {
        this.fm_FinBranch = fm_FinBranch;
    }
    
    public int getFm_NumberOfTerms() {
        return this.fm_NumberOfTerms;
    }
    
    public void setFm_NumberOfTerms(final int fm_NumberOfTerms) {
        this.fm_NumberOfTerms = fm_NumberOfTerms;
    }
    
    public BigDecimal getFm_RepayProfitRate() {
        return this.fm_RepayProfitRate;
    }
    
    public void setFm_RepayProfitRate(final BigDecimal fm_RepayProfitRate) {
        this.fm_RepayProfitRate = fm_RepayProfitRate;
    }
    
    public BigDecimal getFm_TotalGrossPft() {
        return this.fm_TotalGrossPft;
    }
    
    public void setFm_TotalGrossPft(final BigDecimal fm_TotalGrossPft) {
        this.fm_TotalGrossPft = fm_TotalGrossPft;
    }
    
    public BigDecimal getFm_TotalRepayAmt() {
        return this.fm_TotalRepayAmt;
    }
    
    public void setFm_TotalRepayAmt(final BigDecimal fm_TotalRepayAmt) {
        this.fm_TotalRepayAmt = fm_TotalRepayAmt;
    }
    
    public BigDecimal getFm_FirstRepay() {
        return this.fm_FirstRepay;
    }
    
    public void setFm_FirstRepay(final BigDecimal fm_FirstRepay) {
        this.fm_FirstRepay = fm_FirstRepay;
    }
    
    public BigDecimal getFm_LastRepay() {
        return this.fm_LastRepay;
    }
    
    public void setFm_LastRepay(final BigDecimal fm_LastRepay) {
        this.fm_LastRepay = fm_LastRepay;
    }
    
    public Date getFm_FinStartDate() {
        return this.fm_FinStartDate;
    }
    
    public void setFm_FinStartDate(final Date fm_FinStartDate) {
        this.fm_FinStartDate = fm_FinStartDate;
    }
    
    public BigDecimal getFm_FinAmount() {
        return this.fm_FinAmount;
    }
    
    public void setFm_FinAmount(final BigDecimal fm_FinAmount) {
        this.fm_FinAmount = fm_FinAmount;
    }
    
    public BigDecimal getFm_FinRepaymentAmount() {
        return this.fm_FinRepaymentAmount;
    }
    
    public void setFm_FinRepaymentAmount(final BigDecimal fm_FinRepaymentAmount) {
        this.fm_FinRepaymentAmount = fm_FinRepaymentAmount;
    }
    
    public BigDecimal getFm_FeeChargeAmt() {
        return this.fm_FeeChargeAmt;
    }
    
    public void setFm_FeeChargeAmt(final BigDecimal fm_FeeChargeAmt) {
        this.fm_FeeChargeAmt = fm_FeeChargeAmt;
    }
    
    public Boolean getFm_JointAc() {
        return this.fm_JointAc;
    }
    
    public void setFm_JointAc(final Boolean fm_JointAc) {
        this.fm_JointAc = fm_JointAc;
    }
    
    public long getFm_JointCustID() {
        return this.fm_JointCustID;
    }
    
    public void setFm_JointCustID(final long fm_JointCustID) {
        this.fm_JointCustID = fm_JointCustID;
    }
    
    public long getFm_MandateID() {
        return this.fm_MandateID;
    }
    
    public void setFm_MandateID(final long fm_MandateID) {
        this.fm_MandateID = fm_MandateID;
    }
    
    public Boolean getFm_AlowBPI() {
        return this.fm_AlowBPI;
    }
    
    public void setFm_AlowBPI(final Boolean fm_AlowBPI) {
        this.fm_AlowBPI = fm_AlowBPI;
    }
    
    public String getFm_BPITreatment() {
        return this.fm_BPITreatment;
    }
    
    public void setFm_BPITreatment(final String fm_BPITreatment) {
        this.fm_BPITreatment = fm_BPITreatment;
    }
    
    public Boolean getFm_AlwMultiDisb() {
        return this.fm_AlwMultiDisb;
    }
    
    public void setFm_AlwMultiDisb(final Boolean fm_AlwMultiDisb) {
        this.fm_AlwMultiDisb = fm_AlwMultiDisb;
    }
    
    public BigDecimal getFm_BPIAmount() {
        return this.fm_BPIAmount;
    }
    
    public void setFm_BPIAmount(final BigDecimal fm_BPIAmount) {
        this.fm_BPIAmount = fm_BPIAmount;
    }
    
    public BigDecimal getFm_DeductFeeDisb() {
        return this.fm_DeductFeeDisb;
    }
    
    public void setFm_DeductFeeDisb(final BigDecimal fm_DeductFeeDisb) {
        this.fm_DeductFeeDisb = fm_DeductFeeDisb;
    }
    
    public int getFdd_TotDisbCount() {
        return this.fdd_TotDisbCount;
    }
    
    public void setFdd_TotDisbCount(final int fdd_TotDisbCount) {
        this.fdd_TotDisbCount = fdd_TotDisbCount;
    }
    
    public BigDecimal getFdd_TotDisbAmount() {
        return this.fdd_TotDisbAmount;
    }
    
    public void setFdd_TotDisbAmount(final BigDecimal fdd_TotDisbAmount) {
        this.fdd_TotDisbAmount = fdd_TotDisbAmount;
    }
    
    public BigDecimal getFdd_TotFeeChargeAmt() {
        return this.fdd_TotFeeChargeAmt;
    }
    
    public void setFdd_TotFeeChargeAmt(final BigDecimal fdd_TotFeeChargeAmt) {
        this.fdd_TotFeeChargeAmt = fdd_TotFeeChargeAmt;
    }
    
    public int getFap_TotInstructions() {
        return this.fap_TotInstructions;
    }
    
    public void setFap_TotInstructions(final int fap_TotInstructions) {
        this.fap_TotInstructions = fap_TotInstructions;
    }
    
    public BigDecimal getFap_TotInstructAmount() {
        return this.fap_TotInstructAmount;
    }
    
    public void setFap_TotInstructAmount(final BigDecimal fap_TotInstructAmount) {
        this.fap_TotInstructAmount = fap_TotInstructAmount;
    }
    
    public BigDecimal getFsd_ProfitSchd() {
        return this.fsd_ProfitSchd;
    }
    
    public void setFsd_ProfitSchd(final BigDecimal fsd_ProfitSchd) {
        this.fsd_ProfitSchd = fsd_ProfitSchd;
    }
    
    public BigDecimal getFsd_PrincipalSchd() {
        return this.fsd_PrincipalSchd;
    }
    
    public void setFsd_PrincipalSchd(final BigDecimal fsd_PrincipalSchd) {
        this.fsd_PrincipalSchd = fsd_PrincipalSchd;
    }
    
    public BigDecimal getFsd_RepayAmount() {
        return this.fsd_RepayAmount;
    }
    
    public void setFsd_RepayAmount(final BigDecimal fsd_RepayAmount) {
        this.fsd_RepayAmount = fsd_RepayAmount;
    }
    
    public BigDecimal getFsd_SchdPftPaid() {
        return this.fsd_SchdPftPaid;
    }
    
    public void setFsd_SchdPftPaid(final BigDecimal fsd_SchdPftPaid) {
        this.fsd_SchdPftPaid = fsd_SchdPftPaid;
    }
    
    public BigDecimal getFsd_SchdPriPaid() {
        return this.fsd_SchdPriPaid;
    }
    
    public void setFsd_SchdPriPaid(final BigDecimal fsd_SchdPriPaid) {
        this.fsd_SchdPriPaid = fsd_SchdPriPaid;
    }
    
    public BigDecimal getRch_TotalReceiptAmount() {
        return this.rch_TotalReceiptAmount;
    }
    
    public void setRch_TotalReceiptAmount(final BigDecimal rch_TotalReceiptAmount) {
        this.rch_TotalReceiptAmount = rch_TotalReceiptAmount;
    }
    
    public BigDecimal getRch_TotalWaivedAmount() {
        return this.rch_TotalWaivedAmount;
    }
    
    public void setRch_TotalWaivedAmount(final BigDecimal rch_TotalWaivedAmount) {
        this.rch_TotalWaivedAmount = rch_TotalWaivedAmount;
    }
    
    public BigDecimal getRch_TotalFeeAmount() {
        return this.rch_TotalFeeAmount;
    }
    
    public void setRch_TotalFeeAmount(final BigDecimal rch_TotalFeeAmount) {
        this.rch_TotalFeeAmount = rch_TotalFeeAmount;
    }
    
    public Boolean getRcd_ReceiptIDFound() {
        return this.rcd_ReceiptIDFound;
    }
    
    public void setRcd_ReceiptIDFound(final Boolean rcd_ReceiptIDFound) {
        this.rcd_ReceiptIDFound = rcd_ReceiptIDFound;
    }
    
    public BigDecimal getRcd_TotalReceiptAmount() {
        return this.rcd_TotalReceiptAmount;
    }
    
    public void setRcd_TotalReceiptAmount(final BigDecimal rcd_TotalReceiptAmount) {
        this.rcd_TotalReceiptAmount = rcd_TotalReceiptAmount;
    }
    
    public BigDecimal getRad_TOTPAID_NIA() {
        return this.rad_TOTPAID_NIA;
    }
    
    public void setRad_TOTPAID_NIA(final BigDecimal rad_TOTPAID_NIA) {
        this.rad_TOTPAID_NIA = rad_TOTPAID_NIA;
    }
    
    public BigDecimal getRad_TOTPAID_PRIN() {
        return this.rad_TOTPAID_PRIN;
    }
    
    public void setRad_TOTPAID_PRIN(final BigDecimal rad_TOTPAID_PRIN) {
        this.rad_TOTPAID_PRIN = rad_TOTPAID_PRIN;
    }
    
    public BigDecimal getRad_TOTPAID_CHDIS() {
        return this.rad_TOTPAID_CHDIS;
    }
    
    public void setRad_TOTPAID_CHDIS(final BigDecimal rad_TOTPAID_CHDIS) {
        this.rad_TOTPAID_CHDIS = rad_TOTPAID_CHDIS;
    }
    
    public BigDecimal getRad_TOTPAID_INT() {
        return this.rad_TOTPAID_INT;
    }
    
    public void setRad_TOTPAID_INT(final BigDecimal rad_TOTPAID_INT) {
        this.rad_TOTPAID_INT = rad_TOTPAID_INT;
    }
    
    public BigDecimal getRad_TOTPAID_TPF() {
        return this.rad_TOTPAID_TPF;
    }
    
    public void setRad_TOTPAID_TPF(final BigDecimal rad_TOTPAID_TPF) {
        this.rad_TOTPAID_TPF = rad_TOTPAID_TPF;
    }
    
    public BigDecimal getRad_TOTPAID_IT() {
        return this.rad_TOTPAID_IT;
    }
    
    public void setRad_TOTPAID_IT(final BigDecimal rad_TOTPAID_IT) {
        this.rad_TOTPAID_IT = rad_TOTPAID_IT;
    }
    
    public BigDecimal getRad_TOTPAID_PF() {
        return this.rad_TOTPAID_PF;
    }
    
    public void setRad_TOTPAID_PF(final BigDecimal rad_TOTPAID_PF) {
        this.rad_TOTPAID_PF = rad_TOTPAID_PF;
    }
    
    public BigDecimal getRad_TOTPAID_ODC() {
        return this.rad_TOTPAID_ODC;
    }
    
    public void setRad_TOTPAID_ODC(final BigDecimal rad_TOTPAID_ODC) {
        this.rad_TOTPAID_ODC = rad_TOTPAID_ODC;
    }
    
    public BigDecimal getRad_TOTPAID_FRE() {
        return this.rad_TOTPAID_FRE;
    }
    
    public void setRad_TOTPAID_FRE(final BigDecimal rad_TOTPAID_FRE) {
        this.rad_TOTPAID_FRE = rad_TOTPAID_FRE;
    }
    
    public BigDecimal getRad_TOTPAID_DOCC() {
        return this.rad_TOTPAID_DOCC;
    }
    
    public void setRad_TOTPAID_DOCC(final BigDecimal rad_TOTPAID_DOCC) {
        this.rad_TOTPAID_DOCC = rad_TOTPAID_DOCC;
    }
    
    public BigDecimal getRad_TOTPAID_APF() {
        return this.rad_TOTPAID_APF;
    }
    
    public void setRad_TOTPAID_APF(final BigDecimal rad_TOTPAID_APF) {
        this.rad_TOTPAID_APF = rad_TOTPAID_APF;
    }
    
    public BigDecimal getRad_TOTPAID_GINS() {
        return this.rad_TOTPAID_GINS;
    }
    
    public void setRad_TOTPAID_GINS(final BigDecimal rad_TOTPAID_GINS) {
        this.rad_TOTPAID_GINS = rad_TOTPAID_GINS;
    }
    
    public BigDecimal getRad_TOTPAID_EXS() {
        return this.rad_TOTPAID_EXS;
    }
    
    public void setRad_TOTPAID_EXS(final BigDecimal rad_TOTPAID_EXS) {
        this.rad_TOTPAID_EXS = rad_TOTPAID_EXS;
    }
    
    public BigDecimal getRad_TOTPAID_PAC() {
        return this.rad_TOTPAID_PAC;
    }
    
    public void setRad_TOTPAID_PAC(final BigDecimal rad_TOTPAID_PAC) {
        this.rad_TOTPAID_PAC = rad_TOTPAID_PAC;
    }
    
    public BigDecimal getRad_TOTPAID_INS() {
        return this.rad_TOTPAID_INS;
    }
    
    public void setRad_TOTPAID_INS(final BigDecimal rad_TOTPAID_INS) {
        this.rad_TOTPAID_INS = rad_TOTPAID_INS;
    }
    
    public BigDecimal getRad_TOTPAID_RI() {
        return this.rad_TOTPAID_RI;
    }
    
    public void setRad_TOTPAID_RI(final BigDecimal rad_TOTPAID_RI) {
        this.rad_TOTPAID_RI = rad_TOTPAID_RI;
    }
    
    public BigDecimal getRad_TOTPAID_CS() {
        return this.rad_TOTPAID_CS;
    }
    
    public void setRad_TOTPAID_CS(final BigDecimal rad_TOTPAID_CS) {
        this.rad_TOTPAID_CS = rad_TOTPAID_CS;
    }
    
    public BigDecimal getRad_TOTPAID_CRS() {
        return this.rad_TOTPAID_CRS;
    }
    
    public void setRad_TOTPAID_CRS(final BigDecimal rad_TOTPAID_CRS) {
        this.rad_TOTPAID_CRS = rad_TOTPAID_CRS;
    }
    
    public BigDecimal getRad_TOTPAID_INST() {
        return this.rad_TOTPAID_INST;
    }
    
    public void setRad_TOTPAID_INST(final BigDecimal rad_TOTPAID_INST) {
        this.rad_TOTPAID_INST = rad_TOTPAID_INST;
    }
    
    public BigDecimal getRad_TOTPAID_ADMIN() {
        return this.rad_TOTPAID_ADMIN;
    }
    
    public void setRad_TOTPAID_ADMIN(final BigDecimal rad_TOTPAID_ADMIN) {
        this.rad_TOTPAID_ADMIN = rad_TOTPAID_ADMIN;
    }
    
    public BigDecimal getRad_TOTPAID_SC() {
        return this.rad_TOTPAID_SC;
    }
    
    public void setRad_TOTPAID_SC(final BigDecimal rad_TOTPAID_SC) {
        this.rad_TOTPAID_SC = rad_TOTPAID_SC;
    }
    
    public BigDecimal getRph_TotalRepayAmt() {
        return this.rph_TotalRepayAmt;
    }
    
    public void setRph_TotalRepayAmt(final BigDecimal rph_TotalRepayAmt) {
        this.rph_TotalRepayAmt = rph_TotalRepayAmt;
    }
    
    public BigDecimal getRph_TotalPriAmount() {
        return this.rph_TotalPriAmount;
    }
    
    public void setRph_TotalPriAmount(final BigDecimal rph_TotalPriAmount) {
        this.rph_TotalPriAmount = rph_TotalPriAmount;
    }
    
    public BigDecimal getRph_TotalPftAmount() {
        return this.rph_TotalPftAmount;
    }
    
    public void setRph_TotalPftAmount(final BigDecimal rph_TotalPftAmount) {
        this.rph_TotalPftAmount = rph_TotalPftAmount;
    }
    
    public Boolean getRph_ReceiptSeqIDFound() {
        return this.rph_ReceiptSeqIDFound;
    }
    
    public void setRph_ReceiptSeqIDFound(final Boolean rph_ReceiptSeqIDFound) {
        this.rph_ReceiptSeqIDFound = rph_ReceiptSeqIDFound;
    }
    
    public Boolean getRph_RepayIDFound() {
        return this.rph_RepayIDFound;
    }
    
    public void setRph_RepayIDFound(final Boolean rph_RepayIDFound) {
        this.rph_RepayIDFound = rph_RepayIDFound;
    }
    
    public BigDecimal getRpsd_ProfitSchd() {
        return this.rpsd_ProfitSchd;
    }
    
    public void setRpsd_ProfitSchd(final BigDecimal rpsd_ProfitSchd) {
        this.rpsd_ProfitSchd = rpsd_ProfitSchd;
    }
    
    public BigDecimal getRpsd_ProfitSchdPaid() {
        return this.rpsd_ProfitSchdPaid;
    }
    
    public void setRpsd_ProfitSchdPaid(final BigDecimal rpsd_ProfitSchdPaid) {
        this.rpsd_ProfitSchdPaid = rpsd_ProfitSchdPaid;
    }
    
    public BigDecimal getRpsd_PrincipalSchd() {
        return this.rpsd_PrincipalSchd;
    }
    
    public void setRpsd_PrincipalSchd(final BigDecimal rpsd_PrincipalSchd) {
        this.rpsd_PrincipalSchd = rpsd_PrincipalSchd;
    }
    
    public BigDecimal getRpsd_PrincipalSchdPaid() {
        return this.rpsd_PrincipalSchdPaid;
    }
    
    public void setRpsd_PrincipalSchdPaid(final BigDecimal rpsd_PrincipalSchdPaid) {
        this.rpsd_PrincipalSchdPaid = rpsd_PrincipalSchdPaid;
    }
    
    public Boolean getPd_PresentmentIDFound() {
        return this.pd_PresentmentIDFound;
    }
    
    public void setPd_PresentmentIDFound(final Boolean pd_PresentmentIDFound) {
        this.pd_PresentmentIDFound = pd_PresentmentIDFound;
    }
    
    public Boolean getPd_MandateIDFound() {
        return this.pd_MandateIDFound;
    }
    
    public void setPd_MandateIDFound(final Boolean pd_MandateIDFound) {
        this.pd_MandateIDFound = pd_MandateIDFound;
    }
    
    public BigDecimal getPd_TotalSchAmtDue() {
        return this.pd_TotalSchAmtDue;
    }
    
    public void setPd_TotalSchAmtDue(final BigDecimal pd_TotalSchAmtDue) {
        this.pd_TotalSchAmtDue = pd_TotalSchAmtDue;
    }
    
    public BigDecimal getPd_TotalPresentmentAmt() {
        return this.pd_TotalPresentmentAmt;
    }
    
    public void setPd_TotalPresentmentAmt(final BigDecimal pd_TotalPresentmentAmt) {
        this.pd_TotalPresentmentAmt = pd_TotalPresentmentAmt;
    }
    
    public Boolean getPd_ReceiptIDFound() {
        return this.pd_ReceiptIDFound;
    }
    
    public void setPd_ReceiptIDFound(final Boolean pd_ReceiptIDFound) {
        this.pd_ReceiptIDFound = pd_ReceiptIDFound;
    }
    
    public Boolean getMa_FeeTypeIDFound() {
        return this.ma_FeeTypeIDFound;
    }
    
    public void setMa_FeeTypeIDFound(final Boolean ma_FeeTypeIDFound) {
        this.ma_FeeTypeIDFound = ma_FeeTypeIDFound;
    }
    
    public BigDecimal getMa_TotalAdviseAmount() {
        return this.ma_TotalAdviseAmount;
    }
    
    public void setMa_TotalAdviseAmount(final BigDecimal ma_TotalAdviseAmount) {
        this.ma_TotalAdviseAmount = ma_TotalAdviseAmount;
    }
    
    public BigDecimal getMa_TotalPaidAmount() {
        return this.ma_TotalPaidAmount;
    }
    
    public void setMa_TotalPaidAmount(final BigDecimal ma_TotalPaidAmount) {
        this.ma_TotalPaidAmount = ma_TotalPaidAmount;
    }
    
    public Boolean getMa_ReceiptIDFound() {
        return this.ma_ReceiptIDFound;
    }
    
    public void setMa_ReceiptIDFound(final Boolean ma_ReceiptIDFound) {
        this.ma_ReceiptIDFound = ma_ReceiptIDFound;
    }
    
    public Boolean getMam_AdviseIDFound() {
        return this.mam_AdviseIDFound;
    }
    
    public void setMam_AdviseIDFound(final Boolean mam_AdviseIDFound) {
        this.mam_AdviseIDFound = mam_AdviseIDFound;
    }
    
    public BigDecimal getMam_TotalMovementAmount() {
        return this.mam_TotalMovementAmount;
    }
    
    public void setMam_TotalMovementAmount(final BigDecimal mam_TotalMovementAmount) {
        this.mam_TotalMovementAmount = mam_TotalMovementAmount;
    }
    
    public BigDecimal getMam_TotalPaidAmount() {
        return this.mam_TotalPaidAmount;
    }
    
    public void setMam_TotalPaidAmount(final BigDecimal mam_TotalPaidAmount) {
        this.mam_TotalPaidAmount = mam_TotalPaidAmount;
    }
    
    public Boolean getMam_ReceiptIDFound() {
        return this.mam_ReceiptIDFound;
    }
    
    public void setMam_ReceiptIDFound(final Boolean mam_ReceiptIDFound) {
        this.mam_ReceiptIDFound = mam_ReceiptIDFound;
    }
    
    public BigDecimal getFod_TotalODAmount() {
        return this.fod_TotalODAmount;
    }
    
    public void setFod_TotalODAmount(final BigDecimal fod_TotalODAmount) {
        this.fod_TotalODAmount = fod_TotalODAmount;
    }
    
    public BigDecimal getFod_TotalODPrincipal() {
        return this.fod_TotalODPrincipal;
    }
    
    public void setFod_TotalODPrincipal(final BigDecimal fod_TotalODPrincipal) {
        this.fod_TotalODPrincipal = fod_TotalODPrincipal;
    }
    
    public BigDecimal getFod_TotalODProfit() {
        return this.fod_TotalODProfit;
    }
    
    public void setFod_TotalODProfit(final BigDecimal fod_TotalODProfit) {
        this.fod_TotalODProfit = fod_TotalODProfit;
    }
    
    public BigDecimal getFod_TotalPenaltyAmount() {
        return this.fod_TotalPenaltyAmount;
    }
    
    public void setFod_TotalPenaltyAmount(final BigDecimal fod_TotalPenaltyAmount) {
        this.fod_TotalPenaltyAmount = fod_TotalPenaltyAmount;
    }
    
    public BigDecimal getFod_TotalPenaltyPaid() {
        return this.fod_TotalPenaltyPaid;
    }
    
    public void setFod_TotalPenaltyPaid(final BigDecimal fod_TotalPenaltyPaid) {
        this.fod_TotalPenaltyPaid = fod_TotalPenaltyPaid;
    }
    
    public BigDecimal getFod_TotalPenaltyBal() {
        return this.fod_TotalPenaltyBal;
    }
    
    public void setFod_TotalPenaltyBal(final BigDecimal fod_TotalPenaltyBal) {
        this.fod_TotalPenaltyBal = fod_TotalPenaltyBal;
    }
    
    public int getPrv_DueDays() {
        return this.prv_DueDays;
    }
    
    public void setPrv_DueDays(final int prv_DueDays) {
        this.prv_DueDays = prv_DueDays;
    }
    
    public long getPrv_DPDBucketID() {
        return this.prv_DPDBucketID;
    }
    
    public void setPrv_DPDBucketID(final long prv_DPDBucketID) {
        this.prv_DPDBucketID = prv_DPDBucketID;
    }
    
    public long getPrv_NPABucketID() {
        return this.prv_NPABucketID;
    }
    
    public void setPrv_NPABucketID(final long prv_NPABucketID) {
        this.prv_NPABucketID = prv_NPABucketID;
    }
    
    public BigDecimal getPrv_PrincipalDue() {
        return this.prv_PrincipalDue;
    }
    
    public void setPrv_PrincipalDue(final BigDecimal prv_PrincipalDue) {
        this.prv_PrincipalDue = prv_PrincipalDue;
    }
    
    public BigDecimal getPrv_ProfitDue() {
        return this.prv_ProfitDue;
    }
    
    public void setPrv_ProfitDue(final BigDecimal prv_ProfitDue) {
        this.prv_ProfitDue = prv_ProfitDue;
    }
    
    public BigDecimal getPrv_ProvisionAmtCal() {
        return this.prv_ProvisionAmtCal;
    }
    
    public void setPrv_ProvisionAmtCal(final BigDecimal prv_ProvisionAmtCal) {
        this.prv_ProvisionAmtCal = prv_ProvisionAmtCal;
    }
    
    public Boolean getFfd_FeeTypeIDFound() {
        return this.ffd_FeeTypeIDFound;
    }
    
    public void setFfd_FeeTypeIDFound(final Boolean ffd_FeeTypeIDFound) {
        this.ffd_FeeTypeIDFound = ffd_FeeTypeIDFound;
    }
    
    public BigDecimal getFfd_TotalDisbCalFee() {
        return this.ffd_TotalDisbCalFee;
    }
    
    public void setFfd_TotalDisbCalFee(final BigDecimal ffd_TotalDisbCalFee) {
        this.ffd_TotalDisbCalFee = ffd_TotalDisbCalFee;
    }
    
    public BigDecimal getFfd_TotalActualFee() {
        return this.ffd_TotalActualFee;
    }
    
    public void setFfd_TotalActualFee(final BigDecimal ffd_TotalActualFee) {
        this.ffd_TotalActualFee = ffd_TotalActualFee;
    }
    
    public BigDecimal getFfd_TotalPaidFee() {
        return this.ffd_TotalPaidFee;
    }
    
    public void setFfd_TotalPaidFee(final BigDecimal ffd_TotalPaidFee) {
        this.ffd_TotalPaidFee = ffd_TotalPaidFee;
    }
    
    public BigDecimal getFea_Amount() {
        return this.fea_Amount;
    }
    
    public void setFea_Amount(final BigDecimal fea_Amount) {
        this.fea_Amount = fea_Amount;
    }
    
    public BigDecimal getFea_UtilizedAmount() {
        return this.fea_UtilizedAmount;
    }
    
    public void setFea_UtilizedAmount(final BigDecimal fea_tilizedAmount) {
        this.fea_UtilizedAmount = fea_UtilizedAmount;
    }
    
    public BigDecimal getFea_BalanceAmount() {
        return this.fea_BalanceAmount;
    }
    
    public void setFea_BalanceAmount(final BigDecimal fea_BalanceAmount) {
        this.fea_BalanceAmount = fea_BalanceAmount;
    }
    
    public String getErrors() {
        return this.errors;
    }
    
    public void setErrors(final String errors) {
        this.errors = errors;
    }
    
    public String getWarnings() {
        return this.warnings;
    }
    
    public void setWarnings(final String warnings) {
        this.warnings = warnings;
    }
    
    public String getInformation() {
        return this.information;
    }
    
    public void setInformation(final String information) {
        this.information = information;
    }
    
    public static long getSerialversionuid() {
        return 1183720618731771888L;
    }
    
    public BigDecimal getFsd_CpzAmount() {
        return this.fsd_CpzAmount;
    }
    
    public void setFsd_CpzAmount(final BigDecimal fsd_CpzAmount) {
        this.fsd_CpzAmount = fsd_CpzAmount;
    }
    
    public boolean isFm_Cpz() {
        return this.fm_Cpz;
    }
    
    public void setFm_Cpz(final boolean fm_Cpz) {
        this.fm_Cpz = fm_Cpz;
    }
    
    public BigDecimal getFm_CpzAmount() {
        return this.fm_CpzAmount;
    }
    
    public void setFm_CpzAmount(final BigDecimal fm_CpzAmount) {
        this.fm_CpzAmount = fm_CpzAmount;
    }
    
    public BigDecimal getFm_FinAssetValue() {
        return this.fm_FinAssetValue;
    }
    
    public void setFm_FinAssetValue(final BigDecimal fm_FinAssetValue) {
        this.fm_FinAssetValue = fm_FinAssetValue;
    }
    
    public BigDecimal getFm_FinCurrAssetValue() {
        return this.fm_FinCurrAssetValue;
    }
    
    public void setFm_FinCurrAssetValue(final BigDecimal fm_FinCurrAssetValue) {
        this.fm_FinCurrAssetValue = fm_FinCurrAssetValue;
    }
    
    public BigDecimal getFdd_FirstDisbAmount() {
        return this.fdd_FirstDisbAmount;
    }
    
    public void setFdd_FirstDisbAmount(final BigDecimal fdd_FirstDisbAmount) {
        this.fdd_FirstDisbAmount = fdd_FirstDisbAmount;
    }
    
    public BigDecimal getFsd_FirstRepay() {
        return this.fsd_FirstRepay;
    }
    
    public void setFsd_FirstRepay(final BigDecimal fsd_FirstRepay) {
        this.fsd_FirstRepay = fsd_FirstRepay;
    }
    
    public BigDecimal getFsd_LastRepay() {
        return this.fsd_LastRepay;
    }
    
    public void setFsd_LastRepay(final BigDecimal fsd_LastRepay) {
        this.fsd_LastRepay = fsd_LastRepay;
    }
    
    public BigDecimal getRad_TotalAllocated() {
        return this.rad_TotalAllocated;
    }
    
    public void setRad_TotalAllocated(final BigDecimal rad_TotalAllocated) {
        this.rad_TotalAllocated = rad_TotalAllocated;
    }
    
    public BigDecimal getRph_TotalExcessAmount() {
        return this.rph_TotalExcessAmount;
    }
    
    public void setRph_TotalExcessAmount(final BigDecimal rph_TotalExcessAmount) {
        this.rph_TotalExcessAmount = rph_TotalExcessAmount;
    }
    
    public BigDecimal getRph_TotalAdvanceAmount() {
        return this.rph_TotalAdvanceAmount;
    }
    
    public void setRph_TotalAdvanceAmount(final BigDecimal rph_TotalAdvanceAmount) {
        this.rph_TotalAdvanceAmount = rph_TotalAdvanceAmount;
    }
    
    public BigDecimal getRcd_ExcessUtilised() {
        return this.rcd_ExcessUtilised;
    }
    
    public void setRcd_ExcessUtilised(final BigDecimal rcd_ExcessUtilised) {
        this.rcd_ExcessUtilised = rcd_ExcessUtilised;
    }
    
    public BigDecimal getRcd_AdvanceUtilised() {
        return this.rcd_AdvanceUtilised;
    }
    
    public void setRcd_AdvanceUtilised(final BigDecimal rcd_AdvanceUtilised) {
        this.rcd_AdvanceUtilised = rcd_AdvanceUtilised;
    }
    
    public BigDecimal getRsd_PriPaid() {
        return this.rsd_PriPaid;
    }
    
    public void setRsd_PriPaid(final BigDecimal rsd_PriPaid) {
        this.rsd_PriPaid = rsd_PriPaid;
    }
    
    public BigDecimal getRsd_PftPaid() {
        return this.rsd_PftPaid;
    }
    
    public void setRsd_PftPaid(final BigDecimal rsd_PftPaid) {
        this.rsd_PftPaid = rsd_PftPaid;
    }
    
    public BigDecimal getRsd_TdsPaid() {
        return this.rsd_TdsPaid;
    }
    
    public void setRsd_TdsPaid(final BigDecimal rsd_TdsPaid) {
        this.rsd_TdsPaid = rsd_TdsPaid;
    }
    
    public BigDecimal getRsd_LppPaid() {
        return this.rsd_LppPaid;
    }
    
    public void setRsd_LppPaid(final BigDecimal rsd_LppPaid) {
        this.rsd_LppPaid = rsd_LppPaid;
    }
    
    public BigDecimal getRsd_LpiPaid() {
        return this.rsd_LpiPaid;
    }
    
    public void setRsd_LpiPaid(final BigDecimal rsd_LpiPaid) {
        this.rsd_LpiPaid = rsd_LpiPaid;
    }
    
    public BigDecimal getRsd_PriWaived() {
        return this.rsd_PriWaived;
    }
    
    public void setRsd_PriWaived(final BigDecimal rsd_PriWaived) {
        this.rsd_PriWaived = rsd_PriWaived;
    }
    
    public BigDecimal getRsd_PftWaived() {
        return this.rsd_PftWaived;
    }
    
    public void setRsd_PftWaived(final BigDecimal rsd_PftWaived) {
        this.rsd_PftWaived = rsd_PftWaived;
    }
    
    public BigDecimal getRsd_LppWaived() {
        return this.rsd_LppWaived;
    }
    
    public void setRsd_LppWaived(final BigDecimal rsd_LppWaived) {
        this.rsd_LppWaived = rsd_LppWaived;
    }
    
    public BigDecimal getRsd_LpiWaived() {
        return this.rsd_LpiWaived;
    }
    
    public void setRsd_LpiWaived(final BigDecimal rsd_LpiWaived) {
        this.rsd_LpiWaived = rsd_LpiWaived;
    }
}