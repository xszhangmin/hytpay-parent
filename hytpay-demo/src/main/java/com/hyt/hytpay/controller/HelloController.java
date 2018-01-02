package com.hyt.hytpay.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {

	@GetMapping(value = "/say")
	public String say(
			@RequestParam(value = "id", required = false, defaultValue = "0") Integer myId) {
		return "id: " + myId;
	}
}
