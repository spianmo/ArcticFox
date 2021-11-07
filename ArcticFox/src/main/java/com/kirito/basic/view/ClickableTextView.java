package com.kirito.basic.view;

import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * TextView中:(自定义区间段内)可点击 url链接/删除线/下划线/字体颜色/背景色/字体大小
 * <p>
 * Created by GUOQI on 16/6/23.
 */
public class ClickableTextView extends androidx.appcompat.widget.AppCompatTextView {

    public ClickableTextView(Context context) {
        super(context);
        init();

    }

    public ClickableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClickableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMovementMethod(LinkMovementMethod.getInstance());//设置点击事件
    }

    /**
     * 设置可点击文字
     */
    public void setClickText(String s, int start, int end, final OnClickListener listener) {
        SpannableString spannableString = new SpannableString(s);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                listener.onClick(widget);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(ds.linkColor);
                ds.setUnderlineText(false);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(spannableString);
    }


    /**
     * 设置背景色
     */
    public void setBackColorSpan(String s, int start, int end, int resId) {
        SpannableString spanString = new SpannableString(s);
        BackgroundColorSpan span = new BackgroundColorSpan(resId);
        spanString.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(spanString);
    }

    /**
     * 设置超链接
     */
    public void setHtmlLink(String s, ClickHtmlListener clickHtmlListener) {
        setAutoLinkMask(Linkify.WEB_URLS);
        setText(getClickableHtml(s, clickHtmlListener));
    }

    /**
     * 设置字体颜色
     */
    public void setForeColorSpan(String s, int start, int end, int resId) {
        SpannableString spanString = new SpannableString(s);
        ForegroundColorSpan span = new ForegroundColorSpan(resId);
        spanString.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(spanString);
    }

    /**
     * 设置字体
     */
    public void setFontSizeSpan(String s, int start, int end, int fontSize) {
        SpannableString spanString = new SpannableString(s);
        AbsoluteSizeSpan span = new AbsoluteSizeSpan(fontSize);
        spanString.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(spanString);
    }


    /**
     * 删除线
     */
    public void setStrikeSpan(String s, int start, int end) {
        SpannableString spanString = new SpannableString(s);
        StrikethroughSpan span = new StrikethroughSpan();
        spanString.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(spanString);
    }

    /**
     * 下划线
     */
    public void setUnderLineSpan(String s, int start, int end) {
        SpannableString spanString = new SpannableString(s);
        UnderlineSpan span = new UnderlineSpan();
        spanString.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(spanString);
    }

    private void setLinkClickable(final SpannableStringBuilder clickableHtmlBuilder,
                                  final URLSpan urlSpan, final ClickHtmlListener clickHtml) {
        int start = clickableHtmlBuilder.getSpanStart(urlSpan);
        int end = clickableHtmlBuilder.getSpanEnd(urlSpan);
        int flags = clickableHtmlBuilder.getSpanFlags(urlSpan);
        ClickableSpan clickableSpan = new ClickableSpan() {
            public void onClick(View view) {
                clickHtml.onClick(view, urlSpan.getURL());
            }
        };
        clickableHtmlBuilder.setSpan(clickableSpan, start, end, flags);
    }

    private CharSequence getClickableHtml(String html, ClickHtmlListener clickHtmlListener) {
        Spanned spannedHtml = Html.fromHtml(html);
        SpannableStringBuilder clickableHtmlBuilder = new SpannableStringBuilder(spannedHtml);
        URLSpan[] urls = clickableHtmlBuilder.getSpans(0, spannedHtml.length(), URLSpan.class);
        for (final URLSpan span : urls) {
            setLinkClickable(clickableHtmlBuilder, span, clickHtmlListener);
        }
        return clickableHtmlBuilder;
    }

    public interface ClickHtmlListener {
        void onClick(View v, String url);
    }
}
