# Stationeers Language

This project is a simple compiler aiming at producing MIPS code ([stationeer's version](https://stationeers-wiki.com/MIPS)).

## The language

### Example
Here is an example script turning on and off a generator based on the charge ratio of a battery:
```
number battery = 0;
number generator = 1;

number minRatio = 0.2;
number maxRatio = 0.4;

setGeneratorOn(boolean on) {
    setBool(generator, "On", on);
}

setup() {
    setBool(generator, "Lock", true);
    setGeneratorOn(false);
}

update() {
    boolean shouldRun = false;
    number ratio = loadNumber(battery, "Ratio");
    boolean isRunning = loadBoolean(generator, "On");
    
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
    - loadNumber(number deviceIndex, property propertyName): returns the number value associated to the property.
    - loadBoolean(number deviceIndex, property propertyName): returns the boolean value associated to the property.
    - setNumber(number deviceIndex, property propertyName, number value): set the number value to the property.
    - setBoolean(number deviceIndex, property propertyName, boolean value): set the boolean value to the property.
    - more to come (to access slot items, to access MIPS functions, load batch, ...)
5. setup() et update() are special functions. setup() is automatically called once at the beginning of the script.
update() is called at the beginning of each power tick. These functions are not mandatory.

### Documentation
TBD