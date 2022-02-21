package app.manager.dating.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.balysv.materialripple.MaterialRippleLayout;
import com.manager.dating.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.manager.dating.PhotoViewActivity;
import app.manager.dating.ProfileActivity;
import app.manager.dating.VideoViewActivity;
import app.manager.dating.app.App;
import app.manager.dating.constants.Constants;
import app.manager.dating.model.Image;
import app.manager.dating.view.ResizableImageView;




public class ModerateMediaItemsListAdapter extends RecyclerView.Adapter<ModerateMediaItemsListAdapter.ViewHolder> implements Constants {

    private List<Image> items = new ArrayList<>();

    private Context context;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

    private OnItemMenuButtonClickListener onItemMenuButtonClickListener;

    public interface OnItemMenuButtonClickListener {

        void onItemClick(View view, Image obj, int actionId, int position);
    }

    public void setOnMoreButtonClickListener(final OnItemMenuButtonClickListener onItemMenuButtonClickListener) {

        this.onItemMenuButtonClickListener = onItemMenuButtonClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircularImageView mItemAuthorPhoto, mItemAuthorIcon;
        public TextView mItemAuthor;
        public ImageView mItemAuthorOnlineIcon;
        public ImageView mItemMenuButton;
        public ResizableImageView mItemImg;
        public RelativeLayout mImageLayout;
        public TextView mItemDescription;
        public TextView mItemTimeAgo;
        public ProgressBar mImageProgressBar;
        public MaterialRippleLayout mItemRejectBtn, mItemAcceptBtn;

        public ViewHolder(View v) {

            super(v);

            mItemAuthorPhoto = (CircularImageView) v.findViewById(R.id.itemAuthorPhoto);
            mItemAuthorIcon = (CircularImageView) v.findViewById(R.id.itemAuthorIcon);

            mItemAuthor = (TextView) v.findViewById(R.id.itemAuthor);
            mItemAuthorOnlineIcon = (ImageView) v.findViewById(R.id.itemAuthorOnlineIcon);

            mImageLayout = (RelativeLayout) v.findViewById(R.id.image_layout);

            mItemImg = (ResizableImageView) v.findViewById(R.id.item_image);

            mImageProgressBar = (ProgressBar) v.findViewById(R.id.image_progress_bar);

            mItemDescription = (TextView) v.findViewById(R.id.itemDescription);

            mItemMenuButton = (ImageView) v.findViewById(R.id.itemMenuButton);
            mItemTimeAgo = (TextView) v.findViewById(R.id.itemTimeAgo);

            mItemRejectBtn = (MaterialRippleLayout) v.findViewById(R.id.itemRejectBtn);
            mItemAcceptBtn = (MaterialRippleLayout) v.findViewById(R.id.itemAcceptBtn);
        }

    }

    public ModerateMediaItemsListAdapter(Context ctx, List<Image> items) {

        this.context = ctx;
        this.items = items;

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        onBindItem(holder, position);
    }

    public void onBindItem(ViewHolder holder, final int position) {

        final Image p = items.get(position);

        holder.mImageProgressBar.setVisibility(View.GONE);

        holder.mImageLayout.setVisibility(View.GONE);

        holder.mItemAuthorPhoto.setVisibility(View.VISIBLE);

        holder.mItemAuthorPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("profileId", p.getOwner().getId());
                context.startActivity(intent);
            }
        });

        if (p.getOwner().getLowPhotoUrl().length() != 0) {

            imageLoader.get(p.getOwner().getLowPhotoUrl(), ImageLoader.getImageListener(holder.mItemAuthorPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

        } else {

            holder.mItemAuthorPhoto.setVisibility(View.VISIBLE);
            holder.mItemAuthorPhoto.setImageResource(R.drawable.profile_default_photo);
        }

        if (p.getOwner().getVerified() == 1) {

            holder.mItemAuthorIcon.setVisibility(View.VISIBLE);

        } else {

            holder.mItemAuthorIcon.setVisibility(View.GONE);
        }

        holder.mItemAuthor.setVisibility(View.VISIBLE);
        holder.mItemAuthor.setText(p.getOwner().getFullname());

        holder.mItemAuthor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("profileId", p.getOwner().getId());
                context.startActivity(intent);
            }
        });

        holder.mItemAuthorOnlineIcon.setVisibility(View.GONE);

        if (p.getVideoUrl() != null && p.getVideoUrl().length() != 0) {

            holder.mImageLayout.setVisibility(View.VISIBLE);
            holder.mImageProgressBar.setVisibility(View.GONE);
            holder.mItemImg.setImageResource(R.drawable.ic_video_preview);

        } else {

            if (p.getImgUrl().length() != 0) {

                holder.mImageLayout.setVisibility(View.VISIBLE);
                holder.mItemImg.setVisibility(View.VISIBLE);
                holder.mImageProgressBar.setVisibility(View.VISIBLE);

                final ProgressBar progressView = holder.mImageProgressBar;
                final ImageView imageView = holder.mItemImg;

                Picasso.with(context)
                        .load(p.getImgUrl())
                        .into(holder.mItemImg, new Callback() {

                            @Override
                            public void onSuccess() {

                                progressView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {

                                progressView.setVisibility(View.GONE);
                                imageView.setImageResource(R.drawable.profile_default_cover);
                            }
                        });

            }
        }

        holder.mItemImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (p.getVideoUrl() != null && p.getVideoUrl().length() != 0) {

                    watchVideo(p.getVideoUrl());

                } else {

                    Intent i = new Intent(context, PhotoViewActivity.class);
                    i.putExtra("imgUrl", p.getImgUrl());
                    context.startActivity(i);
                }
            }
        });

        if (p.getComment().length() != 0) {

            holder.mItemDescription.setVisibility(View.VISIBLE);
            holder.mItemDescription.setText(p.getComment().replaceAll("<br>", "\n"));

        } else {

            holder.mItemDescription.setVisibility(View.GONE);
        }

        holder.mItemTimeAgo.setVisibility(View.VISIBLE);
        holder.mItemTimeAgo.setText(p.getTimeAgo());


        holder.mItemMenuButton.setVisibility(View.VISIBLE);

        holder.mItemMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                onItemMenuButtonClick(view, p, position, ITEM_ACTION_MENU);
            }
        });

        final ImageView mItemMenuButton = holder.mItemMenuButton;

        holder.mItemMenuButton.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    animateIcon(mItemMenuButton);
                }

                return false;
            }
        });

        holder.mItemAcceptBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                onItemMenuButtonClick(view, p, position, ITEM_ACTION_APPROVE);
            }
        });

        holder.mItemRejectBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                onItemMenuButtonClick(view, p, position, ITEM_ACTION_REJECT);
            }
        });
    }

    private void onItemMenuButtonClick(final View view, final Image item, final int position, final int action){

        onItemMenuButtonClickListener.onItemClick(view, item, position, action);
    }

    private void animateIcon(ImageView icon) {

        ScaleAnimation scale = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(175);
        scale.setInterpolator(new LinearInterpolator());

        icon.startAnimation(scale);
    }

    public void watchVideo(String videoUrl) {

        Intent i = new Intent(context, VideoViewActivity.class);
        i.putExtra("videoUrl", videoUrl);
        context.startActivity(i);
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }
}