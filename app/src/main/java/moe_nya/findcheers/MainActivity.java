package moe_nya.findcheers;


import android.content.Intent;
import android.content.res.Configuration;
//import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import moe_nya.findcheers.artifacts.User;

//import android.widget.ArrayAdapter;
//import android.widget.ListView;
public class MainActivity extends AppCompatActivity{
//public class MainActivity extends AppCompatActivity implements EventFragment.OnItemSelectListener, OnCommentSelectListener {

////    private EventFragment mListFragment;
////    private CommentFragment mGridFragment;
//    @Override
//    public void onItemSelected(int position){
//        if (!isTablet()) {
//            Intent intent = new Intent(this, EventGridActivity.class);
//            intent.putExtra("position", position);
//            startActivity(intent);
//        } else {
//            mGridFragment.onItemSelected(position);
//        }
//    }
//
//    @Override
//    public void onCommentSelected(int position){
//        mListFragment.onCommentSelected(position);
//    }
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mSubmitButton;
    private Button mRegisterButton;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Log.e("Life cycle test", "We are at onCreate()");
            // Get ListView object from xml.
            //*  ListView eventListView = (ListView) findViewById(R.id.event_list);

            //用java如何get到对xml的view？通过id这个Mark，findviewbyid找到对应的xml文件后，强制转换成ListView，之后就可以进行操作
            //操作：对每一行进行赋值操作
            //叫完id之后的返回类型是view，是listview的父类，所以可以强制转换类型

//        // Initialize an adapter.
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                this,
//                R.layout.event_item,
//                //这里的R有点像是java和xml的一个桥梁，要互相沟通的。其实是一个叫R.java 的file，里面存的都是index of xml
//                //是xml转换成java的桥梁
//                R.id.event_name,
//                getEventNames());

            // Assign adapter to ListView.
            //* EventAdapter adapter = new EventAdapter(this);

            //这里传了一个this，是不是activity里面就装着引用，并没有别的地方继续装着source，就是因为转载同一个东西里面才可以传this过去
            //有两个xml文件，一个表示总体的view是什么形式，一个是每一个object怎么显示。两个文件如何连接靠的是这里建立的adapter
            //adapter是连接两个文件的桥梁。

            //*  eventListView.setAdapter(adapter);
//        // Show different fragments based on screen size.
//        if (findViewById(R.id.fragment_container) != null) {
//            Fragment fragment = isTablet() ? new  CommentFragment() : new EventFragment();
//            getSupportFragmentManager().beginTransaction().add (R.id.fragment_container, fragment).commit();
//            //container那段在一开始的时候会有红线。
//            //debug方法是检查add这个method里面各个部分的parameter的正确性，检查出来之后后面的fragment的对应是v4版本的fragment
//            //在外面创建的两个fragment直接改变import的类型，之后再更改这边的import就可以解决红线问题
//        }


//            //add list view用来显示不同的event和list
//            mListFragment = new EventFragment();
//            getSupportFragmentManager().beginTransaction().add(R.id.event_container, mListFragment).commit();
//
//
//            //add Gridview
//            if (isTablet()) {
//                mGridFragment = new CommentFragment();
//                getSupportFragmentManager().beginTransaction().add(R.id.comment_container, mGridFragment).commit();
//            }


//        Test如何添加一段message// Write a message to the database
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        //get到singleton instance【这里是一个singleton的api
//        DatabaseReference myRef = database.getReference("message");
//        //get database reference, build a key named message in reference
//        //and message is set value as Hello, World!
//        //这里是一对key value pair in data base
//
//        myRef.setValue("Hello, World!");
        // Firebase uses singleton to initialize the sdk
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //database里面得到一个数据的初始化是从firebaseDatabase里面getInstance().getReference();
        //有几种方法可以get database的内容
        //检查是不是同一种类型可以看源代码如何实现+是不是有相应的API，这样就可以对自己写的内容进行确认。

        mUsernameEditText = (EditText) findViewById(R.id.editTextLogin);
        mPasswordEditText = (EditText) findViewById(R.id.editTextPassword);
        mSubmitButton = (Button) findViewById(R.id.submit);
        mRegisterButton = (Button) findViewById(R.id.register);
        //如何完成上面几行instance的初始化
        //先findViewById，然后从id里面读出来，就是R.id，从xml文件里面读出来应该对应的id是什么，
        //按住command键之后找到相应的文件可以看到id是什么。
        // 为了保证前后的文件类型的一致性要在前面加一个括号进行强制类型转换。

        //如何将广告load到后端, 先创建一个mAdView,才可以调用关于广告的API，之后发送请求Request。
        //最后这个local variable才可以使用request将广告load到后端
        //因为已经封装好，所以就不用关系如何将广告插入
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        //Implements button registration click event, Edit MainActivity.java,
        // add following code to OnCreate(), try to register any account,
        // and check the firebase console to verify it is successfully registered.
        //下面写出来的匿名类表示的是click之后会发生的事情
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsernameEditText.getText().toString();
                //从getText里面得到输入的内容并转换成String，并存到username这个key里面
                final String password = Utils.md5Encryption(mPasswordEditText.getText().toString());
                //对密码进行MD5的加密
                final User user = new User(username, password, System.currentTimeMillis());
                //创建一个user的class的object，包括三个参数，id，pw和当前系统时间
                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    //在database创建一个叫users的表，对value event listner 重写，
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //将每一秒的结果都存下来，这样不会因为每一秒的数据都不同而影像系统数据处理
                        if (dataSnapshot.hasChild(username)) {
                            //如果hasChild有这个usernamen存在的话就弹出一个textmessage，toast表示就弹出一下的意思
                            Toast.makeText(getBaseContext(),"username is already registered, please change one", Toast.LENGTH_SHORT).show();
                        } else if (!username.equals("") && !password.equals("")){
                            //如果没有的话，并且us和pw都不为空，就把datainsert到database里面去
                            // put username as key to set value
                            mDatabase.child("users").child(user.getUsername()).setValue(user);
                            Toast.makeText(getBaseContext(),"Successfully registered", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        //下面这段表示的是login的内容，逻辑和上面的内容相似
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsernameEditText.getText().toString();
                final String password = Utils.md5Encryption(mPasswordEditText.getText().toString());
                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(username) && (password.equals(dataSnapshot.child(username).child("password").getValue()))) {
                            Log.i( " Your log", "You successfully login");
                            //因为是想在login之后就能看到events于是在这里插入usernameevents的内容
                            Intent myIntent = new Intent(MainActivity.this, EventActivity.class);
                            Utils.username = username; //设定username
                            startActivity(myIntent); //启动这个intent

                        } else {
                            Toast.makeText(getBaseContext(),"Please login again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });



    }


    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >=
                Configuration.SCREENLAYOUT_SIZE_LARGE;
    }




//    /**
//     * A dummy function to get fake event names.
//     *
//     * @return an array of fake event names.
//     */
//    private String[] getEventNames() {
//        String[] names = {
//                "Event1", "Event2", "Event3",
//                "Event4", "Event5", "Event6",
//                "Event7", "Event8", "Event9",
//                "Event10", "Event11", "Event12"};
//        return names;
//    }






    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Life cycle test", "We are at onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Life cycle test", "We are at onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Life cycle test", "We are at onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Life cycle test", "We are at onStop()");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Life cycle test", "We are at onDestroy()");
    }


}
