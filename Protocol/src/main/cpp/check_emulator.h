#ifndef CHECK_EMULATOR_IN_NDK_CHECK_EMULATOR_H
#define CHECK_EMULATOR_IN_NDK_CHECK_EMULATOR_H

#include <jni.h>
#include "debugger.h"

#ifdef  __cplusplus

extern "C" {

#endif

/* C头文件的其它声明 */

/**
 *
 * @param env
 * @param context
 * @return  1:通过(非模拟器) else :不通过(是模拟器)
 */
JNIEXPORT jint JNICALL check_is_emulator(JNIEnv *env);

#ifdef  __cplusplus

}

#endif  /* end of __cplusplus */


#endif



