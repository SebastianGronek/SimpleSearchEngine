package index;

import com.findwise.IndexEntry;

public class IndexEntryImplementation implements IndexEntry {

    private String id;
    private double score;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public double getScore() {
        return score;
    }

    @Override
    public void setScore(double score) {
        this.score = score;
    }

    public IndexEntryImplementation(String id, double score) {
        this.id = id;
        this.score = score;
    }

    @Override
    public String toString() {
        return "IndexEntryImplementation{" +
                "id='" + id + '\'' +
                ", score=" + score +
                '}';
    }
}
