package com.swatt.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Benchmark {
	private NumberFormat nf = new DecimalFormat("#.###");
	private String name;
	private long startTime;			// This is an actual time based on the Unix Epoch time
	private long startNanoTime;		// This is an arbitrary relative time that is not tied to any global time
	private int counter;
	
	public Benchmark(String name) {
		this.name = name;
		reset();
	}
	
	public final void reset() {
		counter = 0;
		startTime = System.currentTimeMillis();
		startNanoTime = System.nanoTime();
	}
	
	public Benchmark() { this("Benchmark"); }

	
	public final void inc() { counter++; }
	
	public final long getDurationNanosec() {
		return System.nanoTime() - startNanoTime;
	}
	
	public final double getDurationMillisec() {
		return getDurationNanosec()/1000000.0;
	}
	
	public final long getStartTime() { 	return startTime;	}
	
	public int getCount() {  return counter; }

	public void time() {
		time(name);
	}
	
	public void time(String prefix) {
		double duration = getDurationMillisec();
		
		System.out.println(prefix + ": " + nf.format(duration) + "ms");
	}
	
	public void count() {
		time(name);
	}
	
	public void count(String prefix) {
		System.out.println(prefix + ": " + counter);
	}
	
	public void average(String prefix) {
		if (counter != 0) {
			System.out.println(prefix + ": " + getDurationMillisec() / counter + " ms");
		} else
			System.out.println(prefix + ": <NONE>");
	}
}
