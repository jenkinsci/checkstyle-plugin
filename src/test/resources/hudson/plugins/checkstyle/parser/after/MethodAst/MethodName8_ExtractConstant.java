package edu.hm.hafner;

/**
 * Document type MethodName8_ExtractMethod.
 *
 * @author Christian M&ouml;stl
 */
public class MethodName8_ExtractConstant {
	
	private static final String CONSTANT = "Hallo Christian";

	private MethodName8_ExtractConstant() {

    }

    /**
     * Prints sth.
     */
    public static void DoSth() {
    	System.out.println(CONSTANT);
    }
}