package tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.text.format.Time;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;

public class Generic {
	public static void showMsg(Context c, String msg, boolean flag){
		if(flag)
			/**
			 * Toast���Ѿ�������ʾ���û��Ŀؼ�����ʾһ��ʱ�����ʧ�����Զ����ʧ
			 * LENGTH_SHORT���ϵ���ʾʱ��
			 * LENGTH_LONG :������ʾʱ��
			 */
			Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
	}
	
 
	// get sysTime
	public static String getSysNowTime() {
		Time localTime = new Time();
		localTime.setToNow();
		String strTime = localTime.format("%Y-%m-%d-%H-%M-%S");

		return strTime;
	}
	
	/**
	 * �õ�sdcard��·��
	 * @return ʧ�ܷ���null
	 */
	public static File getSdCardFile(){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return Environment.getExternalStorageDirectory();
		}
		return null;
	}
	
	

	/**
	 * ��ȡ�������ӵ���wifi�ȵ���ֻ�IP��ַ
	 */  
	public static ArrayList<String> getConnectedIP() {
        ArrayList<String> connectedIP = new ArrayList<String>();
        try {  
            BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {  
                String[] splitted = line.split(" ");
                if (splitted != null && splitted.length >= 4) {  
                    String ip = splitted[0];
                    connectedIP.add(ip);  
                }  
            }  
        } catch (Exception e) {
            e.printStackTrace();  
        }  
          
        return connectedIP;  
    } 

	/**
	 * �õ���Ƭ������ͼ
	 * @param f ��Ƭ�ļ�
	 * @param w ͼƬ��С��Ŀ����
	 * @param h ͼƬ��С��Ŀ��߶�
	 * @return
	 * 1.����android�ṩ��BitmapFactory.Options�ഴ�������ú�options
	 * 2.����File���������
	 * 3.����BitmapFactory.decodeStream���λͼ
	 * 4.�ı�ͼƬΪ�������ţ�����λͼ
	 */
	public static Bitmap getShrinkedPic(File f){
		Bitmap smallBitmap = null;
		
		// ֱ��ͨ��ͼƬ·����ͼƬת��Ϊbitmap,����bitmapѹ���������ڴ����
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 10;// ͼƬ��߶�Ϊԭ����ʮ��֮һ
		options.inPreferredConfig = Bitmap.Config.ARGB_4444;// ÿ������ռ��2byte�ڴ�
		options.inPurgeable = true;// ��� inPurgeable
		// ��ΪTrue�Ļ���ʾʹ��BitmapFactory������Bitmap
		// ���ڴ洢Pixel���ڴ�ռ���ϵͳ�ڴ治��ʱ���Ա�����
		options.inInputShareable = true;
		FileInputStream fInputStream;
		try {
			fInputStream = new FileInputStream(f);
			// ����ʹ��BitmapFactory.decodeStream
			Bitmap bitmap = BitmapFactory.decodeStream(
					fInputStream, null, options);// ֱ�Ӹ���ͼƬ·��ת��Ϊbitmap
			smallBitmap = ThumbnailUtils.extractThumbnail(
					bitmap, 64, 48);// ��������ߴ�������ŵ�λͼ
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		return smallBitmap;
	}

	/**
	 * IntegerֵԽ��������ǰ��
	 * @author Administrator
	 *
	 */
	public static class DescendSortByIndex implements Comparator<Integer> {
		/**
		 * @return ������object2<object1��������object2>object1��0�����
		 */
		@Override
		public int compare(Integer object1, Integer object2) {
			
			return object2.compareTo(object1);
		}
		
	}
	
	/**
	 * File������޸�ʱ��ֵԽ��������ǰ��
	 * @author Administrator
	 *
	 */
	public static class DescendSortByTime implements Comparator<File> {
		/**
		 * @return ������object2<object1��������object2>object1��0�����
		 */
		@Override
		public int compare(File object1, File object2) {
			
			return (int) (object2.lastModified() - object1.lastModified());
		}
		
	}
}
