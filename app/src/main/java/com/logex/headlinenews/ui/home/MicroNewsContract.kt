package com.logex.headlinenews.ui.home

import com.logex.headlinenews.base.BaseView
import com.logex.headlinenews.model.DynamicEntity

/**
 * 创建人: liguangxi
 * 日期: 2018/8/2
 * 邮箱 956328710@qq.com
 * 版本 1.0
 */
interface MicroNewsContract {

    interface MicroNewsView : BaseView {

        fun getDynamicListSuccess(data: DynamicEntity?)

        fun getDynamicListFailure(errInfo: String?)
    }

    interface MicroNewsPresenter {

        fun getDynamicList(userId: String?, count: Int)
    }
}