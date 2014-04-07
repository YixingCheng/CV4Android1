LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include ~/Workspace/OpenCV4Android/OpenCV-2.4.8-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := cv4android1_native
LOCAL_SRC_FILES := jni_part.cpp
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)