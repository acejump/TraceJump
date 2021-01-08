# TraceJump

Mixed reality trace link navigator. Generates trace links from pixels.

## Preview

![](https://user-images.githubusercontent.com/175716/66264340-7ba36e00-e7d1-11e9-8ca2-df94f2d357b6.png)

## Running

Tested on Mac OS X:

```
git clone https://github.com/acejump/tracejump && cd tracejump && ./gradlew run
```

Press <kbd>Ctrl</kbd>+<kbd>\\</kbd> to activate TraceJump. Some links will appear:

![](https://user-images.githubusercontent.com/175716/67155780-4c1d5700-f2e3-11e9-91bf-50b66aa8f6da.png)
 
Use the adjacent two-character tag to select one. A menu will then open:

![](https://user-images.githubusercontent.com/175716/67155736-9b16bc80-f2e2-11e9-8091-bf6145426362.png)

Select a menu option (e.g. by typing `h`) to jump into the semantic web:

![](https://user-images.githubusercontent.com/175716/67155762-f9439f80-f2e2-11e9-9b2e-4c29f40440aa.png)

## To Do

* Migrate to [Kotlin Multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html)/[Skiko](https://github.com/JetBrains/skiko) for multiplatform desktop support
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
