package net.arvin.selector.uis.widgets.editable;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.arvin.selector.R;
import net.arvin.selector.utils.UiUtil;

/**
 * Created by arvinljw on 2020/8/14 16:34
 * Function：
 * Desc：
 */
public class InputTextLayout extends RelativeLayout implements View.OnClickListener, PaintColorBarLayout.OnColorSelectListener {
    private SpecialBgEditText editText;
    private PaintColorBarLayout paintColorBar;
    private ImageView imgTextBg;
    private View layoutToolsBar;

    private InputTextCallback callback;

    public InputTextLayout(Context context) {
        this(context, null);
    }

    public InputTextLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        setBackgroundColor(Color.parseColor("#88000000"));
        inflate(getContext(), R.layout.ps_layout_input_text, this);

        editText = findViewById(R.id.ps_ed_content);
        paintColorBar = findViewById(R.id.ps_paint_color_bar);
        imgTextBg = findViewById(R.id.ps_img_text_bg);
        layoutToolsBar = findViewById(R.id.ps_layout_color_bar);

        paintColorBar.setOnColorSelectListener(this);

        imgTextBg.setOnClickListener(this);
        findViewById(R.id.ps_input_cancel).setOnClickListener(this);
        findViewById(R.id.ps_input_ensure).setOnClickListener(this);


    }

    public void setCallback(InputTextCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ps_input_cancel) {
            hideLayout();
        } else if (id == R.id.ps_input_ensure) {
            if (callback != null) {
                String result = getResult();
                if (!TextUtils.isEmpty(result)) {
                    editText.setText("");
                    int[] colorAndBg = getColorAndBg();
                    callback.inputResult(result, colorAndBg[0], colorAndBg[1], imgTextBg.isSelected());
                }
            }
            hideLayout();
        } else if (v == imgTextBg) {
            changeEditBg();
        }
    }

    private void changeEditBg() {
        imgTextBg.setSelected(!imgTextBg.isSelected());
        if (imgTextBg.isSelected()) {
            imgTextBg.setImageResource(R.drawable.ps_ic_text);
        } else {
            imgTextBg.setImageResource(R.drawable.ps_ic_text_bg);
        }
        resetEditTextColorAndBg();
    }

    private void resetEditTextColorAndBg() {
        int[] colorAndBg = getColorAndBg();
        editText.setTextColor(colorAndBg[0]);
        if (imgTextBg.isSelected()) {
            editText.setBgColor(colorAndBg[1]);
            editText.setShowBg(true);
        } else {
            editText.setShowBg(false);
        }
    }

    private String getResult() {
        return editText.getText().toString();
    }

    public void showLayout() {
        setVisibility(View.VISIBLE);
        UiUtil.showKeyboard(editText);
    }

    public void hideLayout() {
        setVisibility(View.GONE);
        UiUtil.closeKeyboard(editText);
        if (callback != null) {
            callback.hideCallback();
        }
    }

    public boolean isVisible() {
        return getVisibility() == View.VISIBLE;
    }

    public int[] getColorAndBg() {
        int[] colorAndBg = new int[2];
        int[] paintColor = paintColorBar.getPaintColor();
        if (imgTextBg.isSelected()) {
            colorAndBg[1] = paintColor[0];
            if (paintColor[1] == PaintColorBarLayout.OnColorSelectListener.TYPE_COLOR_WITHE) {
                colorAndBg[0] = Color.BLACK;
            } else {
                colorAndBg[0] = Color.WHITE;
            }
        } else {
            colorAndBg[0] = paintColor[0];
        }
        return colorAndBg;
    }

    @Override
    public void onColorSelected(int color, int type) {
        resetEditTextColorAndBg();
    }

    public void softOpen(int height) {
        LayoutParams params = (LayoutParams) layoutToolsBar.getLayoutParams();
        params.bottomMargin = height;
        layoutToolsBar.setLayoutParams(params);
    }

    public void softClose() {
        RelativeLayout.LayoutParams params = (LayoutParams) layoutToolsBar.getLayoutParams();
        params.bottomMargin = 0;
        layoutToolsBar.setLayoutParams(params);
    }

    public interface InputTextCallback {
        void inputResult(String result, int textColor, int bgColor, boolean hasBg);

        void hideCallback();
    }


}
