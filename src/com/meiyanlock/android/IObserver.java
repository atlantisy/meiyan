package com.meiyanlock.android;

public interface IObserver {

	public void register();

	public void unRegister();

	public boolean isRegister();
}