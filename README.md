# ymage

Yitimo's image libs for Android. Include:

1. Image picker 相册图片选择器
2. Image browser 基于ViewPager的图片滑动浏览
3. Image grider 信息流九宫格图片组件
4. Image cutter 简单图片裁剪组件


To run my sample: ``clone the repo and run the sample app``

To import the lib into your project, add the following line to your app's gradle file:

```
implementation 'com.yitimo.ymage:ymage:1.3.9'
```

Then you should add some code to your project's ``BaseApplication`` or any other app-init file as below:

```
// for ymage over v1.3.7
Ymager.debug = true // to enable debug mode
Ymager.browserClickBack = true // to enable browser close by single click
Ymager.setTheme(R.style.MyYmage) // set custom theme color for ymage, remove to use default

// glide engine code for bitmap load
Ymager.loadBitmap = fun (context: Context, url: String, holderRes: Int, callback: (Bitmap) -> Unit) {
    GlideApp.with(context)
            .asBitmap()
            .load(url)
            .placeholder(holderRes)
            .error(holderRes)
            .listener(object : RequestListener<Bitmap> {
                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (resource == null) {
                        return false
                    }
                    callback(resource)
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    return false
                }
            }).submit()
}
// glide engine code for file load
Ymager.loadFile = fun (context: Context, url: String, holderRes: Int, callback: (File) -> Unit) {
    GlideApp.with(context)
            .asFile()
            .load(url)
            .placeholder(holderRes)
            .error(holderRes)
            .listener(object : RequestListener<File> {
                override fun onResourceReady(resource: File?, model: Any?, target: Target<File>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (resource == null) {
                        return false
                    }
                    callback(resource)
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<File>?, isFirstResource: Boolean): Boolean {
                    return false
                }
            }).submit()
}
// glide engine code for bitmap load with size
Ymager.loadLimitBitmap = fun (context: Context, url: String, holderRes: Int, size: Pair<Int, Int>, callback: (Bitmap) -> Unit) {
    GlideApp.with(context)
            .asBitmap()
            .load(url)
            .override(size.first, size.second)
            .placeholder(holderRes)
            .error(holderRes)
            .listener(object : RequestListener<Bitmap> {
                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (resource == null) {
                        return false
                    }
                    callback(resource)
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    return false
                }
            }).submit()
}
// glide engine code for grid image
Ymager.setGridItem = fun (context: Context, imageView: ImageView, src: String, size: Int, fade: Int, holderRes: Int) {
    GlideApp.with(context)
            .load(src)
            .error(holderRes)
            .transition(DrawableTransitionOptions.withCrossFade(fade))
            .override(size, size)
            .centerCrop()
            .into(imageView)
}
// glide engine code for gif
Ymager.setGif = fun (context: Context, iv: ImageView, url: String, holderRes: Int) {
    GlideApp.with(context)
            .load(url)
            .placeholder(holderRes)
            .error(holderRes)
            .centerInside()
            .into(iv)
}
// glide engine for pause
Ymager.pauseGlide = fun (context: Context) {
    GlideApp.with(context).pauseRequests()
}
// glide engine for resume
Ymager.resumeGlide = fun (context: Context) {
    GlideApp.with(context).resumeRequests()
}
```

For version before v1.3.7 can get code [here](https://github.com/yitimo/ymage/blob/2ded3728d0a1b3434d438f1d1e0402313cead702/sample/src/main/java/com/yitimo/ymage/sample/BaseApplication.kt).

## Picker

1. Thumb list with 4 columns grid layout
2. Album switching support (group by ``Bucket``)
3. Single photo review, support scale, drag to switch

### how to use

``` kotlin
Ymager.pick(activity, limitCount, showCamera, lastChosen)
```

### available features

1. Custom theme color
2. Chosen history support (parse ``Ymage`` list in and set them chosen)
3. Light/dark built-in theme support

### in coming features

1. Custom nav bar support. (can still use themed colors&values)
2. Multi built-in nav bar support.

### Todo features

1. Photo database improvement.
2. More customizable properties.

## Browser

1. Image list browser base on DialogFragment and ViewPager
2. Long tap & single tap listener support
3. Long image and GIF support

### how to use

``` kotlin
val dialog = Ymager.browse(fragmentManager, list, index)
dialog.setOnLongClickListener { src, index ->
    // todo
}
```

## Grider

1. Different effect for single/double/multi images.
2. Automatically add tag for GIF and long image.

### how to use

``` xml
<com.yitimo.ymage.grider.YmageGridView
    android:id="@+id/grider_single"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:itemSpace="@dimen/dimenSpace"/>
```

``` kotlin
val singleYGV = findViewById(R.id.grider_single)
singleYGV.limit = Resources.getSystem().displayMetrics.widthPixels.toFloat()
singleYGV.itemSpace = 30f
singleYGV.items = list
singleYGV.setOnImageClickListener {
    Ymager.browse(this, it, list)
}
```

## Cutter

1. Use as custom view, simple to import
2. Rotate and scale support

### how to use

```
<com.yitimo.ymage.cutter.YmageCutterView
    android:id="@+id/cutter_edit_body"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    app:ratio="4:3"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintBottom_toBottomOf="parent"/>
```

```
val bodyYCV = findViewById(R.id.cutter_edit_body)
val result: Bitmap? = bodyYCV.shutter()
```
