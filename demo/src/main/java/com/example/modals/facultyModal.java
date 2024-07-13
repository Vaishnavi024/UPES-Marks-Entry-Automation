package com.example.modals;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.ArrayList;

public class facultyModal {
    // Method to initialize and return the faculty collection
    public static MongoCollection<Document> getFacultyCollection() {
        // Connect to MongoDB Atlas
        MongoClient client = MongoClients.create("mongodb+srv://ishitagarwal0207:rhl0xhfqX6mdrqHX@cluster0.lhfmmlw.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");

        // Access the database
        MongoDatabase db = client.getDatabase("GradeCardGenerator");

        // Access the faculty collection
        MongoCollection<Document> facultyCollection = db.getCollection("faculty");

        return facultyCollection;
    }

    // Method to define and return the faculty schema
    public static Document getFacultySchema() {
        // Define the faculty schema
        Document facultySchema = new Document()
                .append("_id", "")
                .append("faculty_id", "")
                .append("name", "")
                .append("email", "")
                .append("department", "")
                .append("courses", new ArrayList<Document>());

        // Define a course schema within the faculty schema
        Document courseSchema = new Document()
                .append("course_id", "")
                .append("course_name", "");

        facultySchema.append("courses", courseSchema);

        return facultySchema;
    }
}
