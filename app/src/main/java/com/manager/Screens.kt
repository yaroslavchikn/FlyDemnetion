package com.manager

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.io.File

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(viewModel: MainViewModel) {
    val ui by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val activity = LocalContext.current as? Activity

    BackHandler {
        if (!viewModel.handleBack()) {
            activity?.finish()
        }
    }

    LaunchedEffect(ui.message) {
        ui.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        bottomBar = {
            CustomBottomNav(ui.selectedTab, viewModel::selectTab)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedContent(
                targetState = ui.selectedTab,
                transitionSpec = {
                    val direction = if (targetState.ordinal > initialState.ordinal) 1 else -1

                    (slideInVertically { height -> height * direction } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height * direction } + fadeOut()
                    )
                },
                label = "tabs"
            ) { tab ->
                when (tab) {
                    BottomTab.PHONE -> FileBrowserScreen(viewModel, ui)
                    BottomTab.FAVORITES -> FavoritesScreen(viewModel, ui)
                    BottomTab.TRASH -> TrashScreen(viewModel, ui)
                }
            }

            AnimatedVisibility(
                visible = ui.selectionMode,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                SelectionActionBar(viewModel, ui)
            }
        }
    }

    ui.viewer?.let { viewer ->
        ViewerHost(viewer = viewer, onClose = viewModel::closeViewer)
    }

    ui.dialog?.let { dialog ->
        DialogHost(dialog = dialog, viewModel = viewModel, ui = ui)
    }
}

@Composable
fun FileBrowserScreen(viewModel: MainViewModel, ui: UiState) {
    LaunchedEffect(ui.searchQuery, ui.searchMode) {
        if (ui.searchMode && ui.searchQuery.isNotBlank()) {
            delay(350)
            viewModel.performSearch()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BrowserTopBar(viewModel, ui)

        if (ui.searchMode) {
            SearchContent(viewModel, ui)
        } else {
            BrowserContent(viewModel, ui)
        }
    }
}

@Composable
fun BrowserTopBar(viewModel: MainViewModel, ui: UiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (ui.selectionMode) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconButton(onClick = viewModel::clearSelection) { color ->
                    FClose(Modifier.size(22.dp), color)
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "${ui.selectedPaths.size} выбрано",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                AppIconButton(onClick = viewModel::selectAll) { color ->
                    FSelectAll(Modifier.size(22.dp), color)
                }
            }
        } else if (ui.searchMode) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconButton(onClick = viewModel::closeSearch) { color ->
                    FBack(Modifier.size(22.dp), color)
                }

                Spacer(Modifier.width(8.dp))

                SearchField(
                    value = ui.searchQuery,
                    onValueChange = viewModel::onSearchQuery,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconButton(
                    onClick = { viewModel.goBack() },
                    enabled = ui.currentPath != viewModel.rootPath
                ) { color ->
                    FBack(Modifier.size(22.dp), color)
                }

                Spacer(Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (ui.currentPath == viewModel.rootPath) {
                            "Мой телефон"
                        } else {
                            File(ui.currentPath).name
                        },
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${ui.items.size} элементов",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AppIconButton(onClick = viewModel::openSearch, size = 38.dp) { color ->
                    FSearch(Modifier.size(21.dp), color)
                }

                AppIconButton(onClick = viewModel::toggleViewMode, size = 38.dp) { color ->
                    if (ui.viewMode == ViewMode.LIST) {
                        FGrid(Modifier.size(21.dp), color)
                    } else {
                        FList(Modifier.size(21.dp), color)
                    }
                }

                AppIconButton(onClick = viewModel::showSortDialog, size = 38.dp) { color ->
                    FSort(Modifier.size(21.dp), color)
                }

                AppIconButton(onClick = viewModel::showCreateFolderDialog, size = 38.dp) { color ->
                    FFolderPlus(Modifier.size(21.dp), color)
                }
            }
        }

        if (!ui.searchMode && !ui.selectionMode) {
            Breadcrumbs(
                currentPath = ui.currentPath,
                rootPath = viewModel.rootPath,
                onOpen = viewModel::openFolder
            )
        }
    }
}

@Composable
fun BrowserContent(viewModel: MainViewModel, ui: UiState) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (ui.currentPath == viewModel.rootPath) {
            StorageCard(
                freeSpace = ui.freeSpace,
                totalSpace = ui.totalSpace,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }

        if (ui.items.isEmpty()) {
            EmptyView("Папка пуста")
        } else {
            AnimatedContent(
                targetState = ui.viewMode,
                modifier = Modifier.weight(1f),
                label = "viewMode"
            ) { mode ->
                if (mode == ViewMode.LIST) {
                    FileList(
                        items = ui.items,
                        selectedPaths = ui.selectedPaths,
                        favorites = ui.favorites,
                        viewModel = viewModel
                    )
                } else {
                    FileGrid(
                        items = ui.items,
                        selectedPaths = ui.selectedPaths,
                        favorites = ui.favorites,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun SearchContent(viewModel: MainViewModel, ui: UiState) {
    if (ui.searchQuery.isBlank()) {
        EmptyView("Введите имя файла или папки")
    } else if (ui.searchResults.isEmpty()) {
        EmptyView("Ничего не найдено")
    } else {
        FileList(
            items = ui.searchResults,
            selectedPaths = ui.selectedPaths,
            favorites = ui.favorites,
            viewModel = viewModel,
            showPath = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileList(
    items: List<FileItem>,
    selectedPaths: Set<String>,
    favorites: Set<String>,
    viewModel: MainViewModel,
    showPath: Boolean = false,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.path }) { item ->
            FileRow(
                item = item,
                selected = selectedPaths.contains(item.path),
                favorite = favorites.contains(item.path),
                showPath = showPath,
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileRow(
    item: FileItem,
    selected: Boolean,
    favorite: Boolean,
    showPath: Boolean,
    viewModel: MainViewModel
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
        else MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
        shadowElevation = if (selected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { viewModel.onClickFile(item) },
                    onLongClick = { viewModel.onLongClickFile(item) }
                )
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                FileTypeIcon(
                    item = item,
                    tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = if (showPath) {
                        File(item.path).parent ?: item.path
                    } else if (item.isDirectory) {
                        item.modified.formatDateTime()
                    } else {
                        "${item.size.formatSize()} · ${item.modified.formatDateTime()}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (favorite) {
                FStar(
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileGrid(
    items: List<FileItem>,
    selectedPaths: Set<String>,
    favorites: Set<String>,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.path }) { item ->
            val selected = selectedPaths.contains(item.path)

            Surface(
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.height(150.dp),
                color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .combinedClickable(
                            onClick = { viewModel.onClickFile(item) },
                            onLongClick = { viewModel.onLongClickFile(item) }
                        )
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        FileTypeIcon(
                            item = item,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = item.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun FileTypeIcon(item: FileItem, tint: Color, modifier: Modifier = Modifier.size(24.dp)) {
    val file = File(item.path)

    when {
        item.isDirectory -> FFolder(modifier, tint)
        file.isImage() -> FImageFile(modifier, tint)
        file.isVideo() -> FVideoFile(modifier, tint)
        file.isAudio() -> FAudioFile(modifier, tint)
        else -> FFile(modifier, tint)
    }
}

@Composable
fun Breadcrumbs(currentPath: String, rootPath: String, onOpen: (String) -> Unit) {
    val crumbs = remember(currentPath) {
        buildList {
            add("Мой телефон" to rootPath)

            if (currentPath != rootPath) {
                val relative = currentPath.removePrefix(rootPath).trim('/')
                val parts = if (relative.isEmpty()) emptyList() else relative.split('/')

                var acc = rootPath
                parts.forEach { part ->
                    acc = if (acc.endsWith("/")) acc + part else "$acc/$part"
                    add(part to acc)
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        crumbs.forEachIndexed { index, crumb ->
            PathChip(
                label = crumb.first,
                selected = index == crumbs.lastIndex,
                onClick = { onOpen(crumb.second) }
            )

            if (index < crumbs.lastIndex) {
                FChevron(
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun FavoritesScreen(viewModel: MainViewModel, ui: UiState) {
    val favoriteItems = remember(ui.favorites) {
        ui.favorites.map { path ->
            val file = File(path)
            if (file.exists()) {
                file.toFileItem()
            } else {
                FileItem(
                    path = path,
                    name = path.substringAfterLast('/'),
                    isDirectory = false,
                    size = 0L,
                    modified = 0L
                )
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenTitle("Избранное")

        if (favoriteItems.isEmpty()) {
            EmptyView("Нет избранных файлов")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteItems, key = { it.path }) { item ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(onClick = { viewModel.openFavorite(item.path) })
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                FileTypeIcon(
                                    item = item,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = item.path,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            AppIconButton(
                                onClick = { viewModel.removeFavorite(item.path) },
                                size = 36.dp,
                                tint = MaterialTheme.colorScheme.primary
                            ) { color ->
                                FStar(Modifier.size(20.dp), color)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrashScreen(viewModel: MainViewModel, ui: UiState) {
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenTitle("Корзина")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppGradientButton(
                text = "Восстановить все",
                onClick = viewModel::restoreAllTrash,
                enabled = ui.trash.isNotEmpty(),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
            )

            AppGradientButton(
                text = "Очистить",
                onClick = viewModel::emptyTrash,
                enabled = ui.trash.isNotEmpty(),
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error),
                contentColor = MaterialTheme.colorScheme.onError,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
            )
        }

        if (ui.trash.isEmpty()) {
            EmptyView("Корзина пуста")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ui.trash, key = { it.trashPath }) { item ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                if (item.isDirectory) {
                                    FFolder(Modifier.size(24.dp), MaterialTheme.colorScheme.primary)
                                } else {
                                    FFile(Modifier.size(24.dp), MaterialTheme.colorScheme.primary)
                                }
                            }

                            Spacer(Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = "Удалено: ${item.deletedAt.formatDateTime()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            AppIconButton(
                                onClick = { viewModel.restoreTrash(item) },
                                size = 36.dp,
                                tint = MaterialTheme.colorScheme.primary
                            ) { color ->
                                FRestore(Modifier.size(20.dp), color)
                            }

                            AppIconButton(
                                onClick = { viewModel.permanentDeleteTrash(item) },
                                size = 36.dp,
                                tint = MaterialTheme.colorScheme.error
                            ) { color ->
                                FDeleteForever(Modifier.size(20.dp), color)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionActionBar(viewModel: MainViewModel, ui: UiState) {
    val single = ui.selectedPaths.size == 1

    Surface(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 10.dp,
        shadowElevation = 10.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            ActionChip(
                label = "Копировать",
                onClick = { viewModel.showMoveCopyDialog(MoveCopyMode.COPY) }
            ) { color -> FCopy(Modifier.size(22.dp), color) }

            ActionChip(
                label = "Переместить",
                onClick = { viewModel.showMoveCopyDialog(MoveCopyMode.MOVE) }
            ) { color -> FMove(Modifier.size(22.dp), color) }

            ActionChip(
                label = "Переименовать",
                enabled = single,
                onClick = viewModel::showRenameDialog
            ) { color -> FEdit(Modifier.size(22.dp), color) }

            ActionChip(
                label = "Поделиться",
                onClick = viewModel::shareSelected
            ) { color -> FShare(Modifier.size(22.dp), color) }

            ActionChip(
                label = "Избранное",
                onClick = viewModel::toggleFavoriteSelected
            ) { color -> FStar(Modifier.size(22.dp), color) }

            ActionChip(
                label = "Свойства",
                enabled = single,
                onClick = { viewModel.showDetails() }
            ) { color -> FInfo(Modifier.size(22.dp), color) }

            ActionChip(
                label = "Удалить",
                onClick = viewModel::showDeleteOptions
            ) { color -> FTrash(Modifier.size(22.dp), color) }
        }
    }
}

@Composable
fun ScreenTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun EmptyView(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FFolder(
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun DialogHost(dialog: DialogState, viewModel: MainViewModel, ui: UiState) {
    when (dialog) {
        DialogState.CreateFolder -> {
            TextDialog(
                title = "Новая папка",
                label = "Имя папки",
                initial = "",
                confirmText = "Создать",
                onConfirm = viewModel::createFolder,
                onDismiss = viewModel::closeDialog
            )
        }

        is DialogState.Rename -> {
            TextDialog(
                title = "Переименовать",
                label = "Новое имя",
                initial = dialog.initialName,
                confirmText = "OK",
                onConfirm = viewModel::rename,
                onDismiss = viewModel::closeDialog
            )
        }

        DialogState.DeleteOptions -> {
            AlertDialog(
                onDismissRequest = viewModel::closeDialog,
                title = { Text("Удаление") },
                text = { Text("Что сделать с выбранными элементами?") },
                confirmButton = {
                    AppGradientButton(
                        text = "В корзину",
                        onClick = { viewModel.deleteSelected(true) },
                        modifier = Modifier.height(40.dp)
                    )
                },
                dismissButton = {
                    AppGradientButton(
                        text = "Навсегда",
                        onClick = { viewModel.deleteSelected(false) },
                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error),
                        contentColor = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.height(40.dp)
                    )
                }
            )
        }

        DialogState.Sort -> {
            SortDialog(viewModel, ui)
        }

        is DialogState.Details -> {
            DetailsDialog(dialog.path, viewModel)
        }

        is DialogState.MoveCopy -> {
            MoveCopyDialog(dialog, viewModel)
        }
    }
}

@Composable
fun TextDialog(
    title: String,
    label: String,
    initial: String,
    confirmText: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            AppTextField(
                value = text,
                onValueChange = { text = it },
                hint = label
            )
        },
        confirmButton = {
            AppGradientButton(
                text = confirmText,
                onClick = { onConfirm(text.trim()) },
                enabled = text.isNotBlank(),
                modifier = Modifier.height(40.dp)
            )
        },
        dismissButton = {
            DialogTextButton("Отмена", onDismiss)
        }
    )
}

@Composable
fun SortDialog(viewModel: MainViewModel, ui: UiState) {
    var mode by remember { mutableStateOf(ui.sortMode) }
    var ascending by remember { mutableStateOf(ui.sortAscending) }

    AlertDialog(
        onDismissRequest = viewModel::closeDialog,
        title = { Text("Сортировка") },
        text = {
            Column {
                SortMode.values().forEach { item ->
                    OptionRow(
                        label = item.label,
                        selected = mode == item,
                        onClick = { mode = item }
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DirectionChip(
                        label = "По возрастанию",
                        selected = ascending,
                        onClick = { ascending = true }
                    )

                    DirectionChip(
                        label = "По убыванию",
                        selected = !ascending,
                        onClick = { ascending = false }
                    )
                }
            }
        },
        confirmButton = {
            AppGradientButton(
                text = "Применить",
                onClick = { viewModel.applySort(mode, ascending) },
                modifier = Modifier.height(40.dp)
            )
        },
        dismissButton = {
            DialogTextButton("Отмена", viewModel::closeDialog)
        }
    )
}

@Composable
fun OptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selected) {
            FCheck(Modifier.size(20.dp), MaterialTheme.colorScheme.primary)
        } else {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
        }

        Spacer(Modifier.width(10.dp))

        Text(label)
    }
}

@Composable
fun DirectionChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DetailsDialog(path: String, viewModel: MainViewModel) {
    val file = File(path)

    val size by produceState(-1L, path) {
        value = viewModel.calculateSize(path)
    }

    AlertDialog(
        onDismissRequest = viewModel::closeDialog,
        title = { Text("Свойства") },
        text = {
            Column {
                DetailRow("Имя", file.name)
                DetailRow("Путь", file.absolutePath)
                DetailRow("Изменён", file.lastModified().formatDateTime())
                DetailRow("Размер", if (size < 0L) "Вычисление..." else size.formatSize())
                DetailRow(
                    "Тип",
                    if (file.isDirectory) "Папка"
                    else file.extension.uppercase().ifBlank { "Файл" }
                )
            }
        },
        confirmButton = {
            AppGradientButton(
                text = "Открыть",
                onClick = { viewModel.openPathFromDetails(path) },
                modifier = Modifier.height(40.dp)
            )
        },
        dismissButton = {
            if (file.isFile) {
                AppGradientButton(
                    text = "Поделиться",
                    onClick = {
                        viewModel.shareFile(path)
                        viewModel.closeDialog()
                    },
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.secondary),
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.height(40.dp)
                )
            } else {
                DialogTextButton("Закрыть", viewModel::closeDialog)
            }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun MoveCopyDialog(dialog: DialogState.MoveCopy, viewModel: MainViewModel) {
    AlertDialog(
        onDismissRequest = viewModel::closeDialog,
        title = { Text(dialog.mode.title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp)
            ) {
                Text(
                    text = "Текущая папка: ${File(dialog.currentPath).name}",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(8.dp))

                if (dialog.currentPath != viewModel.rootPath) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = viewModel::moveCopyUp)
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FUp(Modifier.size(18.dp), MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Вверх")
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (dialog.folders.isEmpty()) {
                    Text("Нет папок")
                } else {
                    LazyColumn {
                        items(dialog.folders, key = { it.path }) { folder ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .combinedClickable(onClick = {
                                        viewModel.moveCopyNavigate(folder.path)
                                    })
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FFolder(Modifier.size(20.dp), MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = folder.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                FChevron(Modifier.size(16.dp), MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            AppGradientButton(
                text = "Выбрать текущую папку",
                onClick = viewModel::confirmMoveCopy,
                modifier = Modifier.height(40.dp)
            )
        },
        dismissButton = {
            DialogTextButton("Отмена", viewModel::closeDialog)
        }
    )
}
