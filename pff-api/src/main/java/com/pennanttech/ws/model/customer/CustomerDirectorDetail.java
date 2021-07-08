package com.pennanttech.ws.model.customer;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.DirectorDetail;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerDirectorDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private String cif;
	private DirectorDetail directorDetail;
	private long directorId;
	private WSReturnStatus returnStatus;

	public CustomerDirectorDetail() {
		super();
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public DirectorDetail getDirectorDetail() {
		return directorDetail;
	}

	public void setDirectorDetail(DirectorDetail directorDetail) {
		this.directorDetail = directorDetail;
	}

	public WSReturnStatus getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(WSReturnStatus returnStatus) {
		this.returnStatus = returnStatus;
	}

	public long getDirectorId() {
		return directorId;
	}

	public void setDirectorId(long directorId) {
		this.directorId = directorId;
	}

}
