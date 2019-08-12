package group.u.mdas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class CustomStepListener implements StepExecutionListener {
    private Logger logger = LoggerFactory.getLogger(CustomStepListener.class);
    private ThreadPoolTaskExecutor executor;

    public CustomStepListener(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("StepExecutionListener - beforeStep");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.exit(0);
        return ExitStatus.COMPLETED;
    }
}
