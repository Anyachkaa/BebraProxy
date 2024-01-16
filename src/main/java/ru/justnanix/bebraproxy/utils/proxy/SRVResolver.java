package ru.justnanix.bebraproxy.utils.proxy;

import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class SRVResolver {
    public static String[] getServerAddress(String p_78863_0_) {
        try {
            Hashtable<String, String> var2 = new Hashtable<>();
            var2.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            var2.put("java.naming.provider.url", "dns:");
            var2.put("com.sun.jndi.dns.timeout.retries", "1");
            InitialDirContext var3 = new InitialDirContext(var2);
            Attributes var4 = var3.getAttributes("_minecraft._tcp." + p_78863_0_, new String[]{"SRV"});
            String[] var5 = var4.get("srv").get().toString().split(" ", 4);
            return new String[]{var5[3], var5[2]};
        } catch (Throwable var6) {
            return new String[]{p_78863_0_, "25565"};
        }
    }
}