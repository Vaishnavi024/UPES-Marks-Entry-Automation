package com.example.client;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class facultyMarksEntry extends JFrame {
    private JLabel filePathLabel;
    private JTextField filePathField;
    private JTextField numQuestionsField;
    private JTextField numCOsField;
    private JTextArea outputArea;

    private int numQuestions;
    private String[] coMapping;
    private int[] maxMarks;
    private JFrame answerSheetFrame;
    private String selectedSheetName;

    public facultyMarksEntry() {
        setTitle("Marks Entry System");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel filePanel = new JPanel(new BorderLayout());
        add(filePanel, BorderLayout.NORTH);

        filePathLabel = new JLabel("Excel File:");
        filePanel.add(filePathLabel, BorderLayout.WEST);

        filePathField = new JTextField();
        filePathField.setEditable(false);
        filePanel.add(filePathField, BorderLayout.CENTER);

        JButton chooseFileButton = new JButton("Choose File");
        filePanel.add(chooseFileButton, BorderLayout.EAST);
        chooseFileButton.addActionListener(e -> chooseFile());

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10);

        add(inputPanel, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Number of Questions:"), gbc);

        numQuestionsField = new JTextField(5);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        inputPanel.add(numQuestionsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Number of COs:"), gbc);

        numCOsField = new JTextField(5);
        gbc.gridx = 1;
        inputPanel.add(numCOsField, gbc);

        JButton submitButton = new JButton("Submit");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; 
        gbc.weightx = 0; 
        inputPanel.add(submitButton, gbc);
        submitButton.addActionListener(e -> processInput());

        outputArea = new JTextArea(5, 30);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx", "xls"));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(fileChooser.getSelectedFile().getPath());
            try {
                selectedSheetName = selectSheet(fileChooser.getSelectedFile().getPath());
            } catch (IOException ex) {
                ex.printStackTrace();
                outputArea.append("Error reading Excel file: " + ex.getMessage() + "\n");
            }
        }
    }

    private String selectSheet(String filePath) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = new HSSFWorkbook(fileInputStream)) {
            int numberOfSheets = workbook.getNumberOfSheets();
            String[] sheetNames = new String[numberOfSheets];
            for (int i = 0; i < numberOfSheets; i++) {
                sheetNames[i] = workbook.getSheetName(i);
            }
            return (String) JOptionPane.showInputDialog(null, "Choose sheet:", "Select Sheet", JOptionPane.QUESTION_MESSAGE, null, sheetNames, sheetNames[0]);
        }
    }

    private void processInput() {
        try {
            int numQuestions = Integer.parseInt(numQuestionsField.getText());
            createQuestionForm(numQuestions);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer for the number of questions.");
        }
    }

    private void createQuestionForm(int numOfQuestions) {
        JFrame questionFrame = new JFrame("Question Details");
        questionFrame.setSize(600, 400);
        questionFrame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();// Rows for questions + 1 header row
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding between components
        gbc.anchor = GridBagConstraints.WEST;

        // Header row
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Question Number"), gbc);
        gbc.gridx = 1;
        inputPanel.add(new JLabel("CO"), gbc);
        gbc.gridx = 2;
        inputPanel.add(new JLabel("Maximum Marks"), gbc);
        System.out.println(numOfQuestions);
        JTextField[] coFields = new JTextField[numOfQuestions];
        JTextField[] maxMarksFields = new JTextField[numOfQuestions];
        // Dynamically generate input fields for each question
        for (int i = 0; i < numOfQuestions; i++) {
            int questionNum = i + 1;
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            //inputPanel.add(new JLabel(String.valueOf(questionNum))); // Question number
            inputPanel.add(new JLabel("Q" + questionNum), gbc);

            gbc.gridx = 1;
            coFields[i] = new JTextField(10);
            //coFields[i].setPreferredSize(new Dimension(50, 20)); // CO field
            inputPanel.add(coFields[i], gbc);
            
            gbc.gridx = 2;
            maxMarksFields[i] = new JTextField(10);
            //maxMarksFields[i].setPreferredSize(new Dimension(50, 20));
            inputPanel.add(maxMarksFields[i], gbc);
        }

        JButton submitButton = new JButton("Submit");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(submitButton);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        questionFrame.add(new JScrollPane(inputPanel), BorderLayout.CENTER);
        questionFrame.add(buttonPanel, BorderLayout.SOUTH);
    
        submitButton.addActionListener(e -> {
            try {
                coMapping = new String[numOfQuestions];
                maxMarks = new int[numOfQuestions];
    
                for (int i = 0; i < numOfQuestions; i++) {
                    coMapping[i] = coFields[i].getText();
                    maxMarks[i] = Integer.parseInt(maxMarksFields[i].getText());
                }
                System.out.println(maxMarks[0]);

                int numAnswerSheets = Integer.parseInt(JOptionPane.showInputDialog("Enter the number of answer sheets: "));
                for (int s = 0; s < numAnswerSheets; s++) {
                    createAnswerSheetForm(numOfQuestions, numAnswerSheets);
                }
    
                questionFrame.dispose(); // Close the question form after submission
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(questionFrame, "Please enter valid numbers for Maximum Marks and Answer Sheets.");
            }
        });
        buttonPanel = new JPanel();
        buttonPanel.add(submitButton);

        JScrollPane scrollPane = new JScrollPane(inputPanel);
        questionFrame.add(scrollPane, BorderLayout.CENTER);
        questionFrame.add(buttonPanel, BorderLayout.SOUTH);
    
        questionFrame.setVisible(true);
    }    

    private void createAnswerSheetForm(int numOfQuestions, int remainingAnswerSheets) {
        if (remainingAnswerSheets <= 0) {
            return; // Exit the method if there are no remaining answer sheets
        }
        answerSheetFrame = new JFrame("Answer Sheet");
        answerSheetFrame.setSize(400, 300);
        answerSheetFrame.setLayout(new GridLayout(numOfQuestions + 2, 2));
    
        JLabel sapIdLabel = new JLabel("SAP ID:");
        JTextField sapIdField = new JTextField();
        answerSheetFrame.add(sapIdLabel);
        answerSheetFrame.add(sapIdField);
    
        JTextField[] marksFields = new JTextField[numOfQuestions];
        for (int i = 0; i < numOfQuestions; i++) {
            JLabel questionLabel = new JLabel("Question " + (i + 1) + ":");
            marksFields[i] = new JTextField();
            answerSheetFrame.add(questionLabel);
            answerSheetFrame.add(marksFields[i]);
        }
    
        JButton saveButton = new JButton("Save");
        answerSheetFrame.add(saveButton);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveStudentData(numOfQuestions, sapIdField.getText(), marksFields, remainingAnswerSheets);
                createAnswerSheetForm(numOfQuestions, remainingAnswerSheets - 1);
            }
        });
    
        answerSheetFrame.setVisible(true);
    }
    
    private void saveStudentData(int numOfQuestions, String sapId, JTextField[] marksFields, int numAnswerSheets) {
        String filePath = filePathField.getText();
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = new HSSFWorkbook(fileInputStream)) {
    
            Sheet sheet = workbook.getSheet(selectedSheetName);
            int rowNum = findRowByModifiedSAPID(sheet, "5000" + sapId); // Assuming '5000' is a prefix
            if (rowNum == -1) {
                JOptionPane.showMessageDialog(null, "Student with SAP ID " + sapId + " not found. Please try again.");
                return;
            }
    
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }
    
            int totalMarks = 0;
            for (int i = 0; i < numOfQuestions; i++) {
                int marks = Integer.parseInt(marksFields[i].getText());
                if (marks > maxMarks[i]) {
                    JOptionPane.showMessageDialog(null, "Entered marks exceed maximum marks for question " + (i + 1) + ". Please enter again.");
                    return;
                }
                Cell cell = row.createCell(determineColumnIndex(i + 1, coMapping[i]));
                cell.setCellValue(marks);
                totalMarks += marks;
            }
    
            Cell totalMarksCell = row.createCell(154); // Assuming 154 is the total marks column index
            totalMarksCell.setCellValue(totalMarks);
    
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
            JOptionPane.showMessageDialog(null, "Marks updated successfully for SAP ID " + sapId + "!");
    
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error processing the Excel file: " + ex.getMessage());
        }
    
        // Close the current form and potentially open a new one for the next student
        answerSheetFrame.dispose(); 
        
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
