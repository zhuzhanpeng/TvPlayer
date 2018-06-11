package com.onairm.tvbaselibrary.player;

import android.content.Context;

/**
 * 线程不安全的
 */

public class PlayerFactory {
    private final static PlayerFactory instance = new PlayerFactory();

    private PlayerFactory() {

    }

    public static PlayerFactory getInstance() {
        return instance;
    }

    public static AbstractTvPlayerView newInstance(Class<? extends AbstractTvPlayerView> clz, Context ctx) {
        //根据类名获取Class对象
        Object o=null;
        try {
            Class c = Class.forName(clz.getName());
            //参数类型数组
            Class[] parameterTypes = {Context.class};
//根据参数类型获取相应的构造函数
            java.lang.reflect.Constructor constructor = c.getConstructor(parameterTypes);
//参数数组
            Object[] parameters = {ctx};
//根据获取的构造函数和参数，创建实例
            o = constructor.newInstance(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (AbstractTvPlayerView) o;
    }

}
