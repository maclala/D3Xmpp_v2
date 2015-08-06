/**
 * 
 */
package com.d3.d3xmpp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.d3.d3xmpp.model.Room;
import com.d3.d3xmpp.R;

/**
 * @author MZH
 *
 */
public class MyRoomAdapter extends ArrayAdapter<Room> {
	Context context;
	
	public MyRoomAdapter(Context context) {
		super(context, 0);
		this.context = context;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=new ViewHolder();
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.row_room, null);
			viewHolder.nameView = (TextView) convertView.findViewById(R.id.nameView);
			viewHolder.memberMun=(TextView) convertView.findViewById(R.id.memberMun);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();	
		}
		final Room item = getItem(position);
		viewHolder.nameView.setText(item.name);
		viewHolder.memberMun.setText(item.getOccupantsCount()+"");
		return convertView;
	}
	
	
	class ViewHolder{
		private	TextView nameView,memberMun;
		 
		
	}
}
