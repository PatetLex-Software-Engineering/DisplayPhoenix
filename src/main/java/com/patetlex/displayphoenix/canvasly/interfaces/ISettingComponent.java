package com.patetlex.displayphoenix.canvasly.interfaces;

import com.patetlex.displayphoenix.canvasly.tools.Setting;

public interface ISettingComponent<T> {
    T getValue();
    Setting getSetting();
}
