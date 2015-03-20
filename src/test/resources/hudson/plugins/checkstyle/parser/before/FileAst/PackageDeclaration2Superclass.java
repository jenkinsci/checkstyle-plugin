package edu.hm.hafner;

/**
 * Document type PackageDeclaration2Superclass.
 *
 * @author Christian M&ouml;stl
 */
public class PackageDeclaration2Superclass {
	/**
	 * Do sth...
	 * 
	 * @param a
	 *            number
	 */
	public void doSth(int a) {
		int b = 0;
		if (a > 0) {
			System.out.println(">0");
			if (a == 1) {
				++b;
				System.out.println("a=1");
				if (a == 2) {
					System.out.println("a=2");
				}
			}
		}
		System.out.println(b);
	}	
}