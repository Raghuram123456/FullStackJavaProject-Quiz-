package com.OnlineQuizSystem;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.*;
import java.util.*;

@Controller
public class OnlineQuizSystemController {

    private static final String URL = "jdbc:mysql://localhost:3306/quiz_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    @GetMapping("/getLogin")
    public String getLogin() {
        return "Login";
    }

    @GetMapping("/getHome")
    public String getHome(){
        return "Home";
    }

    @GetMapping("/getCard")
    public String getCard(){
        return "Card";
    }

    @GetMapping("/getJavaQuizz")
    public String getJavaQuizz(Model model) {
        model.addAttribute("questionList", fetchQuestionsWithAnswers("questions", "answers"));
        return "JavaQuizz";
    }

    @GetMapping("/getCQuizz")
    public String getCQuizz(Model model) {
        model.addAttribute("cQuestionList", fetchQuestionsWithAnswers("c_questions", "c_answers"));
        return "CQuizz";
    }

    private List<Map<String, Object>> fetchQuestionsWithAnswers(String questionTable, String answerTable) {
        List<Map<String, Object>> listOfQuestions = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "SELECT q.id AS question_id, q.question_text, a.id AS answer_id, a.answer_text, a.is_correct " +
                    "FROM " + questionTable + " q LEFT JOIN " + answerTable + " a ON q.id = a.question_id";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            Map<String, Map<String, Object>> questionMap = new HashMap<>();

            while (rs.next()) {
                String questionId = rs.getString("question_id");
                String questionText = rs.getString("question_text");
                String answerId = rs.getString("answer_id");
                String answerText = rs.getString("answer_text");
                boolean isCorrect = rs.getBoolean("is_correct");

                if (!questionMap.containsKey(questionId)) {
                    Map<String, Object> questionData = new HashMap<>();
                    questionData.put("QuestionId", questionId);
                    questionData.put("QuestionText", questionText);
                    questionData.put("answers", new ArrayList<Map<String, Object>>());
                    questionMap.put(questionId, questionData);
                    listOfQuestions.add(questionData);
                }

                if (answerId != null) {
                    Map<String, Object> answerData = new HashMap<>();
                    answerData.put("AnswerId", answerId);
                    answerData.put("AnswerText", answerText);
                    answerData.put("CorrectAnswer", isCorrect);
                    ((List<Map<String, Object>>) questionMap.get(questionId).get("answers")).add(answerData);
                }
            }

            System.out.println("The map data are " + listOfQuestions);
        } catch (SQLException e) {
            System.out.println("The exception occurred: " + e);
        }
        return listOfQuestions;
    }

    @PostMapping("/submitJavaQuiz")
    public String submitJavaQuiz(@RequestParam Map<String, String> allParams, Model model) {
        processQuizSubmission(allParams, "UserAnswers");
        return "Result";
    }

    @PostMapping("/submitCQuiz")
    public String submitCQuiz(@RequestParam Map<String, String> allParams, Model model) {
        processQuizSubmission(allParams, "CUserAnswers");
        return "Result";
    }

    private void processQuizSubmission(Map<String, String> allParams, String answerTable) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            allParams.forEach((key, value) -> {
                if (key.startsWith("question_")) {
                    System.out.println("Question ID: " + key.substring(9) + ", Selected Answer ID: " + value);
                    String sql = "INSERT INTO " + answerTable + " (userid, question_id, answer_text) VALUES ('R123', ?, ?)";
                    try (PreparedStatement pstatement = connection.prepareStatement(sql)) {
                        pstatement.setInt(1, Integer.parseInt(key.substring(9)));
                        pstatement.setString(2, value);
                        pstatement.execute();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (SQLException e) {
            System.out.println("The exception occurred: " + e);
        }
    }
}



////package com.OnlineQuizSystem;
////
////import org.springframework.stereotype.Controller;
////import org.springframework.ui.Model;
////import org.springframework.web.bind.annotation.GetMapping;
////import org.springframework.web.bind.annotation.ModelAttribute;
////import org.springframework.web.bind.annotation.ResponseBody;
////
////import java.sql.*;
////import java.util.*;
////
////@Controller
////public class OnlineQuizSystemController {
////
////    private static final String URL = "jdbc:mysql://localhost:3306/quiz_db";
////    private static final String USER = "root";
////    private static final String PASSWORD = "root";
////
////    @GetMapping("/getLogin")
////    public String getLogin() {
////        return "Login";
////    }
////
////    @GetMapping("/getHome")
////    public String getHome(){
////        return "Home";
////    }
////
////    @GetMapping("/getCard")
////    public String getCard(){
////        return "Card";
////    }
////
////    @GetMapping("/getJavaQuizz")
////    public String getJavaQuizz(){
////        return "JavaQuizz";
////    }
////
////    @GetMapping("/getQuestions")
////    public String getQuestions(Model model) {
////        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
////        model.addAttribute("questionList",list);
////        return "JavaQuizz";
////
////    }
////
////    @ModelAttribute("questionList")
////    public List<Map<String,Object>> fetchQuestion(){
////        List<Map<String,Object>> listOfQuestions = new ArrayList<>();
////        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
////            String sql = "select * from questions";
////            PreparedStatement statement = connection.prepareStatement(sql);
////            ResultSet rs = statement.executeQuery();
////            while (rs.next()) {
////                Map<String,Object> mp = new HashMap<>();
////                mp.put("QuestionId", rs.getString("id"));
////                mp.put("QuestionText", rs.getString("question_text"));
////                listOfQuestions.add(mp);
////            }
////            System.out.println("The map data are "+listOfQuestions);
////        } catch (SQLException e) {
////            System.out.println("The exception occurred: " + e);
////        }
////        return listOfQuestions;
////    }
////
////
////    @ModelAttribute("answerList")
////    public List<Map<String,Object>> fetchAnswer(){
////        List<Map<String,Object>> listOfAnswers = new ArrayList<>();
////        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
////            String sql = "select * from answers";
////            PreparedStatement statement = connection.prepareStatement(sql);
////            ResultSet rs = statement.executeQuery();
////            while (rs.next()) {
////                Map<String,Object> mp = new HashMap<>();
////                mp.put("AnswerId", rs.getString("id"));
////                mp.put("QuestionId", rs.getString("question_id"));
////                mp.put("AnswerText", rs.getString("answer_text"));
////                mp.put("CorrectAnswer", rs.getString("is_correct"));
////                listOfAnswers.add(mp);
////            }
////            System.out.println("The map data are "+listOfAnswers);
////        } catch (SQLException e) {
////            System.out.println("The exception occurred: " + e);
////        }
////        return listOfAnswers;
////    }
////}
//package com.OnlineQuizSystem;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//
//import java.sql.*;
//import java.util.*;
//
//@Controller
//public class OnlineQuizSystemController {
//
//    private static final String URL = "jdbc:mysql://localhost:3306/quiz_db";
//    private static final String USER = "root";
//    private static final String PASSWORD = "root";
//
//    @GetMapping("/getLogin")
//    public String getLogin() {
//        return "Login";
//    }
//
//    @GetMapping("/getHome")
//    public String getHome() {
//        return "Home";
//    }
//
//    @GetMapping("/getCard")
//    public String getCard() {
//        return "Card";
//    }
//
//    @GetMapping("/getJavaQuizz")
//    public String getJavaQuizz(Model model) {
//        model.addAttribute("questionList", fetchQuestionsWithAnswers());
//        return "JavaQuizz";
//    }
//
//
//    private List<Map<String, Object>> fetchQuestionsWithAnswers() {
//        List<Map<String, Object>> listOfQuestions = new ArrayList<>();
//        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
//            String sql = "SELECT q.id AS question_id, q.question_text, a.id AS answer_id, a.answer_text, a.is_correct " +
//                    "FROM questions q LEFT JOIN answers a ON q.id = a.question_id";
//            PreparedStatement statement = connection.prepareStatement(sql);
//            ResultSet rs = statement.executeQuery();
//
//            Map<String, Map<String, Object>> questionMap = new HashMap<>();
//
//            while (rs.next()) {
//                String questionId = rs.getString("question_id");
//                String questionText = rs.getString("question_text");
//                String answerId = rs.getString("answer_id");
//                String answerText = rs.getString("answer_text");
//                boolean isCorrect = rs.getBoolean("is_correct");
//
//                if (!questionMap.containsKey(questionId)) {
//                    Map<String, Object> questionData = new HashMap<>();
//                    questionData.put("QuestionId", questionId);
//                    questionData.put("QuestionText", questionText);
//                    questionData.put("answers", new ArrayList<Map<String, Object>>());
//                    questionMap.put(questionId, questionData);
//                    listOfQuestions.add(questionData);
//                }
//
//                if (answerId != null) {
//                    Map<String, Object> answerData = new HashMap<>();
//                    answerData.put("AnswerId", answerId);
//                    answerData.put("AnswerText", answerText);
//                    answerData.put("CorrectAnswer", isCorrect);
//                    ((List<Map<String, Object>>) questionMap.get(questionId).get("answers")).add(answerData);
//                }
//            }
//
//            System.out.println("The map data are " + listOfQuestions);
//        } catch (SQLException e) {
//            System.out.println("The exception occurred: " + e);
//        }
//        return listOfQuestions;
//    }
//}




//package com.OnlineQuizSystem;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.sql.*;
//import java.util.*;
//
//@Controller
//public class OnlineQuizSystemController {
//
//    private static final String URL = "jdbc:mysql://localhost:3306/quiz_db";
//    private static final String USER = "root";
//    private static final String PASSWORD = "root";
//
//    @GetMapping("/getLogin")
//    public String getLogin() {
//        return "Login";
//    }
//
//    @GetMapping("/getHome")
//    public String getHome(){
//        return "Home";
//    }
//
//    @GetMapping("/getCard")
//    public String getCard(){
//        return "Card";
//    }
//
//    @GetMapping("/getJavaQuizz")
//    public String getJavaQuizz(Model model) {
//        model.addAttribute("questionList", fetchQuestionsWithAnswers());
//        return "JavaQuizz";
//    }
//
//    private List<Map<String, Object>> fetchQuestionsWithAnswers() {
//        List<Map<String, Object>> listOfQuestions = new ArrayList<>();
//        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
//            String sql = "SELECT q.id AS question_id, q.question_text, a.id AS answer_id, a.answer_text, a.is_correct " +
//                    "FROM questions q LEFT JOIN answers a ON q.id = a.question_id";
//            PreparedStatement statement = connection.prepareStatement(sql);
//            ResultSet rs = statement.executeQuery();
//
//            Map<String, Map<String, Object>> questionMap = new HashMap<>();
//
//            while (rs.next()) {
//                String questionId = rs.getString("question_id");
//                String questionText = rs.getString("question_text");
//                String answerId = rs.getString("answer_id");
//                String answerText = rs.getString("answer_text");
//                boolean isCorrect = rs.getBoolean("is_correct");
//
//                if (!questionMap.containsKey(questionId)) {
//                    Map<String, Object> questionData = new HashMap<>();
//                    questionData.put("QuestionId", questionId);
//                    questionData.put("QuestionText", questionText);
//                    questionData.put("answers", new ArrayList<Map<String, Object>>());
//                    questionMap.put(questionId, questionData);
//                    listOfQuestions.add(questionData);
//                }
//
//                if (answerId != null) {
//                    Map<String, Object> answerData = new HashMap<>();
//                    answerData.put("AnswerId", answerId);
//                    answerData.put("AnswerText", answerText);
//                    answerData.put("CorrectAnswer", isCorrect);
//                    ((List<Map<String, Object>>) questionMap.get(questionId).get("answers")).add(answerData);
//                }
//            }
//
//            System.out.println("The map data are " + listOfQuestions);
//        } catch (SQLException e) {
//            System.out.println("The exception occurred: " + e);
//        }
//        return listOfQuestions;
//    }
//
//    @PostMapping("/submitQuiz")
//    public String submitQuiz(@RequestParam Map<String, String> allParams, Model model) {
//        // Process the submitted answers
//        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
//        allParams.forEach((key, value) -> {
//            if (key.startsWith("question_")) {
//                System.out.println("Question ID: " + key.substring(9) + ", Selected Answer ID: " + value);
//                String sql = "insert into UserAnswers values ('R123',?,?)";
//                PreparedStatement pstatement = null;
//                try {
//                    pstatement = connection.prepareStatement(sql);
//                    pstatement.setInt(1, Integer.parseInt(key.substring(9)));
//                    pstatement.setString(2, value);
//                    pstatement.execute();
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//
//
//                Map<String, Map<String, Object>> questionMap = new HashMap<>();
//            }
//        });
//
//        }catch (SQLException e) {
//                System.out.println("The exception occurred: " + e);
//            }
//        // Return to a results page or some other view
//        return "Home";
//    }
//}
