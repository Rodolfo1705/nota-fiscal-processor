package com.desafio.nota_fiscal_processor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class NotaFiscalProcessorApplication implements CommandLineRunner {
	private final JobLauncher jobLauncher;
	private final ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(NotaFiscalProcessorApplication.class, args);
	}

	public NotaFiscalProcessorApplication(JobLauncher jobLauncher, ApplicationContext applicationContext) {
		this.jobLauncher = jobLauncher;
		this.applicationContext = applicationContext;
	}

	@Override
	public void run(String... args) throws Exception {
		Job job = (Job) applicationContext.getBean("notaFiscalJob");

		JobParameters jobParameters = new JobParametersBuilder()
				.addString("jobId", String.valueOf(System.currentTimeMillis()))
				.toJobParameters();

		jobLauncher.run(job, jobParameters);
	}
}
