package com.fhict.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fhict.calculator.R;

public class MainActivity extends Activity {
	public ICalculator calculator;
	private EditText textInputFirst;
	private EditText textInputSecond;
	private RadioGroup radioGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calculator);

		textInputFirst = (EditText) findViewById(R.id.editTextInputA);
		textInputSecond = (EditText) findViewById(R.id.editTextInputB);
		radioGroup = (RadioGroup)findViewById(R.id.radioGroup);

		calculator = new Calculator();

		Button calculateButton = (Button) findViewById(R.id.buttonCalculate);
		calculateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				calculate();
			}
		});
	}

	private void setOutput(String message) {
		TextView textOutput = (TextView) findViewById(R.id.textViewOutput);
		textOutput.setText(message);
	}
	
	private void calculate() {
		calculator.clear();

		boolean firstNumberPresent = textInputFirst.getText().toString().length() > 0;
		boolean secondNumberPresent = textInputSecond.getText().toString().length() > 0;
		boolean allNumbersPresent = firstNumberPresent && secondNumberPresent;
		
		if(!allNumbersPresent) {
			setOutput(getResources().getString(R.string.noNumbersPresent));
			return;
		}
		
		Double numberFirst = Double.parseDouble(textInputFirst.getText().toString());
		Double numberSecond = Double.parseDouble(textInputSecond.getText().toString());

		calculator.set(numberFirst);

		int radioButtonId = radioGroup.getCheckedRadioButtonId();
		if(R.id.radioButtonPlus == radioButtonId) {
			calculator.plus();
		} 
		else if(R.id.radioButtonMinus == radioButtonId) {
			calculator.minus();
		}
		else if(R.id.radioButtonMultiply == radioButtonId) {
			calculator.multiply();
		}
		else if(R.id.radioButtonDivide == radioButtonId) {
			calculator.divide();
		}
		else {
			setOutput(getResources().getString(R.string.operatorUnknown));
			return;
		}

		calculator.set(numberSecond);
		setOutput(calculator.getResult().toString());
	}
}
