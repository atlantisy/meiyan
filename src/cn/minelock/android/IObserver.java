package cn.minelock.android;

public interface IObserver {

	public void register();

	public void unRegister();

	public boolean isRegister();
}