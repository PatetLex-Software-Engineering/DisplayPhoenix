package net.displayphoenix.image.interfaces;

import net.displayphoenix.image.tools.Setting;

public interface ISettingComponent<T> {
    T getValue();
    Setting getSetting();
}
