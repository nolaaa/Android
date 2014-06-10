package com.tippal.drawer;

import java.text.DecimalFormat;

import com.actionbarsherlock.app.SherlockFragment;
import com.tippal.app.R;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Fragment4 extends SherlockFragment {
	
	View rootView;
	
	Context context;
	public Fragment4(Context context) {
		this.context = context;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment4, container, false);
        
        setupUserInterface();
        
        setupHideKeyboardOnClickOutside(rootView);
        
        return rootView;
    }

	private void setupUserInterface() {
		// Bill Amount EditText
		EditText etBillAmount = (EditText) rootView.findViewById(R.id.et_bill_amount);
		etBillAmount.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				calculateTip();
			}
		});
		
		// Subtract Tip Percent Button
		Button btnSubtractTipPercent = (Button) rootView.findViewById(R.id.btn_subtract_tip_percent);
		btnSubtractTipPercent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView tvTipPercent = (TextView) rootView.findViewById(R.id.tv_tip_amount);
				String sTipPercent = tvTipPercent.getText().toString();
				sTipPercent = sTipPercent.substring(0,sTipPercent.indexOf("%"));
				int iTipPercent = Integer.parseInt(sTipPercent);
				if (iTipPercent > 0)
					iTipPercent--;
				tvTipPercent.setText(String.valueOf(iTipPercent) + "%");
				calculateTip();
			}
		});
		// Add Tip Percent Button
		Button btnAddTipPercent = (Button) rootView.findViewById(R.id.btn_add_tip_percent);
		btnAddTipPercent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView tvTipPercent = (TextView) rootView.findViewById(R.id.tv_tip_amount);
				String sTipPercent = tvTipPercent.getText().toString();
				sTipPercent = sTipPercent.substring(0,sTipPercent.indexOf("%"));
				int iTipPercent = Integer.parseInt(sTipPercent);
				if (iTipPercent < 100)
					iTipPercent++;
				tvTipPercent.setText(String.valueOf(iTipPercent) + "%");
				calculateTip();
			}
		});
	}
	
	private void calculateTip() {
		TextView tvTipPercent = (TextView) rootView.findViewById(R.id.tv_tip_amount);
		String sTipPercent = tvTipPercent.getText().toString();
		sTipPercent = sTipPercent.substring(0,sTipPercent.indexOf("%"));

		double dTipPercent, dTip, dTotal, dBillAmount;
		if (Integer.parseInt(sTipPercent) < 10)
			dTipPercent = Double.parseDouble(".0" + sTipPercent);
		else if (Integer.parseInt(sTipPercent) == 100)
			dTipPercent = 1;
		else
			dTipPercent = Double.parseDouble("." + sTipPercent);
		
		EditText etBillAmount = (EditText) rootView.findViewById(R.id.et_bill_amount);
		try {
			dBillAmount = Double.parseDouble(etBillAmount.getText().toString());
		} catch (Exception e) {
			return;
		}
		try {
			dTip = dTipPercent * dBillAmount;
			dTotal = dTip + dBillAmount;
		} catch (Exception e) {
			return;
		}
		TextView tvTipAmountResult = (TextView) rootView.findViewById(R.id.tv_tip_amount_result);
		TextView tvBillAmountResult = (TextView) rootView.findViewById(R.id.tv_bill_amount_result);
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		tvTipAmountResult.setText(String.valueOf("$" + decimalFormat.format(dTip)));
		tvBillAmountResult.setText(String.valueOf("$" + decimalFormat.format(dTotal)));
	}
	
	public void setupHideKeyboardOnClickOutside(View v) {
        if(!(v instanceof EditText)) {
            v.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v1, MotionEvent event) {
					InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					EditText etBillAmount = (EditText) rootView.findViewById(R.id.et_bill_amount);
					if (etBillAmount != null)
						imm.hideSoftInputFromWindow(etBillAmount.getWindowToken(), 0);
					return false;
				}
            });
        }
        if (v instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                View innerView = ((ViewGroup) v).getChildAt(i);
                setupHideKeyboardOnClickOutside(innerView);
            }
        }
    }
    
}