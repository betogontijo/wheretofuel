package com.betogontijo.wheretofuel;

import com.betogontijo.wheretofuel.client.WhereToFuelTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class WhereToFuelSuite extends GWTTestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for WhereToFuel");
		suite.addTestSuite(WhereToFuelTest.class);
		return suite;
	}
}
