/**
 * 
 */
package com.d3.d3xmpp.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.d3.d3xmpp.R;
import com.d3.d3xmpp.activites.ChatActivity;
import com.d3.d3xmpp.constant.Constants;
import com.d3.d3xmpp.constant.MyApplication;
import com.d3.d3xmpp.d3View.D3Fragment;
import com.d3.d3xmpp.d3View.D3View;
import com.d3.d3xmpp.model.Friend;
import com.d3.d3xmpp.model.User;
import com.d3.d3xmpp.util.LoadThreadNoDialog;
import com.d3.d3xmpp.util.Tool;
import com.d3.d3xmpp.xmpp.XmppConnection;

/**
 * @author MZH
 * 
 */
public class AdrFragment extends D3Fragment {
	@D3View(click = "onClick")
	Button resetBtn, searchSureBtn, searchBtn;
	@D3View
	public LinearLayout searchView;
	@D3View
	EditText searchText;
	MapView mMapView = null;
	BaiduMap mBaiduMap;
	public LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;
	boolean isFirstLoc = true;
	List<Overlay> markers = new ArrayList<Overlay>();
	public Timer timer1 = new Timer();
	public Timer timer2 = new Timer();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SDKInitializer.initialize(getActivity().getApplicationContext());
		View view = setContentView(inflater, R.layout.acti_adr);

		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// ������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		// ��λ��ʼ��
		mLocClient = new LocationClient(getActivity());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		initFriendAdr();

		timer1.schedule(new TimerTask() { // 1���ʼ��5����ˢ��һ��
					@Override
					public void run() {
						initFriendAdr();
					}
				}, 1000, Constants.UPDATE_TIME);

		timer2.schedule(new TimerTask() { // 1���ʼ��5����ˢ��һ��
					@Override
					public void run() {
						mLocClient.start();
					}
				}, 1000, Constants.ADR_UPDATE_TIME);
		return view;
	}

	public void initFriendAdr() {
		for (Overlay marker : markers) {
			marker.remove();
		}

		for (final Friend friend : XmppConnection.getInstance()
				.getFriendBothList()) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("userId", friend.username);

			// new LoadThreadNoDialog(getActivity(),Constants.URL_GET_ADR,map) {

			// @Override
			// protected void refreshUI(String result) {
			// String[] latAndlon = result.split(",");

			// double lat = Double.valueOf(latAndlon[0]);
			// double lon = Double.valueOf(latAndlon[1]);
			double lat = 60;
			double lon = 120;

			if (lat != 4.9E-324 && lon != 4.9E-324) {
				LatLng point = new LatLng(lat, lon);
				BitmapDescriptor bitmap = BitmapDescriptorFactory
						.fromResource(R.drawable.icon_gcoding);
				// ����Markerͼ��
				OverlayOptions option = new MarkerOptions().position(point)
						.icon(bitmap).title(friend.username);

				markers.add(mBaiduMap.addOverlay(option));
				mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(final Marker marker) {
						new AlertDialog.Builder(getActivity())
								.setTitle("��ʾ")
								.setMessage(
										"���Ǻ���" + marker.getTitle()
												+ ",Ҫ��Ta������")
								.setPositiveButton(
										"��",
										new android.content.DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												Intent intent = new Intent();
												intent.setClass(getActivity(),
														ChatActivity.class);
												intent.putExtra("chatName",
														marker.getTitle());
												startActivity(intent);
											}
										}).setNegativeButton("��", null).show();
						return false;
					}
				});
			}
		}
	};

	// }
	// }

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.resetBtn:
			isFirstLoc = true;
			if (!mLocClient.isStarted()) {
				mLocClient.start();
			}
			break;

		case R.id.searchBtn:
			searchView.setVisibility(View.VISIBLE);
			break;

		case R.id.searchSureBtn:
			String searchString = searchText.getText().toString();
			if (searchString.equals("")) {
				searchView.setVisibility(View.GONE);
			} else {
				if (XmppConnection.getInstance().getFriendBothList()
						.contains(new Friend(searchString))) {
					User user = new User(XmppConnection.getInstance()
							.getUserInfo(searchString));
					if (user.lat != 4.9E-324 && user.lon != 4.9E-324) {
						Tool.initToast(getActivity().getApplicationContext(),
								"�ҵ����ѣ������Ƶ�TA��λ��..");
						LatLng ll = new LatLng(user.lat, user.lon);
						MapStatusUpdate u = MapStatusUpdateFactory
								.newLatLng(ll);
						mBaiduMap.animateMapStatus(u);
						searchView.setVisibility(View.GONE);
					} else {
						Tool.initToast(getActivity().getApplicationContext(),
								"���ߺ���û��������ֵ�Ŷ");
					}
				} else {
					Tool.initToast(getActivity().getApplicationContext(),
							"û���������Ŷ");
				}
			}
			break;

		default:
			break;
		}
	}

	/**
	 * ��λSDK��������
	 */
	class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;
			MyApplication.lat = location.getLatitude();
			MyApplication.lon = location.getLongitude();

			if (MyApplication.sharedPreferences.getBoolean("isShare", true)) {
				MyApplication.getInstance().uploadAdr();
			} else {
				mLocClient.stop();
			}

			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
			mLocClient.stop();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	public void onDestroy() {
		mLocClient.stop();
		super.onDestroy();
	}
}
