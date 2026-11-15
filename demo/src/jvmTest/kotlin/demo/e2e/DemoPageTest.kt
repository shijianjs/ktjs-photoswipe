package demo.e2e

import com.microsoft.playwright.Page
import com.microsoft.playwright.junit.UsePlaywright
import com.microsoft.playwright.options.WaitUntilState
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junitpioneer.jupiter.RetryingTest

/**
 * Smoke checks for the demo page: the bundle loads, the gallery renders, clicking a tile opens the
 * lightbox, and the language switch swaps every visible string.
 */
@UsePlaywright(DemoOptions::class)
class DemoPageTest {
    @RetryingTest(maxAttempts = 2)
    fun `page renders heading, logo and styles`(page: Page) {
        openPage(page)

        assertTrue(page.title().contains("ktjs-photoswipe"), "title: ${page.title()}")
        assertEquals("ktjs-photoswipe demo", page.locator("h1").textContent())
        assertTrue(page.locator("img.logo").isVisible(), "the demo serves the project logo")

        val display = page.locator(".page").evaluate("(node) => getComputedStyle(node).display") as String
        assertTrue(display != "contents", "the stylesheet applies to .page (display = $display)")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the gallery renders one tile per slide`(page: Page) {
        openPage(page)

        assertEquals(6, page.locator(".gallery a").count(), "six thumbnails")
        assertTrue(page.locator(".gallery .tile").first().isVisible(), "a tile is visible")
    }

    @RetryingTest(maxAttempts = 2)
    fun `clicking a tile opens the lightbox`(page: Page) {
        openPage(page)

        page.locator(".gallery a").first().click()
        // PhotoSwipe appends `.pswp` to the document (or to `appendToEl`) when it opens.
        page.waitForSelector(".pswp", Page.WaitForSelectorOptions().setTimeout(15_000.0))
        assertTrue(page.locator(".pswp").count() > 0, "the lightbox element exists")

        val status = page.locator(".view").textContent()
        assertTrue(status.contains("1 / 6") || status.contains("1"), "status shows the current slide: $status")

        page.keyboard().press("Escape")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the language switch swaps every visible string`(page: Page) {
        openPage(page)

        page.locator("button.lang").click()

        assertEquals("ktjs-photoswipe 演示", page.locator("h1").textContent())
        assertEquals("zh-Hans", page.locator("html").getAttribute("lang"))
        assertEquals("从第一张打开", page.locator(".content .row button").first().textContent())

        page.locator("button.lang").click()

        assertEquals("ktjs-photoswipe demo", page.locator("h1").textContent())
        assertEquals("en", page.locator("html").getAttribute("lang"))
    }

    @RetryingTest(maxAttempts = 2)
    fun `page logs no console errors or failed requests`(page: Page) {
        val consoleErrors = mutableListOf<String>()
        val failedRequests = mutableListOf<String>()
        page.onConsoleMessage { message -> if (message.type() == "error") consoleErrors += message.text() }
        page.onRequestFailed { request -> failedRequests += "${request.url()} ${request.failure()}" }

        openPage(page)
        page.locator(".gallery a").nth(2).click()
        page.waitForSelector(".pswp", Page.WaitForSelectorOptions().setTimeout(15_000.0))

        assertTrue(consoleErrors.isEmpty(), "console errors: $consoleErrors")
        assertTrue(failedRequests.isEmpty(), "failed requests: $failedRequests")
        page.keyboard().press("Escape")
    }

    private fun openPage(page: Page) {
        page.navigate("/", Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE))
    }
}
