package edu.hm.hafner;

/**
 * Useless class - only for test-cases.
 *
 * @author Christian M&ouml;stl
 */
public class FinalClass {

    private int a;

    private FinalClass() {

    }
    
    private static class Test {
        private static void doSth() {
            System.out.println("Hallo");
        }
    }
}