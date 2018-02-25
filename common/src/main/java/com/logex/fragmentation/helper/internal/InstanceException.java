package com.logex.fragmentation.helper.internal;

/**
 * An {@link RuntimeException} thrown in cases something went wrong inside Tower.
 * <p/>
 * Created by YoKey on 17/2/5.
 */
public class InstanceException extends RuntimeException {
    public InstanceException(String detailMessage) {
        super(detailMessage);
    }
}
