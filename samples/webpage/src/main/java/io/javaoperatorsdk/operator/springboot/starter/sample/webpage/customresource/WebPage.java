package io.javaoperatorsdk.operator.springboot.starter.sample.webpage.customresource;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("sample.javaoperatorsdk")
@Version("v1")
public class WebPage extends CustomResource<WebPageSpec, WebPageStatus> implements Namespaced {

  @Override
  public String toString() {
    return "WebPage{" + "spec=" + spec + ", status=" + status + '}';
  }
}
