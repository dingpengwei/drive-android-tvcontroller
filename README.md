realtime-android-tvcontroller
  [![Build Status](https://travis-ci.org/dingpengwei/drive-android-tvcontroller.svg?branch=master)](https://travis-ci.org/dingpengwei/drive-android-tvcontroller)
==================


## Build from source and run

### Pre-requisites
- [JDK 6+](https://jdk8.java.net/download.html)
- [Android SDK](http://developer.android.com/sdk/index.html)
- [Apache Maven](http://maven.apache.org/download.html)
- [Git](https://help.github.com/articles/set-up-git)

### Check out sources and run the app with Maven
```bash
git clone https://github.com/dingpengwei/drive-android-tvcontroller.git
cd realtime-android-tvcontroller
export ANDROID_HOME=$your-android-skd-directory/android-sdk-linux
export PATH=${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools
mvn clean package android:deploy android:run
```
