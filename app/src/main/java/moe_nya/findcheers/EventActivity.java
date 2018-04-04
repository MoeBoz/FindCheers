package moe_nya.findcheers;


import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class EventActivity extends AppCompatActivity {
    private Fragment mEventsFragment;
    private Fragment mEventMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);


        //如果当前没有eventfragment就创建一个，然后把它传入到activity这个container里面去
        if (mEventsFragment == null) {
            mEventsFragment = new EventsFragment();
        }

        //mEventMapFragment = new EventMapFragment();


        //after login which should show first
        getSupportFragmentManager().beginTransaction().
                add(R.id.relativelayout_event, mEventsFragment).commit();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        //eventmapfragment should be able to be added to eventActivity, which is controlled by the navigation bar
        //when we click events, it will show events as a list, if we click on map, it will show events that at most 10 miles away from yout current location
        //edit eventAvtivity oncreate


        //Log.i = information
        //Log.e = error
        //Log.w = warning
        //Log.v = verbose
        //Log.d = debug

        //set item click listener to the menu items
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_event_list:
                                getSupportFragmentManager().beginTransaction().replace(R.id.relativelayout_event, mEventsFragment).commit();
                                break;
                            case R.id.action_event_map:
                                if (mEventMapFragment == null) {
                                    //lazy loading,当我们不使用map的时候就不会创建，只有需要的时候才会创建新的fragment显示
                                    mEventMapFragment = new EventMapFragment();
                                }
                                //replace的意思是将现在这个fragment放到上面，将别的fragment放到下面
                                getSupportFragmentManager().beginTransaction().replace(R.id.relativelayout_event, mEventMapFragment).commit();

                        }
                        return false;
                    }
                }
        );

    }
}
