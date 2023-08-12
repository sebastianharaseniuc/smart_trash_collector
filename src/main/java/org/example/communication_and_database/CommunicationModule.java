package org.example.communication_and_database;

public class CommunicationModule {
    public static void sendDataToUser(String holderType, double currentCapacity) {
        System.out.println(holderType + " Current Level: " + currentCapacity);
    }
}
