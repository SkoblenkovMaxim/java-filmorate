package ru.yandex.practicum.filmorate.controller.director;

import ru.yandex.practicum.filmorate.model.director.DirectorDto;
import ru.yandex.practicum.filmorate.service.director.DirectorService;
import ru.yandex.practicum.filmorate.model.director.DirectorMapper;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;
    private final DirectorMapper directorMapper;

    @GetMapping
    public Collection<DirectorDto> getAllDirectors() {
        return directorService.getAllDirectors()
                .stream()
                .map(directorMapper::toDirectorDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DirectorDto getDirectorById(@PathVariable Long id) {
        return directorMapper.toDirectorDto(directorService.getDirectorById(id));
    }

    @PostMapping
    public DirectorDto createDirector(@Valid @RequestBody DirectorDto directorDto) {
        var director = directorMapper.toDirector(directorDto);
        return directorMapper.toDirectorDto(directorService.createDirector(director));
    }

    @PutMapping
    public DirectorDto updateDirector(@Valid @RequestBody DirectorDto directorDto) {
        var director = directorMapper.toDirector(directorDto);
        return directorMapper.toDirectorDto(directorService.updateDirector(director));
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Long id) {
        directorService.deleteDirector(id);
    }
}
