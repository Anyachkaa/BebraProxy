package ru.justnanix.bebraproxy.commands;

import lombok.Getter;
import ru.justnanix.bebraproxy.player.ProxiedPlayer;
import ru.justnanix.bebraproxy.network.connection.ConnectionInfo;
import ru.justnanix.bebraproxy.player.plan.Plan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public abstract class Command {
    private final String name;
    private final String desc;
    private final String usage;
    private final List<ConnectionInfo> allowedStates;
    private final List<Plan> allowedPlans;

    public Command() {
        CommandInfo info = this.getClass().getDeclaredAnnotation(CommandInfo.class);
        this.name = info.name();
        this.desc = info.desc();
        this.usage = info.usage();
        this.allowedStates = Arrays.stream(info.allowedStates()).collect(Collectors.toList());
        if (allowedStates.isEmpty()) allowedStates.addAll(Arrays.asList(ConnectionInfo.values()));
        this.allowedPlans = Arrays.stream(info.allowedPlans()).collect(Collectors.toList());
        if (allowedPlans.isEmpty()) allowedPlans.addAll(Arrays.asList(Plan.values()));
    }

    public abstract void onCommand(ProxiedPlayer player, String[] args) throws Exception;

    @Target(ElementType.TYPE_USE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CommandInfo {
        String name();
        String desc() default "&r&7Описание не указано!";
        String usage() default "";
        ConnectionInfo[] allowedStates() default {};
        Plan[] allowedPlans() default {};
    }
}