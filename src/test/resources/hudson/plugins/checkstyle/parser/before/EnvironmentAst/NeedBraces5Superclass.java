package edu.hm.hafner;

/**
 * Document type NeedBraces5Superclass.
 *
 * @author Christian M�stl
 */
public class NeedBraces5Superclass {
	/**
	 * Do sth.
	 * 
	 * @param a
	 *            number
	 * @return return sth.
	 */
	public int doSth(final int a) {
		System.out.println(a);
		int x = 3;
		if (a < 0)
			x = x - a;
		else {
			x = x + a;
		}
		
		return x;
	}
}