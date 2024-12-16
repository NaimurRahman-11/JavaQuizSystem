import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class JavaQuizSystem {
    public static void main(String[] args) throws IOException, ParseException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("System:> Enter your username\nUser:> ");
        String username = scanner.nextLine();
        System.out.print("System:> Enter password\nUser:> ");
        String password = scanner.nextLine();

        String role = authenticateUser(username, password);

        if (role == null) {
            System.out.println("System:> Invalid credentials. Please try again.");
            return;
        }

        if (role.equals("admin")) {
            System.out.println("System:> Welcome admin! Please create new questions in the question bank.");
            handleAdminActions(scanner);
        } else if (role.equals("student")) {
            System.out.println("System:> Welcome " + username + " to the quiz! We will throw you 10 questions. Each MCQ mark is 1 and no negative marking. Are you ready? Press 's' to start.");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("s")) {
                conductQuiz(scanner);
            } else {
                System.out.println("System:> Quiz canceled.");
            }
        }
    }

    public static String authenticateUser(String username, String password) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONArray users = (JSONArray) parser.parse(new FileReader("E:\\Java\\JavaQuizSystem\\src\\main\\resources\\users.json"));

        for (Object obj : users) {
            JSONObject user = (JSONObject) obj;
            if (user.get("username").equals(username) && user.get("password").equals(password)) {
                return (String) user.get("role");
            }
        }
        return null;
    }


    public static void handleAdminActions(Scanner scanner) throws IOException, ParseException {
        JSONParser parser = new JSONParser();

        JSONArray quizBank = new JSONArray();

        try {
            FileReader reader = new FileReader("E:\\Java\\JavaQuizSystem\\src\\main\\resources\\quiz.json");
            quizBank = (JSONArray) parser.parse(reader);
        } catch (FileNotFoundException e) {
            System.out.println("Quiz file not found.");
        }


        while (true) {
            System.out.println("System:> Input your question");
            System.out.print("Admin:> ");
            String question = scanner.nextLine();

            JSONObject newQuestion = new JSONObject();
            newQuestion.put("question", question);

            System.out.println("System: Input option 1:");
            System.out.print("Admin:> ");
            newQuestion.put("option1", scanner.nextLine());

            System.out.println("System: Input option 2:");
            System.out.print("Admin:> ");
            newQuestion.put("option2", scanner.nextLine());

            System.out.println("System: Input option 3:");
            System.out.print("Admin:> ");
            newQuestion.put("option3", scanner.nextLine());

            System.out.println("System: Input option 4:");
            System.out.print("Admin:> ");
            newQuestion.put("option4", scanner.nextLine());

            System.out.println("System: What is the answer key?");
            System.out.print("Admin:> ");
            newQuestion.put("answer", scanner.nextLine());

            quizBank.add(newQuestion);

            System.out.println("System:> Saved successfully! Do you want to add more questions? (press s for start and q for quit)");
            System.out.print("Admin:> ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("q")) {
                break;
            }
        }


        try (FileWriter writer = new FileWriter("E:\\Java\\JavaQuizSystem\\src\\main\\resources\\quiz.json")) {
            writer.write(quizBank.toJSONString());
        }

    }

    public static void conductQuiz(Scanner scanner) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONArray quizBank = (JSONArray) parser.parse(new FileReader("E:\\Java\\JavaQuizSystem\\src\\main\\resources\\quiz.json"));

        List<JSONObject> questionsList = new java.util.ArrayList<>();
        for (Object obj : quizBank) {
            questionsList.add((JSONObject) obj);
        }

        Collections.shuffle(questionsList);

        int score = 0;
        int questionCount = Math.min(10, quizBank.size());

        for (int i = 0; i < questionCount; i++) {
            JSONObject question = questionsList.get(i);
            System.out.println("\n[Question " + (i + 1) + "] " + question.get("question"));
            System.out.println("1. " + question.get("option1"));
            System.out.println("2. " + question.get("option2"));
            System.out.println("3. " + question.get("option3"));
            System.out.println("4. " + question.get("option4"));

            System.out.print("Student:> ");
            String answer = scanner.nextLine();

            try {
                int selectedOption = Integer.parseInt(answer);

                if (selectedOption >= 1 && selectedOption <= 4) {
                    if (String.valueOf(selectedOption).equals(question.get("answer"))) {
                        score++;
                    }
                } else {
                    System.out.println("System:> Invalid input. Please choose a number between 1 and 4. Moving to the next question.");
                }
            } catch (NumberFormatException e) {
                System.out.println("System:> Invalid input. Please enter a valid number. Moving to the next question.");
            }
        }


        if (score >= 8) {
            System.out.println("System:> Excellent! You have got " + score + " out of " + questionCount);
        } else if (score >= 5) {
            System.out.println("System:> Good. You have got " + score + " out of " + questionCount);
        } else if (score >= 2) {
            System.out.println("System:> Very poor! You have got " + score + " out of " + questionCount);
        } else {
            System.out.println("System:> Very sorry you are failed. You have got " + score + " out of " + questionCount);
        }

        System.out.println("\nWould you like to start again? Press 's' for start or 'q' for quit.");
        System.out.print("Student:> ");
        String choice = scanner.nextLine();

        while (!choice.equalsIgnoreCase("s") && !choice.equalsIgnoreCase("q")) {
            System.out.println("System:> Invalid input. Please press 's' to start or 'q' to quit.");
            System.out.print("Student:> ");
            choice = scanner.nextLine();
        }

        if (choice.equalsIgnoreCase("s")) {
            conductQuiz(scanner);
        } else {
            System.out.println("System:> Thank you for participating!");
        }
    }
}
