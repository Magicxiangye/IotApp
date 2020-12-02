package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import io.MjpegInputStream;

/**
 * ����̳���SurfaceViewʵ����SurfaceHolder.Callback�ӿ�
 * SurfaceView����ͼ��(view)�ļ̳��࣬�����ͼ����Ƕ����һ��ר�����ڻ��Ƶ�Surface	�����Կ������Surface�ĸ�ʽ�ͳߴ�
 * SurfaceView�������Surface�Ļ���λ��
 * surface����������(Z-ordered)�ģ�������������Լ����ڴ��ڵĺ��档surfaceview�ṩ��һ���ɼ�����
 * ֻ��������ɼ������� ��surface�������ݲſɼ����ɼ�������Ĳ��ֲ��ɼ���surface���Ű���ʾ�ܵ���ͼ�㼶��ϵ��Ӱ��
 * �����ֵ���ͼ�����ڶ�����ʾ������ζ�� surface�����ݻᱻ�����ֵ���ͼ�ڵ�����һ���Կ������������ڸ���(overlays)(���磬�ı��Ͱ�ť�ȿؼ�)
 * ����ͨ��SurfaceHolder�ӿڷ������surface��getHolder()�������Եõ�����ӿ�
 * surfaceview��ÿɼ�ʱ��surface��������surfaceview����ǰ��surface�����٣������ܽ�ʡ��Դ�������Ҫ�鿴 surface�����������ٵ�ʱ��
 * ��������surfaceCreated(SurfaceHolder)�� surfaceDestroyed(SurfaceHolder)
 * surfaceview�ĺ��������ṩ�������߳�:UI�̺߳���Ⱦ�̣߳�����Ӧע��:
 * 1> ����SurfaceView��SurfaceHolder.Callback�ķ�����Ӧ����UI�߳�����ã�һ����˵����Ӧ�ó������̣߳���Ⱦ�߳���Ҫ���ʵĸ��ֱ���Ӧ����ͬ������
 * 2> ����surface���ܱ����٣���ֻ��SurfaceHolder.Callback.surfaceCreated()�� SurfaceHolder.Callback.surfaceDestroyed()֮����Ч��
 * ����Ҫȷ����Ⱦ�̷߳��ʵ��ǺϷ���Ч��surface
 * ��������:�̳�SurfaceView��ʵ��SurfaceHolder.Callback�ӿ� ----> SurfaceView.getHolder()���SurfaceHolder����(Surface������) 
 * ---->SurfaceHolder.addCallback(callback)��ӻص�����---->SurfaceHolder.lockCanvas()���Canvas������������
 * ----> Canvas�滭 ---->SurfaceHolder.unlockCanvasAndPost(Canvas canvas)����������ͼ�����ύ�ı䣬��ͼ����ʾ��
 */
public class MjpegView extends SurfaceView implements SurfaceHolder.Callback {
	/*fps��ʾλ��*/
	public final static int POSITION_UPPER_LEFT = 9;
	public final static int POSITION_UPPER_RIGHT = 3;
	public final static int POSITION_LOWER_LEFT = 12;
	public final static int POSITION_LOWER_RIGHT = 6;
	/*ͼ����ʾģʽ*/
	public final static int STANDARD_MODE = 1;//��׼�ߴ�
	public final static int KEEP_SCALE_MODE = 4;//���ֿ�߱���
	public final static int FULLSCREEN_MODE = 8;//ȫ��

	private Context mContext = null;
	private MjpegViewThread mvThread = null;
	private MjpegInputStream mIs = null;
	private Paint overlayPaint = null;//����fpsͿ��滭��
	private boolean bIsShowFps = true;
	private boolean bRun = false;
	private boolean bsurfaceIsCreate = false;
	private int overlayTextColor;
	private int overlayBackgroundColor;
	private int ovlPos;
	private int dispWidth;//MjpegView�Ŀ��
	private int dispHeight;//MjpegView�ĸ߶�
	private int displayMode;//����ģʽ

	public MjpegView(Context context) {
		super(context);
		init(context);
	}
	
	/**
	 * ��Ϊ��res/layoutĿ¼�µ�xml�ļ�����Ϊ�Զ���Ŀؼ�ʹ��������࣬������Ҫ�������ṩ���������βεĹ��캯��
	 * ����MainActivityͨ��ID�ҵ����Զ���Ŀؼ�ʱ���ù��캯���������ã����Խ��ù��캯����Ϊpublic	
	 * @param context
	 * @param attrs
	 */
	public MjpegView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	/**
	 * ���˽�з���
	 * 1.���Surface��������ΪSurface��������ӻص��ӿ�
	 * 2.�½���Ⱦ�߳�MjpegViewThread
	 * 3.�½����ǻ��ʣ������ı��Ķ��뷽ʽ���ı����ȡ����塢�����ı���ɫ�����ʱ���
	 * 4.���ø��Ƕ�̬�ı��ĸ���λ�� //�����ֻ��Ҫʵ�ּ�ػ���Ĺ��ܣ�3��4������ʡ��
	 * 5.����MjpegView��ʾģʽ
	 * @param context
	 */
	private void init(Context context) {
		mContext = context;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		mvThread = new MjpegViewThread(holder, context);
		setFocusable(true);
		overlayPaint = new Paint();
		overlayPaint.setTextAlign(Paint.Align.LEFT);
		overlayPaint.setTextSize(40);
		overlayPaint.setTypeface(Typeface.DEFAULT);

		overlayTextColor = Color.RED;
		overlayBackgroundColor = Color.TRANSPARENT;
		ovlPos = MjpegView.POSITION_UPPER_RIGHT;
		displayMode = MjpegView.KEEP_SCALE_MODE;
		
	}
	/**
	 *  Surface���κνṹ�Խṹ�Եĸı�(���ʽ����С)�������˷���
     *  ��Ҫ������Ⱦ�̵߳�setSurfaceSize������Surface�Ŀ�͸�
	 */
	public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
		mvThread.setSurfaceSize(w, h);
	}
	/**
	 * Surface������֮ǰ�������˷���������ֻ���ñ��λ����ʾSurface���������ˡ�
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		bsurfaceIsCreate = false;
	}
	/**
	 * Surface����һ�δ����󽫼����˷���������ֻ���ñ��λ����ʾSurface���������ˡ�
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		bsurfaceIsCreate = true;
	}
	/**
	 * setFps��getFps��set source����MaiActivityʹ��
	 * @param b
	 */
	public void setFps(boolean b) {
		bIsShowFps = b;
	}
	
	public boolean getFps(){
		return bIsShowFps;
	}

	public void setSource(MjpegInputStream source) {
		mIs = source;
	}
	
	/**
	 * ��ʼ�����߳�
	 * ���ñ�ǣ���ʾ��Surface�������ˡ���Ȼ�������Ⱦ�̵߳ĵ�run����������Ⱦ
	 */
	public void startPlay() {
		if (mIs != null) {
			bRun = true;
			mvThread.start();
		}
	}

	/**
	 * ֹͣ�����߳�
	 * 1.�����ñ�ǣ���ʾ"ֹͣ����"
	 * 2.�ȴ������̵߳��˳�
	 * 3.�ر�������
	 */
	public void stopPlay() {
		bRun = false;
		boolean retry = true;
		while (retry) {
			try {
				mvThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
		
		//�߳�ֹͣ��ر�Mjpeg��(����Ҫ)
		mIs.closeInstance();
	}
	/**
	 * mjpegview�Ļ�ȡλͼ������������Ⱦ�̵߳Ļ�ȡλͼ����
	 * @return
	 */
	public Bitmap getBitmap(){
		return mvThread.getBitmap();
	}
	
	/**
	 * ������ʾģʽ����Activity��initview����
	 * @param s
	 */
	public void setDisplayMode(int s) {
		displayMode = s;
	}
	/**
	 * ��Ȼ��������ʾģʽ����Ӧ��Ҳ�л����ʾģʽ������java�����÷�������ķ��
	 * @return
	 */
	public int getDisplayMode() {
		return displayMode;
	}
	/**
	 * ����Ⱦ�߳��������������ص㣬Ӧ���ص�����
	 * @author Administrator
	 *
	 */
	public class MjpegViewThread extends Thread {
		private SurfaceHolder mSurfaceHolder = null;
		private int frameCounter = 0;
		private long start = 0;
		private Canvas c = null;
		private Bitmap overlayBitmap = null;
		private Bitmap mjpegBitmap = null;
		private PorterDuffXfermode mode = null;
		/**
		 * ��һ�����������洫������surfaceHolder
		 * �½�һ��Ŀ��ͼ��͸���ͼ����ཻģʽ��mjpegviewΪĿ��ͼ�㣬����ͼ��Ϊ���ϽǵĶ�̬"�ı�"
		 * mode��calculateFps������ʹ��
		 * @param surfaceHolder:Surfaceview������
		 * @param context : �����Ļ���
		 */
		public MjpegViewThread(SurfaceHolder surfaceHolder, Context context) {
			mSurfaceHolder = surfaceHolder;
			mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);/*�ཻʱ��̬�ı�����mjpegview*/
		}
		
		public Bitmap getBitmap(){
			return mjpegBitmap;
		}

		/**
		 * ����ͼ��ߴ�
		 * @param bmw bitmap��
		 * @param bmh bitmap��
		 * @return ͼ�����
		 */
		private Rect destRect(int bmw, int bmh) {
			int tempx;
			int tempy;
			/**
			 * ��ʾģʽֻ����ȫ���Ͱ���ģʽ֮���л��������������STANDARD_MODEģʽ���������if��֧����ȥ��
			 */
			if (displayMode == MjpegView.STANDARD_MODE) {
				tempx = (dispWidth / 2) - (bmw / 2);
				tempy = (dispHeight / 2) - (bmh / 2);
				return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
			}
			/**
			 * һ��ʼ��������KEEP_SCALE_MODEģʽ����ʾ������ʾ����
			 */
			if (displayMode == MjpegView.KEEP_SCALE_MODE) {
				float bmasp = (float) bmw / (float) bmh;
				bmw = dispWidth;
				bmh = (int) (dispWidth / bmasp);/*�����ֻ���Ļ��һ��*/
				if (bmh > dispHeight) {
					bmh = dispHeight;
					bmw = (int) (dispHeight * bmasp);
				}
				tempx = (dispWidth / 2) - (bmw / 2);
				tempy = (dispHeight / 2) - (bmh / 2);
				/**
				 * Rect(��ߣ����ߣ��ұߣ��±�)�������ǻ���һ���ض�����ľ���
				 * ��˵�������Ͻ�����Ϊ(0��0),���½�����Ϊ(bmw��bmh)
				 */
				return new Rect(0, 0, bmw + 0, bmh + 0);
			}
			/**
			 * �����ʾģʽΪȫ������ȫ����ʾ����
			 * dispWidth��dispHeight�������setSurfaceSize����ʹ�ã����Ǳ�ʾmjpegview�Ŀ�͸�
			 */
			if (displayMode == MjpegView.FULLSCREEN_MODE)
				return new Rect(0, 0, dispWidth, dispHeight);
			return null;
		}
		/**
		 * ��mjpegview�����κνṹ�Եĸı�ʱ���������˷�����ǰ��Ҳ�ᵽ����Ⱦ�߳�ʹ�õĸ��ֱ�������ͬ������
		 * synchronized�ڵľ���ͬ������飬Ϊ�˷�ֹ�߳�֮����ٽ���Դ�ľ���
		 * @param width
		 * @param height
		 */
		public void setSurfaceSize(int width, int height) {
			synchronized (mSurfaceHolder) {
				dispWidth = width;
				dispHeight = height;
			}
		}
		/**
		 * �˷�����calculateFpsʹ�ã�calculateFps�ֱ���Ⱦ�̵߳�run����ʹ��
		 * �����Ƿ���һ��λͼ
		 * @param p:����"�ı�"�õĻ���
		 * @param text:Ҫ���Ƶ��ַ� ��:֡
		 * @return bm
		 */
		private Bitmap makeFpsOverlay(Paint p, String text) {
			int nWidth, nHeight;
			
			Rect b = new Rect();
			//int  a = b.left ;
			/**
			 * �����ǻ�ô�ԭ�㿪ʼ���ַ�Χ�Ƶ���С�ľ���
			 * text���ַ�
			 * 0����ʾ��һ���ַ�
			 * text.length:���������һ���ַ�
			 * b:���ڴ�Ż�õ��ַ�����
			 * �����text�ı߽��Ϳ��Եõ����εĿ�͸�
			 */
			p.getTextBounds(text, 0, text.length(), b);
			nWidth = b.width() + 2;
			nHeight = b.height() + 2;
			/**
			 * ÿһ������4�ֽڣ����������õĿ�͸߷���һ��λͼ
			 */
			Bitmap bm = Bitmap.createBitmap(nWidth, nHeight,
					Bitmap.Config.ARGB_8888);
			/**
			 * Canvas :����������ͼ����Ļ�����Ԫ
			 * ��ͼʱ����Ҫ4����Ҫ��Ԫ�أ�
			 * 1.�������ص�λͼ
			 * 2.��ͼ��λͼ�Ļ��� 
			 * 3.���� 
			 * 4. ������ɫ�ͻ��Ʒ��Ļ��� 
			 * Canvas(bm):�����һ��Ҫ���Ƶ�λͼ�Ļ���
			 */
			Canvas c = new Canvas(bm);
		/**
		 * Paint�����  
		 * Paint�����ʣ��ڻ�ͼ���������˼�����Ҫ�����ã�������Ҫ��������ɫ��  
		 * ��ʽ�Ȼ�����Ϣ��ָ������λ����ı���ͼ�Σ����ʶ����кܶ����÷�����  
		 * �����Ͽ��Է�Ϊ���࣬һ����ͼ�λ�����أ�һ�����ı�������ء�         
		 *   
		 * 1.ͼ�λ���        
		 * setColor(int color);  
		 * ���û��Ƶ���ɫ��ʹ����ɫֵ����ʾ������ɫֵ����͸���Ⱥ�RGB��ɫ��    
		 * setDither(boolean dither);       
		 * setXfermode(Xfermode xfermode);  
		 * ����ͼ���ص�ʱ�Ĵ���ʽ����ϲ���ȡ�����򲢼�����������������Ƥ�Ĳ���Ч��  
		 *   
		 * 2.�ı�����  
		 * setFakeBoldText(boolean fakeBoldText);  
		 * ģ��ʵ�ִ������֣�������С������Ч����ǳ���     
		 * setSubpixelText(boolean subpixelText);  
		 * ���ø���Ϊtrue�����������ı���LCD��Ļ�ϵ���ʾЧ��  
		 *   
		 * setTextAlign(Paint.Align align);  
		 * ���û������ֵĶ��뷽��    
		 * setTextSize(float textSize);  
		 * ���û������ֵ��ֺŴ�С  
		 * setTypeface(Typeface typeface);  
		 * ����Typeface���󣬼������񣬰������壬б���Լ������壬�ǳ������    
		 */  
		 
			p.setColor(overlayBackgroundColor);// ������ɫ
			c.drawRect(0, 0, nWidth, nHeight, p);/*���ƾ���*/
			p.setColor(overlayTextColor);// ������ɫ
			/**
			 * �����Ļ������ַ���
			 * test:Ҫ���Ƶ��ַ�
			 * -b.left:�ַ���ʼλ�õ�x���꣬�����Ǿ��ε����
			 * (nHeight / 2) - ((p.ascent() + p.descent()) / 2) + 1:�ַ���ʼλ�õ�y����
			 * p:�õ��Ļ���
			 * �����漰�ľ������Կɿ�����  http://mikewang.blog.51cto.com/3826268/871765
			 */
			c.drawText(text, -b.left + 1,
					(nHeight / 2) - ((p.ascent() + p.descent()) / 2) + 1, p);
			
			return bm;
		}

		/**
		 * ��ͷϷ
		 * ����߳������еģ�SurfaceViewҲ�����˵�
		 * ����������MjpegInputStream.java�е�readMjpegFrame�������mjpeg��Ƶ��������
		 * mjpeg��Ƶ�����ݾ�����λͼ��Ȼ�������λͼ���ƾ��Σ��ٻ�����Ӧ��λͼ�����λͼ����������Ҫ��
		 * ���������֡���ı�������mjpegview�ϸ��ǣ�����������
		 */
		public void run() {
			start = System.currentTimeMillis();
			Rect destRect;
			Paint p = new Paint();
	//		String fps = "";
			while (bRun) {
				if (bsurfaceIsCreate) {
					c = mSurfaceHolder.lockCanvas();
					try {
						mjpegBitmap = mIs.readMjpegFrame();/*����Inputstrean�ķ���*/

						if(mjpegBitmap!=null){
							/*ͬ��ͼ��Ŀ������*/
							synchronized (mSurfaceHolder) {
								destRect = destRect(mjpegBitmap.getWidth(),
										mjpegBitmap.getHeight());
							}
							/**
							 * ����activity�������������תʱ��Surfaceview�����٣���ʱc��Ϊ��
							 */
							if(c != null){
								c.drawPaint(new Paint());
								c.drawBitmap(mjpegBitmap, null, destRect, p);
								if (bIsShowFps)
									calculateFps(destRect, c, p);
								mSurfaceHolder.unlockCanvasAndPost(c);
							}
						}else{
							mSurfaceHolder.unlockCanvasAndPost(c);
						}
					} catch (IOException e) {
					}
				}else {
					try {
						Thread.sleep(500);//�߳����ߣ��ó�����
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		/**
		 * ʹ��ǰ��ķ��������Ƴ�����ʾ֡�ʡ��ı���Ч��Ϊ"i֡"��i����
		 * @param destRect
		 * @param c
		 * @param p
		 */
		public void calculateFps(Rect destRect, Canvas c, Paint p) {
			int width;
			int height;
			String fps;
			
			p.setXfermode(mode);/* �������������ཻʱ��ģʽ*/
			if (overlayBitmap != null) {
				/**
				 * ������ı��Ŀ�͸�
				 * Ȼ����û����Ļ���λͼ������ͼ
				 */
				height = ((ovlPos & 1) == 1) ? destRect.top
						: destRect.bottom - overlayBitmap.getHeight();
				width = ((ovlPos & 8) == 8) ? destRect.left 
						: destRect.right - overlayBitmap.getWidth();
				c.drawBitmap(overlayBitmap, width, height, null);
			}
			p.setXfermode(null);
			frameCounter++;
			/**
			 * currentTimeMillis��ʾϵͳ��January 1, 1970 00:00:00.0 UTC��ʼ�ĺ�����
			 * start��ǰ���Ѿ����úã���ʾ��Ⱦ�߳̿�ʼ��ϵͳʱ��
			 */
			if ((System.currentTimeMillis() - start) >= 1000) {
				fps = frameCounter+ "fps";
				start = System.currentTimeMillis();
				overlayBitmap = makeFpsOverlay(overlayPaint, fps);/*�����Ļ������"�ı�"*/
				frameCounter = 0;				
			}
		}
		
		
	}

}
