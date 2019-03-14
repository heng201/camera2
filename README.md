# camera2
## 基本功能 ##
- camera2的使用，camera1的使用，连拍的实现，拍照预览，录像等等  
## 简介 ##
- app和camera2app下为camera2的使用 
- camera1app下为camera1的使用
- radio下为录像的实现和连拍的使用
- myfilter下为各种滤镜的实现

### camera1的连拍 ###
- 其中连拍的实现采用获取视频帧画面的方式

- 其中获取连拍的方法由于android提供的MediaMetadataRetriever不能准确的的获取相应时间节点的图片
所以采用GitHub上的一个获取方式：FFmpegMediaMetadataRetriever，地址为：`https://github.com/wseemann/FFmpegMediaMetadataRetriever`

后续会陆续更新camera2的详细使用：

### 2018.02.09更新 ###
- 添加使用camera2API实现手动焦距，ISO，AWB白平衡，缩放，曝光补偿和曝光时间的调节
- 实现camera2的连拍效果
- 消除连拍时预览卡住的情况
### 2018.02.09更新 ###
- 添加使用camera2实现对传感器的控制,虽然传感器下有HDR模式，但设置之后并没有什么效果，可能是手机不支持

### 2018.02.28更新 ###
- 实现camera2的连拍功能，采用camera2生成图片image的原生编码YUV_420888，之后将其转化为NV21，之后再转化为JPEG格式保存
- 获取图片的理想帧率是30FPS左右，不知道是不是手机不支持，每获取五张图片之后本来应该是33ms的间隔的，但是却飙升到150ms左右，具体原因尚不清楚

### 2018.10.10更新 ###
- 更新相关camera2的使用方法
- 添加各种android基础的例子，如aidl，调节屏幕亮度，通知栏显示通知等等

### 2019.03.14更新 ###
- 更新相关rxjava与retrofit的使用方法
- 设置界面的实现方法


# 交流邮箱 1689854162@qq.com#
