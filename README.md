# LockaxialQQ
AndroidEx


#### 版本说明

    - com.androidex.config.USER_ID定义了用户类型；
    - A00000X为格式针对蓝牙用户，单独定义的；A000000为通用用户类型；
    - 在module下的build 标记versionName，V.T为通用版本，V.X单独针对蓝牙用户；

#### 签名文件 ##

    - 路径：LockaxialQQ\signapk\androidex.jks
    - 密码：123456，别名：androidex

#### 对目标apk文件签名

            通过命令行切换到该目录我的路径F:\signapk

            先从C盘切换到F盘命令"F:"

            然后进入到存放签名和APP文件目录命令："cd signapk"

            然后对目标apk文件签名，我需要签名的apk的文件名："a.apk"，签名后生成的apk命名为"b.apk"，命令如下：

            "java -jar signapk.jar platform.x509.pem platform.pk8 a.apk b.apk"

            这样就生成了拥有系统权限的App，可以通过SilentInstall.install(String path) 进行安装。
