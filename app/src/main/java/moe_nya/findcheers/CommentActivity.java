package moe_nya.findcheers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import moe_nya.findcheers.artifacts.Comment;
import moe_nya.findcheers.artifacts.Events;

public class CommentActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private RecyclerView mRecyclerView;
    private EditText mEditTextComment;
    private CommentAdapter commentAdapter;
    private Button mCommentSubmitButton;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        final String eventId = intent.getStringExtra("EventID");

        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler_view);
        mEditTextComment = (EditText) findViewById(R.id.comment_edittext);
        mCommentSubmitButton = (Button) findViewById(R.id.comment_submit);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        commentAdapter = new CommentAdapter(this);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getData(eventId, commentAdapter);

        mCommentSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(eventId);
                mEditTextComment.setText("");
                getData(eventId, commentAdapter);

            }
        });


    }

    private void getData(final String eventId, final CommentAdapter commentAdapter) {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //创建一个comments的表
                DataSnapshot commentSnapshot = dataSnapshot.child("comments");
                //get到所有的comments
                List<Comment> comments = new ArrayList<Comment>();

                //如果表不为空，将所有的内容放到comments的list里面去
                for (DataSnapshot noteDataSnapshot : commentSnapshot.getChildren()) {
                    Comment comment = noteDataSnapshot.getValue(Comment.class);
                    if(comment.getEventId().equals(eventId)) {
                        comments.add(comment);
                    }
                }
                //得到events的那个表，对应的eventId，对应的CommentNumber设置为刚刚得到的commentsize，upload
                //older code said there comment number and add comment share the same snapshot.
                //for update to newly commentNumber, using new dataSnapshot for that.

                mDatabaseReference.getRef().child("events").child(eventId).
                        child("commentNumber").setValue(comments.size());
                commentAdapter.setComments(comments);

                //得到events所有的信息，找到对应的events那一行，
                DataSnapshot eventSnapshot = dataSnapshot.child("events");
                for (DataSnapshot noteDataSnapshot : eventSnapshot.getChildren()) {
                    Events event = noteDataSnapshot.getValue(Events.class);
                    if(event.getId().equals(eventId)) {
                        //判断一下set成为正确的那个events
                        commentAdapter.setEvent(event);
                        int number = event.getCommentNumber();
                        noteDataSnapshot.getRef().child("comments").setValue(number + 1);
                        break;
                    }
                }

                //如果adapter都change好的话就notify说已经弄好了，和getView说重新run 一下，数据变了，重新rander
                if (mRecyclerView.getAdapter() != null) {
                    commentAdapter.notifyDataSetChanged();
                } else {
                    mRecyclerView.setAdapter(commentAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendComment(final String eventId) {
        String description = mEditTextComment.getText().toString();
        if (description.equals("")) {
            return;
        }
        Comment comment = new Comment();
        comment.setCommenter(Utils.username);
        comment.setEventId(eventId);
        comment.setDescription(description);
        comment.setTime(System.currentTimeMillis());
        String key = mDatabaseReference.child("comments").push().getKey();
        comment.setCommentId(key);
        mDatabaseReference.child("comments").child(key).setValue(comment, new DatabaseReference.
                CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast toast = Toast.makeText(getApplicationContext(), ":( The comment is failed," +
                            " please check you network status.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Yay! Comment is posted!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }


}
