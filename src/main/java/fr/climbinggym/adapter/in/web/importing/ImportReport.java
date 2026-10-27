package fr.climbinggym.adapter.in.web.importing;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

public record ImportReport(
        Long executionId,
        String status,
        long read,
        long written,
        long filtered,
        long skipped
) {

    public static ImportReport from(JobExecution execution) {
        var steps = execution.getStepExecutions();
        return new ImportReport(
                execution.getId(),
                execution.getStatus().name(),
                steps.stream().mapToLong(StepExecution::getReadCount).sum(),
                steps.stream().mapToLong(StepExecution::getWriteCount).sum(),
                steps.stream().mapToLong(StepExecution::getFilterCount).sum(),
                steps.stream().mapToLong(StepExecution::getSkipCount).sum()
        );
    }
}
