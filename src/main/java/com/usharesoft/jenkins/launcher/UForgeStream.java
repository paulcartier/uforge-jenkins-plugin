package com.usharesoft.jenkins.launcher;

import com.usharesoft.jenkins.UForgeEnvironmentVariables;

import java.io.FilterOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UForgeStream extends FilterOutputStream {
    private UForgeEnvironmentVariables uForgeEnvVars;

    UForgeStream(OutputStream out, UForgeEnvironmentVariables uForgeEnvVars) {
        super(out);
        this.uForgeEnvVars = uForgeEnvVars;
    }

    public void println(String x) {
        ((PrintStream) out).println(x);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        String logs = new String(b, StandardCharsets.UTF_8).substring(off, len);

        logs = removeANSICodes(logs);

        for (String line: logs.split("\n")) {
            println(line);
            fillApplianceIdEnvVar(line);
            fillImageIdEnvVar(line);
        }
    }

    private String removeANSICodes(String logs) {
        return logs.replaceAll("\\x1b\\[[0-9;]*m", "");
    }

    void fillApplianceIdEnvVar(String line) {
        Pattern r = Pattern.compile("^Template Id\\D*(\\d*)$");
        Matcher m = r.matcher(line);
        if (m.find()) {
            uForgeEnvVars.addEnvVar("UFORGE_APPLIANCE_ID", m.group(1));
        }
    }

    void fillImageIdEnvVar(String line) {
        Pattern r = Pattern.compile("^Image Id\\D*(\\d*)$");
        Matcher m = r.matcher(line);
        if (m.find()) {
            uForgeEnvVars.addEnvVar("UFORGE_IMAGE_ID", m.group(1));
        }
    }
}

