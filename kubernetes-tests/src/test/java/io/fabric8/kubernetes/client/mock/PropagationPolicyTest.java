/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.kubernetes.client.mock;

import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.DeletionPropagation;
import io.fabric8.kubernetes.api.model.KubernetesListBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.ReplicationControllerBuilder;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinitionBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.kubernetes.api.model.batch.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.kubernetes.client.mock.crd.DoneablePodSet;
import io.fabric8.kubernetes.client.mock.crd.PodSet;
import io.fabric8.kubernetes.client.mock.crd.PodSetList;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableRuleMigrationSupport
public class PropagationPolicyTest {
  @Rule
  public KubernetesServer server = new KubernetesServer();

  @Test
  @DisplayName("Should delete a ConfigMap with PropagationPolicy=Background")
  public void testDeleteConfigMap() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/api/v1/namespaces/ns1/configmaps/myconfigMap")
      .andReturn(HttpURLConnection.HTTP_OK, new ConfigMapBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.configMaps().inNamespace("ns1").withName("myconfigMap").delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.BACKGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a ConfigMap with specified PropagationPolicy")
  public void testDeleteConfigMapWithExplicitPropagationPolicy() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/api/v1/namespaces/ns1/configmaps/myconfigMap")
      .andReturn(HttpURLConnection.HTTP_OK, new ConfigMapBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.configMaps().inNamespace("ns1").withName("myconfigMap").withPropagationPolicy(DeletionPropagation.FOREGROUND).delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.FOREGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a Service with PropagationPolicy=Background")
  public void testDeleteService() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/api/v1/namespaces/ns1/services/myservice")
      .andReturn(HttpURLConnection.HTTP_OK, new ServiceBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.services().inNamespace("ns1").withName("myservice").delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.BACKGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a Service with specified PropagationPolicy")
  public void testDeleteServiceWithPropagationPolicy() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/api/v1/namespaces/ns1/services/myservice")
      .andReturn(HttpURLConnection.HTTP_OK, new ServiceBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.services().inNamespace("ns1").withName("myservice").delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.BACKGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a Service with PropagationPolicy=Background")
  public void testDeleteSecret() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/api/v1/namespaces/ns1/secrets/mysecret")
      .andReturn(HttpURLConnection.HTTP_OK, new SecretBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.secrets().inNamespace("ns1").withName("mysecret").delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.BACKGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a Secret with specified PropagationPolicy")
  public void testDeleteSecretWithExplicitPropagationPolicy() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/api/v1/namespaces/ns1/secrets/mysecret")
      .andReturn(HttpURLConnection.HTTP_OK, new SecretBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.secrets().inNamespace("ns1").withName("mysecret").withPropagationPolicy(DeletionPropagation.FOREGROUND).delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.FOREGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a Deployment with PropagationPolicy=Background")
  public void testDeleteDeployment() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/apis/apps/v1/namespaces/ns1/deployments/mydeployment")
      .andReturn(HttpURLConnection.HTTP_OK, new DeploymentBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.apps().deployments().inNamespace("ns1").withName("mydeployment").delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.BACKGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a Deployment with explicitly set PropagationPolicy")
  public void testDeleteDeploymentWithExplicitPropagationPolicy() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/apis/apps/v1/namespaces/ns1/deployments/mydeployment")
      .andReturn(HttpURLConnection.HTTP_OK, new DeploymentBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.apps().deployments().inNamespace("ns1").withName("mydeployment").withPropagationPolicy(DeletionPropagation.FOREGROUND).delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.FOREGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a StatefulSet with PropagationPolicy=Background")
  public void testDeleteStatefulSet() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/apis/apps/v1/namespaces/ns1/statefulsets/mystatefulset")
      .andReturn(HttpURLConnection.HTTP_OK, new StatefulSetBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.apps().statefulSets().inNamespace("ns1").withName("mystatefulset").delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.BACKGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a StatefulSet with explicitly set PropagationPolicy")
  public void testDeleteStatefulSetWithExplicitPropagationPolicy() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/apis/apps/v1/namespaces/ns1/statefulsets/mystatefulset")
      .andReturn(HttpURLConnection.HTTP_OK, new StatefulSetBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.apps().statefulSets().inNamespace("ns1").withName("mystatefulset").withPropagationPolicy(DeletionPropagation.FOREGROUND).delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.FOREGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a ReplicationController with PropagationPolicy=Background")
  public void testDeleteReplicationController() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/api/v1/namespaces/ns1/replicationcontrollers/myreplicationcontroller")
      .andReturn(HttpURLConnection.HTTP_OK, new ReplicationControllerBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.replicationControllers().inNamespace("ns1").withName("myreplicationcontroller").delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.BACKGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a ReplicationController with explicitly specified PropagationPolicy")
  public void testDeleteReplicationControllerWithExplicitPropagationPolicy() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/api/v1/namespaces/ns1/replicationcontrollers/myreplicationcontroller")
      .andReturn(HttpURLConnection.HTTP_OK, new ReplicationControllerBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.replicationControllers().inNamespace("ns1").withName("myreplicationcontroller").withPropagationPolicy(DeletionPropagation.ORPHAN).delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.ORPHAN.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a Job with PropagationPolicy=Background")
  public void testDeleteJob() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/apis/batch/v1/namespaces/ns1/jobs/myjob")
      .andReturn(HttpURLConnection.HTTP_OK, new JobBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.batch().jobs().inNamespace("ns1").withName("myjob").delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.BACKGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a Job with explicitly set PropagationPolicy")
  public void testDeleteJobWithExplicitPropagationPolicy() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/apis/batch/v1/namespaces/ns1/jobs/myjob")
      .andReturn(HttpURLConnection.HTTP_OK, new JobBuilder().build())
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.batch().jobs().inNamespace("ns1").withName("myjob").withPropagationPolicy(DeletionPropagation.FOREGROUND).delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.FOREGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a resource with PropagationPolicy=Background")
  public void testDeleteResource() throws InterruptedException {
    // Given
    Pod testPod = new PodBuilder().withNewMetadata().withName("testpod").endMetadata().build();
    server.expect().delete().withPath("/api/v1/namespaces/foo/pods/testpod")
      .andReturn(HttpURLConnection.HTTP_OK, testPod)
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.resource(testPod).inNamespace("foo").delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.BACKGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a resource with specified PropagationPolicy")
  public void testDeleteResourceWithSpecifiedPropagationPolicy() throws InterruptedException {
    // Given
    Pod testPod = new PodBuilder().withNewMetadata().withName("testpod").endMetadata().build();
    server.expect().delete().withPath("/api/v1/namespaces/foo/pods/testpod")
      .andReturn(HttpURLConnection.HTTP_OK, testPod)
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.resource(testPod).inNamespace("foo").withPropagationPolicy(DeletionPropagation.FOREGROUND).delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.FOREGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a resource list with PropagationPolicy=Background")
  public void testDeleteResourceList() throws InterruptedException {
    // Given
    Pod testPod = new PodBuilder().withNewMetadata().withName("testpod").endMetadata().build();
    server.expect().delete().withPath("/api/v1/namespaces/foo/pods/testpod")
      .andReturn(HttpURLConnection.HTTP_OK, testPod)
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.resourceList(new KubernetesListBuilder().withItems(testPod).build()).inNamespace("foo").delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.BACKGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a resource list with specified PropagationPolicy")
  public void testDeleteResourceListWithSpecifiedPropagationPolicy() throws InterruptedException {
    // Given
    Pod testPod = new PodBuilder().withNewMetadata().withName("testpod").endMetadata().build();
    server.expect().delete().withPath("/api/v1/namespaces/foo/pods/testpod")
      .andReturn(HttpURLConnection.HTTP_OK, testPod)
      .once();
    KubernetesClient client = server.getClient();

    // When
    Boolean isDeleted = client.resourceList(new KubernetesListBuilder().withItems(testPod).build()).inNamespace("foo").withPropagationPolicy(DeletionPropagation.FOREGROUND).delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.FOREGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a Raw Custom Resource with PropagationPolicy=Background")
  public void testDeleteRawCustomResource() throws InterruptedException, IOException {
    // Given
    server.expect().delete().withPath("/apis/test.fabric8.io/v1alpha1/namespaces/ns1/hellos/example-hello")
      .andReturn(HttpURLConnection.HTTP_OK, "{\"metadata\":{},\"apiVersion\":\"v1\",\"kind\":\"Status\",\"details\":{\"name\":\"prometheus-example-rules\",\"group\":\"monitoring.coreos.com\",\"kind\":\"prometheusrules\",\"uid\":\"b3d085bd-6a5c-11e9-8787-525400b18c1d\"},\"status\":\"Success\"}").once();
    KubernetesClient client = server.getClient();

    // When
    Map<String, Object> result = client.customResource(new CustomResourceDefinitionContext.Builder()
      .withGroup("test.fabric8.io")
      .withName("hellos.test.fabric8.io")
      .withPlural("hellos")
      .withScope("Namespaced")
      .withVersion("v1alpha1")
      .build())
      .delete("ns1", "example-hello");

    // Then
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.BACKGROUND.toString(), server.getLastRequest());
  }

  @Test
  @DisplayName("Should delete a Custom Resource with PropagationPolicy=Background")
  public void testDeleteCustomResource() throws InterruptedException {
    // Given
    server.expect().delete().withPath("/apis/demo.k8s.io/v1alpha1/namespaces/test/podsets/example-podset").andReturn(HttpURLConnection.HTTP_OK, new PodSet()).once();
    MixedOperation<PodSet, PodSetList, DoneablePodSet, Resource<PodSet, DoneablePodSet>> podSetClient = server.getClient().customResources(new CustomResourceDefinitionBuilder()
      .withNewMetadata().withName("podsets.demo.k8s.io").endMetadata()
      .withNewSpec()
      .withGroup("demo.k8s.io")
      .withVersion("v1alpha1")
      .withNewNames().withKind("PodSet").withPlural("podsets").endNames()
      .withScope("Namespaced")
      .endSpec()
      .build(), PodSet.class, PodSetList.class, DoneablePodSet.class);

    // When
    boolean isDeleted = podSetClient.inNamespace("test").withName("example-podset").delete();

    // Then
    assertTrue(isDeleted);
    assertDeleteOptionsInLastRecordedRequest(DeletionPropagation.BACKGROUND.toString(), server.getLastRequest());
  }

  private void assertDeleteOptionsInLastRecordedRequest(String propagationPolicy, RecordedRequest recordedRequest) {
    assertEquals("{\"apiVersion\":\"v1\",\"kind\":\"DeleteOptions\",\"propagationPolicy\":\"" + propagationPolicy + "\"}", recordedRequest.getBody().readUtf8());
  }

}
