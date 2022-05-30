# io.il2.iltags

## Introduction

The **io.il2.iltags** is a complete reimplementation of the original
**[io.interlockledger.tags](https://github.com/interlockledger/io.interlockledger.tags)**
with the focus on small code footprint and extensibility. See 
[ILInt](https://github.com/interlockledger/specification/tree/master/ILInt) and 
[ILTags](https://github.com/interlockledger/specification/tree/master/ILTags)
standards defined by **InterlockLedger** for further information about this library.

The primary intent of this library is to be used for interactions with **InterlockLedger**
nodes and formats using Java. Regardless of that, this library can be used for any
purpose without restrictions.

## Compatibility with io.interlockledger.tags

This new library do contain some classes and functionalities directly copied from 
**[io.interlockledger.tags](https://github.com/interlockledger/io.interlockledger.tags)**.
However the components in this library 

it is not guaranteed to have the same interface or be compatible with it.

is not compatible with **io.interlockledger.tags** but shares some of its 
code base and utilities. It also changed the unit-tests library to **JUnit Jupiter** in
replacement of **JUnit 4**.

## Version history

    2.0.0:
        First production ready implementation.

## Bug report

Any bugs and suggestions may be reported directly using the 
[GitHub Issue System](https://github.com/interlockledger/java-io.il2.iltags/issues).

## Collaboration

This project will be managed by the **InterlockLedger** team but any collaboration will
be accepted and properly credited. Feel free to clone this project and propose changes
via pull requests.

It is important to notice that, for now, this project restricts itself to Java 8 only. No
external dependencies will be allowed.

## License

This library is licensed under the **The 3-Clause BSD License**.

## Support

This is an OSS project, the **InterlockLedger Network** does not provide any direct
support for this library.

## FAQ

### Can I use this library on commercial software?

Yes. Just follow the license restrictions.

### Does this this library use external dependencies?

No. It is supposed to use only Java 8 core classes and nothing more.

### What is the minimum Java version required to run this library?

The library is restricted to Java 8 in order to make it as portable as possible.

