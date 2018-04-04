 package moe_nya.findcheers;

        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.os.AsyncTask;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.FrameLayout;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.google.android.gms.ads.AdListener;
        import com.google.android.gms.ads.AdLoader;
        import com.google.android.gms.ads.AdRequest;

        import com.google.android.gms.ads.formats.NativeAd;
        import com.google.android.gms.ads.formats.NativeContentAd;
        import com.google.android.gms.ads.formats.NativeContentAdView;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        import moe_nya.findcheers.artifacts.Events;
        import moe_nya.findcheers.artifacts.Like;

 /**
 * Created by moenya on 3/24/18.
 */

public class EventListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Events> eventList;
    private Context context;
    private DatabaseReference databaseReference;


     //TYPE_ITEM and TYPE_ADS are identification of item type
//TYPE_ITEM = event
//TYPE_ADS = ads
     private static final int TYPE_ITEM = 0;
     private static final int TYPE_ADS = 1;

     private AdLoader.Builder builder;
     private LayoutInflater inflater;


     //Keep position of the ads in the list\
     private Map<Integer, Object> map =
             new HashMap<Integer, Object>();

     private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";




     /**
      * Constructor, create a new list that references right item in right location
      * @param events events need to show
      * @param context context
      */

     public EventListAdapter(List<Events> events, final Context context) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        eventList = events;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(
                 Context.LAYOUT_INFLATER_SERVICE);

         eventList = new ArrayList<Events>();
         int count = 0;
         for (int i = 0; i < events.size() ; i++) {
             if (i % 3 == 2) {
                 map.put(i - count, new Object());
                 count++;
                 eventList.add(new Events());
             }
             eventList.add(events.get(events.size() - 1 - i));
         }

     }




    /**
     * Use ViewHolder to hold view widget, view holder is required to be used in recycler view
     * https://developer.android.com/training/improving-layouts/smooth-scrolling.html
     * describe the advantage of using view holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView location;
        public TextView description;
        public TextView time;
        public ImageView imgview;
        public View layout;

        public ImageView img_view_good;
        public ImageView img_view_comment;

        public TextView good_number;
        public TextView comment_number;

//        public TextView eventLikeNumber;
//        public ImageView eventImgViewGood;


        public ViewHolder(View v) {
            super(v);
            layout = v;
            title = (TextView) v.findViewById(R.id.event_item_title);
            location = (TextView) v.findViewById(R.id.event_item_location);
            description = (TextView) v.findViewById(R.id.event_item_description);
            time = (TextView) v.findViewById(R.id.event_item_time);
            imgview = (ImageView) v.findViewById(R.id.event_item_img);

            img_view_good = (ImageView) v.findViewById(R.id.event_good_img);
            img_view_comment = (ImageView) v.findViewById(R.id.event_comment_img);
            good_number = (TextView) v.findViewById(R.id.event_good_number);
            comment_number = (TextView) v.findViewById(R.id.event_comment_number);

        }
    }

     /**
      * View Holder Class for advertisement
      */
     public class ViewHolderAds extends RecyclerView.ViewHolder {
         public FrameLayout frameLayout;
         ViewHolderAds(View v) {
             super(v);
             frameLayout = (FrameLayout)v;
         }
     }

     @Override
     public int getItemViewType(int position) {
         return map.containsKey(position) ? TYPE_ADS : TYPE_ITEM;
     }





//     /**
//     * OnBindViewHolder will render created view holder on screen
//     * @param holder View Holder created for each position
//     * @param position position needs to show
//     */
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, final int position) {




     // Replace the contents of a view (invoked by the layout manager)
     @Override
     public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
         switch (holder.getItemViewType()) {
             //TODO : according to different view type, show corresponding view
             case TYPE_ITEM:
                 ViewHolder viewHolderItem = (ViewHolder) holder;
                 configureItemView(viewHolderItem, position);
                 break;
             case TYPE_ADS:
                 ViewHolderAds viewHolderAds = (ViewHolderAds) holder;
                 refreshAd(viewHolderAds.frameLayout);
                 break;

         }
     }



     /**
     * By calling this method, each ViewHolder will be initiated and passed to OnBindViewHolder
     * for rendering
     * @param parent parent view
     * @param viewType we might have multiple view types
     * @return ViewHolder created
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(
//                parent.getContext());
//        View v = inflater.inflate(R.layout.event_list_item, parent, false);
//        ViewHolder vh = new ViewHolder(v);
//        return vh;

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType) {
            case TYPE_ITEM:
                v = inflater.inflate(R.layout.event_list_item, parent, false);
                viewHolder = new ViewHolder(v);
                break;
            case TYPE_ADS:
                v = inflater.inflate(R.layout.ads_container,
                        parent, false);
                viewHolder = new ViewHolderAds(v);
                break;
        }
        return viewHolder;

    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }


     //检查是不是在相对应的event里面有点过赞，如果点过就删除，如果没有就+1， 建立一个likes的表格，
     //把eventId和username建立一个表，这样就可以完成下列操作
     private final void setLike(final DataSnapshot eventsSnapshot, final ViewHolder holder,
                                final int number, final Events event) {

         databaseReference.child("likes").addListenerForSingleValueEvent(new ValueEventListener(){
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                     Like like = snapshot.getValue(Like.class);
                     if (like.getUserId().equals(Utils.username) && like.getEventId().equals(event.getId())) {
                         snapshot.getRef().removeValue();
                         holder.good_number.setText(String.valueOf(number - 1));
                         holder.img_view_good.setImageResource(R.drawable.star);
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
                 holder.good_number.setText(String.valueOf(number + 1));
                 holder.img_view_good.setImageResource(R.drawable.stared);
                 eventsSnapshot.getRef().child("like").setValue(number + 1);
             }
             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
     }

     /**
      * Show Event
      * @param holder event view holder
      * @param position position of the event
      */
     //Pull out main item rendering to extra function
     private void configureItemView(final ViewHolder holder, final int position) {
         final Events event = eventList.get(position);
        holder.title.setText(event.getTitle());
        String[] locations = event.getAddress().split(",");
        holder.location.setText(locations[1] + "," + locations[2]);
        holder.description.setText(event.getDescription());
        holder.time.setText(String.valueOf(Utils.timeTransFormer(event.getTime())));

        holder.good_number.setText(String.valueOf(event.getLike()));

        if (event.getImgUri() != null) {
            final String url = event.getImgUri();
            holder.imgview.setVisibility(View.VISIBLE);
            new AsyncTask<Void, Void, Bitmap>(){
                @Override
                protected Bitmap doInBackground(Void... params) {
                    return Utils.getBitmapFromURL(url);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    holder.imgview.setImageBitmap(bitmap);
                }
            }.execute();
        } else {
            holder.imgview.setVisibility(View.GONE);
        }

        //When user likes the event, push like number to firebase database
        holder.img_view_good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Events recordedevent = snapshot.getValue(Events.class);
                            if (recordedevent.getId().equals(event.getId())) {
                                int number = recordedevent.getLike();
                                setLike(snapshot, holder, number, event);
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
                        holder.comment_number.setText(String.valueOf(number));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("EventID", event.getId());
                context.startActivity(intent);
            }
        });
     }

     /**
      * refresh ads, there are several steps falling through
      * First, load advertisement from remote
      * Second, add content to ads view
      * @param frameLayout
      */
     private void refreshAd(final FrameLayout frameLayout) {
         AdLoader.Builder builder = new AdLoader.Builder(context, ADMOB_AD_UNIT_ID);
         builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
             @Override
             public void onContentAdLoaded(NativeContentAd ad) {
                 NativeContentAdView adView = (NativeContentAdView) inflater
                         .inflate(R.layout.ads_contain, null);
                 populateContentAdView(ad, adView);
                 frameLayout.removeAllViews();
                 frameLayout.addView(adView);
             }
         });

         AdLoader adLoader = builder.withAdListener(new AdListener() {
             @Override
             public void onAdFailedToLoad(int errorCode) {
             }
         }).build();

         adLoader.loadAd(new AdRequest.Builder().build());
     }


     private void populateContentAdView(NativeContentAd nativeContentAd,
                                        NativeContentAdView adView) {
         adView.setHeadlineView(adView.findViewById(R.id.ads_headline));
         adView.setImageView(adView.findViewById(R.id.ads_image));
         adView.setBodyView(adView.findViewById(R.id.ads_body));
         adView.setAdvertiserView(adView.findViewById(R.id.ads_advertiser));

         // Some assets are guaranteed to be in every NativeContentAd.
         ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
         ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
         ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

         List<NativeAd.Image> images = nativeContentAd.getImages();

         if (images.size() > 0) {
             ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
         }

         // Assign native ad object to the native view.
         adView.setNativeAd(nativeContentAd);
     }




 }
