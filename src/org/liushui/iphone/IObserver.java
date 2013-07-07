package org.liushui.iphone;

public interface IObserver {

	public void register();

	public void unRegister();

	public boolean isRegister();
}