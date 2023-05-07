package com.pennant.pff.noc.upload.model;

import java.util.Date;

import com.pennant.pff.upload.model.UploadDetails;

public class CourierDetailUpload extends UploadDetails {
	private static final long serialVersionUID = 1L;

	private String letterType;
	private Date letterDate;
	private Date dispatchDate;
	private String courierAgency;
	private String deliveryStatus;
	private Date deliveryDate;

	public CourierDetailUpload() {
		super();
	}

	public String getLetterType() {
		return letterType;
	}

	public void setLetterType(String letterType) {
		this.letterType = letterType;
	}

	public Date getLetterDate() {
		return letterDate;
	}

	public void setLetterDate(Date letterDate) {
		this.letterDate = letterDate;
	}

	public Date getDispatchDate() {
		return dispatchDate;
	}

	public void setDispatchDate(Date dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public String getCourierAgency() {
		return courierAgency;
	}

	public void setCourierAgency(String courierAgency) {
		this.courierAgency = courierAgency;
	}

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

}