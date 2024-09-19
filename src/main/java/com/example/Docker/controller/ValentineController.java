package com.example.Docker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.Docker.model.Valentine;
import com.example.Docker.repository.ValentineRepository;

import java.util.List;

@RestController
@RequestMapping("/valentines")
public class ValentineController {

    @Autowired
    private ValentineRepository valentineRepository;

    @GetMapping
    public List<Valentine> getAllValentines() {
        return valentineRepository.findAll();
    }

    @PostMapping
    public Valentine createValentine(@RequestBody Valentine valentine) {
        return valentineRepository.save(valentine);
    }
}

