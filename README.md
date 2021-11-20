# BTEEnhanced (1.12.2) 🍝

Bukkit plugin created for the BuildtheEarth project to make creating custom forests easier. Uses [Bridson's algorithm](https://sighack.com/post/poisson-disk-sampling-bridsons-algorithm) for poisson disk sampling (randomly picking packed points to place trees at).

Commands:
- `/wood <schematic(s)> [!]<blockID> [flags -includeAir,-dontRotate,-r:x]` Explained in the next section
- `/wood-undo` Undoes /wood, max number of undoes is set in config
- `/wood-redo` Redoes /wood, max number of redoes is set in config

Permissions:
- `bteenhanced.*`  Wildcard
- `bteenhanced.reload` Reload config
- `bteenhanced.wood`Allows usage of /wood, /wood-undo, and /wood-redo
- `bteenhanced.wood.all` Allows setting the /wood directory to *, using all schematics in the folder. Not a good idea since there will likely be large builds there. Instead, trees should be placed in a tree folder.

Config: Look at the config.yml

Dependencies:
- `WorldEdit`

## How to use /wood
< schematic(s) > is the path of a schematic file or a folder containing schematics. (From the WorldEdit schematics folder)
Adding a * after the file separator will randomize the schematics from that folder (and subfolders).

[!]< blockID > is the block you want trees to be placed above. If you add a ! at the start it uses all blocks except the one you mention.

### Flags
All are **optional**
- `-includeAir` is the equivalent of not adding -a when pasting. (By default ignores air blocks)
- `-dontRotate` disables the random rotation (90 degree increments) of schematics.
- `-r:x` overrides the automatically created default radius. Radius being the minimum spacing between trees. The radius by default is calculated by averaging the width or height (whichever is larger), and dividing by 2. An example of the flag being used is -r:10

## Examples
These schematic paths are for trees from the BTE tree pack.
- `/wood trees/oak/M/* 2` Uses all schematics in `plugins/WorldEdit/trees/oak/M/`, including subdirectories. 2 is the block ID for grass blocks, meaning trees will only be placed above grass blocks.
- `/wood trees/snow/S/Pine_Snowy_Small !35:5 -dontRotate -r:6` Uses only the Pine_Snowy_Small.schematic. Trees are pasted above all blocks except 35:5, which is green wool. `-dontRotate` prevents a random rotation from being applied to each tree. `-r:6` overrides the radius to 6.