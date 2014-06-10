package com.tippal.drawer;

import com.actionbarsherlock.app.SherlockFragment;
import com.tippal.app.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
 
public class Fragment3 extends SherlockFragment {
	
	View rootView;

	Context context;
	public Fragment3(Context context) {
		this.context = context;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment3, container, false);
        
        setupUserInterface(rootView);
        
        setupHideKeyboardOnClickOutside(rootView);
        
        return rootView;
    }

	private void setupUserInterface(final View rootView) {
		// Get Receiver PayPal Address Button
		Button btnGetReceiverPaypal = (Button) rootView.findViewById(R.id.btn_get_receiver_paypal_address);
		btnGetReceiverPaypal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		// Authorize PayPal Payment Button
		Button btnAuthorizePayment = (Button) rootView.findViewById(R.id.btn_authorize_paypal_payment);
		btnAuthorizePayment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText etUserIdOfReceiver = (EditText) rootView.findViewById(R.id.et_user_id_of_receiver);
				EditText etPaypalCurrencyToSend = (EditText) rootView.findViewById(R.id.et_paypal_currency_to_send);
				String sUserIdOfReceiver = etUserIdOfReceiver.getText().toString();
				String stPaypalCurrencyToSend = etPaypalCurrencyToSend.getText().toString();
				if (sUserIdOfReceiver.isEmpty() || stPaypalCurrencyToSend.isEmpty()) { // 1. Check if fields are empty and notify user.
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			        builder.setMessage("Provide a Receiver Paypal Email and Amount to send.")
			               .setPositiveButton(R.string.dialog_alert_exit, null);
			        builder.show();
				}
				else { // 2. Everything is OK 
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?business=" + sUserIdOfReceiver + "&cmd=_xclick&currency_code=USD&amount=" + stPaypalCurrencyToSend + "&item_name=tip"));
					startActivity(intent);
				}
			}
		});
	}
	
	public void setupHideKeyboardOnClickOutside(View v) {
        if(!(v instanceof EditText)) {
            v.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v1, MotionEvent event) {
					InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					EditText etUserIdOfReceiver = (EditText) rootView.findViewById(R.id.et_user_id_of_receiver);
					EditText etPaypalCurrencyToSend = (EditText) rootView.findViewById(R.id.et_paypal_currency_to_send);
					if (etUserIdOfReceiver != null)
						imm.hideSoftInputFromWindow(etUserIdOfReceiver.getWindowToken(), 0);
					else if (etPaypalCurrencyToSend != null)
						imm.hideSoftInputFromWindow(etPaypalCurrencyToSend.getWindowToken(), 0);
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