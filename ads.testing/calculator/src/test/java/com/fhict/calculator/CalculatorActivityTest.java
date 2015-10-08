package com.fhict.calculator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

//@Config(manifest="../Calculator/AndroidManifest.xml")
//@RunWith(RobolectricTestRunner.class)
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class CalculatorActivityTest {

	private MainActivity activity;

	private TextView outputView;
	private TextView inputAView;
	private TextView inputBView;
	private RadioGroup radioGroup;

	private Button button;

	@Before
	public void setUp()
	{
		activity = Robolectric.buildActivity(MainActivity.class).create().get();

		assertNotNull(activity);

		outputView = (TextView) activity.findViewById(R.id.textViewOutput);
		inputAView = (TextView) activity.findViewById(R.id.editTextInputA);
		inputBView = (TextView) activity.findViewById(R.id.editTextInputB);
		radioGroup = (RadioGroup) activity.findViewById(R.id.radioGroup);
		button = (Button) activity.findViewById(R.id.buttonCalculate);
	}

	private void clickOnCalculateButton() {
		button.performClick();
	}

	private void setSomeInput() {
		inputAView.setText("3");
		inputBView.setText("4");
	}

	private void assertOutputText(String expected) {
		String actual = outputView.getText().toString();
		assertEquals(expected,actual);
	}

	@Test
	public void testOutputHintIsDisplayed() {
		assertTrue(false);
	}

	@Test
	public void testNumbersCanBeSetAreCalculatedWithAddAndDisplayed() throws Throwable {
		assertTrue(false);
	}

	@Test
	public void testErrorIsDisplayedWhenNoOperationSelected() {
		assertTrue(false);
	}

	@Test
	public void testWhenNoNumberAreFilledInMessageIsDisplayed() {
		assertTrue(false);
	}
}