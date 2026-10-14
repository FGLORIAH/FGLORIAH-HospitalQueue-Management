// ACS 311 Week 5: Hospital Queue Management System (Java version)
// Patients get a queue number and are served by priority:
// EMERGENCY first, then PRIORITY (children 5 and under, elderly 65+), then NORMAL.

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class HospitalQueue {

    static final double AVG_MINUTES = 8.5; // average consultation time per patient
    static final String[] CATEGORIES = {"EMERGENCY", "PRIORITY", "NORMAL"};
    static final String[] PREFIX = {"E", "P", "N"};

    static ArrayList<Patient> queue = new ArrayList<>();
    static int[] counters = new int[3];
    static int arrivalCounter = 0;
    static int servedCount = 0;
    static Scanner sc = new Scanner(System.in);

    // One waiting patient
    static class Patient {
        String number, name, category;
        int age, rank, arrival;

        Patient(String number, String name, int age, String category, int rank, int arrival) {
            this.number = number;
            this.name = name;
            this.age = age;
            this.category = category;
            this.rank = rank;
            this.arrival = arrival;
        }
    }

    // Decide the category index from age and emergency flag
    static int getCategory(int age, boolean isEmergency) {
        if (isEmergency) return 0;
        if (age <= 5 || age >= 65) return 1;
        return 2;
    }

    // Order the queue by category rank, then by arrival order
    static void sortQueue() {
        queue.sort(Comparator.comparingInt((Patient p) -> p.rank).thenComparingInt(p -> p.arrival));
    }

    // Estimated waiting time in minutes
    static double estimateWait(int position) {
        return position * AVG_MINUTES;
    }

    // Keep asking until the user types a valid age (0 to 120)
    static int readAge() {
        while (true) {
            System.out.print("Enter patient age: ");
            String text = sc.nextLine().trim();
            try {
                int age = Integer.parseInt(text);
                if (age < 0 || age > 120) {
                    System.out.println("Invalid input: age must be between 0 and 120. Try again.");
                    continue;
                }
                return age;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: age must be a whole number. Try again.");
            }
        }
    }

    // Register a new patient and give them a queue number
    static void registerPatient() {
        System.out.print("Enter patient name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Invalid input: name cannot be empty.");
            return;
        }
        int age = readAge();
        System.out.print("Is this an emergency? (y/n): ");
        boolean isEmergency = sc.nextLine().trim().equalsIgnoreCase("y");

        int c = getCategory(age, isEmergency);
        counters[c]++;
        arrivalCounter++;
        String number = PREFIX[c] + "-" + String.format("%03d", counters[c]);

        queue.add(new Patient(number, name, age, CATEGORIES[c], c, arrivalCounter));
        sortQueue();
        int position = 0;
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).number.equals(number)) position = i;
        }
        System.out.println("Registered: " + name + " | Queue number: " + number + " | Category: " + CATEGORIES[c]);
        System.out.printf("Patients ahead: %d | Estimated wait: %.1f minutes%n", position, estimateWait(position));
    }

    // Call the next patient and remove them from the queue
    static void callNext() {
        if (queue.isEmpty()) {
            System.out.println("The queue is empty. No patient to call.");
            return;
        }
        Patient p = queue.remove(0);
        servedCount++;
        System.out.println("Now calling: " + p.number + " - " + p.name + " (" + p.category + ")");
    }

    // Show every waiting patient in service order
    static void displayQueue() {
        if (queue.isEmpty()) {
            System.out.println("The queue is empty.");
            return;
        }
        System.out.printf("%-5s%-9s%-15s%-6s%-11s%s%n", "Pos", "Number", "Name", "Age", "Category", "Wait (min)");
        for (int i = 0; i < queue.size(); i++) {
            Patient p = queue.get(i);
            System.out.printf("%-5d%-9s%-15s%-6d%-11s%.1f%n", i + 1, p.number, p.name, p.age, p.category, estimateWait(i));
        }
    }

    // Show totals for the day
    static void showSummary() {
        System.out.println("Served so far: " + servedCount + " | Still waiting: " + queue.size());
        for (int c = 0; c < 3; c++) {
            int waiting = 0;
            for (Patient p : queue) {
                if (p.rank == c) waiting++;
            }
            System.out.println("  " + CATEGORIES[c] + ": registered " + counters[c] + ", waiting " + waiting);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Hospital Queue Management System ===");
        while (true) {
            System.out.println("\n1. Register patient\n2. Call next patient\n3. View queue\n4. Summary\n5. Exit");
            System.out.print("Choose an option (1-5): ");
            String choice = sc.nextLine().trim();
            if (choice.equals("1")) registerPatient();
            else if (choice.equals("2")) callNext();
            else if (choice.equals("3")) displayQueue();
            else if (choice.equals("4")) showSummary();
            else if (choice.equals("5")) {
                System.out.println("Goodbye.");
                break;
            } else {
                System.out.println("Invalid option. Please enter a number from 1 to 5.");
            }
        }
    }
}
