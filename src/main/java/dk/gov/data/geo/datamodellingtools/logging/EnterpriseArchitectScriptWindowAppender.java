package dk.gov.data.geo.datamodellingtools.logging;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.status.ErrorStatus;
import dk.gov.data.geo.datamodellingtools.ea.EnterpriseArchitectWrapper;

/**
 * Appender, configured in logback.xml, that writes to the Script Window in Enterprise Architect.
 *
 * @param <E> Is {@link ILoggingEvent} as logback-classic is used.
 */
public class EnterpriseArchitectScriptWindowAppender<E> extends AppenderBase<E> {

  private EnterpriseArchitectWrapper enterpriseArchitectWrapper;

  private PatternLayoutEncoder encoder;

  @Override
  public void start() {
    if (this.enterpriseArchitectWrapper == null) {
      return;
    }
    super.start();
  }

  @Override
  protected void append(E eventObject) {
    if (!isStarted()) {
      return;
    }

    try {
      String message = this.encoder.getLayout().doLayout((ILoggingEvent) eventObject);
      enterpriseArchitectWrapper.writeToScriptWindow(message);
    } catch (Exception e) {
      this.started = false;
      addStatus(new ErrorStatus("Failure in appender", this, e));
    }

  }

  public PatternLayoutEncoder getEncoder() {
    return encoder;
  }

  public void setEncoder(PatternLayoutEncoder encoder) {
    this.encoder = encoder;
  }

  public EnterpriseArchitectWrapper getEnterpriseArchitectWrapper() {
    return enterpriseArchitectWrapper;
  }

  public void setEnterpriseArchitectWrapper(EnterpriseArchitectWrapper enterpriseArchitectWrapper) {
    this.enterpriseArchitectWrapper = enterpriseArchitectWrapper;
  }

}
