package com.plantappmvi.android.data.home.mapper

import com.plantappmvi.android.data.home.dto.CategoryDto
import com.plantappmvi.android.data.home.dto.CategoryImageDto
import com.plantappmvi.android.data.home.dto.QuestionDto
import io.kotest.matchers.shouldBe
import org.junit.Test

/**
 * The API is external and hands back nulls in fields the UI treats as
 * required, so this is where that contract is pinned.
 */
class HomeMappersTest {

    @Test
    fun `a fully populated category maps every field`() {
        val entity = CategoryDto(
            id = 7,
            title = "Ferns",
            rank = 2,
            image = CategoryImageDto("https://example.test/fern.png"),
        ).toEntity()

        entity.id shouldBe 7
        entity.title shouldBe "Ferns"
        entity.rank shouldBe 2
        entity.imageUrl shouldBe "https://example.test/fern.png"
        entity.hasImage shouldBe true
    }

    @Test
    fun `null category fields collapse to safe defaults`() {
        val entity = CategoryDto(id = 1).toEntity()

        entity.title shouldBe ""
        entity.rank shouldBe 0
        entity.imageUrl shouldBe ""
        entity.hasImage shouldBe false
    }

    @Test
    fun `a category with an image object but no url still collapses cleanly`() {
        val entity = CategoryDto(id = 1, image = CategoryImageDto(url = null)).toEntity()

        entity.imageUrl shouldBe ""
        entity.hasImage shouldBe false
    }

    @Test
    fun `categories are sorted by rank, which the payload does not guarantee`() {
        val entities = listOf(
            CategoryDto(id = 1, title = "third", rank = 3),
            CategoryDto(id = 2, title = "first", rank = 1),
            CategoryDto(id = 3, title = "second", rank = 2),
        ).toCategoryEntities()

        entities.map { it.title } shouldBe listOf("first", "second", "third")
    }

    @Test
    fun `null question fields collapse to safe defaults`() {
        val entity = QuestionDto(id = 4).toEntity()

        entity.title shouldBe ""
        entity.subtitle shouldBe ""
        entity.imageUrl shouldBe ""
        entity.articleUrl shouldBe ""
        entity.order shouldBe 0
    }

    @Test
    fun `the snake_case image field is read into imageUrl`() {
        val entity = QuestionDto(id = 4, imageUri = "https://example.test/a.png").toEntity()

        entity.imageUrl shouldBe "https://example.test/a.png"
    }

    @Test
    fun `questions are sorted by order`() {
        val entities = listOf(
            QuestionDto(id = 1, title = "b", order = 2),
            QuestionDto(id = 2, title = "a", order = 1),
        ).toQuestionEntities()

        entities.map { it.title } shouldBe listOf("a", "b")
    }
}
