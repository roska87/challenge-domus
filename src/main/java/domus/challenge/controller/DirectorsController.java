package domus.challenge.controller;

import domus.challenge.api.DirectorsResponse;
import domus.challenge.service.DirectorsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class DirectorsController {

    private final DirectorsService directorsService;

    @Operation(
            summary = "Directores con cantidad de películas mayor al umbral",
            description = """
                    Devuelve los nombres de directores cuyo conteo de películas (año > 2010 según la API de origen)
                    es estrictamente mayor que el parámetro `threshold`. 
                    - Si `threshold` < 0 => se devuelve lista vacía.
                    - Si `threshold` no es numérico => error 400.
                    Los nombres se devuelven ordenados alfabéticamente (case-insensitive).
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = DirectorsResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Parámetro inválido",
                            content = @Content)
            }
    )
    @GetMapping("/directors")
    public Mono<DirectorsResponse> getDirectorsOverThreshold(
            @Parameter(description = "Umbral estricto. Devuelve directores con #películas > threshold",
                    example = "4")
            @RequestParam(name = "threshold") @NotNull Integer threshold
    ) {
        return directorsService.findDirectorsOverThreshold(threshold)
                .map(DirectorsResponse::new);
    }

}
