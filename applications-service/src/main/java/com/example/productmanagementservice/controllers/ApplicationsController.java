package com.example.productmanagementservice.controllers;

import com.example.productmanagementservice.database.repositories.ApplicationsRepository;
import com.example.productmanagementservice.dto.*;
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
    public ApplicationResponse createApplications() {
        return applicationService.createApplication();
    }

    @RequestMapping(value = "/applications/{id}/debit-card", method = RequestMethod.POST)
    public void addDebitCard(@PathVariable("id") long idApplication) {
        applicationService.addDebitCardToApplication(idApplication);
    }

    @RequestMapping(value = "/applications/{id}/credit-card", method = RequestMethod.POST)
    public void addCreditCash(@PathVariable("id") long idApplication, @RequestBody CreditCard creditCard) {
        applicationService.addCreditCardToApplication(idApplication, creditCard.getLimit());
    }

    @RequestMapping(value = "/applications/{id}/credit-cash", method = RequestMethod.POST)
    public void addCreditCash(@PathVariable("id") long idApplication, @RequestBody CreditCash creditCash) {
        applicationService.addCreditCashToApplication(idApplication, creditCash.getAmount(),
                creditCash.getTimeInMonth());
    }

    @RequestMapping(value = "/applications/{id}", method = RequestMethod.POST)
    public void sentApplication(@PathVariable("id") long idApplication) {
        applicationService.sendApplicationForApproval(idApplication);
    }

    @RequestMapping(value = "/applications", method = RequestMethod.GET)
    public List<ApplicationResponse> getListApplicationsClientForApproval(@RequestParam("userId") long userId) {
        return applicationService.getApplicationsClientForApproval(userId);
    }

    @RequestMapping(value = "/applications/my", method = RequestMethod.GET)
    public List<ApplicationResponse> getMyListApplicationsForApproval() {
        return applicationService.getApplicationsForApproval();
    }

    @RequestMapping(value = "/applications/getApprovedStatistics", method = RequestMethod.GET)
    public List<Statistic> getApprovedStatistics() {
        return applicationsRepository.getApprovedStatistics();
    }

    @RequestMapping(value = "/applications/getNegativeStatistics", method = RequestMethod.GET)
    public List<Statistic> getNegativeStatistics() {
        return applicationsRepository.getNegativeStatistics();
    }

    @RequestMapping(value = "/applications/{id}/approve", method = RequestMethod.POST)
    public void approveApplication(@PathVariable("id") long idApplication) {
        applicationService.approveApplication(idApplication);
    }

    @RequestMapping(value = "/applications/{id}/negative", method = RequestMethod.POST)
    public void negativeApplication(@PathVariable("id") long idApplication, @RequestBody Reason reason) {
        applicationService.negativeApplication(idApplication, reason.getReason());
    }
}
