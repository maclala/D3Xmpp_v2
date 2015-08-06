package com.d3.d3xmpp.xmpp;

import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;

import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.model.Friend;

import android.content.Intent;
import android.util.Log;

public class XmppPresenceInterceptor implements PacketInterceptor {

	@Override
	public void interceptPacket(Packet packet) {
		Presence presence = (Presence) packet;
		if(Constants.IS_DEBUG)
		Log.e("xmppchat send", presence.toXML());

		String from = presence.getFrom();// ���ͷ�
		String to = presence.getTo();// ���շ�
		// Presence.Type��7��״̬
		if (presence.getType().equals(Presence.Type.subscribe)) {// ������������
			for (Friend friend : XmppConnection.getInstance().getFriendList()) {
				System.out.println("�Һ���"+friend.username+"���ҵĹ�ϵ"+friend.type);
				if ((friend.username.equals(XmppConnection.getUsername(to)) && friend.type == ItemType.from)) {
					XmppConnection.getInstance().changeFriend(friend, ItemType.both);
			        Log.e("friend", "����"+to+"������������toBoth");
				}
				else if (friend.username.equals(XmppConnection.getUsername(to))) {
					XmppConnection.getInstance().changeFriend(friend, ItemType.to);
			        Log.e("friend", "����"+to+"������������toTo");
				}
			}
			
			if (!XmppConnection.getInstance().getFriendList().contains(new Friend(XmppConnection.getUsername(to)))) {
				Friend friend  = new Friend(XmppConnection.getUsername(to));
				friend.type = ItemType.to;
				XmppConnection.getInstance().getFriendList().add(friend);
			}
			MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
		} 
		else if (presence.getType().equals(Presence.Type.subscribed)) {// ͬ����Ӻ���
			if(Constants.IS_DEBUG)
	        Log.e("friend", to+"��ͬ��������");
//			MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
		} 
		else if (presence.getType().equals(Presence.Type.unsubscribe)||presence.getType().equals(Presence.Type.unsubscribed)) {// �ܾ���Ӻ��� ɾ������
			if(Constants.IS_DEBUG)
		       Log.e("friend", "��ɾ������"+to);
			for (Friend friend : XmppConnection.getInstance().getFriendList()) {
				if (friend.username.equals(XmppConnection.getUsername(to))) {
					XmppConnection.getInstance().changeFriend(friend, ItemType.remove);
				}
			}
			MyApplication.getInstance().sendBroadcast(new Intent("friendChange"));
		}
	}
}
