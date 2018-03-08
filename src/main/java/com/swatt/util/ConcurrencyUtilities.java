package com.swatt.util;

import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;

// FIXME: Annotated Concurrency Utilities

public class ConcurrencyUtilities {
	private static int nextThreadNumber = 0;
	
	public static boolean sleep(long millis) {
		try {
			Thread.sleep(millis);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}
	
	public static final void yield() {
		Thread.yield();
	}
	
	public static final void waitForCompletion(Thread thread) {
		try {
			thread.join();
		} catch (InterruptedException e) { }
	}
	
	public static final void waitForCompletion(Collection<Thread> threads) {
		LinkedList<Thread> activeThreads = new LinkedList<Thread>(threads);	// make a copy just to be sure he want's to retain the list
		
		Thread.yield();			// be sure that we don't race ahead of our caller's starting of threads()
		
		for(;;) {
			Thread thread = null;
			
			if(activeThreads.size() == 0)
				break;
			else 
				thread = (Thread) activeThreads.removeFirst();
			
			waitForCompletion(thread);
		}
	}
	
	public static final boolean waitOn(Object obj, long time) {
		return waitOn(obj, time, false);
	}
	
	public static final boolean waitForNotification(Object obj, long time) {
		return waitOn(obj, time, true);
	}
	
	public static final boolean waitOn(Object obj, long time, boolean ignoreInterrupts) {
		if (time <= 0)				// Give the caller a break for a time slippage in their logic		
			return true;
		
		long wakeTime = System.currentTimeMillis() + time;
		
		while((wakeTime - System.currentTimeMillis()) > 0) {
			synchronized(obj) {
				try {
					obj.wait(time);
					
					return true;
				} catch(InterruptedException e) {
					if (!ignoreInterrupts)
						return false;
				}
			}
		}
		
		return true;
	}
	
	public static final boolean waitOn(Object obj) {
		return waitOn(obj, false);
	}
	
	public static final boolean waitForNotification(Object obj) {
		return waitOn(obj, true);
	}
	
	public static final boolean waitOn(Object obj, boolean ignoreInterrupts) {
		for (;;) {
			synchronized(obj) {
				try {
					obj.wait();
					return true;
				} catch(InterruptedException e) { 
					if (!ignoreInterrupts)
						return false;
				}
			}
		}
	}
	
	public static final void notify(Object obj) {
		obj.notify();
	}
	
	public static final void notify(Object obj, int notifyCount) {
		for (int i=0; i < notifyCount; i++)
			obj.notify();
	}
	
	public static final void notifyAll(Object obj) {
		obj.notifyAll();
	}
	
	public static final void blockForever() {
		waitOn(new Object());
	}
	
	public static final Thread startThread(Runnable runnable, String name) {
		Thread thread = new Thread(runnable, name);
		thread.start();
		return thread;
	}
	
	public static final Thread startThread(Runnable runnable, String name, int priority) {
		Thread thread = new Thread(runnable, name);
		thread.setPriority(priority);
		thread.start();
		return thread;
	}	
	
	public static synchronized final Thread startThread(Runnable runnable) {
		Thread thread = new Thread(runnable, "ConcurrencyUtilities-" + nextThreadNumber++);
		thread.start();
		return thread;
	}
	
	public static final Thread createThread(Runnable runnable, String name) {
		Thread thread = new Thread(runnable, name);
		return thread;
	}
	
	public static final Thread createThread(Runnable runnable, String name, int priority) {
		Thread thread = new Thread(runnable, name);
		thread.setPriority(priority);
		return thread;
	}	
	
	public static synchronized final Thread createThread(Runnable runnable) {
		Thread thread = new Thread(runnable, "ConcurrencyUtilities-" + nextThreadNumber++);
		return thread;
	}
	
	public static final void startAutoDestructTimer(final int timeout, final int errorCode) {
		startAutoDestructTimer(null, timeout, null, errorCode);
	}
	
	
	public static final void startAutoDestructTimer(final long timeout) {
		startAutoDestructTimer(null, timeout, null, 0);
	}
	
	public static final void startAutoDestructTimer(final long timeout, final Runnable action) {
		startAutoDestructTimer(null, timeout, action, 0);
	}

	public static final void startAutoDestructTimer(final String message, final long timeout) {
		startAutoDestructTimer(message, timeout, null, 0);
	}
	
	public static final void startAutoDestructTimer(final String message, final long timeout, final Runnable action, final int errorCode) {
		startThread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(timeout);
					
					System.out.println("Application Auto Exiting (Timeout=" + timeout + ")...");
					
					if (action != null) {
						System.out.println("Cleaning up....");
						
						action.run();
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
				
				if (message != null)
					System.out.println(message);
				
				System.out.println("Exiting...");
				System.exit(errorCode);
			}
		}, "AutoExit");
	}
	
}
