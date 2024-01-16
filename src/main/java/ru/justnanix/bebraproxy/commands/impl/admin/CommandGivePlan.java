package ru.justnanix.bebraproxy.commands.impl.admin;

import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.commands.Command;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.player.plan.Plan;
import ru.justnanix.bebraproxy.player.plan.PlanAccount;
import ru.justnanix.bebraproxy.utils.minecraft.ChatUtil;

import java.util.Calendar;

@Command.CommandInfo(
        name = "giveplan",
        desc = "Выдаёт тариф указанному игроку",
        usage = "<имя тарифа> <время в часах> <ник игрока> <пароль>",
        allowedPlans = {Plan.ADMIN})
public class CommandGivePlan extends Command {
    @Override
    public void onCommand(ProxiedPlayer player, String[] args) throws Exception {
        // TODO: Recode that shit

        Plan plan = Plan.valueOf(args[0].toUpperCase());
        int expires = Integer.parseInt(args[1]);
        String nick = args[2];
        String password = args[3];

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expires);

        BebraProxy.getInstance().getPlanManager().getAccounts().add(new PlanAccount(nick, password, plan, calendar.getTime()));
        ChatUtil.sendChatMessage("Добавлен аккаунт &c" + nick + " &7с тарифом &c" + plan.name() + " &7в датабазу!", player, true);
    }
}
