package com.desafio.nota_fiscal_processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class NotaFiscalProcessorApplicationTests {
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private Job job;

	@BeforeEach
	public void before() {
		jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJob(job);
	}

	@Test
	public void test() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();

		JobExecution execution = jobLauncherTestUtils.launchJob(jobParameters);

		assertEquals(BatchStatus.COMPLETED, execution.getStatus());
		assertTrue(Files.exists(Paths.get("notas_validas.csv")));
		assertTrue(Files.exists(Paths.get("notas_invalidas.csv")));
	}
}
