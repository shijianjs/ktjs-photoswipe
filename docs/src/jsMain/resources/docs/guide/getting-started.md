---
sidebar_position: 2
title: Getting started
---

# Getting started

## Dependency

```kotlin
kotlin {
    js {
        binaries.executable()
        browser()
    }
    sourceSets {
        jsMain.dependencies {
            implementation("io.github.shijianjs:ktjs-photoswipe:5.4.4-kt.1")
            // The public API exposes `kotlin-browser`'s `web.*` types, so a consumer must be able to
            // compile against them.
            implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.11.1")
        }
    }
}
```

Kotlin 2.4.21 or newer; the module emits ESM and targets `es2015` or later.

## Register the plugins

There is no plugin registry: PhotoSwipe 5 has two modules and the lightbox needs **the core class** in its `pswpModule` option before it can open anything (upstream `preload()` throws `pswpModule is not valid` otherwise). The wrapper ships the loader for that:

```kotlin
import io.github.shijianjs.ktjs.photoswipe.createPhotoSwipeLightbox
import io.github.shijianjs.ktjs.photoswipe.photoSwipeModuleLoader

val lightbox = createPhotoSwipeLightbox(
    photoSwipeOptions {
        gallery = ElementProvider("#gallery")
        childSelector = "a"
        pswpModule = photoSwipeModuleLoader()
    },
)
lightbox.init()
```

`photoSwipeModuleLoader()` returns a function resolving the already-imported core module, which is the form upstream documents for ES modules.

## First call

```kotlin
import io.github.shijianjs.ktjs.photoswipe.createPhotoSwipeLightbox
import io.github.shijianjs.ktjs.photoswipe.core.DataSource
import io.github.shijianjs.ktjs.photoswipe.core.ElementProvider
import io.github.shijianjs.ktjs.photoswipe.core.photoSwipeOptions
import io.github.shijianjs.ktjs.photoswipe.photoSwipeModuleLoader

fun bindGallery(gallery: web.html.HTMLElement) {
    val lightbox = createPhotoSwipeLightbox(
        photoSwipeOptions {
            this.gallery = ElementProvider(gallery)
            childSelector = "a"
            // The lightbox reads `dataSource` to turn the anchors into slides.
            dataSource = DataSource(gallery, null)
            pswpModule = photoSwipeModuleLoader()
            bgOpacity = 0.9
        },
    )
    lightbox.on("change") { /* read lightbox.pswp?.currIndex */ }
    lightbox.init()
}
```

Each thumbnail is an `<a href="...">` carrying `data-pswp-width` and `data-pswp-height`; those two attributes are where PhotoSwipe reads the full-size dimensions.

## Build and test

```bash
./gradlew build                     # compile, Detekt, browser tests
./gradlew :ktjs-photoswipe:jsBrowserTest     # only the wrapper's browser tests
./gradlew :docs:docusaurusStart     # documentation dev server
./gradlew pagesSite                 # docs at /, demo at /demo/, Dokka at /api/
```
