#include "com_yiyi_providecodelib_CodeUtils.h"
#include <android/log.h>

#define DEBUG 0
#define LOG_TAG   "TAG"
#if DEBUG
#define  LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#else
#define  LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,"")
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,"")
#endif

JNIEXPORT jstring JNICALL Java_com_yiyi_providecodelib_CodeUtils_getSign
        (JNIEnv *env, jclass clazz, jobject j_map) {

    //Iterator Iterator<String> iterator = params.keySet().iterator();
    jclass j_map_clazz = (*env)->FindClass(env, "java/util/Map");
    jmethodID m_keySet = (*env)->GetMethodID(env, j_map_clazz, "keySet", "()Ljava/util/Set;");
    jobject j_keySet = (*env)->CallObjectMethod(env, j_map, m_keySet);
    LOGI("获得------------->j_keySet");
    jclass j_keySet_clazz = (*env)->FindClass(env, "java/util/Set");
    jmethodID m_iterator = (*env)->GetMethodID(env, j_keySet_clazz, "iterator", "()Ljava/util/Iterator;");
    jobject j_iterator = (*env)->CallObjectMethod(env, j_keySet, m_iterator);
    LOGI("获得------------->j_iterator");

    //初始化一个ArrayList List<String> keyList = new ArrayList<String>();
    jclass j_List_clazz = (*env)->FindClass(env, "java/util/ArrayList");
    jmethodID m_initList = (*env)->GetMethodID(env, j_List_clazz, "<init>", "()V");
    jobject j_list = (*env)->NewObject(env, j_List_clazz, m_initList);
    LOGI("获得------------->j_list");

    /*
       while (iterator.hasNext()) {
            keyList.add(iterator.next());
        }
     */
    jclass j_Iterator_clazz = (*env)->FindClass(env, "java/util/Iterator");
    jmethodID m_hasNext = (*env)->GetMethodID(env, j_Iterator_clazz, "hasNext", "()Z");
    jmethodID m_next = (*env)->GetMethodID(env, j_Iterator_clazz, "next", "()Ljava/lang/Object;");
    jmethodID m_add = (*env)->GetMethodID(env, j_List_clazz, "add", "(Ljava/lang/Object;)Z");
    while ((*env)->CallBooleanMethod(env, j_iterator, m_hasNext)) {
        jobject j_Key = (*env)->CallObjectMethod(env, j_iterator, m_next);
        const char *key = (*env)->GetStringUTFChars(env, (jstring) j_Key, 0);
        LOGI("hasNext----->%s", key);
        jboolean i = (*env)->CallBooleanMethod(env, j_list, m_add, (jstring) j_Key);
        LOGI("j_list----->add(%s)", key);
        (*env)->ReleaseStringUTFChars(env, (jstring) j_Key, key);
        (*env)->DeleteLocalRef(env, j_Key);
    }

    //初始化一个Collections
    jclass j_Collections_clazz = (*env)->FindClass(env, "java/util/Collections");
    jmethodID m_sort = (*env)->GetStaticMethodID(env, j_Collections_clazz, "sort", "(Ljava/util/List;)V");
    //对list排序 Collections.sort(keyList);
    (*env)->CallStaticVoidMethod(env, j_Collections_clazz, m_sort, j_list);
    LOGI("调用---------->Collections.sort(keyList)");

//    StringBuffer sb = new StringBuffer();
    jclass j_SB_clazz = (*env)->FindClass(env, "java/lang/StringBuilder");
    jmethodID m_initSB = (*env)->GetMethodID(env, j_SB_clazz, "<init>", "()V");
    jobject j_StringBuilder = (*env)->NewObject(env, j_SB_clazz, m_initSB);
    LOGI("获得------------->j_StringBuilder");

    /*
     for (int i = 0; i < keyList.size(); i++) {
            String key = keyList.get(i);
            String value = params.get(key);
            if (i == 0) {
                sb.append(key);
            } else {
                sb.append("&").append(key);
            }
            sb.append("=").append(value);
        }
     */

    jmethodID m_GetSize = (*env)->GetMethodID(env, j_List_clazz, "size", "()I");
    jint size = (*env)->CallIntMethod(env, j_list, m_GetSize);
    LOGI("j_list size ------->%d", size);

    jmethodID m_list_get = (*env)->GetMethodID(env, j_List_clazz, "get", "(I)Ljava/lang/Object;");
    jmethodID m_map_get = (*env)->GetMethodID(env, j_map_clazz, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
    jmethodID m_append = (*env)->GetMethodID(env, j_SB_clazz, "append",
                                             "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
    for (int i = 0; i < size; ++i) {
        LOGI("遍历第%d个参数......", i + 1);
        jstring j_key = (jstring) (*env)->CallObjectMethod(env, j_list, m_list_get, i);
        jstring j_value = (jstring) (*env)->CallObjectMethod(env, j_map, m_map_get, j_key);

        if (i == 0) {
            (*env)->CallObjectMethod(env, j_StringBuilder, m_append, j_key);
        } else {
            (*env)->CallObjectMethod(env, j_StringBuilder, m_append, (*env)->NewStringUTF(env, "&"));
            (*env)->CallObjectMethod(env, j_StringBuilder, m_append, j_key);
        }
        (*env)->CallObjectMethod(env, j_StringBuilder, m_append, (*env)->NewStringUTF(env, "="));
        (*env)->CallObjectMethod(env, j_StringBuilder, m_append, j_value);
//        LOGI("j_StringBuilder----->append(%s,%s)", j_key, j_value);

        (*env)->DeleteLocalRef(env, j_key);
        (*env)->DeleteLocalRef(env, j_value);
    }

//    String stringSignTemp = sb.toString()+"&key="+key;

    (*env)->CallObjectMethod(env, j_StringBuilder, m_append, (*env)->NewStringUTF(env, "&key=42343RdaaD12ccEjavaa"));
    jmethodID m_toString = (*env)->GetMethodID(env, j_SB_clazz, "toString", "()Ljava/lang/String;");
    jstring j_temp = (jstring) (*env)->CallObjectMethod(env, j_StringBuilder, m_toString);
    LOGI("参数是：%s",(*env)->GetStringUTFChars(env, j_temp, 0));

//    MD5.getMD5Str(stringSignTemp).toUpperCase()
    jclass j_MD5_clazz = (*env)->FindClass(env, "com/example/dell/dkcwallet/util/MD5");
    jmethodID m_getMd5 = (*env)->GetStaticMethodID(env, j_MD5_clazz, "getMD5Str", "(Ljava/lang/String;)Ljava/lang/String;");

    (*env)->DeleteLocalRef(env, j_keySet);
    (*env)->DeleteLocalRef(env, j_iterator);
    (*env)->DeleteLocalRef(env, j_list);
    (*env)->DeleteLocalRef(env, j_StringBuilder);
    (*env)->DeleteLocalRef(env, j_map);

    (*env)->DeleteLocalRef(env, j_Collections_clazz);
    (*env)->DeleteLocalRef(env, j_Iterator_clazz);
    (*env)->DeleteLocalRef(env, j_keySet_clazz);
    (*env)->DeleteLocalRef(env, j_List_clazz);
    (*env)->DeleteLocalRef(env, j_map_clazz);
    (*env)->DeleteLocalRef(env, j_SB_clazz);

    return (*env)->CallStaticObjectMethod(env, j_MD5_clazz, m_getMd5, j_temp);
}

