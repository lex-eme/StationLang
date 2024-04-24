# Stationeers Language

In [Stationeers](https://store.steampowered.com/app/544550/Stationeers/) you have the ability to control machines and
devices with code. The game uses [MIPS](https://stationeers-wiki.com/MIPS) as it's in-game scripting language.
MIPS is a low-level/assembly language.


This project is a simple compiler aiming at producing MIPS code for Stationeers.

## The language

### Example
Here is an example script turning on and off a generator based on the charge ratio of a battery:
```
number battery = 0;
number generator = 1;

number minRatio = 0.2;
number maxRatio = 0.4;

setGeneratorOn(boolean on) {
    writeBoolean(generator, "On", on);
}

setup() {
    writeBoolean(generator, "Lock", true);
    setGeneratorOn(false);
}

update() {
    boolean shouldRun = false;
    number ratio = readNumber(battery, "Ratio");
    boolean isRunning = readBoolean(generator, "On");
    
    if (isRunning) {
        shouldRun = ratio > maxRatio;
    } else {
        shouldRun = ratio < minRatio;
    }
    
    setGeneratorOn(shouldRun);
}
```
### Features
1. Types:
    - number: floating-point numbers
    - boolean
2. Variables: can be defined in global or local scope.
3. Functions: can be defined in global scope.
4. Built-in functions to access devices connected to the IC:
    - readNumber(number deviceIndex, property propertyName): returns the number value associated to the property.
    - readBoolean(number deviceIndex, property propertyName): returns the boolean value associated to the property.
    - writeNumber(number deviceIndex, property propertyName, number value): set the number value to the property.
    - writeBoolean(number deviceIndex, property propertyName, boolean value): set the boolean value to the property.
    - more to come (to access slot items, to access MIPS functions, load batch, ...)
5. setup() et update() are special functions. setup() is automatically called once at the beginning of the script.
update() is called at the beginning of each power tick. These functions are not mandatory.

### Documentation
TBD