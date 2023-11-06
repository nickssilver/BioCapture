#include "mls-jni.h"
#include "tpapi.h"

#include <android/log.h>
#include <android/bitmap.h>

#define  LOG_TAG    "Debug"
#define  ALOG(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

long callbckChangeImg(U32, U32, U8 *, U16, U16, void *);
long Nullcallbck(U32, U32, U8 *, U16, U16, void *);

JNIEnv *jnienv;
JavaVM* g_vm;
JNIEnv* g_jnienv;
jobject g_obj;
jmethodID  g_CallbackMethod;
bool FingersBox = false;
bool CheckFingers = false;
int FngDetected = 0;
int FngExpected = 0;
eCaptureType mCapType;

JNIEXPORT void JNICALL
Java_com_example_morpholivescan_MorphoLiveScan_SetFingersBox(JNIEnv *env, jobject, jboolean fingersBox)
{
    FingersBox = (bool)fingersBox;
    return;
}

JNIEXPORT jlong JNICALL
Java_com_example_morpholivescan_MorphoLiveScan_InitializeAPI(JNIEnv *env, jobject)
{
    return TP_InitializeAPI(env);
}

JNIEXPORT jlong JNICALL
Java_com_example_morpholivescan_MorphoLiveScan_TerminateAPI(JNIEnv *env, jobject)
{
    return TP_TerminateAPI();
}

JNIEXPORT jobjectArray JNICALL
Java_com_example_morpholivescan_MorphoLiveScan_GetAndroidDeviceList(JNIEnv *env, jobject)
{
    U32 NumDev = 1;
    jobjectArray jDevList;
    struct s_tpDevList pList[NumDev];
    long ret = TP_GetAndroidDeviceList(&NumDev, pList, env);
    if(ret == 0) {
        jclass cls = env->FindClass("com/example/morpholivescan/tpDevList");
        jmethodID mid = env->GetMethodID(cls, "<init>",
                                         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V");
        jDevList = env->NewObjectArray(NumDev, cls, 0);
        for (int ii = 0; ii < NumDev; ii++) {
            jstring JdevName = (*env).NewStringUTF(pList[ii].devName);
            jstring JAssyModel = (*env).NewStringUTF(pList[ii].assy_model);
            jstring JAssySn = (*env).NewStringUTF(pList[ii].assy_sn);
            jobject devObj = env->NewObject(cls, mid,
                                            JdevName,
                                            JAssyModel,
                                            JAssySn,
                                            pList[ii].libType);

            // Add to jDevList
            env->SetObjectArrayElement(jDevList, ii, devObj);
            env->DeleteLocalRef(devObj);
        }
    }
    else
    {
        ALOG("GetAndroidDeviceList return ret = %d", (int)ret);
        return NULL;
    }
    //delete pList;
    return jDevList;
}

JNIEXPORT jlong JNICALL
Java_com_example_morpholivescan_MorphoLiveScan_OpenDeviceSN(JNIEnv *env, jobject, jobject tpDevice)
{
    jmethodID getSN = env->GetMethodID(env->FindClass("com/example/morpholivescan/tpDevList"),
                                                "getAssy_sn", "()Ljava/lang/String;");
    jmethodID getLibType = env->GetMethodID(env->FindClass("com/example/morpholivescan/tpDevList"),
                                       "getLibType", "()I");
    jmethodID getHdl = env->GetMethodID(env->FindClass("com/example/morpholivescan/tpDevList"),
                                            "getHandel", "()I");
    jmethodID setHdl = env->GetMethodID(env->FindClass("com/example/morpholivescan/tpDevList"),
                                            "setHandel", "(I)V");
    jint jlibType = env->CallIntMethod(tpDevice,getLibType);
    jstring jSN = (jstring)env->CallObjectMethod(tpDevice,getSN);
    jint jHdl = env->CallIntMethod(tpDevice,getHdl);

    U32 libType = jlibType;
    char *pSN = (char *)env->GetStringUTFChars(jSN,NULL);
    U32 hdl = (U32)jHdl;
    long ret = TP_OpenDeviceSN(pSN,libType,&hdl);

    jHdl = (jint)hdl;
    env->CallVoidMethod(tpDevice,setHdl,jHdl);

    return ret;
}

JNIEXPORT jlong JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_CloseDevice(JNIEnv *env, jobject, jint jhdl)
{
    U32 hdl = (U32)jhdl;
    return TP_CloseDevice(hdl);
}

JNIEXPORT jboolean JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_DeviceConnected(JNIEnv *env, jobject, jint jhdl)
{
    U32 hdl = (U32)jhdl;
    return TP_DeviceConnected(hdl);
}

JNIEXPORT jlong JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_GetDeviceInfo(JNIEnv *env, jobject, jint jhdl,
                                                             jobject jpInfo)
{
    U32 hdl = (U32)jhdl;
    auto pInfo = std::make_unique<s_devInfo>();

    long ret = TP_GetDeviceInfo(hdl,pInfo.get());

    if(ret != 0)
    {
        return ret;
    }

    jmethodID setMake = env->GetMethodID(env->FindClass("com/example/morpholivescan/devInfo"),
                                       "setAssy_make", "(Ljava/lang/String;)V");
    jmethodID setModel = env->GetMethodID(env->FindClass("com/example/morpholivescan/devInfo"),
                                       "setAssy_model", "(Ljava/lang/String;)V");
    jmethodID setSN = env->GetMethodID(env->FindClass("com/example/morpholivescan/devInfo"),
                                       "setAssy_sn", "(Ljava/lang/String;)V");
    jmethodID setVersion = env->GetMethodID(env->FindClass("com/example/morpholivescan/devInfo"),
                                       "setSw_version", "(Ljava/lang/String;)V");
    if(setMake)
    {
        env->CallVoidMethod(jpInfo, setMake, env->NewStringUTF(pInfo->assy_make));
    }
    else
    {
        pInfo->assy_make[0] = '\0';
    }

    if(setModel)
    {
        env->CallVoidMethod(jpInfo, setModel, env->NewStringUTF(pInfo->assy_model));
    }
    else
    {
        pInfo->assy_model[0] = '\0';
    }

    if(setSN)
    {
        env->CallVoidMethod(jpInfo, setSN, env->NewStringUTF(pInfo->assy_sn));
    }
    else
    {
        pInfo->assy_sn[0] = '\0';
    }

    if(setVersion)
    {
        env->CallVoidMethod(jpInfo,setVersion,env->NewStringUTF(pInfo->sw_version));
    }
    else
    {
        pInfo->sw_version[0] = '\0';
    }

    return ret;
}

JNIEXPORT jlong JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_GetMaxVideoSize(JNIEnv *env, jobject, jint jhdl,
                                                               jobject jcapType, jobject jpVidioSize)
{
    U32 hdl = (U32)jhdl;
    U16 videoW = 0;
    U16 videoH = 0;

    jmethodID getCaptureType = env->GetMethodID(env->FindClass("com/example/morpholivescan/eCaptureType"),
                                                "ordinal", "()I");
    jint value = env->CallIntMethod(jcapType, getCaptureType);
    eCaptureType capType;
    capType = (eCaptureType)value ;

    long ret = TP_GetMaxVideoSize(hdl,capType,&videoW,&videoH);

    jmethodID setVideoW = env->GetMethodID(env->FindClass("com/example/morpholivescan/imgSize"),
                                           "setX", "(I)V");
    jmethodID setVideoH = env->GetMethodID(env->FindClass("com/example/morpholivescan/imgSize"),
                                           "setY", "(I)V");
    env->CallVoidMethod(jpVidioSize,setVideoW,(jint)videoW);
    env->CallVoidMethod(jpVidioSize,setVideoH,(jint)videoH);

    return ret;

}

JNIEXPORT jlong JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_CaptureControl(JNIEnv *env, jobject obj, jint jhdl, jobject jcapControl)
{
    U32 hdl = (U32)jhdl;

    jmethodID getCapControl = env->GetMethodID(env->FindClass("com/example/morpholivescan/eCapControl"),
                                                   "ordinal", "()I");
    jint value = env->CallIntMethod(jcapControl, getCapControl);
    eCapControl capControl;
    capControl = (eCapControl)value;

    long ret = TP_CaptureControl(hdl, capControl);

    return ret;
}

JNIEXPORT jlong JNICALL
Java_com_idemia_morpholivescan_MorphoLiveScan_StartPreview(JNIEnv *env, jobject obj, jint jhdl,
                                                            jobject jcapType, jint jcapMode,
                                                            jshort jvideoW, jshort jvideoH,
                                                            jstring jimgCallbk)
{
    U32 hdl = (U32)jhdl;
    jmethodID getCaptureType = env->GetMethodID(env->FindClass("com/example/morpholivescan/eCaptureType"),
                                                "ordinal", "()I");
    jint value = env->CallIntMethod(jcapType, getCaptureType);
    eCaptureType capType;
    mCapType = capType = (eCaptureType)value ;

    U32 capMode = (U32) jcapMode;
    U16 videoW = (U16) jvideoW;
    U16 videoH = (U16) jvideoH;

    int Ctxt = -1;

    g_jnienv = env;
    g_obj = env->NewGlobalRef(obj);
    jclass gclass = env->GetObjectClass(g_obj);
    ALOG("CapMode = 0x%08x", capMode);
    if(gclass == NULL)
    {
        ALOG("UNABLE TO GET CLASS");
        return -1;
    }
    g_CallbackMethod = env->GetMethodID(gclass,"ImageCallBack","([BIII[B)V");
    if(g_CallbackMethod == NULL)
    {
        ALOG("UNABLE TO GET callback method");
        return -1;
    }

    TP_ImgCallbk imgCallbk;

    const char *cstr = env->GetStringUTFChars(jimgCallbk, NULL);
    std::string Callbk = std::string(cstr);
    //ALOG("Callbk is %s", Callbk.c_str());
    if(Callbk == "changeImg") {
        imgCallbk = callbckChangeImg;
    }
    else
        imgCallbk = Nullcallbck;

    long ret = TP_StartPreview(hdl,capType,capMode,videoW,videoH,imgCallbk,(void *) &Ctxt);

    return ret;
}

long callbckChangeImg(U32 handleDevice, U32 CapState, U8 *ImageBuf, U16 Xsize, U16 Ysize, void *pCtxt)
{
    JNIEnv* env;
    void* vEnv;
    int getEnvState = g_vm->GetEnv(&vEnv,JNI_VERSION_1_6);
    int pos = 0;
    jbyteArray tmp2 = NULL;
    env = (JNIEnv*)vEnv;
    if(getEnvState == JNI_EDETACHED)
    {
        if(g_vm->AttachCurrentThread(&env,NULL)!=0) {
            ALOG("UNABLE TO ATTACH THREAD");
            return 0;
        }
    }
    else if(getEnvState == JNI_EVERSION)
    {
        ALOG("JNI VERSION PB");
        return 0;
    }

    int size = Xsize * Ysize;
    s_MTOPS_Results l_ResultsStruct;
    l_ResultsStruct.BoxResults = NULL;
    if(CapState == 5 && FingersBox)
    {
		tmp2 = env->NewByteArray(size);
        env->SetByteArrayRegion(tmp2,0,size,(jbyte *) ImageBuf);
        // U8 *Img_original = (U8 *)malloc(sizeof(U8) * size * 4);
        // for (int i = 0; i < Xsize * Ysize; i++) {
            // pos = i;
            // Img_original[pos * 4 + 1] = Img_original[pos * 4 + 2] = Img_original[pos * 4 + 3] = ImageBuf[i];
            // Img_original[pos * 4] = 0xff;

        // }
        // tmp2 = env->NewByteArray(size * 4);
        // env->SetByteArrayRegion(tmp2,0,size * 4,(jbyte *) Img_original);
        // free(Img_original);
        // Img_original = NULL;

        long ret = TP_GetSlapFngrBox(handleDevice, &l_ResultsStruct);
        if(ret == TPAPI_NOERROR && l_ResultsStruct.BoxResults != NULL) {
            int startline = 0, startCol = 0, width = 0, height = 0;
            if(CheckFingers)
            {
                switch (mCapType)
                {
                    case CTYPE_SLAP_FOUR:
                    case CTYPE_SLAP_LFOUR:
                    case CTYPE_SLAP_RFOUR:
                        if (l_ResultsStruct.nbFingDetected > 4)
                        {
                            FngExpected = 4;
                            FngDetected = l_ResultsStruct.nbFingDetected;
                        }
                        break;
                    case CTYPE_SLAP_ONE:
                    case CTYPE_SLAP_LTHUMB:
                    case CTYPE_SLAP_RTHUMB:
                        if (l_ResultsStruct.nbFingDetected > 1)
                        {
                            FngExpected = 1;
                            FngDetected = l_ResultsStruct.nbFingDetected;
                        }
                        break;
                    case CTYPE_SLAP_THUMBS:
                        if (l_ResultsStruct.nbFingDetected > 2)
                        {
                            FngExpected = 2;
                            FngDetected = l_ResultsStruct.nbFingDetected;
                        }
                        break;
                    default:
                        break;
                }
            }
            int ii;
            for (ii = 0; ii < l_ResultsStruct.nbFingDetected; ++ii) {
                if (l_ResultsStruct.BoxResults[ii].statusfinger == 1) {
                    startline = l_ResultsStruct.BoxResults[ii].boxStartingLine;
                    startCol = l_ResultsStruct.BoxResults[ii].boxStartingColumn;
                    width = l_ResultsStruct.BoxResults[ii].boxWidth;
                    height = l_ResultsStruct.BoxResults[ii].boxHeight;
                    int total_size = Xsize * Ysize;

                    const int COLOR = 0;
                    const int LINE_WIDTH = 5;
                    int index_1 = 0;
                    int index_2 = 0;
                    int current_line, current_column;
                    int nbrline;
                    int i = 0;

                    for (nbrline = 0; nbrline < LINE_WIDTH; nbrline++) {
                        current_column = startCol;

                        index_1 = (startline + nbrline) * Xsize + startCol;
                        index_2 = (startline + height + nbrline) * Xsize + startCol;

                        for (i = 0; i < width; i++, index_1++, index_2++, current_column++) {
                            if (index_1 < total_size && current_column < Xsize)
                                ImageBuf[index_1] = COLOR;

                            if (index_2 < total_size && current_column < Xsize)
                                ImageBuf[index_2] = COLOR;
                        }
                    }

                    for (nbrline = 0; nbrline < LINE_WIDTH; nbrline++) {
                        for (i = 0; i < height; i++) {
                            current_line = (startline + i);

                            if (startCol + nbrline < Xsize)
                                index_1 = (startline + i) * Xsize + startCol + nbrline;
                            else
                                index_1 = (startline + i) * Xsize;

                            if (startCol + nbrline + width < Xsize)
                                index_2 = (startline + i) * Xsize + startCol + nbrline + width;
                            else
                                index_2 = (startline + i) * Xsize + Xsize - 1;

                            if (index_1 < total_size && current_line < Ysize)
                                ImageBuf[index_1] = COLOR;

                            if (index_2 < total_size && current_line < Ysize)
                                ImageBuf[index_2] = COLOR;
                        }
                    }
                }
            }
        }
    }

    U8 *buf = ImageBuf;
    typedef unsigned long long int  U64;
    for(int i =0 ; i < (Xsize * Ysize) / 8 ; i++)
    {
        // ImageBuf[i] =  255 -ImageBuf[i];
        *(U64*)buf = 0xffffffffffffffff - *(U64*)buf;
        buf+=8;
    }

    //ALOG("TPAPI JNI sizereminder  = %d",((Xsize * Ysize) & 8));
    for(int i =0 ; i < ((Xsize * Ysize) & 8) ; i++)
    {
        //ImageBuf[i] =  255 -ImageBuf[i];
        *buf = 0xff - *buf;
        buf+=1;
    }

    jbyteArray tmp = env->NewByteArray(size);
    env->SetByteArrayRegion(tmp,0,size, (jbyte *)ImageBuf);

    env->CallVoidMethod(g_obj, g_CallbackMethod, tmp, (jint) Xsize, (jint) Ysize, (jint) CapState, tmp2);
    g_vm->DetachCurrentThread();

    if (l_ResultsStruct.BoxResults != NULL)
    {
        TP_FreeSlapFngrBox(&l_ResultsStruct);
    }
    return (long)0;
}

long Nullcallbck(U32 handleDevice, U32 CapState, U8 *ImageBuf, U16 Xsize, U16 Ysize, void *pCtxt)
{
    return (long)0;
}

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    g_vm = vm;

    return JNI_VERSION_1_6;
}
