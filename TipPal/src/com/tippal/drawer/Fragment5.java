package com.tippal.drawer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tippal.app.MainActivity;
import com.tippal.app.R;

public class Fragment5 extends SherlockFragment {
	
	View rootView;
	
	private Spinner spinnerCurrency1, spinnerCurrency2;
	
	private EditText etCurrency1, etCurrency2;
	protected TextWatcher twCurrency1, twCurrency2;

	public JsonArray jaCurrencies;
	public JsonObject joCurrencyConversions;
	
	private List<String> listCurrencies = new ArrayList<String>();
	
	MainActivity activity;
	public Fragment5(MainActivity activity) {
		this.activity = activity;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment5, container, false);
        
        setupUserInterface();
        
        requestCurrencyInfo();
        
        setupHideKeyboardOnClickOutside(rootView);
        
        return rootView;
    }

	private void requestCurrencyInfo() {
		HttpClient httpClient = new HttpClient();
    	httpClient.start();
	}

	public void setupCurrencyOptions() {	
		List<String> list = new ArrayList<String>();
		list.add("Bitcoin (BTC)");
		listCurrencies.add("btc");
		for (int i=0; i<jaCurrencies.size(); i++) {
			try {
				JsonArray jaCurrency = (JsonArray) new JsonParser().parse((jaCurrencies.get(i).toString()));
				list.add(jaCurrency.get(0).getAsString());
				listCurrencies.add(jaCurrency.get(1).getAsString());
			} catch (Exception e) {
			}
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, R.layout.spinner_item, list);
		dataAdapter.setDropDownViewResource(R.layout.spinner_item);
		spinnerCurrency1.setAdapter(dataAdapter);
		spinnerCurrency1.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				calculateConversion(true);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		spinnerCurrency2.setAdapter(dataAdapter);
		spinnerCurrency2.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				calculateConversion(false);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}

	private void setupUserInterface() {
		// Currency1 Conversion EditText
		etCurrency1 = (EditText) rootView.findViewById(R.id.et_currency1_conversion);
		etCurrency1.addTextChangedListener(twCurrency1 = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				recursionSolve(true);
			}
		});
		// Currency2 Conversion EditText
		etCurrency2 = (EditText) rootView.findViewById(R.id.et_currency2_conversion);
		etCurrency2.addTextChangedListener(twCurrency2 = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				recursionSolve(false);
			}
		});
		spinnerCurrency1 = (Spinner) rootView.findViewById(R.id.spinner_currency1_conversion);
		spinnerCurrency2 = (Spinner) rootView.findViewById(R.id.spinner_currency2_conversion);
	}
	
	protected void recursionSolve(boolean b) {
		etCurrency1.removeTextChangedListener(twCurrency1);
		etCurrency2.removeTextChangedListener(twCurrency2);
		
		calculateConversion(b);
		
	 	etCurrency1.addTextChangedListener(twCurrency1);
		etCurrency2.addTextChangedListener(twCurrency2);
	}
	
	// true for currency 1 and false for currency 2
	public void calculateConversion(Boolean b) {
		double dCurrency1, dCurrency2;
		String sCurrency1, sCurrency2, sConversion;
		try {
			dCurrency1 = Double.parseDouble(etCurrency1.getText().toString());
		} catch (Exception e) {
			return;
		}
		try {
			dCurrency2 = Double.parseDouble(etCurrency2.getText().toString());
		} catch (Exception e) {
			return;
		}
		try {
			sCurrency1 = listCurrencies.get(spinnerCurrency1.getSelectedItemPosition());
		} catch (Exception e) {
			return;
		}
		try {
			sCurrency2 = listCurrencies.get(spinnerCurrency2.getSelectedItemPosition());
		} catch (Exception e) {
			return;
		}
		try {
			if (b)
				sConversion = sCurrency1.toLowerCase(Locale.getDefault()) + "_to_" + sCurrency2.toLowerCase(Locale.getDefault());
			else
				sConversion = sCurrency2.toLowerCase(Locale.getDefault()) + "_to_" + sCurrency1.toLowerCase(Locale.getDefault());
		
			String sResult = joCurrencyConversions.get(sConversion).getAsString();
			if (b)
				etCurrency2.setText(String.valueOf((double)Math.round((Double.parseDouble(sResult)*dCurrency1)* 100000) / 100000));
			else
				etCurrency1.setText(String.valueOf((double)Math.round((Double.parseDouble(sResult)*dCurrency2)* 100000) / 100000));
		} catch (Exception e) {
			return;
		}
	}
	
	public void setupHideKeyboardOnClickOutside(View v) {
        if(!(v instanceof EditText)) {
            v.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v1, MotionEvent event) {
					InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
					EditText etCurrency1Conversion = (EditText) rootView.findViewById(R.id.et_currency1_conversion);
					EditText etCurrency2Conversion = (EditText) rootView.findViewById(R.id.et_currency2_conversion);
					if (etCurrency1Conversion != null)
						imm.hideSoftInputFromWindow(etCurrency1Conversion.getWindowToken(), 0);
					else if (etCurrency2Conversion != null)
						imm.hideSoftInputFromWindow(etCurrency2Conversion.getWindowToken(), 0);
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
	
	public class HttpClient extends Thread {
		
		@Override
		public void run() {
			try {
				AsyncHttpClient client = new AsyncHttpClient();
				client.get("https://coinbase.com/api/v1/currencies", new AsyncHttpResponseHandler() {
				    @Override
				    public void onSuccess(String response) {
				    	try {
							jaCurrencies = (JsonArray) new JsonParser().parse(response);
							activity.runOnUiThread(new Runnable() {  
			                    @Override
			                    public void run() {
			                    	setupCurrencyOptions();
			                    }
							});
						} catch (Exception e) {
						}
				    }
				});
				client.get("https://coinbase.com/api/v1/currencies/exchange_rates", new AsyncHttpResponseHandler() {
				    @Override
				    public void onSuccess(String response) {
				        try {
							joCurrencyConversions = (JsonObject) new JsonParser().parse(response);
						} catch (Exception e) {
						}
				    }
				});
			} catch (Exception e) {
			}
		}
	}    
}