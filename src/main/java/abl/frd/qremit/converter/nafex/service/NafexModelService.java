package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.helper.NafexModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.*;
import abl.frd.qremit.converter.nafex.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public FileInfoModel save(MultipartFile file) {
        try
        {
            FileInfoModel fileInfoModel = new FileInfoModel();
            List<NafexEhMstModel> nafexModels = NafexModelServiceHelper.csvToNafexModels(file.getInputStream());
            int ind=0;
            for(NafexEhMstModel nafexModel : nafexModels){
                nafexModel.setExchangeCode("7010234");
                nafexModel.setFileInfoModel(fileInfoModel);
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
            fileInfoModel.setUploadDate("test");
            fileInfoModel.setNafexEhMstModel(nafexModels);
            fileInfoModel.setCocModelList(cocModelList);
            fileInfoModel.setAccountPayeeModelList(accountPayeeModelList);
            fileInfoModel.setBeftnModelList(beftnModelList);
            fileInfoModel.setOnlineModelList(onlineModelList);

            for(CocModel cocModel:cocModelList){
                cocModel.setFileInfoModel(fileInfoModel);
            }
            for (AccountPayeeModel accountPayeeModel:accountPayeeModelList){
                accountPayeeModel.setFileInfoModel(fileInfoModel);
            }
            for(BeftnModel beftnModel:beftnModelList){
                beftnModel.setFileInfoModel(fileInfoModel);
            }
            for (OnlineModel onlineModel:onlineModelList){
                onlineModel.setFileInfoModel(fileInfoModel);
            }
            // SAVING TO MySql Data Table
            fileInfoModelRepository.save(fileInfoModel);
            return fileInfoModel;
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }
}
