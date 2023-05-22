package com.pennant.backend.model.customermasters;

import java.io.Serializable;
import java.util.Date;

public class Abuser implements Serializable {

	private static final long serialVersionUID = -7305078754814792618L;

	private String abuserIDType;
	private String abuserIDNumber;
	private Date abuserExpDate;

	public Abuser() {
	    super();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getAbuserIDType() {
		return abuserIDType;
	}

	public void setAbuserIDType(String abuserIDType) {
		this.abuserIDType = abuserIDType;
	}

	public String getAbuserIDNumber() {
		return abuserIDNumber;
	}

	public void setAbuserIDNumber(String abuserIDNumber) {
		this.abuserIDNumber = abuserIDNumber;
	}

	public Date getAbuserExpDate() {
		return abuserExpDate;
	}

	public void setAbuserExpDate(Date abuserExpDate) {
		this.abuserExpDate = abuserExpDate;
	}

}
