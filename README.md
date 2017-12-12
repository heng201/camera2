# camera2

camera2的使用，camera1的使用，连拍的实现，拍照预览，录像等等  

app下为camera2的使用 ，camera1下为camera1的使用，radio下为录像的实现和连拍的使用。

其中连拍的实现采用获取视频帧画面的方式

其中获取连拍的方法由于android提供的MediaMetadataRetriever不能准确的的获取相应时间节点的图片
所以采用GitHub上的一个获取方式：FFmpegMediaMetadataRetriever，地址为：https://github.com/wseemann/FFmpegMediaMetadataRetriever

后续会陆续更新camera2的详细使用
