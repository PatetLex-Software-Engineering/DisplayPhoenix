package net.displayphoenix.canvasly.interfaces;

import net.displayphoenix.canvasly.tools.Setting;

public interface ISettingComponent<T> {
    T getValue();
    Setting getSetting();
}
