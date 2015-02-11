package com.wilson.tasker.ui;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.wilson.tasker.R;
import com.wilson.tasker.conditions.OrientationCondition;
import com.wilson.tasker.manager.AirplaneModeEnabler;
import com.wilson.tasker.manager.ApplicationManager;
import com.wilson.tasker.manager.BatteryLevelMonitor;
import com.wilson.tasker.manager.BluetoothEnabler;
import com.wilson.tasker.manager.DisplayManager;
import com.wilson.tasker.manager.JobScheduler;
import com.wilson.tasker.manager.OrientationManager;
import com.wilson.tasker.manager.PhoneCallManager;
import com.wilson.tasker.manager.RingtoneManager;
import com.wilson.tasker.manager.SmsManager;
import com.wilson.tasker.manager.TaskManager;
import com.wilson.tasker.manager.WifiEnabler;
import com.wilson.tasker.model.Condition;
import com.wilson.tasker.model.Task;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

public class TestActivity extends Activity {
	private static final String TAG = "Tasker";

	private Switch wifi;
	private Switch bluetooth;
	private Switch airplaneMode;
	private SeekBar brightness;
	private Button wallpaper;
	private EditText timeOut;
	private Button screenOffTimeOut;
	private Switch trackApp;
	private Button battery;
	private Switch scheduleOneshot;
	private Switch scheduleFixedRate;
	private Switch orientation;
	private Switch caller;
	private Switch sms;
	private Button ringtone;

	OrientationManager orientationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		wifi = (Switch) findViewById(R.id.wifi);
		bluetooth = (Switch) findViewById(R.id.bluetooth);
		airplaneMode = (Switch) findViewById(R.id.airplane_mode);
		brightness = (SeekBar) findViewById(R.id.brightness);
		wallpaper = (Button) findViewById(R.id.set_wallpaper);

		timeOut = (EditText) findViewById(R.id.timeout);
		screenOffTimeOut = (Button) findViewById(R.id.screen_off_timeout);
		trackApp = (Switch) findViewById(R.id.track_top_app);
		battery = (Button) findViewById(R.id.battery_level);
		scheduleOneshot = (Switch) findViewById(R.id.schedule_one_shot_job);
		scheduleFixedRate = (Switch) findViewById(R.id.schedule_fixed_rate_job);
		orientation = (Switch) findViewById(R.id.orientation);
		caller = (Switch) findViewById(R.id.caller);
		sms = (Switch) findViewById(R.id.sms);
		ringtone = (Button) findViewById(R.id.ringtone);

		final WifiEnabler wifiEnabler = WifiEnabler.getsInstance(this);
		final BluetoothEnabler bluetoothEnabler = BluetoothEnabler.getsInstance(this);
		final AirplaneModeEnabler airplaneModeEnabler = AirplaneModeEnabler.getInstance(this);
		final DisplayManager displayManager = DisplayManager.getsInstance(this);
		final BatteryLevelMonitor batteryLevelMonitor = BatteryLevelMonitor.getsInstance(this);

		wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				wifiEnabler.setWifiEnabled(b);
			}
		});

		bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				bluetoothEnabler.setBluetoothEnabled(b);
			}
		});

		airplaneMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				airplaneModeEnabler.setAirplaneModeOn(b);
			}
		});

		brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				Log.d(TAG, "progress=" + i);
				int newBrightness = (int) (20 + (255 - 20) * i * 0.01f);
				displayManager.setBrightness(TestActivity.this, newBrightness);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		wallpaper.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					displayManager.setWallpaper(TestActivity.this.getAssets().open("wallpaper.jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		screenOffTimeOut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				int time = Integer.parseInt(timeOut.getText().toString());
				Log.d(TAG, "timeout=" + time);
				displayManager.setScreenOffTimeout(time);
			}
		});

		battery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				float percentage = batteryLevelMonitor.getCurrentBatteryLevel();
				Log.d("DEBUG", "current battery level=" + percentage);
			}
		});

		final Runnable oneshot = new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "oneshot job");
			}
		};
		final Runnable fixedRate = new Runnable() {
			@Override
			public void run() {
				Log.d(TAG, "fixed rate job");
			}
		};

		scheduleOneshot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					JobScheduler.getInstance().schedule(oneshot, 5, TimeUnit.SECONDS);
				} else {
					JobScheduler.getInstance().removeCallback(oneshot);
				}
			}
		});
		
		scheduleFixedRate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					JobScheduler.getInstance().scheduleAtFixRate(fixedRate, 5, TimeUnit.SECONDS);
				} else {
					JobScheduler.getInstance().removeCallback(fixedRate);
				}
			}
		});

		orientationManager = OrientationManager.getsInstance(this);
		orientation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					orientationManager.register();
				} else {
					orientationManager.unregister();
				}
			}
		});

		final PhoneCallManager phoneCallManager = PhoneCallManager.getsInstance(this);
		caller.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					phoneCallManager.register();
				} else {
					phoneCallManager.unregister();
				}
			}
		});

		final SmsManager smsManager = SmsManager.getsInstance(this);
		sms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					smsManager.register();
				} else {
					smsManager.unregister();
				}
			}
		});

		final RingtoneManager ringtoneManager = RingtoneManager.getsInstance(this);
		ringtone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = RingtoneManager.obtainSetSoundIntent(android.media.RingtoneManager.TYPE_RINGTONE);
				TestActivity.this.startActivityForResult(intent, RingtoneManager.REQUEST_CODE_SET_RINGTONE);
			}
		});

		final ApplicationManager am = ApplicationManager.getInstance(this);
		trackApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					am.startTracking();
				} else {
					am.stopTracking();
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == RingtoneManager.REQUEST_CODE_SET_RINGTONE && resultCode == RESULT_OK) {
			// Set ringtone to the returned Uri
			Uri uri = data.getParcelableExtra(android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if (uri != null) {
				String ringTonePath = uri.toString();
				android.media.RingtoneManager.setActualDefaultRingtoneUri(this,
						android.media.RingtoneManager.TYPE_RINGTONE, uri);
				Log.d(TAG, "ringtone Uri=" + ringTonePath);
			} else {
				Settings.System.putString(this.getContentResolver(), Settings.System.RINGTONE, null);
				Log.d(TAG, "silence selected");
			}
		} else if (requestCode == RingtoneManager.REQUEST_CODE_SET_NOTIFICATION && resultCode == RESULT_OK) {
			// Set notification to the returned Uri
			Uri uri = data.getParcelableExtra(android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if (uri != null) {
				String notiPath = uri.toString();
				android.media.RingtoneManager.setActualDefaultRingtoneUri(this,
						android.media.RingtoneManager.TYPE_NOTIFICATION, uri);
				Log.d(TAG, "ringtone Uri=" + notiPath);
			} else {
				Settings.System.putString(this.getContentResolver(), Settings.System.NOTIFICATION_SOUND, null);
				Log.d(TAG, "silence selected");
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		TaskManager.getInstance().onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		TaskManager.getInstance().onStop();
	}
}