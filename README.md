## PictureSelector

我终于更新了，这是仿微信的图片选择器简化版，简化了编辑功能，就像知乎的图片选择器一样，提供的就是从手机的媒体库中选择文件功能。

### 功能

1、选择本地媒体库的图片和视频
2、拍照
3、媒体文件分页加载
4、图片可选是否支持gif
5、可自定义皮肤
6、可自定义文案
7、可自定义图片加载引擎

### 使用方式

#### 引用

1、在根目录的build.gradle中加入如下配置

```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

2、在要是用的module中增加如下引用

```
dependencies {
    ...
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'androidx.recyclerview:recyclerview:1.1.0'
    implementation 'com.github.chrisbanes:PhotoView:2.3.0'
    implementation 'com.github.arvinljw:PictureSelector:3.0.0'
    //例如demo中引入的是glide加载图片，所以引入glide
    implementation 'com.github.bumptech.glide:glide:4.11.0'
}
```

3、在AndroidManifest文件中配置FileProvider

```
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.ps.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/ps_file_paths" />
</provider>
```

#### 使用

首先需要申明，在调用拍照功能之前需要先申请拍照功能，在打开选择器之前需要申请文件读写权限。

对于权限申请库，没有好的可使用的话，可以使用我的[PermissionHelper](https://github.com/arvinljw/PermissionHelper)。

**初始化**

需要配置图片加载引擎：

```
public static void init(ImageEngine imageEngine)
```

以glide为例的实现方式如下：

```
imageEngine = new ImageEngine() {
    @Override
    public void loadImage(ImageView imageView, Uri uri) {
        Glide.with(imageView)
                .load(uri)
                .into(imageView);
    }
};
```

自定义文案配置，可不配置则使用默认的，配置方法如下：

```
public static void init(ImageEngine imageEngine, TextEngine textEngine)
```

也就是在初始化图片加载引擎的时候，同时传入文案引擎即可，不设置则使用DefaultTextEngine，如果需要适配多语言则需要自己定义文案引擎来实现。

**打开本地媒体库**

初始化完成之后，就可以打开本地媒体库了。

```
new SelectorHelper.Builder()
        .setChooseSize(9)//设置选择的数量，最小为1
        .setMediaType(MediaType.IMAGE)//设置选择的媒体类型
        .setStyle(R.style.PS_Customer)//设置自定义皮肤，参考demo
        .build()
        .forResult(MainActivity.this, 1001);//打开本地媒体库
```

**打开相机**

```
//TakePhotoUtil类中的下述方法
public static Uri takePhoto(Activity activity, MediaStorageStrategy storageStrategy, int requestCode)
```

其中MediaStorageStrategy是拍照时传入的照片存储策略，是否放到在公共的区域。

最后在onActivityResult接收打开本地媒体库和相机返回的数据：

```
SelectorHelper.getMediaDataFromIntent(data);//获取选择的本地媒体数据
SelectorHelper.getMediaIsOriginalImage(data);//获取是否是原图标识，如果不是原图，可根据返回的数据自己按需处理

//如果是放到自己应用下的私有文件，且不想公开让别的使用则直接返回takePhoto返回的uri即可
//如果是放到公共区域，可公开让别的应用使用的则使用下述方法扫描放入媒体库中，并根据返回结果使用即可
TakePhotoUtil.scanPath(this, TakePhotoUtil.getPhotoPath(), new MediaScanner.ScanCompletedCallback());
//扫描拍照回来的路径得到在媒体库中的真实uri，同时也为了打开媒体库时能开到改图片
```

其中ScanCompletedCallback回调回来是在子线程，可转换到主线程在加载图片。


**自定义皮肤**

```
<style name="PS.Customer" parent="PS.Default">
    <!--下边的所有属性都可以改变，几乎涵盖了该库的所有布局中用到的颜色-->
    <!--        <item name="colorPrimary">@color/ps_primary</item>-->
    <!--        <item name="colorPrimaryDark">@color/ps_primary_dark</item>-->
    <!--        <item name="titleColor">@color/ps_title_color</item>-->
    <!--        <item name="titleBg">@drawable/ps_bg_title</item>-->
    <!--        <item name="sendTextColorEnable">@color/ps_send_text_enable</item>-->
    <!--        <item name="sendTextColorDisable">@color/ps_send_text_disable</item>-->
    <!--        <item name="sendBg">@drawable/ps_bg_send</item>-->
    <!--        <item name="bottomBg">@color/ps_bottom_bg</item>-->
    <!--        <item name="listBg">@color/ps_list_bg</item>-->
    <!--        <item name="radioBg">@drawable/ps_item_select_bg</item>-->
    <!--        <item name="itemSelectTextColor">@color/ps_item_select_text_color</item>-->
    <!--        <item name="folderCountColor">@color/ps_folder_count_color</item>-->
    <!--        <item name="folderDivider">@color/ps_folder_divider_color</item>-->
    <!--        <item name="previewTitleColor">@color/ps_preview_title_color</item>-->
    <!--        <item name="previewBottomBg">@color/ps_preview_bottom_bg</item>-->
    <!--        <item name="bottomTextColor">@color/ps_bottom_text_color</item>-->
    <!--        <item name="bottomDivider">@color/ps_bottom_divider</item>-->
</style>
```

对于color的属性值就是替换成自己的颜色，而对于drawable的则需要参考原来的drawable替换drawable中的颜色。
对于上述属性，都是字面意思结合打开之后的ui对比，就能知道了，有点麻烦就暂时不挨着注释了。

### 感谢

这次的仿写微信是简略版本的，ui全是根据微信v7.0.17，自己切图实现。

加载数据方面参考了知乎的[Matisse](https://github.com/zhihu/Matisse)以及另一位的[PictureSelector](https://github.com/LuckSiege/PictureSelector)。

在此表示感谢。

### License

```
   Copyright 2020 arvinljw

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
