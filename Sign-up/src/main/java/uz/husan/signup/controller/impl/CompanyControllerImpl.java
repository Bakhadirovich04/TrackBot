package uz.husan.signup.controller.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uz.husan.signup.controller.CompanyController;
import uz.husan.signup.dto.company.CompanyRequestDTO;
import uz.husan.signup.entity.ResponseMessage;
import uz.husan.signup.entity.Company;
import uz.husan.signup.service.CompanyService;

@RequiredArgsConstructor
@RestController
public class CompanyControllerImpl implements CompanyController {
    private final CompanyService companyService;
    @Override
    public ResponseEntity<?> createCompany(CompanyRequestDTO companyRequestDTO) {
       ResponseMessage responseMessage =companyService.createCompany(companyRequestDTO);
       return ResponseEntity.status(responseMessage.getSuccess()? 200 : 400).body(responseMessage);
    }

    @Override
    public ResponseEntity<?> getCompanies(Integer page, Integer size) {
        ResponseMessage responseMessage =companyService.getCompanies(page, size);
        return ResponseEntity.status(responseMessage.getSuccess() ? 200 : 400).body(responseMessage);
    }

    @Override
    public ResponseEntity<?> updateCompany(Long id,Company company) {
        ResponseMessage responseMessage =companyService.updateCompany(id,company);
        return ResponseEntity.status(responseMessage.getSuccess() ? 200 : 400).body(responseMessage);
    }

    @Override
    public ResponseEntity<?> deleteCompany(Long companyId) {
        ResponseMessage responseMessage =companyService.deleteCompany(companyId);
        return ResponseEntity.status(responseMessage.getSuccess() ? 200 : 400).body(responseMessage);
    }
}
