package abl.frd.qremit.converter.nafex.model;

public class FileInfoModelDTO {
    public FileInfoModel fileInfoModel;
    public int count;

    public FileInfoModelDTO(FileInfoModel fileInfoModel, Long count){
        this.fileInfoModel = fileInfoModel;
        int cnt = (count != null) ? (int) (long) count : 0;
        this.count = cnt;
    }

    public FileInfoModel getFileInfoModel() {
        return fileInfoModel;
    }

    public void setFileInfoModel(FileInfoModel fileInfoModel) {
        this.fileInfoModel = fileInfoModel;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "FileInfoModelDTO{" +
            " fileInfoModel='" + getFileInfoModel() + "'" +
            ", count='" + getCount() + "'" +
            "}";
    }

    
}
