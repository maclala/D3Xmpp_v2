/**
 * 
 */
package com.d3.d3xmpp.activites;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.d3.d3xmpp.R;
import com.d3.d3xmpp.adapter.ChatAdapter;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.d3View.D3Activity;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.d3View.MyListView;
import com.d3.d3xmpp.d3View.MyListView.OnRefreshListener;
import com.d3.d3xmpp.d3View.RecordButton;
import com.d3.d3xmpp.d3View.RecordButton.OnFinishedRecordListener;
import com.d3.d3xmpp.d3View.expression.ExpressionListener;
import com.d3.d3xmpp.d3View.expression.ExpressionView;
import com.d3.d3xmpp.dao.MsgDbHelper;
import com.d3.d3xmpp.dao.NewMsgDbHelper;
import com.d3.d3xmpp.model.ChatItem;
import com.d3.d3xmpp.util.FileUtil;
import com.d3.d3xmpp.util.ImageUtil;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.xmpp.XmppConnection;

/**
 * @author MZH
 *
 */
@SuppressLint("NewApi")
public class ChatActivity extends D3Activity {
	@D3View(click="onClick") ImageView leftBtn,rightBtn,sendBtn,picBtn,expBtn,moreBtn;
	@D3View(click="onClick") Button takePicBtn,chosePicBtn,fileBtn,adrBtn;
	@D3View LinearLayout moreLayout;
	@D3View RecordButton recordBtn;
	@D3View ExpressionView expView;
	@D3View EditText msgText;
	@D3View TextView titleView;
	@D3View MyListView listView;
	private ChatAdapter adapter;
	private List<ChatItem> chatItems = new ArrayList<ChatItem>();
	private UpMessageReceiver mUpMessageReceiver;
	private String chatName;   //Ⱥ��ʱ����Ⱥ����
	private int chatType = ChatItem.CHAT;
	public LocationClient mLocationClient;
	public static boolean isExit = false;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.acti_chat);
		chatName = getIntent().getStringExtra("chatName");
		chatType = getIntent().getIntExtra("chatType",ChatItem.CHAT);
		initView();
		initData();
		titleView.setText(chatName);
		mLocationClient = new LocationClient(this.getApplicationContext());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				//Receive Location 
				String adr = "[/a0,"+location.getLatitude() +"," + location.getLongitude();
				try {
					XmppConnection.getInstance().sendMsg(adr,chatType);
				} catch (Exception e) {
					autoSendIfFail(adr);
					e.printStackTrace();
				}
				Log.i("BaiduLocationApiDem", adr);
//				Tool.initToast(getApplicationContext(), adr);
				mLocationClient.stop();
				MyApplication.lat = location.getLatitude();
				MyApplication.lon = location.getLongitude();
			}
		});
	}
	
	private void initView() {
		adapter = new ChatAdapter(getApplicationContext(), chatName);
		listView.setAdapter(adapter);
		listView.setonRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0);
					}
				}).start();
			}
		});
		
		msgText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				expView.setVisibility(View.GONE);
			}
		});
		msgText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if("".equals(msgText.getText().toString())){
					sendBtn.setImageResource(R.drawable.icon_voice);
				}
				else
					sendBtn.setImageResource(R.drawable.icon_send_w);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		
		recordBtn.setOnFinishedRecordListener(new OnFinishedRecordListener() {
			@Override
			public void onFinishedRecord(String audioPath, int time) {
				if (audioPath != null) {
					try {
						XmppConnection.getInstance().sendMsgWithParms(FileUtil.getFileName(audioPath), 
								new String[]{"imgData"}, new Object[]{ImageUtil.getBase64StringFromFile(audioPath)},chatType);
					} catch (Exception e) {
						autoSendIfFail(FileUtil.getFileName(audioPath),new String[]{"imgData"}, new Object[]{ImageUtil.getBase64StringFromFile(audioPath)});
						e.printStackTrace();
					}
				} else {
					Tool.initToast(ChatActivity.this, "����ʧ��");
				}

			}
		});
		expView.setEditText(msgText);
		expView.setGifListener(new ExpressionListener() {
			@Override
			public void clickGif(String msg) {
				try {
					XmppConnection.getInstance().sendMsg(msg,chatType);
				} catch (Exception e) {
					autoSendIfFail(msg);
					e.printStackTrace();
				}
			}
		});
		// �Ự���ݸı䣬���ܹ㲥
		mUpMessageReceiver = new UpMessageReceiver();
		registerReceiver(mUpMessageReceiver, new IntentFilter("ChatNewMsg"));
		registerReceiver(mUpMessageReceiver, new IntentFilter("LeaveRoom"));
		
		XmppConnection.getInstance().setRecevier(chatName,chatType);
	}
	
	private void initData(){
		chatItems = MsgDbHelper.getInstance(getApplicationContext()).getChatMsg(chatName);
		adapter.clear();
		adapter.addAll(chatItems);
		listView.setSelection(adapter.getCount() + 1);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				listView.onRefreshComplete();
				List<ChatItem> moreChatItems = MsgDbHelper.getInstance(getApplicationContext()).getChatMsgMore(
						listView.getCount()-1, chatName);
				for (int i = 0; i < moreChatItems.size(); i++) {
					chatItems.add(i, moreChatItems.get(i));
				}
				adapter.clear();
				adapter.addAll(chatItems);
				adapter.notifyDataSetChanged();
			}
		}
	};
	
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
			
		case R.id.rightBtn:
			Intent intent = new Intent();
			if (chatType == ChatItem.CHAT) {
				intent.setClass(getApplicationContext(), FriendActivity.class);
				intent.putExtra("username", chatName);
				startActivity(intent);
			}
			else if(chatType == ChatItem.GROUP_CHAT){
				intent.setClass(getApplicationContext(), RoomMemActivity.class);
				intent.putExtra("roomName", chatName);
				startActivity(intent);
			}
			break;
			
		case R.id.sendBtn:
			String msg = msgText.getText().toString(); // ��ȡtext�ı�
			if(!msg.isEmpty()){     //�ı���Ϊ�գ�ֱ�ӷ��ı���Ϣ
				try {
					XmppConnection.getInstance().sendMsg(msg,chatType);
				} catch (Exception e) {
					autoSendIfFail(msg);
					e.printStackTrace();
				}
				msgText.setText("");
			}
			else if(recordBtn.getVisibility() == View.GONE){   //�ı�Ϊ�գ����ı�����ģʽ�л�����������ģʽ
				msgText.setVisibility(View.GONE);
				recordBtn.setVisibility(View.VISIBLE);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(msgText.getWindowToken(), 0);
				sendBtn.setImageResource(R.drawable.icon_keyboard);
			}
			else if(msgText.getVisibility() == View.GONE){ //�ı�Ϊ�գ�����������ģʽ�л����ı�����ģʽ
				msgText.setVisibility(View.VISIBLE);
				recordBtn.setVisibility(View.GONE);
				sendBtn.setImageResource(R.drawable.icon_voice);
			}
			break;
			

		case R.id.msgText:   //ѡ���ı��������¼�������һ��
			expView.setVisibility(View.GONE);
			listView.setSelection(adapter.getCount()); // ȥ�����һ��
			break;	
			
			
		case R.id.moreBtn:    //�������෢������,ѡͼ�����࣬����λ��
			if (moreLayout.getVisibility() == View.GONE) {
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				moreLayout.setVisibility(View.VISIBLE);
				expView.setVisibility(View.GONE);
			} else {
				moreLayout.setVisibility(View.GONE);
			}
			break;

		case R.id.takePicBtn:    //����
			Intent intent1 = new Intent();
			CropImageActivity.isAutoSend = true;
			intent1.setClass(this, PicSrcPickerActivity.class);
			intent1.putExtra("type", PicSrcPickerActivity.TAKE_PIC);
			startActivityForResult(intent1,PicSrcPickerActivity.CROP);
			break;

		case R.id.chosePicBtn:   //ͼ��
			Intent intent2 = new Intent();
			CropImageActivity.isAutoSend = true;
			intent2.setClass(this, PicSrcPickerActivity.class);
			intent2.putExtra("type", PicSrcPickerActivity.CHOSE_PIC);
			startActivityForResult(intent2,PicSrcPickerActivity.CROP);
			break;
			
		case R.id.adrBtn:   //����λ��
			mLocationClient.start();
			break;
			
		case R.id.expBtn:  //�������icon
			if (expView.getVisibility() == View.GONE) {
				InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				expView.setVisibility(View.VISIBLE);
				moreLayout.setVisibility(View.GONE);
			} else {
				expView.setVisibility(View.GONE);
			}
			//Determine the voice
			if(msgText.getVisibility() == View.GONE){
				msgText.setVisibility(View.VISIBLE);
				recordBtn.setVisibility(View.GONE);
				if("".equals(msgText.getText().toString()))
					sendBtn.setImageResource(R.drawable.icon_voice);
				else
					sendBtn.setImageResource(R.drawable.icon_send_w);
			}
			break;
		
			
		default:
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode) {
			switch (requestCode) { //ѡͼ�ص�
			case PicSrcPickerActivity.CROP:
				String imgName = data.getStringExtra("imgName");
				String base64String = data.getStringExtra("base64String");
				if (imgName != null) {
					try {
						XmppConnection.getInstance().sendMsgWithParms(imgName, new String[]{"imgData"}, new Object[]{base64String},chatType);
					} catch (Exception e) {
						autoSendIfFail(imgName,new String[]{"imgData"}, new Object[]{base64String});
						e.printStackTrace();
					}
				}
				break;
				
			default:
				break;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		try {
			adapter.mping.stop();
			if (MyApplication.getInstance() != null) {
				unregisterReceiver(mUpMessageReceiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		clearMsgCount();
		finish();
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		msgText.clearFocus();
		if (isExit) {
			isExit = false;
			finish();
		}
	}
	
	
	private void clearMsgCount() {
		NewMsgDbHelper.getInstance(getApplicationContext()).delNewMsg(chatName);
		MyApplication.getInstance().sendBroadcast(new Intent("ChatNewMsg"));
	}
	
	
	private class UpMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// �յ��V���������ǵĽ���
			if (intent.getAction().equals("LeaveRoom")) {
				finish();
			}
			else{
				initData();
			}
		}
	}
	
	//�����Ƕ��߷���������ʱ�Զ��ط�
	public static boolean isLeaving = false;
	public void autoSendIfFail(final String msg){
		Tool.initToast(MyApplication.getInstance(), "������..");
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {  //1���ʼ
			int count = 0;
			@Override
			public void run() {
				try {
					count++;
					if (!isLeaving) {
						XmppConnection.getInstance().setRecevier(chatName, chatType);
						XmppConnection.getInstance().sendMsg(msg,chatType);
						timer.cancel();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					Log.e("muc", "autosend      "+count);
					if (count > 8) {
						Tool.initToast(MyApplication.getInstance(), "����ʧ��");
						timer.cancel();
					}
				}
			}
		}, 1000,1000);
	}
	
	public void autoSendIfFail(final String msg,final String[] s,final Object[] obj){
		Tool.initToast(MyApplication.getInstance(), "������..");
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {  //1���ʼ
			int count = 0;
			@Override
			public void run() {
				try {
					count++;
					if (!isLeaving) {
						XmppConnection.getInstance().setRecevier(chatName, chatType);
						XmppConnection.getInstance().sendMsgWithParms(msg, s, obj,chatType);
						timer.cancel();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					Log.e("muc", "autosend      "+count);
					if (count > 8) {
						Tool.initToast(MyApplication.getInstance(), "����ʧ��");
						timer.cancel();
					}
				}
			}
		}, 1000,1000);
	}
}
