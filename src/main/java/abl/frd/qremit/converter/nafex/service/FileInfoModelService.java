package abl.frd.qremit.converter.nafex.service;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import abl.frd.qremit.converter.nafex.model.FileInfoModel;
import abl.frd.qremit.converter.nafex.repository.FileInfoModelRepository;

@Service
public class FileInfoModelService {
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    public List<FileInfoModel> getUploadedFileDetails(int userId){
        return fileInfoModelRepository.getUploadedFileDetails(userId);
    }

    public Map<String, Object> deleteFileInfoModelById(int id){
        Map<String, Object> resp = CommonService.getResp(1,"Data not deleted", null);
        try{    
            if(fileInfoModelRepository.existsById(id)){
                fileInfoModelRepository.deleteById(id);
                if(!fileInfoModelRepository.existsById(id)) resp = CommonService.getResp(0, "Data deleted succesful", null);
            }else   resp = CommonService.getResp(1, "Data not exists", null);
        }catch(Exception e){
            e.printStackTrace();
            resp = CommonService.getResp(1, "Error Occured ", null);
        }
        return resp;
    }

}
