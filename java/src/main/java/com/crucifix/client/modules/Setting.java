package com.crucifix.client.modules;

/**
 * Generic setting class for module configuration
 */
public class Setting<T> {
    private final String name;
    private T value;
    private final T defaultValue;
    private final T minValue;
    private final T maxValue;
    private final T step;
    private final String[] options;
    
    private Setting(String name, T value, T defaultValue, T minValue, T maxValue, T step, String[] options) {
        this.name = name;
        this.value = value;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
        this.options = options;
    }
    
    public static <T> Setting<T> create(String name, T defaultValue) {
        return new Setting<>(name, defaultValue, defaultValue, null, null, null, null);
    }
    
    public static Setting<Double> createSlider(String name, double defaultValue, double minValue, double maxValue, double step) {
        return new Setting<>(name, defaultValue, defaultValue, minValue, maxValue, step, null);
    }
    
    public static Setting<Integer> createSlider(String name, int defaultValue, int minValue, int maxValue, int step) {
        return new Setting<>(name, defaultValue, defaultValue, minValue, maxValue, step, null);
    }
    
    public static Setting<String> createDropdown(String name, String[] options) {
        return new Setting<>(name, options[0], options[0], null, null, null, options);
    }
    
    public static Setting<String> createDropdown(String name, String defaultValue, String[] options) {
        return new Setting<>(name, defaultValue, defaultValue, null, null, null, options);
    }
    
    public static Setting<Boolean> createToggle(String name, boolean defaultValue) {
        return new Setting<>(name, defaultValue, defaultValue, null, null, null, null);
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public T getValue() {
        return value;
    }
    
    @SuppressWarnings("unchecked")
    public double getDoubleValue() {
        return (Double) value;
    }
    
    @SuppressWarnings("unchecked")
    public int getIntValue() {
        return (Integer) value;
    }
    
    @SuppressWarnings("unchecked")
    public boolean getBooleanValue() {
        return (Boolean) value;
    }
    
    @SuppressWarnings("unchecked")
    public String getStringValue() {
        return (String) value;
    }
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public T getDefaultValue() {
        return defaultValue;
    }
    
    public T getMinValue() {
        return minValue;
    }
    
    public T getMaxValue() {
        return maxValue;
    }
    
    public T getStep() {
        return step;
    }
    
    public String[] getOptions() {
        return options;
    }
    
    public boolean isSlider() {
        return minValue != null && maxValue != null;
    }
    
    public boolean isDropdown() {
        return options != null;
    }
    
    public boolean isToggle() {
        return value instanceof Boolean;
    }
}
