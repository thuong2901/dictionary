package dictionary;

public class Word {
    private String word_target;
    private String word_explain;

    public void setWord_target(String w_target) {
        this.word_target=w_target;
    }

    public String getWord_target() {
        return this.word_target;
    }

    public void setWord_explain(String w_explain) {
        this.word_explain = w_explain;
    }

    public String getWord_explain() {
        return this.word_explain;
    }

    public Word() {
        this.word_explain = new String();
        this.word_target = new String();
    }

    public Word(String w_target, String w_explain) {
        this.word_explain = w_explain;
        this.word_target = w_target;
    }
}
