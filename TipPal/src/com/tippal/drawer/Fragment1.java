package com.tippal.drawer;

import com.actionbarsherlock.app.SherlockFragment;
import com.tippal.app.R;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
 
public class Fragment1 extends SherlockFragment {
	
	View rootView;
	
	Context context;
	public Fragment1(Context context) {
		this.context = context;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment1, container, false);
        
        setupHideKeyboardOnClickOutside(rootView);
        
        return rootView;
    }
	
	public void setupHideKeyboardOnClickOutside(View v) {
        if(!(v instanceof EditText)) {
            v.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v1, MotionEvent event) {
					InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
					EditText etUserFeedback = (EditText) rootView.findViewById(R.id.etUserFeedback);
					if (etUserFeedback != null)
						imm.hideSoftInputFromWindow(etUserFeedback.getWindowToken(), 0);
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