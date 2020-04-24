package br.com.kotar.core.helper.handler;

public class Handler {
	
	public void postDelayed(int delay, Runnable runnable) {
		
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		
		runnable.run();
	}
}