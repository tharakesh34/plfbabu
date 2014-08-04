package com.pennant.app.eod.upload;

import java.util.Date;

import com.pennant.app.eod.service.UploadFinPftDetailService;
import com.pennant.backend.model.ExecutionStatus;
import com.pennant.backend.util.PennantConstants;

public class UploadProfitDetailProcess extends Thread {

	private static UploadProfitDetailProcess me = null;
	private  ExecutionStatus status = new ExecutionStatus();
	
	public static String RUNNING = "";

	private UploadFinPftDetailService uploadFinPftDetailService;

	private UploadProfitDetailProcess(){

	}

	private UploadProfitDetailProcess(UploadFinPftDetailService uploadFinPftDetailService){
		this.uploadFinPftDetailService = uploadFinPftDetailService;
	}

	public static UploadProfitDetailProcess getInstance(UploadFinPftDetailService uploadFinPftDetailService) {
		if("".equals(RUNNING) || me == null) {
			me = new UploadProfitDetailProcess(uploadFinPftDetailService);
		}
		return me;
	}
	
	public static UploadProfitDetailProcess getInstance() {
		if(me == null) {
			me = new UploadProfitDetailProcess();
		}
		return me;
	}

	public void run() {
		try {
			RUNNING = "STARTED";
			status.setExecutionName(PennantConstants.EOD_PFT_DTL_UPLOAD);
			
			status.setStartTime(new Date(System.currentTimeMillis()));
			this.status.setStatus("EXECUTING");
			try{
				uploadFinPftDetailService.doUploadPftDetails(status);
				status.setEndTime(new Date(System.currentTimeMillis()));
				this.status.setStatus("COMPLETED");
			}catch (Exception e) {
				this.status.setStatus("FAILED");
				RUNNING = "FAILED";
				return;
			} finally {
				status.setEndTime(new Date(System.currentTimeMillis()));
			}			
			RUNNING = "COMPLETED";
		} catch (Exception e) {
			RUNNING = "FAILED";
		} finally {
		}

	}

	public ExecutionStatus getStatus() {
    	return status;
    }

	public void setStatus(ExecutionStatus status) {
    	this.status = status;
    }	
}
