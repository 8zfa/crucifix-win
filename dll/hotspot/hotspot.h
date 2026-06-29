#pragma once

#include <cstdint>
#include <string>
#include <jni.h>

// Source: https://github.com/Lefraudeur/RiptermsGhost/blob/master/HotSpot/HotSpot.hpp
struct HotSpot
{
    static bool Init();

    enum JavaThreadState
    {
        _thread_uninitialized = 0,
        _thread_new = 2,
        _thread_new_trans = 3,
        _thread_in_native = 4,
        _thread_in_native_trans = 5,
        _thread_in_vm = 6,
        _thread_in_vm_trans = 7,
        _thread_in_Java = 8,
        _thread_in_Java_trans = 9,
        _thread_blocked = 10,
        _thread_blocked_trans = 11,
        _thread_max_state = 12
    };

    typedef struct {
        const char* typeName;
        const char* superclassName;
        int32_t isOopType;
        int32_t isIntegerType;
        int32_t isUnsigned;
        uint64_t size;
    } VMTypeEntry;

    typedef struct {
        const char* typeName;
        const char* fieldName;
        const char* typeString;
        int32_t  isStatic;
        uint64_t offset;
        void* address;
    } VMStructEntry;

    struct ConstantPool
    {
        void** GetBase();
        static int GetSize();
        int GetLength();
    };

    struct ConstMethod
    {
        ConstantPool* GetConstants();
        void SetConstants(ConstantPool* _constants);
        unsigned short GetNameIndex();
        unsigned short GetSignatureIndex();
    };

    struct AccessFlags
    {
        jint _flags;
        bool IsStatic() const;
    };

    struct Method
    {
        ConstMethod* GetConstMethod();
        std::string GetSignature();
        std::string GetName();
        int GetParametersCount();
        AccessFlags* GetAccessFlags();
        void* GetFromInterpretedEntry();
        void SetFromInterpretedEntry(void* entry);
        void* GetFromCompiledEntry();
        void SetFromCompiledEntry(void* entry);
        void* GetI2iEntry();
        unsigned short* GetFlags();
        void SetDontInline(bool enabled);
    };

    struct frame
    {
        inline static int localsOffset = -56;
        void** GetLocals();
        Method* GetMethod();
    };

    struct Thread
    {
        JNIEnv* GetEnv();
        uint32_t GetSuspendFlags();
        JavaThreadState GetThreadState();
        void SetThreadState(JavaThreadState state);
        static int GetThreadStateOffset();
    };

    struct Symbol
    {
        std::string ToString();
    };

    enum
    {
        JVM_ACC_NOT_C2_COMPILABLE = 0x02000000,
        JVM_ACC_NOT_C1_COMPILABLE = 0x04000000,
        JVM_ACC_NOT_C2_OSR_COMPILABLE = 0x08000000,
        JVM_ACC_QUEUED = 0x01000000,
        JVM_ACC_PUBLIC = 0x0001,
        JVM_ACC_PRIVATE = 0x0002,
        JVM_ACC_PROTECTED = 0x0004,
        JVM_ACC_STATIC = 0x0008,
        JVM_ACC_FINAL = 0x0010,
        JVM_ACC_SYNCHRONIZED = 0x0020,
        JVM_ACC_VOLATILE = 0x0040,
        JVM_ACC_TRANSIENT = 0x0080,
        JVM_ACC_NATIVE = 0x0100,
        JVM_ACC_INTERFACE = 0x0200,
        JVM_ACC_ABSTRACT = 0x0400,
    };

    enum Flags
    {
        _caller_sensitive = 1 << 0,
        _force_inline = 1 << 1,
        _dont_inline = 1 << 2,
        _hidden = 1 << 3,
        _has_injected_profile = 1 << 4,
        _intrinsic_candidate = 1 << 5,
        _reserved_stack_access = 1 << 6,
        _scoped = 1 << 7
    };
};

extern "C" JNIIMPORT HotSpot::VMTypeEntry* gHotSpotVMTypes;
extern "C" JNIIMPORT HotSpot::VMStructEntry* gHotSpotVMStructs;
