package com.wilson.tasker.manager;

import android.content.Context;
import android.util.Log;

import com.wilson.tasker.model.Event;
import com.wilson.tasker.model.Condition;
import com.wilson.tasker.model.Scene;

import java.util.ArrayList;
import java.util.List;

public class SceneManager {
	private static final String TAG = "DEBUG";

	private static SceneManager sInstance = new SceneManager();

	private ArrayList<Scene> scenes;

	public static SceneManager getInstance() {
		return sInstance;
	}

	protected SceneManager() {
		scenes = new ArrayList<>(5);
	}

	public List<Scene> findScenesByEvent(int eventCode) {
		List<Scene> result = new ArrayList<>();
		for (Scene s : scenes) {
			final List<Condition> conditions = s.conditions;
			for (Condition c : conditions) {
				if (c.eventCode == eventCode && result.indexOf(s) == -1) {
					result.add(s);
				}
			}
		}
		return result;
	}

	public boolean handleSceneActivated(Context context, Scene scene) {
		boolean success = scene.runScene(context);
		if (success) {
			Log.d(TAG, "runScene [" + scene.toString() + "] succeeded.");
		} else {
			Log.d(TAG, "runScene [" + scene.toString() + "] failed.");
		}
		return success;
	}

	public void handleSceneDeactivated(Scene scene) {
		// TODO - implements me
	}

	public void addScene(Scene scene, Context context) {
		scenes.add(scene);
		registerManager(context, scene.conditions);
	}

	public void removeScene(Scene scene, Context context) {
		scenes.remove(scene);
		unregisterManager(context, scene.conditions);
	}

	private void registerManager(Context context, List<Condition> conditions) {
		for (Condition c : conditions) {
			switch (c.eventCode) {
				case Event.EVENT_BATTERY_LEVEL:
					if (!BatteryLevelMonitor.getInstance(context).isRegistered()) {
						BatteryLevelMonitor.getInstance(context).register();
					}
					break;
				case Event.EVENT_CALLER:
					if (!PhoneCallManager.getsInstance(context).isRegistered()) {
						PhoneCallManager.getsInstance(context).register();
					}
					break;
				case Event.EVENT_CHARGER:
					if (!BatteryLevelMonitor.getInstance(context).isRegistered()) {
						BatteryLevelMonitor.getInstance(context).register();
					}
					break;
				case Event.EVENT_LOCATION:
					// TODO - implement me
					break;
				case Event.EVENT_ORIENTATION:
					if (!OrientationManager.getsInstance(context).isRegistered()) {
						OrientationManager.getsInstance(context).register();
					}
					break;
				case Event.EVENT_TIME:
					break;
				case Event.EVENT_SMS:
					if (!SmsManager.getsInstance(context).isRegistered()) {
						SmsManager.getsInstance(context).register();
					}
					break;
				case Event.EVENT_TOP_APP_CHANGED:
					break;
			}
		}
	}

	private void unregisterManager(Context context, List<Condition> conditions) {
		// TODO - 通过引用计数控制反注册
//		for (Condition c : conditions) {
//			switch (c.eventCode) {
//				case Event.EVENT_BATTERY_LEVEL:
//					if (BatteryLevelMonitor.getInstance(context).isRegistered()) {
//						BatteryLevelMonitor.getInstance(context).unregister();
//					}
//					break;
//				case Event.EVENT_CALLER:
//					if (PhoneCallManager.getsInstance(context).isRegistered()) {
//						PhoneCallManager.getsInstance(context).unregister();
//					}
//					break;
//				case Event.EVENT_CHARGER:
//					if (BatteryLevelMonitor.getInstance(context).isRegistered()) {
//						BatteryLevelMonitor.getInstance(context).unregister();
//					}
//					break;
//				case Event.EVENT_LOCATION:
//					// TODO - implement me
//					break;
//				case Event.EVENT_ORIENTATION:
//					if (OrientationManager.getsInstance(context).isRegistered()) {
//						OrientationManager.getsInstance(context).unregister();
//					}
//					break;
//				case Event.EVENT_TIME:
//					break;
//				case Event.EVENT_SMS:
//					if (SmsManager.getsInstance(context).isRegistered()) {
//						SmsManager.getsInstance(context).unregister();
//					}
//					break;
//				case Event.EVENT_TOP_APP_CHANGED:
//					break;
//			}
//		}
	}

	public ArrayList<Scene> getScenes() {
		return scenes;
	}

}
