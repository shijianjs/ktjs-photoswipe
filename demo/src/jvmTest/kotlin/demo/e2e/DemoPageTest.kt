package demo.e2e

import com.microsoft.playwright.Page
import com.microsoft.playwright.junit.UsePlaywright
import com.microsoft.playwright.options.WaitUntilState
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junitpioneer.jupiter.RetryingTest

/**
 * Smoke checks for the demo page: the shell comes from the kit, the gallery renders one tile per
 * slide, a tile opens the lightbox, and the language switch swaps every visible string.
 *
 * The kit's shell lives in a shadow root; Playwright's CSS pierces shadow boundaries, so the
 * selectors here are the same ones a browser-side probe would use.
 */
@UsePlaywright(DemoOptions::class)
class DemoPageTest {
    @RetryingTest(maxAttempts = 2)
    fun `page renders the shell, the logo and an overview`(page: Page) {
        openPage(page)

        assertTrue(page.title().contains("ktjs-photoswipe"), "title: ${page.title()}")
        assertTrue(page.locator("img.layout__logo").isVisible(), "the shell shows the project logo")
        assertTrue(
            page.locator("vaadin-side-nav-item[data-route='overview']").isVisible(),
            "the sidebar lists the overview route",
        )
        assertTrue(
            page.locator("ktjs-demo-page h1").textContent().contains("Overview"),
            "the overview page is mounted",
        )
    }

    @RetryingTest(maxAttempts = 2)
    fun `the gallery renders one tile per slide`(page: Page) {
        openPage(page)
        goTo(page, "gallery")

        assertEquals(6, page.locator("[data-role='tile']").count(), "six thumbnails")
        assertTrue(page.locator("[data-role='tile-body']").first().isVisible(), "a tile is visible")
        assertEquals(6, page.locator("[data-role='tile-body'] span").count(), "one label per tile")
    }

    @RetryingTest(maxAttempts = 2)
    fun `clicking a tile opens the lightbox and tracks the slide`(page: Page) {
        openPage(page)
        goTo(page, "gallery")

        page.locator("[data-role='tile']").first().click()
        // PhotoSwipe appends its root to the page when it opens.
        page.waitForSelector(".pswp", Page.WaitForSelectorOptions().setTimeout(15_000.0))
        assertTrue(page.locator(".pswp").count() > 0, "the lightbox element exists")

        val status = page.locator("[data-status]").textContent()
        assertTrue(status.contains("1 / 6"), "status shows the current slide: $status")

        page.keyboard().press("Escape")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the open button starts at the first slide`(page: Page) {
        openPage(page)
        goTo(page, "gallery")

        page.locator("[data-action='open']").click()
        page.waitForSelector(".pswp", Page.WaitForSelectorOptions().setTimeout(15_000.0))

        val status = page.locator("[data-status]").textContent()
        assertTrue(status.contains("1 / 6"), "opening from the button lands on slide 1: $status")

        page.keyboard().press("Escape")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the language switch swaps every visible string`(page: Page) {
        openPage(page)
        goTo(page, "gallery")

        page.locator("[data-menu='lang']").first().click()
        page.locator("[data-action='lang:zh-Hans']").click()

        assertEquals("zh-Hans", page.locator("html").getAttribute("lang"))
        assertEquals("画廊", page.locator("vaadin-side-nav-item[data-route='gallery']").textContent())
        assertEquals("从第一张打开", page.locator("[data-action='open']").textContent())
        assertTrue(
            page.locator("[data-role='tile-body'] span").first().textContent().contains("薄荷"),
            "tile labels follow the language",
        )

        page.locator("[data-menu='lang']").first().click()
        page.locator("[data-action='lang:en']").click()

        assertEquals("en", page.locator("html").getAttribute("lang"))
        assertEquals("Gallery", page.locator("vaadin-side-nav-item[data-route='gallery']").textContent())
    }

    @RetryingTest(maxAttempts = 2)
    fun `page logs no console errors or failed requests`(page: Page) {
        val consoleErrors = mutableListOf<String>()
        val failedRequests = mutableListOf<String>()
        page.onConsoleMessage { message -> if (message.type() == "error") consoleErrors += message.text() }
        page.onRequestFailed { request -> failedRequests += "${request.url()} ${request.failure()}" }

        openPage(page)
        goTo(page, "gallery")
        page.locator("[data-role='tile']").nth(2).click()
        page.waitForSelector(".pswp", Page.WaitForSelectorOptions().setTimeout(15_000.0))

        assertTrue(consoleErrors.isEmpty(), "console errors: $consoleErrors")
        assertTrue(failedRequests.isEmpty(), "failed requests: $failedRequests")
        page.keyboard().press("Escape")
    }

    /** The shell routes on the hash; a click on the sidebar is the way a visitor gets there. */
    private fun goTo(page: Page, route: String) {
        page.locator("vaadin-side-nav-item[data-route='$route']").click()
        page.waitForSelector("ktjs-demo-page h1")
    }

    private fun openPage(page: Page) {
        page.navigate("/", Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE))
    }
}
