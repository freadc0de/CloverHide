package com.fread.cloverhide;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandListener implements Listener {

    private final CloverHide plugin;

    public CommandListener(CloverHide plugin) {
        this.plugin = plugin;
    }

    /**
     * Отслеживаем любые введённые команды и скрываем, если нужно.
     */
    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage(); // Например, "/op"

        if (message.startsWith("/")) {
            message = message.substring(1); // убираем '/'
        }
        String[] args = message.split(" ");
        if (args.length == 0) return;

        // Имя вводимой команды (без '/')
        String commandName = args[0].toLowerCase();

        // Берём список скрытых команд из плагина (в памяти)
        List<String> hiddenCommands = plugin.getHiddenCommands();

        // Если команда в списке скрытых и у игрока нет bypass-разрешения, отменяем
        if (hiddenCommands.contains(commandName) && !player.hasPermission(plugin.getPermissionToBypass())) {
            event.setCancelled(true);

            // Выводим все строки из "command-not-found"
            List<String> lines = plugin.getMessages("command-not-found");
            for (String line : lines) {
                player.sendMessage(line);
            }
        }
    }

    /**
     * Скрытие команд в таб-комплите, если hide-in-tab-complete == true.
     */
    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        if (!plugin.isHideInTabComplete()) return;
        if (!(event.getSender() instanceof Player)) return;

        Player player = (Player) event.getSender();
        String buffer = event.getBuffer();

        // Проверяем, действительно ли это команда (начинается со слеша)
        if (!buffer.startsWith("/")) {
            return;
        }

        // Убираем '/', приводим к нижнему регистру
        String commandWithoutSlash = buffer.substring(1).toLowerCase();
        String[] split = commandWithoutSlash.split(" ");
        String commandName = split[0]; // первая часть — имя команды

        List<String> hiddenCommands = plugin.getHiddenCommands();
        List<String> completions = event.getCompletions();

        // Если у игрока нет bypass-права, скрываем нужные команды
        if (!player.hasPermission(plugin.getPermissionToBypass())) {
            // Если уже ввёл конкретную скрытую команду — убираем все варианты
            if (hiddenCommands.contains(commandName)) {
                completions.clear();
            } else {
                // Иначе фильтруем все, что есть в hiddenCommands
                List<String> toRemove = new ArrayList<>();
                for (String completion : completions) {
                    // Обычно в completions команды без '/', например "op"
                    String c = completion.toLowerCase().replace("/", "");
                    if (hiddenCommands.contains(c)) {
                        toRemove.add(completion);
                    }
                }
                completions.removeAll(toRemove);
            }
            event.setCompletions(completions);
        }
    }
}