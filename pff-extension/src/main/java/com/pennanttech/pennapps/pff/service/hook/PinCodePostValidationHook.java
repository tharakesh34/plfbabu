package com.pennanttech.pennapps.pff.service.hook;

public interface PinCodePostValidationHook<T, R> {

	R getMinimumLength(T object);
}
