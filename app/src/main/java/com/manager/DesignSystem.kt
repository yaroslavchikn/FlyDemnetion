package com.manager

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

private fun DrawScope.iconStroke(factor: Float = 0.09f): Stroke {
    return Stroke(
        width = minOf(size.width, size.height) * factor,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
    )
}

@Composable
private fun IconBase(
    modifier: Modifier,
    tint: Color,
    onDraw: DrawScope.(Color) -> Unit
) {
    Canvas(modifier = modifier) {
        onDraw(tint)
    }
}

private fun starPath(center: Offset, outer: Float, inner: Float): Path {
    val path = Path()

    var angle = -Math.PI / 2
    val step = Math.PI / 5

    path.moveTo(
        x = center.x + (outer * cos(angle).toFloat()),
        y = center.y + (outer * sin(angle).toFloat())
    )

    for (i in 1 until 10) {
        angle += step
        val radius = if (i % 2 == 0) outer else inner

        path.lineTo(
            x = center.x + (radius * cos(angle).toFloat()),
            y = center.y + (radius * sin(angle).toFloat())
        )
    }

    path.close()
    return path
}

@Composable
fun FBack(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.1f)
    val w = size.width
    val h = size.height

    drawLine(color, Offset(w * 0.78f, h * 0.5f), Offset(w * 0.22f, h * 0.5f), stroke)
    drawLine(color, Offset(w * 0.44f, h * 0.28f), Offset(w * 0.22f, h * 0.5f), stroke)
    drawLine(color, Offset(w * 0.44f, h * 0.72f), Offset(w * 0.22f, h * 0.5f), stroke)
}

@Composable
fun FClose(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.1f)
    val w = size.width
    val h = size.height

    drawLine(color, Offset(w * 0.24f, h * 0.24f), Offset(w * 0.76f, h * 0.76f), stroke)
    drawLine(color, Offset(w * 0.76f, h * 0.24f), Offset(w * 0.24f, h * 0.76f), stroke)
}

@Composable
fun FSearch(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.09f)
    val min = minOf(size.width, size.height)

    drawCircle(
        color = color,
        center = Offset(size.width * 0.42f, size.height * 0.42f),
        radius = min * 0.22f,
        style = stroke
    )

    drawLine(
        color = color,
        start = Offset(size.width * 0.60f, size.height * 0.60f),
        end = Offset(size.width * 0.84f, size.height * 0.84f),
        strokeWidth = stroke.width,
        cap = stroke.cap
    )
}

@Composable
fun FGrid(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val w = size.width
    val h = size.height
    val box = Size(w * 0.3f, h * 0.3f)
    val corner = CornerRadius(w * 0.07f)

    drawRoundRect(color, Offset(w * 0.14f, h * 0.14f), box, corner)
    drawRoundRect(color, Offset(w * 0.56f, h * 0.14f), box, corner)
    drawRoundRect(color, Offset(w * 0.14f, h * 0.56f), box, corner)
    drawRoundRect(color, Offset(w * 0.56f, h * 0.56f), box, corner)
}

@Composable
fun FList(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.08f)
    val w = size.width
    val h = size.height

    drawCircle(color, Offset(w * 0.18f, h * 0.28f), w * 0.045f)
    drawLine(color, Offset(w * 0.32f, h * 0.28f), Offset(w * 0.84f, h * 0.28f), stroke)

    drawCircle(color, Offset(w * 0.18f, h * 0.5f), w * 0.045f)
    drawLine(color, Offset(w * 0.32f, h * 0.5f), Offset(w * 0.84f, h * 0.5f), stroke)

    drawCircle(color, Offset(w * 0.18f, h * 0.72f), w * 0.045f)
    drawLine(color, Offset(w * 0.32f, h * 0.72f), Offset(w * 0.84f, h * 0.72f), stroke)
}

@Composable
fun FSort(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.09f)
    val w = size.width
    val h = size.height

    drawLine(color, Offset(w * 0.18f, h * 0.3f), Offset(w * 0.82f, h * 0.3f), stroke)
    drawLine(color, Offset(w * 0.18f, h * 0.5f), Offset(w * 0.66f, h * 0.5f), stroke)
    drawLine(color, Offset(w * 0.18f, h * 0.7f), Offset(w * 0.5f, h * 0.7f), stroke)
}

@Composable
fun FFolderPlus(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.08f)
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(w * 0.1f, h * 0.32f)
        lineTo(w * 0.36f, h * 0.32f)
        lineTo(w * 0.44f, h * 0.4f)
        lineTo(w * 0.9f, h * 0.4f)
        lineTo(w * 0.9f, h * 0.72f)
        lineTo(w * 0.1f, h * 0.72f)
        close()
    }

    drawPath(path, color, style = stroke)

    drawLine(color, Offset(w * 0.5f, h * 0.48f), Offset(w * 0.5f, h * 0.64f), stroke)
    drawLine(color, Offset(w * 0.42f, h * 0.56f), Offset(w * 0.58f, h * 0.56f), stroke)
}

@Composable
fun FFolder(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(w * 0.08f, h * 0.32f)
        lineTo(w * 0.35f, h * 0.32f)
        lineTo(w * 0.43f, h * 0.4f)
        lineTo(w * 0.92f, h * 0.4f)
        lineTo(w * 0.92f, h * 0.72f)
        lineTo(w * 0.08f, h * 0.72f)
        close()
    }

    drawPath(path, color)
}

@Composable
fun FFile(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.08f)
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(w * 0.26f, h * 0.14f)
        lineTo(w * 0.6f, h * 0.14f)
        lineTo(w * 0.74f, h * 0.28f)
        lineTo(w * 0.74f, h * 0.86f)
        lineTo(w * 0.26f, h * 0.86f)
        close()
    }

    drawPath(path, color, style = stroke)
    drawLine(color, Offset(w * 0.6f, h * 0.14f), Offset(w * 0.6f, h * 0.28f), stroke)
    drawLine(color, Offset(w * 0.6f, h * 0.28f), Offset(w * 0.74f, h * 0.28f), stroke)
}

@Composable
fun FImageFile(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.07f)
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(w * 0.24f, h * 0.16f)
        lineTo(w * 0.6f, h * 0.16f)
        lineTo(w * 0.76f, h * 0.32f)
        lineTo(w * 0.76f, h * 0.84f)
        lineTo(w * 0.24f, h * 0.84f)
        close()
    }

    drawPath(path, color, style = stroke)
    drawCircle(color, Offset(w * 0.39f, h * 0.38f), w * 0.05f)

    val mountain = Path().apply {
        moveTo(w * 0.3f, h * 0.68f)
        lineTo(w * 0.45f, h * 0.5f)
        lineTo(w * 0.54f, h * 0.6f)
        lineTo(w * 0.61f, h * 0.52f)
        lineTo(w * 0.7f, h * 0.68f)
    }

    drawPath(mountain, color, style = stroke)
}

@Composable
fun FVideoFile(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.07f)
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(w * 0.24f, h * 0.16f)
        lineTo(w * 0.6f, h * 0.16f)
        lineTo(w * 0.76f, h * 0.32f)
        lineTo(w * 0.76f, h * 0.84f)
        lineTo(w * 0.24f, h * 0.84f)
        close()
    }

    drawPath(path, color, style = stroke)

    val play = Path().apply {
        moveTo(w * 0.42f, h * 0.42f)
        lineTo(w * 0.42f, h * 0.64f)
        lineTo(w * 0.62f, h * 0.53f)
        close()
    }

    drawPath(play, color)
}

@Composable
fun FAudioFile(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.07f)
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(w * 0.24f, h * 0.16f)
        lineTo(w * 0.6f, h * 0.16f)
        lineTo(w * 0.76f, h * 0.32f)
        lineTo(w * 0.76f, h * 0.84f)
        lineTo(w * 0.24f, h * 0.84f)
        close()
    }

    drawPath(path, color, style = stroke)
    drawCircle(color, Offset(w * 0.43f, h * 0.62f), w * 0.06f)
    drawLine(color, Offset(w * 0.49f, h * 0.62f), Offset(w * 0.56f, h * 0.38f), stroke)
}

@Composable
fun FStar(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val center = Offset(size.width / 2f, size.height / 2f)
    val outer = minOf(size.width, size.height) * 0.42f
    val inner = outer * 0.45f

    drawPath(starPath(center, outer, inner), color)
}

@Composable
fun FTrash(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.08f)
    val thin = iconStroke(0.06f)
    val w = size.width
    val h = size.height

    drawLine(color, Offset(w * 0.2f, h * 0.28f), Offset(w * 0.8f, h * 0.28f), stroke)
    drawLine(color, Offset(w * 0.4f, h * 0.2f), Offset(w * 0.6f, h * 0.2f), stroke)

    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.26f, h * 0.28f),
        size = Size(w * 0.48f, h * 0.52f),
        cornerRadius = CornerRadius(w * 0.08f),
        style = stroke
    )

    drawLine(color, Offset(w * 0.42f, h * 0.4f), Offset(w * 0.42f, h * 0.68f), thin)
    drawLine(color, Offset(w * 0.58f, h * 0.4f), Offset(w * 0.58f, h * 0.68f), thin)
}

@Composable
fun FRestore(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.09f)
    val w = size.width
    val h = size.height

    drawArc(
        color = color,
        startAngle = 40f,
        sweepAngle = 280f,
        useCenter = false,
        topLeft = Offset(w * 0.2f, h * 0.2f),
        size = Size(w * 0.6f, h * 0.6f),
        style = stroke
    )

    drawLine(color, Offset(w * 0.66f, h * 0.18f), Offset(w * 0.78f, h * 0.24f), stroke)
    drawLine(color, Offset(w * 0.78f, h * 0.24f), Offset(w * 0.68f, h * 0.34f), stroke)
}

@Composable
fun FDeleteForever(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.08f)
    val thin = iconStroke(0.06f)
    val w = size.width
    val h = size.height

    drawLine(color, Offset(w * 0.2f, h * 0.28f), Offset(w * 0.8f, h * 0.28f), stroke)
    drawLine(color, Offset(w * 0.4f, h * 0.2f), Offset(w * 0.6f, h * 0.2f), stroke)

    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.26f, h * 0.28f),
        size = Size(w * 0.48f, h * 0.52f),
        cornerRadius = CornerRadius(w * 0.08f),
        style = stroke
    )

    drawLine(color, Offset(w * 0.42f, h * 0.44f), Offset(w * 0.58f, h * 0.62f), thin)
    drawLine(color, Offset(w * 0.58f, h * 0.44f), Offset(w * 0.42f, h * 0.62f), thin)
}

@Composable
fun FCopy(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.08f)
    val w = size.width
    val h = size.height

    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.32f, h * 0.14f),
        size = Size(w * 0.48f, h * 0.56f),
        cornerRadius = CornerRadius(w * 0.08f),
        style = stroke
    )

    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.2f, h * 0.3f),
        size = Size(w * 0.48f, h * 0.56f),
        cornerRadius = CornerRadius(w * 0.08f),
        style = stroke
    )
}

@Composable
fun FMove(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.09f)
    val w = size.width
    val h = size.height

    drawLine(color, Offset(w * 0.16f, h * 0.5f), Offset(w * 0.76f, h * 0.5f), stroke)
    drawLine(color, Offset(w * 0.6f, h * 0.34f), Offset(w * 0.76f, h * 0.5f), stroke)
    drawLine(color, Offset(w * 0.6f, h * 0.66f), Offset(w * 0.76f, h * 0.5f), stroke)
}

@Composable
fun FEdit(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.08f)
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(w * 0.2f, h * 0.8f)
        lineTo(w * 0.24f, h * 0.64f)
        lineTo(w * 0.62f, h * 0.26f)
        lineTo(w * 0.74f, h * 0.38f)
        lineTo(w * 0.36f, h * 0.76f)
        close()
    }

    drawPath(path, color, style = stroke)
}

@Composable
fun FShare(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.06f)
    val w = size.width
    val h = size.height

    drawCircle(color, Offset(w * 0.3f, h * 0.5f), w * 0.08f)
    drawCircle(color, Offset(w * 0.7f, h * 0.3f), w * 0.08f)
    drawCircle(color, Offset(w * 0.7f, h * 0.7f), w * 0.08f)

    drawLine(color, Offset(w * 0.37f, h * 0.46f), Offset(w * 0.62f, h * 0.34f), stroke)
    drawLine(color, Offset(w * 0.37f, h * 0.54f), Offset(w * 0.62f, h * 0.66f), stroke)
}

@Composable
fun FInfo(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.08f)
    val w = size.width
    val h = size.height

    drawCircle(color, Offset(w * 0.5f, h * 0.5f), minOf(w, h) * 0.34f, style = stroke)
    drawLine(color, Offset(w * 0.5f, h * 0.44f), Offset(w * 0.5f, h * 0.68f), stroke)
    drawCircle(color, Offset(w * 0.5f, h * 0.32f), w * 0.035f)
}

@Composable
fun FSelectAll(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.08f)
    val w = size.width
    val h = size.height

    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.18f, h * 0.18f),
        size = Size(w * 0.64f, h * 0.64f),
        cornerRadius = CornerRadius(w * 0.1f),
        style = stroke
    )

    drawLine(color, Offset(w * 0.34f, h * 0.52f), Offset(w * 0.46f, h * 0.64f), stroke)
    drawLine(color, Offset(w * 0.46f, h * 0.64f), Offset(w * 0.68f, h * 0.38f), stroke)
}

@Composable
fun FMore(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val w = size.width
    val h = size.height
    val radius = minOf(w, h) * 0.07f

    drawCircle(color, Offset(w * 0.5f, h * 0.28f), radius)
    drawCircle(color, Offset(w * 0.5f, h * 0.5f), radius)
    drawCircle(color, Offset(w * 0.5f, h * 0.72f), radius)
}

@Composable
fun FCheck(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.11f)
    val w = size.width
    val h = size.height

    drawLine(color, Offset(w * 0.2f, h * 0.52f), Offset(w * 0.42f, h * 0.72f), stroke)
    drawLine(color, Offset(w * 0.42f, h * 0.72f), Offset(w * 0.8f, h * 0.3f), stroke)
}

@Composable
fun FChevron(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.1f)
    val w = size.width
    val h = size.height

    drawLine(color, Offset(w * 0.38f, h * 0.28f), Offset(w * 0.62f, h * 0.5f), stroke)
    drawLine(color, Offset(w * 0.62f, h * 0.5f), Offset(w * 0.38f, h * 0.72f), stroke)
}

@Composable
fun FUp(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.1f)
    val w = size.width
    val h = size.height

    drawLine(color, Offset(w * 0.5f, h * 0.78f), Offset(w * 0.5f, h * 0.24f), stroke)
    drawLine(color, Offset(w * 0.3f, h * 0.42f), Offset(w * 0.5f, h * 0.24f), stroke)
    drawLine(color, Offset(w * 0.7f, h * 0.42f), Offset(w * 0.5f, h * 0.24f), stroke)
}

@Composable
fun FPhone(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.08f)
    val w = size.width
    val h = size.height

    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.3f, h * 0.14f),
        size = Size(w * 0.4f, h * 0.72f),
        cornerRadius = CornerRadius(w * 0.08f),
        style = stroke
    )

    drawLine(color, Offset(w * 0.44f, h * 0.78f), Offset(w * 0.56f, h * 0.78f), stroke)
}

@Composable
fun FPlay(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(w * 0.35f, h * 0.25f)
        lineTo(w * 0.75f, h * 0.5f)
        lineTo(w * 0.35f, h * 0.75f)
        close()
    }

    drawPath(path, color)
}

@Composable
fun FPause(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val w = size.width
    val h = size.height
    val corner = CornerRadius(w * 0.04f)

    drawRoundRect(color, Offset(w * 0.34f, h * 0.28f), Size(w * 0.1f, h * 0.44f), corner)
    drawRoundRect(color, Offset(w * 0.56f, h * 0.28f), Size(w * 0.1f, h * 0.44f), corner)
}

@Composable
fun FVolume(
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.colorScheme.onSurface
) = IconBase(modifier, tint) { color ->
    val stroke = iconStroke(0.07f)
    val w = size.width
    val h = size.height

    val speaker = Path().apply {
        moveTo(w * 0.2f, h * 0.4f)
        lineTo(w * 0.34f, h * 0.4f)
        lineTo(w * 0.5f, h * 0.26f)
        lineTo(w * 0.5f, h * 0.74f)
        lineTo(w * 0.34f, h * 0.6f)
        lineTo(w * 0.2f, h * 0.6f)
        close()
    }

    drawPath(speaker, color)

    drawArc(
        color = color,
        startAngle = -30f,
        sweepAngle = 60f,
        useCenter = false,
        topLeft = Offset(w * 0.55f, h * 0.35f),
        size = Size(w * 0.3f, h * 0.3f),
        style = stroke
    )
}

@Composable
fun AppIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable (Color) -> Unit
) {
    val finalTint = if (enabled) tint else tint.copy(alpha = 0.38f)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(size * 0.18f),
        contentAlignment = Alignment.Center
    ) {
        content(finalTint)
    }
}

@Composable
fun AppGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    brush: Brush? = null,
    contentColor: Color? = null,
    icon: (@Composable (Color) -> Unit)? = null
) {
    val defaultBrush = if (enabled) {
        Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.tertiary
            )
        )
    } else {
        SolidColor(MaterialTheme.colorScheme.surfaceVariant)
    }

    val finalTextColor = contentColor
        ?: if (enabled) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(brush ?: defaultBrush)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.invoke(finalTextColor)

            if (icon != null) {
                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = text,
                color = finalTextColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun DialogTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = hint,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FSearch(
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.width(8.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = "Поиск файлов",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (value.isNotEmpty()) {
            AppIconButton(
                onClick = { onValueChange("") },
                size = 32.dp,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            ) { color ->
                FClose(Modifier.size(18.dp), color)
            }
        }
    }
}

@Composable
fun ActionChip(
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    icon: @Composable (Color) -> Unit
) {
    val color = if (enabled) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon(color)

        Spacer(Modifier.height(4.dp))

        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface.copy(
                alpha = if (enabled) 1f else 0.4f
            ),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun StorageCard(
    freeSpace: Long,
    totalSpace: Long,
    modifier: Modifier = Modifier
) {
    val used = (totalSpace - freeSpace).coerceAtLeast(0L)
    val progress = if (totalSpace > 0) used.toFloat() / totalSpace.toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.85f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Внутренняя память",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${used.formatSize()} из ${totalSpace.formatSize()}",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color.White)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Свободно: ${freeSpace.formatSize()}",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PathChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
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
fun CustomBottomNav(
    selected: BottomTab,
    onSelect: (BottomTab) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 10.dp,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            BottomTab.values().forEach { tab ->
                val isSelected = selected == tab

                val activeColor = MaterialTheme.colorScheme.primary
                val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant

                val color by animateColorAsState(
                    targetValue = if (isSelected) activeColor else inactiveColor
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSelect(tab) }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                else Color.Transparent
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        when (tab) {
                            BottomTab.PHONE -> FPhone(Modifier.size(24.dp), color)
                            BottomTab.FAVORITES -> FStar(Modifier.size(24.dp), color)
                            BottomTab.TRASH -> FTrash(Modifier.size(24.dp), color)
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = tab.label,
                        color = color,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
