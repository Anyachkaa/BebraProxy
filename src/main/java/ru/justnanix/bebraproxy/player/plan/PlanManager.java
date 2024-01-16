package ru.justnanix.bebraproxy.player.plan;

import lombok.Getter;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PlanManager {
    private final Set<PlanAccount> accounts = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void init() {

    }

    public PlanAccount getAccountByKeyName(String keyName) {
        return accounts.stream()
                .filter(account -> account.getKeyName().equals(keyName))
                .findAny().orElse(null);
    }
}
