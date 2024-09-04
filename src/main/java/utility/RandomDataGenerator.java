package utility;

import java.util.Random;

public class RandomDataGenerator {
    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";

    public static String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHANUMERIC_STRING.length());
            builder.append(ALPHANUMERIC_STRING.charAt(index));
        }
        return builder.toString();
    }
}
