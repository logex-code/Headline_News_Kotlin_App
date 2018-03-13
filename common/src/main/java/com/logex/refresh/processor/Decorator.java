package com.logex.refresh.processor;


import com.logex.refresh.PullRefreshLayout;

/**
 * Created by lcodecore on 2017/3/3.
 *
 */

public abstract class Decorator implements IDecorator {
    protected IDecorator decorator;
    protected PullRefreshLayout.CoContext cp;

    public Decorator(PullRefreshLayout.CoContext processor, IDecorator decorator1) {
        cp = processor;
        decorator = decorator1;
    }
}
