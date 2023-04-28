package com.example.projecttest.scheduler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.projecttest.R;

/**
 *
 */
public class GameTipManager extends BaseAnimManager<GameBean> {

    private AnimatorSet mAnimatorSet;

    /**
     * 延迟设置隐藏动画的结束时间
     */
    private ObjectAnimator animatorHide1;

    /**
     * 滚动速率，px/ms
     */
    private float scrollSpeed = 0.10f;

    private View slideItemView;

    /**
     * 是否接收消息
     */
    private boolean offerMessage = true;

    public GameTipManager(Activity activity, int parentViewResId, String animName, ILiveAnimaListener listener) {
        super(activity, parentViewResId, animName, listener);
        initLocation();
    }

    /**
     * 初始化升级提示框的位置
     * @param
     * @return
     */
    private void initLocation() {

    }

    /**
     * 添加动画到队列
     * @param:
     * @return: void
     */
    @Override
    public void addAnim(GameBean broatcastBean) {
        if (broatcastBean != null && offerMessage) {
            queue(broatcastBean);
        }
    }

    @Override
    protected void lazyInitView() {
        super.lazyInitView();

    }

    /**
     * 执行动画
     * @param: parentView 父容器
     * @return:
     */
    @Override
    protected boolean startAniming(@NonNull final Activity activity, @NonNull final GameBean bean, @NonNull final ViewGroup parentView) {

        final View v = getOrCreateView();
        final ViewHolder holder = v != null ? (ViewHolder)v.getTag() : null;

        if (v == null || holder == null)
            return false;

        v.post(new Runnable() {
            @Override
            public void run() {
                getAnimator2(parentView, v, bean, new CallBack<AnimatorSet>(){
                    @Override
                    public void onSuccess(AnimatorSet obj) {
                        super.onSuccess(obj);
                        // 4.启动
                        if(obj != null) {
                            obj.start();
                        } else {
                            notifyTaskEnd(bean);
                        }
                    }
                }, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        holder.flytParent.setVisibility(View.INVISIBLE);
                        notifyTaskEnd(bean);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (holder.flytParent.getVisibility() != View.VISIBLE)
                            holder.flytParent.setVisibility(View.VISIBLE);

                    }
                });

            }
        });

        return true;
    }

    @Nullable
    private View getOrCreateView() {
        Activity a = getActivity();
        if (null == slideItemView && a != null) {
            slideItemView = LayoutInflater.from(a).inflate(R.layout.view_live_chat_slide_item, mAnimParentView, false);
            TextView tv = slideItemView.findViewById(R.id.tv_scroll);
            FrameLayout hsv = slideItemView.findViewById(R.id.flyt_slide_parent);
            ViewHolder holder = new ViewHolder();
            holder.flytParent = hsv;
            holder.tvContent = tv;
            slideItemView.setVisibility(View.INVISIBLE);
            slideItemView.setTag(holder);
        }
        if (mAnimParentView != null
                && slideItemView != null
                && slideItemView.getParent() == null){
            mAnimParentView.addView(slideItemView);
        }

        return slideItemView;
    }

    /**
     * 获得动画
     * @param
     * @return 动画
     */
    private void getAnimator2(final View parent, final View view, @NonNull final GameBean data, final ICallBack<AnimatorSet> callBack, final AnimatorListenerAdapter animAdapter) {
        final ViewHolder holder = (ViewHolder)view.getTag();

        getContent(holder, data, new CallBack<SpannableStringBuilder>(){
            @Override
            public void onSuccess(SpannableStringBuilder obj) {
                super.onSuccess(obj);

                holder.tvContent.setText(obj);
                int textViewWidth = getTextViewWidth(holder.tvContent); // 文本textview的总长度
                int showWidth = holder.flytParent.getWidth() - holder.flytParent.getPaddingLeft() - holder.flytParent.getPaddingRight(); // 显示区域的长度，需要考虑padding值
                int scrollWidth = textViewWidth - showWidth; // 滚动距离，如果为0或负数不需要滚动
                if (getActivity() != null) {
                    if(holder.tvContent.getTranslationX() != 0) { // 恢复文本初始位置
                        holder.tvContent.setTranslationX(0);
                    }

                    ObjectAnimator animatorShow1 = ObjectAnimator.ofFloat(holder.flytParent, "translationX", parent.getWidth(), -ScreenUtils.dip2px(10)).setDuration(200);
                    ObjectAnimator animatorShow2 = ObjectAnimator.ofFloat(holder.flytParent, "translationX", -ScreenUtils.dip2px(10), 0).setDuration(180);

                    animatorHide1 = ObjectAnimator.ofFloat(holder.flytParent, "translationX", 0, ScreenUtils.dip2px(10)).setDuration(180);
                    animatorHide1.setStartDelay(3000);

                    ObjectAnimator animatorHide2 = ObjectAnimator.ofFloat(holder.flytParent, "translationX", ScreenUtils.dip2px(10), -parent.getWidth()).setDuration(200);

                    ObjectAnimator tvAnim = null;
                    if(scrollWidth > 0) { // 移动距离大于0时才开始移动
                        tvAnim = ObjectAnimator.ofFloat(holder.tvContent, "translationX", 0, -scrollWidth).setDuration((long)(scrollWidth / scrollSpeed)); // 滚动时间
                        tvAnim.setStartDelay(1000); // 延时1000ms开始移动
                    }

                    if(mAnimatorSet != null) {
                        mAnimatorSet.cancel();
                        mAnimatorSet.removeAllListeners();
                    }

                    mAnimatorSet = new AnimatorSet();

                    if(tvAnim == null) {
                        mAnimatorSet.play(animatorShow1);
                        mAnimatorSet.play(animatorShow2).after(animatorShow1);
                        mAnimatorSet.play(animatorHide1).after(animatorShow2);
                        mAnimatorSet.play(animatorHide2).after(animatorHide1);
                    } else {
                        mAnimatorSet.play(animatorShow1);
                        mAnimatorSet.play(animatorShow2).after(animatorShow1);
                        mAnimatorSet.play(tvAnim).after(animatorShow2);
                        mAnimatorSet.play(animatorHide1).after(tvAnim);
                        mAnimatorSet.play(animatorHide2).after(animatorHide1);
                    }

                    mAnimatorSet.addListener(animAdapter);
                }

                callBack.onSuccess(mAnimatorSet);
            }
        });

    }

    /**
     * 获取文本内容
     * @param data 广播实体
     * @return 显示内容
     */
    private void getContent(final ViewHolder holder, @NonNull final GameBean data, final ICallBack<SpannableStringBuilder> callBack) {

        int type = data.getBroatcastType();

//        if ((type == 2 && data.getReciever() == null)
//                || (type == 10 && data.getSender() == null)){
//            callBack.onSuccess(new SpannableStringBuilder(parseContent(data)));
//            return;
//        }

        int level = -1;
        if (type == 2 && data.getReciever() != null){
            level = data.getReciever().getStarLev();
        } else if (type == 10 && data.getSender() != null){
            level = data.getSender().getWealthLev();
        }

        SpannableStringBuilder showString = new SpannableStringBuilder(parseContent(data));
        Activity a = getActivity();
        if (level > -1 && a != null) {
            showString.append(" ");
            showString.append(LevelResManager.with(a).level(level).isStar(type == 2).request().string());
        }

        callBack.onSuccess(showString);

    }

    /**
     * 获取文本宽度
     * @param tvScroll 获取对象
     * @return 文本宽度
     */
    private int getTextViewWidth(TextView tvScroll) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        tvScroll.measure(spec,spec);
        tvScroll.layout(0, 0, tvScroll.getMeasuredWidth(), tvScroll.getMeasuredHeight());
        return tvScroll.getMeasuredWidth();
    }


    /**
     * viewholder
     */
    private static class ViewHolder{

        public TextView tvContent = null;
        public FrameLayout flytParent = null;

        public ViewHolder() {

        }
    }

    public void setAnimationVisible (boolean visible) {
        if(mAnimParentView != null) {
            if(visible) {
                offerMessage = true;
            } else {
                clearCurrentAnim();
                offerMessage = false;
            }
            mAnimParentView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * 清除当前正在播放的动画，请清空队列
     */
    @Override
    public void clearCurrentAnim() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet.removeAllListeners();
        }
        super.clearCurrentAnim();
    }

    @Override
    public void clearAnim() {
        super.clearAnim();
        if (null != mAnimParentView) {
            mAnimParentView.setOnClickListener(null);
            mAnimParentView = null;
        }
        if (null != mAnimatorSet) {
            mAnimatorSet.removeAllListeners();
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }

}
