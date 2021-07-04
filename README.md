# 记录与分享灵感的应用

## 展示

![](showPicture/Recorder.gif)
<!-- <img src="showPicture/REcorder.git?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Nhc2VtYXRl,size_16,color_FFFFFF,t_70#pic_left" width="70%"> -->
## 使用技术

- 使用 RecyclerView 展示记录列表
- 使用 ViewPager + Fragment 实现详细信息与编辑界面，支持滑动切换下一条记录、快速切换到首条和最后一条
- 使用 DialogFragment 修改时间和日期
- 使用 ContentResolver 获取本机通讯录
- 使用隐式 Intent 调取相机拍照，并使用 FileProvider 获取拍摄的照片
- 使用 SQLite 存储记录，并使用原生类操作数据库
- 适配横屏，同时展示列表与详细信息两个界面，并同步刷新数据
