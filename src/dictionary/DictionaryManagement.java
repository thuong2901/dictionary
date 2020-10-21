package dictionary;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.String;

public class DictionaryManagement extends Dictionary {
    private int numOfWord = 0;

    public void setNumOfWord(int num) {
        this.numOfWord=num;
    }

    public int getNumOfWord() {
        return this.numOfWord;
    }

    //insert word from Commandline
    public void insertFromCommandline() {
        Scanner scan = new Scanner(System.in);
        this.setNumOfWord(scan.nextInt());
        scan.nextLine();
        for(int i = 0; i < this.getNumOfWord(); i++) {
            Word newWord = new Word(scan.nextLine(),scan.nextLine());
            words.add(newWord);
        }
    }

    /*public void insertFromFileText() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("dictionaries.txt"));
            String engWord;
            while((engWord = br.readLine()) != null) {
                String vieWord = br.readLine();
                Word newWord = new Word(engWord, vieWord);
                words.add(newWord);
                ++numOfWord;
            };
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    //get data form file .db using sqlite
    public void insertFromFile() {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM av;" );
            while(rs.next()) {
                String eng_word = rs.getString("word");
                String vie_word = rs.getString("description");
                Word newWord = new Word(eng_word, vie_word);
                words.add(newWord);
                ++numOfWord;
            }
            rs.close();
            stmt.close();
            c.close();
        }
        catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    //look up a word from Commandline
    public void dictionaryLookup() {
        System.out.println("\nnhap tu can tra: ");
        Scanner scan = new Scanner(System.in);
        String searchWord = scan.nextLine();
        boolean temp = false;
        for(int i = 0; i < numOfWord; i++) {
            if(searchWord.equals(words.get(i).getWord_target())) {
                System.out.println(words.get(i).getWord_explain());
                temp = true;
            }
        }
        if(!temp) {
            System.out.println("There is not that word in the dictionary");
        }
    }

    //export data to file
    public void dictionaryExportToFile() throws Exception {
        FileWriter writer = new FileWriter("outDictionaries.txt");
        BufferedWriter bw = new BufferedWriter(writer);
        for(int i = 0; i < getNumOfWord(); i++){
            bw.write(words.get(i).getWord_target());
            bw.write("\t");
            bw.write(words.get(i).getWord_explain());
            bw.write("\n");
        }
        bw.close();
    }

    //look up a word in dictionaries
    public String lookup(String search_word) {
        int i;
        for(i=0; i<getNumOfWord(); i++){
            if(words.get(i).getWord_target().equals(search_word)){
                break;
            }
        }
        return words.get(i).getWord_explain();
    }

    //look up words start with preWord
    public ArrayList preLookup (String preWord) {
        ArrayList<String> preWords = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            String sql = "SELECT * FROM av;";
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next() ) {
                String word = rs.getString("word");
                if(word.startsWith(preWord)) {
                    preWords.add(word);
                }
            }
            rs.close();
            stmt.close();
            c.close();
        }
        catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return preWords;
    }

    //add a word to database
    public void add(Word addWord) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            String sql = "INSERT INTO av (id, word, html, description, pronounce) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setString(2, addWord.getWord_target());
            stmt.setString(3, addWord.getWord_explain());
            stmt.execute();
            stmt.close();
            c.commit();
            c.close();
        }
        catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    //edit word in database
    public void edit(Word editWord, Word originalWord) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            String sql1 = "SELECT *" + "FROM av WHERE word = ?";
            PreparedStatement stmt1 = c.prepareStatement(sql1);
            stmt1.setString(1, originalWord.getWord_target());
            ResultSet rs = stmt1.executeQuery();
            int id = rs.getInt("id");
            String sql2 = "UPDATE av SET word = ?," + "html = ?" + "WHERE id = ?";
            PreparedStatement stmt2 = c.prepareStatement(sql2);
            stmt2.setString(1, editWord.getWord_target());
            stmt2.setString(2, editWord.getWord_explain());
            stmt2.setInt(3, id);
            stmt2.executeUpdate();
            stmt1.close();
            stmt2.close();
            rs.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    //delete a word from database
    public void remove(Word removeWord) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            String sql = "DELETE FROM av WHERE word = ?";
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setString(1, removeWord.getWord_target());
            stmt.executeUpdate();
            stmt.close();
            c.commit();
            c.close();
        }
        catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
}
