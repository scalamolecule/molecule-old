# Build cmds cheat sheet


## Compile

Compile to 2.13 only:

    sbt clean compile
    # or
    sbt compile

Compile to 2.12 only:

    sbt ++2.12.15 clean compile

Compile to 2.12 and 2.13:

    sbt clean +compile

To have molecule jars generated, add `-Dmolecule=true`

    sbt clean +compile -Dmolecule=true
    sbt +compile -Dmolecule=true

## Compile JS

> sbt
> moleculeJS/fastOptJS


## Publishing

2.12 & 2.13:
> sbt +publishLocal
> // or to maven central with docs generated too
> sbt +publishSigned -Ddocs=true

Or publish versions separately if the compiler is struggling (and even compile in advance if needed)

> // publish 2.12
> sbt ++2.12.15 publishSigned -Ddocs=true
> 
> // ...then 2.13
> sbt publishSigned -Ddocs=true
