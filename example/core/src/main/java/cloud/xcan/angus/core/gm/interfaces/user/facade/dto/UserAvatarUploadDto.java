package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserAvatarUploadDto {

  private MultipartFile file;

}
