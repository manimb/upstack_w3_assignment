package org.upgrad.upstac.testrequests.consultation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;


@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    Logger log = LoggerFactory.getLogger(ConsultationController.class);




    @Autowired
    private TestRequestUpdateService testRequestUpdateService;

    @Autowired
    private TestRequestQueryService testRequestQueryService;


    @Autowired
    TestRequestFlowService  testRequestFlowService;

    @Autowired
    private UserLoggedInService userLoggedInService;



    @GetMapping("/in-queue")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForConsultations()  {

        // Method Implementation - Added by Mani
        // Obtain the list of test requests pending for Consultations (having status as 'LAB_TEST_COMPLETED')

        // Return the list of test requests with the status with status as 'LAB_TEST_COMPLETED'.
        // These are the results pending for consultation with the Doctor
        return testRequestQueryService.findBy(RequestStatus.LAB_TEST_COMPLETED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public List<TestRequest> getForDoctor()  {

        // Method Implementation - Added by Mani
        // Returns the list of test requests assigned to current doctor

        // Create an object of User class and store the current logged in Doctor in it.
        User doctor = userLoggedInService.getLoggedInUser();

        // Return the list of test requests assigned to current logged-in Doctor
        return testRequestQueryService.findByDoctor(doctor);
    }



    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/assign/{id}")
    public TestRequest assignForConsultation(@PathVariable Long id) {

        // Method Implementation -Added by Mani
        // Assigns a particular test request ID to the current doctor(logged-in user)

        try {

            //Create an object of User class and get the current logged in Doctor
            User doctor = userLoggedInService.getLoggedInUser();

            // Assigns the id to the logged-in doctor (current Logged-in Doctor) (object created) and returns the object
            return   testRequestUpdateService.assignForConsultation(id, doctor);

        }catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }



    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping("/update/{id}")
    public TestRequest updateConsultation(@PathVariable Long id,@RequestBody CreateConsultationRequest testResult) {

        // Method Implementation - Added by Mani
        // Updates the result of the current test request id with test doctor comments

        try {
            // Create an object of User class and get the current logged in Doctor
            User doctor = userLoggedInService.getLoggedInUser();

            // Updates the current test request id with the testResult details by the current Logged-in Doctor (object created)
            return testRequestUpdateService.updateConsultation(id,testResult,doctor);

        } catch (ConstraintViolationException e) {
            throw asConstraintViolation(e);
        }catch (AppException e) {
            throw asBadRequest(e.getMessage());
        }
    }



}
