# LockaxialQQ
AndroidEx


##给app签名##

- 做了静默升级，使用到了系统权限，需要在安装前给app签上系统权限，才能安装；
- 拷贝signapk目录下的文件到电脑
- Android studio -> Build -> Build APK(s) -> 得到a.apk
- 使用 **java -jar signapk.jar platform.x509.pem platform.pk8 a.apk b.apk**对app签名；b.apk为签名后带系统权限的app；