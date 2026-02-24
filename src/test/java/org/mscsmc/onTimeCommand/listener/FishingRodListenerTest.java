package org.mscsmc.onTimeCommand.listener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mscsmc.onTimeCommand.OnTimeCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FishingRodListenerTest {

    @TempDir
    Path tempDir;
    private FishingRodListener fishingRodListener;
    private Player player;
    private Location location;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        OnTimeCommand plugin = mock(OnTimeCommand.class);
        player = mock(Player.class);
        location = mock(Location.class);
        World world = mock(World.class);
        Logger logger = mock(Logger.class);

        // Configure mocks
        when(plugin.getDataFolder()).thenReturn(tempDir.toFile());
        when(plugin.getLogger()).thenReturn(logger);
        when(player.getName()).thenReturn("TestPlayer");
        when(location.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("test_world");
        when(location.getX()).thenReturn(123.45);
        when(location.getY()).thenReturn(64.0);
        when(location.getZ()).thenReturn(-78.90);

        // Create listener instance
        fishingRodListener = new FishingRodListener(plugin);
    }

    @Test
    void testHandleSummonedTNTPlayerList_CreatesNewFile() throws IOException {
        // Given
        File jsonFile = new File(tempDir.toFile(), "summonedTNTPlayerList.json");
        assertFalse(jsonFile.exists(), "JSON file should not exist initially");

        // When
        fishingRodListener.handleSummonedTNTPlayerList(player, location);

        // Then
        assertTrue(jsonFile.exists(), "JSON file should be created");

        // Verify file content
        String content = Files.readString(jsonFile.toPath());
        assertNotNull(content, "File content should not be null");
        assertFalse(content.isEmpty(), "File content should not be empty");

        // Parse JSON and verify structure
        Gson gson = new GsonBuilder().create();
        List<TNTPRecord> records = gson.fromJson(content, new TypeToken<List<TNTPRecord>>() {
        }.getType());
        assertNotNull(records, "Records should not be null");
        assertEquals(1, records.size(), "Should contain exactly one record");

        TNTPRecord record = records.getFirst();
        assertEquals("TestPlayer", record.getPlayer(), "Player name should match");
        assertTrue(record.getSummonLocation().contains("test_world"),
                "Location should contain world name");
    }

    @Test
    void testHandleSummonedTNTPlayerList_AddsToExistingFile() throws IOException {
        // Given - Create initial file with one record
        File jsonFile = new File(tempDir.toFile(), "summonedTNTPlayerList.json");
        Gson gson = new GsonBuilder().create();

        // Create initial record manually to avoid Gson issues
        String initialJson = "[{\"player\":\"InitialPlayer\",\"summonLocation\":\"World: initial, X: 0.00, Y: 0.00, Z: 0.00\",\"time\":\"2026-01-01 12:00:00\"}]";
        Files.writeString(jsonFile.toPath(), initialJson);
        assertTrue(jsonFile.exists(), "Initial file should exist");

        // When
        fishingRodListener.handleSummonedTNTPlayerList(player, location);

        // Then
        String content = Files.readString(jsonFile.toPath());
        List<TNTPRecord> records = gson.fromJson(content, new TypeToken<List<TNTPRecord>>() {
        }.getType());
        assertEquals(2, records.size(), "Should contain two records");

        // Verify both records exist
        TNTPRecord firstRecord = records.get(0);
        assertEquals("InitialPlayer", firstRecord.getPlayer(), "First record player should match");

        TNTPRecord secondRecord = records.get(1);
        assertEquals("TestPlayer", secondRecord.getPlayer(), "Second record player should match");
    }

    @Test
    void testTNTPRecord_GettersAndSetters() {
        // Given
        TNTPRecord record = new TNTPRecord();
        String playerName = "TestPlayer";
        String locationStr = "World: test, X: 1.00, Y: 2.00, Z: 3.00";
        String timeStr = "2026-01-01 12:00:00";

        // When
        record.setPlayer(playerName);
        record.setSummonLocation(locationStr);
        record.setTime(timeStr);

        // Then
        assertEquals(playerName, record.getPlayer(), "Player getter should return correct value");
        assertEquals(locationStr, record.getSummonLocation(), "Location getter should return correct value");
        assertEquals(timeStr, record.getTime(), "Time getter should return correct value");
    }
}