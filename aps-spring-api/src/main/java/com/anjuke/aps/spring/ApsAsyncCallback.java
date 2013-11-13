package com.anjuke.aps.spring;

public interface ApsAsyncCallback<T> {
	public void callback(T returnValue);
}
