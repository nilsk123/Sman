package com.fhict.calculator;

public interface ICalculator {

	ICalculator set(Double number);

	ICalculator plus();
	ICalculator minus();
	ICalculator divide();
	ICalculator multiply();
	
	Double getResult();
	
	ICalculator clear();

}