package com.pennant.eod.dao;

import java.util.Date;

public interface CustomerDatesDAO {

	void saveCustomerDates(Date appDate, Date valueDate, Date nextBusinessDate);

	void updateCustomerDates(long custId, Date appDate, Date valueDate, Date nextBusinessDate);

}
