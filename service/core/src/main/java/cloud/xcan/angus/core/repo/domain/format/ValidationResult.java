package cloud.xcan.angus.core.repo.domain.format;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ValidationResult {

  private boolean valid;
  private List<String> errors = new ArrayList<>();
  private List<String> warnings = new ArrayList<>();

  public ValidationResult(boolean valid) {
    this.valid = valid;
  }

  public static ValidationResult success() {
    return new ValidationResult(true);
  }

  public static ValidationResult failure(String error) {
    ValidationResult result = new ValidationResult(false);
    result.getErrors().add(error);
    return result;
  }

  public static ValidationResult failure(List<String> errors) {
    ValidationResult result = new ValidationResult(false);
    result.setErrors(errors);
    return result;
  }

  public void addError(String error) {
    this.errors.add(error);
    this.valid = false;
  }

  public void addWarning(String warning) {
    this.warnings.add(warning);
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public boolean hasWarnings() {
    return !warnings.isEmpty();
  }
}
