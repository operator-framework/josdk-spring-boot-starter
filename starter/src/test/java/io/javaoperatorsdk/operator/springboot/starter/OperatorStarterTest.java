package io.javaoperatorsdk.operator.springboot.starter;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;

import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.RegisteredController;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OperatorStarterTest {

  @Mock
  private Operator operator;
  @Mock
  private CrdUploader crdUploader;
  @Mock
  private ApplicationReadyEvent event;
  @Mock
  private ConfigurableApplicationContext context;
  @InjectMocks
  private OperatorStarter starter;

  @Test
  void shouldShutdownOnUploadFailure() throws Exception {
    when(event.getApplicationContext()).thenReturn(context);
    when(operator.getRegisteredControllers()).thenReturn(Set.of(mock(RegisteredController.class)));
    doThrow(new IllegalStateException("False Knees")).when(crdUploader).upload();

    starter.start(event);

    verify(crdUploader).upload();
    verify(operator, never()).start();
    verify(context).close();
  }

  @Test
  void shouldShutdownOnStartFailure() throws Exception {
    when(event.getApplicationContext()).thenReturn(context);
    when(operator.getRegisteredControllers()).thenReturn(Set.of(mock(RegisteredController.class)));
    doThrow(new IllegalStateException("False Knees")).when(operator).start();

    starter.start(event);

    verify(crdUploader).upload();
    verify(operator).start();
    verify(context).close();
  }

  @Test
  void shouldNotStartWithoutReconcilers() throws Exception {
    starter.start(event);

    verify(operator, never()).start();
    verify(crdUploader, never()).upload();
  }

}
