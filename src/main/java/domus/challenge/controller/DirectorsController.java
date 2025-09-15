package domus.challenge.controller;

import domus.challenge.api.DirectorsResponse;
import domus.challenge.context.Constants;
import domus.challenge.service.DirectorsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = Constants.API_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class DirectorsController {

    private final DirectorsService directorsService;

    @Operation(
            summary = "Directors with a number of movies greater than the threshold",
            description = """
                    Returns the names of directors whose movie count is strictly greater than the `threshold` parameter.
                    - If `threshold` < 0 => empty list is returned.
                    - If `threshold` is not numeric => error 400. \n
                    Names are returned alphabetically sorted (case-insensitive).
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = DirectorsResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid parameter",
                            content = @Content)
            }
    )
    @GetMapping(Constants.SERVICE_DIRECTORS)
    public Mono<DirectorsResponse> getDirectorsOverThreshold(
            @Parameter(description = "Strict threshold. Returns directors with #movies > threshold",
                    example = "4")
            @RequestParam(name = "threshold") @NotNull Integer threshold
    ) {
        return directorsService.findDirectorsOverThreshold(threshold)
                .map(DirectorsResponse::new);
    }

}
