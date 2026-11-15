# ktjs-photoswipe

PhotoSwipe 5 的 Kotlin/JS 绑定 —— 图片画廊与灯箱

封装覆盖 `photoswipe` 5 整包：绑定缩略图点击的灯箱层、它打开的核心画廊、共用的事件与过滤器层、完整的带类型选项，以及数据类型（`SlideData`、`DataSource`、`ElementProvider`）。上游两个子路径都只有 default 导出，因此都绑定成 external object；又因为灯箱本身不含核心，`photoSwipeModuleLoader()` 负责提供上游要求的 `pswpModule`。

- 上游包：[`photoswipe`](https://github.com/dimsemenov/PhotoSwipe) 5.4.4
- 文档：https://shijianjs.github.io/ktjs-photoswipe/
- 演示：https://shijianjs.github.io/ktjs-photoswipe/demo/
- API：https://shijianjs.github.io/ktjs-photoswipe/api/
- 英文说明：[README.md](README.md)

## 用法

```kotlin
implementation("io.github.shijianjs:ktjs-photoswipe:5.4.4-kt.1")
```

## 构建

```bash
./gradlew build
./gradlew :ktjs-photoswipe:jsBrowserTest
./gradlew :demo:e2eTest
./gradlew pagesSite
```

## 许可证

wrapper 与上游 `photoswipe` 同为 MIT；版权声明见文档的「许可证」页。
