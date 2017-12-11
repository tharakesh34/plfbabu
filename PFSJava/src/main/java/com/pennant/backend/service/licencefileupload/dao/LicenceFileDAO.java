package com.pennant.backend.service.licencefileupload.dao;

import com.pennant.backend.licencefileupload.LicenceFile;

/**
 * DAO methods declaration for the <b>LicenceFileUpload model</b> class.<br>
 * 
 */
public interface LicenceFileDAO{

	/**
	 * @param licenceFileUpload
	 * @return
	 */
	boolean save(LicenceFile licenceFileUpload);
	/**
	 * @param fileID
	 * @param active
	 */
	void update(long fileID,boolean active);
	/**
	 * @param active
	 * @return
	 */
	LicenceFile getActiveLicenceFileUpload(boolean active);
}