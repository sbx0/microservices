package wsl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * setup wsl2 ip to host
 *
 * @author sbx0
 * @since 2022/5/31
 */
public class WSL2Support {
    public static final String WSL_HOSTS = "/etc/hosts";
    public static final String WINDOWS_HOSTS = "/mnt/c/Windows/System32/drivers/etc/hosts";
    public static final String WSL_DOMAIN = "wsl2.sbx0.cn";
    public static final String WINDOWS_DOMAIN = "win.sbx0.cn";

    public static void main(String[] args) {
        String wslIp = exec("ifconfig eth0 | grep -w inet | awk '{print $2}'");
        if ("".equals(wslIp)) {
            System.out.println("please 'apt install net-tools' first.");
        }
        String winIp = exec("cat /etc/resolv.conf | grep 'nameserver' | awk '{print $2}'");

        System.out.println("change wsl host");
        changeHost(WSL_HOSTS, wslIp, winIp);
        System.out.println("change windows host");
        changeHost(WINDOWS_HOSTS, wslIp, winIp);
    }

    public static void changeHost(String host, String wlsIpSource, String winIpSource) {
        StringBuilder wslIp = new StringBuilder(wlsIpSource);
        StringBuilder winIp = new StringBuilder(winIpSource);
        Path path = Paths.get(host);
        try {
            boolean findWslDomain = false;
            boolean findWindowsDomain = false;
            List<String> lines = Files.readAllLines(path);
            for (int i = 0, linesSize = lines.size(); i < linesSize; i++) {
                String line = lines.get(i);
                if (line.contains(WSL_DOMAIN)) {
                    System.out.println(line + "\told");
                    line = wslIp.append("\t" + WSL_DOMAIN).toString();
                    System.out.println(line + "\tnew");
                    lines.set(i, line);
                    findWslDomain = true;
                } else if (line.contains(WINDOWS_DOMAIN)) {
                    System.out.println(line + "\told");
                    line = winIp.append("\t" + WINDOWS_DOMAIN).toString();
                    System.out.println(line + "\tnew");
                    lines.set(i, line);
                    findWindowsDomain = true;
                }
            }
            if (!findWslDomain) {
                lines.add(wslIp.append("\t" + WSL_DOMAIN).toString());
            }
            if (!findWindowsDomain) {
                lines.add(winIp.append("\t" + WINDOWS_DOMAIN).toString());
            }
            write(host, lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(String file, List<String> contents) throws IOException {
        FileWriter fw = null;
        try {
            File f = new File(file);
            fw = new FileWriter(f, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fw != null) {
            PrintWriter pw = new PrintWriter(fw);
            for (String line : contents) {
                pw.println(line);
            }
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        }
    }

    public static String exec(String command) {
        String[] commands = {"/bin/sh", "-c", command};
        try {
            Process process = Runtime.getRuntime().exec(commands);
            InputStreamReader reader = new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder resultBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                resultBuilder.append(line);
            }
            return resultBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
