package net.arvin.selector.engines.defaultimpl;

import android.content.Context;

import net.arvin.selector.R;
import net.arvin.selector.data.MediaType;
import net.arvin.selector.engines.TextEngine;

/**
 * Created by arvinljw on 2020/7/14 16:53
 * Function：
 * Desc：
 */
public class DefaultTextEngine implements TextEngine {

    @Override
    public String titleAll(Context context, MediaType mediaType) {
        if (mediaType == MediaType.IMAGE) {
            return context.getResources().getString(R.string.ps_image_all);
        } else if (mediaType == MediaType.VIDEO) {
            return context.getResources().getString(R.string.ps_video_all);
        }
        return context.getResources().getString(R.string.ps_type_all);
    }

    @Override
    public String titleAllVideo(Context context) {
        return context.getResources().getString(R.string.ps_video_all);
    }

    @Override
    public String ensureChoose(Context context, int count, int maxCount) {
        if (count == 0 || maxCount == 1) {
            return context.getResources().getString(R.string.ps_ensure);
        }
        return String.format(context.getResources().getString(R.string.ps_ensure_count), count, maxCount);
    }

    @Override
    public String alreadyMaxCount(Context context, int count) {
        return String.format(context.getResources().getString(R.string.ps_max_count), count);
    }

    @Override
    public String previewTitle(Context context, int count, int maxCount) {
        return String.format(context.getResources().getString(R.string.ps_preview_title_count), count, maxCount);
    }

    @Override
    public String previewCount(Context context, int count) {
        if (count == 0) {
            return context.getResources().getString(R.string.ps_preview);
        }
        return String.format(context.getResources().getString(R.string.ps_preview_count), count);
    }

    @Override
    public String currentWeek(Context context) {
        return context.getResources().getString(R.string.ps_curr_week);
    }

    @Override
    public String currentMonth(Context context) {
        return context.getResources().getString(R.string.ps_curr_month);
    }

    @Override
    public String notFoundVideoPlayer(Context context) {
        return context.getResources().getString(R.string.ps_not_found_video_player);
    }

    @Override
    public String deleteTips(Context context) {
        return context.getString(R.string.ps_edit_delete_tips);
    }

    @Override
    public String releaseDeleteTips(Context context) {
        return context.getString(R.string.ps_edit_release_delete_tips);
    }
}
