package com.fread.cloverhide;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class CloverHide extends JavaPlugin {

    private static CloverHide instance;

    private String permissionToBypass;
    private boolean hideInTabComplete;

    // Список скрытых команд, загружается из конфига один раз
    private List<String> hiddenCommands;

    @Override
    public void onEnable() {
        instance = this;

        // Создаём/загружаем config.yml
        saveDefaultConfig();
        reloadConfigValues();

        // Регистрируем слушатель команд
        Bukkit.getPluginManager().registerEvents(new CommandListener(this), this);

        getLogger().info("CloverHide плагин включен!");
    }

    /**
     * Метод для перезагрузки конфига (например, при /reload).
     */
    public void reloadConfigValues() {
        reloadConfig();
        this.permissionToBypass = getConfig().getString("permission-to-bypass", "cloverhide.bypass");
        this.hideInTabComplete = getConfig().getBoolean("hide-in-tab-complete", true);

        // Сохраняем список команд в память (для оптимизации вместо постоянного чтения из конфига)
        this.hiddenCommands = getConfig().getStringList("hidden-commands");
    }

    public static CloverHide getInstance() {
        return instance;
    }

    public String getPermissionToBypass() {
        return permissionToBypass;
    }

    public boolean isHideInTabComplete() {
        return hideInTabComplete;
    }

    /**
     * Список скрытых команд, загруженный в память.
     */
    public List<String> getHiddenCommands() {
        return hiddenCommands;
    }

    /**
     * Получить сообщение (список строк) из конфига и перевести цветовые коды (включая HEX).
     */
    public List<String> getMessages(String path) {
        List<String> list = getConfig().getStringList("messages." + path);

        // Если список пуст, проверим, нет ли одиночной строки
        if (list.isEmpty()) {
            String single = getConfig().getString("messages." + path);
            if (single != null) {
                list = new ArrayList<>();
                list.add(single);
            }
        }

        // Переводим цвета для каждой строки
        list.replaceAll(ColorUtils::translateColorCodes);
        return list;
    }
}