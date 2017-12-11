
package com.pennant.backend.service.licencefileupload.impl;

import org.apache.log4j.Logger;

import com.pennant.backend.licencefileupload.LicenceFile;
import com.pennant.backend.service.licencefileupload.LicenceFileService;
import com.pennant.backend.service.licencefileupload.dao.LicenceFileDAO;

/**
 * Service Declaration for methods that depends on <b>LicenceFileUpload</b>.<br>
 * 
 */
public class LicenceFileServiceImpl implements LicenceFileService {
	private static Logger logger = Logger.getLogger(LicenceFileServiceImpl.class);
	
	private LicenceFileDAO licenceFileDAO;
	
	@Override
	public	LicenceFile getActiveLicenceFileUpload(boolean active){
		LicenceFile licenceFileUpload = getLicenceFileDAO().getActiveLicenceFileUpload(active);
		return licenceFileUpload;
	}
	
	@Override
	public	boolean saveOrUpdate(LicenceFile licenceFileUpload){
		logger.debug("Entering ");
		boolean result = false;
		if(licenceFileUpload.isNewRecord()){
			result = getLicenceFileDAO().save(licenceFileUpload);
			getLicenceFileDAO().update(licenceFileUpload.getFileID(),false);
			return result;
		}
		logger.debug("Leaving ");
		return result;
	}
	
	public LicenceFileDAO getLicenceFileDAO() {
		return licenceFileDAO;
	}

	public void setLicenceFileDAO(LicenceFileDAO licenceFileDAO) {
		this.licenceFileDAO = licenceFileDAO;
	}
}