# SkyTags

Role tags for the **SkyMinigames** server (Spigot/Paper 1.8.8, Java 8).

## Features
- Coloured role prefix above players' heads and in the tab list (e.g. `[Master] ImLighTzzZ_`)
- Tab list sorted by role: Master, Gerente, Desenvolvedor, YouTuber, Moderador, Ajudante, Membro
- Tab list header and footer with the online player count
- Role changes (`pex user <nick> group set <role>`) show up within a few seconds, no rejoin needed
- `/skytags reload` (permission `skytags.admin`)

## Where the data comes from
Groups, prefixes and ranks are read from **PermissionsEx through Vault** (both required on the server):

| PermissionsEx group option | Used for |
|---|---|
| `prefix` / `suffix` | Tag shown before / after the name |
| `rank` | Order in the tab list (lower = higher) |

Minecraft 1.8 limits tags to 16 characters, colour codes included. Groups with a longer chat prefix
can get a shorter tag in `config.yml` (`groups.<Group>.prefix`); `Desenvolvedor` uses `[Dev]` by default.

## Project layout
| Package | Purpose |
|---|---|
| `tag` | Reads tags from Vault and applies them as scoreboard teams |
| `tab` | Tab list header and footer |
| `listener` | Join / quit |
| `command` | `/skytags` |
| `config` / `message` | `config.yml` and `messages.yml` |
| `util` | Startup banner |

## Build
```bash
mvn clean package
```
In VS Code, run the **Deploy to server** task to build and copy the jar to the SkyMinigames server's `plugins` folder
(stop the server first).
