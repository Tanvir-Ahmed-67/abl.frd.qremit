package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.helper.AccountPayeeModelServiceHelper;
import abl.frd.qremit.converter.nafex.helper.OnlineModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.AccountPayeeModel;
import abl.frd.qremit.converter.nafex.model.OnlineModel;
import abl.frd.qremit.converter.nafex.repository.OnlineModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class OnlineModelService {
    @Autowired
    OnlineModelRepository onlineModelRepository;

    public ByteArrayInputStream load(String fileId, String fileType) {
        List<OnlineModel> onlineModes = onlineModelRepository.findAllOnlineModelHavingFileInfoId(Long.parseLong(fileId));
        ByteArrayInputStream in = OnlineModelServiceHelper.OnlineModelToCSV(onlineModes);
        return in;
    }
}
