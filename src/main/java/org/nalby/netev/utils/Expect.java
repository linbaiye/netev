package org.nalby.netev.utils;

public final class Expect {
	private Expect() {}
	
	public static void notNull(Object object, String errorMessage) {
		if (object == null)  {
			throw new IllegalArgumentException(errorMessage != null? errorMessage: "Null value passed.");
		}
	}
	
	public static void toBeTrue(boolean expressionResult, String errorMessage) {
		if (!expressionResult)  {
			throw new IllegalArgumentException(errorMessage != null? errorMessage: "Unexpected condition.");
		}
	}
	

}
