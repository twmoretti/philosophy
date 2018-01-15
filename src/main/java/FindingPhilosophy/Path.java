package FindingPhilosophy;

import javax.persistence.*;

@Entity
public class Path {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String article;
    @Lob
    @Column(name="PATH", length=10000)
    private  String path;
    private Integer numHops;
    private String lastRun;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getNumHops() {
        return numHops;
    }

    public void setNumHops(Integer numHops) {
        this.numHops = numHops;
    }

    public String getLastRun() {
        return lastRun;
    }

    public void setLastRun(String lastRun) {
        this.lastRun = lastRun;
    }
}
