package io.github.shijianjs.ktjs.photoswipe

import io.github.shijianjs.ktjs.photoswipe.core.DataSource
import io.github.shijianjs.ktjs.photoswipe.core.ElementProvider
import io.github.shijianjs.ktjs.photoswipe.core.SlideData
import io.github.shijianjs.ktjs.photoswipe.core.photoSwipeOptions
import io.github.shijianjs.ktjs.photoswipe.core.slideData
import kotlin.js.JsAny
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import web.dom.document
import web.html.HTMLElement
import web.html.HtmlTagName

/**
 * Runs the wrapper against the real `photoswipe` package in Chromium.
 *
 * The lightbox reads its items out of ordinary DOM: each assertion below depends on a binding that
 * actually executed, so a wrapper wired to the wrong export fails instead of compiling quietly.
 */
class PhotoSwipeTest {
    private fun mountGallery(links: Int): HTMLElement {
        val gallery = document.createElement(HtmlTagName.div) as HTMLElement
        gallery.setAttribute("id", "gallery")
        (0 until links).forEach { index ->
            val anchor = document.createElement(HtmlTagName.a) as HTMLElement
            anchor.setAttribute("href", "photo-$index.jpg")
            anchor.setAttribute("data-pswp-width", "${800 + index}")
            anchor.setAttribute("data-pswp-height", "${600 + index}")
            gallery.appendChild(anchor)
        }
        document.body.appendChild(gallery)
        return gallery
    }

    // The lightbox reads `dataSource` (`{ gallery, items }`) and falls back to `childSelector` to
    // turn the anchors into items; `gallery`/`children` alone only drive the click binding.
    private fun optionsFor(gallery: HTMLElement) = photoSwipeOptions {
        this.gallery = ElementProvider(gallery)
        childSelector = "a"
        dataSource = DataSource(gallery, null)
    }

    @Test
    fun theLightboxReadsTheThumbnailsOutOfTheGallery() {
        val lightbox = createPhotoSwipeLightbox(optionsFor(mountGallery(3)))
        lightbox.init()

        assertEquals(3.0, lightbox.getNumItems())
        val first = lightbox.getItemData(0.0)
        // The lightbox resolves the anchor href against the document base, so only the tail of the
        // path is what this test controls.
        assertTrue(first.src!!.endsWith("photo-0.jpg"), "src: ${first.src}")
        assertEquals(800.0, first.width)
        assertEquals(600.0, first.height)

        lightbox.destroy()
    }

    @Test
    fun aFilterCanRewriteTheItemData() {
        val lightbox = createPhotoSwipeLightbox(optionsFor(mountGallery(2)))
        lightbox.addFilter(
            "itemData",
            { data: JsAny ->
                (data as SlideData).alt = "filtered"
                data
            },
            null,
        )
        lightbox.init()

        assertEquals("filtered", lightbox.getItemData(0.0).alt)
        lightbox.destroy()
    }

    @Test
    fun listenersCanBeRegisteredAndRemoved() {
        var calls = 0
        val listener: (JsAny) -> Unit = { calls++ }
        val lightbox = createPhotoSwipeLightbox(optionsFor(mountGallery(1)))
        lightbox.on("beforeOpen", listener)
        lightbox.off("beforeOpen", listener)
        lightbox.init()

        assertEquals(0, calls, "a removed listener is not called")
        lightbox.destroy()
    }

    @Test
    fun theCoreTakesADataSourceArray() {
        val core = createPhotoSwipe(
            photoSwipeOptions {
                dataSource = DataSource(
                    arrayOf(
                        slideData {
                            src = "photo-0.jpg"
                            width = 800.0
                            height = 600.0
                            alt = "first"
                        },
                        slideData {
                            src = "photo-1.jpg"
                            width = 1024.0
                            height = 768.0
                            alt = "second"
                        },
                    ),
                )
                bgOpacity = 0.9
                spacing = 0.1
                loop = true
                escKey = true
            },
        )

        assertEquals(2.0, core.getNumItems())
        assertEquals("second", core.getItemData(1.0).alt)
        assertEquals(0.0, core.currIndex, "the first slide is current before init")
        core.destroy()
    }

    @Test
    fun anUnsetOptionStaysAbsent() {
        val options = photoSwipeOptions {
            bgOpacity = 0.5
            loop = false
        }
        assertEquals(0.5, options.bgOpacity)
        assertEquals(false, options.loop)
        assertTrue(options.dataSource == null, "an option that was not set reads back as null")
    }
}
