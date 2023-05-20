package org.example;

import java.io.*;
import java.util.*;
import java.sql.*;

public class Main {
    private static final String url = "jdbc:h2:file:/Users/Beth/Lab4/db/Files";
    private static final String user = "Beth";
    private static final String password = "6742";

    private static Connection con;
    private static Statement stmt;
    private static Statement stm;
    private static ResultSet rs;

    public static void main(String[] args) {
        try{
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            stm = con.prepareStatement("insert into FILES values (?,?,?)");
            countWords("C://Users//Beth//Desktop");
            rs = stmt.executeQuery("SELECT * FROM FILES");
            while (rs.next()) {
                String a = rs.getObject(1).toString();
                String b = rs.getObject(2).toString();
                String c = rs.getObject(3).toString();
                System.out.println(a + " " + b + " " + c);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try { con.close();} catch (SQLException se) {}
            try { stmt.close();} catch (SQLException se) {}
            try { rs.close();} catch (SQLException se) {}
        }
    }
    public static void countWords(String path) throws IOException, SQLException {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            System.err.println("Нет такой директории" + path);
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                countWords(file.getPath());
            } else if (file.getName().endsWith(".txt")) {
                Map<String, Integer> wordCount = new HashMap<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] words = line.split("\\s+");
                        for (String word : words) {
                            if (!word.isEmpty()) {
                                int count = wordCount.getOrDefault(word.toLowerCase(), 0) + 1;
                                wordCount.put(word.toLowerCase(), count);
                            }
                        }
                    }
                }
                for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                    PreparedStatement stm = con.prepareStatement("insert into FILES values (?,?,?)");
                    stm.setString(1, file.getPath());
                    stm.setString(2, entry.getKey());
                    stm.setInt(3, entry.getValue());
                    stm.executeUpdate();
                }
            }
        }
    }
}