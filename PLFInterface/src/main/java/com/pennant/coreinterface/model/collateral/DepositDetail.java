package com.pennant.coreinterface.model.collateral;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pennant.mq.util.InterfaceMasterConfigUtil;

@XmlRootElement(name = "DepositDetails ")
public class DepositDetail implements Serializable {

	private static final long serialVersionUID = -1095187366632375060L;
	
	private String depositID;
	private BigDecimal insAmount = BigDecimal.ZERO;
	private Date blockingDate;
	private String reason;
	
	public DepositDetail() {
		
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
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
	
	public void setBlockingDate(Date blockingDate) {
		this.blockingDate = blockingDate;
	}

	@XmlElement(name = "Reason")
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	private static class DateFormatterAdapter extends XmlAdapter<String, Date> {
		private final SimpleDateFormat dateFormat = new SimpleDateFormat(InterfaceMasterConfigUtil.SHORT_DATE);

		@Override
		public Date unmarshal(final String v) throws Exception {
			return dateFormat.parse(v);
		}

		@Override
		public String marshal(final Date v) throws Exception {
			return dateFormat.format(v);
		}
	}

}
