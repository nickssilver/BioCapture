#ifndef JNI_TEST_TPAPI_LIB_H
#define JNI_TEST_TPAPI_LIB_H

#include <jni.h>
#include <string>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_SetFingersBox(JNIEnv *, jobject, jboolean);

JNIEXPORT jlong JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_InitializeAPI(JNIEnv *, jobject);

JNIEXPORT jlong JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_TerminateAPI(JNIEnv *, jobject);

JNIEXPORT jobjectArray JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_GetAndroidDeviceList(JNIEnv *, jobject);

JNIEXPORT jlong JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_OpenDeviceSN(JNIEnv *, jobject, jobject);

JNIEXPORT jlong JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_CloseDevice(JNIEnv *, jobject, jint);

JNIEXPORT jboolean JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_DeviceConnected(JNIEnv *, jobject, jint);

JNIEXPORT jlong JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_GetDeviceInfo(JNIEnv *, jobject, jint, jobject);

JNIEXPORT jlong JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_GetMaxVideoSize(JNIEnv *, jobject, jint, jobject, jobject);

JNIEXPORT jlong JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_CaptureControl(JNIEnv *env, jobject obj, jint jhdl, jobject jcapControl);

JNIEXPORT jlong JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_StartPreview(JNIEnv *, jobject, jint, jobject, jint, jshort, jshort, jstring);

#ifdef __cplusplus
}
#endif

#endif //JNI_TEST_TPAPI_LIB_H
