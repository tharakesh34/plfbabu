package com.pennanttech.ws.model.customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennanttech.pennapps.core.model.ErrorDetail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "cif", "extendedDetails", "returnStatus" })
public class CustomerExtendedFieldDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement
	private String cif;
	@XmlElementWrapper(name = "extendedDetails")
	@XmlElement(name = "extendedDetail")
	private List<ExtendedField> extendedDetails;
	@XmlElement(name = "extendedDetail")
	private ExtendedFieldHeader extendedFieldHeader;
	private ExtendedFieldRender extendedFieldRender;
	private List<ErrorDetail> errorDetails = new ArrayList<>(1);
	@XmlElement
	private WSReturnStatus returnStatus;

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public List<ExtendedField> getExtendedDetails() {
		return extendedDetails;
	}

	public void setExtendedDetails(List<ExtendedField> extendedDetails) {
		this.extendedDetails = extendedDetails;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}

	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public List<ErrorDetail> getErrorDetails() {
		return errorDetails;
	}

	public void setErrorDetails(List<ErrorDetail> errorDetails) {
		this.errorDetails = errorDetails;
	}

}
