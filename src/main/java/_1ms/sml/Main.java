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

import java.io.File;
import java.io.IOException;
import java.util.UUID;

//.jar libs in libraries folder, .dlls in natives folder, from bin, mc jar next to the launcher., assets in assets folder, assetIndex is the num: assets\indexes\[num].json

public class Main {
    public static void main(String[] args) throws IOException {
        String currentDir = System.getProperty("user.dir");
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classPath = currentDir + File.separator + "libraries" + File.separator + "*" + File.pathSeparator + currentDir + File.separator + "1.21.5.jar";
        String nativesPath = currentDir + File.separator + "natives";
        String gameDir = currentDir + File.separator + "MC";
        String assetsDir = currentDir + File.separator + "assets";
        String launcherBrand = "SML";

        ProcessBuilder proc= new ProcessBuilder(
                javaBin,
                "-Xms1024M",
                "-Xmx4096M",
                "-Djava.library.path=" + nativesPath,
                "-Djna.tmpdir=" + nativesPath,
                "-Dorg.lwjgl.system.SharedLibraryExtractPath=" + nativesPath,
                "-Dio.netty.native.workdir="+nativesPath,
                "-cp", classPath,
                "-Xmx2G", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseG1GC", "-XX:G1NewSizePercent=20", "-XX:G1ReservePercent=20", "-XX:MaxGCPauseMillis=50", "-XX:G1HeapRegionSize=32M", //GC
                "-Dminecraft.launcher.brand=" + launcherBrand,
                "-Dminecraft.launcher.version=" + "1.0",
                "-XX:HeapDumpPath=" + "MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump",
                "-Xss1M",
                "net.minecraft.client.main.Main",
                //"--username", "asd",
                "--uuid", UUID.randomUUID().toString(),
                "--clientId", launcherBrand,
                "--xuid", launcherBrand,
                "--version", "1.21.5",
                "--versionType", launcherBrand,
                "--gameDir", gameDir,
                "--assetsDir", assetsDir,
                "--assetIndex", "24",
                "--accessToken", launcherBrand
        );
        proc.inheritIO();
        proc.start();
    }
}