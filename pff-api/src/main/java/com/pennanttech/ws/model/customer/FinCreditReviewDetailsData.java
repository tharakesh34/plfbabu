package com.pennanttech.ws.model.customer;

import java.util.List;

import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "cif", "finCreditReviewDetails" })
public class FinCreditReviewDetailsData {

	private String cif;
	private List<FinCreditReviewDetails> finCreditReviewDetails;

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public List<FinCreditReviewDetails> getFinCreditReviewDetails() {
		return finCreditReviewDetails;
	}

	public void setFinCreditReviewDetails(List<FinCreditReviewDetails> finCreditReviewDetails) {
		this.finCreditReviewDetails = finCreditReviewDetails;
	}

}
