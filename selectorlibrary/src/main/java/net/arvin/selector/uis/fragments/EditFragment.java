package net.arvin.selector.uis.fragments;

import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import net.arvin.selector.R;
import net.arvin.selector.SelectorHelper;
import net.arvin.selector.data.Media;
import net.arvin.selector.uis.widgets.editable.CropRotateView;
import net.arvin.selector.uis.widgets.editable.EditableView;
import net.arvin.selector.uis.widgets.editable.InputTextLayout;
import net.arvin.selector.uis.widgets.editable.KeyboardHeightUtil;
import net.arvin.selector.uis.widgets.editable.PaintColorBarLayout;
import net.arvin.selector.uis.widgets.editable.TextHelper;
import net.arvin.selector.utils.UiUtil;

/**
 * Created by arvinljw on 2020/8/13 12:55
 * Function：
 * Desc：
 */
public class EditFragment extends BaseFragment implements View.OnClickListener, PaintColorBarLayout.OnColorSelectListener,
        InputTextLayout.InputTextCallback, TextHelper.TextDraggingCallback, KeyboardHeightUtil.OnResizeListener {
    public static final String KEY_MEDIA = "media";
    private Media media;
    private EditableView editableView;
    private ImageView imgClose;
    private ImageView imgPencil;
    private View layoutPaintColorBar;
    private PaintColorBarLayout paintColorBar;
    private View layoutToolsBar;
    private InputTextLayout layoutInput;

    private View layoutDeleteRect;
    private TextView tvDeleteText;
    private CropRotateView cropRotateView;
    private View layoutResizeBar;

    private KeyboardHeightUtil keyboardHeightUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.ps_fragment_edit;
    }

    @Override
    protected void init() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            media = arguments.getParcelable(KEY_MEDIA);
        }
        initView();
        initListener();
        reset();

        SelectorHelper.imageEngine.loadImage(editableView, media.getUri());
    }

    @SuppressWarnings("ConstantConditions")
    private void reset() {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ps_ic_pencil);
        UiUtil.setTint(imgPencil, drawable, Color.WHITE);
    }

    private void initView() {
        editableView = findViewById(R.id.ps_editable_view);

        cropRotateView = findViewById(R.id.ps_ed_crop);
        layoutResizeBar = findViewById(R.id.ps_layout_resize_bar);

        imgClose = findViewById(R.id.ps_img_close_edit);
        imgPencil = findViewById(R.id.ps_img_pencil);
        layoutPaintColorBar = findViewById(R.id.ps_layout_paint_color_bar);
        paintColorBar = findViewById(R.id.ps_paint_color_bar);

        layoutToolsBar = findViewById(R.id.ps_layout_tools_bar);
        layoutInput = findViewById(R.id.ps_layout_input);

        layoutDeleteRect = findViewById(R.id.ps_layout_delete_rect);
        tvDeleteText = findViewById(R.id.ps_tv_delete_rect);
    }

    private void initListener() {
        imgClose.setOnClickListener(this);
        imgPencil.setOnClickListener(this);
        paintColorBar.setOnColorSelectListener(this);
        layoutInput.setCallback(this);

        findViewById(R.id.ps_img_text).setOnClickListener(this);
        findViewById(R.id.ps_img_resize).setOnClickListener(this);
        findViewById(R.id.ps_img_paint_rollback).setOnClickListener(this);
        editableView.setTextDraggingCallback(this);

        findViewById(R.id.ps_tv_cancel_resize).setOnClickListener(this);
        findViewById(R.id.ps_img_reset).setOnClickListener(this);
        findViewById(R.id.ps_img_rotate).setOnClickListener(this);
        findViewById(R.id.ps_btn_resize_ok).setOnClickListener(this);

        keyboardHeightUtil = new KeyboardHeightUtil(getActivity(), this);
        keyboardHeightUtil.addSoftOpenListener(root);
    }

    @Override
    public void onClick(View v) {
        if (getActivity() == null) {
            return;
        }
        int id = v.getId();
        if (id == R.id.ps_img_close_edit) {
            UiUtil.removeFragment(getActivity(), this);
        } else if (id == R.id.ps_img_pencil) {
            pencilClicked();
        } else if (id == R.id.ps_img_text) {
            openInput();
        } else if (id == R.id.ps_img_resize) {
            resizeImage();
        } else if (id == R.id.ps_img_paint_rollback) {
            editableView.getPaintingHelper().rollback();
        } else if (id == R.id.ps_tv_cancel_resize) {
            cancelResize();
        } else if (id == R.id.ps_img_reset) {
            resetResize();
        } else if (id == R.id.ps_img_rotate) {
            rotateImage();
        } else if (id == R.id.ps_btn_resize_ok) {
            resizeOk();
        }
    }

    private void pencilClicked() {
        if (getActivity() == null) {
            return;
        }
        imgPencil.setSelected(!imgPencil.isSelected());
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.ps_ic_pencil);
        if (imgPencil.isSelected()) {
            UiUtil.setTint(imgPencil, drawable, getResources().getColor(R.color.ps_send_bg_enable));
            editableView.setOpenPainting(true);
            showPaintColorBar();
            setDefaultColor();
        } else {
            UiUtil.setTint(imgPencil, drawable, Color.WHITE);
            editableView.setOpenPainting(false);
            hidePaintColorBar();
        }
    }

    private void hidePaintColorBar() {
        layoutPaintColorBar.setVisibility(View.GONE);
    }

    private void showPaintColorBar() {
        layoutPaintColorBar.setVisibility(View.VISIBLE);
    }

    private void setDefaultColor() {
        int[] paintColor = paintColorBar.getPaintColor();
        onColorSelected(paintColor[0], paintColor[1]);
    }

    private void openInput() {
        layoutInput.showLayout();
        hidePaintColorBar();
        layoutToolsBar.setVisibility(View.GONE);
        imgClose.setVisibility(View.GONE);
    }

    /**
     * 去裁剪预览
     */
    private void resizeImage() {
        editableView.setOpenPainting(false);

        hideFromResize();
        cropRotateView.setVisibility(View.VISIBLE);
        layoutResizeBar.setVisibility(View.VISIBLE);
        RectF rectF = editableView.calculateCropRect(0);
        editableView.setCropBaseRect(rectF);
        editableView.setCropRect(null);
        cropRotateView.setCropRect(rectF);
    }

    private void cancelResize() {
        showFromResize();
        cropRotateView.setVisibility(View.GONE);
        layoutResizeBar.setVisibility(View.GONE);
        editableView.rollback();
        cropRotateView.setCropRect(null);
    }

    private void resetResize() {
        editableView.setCropDegree(0);
        RectF rectF = editableView.calculateCropRect(0);
        editableView.changeCropBaseRect(rectF);
        editableView.setCropRect(null);
        cropRotateView.setCropRect(rectF);
    }

    private void rotateImage() {
        float cropDegree = editableView.getCropDegree();
        float changeDegree = cropDegree - 90;
        editableView.setCropDegree(changeDegree);

        RectF rectF = editableView.calculateCropRect(changeDegree);
        editableView.changeCropBaseRect(rectF);
        editableView.setCropRect(null);
        cropRotateView.setCropRect(rectF);

        editableView.setRotationTo(changeDegree);
    }

    /**
     * 裁剪完成
     */
    private void resizeOk() {
        showFromResize();
        cropRotateView.setVisibility(View.GONE);
        layoutResizeBar.setVisibility(View.GONE);
        RectF rectF = cropRotateView.getCropRect();
        editableView.setCropRect(rectF);
        editableView.setCropBaseRect(rectF, cropRotateView.getScale(), cropRotateView.getTransY());
        cropRotateView.setCropRect(null);
    }

    private void hideFromResize() {
        imgClose.setVisibility(View.GONE);
        layoutToolsBar.setVisibility(View.GONE);
        if (imgPencil.isSelected()) {
            hidePaintColorBar();
        }
    }

    private void showFromResize() {
        imgClose.setVisibility(View.VISIBLE);
        layoutToolsBar.setVisibility(View.VISIBLE);
        if (imgPencil.isSelected()) {
            showPaintColorBar();
            editableView.setOpenPainting(true);
        }
    }

    @Override
    public void onColorSelected(int color, int type) {
        editableView.getPaintingHelper().setColor(color, type);
    }

    @Override
    public void inputResult(String result, int textColor, int bgColor, boolean hasBg) {
        editableView.addText(result, textColor, bgColor, hasBg);
    }

    @Override
    public void hideCallback() {
        layoutToolsBar.setVisibility(View.VISIBLE);
        imgClose.setVisibility(View.VISIBLE);

        if (imgPencil.isSelected()) {
            showPaintColorBar();
            editableView.setOpenPainting(true);
        }
    }

    public boolean hideEditView() {
        if (layoutInput.isVisible()) {
            layoutInput.hideLayout();
            return true;
        }

        return false;
    }

    @Override
    public void onTextStartDragging() {
        imgClose.setVisibility(View.GONE);
        layoutToolsBar.setVisibility(View.GONE);

        if (imgPencil.isSelected()) {
            hidePaintColorBar();
        }

        layoutDeleteRect.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTextDragging(TextHelper.TextItem draggingItem) {
        RectF touchRect = editableView.getRealTouchRect(draggingItem);
        boolean canDelete = canDelete(touchRect);
        layoutDeleteRect.setSelected(canDelete);
        if (canDelete) {
            tvDeleteText.setText(SelectorHelper.textEngine.releaseDeleteTips(getActivity()));
        } else {
            tvDeleteText.setText(SelectorHelper.textEngine.deleteTips(getActivity()));
        }
    }

    private boolean canDelete(RectF touchRect) {
        return touchRect.bottom > (layoutDeleteRect.getHeight() / 2f + layoutDeleteRect.getTop());
    }

    @Override
    public boolean onTextDraggingRelease(TextHelper.TextItem draggingItem) {
        imgClose.setVisibility(View.VISIBLE);
        layoutToolsBar.setVisibility(View.VISIBLE);

        if (imgPencil.isSelected()) {
            showPaintColorBar();
        }

        boolean canDelete = canDelete(editableView.getRealTouchRect(draggingItem));

        layoutDeleteRect.setVisibility(View.GONE);

        return canDelete;
    }

    @Override
    public void OnSoftPop(int height) {
        layoutInput.softOpen(height);
    }

    @Override
    public void OnSoftClose() {
        layoutInput.softClose();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        keyboardHeightUtil.onDestroy();
    }
}
