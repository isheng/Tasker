package com.wilson.tasker.manager;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

/**
 * 开启/关闭飞行模式
 */
public class AirplaneModeEnabler {

	private Context mContext;
	private static AirplaneModeEnabler sInstance;

	private AirplaneModeEnabler(Context context) {
		mContext = context;
	}

    public static AirplaneModeEnabler getInstance(Context context) {
	    if (sInstance == null) {
		    sInstance = new AirplaneModeEnabler(context);
	    }
	    return sInstance;
    }

    @SuppressWarnings("deprecation")
	public boolean isAirplaneModeOn() {
        return Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }

    @SuppressWarnings("deprecation")
	public void setAirplaneModeOn(boolean enabling) {
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.AIRPLANE_MODE_ON,
                                enabling ? 1 : 0);
        
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enabling);
        mContext.sendBroadcast(intent);
    }
}