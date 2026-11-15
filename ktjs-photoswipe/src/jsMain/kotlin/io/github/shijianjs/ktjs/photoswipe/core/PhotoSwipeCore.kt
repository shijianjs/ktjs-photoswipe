package io.github.shijianjs.ktjs.photoswipe.core

import kotlin.js.JsAny
import web.html.HTMLDivElement

/**
 * The core gallery, upstream `class PhotoSwipe`.
 *
 * Upstream's `PhotoSwipe extends PhotoSwipeBase extends Eventable`. An `external` class cannot inherit
 * declared members from an external interface, so the inherited data and event members
 * ([getNumItems], [getItemData], [on], [off], [addFilter], [removeFilter], [applyFilters]) are
 * repeated here — they are the same JavaScript methods, and `core/PhotoSwipeBase.kt` documents the
 * shared shape.
 */
external class PhotoSwipeCore {
    /** The options the gallery runs with, defaults filled in. */
    val options: PhotoSwipeOptions

    /** The root element of the gallery, once it is open. */
    val element: HTMLDivElement?

    /** Index of the current slide. */
    val currIndex: Double

    /** Whether the gallery is open. */
    val isOpen: Boolean

    /** Viewport size of the gallery. */
    val viewportSize: Point

    /** Backdrop opacity. */
    val bgOpacity: Double

    /** Whether the user is on a device with a mouse. */
    val hasMouse: Boolean

    /** Total number of slides. */
    fun getNumItems(): Double

    /** Normalised item data of the slide at [index]. */
    fun getItemData(index: Double): SlideData

    /** Registers [listener] for the event named [name]. */
    fun on(name: String, listener: (JsAny) -> Unit)

    /** Removes a listener registered with [on]. */
    fun off(name: String, listener: (JsAny) -> Unit)

    /** Registers [fn] as a filter for [name]. */
    fun addFilter(name: String, fn: (JsAny) -> JsAny, priority: Double?)

    /** Removes a filter registered with [addFilter]. */
    fun removeFilter(name: String, fn: (JsAny) -> JsAny)

    /** Runs the filters of [name] over [args] and returns the first argument. */
    fun applyFilters(name: String, vararg args: JsAny): JsAny

    /** Appends the heavy (image) part of the DOM; the lightbox calls this after opening. */
    fun appendHeavy()

    /** Initialises the gallery on the DOM it was prepared for. */
    fun init(): Boolean

    /** The looped index of [index] (`-1` is the last slide). */
    fun getLoopedIndex(index: Double): Double

    /** Goes to the slide at [index]. */
    fun goTo(index: Double)

    /** Goes to the next slide. */
    fun next()

    /** Goes to the previous slide. */
    fun prev()

    /** Zooms the current slide to [destZoomLevel]. */
    fun zoomTo(destZoomLevel: Double, centerPoint: Point?, transitionDuration: JsAny?, ignoreBounds: Boolean?)

    /** Toggles between the current and the initial zoom level. */
    fun toggleZoom()

    /** Starts the closing transition; the gallery destroys itself when it ends. */
    fun close()

    /** Closes instantly, unbinds events and removes the elements from the DOM. */
    fun destroy()

    /** Re-loads the content of the slide at [slideIndex]. */
    fun refreshSlideContent(slideIndex: Double)

    /** Centre point of the viewport. */
    fun getViewportCenterPoint(): Point

    /** Re-measures the viewport and every element; [force] ignores the size cache. */
    fun updateSize(force: Boolean?)
}
