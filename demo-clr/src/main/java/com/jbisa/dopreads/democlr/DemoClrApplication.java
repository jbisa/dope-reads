package com.jbisa.dopreads.democlr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoClrApplication {

	private static final Logger LOG = LoggerFactory.getLogger(DemoClrApplication.class);

	@Bean
	public CommandLineRunner run() throws Exception {
		return args -> {
			LOG.info("Starting CLR application...");

			var sb = new StringBuilder();
			for (int i = 1; i < 101; i++) {
				if (i % 3 == 0) {
					sb.append("Fizz");
				}

				if (i % 5 == 0) {
					sb.append("Buzz");
				}

				if (sb.length() == 0) {
					sb.append(i);
				}

				LOG.info(sb.toString());
				sb.setLength(0);
			}

			LOG.info("Finished CLR application...");
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoClrApplication.class, args);
	}

}
