package com.pennant.interfaceservice.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "AccountDetails")
public class AccountDetail {

	private String accNum;
	private String description;
	private BigDecimal insAmount = BigDecimal.ZERO;
	private Date blockingDate;

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++ getter / setter +++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++ //

	@XmlElement(name = "AccNum")
	public String getAccNum() {
		return accNum;
	}

	public void setAccNum(String accNum) {
		this.accNum = accNum;
	}

	@XmlElement(name = "Description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

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
