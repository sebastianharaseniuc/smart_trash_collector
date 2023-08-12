package org.example.main;

import org.example.communication_and_database.DatabaseConnection;
import org.example.enums.TrashType;
import org.example.materials.Trash;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.example.communication_and_database.CommunicationModule;
import org.example.exception.TrashIsFullException;
import org.example.materials.PlasticTrash;
import org.example.materials.PaperTrash;
import org.example.materials.GlassTrash;
import org.example.person.Person;
import org.example.recycling_units.Unit;

public class Main {

    public static void main(String[] args) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.initializeDatabaseTables();
        List<Unit> availableUnits = databaseConnection.initializeDatabaseDataIntoTables();

        Person user = new Person("User");

        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Enter the number of the unit location:\n1. Zorilor\n2. Manastur\n3. Marasti");
            int unitChoice = scanner.nextInt();
            scanner.nextLine();

            if (unitChoice < 1 || unitChoice > availableUnits.size()) {
                System.out.println("Invalid unit number. Exiting...");
                return;
            }

            Unit selectedUnit = availableUnits.get(unitChoice - 1);
            user.setUnit(selectedUnit);

            while (true) {
                System.out.println("Enter the number of the category of recyclable trash (type '0' to stop): ");
                System.out.println("1. Plastic");
                System.out.println("2. Paper");
                System.out.println("3. Glass");

                String userInput = scanner.nextLine();
                if (userInput.equalsIgnoreCase("0")) {
                    System.out.println("Exiting...");
                    break;
                }

                boolean validInput = true;
                TrashType trashType = getTrashTypeByChoice(Integer.parseInt(userInput));

                if (trashType != null) {
                    Trash trash = createTrashItem(trashType);

                    if (trash != null) {
                        try {
                            user.addNewGarbage(trash);
                            selectedUnit.receives(user.getGarbage());
                        } catch (TrashIsFullException e) {
                            System.out.println("Error: " + e.getMessage());
                            System.out.println("There is too much trash to handle, please add fewer items");
                            validInput = false;
                        }
                    } else {
                        validInput = false;
                    }
                } else {
                    System.out.println("Invalid trash category. Please try again.");
                    validInput = false;
                }

                if (!validInput) {
                    System.out.println("Please try again.");
                }
            }

            double plasticCapacityMain = databaseConnection.getCurrentCapacityForHolder(selectedUnit.getPlasticHolder().getHolderId());
            double paperCapacityMain = databaseConnection.getCurrentCapacityForHolder(selectedUnit.getPaperHolder().getHolderId());
            double glassCapacityMain = databaseConnection.getCurrentCapacityForHolder(selectedUnit.getGlassHolder().getHolderId());

            CommunicationModule.sendDataToUser("Plastic Holder", plasticCapacityMain);
            CommunicationModule.sendDataToUser("Paper Holder", paperCapacityMain);
            CommunicationModule.sendDataToUser("Glass Holder", glassCapacityMain);


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unknown error occurred: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static TrashType getTrashTypeByChoice(int categoryChoice) {
        switch (categoryChoice) {
            case 1:
                return TrashType.PLASTIC;
            case 2:
                return TrashType.PAPER;
            case 3:
                return TrashType.GLASS;
            default:
                return null;
        }
    }

    private static TrashType getTrashTypeByName(String trashName) {
        for (TrashType type : TrashType.values()) {
            if (type.name().equalsIgnoreCase(trashName)) {
                return type;
            }
        }
        return null;
    }

    private static Trash createTrashItem(TrashType trashType) {
        Scanner scanner = new Scanner(System.in);
        switch (trashType) {
            case PLASTIC:
                System.out.println("Available plastic trash items:");
                int index = 1;
                for (TrashType.PlasticTrashItem item : TrashType.PlasticTrashItem.values()) {
                    System.out.println(index + ". " + item.name() + " - Weight: " + item.getWeight());
                    index++;
                }
                System.out.println("Enter the number of the plastic trash item (or 0 to cancel): ");
                int selectedItemNumber = scanner.nextInt();
                scanner.nextLine();

                if (selectedItemNumber == 0) {
                    System.out.println("Operation canceled.");
                    return null;
                } else if (selectedItemNumber < 1 || selectedItemNumber > TrashType.PlasticTrashItem.values().length) {
                    System.out.println("Invalid item number. Please try again.");
                    return null;
                }

                TrashType.PlasticTrashItem selectedPlasticItem = TrashType.PlasticTrashItem.values()[selectedItemNumber - 1];

                if (selectedPlasticItem == TrashType.PlasticTrashItem.USED_VACCINE) {
                    System.out.println("Error: USED_VACCINE is non-recyclable and cannot be added.");
                    return null;
                }

                System.out.println("Enter the quantity of the plastic trash item: ");
                int quantity = scanner.nextInt();
                scanner.nextLine();

                double totalWeight = selectedPlasticItem.getWeight() * quantity;
                PlasticTrash plasticTrash = new PlasticTrash(selectedPlasticItem.name(), totalWeight);

                return plasticTrash;

            case GLASS:
                System.out.println("Available glass trash items:");
                index = 1;
                for (TrashType.GlassTrashItem item : TrashType.GlassTrashItem.values()) {
                    System.out.println(index + ". " + item.name() + " - Weight: " + item.getWeight());
                    index++;
                }
                System.out.println("Enter the number of the glass trash item (or 0 to cancel): ");
                selectedItemNumber = scanner.nextInt();
                scanner.nextLine();

                if (selectedItemNumber == 0) {
                    System.out.println("Operation canceled.");
                    return null;
                } else if (selectedItemNumber < 1 || selectedItemNumber > TrashType.GlassTrashItem.values().length) {
                    System.out.println("Invalid item number. Please try again.");
                    return null;
                }

                TrashType.GlassTrashItem selectedGlassItem = TrashType.GlassTrashItem.values()[selectedItemNumber - 1];

                System.out.println("Enter the quantity of the glass trash item: ");
                quantity = scanner.nextInt();
                scanner.nextLine();

                totalWeight = selectedGlassItem.getWeight() * quantity;
                GlassTrash glassTrash = new GlassTrash(selectedGlassItem.name(), totalWeight);

                return glassTrash;


            case PAPER:
                System.out.println("Available paper trash items:");
                index = 1;
                for (TrashType.PaperTrashItem item : TrashType.PaperTrashItem.values()) {
                    System.out.println(index + ". " + item.name() + " - Weight: " + item.getWeight());
                    index++;
                }
                System.out.println("Enter the number of the paper trash item (or 0 to cancel): ");
                selectedItemNumber = scanner.nextInt();
                scanner.nextLine();

                if (selectedItemNumber == 0) {
                    System.out.println("Operation canceled.");
                    return null;
                } else if (selectedItemNumber < 1 || selectedItemNumber > TrashType.PaperTrashItem.values().length) {
                    System.out.println("Invalid item number. Please try again.");
                    return null;
                }

                TrashType.PaperTrashItem selectedPaperItem = TrashType.PaperTrashItem.values()[selectedItemNumber - 1];

                System.out.println("Enter the quantity of the paper trash item: ");
                quantity = scanner.nextInt();
                scanner.nextLine();

                totalWeight = selectedPaperItem.getWeight() * quantity;
                PaperTrash paperTrash = new PaperTrash(selectedPaperItem.name(), totalWeight);

                return paperTrash;

            default:
                System.out.println("Invalid trash type. Please try again.");
                return null;
        }
    }
}