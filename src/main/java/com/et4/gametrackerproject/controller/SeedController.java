package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.seed.GameSeeder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/seed")
@RequiredArgsConstructor
public class SeedController {

    private final GameSeeder gameSeeder;

    @PostMapping("/games")
    public String seedGames() {
        gameSeeder.seedGames();
        return "Games seeded successfully";
    }
}