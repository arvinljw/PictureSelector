package net.arvin.selector.uis.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.arvin.selector.R;
import net.arvin.selector.data.ConstantData;
import net.arvin.selector.entities.FileEntity;
import net.arvin.selector.uis.views.ColorBarLayout;
import net.arvin.selector.uis.views.FlexibleTextView;
import net.arvin.selector.uis.views.GraffitiView;
import net.arvin.selector.uis.views.LayoutChangeLinearLayout;
import net.arvin.selector.uis.views.photoview.PhotoView;
import net.arvin.selector.utils.PSGlideUtil;
import net.arvin.selector.utils.PSScreenUtil;

import java.util.ArrayList;

/**
 * Created by arvinljw on 18/1/5 15:04
 * Function：
 * Desc：
 */
public class EditFragment extends BaseFragment implements View.OnClickListener, ColorBarLayout.OnColorSelectedListener {
    private PhotoView mImgEdit;
    private GraffitiView mGraffiti;
    private FlexibleTextView mTvFlexible;
    private ImageView mImgPencil;
    private ImageView mImgText;
    private ImageView mImgResize;
    private ColorBarLayout mColorBar;

    private LayoutChangeLinearLayout mLayoutEdit;
    private ColorBarLayout mEdColorBar;
    private EditText mEdText;

    private FileEntity mItem;

    @Override
    protected int getLayout() {
        return R.layout.ps_fragment_edit;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();

        update(getArguments());
    }

    private void initView() {
        mImgEdit = mRoot.findViewById(R.id.ps_img_edit);
        mGraffiti = mRoot.findViewById(R.id.ps_graffiti);
        mTvFlexible = mRoot.findViewById(R.id.ps_tv_flexible);
        mImgPencil = mRoot.findViewById(R.id.ps_img_pencil);
        mImgText = mRoot.findViewById(R.id.ps_img_text);
        mImgResize = mRoot.findViewById(R.id.ps_img_resize);
        mColorBar = mRoot.findViewById(R.id.ps_layout_color_bar);

        mLayoutEdit = mRoot.findViewById(R.id.ps_layout_edit);
        mEdText = mRoot.findViewById(R.id.ps_ed_text);
        mEdColorBar = mRoot.findViewById(R.id.ps_edit_color_bar);

        mImgPencil.setOnClickListener(this);
        mImgText.setOnClickListener(this);
        mImgResize.setOnClickListener(this);

        mRoot.findViewById(R.id.ps_tv_cancel).setOnClickListener(this);
        mRoot.findViewById(R.id.ps_tv_ensure).setOnClickListener(this);
        mRoot.findViewById(R.id.ps_tv_cancel_edit).setOnClickListener(this);
        mRoot.findViewById(R.id.ps_tv_ensure_edit).setOnClickListener(this);
        mRoot.findViewById(R.id.ps_img_undo).setOnClickListener(this);
        mTvFlexible.setOnClickListener(this);

        mColorBar.setOnColorSelectedListener(this);
        mEdColorBar.setOnColorSelectedListener(this);
        mImgEdit.setOnMatrixChangeListener(mTvFlexible);
        mImgEdit.setOnMatrixChangeListener(mGraffiti);

        mLayoutEdit.setLayoutCallback(new LayoutChangeLinearLayout.OnLayoutCallback() {
            @Override
            public void layoutCallback() {
                showHideColorBar();
            }
        });
    }

    private void showHideColorBar() {
        int screenHeight = PSScreenUtil.getScreenHeight();
        int threshold = screenHeight / 3;
        int rootViewBottom = mLayoutEdit.getBottom();
        Rect rect = new Rect();
        mLayoutEdit.getWindowVisibleDisplayFrame(rect);
        int visibleBottom = rect.bottom - PSScreenUtil.dp2px(24);
        int heightDiff = rootViewBottom - visibleBottom;
        boolean isShowSoftKeyboard = heightDiff > threshold;

        if (isShowSoftKeyboard) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mEdColorBar.getLayoutParams();
            params.bottomMargin = heightDiff;
            mEdColorBar.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mEdColorBar.getLayoutParams();
            params.bottomMargin = 0;
            mEdColorBar.setLayoutParams(params);
        }
    }

    @Override
    public void update(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        initBaseInfo(bundle);

        mItem = bundle.getParcelable(ConstantData.KEY_CURR_ITEM);

        mGraffiti.clear();
        mTvFlexible.setText("");
        PSGlideUtil.loadImage(getActivity(), mItem.getPath(), mImgEdit);
    }

    @Override
    protected ArrayList<String> getSelectedPictures() {
        ArrayList<String> selectedPics = new ArrayList<>();
        selectedPics.add(mItem.getPath());
        return selectedPics;
    }

    @Override
    protected ArrayList<String> getSelectedVideos() {
        return null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ps_tv_cancel) {
            onBackClicked();
        } else if (id == R.id.ps_tv_ensure) {
            onEnsureClicked();
        } else if (id == R.id.ps_tv_cancel_edit) {
            mLayoutEdit.setVisibility(View.GONE);
            mRoot.findViewById(R.id.ps_layout_operate_bar).setVisibility(View.VISIBLE);
        } else if (id == R.id.ps_tv_ensure_edit) {
            mLayoutEdit.setVisibility(View.GONE);
            mRoot.findViewById(R.id.ps_layout_operate_bar).setVisibility(View.VISIBLE);
            mTvFlexible.setText(mEdText.getText().toString().trim());
            mTvFlexible.setColor(mEdColorBar.getColor());
        } else if (id == R.id.ps_img_pencil) {
            onPencilChoose();
        } else if (id == R.id.ps_img_text) {
            onTextChoose();
        } else if (id == R.id.ps_img_resize) {
            onResizeChoose();
        } else if (id == R.id.ps_img_undo) {
            mGraffiti.undo();
        } else if (id == R.id.ps_tv_flexible) {
            showEditView(mTvFlexible.getText());
        }
    }

    private void onPencilChoose() {
        mImgText.setSelected(false);
        mImgResize.setSelected(false);
        mImgPencil.setSelected(!mImgPencil.isSelected());
        mTvFlexible.setCanTouch(false);

        if (mImgPencil.isSelected()) {
            mRoot.findViewById(R.id.ps_layout_pencil_tool).setVisibility(View.VISIBLE);
            mGraffiti.setColor(mColorBar.getColor());
            mGraffiti.setCanDraw(true);
        } else {
            mRoot.findViewById(R.id.ps_layout_pencil_tool).setVisibility(View.GONE);
            mGraffiti.setCanDraw(false);
        }
    }

    private void onTextChoose() {
        mImgPencil.setSelected(false);
        mImgResize.setSelected(false);
        mImgText.setSelected(!mImgText.isSelected());
        mRoot.findViewById(R.id.ps_layout_pencil_tool).setVisibility(View.GONE);
        mGraffiti.setCanDraw(false);

        if (mImgText.isSelected()) {
            String text = mTvFlexible.getText();
            if (TextUtils.isEmpty(text)) {
                showEditView(text);
            }
            mTvFlexible.setCanTouch(true);
        } else {
            mTvFlexible.setCanTouch(false);
        }
    }

    private void onResizeChoose() {
        mImgPencil.setSelected(false);
        mImgText.setSelected(false);
        mImgResize.setSelected(!mImgResize.isSelected());
        mRoot.findViewById(R.id.ps_layout_pencil_tool).setVisibility(View.GONE);
        mGraffiti.setCanDraw(false);
        mTvFlexible.setCanTouch(false);
    }

    private void showEditView(String text) {
        mEdText.setText(text);
        mEdText.setSelection(text.length());
        mLayoutEdit.setVisibility(View.VISIBLE);
        mRoot.findViewById(R.id.ps_layout_operate_bar).setVisibility(View.GONE);
    }

    @Override
    public void onColorSelected(View v, int color) {
        if (v.getId() == R.id.ps_layout_color_bar) {
            mGraffiti.setColor(color);
        } else {
            mEdText.setTextColor(color);
        }
    }

    @Override
    protected void onBackClicked() {
        if (mTransactionListener != null) {
            mTransactionListener.hideFragment(ConstantData.VALUE_CHANGE_FRAGMENT_EDIT, ConstantData.VALUE_CHANGE_FRAGMENT_REVIEW);
        }
    }

    @Override
    protected void onEnsureClicked() {
        super.onEnsureClicked();
    }

    @Override
    public void onDestroyView() {
        mImgEdit.getAttacher().removeMatrixChangeListener(mGraffiti);
        mImgEdit.getAttacher().removeMatrixChangeListener(mTvFlexible);
        super.onDestroyView();
    }
}
