# **🌍 Guide to setting jsons in NullCore**
>### Do you want to customize the music in your mod or finally set eternal night and eternal rain in your biome? Now I will tell you in detail how to do this through two simple JSON files.

## **🌍 File ```tracks.json``` - track settings**
>#### Here we write down what music will play in different worlds and structures:
>>Configure music for different dimensions:
>>
>> PS: If you are using vanilla dimension, please note that the original background music will stop playing in these places
> ```json
> "dimensions": {
>   "minecraft:nether": [
>     {
>       "sound": "modid:music.hell", //resourse location SoundEvent
>       "min_delay": 1000, //min tick
>       "max_delay": 2000 //max tick
>     }
>   ]
> }
> ```
>
>>Set music for specific structures:
> ```json
> "structures": {
>   "minecraft:ancient_city": [
>     {
>       "sound": "modid:music.goida", //resourse location SoundEvent
>       "min_delay": 600, //min tick
>       "max_delay": 1200 //max tick
>     }
>   ]
> }
> ```
>
>>Set music for specific mobs:
> ```json
> "mobs": {
>   "minecraft:zombie": [
>     {
>       "sound": "modid:music.zombiebossfight", //resourse location SoundEvent
>       "min_delay": 100, //min tick
>       "max_delay": 200 //max tick
>     }
>   ]
> }
> ```
> Move json to path **data/modid/level/tracks.json**

## **🌍 File ```biome_rules.json``` - biome settings**
>#### Here you can manipulate [all registered biome rules](https://github.com/easynull/NullCore/blob/1.21.4-neoforge/src/main/java/com/mw/nullcore/data/BiomeRulesManager.java#L25):
> ```json
> {
>   "minecraft:desert": {
>     "fixed_time": 18000, //max = 24000
>     "has_precipitation": true //Enable if you want snow/rain particles to be rendered within your biome.
>   },
>   "minecraft:jungle": {
>     "rain_force": 0.8 //Rain > 0.2; thunder > 0.9; max = 1
>   }
> }
> ```
> Move json to path **data/modid/level/biome_rules.json**

>### I hope these functions will be very useful to use, without any bugs :)
