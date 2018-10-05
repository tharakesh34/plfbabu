package com.pennant.datamigration.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.aspose.pdf.Operator.BI;

public class SourceDataSummary implements Serializable {

	private static final long serialVersionUID = 1183720618731771888L;
	private String fm_FinReference;
	private long fm_CustID = 0;
	private String fm_FinBranch;
	private int fm_NumberOfTerms = 0;
	private boolean fm_Cpz = false;
	private BigDecimal fm_RepayProfitRate = BigDecimal.ZERO;
	private BigDecimal fm_TotalGrossPft = BigDecimal.ZERO;
	private BigDecimal fm_CpzAmount = BigDecimal.ZERO;
	private BigDecimal fm_TotalRepayAmt = BigDecimal.ZERO;
	private BigDecimal fm_FirstRepay = BigDecimal.ZERO;
	private BigDecimal fm_LastRepay = BigDecimal.ZERO;
	private Date fm_FinStartDate;
	private BigDecimal fm_FinAmount = BigDecimal.ZERO;
	private BigDecimal fm_FinRepaymentAmount = BigDecimal.ZERO;
	private BigDecimal fm_FeeChargeAmt = BigDecimal.ZERO;
	private BigDecimal fm_FinAssetValue = BigDecimal.ZERO;
	private BigDecimal fm_FinCurrAssetValue = BigDecimal.ZERO;
	private Boolean fm_JointAc = false;
	private long fm_JointCustID = 0;
	private long fm_MandateID = 0;
	private Boolean fm_AlowBPI = false;
	private String fm_BPITreatment;
	private Boolean fm_AlwMultiDisb = false;
	private BigDecimal fm_BPIAmount = BigDecimal.ZERO;
	private BigDecimal fm_DeductFeeDisb = BigDecimal.ZERO;
	private int fdd_TotDisbCount = 0;
	private BigDecimal fdd_TotDisbAmount = BigDecimal.ZERO;
	private BigDecimal fdd_TotFeeChargeAmt = BigDecimal.ZERO;
	private BigDecimal fdd_FirstDisbAmount = BigDecimal.ZERO;
	private int fap_TotInstructions = 0;
	private BigDecimal fap_TotInstructAmount = BigDecimal.ZERO;
	private BigDecimal fsd_ProfitSchd = BigDecimal.ZERO;
	private BigDecimal fsd_PrincipalSchd = BigDecimal.ZERO;
	private BigDecimal fsd_RepayAmount = BigDecimal.ZERO;
	private BigDecimal fsd_SchdPftPaid = BigDecimal.ZERO;
	private BigDecimal fsd_SchdPriPaid = BigDecimal.ZERO;
	private BigDecimal fsd_CpzAmount = BigDecimal.ZERO;
	private BigDecimal fsd_FirstRepay =  BigDecimal.ZERO;
	private BigDecimal fsd_LastRepay =  BigDecimal.ZERO;
	private BigDecimal rch_TotalReceiptAmount = BigDecimal.ZERO;
	private BigDecimal rch_TotalWaivedAmount = BigDecimal.ZERO;
	private BigDecimal rch_TotalFeeAmount = BigDecimal.ZERO;
	private Boolean rcd_ReceiptIDFound;
	private BigDecimal rcd_TotalReceiptAmount = BigDecimal.ZERO;
	private BigDecimal rad_TotalAllocated = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_NIA = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_PRIN = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_CHDIS = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_INT = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_TPF = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_IT = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_PF = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_ODC = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_FRE = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_DOCC = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_APF = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_GINS = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_EXS   = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_PAC = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_INS = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_RI = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_CS = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_CRS = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_INST = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_ADMIN = BigDecimal.ZERO;
	private BigDecimal rad_TOTPAID_SC = BigDecimal.ZERO;
	private BigDecimal rph_TotalRepayAmt = BigDecimal.ZERO;
	private BigDecimal rph_TotalPriAmount = BigDecimal.ZERO;
	private BigDecimal rph_TotalPftAmount = BigDecimal.ZERO;
	private Boolean rph_ReceiptSeqIDFound = false;
	private Boolean rph_RepayIDFound = false;
	private BigDecimal rpsd_ProfitSchd = BigDecimal.ZERO;
	private BigDecimal rpsd_ProfitSchdPaid = BigDecimal.ZERO;
	private BigDecimal rpsd_PrincipalSchd = BigDecimal.ZERO;
	private BigDecimal rpsd_PrincipalSchdPaid = BigDecimal.ZERO;
	private Boolean pd_PresentmentIDFound = false;
	private Boolean pd_MandateIDFound = false;
	private BigDecimal pd_TotalSchAmtDue = BigDecimal.ZERO;
	private BigDecimal pd_TotalPresentmentAmt = BigDecimal.ZERO;
	private Boolean pd_ReceiptIDFound = false;
	private Boolean ma_FeeTypeIDFound = false;
	private BigDecimal ma_TotalAdviseAmount = BigDecimal.ZERO;
	private BigDecimal ma_TotalPaidAmount = BigDecimal.ZERO;
	private Boolean ma_ReceiptIDFound = false;
	private Boolean mam_AdviseIDFound = false;
	private BigDecimal mam_TotalMovementAmount = BigDecimal.ZERO;
	private BigDecimal mam_TotalPaidAmount = BigDecimal.ZERO;
	private Boolean mam_ReceiptIDFound = false;
	private BigDecimal fod_TotalODAmount = BigDecimal.ZERO;
	private BigDecimal fod_TotalODPrincipal = BigDecimal.ZERO;
	private BigDecimal fod_TotalODProfit = BigDecimal.ZERO;
	private BigDecimal fod_TotalPenaltyAmount = BigDecimal.ZERO;
	private BigDecimal fod_TotalPenaltyPaid = BigDecimal.ZERO;
	private BigDecimal fod_TotalPenaltyBal = BigDecimal.ZERO;
	private int prv_DueDays = 0;
	private long prv_DPDBucketID = 0;
	private long prv_NPABucketID = 0;
	private BigDecimal prv_PrincipalDue = BigDecimal.ZERO;
	private BigDecimal prv_ProfitDue = BigDecimal.ZERO;
	private BigDecimal prv_ProvisionAmtCal = BigDecimal.ZERO;
	private Boolean ffd_FeeTypeIDFound = false;
	private BigDecimal ffd_TotalDisbCalFee = BigDecimal.ZERO;
	private BigDecimal ffd_TotalActualFee = BigDecimal.ZERO;
	private BigDecimal ffd_TotalPaidFee = BigDecimal.ZERO;
	private BigDecimal fea_Amount = BigDecimal.ZERO;
	private BigDecimal fea_UtilizedAmount = BigDecimal.ZERO;
	private BigDecimal fea_BalanceAmount = BigDecimal.ZERO;
	private String errors = "";
	private String warnings = "";
	private String information = "";
	
	public String getFm_FinReference() {
		return fm_FinReference;
	}

	public void setFm_FinReference(String fm_FinReference) {
		this.fm_FinReference = fm_FinReference;
	}

	public long getFm_CustID() {
		return fm_CustID;
	}

	public void setFm_CustID(long fm_CustID) {
		this.fm_CustID = fm_CustID;
	}

	public String getFm_FinBranch() {
		return fm_FinBranch;
	}

	public void setFm_FinBranch(String fm_FinBranch) {
		this.fm_FinBranch = fm_FinBranch;
	}

	public int getFm_NumberOfTerms() {
		return fm_NumberOfTerms;
	}

	public void setFm_NumberOfTerms(int fm_NumberOfTerms) {
		this.fm_NumberOfTerms = fm_NumberOfTerms;
	}

	public BigDecimal getFm_RepayProfitRate() {
		return fm_RepayProfitRate;
	}

	public void setFm_RepayProfitRate(BigDecimal fm_RepayProfitRate) {
		this.fm_RepayProfitRate = fm_RepayProfitRate;
	}

	public BigDecimal getFm_TotalGrossPft() {
		return fm_TotalGrossPft;
	}

	public void setFm_TotalGrossPft(BigDecimal fm_TotalGrossPft) {
		this.fm_TotalGrossPft = fm_TotalGrossPft;
	}

	public BigDecimal getFm_TotalRepayAmt() {
		return fm_TotalRepayAmt;
	}

	public void setFm_TotalRepayAmt(BigDecimal fm_TotalRepayAmt) {
		this.fm_TotalRepayAmt = fm_TotalRepayAmt;
	}

	public BigDecimal getFm_FirstRepay() {
		return fm_FirstRepay;
	}

	public void setFm_FirstRepay(BigDecimal fm_FirstRepay) {
		this.fm_FirstRepay = fm_FirstRepay;
	}

	public BigDecimal getFm_LastRepay() {
		return fm_LastRepay;
	}

	public void setFm_LastRepay(BigDecimal fm_LastRepay) {
		this.fm_LastRepay = fm_LastRepay;
	}

	public Date getFm_FinStartDate() {
		return fm_FinStartDate;
	}

	public void setFm_FinStartDate(Date fm_FinStartDate) {
		this.fm_FinStartDate = fm_FinStartDate;
	}

	public BigDecimal getFm_FinAmount() {
		return fm_FinAmount;
	}

	public void setFm_FinAmount(BigDecimal fm_FinAmount) {
		this.fm_FinAmount = fm_FinAmount;
	}

	public BigDecimal getFm_FinRepaymentAmount() {
		return fm_FinRepaymentAmount;
	}

	public void setFm_FinRepaymentAmount(BigDecimal fm_FinRepaymentAmount) {
		this.fm_FinRepaymentAmount = fm_FinRepaymentAmount;
	}

	public BigDecimal getFm_FeeChargeAmt() {
		return fm_FeeChargeAmt;
	}

	public void setFm_FeeChargeAmt(BigDecimal fm_FeeChargeAmt) {
		this.fm_FeeChargeAmt = fm_FeeChargeAmt;
	}

	public Boolean getFm_JointAc() {
		return fm_JointAc;
	}

	public void setFm_JointAc(Boolean fm_JointAc) {
		this.fm_JointAc = fm_JointAc;
	}

	public long getFm_JointCustID() {
		return fm_JointCustID;
	}

	public void setFm_JointCustID(long fm_JointCustID) {
		this.fm_JointCustID = fm_JointCustID;
	}

	public long getFm_MandateID() {
		return fm_MandateID;
	}

	public void setFm_MandateID(long fm_MandateID) {
		this.fm_MandateID = fm_MandateID;
	}

	public Boolean getFm_AlowBPI() {
		return fm_AlowBPI;
	}

	public void setFm_AlowBPI(Boolean fm_AlowBPI) {
		this.fm_AlowBPI = fm_AlowBPI;
	}

	public String getFm_BPITreatment() {
		return fm_BPITreatment;
	}

	public void setFm_BPITreatment(String fm_BPITreatment) {
		this.fm_BPITreatment = fm_BPITreatment;
	}

	public Boolean getFm_AlwMultiDisb() {
		return fm_AlwMultiDisb;
	}

	public void setFm_AlwMultiDisb(Boolean fm_AlwMultiDisb) {
		this.fm_AlwMultiDisb = fm_AlwMultiDisb;
	}

	public BigDecimal getFm_BPIAmount() {
		return fm_BPIAmount;
	}

	public void setFm_BPIAmount(BigDecimal fm_BPIAmount) {
		this.fm_BPIAmount = fm_BPIAmount;
	}

	public BigDecimal getFm_DeductFeeDisb() {
		return fm_DeductFeeDisb;
	}

	public void setFm_DeductFeeDisb(BigDecimal fm_DeductFeeDisb) {
		this.fm_DeductFeeDisb = fm_DeductFeeDisb;
	}

	public int getFdd_TotDisbCount() {
		return fdd_TotDisbCount;
	}

	public void setFdd_TotDisbCount(int fdd_TotDisbCount) {
		this.fdd_TotDisbCount = fdd_TotDisbCount;
	}

	public BigDecimal getFdd_TotDisbAmount() {
		return fdd_TotDisbAmount;
	}

	public void setFdd_TotDisbAmount(BigDecimal fdd_TotDisbAmount) {
		this.fdd_TotDisbAmount = fdd_TotDisbAmount;
	}

	public BigDecimal getFdd_TotFeeChargeAmt() {
		return fdd_TotFeeChargeAmt;
	}

	public void setFdd_TotFeeChargeAmt(BigDecimal fdd_TotFeeChargeAmt) {
		this.fdd_TotFeeChargeAmt = fdd_TotFeeChargeAmt;
	}

	public int getFap_TotInstructions() {
		return fap_TotInstructions;
	}

	public void setFap_TotInstructions(int fap_TotInstructions) {
		this.fap_TotInstructions = fap_TotInstructions;
	}

	public BigDecimal getFap_TotInstructAmount() {
		return fap_TotInstructAmount;
	}

	public void setFap_TotInstructAmount(BigDecimal fap_TotInstructAmount) {
		this.fap_TotInstructAmount = fap_TotInstructAmount;
	}

	public BigDecimal getFsd_ProfitSchd() {
		return fsd_ProfitSchd;
	}

	public void setFsd_ProfitSchd(BigDecimal fsd_ProfitSchd) {
		this.fsd_ProfitSchd = fsd_ProfitSchd;
	}

	public BigDecimal getFsd_PrincipalSchd() {
		return fsd_PrincipalSchd;
	}

	public void setFsd_PrincipalSchd(BigDecimal fsd_PrincipalSchd) {
		this.fsd_PrincipalSchd = fsd_PrincipalSchd;
	}

	public BigDecimal getFsd_RepayAmount() {
		return fsd_RepayAmount;
	}

	public void setFsd_RepayAmount(BigDecimal fsd_RepayAmount) {
		this.fsd_RepayAmount = fsd_RepayAmount;
	}

	public BigDecimal getFsd_SchdPftPaid() {
		return fsd_SchdPftPaid;
	}

	public void setFsd_SchdPftPaid(BigDecimal fsd_SchdPftPaid) {
		this.fsd_SchdPftPaid = fsd_SchdPftPaid;
	}

	public BigDecimal getFsd_SchdPriPaid() {
		return fsd_SchdPriPaid;
	}

	public void setFsd_SchdPriPaid(BigDecimal fsd_SchdPriPaid) {
		this.fsd_SchdPriPaid = fsd_SchdPriPaid;
	}

	public BigDecimal getRch_TotalReceiptAmount() {
		return rch_TotalReceiptAmount;
	}

	public void setRch_TotalReceiptAmount(BigDecimal rch_TotalReceiptAmount) {
		this.rch_TotalReceiptAmount = rch_TotalReceiptAmount;
	}

	public BigDecimal getRch_TotalWaivedAmount() {
		return rch_TotalWaivedAmount;
	}

	public void setRch_TotalWaivedAmount(BigDecimal rch_TotalWaivedAmount) {
		this.rch_TotalWaivedAmount = rch_TotalWaivedAmount;
	}

	public BigDecimal getRch_TotalFeeAmount() {
		return rch_TotalFeeAmount;
	}

	public void setRch_TotalFeeAmount(BigDecimal rch_TotalFeeAmount) {
		this.rch_TotalFeeAmount = rch_TotalFeeAmount;
	}

	public Boolean getRcd_ReceiptIDFound() {
		return rcd_ReceiptIDFound;
	}

	public void setRcd_ReceiptIDFound(Boolean rcd_ReceiptIDFound) {
		this.rcd_ReceiptIDFound = rcd_ReceiptIDFound;
	}

	public BigDecimal getRcd_TotalReceiptAmount() {
		return rcd_TotalReceiptAmount;
	}

	public void setRcd_TotalReceiptAmount(BigDecimal rcd_TotalReceiptAmount) {
		this.rcd_TotalReceiptAmount = rcd_TotalReceiptAmount;
	}

	public BigDecimal getRad_TOTPAID_NIA() {
		return rad_TOTPAID_NIA;
	}

	public void setRad_TOTPAID_NIA(BigDecimal rad_TOTPAID_NIA) {
		this.rad_TOTPAID_NIA = rad_TOTPAID_NIA;
	}

	public BigDecimal getRad_TOTPAID_PRIN() {
		return rad_TOTPAID_PRIN;
	}

	public void setRad_TOTPAID_PRIN(BigDecimal rad_TOTPAID_PRIN) {
		this.rad_TOTPAID_PRIN = rad_TOTPAID_PRIN;
	}

	public BigDecimal getRad_TOTPAID_CHDIS() {
		return rad_TOTPAID_CHDIS;
	}

	public void setRad_TOTPAID_CHDIS(BigDecimal rad_TOTPAID_CHDIS) {
		this.rad_TOTPAID_CHDIS = rad_TOTPAID_CHDIS;
	}

	public BigDecimal getRad_TOTPAID_INT() {
		return rad_TOTPAID_INT;
	}

	public void setRad_TOTPAID_INT(BigDecimal rad_TOTPAID_INT) {
		this.rad_TOTPAID_INT = rad_TOTPAID_INT;
	}

	public BigDecimal getRad_TOTPAID_TPF() {
		return rad_TOTPAID_TPF;
	}

	public void setRad_TOTPAID_TPF(BigDecimal rad_TOTPAID_TPF) {
		this.rad_TOTPAID_TPF = rad_TOTPAID_TPF;
	}

	public BigDecimal getRad_TOTPAID_IT() {
		return rad_TOTPAID_IT;
	}

	public void setRad_TOTPAID_IT(BigDecimal rad_TOTPAID_IT) {
		this.rad_TOTPAID_IT = rad_TOTPAID_IT;
	}

	public BigDecimal getRad_TOTPAID_PF() {
		return rad_TOTPAID_PF;
	}

	public void setRad_TOTPAID_PF(BigDecimal rad_TOTPAID_PF) {
		this.rad_TOTPAID_PF = rad_TOTPAID_PF;
	}

	public BigDecimal getRad_TOTPAID_ODC() {
		return rad_TOTPAID_ODC;
	}

	public void setRad_TOTPAID_ODC(BigDecimal rad_TOTPAID_ODC) {
		this.rad_TOTPAID_ODC = rad_TOTPAID_ODC;
	}

	public BigDecimal getRad_TOTPAID_FRE() {
		return rad_TOTPAID_FRE;
	}

	public void setRad_TOTPAID_FRE(BigDecimal rad_TOTPAID_FRE) {
		this.rad_TOTPAID_FRE = rad_TOTPAID_FRE;
	}

	public BigDecimal getRad_TOTPAID_DOCC() {
		return rad_TOTPAID_DOCC;
	}

	public void setRad_TOTPAID_DOCC(BigDecimal rad_TOTPAID_DOCC) {
		this.rad_TOTPAID_DOCC = rad_TOTPAID_DOCC;
	}

	public BigDecimal getRad_TOTPAID_APF() {
		return rad_TOTPAID_APF;
	}

	public void setRad_TOTPAID_APF(BigDecimal rad_TOTPAID_APF) {
		this.rad_TOTPAID_APF = rad_TOTPAID_APF;
	}

	public BigDecimal getRad_TOTPAID_GINS() {
		return rad_TOTPAID_GINS;
	}

	public void setRad_TOTPAID_GINS(BigDecimal rad_TOTPAID_GINS) {
		this.rad_TOTPAID_GINS = rad_TOTPAID_GINS;
	}

	public BigDecimal getRad_TOTPAID_EXS() {
		return rad_TOTPAID_EXS;
	}

	public void setRad_TOTPAID_EXS(BigDecimal rad_TOTPAID_EXS) {
		this.rad_TOTPAID_EXS = rad_TOTPAID_EXS;
	}

	public BigDecimal getRad_TOTPAID_PAC() {
		return rad_TOTPAID_PAC;
	}

	public void setRad_TOTPAID_PAC(BigDecimal rad_TOTPAID_PAC) {
		this.rad_TOTPAID_PAC = rad_TOTPAID_PAC;
	}

	public BigDecimal getRad_TOTPAID_INS() {
		return rad_TOTPAID_INS;
	}

	public void setRad_TOTPAID_INS(BigDecimal rad_TOTPAID_INS) {
		this.rad_TOTPAID_INS = rad_TOTPAID_INS;
	}

	public BigDecimal getRad_TOTPAID_RI() {
		return rad_TOTPAID_RI;
	}

	public void setRad_TOTPAID_RI(BigDecimal rad_TOTPAID_RI) {
		this.rad_TOTPAID_RI = rad_TOTPAID_RI;
	}

	public BigDecimal getRad_TOTPAID_CS() {
		return rad_TOTPAID_CS;
	}

	public void setRad_TOTPAID_CS(BigDecimal rad_TOTPAID_CS) {
		this.rad_TOTPAID_CS = rad_TOTPAID_CS;
	}

	public BigDecimal getRad_TOTPAID_CRS() {
		return rad_TOTPAID_CRS;
	}

	public void setRad_TOTPAID_CRS(BigDecimal rad_TOTPAID_CRS) {
		this.rad_TOTPAID_CRS = rad_TOTPAID_CRS;
	}

	public BigDecimal getRad_TOTPAID_INST() {
		return rad_TOTPAID_INST;
	}

	public void setRad_TOTPAID_INST(BigDecimal rad_TOTPAID_INST) {
		this.rad_TOTPAID_INST = rad_TOTPAID_INST;
	}

	public BigDecimal getRad_TOTPAID_ADMIN() {
		return rad_TOTPAID_ADMIN;
	}

	public void setRad_TOTPAID_ADMIN(BigDecimal rad_TOTPAID_ADMIN) {
		this.rad_TOTPAID_ADMIN = rad_TOTPAID_ADMIN;
	}

	public BigDecimal getRad_TOTPAID_SC() {
		return rad_TOTPAID_SC;
	}

	public void setRad_TOTPAID_SC(BigDecimal rad_TOTPAID_SC) {
		this.rad_TOTPAID_SC = rad_TOTPAID_SC;
	}

	public BigDecimal getRph_TotalRepayAmt() {
		return rph_TotalRepayAmt;
	}

	public void setRph_TotalRepayAmt(BigDecimal rph_TotalRepayAmt) {
		this.rph_TotalRepayAmt = rph_TotalRepayAmt;
	}

	public BigDecimal getRph_TotalPriAmount() {
		return rph_TotalPriAmount;
	}

	public void setRph_TotalPriAmount(BigDecimal rph_TotalPriAmount) {
		this.rph_TotalPriAmount = rph_TotalPriAmount;
	}

	public BigDecimal getRph_TotalPftAmount() {
		return rph_TotalPftAmount;
	}

	public void setRph_TotalPftAmount(BigDecimal rph_TotalPftAmount) {
		this.rph_TotalPftAmount = rph_TotalPftAmount;
	}

	public Boolean getRph_ReceiptSeqIDFound() {
		return rph_ReceiptSeqIDFound;
	}

	public void setRph_ReceiptSeqIDFound(Boolean rph_ReceiptSeqIDFound) {
		this.rph_ReceiptSeqIDFound = rph_ReceiptSeqIDFound;
	}

	public Boolean getRph_RepayIDFound() {
		return rph_RepayIDFound;
	}

	public void setRph_RepayIDFound(Boolean rph_RepayIDFound) {
		this.rph_RepayIDFound = rph_RepayIDFound;
	}

	public BigDecimal getRpsd_ProfitSchd() {
		return rpsd_ProfitSchd;
	}

	public void setRpsd_ProfitSchd(BigDecimal rpsd_ProfitSchd) {
		this.rpsd_ProfitSchd = rpsd_ProfitSchd;
	}

	public BigDecimal getRpsd_ProfitSchdPaid() {
		return rpsd_ProfitSchdPaid;
	}

	public void setRpsd_ProfitSchdPaid(BigDecimal rpsd_ProfitSchdPaid) {
		this.rpsd_ProfitSchdPaid = rpsd_ProfitSchdPaid;
	}

	public BigDecimal getRpsd_PrincipalSchd() {
		return rpsd_PrincipalSchd;
	}

	public void setRpsd_PrincipalSchd(BigDecimal rpsd_PrincipalSchd) {
		this.rpsd_PrincipalSchd = rpsd_PrincipalSchd;
	}

	public BigDecimal getRpsd_PrincipalSchdPaid() {
		return rpsd_PrincipalSchdPaid;
	}

	public void setRpsd_PrincipalSchdPaid(BigDecimal rpsd_PrincipalSchdPaid) {
		this.rpsd_PrincipalSchdPaid = rpsd_PrincipalSchdPaid;
	}

	public Boolean getPd_PresentmentIDFound() {
		return pd_PresentmentIDFound;
	}

	public void setPd_PresentmentIDFound(Boolean pd_PresentmentIDFound) {
		this.pd_PresentmentIDFound = pd_PresentmentIDFound;
	}

	public Boolean getPd_MandateIDFound() {
		return pd_MandateIDFound;
	}

	public void setPd_MandateIDFound(Boolean pd_MandateIDFound) {
		this.pd_MandateIDFound = pd_MandateIDFound;
	}

	public BigDecimal getPd_TotalSchAmtDue() {
		return pd_TotalSchAmtDue;
	}

	public void setPd_TotalSchAmtDue(BigDecimal pd_TotalSchAmtDue) {
		this.pd_TotalSchAmtDue = pd_TotalSchAmtDue;
	}

	public BigDecimal getPd_TotalPresentmentAmt() {
		return pd_TotalPresentmentAmt;
	}

	public void setPd_TotalPresentmentAmt(BigDecimal pd_TotalPresentmentAmt) {
		this.pd_TotalPresentmentAmt = pd_TotalPresentmentAmt;
	}

	public Boolean getPd_ReceiptIDFound() {
		return pd_ReceiptIDFound;
	}

	public void setPd_ReceiptIDFound(Boolean pd_ReceiptIDFound) {
		this.pd_ReceiptIDFound = pd_ReceiptIDFound;
	}

	public Boolean getMa_FeeTypeIDFound() {
		return ma_FeeTypeIDFound;
	}

	public void setMa_FeeTypeIDFound(Boolean ma_FeeTypeIDFound) {
		this.ma_FeeTypeIDFound = ma_FeeTypeIDFound;
	}

	public BigDecimal getMa_TotalAdviseAmount() {
		return ma_TotalAdviseAmount;
	}

	public void setMa_TotalAdviseAmount(BigDecimal ma_TotalAdviseAmount) {
		this.ma_TotalAdviseAmount = ma_TotalAdviseAmount;
	}

	public BigDecimal getMa_TotalPaidAmount() {
		return ma_TotalPaidAmount;
	}

	public void setMa_TotalPaidAmount(BigDecimal ma_TotalPaidAmount) {
		this.ma_TotalPaidAmount = ma_TotalPaidAmount;
	}

	public Boolean getMa_ReceiptIDFound() {
		return ma_ReceiptIDFound;
	}

	public void setMa_ReceiptIDFound(Boolean ma_ReceiptIDFound) {
		this.ma_ReceiptIDFound = ma_ReceiptIDFound;
	}

	public Boolean getMam_AdviseIDFound() {
		return mam_AdviseIDFound;
	}

	public void setMam_AdviseIDFound(Boolean mam_AdviseIDFound) {
		this.mam_AdviseIDFound = mam_AdviseIDFound;
	}

	public BigDecimal getMam_TotalMovementAmount() {
		return mam_TotalMovementAmount;
	}

	public void setMam_TotalMovementAmount(BigDecimal mam_TotalMovementAmount) {
		this.mam_TotalMovementAmount = mam_TotalMovementAmount;
	}

	public BigDecimal getMam_TotalPaidAmount() {
		return mam_TotalPaidAmount;
	}

	public void setMam_TotalPaidAmount(BigDecimal mam_TotalPaidAmount) {
		this.mam_TotalPaidAmount = mam_TotalPaidAmount;
	}

	public Boolean getMam_ReceiptIDFound() {
		return mam_ReceiptIDFound;
	}

	public void setMam_ReceiptIDFound(Boolean mam_ReceiptIDFound) {
		this.mam_ReceiptIDFound = mam_ReceiptIDFound;
	}

	public BigDecimal getFod_TotalODAmount() {
		return fod_TotalODAmount;
	}

	public void setFod_TotalODAmount(BigDecimal fod_TotalODAmount) {
		this.fod_TotalODAmount = fod_TotalODAmount;
	}

	public BigDecimal getFod_TotalODPrincipal() {
		return fod_TotalODPrincipal;
	}

	public void setFod_TotalODPrincipal(BigDecimal fod_TotalODPrincipal) {
		this.fod_TotalODPrincipal = fod_TotalODPrincipal;
	}

	public BigDecimal getFod_TotalODProfit() {
		return fod_TotalODProfit;
	}

	public void setFod_TotalODProfit(BigDecimal fod_TotalODProfit) {
		this.fod_TotalODProfit = fod_TotalODProfit;
	}

	public BigDecimal getFod_TotalPenaltyAmount() {
		return fod_TotalPenaltyAmount;
	}

	public void setFod_TotalPenaltyAmount(BigDecimal fod_TotalPenaltyAmount) {
		this.fod_TotalPenaltyAmount = fod_TotalPenaltyAmount;
	}

	public BigDecimal getFod_TotalPenaltyPaid() {
		return fod_TotalPenaltyPaid;
	}

	public void setFod_TotalPenaltyPaid(BigDecimal fod_TotalPenaltyPaid) {
		this.fod_TotalPenaltyPaid = fod_TotalPenaltyPaid;
	}

	public BigDecimal getFod_TotalPenaltyBal() {
		return fod_TotalPenaltyBal;
	}

	public void setFod_TotalPenaltyBal(BigDecimal fod_TotalPenaltyBal) {
		this.fod_TotalPenaltyBal = fod_TotalPenaltyBal;
	}

	public int getPrv_DueDays() {
		return prv_DueDays;
	}

	public void setPrv_DueDays(int prv_DueDays) {
		this.prv_DueDays = prv_DueDays;
	}

	public long getPrv_DPDBucketID() {
		return prv_DPDBucketID;
	}

	public void setPrv_DPDBucketID(long prv_DPDBucketID) {
		this.prv_DPDBucketID = prv_DPDBucketID;
	}

	public long getPrv_NPABucketID() {
		return prv_NPABucketID;
	}

	public void setPrv_NPABucketID(long prv_NPABucketID) {
		this.prv_NPABucketID = prv_NPABucketID;
	}

	public BigDecimal getPrv_PrincipalDue() {
		return prv_PrincipalDue;
	}

	public void setPrv_PrincipalDue(BigDecimal prv_PrincipalDue) {
		this.prv_PrincipalDue = prv_PrincipalDue;
	}

	public BigDecimal getPrv_ProfitDue() {
		return prv_ProfitDue;
	}

	public void setPrv_ProfitDue(BigDecimal prv_ProfitDue) {
		this.prv_ProfitDue = prv_ProfitDue;
	}

	public BigDecimal getPrv_ProvisionAmtCal() {
		return prv_ProvisionAmtCal;
	}

	public void setPrv_ProvisionAmtCal(BigDecimal prv_ProvisionAmtCal) {
		this.prv_ProvisionAmtCal = prv_ProvisionAmtCal;
	}

	public Boolean getFfd_FeeTypeIDFound() {
		return ffd_FeeTypeIDFound;
	}

	public void setFfd_FeeTypeIDFound(Boolean ffd_FeeTypeIDFound) {
		this.ffd_FeeTypeIDFound = ffd_FeeTypeIDFound;
	}

	public BigDecimal getFfd_TotalDisbCalFee() {
		return ffd_TotalDisbCalFee;
	}

	public void setFfd_TotalDisbCalFee(BigDecimal ffd_TotalDisbCalFee) {
		this.ffd_TotalDisbCalFee = ffd_TotalDisbCalFee;
	}

	public BigDecimal getFfd_TotalActualFee() {
		return ffd_TotalActualFee;
	}

	public void setFfd_TotalActualFee(BigDecimal ffd_TotalActualFee) {
		this.ffd_TotalActualFee = ffd_TotalActualFee;
	}

	public BigDecimal getFfd_TotalPaidFee() {
		return ffd_TotalPaidFee;
	}

	public void setFfd_TotalPaidFee(BigDecimal ffd_TotalPaidFee) {
		this.ffd_TotalPaidFee = ffd_TotalPaidFee;
	}

	public BigDecimal getFea_Amount() {
		return fea_Amount;
	}

	public void setFea_Amount(BigDecimal fea_Amount) {
		this.fea_Amount = fea_Amount;
	}

	public BigDecimal getFea_UtilizedAmount() {
		return fea_UtilizedAmount;
	}

	public void setFea_UtilizedAmount(BigDecimal fea_UtilizedAmount) {
		this.fea_UtilizedAmount = fea_UtilizedAmount;
	}

	public BigDecimal getFea_BalanceAmount() {
		return fea_BalanceAmount;
	}

	public void setFea_BalanceAmount(BigDecimal fea_BalanceAmount) {
		this.fea_BalanceAmount = fea_BalanceAmount;
	}

	public String getErrors() {
		return errors;
	}

	public void setErrors(String errors) {
		this.errors = errors;
	}

	public String getWarnings() {
		return warnings;
	}

	public void setWarnings(String warnings) {
		this.warnings = warnings;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public BigDecimal getFsd_CpzAmount() {
		return fsd_CpzAmount;
	}

	public void setFsd_CpzAmount(BigDecimal fsd_CpzAmount) {
		this.fsd_CpzAmount = fsd_CpzAmount;
	}

	public boolean isFm_Cpz() {
		return fm_Cpz;
	}

	public void setFm_Cpz(boolean fm_Cpz) {
		this.fm_Cpz = fm_Cpz;
	}

	public BigDecimal getFm_CpzAmount() {
		return fm_CpzAmount;
	}

	public void setFm_CpzAmount(BigDecimal fm_CpzAmount) {
		this.fm_CpzAmount = fm_CpzAmount;
	}

	public BigDecimal getFm_FinAssetValue() {
		return fm_FinAssetValue;
	}

	public void setFm_FinAssetValue(BigDecimal fm_FinAssetValue) {
		this.fm_FinAssetValue = fm_FinAssetValue;
	}

	public BigDecimal getFm_FinCurrAssetValue() {
		return fm_FinCurrAssetValue;
	}

	public void setFm_FinCurrAssetValue(BigDecimal fm_FinCurrAssetValue) {
		this.fm_FinCurrAssetValue = fm_FinCurrAssetValue;
	}

	public BigDecimal getFdd_FirstDisbAmount() {
		return fdd_FirstDisbAmount;
	}

	public void setFdd_FirstDisbAmount(BigDecimal fdd_FirstDisbAmount) {
		this.fdd_FirstDisbAmount = fdd_FirstDisbAmount;
	}

	public BigDecimal getFsd_FirstRepay() {
		return fsd_FirstRepay;
	}

	public void setFsd_FirstRepay(BigDecimal fsd_FirstRepay) {
		this.fsd_FirstRepay = fsd_FirstRepay;
	}

	public BigDecimal getFsd_LastRepay() {
		return fsd_LastRepay;
	}

	public void setFsd_LastRepay(BigDecimal fsd_LastRepay) {
		this.fsd_LastRepay = fsd_LastRepay;
	}

	public BigDecimal getRad_TotalAllocated() {
		return rad_TotalAllocated;
	}

	public void setRad_TotalAllocated(BigDecimal rad_TotalAllocated) {
		this.rad_TotalAllocated = rad_TotalAllocated;
	}

}
