package ru.justnanix.bebraproxy.player.plan;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@RequiredArgsConstructor
@Getter @Setter
public class PlanAccount {
    private final String keyName;
    private final String password;
    private final Plan plan;
    private final Date expires;
    private IpInfo ipInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlanAccount account = (PlanAccount) o;
        return Objects.equals(keyName, account.keyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyName);
    }

    @Data
    public static class IpInfo {
        private final String city, isp, country;

        public String serialize() {
            return city + ":" + isp + ":" + country;
        }
    }
}
