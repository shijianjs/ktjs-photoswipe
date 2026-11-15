package io.github.shijianjs.ktjs.photoswipe.core

import kotlin.js.JsAny

/**
 * The data and event layer shared by [PhotoSwipeCore] and [PhotoSwipeLightbox], upstream
 * `PhotoSwipeBase extends Eventable`.
 *
 * Both classes repeat these members rather than inheriting them: an `external` class cannot take
 * declared members from an external interface, and the compiler rejects it. This interface exists so
 * the shared shape is written down once — a function can still accept `PhotoSwipeBase` when it only
 * needs to register listeners.
 */
external interface PhotoSwipeBase {
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

    /** Runs the filters of [name] over [args] and returns the first argument. */
    fun applyFilters(name: String, vararg args: JsAny): JsAny
}
