package pic;

import android.graphics.Bitmap;

/**
 * pictureʵ����
 * 1���캯��
 * ����λͼ/��� λͼ�����������ļ����ͻ���ļ�������
 * ���ñ�ѡ�кͻ���Ƿ�ѡ�е�״̬����
 * @author Administrator
 *
 */
public class PicEntity {
	private String name;//�ļ���
	private Bitmap bm;//����ͼ
	private boolean bIsSelect = false;//item�Ƿ�ѡ��

	public PicEntity(String name, Bitmap bm) {
		this.name = name;
		this.bm = bm;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Bitmap getBm() {
		return bm;
	}

	public void setBm(Bitmap bm) {
		this.bm = bm;
	}

	public boolean getIsSelect() {
		return bIsSelect;
	}

	public void setIsSelect(boolean bIsSelect) {
		this.bIsSelect = bIsSelect;
	}
}
