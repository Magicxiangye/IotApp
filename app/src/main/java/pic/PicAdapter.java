package pic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.List;

/**
 * һ�̳�BaseAdapter������Ҫ�������еķ���
 * BaseAdapter��AndroidӦ�ó����о����õ��Ļ������������������Ǽ̳��Խӿ���Adapter
 * ��Ҫ��;�ǽ�һ�����ݴ�����ListView��Spinner��Gallery��GridView��UI��ʾ�����
 * ����Խϸߣ��Զ���Adapter���࣬����Ҫʵ�����Ķ����������������Ҫ����getView()����
 * ���ǽ���ȡ���ݺ��View���(��ListView��ÿһ�����TextView��Gallery�е�ÿ��ImageView)����
 */
public class PicAdapter extends BaseAdapter {

      private Context mContext = null;
    /**
    * ��ʵ�ʿ�����LayoutInflater��ǳ�����,��������������findViewById()
    * ��ͬ�����LayoutInflater��������layout��xml�����ļ�������ʵ������
    * ��findViewById()���Ҿ���xml�µľ���widget�ؼ�(��:Button,TextView��)
    */
    private LayoutInflater mInflater = null;
    private List<PicEntity> picList = null;
    /**
    * �������Ĺ��췽������newһ��������ʱ�������˷���
    * @param context
    */
     public PicAdapter(Context context) {
		  mContext = context;
		/**
		 *�����ֻ��LayoutInflater�ķ���,���Ǳ�����һ����
		 *(1)ͨ��SystemService���:
		 *LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 *(2)�Ӹ�����context�л�ȡ:
		 *LayoutInflater inflater = LayoutInflater.from(Activity.this);// �����õľ����ⷽ��
		 *
		 */
		this.mInflater = LayoutInflater.from(mContext);/*�ӱ����л��LayoutInflater*/
		picList = new ArrayList<PicEntity>();
		}

     //�õ�listview����
     public List<PicEntity> getData(){
		return picList;
     }

     //������ݵ�listview
        public void addData(List<PicEntity> l){
		picList.addAll(l);
		}

     //�õ�ѡ�е�Item���ϣ����������ΪItem��Ӧ��Position
      public List<Integer> getSelectItem(){
		List<Integer> selectItemList = new ArrayList<Integer>();

		for(int j=0; j<picList.size(); j++){
		if(picList.get(j).getIsSelect())
		selectItemList.add(j);
		}

		return selectItemList;
		}

   @Override
/**
 * ������û�õ�������Ҫ���� Ҫ��Ȼ����(�Ժ�����)
 */
      public int getCount() {
		return picList.size();
      }

   @Override
  /**
  * Ҳû�õ�
  */
     public Object getItem(int position) {
		return picList.get(position);
     }

   @Override
  /**
  * ������Ŀ��λ��
  */
      public long getItemId(int position) {
		return position;
      }

@Override
/**
 * �����ص㣬�����ǻ��View
 * 1.����xml �ҵ�view���ҵ��ؼ�
 * 2.һ��ʼ��View���ҵ��ؼ�ʵ�壬ΪView����Tag
 * 3.���Tag����������ʵ����listview�еĿؼ�
 */
    public View getView(final int position, View convertView, ViewGroup parent) {
		viewHolder vHolder = null;

		if (convertView == null) {
		convertView = mInflater.inflate(R.layout.pic_listview_item, null);/*��LayOut�ļ�ת����View*/
		vHolder = new viewHolder();
		vHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
		vHolder.textView = (TextView) convertView.findViewById(R.id.picname);
		vHolder.checkBox = (CheckBox) convertView.findViewById(R.id.chbox);
		convertView.setTag(vHolder);
		}else {
		vHolder=(viewHolder) convertView.getTag();
		}
		/**
		 * ���vHolder�� �Ϳ���ʹ����
		 */
		vHolder.imageView.setImageBitmap(picList.get(position).getBm());
		vHolder.textView.setText(picList.get(position).getName());
		vHolder.checkBox.setChecked(picList.get(position).getIsSelect());
		/**
		 * Ϊcheckbox�󶨼����������������е�onclick����
		 */
		vHolder.checkBox.setOnClickListener(new OnClickListener() {
@Override
   public void onClick(View arg0) {
		boolean state = ((CheckBox)arg0).isChecked();/*���checkbox��ѡ��״̬*/
		picList.get(position).setIsSelect(state);/*����ѡ��״̬������checkbox*/
		}
		});

		return convertView;
		}
/**
 * Ϊ���㣬��listview�Ŀؼ�����һ���Զ����һ��
 */
    public final class viewHolder{
	public ImageView imageView;
	public TextView textView;
	public CheckBox checkBox;
   }

}