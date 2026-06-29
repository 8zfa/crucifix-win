#pragma once
#include <string>
#include <jni.h>

class Module
{
public:
    Module(const std::string& name) : m_name(name), m_enabled(false) {}
    virtual ~Module() = default;

    virtual void OnEnable() {}
    virtual void OnDisable() {}
    virtual void OnUpdate() {}

    void SetEnabled(bool enabled)
    {
        if (m_enabled != enabled)
        {
            m_enabled = enabled;
            if (enabled)
                OnEnable();
            else
                OnDisable();
        }
    }

    bool IsEnabled() const { return m_enabled; }
    const std::string& GetName() const { return m_name; }

protected:
    std::string m_name;
    bool m_enabled;
};
