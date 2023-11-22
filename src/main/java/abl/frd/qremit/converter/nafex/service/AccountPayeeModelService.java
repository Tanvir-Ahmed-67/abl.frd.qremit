package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.helper.AccountPayeeModelServiceHelper;
import abl.frd.qremit.converter.nafex.helper.CocModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.AccountPayeeModel;
import abl.frd.qremit.converter.nafex.model.CocModel;
import abl.frd.qremit.converter.nafex.repository.AccountPayeeModelRepository;
import abl.frd.qremit.converter.nafex.repository.CocModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class AccountPayeeModelService {
    @Autowired
    AccountPayeeModelRepository accountPayeeModelRepository;
    public ByteArrayInputStream load(String fileId, String fileType) {
        List<AccountPayeeModel> accountPayeeModes = accountPayeeModelRepository.findAllAccountPayeeModelHavingFileInfoId(Long.parseLong(fileId));
        ByteArrayInputStream in = AccountPayeeModelServiceHelper.AccountPayeeModelToCSV(accountPayeeModes);
        return in;
    }
    public ByteArrayInputStream loadAll() {
        List<AccountPayeeModel> accountPayeeModes = accountPayeeModelRepository.findAllAccountPayeeModel();
        ByteArrayInputStream in = AccountPayeeModelServiceHelper.AccountPayeeModelToCSV(accountPayeeModes);
        return in;
    }
}
