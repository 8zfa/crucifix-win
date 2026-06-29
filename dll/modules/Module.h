#pragma once

#include <string>
#include <functional>
#include <iostream>

class Module {
public:
    Module(const std::string& name, const std::string& category, int key = 0)
        : m_name(name), m_category(category), m_key(key), m_enabled(false) {}
    
    virtual ~Module() = default;
    
    virtual void onEnable() {}
    virtual void onDisable() {}
    virtual void onUpdate() {}
    virtual void onRender() {}
    
    void toggle() {
        m_enabled = !m_enabled;
        if (m_enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }
    
    bool isEnabled() const { return m_enabled; }
    const std::string& getName() const { return m_name; }
    const std::string& getCategory() const { return m_category; }
    int getKey() const { return m_key; }
    void setKey(int key) { m_key = key; }
    
protected:
    std::string m_name;
    std::string m_category;
    int m_key;
    bool m_enabled;
};
