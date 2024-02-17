# Pojo
Free and open source Bukkit plugin for custom items (items only for now).

## Features
> **Note**: Some features described in here are not currently present, as Pojo is still under development.

- Custom items
- In-game editor
- Scripting support, powered by [MangoScript](https://github.com/MangoPlex/MangoScript)
- _Consider suggest a cool feature in [Issues tab](https://github.com/nahkd123/Pojo)!_

### Pojo Expansion
Pojo Expansion is an expansion plugin for Pojo, adding hooks to various popular plugins (assuming they are open sourced), as well as expanding the item components library for more cool stuffs.

### Scripting (Item components only)
> **Note**: Scripting is available for item components only!

Take custom items to the max with scripting! Make your own custom item component and apply it to your existing item, just like any other components.

Pojo Scripts are powered by [MangoScript](https://github.com/MangoPlex/MangoScript), which is another project of mine (but released under MangoPlex organization, as we want to use it for other experimental projects). The syntax should be similar to C and Java, but if you only touched Python in your life then it shouldn't be too hard to learn (spoiler: it doesn't have pointers).

## TODOs
- [ ] Social banner image
- [ ] Custom items
    + [x] Display component (kinda completed?)
    + [ ] Unique identifier component
    + [ ] Emit warning when configuration is invalid
- [ ] Pojo Expansion
    + [ ] Stats component
        + [x] Vanilla attributes
        + [ ] Hook with other plugins (mainly RPG plugins)
    + [x] Gemstones component
        + [x] Allow other components to modify a component's output (quite complicated I'd say)
        + [x] ~~Item type filtering (eg: only apply effects from `pojo:gemstones` type)~~ Replaced with gemstone slot ID
- [ ] Custom blocks
- [ ] Scripting support
    + [x] [MangoScript](https://github.com/MangoPlex/MangoScript)
    + [ ] Custom item component

## Some questions that I think you want to ask me.
### "Why was Pojo created?"
Pojo was originally created because I'm tired of dealing with API from other custom items plugins that are paid. I usually have to deal with them when someone commissioned me to make a new plugin that have integrations for those plugins. Some plugins are source-available (which is nice, by the way!), but the author made it difficult to obtain the dependencies (either lock it behind a paywall, or made it painful to compile).

### "Why are you using Bukkit API only? Why don't you use NMS/remapped Mojmap? Isn't it good for performance?"
The whole point of sticking with just Bukkit API is to ensure that I don't need spend a lot of time maintaining Pojo. After all, Pojo is a personal project of mine, and I only maintain it when I feel like it.

### "Why don't you make Pojo a paid plugin?"
I'd lose to other premium plugins. Pojo is not even a well-established plugin.

### "What is the future for Pojo?"
I honestly don't know. If Bukkit API is dying, I might port Pojo to Fabric.

## Copyright and License
(c) nahkd and Pojo contributors 2024. Licensed under MIT license.