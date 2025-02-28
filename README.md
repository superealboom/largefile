> 大文件上传模块

1. `http://localhost:8010/file/index` 文件上传地址
2. `largeFile/src/main/resources/largeFile` 文件夹存放文件
3. `largeFile/uploadChunks` 文件夹存放分割的块
4. 分割块大小前端默认`chunkSize 1M` ，异步上传，后台组装通过分布式锁合并文件
5. 支持分割上传，断点续传，续传根据文件名判断

![image-20241014162208184](assets/image-20241014162208184.png)

有期可优化拓展：

1. 根据文件大小动态变化`chunkSize`
2. 文件上传目录可更改 oss
3. 根据文件名判断文件可更改置根据文件的md5值等判断
4. 增加数据库表记录统计数据，文件位置，块详情
5. 页面可以增加块上传进度
6. 也可将文件分割放到后台使用多线程来做