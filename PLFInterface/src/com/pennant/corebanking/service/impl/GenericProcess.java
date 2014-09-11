package com.pennant.corebanking.service.impl;

import java.math.BigDecimal;

import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

public class GenericProcess {

	protected String getString(ProgramCallDocument doc, String pcml, String name) throws PcmlException {		
		String value = (String) doc.getValue(pcml + name);		
		if (value != null) {
			return value.toString();
		} else {
			return value;
		}

	}
	
	protected BigDecimal getEquationDate(ProgramCallDocument doc, String pcml, String name) throws PcmlException {
		return (BigDecimal) doc.getValue(pcml + name);
	}
	
	protected BigDecimal getBalance(ProgramCallDocument doc, String pcml, String name) throws PcmlException {
		return new BigDecimal(doc.getValue(pcml + name).toString());
	}

}
