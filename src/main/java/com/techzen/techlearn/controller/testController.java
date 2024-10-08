package com.techzen.techlearn.controller;

import com.techzen.techlearn.repository.SubmitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("test")
public class testController {
     @Autowired
    SubmitionRepository submitionRepository;
    @GetMapping("submit")
    public Page<?> deCodeBase64(@RequestParam UUID id, @RequestParam long assignment){
        Pageable pageable = PageRequest.of(1, 10);
        Page<?> x = submitionRepository.findAllByAssignmentIdAndUserId(pageable,assignment, id);
        return x;

    }

}
