package com.plantappmvi.android.presentation.home.view.props

import com.plantappmvi.android.core.designsystem.R as DesignSystemR
import com.plantappmvi.android.core.presentation.resource.TextResource
import com.plantappmvi.android.domain.home.entities.Category
import com.plantappmvi.android.presentation.home.R
import com.plantappmvi.android.presentation.home.model.HomeFailure
import com.plantappmvi.android.presentation.home.model.HomeScreenState
import io.kotest.matchers.shouldBe
import org.junit.Test

/**
 * `mapStateToProps` is a plain non-composable function, so the screen's whole
 * presentation layer is testable without a Compose runtime — which is the
 * point of the Props indirection.
 */
class HomeScreenPropsTest {

    private val loaded = HomeScreenState.initial().copy(
        isLoading = false,
        categories = listOf(Category(1, "Ferns", ""), Category(2, "Cacti", "")),
    )

    @Test
    fun `the grid receives only the categories the filter kept`() {
        val props = mapStateToProps(loaded.copy(appliedQuery = "fern"))

        props.categories.map { it.id } shouldBe listOf(1)
    }

    @Test
    fun `a network failure gets the shared connection message`() {
        val state = loaded.copy(categories = emptyList(), categoriesFailure = HomeFailure.NETWORK)

        val props = mapStateToProps(state)

        props.showCategoriesError shouldBe true
        props.categoriesErrorMessage shouldBe
            TextResource.fromId(DesignSystemR.string.error_no_connection)
    }

    @Test
    fun `a server failure gets the section's own wording`() {
        // The user can act on a connection failure and cannot act on a broken
        // server, so the two do not share a message.
        val state = loaded.copy(categories = emptyList(), categoriesFailure = HomeFailure.SERVER)

        mapStateToProps(state).categoriesErrorMessage shouldBe
            TextResource.fromId(R.string.home_categories_error)
    }

    @Test
    fun `the empty-search message quotes the applied query`() {
        val props = mapStateToProps(loaded.copy(appliedQuery = "orchid"))

        props.emptySearchMessage shouldBe
            TextResource.fromId(R.string.home_search_empty, "orchid")
    }

    @Test
    fun `there is no empty-search message when the data set is simply empty`() {
        val props = mapStateToProps(HomeScreenState.initial().copy(isLoading = false))

        props.emptySearchMessage shouldBe null
    }

    @Test
    fun `the bar marks home as the current destination and nothing else`() {
        val props = mapStateToProps(loaded)

        props.destinations.count { it.isCurrent } shouldBe 1
        props.destinations.first().isCurrent shouldBe true
    }

    @Test
    fun `the field shows the query being typed, not the one being filtered on`() {
        val props = mapStateToProps(loaded.copy(query = "fer", appliedQuery = ""))

        props.query shouldBe "fer"
    }

    @Test
    fun `the premium strip carries the handler that opens the paywall`() {
        // The strip is the screen's one way out, and it was drawn as a button
        // for a while before it was wired to anything.
        var opened = false

        mapStateToProps(loaded, onPremiumBannerClick = { opened = true })
            .onPremiumBannerClick()

        opened shouldBe true
    }
}
