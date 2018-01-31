package net.arvin.selector.listeners;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by arvinljw on 17/12/25 09:49
 * Function：数据回调以及界面切换
 * Desc：Fragment回传数据到Activity的回调
 */
public interface TransactionListener {
    void exchangeData(Intent data);

    /**
     * @param fragment 可选值为：
     *                 {@link VALUE_CHANGE_FRAGMENT_SELECTOR}、
     *                 {@link VALUE_CHANGE_FRAGMENT_REVIEW}、
     *                 {@link VALUE_CHANGE_FRAGMENT_CROP}
     *                 {@link VALUE_CHANGE_FRAGMENT_TAKE_PHOTO}
     */
    @SuppressWarnings("JavadocReference")
    void switchFragment(int fragmentPos, Bundle bundle);

    void showFragment(int fragmentPos, Bundle bundle);

    void hideFragment(int hidePos, int currPos);
}
