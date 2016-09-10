# PictureSelector
这是一个仿微信的图片选择器

## Features
1、图片来源：拍照，本地jpg和png类型的图片

2、功能：多选，单选，图片预览，裁剪

3、todo：使用MVP模式重构代码、优化细节（一些动画）、整理解析文档

## Usage
1、引用Lib

下载Lib通过引用Module方式引用

2、启动PictureSelector：

```
private final int REQUEST_CODE_1 = 1001;
private ArrayList<ImageEntity> selectedImages;
...
PSConfigUtil.getInstance().showSelector(MainActivity.this, REQUEST_CODE_1, selectedImages);
```

3、接收选择的图片数据：

```
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_1:
                    selectedImages.clear();
                    List<ImageEntity> temp = data.getParcelableArrayListExtra(PSConstanceUtil.PASS_SELECTED);
                    selectedImages.addAll(temp);
                    for (ImageEntity selectedImage : selectedImages) {
                        Log.d("back_data", selectedImage.getPath());
                    }
                    break;
            }
        }
    }
```

4、清除图片缓存(删除)

* 清除裁剪图片：

```
PSCropUtil.clear();
```
* 清除拍照图片

```
PSTakePhotoUtil.clear();
```

* 清除所有

```
PSConfigUtil.clearCache();
```

## Thanks
* 图片预览在PhotoView的基础上做了一定的修改；
* 图片裁剪使用了鸿洋大神的[仿微信头像裁剪](http://blog.csdn.net/lmj623565791/article/details/39761281),加载和裁剪算法并做了一定的修改。
* 图片加载使用Glide
* 事件传递使用Eventbus
* 异步操作使用了Rxjava和Rxandroid

在此表示感谢！


## License
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