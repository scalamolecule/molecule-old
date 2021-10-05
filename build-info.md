# Build cmds cheat sheet


## Compile

Add `-Dfree=true` to use free Datomic version. Default is Datomic dev/pro

Compile to 2.13 only:

    sbt clean compile -Dfree=true

Compile to 2.12 only:

    sbt ++2.12.15 clean compile -Dfree=true

Compile to 2.12 and 2.13:

    sbt clean +compile -Dfree=true

To have molecule jars generated, add `-Dmolecule=true`

    sbt clean +compile -Dfree=true -Dmolecule=true

## Compile JS

> sbt
> moleculeJS/fastOptJS


## Publishing

- Check that changes applied in jvm module are applied to both 2.12 and 2.13!
- Un-comment `Publish.withDocs` in build.sbt

2.12 & 2.13:
> sbt +publishLocal -Dfree=true
> // or
> sbt +publishSigned -Dfree=true

This might be too heavy for the compiler. In that case split up the process:

> sbt ++2.12.15 clean compile -Dfree=true
> sbt ++2.12.15 doc
> sbt ++2.12.15 publishSigned -Dfree=true
> 
> // publish 2.12 at maven central, then do 2.13:
> 
> sbt clean compile -Dfree=true
> sbt doc
> sbt publishSigned -Dfree=true
> // publish 2.13 at maven central


