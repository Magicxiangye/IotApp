package io;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * ����̳���DataInputStreamʵ����Serializable�ӿ�
 * 1. ʵ������,��ȡ��ʼ�����͹ر�ʵ�����ķ���
 * 2. һ�����캯��
 * 3. һ������֡���ݴ�С���λͼ����
 */
public class MjpegInputStream extends DataInputStream implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ��UE�򿪷��� ÿһ��jpg��ʽ��ͼƬ ��ʼ���ֽڶ��� 0xFF,0xD8
	 */
	private final byte[] SOI_MARKER = { (byte) 0xFF, (byte) 0xD8 };
//	private final byte[] EOF_MARKER = { (byte) 0xFF, (byte) 0xD9 };
	/**
	 * ��ʾ�����������ͻ��˵�һ֡���ݵĳ���
	 */
	private final String CONTENT_LENGTH = "Content-Length";
	private final static int HEADER_MAX_LENGTH = 100;
	private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;
	private int mContentLength = -1;
	private static MjpegInputStream mis = null;
	/**
	 * ���ø���Ĺ��췽�� ����MjpegInputStream��
	 * @param is
	 */
	public static void initInstance(InputStream is){
		if(mis == null)
			mis = new MjpegInputStream(is);
		
	}
	/**
	 * ��ô�����mjpegInputsteam��
	 * @return
	 */
	public static MjpegInputStream getInstance(){
		if(mis != null)
			return mis;
		
		return null;
	}
	/**
	 * ��Ϊmpjeginputstream�̳���datainputstream
	 * ���Կ��Ե���mpjeginputstream�Ĺر�������
	 */
	public static void closeInstance(){
		try {
			mis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mis = null;
	}

	private MjpegInputStream(InputStream in) {
		super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
	}
	/**
	 * ��������������SOI_MARKER={(byte)0xFF,(byte) 0xD8}
	 * ���ж�IO���Ĳ��������׳��쳣
	 * @param in
	 * @param sequence
	 * @return
	 * @throws IOException
	 */
	private int getEndOfSeqeunce(DataInputStream in, byte[] sequence)
			throws IOException {
		int seqIndex = 0;
		byte c;
		for (int i = 0; i < FRAME_MAX_LENGTH; i++) {// 0 1 2 3
			c = (byte) in.readUnsignedByte();
			if (c == sequence[seqIndex]) {
				seqIndex++;
				if (seqIndex == sequence.length)//2
					return i + 1;//3
			} else
				seqIndex = 0;
		}
		return -1;
	}
	/**
	 * �˷����������ҵ�����0xFF,0XD8���ַ�����λ��
	 * ������������ʽ��httpͷ��Ϣ ֡ͷ(0xFF 0xD8) ֡���� ֡β(0xFF 0xD9)
     * 1������ͨ��0xFF 0xD8�ҵ�֡ͷλ��
     * 2��֡ͷλ��ǰ�����ݾ���httpͷ���������Content-Length������ֶ�ָʾ������֡���ݵĳ���
     * 3��֡ͷλ�ú�������ݾ���֡ͼ��Ŀ�ʼλ��
	 * @param in
	 * @param sequence
	 * @return
	 * @throws IOException
	 */
	private int getStartOfSequence(DataInputStream in, byte[] sequence)
			throws IOException {
		int a=0;
		int b=0;
		int end = getEndOfSeqeunce(in, sequence);
		a=end;
		b=end;
		return (end < 0) ? (-1) : (end - sequence.length);
	}
	/**
	 * ��http��ͷ��Ϣ�л�ȡContent-Length��֪��һ֡���ݵĳ���
	 * @param headerBytes
	 * @return
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	private int parseContentLength(byte[] headerBytes) throws IOException,
            NumberFormatException {
		/**
		 * �����ֽ�������ByteArrayInputStream��
		 * Properties��java.util�����һ���࣬���д������Ͳ��������Ĺ��췽������ʾ������Ĭ��ֵ����Ĭ��ֵ�������б�
		 * �������е�httpͷ��Ϣ���������ļ���Ȼ���ҵ������ļ�CONTENT_LENGTH��value������ҵ���Ҫ��õ�֡���ݴ�С
		 * ����һ�� ByteArrayInputStream��ʹ�� headerBytes��Ϊ�仺��������
		 */
		
		String str = new String(headerBytes);
		ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
		Properties props = new Properties();/*����һ����Ĭ��ֵ�Ŀ������б�*/
		props.load(headerIn);/*�������������������б�����Ԫ�ضԣ���*/
		return Integer.parseInt(props.getProperty(CONTENT_LENGTH));/*��ָ���ļ��ڴ������б����������ԡ�*/
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Bitmap readMjpegFrame() throws IOException {
		mark(FRAME_MAX_LENGTH);/*���е�ǰ�ı��λ��*/
		int headerLen = getStartOfSequence(this, SOI_MARKER);
		reset();/*����������λ������Ϊ���λ��*/

		byte[] header = new byte[headerLen];

		readFully(header);/*��һֱ�����ȴ���ֱ������ȫ������(���ݻ�����װ��)*/
		String s = new String(header);

		try {
			mContentLength = parseContentLength(header);// ?
		} catch (NumberFormatException e) {
			return null;
		}
		/**
		 * ����֡���ݵĴ�С�����ֽ�����
		 */
		byte[] frameData = new byte[mContentLength];
		readFully(frameData);
		/**
		 * ���ݲ�ͬ��Դ(file��stream��byte-arrays)����λͼ
		 * �������ֽ�����תΪλͼ
		 */
		return BitmapFactory.decodeStream(new ByteArrayInputStream(frameData));
	}
}
