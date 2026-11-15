# ktjs-photoswipe

Kotlin/JS bindings for PhotoSwipe 5 — image gallery and lightbox

The wrapper covers the whole `photoswipe` 5 package: the lightbox layer that binds thumbnail clicks, the core gallery it opens, the shared event and filter layer, the full set of typed options, and the data types (`SlideData`, `DataSource`, `ElementProvider`). Both upstream sub-paths only have a default export, so they are bound as external objects; and because the lightbox ships without the core, `photoSwipeModuleLoader()` supplies the `pswpModule` upstream demands.

- Upstream package: [`photoswipe`](https://github.com/dimsemenov/PhotoSwipe) 5.4.4
- Docs: https://shijianjs.github.io/ktjs-photoswipe/
- Demo: https://shijianjs.github.io/ktjs-photoswipe/demo/
- API: https://shijianjs.github.io/ktjs-photoswipe/api/
- 中文说明：[README.zh-CN.md](README.zh-CN.md)

## Usage

```kotlin
implementation("io.github.shijianjs:ktjs-photoswipe:5.4.4-kt.1")
```

## Build

```bash
./gradlew build
./gradlew :ktjs-photoswipe:jsBrowserTest
./gradlew :demo:e2eTest
./gradlew pagesSite
```

## License

The wrapper is MIT, same as the upstream `photoswipe` — see the "License" page of the docs.
