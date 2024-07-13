package com.example.controllers;

import static spark.Spark.post;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.json.JSONObject;
import org.apache.poi.ss.usermodel.*;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class studentController {
    public static void main() {
        MongoClient client = MongoClients.create("mongodb+srv://ishitagarwal0207:rhl0xhfqX6mdrqHX@cluster0.lhfmmlw.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");

        // Access the database
        MongoDatabase db = client.getDatabase("GradeCardGenerator");

        // Access the student collection
        MongoCollection<Document> studentCollection = db.getCollection("student");

        post("/students/signup", (request, response) -> {
            // Parse incoming JSON data
            JSONObject requestBody = new JSONObject(request.body());

            // Validate student email
            String email = requestBody.getString("email");
            if (!isValidStudentEmail(email)) {
                response.status(400); // Bad Request
                return "Invalid student email format";
            }

            // Hash the password
            String password = requestBody.getString("password");
            String hashedPassword = hashPassword(password);

            // Insert student data into MongoDB
            Document studentDoc = new Document()
                .append("email", email)
                .append("password", hashedPassword);
            studentCollection.insertOne(studentDoc);

            return "Student signup successful!";
        });

        post("/students/login", (request, response) -> {
            // Parse incoming JSON data
            JSONObject requestBody = new JSONObject(request.body());

            // Retrieve email and password from request
            String email = requestBody.getString("email");
            String password = requestBody.getString("password");

            // Verify credentials against database
            if (verifyStudentCredentials(email, password, studentCollection)) {
                return "Student login successful!";
            } else {
                response.status(401); 
                return "Invalid credentials";
            }
        });

        post("/submitData", (req, res) -> {
            int numQuestions = Integer.parseInt(req.queryParams("numQuestions"));
            int numCOs = Integer.parseInt(req.queryParams("numCOs"));
    
            Map<Integer, Integer> questionToCO = new HashMap<>();
            // Here, you can prompt for question-CO mapping if needed
    
            try (Workbook workbook = new HSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("QuestionCO Mapping");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Question");
                headerRow.createCell(1).setCellValue("CO");
    
                int rowNum = 1;
                for (Map.Entry<Integer, Integer> entry : questionToCO.entrySet()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(entry.getKey());
                    row.createCell(1).setCellValue(entry.getValue());
                }
    
                FileOutputStream fileOut = new FileOutputStream("QuestionCO_Mapping.xlsx");
                workbook.write(fileOut);
                fileOut.close();
                return "Data submitted successfully";
            } catch (IOException e) {
                e.printStackTrace();
                return "Error submitting data";
            }
        });
    }

    private static boolean isValidStudentEmail(String email) {
        return email.matches("\\d+@stu\\.upes\\.ac\\.in");
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean verifyStudentCredentials(String email, String password, MongoCollection<Document> studentCollection) {
        Document query = new Document("email", email).append("password", hashPassword(password));
        return studentCollection.find(query).first() != null;
    }
    
}
