# AMTG

AMTG generates material tags to be used with [Apoli](https://github.com/apace100/apoli), which does most of the heavy-lifting for the [Origins](https://github.com/apace100/origins-fabric) mod.

This mod was created as a result of Mojang's decision to remove the `Material` class from the game in favor of specifying block properties on a block-by-block basis. The removal of the `Material` class broke Apoli's Material block condition, which in turn broke backward-compatibility. To circumvent the missing `Material` class the mod instead opted to use tags to determine block materials. While every vanilla block up to game version 1.19.4 already has the necessary material tags, modded blocks remain non-functional.

AMTG was created to solve the problem of missing modded blocks. When the game is loaded in version 1.19.4 with this mod installed a datapack will be generated in the game directory (usually `%APPDATA%\.minecraft` when the mod is installed on the client, or in the directory where the server resides when installed on a dedicated server) called `Apoli-Material-Tags.zip`. This datapack contains material definitions for any registered blocks, including modded ones. When this datapack is loaded into the game in version 1.20+ the Material block condition will once again work as intended.

Please note that any blocks added between 1.19.4 and whatever version the datapack is used in will not be included and have to be manually added in a handwritten datapack. Any modded `Material` types will also not be included in the datapack.
