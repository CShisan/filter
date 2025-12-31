
Installation information
=======

This template repository can be directly cloned to get you started with a new
mod. Simply create a new repository cloned from this one, by following the
instructions provided by [GitHub](https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template).

Once you have your clone, simply open the repository in the IDE of your choice. The usual recommendation for an IDE is either IntelliJ IDEA or Eclipse.

If at any point you are missing libraries in your IDE, or you've run into problems you can
run `gradlew --refresh-dependencies` to refresh the local cache. `gradlew clean` to reset everything 
{this does not affect your code} and then start the process again.

Mapping Names:
============
By default, the MDK is configured to use the official mapping names from Mojang for methods and fields 
in the Minecraft codebase. These names are covered by a specific license. All modders should be aware of this
license. For the latest license text, refer to the mapping file itself, or the reference copy here:
https://github.com/NeoForged/NeoForm/blob/main/Mojang.md

Additional Resources: 
==========
Community Documentation: https://docs.neoforged.net/  
NeoForged Discord: https://discord.neoforged.net/

功能说明
========

本模组用于限制玩家拾取被标记的物品类型（仅对标记者本人生效）。提供以下指令（在服务端执行）：

- `/filter mark`：标记主手物品类型，标记者本人无法拾取同类物品。
- `/filter unmark`：取消主手物品类型标记。
- `/filter status`：查看主手物品类型是否被标记。
- `/filter list`：列出当前已标记的物品类型。
- 指令支持可选参数：`/filter mark <item>`、`/filter unmark <item>`、`/filter status <item>`。
- 标签指令：`/filter marktag <tag>`、`/filter unmarktag <tag>`。

物品栏标记栏
===========

玩家打开物品栏时会显示可折叠的标记栏：

- 上方为带滚动条的已标记物品类型列表（点击条目可取消标记）。
- 下方左侧为物品标记框（拖动物品到此处进行选择，右键可清空）。
- 下方右侧“+”按钮悬浮后展示标签列表，选择标签后完成标记。
