package cloud.xcan.angus.core.repo.domain.format;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ValidationResultTest {

  @Test
  void testSuccessResult() {
    ValidationResult result = ValidationResult.success();
    assertThat(result.isValid()).isTrue();
    assertThat(result.hasErrors()).isFalse();
    assertThat(result.hasWarnings()).isFalse();
    assertThat(result.getErrors()).isEmpty();
    assertThat(result.getWarnings()).isEmpty();
  }

  @Test
  void testFailureResultWithSingleError() {
    ValidationResult result = ValidationResult.failure("test error");
    assertThat(result.isValid()).isFalse();
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).containsExactly("test error");
  }

  @Test
  void testFailureResultWithMultipleErrors() {
    ValidationResult result = ValidationResult.failure(java.util.List.of("error1", "error2"));
    assertThat(result.isValid()).isFalse();
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).containsExactly("error1", "error2");
  }

  @Test
  void testAddError() {
    ValidationResult result = ValidationResult.success();
    assertThat(result.isValid()).isTrue();
    result.addError("new error");
    assertThat(result.isValid()).isFalse();
    assertThat(result.hasErrors()).isTrue();
    assertThat(result.getErrors()).containsExactly("new error");
  }

  @Test
  void testAddWarning() {
    ValidationResult result = ValidationResult.success();
    result.addWarning("warning");
    assertThat(result.isValid()).isTrue();
    assertThat(result.hasWarnings()).isTrue();
    assertThat(result.getWarnings()).containsExactly("warning");
  }
}
