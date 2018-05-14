package com.my51c.see51.yzxvoip;


/**
 * @author zhuqian
 * @Title DBObserver
 * @Description 数据库改变观察者
 * @Company yunzhixun
 * @date 2015-12-2 下午12:25:01
 */
public class DBObserver extends YZXObservable<OnDbChangeListener> {

    /**
     * @param notifyId 通知id
     * @return void
     * @Description 通知观察者
     * @date 2015-12-2 下午12:26:51
     * @author zhuqian
     */
    public void notify(String notifyId) {
        synchronized (observers) {
            for (int i = observers.size() - 1; i >= 0; i--) {
                if (observers.get(i) != null) {
                    observers.get(i).onChange(notifyId);
                }
            }
        }
    }
}
