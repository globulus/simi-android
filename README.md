# Šimi for Android

Šimi is an awesome programming language! Check out its [main project repo here](https://github.com/globulus/simi)!

This repo contains a sample project showcasing how Šimi code can be embedded and used in an Android app.

Be sure to check out [Android implementation of Šimi Sync!](https://github.com/globulus/simi-sync/android)

### How-to

1. Add **simi.jar** to your app's **lib** folder (you can either copy one from sample project or build the Šimi project yourself).
2. Add an **assets** folder to your project.
3. Paste the **Šimi stdlib folder** into your **assets** folder.
4. Add all **JAR files** to the **lib** folder.
5. Add any additional **Šimi code files** to the **assets** folder. Subfolders are supported and you can structure your Šimi files any way you'd like.
6. At the entry point of your application (Application subclass or a Login/Main activity onCreate()), set the import resolver and load your Šimi files. Loading sets up the interpreter and interprets the listed files, retaining the interpreter instance for future use.
```kotlin
...
// We set the ImportResolver to just read the content of the supplied Assets file.
// This is generally the way you want to set up the import resolver for Android.
ActiveSimi.setImportResolver(object : ActiveSimi.ImportResolver {
    override fun readFile(path: String): String {
        return application.assets.open(path).bufferedReader().use { it.readText() }
    }

    override fun useApiClassName(path: String): Boolean {
        return false
    }

    override fun resolve(s: String): URL {
        val fileName = s.substring(s.lastIndexOf('/') + 1)
        val libMain = File(getDir("libs", 0), fileName)
        return libMain.toURL()
    }

})

// You may use any number of comma-separated files.
// Stdlib.simi is loaded automatically.
// This method MUST be invoked before any calls to eval() or evalAsync().
ActiveSimi.load("Calc.simi")
...
```
6. Use *ActiveSimi.eval()* (synchronous) or *ActiveSimi.evalAsync()* (asynchronous) for invoking static, non-static or freestanding methods in your Šimi code. Calling these methods returns a SimiProperty.
```kotlin
...
findViewById<Button>(R.id.go).setOnClickListener {
    val l = left.text.toString().toDouble()
    val r = right.text.toString().toDouble()
    val o = op.text.toString()
    val res = ActiveSimi.eval("Calc", "compute", SimiValue.Number(l), SimiValue.String(o), SimiValue.Number(r))
    Toast.makeText(baseContext, res.toString(), Toast.LENGTH_LONG).show()
}
...
```
7. You can use static methods in *SimiMapper* class to convert Java values, maps and lists to Šimi ones, or you can do it manually like in the example above.
