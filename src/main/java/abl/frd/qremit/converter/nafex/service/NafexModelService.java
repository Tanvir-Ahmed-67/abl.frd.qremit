package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.helper.NafexModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.*;
import abl.frd.qremit.converter.nafex.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class NafexModelService {
    @Autowired
    NafexModelRepository nafexModelRepository;
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
            List<NafexEhMstModel> nafexModels = NafexModelServiceHelper.csvToNafexModels(file.getInputStream());
            int ind=0;
            for(NafexEhMstModel nafexModel : nafexModels){
                nafexModel.setExchangeCode("7010234");
                nafexModel.setFileInfoModel(fileInfoModel);
                nafexModel.setUserModel(user);
                if(ind==0) {
                    fileInfoModel.setExchangeCode(nafexModel.getExchangeCode());
                    ind++;
                }
            }

            // 4 DIFFERENTS DATA TABLE GENERATION GOING ON HERE
            List<OnlineModel> onlineModelList = NafexModelServiceHelper.generateOnlineModelList(nafexModels);
            List<CocModel> cocModelList = NafexModelServiceHelper.generateCocModelList(nafexModels);
            List<AccountPayeeModel> accountPayeeModelList = NafexModelServiceHelper.generateAccountPayeeModelList(nafexModels);
            List<BeftnModel> beftnModelList = NafexModelServiceHelper.generateBeftnModelList(nafexModels);


            // FILE INFO TABLE GENERATION HERE......
            fileInfoModel.setAccountPayeeCount(String.valueOf(accountPayeeModelList.size()));
            fileInfoModel.setOnlineCount(String.valueOf(onlineModelList.size()));
            fileInfoModel.setBeftnCount(String.valueOf(beftnModelList.size()));
            fileInfoModel.setCocCount(String.valueOf(cocModelList.size()));
            fileInfoModel.setTotalCount(String.valueOf(nafexModels.size()));
            fileInfoModel.setFileName(file.getOriginalFilename());
            fileInfoModel.setProcessedCount("test");
            fileInfoModel.setUnprocessedCount("test");
            fileInfoModel.setUploadDateTime(currentDateTime);
            fileInfoModel.setNafexEhMstModel(nafexModels);
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

    public List<Integer> CountAllFourTypesOfData(){
        List<Integer> count = new ArrayList<Integer>(5);
        count.add(onlineModelRepository.countByIsProcessed("0"));
        count.add(cocModelRepository.countByIsProcessed("0"));
        count.add(accountPayeeModelRepository.countByIsProcessed("0"));
        count.add(beftnModelRepository.countByIsProcessedMain("0"));
        count.add(beftnModelRepository.countByIsProcessedIncentive("0"));
        return count;
    }
}
