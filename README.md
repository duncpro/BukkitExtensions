# BukkitExtensions
A toolkit for easing serverside Minecraft development on Bukkit.

## Features
This is not an exhaustive list of features.
- **Player Heading Service** which tracks player location over time to
estimate future position. 
- **Persistent Chunk Map** for associating persistent data with specific
blocks within a world.
- **Perspective Cuboid** for modeling logically oriented structures within the world.
- **Schematic Service Abstraction** for easing interaction with schematic libraries
such as WorldEdit.
- **Command Framework** which leverages Apache Commons CLI for parsing
UNIX-style command strings. Uses a declarative API based on an annotations.
Supports customization.
- **IocJavaPlugin** is an extensions of Bukkit's **JavaPlugin** which
supports dependency injection via **Guice**. See **Default Injections**.
- **Logging Service** to ease debugging by routing messages to player's 
in-game.
- **Temporary Entity Service** for preventing persistence of entities over
restart boundaries. 
- Idempotent operations over Bukkit's **Vector**, such as `sum`, `difference`,
`product`, `quotient`, etc.
- **Region Selection Abstraction** for easing integration with plugins such as **WorldEdit**.
- **Extent Locking Service** for temporarily preventing construction and destruction over regions of the
world.
- **Temporary Metadata Service** for associating expiring metadata with Blocks, Entities, etc.  
- `//trim` for trimming selections made with plugins such as `WorldEdit`.
  
## Includes Libraries
- Guice for Dependency Injection
- H2 for Storing Relational Data
- Apache Commons CLI

## Usage
Should be installed on the server as a companion plugin and not shaded into consumer's jar file.

You may also need third-party plugins to provide service implementations which do not ship with
BukkitExtensions. Consider using [Default Integrations for BukkitExtensions](https://github.com/duncpro/Default-Integrations-for-BukkitExtensions).
