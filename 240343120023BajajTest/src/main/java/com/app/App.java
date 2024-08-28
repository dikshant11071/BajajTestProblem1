package com.app;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class App {

    private static final int STRING_LENGTH = 8;
    
    private static String getDestinationValue(String jsonFilePath) {
        try (FileReader reader = new FileReader(jsonFilePath)) {
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));
            return findDestinationValue(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private static String findDestinationValue(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String result = findDestinationValue((JSONObject) value);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }
        return result.toString();
    }

    private static String generateMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Enter format: java -jar <DestinationHashGenerator.jar> <PRN Number> <path to JSON file>");
            System.exit(1);
        }
        
        String prnNumber = args[0].trim().toLowerCase();
        String jsonFilePath = args[1].trim();
        
        String destinationValue = getDestinationValue(jsonFilePath);
        if (destinationValue == null) {
            System.err.println("Destination key not found in JSON.");
            System.exit(1);
        }

        String randomString = generateRandomString(STRING_LENGTH);
        String concatenatedString = prnNumber + destinationValue + randomString;
        String md5Hash = generateMd5Hash(concatenatedString);

        System.out.println(md5Hash + ";" + randomString);
    }

    
}
