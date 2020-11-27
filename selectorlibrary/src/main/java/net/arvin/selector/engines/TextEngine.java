package net.arvin.selector.engines;

import android.app.Activity;
import android.content.Context;

import net.arvin.selector.data.MediaType;
import net.arvin.selector.uis.fragments.PreviewFragment;

/**
 * Created by arvinljw on 2020/7/14 16:17
 * Function：
 * Desc：
 */
public interface TextEngine {
    String titleAll(Context context, MediaType mediaType);

    String titleAllVideo(Context context);

    String ensureChoose(Context context, int count, int maxCount);

    String alreadyMaxCount(Context context, int count);

    String previewTitle(Context context, int count, int maxCount);

    String previewCount(Context context, int count);

    String currentWeek(Context context);

    String currentMonth(Context context);

    String notFoundVideoPlayer(Context context);

    String deleteTips(Context context);

    String releaseDeleteTips(Context context);
}
