package com.example.projecttest.scheduler;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.projecttest.R;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

/**
 *
 */
public class GameTipManager extends BaseAnimManager<FrameLayout, GameBean> {

    private AnimatorSet mAnimatorSet;

    private SVGAImageView mAnimView;

    /**
     * 是否接收消息
     */
    private boolean offerMessage = true;

    public GameTipManager(Activity activity, int parentViewResId, String animName, ILiveAnimaListener listener) {
        super(activity, parentViewResId, animName, listener, true);
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
    protected void lazyInitView(ViewStub vs) {
        super.lazyInitView(vs);
        if (vs != null) {
            mFrameLayout = (FrameLayout) vs.inflate();
//            mAnimView = mFrameLayout.findViewById(R.id.svga_test1);

        }
    }

    /**
     * 执行动画
     * @param: parentView 父容器
     * @return:
     */
    @Override
    protected boolean startAniming(@NonNull final Activity activity, @NonNull final GameBean bean, @NonNull final ViewGroup parentView) {
        initView(bean);

        if(mAnimView != null && mActivity != null && mActivity.get() != null) {
            mAnimView.setVisibility(View.VISIBLE);
            SVGAParser.Companion.shareParser().init(mActivity.get()); // 这个在全局初始化了

            SVGAParser svgaParser = SVGAParser.Companion.shareParser();
            svgaParser.setFrameSize(100, 100);
            svgaParser.decodeFromAssets("anim_game_win_screen.svga", new SVGAParser.ParseCompletion() {
                @Override
                public void onComplete(@NonNull SVGAVideoEntity videoItem) {
                    Log.e("zzzz", "onComplete: ");
                    mAnimView.setVideoItem(videoItem);
                    mAnimView.stepToFrame(0, true);
                }

                @Override
                public void onError() {
                    Log.e("zzzz", "onComplete: ");
                }

            }, null);
        }
        return true;
    }

    private void initView( final GameBean bean) {
        Activity activity = getActivity();

        if(mAnimView == null && activity != null) {
            View v = LayoutInflater.from(activity).inflate(R.layout.view_game_tip_item, mFrameLayout, false);
            if(v instanceof SVGAImageView && v != null) {
                mAnimView = (SVGAImageView)v;
                mAnimView.setLoops(1);
                ViewHolder holder = new ViewHolder();
                mAnimView.setVisibility(View.INVISIBLE);
                mAnimView.setTag(holder);
            }

        }
        if (mFrameLayout != null
                && mAnimView != null
                && mAnimView.getParent() == null){
            mAnimView.setCallback(new SVGACallback() {
                @Override
                public void onPause() {
                    System.out.println("========================> onPause");
                }

                @Override
                public void onFinished() {
                    System.out.println("========================> onFinished");
                    notifyTaskEnd(bean);
                }

                @Override
                public void onRepeat() {
                    System.out.println("========================> onRepeat");
                }

                @Override
                public void onStep(int i, double v) {
                    System.out.println("========================> onStep(");
                }
            });
            mFrameLayout.addView(mAnimView);
        }

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
        if(mFrameLayout != null) {
            if(visible) {
                offerMessage = true;
            } else {
                clearCurrentAnim();
                offerMessage = false;
            }
            mFrameLayout.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
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
        if (null != mFrameLayout) {
            mFrameLayout.setOnClickListener(null);
            mFrameLayout = null;
        }
        if (null != mAnimatorSet) {
            mAnimatorSet.removeAllListeners();
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }

}
