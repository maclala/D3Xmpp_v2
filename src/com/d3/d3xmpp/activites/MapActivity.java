/**
 * 
 */
package com.d3.d3xmpp.activites;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.d3.d3xmpp.R;
import com.d3.d3xmpp.d3View.D3View;

/**
 * @author MZH
 *
 */
public class MapActivity extends BaseActivity {
	@D3View(click="onClick") ImageView leftBtn;
	MapView mMapView = null;  
	BaiduMap mBaiduMap ;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.acti_map);
		double lat = getIntent().getDoubleExtra("lat", 0.0);
		double lon = getIntent().getDoubleExtra("lon", 0.0);
		
		mMapView = (MapView) findViewById(R.id.bmapView);  
		mBaiduMap =  mMapView.getMap();
		// ������λͼ��  
		mBaiduMap.setMyLocationEnabled(true);  
		// ���춨λ����  
		System.out.println("rttttttt,"+lat+","+lon);
		MyLocationData locData = new MyLocationData.Builder()  
		    .accuracy((float)40.0)  
		    // �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360  
		    .direction(100).latitude(lat)  
		    .longitude(lon).build();  
		// ���ö�λ����  
		mBaiduMap.setMyLocationData(locData);  
		LatLng ll = new LatLng(lat,lon);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
//		 ������Ҫ��λͼ��ʱ�رն�λͼ��  
	}
	
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.leftBtn:
			finish();
			break;
		}
	}
	
	 @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
    }  
    @Override  
    protected void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
        }  
}
