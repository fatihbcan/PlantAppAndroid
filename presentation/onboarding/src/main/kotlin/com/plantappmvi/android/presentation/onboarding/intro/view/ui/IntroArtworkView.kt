package com.plantappmvi.android.presentation.onboarding.intro.view.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plantappmvi.android.presentation.onboarding.R
import com.plantappmvi.android.presentation.onboarding.intro.view.props.IntroArtwork

/**
 * Draws the artwork for one intro page.
 *
 * Every piece is laid out as a fraction of the space it is given, taken from
 * the design's own proportions, so the composition holds from a small phone to
 * a tablet instead of drifting apart at fixed offsets. The artwork is
 * decorative throughout — the copy above it already says what the app does —
 * so none of it carries a content description.
 */
@Composable
internal fun IntroArtworkView(
    artwork: IntroArtwork,
    modifier: Modifier = Modifier,
) {
    when (artwork) {
        IntroArtwork.WELCOME -> WelcomeArtwork(modifier)
        IntroArtwork.IDENTIFY -> IdentifyArtwork(modifier)
        IntroArtwork.CARE_GUIDES -> CareGuidesArtwork(modifier)
    }
}

/** The potted plant, the viewfinder over it, and the three care badges. */
@Composable
private fun WelcomeArtwork(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val boxWidth = maxWidth
        val boxHeight = maxHeight

        // The plant is placed explicitly rather than through an alignment, so
        // the badges can hang off its rendered rect. In the design they are
        // pinned to the plant, not to the page, and anchoring them to the box
        // instead is what let them drift.
        val plantHeight = boxHeight * PlantHeightFactor
        val plantWidth = plantHeight * PlantAspect
        val plantLeft = (boxWidth - plantWidth) / 2
        val plantTop = boxHeight * PlantTopFactor

        Image(
            painter = painterResource(R.drawable.img_onboarding_welcome_plant),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .offset(x = plantLeft, y = plantTop)
                .size(width = plantWidth, height = plantHeight),
        )

        ScanFrame(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -boxHeight * ScanFrameRiseFactor)
                .size(width = boxWidth * 0.62f, height = boxHeight * 0.44f),
        )

        PlantBadge(R.drawable.img_badge_spray, plantLeft, plantTop, plantWidth, plantHeight, 0.109f, 0.126f, 0.212f)
        PlantBadge(R.drawable.img_badge_sun, plantLeft, plantTop, plantWidth, plantHeight, 0.883f, 0.135f, 0.154f)
        PlantBadge(R.drawable.img_badge_water, plantLeft, plantTop, plantWidth, plantHeight, 0.737f, 0.824f, 0.116f)
    }
}

/** One care badge, centred on a point given in the plant's own coordinates. */
@Composable
private fun PlantBadge(
    @DrawableRes drawable: Int,
    plantLeft: Dp,
    plantTop: Dp,
    plantWidth: Dp,
    plantHeight: Dp,
    centreX: Float,
    centreY: Float,
    scale: Float,
) {
    val size = plantWidth * scale
    Image(
        painter = painterResource(drawable),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .offset(
                x = plantLeft + plantWidth * centreX - size / 2,
                y = plantTop + plantHeight * centreY - size / 2,
            )
            .size(size),
    )
}

/**
 * The phone mockup with the plant growing out behind it and the viewfinder
 * over its camera preview.
 */
@Composable
private fun IdentifyArtwork(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val boxWidth = maxWidth
        val boxHeight = maxHeight

        val phoneWidth = boxWidth * IdentifyPhoneWidthFactor
        val phoneHeight = phoneWidth * PhoneAspect
        val phoneTop = boxHeight - phoneHeight - boxHeight * PhoneLiftFactor

        // The plant stands behind the phone and only its crown shows, which is
        // what the design's silhouette depends on.
        //
        // It is drawn whole at the phone's own width and clipped to the band
        // above it, which is what keeps only the leaves in view: sizing it to
        // the band instead — the artwork box less a share of the phone — left
        // half the plant, stems and all, standing above the mockup, and cost
        // it the sides of its leaves to the crop besides.
        //
        // Phone width exactly, and the band runs a little *under* the phone's
        // top edge: the clip cuts the plant flat, and the phone has to be wide
        // and high enough to hide that cut. A crown any wider leaves the flat
        // edge showing beside the mockup.
        val crownWidth = phoneWidth
        val crownHeight = crownWidth / PlantAspect
        Box(
            modifier = Modifier
                .offset(x = (boxWidth - phoneWidth) / 2)
                .size(width = phoneWidth, height = phoneTop + phoneHeight * CrownTuckFactor)
                .clipToBounds(),
        ) {
            Image(
                painter = painterResource(R.drawable.img_onboarding_welcome_plant),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                // The plant is deliberately taller than the band that clips
                // it, so it has to be measured unbounded and pinned to the
                // band's top edge. `size` on its own is coerced back into the
                // band and squashes the whole plant into it; `requiredSize`
                // escapes the constraints but then centres what it drew, which
                // shows the plant's middle instead of its crown.
                modifier = Modifier
                    .wrapContentSize(align = Alignment.TopCenter, unbounded = true)
                    .size(width = crownWidth, height = crownHeight),
            )
        }

        Box(
            modifier = Modifier
                .offset(x = (boxWidth - phoneWidth) / 2, y = phoneTop)
                .size(width = phoneWidth, height = phoneHeight),
        ) {
            Image(
                painter = painterResource(R.drawable.img_onboarding_identify_phone),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize(),
            )
            ScanFrame(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(width = phoneWidth * 0.62f, height = phoneHeight * 0.42f),
            )
        }
    }
}

/**
 * A phone showing a plant-care page, with the guide cards floating over it and
 * out-of-focus foliage scattered behind.
 */
@Composable
private fun CareGuidesArtwork(modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val boxWidth = maxWidth
        val boxHeight = maxHeight

        val phoneWidth = boxWidth * CarePhoneWidthFactor
        val bezel = phoneWidth * BezelFactor
        val radius = phoneWidth * PhoneRadiusFactor
        val phoneTop = boxHeight * CarePhoneTopFactor

        // The export is only softly out of focus; the extra blur is what turns
        // it into the green haze the design puts behind the phone.
        Image(
            painter = painterResource(R.drawable.img_leaf_blobs),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(LeafBlurRadius),
        )

        Box(
            modifier = Modifier
                .offset(x = (boxWidth - phoneWidth) / 2, y = phoneTop)
                .size(width = phoneWidth, height = boxHeight - phoneTop)
                // Square at the bottom: the phone runs behind the call to
                // action rather than stopping above it, so rounding there
                // would leave a visible gap.
                .clip(RoundedCornerShape(topStart = radius, topEnd = radius))
                .background(PhoneBezelColor)
                .padding(start = bezel, top = bezel, end = bezel),
        ) {
            // Crop, not FillWidth: the export is not as tall as the bezel it
            // sits in, so scaling it by width alone left a black strip of
            // bezel between the screenshot and the call to action below.
            // Covering the box hides that strip, at the cost of a sliver off
            // the export's bottom — which is under the button anyway.
            Image(
                painter = painterResource(R.drawable.img_onboarding_care_screen),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = radius - bezel, topEnd = radius - bezel)),
            )
        }

        Image(
            painter = painterResource(R.drawable.img_onboarding_care_cards),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = boxHeight * 0.02f)
                .fillMaxWidth(CareCardsWidthFactor),
        )

        FloatingBadge(R.drawable.img_badge_spray, Alignment.TopCenter, boxWidth * 0.095f, boxWidth * 0.15f)
        FloatingBadge(R.drawable.img_badge_sun, Alignment.TopEnd, boxWidth * 0.075f, -boxWidth * 0.02f)
    }
}

@Composable
private fun BoxWithConstraintsScope.FloatingBadge(
    @DrawableRes drawable: Int,
    alignment: Alignment,
    size: Dp,
    offsetX: Dp,
) {
    Image(
        painter = painterResource(drawable),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .align(alignment)
            .offset(x = offsetX, y = maxHeight * 0.01f)
            .size(size),
    )
}

/** The plant export's own aspect, and where its box sits in the artwork area. */
private const val PlantAspect = 200f / 332f
private const val PlantHeightFactor = 0.87f
private const val PlantTopFactor = 0.026f
private const val ScanFrameRiseFactor = 0.11f

/** Height over width of the cropped phone export. */
private const val PhoneAspect = 320f / 197f
private const val IdentifyPhoneWidthFactor = 0.69f
private const val PhoneLiftFactor = 0.05f

/** How far the crown runs on under the phone, so its clipped edge is hidden. */
private const val CrownTuckFactor = 0.04f

private const val CarePhoneWidthFactor = 0.72f
private const val CareCardsWidthFactor = 0.40f
private const val BezelFactor = 0.035f
private const val PhoneRadiusFactor = 0.13f
private const val CarePhoneTopFactor = 0.10f

private val LeafBlurRadius = 12.dp
private val PhoneBezelColor = Color(0xFF0B0B0B)
