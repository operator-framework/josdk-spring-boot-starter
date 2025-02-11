package io.javaoperatorsdk.operator.springboot.starter.properties;

import java.util.Optional;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KubernetesClientProperties {

  boolean openshift = false;
  String context;
  String username;
  String password;
  String oauthToken;
  String masterUrl;
  boolean trustSelfSignedCertificates = false;

  public Optional<String> getContext() {
    return Optional.ofNullable(context);
  }

  public Optional<String> getUsername() {
    return Optional.ofNullable(username);
  }

  public Optional<String> getPassword() {
    return Optional.ofNullable(password);
  }

  public Optional<String> getOauthToken() {
    return Optional.ofNullable(oauthToken);
  }

  public Optional<String> getMasterUrl() {
    return Optional.ofNullable(masterUrl);
  }
}
