# ktjs-photoswipe (module)

Kotlin/JS wrapper for [`photoswipe`](https://www.npmjs.com/package/photoswipe) 5.4.4.

One npm package with two entry points (`photoswipe` and `photoswipe/lightbox`), one Gradle module, one
Kotlin package (`io.github.shijianjs.ktjs.photoswipe`) with the `core` sub-package.

## Installation

```kotlin
dependencies {
    implementation("io.github.shijianjs:ktjs-photoswipe:5.4.4-kt.1")
}
```

## Usage

```kotlin
import io.github.shijianjs.ktjs.photoswipe.createPhotoSwipeLightbox
import io.github.shijianjs.ktjs.photoswipe.core.DataSource
import io.github.shijianjs.ktjs.photoswipe.core.ElementProvider
import io.github.shijianjs.ktjs.photoswipe.core.photoSwipeOptions
import io.github.shijianjs.ktjs.photoswipe.photoSwipeModuleLoader

val lightbox = createPhotoSwipeLightbox(
    photoSwipeOptions {
        gallery = ElementProvider(galleryElement)
        childSelector = "a"
        dataSource = DataSource(galleryElement, null)
        pswpModule = photoSwipeModuleLoader()
        bgOpacity = 0.9
    },
)
lightbox.init()
```

- `pswpModule` is mandatory: the `photoswipe/lightbox` bundle does not contain the core gallery, and
  upstream throws `pswpModule is not valid` at the first click without it.
- `dataSource` (`{ gallery, items? }`) is what `getNumItems()` and `getItemData()` read; `gallery` +
  `childSelector` alone only bind the click handler.
- Each thumbnail is an `<a>` with `data-pswp-width` / `data-pswp-height`; the stylesheet is
  `photoswipe/dist/photoswipe.css` and has to be included by the page.

## Build and test

```bash
./gradlew :ktjs-photoswipe:jsBrowserTest
```

After adding or upgrading an npm dependency, refresh the shared lock file with the official registry
(the lock file is committed, and CI has no mirror):

```bash
./gradlew --stop
$env:npm_config_registry='https://registry.npmjs.org'
./gradlew kotlinUpgradePackageLock
```

## License

MIT, same as the upstream `photoswipe` package (Copyright (c) 2014-2022 Dmitry Semenov).
