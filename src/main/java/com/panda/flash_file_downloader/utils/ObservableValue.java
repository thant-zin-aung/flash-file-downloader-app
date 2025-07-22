package com.panda.flash_file_downloader.utils;

public class ObservableValue<T> {
    private T value;
    private ValueChangeListener<T> listener;

    public void setListener(ValueChangeListener<T> listener) {
        this.listener = listener;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T newValue) {
        if (value == null || !value.equals(newValue)) {
            T oldValue = value;
            value = newValue;
            if (listener != null) {
                listener.onChange(oldValue, newValue);
            }
        }
    }

    public interface ValueChangeListener<T> {
        void onChange(T oldValue, T newValue);
    }
}
