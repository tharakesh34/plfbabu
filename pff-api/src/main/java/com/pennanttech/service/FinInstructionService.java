package com.pennanttech.service;

import org.jaxen.JaxenException;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.exception.PFFInterfaceException;

public interface FinInstructionService {

	public FinanceDetail addRateChange(FinServiceInstruction finServiceInstruction) throws JaxenException,
			PFFInterfaceException;

	public FinanceDetail changeRepayAmt(FinServiceInstruction finServiceInstruction) throws JaxenException,
			PFFInterfaceException;

	public FinanceDetail deferments(FinServiceInstruction finServiceInstruction);

	public FinanceDetail addTerms(FinServiceInstruction finServiceInstruction) throws JaxenException,
			PFFInterfaceException;
	
	public FinanceDetail removeTerms(FinServiceInstruction finServiceInstruction) throws JaxenException,
			PFFInterfaceException;
	
	public FinanceDetail recalculate(FinServiceInstruction finServiceInstruction) throws JaxenException,
			PFFInterfaceException;

	public FinanceDetail changeInterest(FinServiceInstruction finServiceInstruction) throws JaxenException,
			PFFInterfaceException;

	public FinanceDetail addDisbursement(FinServiceInstruction finServiceInstruction) throws JaxenException,
			PFFInterfaceException;

	public FinanceDetail partialSettlement(FinServiceInstruction finServiceInstruction) throws JaxenException,
			PFFInterfaceException;

	public FinanceDetail earlySettlement(FinServiceInstruction finServiceInstruction) throws JaxenException,
			PFFInterfaceException;

	public FinanceDetail changeInstallmentFrq(FinServiceInstruction finServiceInstruction) throws JaxenException,
			PFFInterfaceException;

	public FinanceDetail reScheduling(FinServiceInstruction finServiceInstruction) throws JaxenException,
			PFFInterfaceException;

	public WSReturnStatus updateLoanBasicDetails(FinServiceInstruction finServiceInstruction) throws JaxenException,
			PFFInterfaceException;

	public WSReturnStatus updateLoanPenaltyDetails(FinServiceInstruction finServiceInstruction) throws JaxenException,
			PFFInterfaceException;

}
