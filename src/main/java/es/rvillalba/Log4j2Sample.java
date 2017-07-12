package es.rvillalba;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Log4j2Sample {

    public static final String SAMPLE_MESSAGE_MONGO = "Sample message mongo";

    public Log4j2Sample() {
        log.info(SAMPLE_MESSAGE_MONGO);
    }
}
