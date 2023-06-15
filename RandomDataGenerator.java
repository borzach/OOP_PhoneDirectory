import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RandomDataGenerator {
    private static final int NUMBER_OF_LINES = 500;
    private static final String FILE_NAME = "random_data.txt";

    public static void main(String[] args) {
        generateRandomData();
    }

    public static void generateRandomData() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
            Random random = new Random();

            for (int i = 0; i < NUMBER_OF_LINES; i++) {
                String name = generateRandomName();
                String phoneNumber = generateRandomPhoneNumber();
                writer.write(name + ", " + phoneNumber + "\n");
            }

            writer.close();
            System.out.println("Random data generation completed successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred while generating random data: " + e.getMessage());
        }
    }

    public static String generateRandomName() {
        String[] names = {"John", "Jane", "David", "Sarah", "Michael", "Emily", "Robert", "Olivia", "William", "Ava"};
        Random random = new Random();
        int randomIndex = random.nextInt(names.length);
        return names[randomIndex];
    }

    public static String generateRandomPhoneNumber() {
        Random random = new Random();
        StringBuilder phoneNumber = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int digit = random.nextInt(10);
            phoneNumber.append(digit);
        }

        return phoneNumber.toString();
    }
}
