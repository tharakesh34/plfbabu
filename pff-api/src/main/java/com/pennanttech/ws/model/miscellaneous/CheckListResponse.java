package com.pennanttech.ws.model.miscellaneous;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pennant.backend.model.WSReturnStatus;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "checkListId", "checkListDesc", "mandInputInStage", "checkMinCount", "checkMaxCount",
		"checkListMandnetory", "checkListDetail", "returnStatus" })
@XmlRootElement(name = "checklist")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckListResponse implements Serializable {
	private static final long serialVersionUID = 6569842731762889262L;
	@XmlElement
	private String finType;
	@JsonProperty("checkListId")
	private long finRefId;
	@JsonProperty("checkListDesc")
	private String lovDescRefDesc;
	@JsonProperty("mandInputInStage")
	private String mandInputInStage;
	@JsonProperty("checkMinCount")
	private long lovDescCheckMinCount;
	@JsonProperty("checkMaxCount")
	private long lovDescCheckMaxCount;
	@XmlElement
	private boolean checkListMandnetory = false;
	@JsonProperty("checkListDetail")
	private List<CheckListDetailsRespons> lovDesccheckListDetail;
	@XmlElement
	private WSReturnStatus returnStatus;

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getFinType() {
		return finType;
	}

	public void setFinType(String finType) {
		this.finType = finType;

	}

	public long getFinRefId() {
		return finRefId;
	}

	public void setFinRefId(long finRefId) {
		this.finRefId = finRefId;
	}

	public String getMandInputInStage() {
		return mandInputInStage;
	}

	public void setMandInputInStage(String mandInputInStage) {
		this.mandInputInStage = mandInputInStage;
	}

	public String getLovDescRefDesc() {
		return lovDescRefDesc;
	}

	public void setLovDescRefDesc(String lovDescRefDesc) {
		this.lovDescRefDesc = lovDescRefDesc;
	}

	public List<CheckListDetailsRespons> getLovDesccheckListDetail() {
		return lovDesccheckListDetail;
	}

	public void setLovDesccheckListDetail(List<CheckListDetailsRespons> lovDesccheckListDetail) {
		this.lovDesccheckListDetail = lovDesccheckListDetail;
	}

	public long getLovDescCheckMinCount() {
		return lovDescCheckMinCount;
	}

	public void setLovDescCheckMinCount(long lovDescCheckMinCount) {
		this.lovDescCheckMinCount = lovDescCheckMinCount;
	}

	public long getLovDescCheckMaxCount() {
		return lovDescCheckMaxCount;
	}

	public void setLovDescCheckMaxCount(long lovDescCheckMaxCount) {
		this.lovDescCheckMaxCount = lovDescCheckMaxCount;
	}

	public boolean isCheckListMandnetory() {
		return checkListMandnetory;
	}

	public void setCheckListMandnetory(boolean checkListMandnetory) {
		this.checkListMandnetory = checkListMandnetory;
	}

}
