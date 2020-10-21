package dictionary;

import com.sun.speech.freetts.VoiceManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Controller extends DictionaryManagement {

    @FXML
    private TextField WordSearch = new TextField();

    @FXML
    private WebView WordExplain = new WebView();

    //event to search
    @FXML
    public void Search (ActionEvent event) {
        listView.setVisible(false);
        String word_search = WordSearch.getText();
        WebEngine webEngine = WordExplain.getEngine();
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            String sql = "SELECT *" + "FROM av WHERE word = ?";
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setString(1, word_search);
            ResultSet rs = stmt.executeQuery();
            String vie_word = rs.getString("html");
            webEngine.loadContent(vie_word, "text/html");
            rs.close();
            stmt.close();
            c.close();
        }
        catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    //ListView
    @FXML
    private ListView<String> listView = new ListView<>();

    ObservableList<String> listWord = FXCollections.observableArrayList();

    public void loadPre(String preWord) {
        listWord.clear();
        ArrayList<String> preWords = Main.dm.preLookup(preWord);
        for(int i=0; i< preWords.size(); i++) {
            listWord.add(preWords.get(i));
        }
    }

    //TO DO
    public void initialize() {
        listView.setVisible(false);
        listView.setItems(listWord);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        WordSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.isEmpty()) {
                loadPre(newValue);
                listView.setVisible(true);
            } else {
                listWord.clear();
            }
        });
    }

    //select word from listview
    public void listViewPushed(MouseEvent event) {
        String wordSearch = listView.getSelectionModel().getSelectedItem();
        WordSearch.setText(wordSearch);
        WebEngine webEngine = WordExplain.getEngine();
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            c.setAutoCommit(false);
            String sql = "SELECT *" + "FROM av WHERE word = ?";
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setString(1, wordSearch);
            ResultSet rs = stmt.executeQuery();
            String vie_word = rs.getString("html");
            webEngine.loadContent(vie_word, "text/html");
            rs.close();
            stmt.close();
            c.close();
        }
        catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        listView.setVisible(false);
    }

    //Tab Add
    @FXML
    private TextField addWord = new TextField();
    @FXML
    private TextField addWordExplain = new TextField();

    //add a word
    public void add(ActionEvent event) {
        Word add_word = new Word(addWord.getText(),addWordExplain.getText());
        Main.dm.add(add_word);
        //Main.dm.addTodm(add_word);
        addWord.clear();
        addWordExplain.clear();
        WebEngine webEngine = WordExplain.getEngine();
        webEngine.loadContent("");
    }

    //Tab Edit
    @FXML
    private TextField editWord = new TextField();
    @FXML
    private TextField editWordExplain = new TextField();

    //edit a word
    public void edit(ActionEvent event) {
        String original_explain = Main.dm.lookup(WordSearch.getText());
        Word original_word = new Word(WordSearch.getText(), original_explain);
        Word edit_word = new Word(editWord.getText(), editWordExplain.getText());
        Main.dm.edit(edit_word, original_word);
        editWord.clear();
        editWordExplain.clear();
        WordSearch.clear();
        WebEngine webEngine = WordExplain.getEngine();
        webEngine.loadContent("");
    }

    //delete
    public void delete(ActionEvent event) {
        String remove_explain = Main.dm.lookup(WordSearch.getText());
        Word remove_word = new Word(WordSearch.getText(), remove_explain);
        Main.dm.remove(remove_word);
        WordSearch.clear();
        WebEngine webEngine = WordExplain.getEngine();
        webEngine.loadContent("");
    }

    //pronounce
    public void pronounce(ActionEvent event) {
        String word = WordSearch.getText();
        VoiceManager voiceManager = VoiceManager.getInstance();
        com.sun.speech.freetts.Voice synthesizer = voiceManager.getVoice("kevin16");
        synthesizer.allocate();
        synthesizer.speak(word);
        synthesizer.deallocate();
    }


    //translate Tab
    @FXML
    TextField search = new TextField();
    @FXML
    TextArea explain = new TextArea();

    //get api from google translate english to Vietnamese
    public void translateEV(ActionEvent event) throws IOException {
        String e_word = search.getText();
        String urlStr = "https://script.google.com/macros/s/AKfycbzuAVrY273vC30HuSNOGefuuAvCWVEfFWlF_9PitWytSwegno-e/exec" +
                "?q="+ URLEncoder.encode(e_word,"UTF-8") +
                "&target=" + "vi" + "&source" + "en";
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        explain.setText(response.toString());
    }

    //get api from google translate Vietnamese to english
    public void translateVE(ActionEvent event) throws IOException {
        String vi_word = search.getText();
        String urlStr = "https://script.google.com/macros/s/AKfycbzuAVrY273vC30HuSNOGefuuAvCWVEfFWlF_9PitWytSwegno-e/exec" +
                "?q="+ URLEncoder.encode(vi_word,"UTF-8") +
                "&target=" + "en" + "&source" + "vi";
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        explain.setText(response.toString());
    }
}
