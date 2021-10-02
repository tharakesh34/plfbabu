package com.pennant.backend.dao.collateral;

import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennanttech.pff.core.TableType;

public interface ExtendedFieldExtensionDAO {

	long getExtFieldExtensionId();

	void save(ExtendedFieldExtension extendedFieldExtension, TableType tableType);

	void update(ExtendedFieldExtension extendedFieldExtension, TableType tableType);

	void delete(ExtendedFieldExtension extendedFieldExtension, TableType tableType);

	ExtendedFieldExtension getExtendedFieldExtension(String externalRef, String modeStatus, String finEvent,
			TableType tableType);

	boolean isDuplicateKey(ExtendedFieldExtension extendedFieldExtension, TableType tableType);

	boolean isExtenstionExist(ExtendedFieldExtension extendedFieldExtension, TableType tableType);

}
