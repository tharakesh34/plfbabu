package com.pennant.datamigration.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.aspose.pdf.Operator.BI;

public class BasicLoanRecon implements Serializable {

	private static final long serialVersionUID = 1183720618731771888L;
	private String finReference;
	private String branch;
	private String fintype;
	private long custID = 0;
	private boolean graceExist = false;
	private boolean bpiExist = false;
	
	private BigDecimal srcSanctionedAmount = BigDecimal.ZERO;
	private BigDecimal srcDisbursedAmount = BigDecimal.ZERO;
	private BigDecimal srcUnDisbursedAmount = BigDecimal.ZERO;
	
	private BigDecimal sanctionedAmount = BigDecimal.ZERO;
	private BigDecimal disbursedAmount = BigDecimal.ZERO;
	private BigDecimal unDisbursedAmount = BigDecimal.ZERO;

	private BigDecimal srcEMISchd = BigDecimal.ZERO;
	private BigDecimal plfEMISchd = BigDecimal.ZERO;
	private BigDecimal difEMISchd = BigDecimal.ZERO;

	private BigDecimal srcIntSchd = BigDecimal.ZERO;
	private BigDecimal plfIntSchd = BigDecimal.ZERO;
	private BigDecimal difIntSchd = BigDecimal.ZERO;

	private BigDecimal srcPriSchd = BigDecimal.ZERO;
	private BigDecimal plfPriSchd = BigDecimal.ZERO;
	private BigDecimal difPriSchd = BigDecimal.ZERO;

	private BigDecimal srcEMIReceived = BigDecimal.ZERO;
	private BigDecimal plfEMIReceived = BigDecimal.ZERO;
	private BigDecimal difEMIReceived = BigDecimal.ZERO;

	private BigDecimal srcIntReceived = BigDecimal.ZERO;
	private BigDecimal plfIntReceived = BigDecimal.ZERO;
	private BigDecimal difIntReceived = BigDecimal.ZERO;

	private BigDecimal srcPriReceived = BigDecimal.ZERO;
	private BigDecimal plfPriReceived = BigDecimal.ZERO;
	private BigDecimal difPriReceived = BigDecimal.ZERO;

	private BigDecimal srcEMIPastDue = BigDecimal.ZERO;
	private BigDecimal plfEMIPastDue = BigDecimal.ZERO;
	private BigDecimal difEMIPastDue = BigDecimal.ZERO;

	private BigDecimal srcIntPastDue = BigDecimal.ZERO;
	private BigDecimal plfIntPastDue = BigDecimal.ZERO;
	private BigDecimal difIntPastDue = BigDecimal.ZERO;

	private BigDecimal srcPriPastDue = BigDecimal.ZERO;
	private BigDecimal plfPriPastDue = BigDecimal.ZERO;
	private BigDecimal difPriPastDue = BigDecimal.ZERO;

	private BigDecimal srcActiveLPPDue = BigDecimal.ZERO;
	private BigDecimal plfActiveLPPDue = BigDecimal.ZERO;
	private BigDecimal difActiveLPPDue = BigDecimal.ZERO;

	private String errors = "";
	private String warnings = "";
	private String information = "";

	public BigDecimal getSrcSanctionedAmount() {
		return srcSanctionedAmount;
	}

	public void setSrcSanctionedAmount(BigDecimal srcSanctionedAmount) {
		this.srcSanctionedAmount = srcSanctionedAmount;
	}

	public BigDecimal getSrcDisbursedAmount() {
		return srcDisbursedAmount;
	}

	public void setSrcDisbursedAmount(BigDecimal srcDisbursedAmount) {
		this.srcDisbursedAmount = srcDisbursedAmount;
	}

	public BigDecimal getSrcUnDisbursedAmount() {
		return srcUnDisbursedAmount;
	}

	public void setSrcUnDisbursedAmount(BigDecimal srcUnDisbursedAmount) {
		this.srcUnDisbursedAmount = srcUnDisbursedAmount;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getFintype() {
		return fintype;
	}

	public void setFintype(String fintype) {
		this.fintype = fintype;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public BigDecimal getSanctionedAmount() {
		return sanctionedAmount;
	}

	public void setSanctionedAmount(BigDecimal sanctionedAmount) {
		this.sanctionedAmount = sanctionedAmount;
	}

	public BigDecimal getDisbursedAmount() {
		return disbursedAmount;
	}

	public void setDisbursedAmount(BigDecimal disbursedAmount) {
		this.disbursedAmount = disbursedAmount;
	}

	public BigDecimal getUnDisbursedAmount() {
		return unDisbursedAmount;
	}

	public void setUnDisbursedAmount(BigDecimal unDisbursedAmount) {
		this.unDisbursedAmount = unDisbursedAmount;
	}

	public BigDecimal getSrcEMISchd() {
		return srcEMISchd;
	}

	public void setSrcEMISchd(BigDecimal srcEMISchd) {
		this.srcEMISchd = srcEMISchd;
	}

	public BigDecimal getPlfEMISchd() {
		return plfEMISchd;
	}

	public void setPlfEMISchd(BigDecimal plfEMISchd) {
		this.plfEMISchd = plfEMISchd;
	}

	public BigDecimal getDifEMISchd() {
		return difEMISchd;
	}

	public void setDifEMISchd(BigDecimal difEMISchd) {
		this.difEMISchd = difEMISchd;
	}

	public BigDecimal getSrcIntSchd() {
		return srcIntSchd;
	}

	public void setSrcIntSchd(BigDecimal srcIntSchd) {
		this.srcIntSchd = srcIntSchd;
	}

	public BigDecimal getPlfIntSchd() {
		return plfIntSchd;
	}

	public void setPlfIntSchd(BigDecimal plfIntSchd) {
		this.plfIntSchd = plfIntSchd;
	}

	public BigDecimal getDifIntSchd() {
		return difIntSchd;
	}

	public void setDifIntSchd(BigDecimal difIntSchd) {
		this.difIntSchd = difIntSchd;
	}

	public BigDecimal getSrcPriSchd() {
		return srcPriSchd;
	}

	public void setSrcPriSchd(BigDecimal srcPriSchd) {
		this.srcPriSchd = srcPriSchd;
	}

	public BigDecimal getPlfPriSchd() {
		return plfPriSchd;
	}

	public void setPlfPriSchd(BigDecimal plfPriSchd) {
		this.plfPriSchd = plfPriSchd;
	}

	public BigDecimal getDifPriSchd() {
		return difPriSchd;
	}

	public void setDifPriSchd(BigDecimal difPriSchd) {
		this.difPriSchd = difPriSchd;
	}

	public BigDecimal getSrcEMIReceived() {
		return srcEMIReceived;
	}

	public void setSrcEMIReceived(BigDecimal srcEMIReceived) {
		this.srcEMIReceived = srcEMIReceived;
	}

	public BigDecimal getPlfEMIReceived() {
		return plfEMIReceived;
	}

	public void setPlfEMIReceived(BigDecimal plfEMIReceived) {
		this.plfEMIReceived = plfEMIReceived;
	}

	public BigDecimal getDifEMIReceived() {
		return difEMIReceived;
	}

	public void setDifEMIReceived(BigDecimal difEMIReceived) {
		this.difEMIReceived = difEMIReceived;
	}

	public BigDecimal getSrcIntReceived() {
		return srcIntReceived;
	}

	public void setSrcIntReceived(BigDecimal srcIntReceived) {
		this.srcIntReceived = srcIntReceived;
	}

	public BigDecimal getPlfIntReceived() {
		return plfIntReceived;
	}

	public void setPlfIntReceived(BigDecimal plfIntReceived) {
		this.plfIntReceived = plfIntReceived;
	}

	public BigDecimal getDifIntReceived() {
		return difIntReceived;
	}

	public void setDifIntReceived(BigDecimal difIntReceived) {
		this.difIntReceived = difIntReceived;
	}

	public BigDecimal getSrcPriReceived() {
		return srcPriReceived;
	}

	public void setSrcPriReceived(BigDecimal srcPriReceived) {
		this.srcPriReceived = srcPriReceived;
	}

	public BigDecimal getPlfPriReceived() {
		return plfPriReceived;
	}

	public void setPlfPriReceived(BigDecimal plfPriReceived) {
		this.plfPriReceived = plfPriReceived;
	}

	public BigDecimal getDifPriReceived() {
		return difPriReceived;
	}

	public void setDifPriReceived(BigDecimal difPriReceived) {
		this.difPriReceived = difPriReceived;
	}

	public BigDecimal getSrcEMIPastDue() {
		return srcEMIPastDue;
	}

	public void setSrcEMIPastDue(BigDecimal srcEMIPastDue) {
		this.srcEMIPastDue = srcEMIPastDue;
	}

	public BigDecimal getPlfEMIPastDue() {
		return plfEMIPastDue;
	}

	public void setPlfEMIPastDue(BigDecimal plfEMIPastDue) {
		this.plfEMIPastDue = plfEMIPastDue;
	}

	public BigDecimal getDifEMIPastDue() {
		return difEMIPastDue;
	}

	public void setDifEMIPastDue(BigDecimal difEMIPastDue) {
		this.difEMIPastDue = difEMIPastDue;
	}

	public BigDecimal getSrcIntPastDue() {
		return srcIntPastDue;
	}

	public void setSrcIntPastDue(BigDecimal srcIntPastDue) {
		this.srcIntPastDue = srcIntPastDue;
	}

	public BigDecimal getPlfIntPastDue() {
		return plfIntPastDue;
	}

	public void setPlfIntPastDue(BigDecimal plfIntPastDue) {
		this.plfIntPastDue = plfIntPastDue;
	}

	public BigDecimal getDifIntPastDue() {
		return difIntPastDue;
	}

	public void setDifIntPastDue(BigDecimal difIntPastDue) {
		this.difIntPastDue = difIntPastDue;
	}

	public BigDecimal getSrcPriPastDue() {
		return srcPriPastDue;
	}

	public void setSrcPriPastDue(BigDecimal srcPriPastDue) {
		this.srcPriPastDue = srcPriPastDue;
	}

	public BigDecimal getPlfPriPastDue() {
		return plfPriPastDue;
	}

	public void setPlfPriPastDue(BigDecimal plfPriPastDue) {
		this.plfPriPastDue = plfPriPastDue;
	}

	public BigDecimal getDifPriPastDue() {
		return difPriPastDue;
	}

	public void setDifPriPastDue(BigDecimal difPriPastDue) {
		this.difPriPastDue = difPriPastDue;
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

	public BigDecimal getSrcActiveLPPDue() {
		return srcActiveLPPDue;
	}

	public void setSrcActiveLPPDue(BigDecimal srcActiveLPPDue) {
		this.srcActiveLPPDue = srcActiveLPPDue;
	}

	public BigDecimal getPlfActiveLPPDue() {
		return plfActiveLPPDue;
	}

	public void setPlfActiveLPPDue(BigDecimal plfActiveLPPDue) {
		this.plfActiveLPPDue = plfActiveLPPDue;
	}

	public BigDecimal getDifActiveLPPDue() {
		return difActiveLPPDue;
	}

	public void setDifActiveLPPDue(BigDecimal difActiveLPPDue) {
		this.difActiveLPPDue = difActiveLPPDue;
	}

	public boolean isGraceExist() {
		return graceExist;
	}

	public void setGraceExist(boolean graceExit) {
		this.graceExist = graceExit;
	}

	public boolean isBpiExist() {
		return bpiExist;
	}

	public void setBpiExist(boolean bpiExist) {
		this.bpiExist = bpiExist;
	}

}
