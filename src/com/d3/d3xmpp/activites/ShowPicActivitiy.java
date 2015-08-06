package com.d3.d3xmpp.activites;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.d3.d3xmpp.R;

public class ShowPicActivitiy extends Activity{
    private LinearLayout ll_viewArea;
    private LinearLayout.LayoutParams parm;
    private ViewArea viewArea;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ȥ��title    
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
         //ȥ��Activity�����״̬��  
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN,  
                       WindowManager.LayoutParams. FLAG_FULLSCREEN);
        
        setContentView(R.layout.acti_show_pic);
        
        ll_viewArea = (LinearLayout) findViewById(R.id.ll_viewArea);
        parm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
//        parm.gravity =  Gravity.CENTER;
        viewArea = new ViewArea(ShowPicActivitiy.this,getIntent().getStringExtra("picPath"));    //�Զ��岼�ֿؼ���������ʼ��������Զ���imageView
        ll_viewArea.addView(viewArea,parm);

        ll_viewArea.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
        
        
    }
}
//��δ�����Ҫע���������ȥ��title��״̬������������ŵ� setContentView(R.layout.main);����ǰ�档���������仰�����У���Ϊ�������ص������Ǹ���ȫ������ģ��ҵ�i9000����480x800���������ȥ��title��״̬��������Ļص����������ǻص�������Ҫ��λ�á�

//���濴��ViewArea.java�ļ�������������źͳ�ʼ���Զ���imageView�ĵط����������Զ���ImageView�����������ڲ��ƶ����š�
class ViewArea extends FrameLayout{  //ǰ��˵��ViewArea��һ�����֣� �������ﵱȻҪ�̳�һ�������ˡ�LinearLayoutҲ����
    private int imgDisplayW;    
    private int imgDisplayH;    
//    private int imgW;        
//    private int imgH;        
    private TouchView touchView;
    private DisplayMetrics dm;
    //resIdΪͼƬ��Դid
    public ViewArea(Context context,String img) { //�ڶ���������ͼƬ����ԴID����ȻҲ�����ñ�ķ�ʽ��ȡͼƬ
/*        dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        imgDisplayW = dm.widthPixels;
        imgDisplayH = dm.heightPixels;*/ //���ַ�ʽ��ȡ����Ļ��С������ķ�ʽ�����һ���ģ�����480x800��i9000�ֱ��ʣ�
        super(context);
        imgDisplayW = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();//����Ŀ��Ҫ��xml�е�LinearLayout��Сһ�£����Ҫָ����С��xml�� LinearLayout�Ŀ��һ��Ҫ��px���ص�λ����Ϊ����Ŀ�������أ���dp������
        imgDisplayH = ((Activity)context).getWindowManager().getDefaultDisplay().getHeight();
        
        touchView = new TouchView(context,imgDisplayW,imgDisplayH);//���������ǵ��Զ���ImageView
//        touchView.setImageResource(resId);//�����ǵ��Զ���imageView����Ҫ��ʾ��ͼƬ

//		ImgConfig.showImg(img, touchView);
        
        Bitmap bitmap = BitmapFactory.decodeFile(img);
        touchView.setImageBitmap(bitmap);
//        imgW = img.getWidth();
//        imgH = img.getHeight();
        //ͼƬ��һ�μ��ؽ������ж�ͼƬ��С�Ӷ�ȷ����һ��ͼƬ����ʾ��ʽ��
//        int layout_w = imgW>imgDisplayW?imgDisplayW:imgW;  
//        int layout_h = imgH>imgDisplayH?imgDisplayH:imgH;
        int layout_w = imgDisplayW;
        int layout_h = imgDisplayH;
        
//����Ĵ������ж�ͼƬ��ʼ��ʾ��ʽ�ģ���Ȼ���Ը�������뷨������ʾ���������ǽ�����ڸߵ�ͼƬ���տ���С�ı����Ѹ�ѹ����ǰ������ǿ�ȳ�������Ļ��С���෴������ߴ��ڿ��ҽ�ͼƬ���ո���С�ı����ѿ�ѹ����ǰ������Ǹ߶ȳ�������Ļ��С
//        if(imgW>=imgH) {
//                layout_h = (int) (imgH*((float)imgDisplayW/imgW));
//        }else {
//                layout_w = (int) (imgW*((float)imgDisplayH/imgH));
//        }
//������Ҫע����ǣ�����FreamLayout����LinearLayout�ĺô��ǣ����ѹ�����ͼƬ����һ���ߴ�����Ļ����ôֻ��ʾ����Ļ�ڵĲ��֣�����ͨ���ƶ��󿴼��ⲿ������ü���ͼƬ�����������RelativeLayout���֣�ͼƬ��ʼ��������ʾ����Ļ�ڲ��������г�����Ļ���������ͼƬ������ȫռ����Ļ����ô����Ļ��û��ͼƬ�ĵط��϶���ͼƬҲ���ƶ������������鲻̫�ã�������FreamLayout����LinearLayout��
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(layout_w,layout_h);
        params.gravity =  Gravity.CENTER;
        touchView.setLayoutParams(params);//�����Զ���imageView�Ĵ�С��Ҳ���Ǵ�����Χ
        this.addView(touchView);
    }

}

class TouchView extends ImageView
{
    static final int NONE = 0;//��ʾ��ǰû��״̬
    static final int DRAG = 1;     //��ʾ��ǰ�����ƶ�״̬
    static final int ZOOM = 2;     //��ʾ��ǰ��������״̬
    static final int BIGGER = 3;   //��ʾ�Ŵ�ͼƬ
    static final int SMALLER = 4;  //��ʾ��СͼƬ
    private int mode = NONE;      //mode���ڱ�ʾ��ǰ����ʲô״̬

    private float beforeLenght;  //��һ�δ�������ľ���
    private float afterLenght;    //�ƶ�������ľ���
    private float scale = 0.04f;  //��������
   
    private int screenW;//��������ͼƬ���ƶ���Χ����ViewArea�ķ�Χ��Ҳ����linearLayout�ķ�Χ��Ҳ������Ļ��λ�������������ؼ����ԣ�
    private int screenH;
    
    
    private int start_x;//��ʼ������
    private int start_y;
    private int stop_x ;//����������
    private int stop_y ;
    private TranslateAnimation trans; //�ص�����
    
    public TouchView(Context context,int w,int h)//���ﴫ������w��h����ͼƬ���ƶ���Χ
    {
        super(context);
        this.setPadding(0, 0, 0, 0);
        screenW = w;
        screenH = h;
    }
    //��������2��������ľ���
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
    

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {    
        switch (event.getAction() & MotionEvent.ACTION_MASK) {  //MotionEvent.ACTION_MASK ��ʾ��㴥���¼�
        case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                stop_x = (int) event.getRawX();//��ʾ�������Ļ���Ͻ�Ϊԭ�������
                stop_y = (int) event.getRawY();//ͬ��
                start_x = stop_x - this.getLeft();//��(int) event.getX();һ��,��ʾ����ڵ�ǰ���Widget���ؼ������Ͻǵ����꣬�������������Զ���imageView���Ͻǵ�����.������ǰ�ߣ��������ȫ���϶�������ָ����Χ�ڣ�һ�����ã�
                start_y = stop_y - this.getTop();////��(int) event.getY();һ��,this.getTop()��ʾ�䶥������ڸ��ؼ��ľ���
                
                if(event.getPointerCount()==2)
                    beforeLenght = spacing(event);
                break;
        case MotionEvent.ACTION_POINTER_DOWN:
                if (spacing(event) > 10f) {
                        mode = ZOOM;
                        beforeLenght = spacing(event);
                }
                break;
        case MotionEvent.ACTION_UP:

                int disX = 0;
                int disY = 0;
                if(getHeight()<=screenH )//
                {
                    if(this.getTop()<0 )
                    {
                        disY = getTop();
//layout(left , top, right,bottom)������ʾ����view��λ�á�
                        this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());

                    }
                    else if(this.getBottom()>=screenH)
                    {
                        disY = getHeight()- screenH+getTop();
                        this.layout(this.getLeft(), screenH-getHeight(), this.getRight(), screenH);
                    }
                }else{
                    int Y1 = getTop();
                    int Y2 = getHeight()- screenH+getTop();
                        if(Y1>0)
                        {
                            disY= Y1;
                            this.layout(this.getLeft(), 0, this.getRight(), 0 + this.getHeight());
                        }else if(Y2<0){
                            disY = Y2;
                            this.layout(this.getLeft(), screenH-getHeight(), this.getRight(), screenH);
                        }
                }
                if(getWidth()<=screenW)
                {
                    if(this.getLeft()<0)
                    {
                        disX = getLeft();
                        this.layout(0, this.getTop(), 0+getWidth(), this.getBottom());
                    }
                    else if(this.getRight()>screenW)
                    {
                        disX = getWidth()-screenW+getLeft();
                        this.layout(screenW-getWidth(), this.getTop(), screenW, this.getBottom());
                    }
                }else {
                    int X1 = getLeft();
                    int X2 = getWidth()-screenW+getLeft();
                    if(X1>0) {
                        disX = X1;
                        this.layout(0, this.getTop(), 0+getWidth(), this.getBottom());
                    }else if(X2<0) {
                        disX = X2;
                        this.layout(screenW-getWidth(), this.getTop(), screenW, this.getBottom());
                    }
                    
                }
                //���ͼƬ���ŵ��������һ��С��100����ô�Զ��Ŵ�ֱ������100.
                while(getHeight()<100||getWidth()<100) {
                    
                    setScale(scale,BIGGER);
                }
//����disX��disY��ƫ���������ƶ������ص���λ������ʱ��Ϊ500���롣
                if(disX!=0 || disY!=0)
                {
                    trans = new TranslateAnimation(disX, 0, disY, 0);
                    trans.setDuration(500);
                    this.startAnimation(trans);
                }
                mode = NONE;
                break;
        case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
//ִ���϶��¼���ʱ�����ϱ任�Զ���imageView��λ�ôӶ��ﵽ�϶�Ч��
                        this.setPosition(stop_x - start_x, stop_y - start_y, stop_x + this.getWidth() - start_x, stop_y - start_y + this.getHeight());               
                        stop_x = (int) event.getRawX();
                        stop_y = (int) event.getRawY();
                        
                } else if (mode == ZOOM) {
                    if(spacing(event)>10f)
                    {
                        afterLenght = spacing(event);
                        float gapLenght = afterLenght - beforeLenght;                     
                        if(gapLenght == 0) {  
                           break;
                        }
//ͼƬ��ȣ�Ҳ�����Զ���imageView���������70�ſ�������
                        else if(Math.abs(gapLenght)>5f&&getWidth()>70)
                        {
                            if(gapLenght>0) { 
                                this.setScale(scale,BIGGER);   
                            }else {  
                                this.setScale(scale,SMALLER);   
                            }                             
                            beforeLenght = afterLenght; //��䲻���١�
                        }
                    }
                }
                break;
        }
        return true;    
    }
    

    private void setScale(float temp,int flag) {   
        
        if(flag==BIGGER) {  
//setFrame(left , top, right,bottom)������ʾ�ı䵱ǰview�Ŀ�ܣ�Ҳ���Ǵ�С��
            this.setFrame(this.getLeft()-(int)(temp*this.getWidth()),    
                          this.getTop()-(int)(temp*this.getHeight()),    
                          this.getRight()+(int)(temp*this.getWidth()),    
                          this.getBottom()+(int)(temp*this.getHeight()));      
        }else if(flag==SMALLER){   
            this.setFrame(this.getLeft()+(int)(temp*this.getWidth()),    
                          this.getTop()+(int)(temp*this.getHeight()),    
                          this.getRight()-(int)(temp*this.getWidth()),    
                          this.getBottom()-(int)(temp*this.getHeight()));   
        }   
    }
    

    private void setPosition(int left,int top,int right,int bottom) {  
        this.layout(left,top,right,bottom);               
    }

}