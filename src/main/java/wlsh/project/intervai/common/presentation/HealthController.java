package wlsh.project.intervai.common.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wlsh.project.intervai.common.presentation.dto.HealthResponse;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("UP"));
    }
}
