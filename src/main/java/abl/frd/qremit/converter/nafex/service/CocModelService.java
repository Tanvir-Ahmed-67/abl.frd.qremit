package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.helper.CocModelServiceHelper;
import abl.frd.qremit.converter.nafex.model.CocModel;
import abl.frd.qremit.converter.nafex.repository.CocModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class CocModelService {
    @Autowired
    CocModelRepository CocModelRepository;
    public ByteArrayInputStream load(String fileId, String fileType) {
        List<CocModel> cocModels = CocModelRepository.findAllCocModelHavingFileInfoId(Long.parseLong(fileId));
        ByteArrayInputStream in = CocModelServiceHelper.cocModelToCSV(cocModels);
        return in;
    }
    public ByteArrayInputStream loadAll() {
        List<CocModel> cocModels = CocModelRepository.findAllCocModel();
        ByteArrayInputStream in = CocModelServiceHelper.cocModelToCSV(cocModels);
        return in;
    }

}
