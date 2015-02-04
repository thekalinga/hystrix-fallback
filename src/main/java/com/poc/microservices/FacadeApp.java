package com.poc.microservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.FeignConfiguration;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@SpringBootApplication
@EnableEurekaClient
@EnableHystrix
public class FacadeApp extends FeignConfiguration {

  public static void main(String[] args) {
  	SpringApplication.run(FacadeApp.class, args);
  }

  @Bean
  FallbackClient fallbackClient() {
    return loadBalance(FallbackClient.class, "http://fallback");
  }
}

interface FallbackClient {
  @RequestMapping(method = RequestMethod.GET, value = "/ping")
  String ping();
}

@RestController
class FacadeRestController {

	@Autowired
	FallbackableService fallbackableService;

  @RequestMapping("/ping")
	String fallbackTestServiceEndPoint() {
		return fallbackableService.ping();
	}

}

@Service
class FallbackableService {

	@Autowired
	FallbackClient fallbackClient;

  @HystrixCommand(fallbackMethod = "defaultLink")
  String ping() {
		return fallbackClient.ping();
	}

  String fallback() {
  	return "PONG: from fallback";
  }

}
