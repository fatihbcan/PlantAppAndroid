package com.plantappmvi.android.presentation.onboarding.intro

import com.plantappmvi.android.presentation.onboarding.intro.model.IntroScreenEvent
import com.plantappmvi.android.presentation.onboarding.intro.model.IntroScreenState
import com.plantappmvi.android.presentation.onboarding.intro.view.props.IntroArtwork
import com.plantappmvi.android.presentation.onboarding.intro.view.props.mapStateToProps
import io.kotest.matchers.shouldBe
import org.junit.Test

class IntroScreenStateTest {

    @Test
    fun `the welcome page shows no dots`() {
        // The design's three dots cover pages two and three plus the paywall;
        // the welcome page sits before the run.
        IntroScreenState.initial().showsPageIndicator shouldBe false
    }

    @Test
    fun `the first dot belongs to the second page`() {
        val state = IntroScreenState.initial().copy(pageIndex = 1)

        state.showsPageIndicator shouldBe true
        state.indicatorIndex shouldBe 0
    }

    @Test
    fun `the last page is the one that leaves the flow`() {
        IntroScreenState.initial().isLastPage shouldBe false
        IntroScreenState.initial().copy(pageIndex = 2).isLastPage shouldBe true
    }

    @Test
    fun `the announced page number is one-based`() {
        IntroScreenState.initial().humanPageNumber shouldBe 1
    }
}

class IntroScreenEventTest {

    @Test
    fun `PageChanged moves to the requested page`() {
        val new = IntroScreenEvent.PageChanged(1).reduce(IntroScreenState.initial())

        new.pageIndex shouldBe 1
    }

    @Test
    fun `a page beyond the end is clamped rather than accepted`() {
        val new = IntroScreenEvent.PageChanged(99).reduce(IntroScreenState.initial())

        new.pageIndex shouldBe 2
    }

    @Test
    fun `a negative page is clamped to the first`() {
        val old = IntroScreenState.initial().copy(pageIndex = 2)

        IntroScreenEvent.PageChanged(-4).reduce(old).pageIndex shouldBe 0
    }

    @Test
    fun `re-reporting the current page returns the same instance`() {
        // The pager reports its settled page on every settle, including ones
        // this screen caused. Returning the identical state is what stops that
        // from becoming a redundant emission.
        val old = IntroScreenState.initial().copy(pageIndex = 1)

        val new = IntroScreenEvent.PageChanged(1).reduce(old)

        (new === old) shouldBe true
    }
}

class IntroScreenPropsTest {

    @Test
    fun `each page carries its own artwork and call to action`() {
        val props = mapStateToProps(IntroScreenState.initial())

        props.pages.map { it.artwork } shouldBe listOf(
            IntroArtwork.WELCOME,
            IntroArtwork.IDENTIFY,
            IntroArtwork.CARE_GUIDES,
        )
    }

    @Test
    fun `only the welcome page carries the consent line`() {
        val props = mapStateToProps(IntroScreenState.initial())

        props.pages.count { it.legal != null } shouldBe 1
        props.currentLegal shouldBe props.pages.first().legal
    }

    @Test
    fun `the welcome page emphasises with weight alone, the others with the stroke`() {
        val props = mapStateToProps(IntroScreenState.initial())

        props.pages.map { it.isHighlightUnderlined } shouldBe listOf(false, true, true)
    }

    @Test
    fun `the footer and the call to action follow the visible page`() {
        val props = mapStateToProps(IntroScreenState.initial().copy(pageIndex = 1))

        props.currentCta shouldBe props.pages[1].cta
        props.currentLegal shouldBe null
        props.showsPageIndicator shouldBe true
    }
}
