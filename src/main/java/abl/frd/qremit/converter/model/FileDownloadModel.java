package abl.frd.qremit.converter.model;
import java.time.LocalDateTime;

import javax.persistence.*;
@Entity
@Table(name="download_file_info")
public class FileDownloadModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int  id;
    @Column(name = "download_date_time", columnDefinition = "DATETIME")
    private LocalDateTime downlaodDateTime;
    @Column(name = "type", length = 10)
    private String type;
    @Column(name = "file_name", length = 128)
    private String fileName;
    @Column(name = "url", length = 128)
    private String url;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDownlaodDateTime() {
        return this.downlaodDateTime;
    }

    public void setDownlaodDateTime(LocalDateTime downlaodDateTime) {
        this.downlaodDateTime = downlaodDateTime;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FileDownloadModel() {
    }

    public FileDownloadModel(LocalDateTime downlaodDateTime, String type, String fileName, String url) {
        this.downlaodDateTime = downlaodDateTime;
        this.type = type;
        this.fileName = fileName;
        this.url = url;
    }
}
