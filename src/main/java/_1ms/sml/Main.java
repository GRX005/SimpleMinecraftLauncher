/*
    A simple minecraft launcher.
    Copyright (C) 2025-2026 _1ms

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
import java.util.UUID;
import java.util.jar.JarFile;

//.jar libs in libraries folder, .dlls in natives folder from bin, mc.jar next to the launcher., assets in assets folder, assetIndex is the num: assets\indexes\[num].json

public class Main {

    void main(String[] args) throws IOException, InterruptedException {
        var ind = getIndex();
        var ver = getVersion();
        System.out.println("Loading MC "+ ver + " with assetIndex "+ ind + "...");

        String currentDir = System.getProperty("user.dir");
        String sep = File.separator;
        String javaBin = System.getProperty("java.home") + sep + "bin" + sep + "java";
        String classPath = currentDir + sep + "libraries" + sep + "*" + File.pathSeparator + currentDir + sep + "mc.jar";
        String nativesPath = currentDir + sep + "natives";
        String gameDir = currentDir + sep + "MC";
        String assetsDir = currentDir + sep + "assets";
        String launcherBrand = "SML";
        String uname = args.length>0 ? args[0] : null;

        var baseArgs = new ArrayList<>(Arrays.asList(
                javaBin,
                "-Xms2G",
                "-Xmx4G",
                "--sun-misc-unsafe-memory-access=allow",
                "--enable-native-access=ALL-UNNAMED",
                "-Djava.library.path=" + nativesPath,
                "-Djna.tmpdir=" + nativesPath,
                "-Dorg.lwjgl.system.SharedLibraryExtractPath=" + nativesPath,
                "-Dio.netty.native.workdir=" + nativesPath,
                "-cp", classPath,
                "-XX:+UseCompactObjectHeaders", "-XX:+AlwaysPreTouch",
                "-XX:+UseStringDeduplication", "-XX:+UseZGC",
                "-Dminecraft.launcher.brand=" + launcherBrand,
                "-Dminecraft.launcher.version=1.7",
                "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump",
                "-Xss1M",
                "net.minecraft.client.main.Main",
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

        if (uname!=null) {
            baseArgs.add("--username");
            baseArgs.add(uname);
        }

        // single ProcessBuilder invocation
        new ProcessBuilder(baseArgs).inheritIO().start().waitFor();
    }

    String getVersion() throws IOException {
        try (var jar = new JarFile("mc.jar");
             var reader = new BufferedReader(new InputStreamReader(
                     jar.getInputStream(jar.getEntry("version.json"))))) {
            reader.readLine(); // skip first line "{"
            return reader.readLine().split("\"")[3]; // extracts version, like "1.21.11"
        }
    }

    String getIndex() throws IOException {
        try (var stream = Files.newDirectoryStream(Path.of("assets","indexes"))) {
            var s = stream.iterator().next().getFileName().toString();
            return s.substring(0, s.length()-5);
        }
    }

}