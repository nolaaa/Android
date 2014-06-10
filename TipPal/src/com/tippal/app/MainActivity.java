package com.tippal.app;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bitmonet.BitmonetOAuthStatusListener;
import com.tippal.app.R;
import com.tippal.drawer.Fragment1;
import com.tippal.drawer.Fragment2;
import com.tippal.drawer.Fragment3;
import com.tippal.drawer.Fragment4;
import com.tippal.drawer.Fragment5;
import com.tippal.drawer.MenuListAdapter;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends SherlockFragmentActivity implements BitmonetOAuthStatusListener {
	
	protected static final String LOGC = "MainActivity";
	
	/*--------- SLIDING DRAWER VARIABLES (Start) ---------*/
	private CharSequence mDrawerTitle;
    private CharSequence mTitle;
	
	public DrawerLayout mDrawerLayout;
    public ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    MenuListAdapter mMenuAdapter;
    
    int[] icon;
    public String[] title, subtitle;
    
    Fragment fragment1, fragment3, fragment4, fragment5;
    Fragment2 fragment2;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);
        
        fragment1 = new Fragment1(getApplicationContext()); // HOME
        fragment2 = new Fragment2(MainActivity.this); // BITCOIN TIP - CoinBase
        fragment3 = new Fragment3(getApplicationContext()); // PAYPAL TIP
        fragment4 = new Fragment4(getApplicationContext()); // TIP Calculator
        fragment5 = new Fragment5(MainActivity.this); // BitCoin Conversion

        mTitle = mDrawerTitle = getTitle(); // Get the Title

        title = new String[] { "Home", "Bitcoin Tip", "PayPal Tip", "Tip Calculator", "Bitcoin Conversion"}; // Titles

        subtitle = new String[] { "Tip Pal", "Coinbase", "PayPal TM", "Calculate the Tip", "Bitcoin Conversion Tool" }; // Subtitles

        icon = new int[] { R.drawable.action_about, R.drawable.action_coinbase, R.drawable.action_paypal, R.drawable.action_tip_calculator, R.drawable.action_bitcoin }; // Icons

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // Locate DrawerLayout in drawer_main.xml
        
        mDrawerList = (ListView) findViewById(R.id.listview_drawer); // Locate ListView in drawer_main.xml

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START); // Set a custom shadow that overlays the main content when the drawer opens

        mMenuAdapter = new MenuListAdapter(MainActivity.this, title, subtitle, icon); // Pass string arrays to MenuListAdapter

        mDrawerList.setAdapter(mMenuAdapter); // Set the MenuListAdapter to the ListView
        
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener()); // Capture ListView menu item click
        
        // Enable ActionBar application icon to behave as action to toggle navigation drawer
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
 
        // ActionBarDrawerToggle ties together the the proper interactions between the sliding drawer and the action bar application icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
        	
        	public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle); // Set the title on the action when drawer open
                super.onDrawerOpened(drawerView);
            }
        	
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {
            selectItem(0);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

	@Override
	 public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            if (mDrawerLayout.isDrawerOpen(mDrawerList))
            	mDrawerLayout.closeDrawer(mDrawerList);
            else
                mDrawerLayout.openDrawer(mDrawerList);
        switch(item.getItemId()){
			case R.id.action_settings:
				if (mDrawerLayout.isDrawerOpen(mDrawerList))
	            	mDrawerLayout.closeDrawer(mDrawerList);
	            else
	                mDrawerLayout.openDrawer(mDrawerList);
				break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position); // ListView click listener in the navigation drawer
        }
    }

    private void selectItem(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (position) { // Locate Position
	        case 0:
	            ft.replace(R.id.content_frame, fragment1);
	            break;
	        case 1:
	            ft.replace(R.id.content_frame, fragment2);
	            break;
	        case 2:
	            ft.replace(R.id.content_frame, fragment3);
	            break;
	        case 3:
	            ft.replace(R.id.content_frame, fragment4);
	            break;
	        case 4:
	            ft.replace(R.id.content_frame, fragment5);
	            break;
		}
        ft.commit();
        mDrawerList.setItemChecked(position, true);
        setTitle(title[position]); // Get the title followed by the position
        mDrawerLayout.closeDrawer(mDrawerList); // Close drawer
    }
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState(); // Sync the toggle state after onRestoreInstanceState has occurred.
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig); 
        mDrawerToggle.onConfigurationChanged(newConfig); // Pass any configuration change to the drawer toggles
    }
 
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void walletOAuthStatusListener(boolean status) {
		if (fragment2 != null) {
			fragment2.walletOAuthStatusListener(status);
		}
	}
	
}
