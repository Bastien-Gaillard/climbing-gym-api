package fr.climbinggym.adapter.in.batch;

import fr.climbinggym.application.port.in.MemberUseCase;
import fr.climbinggym.application.port.in.SubscriptionUseCase;
import fr.climbinggym.domain.shared.DomainException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.charset.StandardCharsets;

/**
 * Imports members from a CSV file (separator ';', header line skipped):
 * lastName;firstName;email;birthDate;belayCertified;subscriptionType
 */
@Configuration
public class MemberImportJobConfig {

    public static final String JOB_NAME = "memberImportJob";
    public static final String FILE_PATH_PARAM = "filePath";

    private static final int CHUNK_SIZE = 50;
    private static final int SKIP_LIMIT = 1_000;

    @Bean
    public Job memberImportJob(JobRepository jobRepository, Step memberImportStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(memberImportStep)
                .build();
    }

    @Bean
    public Step memberImportStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 FlatFileItemReader<MemberCsvLine> memberCsvReader,
                                 MemberCsvLineProcessor memberCsvLineProcessor,
                                 MemberImportWriter memberImportWriter) {
        return new StepBuilder("memberImportStep", jobRepository)
                .<MemberCsvLine, MemberImportRow>chunk(CHUNK_SIZE, transactionManager)
                .reader(memberCsvReader)
                .processor(memberCsvLineProcessor)
                .writer(memberImportWriter)
                .faultTolerant()
                .processorNonTransactional()
                .skip(FlatFileParseException.class)
                .skip(InvalidCsvLineException.class)
                .skip(DomainException.class)
                .skipLimit(SKIP_LIMIT)
                .listener(new MemberImportSkipListener())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<MemberCsvLine> memberCsvReader(@Value("#{jobParameters['filePath']}") String filePath) {
        return new FlatFileItemReaderBuilder<MemberCsvLine>()
                .name("memberCsvReader")
                .resource(new FileSystemResource(filePath))
                .encoding(StandardCharsets.UTF_8.name())
                .linesToSkip(1)
                .delimited()
                .delimiter(";")
                .names(MemberCsvLine.COLUMNS)
                .fieldSetMapper(fields -> new MemberCsvLine(
                        fields.readString("lastName"),
                        fields.readString("firstName"),
                        fields.readString("email"),
                        fields.readString("birthDate"),
                        fields.readString("belayCertified"),
                        fields.readString("subscriptionType")
                ))
                .build();
    }

    @Bean
    @StepScope
    public MemberCsvLineProcessor memberCsvLineProcessor(MemberUseCase memberUseCase) {
        return new MemberCsvLineProcessor(memberUseCase);
    }

    @Bean
    public MemberImportWriter memberImportWriter(MemberUseCase memberUseCase, SubscriptionUseCase subscriptionUseCase) {
        return new MemberImportWriter(memberUseCase, subscriptionUseCase);
    }
}
