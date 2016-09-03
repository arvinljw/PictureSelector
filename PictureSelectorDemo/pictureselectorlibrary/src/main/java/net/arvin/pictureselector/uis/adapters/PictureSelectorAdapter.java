package net.arvin.pictureselector.uis.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import net.arvin.pictureselector.R;
import net.arvin.pictureselector.entities.ImageEntity;
import net.arvin.pictureselector.listeners.OnClickListener;
import net.arvin.pictureselector.listeners.OnItemClickListener;
import net.arvin.pictureselector.listeners.OnItemSelectedListener;
import net.arvin.pictureselector.utils.PSConfigUtil;
import net.arvin.pictureselector.utils.PSGlideUtil;
import net.arvin.pictureselector.views.SquareImageView;

import java.util.List;

/**
 * created by arvin on 16/8/29 21:58
 * email：1035407623@qq.com
 */
public class PictureSelectorAdapter extends RecyclerView.Adapter {
    private static final int TYPE_TAKE_PHOTO = 1;
    private static final int TYPE_NORMAL = 2;

    private Context mContext;
    private List<ImageEntity> mItems;

    private OnItemClickListener onItemClickListener;
    private OnItemSelectedListener onItemSelectedListener;

    public PictureSelectorAdapter(Context mContext, List<ImageEntity> mItems) {
        this.mContext = mContext;
        this.mItems = mItems;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (PSConfigUtil.getInstance().isCanTakePhoto() && position == 0) {
            return TYPE_TAKE_PHOTO;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mItems.size() + getCanTakePhoto();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TAKE_PHOTO) {
            return new TakePhotoHolder(LayoutInflater.from(mContext).inflate(R.layout.ps_item_take_photo, parent, false));
        }
        return new PictureSelectorHolder(LayoutInflater.from(mContext).inflate(R.layout.ps_item_picture_selector, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        setItemClick(holder, position);

        if (getItemViewType(position) == TYPE_TAKE_PHOTO) {
            return;
        }
        if (PSConfigUtil.getInstance().isCanTakePhoto()) {
            position -= 1;
        }
        ((PictureSelectorHolder) holder).setData(mItems.get(position));
        ((PictureSelectorHolder) holder).setEvent(position);
    }

    private void setItemClick(RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new OnClickListener(position) {
            @Override
            public void onClick(View v, int position) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    public class PictureSelectorHolder extends RecyclerView.ViewHolder {
        SquareImageView imgContent;
        ImageView imgSelector;
        FrameLayout layoutSelector;

        public PictureSelectorHolder(View itemView) {
            super(itemView);
            imgContent = (SquareImageView) itemView.findViewById(R.id.img_content);
            imgSelector = (ImageView) itemView.findViewById(R.id.img_selector);
            layoutSelector = (FrameLayout) itemView.findViewById(R.id.layout_selector);
        }

        public void setData(ImageEntity item) {
            PSGlideUtil.loadImage(mContext, "file://" + item.getPath(), imgContent);
            imgSelector.setSelected(item.isSelected());
        }

        public void setEvent(int position) {
            if(!PSConfigUtil.getInstance().canReview()){
                layoutSelector.setVisibility(View.GONE);
            }else{
                layoutSelector.setVisibility(View.VISIBLE);
            }

            layoutSelector.setOnClickListener(new OnClickListener(position) {
                @Override
                public void onClick(View v, int position) {
                    ImageEntity item = mItems.get(position);
                    if (!PSConfigUtil.getInstance().canAdd()&&!item.isSelected()) {
                        Toast.makeText(mContext, mContext.getString(R.string.ps_max_count_tips,PSConfigUtil.getInstance().getMaxCount()),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    item.setSelected(!item.isSelected());
                    imgSelector.setSelected(item.isSelected());
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onItemSelected(v, position, item.isSelected());
                    }
                }
            });
        }
    }

    private int getCanTakePhoto() {
        return PSConfigUtil.getInstance().isCanTakePhoto() ? 1 : 0;
    }

    public class TakePhotoHolder extends RecyclerView.ViewHolder {
        public TakePhotoHolder(View itemView) {
            super(itemView);
        }
    }

}
