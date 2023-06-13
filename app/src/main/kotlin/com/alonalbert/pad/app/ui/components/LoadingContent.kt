package com.alonalbert.pad.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Display an initial empty state or swipe to refresh content.
 *
 * @param isLoading (state) when true, display a loading spinner over [content]
 * @param onRefresh (event) event to request refresh
 * @param modifier the modifier to apply to this layout.
 * @param content (slot) the main content to show
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadingContent(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(isLoading, onRefresh = onRefresh)
    Box(modifier.pullRefresh(pullRefreshState)) {
        content()
        PullRefreshIndicator(isLoading, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}
