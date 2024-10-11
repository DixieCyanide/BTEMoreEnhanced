# BTEMoreEnhanced (FAWE edition) 🍝

Bukkit plugin created for the BuildtheEarth project to make creating custom forests easier. Uses [Bridson's algorithm](https://sighack.com/post/poisson-disk-sampling-bridsons-algorithm) for poisson disk sampling (randomly picking packed points to place trees at).


[![](https://bstats.org/signatures/bukkit/BTEMoreEnhanced.svg)](https://bstats.org/plugin/bukkit/BTEMoreEnhanced "BTEMoreEnhanced on bStats")

**Treepack:**
<details>
    <summary>Download link</summary>
    https://www.dropbox.com/s/glw3837szae16rc/newtrees.zip
</details>

**Commands:**
<details>
    <summary>//wood {schematic(s)} [!]{block ID(s)} [flags: -includeAir,-dontRotate,-r:x]</summary>
*(Aliases: //wood, //w)* More info in "How to use //wood"
</details>
<details>
    <summary>//treebrush {type} [height/size] [size/thickness] [thickness]
    /treebrush -s {schematicName} </summary>
    *(Aliases: //tbr, //treebr, //treebrush)* Easy to use brush specifically for trees on top of //schbr ([Schematic Brush Plugin](https://github.com/mikeprimm/SchematicBrush)). Ex: /treebr oak M any thin | 
    Use -s flag if you want to use specific tree. Ex: //treebr -s general01
</details>
<details>
    <summary>/bteenhanced-reload</summary>
    Reload config
</details>
<details>
    <summary>//dell [num]</summary>
    *(Aliases: //dellast, //dell)* Deletes the last `[num]` amount of points in the selection. (Currently only supports poly2d and convex selections) If `[num]` is not specified it will delete the last point.
</details>
<details>
    <summary>//delf [num]</summary>
    *(Aliases: //delfirst, //delf)* Deletes the first `[num]` amount of points in the selection. (Currently only supports poly2d and convex selections) If `[num]` is not specified it will delete the first point.
</details>
<details>
    <summary>//delp {num}</summary>
    *(Aliases: //delpoint, //delp)* Deletes the `{num}`'th point in the selection. (Currently only supports poly2d and convex selections)
</details>
<details>
    <summary>//terraform {height} [delbot] [deltop]</summary>
    *(Aliases: //terraform, //terr, //tf)* Allows for easy terraforming to desired {height} and vertical cleanup under([delbot]) and over([deltop]) selected height. (Available for cuboid and poly2d selections)
</details>
<details>
    <summary>//reach {length}</summary>
    *(Aliases: //reach)* Changes interaction distance {length} with blocks and entities. If {length} is not specified, reverts to default values.
</details>

**Permissions:** Look [here](src/main/resources/plugin.yml)

**Config:** Look [here](src/main/resources/config.yml)

**Dependencies:**
- `FastAsyncWorldEdit`
- `SchematicBrushReborn`

## How to use //wood
First make a region selection, all selections such as cuboid, poly, and convex work.
*This plugin saves edit sessions from /wood to the player's local session, so players can use WorldEdit's //undo and //redo. This means players will need to have the WorldEdit permissions for //undo and //redo*

`{schematic(s)}` is the path of a schematic file or a folder containing schematics. (From the WorldEdit schematics folder)
Adding a * after the file separator will randomize the schematics from that folder (and sub folders).

`[!]{block ID(s)}` are the blocks you want trees to be placed above. If you add a "!" at the start it uses all blocks except the ones you mention.

### Flags
All are **optional**
<details>
    <summary>-includeAir</summary>
    Equivalent of not adding -a when pasting with WorldEdit. (By default command ignores air blocks)
</details>
<details>
    <summary>-dontRotate</summary>
    Disables the random rotation (90 degree increments) of schematics.
</details>
<details>
    <summary>-r:x</summary>
    Overrides the automatically created default radius. Radius being the minimum spacing between trees. The radius by default is calculated by averaging the width or height (whichever is larger), and dividing by 2. An example of the flag being used is -r:10
</details>

## Examples
These schematic paths are for trees from the BTE tree pack.
<details>
    <summary>//wood oak M grass_block,moss_block</summary>
    Uses all schematics in `plugins/FastAsyncWorldEdit/schemtics/newtrees/oak/M/`, including subdirectories. grass_block is the block ID for grass blocks, and moss_block is the block ID for moss block, meaning trees will only be placed above grass and moss.
</details>
<details>
    <summary>//wood snowy M 22,23 !lime_wool -dontRotate -r:6</summary>
    Uses only trees with height of 22 and 23 blocks. Trees are pasted above all blocks except lime wool. `-dontRotate` prevents a random rotation from being applied to each tree. `-r:6` overrides the radius to 6.
</details>
<details>
    <summary>//wood -s longleaf018 !lime_wool -includeAir</summary>
    Uses only longleaf018.schematic. Trees are pasted above all blocks except lime wool. `-includeAir` pastes schematic with all air blocks, as if //paste was used.
</details>
