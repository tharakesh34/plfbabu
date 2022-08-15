package com.pennant.backend.model.finance;

import java.math.BigDecimal;

public class ExposureLinking {

	private String finReference;

	private String expReference;

	private BigDecimal pos;

	public ExposureLinking copyEntity() {
		ExposureLinking entity = new ExposureLinking();
		entity.setFinReference(this.finReference);
		entity.setExpReference(this.expReference);
		entity.setPos(this.pos);
		return entity;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getExpReference() {
		return expReference;
	}

	public void setExpReference(String expReference) {
		this.expReference = expReference;
	}

	public BigDecimal getPos() {
		return pos;
	}

	public void setPos(BigDecimal pos) {
		this.pos = pos;
	}

}
