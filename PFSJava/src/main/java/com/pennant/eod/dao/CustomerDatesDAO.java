package com.pennant.eod.dao;

import java.util.Date;

import com.pennant.eod.beans.CustomerDates;

public interface CustomerDatesDAO {

	void saveCustomerDates(Date appDate, Date valueDate, Date nextBusinessDate);

	void updateCustomerDates(long custId, Date appDate, Date valueDate, Date nextBusinessDate);

	CustomerDates getCustomerDates(long custId);
}
