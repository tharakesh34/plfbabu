package com.pennant.backend.dao.licencefile;

import com.pennant.backend.licencefile.LicenceFile;

/**
 * DAO methods declaration for the <b>LicenceFile model</b> class.<br>
 * 
 */
public interface LicenceFileDAO{

	/**
	 * @param licenceFile
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
	LicenceFile getActiveLicenceFile();
}