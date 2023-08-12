package org.example.communication_and_database;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class DataLogger {
    public static final String FILE_PATH = "/Users/Seb/IdeaProjects/SmartTrashCollector/src/main/java/garbage_collection_log.txt";

    public static void logGarbageInsertion(String trashType, String location) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            writer.write("Garbage inserted in " + trashType + " holder at: " + timestamp + ", Location: " + location + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to the log file: " + e.getMessage());
        }
    }

    public static void logTrashNotAdded(String trashType, String location) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            writer.write("Failed to add " + trashType + " trash at: " + timestamp + ", Location: " + location + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to the log file: " + e.getMessage());
        }
    }
    public static void logCollectorActivity(String trashType, String location) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            writer.write("Collector came and emptied " + trashType + " holder at: " + timestamp + ", Location: " + location + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to the log file: " + e.getMessage());
        }
    }
}
