
package com.pennant.backend.service.licencefile.impl;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.pennant.backend.dao.licencefile.LicenceFileDAO;
import com.pennant.backend.licencefile.LicenceFile;
import com.pennant.backend.service.licencefile.LicenceFileService;

/**
 * Service Declaration for methods that depends on <b>LicenceFile</b>.<br>
 * 
 */
public class LicenceFileServiceImpl implements LicenceFileService {
	private static Logger logger = Logger.getLogger(LicenceFileServiceImpl.class);
	
	private LicenceFileDAO licenceFileDAO;
	
	@Override
	public	LicenceFile getActiveLicenceFile(){
		LicenceFile licenceFile = getLicenceFileDAO().getActiveLicenceFile();
		return licenceFile;
	}
	
	@Override
	public	boolean saveOrUpdate(LicenceFile licenceFile){
		logger.debug("Entering ");
		boolean result = false;
		if(licenceFile.isNewRecord()){
			result = getLicenceFileDAO().save(licenceFile);
			getLicenceFileDAO().update(licenceFile.getFileID(),false);
			return result;
		}
		logger.debug("Leaving ");
		return result;
	}
	
	public boolean init() throws IOException {
		return setLicenseFile(getActiveLicenceFile());
	}

	public boolean setLicenseFile(LicenceFile licenceFile) throws IOException {
		if(licenceFile==null){
			//error
		}else{
			licenceFile.getLicenseFile().deleteOnExit();
		}
		return true;
	}


	
	public LicenceFileDAO getLicenceFileDAO() {
		return licenceFileDAO;
	}

	public void setLicenceFileDAO(LicenceFileDAO licenceFileDAO) {
		this.licenceFileDAO = licenceFileDAO;
	}
}