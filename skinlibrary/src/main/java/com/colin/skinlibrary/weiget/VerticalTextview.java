package com.colin.skinlibrary.weiget;/**
 * Created by xiehehe on 16/7/19.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.colin.skinlibrary.R;
import com.colin.skinlibrary.SkinManager;

import java.util.ArrayList;

import skin.support.widget.SkinCompatSupportable;

/**
 * User: xiehehe
 * Date: 2016-07-19
 * Time: 22:45
 * FIXME
 */
public class VerticalTextview extends TextSwitcher implements ViewSwitcher.ViewFactory, SkinCompatSupportable {

    private static final int FLAG_START_AUTO_SCROLL = 0;
    private static final int FLAG_STOP_AUTO_SCROLL = 1;

    private static final int STATE_PAUSE = 2;
    private static final int STATE_SCROLL = 3;

    private float mTextSize = 16;
    private int mPadding = 5;
    private int textColor = Color.BLACK;
    private int nightTextColor = Color.BLACK;
    public boolean isNeedGray = false;
    private int mScrollState = STATE_PAUSE;

    /**
     * @param textSize  textsize
     * @param padding   padding
     * @param textColor textcolor
     */
    public void setText(float textSize, int padding, int textColor) {
        mTextSize = textSize;
        mPadding = padding;
        this.textColor = textColor;
    }

    private OnItemClickListener itemClickListener;
    private Context mContext;
    private int currentId = -1;
    private ArrayList<String> textList;
    private Handler handler;

    public VerticalTextview(Context context) {
        this(context, null);
        mContext = context;
    }

    public VerticalTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalTestView);
        textColor = typedArray.getColor(R.styleable.VerticalTestView_textColor, 0x50000000);
        nightTextColor = typedArray.getColor(R.styleable.VerticalTestView_nightTextColor, 0x50000000);
        isNeedGray = typedArray.getBoolean(R.styleable.VerticalTestView_isNeedGray, false);

        mTextSize = typedArray.getDimensionPixelSize(R.styleable.VerticalTestView_textSize, 0);
        mContext = context;
        textList = new ArrayList<String>();
    }

    public void setAnimTime(long animDuration) {
        setFactory(this);
        Animation in = new TranslateAnimation(0, 0, animDuration, 0);
        in.setDuration(animDuration);
        in.setInterpolator(new AccelerateInterpolator());
        Animation out = new TranslateAnimation(0, 0, 0, -animDuration);
        out.setDuration(animDuration);
        out.setInterpolator(new AccelerateInterpolator());
        setInAnimation(in);
        setOutAnimation(out);
    }

    /**
     * set time.
     *
     * @param time
     */
    public void setTextStillTime(final long time) {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FLAG_START_AUTO_SCROLL:
                        if (textList.size() > 0) {
                            currentId++;
                            setText(textList.get(currentId % textList.size()));
                        }
                        handler.sendEmptyMessageDelayed(FLAG_START_AUTO_SCROLL, time);
                        break;
                    case FLAG_STOP_AUTO_SCROLL:
                        handler.removeMessages(FLAG_START_AUTO_SCROLL);
                        break;
                }
            }
        };
    }

    /**
     * set Data list.
     *
     * @param titles
     */
    public void setTextList(ArrayList<String> titles) {
        textList.clear();
        textList.addAll(titles);
        currentId = -1;
    }

    /**
     * start auto scroll
     */
    public void startAutoScroll() {
        mScrollState = STATE_SCROLL;
        handler.removeMessages(FLAG_START_AUTO_SCROLL);
        handler.removeMessages(FLAG_STOP_AUTO_SCROLL);
        handler.sendEmptyMessage(FLAG_START_AUTO_SCROLL);
    }

    /**
     * stop auto scroll
     */
    public void stopAutoScroll() {
        mScrollState = STATE_PAUSE;
        handler.removeMessages(FLAG_START_AUTO_SCROLL);
        handler.removeMessages(FLAG_STOP_AUTO_SCROLL);
        handler.sendEmptyMessage(FLAG_STOP_AUTO_SCROLL);
    }

    @Override
    public View makeView() {
        TextView t = new TextView(mContext);
        t.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        t.setMaxLines(1);
        t.setPadding(mPadding, mPadding, mPadding, mPadding);
        t.setTextColor(getTextColor());
        t.setTextSize(mTextSize);

        t.setClickable(true);
        t.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null && textList.size() > 0 && currentId != -1) {
                    itemClickListener.onItemClick(currentId % textList.size());
                }
            }
        });
        return t;
    }

    private int getTextColor(){
        if (isNeedGray&&SkinManager.isGragyMode()){
            return Color.GRAY;
        }else if (SkinManager.checkIsDefaultMode()){
            return textColor;
        }else
            return nightTextColor;
    }

    /**
     * set onclick listener
     *
     * @param itemClickListener listener
     */
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void applySkin() {
    }

    /**
     * item click listener
     */
    public interface OnItemClickListener {
        /**
         * callback
         *
         * @param position position
         */
        void onItemClick(int position);
    }

    public boolean isScroll(){
        return mScrollState ==STATE_SCROLL;
    }

    public boolean isPause(){
        return mScrollState == STATE_PAUSE;
    }

    //memory leancks.
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
    }
}
