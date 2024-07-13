package com.example.modals;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class courseModal {

        public static MongoCollection<Document> getCourseCollection() {
        // Connect to MongoDB Atlas
        MongoClient client = MongoClients.create("mongodb+srv://ishitagarwal0207:rhl0xhfqX6mdrqHX@cluster0.lhfmmlw.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");

        // Access the database
        MongoDatabase db = client.getDatabase("GradeCardGenerator");

        // Access the faculty collection
        MongoCollection<Document> courseCollection = db.getCollection("course");

        return courseCollection;
    }

    public static Document getCourseSchema() {
        // Define the faculty schema
        Document courseSchema = new Document()
            .append("program", "")
            .append("semester", "")
            .append("course_name", "")
            .append("facultyName", "")
            .append("course_id", "");

        return courseSchema;
    }
}
