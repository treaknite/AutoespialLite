#include<jni.h>

JNIEXPORT jstring JNICALL
Java_com_treaknite_autoespiallite_Result_getTokenKey(JNIEnv *env, jclass instance) {
    return (*env)->NewStringUTF(env, "ZDM4ZTVmMzEtM2I5Yy00YzAzLTkwMGMtYWQ2ZDE3NzBkZmM3");
}
