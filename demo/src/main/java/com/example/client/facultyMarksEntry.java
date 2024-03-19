package com.example.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class facultyMarksEntry extends JFrame {
    private JTextField filePathField;
    private JTextField numQuestionsField;
    private JTextField numCOsField;
    private JTextArea outputArea;

    public facultyMarksEntry() {
        setTitle("Marks Entry System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Excel File Path:"));
        filePathField = new JTextField();
        add(filePathField);

        add(new JLabel("Number of Questions:"));
        numQuestionsField = new JTextField();
        add(numQuestionsField);

        add(new JLabel("Number of COs:"));
        numCOsField = new JTextField();
        add(numCOsField);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new SubmitButtonListener());
        add(submitButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane);

        setVisible(true);
    }

    private class SubmitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String filePath = filePathField.getText();
            int numQuestions = Integer.parseInt(numQuestionsField.getText());
            int numCOs = Integer.parseInt(numCOsField.getText());

            try (FileInputStream fileInputStream = new FileInputStream(filePath);
                 Workbook workbook = new HSSFWorkbook(fileInputStream)) {

                Sheet sheet = workbook.getSheet("Sheet1");

                // Mapping questions to Course Outcomes
                String[] coMapping = new String[numQuestions];
                int[] maxMarks = new int[numQuestions];

                // Prompting the teacher to enter the CO and maximum marks for each question
                for (int i = 0; i < numQuestions; i++) {
                    String co = JOptionPane.showInputDialog("Enter the CO for question " + (i + 1) + ": ");
                    coMapping[i] = co;
                    maxMarks[i] = Integer.parseInt(JOptionPane.showInputDialog("Enter the maximum marks for question " + (i + 1) + ": "));
                }

                // Asking for the number of answer sheets
                int numAnswerSheets = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of answer sheets: "));

                // Loop for entering marks for each answer sheet
                for (int s = 0; s < numAnswerSheets; s++) {
                    String sapIdLast5;
                    int rowNum;
                    do {
                        sapIdLast5 = JOptionPane.showInputDialog("Enter the last 5 digits of the SAP ID for answer sheet " + (s + 1) + ": ");
                        String modifiedSapId = "5000" + sapIdLast5;
                        rowNum = findRowByModifiedSAPID(sheet, modifiedSapId);
                        if (rowNum == -1) {
                            outputArea.append("Student with SAP ID ending with " + sapIdLast5 + " not found. Please try again.\n");
                        }
                    } while (rowNum == -1);

                    int totalMarks = 0; // To store the sum of marks for the current answer sheet

                    for (int i = 0; i < numQuestions; i++) {
                        int marks;
                        do {
                            marks = Integer.parseInt(JOptionPane.showInputDialog("Enter marks for Question " + (i + 1) + " in answer sheet " + (s + 1) + ": "));
                            if (marks > maxMarks[i]) {
                                JOptionPane.showMessageDialog(null, "Entered marks exceed maximum marks for question " + (i + 1) + ". Please enter again.");
                            }
                        } while (marks > maxMarks[i]);

                        totalMarks += marks; // Add marks to totalMarks

                        int columnIndex = determineColumnIndex(i + 1, coMapping[i]);
                        Row row = sheet.getRow(rowNum);
                        Cell cell = row.createCell(columnIndex);
                        cell.setCellValue(marks);
                    }

                    // Fill the total marks in cell EY of the current answer sheet's row
                    int totalMarksColumnIndex = 154; // Column index for column EY
                    Row row = sheet.getRow(rowNum);
                    Cell cell = row.createCell(totalMarksColumnIndex);
                    cell.setCellValue(totalMarks);

                    outputArea.append("Marks updated successfully for answer sheet " + (s + 1) + "!\n");
                }

                try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                    workbook.write(outputStream);
                    outputArea.append("All marks updated successfully!\n");
                } catch (IOException ex) {
                    outputArea.append("Error saving the Excel file: " + ex.getMessage() + "\n");
                }

            } catch (IOException ex) {
                outputArea.append("Error reading the Excel file: " + ex.getMessage() + "\n");
            }
        }
    }

    private int findRowByModifiedSAPID(Sheet sheet, String modifiedSapId) {
        int modifiedSapIdInt = Integer.parseInt(modifiedSapId);
        for (Row row : sheet) {
            Cell cell = row.getCell(2); // Assuming SAP ID is in column C (index 2)
            if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                int sapId = (int) cell.getNumericCellValue();
                if (String.valueOf(sapId).endsWith(modifiedSapId)) {
                    return row.getRowNum();
                }
            }
        }
        return -1; // Return -1 if modified SAP ID is not found
    }

    private int determineColumnIndex(int questionNumber, String co) {
        String coNumber = co.replaceAll("\\D+","");
        return 4 + (6 * (questionNumber - 1)) + (Integer.parseInt(coNumber) - 1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(facultyMarksEntry::new);
    }
}
