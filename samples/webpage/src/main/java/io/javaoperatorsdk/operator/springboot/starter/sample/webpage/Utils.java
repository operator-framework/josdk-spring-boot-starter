package io.javaoperatorsdk.operator.springboot.starter.sample.webpage;

import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.javaoperatorsdk.operator.api.reconciler.ErrorStatusUpdateControl;
import io.javaoperatorsdk.operator.springboot.starter.sample.webpage.customresource.WebPage;
import io.javaoperatorsdk.operator.springboot.starter.sample.webpage.customresource.WebPageStatus;

import static io.javaoperatorsdk.operator.ReconcilerUtils.loadYaml;

public class Utils {

  private Utils() {}

  public static WebPage createWebPageForStatusUpdate(WebPage webPage, String configMapName) {
    WebPage res = new WebPage();
    res.setMetadata(
        new ObjectMetaBuilder()
            .withName(webPage.getMetadata().getName())
            .withNamespace(webPage.getMetadata().getNamespace())
            .build());
    res.setStatus(createStatus(configMapName));
    return res;
  }

  public static WebPageStatus createStatus(String configMapName) {
    WebPageStatus status = new WebPageStatus();
    status.setHtmlConfigMap(configMapName);
    status.setAreWeGood(true);
    status.setErrorMessage(null);
    return status;
  }

  public static String configMapName(WebPage nginx) {
    return nginx.getMetadata().getName() + "-html";
  }

  public static String deploymentName(WebPage nginx) {
    return nginx.getMetadata().getName();
  }

  public static String serviceName(WebPage webPage) {
    return webPage.getMetadata().getName();
  }

  public static ErrorStatusUpdateControl<WebPage> handleError(WebPage resource, Exception e) {
    resource.getStatus().setErrorMessage("Error: " + e.getMessage());
    return ErrorStatusUpdateControl.patchStatus(resource);
  }

  public static Ingress makeDesiredIngress(WebPage webPage) {
    Ingress ingress = loadYaml(Ingress.class, Utils.class, "ingress.yaml");
    ingress.getMetadata().setName(webPage.getMetadata().getName());
    ingress.getMetadata().setNamespace(webPage.getMetadata().getNamespace());
    ingress
        .getSpec()
        .getRules()
        .get(0)
        .getHttp()
        .getPaths()
        .get(0)
        .getBackend()
        .getService()
        .setName(serviceName(webPage));
    return ingress;
  }
}
