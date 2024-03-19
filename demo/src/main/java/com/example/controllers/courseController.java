package com.example.controllers;

import static spark.Spark.post;

import org.json.JSONObject;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class courseController {
    public static void main() {
        MongoClient client = MongoClients.create("mongodb+srv://ishitagarwal0207:rhl0xhfqX6mdrqHX@cluster0.lhfmmlw.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");

        // Access the database
        MongoDatabase db = client.getDatabase("GradeCardGenerator");

        // Access the course collection
        MongoCollection<Document> courseCollection = db.getCollection("course");

        // Define route to post courses
        post("/courses", (request, response) -> {
            // Parse incoming JSON data
            JSONObject requestBody = new JSONObject(request.body());

            // Extract course details from request body
            String program = requestBody.getString("program");
            String semester = requestBody.getString("semester");
            String courseName = requestBody.getString("course_name");
            String facultyName = requestBody.getString("facultyName");
            String courseId = requestBody.getString("course_id");

            // Create a new document for the course
            Document courseDoc = new Document()
                    .append("program", program)
                    .append("semester", semester)
                    .append("course_name", courseName)
                    .append("facultyName", facultyName)
                    .append("course_id", courseId);

            // Insert the course document into the course collection
            courseCollection.insertOne(courseDoc);

            // Set response status and return success message
            response.status(201); // Created
            return "Course added successfully!";
        });
    }
}
