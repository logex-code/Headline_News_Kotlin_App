package com.logex.fragmentation.debug;

import java.util.List;

/**
 * 为了调试时 查看栈视图
 * Created by liguangxi on 16-12-18.
 */
public class DebugFragmentRecord {
    public String fragmentName;
    public List<DebugFragmentRecord> childFragmentRecord;

    public DebugFragmentRecord(String fragmentName, List<DebugFragmentRecord> childFragmentRecord) {
        this.fragmentName = fragmentName;
        this.childFragmentRecord = childFragmentRecord;
    }
}
