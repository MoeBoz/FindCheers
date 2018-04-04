package moe_nya.findcheers;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment {
    private GridView mGridView;
    OnCommentSelectListener mCallback;

    public CommentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        mGridView = (GridView) view.findViewById(R.id.comment_grid);
        mGridView.setAdapter(new EventAdapter(getActivity()));

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCallback.onCommentSelected(i);
                // ? 为什么加了之后会红线 因为自己调用自己的function 不用指明他在哪

                onItemSelected(i);
                //如果没有加这一行就会变成两个互相指变色但是不能同时变色
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) { //因为context是activity的一个子类，在这里可以看成传入一个activity
        //因为这里是传入子类要向上convert，所以要override implement
        super.onAttach(context);
        try {
            mCallback = (OnCommentSelectListener) context; //传入一个context并将其强制转换成之前创建的interface
        } catch (ClassCastException e) {
            //do something
        }
    }

    // Change background color if the item is selected
    public void onItemSelected(int position){
        for (int i = 0; i < mGridView.getChildCount(); i++){ //get 之前的number
            if (position == i) {
                mGridView.getChildAt(i).setBackgroundColor(Color.BLUE);
            } else {
                mGridView.getChildAt(i).setBackgroundColor(Color.parseColor("#FAFAFA"));
                //fafafa表示颜色不变
                //这段的意思是先得到你的按键按到哪了，如果i和position的位置相同就把背景换成蓝色的。如果没有就保持不变
                //getchild需要确定child是在position里面的
            }
        }
    }


}
