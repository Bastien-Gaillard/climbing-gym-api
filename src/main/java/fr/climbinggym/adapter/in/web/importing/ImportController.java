package fr.climbinggym.adapter.in.web.importing;

import fr.climbinggym.adapter.in.batch.MemberImportJobConfig;
import fr.climbinggym.adapter.in.web.ApiResponse;
import fr.climbinggym.domain.shared.InvalidValueException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;

@RestController
@RequestMapping("/api/imports")
public class ImportController {

    private final JobLauncher jobLauncher;
    private final Job memberImportJob;

    public ImportController(JobLauncher jobLauncher, @Qualifier(MemberImportJobConfig.JOB_NAME) Job memberImportJob) {
        this.jobLauncher = jobLauncher;
        this.memberImportJob = memberImportJob;
    }

    @PostMapping(path = "/members", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ImportReport> importMembers(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) throw new InvalidValueException("file", "must not be empty");

        var tempFile = Files.createTempFile("members-import-", ".csv");
        try {
            file.transferTo(tempFile);
            var parameters = new JobParametersBuilder()
                    .addString(MemberImportJobConfig.FILE_PATH_PARAM, tempFile.toString())
                    .addLong("requestedAt", System.currentTimeMillis())
                    .toJobParameters();

            return ApiResponse.of(ImportReport.from(jobLauncher.run(memberImportJob, parameters)));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
