package fr.climbinggym.adapter.in.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;

public class MemberImportSkipListener implements SkipListener<MemberCsvLine, MemberImportRow> {

    private static final Logger log = LoggerFactory.getLogger(MemberImportSkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("Skipped unreadable line: {}", t.getMessage());
    }

    @Override
    public void onSkipInProcess(MemberCsvLine line, Throwable t) {
        log.warn("Skipped line for {}: {}", line.email(), t.getMessage());
    }

    @Override
    public void onSkipInWrite(MemberImportRow row, Throwable t) {
        log.warn("Skipped member {}: {}", row.member().email(), t.getMessage());
    }
}
