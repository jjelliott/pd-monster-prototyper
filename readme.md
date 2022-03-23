# progs_dump Monster Prototype

This takes a customized monster entity and creates a "real" entity definition out of it.

This allows the monster customization to work as a prototyping tool to permanently add 
new monsters to your mod.

## Usage

Copy your customized monster from Trenchbroom or directly from your .map into a text file. Name this file what you would like to call your spawn function, for example `monster_quadgrunt.txt`. 

Call `pd-monster-prototyper monster_quadgrunt.txt`. This will create a new file beside the text file called `monster_quadgrunt.txt.qc` containing your new spawn function.

You can then add the new file to your `progs.src`. You can rename the file if you wish. 

## Building

Run `./gradlew jar` to build a jar file for usage with a Java runtime on your system.

Run `./gradlew nativeImage` to build a native binary for your system. Note that you will need to follow [this guide](https://www.graalvm.org/22.0/reference-manual/native-image/) to get this to function.

## TODO
- Create FGD entries for new monsters
- Make sure all customization fields are covered
- Allow some way of overwriting parsed fields (for future proofing)
- Add proper cli arg help
- Tests, maybe
