package com.leso.demo.annotation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by DANGNGOCDUC on 6/9/2017.
 */

public class DevLog {

    public static final String LOG_FILE = "I:/APT/log.txt";

    public static void log(Object message) {
        System.out.println("=======================================" + message.toString());
        if (message == null) {
            return;
        }

        // Make sure the path exists.
        new File(LOG_FILE).getParentFile().mkdirs();
        //
        FileWriter writer = null;
        try {
            writer = new FileWriter(LOG_FILE, true);
            writer.append(message.toString());
            writer.append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                writer.close();
            } catch (IOException e1) {
            }
        }
    }
}
