# ymage

Yitimo's image libs for Android. Include:

1. Image picker 相册图片选择器
2. Image browser 基于ViewPager的图片滑动浏览
3. Image grider 信息流九宫格图片组件
4. Image cutter 简单图片裁剪组件

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
