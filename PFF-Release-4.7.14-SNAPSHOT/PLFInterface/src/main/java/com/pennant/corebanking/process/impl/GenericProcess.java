package com.pennant.corebanking.process.impl;

import java.math.BigDecimal;

import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

public class GenericProcess {

	public GenericProcess() {
		
	}
	
	protected String getString(ProgramCallDocument doc, String pcml, String name) throws PcmlException {		
		return (String) doc.getValue(pcml + name);		
	}
	
	protected BigDecimal getEquationDate(ProgramCallDocument doc, String pcml, String name) throws PcmlException {
		return (BigDecimal) doc.getValue(pcml + name);
	}
	
	protected BigDecimal getBalance(ProgramCallDocument doc, String pcml, String name) throws PcmlException {
		return new BigDecimal(doc.getValue(pcml + name).toString());
	}

}
