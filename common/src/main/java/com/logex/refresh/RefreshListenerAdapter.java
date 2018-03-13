package com.logex.refresh;

public abstract class RefreshListenerAdapter implements PullListener {
        @Override
        public void onPullingDown(PullRefreshLayout refreshLayout, float fraction) {
        }

        @Override
        public void onPullingUp(PullRefreshLayout refreshLayout, float fraction) {
        }

        @Override
        public void onPullDownReleasing(PullRefreshLayout refreshLayout, float fraction) {
        }

        @Override
        public void onPullUpReleasing(PullRefreshLayout refreshLayout, float fraction) {
        }

        @Override
        public void onRefresh(PullRefreshLayout refreshLayout) {
        }

        @Override
        public void onLoadMore(PullRefreshLayout refreshLayout) {
        }

        @Override
        public void onFinishRefresh() {

        }

        @Override
        public void onFinishLoadMore() {

        }

        @Override
        public void onRefreshCanceled() {

        }

        @Override
        public void onLoadMoreCanceled() {

        }
}