package com.wilson.tasker.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.util.Log;

import com.wilson.tasker.events.BatteryLevelEvent;
import com.wilson.tasker.events.SceneDeactivatedEvent;
import com.wilson.tasker.manager.ApplicationManager;
import com.wilson.tasker.manager.BatteryLevelMonitor;
import com.wilson.tasker.manager.SceneManager;
import com.wilson.tasker.model.Condition;
import com.wilson.tasker.model.Event;
import com.wilson.tasker.model.Scene;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;

public class WorkerService extends Service {
	private static final String TAG = "WorkerService";

	private static final Set<Integer> PASSIVE_POLLING_CONDITIONS = new HashSet<>(Arrays.asList(new Integer[] {
		Event.EVENT_BATTERY_LEVEL, Event.EVENT_TOP_APP_CHANGED,
	}));

	private ServiceHandler handler;

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate()");
		// 新开一个后台线程执行定时任务
		HandlerThread thread = new HandlerThread("worker_thread", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		handler = new ServiceHandler(thread.getLooper());
		EventBus.getDefault().register(this);
		scheduleSelf();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy()");
		EventBus.getDefault().unregister(this);
	}

	/**
	 * 重复调度执行WorkerService，间隔5秒
	 */
	private void scheduleSelf() {
		AlarmManager alarmMgr =  (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(this, WorkerService.class);
		PendingIntent pi = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, 5000, pi);
	}

	public void onEvent(final Event event) {
		Log.d(TAG, "onEvent [" + Event.eventCodeToString(event.eventCode) + "]");

		handler.post(new Runnable() {
			@Override
			public void run() {
				// 处理EVENT_SCENE_DEACTIVATED事件
				if (event.eventCode == Event.EVENT_SCENE_DEACTIVATED) {
					SceneManager.getInstance().handleSceneDeactivated(((SceneDeactivatedEvent) event).scene);
					return;
				}
				List<Scene> interestedScenes = SceneManager.getInstance().findScenesByEvent(event.eventCode);
				if (interestedScenes.size() <= 0) {
					return;
				}
				for (Scene s : interestedScenes) {
					if (s.state == Scene.STATE_DISABLED) {
						continue;
					}
					s.dispatchEvent(event);
				}
			}
		});
	}

	private void checkPassiveConditions() {
		for (Scene s : SceneManager.getInstance().getScenes()) {
			if (s.state != Scene.STATE_DISABLED) {
				for (Condition c : s.conditions) {
					if (PASSIVE_POLLING_CONDITIONS.contains(c.eventCode)) {
						if (c.eventCode == Event.EVENT_BATTERY_LEVEL) {
							float currBatteryLevel = BatteryLevelMonitor.getsInstance(this).getCurrentBatteryLevel();
							EventBus.getDefault().post(new BatteryLevelEvent(currBatteryLevel));
						} else if (c.eventCode == Event.EVENT_TOP_APP_CHANGED) {
							ApplicationManager.getInstance(this).getCurrTopApp();
						}
					}
				}
			}
		}
	}
}
