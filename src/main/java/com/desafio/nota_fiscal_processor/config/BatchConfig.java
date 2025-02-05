package com.desafio.nota_fiscal_processor.config;

import com.desafio.nota_fiscal_processor.model.NotaFiscalResult;
import com.desafio.nota_fiscal_processor.service.NotaFiscalCompositeWriter;
import com.desafio.nota_fiscal_processor.service.NotaFiscalProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Bean
    public FlatFileItemReader<String> reader(){
       return new FlatFileItemReaderBuilder<String>()
                .name("notaFiscalReader")
                .resource(new ClassPathResource("notas_fiscais.csv"))
                .lineMapper(new PassThroughLineMapper())
                .build();
    }

    @Bean
    public ItemProcessor<String, NotaFiscalResult> processor(){
        return new NotaFiscalProcessor();
    }

    @Bean
    public FlatFileItemWriter<String> validWriter(){
        return new FlatFileItemWriterBuilder<String>()
                .name("validWriter")
                .resource(new FileSystemResource("notas_validas.csv"))
                .lineAggregator(item -> item)
                .build();
    }

    @Bean
    public FlatFileItemWriter<String> invalidWriter(){
        return new FlatFileItemWriterBuilder<String>()
                .name("invalidWriter")
                .resource(new FileSystemResource("notas_invalidas.csv"))
                .lineAggregator(item -> item)
                .build();
    }

    @Bean
    public ItemStreamWriter<NotaFiscalResult> compositeWriter(
            FlatFileItemWriter<String> validWriter,
            FlatFileItemWriter<String> invalidWriter){
        return new NotaFiscalCompositeWriter(
                validWriter,
                invalidWriter
        );
    }

    @Bean
    public Step processStep(ItemReader<String> reader,
                            ItemProcessor<String, NotaFiscalResult> processor,
                            ItemStreamWriter<NotaFiscalResult> writer){
        System.out.println("6");
        return new StepBuilder("processStep", jobRepository)
                .<String, NotaFiscalResult>chunk(5, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .stream(validWriter())
                .stream(invalidWriter())
                .transactionManager(platformTransactionManager)
                .build();
    }

    @Bean
    public Job notaFiscalJob(Step processStep){
        return new JobBuilder("notaFiscalJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(processStep)
                .build();
    }
}
