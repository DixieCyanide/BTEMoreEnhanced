# BTEMoreEnhanced (FAWE edition) 🍝

Bukkit plugin created for the BuildtheEarth project to make creating custom forests easier. Uses [Bridson's algorithm](https://sighack.com/post/poisson-disk-sampling-bridsons-algorithm) for poisson disk sampling (randomly picking packed points to place trees at).


[![](https://bstats.org/signatures/bukkit/BTEMoreEnhanced.svg)](https://bstats.org/plugin/bukkit/BTEMoreEnhanced "BTEMoreEnhanced on bStats")

**Treepack**
<details>
    <summary>Download link</summary>
    https://www.dropbox.com/s/glw3837szae16rc/newtrees.zip
</details>

**Commands:**
- `//wood {treepack(s)} [type(s)] [height/size(s)] [size/thickness(s)] [thickness(s)] [!]{block ID(s)} [flags: -includeAir, -dontRotate, -r:x]`
- `//wood -s {schematicName(s)} [!]{block ID(s)} [flags: -includeAir, -dontRotate, -r:x]`
&nbsp;&nbsp;*(Aliases: //wood, //w)*  
&nbsp;&nbsp;More info [here](how-to-use-//wood)

- `//treebrush {treepack(s)} [type(s)] [height/size(s)] [size/thickness(s)] [thickness(s)]`  
  `//treebrush -s {schematicName(s)}`  
&nbsp;&nbsp;*(Aliases: `//tbr`, `//treebr`, `//treebrush`)*  
&nbsp;&nbsp;Easy to use brush specifically for trees, on top of `//schbr` ([Schematic Brush Plugin](https://github.com/mikeprimm/SchematicBrush)).  
&nbsp;&nbsp;Ex: `/treebr oak M any thin`  
&nbsp;&nbsp;Use `-s` flag if you want to use specific tree. Ex: `//treebr -s general01`  
&nbsp;&nbsp;You can disable some treepacks with peronal [settings](#settings)

- `/bteenhanced-reload`  
&nbsp;&nbsp;Reload config and personal settings for online players

- `//dell [num]`  
&nbsp;&nbsp;*(Aliases: `//dellast`, `//dell`)*  
&nbsp;&nbsp;Deletes the last `[num]` amount of points in the selection.  
&nbsp;&nbsp;If `[num]` is not specified it will delete the last point.  
&nbsp;&nbsp;(Currently only supports poly2d and convex selections)

- `//delf [num]`  
&nbsp;&nbsp;*(Aliases: `//delfirst`, `//delf`)*  
&nbsp;&nbsp;Deletes the first `[num]` amount of points in the selection.  
&nbsp;&nbsp;If `[num]` is not specified it will delete the first point.  
&nbsp;&nbsp;(Currently only supports poly2d and convex selections)  

- `//delp {num}`
&nbsp;&nbsp;*(Aliases: `//delpoint`, `//delp`)*  
&nbsp;&nbsp;Deletes the `{num}`'th point in the selection.  
&nbsp;&nbsp;(Currently only supports poly2d and convex selections)  

- `//terraform {height} [delbot] [deltop]`  
&nbsp;&nbsp;*(Aliases: `//terraform`, `//terr`, `//tf`)*  
&nbsp;&nbsp;Allows for easy terraforming to desired `{height}` and vertical cleanup under(`[delbot]`) and over(`[deltop]`) selected height and biome* change (if specified in [config](src/main/resources/config.yml)/[settings](#settings)).  
&nbsp;&nbsp;You can change default values (`[delbot]`, `[deltop]`, block and biome) in [settings](#settings)  
&nbsp;&nbsp;(Available for cuboid and poly2d selections)  

- `//reach {length}`  
&nbsp;&nbsp;*(Aliases: `//reach`)*  
&nbsp;&nbsp;Changes interaction distance `{length}` with blocks and entities.  
&nbsp;&nbsp;If `{length}` is not specified, reverts to default values (In [config](src/main/resources/config.yml)/[settings](#settings): `-1` to use default minecraft values, or use any other value to override minecraft defaults)  

### Settings
The moment player joins, they get default personal config, which is, basically, a copy of customisable values of general plugin config.

- `//bmesettings`  
&nbsp;&nbsp;*(Aliases: `//bmesettings`, `//settings`)*  
&nbsp;&nbsp;Sends TUI with personal settings, which can be changed.  
&nbsp;&nbsp;Things that can be customised:
    - Reach minecraft defaults override (`-1` to use minecraft defaults)
    - Terraform top remove  
    - Terraform bottom remove  
    - Terraform block  
    - Terraform biome (`none` to disable)
    - Treepacks which player does not want to use (`none` to clear)  
&nbsp;&nbsp;**Reset** button at the bottom and `reset` argument if settings file is fucked up a little.

**Permissions:** Look [here](src/main/resources/plugin.yml)

**Config:** Look [here](src/main/resources/config.yml)

**Dependencies:**
- `FastAsyncWorldEdit`
- `SchematicBrushReborn`

## How to use //wood
First make a region selection, all selections such as `cuboid`, `poly`, and `convex` work.
*This plugin saves edit sessions from `//wood` to the player's local session, so players can use WorldEdit's `//undo` and `//redo`. This means players will need to have the WorldEdit permissions for `//undo` and `//redo`*

`{treepack(s)} [type(s)] [height/size(s)] [size/thickness(s)] [thickness(s)]` are the type(s) of trees you want to populate your forest with. Using `any` as argument will use any trees for argument player decided bot to bother with.  
&nbsp;&nbsp;Affected by `Unused treepacks` value of personal [settings](#settings)

`[!]{block ID(s)}` are the blocks you want trees to be placed above. If you add a `!` at the start it uses all blocks except the ones you mention.

### Flags
All are **optional**

- `-includeAir`  
&nbsp;&nbsp;Equivalent of not adding `-a` when pasting with WorldEdit. (By default command ignores air blocks)

- `-dontRotate`  
&nbsp;&nbsp;Disables the random rotation (90 degree increments) of schematics.

- `-r:x`  
&nbsp;&nbsp;Overrides the automatically created default radius. Radius being the minimum spacing between trees. The radius by default is calculated by averaging the width or height (whichever is larger), and dividing by 2. An example of the flag being used is `-r:10`

## Examples
These schematic paths are for trees from the BTE tree pack.  

- `//wood oak M grass_block,moss_block`
&nbsp;&nbsp;Uses all schematics in `plugins/FastAsyncWorldEdit/schemtics/newtrees/oak/M/`, including subdirectories. `grass_block` is the block ID for grass blocks, and `moss_block` is the block ID for moss block, meaning trees will only be placed above grass and moss.  

- `//wood snowy M 22,23 !lime_wool -dontRotate -r:6`  
&nbsp;&nbsp;Uses only trees with height of `22` and `23` blocks. Trees are pasted above all blocks except `lime_wool`. `-dontRotate` prevents a random rotation from being applied to each tree. `-r:6` overrides the radius to `6`.

- `//wood -s longleaf018 !lime_wool -includeAir`  
&nbsp;&nbsp;Uses only `longleaf018.schematic`. Trees are pasted above all blocks except `lime_wool`. `-includeAir` pastes schematic with all air blocks, as if `//paste` was used.
