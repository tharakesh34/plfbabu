package com.pennant.pff.eod.cache;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.service.applicationmaster.CurrencyService;

public class CurrencyConfigCache {
	private static final Logger logger = LogManager.getLogger(CurrencyConfigCache.class);
	private static CurrencyService currencyService;

	private static LoadingCache<String, Currency> currencyCache = CacheBuilder.newBuilder()
			.expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, Currency>() {
				@Override
				public Currency load(String ccyCode) throws Exception {
					return getCurrencyByCcy(ccyCode);
				}
			});

	private static Currency getCurrencyByCcy(String ccy) {
		return currencyService.getApprovedCurrencyById(ccy);
	}

	public static Currency getCacheCurrencyByCode(String ccy) {
		Currency currency = null;
		try {
			currency = currencyCache.get(ccy);
		} catch (Exception e) {
			logger.warn("{} currency details not found in cache", ccy);
			currency = getCurrencyByCode(ccy);
		}
		return currency;
	}

	private static Currency getCurrencyByCode(String code) {
		return getCurrency(code);
	}

	private static Currency getCurrency(String ccy) {
		if (StringUtils.isEmpty(ccy)) {
			ccy = SysParamUtil.getAppCurrency();
		}
		return currencyService.getApprovedCurrencyById(ccy);
	}

	public void setCurrencyService(CurrencyService currencyService) {
		CurrencyConfigCache.currencyService = currencyService;
	}

}
