package com.fhict.calculator;

import java.util.ArrayList;
import java.util.List;

public class Calculator implements ICalculator {
	List<Object> elements;

	public Calculator() {
		elements = new ArrayList<Object>();
	}

	@Override
	public ICalculator set(Double number) {
		elements.add(number);

		return this;
	}

	@Override
	public ICalculator plus() {
		return addOperator(Operator.Plus);
	}

	@Override
	public ICalculator minus() {
		return addOperator(Operator.Minus);
	}

	@Override
	public ICalculator divide() {
		return addOperator(Operator.Divide);
	}

	@Override
	public ICalculator multiply() {
		return addOperator(Operator.Multiply);
	}


	private ICalculator addOperator(Operator operator) {
		elements.add(operator);
	
		return this;
	}

	public Double getResult() {
		Double result = null;
		
		if(elements.size() == 3) {
			Double firstNumber = (Double) elements.get(0);
			Operator operator = (Operator) elements.get(1);
			Double secondNumber = (Double) elements.get(2);

			switch(operator) {
			case Plus:
				result = firstNumber + secondNumber;
				break;
			case Minus:
				result = firstNumber - secondNumber;
				break;
			case Multiply:
				result = firstNumber * secondNumber;
				break;
			case Divide:
				result = firstNumber / secondNumber;
				break;
			}
		}

		return result; // + new Random().nextInt(10);
	}

	@Override
	public ICalculator clear() {
		elements.clear();

		return this;
	}
}
