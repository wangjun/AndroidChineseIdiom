/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_hu_andun7z_AndUn7z */

#ifdef __cplusplus
extern "C" {
#endif

#include "JniWrapper.h"

#define NDK_FUNC(func) Java_com_hu_andun7z_AndUn7z_##func

JNIEXPORT jint JNICALL NDK_FUNC(un7zip)(JNIEnv *env, jclass thiz, jstring filePath, jstring outPath) {
	const char* cfilePath = (const char*) env->GetStringUTFChars(filePath, NULL);
	const char* coutPath = (const char*) env->GetStringUTFChars(outPath, NULL);
	LOGD("start extract filePath[%s], outPath[%s]", cfilePath, coutPath);
	jint ret = extract7z(cfilePath, coutPath);
	LOGD("end extract");
	env->ReleaseStringUTFChars(filePath, cfilePath);
	env->ReleaseStringUTFChars(outPath, coutPath);
	return ret;
}

#ifdef __cplusplus
}
#endif
