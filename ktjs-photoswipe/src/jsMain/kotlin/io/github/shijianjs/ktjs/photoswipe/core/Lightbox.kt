package io.github.shijianjs.ktjs.photoswipe.core

import kotlin.js.JsAny

/**
 * The lightbox layer, upstream `class PhotoSwipeLightbox`.
 *
 * It binds clicks on thumbnails, reads their sizes and then opens a [PhotoSwipeCore]; the core is
 * created lazily on the first click, or eagerly when [loadAndOpen] is called.
 *
 * As with [PhotoSwipeCore], the members upstream inherits from `PhotoSwipeBase` are repeated here:
 * an `external` class cannot inherit them from an external interface.
 */
external class PhotoSwipeLightbox {
    /** The options the lightbox runs with. */
    val options: PhotoSwipeOptions

    /** The core gallery, `null` until it is opened for the first time. */
    val pswp: PhotoSwipeCore?

    /** Whether the next click opens the gallery. */
    val shouldOpen: Boolean

    /** Total number of slides the lightbox found. */
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

    /**
     * Binds the DOM listeners and reads the gallery items.
     *
     * Upstream keeps this out of the constructor on purpose: it lets callers register filters and
     * event listeners first, and the wrapper preserves that order.
     */
    fun init()

    /** The index of the thumbnail the click event [event] belongs to. */
    fun getClickedIndex(event: JsAny): Double

    /** Loads the core module and opens the gallery at [index]. */
    fun loadAndOpen(index: Double, dataSource: DataSource?, initialPoint: Point?): Boolean

    /** Loads the slide at [index] without opening the gallery. */
    fun preload(index: Double, dataSource: DataSource?)

    /** Unbinds every listener and closes the gallery if it is open. */
    fun destroy()
}
