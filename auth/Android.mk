LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_RESOURCE_DIR := \
        $(LOCAL_PATH)/res

LOCAL_PACKAGE_NAME := GoogleAuthSample
LOCAL_SDK_VERSION := 17

LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_THIRD_PARTY_PACKAGE)
