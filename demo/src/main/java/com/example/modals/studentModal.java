package com.example.modals;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;

import org.bson.Document;

public class studentModal {
    // Method to initialize and return the student collection
    public static MongoCollection<Document> getStudentCollection() {
        // Connect to MongoDB Atlas
        MongoClient client = MongoClients.create("mongodb+srv://ishitagarwal0207:rhl0xhfqX6mdrqHX@cluster0.lhfmmlw.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");

        // Access the database
        MongoDatabase db = client.getDatabase("GradeCardGenerator");

        // Access the student collection
        MongoCollection<Document> studentCollection = db.getCollection("student");

        return studentCollection;
    }

    // Method to define and return the student schema
    public static Document getStudentSchema() {
        // Define the student schema
        Document studentSchema = new Document()
                .append("_id", "")
                .append("student_id", "")
                .append("name", "")
                .append("email", "")
                .append("password", "")
                .append("courses", new Document());

        // Define a course schema within the student schema
        Document courseDocument = new Document()
                .append("course_id", "")
                .append("course_name", "")
                .append("grades", new Document());

        // Define a grade schema within the course schema
        Document gradeDocument = new Document()
                .append("semester", "")
                .append("grade", "")
                .append("marks", new ArrayList<Document>());

        // Define a marks schema within the grade schema
        Document marksDocument = new Document()
                .append("question_number", "")
                .append("course_outcome", "")
                .append("marks_obtained", "");

        // Append the grade and marks schema to the course schema
        gradeDocument.append("marks", marksDocument);
        courseDocument.append("grades", gradeDocument);

        // Append the course schema to the student schema
        studentSchema.append("courses", courseDocument);

        return studentSchema;
    }
}
