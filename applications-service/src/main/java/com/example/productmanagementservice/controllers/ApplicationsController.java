package com.example.productmanagementservice.controllers;

import com.example.productmanagementservice.database.repositories.ApplicationsRepository;
import com.example.productmanagementservice.dto.Statistic;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.dto.Reason;
import com.example.productmanagementservice.dto.CreditCard;
import com.example.productmanagementservice.dto.CreditCash;
import com.example.productmanagementservice.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApplicationsController {

    private final ApplicationService applicationService;
    private final ApplicationsRepository applicationsRepository;

    @Autowired
    public ApplicationsController(ApplicationsRepository applicationsRepository, ApplicationService applicationService) {
        this.applicationService = applicationService;
        this.applicationsRepository = applicationsRepository;
    }

    @RequestMapping(value = "/applications", method = RequestMethod.POST)
    public Application createApplications(@RequestHeader("token") String token) {
        return applicationService.createApplication(token);
    }

    @RequestMapping(value = "/applications/{id}/debit-card", method = RequestMethod.POST)
    public void addDebitCard(@PathVariable("id") long idApplication,
                             @RequestHeader("token") String token) {
        applicationService.addDebitCardToApplication(token, idApplication);
    }

    @RequestMapping(value = "/applications/{id}/credit-card", method = RequestMethod.POST)
    public void addCreditCard(@PathVariable("id") long idApplication, @RequestHeader("token") String token,
                              @RequestBody CreditCard creditCard) {
        applicationService.addCreditCardToApplication(token, idApplication, creditCard.getLimit());
    }

    @RequestMapping(value = "/applications/{id}/credit-cash", method = RequestMethod.POST)
    public void addCreditCard(@PathVariable("id") long idApplication, @RequestHeader("token") String token,
                              @RequestBody CreditCash creditCash) {
        applicationService.addCreditCashToApplication(token, idApplication, creditCash.getAmount(),
                creditCash.getTimeInMonth());
    }

    @RequestMapping(value = "/applications/{id}", method = RequestMethod.POST)
    public void sentApplication(@PathVariable("id") long idApplication, @RequestHeader("token") String token) {
        applicationService.sendApplicationForApproval(token, idApplication);
    }

    @RequestMapping(value = "/applications", method = RequestMethod.GET)
    public List<Application> getListApplicationsClientForApproval(@RequestParam("userId") long userId,
                                                                  @RequestHeader("token") String token) {
        return applicationService.getApplicationsClientForApproval(userId, token);
    }

    @RequestMapping(value = "/applications/my", method = RequestMethod.GET)
    public List<Application> getMyListApplicationsForApproval(@RequestHeader("token") String token) {
        return applicationService.getApplicationsForApproval(token);
    }

    @RequestMapping(value = "/applications/getApprovedStatistics", method = RequestMethod.GET)
    public List<Statistic> getApprovedStatistics(){
        return applicationsRepository.getApprovedStatistics();
    }

    @RequestMapping(value = "/applications/getNegativeStatistics", method = RequestMethod.GET)
    public List<Statistic> getNegativeStatistics(){ return applicationsRepository.getNegativeStatistics();    }

    @RequestMapping(value = "/applications/{id}/approve", method = RequestMethod.POST)
    public void approveApplication(@PathVariable("id") long idApplication, @RequestHeader("token") String token) {
        applicationService.approveApplication(idApplication, token);
    }

    @RequestMapping(value = "/applications/{id}/negative", method = RequestMethod.POST)
    public void negativeApplication(@PathVariable("id") long idApplication, @RequestHeader("token") String token,
                                    @RequestBody Reason reason) {
        applicationService.negativeApplication(idApplication, token, reason.getReason());
    }


}
