package com.example.dell.dkcwallet.base;

import android.app.Activity;
import android.content.Context;

import java.util.Stack;

/**
 * Activity管理
 */
public class ActMgrs {

	private static Stack<Activity> activityStack;
	private static ActMgrs instance;

	private ActMgrs() {
	}

	public static class SingleHolde{
		private final static ActMgrs instance = new ActMgrs();
	}
	public static ActMgrs getInstance() {
		return SingleHolde.instance;
	}

	/**
	 * 移除最上层Activity
	 */
	public void popActivity() {
		Activity activity = null;
		try {
			activity = activityStack.lastElement();
		} catch (Exception e) {
		}
		if (activity != null) {
			activity.finish();
			activityStack.remove(activity);
			activity = null;// 消除引用关系，便于系统GC
		}
	}
	/**
	 * 移除最上层Activity
	 */
	public void popActivityWithoutFinish() {
		Activity activity = null;
		try {
			activity = activityStack.lastElement();
		} catch (Exception e) {
		}
		if (activity != null) {
			activityStack.remove(activity);
			activity = null;// 消除引用关系，便于系统GC
		}
	}
	public void popActivityWithoutFinish(Activity activity) {
		try {
			activityStack.remove(activity);
		} catch (Exception e) {
		}
	}

	/**
	 * 移除指定的Activity
	 */
	public void popActivity(Activity activity) {
		if (activity != null) {
			activity.finish();
			activityStack.remove(activity);
			activity = null;// 消除引用关系，便于系统GC
		}
	}

	/**
	 * 得到当前栈中最上面的Activity
	 */
	public Activity currentActivity() {
		Activity activity = null;
		try {
			activity = activityStack.lastElement();
		} catch (Exception e) {
		}

		return activity;
	}

	/**
	 * 判断要弹对话框的act是不是在栈的最上面,防止出现act已经被销毁后弹对话框出现的异常退出
	 * 
	 * @param cls
	 * @return
	 */
	public boolean isExistAct(Class<?> cls) {
		if (currentActivity().getClass().equals(cls)) {
			return true;
		}
		return false;
	}

	/**
	 * Activity进栈
	 */
	public void pushActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		if (activityStack.contains(activity)) {
			return;
		}
		activityStack.add(activity);
	}

	/**
	 * 移除指定的Activity的上面Activity 回到指定的Activity
	 */
	public void popAllActivityExceptOne(Class<?> cls) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(cls)) {
				break;
			}
			popActivity(activity);
		}
	}

	/**
	 * 清除所有activity
	 */
	public void popAllActivity() {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				activityStack = null;
				break;
			}
			popActivity(activity);
		}

	}

	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
			popAllActivity();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

}
