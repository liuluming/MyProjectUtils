package com.my51c.see51.yzxvoip;

import com.yzxtcp.tools.CustomLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuqian
 * @Title YZXObservable
 * @Description 数据库改变观察者
 * @Company yunzhixun
 * @date 2015-12-2 下午12:04:59
 */
public abstract class YZXObservable<T> {

    protected final List<T> observers = new ArrayList<T>();

    /**
     * @param observer 要添加的观察者
     * @return void
     * @Description 添加观察者
     * @date 2015-12-2 下午12:15:14
     * @author zhuqian
     */
    public void addObserver(T observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The addObserver is null");
        }
        synchronized (observers) {
            if (observers.contains(observer)) {
                throw new IllegalStateException("The observer : " + observer + " is already added");
            }
            observers.add(observer);
        }
    }

    /**
     * @param observer 要移出的观察者
     * @return void
     * @Description 移出观察者
     * @date 2015-12-2 下午12:16:23
     * @author zhuqian
     */
    public void removeObserver(T observer) {
        if (observer == null) {
            throw new IllegalArgumentException("The addObserver is null");
        }
        synchronized (observers) {
            int i = observers.indexOf(observer);
            if (i == -1) {
                CustomLog.d("YZXObservable removeObserver observer " + observer + " is not existed");
                return;
            }
            observers.remove(i);
        }
    }

    /**
     * @return void
     * @Description 清空观察者
     * @date 2015-12-2 下午12:16:41
     * @author zhuqian
     */
    public void clear() {
        synchronized (observers) {
            observers.clear();
        }
    }
}
