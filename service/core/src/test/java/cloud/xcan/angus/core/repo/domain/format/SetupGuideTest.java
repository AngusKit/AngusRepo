package cloud.xcan.angus.core.repo.domain.format;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SetupGuideTest {

  @Test
  void testSetupGuideCreation() {
    SetupGuide guide = new SetupGuide("Maven", "https://repo.example.com", "<config/>");
    assertThat(guide.getFormatName()).isEqualTo("Maven");
    assertThat(guide.getRepositoryUrl()).isEqualTo("https://repo.example.com");
    assertThat(guide.getConfigSnippet()).isEqualTo("<config/>");
  }

  @Test
  void testAddInstruction() {
    SetupGuide guide = new SetupGuide("Maven", "https://repo.example.com", "<config/>");
    guide.addInstruction("Step 1", "Do something");
    guide.addInstruction("Step 2", "Do another thing");
    assertThat(guide.getInstructions()).hasSize(2);
    assertThat(guide.getInstructions().get("Step 1")).isEqualTo("Do something");
    assertThat(guide.getInstructions().get("Step 2")).isEqualTo("Do another thing");
  }

  @Test
  void testDefaultInstructionsIsEmpty() {
    SetupGuide guide = new SetupGuide();
    assertThat(guide.getInstructions()).isEmpty();
  }
}
