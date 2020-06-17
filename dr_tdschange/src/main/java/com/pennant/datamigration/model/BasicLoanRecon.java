package com.pennant.datamigration.model;

import java.io.*;
import java.math.*;

public class BasicLoanRecon implements Serializable
{
    private static final long serialVersionUID = 1183720618731771888L;
    private String finReference;
    private String branch;
    private String fintype;
    private long custID;
    private boolean graceExist;
    private boolean bpiExist;
    private BigDecimal srcSanctionedAmount;
    private BigDecimal srcDisbursedAmount;
    private BigDecimal srcUnDisbursedAmount;
    private BigDecimal sanctionedAmount;
    private BigDecimal disbursedAmount;
    private BigDecimal unDisbursedAmount;
    private BigDecimal srcEMISchd;
    private BigDecimal plfEMISchd;
    private BigDecimal difEMISchd;
    private BigDecimal srcIntSchd;
    private BigDecimal plfIntSchd;
    private BigDecimal difIntSchd;
    private BigDecimal srcPriSchd;
    private BigDecimal plfPriSchd;
    private BigDecimal plfCpz;
    private BigDecimal difPriSchd;
    private BigDecimal srcEMIReceived;
    private BigDecimal plfEMIReceived;
    private BigDecimal difEMIReceived;
    private BigDecimal srcIntReceived;
    private BigDecimal plfIntReceived;
    private BigDecimal difIntReceived;
    private BigDecimal srcPriReceived;
    private BigDecimal plfPriReceived;
    private BigDecimal difPriReceived;
    private BigDecimal srcEMIPastDue;
    private BigDecimal plfEMIPastDue;
    private BigDecimal difEMIPastDue;
    private BigDecimal srcIntPastDue;
    private BigDecimal plfIntPastDue;
    private BigDecimal difIntPastDue;
    private BigDecimal srcPriPastDue;
    private BigDecimal plfPriPastDue;
    private BigDecimal difPriPastDue;
    private BigDecimal srcActiveLPPDue;
    private BigDecimal plfActiveLPPDue;
    private BigDecimal difActiveLPPDue;
    private String errors;
    private String warnings;
    private String information;
    private BigDecimal firstIntDiff;
    private BigDecimal lastIntAdjusted;
    private BigDecimal pastIntReset;
    private BigDecimal posDifference;
    private int reconStatus;
    private BigDecimal srcDtaIntSchd;
    private BigDecimal srcDtaPriSchd;
    private BigDecimal srcDtaIntRcv;
    private BigDecimal srcDtaPriRcv;
    private BigDecimal srcDtaIntDue;
    private BigDecimal srcDtaPriDue;
    private boolean ucLoan;
    private boolean ucSchdBuild;
    private boolean inQDP;
    
    public BasicLoanRecon() {
        this.custID = 0L;
        this.graceExist = false;
        this.bpiExist = false;
        this.srcSanctionedAmount = BigDecimal.ZERO;
        this.srcDisbursedAmount = BigDecimal.ZERO;
        this.srcUnDisbursedAmount = BigDecimal.ZERO;
        this.sanctionedAmount = BigDecimal.ZERO;
        this.disbursedAmount = BigDecimal.ZERO;
        this.unDisbursedAmount = BigDecimal.ZERO;
        this.srcEMISchd = BigDecimal.ZERO;
        this.plfEMISchd = BigDecimal.ZERO;
        this.difEMISchd = BigDecimal.ZERO;
        this.srcIntSchd = BigDecimal.ZERO;
        this.plfIntSchd = BigDecimal.ZERO;
        this.difIntSchd = BigDecimal.ZERO;
        this.srcPriSchd = BigDecimal.ZERO;
        this.plfPriSchd = BigDecimal.ZERO;
        this.plfCpz = BigDecimal.ZERO;
        this.difPriSchd = BigDecimal.ZERO;
        this.srcEMIReceived = BigDecimal.ZERO;
        this.plfEMIReceived = BigDecimal.ZERO;
        this.difEMIReceived = BigDecimal.ZERO;
        this.srcIntReceived = BigDecimal.ZERO;
        this.plfIntReceived = BigDecimal.ZERO;
        this.difIntReceived = BigDecimal.ZERO;
        this.srcPriReceived = BigDecimal.ZERO;
        this.plfPriReceived = BigDecimal.ZERO;
        this.difPriReceived = BigDecimal.ZERO;
        this.srcEMIPastDue = BigDecimal.ZERO;
        this.plfEMIPastDue = BigDecimal.ZERO;
        this.difEMIPastDue = BigDecimal.ZERO;
        this.srcIntPastDue = BigDecimal.ZERO;
        this.plfIntPastDue = BigDecimal.ZERO;
        this.difIntPastDue = BigDecimal.ZERO;
        this.srcPriPastDue = BigDecimal.ZERO;
        this.plfPriPastDue = BigDecimal.ZERO;
        this.difPriPastDue = BigDecimal.ZERO;
        this.srcActiveLPPDue = BigDecimal.ZERO;
        this.plfActiveLPPDue = BigDecimal.ZERO;
        this.difActiveLPPDue = BigDecimal.ZERO;
        this.errors = "";
        this.warnings = "";
        this.information = "";
        this.firstIntDiff = BigDecimal.ZERO;
        this.lastIntAdjusted = BigDecimal.ZERO;
        this.pastIntReset = BigDecimal.ZERO;
        this.posDifference = BigDecimal.ZERO;
        this.reconStatus = 0;
        this.srcDtaIntSchd = BigDecimal.ZERO;
        this.srcDtaPriSchd = BigDecimal.ZERO;
        this.srcDtaIntRcv = BigDecimal.ZERO;
        this.srcDtaPriRcv = BigDecimal.ZERO;
        this.srcDtaIntDue = BigDecimal.ZERO;
        this.srcDtaPriDue = BigDecimal.ZERO;
        this.ucLoan = false;
        this.ucSchdBuild = false;
        this.inQDP = false;
    }
    
    public BigDecimal getSrcSanctionedAmount() {
        return this.srcSanctionedAmount;
    }
    
    public void setSrcSanctionedAmount(final BigDecimal srcSanctionedAmount) {
        this.srcSanctionedAmount = srcSanctionedAmount;
    }
    
    public BigDecimal getSrcDisbursedAmount() {
        return this.srcDisbursedAmount;
    }
    
    public void setSrcDisbursedAmount(final BigDecimal srcDisbursedAmount) {
        this.srcDisbursedAmount = srcDisbursedAmount;
    }
    
    public BigDecimal getSrcUnDisbursedAmount() {
        return this.srcUnDisbursedAmount;
    }
    
    public void setSrcUnDisbursedAmount(final BigDecimal srcUnDisbursedAmount) {
        this.srcUnDisbursedAmount = srcUnDisbursedAmount;
    }
    
    public String getFinReference() {
        return this.finReference;
    }
    
    public void setFinReference(final String finReference) {
        this.finReference = finReference;
    }
    
    public String getBranch() {
        return this.branch;
    }
    
    public void setBranch(final String branch) {
        this.branch = branch;
    }
    
    public String getFintype() {
        return this.fintype;
    }
    
    public void setFintype(final String fintype) {
        this.fintype = fintype;
    }
    
    public long getCustID() {
        return this.custID;
    }
    
    public void setCustID(final long custID) {
        this.custID = custID;
    }
    
    public BigDecimal getSanctionedAmount() {
        return this.sanctionedAmount;
    }
    
    public void setSanctionedAmount(final BigDecimal sanctionedAmount) {
        this.sanctionedAmount = sanctionedAmount;
    }
    
    public BigDecimal getDisbursedAmount() {
        return this.disbursedAmount;
    }
    
    public void setDisbursedAmount(final BigDecimal disbursedAmount) {
        this.disbursedAmount = disbursedAmount;
    }
    
    public BigDecimal getUnDisbursedAmount() {
        return this.unDisbursedAmount;
    }
    
    public void setUnDisbursedAmount(final BigDecimal unDisbursedAmount) {
        this.unDisbursedAmount = unDisbursedAmount;
    }
    
    public BigDecimal getSrcEMISchd() {
        return this.srcEMISchd;
    }
    
    public void setSrcEMISchd(final BigDecimal srcEMISchd) {
        this.srcEMISchd = srcEMISchd;
    }
    
    public BigDecimal getPlfEMISchd() {
        return this.plfEMISchd;
    }
    
    public void setPlfEMISchd(final BigDecimal plfEMISchd) {
        this.plfEMISchd = plfEMISchd;
    }
    
    public BigDecimal getDifEMISchd() {
        return this.difEMISchd;
    }
    
    public void setDifEMISchd(final BigDecimal difEMISchd) {
        this.difEMISchd = difEMISchd;
    }
    
    public BigDecimal getSrcIntSchd() {
        return this.srcIntSchd;
    }
    
    public void setSrcIntSchd(final BigDecimal srcIntSchd) {
        this.srcIntSchd = srcIntSchd;
    }
    
    public BigDecimal getPlfIntSchd() {
        return this.plfIntSchd;
    }
    
    public void setPlfIntSchd(final BigDecimal plfIntSchd) {
        this.plfIntSchd = plfIntSchd;
    }
    
    public BigDecimal getDifIntSchd() {
        return this.difIntSchd;
    }
    
    public void setDifIntSchd(final BigDecimal difIntSchd) {
        this.difIntSchd = difIntSchd;
    }
    
    public BigDecimal getSrcPriSchd() {
        return this.srcPriSchd;
    }
    
    public void setSrcPriSchd(final BigDecimal srcPriSchd) {
        this.srcPriSchd = srcPriSchd;
    }
    
    public BigDecimal getPlfPriSchd() {
        return this.plfPriSchd;
    }
    
    public void setPlfPriSchd(final BigDecimal plfPriSchd) {
        this.plfPriSchd = plfPriSchd;
    }
    
    public BigDecimal getDifPriSchd() {
        return this.difPriSchd;
    }
    
    public void setDifPriSchd(final BigDecimal difPriSchd) {
        this.difPriSchd = difPriSchd;
    }
    
    public BigDecimal getSrcEMIReceived() {
        return this.srcEMIReceived;
    }
    
    public void setSrcEMIReceived(final BigDecimal srcEMIReceived) {
        this.srcEMIReceived = srcEMIReceived;
    }
    
    public BigDecimal getPlfEMIReceived() {
        return this.plfEMIReceived;
    }
    
    public void setPlfEMIReceived(final BigDecimal plfEMIReceived) {
        this.plfEMIReceived = plfEMIReceived;
    }
    
    public BigDecimal getDifEMIReceived() {
        return this.difEMIReceived;
    }
    
    public void setDifEMIReceived(final BigDecimal difEMIReceived) {
        this.difEMIReceived = difEMIReceived;
    }
    
    public BigDecimal getSrcIntReceived() {
        return this.srcIntReceived;
    }
    
    public void setSrcIntReceived(final BigDecimal srcIntReceived) {
        this.srcIntReceived = srcIntReceived;
    }
    
    public BigDecimal getPlfIntReceived() {
        return this.plfIntReceived;
    }
    
    public void setPlfIntReceived(final BigDecimal plfIntReceived) {
        this.plfIntReceived = plfIntReceived;
    }
    
    public BigDecimal getDifIntReceived() {
        return this.difIntReceived;
    }
    
    public void setDifIntReceived(final BigDecimal difIntReceived) {
        this.difIntReceived = difIntReceived;
    }
    
    public BigDecimal getSrcPriReceived() {
        return this.srcPriReceived;
    }
    
    public void setSrcPriReceived(final BigDecimal srcPriReceived) {
        this.srcPriReceived = srcPriReceived;
    }
    
    public BigDecimal getPlfPriReceived() {
        return this.plfPriReceived;
    }
    
    public void setPlfPriReceived(final BigDecimal plfPriReceived) {
        this.plfPriReceived = plfPriReceived;
    }
    
    public BigDecimal getDifPriReceived() {
        return this.difPriReceived;
    }
    
    public void setDifPriReceived(final BigDecimal difPriReceived) {
        this.difPriReceived = difPriReceived;
    }
    
    public BigDecimal getSrcEMIPastDue() {
        return this.srcEMIPastDue;
    }
    
    public void setSrcEMIPastDue(final BigDecimal srcEMIPastDue) {
        this.srcEMIPastDue = srcEMIPastDue;
    }
    
    public BigDecimal getPlfEMIPastDue() {
        return this.plfEMIPastDue;
    }
    
    public void setPlfEMIPastDue(final BigDecimal plfEMIPastDue) {
        this.plfEMIPastDue = plfEMIPastDue;
    }
    
    public BigDecimal getDifEMIPastDue() {
        return this.difEMIPastDue;
    }
    
    public void setDifEMIPastDue(final BigDecimal difEMIPastDue) {
        this.difEMIPastDue = difEMIPastDue;
    }
    
    public BigDecimal getSrcIntPastDue() {
        return this.srcIntPastDue;
    }
    
    public void setSrcIntPastDue(final BigDecimal srcIntPastDue) {
        this.srcIntPastDue = srcIntPastDue;
    }
    
    public BigDecimal getPlfIntPastDue() {
        return this.plfIntPastDue;
    }
    
    public void setPlfIntPastDue(final BigDecimal plfIntPastDue) {
        this.plfIntPastDue = plfIntPastDue;
    }
    
    public BigDecimal getDifIntPastDue() {
        return this.difIntPastDue;
    }
    
    public void setDifIntPastDue(final BigDecimal difIntPastDue) {
        this.difIntPastDue = difIntPastDue;
    }
    
    public BigDecimal getSrcPriPastDue() {
        return this.srcPriPastDue;
    }
    
    public void setSrcPriPastDue(final BigDecimal srcPriPastDue) {
        this.srcPriPastDue = srcPriPastDue;
    }
    
    public BigDecimal getPlfPriPastDue() {
        return this.plfPriPastDue;
    }
    
    public void setPlfPriPastDue(final BigDecimal plfPriPastDue) {
        this.plfPriPastDue = plfPriPastDue;
    }
    
    public BigDecimal getDifPriPastDue() {
        return this.difPriPastDue;
    }
    
    public void setDifPriPastDue(final BigDecimal difPriPastDue) {
        this.difPriPastDue = difPriPastDue;
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
    
    public BigDecimal getSrcActiveLPPDue() {
        return this.srcActiveLPPDue;
    }
    
    public void setSrcActiveLPPDue(final BigDecimal srcActiveLPPDue) {
        this.srcActiveLPPDue = srcActiveLPPDue;
    }
    
    public BigDecimal getPlfActiveLPPDue() {
        return this.plfActiveLPPDue;
    }
    
    public void setPlfActiveLPPDue(final BigDecimal plfActiveLPPDue) {
        this.plfActiveLPPDue = plfActiveLPPDue;
    }
    
    public BigDecimal getDifActiveLPPDue() {
        return this.difActiveLPPDue;
    }
    
    public void setDifActiveLPPDue(final BigDecimal difActiveLPPDue) {
        this.difActiveLPPDue = difActiveLPPDue;
    }
    
    public boolean isGraceExist() {
        return this.graceExist;
    }
    
    public void setGraceExist(final boolean graceExit) {
        this.graceExist = graceExit;
    }
    
    public boolean isBpiExist() {
        return this.bpiExist;
    }
    
    public void setBpiExist(final boolean bpiExist) {
        this.bpiExist = bpiExist;
    }
    
    public BigDecimal getPlfCpz() {
        return this.plfCpz;
    }
    
    public void setPlfCpz(final BigDecimal plfCpz) {
        this.plfCpz = plfCpz;
    }
    
    public BigDecimal getFirstIntDiff() {
        return this.firstIntDiff;
    }
    
    public void setFirstIntDiff(final BigDecimal firstIntDiff) {
        this.firstIntDiff = firstIntDiff;
    }
    
    public BigDecimal getLastIntAdjusted() {
        return this.lastIntAdjusted;
    }
    
    public void setLastIntAdjusted(final BigDecimal lastIntAdjusted) {
        this.lastIntAdjusted = lastIntAdjusted;
    }
    
    public BigDecimal getPastIntReset() {
        return this.pastIntReset;
    }
    
    public void setPastIntReset(final BigDecimal pastIntReset) {
        this.pastIntReset = pastIntReset;
    }
    
    public BigDecimal getPosDifference() {
        return this.posDifference;
    }
    
    public void setPosDifference(final BigDecimal posDifference) {
        this.posDifference = posDifference;
    }
    
    public int getReconStatus() {
        return this.reconStatus;
    }
    
    public void setReconStatus(final int reconStatus) {
        this.reconStatus = reconStatus;
    }
    
    public BigDecimal getSrcDtaIntSchd() {
        return this.srcDtaIntSchd;
    }
    
    public void setSrcDtaIntSchd(final BigDecimal srcDtaIntSchd) {
        this.srcDtaIntSchd = srcDtaIntSchd;
    }
    
    public BigDecimal getSrcDtaPriSchd() {
        return this.srcDtaPriSchd;
    }
    
    public void setSrcDtaPriSchd(final BigDecimal srcDtaPriSchd) {
        this.srcDtaPriSchd = srcDtaPriSchd;
    }
    
    public BigDecimal getSrcDtaIntRcv() {
        return this.srcDtaIntRcv;
    }
    
    public void setSrcDtaIntRcv(final BigDecimal srcDtaIntRcv) {
        this.srcDtaIntRcv = srcDtaIntRcv;
    }
    
    public BigDecimal getSrcDtaPriRcv() {
        return this.srcDtaPriRcv;
    }
    
    public void setSrcDtaPriRcv(final BigDecimal srcDtaPriRcv) {
        this.srcDtaPriRcv = srcDtaPriRcv;
    }
    
    public boolean isUcLoan() {
        return this.ucLoan;
    }
    
    public void setUcLoan(final boolean ucLoan) {
        this.ucLoan = ucLoan;
    }
    
    public boolean isUcSchdBuild() {
        return this.ucSchdBuild;
    }
    
    public void setUcSchdBuild(final boolean ucSchdBuild) {
        this.ucSchdBuild = ucSchdBuild;
    }
    
    public boolean isInQDP() {
        return this.inQDP;
    }
    
    public void setInQDP(final boolean inQDP) {
        this.inQDP = inQDP;
    }
    
    public BigDecimal getSrcDtaIntDue() {
        return this.srcDtaIntDue;
    }
    
    public void setSrcDtaIntDue(final BigDecimal srcDtaIntDue) {
        this.srcDtaIntDue = srcDtaIntDue;
    }
    
    public BigDecimal getSrcDtaPriDue() {
        return this.srcDtaPriDue;
    }
    
    public void setSrcDtaPriDue(final BigDecimal srcDtaPriDue) {
        this.srcDtaPriDue = srcDtaPriDue;
    }
}