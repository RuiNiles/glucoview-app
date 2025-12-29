package com.ruiniles.glucoview.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize.Min
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily.Companion.Monospace
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ruiniles.glucoview.common.domain.Trend
import com.ruiniles.glucoview.common.domain.Trend.UNKNOWN
import com.ruiniles.glucoview.common.util.ContentDescription.MORE_BUTTON_ICON
import com.ruiniles.glucoview.core.service.LogCollector
import com.ruiniles.glucoview.core.service.LogCollector.clear
import com.ruiniles.glucoview.core.service.dashboard.DashboardService
import com.ruiniles.glucoview.core.service.dashboard.PreviewDashboardService
import com.ruiniles.glucoview.core.service.dashboard.PreviewEmptyDashboardService
import com.ruiniles.glucoview.presentation.util.TestTag.CLEAR_BUTTON
import com.ruiniles.glucoview.presentation.util.TestTag.CONSOLE_LOG
import com.ruiniles.glucoview.presentation.util.TestTag.GLUCOSE_READING_TIMESTAMP_TEXT
import com.ruiniles.glucoview.presentation.util.TestTag.GLUCOSE_READING_TREND_ARROW
import com.ruiniles.glucoview.presentation.util.TestTag.GLUCOSE_READING_UNIT_TEXT
import com.ruiniles.glucoview.presentation.util.TestTag.GLUCOSE_READING_VALUE_TEXT
import com.ruiniles.glucoview.presentation.util.TestTag.MORE_BUTTON
import com.ruiniles.glucoview.presentation.util.TestTag.RESET_BUTTON
import com.ruiniles.glucoview.ui.theme.GlucoViewTheme


@Composable
fun DashboardScreen(
    navController: NavController,
    dashboardService: DashboardService,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val logLines = LogCollector.lines.collectAsState()
    val listState = rememberLazyListState()
    val lines = logLines.value

    LaunchedEffect(lines.size) {
        if (lines.isNotEmpty()) {
            listState.animateScrollToItem(lines.lastIndex)
        }
    }

    val lastReading by dashboardService.remoteValue.collectAsStateWithLifecycle(null)

    Column {
        Card(
            shape = RoundedCornerShape(
                topStart = 0.0.dp,
                topEnd = 0.0.dp,
                bottomEnd = 28.0.dp,
                bottomStart = 28.0.dp,
            )
        ) {
            Surface(
                color = colorScheme.onSecondary,
                modifier = modifier
                    .fillMaxWidth()
                    .height(Min)
            ) {
                Column(
                    modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    Row(modifier = modifier.align(End)) {
                        IconButton(
                            modifier = modifier.testTag(MORE_BUTTON),
                            onClick = { expanded = !expanded }
                        ) {
                            Icon(
                                imageVector = Default.MoreVert,
                                contentDescription = MORE_BUTTON_ICON
                            )
                        }
                        MaterialTheme(
                            shapes = MaterialTheme.shapes.copy(
                                extraSmall = RoundedCornerShape(16.dp)
                            )
                        ) {
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                offset = DpOffset((-15).dp, 0.dp)
                            ) {
                                DropdownMenuItem(
                                    modifier = modifier.testTag(RESET_BUTTON),
                                    text = {
                                        Text(
                                            text = "Reset",
                                            fontSize = typography.labelLarge.fontSize,
                                            textAlign = Center
                                        )
                                    },
                                    onClick = { dashboardService.reset(navController) }
                                )
                            }
                        }
                    }
                    Column(
                        modifier = modifier
                            .align(CenterHorizontally)
                            .padding(40.dp)
                    ) {
                        Text(
                            modifier = modifier
                                .requiredHeight(80.dp)
                                .wrapContentHeight(
                                    align = CenterVertically,
                                    unbounded = true
                                )
                                .testTag(GLUCOSE_READING_VALUE_TEXT),
                            text = lastReading?.value?.mmolPerL?.toString() ?: "---",
                            fontSize = 100.sp,
                            textAlign = Center
                        )
                        Row(modifier = modifier.align(CenterHorizontally)) {
                            Text(
                                modifier = modifier.testTag(GLUCOSE_READING_UNIT_TEXT),
                                text = "mmol/L",
                                textAlign = Center,
                                fontSize = typography.titleSmall.fontSize
                            )

                            (lastReading
                                ?.let { Trend.valueOf(it.trend) }
                                ?: UNKNOWN).let {
                                Icon(
                                    modifier = modifier
                                        .align(CenterVertically)
                                        .requiredWidth(15.dp)
                                        .requiredHeight(15.dp)
                                        .testTag(GLUCOSE_READING_TREND_ARROW),
                                    painter = painterResource(it.iconResource),
                                    contentDescription = it.contentDescription
                                )
                            }

                        }
                    }

                    Row(
                        modifier = modifier
                            .align(End)
                            .padding(16.dp)
                    ) {
                        Text(
                            modifier = modifier.testTag(GLUCOSE_READING_TIMESTAMP_TEXT),
                            text = lastReading?.timestamp?.time?.let { "${it.hour}:${it.minute}.${it.second}" }
                                ?: "--:--.--",
                            fontSize = typography.labelSmall.fontSize,
                        )
                        Spacer(modifier = modifier.width(4.dp))
                    }
                }
            }

        }

        Spacer(modifier = modifier.weight(1f))

        Column(
            modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                var altColor = false
                items(lines) { line ->
                    Text(
                        text = line.msg,
                        fontFamily = Monospace,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorScheme.primary.takeIf { altColor } ?: Unspecified)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag(CONSOLE_LOG)
                    )
                    altColor = !altColor
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { clear() },
                    modifier = Modifier
                        .weight(1f)
                        .testTag(CLEAR_BUTTON)
                ) {
                    Text("Clear")
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun DashboardScreenPreview() {
    GlucoViewTheme {
        DashboardScreen(
            rememberNavController(),
            PreviewDashboardService
        )

        LogCollector.log("Message received:")
        LogCollector.log("Sending To Watch")
    }
}

@PreviewLightDark
@Composable
fun DashboardEmptyScreenPreview() {
    GlucoViewTheme {
        DashboardScreen(
            rememberNavController(),
            PreviewEmptyDashboardService
        )
    }
}
