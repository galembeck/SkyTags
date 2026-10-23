package net.skyminigames.tags.tag;

import org.bukkit.ChatColor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerTagTest {

    private static PlayerTag tag(String group, int rank, String prefix) {
        return new PlayerTag(group, rank, prefix, "");
    }

    @Test
    void tabListIsSortedByRank() {
        List<PlayerTag> tags = Arrays.asList(
                tag("Membro", 700, "§7"),
                tag("Master", 100, "§6[Master] "),
                tag("Ajudante", 600, "§e[Ajudante] "),
                tag("Desenvolvedor", 300, "§b[Dev] "),
                tag("YouTuber", 400, "§c[YouTuber] "),
                tag("Gerente", 200, "§4[Gerente] "),
                tag("Moderador", 500, "§2[Moderador] "));

        List<String> names = new ArrayList<>();
        for (PlayerTag tag : tags) {
            names.add(tag.teamName());
        }
        Collections.sort(names);

        List<String> groupsInOrder = new ArrayList<>();
        for (String name : names) {
            for (PlayerTag tag : tags) {
                if (tag.teamName().equals(name)) {
                    groupsInOrder.add(tag.getGroup());
                }
            }
        }
        assertEquals(Arrays.asList("Master", "Gerente", "Desenvolvedor", "YouTuber", "Moderador", "Ajudante", "Membro"), groupsInOrder);
    }

    @Test
    void teamNamesFitMinecraftLimitAndAreRecognised() {
        PlayerTag longGroup = tag("Desenvolvedor_Senior_Extra", 300, "§b[Dev] ");
        String name = longGroup.teamName();
        assertTrue(name.length() <= PlayerTag.MAX_LENGTH, name);
        assertTrue(name.startsWith("300"));
        assertTrue(PlayerTag.isTeamName(name));
        assertFalse(PlayerTag.isTeamName("someOtherTeam"));
    }

    @Test
    void rankIsClampedToThreeDigits() {
        assertTrue(tag("X", 5000, "").teamName().startsWith("999"));
        assertTrue(tag("X", -3, "").teamName().startsWith("000"));
    }

    @Test
    void differentPrefixesInSameGroupGetDifferentTeams() {
        assertNotEquals(tag("Membro", 700, "§7").teamName(), tag("Membro", 700, "§a[VIP] ").teamName());
    }

    @Test
    void chatLineBreaksAreRemovedFromTags() {
        // Staff suffix "\n&r" exists for Legendchat's blank lines; tags must stay on one line
        assertEquals("§r", TagManager.colored("\n&r"));
        assertEquals("§6[Master] ", TagManager.colored("&6[Master] "));
        assertEquals("", TagManager.colored(null));
    }

    @Test
    void limitCutsAt16WithoutDanglingColourSymbol() {
        String desenvolvedor = ChatColor.AQUA + "[Desenvolvedor] ";
        assertEquals(16, PlayerTag.limit(desenvolvedor).length());

        // 15 visible chars + colour symbol at position 16 -> the symbol must be dropped
        String dangling = "abcdefghijklmno" + ChatColor.COLOR_CHAR + "c";
        assertEquals("abcdefghijklmno", PlayerTag.limit(dangling));

        assertEquals("", PlayerTag.limit(null));
        assertEquals("§6[Master] ", PlayerTag.limit("§6[Master] "));
    }
}
