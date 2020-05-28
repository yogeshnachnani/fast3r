// environment variables

// Timeout increase trick from 
// http://blog.jonathanargentiero.com/overriding-karma-mocha-default-timeout-2000ms/ 
// The trick for adding this file came from
// https://github.com/JetBrains/kotlin/blob/7fe66d3456a2e51e5bf228497e81b28a6367766c/libraries/tools/kotlin-gradle-plugin/src/main/kotlin/org/jetbrains/kotlin/gradle/targets/js/testing/karma/KotlinKarma.kt#L44
config.set({
  "client": {
    "mocha": {
      "timeout": 5000
    },
  },
});
