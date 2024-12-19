package abl.frd.qremit.converter.service;

import abl.frd.qremit.converter.helper.NumberToWords;
import abl.frd.qremit.converter.model.ExchangeReportDTO;
import abl.frd.qremit.converter.model.MoDTO;
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

        List<Object> allOtherSummaryData = reportService.getAllOtherSummaryForMo(reportDate);

        moModel.setTotalNumberAllOtherBranch((Long) allOtherSummaryData.get(0));
        moModel.setTotalAmountAllOtherBranch(BigDecimal.valueOf((Double) allOtherSummaryData.get(1)).setScale(2, RoundingMode.DOWN));


        moModel.setTotalNumberIcash(moModel.getTotalNumberIcash());
        moModel.setTotalAmountIcash(moModel.getTotalAmountIcash());

        List<Object> onlineData = reportService.getAllOnlineSummaryForMo(reportDate);

        moModel.setTotalNumberOnline((Long) onlineData.get(0));
        moModel.setTotalAmountOnline(BigDecimal.valueOf((Double) onlineData.get(1)).setScale(2, RoundingMode.DOWN));

        List<Object> apiData = reportService.getAllApiSummaryForMo(reportDate);

        moModel.setTotalNumberApi((Long) apiData.get(0));
        moModel.setTotalAmountApi(BigDecimal.valueOf((Double) apiData.get(1)).setScale(2, RoundingMode.DOWN));

        return moModelRepository.save(moModel);
    }
    public MoModel findIfAlreadyGenerated(MoModel moModel){
        LocalDate reportDate = moModel.getMoDate();
        MoModel model = moModelRepository.findByMoGenerationDate(reportDate);
        return model;
    }

    public MoDTO generateMoDTOForPreparingPdfFile(MoModel moModel, String date){
        MoDTO moDTO = new MoDTO();
        moDTO.setMoDate(LocalDate.parse(date));
        // For BEFTN
        moDTO.setTotalNumberBeftn(moModel.getTotalNumberBeftn());
        moDTO.setTotalAmountBeftn(moModel.getTotalAmountBeftn());
        moDTO.doSumGrandTotalNumber(moDTO.getTotalNumberBeftn());
        moDTO.doSumGrandTotalAmount(moDTO.getTotalAmountBeftn());
        // For All Other Brancs
        moDTO.setTotalNumberAllOtherBranch(moModel.getTotalNumberAllOtherBranch());
        moDTO.setTotalAmountAllOtherBranch(moModel.getTotalAmountAllOtherBranch());
        moDTO.doSumGrandTotalNumber(moDTO.getTotalNumberAllOtherBranch());
        moDTO.doSumGrandTotalAmount(moDTO.getTotalAmountAllOtherBranch());
        // For I Cash
        moDTO.setTotalNumberIcash(moModel.getTotalNumberIcash());
        moDTO.setTotalAmountIcash(moModel.getTotalAmountIcash());
        moDTO.doSumGrandTotalNumber(moDTO.getTotalNumberIcash());
        moDTO.doSumGrandTotalAmount(moDTO.getTotalAmountIcash());
        //For Online A/C Transfer
        moDTO.setTotalNumberOnline(moModel.getTotalNumberOnline());
        moDTO.setTotalAmountOnline(moModel.getTotalAmountOnline());
        moDTO.doSumGrandTotalNumber(moDTO.getTotalNumberOnline());
        moDTO.doSumGrandTotalAmount(moDTO.getTotalAmountOnline());
        //For API
        moDTO.setTotalNumberApi(moModel.getTotalNumberApi());
        moDTO.setTotalAmountApi(moModel.getTotalAmountApi());
        moDTO.doSumGrandTotalNumber(moDTO.getTotalNumberApi());
        moDTO.doSumGrandTotalAmount(moDTO.getTotalAmountApi());

        moDTO.setTotalAmountInWords(NumberToWords.convertBigDecimalToWords(moDTO.getGrandTotalAmount()));

        return moDTO;
    }
    public byte[] generateMoInPdfFormat(MoDTO moDTO, String date) throws Exception {
        List<MoDTO> moDTOList = Collections.singletonList(moDTO);
        JasperReport jasperReport = loadJasperReport("mo.jrxml");
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource( moDTOList);
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
