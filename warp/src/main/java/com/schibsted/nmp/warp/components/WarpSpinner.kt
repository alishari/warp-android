package com.schibsted.nmp.warp.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.schibsted.nmp.warp.theme.WarpTheme

@Composable
fun WarpSpinner(
    modifier: Modifier = Modifier,
    size: WarpSpinnerSize = WarpSpinnerSize.Default,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth
) {
    CircularProgressIndicator(
        modifier = modifier.size(size.size),
        strokeWidth = strokeWidth,
        color = WarpTheme.colors.components.spinner.color,
        trackColor = WarpTheme.colors.components.spinner.trackColor.copy(alpha = .5f)
    )
}

sealed interface WarpSpinnerSize {
    val size: Dp

    data object Small : WarpSpinnerSize {
        override val size = 16.dp
    }

    data object Default : WarpSpinnerSize {
        override val size = 24.dp
    }

    data object Large : WarpSpinnerSize {
        override val size = 32.dp
    }

    data class Custom(val customSize: Dp) : WarpSpinnerSize {
        override val size = customSize
    }
}