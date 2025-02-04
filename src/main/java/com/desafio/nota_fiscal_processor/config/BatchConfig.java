package com.desafio.nota_fiscal_processor.config;

import com.desafio.nota_fiscal_processor.mapper.NotaFiscalMapper;
import com.desafio.nota_fiscal_processor.model.NotaFiscal;
import com.desafio.nota_fiscal_processor.model.NotaFiscalResult;
import com.desafio.nota_fiscal_processor.service.NotaFiscalClassifier;
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
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
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
    public FlatFileItemReader<NotaFiscal> reader(){
        System.out.println("1");
       return new FlatFileItemReaderBuilder<NotaFiscal>()
                .name("notaFiscalReader")
                .delimited()
                .names("id", "cnpj", "valor")
                .targetType(NotaFiscal.class)
                .resource(new ClassPathResource("notas_fiscais.csv"))
                .fieldSetMapper(new NotaFiscalMapper())
                .strict(false)
                .build();
    }

    @Bean
    public ItemProcessor<NotaFiscal, NotaFiscalResult> processor(){
        System.out.println("2");
        return new NotaFiscalProcessor();
    }

    @Bean
    public FlatFileItemWriter<NotaFiscalResult> validWriter(){
        System.out.println("3");
        return new FlatFileItemWriterBuilder<NotaFiscalResult>()
                .name("validWriter")
                .resource(new FileSystemResource("notas_validas.csv"))
                .delimited().delimiter(", ")
                .names("id", "cnpj", "valor")
                .build();
    }

    @Bean
    public FlatFileItemWriter<NotaFiscalResult> invalidWriter(){
        System.out.println("4");
        return new FlatFileItemWriterBuilder<NotaFiscalResult>()
                .name("invalidWriter")
                .resource(new FileSystemResource("notas_invalidas.csv"))
                .delimited().delimiter(", ")
                .names("id", "cnpj", "valor")
                .build();
    }

    @Bean
    public ClassifierCompositeItemWriter<NotaFiscalResult> classifierWriter(NotaFiscalClassifier classifier){
        System.out.println("5");
        ClassifierCompositeItemWriter<NotaFiscalResult> writer = new ClassifierCompositeItemWriter<>();

        writer.setClassifier(classifier);
        return writer;
    }

    @Bean
    public Step processStep(ItemReader<NotaFiscal> reader,
                            ItemProcessor<NotaFiscal, NotaFiscalResult> processor,
                            ClassifierCompositeItemWriter<NotaFiscalResult> writer){
        System.out.println("6");
        return new StepBuilder("processStep", jobRepository)
                .<NotaFiscal, NotaFiscalResult>chunk(5, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .stream(validWriter())
                .stream(invalidWriter())
                .build();
    }

    @Bean
    public Job notaFiscalJob(Step processStep){
        System.out.println("7");
        return new JobBuilder("invoiceJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(processStep)
                .build();
    }

    @Bean
    public NotaFiscalClassifier classifier(FlatFileItemWriter<NotaFiscalResult> validWriter,
                                           FlatFileItemWriter<NotaFiscalResult> invalidWriter){
        System.out.println("8");
        return new NotaFiscalClassifier(validWriter, invalidWriter);
    }

    @Bean
    public ItemStreamWriter<NotaFiscalResult> compositeWriter(NotaFiscalClassifier classifier,
                                                              FlatFileItemWriter<NotaFiscalResult> validWriter,
                                                              FlatFileItemWriter<NotaFiscalResult> invalidWriter){
        System.out.println("9");
        return new NotaFiscalCompositeWriter(
                validWriter,
                invalidWriter,
                classifier
        );
    }
}
