package com.d3.d3xmpp.xmpp;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.packet.DelayInformation;

import android.content.Intent;
import android.util.Log;

import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.dao.MsgDbHelper;
import com.d3.d3xmpp.dao.NewMsgDbHelper;
import com.d3.d3xmpp.model.ChatItem;
import com.d3.d3xmpp.model.Room;
import com.d3.d3xmpp.util.DateUtil;
import com.d3.d3xmpp.util.FileUtil;
import com.d3.d3xmpp.util.MyAndroidUtil;


public class XmppMessageListener implements PacketListener {

	@Override
	public void processPacket(Packet packet) {
		final Message nowMessage = (Message) packet;
		if(Constants.IS_DEBUG)
		Log.e("xmppchat Chat come", nowMessage.toXML());

		if (nowMessage.toXML().contains("<invite")) {
			String noti = "�㱻�������Ⱥ��"+XmppConnection.getRoomName(nowMessage.getFrom());
			MyAndroidUtil.showNoti(noti);
			String userName = XmppConnection.getRoomName(nowMessage.getFrom());
			ChatItem msg =  new ChatItem(ChatItem.GROUP_CHAT,userName,userName, "", noti, DateUtil.now_MM_dd_HH_mm_ss(), 0);
			NewMsgDbHelper.getInstance(MyApplication.getInstance()).saveNewMsg(userName);
			MsgDbHelper.getInstance(MyApplication.getInstance()).saveChatMsg(msg);
			MyApplication.getInstance().sendBroadcast(new Intent("ChatNewMsg"));
		
           XmppConnection.getInstance().joinRoom(XmppConnection.getInstance().getConnection().getUser(), "123456",userName);
		}
		
		
		//�������Է�����������֪ͨ
		
		if(Constants.SERVER_NAME.equals(nowMessage.getFrom())){
			MyAndroidUtil.showNoti(nowMessage.getBody());
         return;
		}
		
		
		Type type = nowMessage.getType();
		System.out.print("================Type=============="+type);
		if ((type == Message.Type.groupchat || type == Message.Type.chat)&&!nowMessage.getBody().equals("")) { 
			String chatName = "";
			String userName = "";
			int chatType = ChatItem.CHAT;
			
			//name
			if (type == Message.Type.groupchat) {
				chatName = XmppConnection.getRoomName(nowMessage.getFrom());
				userName = XmppConnection.getRoomUserName(nowMessage.getFrom());
				chatType = ChatItem.GROUP_CHAT;
			}
			else {
				chatName = userName = XmppConnection.getUsername(nowMessage.getFrom());
			}
			
			if (!userName.equals(Constants.USER_NAME)) {  //�����Լ�������,��Ⱥ��
				//time
				String dateString;
				DelayInformation inf = (DelayInformation) nowMessage.getExtension("x",
						"jabber:x:delay");
				if (inf == null)
					dateString = DateUtil.now_MM_dd_HH_mm_ss();
				else
					dateString = DateUtil.dateToStr_MM_dd_HH_mm_ss(inf.getStamp());
				
				//msg
				ChatItem msg = null;
				String msgBody;                     //�ж��Ƿ�ͼƬ
				if (nowMessage.getProperty("imgData") != null) {
					if(FileUtil.getType(nowMessage.getBody()) == FileUtil.SOUND)
						msgBody = Constants.SAVE_SOUND_PATH + "/"+ nowMessage.getBody();
					else
						msgBody = Constants.SAVE_IMG_PATH + "/"+ nowMessage.getBody();
					FileUtil.saveFileByBase64(nowMessage.getProperty("imgData").toString(),msgBody );
				}
				else if (nowMessage.getType() == Message.Type.groupchat & nowMessage.getBody().contains(":::")) { //���ȵ�
					String[] msgAndData = nowMessage.getBody().split(":::");
					if(FileUtil.getType(msgAndData[0]) == FileUtil.SOUND)
						msgBody = Constants.SAVE_SOUND_PATH + "/" + msgAndData[0];
					else
						msgBody = Constants.SAVE_IMG_PATH + "/" + msgAndData[0];
					FileUtil.saveFileByBase64(msgAndData[1],msgBody );
				}
				else
					msgBody = nowMessage.getBody();
				
				
				if (type == Message.Type.groupchat && XmppConnection.leaveRooms.contains(new Room(chatName))) {  //����������
					System.out.println("���Ѿ��뿪���������");
				}
				else if (nowMessage.getBody().contains("[RoomChange")) {
//					String leaveRoom = nowMessage.getBody().split(",")[1];
//					String leaveUser = nowMessage.getBody().split(",")[2];
//					XmppConnection.getInstance().getMyRoom()
//					.get(XmppConnection.getInstance().getMyRoom().indexOf(new Room(leaveRoom))).friendList.remove(leaveUser);
					XmppConnection.getInstance().reconnect();
				}
				else{
					msg =  new ChatItem(chatType,chatName,userName, "", msgBody, dateString, 0);
					NewMsgDbHelper.getInstance(MyApplication.getInstance()).saveNewMsg(chatName);
					MsgDbHelper.getInstance(MyApplication.getInstance()).saveChatMsg(msg);
					MyApplication.getInstance().sendBroadcast(new Intent("ChatNewMsg"));
					MyAndroidUtil.showNoti(msgBody);
				}
			}
		}
		
		
		
		
		
		
	}
}
