package com.fhict.calculator.test;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fhict.calculator.MainActivity;
import com.fhict.calculator.R;

public class CalculatorActivityTest extends ActivityUnitTestCase<MainActivity> {

    MainActivity activity;
    private TextView outputView;
    private TextView inputAView;
    private TextView inputBView;
    private RadioGroup radioGroup;
	private Button button;

    public CalculatorActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent(getInstrumentation().getTargetContext(),
                MainActivity.class);
        startActivity(intent,null,null);
        activity = getActivity();

        outputView = (TextView) activity.findViewById(R.id.textViewOutput);
        inputAView = (TextView) activity.findViewById(R.id.editTextInputA);
        inputBView = (TextView) activity.findViewById(R.id.editTextInputB);
        radioGroup = (RadioGroup) activity.findViewById(R.id.radioGroup);
        button = (Button) activity.findViewById(R.id.buttonCalculate);
    }

    private void clickOnCalculateButton() {
        button.callOnClick();
    }

    private void setSomeInput() {
        inputAView.setText("3");
        inputBView.setText("4");
    }

    private void assertOutputText(String expected) {
        String actual = outputView.getText().toString();
        assertEquals(expected,actual);
    }
    
    @SmallTest
    @UiThreadTest
    public void testOutputHintIsDisplayed() {

        if(outputView.getHint().toString() == activity.getResources().getString(R.string.outputHint) && inputAView.getHint() == activity.getResources().getString(R.string.inputHint))
        {
            assertTrue(true);
        }

        else
        {
            assertTrue(false);
        }
    }
  
    @SmallTest
    @UiThreadTest
    public void testErrorIsDisplayedWhenNoOperationSelected() {
        inputAView.setText("40");
        inputBView.setText("50");

        button.performClick();

        if(outputView.getText().toString() == activity.getResources().getString(R.string.operatorUnknown))
        {
            assertTrue(true);
        }

        else
        {
            assertTrue(false);
        }

     }
    
    @SmallTest
	@UiThreadTest
	public void testWhenNoNumberAreFilledInMessageIsDisplayed() {

        radioGroup.check(0);

        button.performClick();

        if(outputView.getText().toString() == activity.getResources().getString(R.string.noNumbersPresent))
        {
            assertTrue(true);
        }

        else
        {
            assertTrue(false);
        }
    }
    
    @SmallTest
    @UiThreadTest
    public void testNumbersCanBeSetAndAreCalculatedWithAddAndDisplayed() throws Throwable {
        inputAView.setText("40");
        inputBView.setText("50");
        radioGroup.check(0);

        button.performClick();

        if(outputView.getText().toString() == "90.0")
        {
            assertFalse(true);
        }

        else
        {
            assertFalse(false);
        }
    }

}
