package edu.hm.hafner;

import java.util.Date;
import java.util.Random;

/**
 * Useless class - only for test-cases.
 *
 * @author Christian M�stl
 */
public class NeedBraces1 {
    private final Date date;
    private final int number;
    private final String text;
    
    /**
     * Creates a new instance of {@link NeedBraces1}.
     *
     * @param date
     *            Date
     * @param number
     *            number
     * @param text
     *            Text
     */
    public NeedBraces1(final Date date, final int number, final String text) {
        this.date = date;
        this.number = number;
        this.text = text;
    }
    
    /**
     * Do sth.
     * 
     * @param width
     *            the width
     * @param height
     *            the height
     */
    public void doSth(final int width, final int height) {
        for (int i = 0; i < 10; i++) {
            System.out.println("Prints sth...");
        }
        int[][] result = new int[height][width];
        int size = 50;
        
        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++)
                result[row][col] = size;
            System.out.println("Next Step");
        }
        
        System.out.println("Half = " + result[height / 2][width / 2]);
    }
    
    /**
     * Returns the date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }
    
    /**
     * Returns the number.
     *
     * @return the number
     */
    public int getNumber() {
        return number;
    }
    
    /**
     * Returns the text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }
}