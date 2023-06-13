package com.alonalbert.pad.app.ui.editshows

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.ui.components.PadScaffold
import timber.log.Timber

@Composable
fun EditShowsScreen(
    modifier: Modifier = Modifier,
    viewModel: EditShowsViewModel = hiltViewModel(),
) {
    val shows by viewModel.showListState.collectAsStateWithLifecycle()

    PadScaffold(
        viewModel = viewModel,
        modifier = modifier
    ) {
        ShowPickerContent(shows)
    }
}

@Composable
private fun ShowPickerContent(shows: List<Show>) {
    LazyColumn {
        shows.forEach {
            item {
                ShowCard(show = it)
            }
        }
    }
}

@Composable
private fun ShowCard(show: Show) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        border = BorderStroke(1.dp, Color.Blue),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Timber.v("Rendering show ${show.name}")
        Text(
            text = show.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview
@Composable
fun ShowPickerContentPreview() {
    ShowPickerContent(shows = List(20) { Show(name = "Show $it") })
}