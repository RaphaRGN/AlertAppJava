package com.raphaelprojetos.Sentinel.controller;

import com.raphaelprojetos.Sentinel.repository.SentinelRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//TODO: Terminar o controller

@Controller
@RequestMapping("/alertas")
public class SentinelController {

    private final SentinelRepository sentinelRepository;

    public  SentinelController (SentinelRepository sentinelRepository){

        this.sentinelRepository = sentinelRepository;


    }

}
