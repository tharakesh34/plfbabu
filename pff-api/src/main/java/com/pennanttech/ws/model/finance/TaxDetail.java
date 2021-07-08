package com.pennanttech.ws.model.finance;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;

@XmlType(propOrder = { "gstType", "adviseAmount", "netCGST", "netSGST", "netIGST", "netUGST", "netTGST", "total" })
@XmlRootElement(name = "FinTaxDetails")
@XmlAccessorType(XmlAccessType.NONE)
public class TaxDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	//NET GST
	@XmlElement
	private BigDecimal netCGST = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal netIGST = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal netUGST = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal netSGST = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal netCESS = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal netTGST = BigDecimal.ZERO;

	//API Specific Field
	@XmlElement
	private BigDecimal adviseAmount = BigDecimal.ZERO;
	@XmlElement
	private BigDecimal total = BigDecimal.ZERO;
	@XmlElement
	private String gstType;

	public TaxDetail() {
		super();
	}

	public BigDecimal getNetCGST() {
		return netCGST;
	}

	public void setNetCGST(BigDecimal netCGST) {
		this.netCGST = netCGST;
	}

	public BigDecimal getNetIGST() {
		return netIGST;
	}

	public void setNetIGST(BigDecimal netIGST) {
		this.netIGST = netIGST;
	}

	public BigDecimal getNetUGST() {
		return netUGST;
	}

	public void setNetUGST(BigDecimal netUGST) {
		this.netUGST = netUGST;
	}

	public BigDecimal getNetSGST() {
		return netSGST;
	}

	public void setNetSGST(BigDecimal netSGST) {
		this.netSGST = netSGST;
	}

	public BigDecimal getNetTGST() {
		return netTGST;
	}

	public void setNetTGST(BigDecimal netTGST) {
		this.netTGST = netTGST;
	}

	public BigDecimal getAdviseAmount() {
		return adviseAmount;
	}

	public void setAdviseAmount(BigDecimal adviseAmount) {
		this.adviseAmount = adviseAmount;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getGstType() {
		return gstType;
	}

	public void setGstType(String gstType) {
		this.gstType = gstType;
	}

	public BigDecimal getNetCESS() {
		return netCESS;
	}

	public void setNetCESS(BigDecimal netCESS) {
		this.netCESS = netCESS;
	}

}
