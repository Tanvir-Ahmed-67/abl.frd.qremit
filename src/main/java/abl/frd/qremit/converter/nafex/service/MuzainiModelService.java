package abl.frd.qremit.converter.nafex.service;

//import abl.frd.qremit.converter.nafex.helper.MuzainiModelServiceHelper;
import abl.frd.qremit.converter.nafex.helper.MuzainiModelServiceHelper;
//import abl.frd.qremit.converter.nafex.helper.MuzainiModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.*;
import abl.frd.qremit.converter.nafex.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MuzainiModelService {
    @Autowired
    NafexModelRepository MuzainiModelRepository;
    @Autowired
    OnlineModelRepository onlineModelRepository;
    @Autowired
    CocModelRepository cocModelRepository;
    @Autowired
    AccountPayeeModelRepository accountPayeeModelRepository;
    @Autowired
    BeftnModelRepository beftnModelRepository;
    @Autowired
    FileInfoModelRepository fileInfoModelRepository;
    @Autowired
    UserModelRepository userModelRepository;
    LocalDateTime currentDateTime = LocalDateTime.now();
    public FileInfoModel save(MultipartFile file, int userId) {
        try
        {
            FileInfoModel fileInfoModel = new FileInfoModel();
            fileInfoModel.setUserModel(userModelRepository.findByUserId(userId));
            User user = userModelRepository.findByUserId(userId);
            List<MuzainiModel> muzainiModels = MuzainiModelServiceHelper.csvToMuzainiModels(file.getInputStream());
            int ind=0;
            for(MuzainiModel muzainiModel : muzainiModels){
                muzainiModel.setExchangeCode("7010231");
                muzainiModel.setFileInfoModel(fileInfoModel);
                muzainiModel.setUserModel(user);
                if(ind==0) {
                    fileInfoModel.setExchangeCode(muzainiModel.getExchangeCode());
                    ind++;
                }

            }

            // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
            List<OnlineModel> onlineModelList = MuzainiModelServiceHelper.generateOnlineModelList(muzainiModels);
            List<CocModel> cocModelList = MuzainiModelServiceHelper.generateCocModelList(muzainiModels);
            List<AccountPayeeModel> accountPayeeModelList = MuzainiModelServiceHelper.generateAccountPayeeModelList(muzainiModels);
            List<BeftnModel> beftnModelList = MuzainiModelServiceHelper.generateBeftnModelList(muzainiModels);


            // FILE INFO TABLE GENERATION HERE......
            fileInfoModel.setAccountPayeeCount(String.valueOf(accountPayeeModelList.size()));
            fileInfoModel.setOnlineCount(String.valueOf(onlineModelList.size()));
            fileInfoModel.setBeftnCount(String.valueOf(beftnModelList.size()));
            fileInfoModel.setCocCount(String.valueOf(cocModelList.size()));
            fileInfoModel.setTotalCount(String.valueOf(muzainiModels.size()));
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setProcessedCount("test");
            fileInfoModel.setUnprocessedCount("test");
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModel.setMuzainiModel(muzainiModels);
            fileInfoModel.setCocModelList(cocModelList);
            fileInfoModel.setAccountPayeeModelList(accountPayeeModelList);
            fileInfoModel.setBeftnModelList(beftnModelList);
            fileInfoModel.setOnlineModelList(onlineModelList);

            for(CocModel cocModel:cocModelList){
                cocModel.setFileInfoModel(fileInfoModel);
                cocModel.setUserModel(user);
            }
            for (AccountPayeeModel accountPayeeModel:accountPayeeModelList){
                accountPayeeModel.setFileInfoModel(fileInfoModel);
                accountPayeeModel.setUserModel(user);
            }
            for(BeftnModel beftnModel:beftnModelList){
                beftnModel.setFileInfoModel(fileInfoModel);
                beftnModel.setUserModel(user);
            }
            for (OnlineModel onlineModel:onlineModelList){
                onlineModel.setFileInfoModel(fileInfoModel);
                onlineModel.setUserModel(user);
            }
            // SAVING TO MySql Data Table
            fileInfoModelRepository.save(fileInfoModel);
            return fileInfoModel;
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }
}
