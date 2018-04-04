package moe_nya.findcheers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import moe_nya.findcheers.artifacts.Comment;
import moe_nya.findcheers.artifacts.Events;
import moe_nya.findcheers.artifacts.Like;


//adapter for comment recycler view. add private parameters to the class
    //每一个list of view都需要一个adapter
public class CommentAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private final static int TYPE_EVENT = 0;
    private final static int TYPE_COMMENT = 1;

    private List<Comment> commentList;
    private Events event;

    private DatabaseReference databaseReference;
    private LayoutInflater inflater;



    public CommentAdapter(Context context) {
        this.context = context;
        commentList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setEvent(final Events event) {
        this.event = event;
    }

    public void setComments(final List<Comment> comments) {
        this.commentList = comments;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_EVENT : TYPE_COMMENT;
    }

    @Override
    public int getItemCount() {
        return commentList.size() + 1;
    }



    public class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView eventUser;
        public TextView eventTitle;
        public TextView eventLocation;
        public TextView eventDescription;
        public TextView eventTime;
        public ImageView eventImgView;
        public ImageView eventImgViewGood;
        public ImageView eventImgViewComment;

        public TextView eventLikeNumber;
        public TextView eventCommentNumber;
        public View layout;

        public EventViewHolder(View v) {
            super(v);
            layout = v;
            eventUser = (TextView) v.findViewById(R.id.comment_main_user);
            eventTitle = (TextView) v.findViewById(R.id.comment_main_title);
            eventLocation = (TextView) v.findViewById(R.id.comment_main_location);
            eventDescription = (TextView) v.findViewById(R.id.comment_main_description);
            eventTime = (TextView) v.findViewById(R.id.comment_main_time);
            eventImgView = (ImageView) v.findViewById(R.id.comment_main_image);
            eventImgViewGood = (ImageView) v.findViewById(R.id.comment_main_like_img);
            eventImgViewComment = (ImageView) v.findViewById(R.id.comment_main_comment_img);
            eventLikeNumber = (TextView) v.findViewById(R.id.comment_main_like_number);
            eventCommentNumber = (TextView) v.findViewById(R.id.comment_main_comment_number);
        }
    }



    public class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView commentUser;
        public TextView commentTime;
        public TextView commentDescription;

        public View layout;

        public CommentViewHolder(View v) {
            super(v);
            layout = v;
            commentUser = (TextView) v.findViewById(R.id.comment_item_user);
            commentTime = (TextView) v.findViewById(R.id.comment_item_time);
            commentDescription = (TextView) v.findViewById(R.id.comment_item_description);

        }
    }

    //create viewholder and initialized
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType) {
            case TYPE_EVENT:
                v = inflater.inflate(R.layout.comment_main, parent, false);
                viewHolder = new EventViewHolder(v);
                break;
            case TYPE_COMMENT:
                v = inflater.inflate(R.layout.comment_item, parent, false);
                viewHolder = new CommentViewHolder(v);
                break;
        }
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    // 对每一个item进行绘画
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TYPE_EVENT:
                EventViewHolder viewHolderEvent = (EventViewHolder) holder;
                configureEventView(viewHolderEvent);
                break;
            case TYPE_COMMENT:
                CommentViewHolder viewHolderAds = (CommentViewHolder) holder;
                configureCommentView(viewHolderAds, position);
                break;
        }
    }



    private void configureCommentView(final CommentViewHolder commentHolder, final int position) {
        //why is position -1?
        //because the first one is event, others are comments
        final Comment comment = commentList.get(position - 1);
        commentHolder.commentUser.setText(comment.getCommenter());
        commentHolder.commentDescription.setText(comment.getDescription());
        commentHolder.commentTime.setText(Utils.timeTransFormer(comment.getTime()));
    }

    private void configureEventView(final EventViewHolder holder) {
        holder.eventUser.setText(event.getUsername());
        holder.eventTitle.setText(event.getTitle());
        String[] locations = event.getAddress().split(",");
        holder.eventLocation.setText(locations[1] + "," + locations[2]);
        holder.eventDescription.setText(event.getDescription());
        holder.eventTime.setText(Utils.timeTransFormer(event.getTime()));
        holder.eventCommentNumber.setText(String.valueOf(event.getCommentNumber()));
        holder.eventLikeNumber.setText(String.valueOf(event.getLike()));


        if (event.getImgUri() != null) {
            final String url = event.getImgUri();
            holder.eventImgView.setVisibility(View.VISIBLE);
            new AsyncTask<Void, Void, Bitmap>(){
                @Override
                protected Bitmap doInBackground(Void... params) {
                    return Utils.getBitmapFromURL(url);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    holder.eventImgView.setImageBitmap(bitmap);
                }
            }.execute();
        } else {
            holder.eventImgView.setVisibility(View.GONE);
        }




        holder.eventImgViewGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Events recordedevent = snapshot.getValue(Events.class);
                            if (recordedevent.getId().equals(event.getId())) {
                                int number = recordedevent.getLike();
                                setLike(snapshot, holder, number);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        databaseReference.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Events recordedevent = snapshot.getValue(Events.class);
                    if (recordedevent.getId().equals(event.getId())) {
                        int number = recordedevent.getCommentNumber();
                        holder.eventCommentNumber.setText(String.valueOf(number));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //检查是不是在相对应的event里面有点过赞，如果点过就删除，如果没有就+1， 建立一个likes的表格，
    //把eventId和username建立一个表，这样就可以完成下列操作
    private final void setLike(final DataSnapshot eventsSnapshot, final EventViewHolder holder, final int number) {
        databaseReference.child("likes").addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Like like = snapshot.getValue(Like.class);
                    if (like.getUserId().equals(Utils.username) && like.getEventId().equals(event.getId())) {
                        snapshot.getRef().removeValue();
                        holder.eventLikeNumber.setText(String.valueOf(number - 1));
                        holder.eventImgViewGood.setImageResource(R.drawable.star);
                        eventsSnapshot.getRef().child("like").setValue(number - 1);
                        return;
                    }
                }
                Like like = new Like();
                like.setEventId(event.getId());
                like.setUserId(Utils.username);
                String key = databaseReference.child("likes").push().getKey();

                like.setLikeId(key);
                databaseReference.child("likes").child(key).setValue(like);
                holder.eventLikeNumber.setText(String.valueOf(number + 1));
                holder.eventImgViewGood.setImageResource(R.drawable.stared);
                eventsSnapshot.getRef().child("like").setValue(number + 1);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}

