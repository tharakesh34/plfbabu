package com.pennant.app.eod.accrual;

import java.util.Date;

import com.pennant.app.eod.service.AmortizationService;
import com.pennant.backend.model.ExecutionStatus;
import com.pennant.backend.util.PennantConstants;

public class AccrualProcess extends Thread {

	private static AccrualProcess me = null;
	private  ExecutionStatus calculation = new ExecutionStatus();
	private  ExecutionStatus posting = new ExecutionStatus();
	
	public static String ACC_RUNNING = "";

	private Date valueDate  = null;
	private String branch 	= null;
	private AmortizationService amortizationService;

	private AccrualProcess(){

	}

	private AccrualProcess(AmortizationService amortizationService, Date valueDate, String branch){
		this.amortizationService = amortizationService;
		this.valueDate = valueDate;
		this.branch = branch;
	}

	public static AccrualProcess getInstance(AmortizationService amortizationService, Date valueDate, String branch) {
		if("".equals(ACC_RUNNING) || me == null) {
			me = new AccrualProcess(amortizationService, valueDate, branch);
		}
		return me;
	}
	
	public static AccrualProcess getInstance() {
		if(me == null) {
			me = new AccrualProcess();
		}
		return me;
	}

	public void run() {
		try {
			ACC_RUNNING = "STARTED";
			calculation.setExecutionName(PennantConstants.EOD_ACCRUAL_CALC);
			posting.setExecutionName(PennantConstants.EOD_ACCRUAL_POSTING);
			
			calculation.setStartTime(new Date(System.currentTimeMillis()));
			this.calculation.setStatus("EXECUTING");
			try{
				amortizationService.doAccrualCalculation(calculation, valueDate, "Y");
				calculation.setEndTime(new Date(System.currentTimeMillis()));
				this.calculation.setStatus("COMPLETED");
			}catch (Exception e) {
				this.calculation.setStatus("FAILED");
				ACC_RUNNING = "FAILED";
				return;
			} finally {
				calculation.setEndTime(new Date(System.currentTimeMillis()));
			}
			
			
			posting.setStartTime(new Date(System.currentTimeMillis()));
			this.posting.setStatus("EXECUTING");
			try {
				amortizationService.doAccrualPosting(posting, valueDate, this.branch, "Y");
				posting.setEndTime(new Date(System.currentTimeMillis()));
				this.posting.setStatus("COMPLETED");
			} catch (Exception e) {
				this.posting.setStatus("FAILED");
				ACC_RUNNING = "FAILED";
				return;
			} finally {
				posting.setEndTime(new Date(System.currentTimeMillis()));
			}
			
			ACC_RUNNING = "COMPLETED";
		} catch (Exception e) {
			ACC_RUNNING = "FAILED";
		} finally {
		}

	}

	public ExecutionStatus getCalculation() {
    	return calculation;
    }

	public void setCalculation(ExecutionStatus calculation) {
    	this.calculation = calculation;
    }

	public ExecutionStatus getPosting() {
    	return posting;
    }

	public void setPosting(ExecutionStatus posting) {
    	this.posting = posting;
    }

	
}
