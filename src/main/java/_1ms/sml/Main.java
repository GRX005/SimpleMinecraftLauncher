/*
    A simple minecraft launcher.
    Copyright (C) 2025 _1ms

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package _1ms.sml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarFile;

//.jar libs in libraries folder, .dlls in natives folder from bin, mc.jar next to the launcher., assets in assets folder, assetIndex is the num: assets\indexes\[num].json

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        var ind = getIndex();
        var ver = getVersion();
        System.out.println("Loading MC "+ getVersion() + " with assetIndex "+ getIndex() + "...");

        String currentDir = System.getProperty("user.dir");
        String sep = File.separator;
        String javaBin = System.getProperty("java.home") + sep + "bin" + sep + "java";
        String classPath = currentDir + sep + "libraries" + sep + "*" + File.pathSeparator + currentDir + sep + "mc.jar";
        String nativesPath = currentDir + sep + "natives";
        String gameDir = currentDir + sep + "MC";
        String assetsDir = currentDir + sep + "assets";
        String launcherBrand = "SML";
        String uname = args.length>0 ? args[0] : null;

        List<String> baseArgs = new ArrayList<>(Arrays.asList(
                javaBin,
                "-Xms1024M",
                "-Xmx4096M",
                "-Djava.library.path=" + nativesPath,
                "-Djna.tmpdir=" + nativesPath,
                "-Dorg.lwjgl.system.SharedLibraryExtractPath=" + nativesPath,
                "-Dio.netty.native.workdir=" + nativesPath,
                "-cp", classPath,
                "-XX:+UnlockExperimentalVMOptions", "-XX:+UseG1GC",
                "-XX:G1NewSizePercent=20", "-XX:G1ReservePercent=20",
                "-XX:MaxGCPauseMillis=50", "-XX:G1HeapRegionSize=32M",
                "-Dminecraft.launcher.brand=" + launcherBrand,
                "-Dminecraft.launcher.version=1.5",
                "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump",
                "-Xss1M",
                "net.minecraft.client.main.Main"
        ));

        // 2) Conditionally insert the username args
        if (uname != null) {
            // insert right after the main class name
            int insertIndex = baseArgs.indexOf("net.minecraft.client.main.Main") + 1;
            baseArgs.add(insertIndex, uname);
            baseArgs.add(insertIndex, "--username");
        }

        // 3) Add the remaining constant suffix
        baseArgs.addAll(Arrays.asList(
                "--uuid", UUID.randomUUID().toString(),
                "--clientId", launcherBrand,
                "--xuid", launcherBrand,
                "--version", ver,
                "--versionType", launcherBrand,
                "--gameDir", gameDir,
                "--assetsDir", assetsDir,
                "--assetIndex", ind,
                "--accessToken", launcherBrand
        ));

        // single ProcessBuilder invocation
        new ProcessBuilder(baseArgs).inheritIO().start().waitFor();
    }

    static String getIndex() throws IOException {
        try (var stream = Files.newDirectoryStream(Path.of("assets","indexes"), "*.json")) {
            for (Path p : stream) {
                var fname = p.getFileName().toString();
                return fname.substring(0, fname.length() - 5); // Remove ".json"
            }
        }
        return null;
    }

    static String getVersion() throws IOException {
        try (JarFile jar = new JarFile("mc.jar");
             BufferedReader reader = new BufferedReader(new InputStreamReader(
                     jar.getInputStream(jar.getEntry("version.json"))))) {
            reader.readLine(); // skip first line "{"
            return reader.readLine().split("\"")[3]; // extracts "1.21.11"
        }
    }
}