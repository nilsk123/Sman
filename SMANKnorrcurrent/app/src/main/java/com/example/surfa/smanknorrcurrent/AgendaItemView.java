package com.example.surfa.smanknorrcurrent;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by surfa on 16-9-2015.
 */
public class AgendaItemView extends LinearLayout {

    private TextView itemDate;
    private Button submitButton;
    public AgendaItemView(Context context, AttributeSet attr) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.agendaitem_layout, this);
        this.itemDate = (TextView)findViewById(R.id.itemDate);
        this.submitButton = (Button)findViewById(R.id.submitButton);
    }

    public void setButtonOnclick(Button.OnClickListener ocl)
    {
        this.submitButton.setOnClickListener(ocl);
    }

    public void setDateText(String dateText)
    {
        this.itemDate.setText(dateText);
    }
}
