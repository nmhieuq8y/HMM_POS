package tagger.domain;

public class PreTag_Tag {

    private Tag previousTag;
    private Tag currentTag;

    public PreTag_Tag(Tag previousTag, Tag currentTag) {
        this.previousTag = previousTag;
        this.currentTag = currentTag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PreTag_Tag)) return false;

        PreTag_Tag that = (PreTag_Tag) o;

        return currentTag.equals(that.currentTag) && previousTag.equals(that.previousTag);

    }

    @Override
    public int hashCode() {
        int result = previousTag.hashCode();
        result = 31 * result + currentTag.hashCode();
        return result;
    }

    @Override
    public String toString() {

        return previousTag + "_" + currentTag;
    }
}
