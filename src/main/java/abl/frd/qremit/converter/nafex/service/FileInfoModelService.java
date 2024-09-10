package abl.frd.qremit.converter.nafex.service;

import java.time.LocalDateTime;
import java.util.List;

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

    public void deleteFileInfoModelById(int id){
        try{    
            //if(fileInfoModelRepository.existsById(id){

            //}
        }catch(Exception e){
            
        }
        
    }

}
