package com.d3.d3xmpp.d3View;

import java.lang.reflect.Field;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;

/**
 * @author MZH
 *         ��Ӧֻ�̳�Activity������Ҫ����Fragment�ʼ̳�FragmentActivity��FragmentActivity�̳���Activity
 */
public abstract class D3Activity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
	}

	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		initInjectedView(this);
	}

	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		initInjectedView(this);
	}

	public void setContentView(View view) {
		super.setContentView(view);
		initInjectedView(this);
	}

	private void initInjectedView(Activity activity) {
		initInjectedView(activity, activity.getWindow().getDecorView());
	}

	private void initInjectedView(Object activity, View sourceView) {
		// System.out.println("create");
		Field[] fields = activity.getClass().getDeclaredFields(); // ��ȡ�ֶ�
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				try {
					field.setAccessible(true); // ��Ϊ�ɷ���

					if (field.get(activity) != null)
						continue;

					D3View d3View = field.getAnnotation(D3View.class);
					if (d3View != null) {

						int viewId = d3View.id();
						if (viewId == 0)
							viewId = getResources().getIdentifier(field.getName(), "id", getPackageName());
						if (viewId == 0)
							Log.e("D3Activity", "field " + field.getName() + "not found");

						// �ؼ�,ע���ʼ�����൱�� backBtn = (TextView)
						// findViewById(R.id.back_btn);
						field.set(activity, sourceView.findViewById(viewId));
						// �¼�
						setListener(activity, field, d3View.click(), Method.Click);
						setListener(activity, field, d3View.longClick(), Method.LongClick);
						setListener(activity, field, d3View.itemClick(), Method.ItemClick);
						setListener(activity, field, d3View.itemLongClick(), Method.itemLongClick);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setListener(Object activity, Field field, String methodName, Method method) throws Exception {
		if (methodName == null || methodName.trim().length() == 0)
			return;

		Object obj = field.get(activity);

		switch (method) {
		case Click:
			if (obj instanceof View) {
				((View) obj).setOnClickListener(new EventListener(activity).click(methodName));
			}
			break;
		case ItemClick:
			if (obj instanceof AbsListView) {
				((AbsListView) obj).setOnItemClickListener(new EventListener(activity).itemClick(methodName));
			}
			break;
		case LongClick:
			if (obj instanceof View) {
				((View) obj).setOnLongClickListener(new EventListener(activity).longClick(methodName));
			}
			break;
		case itemLongClick:
			if (obj instanceof AbsListView) {
				((AbsListView) obj).setOnItemLongClickListener(new EventListener(activity).itemLongClick(methodName));
			}
			break;

		case focusChange:
			if (obj instanceof View) {
				((View) obj).setOnFocusChangeListener(new EventListener(activity).focusChange(methodName));
			}
			break;
		default:
			break;
		}
	}

	public enum Method {
		Click, LongClick, ItemClick, itemLongClick, focusChange
	}

}
