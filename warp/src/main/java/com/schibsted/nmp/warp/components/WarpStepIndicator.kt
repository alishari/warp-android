package com.schibsted.nmp.warp.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.schibsted.nmp.warp.theme.WarpBrandedTheme
import com.schibsted.nmp.warp.theme.WarpTheme
import com.schibsted.nmp.warp.utils.FlavorPreviewProvider

/**
 * A component to show a user where they are in a user journey
 * For more info [see](https://warp-ds.github.io/tech-docs/components/steps/)
 * @param modifier Modifier applied to the component
 * @param steps Amount of steps
 * @param activeStep Currently active step
 * @param orientation Orientation to draw the steps in
 * @param onStepClicked will be invoked if a step is clicked
 * @param stepTitle Optional composable that will be drawn with the step
 * @param stepDescription Optional composable that will be drawn below the title
 */
@Composable
fun WarpStepIndicator(
    modifier: Modifier = Modifier,
    steps: Int = 3,
    activeStep: Int = 0,
    orientation: WarpStepIndicatorOrientation = WarpStepIndicatorOrientation.Horizontal,
    onStepClicked: ((Int) -> Unit)?,
    stepTitle: @Composable (Int) -> Unit = {},
    stepDescription: @Composable (Int) -> Unit = {}
) {
    if (orientation == WarpStepIndicatorOrientation.Horizontal) {
        HorizontalWarpStepIndicator(modifier, steps, activeStep, onStepClicked, stepTitle, stepDescription)
    } else {
        VerticalWarpStepIndicator(modifier, steps, activeStep, onStepClicked, stepTitle, stepDescription)
    }
}

@Composable
internal fun VerticalWarpStepIndicator(
    modifier: Modifier,
    steps: Int = 3,
    activeStep: Int = 0,
    onStepClicked: ((Int) -> Unit)?,
    stepTitle: @Composable (Int) -> Unit,
    stepDescription: @Composable (Int) -> Unit
) {
    ConstraintLayout(modifier) {
        var previousDot: ConstrainedLayoutReference? = null
        var previousDescription: ConstrainedLayoutReference? = null
        for (i in 0 until steps) {
            val type = when {
                i < activeStep -> StepState.Completed
                i == activeStep -> StepState.Active
                else -> StepState.Unvisited
            }
            val (title, description, dot) = createRefs()
            val p = previousDescription
            val d = previousDot
            val dotModifier = if (p != null && d != null) {
                val barrier = createBottomBarrier(d, p)
                Modifier.constrainAs(dot) {
                    start.linkTo(parent.start)
                    top.linkTo(barrier)
                }
            } else {
                Modifier.constrainAs(dot) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
            }
            Box(
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(dot.top)
                    bottom.linkTo(dot.bottom)
                    start.linkTo(dot.end)
                }
            ) {
                stepTitle(i)
            }

            Box(
                modifier = Modifier.constrainAs(description) {
                    top.linkTo(title.bottom)
                    start.linkTo(dot.end)
                }
            ) {
                stepDescription(i)
            }
            Dot(
                modifier = dotModifier,
                state = type,
            ) {
                onStepClicked?.invoke(i)
            }
            val completedColor = WarpTheme.colors.components.stepIndicator.completed
            val unvisitedColor = WarpTheme.colors.components.stepIndicator.unvisited
            if (d != null) {
                val divider = createRef()
                Box(
                    modifier = Modifier.fillMaxHeight()
                        .width(2.dp)
                        .background(if (type == StepState.Unvisited) unvisitedColor else completedColor)
                        .constrainAs(divider) {
                            height = Dimension.fillToConstraints
                            top.linkTo(d.bottom)
                            start.linkTo(dot.start)
                            end.linkTo(dot.end)
                            bottom.linkTo(dot.top)
                        }
                )

            }
            previousDescription = description
            previousDot = dot
        }
    }
}

@Composable
internal fun HorizontalWarpStepIndicator(
    modifier: Modifier,
    steps: Int = 3,
    activeStep: Int = 0,
    onStepClicked: ((Int) -> Unit)?,
    stepTitle: @Composable (Int) -> Unit,
    stepDescription: @Composable (Int) -> Unit
) {
    ConstraintLayout(modifier = modifier) {
        val descriptions = arrayListOf<ConstrainedLayoutReference>()
        var previousDot: ConstrainedLayoutReference? = null
        for (i in 0 until steps) {
            val type = when {
                i < activeStep -> StepState.Completed
                i == activeStep -> StepState.Active
                else -> StepState.Unvisited
            }
            val (description, dot) = createRefs()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.constrainAs(description) {

                }) {
                stepTitle(i)
                stepDescription(i)
            }
            Dot(
                state = type,
                modifier = Modifier.constrainAs(dot) {
                    start.linkTo(description.start)
                    end.linkTo(description.end)
                    top.linkTo(description.bottom, margin = 4.dp)
                },
                onClick = onStepClicked?.let { { onStepClicked(i) } }
            )
            val completedColor = WarpTheme.colors.components.stepIndicator.completed
            val unvisitedColor = WarpTheme.colors.components.stepIndicator.unvisited
            previousDot?.let { p ->
                val divider = createRef()
                Divider(
                    color = if (type == StepState.Unvisited) unvisitedColor else completedColor,
                    thickness = 2.dp,
                    modifier = Modifier.constrainAs(divider) {
                        width = Dimension.fillToConstraints
                        start.linkTo(p.end)
                        end.linkTo(dot.start)
                        top.linkTo(dot.top)
                        bottom.linkTo(dot.bottom)
                    }
                )
            }
            descriptions.add(description)
            previousDot = dot
        }
        createHorizontalChain(*descriptions.toTypedArray(), chainStyle = ChainStyle.SpreadInside)
    }
}


enum class StepState {
    Active,
    Completed,
    Unvisited,
    // TODO peeked?
}

@Composable
fun Dot(
    modifier: Modifier = Modifier,
    state: StepState,
    onClick: (() -> Unit)?
) {
    val completedColor = WarpTheme.colors.components.stepIndicator.completed
    val unvisitedColor = WarpTheme.colors.components.stepIndicator.unvisited
    Box(
        contentAlignment = Alignment.Center,
        modifier = (if (onClick != null) modifier
            .clip(CircleShape)
            .clickable { onClick() } else modifier)
    ) {
        when (state) {
            StepState.Active -> {
                Canvas(modifier = Modifier.size(24.dp)) {
                    drawCircle(completedColor)
                }
            }
            StepState.Completed -> {
                Icon(
                    Icons.Filled.Check, // TODO Change to correct one once we have icons?
                    contentDescription = null,  // TODO: content description?
                    tint = WarpTheme.colors.components.stepIndicator.completedIconTint,
                    modifier = Modifier
                        .size(24.dp)
                        .background(completedColor, CircleShape)
                        .padding(2.dp)
                )

            }
            StepState.Unvisited -> {
                Canvas(modifier = Modifier.size(24.dp)) {
                    drawCircle(unvisitedColor, style = Stroke(6f), radius = size.minDimension/2f - 3f)
                }
            }
        }
    }
}

enum class WarpStepIndicatorOrientation {
    Horizontal,
    Vertical
}

@Preview
@Composable
internal fun HorizontalPreview(
    @PreviewParameter(FlavorPreviewProvider::class) flavor: String,
) {
    var active by remember { mutableStateOf(1) }
    WarpBrandedTheme(
        flavor,
        isSystemInDarkTheme()
    ) {
        WarpStepIndicator(
            modifier = Modifier,
            steps = 3,
            activeStep = active,
            onStepClicked = { active = it },
            stepTitle = { Text("Step ${it + 1}", style = WarpTheme.typography.title5) },
            stepDescription = { Text("Description", style = WarpTheme.typography.detail) }
        )
    }
}

@Preview
@Composable
internal fun VerticalPreview(
    @PreviewParameter(FlavorPreviewProvider::class) flavor: String,
) {
    var active by remember { mutableStateOf(1) }
    WarpBrandedTheme(
        flavor,
        isSystemInDarkTheme()
    ) {
        WarpStepIndicator(
            modifier = Modifier,
            orientation = WarpStepIndicatorOrientation.Vertical,
            steps = 3,
            activeStep = active,
            onStepClicked = { active = it },
            stepTitle = { Text("Step ${it + 1}", style = WarpTheme.typography.title4) },
            stepDescription = { Text("Description", style = WarpTheme.typography.caption) }
        )
    }
}
