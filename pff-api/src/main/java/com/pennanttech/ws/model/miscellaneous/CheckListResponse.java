package com.pennanttech.ws.model.miscellaneous;

import java.io.Serializable;
import java.util.List;

import com.pennant.backend.model.WSReturnStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "checkListId", "checkListDesc", "mandInputInStage", "checkMinCount", "checkMaxCount",
		"checkListMandnetory", "checkListDetail", "returnStatus" })
@XmlRootElement(name = "checklist")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckListResponse implements Serializable {
	private static final long serialVersionUID = 6569842731762889262L;
	@XmlElement
	private String finType;
	@XmlElement(name = "checkListId")
	private long finRefId;
	@XmlElement(name = "checkListDesc")
	private String lovDescRefDesc;
	@XmlElement(name = "mandInputInStage")
	private String mandInputInStage;
	@XmlElement(name = "checkMinCount")
	private long lovDescCheckMinCount;
	@XmlElement(name = "checkMaxCount")
	private long lovDescCheckMaxCount;
	@XmlElement
	private boolean checkListMandnetory = false;
	@XmlElement(name = "checkListDetail")
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
