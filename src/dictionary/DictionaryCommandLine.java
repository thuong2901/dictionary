package dictionary;

import java.util.Scanner;

public class DictionaryCommandLine extends DictionaryManagement {
    public void showAllWords() {
        System.out.println("No        | English                  | Vietnamese");
        for(int i = 0; i<words.size(); i++){
            System.out.format("%-10d",i);
            System.out.format("%-27s","| " + words.get(i).getWord_target());
            System.out.print("| " + words.get(i).getWord_explain() + "\n");
        }
    }
    public void dictionaryBasic() {
        insertFromCommandline();
        showAllWords();
    }

    public void dictionaryAdvanced() throws Exception {
        insertFromFile();
        showAllWords();
        dictionaryLookup();
        dictionarySearcher();
        dictionaryExportToFile();
    }

    public void dictionarySearcher() {
        Scanner scan = new Scanner(System.in);
        String search = scan.nextLine().toLowerCase();
        for(int i = 0; i < getNumOfWord(); i++) {
            if(search.length() < words.get(i).getWord_target().length()){
                if(search.equals(words.get(i).getWord_target().substring(0,search.length()))) {
                    System.out.println(words.get(i).getWord_target());
                }
            }
        }
    }

}
