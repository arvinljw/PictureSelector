package net.arvin.selector.utils;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import net.arvin.selector.SelectorHelper;
import net.arvin.selector.data.Media;
import net.arvin.selector.data.MediaFolder;
import net.arvin.selector.uis.adapters.MediaAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by arvinljw on 2020/7/16 16:27
 * Function：
 * Desc：
 */
public final class PSUtil {
    private static final long HOUR = 60 * 60 * 1000;
    private static final long MINUTE = 60 * 1000;

    private static long lastClickTime;

    public static boolean afterVersionQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    public static boolean isQuickClick(int time) {
        long now = System.currentTimeMillis();
        if (now - lastClickTime <= time) {
            return true;
        }
        lastClickTime = now;
        return false;
    }

    /**
     * 未选中，则变为选中
     */
    public static void chooseItem(MediaAdapter adapter, Map<Uri, Integer> choseMedias, List<Media> choseMediaList, int pos, int choseCount) {
        Media media = adapter.getItems().get(pos);
        media.setChoseNum(choseCount);
        adapter.notifyItemChanged(pos);
        choseMedias.put(media.getUri(), media.getChoseNum());
        choseMediaList.add(media);
    }

    /**
     * 变为未选中
     * 并且把这个数量之后的选中数量减1
     */
    public static void cancelItem(MediaAdapter adapter, Map<Uri, Integer> choseMedias, List<Media> choseMediaList, int pos) {
        List<Media> currList = adapter.getItems();
        List<Media> newList = new ArrayList<>();
        Media item = currList.get(pos);
        int choseNum = item.getChoseNum();
        for (Media media : currList) {
            Media newItem = media.clone();
            Uri key = media.getUri();
            if (key.equals(item.getUri())) {
                newItem.setChoseNum(0);
            }
            if (newItem.getChoseNum() > choseNum) {
                newItem.setChoseNum(newItem.getChoseNum() - 1);
            }
            newList.add(newItem);
        }

        //选中的media中调整num
        for (Uri key : choseMedias.keySet()) {
            Integer num = choseMedias.get(key);
            if (num == null) {
                continue;
            }
            if (num > choseNum) {
                choseMedias.put(key, num - 1);
            }
        }
        choseMedias.remove(item.getUri());

        choseMediaList.remove(item);

        adapter.setData(currList, newList, false);
    }

    public static List<Media> generateNewList(MediaFolder folder, Map<Uri, Integer> choseMedias) {
        List<Media> medias = folder.getMedias();
        List<Media> newList = new ArrayList<>();
        for (Media media : medias) {
            Uri key = media.getUri();
            Media newItem = media.clone();
            if (choseMedias.containsKey(key)) {
                Integer choseNum = choseMedias.get(key);
                if (choseNum == null) {
                    choseNum = 0;
                }
                newItem.setChoseNum(choseNum);
            }
            newList.add(newItem);
        }
        return newList;
    }

    public static String getDuration(long duration) {
        long hour = 0;
        if (duration > HOUR) {
            hour = duration / HOUR;
            duration = duration - hour * HOUR;
        }
        long minute = 0;
        if (duration > MINUTE) {
            minute = duration / MINUTE;
            duration = duration - minute * MINUTE;
        }
        long sec = duration / 1000;
        StringBuilder stringBuilder = new StringBuilder();
        if (hour > 0) {
            stringBuilder.append(hour).append(":");
        }
        if (minute > 0) {
            stringBuilder.append(minute).append(":");
        } else {
            stringBuilder.append(0).append(":");
        }
        if (sec > 0) {
            if (sec > 10) {
                stringBuilder.append(sec);
            } else {
                stringBuilder.append(0).append(sec);
            }
        } else {
            stringBuilder.append("00");
        }
        return stringBuilder.toString();
    }

    public static String getDate(Context context, long dateTaken) {
        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTaken);

        if (now.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            if (now.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                if (now.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                    return SelectorHelper.textEngine.currentWeek(context);
                }
                return SelectorHelper.textEngine.currentMonth(context);
            }
            return getMd(dateTaken);
        }
        return getYmd(dateTaken);
    }

    private static String getYmd(long dateTaken) {
        return getTimeStr(dateTaken, "yyyy-MM-dd");
    }

    private static String getMd(long dateTaken) {
        return getTimeStr(dateTaken, "MM-dd");
    }

    private static String getTimeStr(long time, String format) {
        Date date = new Date();
        date.setTime(time);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static void playVideo(Context context, Media media) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(media.getUri(), "video/*");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, SelectorHelper.textEngine.notFoundVideoPlayer(context), Toast.LENGTH_SHORT).show();
        }
    }
}
