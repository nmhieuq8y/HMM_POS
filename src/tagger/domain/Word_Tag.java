package tagger.domain;

public class Word_Tag {

    private Word word;
    private Tag tag;

    public Word_Tag(Word word, Tag tag) {
        this.word = word;
        this.tag = tag;
    }

    @Override
    public String toString() {

        return word + "&" + tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Word_Tag)) return false;

        Word_Tag that = (Word_Tag) o;

        return tag.equals(that.tag) && word.equals(that.word);

    }

    @Override
    public int hashCode() {
        int result = word.hashCode();
        result = 31 * result + tag.hashCode();
        return result;
    }
}
