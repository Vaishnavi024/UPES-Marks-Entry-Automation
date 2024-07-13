package com.example;

// import com.mongodb.client.MongoCollection;
// import org.bson.Document;
// import org.json.JSONObject;
// import static spark.Spark.*;
// import com.example.modals.studentModal;
// import com.example.modals.facultyModal;
import com.example.controllers.studentController;
import com.example.controllers.courseController;
import com.example.controllers.facultyController;
import com.example.client.facultyMarksEntry;
import com.example.client.login;
import com.example.client.signup;

// public class Main {
//     public static void main(String[] args) {
//         System.out.println("Hello");
        
//         studentController.main();
//         facultyController.main();
//         courseController.main();
        
//         post("/students", (request, response) -> {
//                 // Parse incoming JSON data
//                 JSONObject requestBody = new JSONObject(request.body());
            
//                 // Get the student collection
//                 MongoCollection<Document> studentCollection = studentModal.getStudentCollection();
            
//                 // Insert student data into MongoDB
//                 studentCollection.insertOne(Document.parse(requestBody.toString()));
            
//                 return "Student data inserted successfully!";
//         });
            

//         post("/faculty", (request, response) -> {
//                 // Parse incoming JSON data
//                 JSONObject requestBody = new JSONObject(request.body());
                
//                 MongoCollection<Document> facultyCollection = facultyModal.getFacultyCollection();

//                 facultyCollection.insertOne(Document.parse(requestBody.toString()));
    
//                 return "Faculty data inserted successfully!";
//         });
//     }
// }

//package gcg;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
public class Main {

	public static void main(String[] args) {
        studentController.main();
        facultyController.main();
        courseController.main();
        signup.main(args);
        //login.main();
        //facultyMarksEntry.main(args);
        
        try {
            // Create a workbook
            Workbook workbook = new HSSFWorkbook();

            // Create a sheet
            Sheet sheet = workbook.createSheet("workbook");

            // Create a Scanner object to take user input
            Scanner scanner = new Scanner(System.in);

            // Ask user for the number of times to repeat the process
            System.out.print("Enter the number of times to repeat: ");
            int repeatCount = scanner.nextInt();
            scanner.nextLine(); // consume the newline character

            for (int i = 0; i < repeatCount; i++) {
                // Ask user for input
                System.out.print("Enter 5 space-separated values: ");
                String inputLine = scanner.nextLine();

                // Split the input line into individual values
                String[] values = inputLine.split("\\s+");

                // Create a row
                Row row = sheet.createRow(i);

                // Create cells and set values from user input
                for (int j = 0; j < values.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(values[j]);
                }
            }

            // Write the workbook content to a file
            FileOutputStream fileOut = new FileOutputStream("workbook.xls");
            workbook.write(fileOut);
            fileOut.close();

            // Close scanner
            scanner.close();

            // Close workbook
            workbook.close();

            System.out.println("Data entered successfully into Excel sheet.");

        } catch (IOException e) {
            e.printStackTrace();
    }


    }

}