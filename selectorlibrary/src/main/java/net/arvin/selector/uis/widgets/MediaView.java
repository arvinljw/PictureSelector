package net.arvin.selector.uis.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.arvin.selector.R;
import net.arvin.selector.SelectorHelper;
import net.arvin.selector.data.Media;
import net.arvin.selector.data.MediaType;
import net.arvin.selector.utils.PSUtil;

/**
 * Created by arvinljw on 2020/7/17 15:14
 * Function：
 * Desc：
 */
public class MediaView extends RelativeLayout implements View.OnClickListener {
    private ImageView imgMedia;
    private TextView tvSelect;
    private ImageView imgTagGif;
    private View layoutTagVideo;
    private TextView tvVideoDuration;
    private Media media;
    private int pos;

    private OnMediaViewClickListener onMediaViewClickListener;

    public MediaView(Context context) {
        this(context, null);
    }

    public MediaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.ps_layout_media, this);
        imgMedia = findViewById(R.id.ps_media_image);
        tvSelect = findViewById(R.id.ps_tv_select);
        imgTagGif = findViewById(R.id.ps_img_gif_tag);
        layoutTagVideo = findViewById(R.id.ps_layout_video_tag);
        tvVideoDuration = findViewById(R.id.ps_tv_video_duration);

        findViewById(R.id.ps_layout_select).setOnClickListener(this);
        imgMedia.setOnClickListener(this);
    }

    public void setData(Media media, int pos, int chooseSize) {
        this.media = media;
        this.pos = pos;

        SelectorHelper.imageEngine.loadImage(imgMedia, media.getUri());

        boolean isSingle = chooseSize == 1;
        tvSelect.setVisibility(isSingle ? View.GONE : View.VISIBLE);
        if (!isSingle) {
            if (media.getChoseNum() == 0) {
                imgMedia.setColorFilter(null);
                tvSelect.setSelected(false);
                tvSelect.setText("");
            } else {
                imgMedia.setColorFilter(Color.parseColor("#66000000"), PorterDuff.Mode.DARKEN);
                tvSelect.setSelected(true);
                tvSelect.setText(String.valueOf(media.getChoseNum()));
            }
        }

        if (MediaType.isGif(media.getMimeType())) {
            layoutTagVideo.setVisibility(View.GONE);
            imgTagGif.setVisibility(View.VISIBLE);
        } else if (MediaType.isVideo(media.getMimeType())) {
            imgTagGif.setVisibility(View.GONE);
            layoutTagVideo.setVisibility(View.VISIBLE);
            tvVideoDuration.setText(PSUtil.getDuration(media.getDuration()));
        } else {
            imgTagGif.setVisibility(View.GONE);
            layoutTagVideo.setVisibility(View.GONE);
        }
    }

    public void setOnMediaViewClickListener(OnMediaViewClickListener onMediaViewClickListener) {
        this.onMediaViewClickListener = onMediaViewClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        if (v == imgMedia) {
            if (onMediaViewClickListener != null) {
                onMediaViewClickListener.onMediaViewClicked(v, OnMediaViewClickListener.TYPE_IMAGE, media, pos);
            }
        } else if (v.getId() == R.id.ps_layout_select) {
            if (onMediaViewClickListener != null) {
                onMediaViewClickListener.onMediaViewClicked(v, OnMediaViewClickListener.TYPE_SELECT, media, pos);
            }
        }
    }

    public interface OnMediaViewClickListener {
        int TYPE_IMAGE = 1;
        int TYPE_SELECT = 2;

        void onMediaViewClicked(View v, int type, Media item, int pos);
    }
}
