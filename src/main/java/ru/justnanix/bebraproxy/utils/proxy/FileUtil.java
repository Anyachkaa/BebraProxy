package ru.justnanix.bebraproxy.utils.proxy;

import ru.justnanix.bebraproxy.BebraProxy;
import ru.justnanix.bebraproxy.player.plan.Plan;
import ru.justnanix.bebraproxy.player.plan.PlanAccount;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;

public class FileUtil {
    private static final File accountsFile = new File(BebraProxy.getInstance().getDirFolder(), "accounts.txt");
    private static final File accountsBackupFile = new File(BebraProxy.getInstance().getDirFolder(), "accounts_bk.txt");

    public static void createMissing() {
        String[] directories = new String[]{"players"};

        try {
            for (String d : directories) {
                new File(BebraProxy.getInstance().getDirFolder() + "/" + d).mkdir();
            }

            if (!accountsFile.exists()) {
                accountsFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadAccounts() {
        try {
            Scanner s = new Scanner(accountsFile);

            while (s.hasNext()) {
                try {
                    String[] split = s.nextLine().split(":");
                    BebraProxy.getInstance().getPlanManager().getAccounts().add(new PlanAccount(split[0], split[3],
                            Plan.valueOf(split[1]), new Date(Long.parseLong(split[2]))));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static synchronized void saveAccounts() {
        try {
            Files.copy(accountsFile.toPath(), accountsBackupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        accountsFile.delete();

        try (PrintWriter writer = new PrintWriter(new FileWriter(accountsFile))) {
            for (PlanAccount account : BebraProxy.getInstance().getPlanManager().getAccounts()) {
                writer.println(account.getKeyName() + ":" + account.getPlan().name() + ":" +
                        account.getExpires().getTime() + ":" + account.getPassword());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String getIconFile(String s) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(BebraProxy.getInstance().getDirFolder(), s));
            if (bufferedImage.getWidth() != 64 || bufferedImage.getHeight() != 64) {
                throw new IllegalStateException("> Icon must be 64 pixels wide and 64 pixels high");
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", Base64.getEncoder().wrap(os));

            return "data:image/png;base64," + os.toString(StandardCharsets.ISO_8859_1.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}