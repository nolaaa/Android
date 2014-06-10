package com.tippal.drawer;

import com.actionbarsherlock.app.SherlockFragment;
import com.bitmonet.Bitmonet;
import com.bitmonet.BitmonetOAuthStatusListener;
import com.bitmonet.BitmonetPaymentStatusListener;
import com.tippal.app.MainActivity;
import com.tippal.app.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
 
public class Fragment2 extends SherlockFragment implements BitmonetPaymentStatusListener, BitmonetOAuthStatusListener {

	View rootView;
	
	final MainActivity context;
	public Fragment2(MainActivity context) {
		this.context = context;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment2, container, false);
        
        setupUserInterface(rootView);
        
        setupBitcoinCoinBase();
        
        setupHideKeyboardOnClickOutside(rootView);
        
        return rootView;
    }
    
    private void setupUserInterface(final View rootView) {
    	// Authorize CoinBase Button
		Button btnAuthorizeCoinbase = (Button) rootView.findViewById(R.id.btn_Authorize_Coinbase);
		btnAuthorizeCoinbase.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Request Wallet Authorization
				Bitmonet.requestWalletAuthorization(context);
			}
		});
		// Get Receiver BitCoin Address Button
		Button btnGetReceiverBitcoinAddress = (Button) rootView.findViewById(R.id.btn_get_receiver_bitcoin_address);
		btnGetReceiverBitcoinAddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		// Authorize Bitcoin Payment Button
		Button btnAuthorizePayment = (Button) rootView.findViewById(R.id.btn_authorize_coinbase_payment);
		btnAuthorizePayment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText etUserIdOfReceiver = (EditText) rootView.findViewById(R.id.et_user_id_of_receiver);
				EditText etBitcoinCoinbaseCurrencyToSend = (EditText) rootView.findViewById(R.id.et_bitcoin_coinbase_currency_to_send);
				final String sUserIdOfReceiver = etUserIdOfReceiver.getText().toString();
				final String sBitcoinCoinbaseCurrencyToSend = etBitcoinCoinbaseCurrencyToSend.getText().toString();
				if (sUserIdOfReceiver.isEmpty() || sBitcoinCoinbaseCurrencyToSend.isEmpty()) { // 1. Check if fields are empty and notify user.
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			        builder.setMessage("Provide a Receiver Address and Amount to send.")
			               .setPositiveButton(R.string.dialog_alert_exit, null);
			        builder.show();
				}
				else { // 2. Everything is OK so we ask user if everything is OK. :)
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			        builder.setMessage("Do you really want to send " + sBitcoinCoinbaseCurrencyToSend + " BTC to " + sUserIdOfReceiver)
			               .setPositiveButton(R.string.dialog_alert_confirm, new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                	   // Set the address where you want to send your Bitcoins
			                	   Bitmonet.setReceivingAddress(sUserIdOfReceiver);
			                	   
			                	   // Set the transaction currency
			                	   Bitmonet.setTransactionCurrency("BTC");
			                	   
			                	   // Send money and ask the user for a confirmation
			                	   Bitmonet.sendMoney(context, sBitcoinCoinbaseCurrencyToSend + " BTC", Double.parseDouble(sBitcoinCoinbaseCurrencyToSend));
			                   }
			               })
			               .setNegativeButton(R.string.dialog_alert_exit, null);
			        builder.show();
				}
			}
		});
	}

	private void setupBitcoinCoinBase() {
		// Initialize the Bitmonet SDK
		Bitmonet.initialize(context, "7eec10be5fcfc52d6ccb4ae46d81bf46bfc7d213a7e6f87ba17016fe8492ece5", "915200985e843b6b52fa636e95034c36180a32a3f6f54861b9938ba9f3ac1488", "https://coinbase.com");
	}

	@Override
	public void walletOAuthStatusListener(boolean status) {
		Toast.makeText(context, "OAuth Complete: " + String.valueOf(status), Toast.LENGTH_LONG).show();	
	}

	@Override
	public void paymentSuccess(String hash) {
		Toast.makeText(context, "Transaction Hash: " + hash, Toast.LENGTH_LONG).show();
	}

	@Override
	public void paymentFailure(String[] error) {
		String displayError = "";
		for (int i = 0; i < error.length; i++)
			displayError = displayError + error[i] + " ";
		Toast.makeText(context, "Errors: " + displayError, Toast.LENGTH_LONG).show();
	}
	
	public void setupHideKeyboardOnClickOutside(View v) {
        if(!(v instanceof EditText)) {
            v.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v1, MotionEvent event) {
					InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					EditText etUserIdOfReceiver = (EditText) rootView.findViewById(R.id.et_user_id_of_receiver);
					EditText etBitcoinCoinbaseCurrencyToSend = (EditText) rootView.findViewById(R.id.et_bitcoin_coinbase_currency_to_send);
					if (etUserIdOfReceiver != null)
						imm.hideSoftInputFromWindow(etUserIdOfReceiver.getWindowToken(), 0);
					else if (etBitcoinCoinbaseCurrencyToSend != null)
						imm.hideSoftInputFromWindow(etBitcoinCoinbaseCurrencyToSend.getWindowToken(), 0);
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