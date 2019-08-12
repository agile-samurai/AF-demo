package group.u.mdas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
    private Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.debug("***Test");
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.debug("***Test in");
        }
    }
}
