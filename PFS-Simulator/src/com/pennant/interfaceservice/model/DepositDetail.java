package com.pennant.interfaceservice.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "DepositDetails")
public class DepositDetail {

	private String depositID;
	private BigDecimal insAmount = BigDecimal.ZERO;
	private Date blockingDate;
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	@XmlElement(name = "DepositID")
	public String getDepositID() {
		return depositID;
	}

	public void setDepositID(String depositID) {
		this.depositID = depositID;
	}

	@XmlElement(name = "InsAmount")
	public BigDecimal getInsAmount() {
		return insAmount;
	}

	public void setInsAmount(BigDecimal insAmount) {
		this.insAmount = insAmount;
	}

	@XmlElement(name = "BlockingDate")
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Date getBlockingDate() {
		return blockingDate;
	}
	
	
	 private static class DateFormatterAdapter extends XmlAdapter<String, Date> {
	        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");

	        @Override
	        public Date unmarshal(final String v) throws Exception {
	            return dateFormat.parse(v);
	        }

	        @Override
	        public String marshal(final Date v) throws Exception {
	            return dateFormat.format(v);
	        }
	    }

	public void setBlockingDate(Date blockingDate) {
		this.blockingDate = blockingDate;
	}

}
