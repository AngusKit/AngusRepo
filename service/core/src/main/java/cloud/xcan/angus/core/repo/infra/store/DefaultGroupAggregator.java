package cloud.xcan.angus.core.repo.infra.store;

import cloud.xcan.angus.core.repo.domain.format.ArtifactFormatHandler;
import cloud.xcan.angus.core.repo.domain.format.FormatHandlerRegistry;
import cloud.xcan.angus.core.repo.domain.format.store.BlobStore;
import cloud.xcan.angus.core.repo.domain.format.store.GroupAggregator;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link GroupAggregator}.
 *
 * <p>Aggregates content from member repositories defined in a group repository's
 * settings. The member repository IDs are parsed from the group repository's
 * JSON settings field.
 */
@Slf4j
@Component
public class DefaultGroupAggregator implements GroupAggregator {

  private static final String MEMBER_REPOS_KEY = "memberRepositoryIds";
  private static final String CACHE_TENANT = "_group";

  private final FormatHandlerRegistry formatHandlerRegistry;
  private final BlobStore blobStore;
  private final ObjectMapper objectMapper;

  public DefaultGroupAggregator(FormatHandlerRegistry formatHandlerRegistry, BlobStore blobStore) {
    this.formatHandlerRegistry = formatHandlerRegistry;
    this.blobStore = blobStore;
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public byte[] mergeIndex(RepoEntity groupRepo) {
    log.debug("Merging index for group repository: {}", groupRepo.getName());
    ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(groupRepo.getFormat());
    return handler.generateIndex(groupRepo);
  }

  @Override
  public Optional<InputStream> resolveArtifact(RepoEntity groupRepo, String path) {
    log.debug("Resolving artifact path={} from group repository: {}", path, groupRepo.getName());
    List<String> memberRepoIds = parseMemberRepositoryIds(groupRepo);
    if (memberRepoIds.isEmpty()) {
      log.warn("No member repositories configured for group repository: {}", groupRepo.getName());
      return Optional.empty();
    }
    for (String memberRepoId : memberRepoIds) {
      if (blobStore.exists(CACHE_TENANT, memberRepoId, path)) {
        log.debug("Artifact found in member repository: {}", memberRepoId);
        return Optional.of(blobStore.retrieve(CACHE_TENANT, memberRepoId, path));
      }
    }
    log.debug("Artifact not found in any member repository for path: {}", path);
    return Optional.empty();
  }

  /**
   * Parse member repository IDs from the group repository's settings JSON.
   *
   * <p>Expected settings format: {@code {"memberRepositoryIds": ["id1", "id2", ...]}}
   */
  private List<String> parseMemberRepositoryIds(RepoEntity groupRepo) {
    String settings = groupRepo.getSettings();
    if (settings == null || settings.isBlank()) {
      return Collections.emptyList();
    }
    try {
      Map<String, Object> settingsMap = objectMapper.readValue(settings,
          new TypeReference<Map<String, Object>>() {});
      Object memberIds = settingsMap.get(MEMBER_REPOS_KEY);
      if (memberIds instanceof List<?> list) {
        return list.stream()
            .map(String::valueOf)
            .toList();
      }
      return Collections.emptyList();
    } catch (Exception e) {
      log.error("Failed to parse settings for group repository {}: {}",
          groupRepo.getName(), e.getMessage());
      return Collections.emptyList();
    }
  }
}
