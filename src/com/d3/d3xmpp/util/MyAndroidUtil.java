package com.d3.d3xmpp.util;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

import com.d3.d3xmpp.R;
import com.d3.d3xmpp.activites.MainActivity;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.d3View.expression.ExpressionUtil;
import com.d3.d3xmpp.dao.NewMsgDbHelper;

public class MyAndroidUtil {
	private static Notification myNoti = new Notification();
	/**
	 * @param context
	 * @param title
	 * @param message
	 * @param icon
	 * @param okBtn
	 * û��ȡ�����ܵ���
	 */
	public static void showDialog(Context context ,String title,String message,int icon,DialogInterface.OnClickListener okBtn){
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setIcon(icon)
		.setMessage(message)
		.setPositiveButton("ȷ��",okBtn)
		.setNegativeButton("����", null).show();
	}
	
	/**
	 * �޸Ļ���
	 * @param name     һ�㶼name+   actid������userId
	 * @param object        Ҫ�����
	 */
	public static void editXml(String name,Object object) {
		Editor editor = MyApplication.sharedPreferences.edit();
		if (MyApplication.sharedPreferences.getString(name, null) != null) {
			editor.remove(name);
		}
		editor.putString(name, JsonUtil.objectToJson(object));
		editor.commit();
	}
	
	
	/**
	 * �޸Ļ���
	 * @param name     һ�㶼name+   actid������userId
	 * @param result        Ҫ�����
	 */
	public static void editXmlByString(String name,String result) {
		Editor editor = MyApplication.sharedPreferences.edit();
		if (MyApplication.sharedPreferences.getString(name, null) != null) {
			editor.remove(name);
		}
		editor.putString(name, result);
		editor.commit();
	}
	
	/**
	 * �޸Ļ���
	 * @param name     һ�㶼name+   actid������userId
	 * @param true or fasle        Ҫ�����
	 */
	public static void editXml(String name,boolean is) {
		Editor editor = MyApplication.sharedPreferences.edit();
		editor.putBoolean(name, is);
		editor.commit();
	}

	
	public static void removeXml(String name){
		Editor editor = MyApplication.sharedPreferences.edit();
		editor.remove(name);
		editor.commit();
	}
	
	public static void clearNoti(){
		myNoti.number = 0;
		NotificationManager manger = (NotificationManager)MyApplication.getInstance()
				.getSystemService(Service.NOTIFICATION_SERVICE);
		manger.cancelAll();   
	}
	
	public static void showNoti(String notiMsg){
		//android����
		if(notiMsg.contains(Constants.SAVE_IMG_PATH))
			myNoti.tickerText = "[ͼƬ]";
		else if(notiMsg.contains(Constants.SAVE_SOUND_PATH))
			myNoti.tickerText = "[����]";
		else if(notiMsg.contains("[/g0"))
			myNoti.tickerText = "[��������]";
		else if(notiMsg.contains("[/f0"))  //�������
			myNoti.tickerText = ExpressionUtil.getText(MyApplication.getInstance(), StringUtil.Unicode2GBK(notiMsg));
		else if(notiMsg.contains("[/a0"))
			myNoti.tickerText = "[λ��]";
		else{
			myNoti.tickerText = notiMsg;
		}
		
		Intent intent = new Intent();   //Ҫ��ȥ�Ľ���
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(MyApplication.getInstance(), MainActivity.class);
		
		NotificationManager mNotificationManager = 
	    		(NotificationManager) MyApplication.getInstance().getSystemService(Service.NOTIFICATION_SERVICE);
		PendingIntent appIntent = PendingIntent.getActivity(MyApplication.getInstance(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		myNoti.icon = R.drawable.ic_launcher;
		myNoti.flags = Notification.FLAG_SHOW_LIGHTS|Notification.FLAG_AUTO_CANCEL;  //�����
		myNoti.ledARGB= 0xff00ff00;           //��ɫ
		myNoti.number = NewMsgDbHelper.getInstance(MyApplication.getInstance()).getMsgCount();
		
		if (MyApplication.sharedPreferences.getBoolean("isShake", true)) {
			myNoti.defaults = Notification.DEFAULT_VIBRATE; // ��
		}
		if (MyApplication.sharedPreferences.getBoolean("isSound", true)) {
			myNoti.defaults = Notification.DEFAULT_SOUND; // ����
		}
		myNoti.setLatestEventInfo(MyApplication.getInstance(), MyApplication.getInstance().getString(R.string.app_name), myNoti.tickerText, appIntent);
		mNotificationManager.notify(0, myNoti);
	}
}
