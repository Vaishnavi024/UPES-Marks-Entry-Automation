package com.example.controllers;

import static spark.Spark.post;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import static com.mongodb.client.model.Filters.eq;


public class facultyController {
    public static void main() {
        MongoClient client = MongoClients.create("mongodb+srv://ishitagarwal0207:rhl0xhfqX6mdrqHX@cluster0.lhfmmlw.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");

        // Access the database
        MongoDatabase db = client.getDatabase("GradeCardGenerator");

        // Access the student collection
        MongoCollection<Document> facultyCollection = db.getCollection("faculty");
        MongoCollection<Document> studentCollection = db.getCollection("student");
        MongoCollection<Document> courseCollection = db.getCollection("course");

        post("/faculty/signup", (request, response) -> {
            // Parse incoming JSON data
            JSONObject requestBody = new JSONObject(request.body());

            // Validate faculty email
            // ...

            String email = requestBody.getString("email");
            if (!isValidFacultyEmail(email)) {
                response.status(400); // Bad Request
                return "Invalid faculty email format";
            }

            Document existingFaculty = facultyCollection.find(eq("email", email)).first();
            if (existingFaculty != null) {
                response.status(409); // Conflict
                return "Teacher with this email already exists";
            }

            // Hash the password
            String password = requestBody.getString("password");
            String hashedPassword = hashPassword(password);

            // Insert faculty data into MongoDB
            Document facultyDoc = new Document()
                .append("email", email)
                .append("password", hashedPassword);
            facultyCollection.insertOne(facultyDoc);

            return "Faculty signup successful!";
        });

        post("/faculty/login", (request, response) -> {
            // Parse incoming JSON data
            JSONObject requestBody = new JSONObject(request.body());

            // Retrieve email and password from request
            String email = requestBody.getString("email");
            String password = requestBody.getString("password");

            // Verify credentials against database
            if (verifyFacultyCredentials(email, password, facultyCollection)) {
                return "Faculty login successful!";
            } else {
                response.status(401); // Unauthorized
                return "Invalid credentials";
            }
        });

        post("/enterMarks", (req, res) -> {
            Document requestBody = Document.parse(req.body());

            // Extract input data
            ObjectId studentId = requestBody.getObjectId("student_id");
            String courseId = requestBody.getString("course_id");
            Document[] marks = ((List<Document>) requestBody.get("marks")).toArray(new Document[0]);

            // Check if course exists
            Document courseQuery = new Document("course_id", courseId);
            Document course = courseCollection.find(courseQuery).first();
            if (course == null) {
                res.status(400);
                return "Course not found.";
            }

            // Check if student exists
            Document studentQuery = new Document("_id", studentId);
            Document student = studentCollection.find(studentQuery).first();
            if (student == null) {
                res.status(400);
                return "Student not found.";
            }

            // Update student entry with course details and marks
            Document courseDetails = new Document("course_id", courseId)
                    .append("course_name", course.getString("course_name"));

            Document grades = (Document) student.get("courses");
            if (grades == null) {
                grades = new Document();
            }
            Document gradeDetails = new Document("semester", course.getString("semester"))
                    .append("grades", new ArrayList<Document>());
            for (Document mark : marks) {
                Document markDetails = new Document("question_number", mark.getString("question_number"))
                        .append("course_outcome", mark.getString("course_outcome"))
                        .append("marks_obtained", mark.getInteger("marks_obtained"));
                ((List<Document>) gradeDetails.get("grades")).add(markDetails);
            }

            ((Document) grades.get(courseId)).append("grades", gradeDetails);
            studentCollection.updateOne(studentQuery, new Document("$set", new Document("courses", grades)));

            return "Marks entered successfully.";
        });
    }

   
    private static boolean isValidFacultyEmail(String email) {
        return email.matches("\\w+@ddn\\.upes\\.ac\\.in");
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

    private static boolean verifyFacultyCredentials(String email, String password, MongoCollection<Document> facultyCollection) {
        Document query = new Document("email", email).append("password", hashPassword(password));
        return facultyCollection.find(query).first() != null;
    }
}
