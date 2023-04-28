package com.example.projecttest.scheduler;

/**
 * 该接口用于监听直播间动画，按照监听结果出来清除动画按钮的显示或隐藏
 * @author Administrator
 *
 */
public interface ILiveAnimaListener {
	void onAddAnima(BaseAnimManager<?> manager, String name, int size);
	void onAnimaFinish(BaseAnimManager<?> manager, String name);
}
