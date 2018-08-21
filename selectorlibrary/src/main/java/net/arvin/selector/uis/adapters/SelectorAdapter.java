package net.arvin.selector.uis.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import net.arvin.selector.R;
import net.arvin.selector.data.ConstantData;
import net.arvin.selector.entities.FileEntity;
import net.arvin.selector.listeners.OnItemClickListener;
import net.arvin.selector.listeners.OnItemSelectListener;
import net.arvin.selector.utils.PSGlideUtil;
import net.arvin.selector.utils.PSScreenUtil;
import net.arvin.selector.utils.PSToastUtil;
import net.arvin.selector.utils.PSUtil;

import java.util.List;

/**
 * Created by arvinljw on 17/12/25 17:35
 * Function：
 * Desc：
 */
public class SelectorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CAMERA = 1;
    private static final int TYPE_ITEM = 2;
    private Context mContext;
    private List<FileEntity> mItems;

    private boolean mWithCamera;
    private int mMaxCount;
    private int mSelectedCount;

    private OnItemClickListener mItemClickListener;
    private OnItemSelectListener mItemSelectListener;

    public SelectorAdapter(Context context, List<FileEntity> items) {
        this.mContext = context;
        this.mItems = items;
        resetSelectedCount();
    }

    @Override
    public int getItemCount() {
        return mItems.size() + (mWithCamera ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return (mWithCamera && position == 0) ? TYPE_CAMERA : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CAMERA) {
            return new CameraViewHolder(LayoutInflater.from(mContext).inflate(R.layout.ps_item_take_photo, parent, false));
        }
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.ps_item_selector, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_CAMERA) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, -1);
                    }
                }
            });
            return;
        }
        ViewHolder realHolder = (ViewHolder) holder;

        if (mWithCamera) {
            if (position == 0) {
                realHolder.mImgSelector.setVisibility(View.GONE);
                PSGlideUtil.loadImage(mContext, R.drawable.ps_img_take_photo, realHolder.mImgContent);
                return;
            } else {
                position = position - 1;
            }
        }
        FileEntity entity = mItems.get(position);
        PSGlideUtil.loadImage(mContext, PSUtil.createFileUrl(entity.getPath()), realHolder.mImgContent);

        realHolder.mImgSelector.setVisibility(mMaxCount == 1 ? View.GONE : View.VISIBLE);
        realHolder.mImgSelector.setSelected(entity.isSelected());

        final int realPos = position;
        setListener(realHolder, realPos);
    }

    private void setListener(final ViewHolder holder, final int realPos) {
        holder.mLayoutSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileEntity entity = mItems.get(realPos);
                if (!entity.isSelected()) {
                    if (mSelectedCount >= mMaxCount) {
                        PSToastUtil.showToast(R.string.ps_cant_add);
                        return;
                    }
                    ConstantData.addSelectedItem(entity);
                    mSelectedCount++;
                } else {
                    ConstantData.removeItem(entity);
                    mSelectedCount--;
                }
                entity.setSelected(!entity.isSelected());
                holder.mImgSelector.setSelected(entity.isSelected());
                if (mItemSelectListener != null) {
                    mItemSelectListener.onItemSelected(v, mSelectedCount);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, realPos);
                }
            }
        });
    }

    public void setWithCamera(boolean withCamera) {
        this.mWithCamera = withCamera;
    }

    public void setMaxCount(int maxCount) {
        this.mMaxCount = maxCount;
    }

    public void setSelectedCount(int selectedCount) {
        this.mSelectedCount = selectedCount;
    }

    private void resetSelectedCount() {
        this.mSelectedCount = 0;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setItemSelectListener(OnItemSelectListener itemSelectListener) {
        this.mItemSelectListener = itemSelectListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgContent;
        ImageView mImgSelector;
        FrameLayout mLayoutSelector;

        ViewHolder(View itemView) {
            super(itemView);
            mImgContent = itemView.findViewById(R.id.ps_img_content);
            ViewGroup.LayoutParams layoutParams = mImgContent.getLayoutParams();
            layoutParams.height = PSScreenUtil.getScreenWidth() / ConstantData.VALUE_SPAN_COUNT;
            mImgContent.setLayoutParams(layoutParams);

            mImgSelector = itemView.findViewById(R.id.ps_img_selector);
            mLayoutSelector = itemView.findViewById(R.id.ps_layout_selector);
        }
    }

    static class CameraViewHolder extends RecyclerView.ViewHolder {

        CameraViewHolder(View itemView) {
            super(itemView);
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.height = PSScreenUtil.getScreenWidth() / ConstantData.VALUE_SPAN_COUNT;
            itemView.setLayoutParams(layoutParams);
        }
    }
}
