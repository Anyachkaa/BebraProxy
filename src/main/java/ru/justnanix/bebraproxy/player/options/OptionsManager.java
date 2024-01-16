package ru.justnanix.bebraproxy.player.options;

import lombok.Getter;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.player.options.impl.BooleanOption;
import ru.justnanix.bebraproxy.player.options.impl.SwitchOption;
import ru.justnanix.bebraproxy.player.options.impl.ValueOption;
import ru.justnanix.bebraproxy.utils.minecraft.Manager;
import ru.justnanix.bebraproxy.utils.minecraft.TabUtil;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Getter
public class OptionsManager extends Manager<Option> {
    private final ProxiedPlayer player;

    public OptionsManager(ProxiedPlayer player) {
        this.player = player;
    }

    public void loadOptions() {
        elements.add(new BooleanOption(Option.Category.PROXY, player, true, "Показывать игроков", "Показывать игроков в лобби"));
        elements.add(new BooleanOption(Option.Category.PROXY, player, true, "Пакеты", "Показывает пакеты которые отправляет сервер"));
        elements.add(new BooleanOption(Option.Category.PROXY, player, true, "Детектор лагов", "Выводит сообщение когда сервер лагает"));
        elements.add(new BooleanOption(Option.Category.PROXY, player, true, "Scoreboard", "Информация справа"));
        elements.add(new ValueOption(Option.Category.PROXY, player, "$ ", "Префикс команд", "Префикс с которого начинаются команды"));
        elements.add(new ValueOption(Option.Category.PROXY, player, "@", "Префикс чата", "Префикс с которого начинаются сообщения в чат §cBebra§fProxy§7"));
        elements.add(new BooleanOption(Option.Category.PROXY, player, false, "Таблист сервера", "Показывает таблист сервера вместо §cBebra§fProxy§7") {
            @Override
            public void onEnable() {
                TabUtil.updateTab(player);
                super.onEnable();
            }
        });
        
        elements.add(new BooleanOption(Option.Category.BOTS, player, true, "Чат", "Вывод сообщений из чата которые видят боты"));
        elements.add(new BooleanOption(Option.Category.BOTS, player, true, "Авторег", "Автоматическая регистрация ботов"));
        elements.add(new ValueOption(Option.Category.BOTS, player, "/register abobus abobus", "Авторег - команда регистрации", "Команда регистрации авторега"));
        elements.add(new ValueOption(Option.Category.BOTS, player, "/lobby abobus", "Авторег - команда авторизации", "Команда авторизации авторега"));
        elements.add(new BooleanOption(Option.Category.BOTS, player, true, "Автокарта", "Автоматическое решение капч на карте"));
        elements.add(new SwitchOption(Option.Category.BOTS, player, "Отключён", new String[]{"Отключён", "Diff"}, "АвтоGUI", "Автоматическое решение капч с Gui"));

        switch (player.getAccount().getPlan()) {
            case FREE:
                elements.add(new SwitchOption(Option.Category.BOTS, player, "Префикс (BEBRAPROXY_num)",
                        new String[]{"Префикс (BEBRAPROXY_num)"}, "Ник ботов", "Формат ников ботов"));
                break;

            case BASIC:
                elements.add(new SwitchOption(Option.Category.BOTS, player, "Префикс (BEBRAPROXY_num)",
                        new String[]{"Префикс (BEBRAPROXY_num)", "Рандом"}, "Ник ботов", "Формат ников ботов"));
                break;

            case ADVANCED:
            case MAX:
            case ADMIN:
                elements.add(new SwitchOption(Option.Category.BOTS, player, "Префикс (BEBRAPROXY_num)",
                        new String[]{"Префикс (BEBRAPROXY_num)", "Рандом"}, "Ник ботов", "Формат ников ботов"));
        }

        File file = new File(BebraProxy.getInstance().getDirFolder() + "/players", player.getAccount().getKeyName() + ".json");
        if (!file.exists()) {
            this.saveOptions();
        } else {
            JSONParser parser = new JSONParser();
            try {
                Object obj = parser.parse(new FileReader(file));
                JSONObject jsonObj = (JSONObject) obj;

                JSONObject optionsObj = (JSONObject) jsonObj.get("options");
                for (Option option : this.elements) {
                    if (optionsObj.get(option.getName()) != null) {
                        if (option instanceof BooleanOption) {
                            ((BooleanOption) option).setEnabled((Boolean) optionsObj.get(option.getName()));
                        } else if (option instanceof SwitchOption) {
                            ((SwitchOption) option).setCurrentVal((String) optionsObj.get(option.getName()));
                        } else if (option instanceof ValueOption) {
                            ((ValueOption) option).setValue((String) optionsObj.get(option.getName()));
                        }
                    }
                }
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveOptions() {
        try {
            File file = new File(BebraProxy.getInstance().getDirFolder() + "/players", player.getGameProfile().getName() + ".json");
            JSONObject jsonObj = new JSONObject();
            JSONObject optionsObj = new JSONObject();

            this.elements.forEach(option -> {
                if (option instanceof BooleanOption) {
                    optionsObj.put(option.getName(), ((BooleanOption) option).isEnabled());
                } else if (option instanceof SwitchOption) {
                    optionsObj.put(option.getName(), ((SwitchOption) option).getCurrentVal());
                } else if (option instanceof ValueOption) {
                    optionsObj.put(option.getName(), ((ValueOption) option).getValue());
                }
            });

            jsonObj.put("options", optionsObj);

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(new ObjectMapper().defaultPrettyPrintingWriter().writeValueAsString(jsonObj));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Option getOptionByName(String name) {
        return elements.stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);
    }

    public String getCmdPrefix() {
        ValueOption option = this.getOptionByName("Префикс команд").asValueOption();
        if (option.getValue() == null || option.getValue().trim().isEmpty()) option.setValue("$");

        return option.getValue().trim() + " ";
    }
}