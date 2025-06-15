package uz.husan.signup.service;

import org.springframework.stereotype.Service;
import uz.husan.signup.dto.company.CompanyRequestDTO;
import uz.husan.signup.entity.ResponseMessage;
import uz.husan.signup.entity.Company;

@Service
public interface CompanyService {

    ResponseMessage createCompany(CompanyRequestDTO companyRequestDTO);


    ResponseMessage getCompanies(Integer page,Integer size);


    ResponseMessage updateCompany(Long id, Company company);


    ResponseMessage deleteCompany(Long companyId);
}
