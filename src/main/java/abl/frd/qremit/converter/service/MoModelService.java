package abl.frd.qremit.converter.service;

import abl.frd.qremit.converter.model.MoModel;
import abl.frd.qremit.converter.repository.MoModelRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

@Service
public class MoModelService {
    @Autowired
    MoModelRepository moModelRepository;
    @Autowired
    ReportService reportService;
    @Autowired
    CommonService commonService;

    public MoModel processAndGenerateMoData(MoModel moModel){
        LocalDate reportDate = moModel.getMoDate();

        List<Object> beftnData = reportService.getAllBeftnSummaryForMo(reportDate);

        moModel.setTotalNumberBeftn((Long) beftnData.get(0));
        moModel.setTotalAmountBeftn(BigDecimal.valueOf((Double) beftnData.get(1)).setScale(2, RoundingMode.DOWN));
        moModel.doSumGrandTotalNumber(moModel.getTotalNumberBeftn());
        moModel.doSumGrandTotalAmount(moModel.getTotalAmountBeftn());

        List<Object> allOtherSummaryData = reportService.getAllOtherSummaryForMo(reportDate);

        moModel.setTotalNumberAllOtherBranch((Long) allOtherSummaryData.get(0));
        moModel.setTotalAmountAllOtherBranch(BigDecimal.valueOf((Double) allOtherSummaryData.get(1)).setScale(2, RoundingMode.DOWN));
        moModel.doSumGrandTotalNumber(moModel.getTotalNumberAllOtherBranch());
        moModel.doSumGrandTotalAmount(moModel.getTotalAmountAllOtherBranch());

        moModel.setTotalNumberIcash(moModel.getTotalNumberIcash());
        moModel.setTotalAmountIcash(moModel.getTotalAmountIcash());
        moModel.doSumGrandTotalNumber(moModel.getTotalNumberIcash());
        moModel.doSumGrandTotalAmount(moModel.getTotalAmountIcash());

        List<Object> onlineData = reportService.getAllOnlineSummaryForMo(reportDate);

        moModel.setTotalNumberOnline((Long) onlineData.get(0));
        moModel.setTotalAmountOnline(BigDecimal.valueOf((Double) onlineData.get(1)).setScale(2, RoundingMode.DOWN));
        moModel.doSumGrandTotalNumber(moModel.getTotalNumberOnline());
        moModel.doSumGrandTotalAmount(moModel.getTotalAmountOnline());

        List<Object> apiData = reportService.getAllApiSummaryForMo(reportDate);

        moModel.setTotalNumberApi((Long) apiData.get(0));
        moModel.setTotalAmountApi(BigDecimal.valueOf((Double) apiData.get(1)).setScale(2, RoundingMode.DOWN));
        moModel.doSumGrandTotalNumber(moModel.getTotalNumberApi());
        moModel.doSumGrandTotalAmount(moModel.getTotalAmountApi());

        return moModelRepository.save(moModel);
    }
    public MoModel findIfAlreadyGenerated(MoModel moModel){
        LocalDate reportDate = moModel.getMoDate();
        MoModel model = moModelRepository.findByMoGenerationDate(reportDate);
        return model;
    }

    public MoModel generateMoDTOForPreparingPdfFile(MoModel moModel, String date){
        MoModel model = new MoModel();
        model.setId(moModel.getId());
        model.setMoDate(LocalDate.parse(date));

        // For BEFTN
        model.setTotalNumberBeftn(moModel.getTotalNumberBeftn());
        model.setTotalAmountBeftn(moModel.getTotalAmountBeftn());
        model.doSumGrandTotalNumber(moModel.getTotalNumberBeftn());
        model.doSumGrandTotalAmount(moModel.getTotalAmountBeftn());
        // For All Other Brancs
        model.setTotalNumberAllOtherBranch(moModel.getTotalNumberAllOtherBranch());
        model.setTotalAmountAllOtherBranch(moModel.getTotalAmountAllOtherBranch());
        model.doSumGrandTotalNumber(model.getTotalNumberAllOtherBranch());
        model.doSumGrandTotalAmount(model.getTotalAmountAllOtherBranch());
        // For I Cash
        model.setTotalNumberIcash(moModel.getTotalNumberIcash());
        model.setTotalAmountIcash(moModel.getTotalAmountIcash());
        model.doSumGrandTotalNumber(model.getTotalNumberIcash());
        model.doSumGrandTotalAmount(model.getTotalAmountIcash());
        //For Online A/C Transfer
        model.setTotalNumberOnline(moModel.getTotalNumberOnline());
        model.setTotalAmountOnline(moModel.getTotalAmountOnline());
        model.doSumGrandTotalNumber(model.getTotalNumberOnline());
        model.doSumGrandTotalAmount(model.getTotalAmountOnline());
        //For API
        model.setTotalNumberApi(moModel.getTotalNumberApi());
        model.setTotalAmountApi(moModel.getTotalAmountApi());
        model.doSumGrandTotalNumber(model.getTotalNumberApi());
        model.doSumGrandTotalAmount(model.getTotalAmountApi());

        return model;
    }
    public byte[] generateMoInPdfFormat(MoModel moModel, String date) throws Exception {
        List<MoModel> moModelList = Collections.singletonList(moModel);
        JasperReport jasperReport = loadJasperReport("mo.jrxml");
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource( moModelList);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ReportTitle", "Sample Report");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        Path reportPath = commonService.getReportFile(commonService.generateFileName("mo_", date, ".pdf"));
        String outputFile = reportPath.toString();
        if(!Files.exists(reportPath)){
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);
        }
        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    public JasperReport loadJasperReport(String fileName) throws Exception {
        Resource resource = new ClassPathResource(fileName);
        try (InputStream inputStream = resource.getInputStream()) {
            return JasperCompileManager.compileReport(inputStream);
        }
    }
}
