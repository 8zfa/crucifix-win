#include "hotspot.h"
#include <iostream>
#include <cstring>

extern "C" JNIIMPORT HotSpot::VMTypeEntry* gHotSpotVMTypes;
extern "C" JNIIMPORT HotSpot::VMStructEntry* gHotSpotVMStructs;

static HotSpot::VMTypeEntry* FindVMTypeEntry(const char* typeName)
{
    for (HotSpot::VMTypeEntry* entry = gHotSpotVMTypes; entry->typeName != nullptr; ++entry)
    {
        if (typeName && std::strcmp(typeName, entry->typeName)) continue;
        std::cout << "[HotSpot] Found VMTypeEntry: " << typeName << std::endl;
        return entry;
    }
    std::cout << "[HotSpot] Failed to find VMTypeEntry: " << typeName << std::endl;
    return nullptr;
}

static HotSpot::VMStructEntry* FindVMStructEntry(const char* typeName, const char* fieldName, bool isStatic)
{
    for (HotSpot::VMStructEntry* entry = gHotSpotVMStructs; entry->typeName != nullptr; ++entry)
    {
        if (typeName && std::strcmp(typeName, entry->typeName)) continue;
        if (fieldName && std::strcmp(fieldName, entry->fieldName)) continue;
        if (isStatic != (bool)entry->isStatic) continue;
        std::cout << "[HotSpot] Found VMStructEntry: " << typeName << "::" << fieldName << std::endl;
        return entry;
    }
    std::cout << "[HotSpot] Failed to find VMStructEntry: " << typeName << "::" << fieldName << std::endl;
    return nullptr;
}

void** HotSpot::ConstantPool::GetBase()
{
    if (!this) return nullptr;
    int size = GetSize();
    if (!size) return nullptr;
    return (void**)((uint8_t*)this + size);
}

int HotSpot::ConstantPool::GetSize()
{
    static VMTypeEntry* ConstantPool_entry = FindVMTypeEntry("ConstantPool");
    if (!ConstantPool_entry) return 0;
    return (int)ConstantPool_entry->size;
}

int HotSpot::ConstantPool::GetLength()
{
    if (!this) return 0;
    static VMStructEntry* _length_entry = FindVMStructEntry("Array<Klass*>", "_length", false);
    if (!_length_entry) return 0;
    return *(int*)((uint8_t*)this + _length_entry->offset);
}

HotSpot::ConstantPool* HotSpot::ConstMethod::GetConstants()
{
    if (!this) return nullptr;
    static VMStructEntry* _constants_entry = FindVMStructEntry("InstanceKlass", "_constants", false);
    if (!_constants_entry) return nullptr;
    return *(ConstantPool**)((uint8_t*)this + _constants_entry->offset);
}

void HotSpot::ConstMethod::SetConstants(ConstantPool* _constants)
{
    if (!this) return;
    static VMStructEntry* _constants_entry = FindVMStructEntry("ConstMethod", "_constants", false);
    if (!_constants_entry) return;
    *(ConstantPool**)((uint8_t*)this + _constants_entry->offset) = _constants;
}

unsigned short HotSpot::ConstMethod::GetNameIndex()
{
    if (!this) return 0;
    static VMStructEntry* _name_index_entry = FindVMStructEntry("ConstMethod", "_name_index", false);
    if (!_name_index_entry) return 0;
    return *(unsigned short*)((uint8_t*)this + _name_index_entry->offset);
}

unsigned short HotSpot::ConstMethod::GetSignatureIndex()
{
    if (!this) return 0;
    static VMStructEntry* _signature_index_entry = FindVMStructEntry("ConstMethod", "_signature_index", false);
    if (!_signature_index_entry) return 0;
    return *(unsigned short*)((uint8_t*)this + _signature_index_entry->offset);
}

bool HotSpot::AccessFlags::IsStatic() const
{
    return _flags & JVM_ACC_STATIC;
}

HotSpot::ConstMethod* HotSpot::Method::GetConstMethod()
{
    if (!this) return nullptr;
    static VMStructEntry* _constMethod_entry = FindVMStructEntry("Method", "_constMethod", false);
    if (!_constMethod_entry) return nullptr;
    return *(ConstMethod**)((uint8_t*)this + _constMethod_entry->offset);
}

std::string HotSpot::Method::GetSignature()
{
    if (!this) return "";
    ConstMethod* constMethod = GetConstMethod();
    if (!constMethod) return "";
    unsigned short signatureIndex = constMethod->GetSignatureIndex();
    if (signatureIndex == 0) return "";
    
    ConstantPool* constants = constMethod->GetConstants();
    if (!constants) return "";
    
    void** base = constants->GetBase();
    if (!base) return "";
    
    Symbol* symbol = (Symbol*)base[signatureIndex];
    if (!symbol) return "";
    
    return symbol->ToString();
}

std::string HotSpot::Method::GetName()
{
    if (!this) return "";
    ConstMethod* constMethod = GetConstMethod();
    if (!constMethod) return "";
    unsigned short nameIndex = constMethod->GetNameIndex();
    if (nameIndex == 0) return "";
    
    ConstantPool* constants = constMethod->GetConstants();
    if (!constants) return "";
    
    void** base = constants->GetBase();
    if (!base) return "";
    
    Symbol* symbol = (Symbol*)base[nameIndex];
    if (!symbol) return "";
    
    return symbol->ToString();
}

int HotSpot::Method::GetParametersCount()
{
    if (!this) return 0;
    std::string signature = GetSignature();
    int count = 0;
    for (char c : signature) {
        if (c == 'L' || c == '[' || c == 'I' || c == 'F' || c == 'D' || c == 'J' || c == 'Z' || c == 'B' || c == 'C' || c == 'S') {
            count++;
        }
    }
    return count;
}

HotSpot::AccessFlags* HotSpot::Method::GetAccessFlags()
{
    if (!this) return nullptr;
    static VMStructEntry* _access_flags_entry = FindVMStructEntry("Method", "_access_flags", false);
    if (!_access_flags_entry) return nullptr;
    return (AccessFlags*)((uint8_t*)this + _access_flags_entry->offset);
}

void* HotSpot::Method::GetFromInterpretedEntry()
{
    if (!this) return nullptr;
    static VMStructEntry* _i2i_entry_entry = FindVMStructEntry("Method", "_i2i_entry", false);
    if (!_i2i_entry_entry) return nullptr;
    return *(void**)((uint8_t*)this + _i2i_entry_entry->offset);
}

void HotSpot::Method::SetFromInterpretedEntry(void* entry)
{
    if (!this) return;
    static VMStructEntry* _i2i_entry_entry = FindVMStructEntry("Method", "_i2i_entry", false);
    if (!_i2i_entry_entry) return;
    *(void**)((uint8_t*)this + _i2i_entry_entry->offset) = entry;
}

void* HotSpot::Method::GetFromCompiledEntry()
{
    if (!this) return nullptr;
    static VMStructEntry* _from_compiled_entry_entry = FindVMStructEntry("Method", "_from_compiled_entry", false);
    if (!_from_compiled_entry_entry) return nullptr;
    return *(void**)((uint8_t*)this + _from_compiled_entry_entry->offset);
}

void HotSpot::Method::SetFromCompiledEntry(void* entry)
{
    if (!this) return;
    static VMStructEntry* _from_compiled_entry_entry = FindVMStructEntry("Method", "_from_compiled_entry", false);
    if (!_from_compiled_entry_entry) return;
    *(void**)((uint8_t*)this + _from_compiled_entry_entry->offset) = entry;
}

void* HotSpot::Method::GetI2iEntry()
{
    if (!this) return nullptr;
    static VMStructEntry* _i2i_entry_entry = FindVMStructEntry("Method", "_i2i_entry", false);
    if (!_i2i_entry_entry) return nullptr;
    return *(void**)((uint8_t*)this + _i2i_entry_entry->offset);
}

unsigned short* HotSpot::Method::GetFlags()
{
    if (!this) return nullptr;
    static VMStructEntry* _flags_entry = FindVMStructEntry("Method", "_flags", false);
    if (!_flags_entry) return nullptr;
    return (unsigned short*)((uint8_t*)this + _flags_entry->offset);
}

void HotSpot::Method::SetDontInline(bool enabled)
{
    if (!this) return;
    unsigned short* flags = GetFlags();
    if (!flags) return;
    if (enabled) {
        *flags |= _dont_inline;
    } else {
        *flags &= ~_dont_inline;
    }
}

void** HotSpot::frame::GetLocals()
{
    if (!this) return nullptr;
    return (void**)((uint8_t*)this + localsOffset);
}

HotSpot::Method* HotSpot::frame::GetMethod()
{
    if (!this) return nullptr;
    static VMStructEntry* _method_entry = FindVMStructEntry("frame", "_method", false);
    if (!_method_entry) return nullptr;
    return *(Method**)((uint8_t*)this + _method_entry->offset);
}

JNIEnv* HotSpot::Thread::GetEnv()
{
    if (!this) return nullptr;
    static VMStructEntry* _jni_environment_entry = FindVMStructEntry("JavaThread", "_jni_environment", false);
    if (!_jni_environment_entry) return nullptr;
    return *(JNIEnv**)((uint8_t*)this + _jni_environment_entry->offset);
}

uint32_t HotSpot::Thread::GetSuspendFlags()
{
    if (!this) return 0;
    static VMStructEntry* _suspend_flags_entry = FindVMStructEntry("JavaThread", "_suspend_flags", false);
    if (!_suspend_flags_entry) return 0;
    return *(uint32_t*)((uint8_t*)this + _suspend_flags_entry->offset);
}

HotSpot::JavaThreadState HotSpot::Thread::GetThreadState()
{
    if (!this) return _thread_uninitialized;
    static VMStructEntry* _thread_state_entry = FindVMStructEntry("JavaThread", "_thread_state", false);
    if (!_thread_state_entry) return _thread_uninitialized;
    return *(JavaThreadState*)((uint8_t*)this + _thread_state_entry->offset);
}

void HotSpot::Thread::SetThreadState(JavaThreadState state)
{
    if (!this) return;
    static VMStructEntry* _thread_state_entry = FindVMStructEntry("JavaThread", "_thread_state", false);
    if (!_thread_state_entry) return;
    *(JavaThreadState*)((uint8_t*)this + _thread_state_entry->offset) = state;
}

int HotSpot::Thread::GetThreadStateOffset()
{
    static VMStructEntry* _thread_state_entry = FindVMStructEntry("JavaThread", "_thread_state", false);
    if (!_thread_state_entry) return 0;
    return _thread_state_entry->offset;
}

std::string HotSpot::Symbol::ToString()
{
    if (!this) return "";
    static VMStructEntry* _body_entry = FindVMStructEntry("Symbol", "_body", false);
    if (!_body_entry) return "";
    static VMStructEntry* _length_entry = FindVMStructEntry("Symbol", "_length", false);
    if (!_length_entry) return "";
    
    uint8_t* body = *(uint8_t**)((uint8_t*)this + _body_entry->offset);
    int length = *(int*)((uint8_t*)this + _length_entry->offset);
    
    if (!body || length <= 0) return "";
    
    return std::string((char*)body, length);
}

bool HotSpot::Init()
{
    std::cout << "[HotSpot] Initializing HotSpot..." << std::endl;
    
    // Test if we can access the VM structures
    VMTypeEntry* testType = FindVMTypeEntry("Method");
    if (!testType) {
        std::cout << "[HotSpot] Failed to initialize - cannot access VMTypes" << std::endl;
        return false;
    }
    
    VMStructEntry* testStruct = FindVMStructEntry("Method", "_constMethod", false);
    if (!testStruct) {
        std::cout << "[HotSpot] Failed to initialize - cannot access VMStructs" << std::endl;
        return false;
    }
    
    std::cout << "[HotSpot] HotSpot initialized successfully" << std::endl;
    return true;
}
