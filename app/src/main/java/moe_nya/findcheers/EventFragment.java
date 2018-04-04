package moe_nya.findcheers;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;



/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {
    private ListView mListView;
    OnItemSelectListener mCallback;//interface自己定义一个local variable


    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("Life cycle test", "We are at fragment onCreate()");
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        mListView = (ListView) view.findViewById(R.id.event_list);
        //listView.setAdapter(new EventAdapter(getActivity()));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //用安卓自带的arrayAdapter比较简单
                getActivity(),//context，这样就能一行一行显示string的信息
                android.R.layout.simple_list_item_1,// layout设置
                getEventNames()); //将string创建的Event传到adapter中

        // Assign adapter to ListView.
        mListView.setAdapter(adapter);
        //设置adapter


        //listview里面有listener这个方法，方法传进去interface。应用一个匿名类，匿名类implement这个interface
        //创建on...listener的interface，之后override 我们必须要override的方法，叫onItemClick， 之后call传进去的i所对应的position
        // 将position传进去将颜色进行改变
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCallback.onItemSelected(i);
                onCommentSelected(i);
            }
        });
        //当有触摸按键的时候，改变颜色的adapter

        return view;
    }


    // Container Activity must implement this interface
    public interface OnItemSelectListener { //接入一个接口，表示要把第几行传入
        public void onItemSelected(int position);
    }

    @Override
    public void onAttach(Context context) { //因为context是activity的一个子类，在这里可以看成传入一个activity
        //因为这里是传入子类要向上convert，所以要override implement
        super.onAttach(context);
        try {
            mCallback = (OnItemSelectListener) context; //传入一个context并将其强制转换成之前创建的interface
        } catch (ClassCastException e) {
            //do something
        }
    }


    private String[] getEventNames() {
        String[] names = {
                "Event1", "Event2", "Event3",
                "Event4", "Event5", "Event6",
                "Event7", "Event8", "Event9",
                "Event10", "Event11", "Event12"};
        return names;

    }

        //return view;
        //这里和之前在mainactivity里面与第一节课实现的内容是一样的。
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_event, container, false);

    public void onCommentSelected(int position){
        for (int i = 0; i < mListView.getChildCount(); i++){ //get 之前的number
            if (position == i) {
                mListView.getChildAt(i).setBackgroundColor(Color.BLUE);
            } else {
                mListView.getChildAt(i).setBackgroundColor(Color.parseColor("#FAFAFA"));
                //fafafa表示颜色不变
                //这段的意思是先得到你的按键按到哪了，如果i和position的位置相同就把背景换成蓝色的。如果没有就保持不变
                //getchild需要确定child是在position里面的
            }
        }
    }
}


