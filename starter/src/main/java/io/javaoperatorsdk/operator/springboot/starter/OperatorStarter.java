package io.javaoperatorsdk.operator.springboot.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import io.javaoperatorsdk.operator.Operator;

public class OperatorStarter {

  private static final Logger log = LoggerFactory.getLogger(OperatorStarter.class);

  private final Operator operator;
  private final CrdUploader crdUploader;

  public OperatorStarter(Operator operator, CrdUploader crdUploader) {
    this.operator = operator;
    this.crdUploader = crdUploader;
  }

  @EventListener
  public void start(ApplicationReadyEvent event) {
    if (!operator.getRegisteredControllers().isEmpty()) {
      try {
        crdUploader.upload();
        operator.start();
      } catch (Exception ex) {
        log.error("Could not start operator", ex);
        SpringApplication.exit(event.getApplicationContext(), () -> 1);
      }
    } else {
      log.warn(
          "No Reconcilers found in the application context: Not starting the Operator, not looking for CRDs");
    }
  }

}
