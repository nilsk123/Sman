package com.fhict.calculator.test;

import java.util.Random;

import android.test.AndroidTestCase;

import com.fhict.calculator.Calculator;

public class CalculatorTest extends AndroidTestCase {

	Calculator calculator;
	static final double maxDouble = 100;
	static double a,b;

	@Override
	protected void setUp() throws Exception {
		calculator = new Calculator();
		Random random = new Random();
		a = random.nextDouble()*maxDouble;
		b = random.nextDouble()*maxDouble;
		
		super.setUp();
	}

	public void testSimpleAddition() {
		assertTrue(false);
	}

	public void testSimpleMultiplication() {
		assertTrue(false);
	}

}
