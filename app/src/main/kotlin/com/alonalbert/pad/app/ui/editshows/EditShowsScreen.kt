package com.alonalbert.pad.app.ui.editshows

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.alonalbert.pad.app.R
import com.alonalbert.pad.app.data.Show
import com.alonalbert.pad.app.ui.components.PadScaffold
import timber.log.Timber

@Composable
fun EditShowsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: EditShowsViewModel = hiltViewModel(),
) {
    val allShows by viewModel.showListState.collectAsStateWithLifecycle()
    val userState by viewModel.userState.collectAsStateWithLifecycle()

    // todo: Handle better
    userState?.let { user ->
        val userShows = userState?.shows ?: return
        val itemSelectionStates = allShows.associate { it.id to userShows.contains(it) }.toMutableMap()

        val onSaveClick: () -> Unit = {
            val showMap = allShows.associateBy { it.id }
            val shows = itemSelectionStates.filter { it.value }.mapNotNull { showMap[it.key] }
            viewModel.updateUser(user.copy(shows = shows))
            navController.popBackStack()
        }

        PadScaffold(
            viewModel = viewModel,
            floatingActionButton = {
                FloatingActionButton(onClick = onSaveClick) {
                    Icon(Icons.Filled.Done, stringResource(id = R.string.save))
                }
            },
            modifier = modifier
        ) {
            ShowPickerContent(allShows, itemSelectionStates)
        }
    }
}

@Composable
private fun ShowPickerContent(allShows: List<Show>, itemSelectionStates: MutableMap<Long, Boolean>) {
    LazyColumn {
        items(
            items = allShows,
            key = { it.id }
        ) {
            var isSelected by rememberSaveable { mutableStateOf(itemSelectionStates.getOrDefault(it.id, false)) }
            val onClick = {
                isSelected = !isSelected
                itemSelectionStates[it.id] = isSelected
            }
            ShowCard(it, isSelected, onClick)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowCard(
    show: Show,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val color = when (isSelected) {
        true -> MaterialTheme.colorScheme.primaryContainer
        false -> Color.White
    }
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = color),
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
    ShowPickerContent(allShows = List(10) { Show(name = "Show $it") }, mutableMapOf())
}