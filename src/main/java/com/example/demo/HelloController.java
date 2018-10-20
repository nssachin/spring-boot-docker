package com.example.demo;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping
	public String holla() throws UnknownHostException {
		return "Holla! Spring with Docker running on " + InetAddress.getLocalHost().getHostAddress();
	}
}
