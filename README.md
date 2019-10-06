# TraceJump

Mixed reality trace link navigator. Generates trace links from pixels.

## Preview

![](https://user-images.githubusercontent.com/175716/66264340-7ba36e00-e7d1-11e9-8ca2-df94f2d357b6.png)

## Running

Tested on Mac OS X:

```
git clone https://github.com/acejump/tracejump && cd tracejump && ./gradlew run
```

Press <kbd>Ctrl</kbd>+<kbd>\\</kbd> to activate TraceJump, then either (1) click on a highlighted region or (2) use the adjacent two-character tag to jump into the semantic web (i.e. Google).

## To Do

* Migrate to [Kotlin Native](https://kotlinlang.org/docs/reference/native-overview.html) for multiplatform support
* Support configurable trace link targets
* Support context sensitive trace links
* Support modal menu-based navigation
* Prefetching and shortcut navigation (A->B->C => A->C)
* Text-to-speech capabilities
* Mobile application

## Learn more

* [Knowledge and Software Technology (KAST) Research Group](https://www.cs.mcgill.ca/~jguo/lab.html)
* [Towards an Intelligent Domain-Specific Traceability Solution](https://www.cs.mcgill.ca/~jguo/resources/papers/ASE14_DoCIT.pdf)
* [Semantically Enhanced Software Traceability Using Deep Learning Techniques](https://arxiv.org/pdf/1804.02438.pdf)
* [Traceability in the Wild: Automatically Augmenting Incomplete Trace Links](https://arxiv.org/pdf/1804.02433.pdf)