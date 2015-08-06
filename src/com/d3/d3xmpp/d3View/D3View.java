package com.d3.d3xmpp.d3View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author MZH
 * ��ѡ����
 *@D3View TextView textView  ��Ա����������Ӧxml��id��������Ҫ�¼���ֱ������
 *@D3View(id="textView") TextView textView;  //��xml��id����Ӧ���Զ���ID
 *@D3View(click="onClick") TextView textView;    //click="onClick" ��Ӧ��activity���и���������onClick,��ֱ�ӵ���
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) 
public @interface D3View {
	public int id() default 0;
	public String click() default "";
	public String longClick() default "";
	public String itemClick() default "";
	public String itemLongClick() default "";
	public String focusChange() default "";
}