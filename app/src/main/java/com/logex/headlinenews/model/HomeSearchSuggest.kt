package com.logex.headlinenews.model

/**
 * 创建人: liguangxi
 * 日期: 2018/2/23
 * 邮箱: 956328710@qq.com
 * 版本: 1.0
 */
data class HomeSearchSuggest(
        val call_per_refresh: Int,
        val homepage_search_suggest: String,
        val suggest_words: List<SuggestWord>
) {

    data class SuggestWord(
            val id: Int,
            val word: String
    )
}