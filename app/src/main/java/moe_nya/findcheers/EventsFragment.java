package moe_nya.findcheers;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import moe_nya.findcheers.artifacts.Events;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {
    private ImageView mImageViewAdd;

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1072772517";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference database;
    private List<Events> events;



    public EventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        mImageViewAdd = (ImageView) view.findViewById(R.id.img_event_add);
        //先单独创建一个mImageView，从xml里面找相对应button的id，这里为什么要加view，因为activity里面没有view，需要先import view
        //强制类型转换就是cast
        mImageViewAdd.setOnClickListener(new View.OnClickListener() {
            //在这里设置一个clicklistener，要override这个方法，
            @Override
            public void onClick(View view) {
                Intent eventReportIntent = new Intent(getActivity(), EventReportActivity.class);
                //这里的目的是如果我点击了这个button会跳转到下一个activity，这个intent需要指出出发点和目的地
                startActivity(eventReportIntent);
                //之后启动这个activity
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.event_recycler_view);
        database = FirebaseDatabase.getInstance().getReference();
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        setAdapter();


        return view;


        //return inflater.inflate(R.layout.fragment_events, container, false);
    }

    //对数据库进行访问，get到所有数据库中的数据
    public void setAdapter() {
        events = new ArrayList<Events>();
        database.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            //get到events这个索引，然后对他进行判断
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    Events event = noteDataSnapshot.getValue(Events.class);
                    events.add(event);
                }
                //对所有数据进行循环，并将所有的数据加入到我们events这个object当中

                //设置一个adapter，这样所有的数据都可以adapt到events这个上面
                mAdapter = new EventListAdapter(events, getContext()); //这里有报错，需要debug
                recyclerView.setAdapter(mAdapter);
                //setUpAndLoadNativeExpressAds();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO: do something
            }
        });
    }


//    //这里显示的是异步的广告，就是说在加载别的内容的时候在等广告的加载。？
//    private void setUpAndLoadNativeExpressAds() {
//        recyclerView.post(new Runnable() {
//            @Override
//            public void run() {
//                final float scale = getActivity().getResources().getDisplayMetrics().density;
//                //set the ad size and ad unit id for each native express ad int the items list
//                for (int i = 0; i < mAdapter.getEventList().size(); i++) {
//                    if (mAdapter.getMap().containsKey(i)) {
//                        final NativeExpressAdView adView = mAdapter.getMap().get(i);
//                        final CardView cardView = (CardView) getActivity().findViewById(R.id.ad_card_view);
//                        final int adWidth = cardView.getWidth() - cardView.getPaddingLeft() - cardView.getContentPaddingRight();
//                        AdSize adSize = new AdSize((int) (adWidth / scale), 150);
//                        adView.setAdSize(adSize);
//                        adView.setAdUnitId(AD_UNIT_ID);
//                        loadNativeExpressAd(i, adView);
//                    }
//                }
//            }
//        });
//
//    }

    //这里就是有一个listener，看看广告是否load成功。成功直接post， 没有的话重新load
    private void loadNativeExpressAd(final int index, NativeExpressAdView adView) {
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("MainActivity", "The ads failed to load, will fresh");
            }
        });
        adView.loadAd(new AdRequest.Builder().build());
    }

}
