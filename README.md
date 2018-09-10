## PictureSelector

这是仿微信的图片选择器。

### 简介

1、来源：相机，本地图片媒体库中jpg和png类型的图片

2、功能：**多选、单选、拍照、预览、裁剪、大图、支持7.0**

3、TODO：增加**多选时图片编辑功能，视频选择功能**

### Demo下载

![](screenshot/ps_icon.png)

[PicSelector下载地址](app/PicSelector.apk)

![](screenshot/use_sample.png)

### 使用方式

#### 引用

**1、在根目录的build.gradle中加入如下配置**

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
        maven { url "https://dl.bintray.com/thelasterstar/maven/" }
    }
}
```

**2、在要是用的module中增加如下引用**

```
dependencies {
    ...
    api 'com.github.bumptech.glide:glide:4.4.0'
    api 'com.github.arvinljw:PictureSelector:v2.0.8'
}
```

*注：该库引用的第三方代码*

* v7
* recyclerview
* annotations
* exifinterface
* glide

前四个都是com.android.support下边的，版本是**27.1.1**，glide使用版本**4.4.0**

若是引用的包重复可使用类似这样使用

```
dependencies {
    ...
    api 'com.github.bumptech.glide:glide:4.4.0'
    api ('com.github.arvinljw:PictureSelector:v2.0.8'){
        exclude group: 'com.android.support'
    }
    api v7
    api recyclerview
    api annotations
    api exifinterface
}
```

**3、在AndroidManifest文件中添加权限以及必须配置**

**权限**

```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.CAMERA"/>
```

**配置**

```
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}.selector.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/ps_file_paths"/>
</provider>
<activity
    android:name="net.arvin.selector.uis.SelectorActivity"
    android:screenOrientation="portrait"
    android:theme="@style/TransparentTheme"/>
```

#### 使用

本库依然采用外观模式提供了一个[SelectorHelper](selectorlibrary/src/main/java/net/arvin/selector/SelectorHelper.java)，里边有一系列的静态方法去启动选择器，只是传递参数不同而已。

但是到最后都调用一个方法，所以只需要告诉大家这一个方法及其参数就能够比较好的使用了。

```
/**
 * 去选择图片或视频
 *
 * @param type             可选值{@link #VALUE_TYPE_PICTURE}{@link #VALUE_TYPE_VIDEO}{@link #VALUE_TYPE_PICTURE_VIDEO}{@link #VALUE_TYPE_CAMERA}
 * @param singleSelection  是否单选
 * @param canCrop          是否裁剪
 * @param maxCount         最大数量
 * @param withCamera       是否带相机
 * @param selectedPictures 已选中图片
 * @param selectedVideos   已选中视频
 */
private static void select(Activity activity, int type, boolean singleSelection, boolean canCrop, int maxCount, boolean withCamera,
                           ArrayList<String> selectedPictures, ArrayList<String> selectedVideos, int requestCode) {
    Intent intent = new Intent(activity, SelectorActivity.class);
    Bundle bundle = new Bundle();
    bundle.putInt(KEY_TYPE_SELECT, type);
    bundle.putBoolean(KEY_SINGLE_SELECTION, singleSelection);
    bundle.putBoolean(KEY_CAN_CROP, canCrop);
    bundle.putInt(KEY_MAX_COUNT, maxCount);
    bundle.putBoolean(KEY_WITH_CAMERA, withCamera);
    if (selectedPictures != VALUE_SELECTED_PICTURES_NULL) {
        bundle.putStringArrayList(KEY_SELECTED_PICTURES, selectedPictures);
    }
    if (selectedVideos != VALUE_SELECTED_VIDEOS_NULL) {
        bundle.putStringArrayList(KEY_SELECTED_VIDEOS, selectedVideos);
    }
    bundle.putString(KEY_AUTHORITIES, PSUtil.getAuthorities(activity));
    intent.putExtras(bundle);
    activity.startActivityForResult(intent, requestCode);
}
```

其实方法贴出来，也有注释就不用多说大家都明白了。**这里暂时还只支持选择图片。**

```
/**
 * 拍照，是否裁剪
 */
public static void takePhoto(Activity activity, boolean canCrop, int requestCode) {
    select(activity, VALUE_TYPE_CAMERA, VALUE_SINGLE_SELECTION_TRUE, canCrop, VALUE_COUNT_SINGLE,
            VALUE_WITH_CAMERA_TRUE, VALUE_SELECTED_PICTURES_NULL, VALUE_SELECTED_PICTURES_NULL, requestCode);
}
/**
 * 单选图片，不裁剪，带相机
 */
public static void selectPicture(Activity activity, int requestCode) {
    selectPicture(activity, VALUE_CAN_CROP_FALSE, VALUE_WITH_CAMERA_TRUE, requestCode);
}
/**
 * 单选图片
 *
 * @param canCrop    选择是否裁剪
 * @param withCamera 选择是否带相机
 */
public static void selectPicture(Activity activity, boolean canCrop, boolean withCamera, int requestCode) {
    select(activity, VALUE_TYPE_PICTURE, VALUE_SINGLE_SELECTION_TRUE, canCrop, VALUE_COUNT_SINGLE, withCamera,
            VALUE_SELECTED_PICTURES_NULL, VALUE_SELECTED_VIDEOS_NULL, requestCode);
}
/**
 * 多选图片，带相机
 *
 * @param maxCount 多选的最大数量
 */
public static void selectPictures(Activity activity, int maxCount, int requestCode) {
    selectPictures(activity, maxCount, VALUE_WITH_CAMERA_TRUE, VALUE_SELECTED_PICTURES_NULL, requestCode);
}
/**
 * 多选图片
 *
 * @param maxCount         多选的最大数量
 * @param withCamera       选择是否带相机
 * @param selectedPictures 已选中的图片
 */
public static void selectPictures(Activity activity, int maxCount, boolean withCamera, ArrayList<String> selectedPictures, int requestCode) {
    select(activity, VALUE_TYPE_PICTURE, VALUE_SINGLE_SELECTION_FALSE, VALUE_CAN_CROP_FALSE, maxCount, withCamera,
            selectedPictures, VALUE_SELECTED_VIDEOS_NULL, requestCode);
}
```

也就是说这四个静态方法是可以使用的，多选时暂时不支持裁剪，之后会想微信一样增加编辑功能。

最后在onActivityResult中可以获得选中的图片，当然顺序是选择图片时的顺序。

```
ArrayList<String> backPics = data.getStringArrayListExtra(ConstantData.KEY_BACK_PICTURES);
```
这样就获取到选中的图片了，不管单选多选都这样，只是单选就只有一张。

当然6.0以上的访问相机和本地文件的权限需要自己去实现，demo中也提供了一种方式，仅供参考。

若是有什么问题，希望不吝赐教共同进步～

### License

```
   Copyright 2016 arvinljw

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
